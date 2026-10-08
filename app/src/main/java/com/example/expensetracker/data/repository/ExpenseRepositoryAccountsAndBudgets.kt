package com.example.expensetracker.data.repository

import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.Budget
import com.example.expensetracker.data.model.Transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar

// --- Budgets ---
suspend fun ExpenseRepository.saveBudget(budget: Budget): Long = withContext(Dispatchers.IO) {
    val id = dbHelper.saveBudget(budget)
    refreshBudgets()
    triggerAutoSync()
    id
}

suspend fun ExpenseRepository.deleteBudget(id: Long): Boolean = withContext(Dispatchers.IO) {
    val rows = dbHelper.deleteBudget(id)
    refreshBudgets()
    triggerAutoSync()
    rows > 0
}

suspend fun ExpenseRepository.hasBudgetsForMonth(month: Int, year: Int): Boolean = withContext(Dispatchers.IO) {
    dbHelper.hasBudgetsForMonth(month, year)
}

suspend fun ExpenseRepository.copyBudgets(fromMonth: Int, fromYear: Int, toMonth: Int, toYear: Int): Boolean = withContext(Dispatchers.IO) {
    val success = dbHelper.copyBudgets(fromMonth, fromYear, toMonth, toYear)
    if (success) {
        refreshBudgets()
        triggerAutoSync()
    }
    success
}

fun ExpenseRepository.getLastBudgetPromptKey(): String? {
    return dbHelper.getLastBudgetPromptKey()
}

fun ExpenseRepository.markBudgetPromptHandled(key: String) {
    dbHelper.setLastBudgetPromptKey(key)
}

fun ExpenseRepository.getLastBudgetNotifiedKey(): String? {
    return dbHelper.getLastBudgetNotifiedKey()
}

fun ExpenseRepository.markBudgetNotified(key: String) {
    dbHelper.setLastBudgetNotifiedKey(key)
}

suspend fun ExpenseRepository.checkShouldShowUpcomingBudgetPrompt(): Boolean = withContext(Dispatchers.IO) {
    val cal = Calendar.getInstance()
    val currentDay = cal.get(Calendar.DAY_OF_MONTH)
    val maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

    val (targetMonth, targetYear) = if (currentDay == maxDay) {
        val calNext = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, 1) }
        Pair(calNext.get(Calendar.MONTH), calNext.get(Calendar.YEAR))
    } else if (currentDay == 1) {
        Pair(cal.get(Calendar.MONTH), cal.get(Calendar.YEAR))
    } else {
        return@withContext false
    }

    val promptKey = "$targetYear-$targetMonth"
    val lastHandledKey = dbHelper.getLastBudgetPromptKey()
    if (lastHandledKey == promptKey) {
        return@withContext false
    }

    val hasBudgets = dbHelper.hasBudgetsForMonth(targetMonth, targetYear)
    !hasBudgets
}

// --- Accounts ---
suspend fun ExpenseRepository.addAccount(account: Account): Long = withContext(Dispatchers.IO) {
    val id = dbHelper.insertAccount(account)
    refreshAccounts()
    triggerAutoSync()
    id
}

suspend fun ExpenseRepository.updateAccount(account: Account): Boolean = withContext(Dispatchers.IO) {
    val rows = dbHelper.updateAccount(account)
    if (rows > 0) {
        refreshAccounts()
        refreshTransactions()
        refreshRecurringBills()
        triggerAutoSync()
    }
    rows > 0
}

suspend fun ExpenseRepository.getAccountById(id: Long): Account? = withContext(Dispatchers.IO) {
    dbHelper.getAccountById(id)
}

suspend fun ExpenseRepository.deleteAccount(id: Long): Boolean = withContext(Dispatchers.IO) {
    val rows = dbHelper.deleteAccount(id)
    if (rows > 0) {
        refreshAccounts()
        refreshTransactions()
        refreshRecurringBills()
        triggerAutoSync()
    }
    rows > 0
}

suspend fun ExpenseRepository.transferBetweenAccounts(
    fromAccountId: Long,
    toAccountId: Long,
    amount: Double,
    note: String
): Boolean = withContext(Dispatchers.IO) {
    val success = dbHelper.transferBetweenAccounts(fromAccountId, toAccountId, amount, note)
    if (success) {
        refreshAccounts()
        refreshTransactions()
    }
    success
}

suspend fun ExpenseRepository.getTransactionsForAccount(accountId: Long): List<Transaction> = withContext(Dispatchers.IO) {
    dbHelper.getTransactionsForAccount(accountId)
}
