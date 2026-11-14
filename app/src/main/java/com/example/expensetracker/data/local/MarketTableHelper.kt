package com.example.expensetracker.data.local

import android.content.ContentValues
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.data.model.MarketList
import com.example.expensetracker.data.model.MarketListWithItems

internal class MarketTableHelper(private val dbHelper: ExpenseDatabaseHelper) {

    private val itemHelper = MarketItemTableHelper(dbHelper)

    fun createMarketList(title: String, createdAt: Long = System.currentTimeMillis()): Long {
        val db = dbHelper.writableDatabase
        val cv = ContentValues().apply {
            put(COL_ML_TITLE, title.ifBlank { "বাজারের ফর্দ" })
            put(COL_ML_CREATED_AT, createdAt)
            put(COL_ML_IS_COMPLETED, 0)
        }
        return db.insert(TABLE_MARKET_LISTS, null, cv)
    }

    fun getActiveMarketList(): MarketListWithItems? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(TABLE_MARKET_LISTS, null, "$COL_ML_IS_COMPLETED = 0", null, null, null, "$COL_ML_CREATED_AT DESC", "1")
        var marketList: MarketList? = null
        cursor.use {
            if (it.moveToFirst()) {
                marketList = MarketList(
                    id = it.getLong(it.getColumnIndexOrThrow(COL_ML_ID)),
                    title = it.getString(it.getColumnIndexOrThrow(COL_ML_TITLE)),
                    createdAt = it.getLong(it.getColumnIndexOrThrow(COL_ML_CREATED_AT)),
                    isCompleted = it.getInt(it.getColumnIndexOrThrow(COL_ML_IS_COMPLETED)) == 1
                )
            }
        }
        if (marketList == null) return null

        val items = getMarketItemsForList(marketList.id)
        return MarketListWithItems(list = marketList, items = items)
    }

    fun getActiveMarketLists(): List<MarketListWithItems> {
        val db = dbHelper.readableDatabase
        val result = mutableListOf<MarketListWithItems>()
        val cursor = db.query(
            TABLE_MARKET_LISTS,
            null,
            "$COL_ML_IS_COMPLETED = 0",
            null,
            null,
            null,
            "$COL_ML_CREATED_AT DESC"
        )
        val lists = mutableListOf<MarketList>()
        cursor.use {
            while (it.moveToNext()) {
                lists.add(
                    MarketList(
                        id = it.getLong(it.getColumnIndexOrThrow(COL_ML_ID)),
                        title = it.getString(it.getColumnIndexOrThrow(COL_ML_TITLE)),
                        createdAt = it.getLong(it.getColumnIndexOrThrow(COL_ML_CREATED_AT)),
                        isCompleted = it.getInt(it.getColumnIndexOrThrow(COL_ML_IS_COMPLETED)) == 1
                    )
                )
            }
        }
        for (list in lists) {
            val items = getMarketItemsForList(list.id)
            result.add(MarketListWithItems(list, items))
        }
        return result
    }

    fun getMarketListById(listId: Long): MarketListWithItems? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            TABLE_MARKET_LISTS,
            null,
            "$COL_ML_ID = ?",
            arrayOf(listId.toString()),
            null,
            null,
            null
        )
        var marketList: MarketList? = null
        cursor.use {
            if (it.moveToFirst()) {
                marketList = MarketList(
                    id = it.getLong(it.getColumnIndexOrThrow(COL_ML_ID)),
                    title = it.getString(it.getColumnIndexOrThrow(COL_ML_TITLE)),
                    createdAt = it.getLong(it.getColumnIndexOrThrow(COL_ML_CREATED_AT)),
                    isCompleted = it.getInt(it.getColumnIndexOrThrow(COL_ML_IS_COMPLETED)) == 1
                )
            }
        }
        if (marketList == null) return null
        val items = getMarketItemsForList(marketList.id)
        return MarketListWithItems(list = marketList, items = items)
    }

    fun updateMarketListTitle(listId: Long, title: String): Boolean {
        val db = dbHelper.writableDatabase
        val cv = ContentValues().apply {
            put(COL_ML_TITLE, title.trim().ifBlank { "বাজারের ফর্দ" })
        }
        return db.update(TABLE_MARKET_LISTS, cv, "$COL_ML_ID = ?", arrayOf(listId.toString())) > 0
    }

    fun getAllMarketLists(): List<MarketListWithItems> {
        val db = dbHelper.readableDatabase
        val result = mutableListOf<MarketListWithItems>()
        val cursor = db.query(TABLE_MARKET_LISTS, null, null, null, null, null, "$COL_ML_CREATED_AT DESC")
        val lists = mutableListOf<MarketList>()
        cursor.use {
            while (it.moveToNext()) {
                lists.add(
                    MarketList(
                        id = it.getLong(it.getColumnIndexOrThrow(COL_ML_ID)),
                        title = it.getString(it.getColumnIndexOrThrow(COL_ML_TITLE)),
                        createdAt = it.getLong(it.getColumnIndexOrThrow(COL_ML_CREATED_AT)),
                        isCompleted = it.getInt(it.getColumnIndexOrThrow(COL_ML_IS_COMPLETED)) == 1
                    )
                )
            }
        }
        for (list in lists) {
            val items = getMarketItemsForList(list.id)
            result.add(MarketListWithItems(list, items))
        }
        return result
    }

    fun completeMarketList(listId: Long): Boolean {
        val db = dbHelper.writableDatabase
        val cv = ContentValues().apply {
            put(COL_ML_IS_COMPLETED, 1)
        }
        val updated = db.update(TABLE_MARKET_LISTS, cv, "$COL_ML_ID = ?", arrayOf(listId.toString())) > 0
        if (updated) {
            dbHelper.deleteMarketListBudget(listId)
        }
        return updated
    }

    fun deleteMarketList(listId: Long): Boolean {
        val db = dbHelper.writableDatabase
        db.beginTransaction()
        try {
            db.delete(TABLE_MARKET_ITEMS, "$COL_MI_LIST_ID = ?", arrayOf(listId.toString()))
            val rows = db.delete(TABLE_MARKET_LISTS, "$COL_ML_ID = ?", arrayOf(listId.toString()))
            db.setTransactionSuccessful()
            if (rows > 0) {
                dbHelper.deleteMarketListBudget(listId)
            }
            return rows > 0
        } finally {
            db.endTransaction()
        }
    }

    // Delegations to MarketItemTableHelper
    fun getMarketItemsForList(listId: Long): List<MarketItem> = itemHelper.getMarketItemsForList(listId)
    fun addMarketItem(item: MarketItem): Long = itemHelper.addMarketItem(item)
    fun addMarketItems(listId: Long, items: List<MarketItem>) = itemHelper.addMarketItems(listId, items)
    fun updateMarketItem(item: MarketItem): Boolean = itemHelper.updateMarketItem(item)
    fun deleteMarketItem(itemId: Long): Boolean = itemHelper.deleteMarketItem(itemId)
    fun updateMarketItemPurchased(itemId: Long, isPurchased: Boolean, amount: Double?, transactionId: Long?): Boolean =
        itemHelper.updateMarketItemPurchased(itemId, isPurchased, amount, transactionId)
    fun getLastPurchasedRates(): Map<String, Pair<Double, String>> = itemHelper.getLastPurchasedRates()
}
