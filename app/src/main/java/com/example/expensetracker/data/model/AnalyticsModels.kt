package com.example.expensetracker.data.model

import androidx.compose.runtime.Immutable

@Immutable
data class FinancialSummary(
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val netBalance: Double = 0.0,
    val budgetAmount: Double = 0.0,
    val budgetSpent: Double = 0.0,
    val budgetRemaining: Double = 0.0,
    val budgetPercent: Float = 0f,
    val budgetOverspent: Double = 0.0
)

/**
 * Consolidates a list of transactions into a [FinancialSummary],
 * ignoring internal transfers between accounts and debt flows so spending and income metrics stay accurate.
 */
fun List<Transaction>.toFinancialSummary(overallBudget: Double = 0.0): FinancialSummary {
    var totalIncome = 0.0
    var totalExpense = 0.0

    for (tx in this) {
        if (tx.isAccountTransfer() || tx.isDebtFlow()) continue
        if (tx.type == TransactionType.INCOME) {
            totalIncome += tx.amount
        } else {
            totalExpense += tx.amount
        }
    }

    val netBalance = totalIncome - totalExpense
    val budgetRemaining = if (overallBudget > 0) (overallBudget - totalExpense).coerceAtLeast(0.0) else 0.0
    val budgetOverspent = if (overallBudget > 0 && totalExpense > overallBudget) totalExpense - overallBudget else 0.0
    val budgetPercent = if (overallBudget > 0) ((totalExpense / overallBudget).toFloat()).coerceIn(0f, 2f) else 0f

    return FinancialSummary(
        totalIncome = totalIncome,
        totalExpense = totalExpense,
        netBalance = netBalance,
        budgetAmount = overallBudget,
        budgetSpent = totalExpense,
        budgetRemaining = budgetRemaining,
        budgetPercent = budgetPercent,
        budgetOverspent = budgetOverspent
    )
}

@Immutable
data class CategorySpend(
    val categoryId: Long,
    val categoryName: String,
    val categoryIcon: String,
    val categoryColor: String,
    val totalAmount: Double,
    val percentage: Float,
    val transactionCount: Int
)

@Immutable
data class DailySpend(
    val dateMillis: Long,
    val dayLabel: String,
    val income: Double,
    val expense: Double
)

@Immutable
data class AnalyticsOverview(
    val dailyAverageSpend: Double = 0.0,
    val savingsRate: Int = 0,
    val highestExpenseAmount: Double = 0.0,
    val highestExpenseTitle: String = "",
    val totalTransactionsCount: Int = 0,
    val periodChangePercent: Double? = null,
    val isPeriodChangeFavorable: Boolean = true
)

@Immutable
data class PaymentMethodSpend(
    val paymentMethod: PaymentMethod,
    val totalAmount: Double,
    val percentage: Float,
    val transactionCount: Int
)
