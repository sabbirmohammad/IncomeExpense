package com.example.expensetracker.data.repository

import com.example.expensetracker.data.local.*
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun ExpenseRepository.addTransaction(tx: Transaction): Long = withContext(Dispatchers.IO) {
    val id = dbHelper.insertTransaction(tx)
    refreshTransactions()
    refreshAccounts()
    refreshBudgets()
    triggerAutoSync()
    id
}

suspend fun ExpenseRepository.updateTransaction(tx: Transaction): Boolean = withContext(Dispatchers.IO) {
    val rows = dbHelper.updateTransaction(tx)
    refreshTransactions()
    refreshAccounts()
    refreshBudgets()
    triggerAutoSync()
    rows > 0
}

suspend fun ExpenseRepository.deleteTransaction(id: Long): Boolean = withContext(Dispatchers.IO) {
    val rows = dbHelper.deleteTransaction(id)
    refreshTransactions()
    refreshAccounts()
    refreshBudgets()
    triggerAutoSync()
    rows > 0
}

suspend fun ExpenseRepository.getOrCreateCategoryByName(
    name: String,
    type: TransactionType,
    defaultIcon: String = "MoreHoriz",
    defaultColor: String = "#8B90A0"
): Category = withContext(Dispatchers.IO) {
    val cat = dbHelper.getOrCreateCategoryByName(name, type, defaultIcon, defaultColor)
    refreshCategories()
    cat
}

suspend fun ExpenseRepository.addCategory(category: Category): Long = withContext(Dispatchers.IO) {
    val id = dbHelper.insertCategory(category)
    if (id > 0) {
        refreshCategories()
        triggerAutoSync()
    }
    id
}

suspend fun ExpenseRepository.updateCategory(category: Category): Boolean = withContext(Dispatchers.IO) {
    val rows = dbHelper.updateCategory(category)
    if (rows > 0) {
        refreshCategories()
        refreshTransactions()
        refreshBudgets()
        refreshRecurringBills()
        triggerAutoSync()
    }
    rows > 0
}

suspend fun ExpenseRepository.deleteCategory(id: Long): Boolean = withContext(Dispatchers.IO) {
    val rows = dbHelper.deleteCategory(id)
    if (rows > 0) {
        refreshCategories()
        refreshTransactions()
        refreshBudgets()
        refreshRecurringBills()
        triggerAutoSync()
    }
    rows > 0
}

suspend fun ExpenseRepository.renameCommodity(oldName: String, newName: String): Int = withContext(Dispatchers.IO) {
    val rows = dbHelper.renameCommodity(oldName, newName)
    if (rows > 0) {
        refreshProductPrices()
        refreshMarketList()
        triggerAutoSync()
    }
    rows
}

suspend fun ExpenseRepository.mergeCommodities(sourceName: String, targetName: String): Int = withContext(Dispatchers.IO) {
    val rows = dbHelper.mergeCommodities(sourceName, targetName)
    if (rows > 0) {
        refreshProductPrices()
        refreshMarketList()
        triggerAutoSync()
    }
    rows
}
