package com.example.expensetracker.data.local

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.example.expensetracker.common.UnitConverterEngine
import com.example.expensetracker.data.model.MasterProduct
import com.example.expensetracker.ui.dashboard.getMarketItemCategory

internal class MasterProductTableHelper(private val dbHelper: ExpenseDatabaseHelper) {

    /**
     * Records or updates a product in the Master Product Catalog when purchased.
     * Returns a Pair of [MasterProduct] and [Boolean] (true if it was a newly discovered product).
     */
    fun recordOrUpdateProduct(
        productName: String,
        rawQuantity: Double,
        rawUnit: String,
        totalAmount: Double,
        timestamp: Long = System.currentTimeMillis()
    ): Pair<MasterProduct, Boolean>? {
        if (totalAmount <= 0.0 || rawQuantity <= 0.0) return null

        val cleanName = UnitConverterEngine.normalizeProductName(productName)
        if (cleanName.isBlank()) return null

        val normalized = UnitConverterEngine.normalize(rawQuantity, rawUnit)
        val unitPrice = if (normalized.baseQuantity > 0.0) totalAmount / normalized.baseQuantity else totalAmount

        val db = dbHelper.writableDatabase
        val existing = getProductByNormalizedName(db, cleanName)

        return if (existing == null) {
            val category = getMarketItemCategory(cleanName)
            val cv = ContentValues().apply {
                put(COL_MP_NAME, productName.trim())
                put(COL_MP_NORMALIZED_NAME, cleanName)
                put(COL_MP_CATEGORY, category)
                put(COL_MP_DEFAULT_UNIT, normalized.baseUnit)
                put(COL_MP_LATEST_UNIT_PRICE, unitPrice)
                putNull(COL_MP_PREVIOUS_UNIT_PRICE)
                put(COL_MP_MIN_UNIT_PRICE, unitPrice)
                put(COL_MP_MAX_UNIT_PRICE, unitPrice)
                put(COL_MP_AVG_UNIT_PRICE, unitPrice)
                put(COL_MP_PURCHASE_COUNT, 1)
                put(COL_MP_IS_STAPLE, 0)
                put(COL_MP_FIRST_PURCHASED_AT, timestamp)
                put(COL_MP_LAST_PURCHASED_AT, timestamp)
            }
            val id = db.insertWithOnConflict(TABLE_MASTER_PRODUCTS, null, cv, SQLiteDatabase.CONFLICT_REPLACE)
            val newProduct = MasterProduct(
                id = id,
                name = productName.trim(),
                normalizedName = cleanName,
                category = category,
                defaultUnit = normalized.baseUnit,
                latestUnitPrice = unitPrice,
                previousUnitPrice = null,
                minUnitPrice = unitPrice,
                maxUnitPrice = unitPrice,
                avgUnitPrice = unitPrice,
                purchaseCount = 1,
                isStaple = false,
                firstPurchasedAt = timestamp,
                lastPurchasedAt = timestamp
            )
            Pair(newProduct, true)
        } else {
            val newCount = existing.purchaseCount + 1
            val newMin = minOf(existing.minUnitPrice, unitPrice)
            val newMax = maxOf(existing.maxUnitPrice, unitPrice)
            val newAvg = if (newCount > 0) ((existing.avgUnitPrice * existing.purchaseCount) + unitPrice) / newCount else unitPrice
            val isNowStaple = if (newCount >= 3) true else existing.isStaple

            val cv = ContentValues().apply {
                put(COL_MP_LATEST_UNIT_PRICE, unitPrice)
                put(COL_MP_PREVIOUS_UNIT_PRICE, existing.latestUnitPrice)
                put(COL_MP_MIN_UNIT_PRICE, newMin)
                put(COL_MP_MAX_UNIT_PRICE, newMax)
                put(COL_MP_AVG_UNIT_PRICE, newAvg)
                put(COL_MP_PURCHASE_COUNT, newCount)
                put(COL_MP_IS_STAPLE, if (isNowStaple) 1 else 0)
                put(COL_MP_LAST_PURCHASED_AT, timestamp)
            }
            db.update(TABLE_MASTER_PRODUCTS, cv, "$COL_MP_ID = ?", arrayOf(existing.id.toString()))

            val updatedProduct = existing.copy(
                latestUnitPrice = unitPrice,
                previousUnitPrice = existing.latestUnitPrice,
                minUnitPrice = newMin,
                maxUnitPrice = newMax,
                avgUnitPrice = newAvg,
                purchaseCount = newCount,
                isStaple = isNowStaple,
                lastPurchasedAt = timestamp
            )
            Pair(updatedProduct, false)
        }
    }

