package com.example.expensetracker.data.repository

import com.example.expensetracker.data.model.AnalyticsOverview
import com.example.expensetracker.data.model.TimeFilter
import com.example.expensetracker.data.model.TransactionType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar

suspend fun ExpenseRepository.getAnalyticsOverview(
    filter: TimeFilter = TimeFilter.THIS_MONTH,
    type: TransactionType = TransactionType.EXPENSE
): AnalyticsOverview = withContext(Dispatchers.IO) {
    val currentTxs = dbHelper.getFilteredTransactions(timeFilter = filter)
        .filter { !it.isAccountTransfer() && !it.isDebtFlow() }
    val filteredTxs = currentTxs.filter { it.type == type }
    val totalIncome = currentTxs.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
    val totalExpense = currentTxs.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }

    val savingsRate = if (totalIncome > 0) {
        (((totalIncome - totalExpense) / totalIncome) * 100).toInt()
    } else 0

    val cal = Calendar.getInstance()
    val daysElapsed = when (filter) {
        TimeFilter.TODAY -> 1
        TimeFilter.THIS_WEEK -> {
            val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
            val firstDay = cal.firstDayOfWeek
            val diff = (dayOfWeek - firstDay + 7) % 7 + 1
            diff.coerceAtLeast(1)
        }
        TimeFilter.THIS_MONTH -> cal.get(Calendar.DAY_OF_MONTH).coerceAtLeast(1)
        TimeFilter.THIS_YEAR -> cal.get(Calendar.DAY_OF_YEAR).coerceAtLeast(1)
        TimeFilter.ALL, TimeFilter.CUSTOM -> 30
    }

    val dailyAvg = if (type == TransactionType.EXPENSE) {
        totalExpense / daysElapsed
    } else {
        totalIncome / daysElapsed
    }

    val highestTx = filteredTxs.maxByOrNull { it.amount }

    var prevPeriodSpend = 0.0
    when (filter) {
        TimeFilter.THIS_MONTH -> {
            val prevCal = (cal.clone() as Calendar).apply {
                add(Calendar.MONTH, -1)
                set(Calendar.DAY_OF_MONTH, 1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val prevStart = prevCal.timeInMillis
            val prevEndCal = (prevCal.clone() as Calendar).apply {
                val maxDay = getActualMaximum(Calendar.DAY_OF_MONTH)
                val targetDay = cal.get(Calendar.DAY_OF_MONTH).coerceAtMost(maxDay)
                set(Calendar.DAY_OF_MONTH, targetDay)
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            }
            val prevEnd = prevEndCal.timeInMillis
            val prevTxs = dbHelper.getTransactionsBetween(prevStart, prevEnd, type = type)
            prevPeriodSpend = prevTxs.filter { !it.isAccountTransfer() && !it.isDebtFlow() }.sumOf { it.amount }
        }
        TimeFilter.THIS_WEEK -> {
            val weekStartCal = (cal.clone() as Calendar).apply {
                set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val dayOfWeekOffset = (cal.get(Calendar.DAY_OF_WEEK) - cal.firstDayOfWeek + 7) % 7
            val prevStartCal = (weekStartCal.clone() as Calendar).apply {
                add(Calendar.WEEK_OF_YEAR, -1)
            }
            val prevEndCal = (prevStartCal.clone() as Calendar).apply {
                add(Calendar.DAY_OF_YEAR, dayOfWeekOffset)
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            }
            val prevStart = prevStartCal.timeInMillis
            val prevEnd = prevEndCal.timeInMillis
            val prevTxs = dbHelper.getTransactionsBetween(prevStart, prevEnd, type = type)
            prevPeriodSpend = prevTxs.filter { !it.isAccountTransfer() && !it.isDebtFlow() }.sumOf { it.amount }
        }
        TimeFilter.TODAY -> {
            val yesterdayStartCal = (cal.clone() as Calendar).apply {
                add(Calendar.DAY_OF_YEAR, -1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val prevStart = yesterdayStartCal.timeInMillis
            val prevEnd = prevStart + (24L * 60 * 60 * 1000) - 1
            val prevTxs = dbHelper.getTransactionsBetween(prevStart, prevEnd, type = type)
            prevPeriodSpend = prevTxs.filter { !it.isAccountTransfer() && !it.isDebtFlow() }.sumOf { it.amount }
        }
        TimeFilter.THIS_YEAR -> {
            val prevYearStartCal = (cal.clone() as Calendar).apply {
                add(Calendar.YEAR, -1)
                set(Calendar.DAY_OF_YEAR, 1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val prevStart = prevYearStartCal.timeInMillis
            val dayOfYear = cal.get(Calendar.DAY_OF_YEAR)
            val prevYearEndCal = (prevYearStartCal.clone() as Calendar).apply {
                val maxDays = getActualMaximum(Calendar.DAY_OF_YEAR)
                set(Calendar.DAY_OF_YEAR, dayOfYear.coerceAtMost(maxDays))
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            }
            val prevEnd = prevYearEndCal.timeInMillis
            val prevTxs = dbHelper.getTransactionsBetween(prevStart, prevEnd, type = type)
            prevPeriodSpend = prevTxs.filter { !it.isAccountTransfer() && !it.isDebtFlow() }.sumOf { it.amount }
        }
        else -> {
            prevPeriodSpend = 0.0
        }
    }

    val currentAmount = if (type == TransactionType.EXPENSE) totalExpense else totalIncome
    val periodChangePercent: Double? = if (prevPeriodSpend > 0) {
        ((currentAmount - prevPeriodSpend) / prevPeriodSpend) * 100.0
    } else null

    val isFavorable = if (type == TransactionType.EXPENSE) {
        (periodChangePercent ?: 0.0) <= 0.0
    } else {
        (periodChangePercent ?: 0.0) >= 0.0
    }

    AnalyticsOverview(
        dailyAverageSpend = dailyAvg,
        savingsRate = savingsRate,
        highestExpenseAmount = highestTx?.amount ?: 0.0,
        highestExpenseTitle = highestTx?.title ?: "None",
        totalTransactionsCount = filteredTxs.size,
        periodChangePercent = periodChangePercent,
        isPeriodChangeFavorable = isFavorable
    )
}
