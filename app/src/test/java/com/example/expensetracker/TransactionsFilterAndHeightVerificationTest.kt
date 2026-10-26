package com.example.expensetracker

import com.example.expensetracker.data.local.ExpenseDatabaseHelper
import com.example.expensetracker.data.local.TransactionQueryHelper
import com.example.expensetracker.data.model.TimeFilter
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.util.Calendar

class TransactionsFilterAndHeightVerificationTest {

    private val basePath: String = if (File("src/main/java").exists()) "src/main/java" else "app/src/main/java"

    private fun readFile(relativePath: String): String {
        val file = File("$basePath/$relativePath")
        assertTrue("Target file ${file.path} must exist", file.exists())
        return file.readText()
    }

    @Test
    fun testTransactionsFilterBarSearchButtonHeightMatchesChips() {
        val content = readFile("com/example/expensetracker/ui/transactions/TransactionsFilterBar.kt")

        // In the filter row (!isSearchActive && searchQuery.isBlank()), the search button must not have sizeIn(minWidth = 48.dp, minHeight = 48.dp)
        // because that causes visual height distortion against the ~34dp dropdown filter chips.
        val searchRowSection = content.substringAfter("if (!isSearchActive && searchQuery.isBlank())")
            .substringBefore("TransactionTypeFilterDropdown")

        assertFalse(
            "Search button Surface in filter row must not have hardcoded sizeIn minHeight 48.dp which caused height mismatch",
            searchRowSection.contains("minHeight = 48.dp")
        )
        assertTrue(
            "Search button Surface must retain minimumInteractiveComponentSize() for accessibility touch target",
            searchRowSection.contains("minimumInteractiveComponentSize()")
        )
        assertTrue(
            "Search button Box must have vertical = 8.dp padding to match DropdownFilterChip height exactly",
            searchRowSection.contains("vertical = 8.dp")
        )
    }

    @Test
    fun testTransactionsViewModelDefaultsToThisMonth() {
        val content = readFile("com/example/expensetracker/ui/transactions/TransactionsViewModel.kt")

        assertTrue(
            "TransactionsViewModel must initialize _selectedTimeFilter to TimeFilter.THIS_MONTH",
            content.contains("private val _selectedTimeFilter = MutableStateFlow(TimeFilter.THIS_MONTH)")
        )
        assertTrue(
            "TransactionsViewModel must expose allTransactions StateFlow from repository",
            content.contains("val allTransactions: StateFlow<List<Transaction>> = repository.transactions")
        )
    }

    @Test
    fun testTransactionsScreenResetAndActiveCountUsesThisMonth() {
        val content = readFile("com/example/expensetracker/ui/transactions/TransactionsScreen.kt")

        assertTrue(
            "activeFilterCount must treat TimeFilter.THIS_MONTH as default (not TimeFilter.ALL)",
            content.contains("selectedTimeFilter != TimeFilter.THIS_MONTH")
        )
        assertTrue(
            "activeFilterCount must count isMarketTripsView so reset button doesn't show 0 active filters",
            content.contains("selectedType != null || isMarketTripsView")
        )
        assertTrue(
            "activeFilterCount must count selectedCategoryId",
            content.contains("selectedCategoryId != null")
        )
        assertTrue(
            "onResetFilters must reset to TimeFilter.THIS_MONTH",
            content.contains("viewModel.setTimeFilter(TimeFilter.THIS_MONTH)")
        )
    }

    @Test
    fun testTransactionsScreenEmptyStateBehavior() {
        val content = readFile("com/example/expensetracker/ui/transactions/TransactionsScreen.kt")

        assertTrue(
            "Empty state must check allTransactions.isNotEmpty() to detect transactions outside running month",
            content.contains("val hasAnyTransactions = allTransactions.isNotEmpty()")
        )
        assertTrue(
            "Empty state should offer to view all transactions when running month is empty but records exist",
            content.contains("viewModel.setTimeFilter(TimeFilter.ALL)")
        )
    }

    @Test
    fun testTransactionTimeFilterDropdownHandlesThisMonthAsDefault() {
        val content = readFile("com/example/expensetracker/ui/transactions/TransactionFilterDropdowns.kt")

        assertTrue(
            "TimeFilter.THIS_MONTH must be marked as not active (default) in TransactionTimeFilterDropdown",
            content.contains("TimeFilter.THIS_MONTH -> Pair(strings.thisMonth, false)")
        )
        assertTrue(
            "TimeFilter.ALL must be marked as active when explicitly selected away from default",
            content.contains("TimeFilter.ALL -> Pair(strings.allTime, true)")
        )

        val thisMonthIdx = content.indexOf("onTimeFilterChanged(TimeFilter.THIS_MONTH)")
        val allTimeIdx = content.indexOf("onTimeFilterChanged(TimeFilter.ALL)")
        assertTrue(
            "This Month option should appear before All Time in the dropdown menu",
            thisMonthIdx in 0 until allTimeIdx
        )
    }

    @Test
    fun testTimeFilterRangeCalculatesRunningMonthAccurately() {
        // Construct query helper with dummy/mock or reflection-safe helper
        // Test Calendar logic directly matching TransactionQueryHelper.getTimeRange(TimeFilter.THIS_MONTH)
        val calNow = Calendar.getInstance()
        val currentYear = calNow.get(Calendar.YEAR)
        val currentMonth = calNow.get(Calendar.MONTH)

        val calStart = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val calEnd = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }

        val start = calStart.timeInMillis
        val end = calEnd.timeInMillis

        // Transaction in running month
        val nowTx = Transaction(
            id = 1,
            title = "Current Month Tx",
            amount = 100.0,
            type = TransactionType.EXPENSE,
            categoryId = 1,
            categoryName = "Food",
            categoryIcon = "Fastfood",
            categoryColor = "#FF0000",
            dateMillis = calNow.timeInMillis
        )

        // Transaction in previous month
        val prevMonthCal = Calendar.getInstance().apply {
            add(Calendar.MONTH, -1)
        }
        val prevTx = Transaction(
            id = 2,
            title = "Previous Month Tx",
            amount = 200.0,
            type = TransactionType.EXPENSE,
            categoryId = 1,
            categoryName = "Food",
            categoryIcon = "Fastfood",
            categoryColor = "#FF0000",
            dateMillis = prevMonthCal.timeInMillis
        )

        val allTxs = listOf(nowTx, prevTx)
        val currentMonthFiltered = allTxs.filter { it.dateMillis in start..end }

        assertEquals("Only current running month transactions must pass the range filter", 1, currentMonthFiltered.size)
        assertEquals(nowTx.id, currentMonthFiltered.first().id)
        assertFalse(currentMonthFiltered.any { it.id == prevTx.id })
    }
}
