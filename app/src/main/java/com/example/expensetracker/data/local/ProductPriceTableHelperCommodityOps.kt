package com.example.expensetracker.data.local

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.example.expensetracker.common.UnitConverterEngine

internal fun ProductPriceTableHelper.renameCommodity(oldName: String, newName: String): Int {
    val cleanOld = UnitConverterEngine.normalizeProductName(oldName)
    val cleanNew = UnitConverterEngine.normalizeProductName(newName)
    if (cleanOld.isBlank() || cleanNew.isBlank() || cleanOld.equals(cleanNew, ignoreCase = true)) return 0

    val db = dbHelper.writableDatabase
    db.beginTransaction()
    var updatedRows = 0
    try {
        val cvPrice = ContentValues().apply {
            put(COL_PPH_PRODUCT_NAME, cleanNew)
        }
        updatedRows = db.update(
            TABLE_PRODUCT_PRICE_HISTORY,
            cvPrice,
            "$COL_PPH_PRODUCT_NAME = ? COLLATE NOCASE",
            arrayOf(cleanOld)
        )

        val cvMarket = ContentValues().apply {
            put(COL_MI_NAME, newName.trim())
        }
        db.update(
            TABLE_MARKET_ITEMS,
            cvMarket,
            "$COL_MI_NAME = ? COLLATE NOCASE",
            arrayOf(oldName.trim())
        )

        // Reconcile Master Products catalog
        val targetExisting = dbHelper.masterProductHelper.getProductByNormalizedName(db, cleanNew)
        val sourceExisting = dbHelper.masterProductHelper.getProductByNormalizedName(db, cleanOld)
        if (sourceExisting != null) {
            if (targetExisting == null) {
                val cvMp = ContentValues().apply {
                    put(COL_MP_NAME, newName.trim())
                    put(COL_MP_NORMALIZED_NAME, cleanNew)
                }
                db.update(TABLE_MASTER_PRODUCTS, cvMp, "$COL_MP_ID = ?", arrayOf(sourceExisting.id.toString()))
            } else {
                val combinedCount = targetExisting.purchaseCount + sourceExisting.purchaseCount
                val minPrice = minOf(targetExisting.minUnitPrice, sourceExisting.minUnitPrice)
                val maxPrice = maxOf(targetExisting.maxUnitPrice, sourceExisting.maxUnitPrice)
                val avgPrice = if (combinedCount > 0) {
                    ((targetExisting.avgUnitPrice * targetExisting.purchaseCount) + (sourceExisting.avgUnitPrice * sourceExisting.purchaseCount)) / combinedCount
                } else targetExisting.avgUnitPrice
                val cvTarget = ContentValues().apply {
                    put(COL_MP_PURCHASE_COUNT, combinedCount)
                    put(COL_MP_MIN_UNIT_PRICE, minPrice)
                    put(COL_MP_MAX_UNIT_PRICE, maxPrice)
                    put(COL_MP_AVG_UNIT_PRICE, avgPrice)
                }
                db.update(TABLE_MASTER_PRODUCTS, cvTarget, "$COL_MP_ID = ?", arrayOf(targetExisting.id.toString()))
                db.delete(TABLE_MASTER_PRODUCTS, "$COL_MP_ID = ?", arrayOf(sourceExisting.id.toString()))
            }
        }

        db.setTransactionSuccessful()
    } finally {
        db.endTransaction()
    }
    return updatedRows
}

internal fun ProductPriceTableHelper.mergeCommodities(sourceName: String, targetName: String): Int {
    return renameCommodity(sourceName, targetName)
}

internal fun ProductPriceTableHelper.backfillProductPriceHistory(db: SQLiteDatabase) {
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
