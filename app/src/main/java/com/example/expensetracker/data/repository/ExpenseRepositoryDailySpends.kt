package com.example.expensetracker.data.repository

import com.example.expensetracker.data.model.DailySpend
import com.example.expensetracker.data.model.TimeFilter
import com.example.expensetracker.data.model.TransactionType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

suspend fun ExpenseRepository.getDailySpends(filter: TimeFilter = TimeFilter.THIS_WEEK): List<DailySpend> = withContext(Dispatchers.IO) {
    val txs = dbHelper.getFilteredTransactions(timeFilter = filter)
        .filter { !it.isAccountTransfer() && !it.isDebtFlow() }
    val shortSdf = SimpleDateFormat("d MMM", Locale.getDefault())

    val dayMap = mutableMapOf<String, Pair<Double, Double>>()
    val dateMillisMap = mutableMapOf<String, Long>()

    for (tx in txs) {
        val label = shortSdf.format(Date(tx.dateMillis))
        val current = dayMap.getOrDefault(label, Pair(0.0, 0.0))
        if (tx.type == TransactionType.INCOME) {
            dayMap[label] = Pair(current.first + tx.amount, current.second)
        } else {
            dayMap[label] = Pair(current.first, current.second + tx.amount)
        }
        if (!dateMillisMap.containsKey(label)) {
            dateMillisMap[label] = tx.dateMillis
        }
    }

    dayMap.map { (label, pair) ->
        DailySpend(
            dateMillis = dateMillisMap[label] ?: 0L,
            dayLabel = label,
            income = pair.first,
            expense = pair.second
        )
    }.sortedBy { it.dateMillis }
}

suspend fun ExpenseRepository.getRecentDailySpends(daysCount: Int = 7): List<DailySpend> = withContext(Dispatchers.IO) {
    val sdf = SimpleDateFormat("EEE", Locale.getDefault())
    val cal = Calendar.getInstance()
    val result = ArrayList<DailySpend>(daysCount)

    // End of today
    cal.set(Calendar.HOUR_OF_DAY, 23)
    cal.set(Calendar.MINUTE, 59)
    cal.set(Calendar.SECOND, 59)
    cal.set(Calendar.MILLISECOND, 999)
    val endMillis = cal.timeInMillis

    // Start of (daysCount - 1) days ago
    cal.add(Calendar.DAY_OF_YEAR, -(daysCount - 1))
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    val startMillis = cal.timeInMillis

    val txs = dbHelper.getTransactionsBetween(startMillis, endMillis)
        .filter { !it.isAccountTransfer() && !it.isDebtFlow() }

    val iterCal = Calendar.getInstance().apply { timeInMillis = startMillis }
    val dateObj = Date()
    for (i in 0 until daysCount) {
        val dayStart = iterCal.timeInMillis
        dateObj.time = dayStart
        val label = sdf.format(dateObj)

        iterCal.set(Calendar.HOUR_OF_DAY, 23)
        iterCal.set(Calendar.MINUTE, 59)
        iterCal.set(Calendar.SECOND, 59)
        iterCal.set(Calendar.MILLISECOND, 999)
        val dayEnd = iterCal.timeInMillis

        var inc = 0.0
        var exp = 0.0
        for (tx in txs) {
            if (tx.dateMillis in dayStart..dayEnd) {
                if (tx.type == TransactionType.INCOME) inc += tx.amount
                else if (tx.type == TransactionType.EXPENSE) exp += tx.amount
            }
        }

        result.add(
            DailySpend(
                dateMillis = dayStart,
                dayLabel = label,
                income = inc,
                expense = exp
            )
        )
        iterCal.add(Calendar.MILLISECOND, 1)
    }

    result
}
