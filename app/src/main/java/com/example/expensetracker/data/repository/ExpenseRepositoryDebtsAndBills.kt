package com.example.expensetracker.data.repository

import com.example.expensetracker.data.model.BillSplitData
import com.example.expensetracker.data.model.Payable
import com.example.expensetracker.data.model.Receivable
import com.example.expensetracker.data.model.RecurringBill
import com.example.expensetracker.data.model.SavingsGoal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// --- Receivables (পাওনা টাকা) ---
suspend fun ExpenseRepository.addReceivable(receivable: Receivable, deductFromAccountId: Long? = null): Long = withContext(Dispatchers.IO) {
    val id = dbHelper.insertReceivable(receivable, deductFromAccountId)
    refreshReceivables()
    if (deductFromAccountId != null) {
        refreshAccounts()
        refreshTransactions()
    }
    id
}

suspend fun ExpenseRepository.updateReceivable(receivable: Receivable): Boolean = withContext(Dispatchers.IO) {
    val rows = dbHelper.updateReceivable(receivable)
    refreshReceivables()
    rows > 0
}

suspend fun ExpenseRepository.deleteReceivable(id: Long): Boolean = withContext(Dispatchers.IO) {
    val rows = dbHelper.deleteReceivable(id)
    refreshReceivables()
    rows > 0
}

suspend fun ExpenseRepository.recordReceivablePayment(
    receivableId: Long,
    amountPaid: Double,
    depositToAccountId: Long?,
    note: String
): Boolean = withContext(Dispatchers.IO) {
    val success = dbHelper.recordReceivablePayment(receivableId, amountPaid, depositToAccountId, note)
    if (success) {
        refreshReceivables()
        refreshAccounts()
        refreshTransactions()
    }
    success
}

// --- Payables (দেনা টাকা) ---
suspend fun ExpenseRepository.addPayable(payable: Payable, depositToAccountId: Long? = null): Long = withContext(Dispatchers.IO) {
    val id = dbHelper.insertPayable(payable, depositToAccountId)
    refreshPayables()
    if (depositToAccountId != null) {
        refreshAccounts()
        refreshTransactions()
    }
    id
}

suspend fun ExpenseRepository.updatePayable(payable: Payable): Boolean = withContext(Dispatchers.IO) {
    val rows = dbHelper.updatePayable(payable)
    refreshPayables()
    rows > 0
}

suspend fun ExpenseRepository.deletePayable(id: Long): Boolean = withContext(Dispatchers.IO) {
    val rows = dbHelper.deletePayable(id)
    refreshPayables()
    rows > 0
}

suspend fun ExpenseRepository.recordPayablePayment(
    payableId: Long,
    amountPaid: Double,
    deductFromAccountId: Long?,
    note: String
): Boolean = withContext(Dispatchers.IO) {
    val success = dbHelper.recordPayablePayment(payableId, amountPaid, deductFromAccountId, note)
    if (success) {
        refreshPayables()
        refreshAccounts()
        refreshTransactions()
    }
    success
}

// --- Savings Goals (সঞ্চয় লক্ষ্যমাত্রা) ---
suspend fun ExpenseRepository.addSavingsGoal(goal: SavingsGoal): Long = withContext(Dispatchers.IO) {
    val id = dbHelper.insertSavingsGoal(goal)
    refreshSavingsGoals()
    id
}

suspend fun ExpenseRepository.updateSavingsGoal(goal: SavingsGoal): Boolean = withContext(Dispatchers.IO) {
    val rows = dbHelper.updateSavingsGoal(goal)
    refreshSavingsGoals()
    rows > 0
}

suspend fun ExpenseRepository.deleteSavingsGoal(id: Long): Boolean = withContext(Dispatchers.IO) {
    val rows = dbHelper.deleteSavingsGoal(id)
    refreshSavingsGoals()
    rows > 0
}

suspend fun ExpenseRepository.addSavingsContribution(
    goalId: Long,
    amount: Double,
    deductFromAccountId: Long? = null,
    note: String = ""
): Boolean = withContext(Dispatchers.IO) {
    val success = dbHelper.addSavingsContribution(goalId, amount, deductFromAccountId, note)
    if (success) {
        refreshSavingsGoals()
        if (deductFromAccountId != null) {
            refreshAccounts()
            refreshTransactions()
        }
    }
    success
}

// --- Recurring Bills & Split ---
suspend fun ExpenseRepository.addRecurringBill(bill: RecurringBill): Long = withContext(Dispatchers.IO) {
    val id = dbHelper.insertRecurringBill(bill)
    refreshRecurringBills()
    id
}

suspend fun ExpenseRepository.updateRecurringBill(bill: RecurringBill): Boolean = withContext(Dispatchers.IO) {
    val rows = dbHelper.updateRecurringBill(bill)
    refreshRecurringBills()
    rows > 0
}

suspend fun ExpenseRepository.deleteRecurringBill(id: Long): Boolean = withContext(Dispatchers.IO) {
    val rows = dbHelper.deleteRecurringBill(id)
    refreshRecurringBills()
    rows > 0
}

suspend fun ExpenseRepository.payRecurringBill(billId: Long, accountId: Long?, accountName: String?): Long = withContext(Dispatchers.IO) {
    val txId = dbHelper.markBillAsPaid(billId, accountId, accountName)
    if (txId > 0) {
        refreshRecurringBills()
        refreshTransactions()
        refreshAccounts()
    }
    txId
}

suspend fun ExpenseRepository.executeBillSplit(splitData: BillSplitData): Boolean = withContext(Dispatchers.IO) {
    val success = dbHelper.executeBillSplit(splitData)
    if (success) {
        refreshTransactions()
        refreshAccounts()
        refreshReceivables()
    }
    success
}
