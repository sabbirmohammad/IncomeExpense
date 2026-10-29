package com.example.expensetracker.data.local

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.example.expensetracker.common.UnitConverterEngine
import com.example.expensetracker.data.model.PriceTrend
import com.example.expensetracker.data.model.ProductPriceInsight
import com.example.expensetracker.data.model.ProductPriceRecord
import kotlin.math.abs

internal class ProductPriceTableHelper(private val dbHelper: ExpenseDatabaseHelper) {

    /**
     * Records a single purchase price into the history table.
     */
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

    /**
     * Batch records price records for purchased market items.
     */
    fun recordPricesForMarketItems(
        items: List<com.example.expensetracker.data.model.MarketItem>,
        transactionId: Long?,
        timestamp: Long
    ) {
        val validItems = items.filter { it.isPurchased && (it.amount ?: 0.0) > 0.0 && it.quantity > 0.0 }
        if (validItems.isEmpty()) return

        val db = dbHelper.writableDatabase
        db.beginTransaction()
        try {
            for (item in validItems) {
                val cleanName = UnitConverterEngine.normalizeProductName(item.name)
                if (cleanName.isBlank()) continue
                val normalized = UnitConverterEngine.normalize(item.quantity, item.unit)
                val totalAmt = item.amount ?: 0.0
                val unitPrice = if (normalized.baseQuantity > 0.0) totalAmt / normalized.baseQuantity else totalAmt

                val cv = ContentValues().apply {
                    put(COL_PPH_PRODUCT_NAME, cleanName)
                    put(COL_PPH_RAW_QUANTITY, item.quantity)
                    put(COL_PPH_RAW_UNIT, item.unit.trim())
                    put(COL_PPH_BASE_QUANTITY, normalized.baseQuantity)
                    put(COL_PPH_BASE_UNIT, normalized.baseUnit)
                    put(COL_PPH_TOTAL_AMOUNT, totalAmt)
                    put(COL_PPH_UNIT_PRICE, unitPrice)
                    put(COL_PPH_MARKET_ITEM_ID, item.id)
                    if (transactionId != null) put(COL_PPH_TRANSACTION_ID, transactionId) else putNull(COL_PPH_TRANSACTION_ID)
                    put(COL_PPH_TIMESTAMP, timestamp)
                }
                db.insert(TABLE_PRODUCT_PRICE_HISTORY, null, cv)
                dbHelper.masterProductHelper.recordOrUpdateProduct(item.name, item.quantity, item.unit, totalAmt, timestamp)
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    /**
     * Retrieves full analytical price insight for a specific product.
     */
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
        val percentDiff = if (previousRate != null && previousRate > 0) {
            (priceDiff / previousRate) * 100.0
        } else 0.0

        // Find ~30 days ago record (between 20 to 60 days)
        val now = latest.timestamp
        val thirtyDaysMillis = 30L * 24 * 60 * 60 * 1000
        val targetThirtyDaysAgo = now - thirtyDaysMillis
        val thirtyDayRecord = history.find { abs(it.timestamp - targetThirtyDaysAgo) < (15L * 24 * 60 * 60 * 1000) }

        val oneMonthRate = thirtyDayRecord?.unitPrice
        val oneMonthDiff = if (oneMonthRate != null) currentRate - oneMonthRate else null
        val oneMonthPercent = if (oneMonthRate != null && oneMonthRate > 0) {
            ((currentRate - oneMonthRate) / oneMonthRate) * 100.0
        } else null

        val minRate = history.minOf { it.unitPrice }
        val maxRate = history.maxOf { it.unitPrice }

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
            purchaseCount = history.size,
            lastPurchasedTimestamp = latest.timestamp,
            priceTrend = trend,
            recentPricePoints = pricePoints
        )
    }

    /**
     * Fetches all price insights indexed by normalized product name for lightning-fast UI lookups.
     */
    fun getAllPriceInsights(): Map<String, ProductPriceInsight> {
        val db = dbHelper.readableDatabase
        val productNames = mutableListOf<String>()
        val nameCursor = db.rawQuery(
            "SELECT DISTINCT $COL_PPH_PRODUCT_NAME FROM $TABLE_PRODUCT_PRICE_HISTORY ORDER BY $COL_PPH_TIMESTAMP DESC",
            null
        )
        nameCursor.use {
            while (it.moveToNext()) {
                val name = it.getString(0)
                if (!name.isNullOrBlank()) {
                    productNames.add(name)
                }
            }
        }

        val map = mutableMapOf<String, ProductPriceInsight>()
        for (name in productNames) {
            val insight = getPriceInsight(name)
            if (insight != null) {
                map[name] = insight
            }
        }
        return map
    }

    /**
     * Estimates cost for target item quantity and unit based on known price intelligence.
     */
    fun estimateCost(productName: String, quantity: Double, unit: String): Double? {
        val cleanName = UnitConverterEngine.normalizeProductName(productName)
        val insight = getPriceInsight(cleanName) ?: return null
        return UnitConverterEngine.estimateCost(insight.currentUnitPrice, quantity, unit)
    }

    /**
     * Fetches raw chronological price records for charts and timeline.
     */
    fun getPriceHistory(productName: String, limit: Int = 50): List<ProductPriceRecord> {
        val cleanName = UnitConverterEngine.normalizeProductName(productName)
        val db = dbHelper.readableDatabase
        val list = mutableListOf<ProductPriceRecord>()

        val cursor = db.query(
            TABLE_PRODUCT_PRICE_HISTORY,
            null,
            "$COL_PPH_PRODUCT_NAME = ? COLLATE NOCASE",
            arrayOf(cleanName),
            null,
            null,
            "$COL_PPH_TIMESTAMP DESC, $COL_PPH_ID DESC",
            limit.toString()
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

    /**
     * Backfills historical data from existing completed market items.
     */
    fun backfillProductPriceHistory(db: SQLiteDatabase) {
        val checkCursor = db.rawQuery("SELECT COUNT(*) FROM $TABLE_PRODUCT_PRICE_HISTORY", null)
        var count = 0
        checkCursor.use {
            if (it.moveToFirst()) count = it.getInt(0)
        }
        if (count > 0) return

        val cursor = db.query(
            TABLE_MARKET_ITEMS,
            null,
            "$COL_MI_IS_PURCHASED = 1 AND $COL_MI_AMOUNT > 0 AND $COL_MI_QUANTITY > 0",
            null,
            null,
            null,
            "$COL_MI_CREATED_AT ASC, $COL_MI_ID ASC"
        )

        cursor.use {
            while (it.moveToNext()) {
                val rawName = it.getString(it.getColumnIndexOrThrow(COL_MI_NAME)) ?: ""
                val cleanName = UnitConverterEngine.normalizeProductName(rawName)
                if (cleanName.isBlank()) continue

                val rawQty = it.getDouble(it.getColumnIndexOrThrow(COL_MI_QUANTITY))
                val rawUnit = it.getString(it.getColumnIndexOrThrow(COL_MI_UNIT)) ?: "কেজি"
                val amount = it.getDouble(it.getColumnIndexOrThrow(COL_MI_AMOUNT))
                val itemId = it.getLong(it.getColumnIndexOrThrow(COL_MI_ID))
                val txIdx = it.getColumnIndexOrThrow(COL_MI_TRANSACTION_ID)
                val txId = if (!it.isNull(txIdx)) it.getLong(txIdx) else null
                val createdAt = it.getLong(it.getColumnIndexOrThrow(COL_MI_CREATED_AT))

                val normalized = UnitConverterEngine.normalize(rawQty, rawUnit)
                val unitPrice = if (normalized.baseQuantity > 0.0) amount / normalized.baseQuantity else amount

                val cv = ContentValues().apply {
                    put(COL_PPH_PRODUCT_NAME, cleanName)
                    put(COL_PPH_RAW_QUANTITY, rawQty)
                    put(COL_PPH_RAW_UNIT, rawUnit.trim())
                    put(COL_PPH_BASE_QUANTITY, normalized.baseQuantity)
                    put(COL_PPH_BASE_UNIT, normalized.baseUnit)
                    put(COL_PPH_TOTAL_AMOUNT, amount)
                    put(COL_PPH_UNIT_PRICE, unitPrice)
                    put(COL_PPH_MARKET_ITEM_ID, itemId)
                    if (txId != null) put(COL_PPH_TRANSACTION_ID, txId) else putNull(COL_PPH_TRANSACTION_ID)
                    put(COL_PPH_TIMESTAMP, createdAt)
                }
                db.insert(TABLE_PRODUCT_PRICE_HISTORY, null, cv)
                dbHelper.masterProductHelper.recordOrUpdateProduct(rawName, rawQty, rawUnit, amount, createdAt)
            }
        }
    }
}
