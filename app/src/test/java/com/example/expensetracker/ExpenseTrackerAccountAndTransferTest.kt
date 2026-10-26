package com.example.expensetracker

import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.AccountType
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.theme.AppThemeMode
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.formatCurrency
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExpenseTrackerAccountAndTransferTest {

    @Test
    fun testAccountBalanceSynchronizationWithIncomeAndExpense() {
        val initialBalance = 5000.0
        val account = Account(
            id = 1,
            name = "Cash in Hand",
            type = AccountType.CASH,
            initialBalance = initialBalance,
            currentBalance = initialBalance
        )

        val transactions = listOf(
            Transaction(1, "Deposit", 3000.0, TransactionType.INCOME, 1, "Salary", "Work", "#10B981", 1000L, accountId = 1),
            Transaction(2, "Snacks", 250.0, TransactionType.EXPENSE, 2, "Food", "Restaurant", "#FF5722", 2000L, accountId = 1),
            Transaction(3, "Bus Fare", 80.0, TransactionType.EXPENSE, 3, "Transport", "Car", "#2196F3", 3000L, accountId = 1)
        )

        val totalIncome = transactions.filter { it.accountId == account.id && it.type == TransactionType.INCOME }.sumOf { it.amount }
        val totalExpense = transactions.filter { it.accountId == account.id && it.type == TransactionType.EXPENSE }.sumOf { it.amount }
        val syncedBalance = kotlin.math.round((account.initialBalance + totalIncome - totalExpense) * 100.0) / 100.0

        assertEquals(3000.0, totalIncome, 0.001)
        assertEquals(330.0, totalExpense, 0.001)
        assertEquals(7670.0, syncedBalance, 0.001)
    }

    @Test
    fun testCrossAccountTransferMaintainsTotalNetWorth() {
        val bank = Account(1, "Bank Account", AccountType.BANK, initialBalance = 50000.0, currentBalance = 50000.0)
        val bkash = Account(2, "bKash Personal", AccountType.MFS, initialBalance = 10000.0, currentBalance = 10000.0)

        val initialNetWorth = bank.initialBalance + bkash.initialBalance
        assertEquals(60000.0, initialNetWorth, 0.001)

        val transferAmount = 15000.0
        val transferTransactions = listOf(
            Transaction(10, "Transfer to bKash", transferAmount, TransactionType.EXPENSE, 12, "Transfer", "MoreHoriz", "#8B90A0", 1000L, accountId = 1),
            Transaction(11, "Transfer from Bank", transferAmount, TransactionType.INCOME, 19, "Transfer", "AttachMoney", "#8B90A0", 1000L, accountId = 2)
        )

        val bankIncome = transferTransactions.filter { it.accountId == bank.id && it.type == TransactionType.INCOME }.sumOf { it.amount }
        val bankExpense = transferTransactions.filter { it.accountId == bank.id && it.type == TransactionType.EXPENSE }.sumOf { it.amount }
        val bankFinalBalance = bank.initialBalance + bankIncome - bankExpense

        val bkashIncome = transferTransactions.filter { it.accountId == bkash.id && it.type == TransactionType.INCOME }.sumOf { it.amount }
        val bkashExpense = transferTransactions.filter { it.accountId == bkash.id && it.type == TransactionType.EXPENSE }.sumOf { it.amount }
        val bkashFinalBalance = bkash.initialBalance + bkashIncome - bkashExpense

        assertEquals(35000.0, bankFinalBalance, 0.001)
        assertEquals(25000.0, bkashFinalBalance, 0.001)

        val finalNetWorth = bankFinalBalance + bkashFinalBalance
        assertEquals(initialNetWorth, finalNetWorth, 0.001)
    }

    @Test
    fun testTransactionUpdateReversionAndReapplication() {
        val account = Account(1, "Cash in Hand", AccountType.CASH, initialBalance = 5000.0)
        var transactions = mutableListOf(
            Transaction(1, "Grocery", 1200.0, TransactionType.EXPENSE, 1, "Food", "Restaurant", "#FF5722", 1000L, accountId = 1)
        )

        fun calcBalance(): Double {
            val inc = transactions.filter { it.accountId == account.id && it.type == TransactionType.INCOME }.sumOf { it.amount }
            val exp = transactions.filter { it.accountId == account.id && it.type == TransactionType.EXPENSE }.sumOf { it.amount }
            return account.initialBalance + inc - exp
        }

        assertEquals(3800.0, calcBalance(), 0.001)

        // Case 1: Amount increased from 1200 to 1500
        transactions[0] = transactions[0].copy(amount = 1500.0)
        assertEquals(3500.0, calcBalance(), 0.001)

        // Case 2: Transaction reversed and changed from EXPENSE to INCOME (refund)
        transactions[0] = transactions[0].copy(amount = 1500.0, type = TransactionType.INCOME)
        assertEquals(6500.0, calcBalance(), 0.001)

        // Case 3: Transaction deleted
        transactions.clear()
        assertEquals(5000.0, calcBalance(), 0.001)
    }

    @Test
    fun testFloatingPointBalancePrecision() {
        var balance = 1000.0
        val microTransactions = listOf(0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9)
        for (amt in microTransactions) {
            balance -= amt
        }
        val rounded = kotlin.math.round(balance * 100.0) / 100.0
        assertEquals(995.50, rounded, 0.001)
    }

    @Test
    fun testBengaliDigitsConversion() {
        val original = "1234567890"
        val bengali = convertToBengaliDigits(original)
        assertEquals("১২৩৪৫৬৭৮৯০", bengali)

        val formattedBengali = formatCurrency(1250.0, "৳", useBengaliNumerals = true)
        assertTrue(formattedBengali.contains("৳"))
        assertTrue(formattedBengali.contains("১,২৫০") || formattedBengali.contains("১২৫০"))
    }

    @Test
    fun testAppThemeModeParsing() {
        assertEquals(AppThemeMode.SYSTEM, AppThemeMode.fromKey("system"))
        assertEquals(AppThemeMode.DARK, AppThemeMode.fromKey("dark"))
        assertEquals(AppThemeMode.LIGHT, AppThemeMode.fromKey("light"))
        assertEquals(AppThemeMode.AMOLED, AppThemeMode.fromKey("amoled"))
        assertEquals(AppThemeMode.DARK, AppThemeMode.fromKey("unknown_value"))
    }

    @Test
    fun testCustomDateRangeFilter() {
        val startMillis = 1000L
        val endMillis = 5000L
        val txs = listOf(
            Transaction(1, "Old Tx", 100.0, TransactionType.EXPENSE, 1, "Food", "Restaurant", "#FF5722", 500L),
            Transaction(2, "In Range 1", 200.0, TransactionType.EXPENSE, 1, "Food", "Restaurant", "#FF5722", 2000L),
            Transaction(3, "In Range 2", 300.0, TransactionType.EXPENSE, 1, "Food", "Restaurant", "#FF5722", 4000L),
            Transaction(4, "Future Tx", 400.0, TransactionType.EXPENSE, 1, "Food", "Restaurant", "#FF5722", 6000L)
        )

        val filtered = txs.filter { it.dateMillis in startMillis..endMillis }
        assertEquals(2, filtered.size)
        assertEquals(listOf(2L, 3L), filtered.map { it.id })
    }

    @Test
    fun testReconcileBalanceAdjustmentCalculation() {
        val account = Account(1, "Bank Account", AccountType.BANK, initialBalance = 10000.0, currentBalance = 10000.0)

        // Scenario 1: User enters 10500 (found 500 more)
        val enteredBalGain = 10500.0
        val diffGain = enteredBalGain - account.currentBalance
        assertTrue(diffGain > 0)
        assertEquals(500.0, kotlin.math.abs(diffGain), 0.001)
        val adjTypeGain = if (diffGain > 0) TransactionType.INCOME else TransactionType.EXPENSE
        assertEquals(TransactionType.INCOME, adjTypeGain)

        // Scenario 2: User enters 9200 (actual is 800 less)
        val enteredBalLoss = 9200.0
        val diffLoss = enteredBalLoss - account.currentBalance
        assertTrue(diffLoss < 0)
        assertEquals(800.0, kotlin.math.abs(diffLoss), 0.001)
        val adjTypeLoss = if (diffLoss > 0) TransactionType.INCOME else TransactionType.EXPENSE
        assertEquals(TransactionType.EXPENSE, adjTypeLoss)
    }

    @Test
    fun testTransactionWithReceiptImagePath() {
        val txWithoutReceipt = Transaction(
            id = 1L,
            title = "Grocery shopping",
            amount = 1250.0,
            type = TransactionType.EXPENSE,
            categoryId = 2L,
            categoryName = "Groceries",
            categoryIcon = "ShoppingCart",
            categoryColor = "#4CAF50",
            dateMillis = 1700000000000L
        )
        assertEquals(null, txWithoutReceipt.receiptImagePath)

        val txWithReceipt = txWithoutReceipt.copy(receiptImagePath = "/files/receipts/receipt_123.jpg")
        assertEquals("/files/receipts/receipt_123.jpg", txWithReceipt.receiptImagePath)
    }

    @Test
    fun testAccountDeletionPreservesNetWorthByMergingInitialBalance() {
        // Account 1 (Savings): Initial 50,000, 10,000 expense -> Balance = 40,000
        val acc1 = Account(
            id = 1L,
            name = "City Bank Savings",
            type = AccountType.BANK,
            initialBalance = 50000.0,
            currentBalance = 40000.0
        )
        // Account 2 (Cash): Initial 5,000, 2,000 income -> Balance = 7,000
        val acc2 = Account(
            id = 2L,
            name = "Cash in Hand",
            type = AccountType.CASH,
            initialBalance = 5000.0,
            currentBalance = 7000.0
        )

        val preDeletionNetWorth = acc1.currentBalance + acc2.currentBalance
        assertEquals(47000.0, preDeletionNetWorth, 0.001)

        val txListAcc1 = mutableListOf(
            Transaction(1, "Shopping", 10000.0, TransactionType.EXPENSE, 1, "Shopping", "Cart", "#F00", 1000L, accountId = 1L)
        )
        val txListAcc2 = mutableListOf(
            Transaction(2, "Freelance", 2000.0, TransactionType.INCOME, 2, "Salary", "Work", "#0F0", 2000L, accountId = 2L)
        )

        // Simulate Account Deletion Fix:
        // 1. Acc2 absorbs Acc1's initial balance
        val mergedInitialBalance = acc2.initialBalance + acc1.initialBalance
        assertEquals(55000.0, mergedInitialBalance, 0.001)

        // 2. Acc1's transactions migrate to Acc2
        val migratedTxs = txListAcc1.map { it.copy(accountId = acc2.id) }
        val allAcc2Txs = txListAcc2 + migratedTxs

        // 3. Recalculate Acc2 current balance
        val totalInc = allAcc2Txs.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
        val totalExp = allAcc2Txs.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
        val recalculatedAcc2Balance = mergedInitialBalance + totalInc - totalExp

        assertEquals(2000.0, totalInc, 0.001)
        assertEquals(10000.0, totalExp, 0.001)
        assertEquals(47000.0, recalculatedAcc2Balance, 0.001)

        // Post-deletion net worth exactly equals pre-deletion net worth
        val postDeletionNetWorth = recalculatedAcc2Balance
        assertEquals(preDeletionNetWorth, postDeletionNetWorth, 0.001)
    }

    @Test
    fun testMarketTripCompletionAtomicBalanceConsolidation() {
        val initialAccountBalance = 20000.0
        val marketTripItems = listOf(
            Pair("Rice", 1200.0),
            Pair("Fish", 850.0),
            Pair("Vegetables", 350.0)
        )
        val totalMarketSpent = marketTripItems.sumOf { it.second }
        assertEquals(2400.0, totalMarketSpent, 0.001)

        // Individual temporary ticks deducted:
        var interimBalance = initialAccountBalance
        for (item in marketTripItems) {
            interimBalance -= item.second
        }
        assertEquals(17600.0, interimBalance, 0.001)

        // Consolidated transaction replaces individual items:
        // (interim + refunded individual txs - single consolidated tx)
        val consolidatedBalance = interimBalance + totalMarketSpent - totalMarketSpent
        assertEquals(17600.0, consolidatedBalance, 0.001)
    }
}
