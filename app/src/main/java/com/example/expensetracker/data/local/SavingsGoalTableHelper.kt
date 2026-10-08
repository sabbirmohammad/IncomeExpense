package com.example.expensetracker.data.local

import android.content.ContentValues
import android.database.Cursor
import com.example.expensetracker.data.model.AccountType
import com.example.expensetracker.data.model.PaymentMethod
import com.example.expensetracker.data.model.SavingsGoal
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionType

internal class SavingsGoalTableHelper(private val dbHelper: ExpenseDatabaseHelper) {

    fun getAllSavingsGoals(): List<SavingsGoal> {
        val db = dbHelper.readableDatabase
        val list = mutableListOf<SavingsGoal>()
        val cursor = db.query(TABLE_SAVINGS_GOALS, null, null, null, null, null, "$COL_GOAL_IS_COMPLETED ASC, $COL_GOAL_CREATED_DATE DESC")
        cursor.use { c ->
            while (c.moveToNext()) {
                list.add(cursorToSavingsGoal(c))
            }
        }
        return list
    }

    fun getSavingsGoalById(id: Long): SavingsGoal? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(TABLE_SAVINGS_GOALS, null, "$COL_GOAL_ID = ?", arrayOf(id.toString()), null, null, null)
        cursor.use { c ->
            if (c.moveToFirst()) {
                return cursorToSavingsGoal(c)
            }
        }
        return null
    }

    fun insertSavingsGoal(goal: SavingsGoal): Long {
        val db = dbHelper.writableDatabase
        val cv = ContentValues().apply {
            put(COL_GOAL_TITLE, goal.title)
            put(COL_GOAL_TARGET_AMOUNT, goal.targetAmount)
            put(COL_GOAL_SAVED_AMOUNT, goal.savedAmount)
            if (goal.targetDateMillis != null) put(COL_GOAL_TARGET_DATE, goal.targetDateMillis) else putNull(COL_GOAL_TARGET_DATE)
            put(COL_GOAL_CREATED_DATE, goal.createdDateMillis)
            put(COL_GOAL_ICON, goal.icon)
            put(COL_GOAL_COLOR, goal.colorHex)
            put(COL_GOAL_NOTES, goal.notes)
            put(COL_GOAL_IS_COMPLETED, if (goal.isCompleted || goal.savedAmount >= goal.targetAmount) 1 else 0)
        }
        return db.insert(TABLE_SAVINGS_GOALS, null, cv)
    }

    fun updateSavingsGoal(goal: SavingsGoal): Int {
        val db = dbHelper.writableDatabase
        val isComplete = goal.isCompleted || (goal.targetAmount > 0 && goal.savedAmount >= goal.targetAmount - 0.001)
        val cv = ContentValues().apply {
            put(COL_GOAL_TITLE, goal.title)
            put(COL_GOAL_TARGET_AMOUNT, goal.targetAmount)
            put(COL_GOAL_SAVED_AMOUNT, goal.savedAmount)
            if (goal.targetDateMillis != null) put(COL_GOAL_TARGET_DATE, goal.targetDateMillis) else putNull(COL_GOAL_TARGET_DATE)
            put(COL_GOAL_ICON, goal.icon)
            put(COL_GOAL_COLOR, goal.colorHex)
            put(COL_GOAL_NOTES, goal.notes)
            put(COL_GOAL_IS_COMPLETED, if (isComplete) 1 else 0)
        }
        return db.update(TABLE_SAVINGS_GOALS, cv, "$COL_GOAL_ID = ?", arrayOf(goal.id.toString()))
    }

    fun deleteSavingsGoal(id: Long): Int {
        val db = dbHelper.writableDatabase
        return db.delete(TABLE_SAVINGS_GOALS, "$COL_GOAL_ID = ?", arrayOf(id.toString()))
    }

    fun addSavingsContribution(
        goalId: Long,
        amount: Double,
        deductFromAccountId: Long? = null,
        note: String = ""
    ): Boolean {
        if (amount <= 0.0) return false
        val goal = getSavingsGoalById(goalId) ?: return false

        val db = dbHelper.writableDatabase
        db.beginTransaction()
        try {
            val newSaved = goal.savedAmount + amount
            val isNowCompleted = newSaved >= goal.targetAmount - 0.001

            val cv = ContentValues().apply {
                put(COL_GOAL_SAVED_AMOUNT, newSaved)
                put(COL_GOAL_IS_COMPLETED, if (isNowCompleted) 1 else 0)
            }
            db.update(TABLE_SAVINGS_GOALS, cv, "$COL_GOAL_ID = ?", arrayOf(goalId.toString()))

            if (deductFromAccountId != null) {
                val (sourceAccountId, sourceAccountName) = dbHelper.resolveAccountForTransaction(db, deductFromAccountId, PaymentMethod.CASH)
                val acc = dbHelper.getAccountById(sourceAccountId)
                val savingsCat = dbHelper.getOrCreateCategoryByName("Savings & Investment", TransactionType.EXPENSE, "Savings", "#10B981")
                val expenseTx = Transaction(
                    id = 0,
                    title = "Saved for: ${goal.title}",
                    amount = amount,
                    type = TransactionType.EXPENSE,
                    categoryId = savingsCat.id,
                    categoryName = savingsCat.name,
                    categoryIcon = savingsCat.icon,
                    categoryColor = savingsCat.colorHex,
                    dateMillis = System.currentTimeMillis(),
                    paymentMethod = when (acc?.type) {
                        AccountType.BANK -> PaymentMethod.BANK_TRANSFER
                        AccountType.MFS -> if (acc.institutionName.contains("bkash", true)) PaymentMethod.BKASH else PaymentMethod.NAGAD
                        else -> PaymentMethod.CASH
                    },
                    notes = if (note.isNotBlank()) note else "Savings deposit: ${goal.title}",
                    accountId = sourceAccountId,
                    accountName = sourceAccountName
                )
                dbHelper.insertTransaction(expenseTx)
            }

            db.setTransactionSuccessful()
            return true
        } catch (_: Exception) {
            return false
        } finally {
            db.endTransaction()
        }
    }

    private fun cursorToSavingsGoal(c: Cursor): SavingsGoal {
        val targetDateIndex = c.getColumnIndexOrThrow(COL_GOAL_TARGET_DATE)
        val targetDate = if (c.isNull(targetDateIndex)) null else c.getLong(targetDateIndex)
        return SavingsGoal(
            id = c.getLong(c.getColumnIndexOrThrow(COL_GOAL_ID)),
            title = c.getString(c.getColumnIndexOrThrow(COL_GOAL_TITLE)),
            targetAmount = c.getDouble(c.getColumnIndexOrThrow(COL_GOAL_TARGET_AMOUNT)),
            savedAmount = c.getDouble(c.getColumnIndexOrThrow(COL_GOAL_SAVED_AMOUNT)),
            targetDateMillis = targetDate,
            createdDateMillis = c.getLong(c.getColumnIndexOrThrow(COL_GOAL_CREATED_DATE)),
            icon = c.getString(c.getColumnIndexOrThrow(COL_GOAL_ICON)),
            colorHex = c.getString(c.getColumnIndexOrThrow(COL_GOAL_COLOR)),
            notes = c.getString(c.getColumnIndexOrThrow(COL_GOAL_NOTES)),
            isCompleted = c.getInt(c.getColumnIndexOrThrow(COL_GOAL_IS_COMPLETED)) == 1
        )
    }
}
