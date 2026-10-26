package com.example.expensetracker

import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.AccountType
import com.example.expensetracker.data.model.Payable
import com.example.expensetracker.data.model.PayableStatus
import com.example.expensetracker.data.model.Receivable
import com.example.expensetracker.data.model.ReceivableStatus
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.data.model.UserProfile
import com.example.expensetracker.data.model.toFinancialSummary
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class FinanceIntegrityAndSecurityTest {

    @Test
    fun testDebtFlowAndReconciliationExclusionFromFinancialSummary() {
        val txs = listOf(
            // Legitimate Income
            Transaction(
                id = 1,
                title = "Monthly Salary",
                amount = 50000.0,
                type = TransactionType.INCOME,
                categoryId = 1,
                categoryName = "Salary",
                categoryIcon = "Work",
                categoryColor = "#10B981",
                dateMillis = 1000L
            ),
            // Legitimate Living Expense
            Transaction(
                id = 2,
                title = "Grocery Shopping",
                amount = 12000.0,
                type = TransactionType.EXPENSE,
                categoryId = 2,
                categoryName = "Groceries",
                categoryIcon = "ShoppingCart",
                categoryColor = "#F59E0B",
                dateMillis = 2000L
            ),
            // Borrowed Debt - MUST NOT count as earned income
            Transaction(
                id = 3,
                title = "Borrowed from Friend",
                amount = 20000.0,
                type = TransactionType.INCOME,
                categoryId = 3,
                categoryName = "Loan & Debt Borrowed",
                categoryIcon = "Handshake",
                categoryColor = "#6366F1",
                dateMillis = 3000L
            ),
            // Repaid Debt - MUST NOT count as living expense
            Transaction(
                id = 4,
                title = "Repaid Loan Principal",
                amount = 8000.0,
                type = TransactionType.EXPENSE,
                categoryId = 4,
                categoryName = "Debt Repayment",
                categoryIcon = "Handshake",
                categoryColor = "#6366F1",
                dateMillis = 4000L
            ),
            // Bengali Borrowed Debt
            Transaction(
                id = 5,
                title = "ধার গ্রহণ",
                amount = 5000.0,
                type = TransactionType.INCOME,
                categoryId = 5,
                categoryName = "ধার বা ঋণ গ্রহণ",
                categoryIcon = "Handshake",
                categoryColor = "#6366F1",
                dateMillis = 5000L
            ),
            // Bengali Repaid Debt
            Transaction(
                id = 6,
                title = "ঋণ পরিশোধ",
                amount = 3000.0,
                type = TransactionType.EXPENSE,
                categoryId = 6,
                categoryName = "দেনা বা ঋণ পরিশোধ",
                categoryIcon = "Handshake",
                categoryColor = "#6366F1",
                dateMillis = 6000L
            ),
            // Transfer Out - MUST NOT count as expense
            Transaction(
                id = 7,
                title = "Transfer to bKash",
                amount = 5000.0,
                type = TransactionType.EXPENSE,
                categoryId = 7,
                categoryName = "Transfer",
                categoryIcon = "SwapHoriz",
                categoryColor = "#6C5CE7",
                dateMillis = 7000L
            ),
            // Transfer In - MUST NOT count as income
            Transaction(
                id = 8,
                title = "Transfer from Bank",
                amount = 5000.0,
                type = TransactionType.INCOME,
                categoryId = 8,
                categoryName = "Transfer",
                categoryIcon = "SwapHoriz",
                categoryColor = "#6C5CE7",
                dateMillis = 8000L
            ),
            // Reconciliation Adjustment - MUST NOT count as living expense
            Transaction(
                id = 9,
                title = "Cash Balance Adjustment",
                amount = 250.0,
                type = TransactionType.EXPENSE,
                categoryId = 9,
                categoryName = "Adjustment",
                categoryIcon = "Tune",
                categoryColor = "#64748B",
                dateMillis = 9000L
            )
        )

        // Verify helper methods individually
        assertTrue(txs[2].isDebtFlow())
        assertTrue(txs[3].isDebtFlow())
        assertTrue(txs[4].isDebtFlow())
        assertTrue(txs[5].isDebtFlow())
        assertFalse(txs[0].isDebtFlow())
        assertFalse(txs[1].isDebtFlow())

        assertTrue(txs[6].categoryName.equals("Transfer", ignoreCase = true))
        assertTrue(txs[7].categoryName.equals("Transfer", ignoreCase = true))

        assertTrue(txs[8].isReconciliation())
        assertFalse(txs[1].isReconciliation())

        // Calculate aggregate financial summary
        val summary = txs.toFinancialSummary()

        // Pure income should only be 50,000.0 (Salary)
        assertEquals(50000.0, summary.totalIncome, 0.001)

        // Pure expense should only be 12,000.0 (Groceries)
        assertEquals(12000.0, summary.totalExpense, 0.001)

        // Net balance should be 50,000 - 12,000 = 38,000
        assertEquals(38000.0, summary.netBalance, 0.001)
    }

    @Test
    fun testReceivableAndPayableClampingDefense() {
        val receivable = Receivable(
            id = 1L,
            personName = "Tareq",
            phoneNumber = "01812345678",
            amount = 10000.0,
            amountReceived = 6000.0,
            status = ReceivableStatus.PARTIAL
        )

        val remainingDue = receivable.remainingAmount
        assertEquals(4000.0, remainingDue, 0.001)

        // Attempting to record payment greater than remaining due should be clamped to remainingDue
        val attemptedPayment = 7000.0
        val effectivePayment = attemptedPayment.coerceAtMost(remainingDue)
        assertEquals(4000.0, effectivePayment, 0.001)

        val updatedCollected = receivable.amountReceived + effectivePayment
        val updatedStatus = if (updatedCollected >= receivable.amount) ReceivableStatus.SETTLED else ReceivableStatus.PARTIAL
        assertEquals(10000.0, updatedCollected, 0.001)
        assertEquals(ReceivableStatus.SETTLED, updatedStatus)

        // Test Payable side
        val payable = Payable(
            id = 2L,
            personName = "Shopkeeper",
            phoneNumber = "01912345678",
            amount = 5000.0,
            amountPaid = 3500.0,
            status = PayableStatus.PARTIAL
        )
        val payableDue = payable.remainingAmount
        assertEquals(1500.0, payableDue, 0.001)

        val payableAttempt = 2000.0
        val payableEffective = payableAttempt.coerceAtMost(payableDue)
        assertEquals(1500.0, payableEffective, 0.001)

        val updatedPaid = payable.amountPaid + payableEffective
        val payableUpdatedStatus = if (updatedPaid >= payable.amount) PayableStatus.SETTLED else PayableStatus.PARTIAL
        assertEquals(5000.0, updatedPaid, 0.001)
        assertEquals(PayableStatus.SETTLED, payableUpdatedStatus)
    }

    @Test
    fun testOverdueRecurringBillRollForwardLogic() {
        val now = 1729000000000L // arbitrary fixed point in time
        val pastDue = now - (25 * 86400000L) // 25 days ago

        // 1. Weekly bill overdue by ~3.5 weeks
        val calWeekly = Calendar.getInstance().apply { timeInMillis = pastDue }
        while (calWeekly.timeInMillis <= now) {
            calWeekly.add(Calendar.WEEK_OF_YEAR, 1)
        }
        assertTrue(calWeekly.timeInMillis > now)
        val weeksAdvanced = ((calWeekly.timeInMillis - pastDue) / (7 * 86400000L)).toInt()
        assertEquals(4, weeksAdvanced)

        // 2. Monthly bill overdue by 2 months
        val calMonthly = Calendar.getInstance().apply {
            timeInMillis = now
            add(Calendar.MONTH, -2)
        }
        while (calMonthly.timeInMillis <= now) {
            calMonthly.add(Calendar.MONTH, 1)
        }
        assertTrue(calMonthly.timeInMillis > now)

        // 3. Yearly bill overdue by 1 year
        val calYearly = Calendar.getInstance().apply {
            timeInMillis = now
            add(Calendar.YEAR, -1)
        }
        while (calYearly.timeInMillis <= now) {
            calYearly.add(Calendar.YEAR, 1)
        }
        assertTrue(calYearly.timeInMillis > now)
    }

    @Test
    fun testBackupProfileIntegrity() {
        val userProfile = UserProfile(
            name = "Sabbir",
            currencySymbol = "৳",
            googleAccountEmail = "user@example.com"
        )

        assertEquals("Sabbir", userProfile.name)
        assertEquals("৳", userProfile.currencySymbol)
        assertEquals("user@example.com", userProfile.googleAccountEmail)
    }

    @Test
    fun testAccountDeletionPromotionAndGuard() {
        val accounts = mutableListOf(
            Account(id = 1L, name = "Primary Cash", type = AccountType.CASH, isDefault = true),
            Account(id = 2L, name = "bKash Wallet", type = AccountType.MFS, isDefault = false)
        )

        // Rule 1: Cannot delete if only 1 account exists
        val singleAccountList = listOf(accounts[0])
        val canDeleteSingle = singleAccountList.size > 1
        assertFalse(canDeleteSingle)

        // Rule 2: When deleting default account (id = 1), promote next account (id = 2) to default
        val deletingAccount = accounts[0]
        assertTrue(deletingAccount.isDefault)

        accounts.removeAt(0)
        assertEquals(1, accounts.size)

        // Promote surviving account to default
        val promoted = accounts[0].copy(isDefault = true)
        accounts[0] = promoted
        assertTrue(accounts[0].isDefault)
        assertEquals("bKash Wallet", accounts[0].name)
    }
}
