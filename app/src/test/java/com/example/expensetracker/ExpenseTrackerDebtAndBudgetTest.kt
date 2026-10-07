package com.example.expensetracker

import com.example.expensetracker.data.model.Budget
import com.example.expensetracker.data.model.DebtBookSummary
import com.example.expensetracker.data.model.Payable
import com.example.expensetracker.data.model.PayableStatus
import com.example.expensetracker.data.model.SavingsGoal
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.data.model.toFinancialSummary
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class ExpenseTrackerDebtAndBudgetTest {

    @Test
    fun testPayableCalculationsAndSettlement() {
        val payable = Payable(
            id = 1L,
            personName = "Rahim",
            phoneNumber = "01700000000",
            amount = 5000.0,
            amountPaid = 2000.0,
            status = PayableStatus.PARTIAL
        )

        assertEquals(3000.0, payable.remainingAmount, 0.001)
        assertEquals(false, payable.isSettled)

        val settledPayable = payable.copy(
            amountPaid = 5000.0,
            status = PayableStatus.SETTLED
        )
        assertEquals(0.0, settledPayable.remainingAmount, 0.001)
        assertEquals(true, settledPayable.isSettled)
    }

    @Test
    fun testSavingsGoalCalculations() {
        val goal = SavingsGoal(
            id = 1L,
            title = "Emergency Fund",
            targetAmount = 50000.0,
            savedAmount = 25000.0
        )

        assertEquals(0.5f, goal.progressPercentage, 0.001f)
        assertEquals(25000.0, goal.remainingAmount, 0.001)
        assertEquals(false, goal.isCompleted)

        val completedGoal = goal.copy(savedAmount = 50000.0, isCompleted = true)
        assertEquals(1.0f, completedGoal.progressPercentage, 0.001f)
        assertEquals(0.0, completedGoal.remainingAmount, 0.001)
        assertEquals(true, completedGoal.isCompleted)
    }

    @Test
    fun testDebtBookNetPositionCalculation() {
        val summary = DebtBookSummary(
            totalReceivable = 15000.0,
            totalPayable = 8000.0
        )

        // Net balance = Receivables - Payables
        assertEquals(7000.0, summary.netBalance, 0.001)
        assertTrue(summary.netBalance > 0) // Net Asset / Surplus

        val deficitSummary = DebtBookSummary(
            totalReceivable = 5000.0,
            totalPayable = 12000.0
        )
        assertEquals(-7000.0, deficitSummary.netBalance, 0.001)
        assertTrue(deficitSummary.netBalance < 0) // Net Debt / Deficit
    }

    @Test
    fun testFinancialStatementKPIsAndSavingsRate() {
        val txs = listOf(
            Transaction(1, "Salary", 40000.0, TransactionType.INCOME, 1, "Salary", "Work", "#10B981", 1000L),
            Transaction(2, "Rent", 12000.0, TransactionType.EXPENSE, 2, "Housing", "Home", "#FF5722", 2000L),
            Transaction(3, "Groceries", 8000.0, TransactionType.EXPENSE, 3, "Groceries", "ShoppingCart", "#4CAF50", 3000L)
        )

        val summary = txs.toFinancialSummary()
        assertEquals(40000.0, summary.totalIncome, 0.001)
        assertEquals(20000.0, summary.totalExpense, 0.001)
        assertEquals(20000.0, summary.netBalance, 0.001)

        val savingsRate = if (summary.totalIncome > 0) {
            ((summary.netBalance / summary.totalIncome) * 100).toInt().coerceIn(-999, 100)
        } else 0

        assertEquals(50, savingsRate)
    }

    @Test
    fun testEndOfMonthBudgetRolloverCalculation() {
        // Test last day detection for September (30 days)
        val calSept = Calendar.getInstance().apply {
            set(Calendar.YEAR, 2026)
            set(Calendar.MONTH, Calendar.SEPTEMBER)
            set(Calendar.DAY_OF_MONTH, 30)
        }
        val isLastDaySept = calSept.get(Calendar.DAY_OF_MONTH) == calSept.getActualMaximum(Calendar.DAY_OF_MONTH)
        assertTrue(isLastDaySept)

        // Target next month calculation
        val calNext = (calSept.clone() as Calendar).apply { add(Calendar.DAY_OF_MONTH, 1) }
        assertEquals(Calendar.OCTOBER, calNext.get(Calendar.MONTH))
        assertEquals(2026, calNext.get(Calendar.YEAR))

        // Test budget copy simulation
        val previousBudgets = listOf(
            Budget(id = 1, categoryId = null, categoryName = "Overall Monthly", amount = 45000.0, month = Calendar.SEPTEMBER, year = 2026),
            Budget(id = 2, categoryId = 10, categoryName = "Groceries", amount = 12000.0, month = Calendar.SEPTEMBER, year = 2026),
            Budget(id = 3, categoryId = 11, categoryName = "Dining", amount = 5000.0, month = Calendar.SEPTEMBER, year = 2026)
        )

        val targetMonth = calNext.get(Calendar.MONTH)
        val targetYear = calNext.get(Calendar.YEAR)

        val copiedBudgets = previousBudgets.map { b ->
            b.copy(id = 0L, month = targetMonth, year = targetYear)
        }

        assertEquals(3, copiedBudgets.size)
        assertEquals(45000.0, copiedBudgets[0].amount, 0.001)
        assertEquals(Calendar.OCTOBER, copiedBudgets[0].month)
        assertEquals(2026, copiedBudgets[0].year)
        assertEquals("Groceries", copiedBudgets[1].categoryName)
        assertEquals(12000.0, copiedBudgets[1].amount, 0.001)
        assertEquals(Calendar.OCTOBER, copiedBudgets[1].month)
    }
}
