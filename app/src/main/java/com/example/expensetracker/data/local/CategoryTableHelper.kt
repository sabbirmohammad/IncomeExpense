package com.example.expensetracker.data.local

import android.content.ContentValues
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.ui.components.CategoryColorHelper

internal class CategoryTableHelper(private val dbHelper: ExpenseDatabaseHelper) {

    fun getAllCategories(): List<Category> {
        val db = dbHelper.readableDatabase
        val list = mutableListOf<Category>()
        val cursor = db.query(TABLE_CATEGORIES, null, null, null, null, null, "$COL_CAT_NAME ASC")
        var hasDuplicates = false
        val seenColors = mutableSetOf<String>()
        cursor.use { c ->
            while (c.moveToNext()) {
                val colorHex = c.getString(c.getColumnIndexOrThrow(COL_CAT_COLOR))
                val norm = CategoryColorHelper.normalizeHex(colorHex)
                if (norm in seenColors) {
                    hasDuplicates = true
                } else {
                    seenColors.add(norm)
                }
                list.add(
                    Category(
                        id = c.getLong(c.getColumnIndexOrThrow(COL_CAT_ID)),
                        name = c.getString(c.getColumnIndexOrThrow(COL_CAT_NAME)),
                        type = TransactionType.valueOf(c.getString(c.getColumnIndexOrThrow(COL_CAT_TYPE))),
                        icon = c.getString(c.getColumnIndexOrThrow(COL_CAT_ICON)),
                        colorHex = colorHex,
                        isDefault = c.getInt(c.getColumnIndexOrThrow(COL_CAT_IS_DEFAULT)) == 1
                    )
                )
            }
        }
        if (hasDuplicates) {
            CategoryColorReconciler.reconcileAndDeduplicateCategoryColors(dbHelper.writableDatabase)
            return CategoryColorReconciler.fetchCategoriesDirect(dbHelper.readableDatabase)
        }
        return list
    }

    fun insertCategory(cat: Category): Long {
        val db = dbHelper.writableDatabase
        val cv = ContentValues().apply {
            put(COL_CAT_NAME, cat.name)
            put(COL_CAT_TYPE, cat.type.name)
            put(COL_CAT_ICON, cat.icon)
            put(COL_CAT_COLOR, cat.colorHex)
            put(COL_CAT_IS_DEFAULT, if (cat.isDefault) 1 else 0)
        }
        return db.insert(TABLE_CATEGORIES, null, cv)
    }

    fun updateCategory(cat: Category): Int {
        val db = dbHelper.writableDatabase
        db.beginTransaction()
        try {
            val cv = ContentValues().apply {
                put(COL_CAT_NAME, cat.name)
                put(COL_CAT_TYPE, cat.type.name)
                put(COL_CAT_ICON, cat.icon)
                put(COL_CAT_COLOR, cat.colorHex)
            }
            val rows = db.update(TABLE_CATEGORIES, cv, "$COL_CAT_ID = ?", arrayOf(cat.id.toString()))
            if (rows > 0) {
                val txCv = ContentValues().apply {
                    put(COL_TX_CAT_NAME, cat.name)
                    put(COL_TX_CAT_ICON, cat.icon)
                    put(COL_TX_CAT_COLOR, cat.colorHex)
                }
                db.update(TABLE_TRANSACTIONS, txCv, "$COL_TX_CAT_ID = ?", arrayOf(cat.id.toString()))

                val bCv = ContentValues().apply {
                    put(COL_BUDGET_CAT_NAME, cat.name)
                }
                db.update(TABLE_BUDGETS, bCv, "$COL_BUDGET_CAT_ID = ?", arrayOf(cat.id.toString()))

                val billCv = ContentValues().apply {
                    put(COL_BILL_CAT_NAME, cat.name)
                    put(COL_BILL_CAT_ICON, cat.icon)
                    put(COL_BILL_CAT_COLOR, cat.colorHex)
                }
                db.update(TABLE_RECURRING_BILLS, billCv, "$COL_BILL_CAT_ID = ?", arrayOf(cat.id.toString()))
            }
            db.setTransactionSuccessful()
            return rows
        } finally {
            db.endTransaction()
        }
    }

