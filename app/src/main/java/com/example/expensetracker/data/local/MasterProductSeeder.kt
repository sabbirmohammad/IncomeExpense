package com.example.expensetracker.data.local

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.example.expensetracker.common.UnitConverterEngine
import com.example.expensetracker.ui.dashboard.getMarketItemCategory

internal object MasterProductSeeder {

    private data class DefaultCommodity(
        val name: String,
        val category: String,
        val defaultUnit: String,
        val benchmarkPrice: Double
    )

    /**
     * Seeds essential standard Bangladeshi daily groceries if the master catalog is empty.
     */
    fun seedDefaultStaplesIfEmpty(db: SQLiteDatabase) {
        val checkCursor = db.rawQuery("SELECT COUNT(*) FROM $TABLE_MASTER_PRODUCTS", null)
        var count = 0
        checkCursor.use {
            if (it.moveToFirst()) count = it.getInt(0)
        }
        if (count > 0) return

        val now = System.currentTimeMillis()
        val defaultStaples = listOf(
            // Produce / Veggies (কাঁচাবাজার)
            DefaultCommodity("আলু", "veggies", "কেজি", 55.0),
            DefaultCommodity("দেশি পেঁয়াজ", "veggies", "কেজি", 110.0),
            DefaultCommodity("কাঁচামরিচ", "veggies", "কেজি", 200.0),
            DefaultCommodity("রসুন", "veggies", "কেজি", 220.0),
            DefaultCommodity("আদা", "veggies", "কেজি", 240.0),
            DefaultCommodity("টমেটো", "veggies", "কেজি", 120.0),
            DefaultCommodity("বেগুন", "veggies", "কেজি", 80.0),
            DefaultCommodity("ধনেপাতা", "veggies", "কেজি", 150.0),
            DefaultCommodity("লেবু", "veggies", "পিস", 10.0),

            // Meat & Fish (মাছ ও মাংস)
            DefaultCommodity("ব্রয়লার মুরগি", "meat_fish", "কেজি", 190.0),
            DefaultCommodity("গরুর মাংস", "meat_fish", "কেজি", 750.0),
            DefaultCommodity("রুই মাছ", "meat_fish", "কেজি", 350.0),
            DefaultCommodity("তেলাপিয়া মাছ", "meat_fish", "কেজি", 220.0),
            DefaultCommodity("পাঙ্গাস মাছ", "meat_fish", "কেজি", 180.0),

            // Dairy & Eggs (দুধ ও ডিম)
            DefaultCommodity("ফার্মের ডিম", "dairy_bakery", "পিস", 12.5),
            DefaultCommodity("তরল দুধ", "dairy_bakery", "লিটার", 90.0),

            // Dry Groceries & Spices (মুদি ও মশলা)
            DefaultCommodity("মিনিকেট চাল", "groceries", "কেজি", 72.0),
            DefaultCommodity("নাজিরশাইল চাল", "groceries", "কেজি", 78.0),
            DefaultCommodity("সয়াবিন তেল", "groceries", "লিটার", 168.0),
            DefaultCommodity("মসুর ডাল", "groceries", "কেজি", 130.0),
            DefaultCommodity("সাদা চিনি", "groceries", "কেজি", 130.0),
            DefaultCommodity("আয়োডিনযুক্ত লবণ", "groceries", "কেজি", 40.0),
            DefaultCommodity("হলুদ গুঁড়া", "groceries", "কেজি", 360.0),
            DefaultCommodity("মরিচ গুঁড়া", "groceries", "কেজি", 420.0),
            DefaultCommodity("জিরা", "groceries", "কেজি", 850.0)
        )

        db.beginTransaction()
        try {
            for (item in defaultStaples) {
                val cleanName = UnitConverterEngine.normalizeProductName(item.name)
                val cv = ContentValues().apply {
                    put(COL_MP_NAME, item.name)
                    put(COL_MP_NORMALIZED_NAME, cleanName)
                    put(COL_MP_CATEGORY, item.category)
                    put(COL_MP_DEFAULT_UNIT, item.defaultUnit)
                    put(COL_MP_LATEST_UNIT_PRICE, item.benchmarkPrice)
                    putNull(COL_MP_PREVIOUS_UNIT_PRICE)
                    put(COL_MP_MIN_UNIT_PRICE, item.benchmarkPrice)
                    put(COL_MP_MAX_UNIT_PRICE, item.benchmarkPrice)
                    put(COL_MP_AVG_UNIT_PRICE, item.benchmarkPrice)
                    put(COL_MP_PURCHASE_COUNT, 3)
                    put(COL_MP_IS_STAPLE, 1)
                    put(COL_MP_FIRST_PURCHASED_AT, now)
                    put(COL_MP_LAST_PURCHASED_AT, now)
                }
                db.insertWithOnConflict(TABLE_MASTER_PRODUCTS, null, cv, SQLiteDatabase.CONFLICT_IGNORE)
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    /**
     * Backfills master products from existing price history records if present.
     */
    fun backfillFromPriceHistory(
        db: SQLiteDatabase,
        getProductByNormalizedName: (SQLiteDatabase, String) -> com.example.expensetracker.data.model.MasterProduct?
    ) {
        val checkCursor = db.rawQuery("SELECT COUNT(*) FROM $TABLE_MASTER_PRODUCTS", null)
        var count = 0
        checkCursor.use {
            if (it.moveToFirst()) count = it.getInt(0)
        }
        if (count > 0) return

        val historyCursor = db.query(
            TABLE_PRODUCT_PRICE_HISTORY,
            null,
            null,
            null,
            null,
            null,
            "$COL_PPH_TIMESTAMP ASC"
        )

        historyCursor.use { cursor ->
            while (cursor.moveToNext()) {
                val rawName = cursor.getString(cursor.getColumnIndexOrThrow(COL_PPH_PRODUCT_NAME)) ?: ""
                val cleanName = UnitConverterEngine.normalizeProductName(rawName)
                if (cleanName.isBlank()) continue

                val baseUnit = cursor.getString(cursor.getColumnIndexOrThrow(COL_PPH_BASE_UNIT)) ?: "কেজি"
                val unitPrice = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_PPH_UNIT_PRICE))
                val timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(COL_PPH_TIMESTAMP))

                val existing = getProductByNormalizedName(db, cleanName)
                if (existing == null) {
                    val category = getMarketItemCategory(cleanName)
                    val cv = ContentValues().apply {
                        put(COL_MP_NAME, rawName.trim())
                        put(COL_MP_NORMALIZED_NAME, cleanName)
                        put(COL_MP_CATEGORY, category)
                        put(COL_MP_DEFAULT_UNIT, baseUnit)
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
                    db.insertWithOnConflict(TABLE_MASTER_PRODUCTS, null, cv, SQLiteDatabase.CONFLICT_REPLACE)
                } else {
                    val newCount = existing.purchaseCount + 1
                    val newMin = minOf(existing.minUnitPrice, unitPrice)
                    val newMax = maxOf(existing.maxUnitPrice, unitPrice)
                    val newAvg = if (newCount > 0) ((existing.avgUnitPrice * existing.purchaseCount) + unitPrice) / newCount else unitPrice
                    val isStaple = if (newCount >= 3) 1 else (if (existing.isStaple) 1 else 0)

                    val cv = ContentValues().apply {
                        put(COL_MP_LATEST_UNIT_PRICE, unitPrice)
                        put(COL_MP_PREVIOUS_UNIT_PRICE, existing.latestUnitPrice)
                        put(COL_MP_MIN_UNIT_PRICE, newMin)
                        put(COL_MP_MAX_UNIT_PRICE, newMax)
                        put(COL_MP_AVG_UNIT_PRICE, newAvg)
                        put(COL_MP_PURCHASE_COUNT, newCount)
                        put(COL_MP_IS_STAPLE, isStaple)
                        put(COL_MP_LAST_PURCHASED_AT, timestamp)
                    }
                    db.update(TABLE_MASTER_PRODUCTS, cv, "$COL_MP_ID = ?", arrayOf(existing.id.toString()))
                }
            }
        }
    }
}
