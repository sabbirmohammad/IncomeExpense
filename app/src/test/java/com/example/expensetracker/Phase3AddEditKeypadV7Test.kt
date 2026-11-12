package com.example.expensetracker

import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.AccountType
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.ui.components.AmountKeypadLogic
import com.example.expensetracker.ui.components.CategoryRecommendationEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.util.Calendar

class Phase3AddEditKeypadV7Test {

    private fun findSourceFile(subpath: String): File {
        val normalized = subpath.removePrefix("app/")
        val candidates = listOf(
            File(normalized),
            File("app", normalized),
            File(System.getProperty("user.dir"), normalized),
            File(System.getProperty("user.dir"), "app/$normalized"),
            File("../$normalized")
        )
        return candidates.firstOrNull { it.exists() } ?: File(normalized)
    }

    // =========================================================================
    // 1. AmountKeypadLogic Unit Tests
    // =========================================================================

    @Test
    fun testAmountKeypadAppendDigit() {
        // Appending to empty string
        assertEquals("5", AmountKeypadLogic.appendDigit("", '5'))
        // Replacing lone "0"
        assertEquals("7", AmountKeypadLogic.appendDigit("0", '7'))
        // Consecutive digits
        assertEquals("72", AmountKeypadLogic.appendDigit("7", '2'))
        assertEquals("720", AmountKeypadLogic.appendDigit("72", '0'))
        // Non-digit ignored
        assertEquals("720", AmountKeypadLogic.appendDigit("720", 'x'))
        // After operator with lone "0"
        assertEquals("50+8", AmountKeypadLogic.appendDigit("50+0", '8'))
    }

    @Test
    fun testAmountKeypadAppendDecimal() {
        // From empty
        assertEquals("0.", AmountKeypadLogic.appendDecimal(""))
        // Normal integer
        assertEquals("12.", AmountKeypadLogic.appendDecimal("12"))
        // Cannot double decimal in same number
        assertEquals("12.5", AmountKeypadLogic.appendDecimal("12.5"))
        // Allowed in subsequent operand
        assertEquals("12.5+3.", AmountKeypadLogic.appendDecimal("12.5+3"))
    }

    @Test
    fun testAmountKeypadAppendOperator() {
        // Allowed '-' from empty for negative
        assertEquals("−", AmountKeypadLogic.appendOperator("", '-'))
        // Cannot start with '+'
        assertEquals("", AmountKeypadLogic.appendOperator("", '+'))
        // Normal append
        assertEquals("120+", AmountKeypadLogic.appendOperator("120", '+'))
        // Operator normalization
        assertEquals("120×", AmountKeypadLogic.appendOperator("120", '*'))
        assertEquals("120÷", AmountKeypadLogic.appendOperator("120", '/'))
        // Operator replacement
        assertEquals("120−", AmountKeypadLogic.appendOperator("120+", '-'))
        assertEquals("120×", AmountKeypadLogic.appendOperator("120−", '×'))
        // Drops trailing decimal before operator
        assertEquals("120+", AmountKeypadLogic.appendOperator("120.", '+'))
    }

    @Test
    fun testAmountKeypadBackspaceAndClear() {
        assertEquals("12", AmountKeypadLogic.backspace("123"))
        assertEquals("1", AmountKeypadLogic.backspace("12"))
        assertEquals("", AmountKeypadLogic.backspace("1"))
        assertEquals("", AmountKeypadLogic.backspace(""))
        assertEquals("", AmountKeypadLogic.clear())
    }

    @Test
    fun testAmountKeypadEvaluateMathExpressions() {
        // Simple evaluation
        assertEquals(205.0, AmountKeypadLogic.evaluate("120+85")!!, 0.001)
        assertEquals(175.0, AmountKeypadLogic.evaluate("50×3.5")!!, 0.001)
        assertEquals(25.0, AmountKeypadLogic.evaluate("100÷4")!!, 0.001)
        assertEquals(75.0, AmountKeypadLogic.evaluate("100−25")!!, 0.001)

        // Operator precedence (* and / before + and -)
        assertEquals(70.0, AmountKeypadLogic.evaluate("10+20×3")!!, 0.001)
        assertEquals(5.0, AmountKeypadLogic.evaluate("20÷4+0")!!, 0.001)

        // Decimal operations
        assertEquals(30.75, AmountKeypadLogic.evaluate("15.5+15.25")!!, 0.001)

        // Incomplete / invalid expressions return null
        assertNull(AmountKeypadLogic.evaluate(""))
        assertNull(AmountKeypadLogic.evaluate("120+"))
        assertNull(AmountKeypadLogic.evaluate("100÷0"))
    }

    @Test
    fun testAmountKeypadFormatting() {
        assertEquals("200", AmountKeypadLogic.formatEvaluated(200.0, isBangla = false))
        assertEquals("200.50", AmountKeypadLogic.formatEvaluated(200.5, isBangla = false))
        assertEquals("২০০", AmountKeypadLogic.formatEvaluated(200.0, isBangla = true))
    }

