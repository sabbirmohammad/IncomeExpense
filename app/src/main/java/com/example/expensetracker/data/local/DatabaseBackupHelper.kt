package com.example.expensetracker.data.local

import com.example.expensetracker.data.model.BackupPayload

internal class DatabaseBackupHelper(private val dbHelper: ExpenseDatabaseHelper) {

    fun createBackupPayload(): BackupPayload {
        val profile = dbHelper.getUserProfile()
        return BackupPayload(
            version = 4,
            exportDateMillis = System.currentTimeMillis(),
            profile = profile,
            categories = dbHelper.getAllCategories(),
            transactions = dbHelper.getAllTransactions(),
            budgets = dbHelper.getAllBudgets(),
            accounts = dbHelper.getAllAccounts(),
            receivables = dbHelper.getAllReceivables(),
            recurringBills = dbHelper.getAllRecurringBills(),
            payables = dbHelper.getAllPayables(),
            savingsGoals = dbHelper.getAllSavingsGoals()
        )
    }

    fun restoreFromBackup(payload: BackupPayload): Boolean {
        return DatabaseRestoreHelper.restore(dbHelper.writableDatabase, payload, dbHelper)
    }
}
