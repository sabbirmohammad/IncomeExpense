package com.example.expensetracker.data.local

import android.content.ContentValues
import com.example.expensetracker.common.UnitConverterEngine
import com.example.expensetracker.data.model.PriceTrend
import com.example.expensetracker.data.model.ProductPriceInsight
import com.example.expensetracker.data.model.ProductPriceRecord
import kotlin.math.abs

internal class ProductPriceTableHelper(internal val dbHelper: ExpenseDatabaseHelper) {

    fun recordPrice(
        productName: String,
        rawQuantity: Double,
        rawUnit: String,
        totalAmount: Double,
        marketItemId: Long? = null,
        transactionId: Long? = null,
        timestamp: Long = System.currentTimeMillis()
    ): Long {
        if (totalAmount <= 0.0 || rawQuantity <= 0.0) return -1L
        val cleanName = UnitConverterEngine.normalizeProductName(productName)
        if (cleanName.isBlank()) return -1L

        val normalized = UnitConverterEngine.normalize(rawQuantity, rawUnit)
        val unitPrice = if (normalized.baseQuantity > 0.0) totalAmount / normalized.baseQuantity else totalAmount
        val db = dbHelper.writableDatabase
        val cv = ContentValues().apply {
            put(COL_PPH_PRODUCT_NAME, cleanName)
            put(COL_PPH_RAW_QUANTITY, rawQuantity)
            put(COL_PPH_RAW_UNIT, rawUnit.trim())
            put(COL_PPH_BASE_QUANTITY, normalized.baseQuantity)
            put(COL_PPH_BASE_UNIT, normalized.baseUnit)
            put(COL_PPH_TOTAL_AMOUNT, totalAmount)
            put(COL_PPH_UNIT_PRICE, unitPrice)
            if (marketItemId != null) put(COL_PPH_MARKET_ITEM_ID, marketItemId) else putNull(COL_PPH_MARKET_ITEM_ID)
            if (transactionId != null) put(COL_PPH_TRANSACTION_ID, transactionId) else putNull(COL_PPH_TRANSACTION_ID)
            put(COL_PPH_TIMESTAMP, timestamp)
        }

        val insertId = db.insert(TABLE_PRODUCT_PRICE_HISTORY, null, cv)
        if (insertId > 0) {
            dbHelper.masterProductHelper.recordOrUpdateProduct(productName, rawQuantity, rawUnit, totalAmount, timestamp)
        }
        return insertId
    }

    fun getPriceInsight(productName: String): ProductPriceInsight? {
        val cleanName = UnitConverterEngine.normalizeProductName(productName)
        if (cleanName.isBlank()) return null
        val history = getPriceHistory(cleanName, limit = 100)
        if (history.isEmpty()) return null

        val latest = history.first()
        val previous = if (history.size > 1) history[1] else null
        val currentRate = latest.unitPrice
        val previousRate = previous?.unitPrice
        val priceDiff = if (previousRate != null) currentRate - previousRate else 0.0
        val percentDiff = if (previousRate != null && previousRate > 0) (priceDiff / previousRate) * 100.0 else 0.0

        val now = latest.timestamp
        val thirtyDaysMillis = 30L * 24 * 60 * 60 * 1000
        val targetThirtyDaysAgo = now - thirtyDaysMillis
        val thirtyDayRecord = history.find { abs(it.timestamp - targetThirtyDaysAgo) < (15L * 24 * 60 * 60 * 1000) }

        val oneMonthRate = thirtyDayRecord?.unitPrice
        val oneMonthDiff = if (oneMonthRate != null) currentRate - oneMonthRate else null
        val oneMonthPercent = if (oneMonthRate != null && oneMonthRate > 0) ((currentRate - oneMonthRate) / oneMonthRate) * 100.0 else null

        val minRate = history.minOf { it.unitPrice }
        val maxRate = history.maxOf { it.unitPrice }
        val totalAmountSum = history.sumOf { it.totalAmount }
        val totalBaseQtySum = history.sumOf { it.baseQuantity }
        val avgRate = if (totalBaseQtySum > 0.0) totalAmountSum / totalBaseQtySum else if (history.isNotEmpty()) history.map { it.unitPrice }.average() else currentRate

        val trend = when {
            percentDiff > 1.0 -> PriceTrend.UP
            percentDiff < -1.0 -> PriceTrend.DOWN
            else -> PriceTrend.STABLE
        }

        val pricePoints = history.take(10).reversed().map { it.unitPrice }

        return ProductPriceInsight(
            productName = cleanName,
            currentUnitPrice = currentRate,
            previousUnitPrice = previousRate,
            baseUnit = latest.baseUnit,
            priceDiff = priceDiff,
            percentDiff = percentDiff,
            oneMonthAgoUnitPrice = oneMonthRate,
            oneMonthDiff = oneMonthDiff,
            oneMonthPercentDiff = oneMonthPercent,
            minUnitPrice = minRate,
            maxUnitPrice = maxRate,
            avgUnitPrice = avgRate,
            purchaseCount = history.size,
            lastPurchasedTimestamp = latest.timestamp,
            priceTrend = trend,
            recentPricePoints = pricePoints
        )
    }

