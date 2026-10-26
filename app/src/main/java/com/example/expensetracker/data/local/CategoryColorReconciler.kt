package com.example.expensetracker.data.local

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.ui.components.CategoryColorHelper

internal object CategoryColorReconciler {

    fun fetchCategoriesDirect(db: SQLiteDatabase): List<Category> {
        val list = mutableListOf<Category>()
        val cursor = db.query(TABLE_CATEGORIES, null, null, null, null, null, "$COL_CAT_NAME ASC")
        cursor.use { c ->
            while (c.moveToNext()) {
                list.add(
                    Category(
                        id = c.getLong(c.getColumnIndexOrThrow(COL_CAT_ID)),
                        name = c.getString(c.getColumnIndexOrThrow(COL_CAT_NAME)),
                        type = TransactionType.valueOf(c.getString(c.getColumnIndexOrThrow(COL_CAT_TYPE))),
                        icon = c.getString(c.getColumnIndexOrThrow(COL_CAT_ICON)),
                        colorHex = c.getString(c.getColumnIndexOrThrow(COL_CAT_COLOR)),
                        isDefault = c.getInt(c.getColumnIndexOrThrow(COL_CAT_IS_DEFAULT)) == 1
                    )
                )
            }
        }
        return list
    }

    fun reconcileAndDeduplicateCategoryColors(db: SQLiteDatabase) {
        db.beginTransaction()
        try {
            val cursor = db.query(TABLE_CATEGORIES, null, null, null, null, null, "$COL_CAT_ID ASC")
            val categories = mutableListOf<Category>()
            cursor.use { c ->
                while (c.moveToNext()) {
                    categories.add(
                        Category(
                            id = c.getLong(c.getColumnIndexOrThrow(COL_CAT_ID)),
                            name = c.getString(c.getColumnIndexOrThrow(COL_CAT_NAME)),
                            type = TransactionType.valueOf(c.getString(c.getColumnIndexOrThrow(COL_CAT_TYPE))),
                            icon = c.getString(c.getColumnIndexOrThrow(COL_CAT_ICON)),
                            colorHex = c.getString(c.getColumnIndexOrThrow(COL_CAT_COLOR)),
                            isDefault = c.getInt(c.getColumnIndexOrThrow(COL_CAT_IS_DEFAULT)) == 1
                        )
                    )
                }
            }

            val claimedColors = mutableSetOf<String>()
            val updatedCategories = mutableListOf<Category>()

            for (cat in categories) {
                val normColor = CategoryColorHelper.normalizeHex(cat.colorHex)
                if (normColor in claimedColors) {
                    val availableColor = CategoryColorHelper.getAvailableColor(updatedCategories)
                    val newCat = cat.copy(colorHex = availableColor)
                    claimedColors.add(CategoryColorHelper.normalizeHex(availableColor))
                    updatedCategories.add(newCat)

                    val cv = ContentValues().apply {
                        put(COL_CAT_COLOR, availableColor)
                    }
                    db.update(TABLE_CATEGORIES, cv, "$COL_CAT_ID = ?", arrayOf(cat.id.toString()))

                    val txCv = ContentValues().apply {
                        put(COL_TX_CAT_COLOR, availableColor)
                    }
                    db.update(TABLE_TRANSACTIONS, txCv, "$COL_TX_CAT_ID = ?", arrayOf(cat.id.toString()))

                    val billCv = ContentValues().apply {
                        put(COL_BILL_CAT_COLOR, availableColor)
                    }
                    db.update(TABLE_RECURRING_BILLS, billCv, "$COL_BILL_CAT_ID = ?", arrayOf(cat.id.toString()))
                } else {
                    claimedColors.add(normColor)
                    updatedCategories.add(cat)
                }
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }
}
