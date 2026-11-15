package com.example.expensetracker.data.local

import android.content.ContentValues
import com.example.expensetracker.common.UnitConverterEngine
import com.example.expensetracker.data.model.MarketItem

internal fun ProductPriceTableHelper.recordPricesForMarketItems(
    items: List<MarketItem>,
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
