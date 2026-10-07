package com.example.expensetracker

import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.AccountType
import com.example.expensetracker.data.model.BackupPayload
import com.example.expensetracker.data.model.Budget
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.data.model.DebtBookSummary
import com.example.expensetracker.data.model.FinancialSummary
import com.example.expensetracker.data.model.Payable
import com.example.expensetracker.data.model.PayableStatus
import com.example.expensetracker.data.model.PaymentMethod
import com.example.expensetracker.data.model.Receivable
import com.example.expensetracker.data.model.ReceivableStatus
import com.example.expensetracker.data.model.SavingsGoal
import com.example.expensetracker.data.model.TimeFilter
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.data.model.UserProfile
import com.example.expensetracker.data.model.toFinancialSummary
import com.example.expensetracker.theme.AppThemeMode
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.formatCurrency
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExpenseTrackerLogicTest {

    private val json = Json { prettyPrint = true; ignoreUnknownKeys = true; encodeDefaults = true }

    @Test
    fun testCurrencyFormatting() {
        val formattedBDT = formatCurrency(15000.0, "৳")
        assertTrue(formattedBDT.contains("৳") && formattedBDT.contains("15"))

        val formattedUSD = formatCurrency(49.99, "$")
        assertTrue(formattedUSD.contains("$") && formattedUSD.contains("49.99"))
    }

    @Test
    fun testFinancialCalculationAccuracy() {
        val transactions = listOf(
            Transaction(1, "Salary", 50000.0, TransactionType.INCOME, 1, "Salary", "Work", "#10B981", 1000L),
            Transaction(2, "Freelance", 12000.0, TransactionType.INCOME, 2, "Freelance", "Laptop", "#0EA5E9", 2000L),
            Transaction(3, "Groceries", 4500.0, TransactionType.EXPENSE, 3, "Groceries", "ShoppingCart", "#4CAF50", 3000L),
            Transaction(4, "Electricity Bill", 2500.0, TransactionType.EXPENSE, 4, "Bills", "Bolt", "#FF9800", 4000L),
            Transaction(5, "Dinner", 1500.0, TransactionType.EXPENSE, 5, "Food", "Restaurant", "#FF5722", 5000L)
        )

        val totalIncome = transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
        val totalExpense = transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
        val netBalance = totalIncome - totalExpense

        assertEquals(62000.0, totalIncome, 0.001)
        assertEquals(8500.0, totalExpense, 0.001)
        assertEquals(53500.0, netBalance, 0.001)

        val budget = 20000.0
        val remainingBudget = (budget - totalExpense).coerceAtLeast(0.0)
        val budgetPercent = (totalExpense / budget).toFloat()

        assertEquals(11500.0, remainingBudget, 0.001)
        assertEquals(0.425f, budgetPercent, 0.001f)
    }

    @Test
    fun testBackupPayloadSerializationRoundTrip() {
        val payload = BackupPayload(
            version = 1,
            exportDateMillis = 1700000000000L,
            profile = UserProfile(name = "Sabbir", currencyCode = "BDT", currencySymbol = "৳"),
            categories = listOf(
                Category(1, "Food", TransactionType.EXPENSE, "Restaurant", "#FF5722", true),
                Category(2, "Salary", TransactionType.INCOME, "Work", "#10B981", true)
            ),
            transactions = listOf(
                Transaction(10, "Lunch", 350.0, TransactionType.EXPENSE, 1, "Food", "Restaurant", "#FF5722", 1700000001000L, PaymentMethod.CASH, "Quick meal")
            ),
            budgets = listOf(
                Budget(1, null, "Overall Monthly", 25000.0, 8, 2026)
            )
        )

        val serialized = json.encodeToString(payload)
        assertNotNull(serialized)
        assertTrue(serialized.contains("Sabbir"))
        assertTrue(serialized.contains("Lunch"))

        val deserialized = json.decodeFromString<BackupPayload>(serialized)
        assertEquals(payload.version, deserialized.version)
        assertEquals("Sabbir", deserialized.profile.name)
        assertEquals(1, deserialized.transactions.size)
        assertEquals(350.0, deserialized.transactions[0].amount, 0.001)
        assertEquals(PaymentMethod.CASH, deserialized.transactions[0].paymentMethod)
        assertEquals(1, deserialized.budgets.size)
    }

    @Test
    fun testCustomDateUtcToLocalConversion() {
        // Suppose user selected 5th September 2026 in UTC from DatePicker
        val utcCal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            clear()
            set(2026, Calendar.SEPTEMBER, 5)
        }
        val selectedUtcMillis = utcCal.timeInMillis

        // Existing local timestamp (e.g. 8:30 PM on 11th September 2026)
        val initialLocalCal = Calendar.getInstance().apply {
            set(Calendar.YEAR, 2026)
            set(Calendar.MONTH, Calendar.SEPTEMBER)
            set(Calendar.DAY_OF_MONTH, 11)
            set(Calendar.HOUR_OF_DAY, 20)
            set(Calendar.MINUTE, 30)
            set(Calendar.SECOND, 0)
        }
        val initialLocalMillis = initialLocalCal.timeInMillis

        // Perform the date update logic used in AddEditTransactionSheet
        val utcSelected = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            timeInMillis = selectedUtcMillis
        }
        val selYear = utcSelected.get(Calendar.YEAR)
        val selMonth = utcSelected.get(Calendar.MONTH)
        val selDay = utcSelected.get(Calendar.DAY_OF_MONTH)

        val updatedLocal = Calendar.getInstance().apply {
            timeInMillis = initialLocalMillis
            set(Calendar.YEAR, selYear)
            set(Calendar.MONTH, selMonth)
            set(Calendar.DAY_OF_MONTH, selDay)
        }

        assertEquals(2026, updatedLocal.get(Calendar.YEAR))
        assertEquals(Calendar.SEPTEMBER, updatedLocal.get(Calendar.MONTH))
        assertEquals(5, updatedLocal.get(Calendar.DAY_OF_MONTH))
        assertEquals(20, updatedLocal.get(Calendar.HOUR_OF_DAY))
        assertEquals(30, updatedLocal.get(Calendar.MINUTE))
    }

    @Test
    fun testDateGroupingWithPastDates() {
        val nowCal = Calendar.getInstance()
        val todayMillis = nowCal.timeInMillis

        val yesterdayCal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
        val yesterdayMillis = yesterdayCal.timeInMillis

        val lastWeekCal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -7) }
        val lastWeekMillis = lastWeekCal.timeInMillis

        val dayGroupFormat = SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault())

        fun getDateGroupLabel(dateMillis: Long): String {
            val calNow = Calendar.getInstance()
            val calTx = Calendar.getInstance().apply { timeInMillis = dateMillis }

            return if (calNow.get(Calendar.YEAR) == calTx.get(Calendar.YEAR) &&
                calNow.get(Calendar.DAY_OF_YEAR) == calTx.get(Calendar.DAY_OF_YEAR)
            ) {
                "Today"
            } else if (calNow.get(Calendar.YEAR) == calTx.get(Calendar.YEAR) &&
                calNow.get(Calendar.DAY_OF_YEAR) - calTx.get(Calendar.DAY_OF_YEAR) == 1
            ) {
                "Yesterday"
            } else {
                dayGroupFormat.format(Date(dateMillis))
            }
        }

        assertEquals("Today", getDateGroupLabel(todayMillis))
        assertEquals("Yesterday", getDateGroupLabel(yesterdayMillis))
        assertEquals(dayGroupFormat.format(Date(lastWeekMillis)), getDateGroupLabel(lastWeekMillis))

        val txs = listOf(
            Transaction(1, "Today Grocery", 500.0, TransactionType.EXPENSE, 1, "Food", "Restaurant", "#FF5722", todayMillis),
            Transaction(2, "Yesterday Taxi", 200.0, TransactionType.EXPENSE, 2, "Transport", "Car", "#2196F3", yesterdayMillis),
            Transaction(3, "Last Week Utility", 1500.0, TransactionType.EXPENSE, 3, "Bills", "Bolt", "#FF9800", lastWeekMillis)
        )

        val grouped = txs.groupBy { getDateGroupLabel(it.dateMillis) }
        assertEquals(3, grouped.size)
        assertTrue(grouped.containsKey("Today"))
        assertTrue(grouped.containsKey("Yesterday"))
        assertTrue(grouped.containsKey(dayGroupFormat.format(Date(lastWeekMillis))))
    }

    @Test
    fun testUserProfileFirstNameAndCapitalization() {
        // Test standard first and last name
        val profile1 = UserProfile(
            firstName = "Sabbir",
            lastName = "Ahmed",
            name = "Sabbir Ahmed"
        )
        assertEquals("Sabbir", profile1.effectiveFirstName)
        assertEquals("Sabbir", profile1.dashboardFirstNameUpper)
        assertEquals("Sabbir Ahmed", profile1.displayName)
        assertEquals("SA", profile1.initials)

        // Test lowercase name preserves casing on dashboard
        val profile2 = UserProfile(
            firstName = "sabbir",
            lastName = "rahman",
            name = "sabbir rahman"
        )
        assertEquals("sabbir", profile2.effectiveFirstName)
        assertEquals("sabbir", profile2.dashboardFirstNameUpper)
        assertEquals("sabbir rahman", profile2.displayName)
        assertEquals("SR", profile2.initials)

        // Test only first name provided
        val profile3 = UserProfile(
            firstName = "Tanvir",
            lastName = ""
        )
        assertEquals("Tanvir", profile3.effectiveFirstName)
        assertEquals("Tanvir", profile3.dashboardFirstNameUpper)
        assertEquals("Tanvir", profile3.displayName)
        assertEquals("TA", profile3.initials)

        // Test legacy fallback when firstName is empty but name has multiple words
        val profileLegacy = UserProfile(
            firstName = "",
            lastName = "",
            name = "Johnathan Doe"
        )
        assertEquals("Johnathan", profileLegacy.effectiveFirstName)
        assertEquals("Johnathan", profileLegacy.dashboardFirstNameUpper)
        assertEquals("Johnathan Doe", profileLegacy.displayName)
        assertEquals("JD", profileLegacy.initials)
    }

    @Test
    fun testUserProfileWithFirstAndLastNameSerialization() {
        val payload = BackupPayload(
            version = 2,
            exportDateMillis = 1700000000000L,
            profile = UserProfile(
                firstName = "Sabbir",
                lastName = "Ahmed",
                name = "Sabbir Ahmed",
                currencyCode = "BDT",
                currencySymbol = "৳",
                avatarIndex = 3
            ),
            categories = emptyList(),
            transactions = emptyList(),
            budgets = emptyList()
        )

        val serialized = json.encodeToString(payload)
        assertTrue(serialized.contains("firstName") && serialized.contains("Sabbir"))
        assertTrue(serialized.contains("lastName") && serialized.contains("Ahmed"))

        val deserialized = json.decodeFromString<BackupPayload>(serialized)
        assertEquals("Sabbir", deserialized.profile.firstName)
        assertEquals("Ahmed", deserialized.profile.lastName)
        assertEquals("Sabbir Ahmed", deserialized.profile.displayName)
        assertEquals("Sabbir", deserialized.profile.dashboardFirstNameUpper)
        assertEquals(3, deserialized.profile.avatarIndex)
    }

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