    // =========================================================================
    // 2. CategoryRecommendationEngine Unit Tests
    // =========================================================================

    @Test
    fun testCategoryRecommendationTimeOfDayPriority() {
        val categories = listOf(
            Category(1, "Groceries", TransactionType.EXPENSE, "ShoppingCart", "#10B981"),
            Category(2, "Dinner", TransactionType.EXPENSE, "Restaurant", "#F97316"),
            Category(3, "Transport", TransactionType.EXPENSE, "DirectionsCar", "#0EA5E9"),
            Category(4, "Other", TransactionType.EXPENSE, "MoreHoriz", "#64748B")
        )

        // Create transactions at 20:00 (Dinner) and 09:00 (Groceries)
        val calDinner = Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, 20) }
        val calMorning = Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, 9) }

        val recentTxs = listOf(
            Transaction(id = 10, title = "Evening Dinner", amount = 350.0, type = TransactionType.EXPENSE, categoryId = 2, categoryName = "Dinner", categoryIcon = "Restaurant", categoryColor = "#F97316", dateMillis = calDinner.timeInMillis),
            Transaction(id = 11, title = "Morning Bazaar", amount = 1200.0, type = TransactionType.EXPENSE, categoryId = 1, categoryName = "Groceries", categoryIcon = "ShoppingCart", categoryColor = "#10B981", dateMillis = calMorning.timeInMillis)
        )

        // Querying at 21:00 (within +-2 hours of 20:00) should recommend Dinner (Category 2)
        val recommendedEvening = CategoryRecommendationEngine.findSmartDefaultCategory(
            categories = categories,
            type = TransactionType.EXPENSE,
            recentTransactions = recentTxs,
            currentHour = 21
        )
        assertNotNull(recommendedEvening)
        assertEquals(2L, recommendedEvening?.id)

        // Querying at 10:00 (within +-2 hours of 09:00) should recommend Groceries (Category 1)
        val recommendedMorning = CategoryRecommendationEngine.findSmartDefaultCategory(
            categories = categories,
            type = TransactionType.EXPENSE,
            recentTransactions = recentTxs,
            currentHour = 10
        )
        assertNotNull(recommendedMorning)
        assertEquals(1L, recommendedMorning?.id)
    }

    @Test
    fun testCategoryRecommendationFrequencyFallback() {
        val categories = listOf(
            Category(1, "Groceries", TransactionType.EXPENSE, "ShoppingCart", "#10B981"),
            Category(2, "Transport", TransactionType.EXPENSE, "DirectionsCar", "#0EA5E9"),
            Category(3, "Other", TransactionType.EXPENSE, "MoreHoriz", "#64748B")
        )

        // Most frequent category is Transport (Category 2)
        val recentTxs = listOf(
            Transaction(id = 1, title = "Bus", amount = 50.0, type = TransactionType.EXPENSE, categoryId = 2, categoryName = "Transport", categoryIcon = "DirectionsCar", categoryColor = "#0EA5E9", dateMillis = 1000L),
            Transaction(id = 2, title = "Metro", amount = 60.0, type = TransactionType.EXPENSE, categoryId = 2, categoryName = "Transport", categoryIcon = "DirectionsCar", categoryColor = "#0EA5E9", dateMillis = 2000L),
            Transaction(id = 3, title = "Bazaar", amount = 500.0, type = TransactionType.EXPENSE, categoryId = 1, categoryName = "Groceries", categoryIcon = "ShoppingCart", categoryColor = "#10B981", dateMillis = 3000L)
        )

        // At 14:00 (no transactions near 14:00), should fallback to most frequent (Transport, Category 2)
        val recommended = CategoryRecommendationEngine.findSmartDefaultCategory(
            categories = categories,
            type = TransactionType.EXPENSE,
            recentTransactions = recentTxs,
            currentHour = 14
        )
        assertNotNull(recommended)
        assertEquals(2L, recommended?.id)
    }

    @Test
    fun testCategoryRecommendationOtherFallbackWhenNoHistory() {
        val categories = listOf(
            Category(1, "Bazaar", TransactionType.EXPENSE, "ShoppingCart", "#10B981"),
            Category(2, "Apparel", TransactionType.EXPENSE, "Checkroom", "#F97316"),
            Category(3, "Other", TransactionType.EXPENSE, "MoreHoriz", "#64748B")
        )

        // With zero history, should fallback to "Other" (Category 3), NEVER alphabetical first
        val recommended = CategoryRecommendationEngine.findSmartDefaultCategory(
            categories = categories,
            type = TransactionType.EXPENSE,
            recentTransactions = emptyList()
        )
        assertNotNull(recommended)
        assertEquals("Other", recommended?.name)
        assertEquals(3L, recommended?.id)
    }

    // =========================================================================
    // 3. Ergonomic & Structural V7 Invariants
    // =========================================================================

    @Test
    fun testAmountKeypadErgonomicsAndTouchTargets() {
        val file = findSourceFile("src/main/java/com/example/expensetracker/ui/components/AmountKeypad.kt")
        assertTrue("AmountKeypad.kt must exist", file.exists())
        val content = file.readText()

        assertTrue(
            "AmountKeypad buttons must declare minHeight = 56.dp",
            content.contains("defaultMinSize(minHeight = 56.dp)")
        )
        assertTrue(
            "AmountKeypad buttons must enforce minimumInteractiveComponentSize()",
            content.contains("minimumInteractiveComponentSize()")
        )
        assertTrue(
            "AmountKeypad must declare math operators (÷, ×, −, +)",
            content.contains("'÷'") && content.contains("'×'") && content.contains("'−'") && content.contains("'+'")
        )
        assertTrue(
            "AmountKeypad must support expression preview line with tap-to-apply",
            content.contains("hasOperators && evaluatedValue != null")
        )
    }

    @Test
    fun testCategoryPickerInlineExpansionNoModal() {
        val file = findSourceFile("src/main/java/com/example/expensetracker/ui/components/CategoryPicker.kt")
        assertTrue("CategoryPicker.kt must exist", file.exists())
        val content = file.readText()

        assertTrue(
            "CategoryPicker chips must declare minHeight = 44.dp",
            content.contains("defaultMinSize(minHeight = 44.dp)")
        )
        assertTrue(
            "CategoryPicker chips must enforce minimumInteractiveComponentSize()",
            content.contains("minimumInteractiveComponentSize()")
        )
        assertTrue(
            "CategoryPicker must expand inline using FlowRow without modal",
            content.contains("FlowRow(") && content.contains("isExpanded")
        )
    }

    @Test
    fun testAccountChipRowTransferModeAndWarning() {
        val file = findSourceFile("src/main/java/com/example/expensetracker/ui/components/AccountChipRow.kt")
        assertTrue("AccountChipRow.kt must exist", file.exists())
        val content = file.readText()

        assertTrue(
            "AccountChipRow chips must declare minHeight = 44.dp",
            content.contains("defaultMinSize(minHeight = 44.dp)")
        )
        assertTrue(
            "AccountChipRow chips must enforce minimumInteractiveComponentSize()",
            content.contains("minimumInteractiveComponentSize()")
        )
        assertTrue(
            "AccountChipRow must support Transfer mode with From and To accounts",
            content.contains("isTransfer: Boolean = false") && content.contains("toAccountId")
        )
        assertTrue(
            "AccountChipRow must show non-blocking insufficient balance warning",
            content.contains("isInsufficientBalance") && content.contains("warningContainer")
        )
    }

    @Test
    fun testTransactionAmountCardAreaFootprint() {
        val file = findSourceFile("src/main/java/com/example/expensetracker/ui/transactions/TransactionAmountCard.kt")
        assertTrue("TransactionAmountCard.kt must exist", file.exists())
        val content = file.readText()

        assertTrue(
            "TransactionAmountCard must restrict height to max 140.dp per C4",
            content.contains("heightIn(max = 140.dp)")
        )
        assertTrue(
            "TransactionAmountCard must use tabular numbers tnum",
            content.contains("fontFeatureSettings = \"tnum\"")
        )
    }

    @Test
    fun testTransactionDateTimeSectionChips() {
        val file = findSourceFile("src/main/java/com/example/expensetracker/ui/transactions/TransactionDateTimeSection.kt")
        assertTrue("TransactionDateTimeSection.kt must exist", file.exists())
        val content = file.readText()

        assertTrue(
            "TransactionDateTimeSection chips must declare minHeight = 44.dp",
            content.contains("defaultMinSize(minHeight = 44.dp)")
        )
        assertTrue(
            "TransactionDateTimeSection chips must enforce minimumInteractiveComponentSize()",
            content.contains("minimumInteractiveComponentSize()")
        )
    }

    @Test
    fun testTransactionMergedTitleAndNotesExpansion() {
        val file = findSourceFile("src/main/java/com/example/expensetracker/ui/transactions/TransactionNoteAndTitleSection.kt")
        assertTrue("TransactionNoteAndTitleSection.kt must exist", file.exists())
        val content = file.readText()

        assertTrue(
            "TransactionNoteAndTitleSection must provide addNote button that expands notes field",
            content.contains("isNoteExpanded") && content.contains("strings.addNote")
        )
    }

    @Test
    fun testAddEditTransactionSheetPreservesMarketContract() {
        val file = findSourceFile("src/main/java/com/example/expensetracker/ui/transactions/AddEditTransactionSheet.kt")
        assertTrue("AddEditTransactionSheet.kt must exist", file.exists())
        val content = file.readText()

        assertTrue(content.contains("initialOpenMarketTab: Boolean = false"))
        assertTrue(content.contains("var isMarketActive by rememberSaveable(existingTransaction"))
        assertTrue(content.contains("LaunchedEffect(initialOpenMarketTab)"))
        assertTrue(content.contains("MarketNativeContent("))
        assertTrue(content.contains("TransactionBottomActionBar("))
        assertTrue(content.contains("BackHandler(enabled = isMarketActive && hasVisitedTransactionForm)"))
    }
}
