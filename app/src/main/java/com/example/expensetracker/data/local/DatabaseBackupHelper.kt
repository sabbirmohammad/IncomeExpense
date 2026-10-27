package com.example.expensetracker.data.local

import com.example.expensetracker.data.model.BackupPayload

internal class DatabaseBackupHelper(private val dbHelper: ExpenseDatabaseHelper) {

    fun createBackupPayload(): BackupPayload {
        val profile = dbHelper.getUserProfile()
        val allMarketListsWithItems = dbHelper.getAllMarketLists()
        val allLists = allMarketListsWithItems.map { it.list }
        val allItems = allMarketListsWithItems.flatMap { it.items }
        val masterProducts = dbHelper.getAllMasterProducts()
        val allPriceHistory = masterProducts.flatMap { dbHelper.getProductPriceHistory(it.name, 100) }.distinctBy { it.id }

        return BackupPayload(
            version = 5,
            exportDateMillis = System.currentTimeMillis(),
            profile = profile,
            categories = dbHelper.getAllCategories(),
            transactions = dbHelper.getAllTransactions(),
            budgets = dbHelper.getAllBudgets(),
            accounts = dbHelper.getAllAccounts(),
            receivables = dbHelper.getAllReceivables(),
            recurringBills = dbHelper.getAllRecurringBills(),
            payables = dbHelper.getAllPayables(),
            savingsGoals = dbHelper.getAllSavingsGoals(),
            marketLists = allLists,
            marketItems = allItems,
            masterProducts = masterProducts,
            productPriceHistory = allPriceHistory
        )
    }

    fun restoreFromBackup(payload: BackupPayload): Boolean {
        return DatabaseRestoreHelper.restore(dbHelper.writableDatabase, payload, dbHelper)
    }
}
