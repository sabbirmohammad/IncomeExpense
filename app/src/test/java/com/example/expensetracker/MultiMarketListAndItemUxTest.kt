package com.example.expensetracker

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Unit and structural verification tests for:
 * 1. Multi-list support & directory management (separating list creation from shopping view).
 * 2. Minimal Item Card UX (Checkbox + Name + Quantity only, removing cluttered inline textfield & trash icon).
 * 3. Swipe-to-delete with confirmation dialog.
 * 4. Popup price entry upon purchasing with quick increment chips.
 */
class MultiMarketListAndItemUxTest {

    private val basePath = "src/main/java"

    private fun readFile(relativePath: String): String {
        val file = File("$basePath/$relativePath")
        assertTrue("Expected file to exist: ${file.absolutePath}", file.exists())
        return file.readText()
    }

    @Test
    fun testMarketTableHelperMultipleActiveLists() {
        val content = readFile("com/example/expensetracker/data/local/MarketTableHelper.kt")
        assertTrue(
            "MarketTableHelper must provide getActiveMarketLists returning List<MarketListWithItems>",
            content.contains("fun getActiveMarketLists(): List<MarketListWithItems>")
        )
        assertTrue(
            "MarketTableHelper must query uncompleted market lists",
            content.contains("\$COL_ML_IS_COMPLETED = 0")
        )
        assertTrue(
            "MarketTableHelper must support getMarketListById",
            content.contains("fun getMarketListById(listId: Long): MarketListWithItems?")
        )
        assertTrue(
            "MarketTableHelper must support updateMarketListTitle",
            content.contains("fun updateMarketListTitle(listId: Long, title: String): Boolean")
        )
    }

    @Test
    fun testExpenseDatabaseHelperMultipleActiveListsDelegation() {
        val content = readFile("com/example/expensetracker/data/local/ExpenseDatabaseHelper.kt")
        assertTrue(
            "ExpenseDatabaseHelper must delegate getActiveMarketLists()",
            content.contains("fun getActiveMarketLists(): List<MarketListWithItems> = marketHelper.getActiveMarketLists()")
        )
        assertTrue(
            "ExpenseDatabaseHelper must delegate updateMarketListTitle",
            content.contains("fun updateMarketListTitle(listId: Long, title: String): Boolean = marketHelper.updateMarketListTitle(listId, title)")
        )
    }

    @Test
    fun testExpenseRepositoryMultipleActiveListsFlow() {
        val content = readFile("com/example/expensetracker/data/repository/ExpenseRepository.kt")
        assertTrue(
            "ExpenseRepository must expose activeMarketLists StateFlow",
            content.contains("val activeMarketLists: StateFlow<List<MarketListWithItems>>")
        )
        assertTrue(
            "ExpenseRepository refreshMarketList must update _activeMarketLists",
            content.contains("_activeMarketLists.value = dbHelper.getActiveMarketLists()")
        )
    }

    @Test
    fun testMarketItemRowSwipeToDismissAndCleanDesign() {
        val content = readFile("com/example/expensetracker/ui/dashboard/MarketItemRow.kt")

        // Must use SwipeToDismissBox
        assertTrue(
            "MarketItemRow must use SwipeToDismissBox for swipe-to-delete",
            content.contains("SwipeToDismissBox(") && content.contains("rememberSwipeToDismissBoxState(")
        )

        // Must support onRequestDelete callback
        assertTrue(
            "MarketItemRow must declare onRequestDelete callback",
            content.contains("onRequestDelete: () -> Unit")
        )

        // Must display Circular Checkbox, Name, and Quantity badge
        assertTrue(
            "MarketItemRow must display item.name",
            content.contains("text = item.name")
        )
        assertTrue(
            "MarketItemRow must display item.formatQuantity()",
            content.contains("text = item.formatQuantity()")
        )

        // Price badge shown when purchased
        assertTrue(
            "MarketItemRow must show price badge when item.isPurchased",
            content.contains("if (item.isPurchased)") && content.contains("formatCurrency(amt, currencySymbol)")
        )
    }

    @Test
    fun testMarketPromptAmountDialogQuickChipsAndAutoFocus() {
        val content = readFile("com/example/expensetracker/ui/dashboard/MarketPromptAmountDialog.kt")

        assertTrue(
            "MarketPromptAmountDialog must use FocusRequester for auto-focusing keyboard",
            content.contains("FocusRequester()") && content.contains("focusRequester.requestFocus()")
        )
        assertTrue(
            "MarketPromptAmountDialog must provide quick increment chips (+50, +100, +200, +500)",
            content.contains("listOf(50, 100, 200, 500)")
        )
        assertTrue(
            "MarketPromptAmountDialog must have Done action and submitAmount",
            content.contains("imeAction = ImeAction.Done") && content.contains("KeyboardActions(onDone = { submitAmount() })")
        )
        assertTrue(
            "MarketPromptAmountDialog must allow marking without price",
            content.contains("onConfirm(0.0)")
        )
    }

    @Test
    fun testCreateMarketListSectionFeatures() {
        val content = readFile("com/example/expensetracker/ui/dashboard/CreateMarketListSection.kt")

        // Title options
        assertTrue(
            "CreateMarketListSection must offer quick title chips (সকালের কাঁচাবাজার, বিকালের মুদি, etc.)",
            content.contains("সকালের কাঁচাবাজার") && content.contains("বিকালের মুদি")
        )

        // Both input modes: WhatsApp bulk paste and manual item entry
        assertTrue(
            "CreateMarketListSection must support WhatsApp paste mode",
            content.contains("MarketListParser.parseBulkText(bulkInputText)")
        )
        assertTrue(
            "CreateMarketListSection must support manual item addition with quick staples",
            content.contains("quickStaples") && content.contains("addManualItem")
        )
    }

    @Test
    fun testActiveMarketListsHubViewSupport() {
        val content = readFile("com/example/expensetracker/ui/dashboard/ActiveMarketListsHubView.kt")

        assertTrue(
            "ActiveMarketListsHubView must display active lists with progress and stats",
            content.contains("activeLists: List<MarketListWithItems>") &&
            content.contains("LinearProgressIndicator") &&
            content.contains("onSelectList")
        )
    }

    @Test
    fun testMarketListSheetSwipeDeleteConfirmDialog() {
        val content = readFile("com/example/expensetracker/ui/dashboard/MarketListSheet.kt")

        assertTrue(
            "MarketListSheet must show confirmation dialog before deleting an item",
            content.contains("itemToDeleteConfirm != null") &&
            content.contains("পণ্য মুছে ফেলতে চান?")
        )
    }
}