    fun getAllMasterProducts(): List<MasterProduct> {
        val db = dbHelper.readableDatabase
        val list = mutableListOf<MasterProduct>()
        val cursor = db.query(
            TABLE_MASTER_PRODUCTS,
            null,
            null,
            null,
            null,
            null,
            "$COL_MP_LAST_PURCHASED_AT DESC"
        )
        cursor.use {
            while (it.moveToNext()) {
                list.add(MasterProductMapper.cursorToMasterProduct(it))
            }
        }
        return list
    }

    fun getStapleProducts(limit: Int = 15): List<MasterProduct> {
        val db = dbHelper.readableDatabase
        val list = mutableListOf<MasterProduct>()
        val cursor = db.query(
            TABLE_MASTER_PRODUCTS,
            null,
            "$COL_MP_IS_STAPLE = 1 OR $COL_MP_PURCHASE_COUNT >= 2",
            null,
            null,
            null,
            "$COL_MP_PURCHASE_COUNT DESC, $COL_MP_LAST_PURCHASED_AT DESC",
            limit.toString()
        )
        cursor.use {
            while (it.moveToNext()) {
                list.add(MasterProductMapper.cursorToMasterProduct(it))
            }
        }
        return list
    }

    fun getMasterProductByName(name: String): MasterProduct? {
        val cleanName = UnitConverterEngine.normalizeProductName(name)
        if (cleanName.isBlank()) return null
        val db = dbHelper.readableDatabase
        return getProductByNormalizedName(db, cleanName)
    }

    fun searchMasterProducts(query: String, limit: Int = 20): List<MasterProduct> {
        val cleanQuery = UnitConverterEngine.normalizeProductName(query)
        if (cleanQuery.isBlank()) return emptyList()
        val db = dbHelper.readableDatabase
        val list = mutableListOf<MasterProduct>()
        val cursor = db.query(
            TABLE_MASTER_PRODUCTS,
            null,
            "$COL_MP_NAME LIKE ? OR $COL_MP_NORMALIZED_NAME LIKE ?",
            arrayOf("%$cleanQuery%", "%$cleanQuery%"),
            null,
            null,
            "$COL_MP_PURCHASE_COUNT DESC, $COL_MP_LAST_PURCHASED_AT DESC",
            limit.toString()
        )
        cursor.use {
            while (it.moveToNext()) {
                list.add(MasterProductMapper.cursorToMasterProduct(it))
            }
        }
        return list
    }

    internal fun getProductByNormalizedName(db: SQLiteDatabase, normalizedName: String): MasterProduct? {
        val cursor = db.query(
            TABLE_MASTER_PRODUCTS,
            null,
            "$COL_MP_NORMALIZED_NAME = ? COLLATE NOCASE",
            arrayOf(normalizedName),
            null,
            null,
            null,
            "1"
        )
        cursor.use {
            if (it.moveToFirst()) {
                return MasterProductMapper.cursorToMasterProduct(it)
            }
        }
        return null
    }

    fun backfillFromPriceHistory(db: SQLiteDatabase) {
        MasterProductSeeder.backfillFromPriceHistory(db) { d, name -> getProductByNormalizedName(d, name) }
    }

    fun seedDefaultStaplesIfEmpty(db: SQLiteDatabase) {
        MasterProductSeeder.seedDefaultStaplesIfEmpty(db)
    }
}
