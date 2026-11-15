package com.example.expensetracker.ui.dashboard

import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.AccountType
import com.example.expensetracker.data.model.BillPaymentStatus
import com.example.expensetracker.data.model.RecurringBill
import java.util.Calendar

internal object DashboardSpendCalculator {
    fun calculateDailySafeSpend(budgetRemaining: Double): Double {
        if (budgetRemaining <= 0) return 0.0
        val cal = Calendar.getInstance()
        val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val currentDay = cal.get(Calendar.DAY_OF_MONTH)
        val remainingDays = (daysInMonth - currentDay + 1).coerceAtLeast(1)
        return budgetRemaining / remainingDays
    }

    fun calculateWalletBalance(accounts: List<Account>): Double {
        return accounts.filter { it.type != AccountType.BANK }.sumOf { it.currentBalance }
    }

    fun filterDueSoonBills(bills: List<RecurringBill>): List<RecurringBill> {
        return bills.filter { bill ->
            bill.isActive && (bill.getPaymentStatus() == BillPaymentStatus.DUE_SOON || bill.getPaymentStatus() == BillPaymentStatus.OVERDUE)
        }
    }
}
