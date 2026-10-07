package com.example.expensetracker

import com.example.expensetracker.data.model.BackupPayload
import com.example.expensetracker.data.model.Budget
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.data.model.PaymentMethod
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.data.model.UserProfile
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
        val utcCal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            clear()
            set(2026, Calendar.SEPTEMBER, 5)
        }
        val selectedUtcMillis = utcCal.timeInMillis

        val initialLocalCal = Calendar.getInstance().apply {
            set(Calendar.YEAR, 2026)
            set(Calendar.MONTH, Calendar.SEPTEMBER)
            set(Calendar.DAY_OF_MONTH, 11)
            set(Calendar.HOUR_OF_DAY, 20)
            set(Calendar.MINUTE, 30)
            set(Calendar.SECOND, 0)
        }
        val initialLocalMillis = initialLocalCal.timeInMillis

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
        val profile1 = UserProfile(firstName = "Sabbir", lastName = "Ahmed", name = "Sabbir Ahmed")
        assertEquals("Sabbir", profile1.effectiveFirstName)
        assertEquals("Sabbir", profile1.dashboardFirstNameUpper)
        assertEquals("Sabbir Ahmed", profile1.displayName)
        assertEquals("SA", profile1.initials)

        val profile2 = UserProfile(firstName = "sabbir", lastName = "rahman", name = "sabbir rahman")
        assertEquals("sabbir", profile2.effectiveFirstName)
        assertEquals("sabbir", profile2.dashboardFirstNameUpper)
        assertEquals("sabbir rahman", profile2.displayName)
        assertEquals("SR", profile2.initials)

        val profile3 = UserProfile(firstName = "Tanvir", lastName = "")
        assertEquals("Tanvir", profile3.effectiveFirstName)
        assertEquals("Tanvir", profile3.dashboardFirstNameUpper)
        assertEquals("Tanvir", profile3.displayName)
        assertEquals("TA", profile3.initials)

        val profileLegacy = UserProfile(firstName = "", lastName = "", name = "Johnathan Doe")
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
}
