package com.example.expensetracker

import android.content.Context
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.AccountType
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.ui.components.AppHaptics
import com.example.expensetracker.ui.components.Strings
import com.example.expensetracker.ui.transactions.AddEditTransactionState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.lang.reflect.Proxy

class QuickShortcutsAndMarketAnalyticsTest {

    private val basePath: String = if (File("src/main/java").exists()) "src/main/java" else "app/src/main/java"

    private fun readFile(relativePath: String): String {
        val file = File("$basePath/$relativePath")
        assertTrue("Target file ${file.path} must exist", file.exists())
        return file.readText()
    }

    @Test
    fun testTransactionEditFormBodyDirectEntryWithoutInternalSwitcher() {
        val content = readFile("com/example/expensetracker/ui/transactions/TransactionEditFormBody.kt")

        assertFalse(
            "TransactionEditFormBody should not invoke TransactionTypeSwitcher to maintain dedicated task focus",
            content.contains("TransactionTypeSwitcher(")
        )
        assertTrue(
            "TransactionEditFormBody must contain TransactionAmountCard",
            content.contains("TransactionAmountCard(")
        )
    }

    @Test
    fun testDashboardScreenIncludesQuickActionsDock() {
        val content = readFile("com/example/expensetracker/ui/dashboard/DashboardScreen.kt")

        assertTrue(
            "DashboardScreen must contain DashboardQuickActionsDock under the main balance card",
            content.contains("DashboardQuickActionsDock")
        )
    }

    @Test
    fun testAnalyticsKpiStripHeightConsistency() {
        val content = readFile("com/example/expensetracker/ui/analytics/AnalyticsKpiStrip.kt")

        assertTrue(
            "AnalyticsKpiStrip Row must use height(IntrinsicSize.Max) to guarantee equal card heights",
            content.contains("height(IntrinsicSize.Max)")
        )
        assertTrue(
            "AnalyticsKpiStrip cards must use fillMaxHeight()",
            content.contains(".fillMaxHeight()")
        )
        assertTrue(
            "Card 3 must use concise label 'আগের তুলনা' to prevent multi-line wrapping and inconsistent height",
            content.contains("আগের তুলনা")
        )
        assertTrue(
            "Titles must be bounded by maxLines = 1 with TextOverflow.Ellipsis",
            content.contains("maxLines = 1") && content.contains("TextOverflow.Ellipsis")
        )
    }

    @Test
    fun testMarketAddNewItemRowKeyboardAndSuggestions() {
        val content = readFile("com/example/expensetracker/ui/dashboard/MarketAddNewItemRow.kt")

        assertTrue(
            "MarketAddNewItemRow must specify ImeAction.Done for quick keyboard entry",
            content.contains("imeAction = ImeAction.Done")
        )
        assertTrue(
            "MarketAddNewItemRow must handle KeyboardActions onDone",
            content.contains("KeyboardActions(onDone = {")
        )
        assertTrue(
            "MarketAddNewItemRow must have quickSuggestions chips",
            content.contains("quickSuggestions")
        )
        assertTrue(
            "MarketAddNewItemRow must accept existingItemNames parameter",
            content.contains("existingItemNames: Set<String> = emptySet()")
        )
    }

