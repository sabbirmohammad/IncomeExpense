package com.example.expensetracker.ui.reports

import com.example.expensetracker.data.model.CategorySpend
import com.example.expensetracker.data.model.FinancialSummary
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.data.model.toFinancialSummary
import java.util.Calendar

object ReportsDataComputer {

    fun computeReportData(
        allTxs: List<Transaction>,
        period: ReportPeriod,
        accId: Long?,
        type: TransactionType?,
        customStart: Long,
        customEnd: Long
    ): Triple<List<Transaction>, FinancialSummary, List<CategorySpend>> {
        val (startMillis, endMillis) = when (period) {
            ReportPeriod.THIS_MONTH -> Pair(getStartOfMonth(0), getEndOfMonth(0))
            ReportPeriod.LAST_MONTH -> Pair(getStartOfMonth(-1), getEndOfMonth(-1))
            ReportPeriod.LAST_3_MONTHS -> Pair(getStartOfMonth(-2), getEndOfMonth(0))
            ReportPeriod.THIS_YEAR -> Pair(getStartOfYear(), getEndOfYear())
            ReportPeriod.ALL_TIME -> Pair(0L, Long.MAX_VALUE)
            ReportPeriod.CUSTOM -> Pair(customStart, customEnd)
        }

        val filtered = allTxs.filter { tx ->
            val inDateRange = tx.dateMillis in startMillis..endMillis
            val inAccount = accId == null || tx.accountId == accId
            val inType = type == null || tx.type == type
            inDateRange && inAccount && inType
        }.sortedByDescending { it.dateMillis }

        val summary = filtered.toFinancialSummary()
        val totalExp = summary.totalExpense

        // Category breakdown for expenses
        val expenseTxs = filtered.filter { it.type == TransactionType.EXPENSE && !it.isAccountTransfer() }
        val categorySpends = if (totalExp > 0.0) {
            expenseTxs.groupBy { it.categoryId }
                .map { (_, list) ->
                    val sample = list.first()
                    val catAmount = list.sumOf { it.amount }
                    CategorySpend(
                        categoryId = sample.categoryId,
                        categoryName = sample.categoryName,
                        categoryIcon = sample.categoryIcon,
                        categoryColor = sample.categoryColor,
                        totalAmount = catAmount,
                        percentage = (catAmount / totalExp).toFloat().coerceIn(0f, 1f),
                        transactionCount = list.size
                    )
                }.sortedByDescending { it.totalAmount }
        } else emptyList()

        return Triple(filtered, summary, categorySpends)
    }

    fun getStartOfMonth(monthOffset: Int): Long {
        val cal = Calendar.getInstance()
        cal.add(Calendar.MONTH, monthOffset)
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    fun getEndOfMonth(monthOffset: Int): Long {
        val cal = Calendar.getInstance()
        cal.add(Calendar.MONTH, monthOffset)
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        return cal.timeInMillis
    }

    fun getStartOfYear(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_YEAR, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    fun getEndOfYear(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_YEAR, cal.getActualMaximum(Calendar.DAY_OF_YEAR))
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        return cal.timeInMillis
    }
}
