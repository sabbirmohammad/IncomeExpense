package com.example.expensetracker.data.local

import android.content.ContentValues
import com.example.expensetracker.data.model.MarketItem

internal class MarketItemTableHelper(private val dbHelper: ExpenseDatabaseHelper) {

    fun getMarketItemsForList(listId: Long): List<MarketItem> {
        val db = dbHelper.readableDatabase
        val items = mutableListOf<MarketItem>()
        val cursor = db.query(TABLE_MARKET_ITEMS, null, "$COL_MI_LIST_ID = ?", arrayOf(listId.toString()), null, null, "$COL_MI_IS_PURCHASED ASC, $COL_MI_ID ASC")
        cursor.use {
            while (it.moveToNext()) {
                val amountIndex = it.getColumnIndexOrThrow(COL_MI_AMOUNT)
                val txIdIndex = it.getColumnIndexOrThrow(COL_MI_TRANSACTION_ID)
                items.add(
                    MarketItem(
                        id = it.getLong(it.getColumnIndexOrThrow(COL_MI_ID)),
                        listId = it.getLong(it.getColumnIndexOrThrow(COL_MI_LIST_ID)),
                        name = it.getString(it.getColumnIndexOrThrow(COL_MI_NAME)),
                        quantity = it.getDouble(it.getColumnIndexOrThrow(COL_MI_QUANTITY)),
                        unit = it.getString(it.getColumnIndexOrThrow(COL_MI_UNIT)),
                        amount = if (!it.isNull(amountIndex)) it.getDouble(amountIndex) else null,
                        isPurchased = it.getInt(it.getColumnIndexOrThrow(COL_MI_IS_PURCHASED)) == 1,
                        transactionId = if (!it.isNull(txIdIndex)) it.getLong(txIdIndex) else null,
                        createdAt = it.getLong(it.getColumnIndexOrThrow(COL_MI_CREATED_AT))
                    )
                )
            }
        }
        return items
    }

    fun addMarketItem(item: MarketItem): Long {
        val db = dbHelper.writableDatabase
        val cv = ContentValues().apply {
            put(COL_MI_LIST_ID, item.listId)
            put(COL_MI_NAME, item.name.trim())
            put(COL_MI_QUANTITY, item.quantity)
            put(COL_MI_UNIT, item.unit)
            if (item.amount != null) put(COL_MI_AMOUNT, item.amount) else putNull(COL_MI_AMOUNT)
            put(COL_MI_IS_PURCHASED, if (item.isPurchased) 1 else 0)
            if (item.transactionId != null) put(COL_MI_TRANSACTION_ID, item.transactionId) else putNull(COL_MI_TRANSACTION_ID)
            put(COL_MI_CREATED_AT, item.createdAt)
        }
        return db.insert(TABLE_MARKET_ITEMS, null, cv)
    }

    fun addMarketItems(listId: Long, items: List<MarketItem>) {
        val db = dbHelper.writableDatabase
        db.beginTransaction()
        try {
            for (item in items) {
                val cv = ContentValues().apply {
                    put(COL_MI_LIST_ID, listId)
                    put(COL_MI_NAME, item.name.trim())
                    put(COL_MI_QUANTITY, item.quantity)
                    put(COL_MI_UNIT, item.unit)
                    if (item.amount != null) put(COL_MI_AMOUNT, item.amount) else putNull(COL_MI_AMOUNT)
                    put(COL_MI_IS_PURCHASED, if (item.isPurchased) 1 else 0)
                    if (item.transactionId != null) put(COL_MI_TRANSACTION_ID, item.transactionId) else putNull(COL_MI_TRANSACTION_ID)
                    put(COL_MI_CREATED_AT, item.createdAt)
                }
                db.insert(TABLE_MARKET_ITEMS, null, cv)
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun updateMarketItem(item: MarketItem): Boolean {
        val db = dbHelper.writableDatabase
        val cv = ContentValues().apply {
            put(COL_MI_NAME, item.name.trim())
            put(COL_MI_QUANTITY, item.quantity)
            put(COL_MI_UNIT, item.unit)
            if (item.amount != null) put(COL_MI_AMOUNT, item.amount) else putNull(COL_MI_AMOUNT)
            put(COL_MI_IS_PURCHASED, if (item.isPurchased) 1 else 0)
            if (item.transactionId != null) put(COL_MI_TRANSACTION_ID, item.transactionId) else putNull(COL_MI_TRANSACTION_ID)
        }
        return db.update(TABLE_MARKET_ITEMS, cv, "$COL_MI_ID = ?", arrayOf(item.id.toString())) > 0
    }

    fun deleteMarketItem(itemId: Long): Boolean {
        val db = dbHelper.writableDatabase
        return db.delete(TABLE_MARKET_ITEMS, "$COL_MI_ID = ?", arrayOf(itemId.toString())) > 0
    }

    fun updateMarketItemPurchased(itemId: Long, isPurchased: Boolean, amount: Double?, transactionId: Long?): Boolean {
        val db = dbHelper.writableDatabase
        val cv = ContentValues().apply {
            put(COL_MI_IS_PURCHASED, if (isPurchased) 1 else 0)
            if (amount != null) put(COL_MI_AMOUNT, amount) else putNull(COL_MI_AMOUNT)
            if (transactionId != null) put(COL_MI_TRANSACTION_ID, transactionId) else putNull(COL_MI_TRANSACTION_ID)
        }
        return db.update(TABLE_MARKET_ITEMS, cv, "$COL_MI_ID = ?", arrayOf(itemId.toString())) > 0
    }

    fun getLastPurchasedRates(): Map<String, Pair<Double, String>> {
        val db = dbHelper.readableDatabase
        val map = mutableMapOf<String, Pair<Double, String>>()
        val cursor = db.query(
            TABLE_MARKET_ITEMS,
            arrayOf(COL_MI_NAME, COL_MI_AMOUNT, COL_MI_QUANTITY, COL_MI_UNIT),
            "$COL_MI_IS_PURCHASED = 1 AND $COL_MI_AMOUNT > 0 AND $COL_MI_QUANTITY > 0",
            null,
            null,
            null,
            "$COL_MI_ID DESC"
        )
        cursor.use {
            while (it.moveToNext()) {
                val name = it.getString(0)?.trim()?.lowercase() ?: continue
                if (!map.containsKey(name)) {
                    val amount = it.getDouble(1)
                    val quantity = it.getDouble(2)
                    val unit = it.getString(3) ?: ""
                    val unitRate = amount / quantity
                    map[name] = Pair(unitRate, unit)
                }
            }
        }
        return map
    }
}