    fun deleteCategory(id: Long): Int {
        val db = dbHelper.writableDatabase
        db.beginTransaction()
        try {
            var catType = TransactionType.EXPENSE
            val cur = db.query(TABLE_CATEGORIES, arrayOf(COL_CAT_TYPE), "$COL_CAT_ID = ?", arrayOf(id.toString()), null, null, null, "1")
            cur.use {
                if (it.moveToFirst()) {
                    val typeStr = it.getString(0)
                    catType = runCatching { TransactionType.valueOf(typeStr) }.getOrDefault(TransactionType.EXPENSE)
                }
            }

            val fallbackName = if (catType == TransactionType.EXPENSE) "Other Expense" else "Other Income"
            val fallback = getOrCreateCategoryByName(
                name = fallbackName,
                type = catType,
                defaultIcon = if (catType == TransactionType.EXPENSE) "MoreHoriz" else "AttachMoney",
                defaultColor = if (catType == TransactionType.EXPENSE) "#64748B" else "#475569"
            )

            val finalFallback = if (fallback.id == id) {
                val altCur = db.query(TABLE_CATEGORIES, null, "$COL_CAT_ID != ? AND $COL_CAT_TYPE = ?", arrayOf(id.toString(), catType.name), null, null, null, "1")
                altCur.use {
                    if (it.moveToFirst()) {
                        Category(
                            id = it.getLong(it.getColumnIndexOrThrow(COL_CAT_ID)),
                            name = it.getString(it.getColumnIndexOrThrow(COL_CAT_NAME)),
                            type = TransactionType.valueOf(it.getString(it.getColumnIndexOrThrow(COL_CAT_TYPE))),
                            icon = it.getString(it.getColumnIndexOrThrow(COL_CAT_ICON)),
                            colorHex = it.getString(it.getColumnIndexOrThrow(COL_CAT_COLOR)),
                            isDefault = it.getInt(it.getColumnIndexOrThrow(COL_CAT_IS_DEFAULT)) == 1
                        )
                    } else fallback
                }
            } else fallback

            if (finalFallback.id != id) {
                val txCv = ContentValues().apply {
                    put(COL_TX_CAT_ID, finalFallback.id)
                    put(COL_TX_CAT_NAME, finalFallback.name)
                    put(COL_TX_CAT_ICON, finalFallback.icon)
                    put(COL_TX_CAT_COLOR, finalFallback.colorHex)
                }
                db.update(TABLE_TRANSACTIONS, txCv, "$COL_TX_CAT_ID = ?", arrayOf(id.toString()))

                val billCv = ContentValues().apply {
                    put(COL_BILL_CAT_ID, finalFallback.id)
                    put(COL_BILL_CAT_NAME, finalFallback.name)
                    put(COL_BILL_CAT_ICON, finalFallback.icon)
                    put(COL_BILL_CAT_COLOR, finalFallback.colorHex)
                }
                db.update(TABLE_RECURRING_BILLS, billCv, "$COL_BILL_CAT_ID = ?", arrayOf(id.toString()))
            }

            db.delete(TABLE_BUDGETS, "$COL_BUDGET_CAT_ID = ?", arrayOf(id.toString()))
            val rows = db.delete(TABLE_CATEGORIES, "$COL_CAT_ID = ?", arrayOf(id.toString()))
            db.setTransactionSuccessful()
            return rows
        } finally {
            db.endTransaction()
        }
    }

    fun getOrCreateCategoryByName(
        name: String,
        type: TransactionType,
        defaultIcon: String = "MoreHoriz",
        defaultColor: String = "#8B90A0"
    ): Category {
        val db = dbHelper.writableDatabase
        val cursor = db.query(TABLE_CATEGORIES, null, "$COL_CAT_NAME = ? AND $COL_CAT_TYPE = ?", arrayOf(name, type.name), null, null, null, "1")
        cursor.use { c ->
            if (c.moveToFirst()) {
                return Category(
                    id = c.getLong(c.getColumnIndexOrThrow(COL_CAT_ID)),
                    name = c.getString(c.getColumnIndexOrThrow(COL_CAT_NAME)),
                    type = TransactionType.valueOf(c.getString(c.getColumnIndexOrThrow(COL_CAT_TYPE))),
                    icon = c.getString(c.getColumnIndexOrThrow(COL_CAT_ICON)),
                    colorHex = c.getString(c.getColumnIndexOrThrow(COL_CAT_COLOR)),
                    isDefault = c.getInt(c.getColumnIndexOrThrow(COL_CAT_IS_DEFAULT)) == 1
                )
            }
        }

        val curFallback = db.query(TABLE_CATEGORIES, null, "$COL_CAT_NAME = ?", arrayOf(name), null, null, null, "1")
        curFallback.use { c ->
            if (c.moveToFirst()) {
                return Category(
                    id = c.getLong(c.getColumnIndexOrThrow(COL_CAT_ID)),
                    name = c.getString(c.getColumnIndexOrThrow(COL_CAT_NAME)),
                    type = TransactionType.valueOf(c.getString(c.getColumnIndexOrThrow(COL_CAT_TYPE))),
                    icon = c.getString(c.getColumnIndexOrThrow(COL_CAT_ICON)),
                    colorHex = c.getString(c.getColumnIndexOrThrow(COL_CAT_COLOR)),
                    isDefault = c.getInt(c.getColumnIndexOrThrow(COL_CAT_IS_DEFAULT)) == 1
                )
            }
        }

        val cv = ContentValues().apply {
            put(COL_CAT_NAME, name)
            put(COL_CAT_TYPE, type.name)
            put(COL_CAT_ICON, defaultIcon)
            put(COL_CAT_COLOR, defaultColor)
            put(COL_CAT_IS_DEFAULT, 0)
        }
        val newId = db.insert(TABLE_CATEGORIES, null, cv)
        return Category(id = newId, name = name, type = type, icon = defaultIcon, colorHex = defaultColor, isDefault = false)
    }
}