    fun getAllPriceInsights(): Map<String, ProductPriceInsight> {
        val db = dbHelper.readableDatabase
        val productNames = mutableListOf<String>()
        val nameCursor = db.rawQuery("SELECT DISTINCT $COL_PPH_PRODUCT_NAME FROM $TABLE_PRODUCT_PRICE_HISTORY ORDER BY $COL_PPH_TIMESTAMP DESC", null)
        nameCursor.use {
            while (it.moveToNext()) {
                val name = it.getString(0)
                if (!name.isNullOrBlank()) productNames.add(name)
            }
        }

        val map = mutableMapOf<String, ProductPriceInsight>()
        for (name in productNames) {
            val insight = getPriceInsight(name)
            if (insight != null) map[name] = insight
        }
        return map
    }

    fun estimateCost(productName: String, quantity: Double, unit: String): Double? {
        val cleanName = UnitConverterEngine.normalizeProductName(productName)
        val insight = getPriceInsight(cleanName) ?: return null
        return UnitConverterEngine.estimateCost(insight.currentUnitPrice, quantity, unit)
    }

    fun getPriceHistory(productName: String, limit: Int = 50): List<ProductPriceRecord> {
        val cleanName = UnitConverterEngine.normalizeProductName(productName)
        val db = dbHelper.readableDatabase
        val list = mutableListOf<ProductPriceRecord>()
        val cursor = db.query(
            TABLE_PRODUCT_PRICE_HISTORY, null, "$COL_PPH_PRODUCT_NAME = ? COLLATE NOCASE", arrayOf(cleanName),
            null, null, "$COL_PPH_TIMESTAMP DESC, $COL_PPH_ID DESC", limit.toString()
        )
        cursor.use {
            while (it.moveToNext()) {
                val txIdx = it.getColumnIndexOrThrow(COL_PPH_TRANSACTION_ID)
                val mItemIdIdx = it.getColumnIndexOrThrow(COL_PPH_MARKET_ITEM_ID)
                list.add(
                    ProductPriceRecord(
                        id = it.getLong(it.getColumnIndexOrThrow(COL_PPH_ID)),
                        productName = it.getString(it.getColumnIndexOrThrow(COL_PPH_PRODUCT_NAME)),
                        rawQuantity = it.getDouble(it.getColumnIndexOrThrow(COL_PPH_RAW_QUANTITY)),
                        rawUnit = it.getString(it.getColumnIndexOrThrow(COL_PPH_RAW_UNIT)),
                        baseQuantity = it.getDouble(it.getColumnIndexOrThrow(COL_PPH_BASE_QUANTITY)),
                        baseUnit = it.getString(it.getColumnIndexOrThrow(COL_PPH_BASE_UNIT)),
                        totalAmount = it.getDouble(it.getColumnIndexOrThrow(COL_PPH_TOTAL_AMOUNT)),
                        unitPrice = it.getDouble(it.getColumnIndexOrThrow(COL_PPH_UNIT_PRICE)),
                        marketItemId = if (!it.isNull(mItemIdIdx)) it.getLong(mItemIdIdx) else null,
                        transactionId = if (!it.isNull(txIdx)) it.getLong(txIdx) else null,
                        timestamp = it.getLong(it.getColumnIndexOrThrow(COL_PPH_TIMESTAMP))
                    )
                )
            }
        }
        return list
    }
}