    @Test
    fun testMarketListSheetFilterTabs() {
        val content = readFile("com/example/expensetracker/ui/dashboard/MarketListSheet.kt")

        assertTrue(
            "MarketListSheet must declare filterMode for filtering items",
            content.contains("var filterMode by remember { mutableIntStateOf(0) }")
        )
        assertTrue(
            "MarketListSheet must have filteredItems list",
            content.contains("val filteredItems = remember(items, filterMode)")
        )
        assertTrue(
            "MarketListSheet must pass existingItemNames to MarketAddNewItemRow",
            content.contains("existingItemNames = items.map { it.name.lowercase() }.toSet()")
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testAddEditTransactionStateIncomeSwitching() {
        val categories = listOf(
            Category(id = 1, name = "Food", type = TransactionType.EXPENSE, icon = "Food", colorHex = "#FF0000"),
            Category(id = 2, name = "Salary", type = TransactionType.INCOME, icon = "Salary", colorHex = "#00FF00")
        )
        val accounts = listOf(
            Account(id = 1, name = "Cash", type = AccountType.CASH, currentBalance = 1000.0, isDefault = true)
        )
        val strings = Strings(isBangla = true)
        val dummyHaptic = object : HapticFeedback {
            override fun performHapticFeedback(hapticFeedbackType: HapticFeedbackType) {}
        }
        val appHaptics = AppHaptics(dummyHaptic, null)
        val dummyContext = object : android.content.ContextWrapper(null) {}
        val testScope = TestScope()

        val state = AddEditTransactionState(
            existingTransaction = null,
            initialType = TransactionType.EXPENSE,
            initialDateMillis = System.currentTimeMillis(),
            categories = categories,
            accounts = accounts,
            strings = strings,
            haptics = appHaptics,
            context = dummyContext,
            scope = testScope,
            onDismiss = {},
            onSave = {}
        )

        // Initially EXPENSE
        assertTrue("Initially should be expense", state.isExpense)
        assertEquals(TransactionType.EXPENSE, state.selectedType)
        assertEquals(1L, state.selectedCategory?.id)

        // Switch to INCOME
        state.handleTypeSelected(TransactionType.INCOME)
        assertFalse("After switching should not be expense", state.isExpense)
        assertEquals(TransactionType.INCOME, state.selectedType)
        assertNotNull("Income category should be selected", state.selectedCategory)
        assertEquals(2L, state.selectedCategory?.id)
        assertEquals(TransactionType.INCOME, state.selectedCategory?.type)
    }

    @Test
    fun testMarketAddNewItemRowNaturalLanguageParsing() {
        val content = readFile("com/example/expensetracker/ui/dashboard/MarketAddNewItemRow.kt")

        assertTrue(
            "MarketAddNewItemRow must import MarketListParser for inline natural parsing",
            content.contains("import com.example.expensetracker.common.MarketListParser")
        )
        assertTrue(
            "MarketAddNewItemRow submitNewItem must parse inline quantity and unit using MarketListParser",
            content.contains("MarketListParser.parseSingleLine(text)")
        )
    }

    @Test
    fun testMarketListSheetDeleteListSupport() {
        val content = readFile("com/example/expensetracker/ui/dashboard/MarketListSheet.kt")

        assertTrue(
            "MarketListSheet must declare onDeleteList parameter",
            content.contains("onDeleteList: ((Long) -> Unit)? = null")
        )
        assertTrue(
            "MarketListSheet must include showDeleteListConfirmDialog",
            content.contains("showDeleteListConfirmDialog")
        )
    }

    @Test
    fun testMarketItemRowPriceClearSupport() {
        val content = readFile("com/example/expensetracker/ui/dashboard/MarketItemRow.kt")

        assertTrue(
            "MarketItemRow must clear price when input becomes blank",
            content.contains("if (it.isBlank()) {") && content.contains("onAmountChanged(0.0)")
        )
    }

    @Test
    fun testMarketPromptAmountDialogImeDoneAction() {
        val content = readFile("com/example/expensetracker/ui/dashboard/MarketPromptAmountDialog.kt")

        assertTrue(
            "MarketPromptAmountDialog must support ImeAction.Done",
            content.contains("imeAction = ImeAction.Done")
        )
        assertTrue(
            "MarketPromptAmountDialog must handle onDone KeyboardActions",
            content.contains("KeyboardActions(onDone = { submitAmount() })")
        )
    }

    @Test
    fun testMarketDepartmentMetaAndShareText() {
        val meta = com.example.expensetracker.ui.dashboard.getDepartmentMeta("veggies")
        assertEquals("কাঁচাবাজার", meta.titleBn)
        assertEquals("🥬", meta.icon)

        val items = listOf(
            MarketItem(id = 1, name = "আলু", quantity = 2.0, unit = "কেজি", isPurchased = true, amount = 100.0),
            MarketItem(id = 2, name = "রুই মাছ", quantity = 1.5, unit = "কেজি", isPurchased = false)
        )
        val text = com.example.expensetracker.ui.dashboard.generateMarketShareText("আজকের বাজার", items, true)
        assertTrue(text.contains("🥬 কাঁচাবাজার:"))
        assertTrue(text.contains("☑ আলু (2 কেজি) - ৳100"))
        assertTrue(text.contains("🐟 মাছ ও মাংস:"))
        assertTrue(text.contains("☐ রুই মাছ (1.5 কেজি)"))
    }

    @Test
    fun testMarketNativeContentRollOverAndBudgetSupport() {
        val content = readFile("com/example/expensetracker/ui/dashboard/MarketNativeContent.kt")
        assertTrue(
            "MarketNativeContent must accept onCompleteListWithRollOver",
            content.contains("onCompleteListWithRollOver: ((listId: Long, rollOverItems: List<MarketItem>) -> Unit)? = null")
        )
        assertTrue(
            "MarketNativeContent must accept lastPurchasedRates",
            content.contains("lastPurchasedRates: Map<String, Pair<Double, String>> = emptyMap()")
        )
        assertTrue(
            "MarketNativeContent must have targetBudget state",
            content.contains("targetBudget")
        )
        assertTrue(
            "MarketNativeContent must have department filter pills",
            content.contains("selectedDepartment")
        )
    }
}
