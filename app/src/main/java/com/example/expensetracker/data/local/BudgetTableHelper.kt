package com.example.expensetracker.data.local

import android.content.ContentValues
import com.example.expensetracker.data.model.Budget

internal class BudgetTableHelper(private val dbHelper: ExpenseDatabaseHelper) {

    fun getBudgets(month: Int, year: Int): List<Budget> {
        val db = dbHelper.readableDatabase
        val list = mutableListOf<Budget>()
        val cursor = db.query(TABLE_BUDGETS, null, "$COL_BUDGET_MONTH = ? AND $COL_BUDGET_YEAR = ?", arrayOf(month.toString(), year.toString()), null, null, "$COL_BUDGET_CAT_NAME ASC")
        cursor.use { c ->
            while (c.moveToNext()) {
                val catIdCol = c.getColumnIndexOrThrow(COL_BUDGET_CAT_ID)
                val catId = if (c.isNull(catIdCol)) null else c.getLong(catIdCol)
                list.add(
                    Budget(
                        id = c.getLong(c.getColumnIndexOrThrow(COL_BUDGET_ID)),
                        categoryId = catId,
                        categoryName = c.getString(c.getColumnIndexOrThrow(COL_BUDGET_CAT_NAME)),
                        amount = c.getDouble(c.getColumnIndexOrThrow(COL_BUDGET_AMOUNT)),
                        month = c.getInt(c.getColumnIndexOrThrow(COL_BUDGET_MONTH)),
                        year = c.getInt(c.getColumnIndexOrThrow(COL_BUDGET_YEAR))
                    )
                )
            }
        }
        return list
    }

    fun getAllBudgets(): List<Budget> {
        val db = dbHelper.readableDatabase
        val list = mutableListOf<Budget>()
        val cursor = db.query(TABLE_BUDGETS, null, null, null, null, null, "$COL_BUDGET_YEAR DESC, $COL_BUDGET_MONTH DESC, $COL_BUDGET_CAT_NAME ASC")
        cursor.use { c ->
            while (c.moveToNext()) {
                val catIdCol = c.getColumnIndexOrThrow(COL_BUDGET_CAT_ID)
                val catId = if (c.isNull(catIdCol)) null else c.getLong(catIdCol)
                list.add(
                    Budget(
                        id = c.getLong(c.getColumnIndexOrThrow(COL_BUDGET_ID)),
                        categoryId = catId,
                        categoryName = c.getString(c.getColumnIndexOrThrow(COL_BUDGET_CAT_NAME)),
                        amount = c.getDouble(c.getColumnIndexOrThrow(COL_BUDGET_AMOUNT)),
                        month = c.getInt(c.getColumnIndexOrThrow(COL_BUDGET_MONTH)),
                        year = c.getInt(c.getColumnIndexOrThrow(COL_BUDGET_YEAR))
                    )
                )
            }
        }
        return list
    }

    fun saveBudget(budget: Budget): Long {
        val db = dbHelper.writableDatabase
        val cv = ContentValues().apply {
            if (budget.categoryId != null) {
                put(COL_BUDGET_CAT_ID, budget.categoryId)
            } else {
                putNull(COL_BUDGET_CAT_ID)
            }
            put(COL_BUDGET_CAT_NAME, budget.categoryName)
            put(COL_BUDGET_AMOUNT, budget.amount)
            put(COL_BUDGET_MONTH, budget.month)
            put(COL_BUDGET_YEAR, budget.year)
        }
        if (budget.id > 0) {
            val rows = db.update(TABLE_BUDGETS, cv, "$COL_BUDGET_ID = ?", arrayOf(budget.id.toString()))
            return if (rows > 0) budget.id else db.insert(TABLE_BUDGETS, null, cv)
        } else {
            val cursor = if (budget.categoryId != null) {
                db.query(
                    TABLE_BUDGETS,
                    arrayOf(COL_BUDGET_ID),
                    "$COL_BUDGET_CAT_ID = ? AND $COL_BUDGET_MONTH = ? AND $COL_BUDGET_YEAR = ?",
                    arrayOf(budget.categoryId.toString(), budget.month.toString(), budget.year.toString()),
                    null, null, null
                )
            } else {
                db.query(
                    TABLE_BUDGETS,
                    arrayOf(COL_BUDGET_ID),
                    "$COL_BUDGET_CAT_ID IS NULL AND $COL_BUDGET_MONTH = ? AND $COL_BUDGET_YEAR = ?",
                    arrayOf(budget.month.toString(), budget.year.toString()),
                    null, null, null
                )
            }
            val existingId = cursor.use { c ->
                if (c.moveToFirst()) c.getLong(0) else null
            }
            return if (existingId != null && existingId > 0) {
                db.update(TABLE_BUDGETS, cv, "$COL_BUDGET_ID = ?", arrayOf(existingId.toString()))
                existingId
            } else {
                db.insert(TABLE_BUDGETS, null, cv)
            }
        }
    }

    fun deleteBudget(id: Long): Int {
        val db = dbHelper.writableDatabase
        return db.delete(TABLE_BUDGETS, "$COL_BUDGET_ID = ?", arrayOf(id.toString()))
    }

    fun hasBudgetsForMonth(month: Int, year: Int): Boolean {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT 1 FROM $TABLE_BUDGETS WHERE $COL_BUDGET_MONTH = ? AND $COL_BUDGET_YEAR = ? LIMIT 1",
            arrayOf(month.toString(), year.toString())
        )
        return cursor.use { it.moveToFirst() }
    }

    fun copyBudgets(fromMonth: Int, fromYear: Int, toMonth: Int, toYear: Int): Boolean {
        val previousBudgets = getBudgets(fromMonth, fromYear)
        if (previousBudgets.isEmpty()) return false
        val db = dbHelper.writableDatabase
        db.beginTransaction()
        try {
            for (b in previousBudgets) {
                val newBudget = Budget(
                    id = 0L,
                    categoryId = b.categoryId,
                    categoryName = b.categoryName,
                    amount = b.amount,
                    month = toMonth,
                    year = toYear
                )
                saveBudget(newBudget)
            }
            db.setTransactionSuccessful()
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        } finally {
            db.endTransaction()
        }
    }
}
