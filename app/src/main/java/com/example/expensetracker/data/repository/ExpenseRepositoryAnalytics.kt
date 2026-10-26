package com.example.expensetracker.data.repository

import com.example.expensetracker.data.model.BillPaymentStatus
import com.example.expensetracker.data.model.CategorySpend
import com.example.expensetracker.data.model.FinancialSummary
import com.example.expensetracker.data.model.PaymentMethodSpend
import com.example.expensetracker.data.model.TimeFilter
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.data.model.toFinancialSummary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Locale

suspend fun ExpenseRepository.getAnonymizedFinancialContext(): String = withContext(Dispatchers.IO) {
    val profile = _userProfile.value
    val summary = getFinancialSummary(TimeFilter.THIS_MONTH)
    val topSpends = getCategorySpends(TimeFilter.THIS_MONTH, TransactionType.EXPENSE).take(5)
    val accounts = _accounts.value
    val bills = _recurringBills.value.filter { it.isActive }
    val recSummary = _receivablesSummary.value

    val currency = profile.currencySymbol
    val sb = StringBuilder()
    sb.appendLine("Current Financial Summary (This Month):")
    sb.appendLine("- Total Income: $currency ${summary.totalIncome}")
    sb.appendLine("- Total Expense: $currency ${summary.totalExpense}")
    sb.appendLine("- Net Balance: $currency ${summary.netBalance}")
    if (summary.budgetAmount > 0) {
        sb.appendLine("- Monthly Budget: $currency ${summary.budgetAmount} (Spent: ${String.format(Locale.US, "%.1f", summary.budgetPercent * 100)}%)")
        sb.appendLine("- Budget Remaining: $currency ${summary.budgetRemaining}")
        if (summary.budgetOverspent > 0) {
            sb.appendLine("- Budget Overspent: $currency ${summary.budgetOverspent}")
        }
    }
    if (topSpends.isNotEmpty()) {
        sb.appendLine("- Top Expense Categories:")
        topSpends.forEach { cs ->
            sb.appendLine("  * ${cs.categoryName}: $currency ${cs.totalAmount} (${String.format(Locale.US, "%.1f", cs.percentage * 100)}%)")
        }
    }
    if (accounts.isNotEmpty()) {
        val totalWallet = accounts.sumOf { it.currentBalance }
        sb.appendLine("- Total Available Across Accounts: $currency $totalWallet")
        accounts.forEach { acc ->
            sb.appendLine("  * ${acc.type.displayName}: $currency ${acc.currentBalance}")
        }
    }
    if (bills.isNotEmpty()) {
        val upcomingBills = bills.filter { it.getPaymentStatus() != BillPaymentStatus.PAID }
        if (upcomingBills.isNotEmpty()) {
            sb.appendLine("- Upcoming/Due Bills:")
            upcomingBills.forEach { b ->
                sb.appendLine("  * ${b.title}: $currency ${b.amount} (Status: ${b.getPaymentStatus()})")
            }
        }
    }
    if (recSummary.remainingDue > 0) {
        sb.appendLine("- Money Owed by Others (Receivables): $currency ${recSummary.remainingDue}")
    }
    sb.toString()
}

suspend fun ExpenseRepository.getFilteredTransactions(
    timeFilter: TimeFilter,
    type: TransactionType?,
    categoryId: Long?,
    query: String?,
    accountId: Long? = null
): List<Transaction> = withContext(Dispatchers.IO) {
    dbHelper.getFilteredTransactions(timeFilter, type, categoryId, query, accountId)
}

suspend fun ExpenseRepository.getTransactionsBetween(
    startMillis: Long,
    endMillis: Long,
    type: TransactionType? = null,
    categoryId: Long? = null,
    query: String? = null,
    accountId: Long? = null
): List<Transaction> = withContext(Dispatchers.IO) {
    dbHelper.getTransactionsBetween(startMillis, endMillis, type, categoryId, query, accountId)
}

suspend fun ExpenseRepository.getSummaryForTransactions(txs: List<Transaction>): FinancialSummary = withContext(Dispatchers.Default) {
    txs.toFinancialSummary()
}

suspend fun ExpenseRepository.getFinancialSummary(filter: TimeFilter = TimeFilter.THIS_MONTH): FinancialSummary = withContext(Dispatchers.IO) {
    val cal = Calendar.getInstance()
    val currentBudgets = dbHelper.getBudgets(cal.get(Calendar.MONTH), cal.get(Calendar.YEAR))
    val overallBudget = currentBudgets.find { it.categoryId == null }?.amount
        ?: currentBudgets.sumOf { it.amount }

    dbHelper.getAggregatedFinancialSummary(filter, overallBudget)
}

suspend fun ExpenseRepository.getCategorySpends(
    filter: TimeFilter = TimeFilter.THIS_MONTH,
    type: TransactionType = TransactionType.EXPENSE
): List<CategorySpend> = withContext(Dispatchers.IO) {
    dbHelper.getAggregatedCategorySpends(filter, type)
}

suspend fun ExpenseRepository.getPaymentMethodSpends(
    filter: TimeFilter = TimeFilter.THIS_MONTH,
    type: TransactionType = TransactionType.EXPENSE
): List<PaymentMethodSpend> = withContext(Dispatchers.IO) {
    dbHelper.getAggregatedPaymentMethodSpends(filter, type)
}
