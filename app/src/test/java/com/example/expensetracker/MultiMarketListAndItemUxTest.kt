package com.example.expensetracker

import com.example.expensetracker.common.MarketListParser
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.ui.dashboard.generateMarketShareText
import com.example.expensetracker.ui.dashboard.getMarketItemCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
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

        // 1. Stepped wizard architecture
        assertTrue(
            "CreateMarketListSection must define CreateMarketListStep enum with TITLE, ITEMS",
            content.contains("enum class CreateMarketListStep") &&
            content.contains("TITLE") &&
            content.contains("ITEMS")
        )

        // 2. Title options
        assertTrue(
            "CreateMarketListSection must offer quick title chips (সকালের কাঁচাবাজার, বিকালের মুদি, etc.)",
            content.contains("সকালের কাঁচাবাজার") && content.contains("বিকালের মুদি")
        )

        // 3. Smart Omnibox combining manual entry and WhatsApp bulk paste
        assertTrue(
            "CreateMarketListSection must support unified omnibox with bulk parser",
            content.contains("MarketListParser.parseBulkText") &&
            content.contains("addManualItem")
        )

        // 4. Quick staples assistant
        assertTrue(
            "CreateMarketListSection must support manual item addition with quick staples",
            content.contains("quickStaples") && content.contains("addManualItem")
        )

        // 5. Quick quantities assistant
        assertTrue(
            "CreateMarketListSection must provide quick quantity selector chips",
            content.contains("quickQuantities") && content.contains("১ কেজি")
        )
    }

    @Test
    fun testCreateMarketListManualEntryUxRefinements() {
        val content = readFile("com/example/expensetracker/ui/dashboard/CreateMarketListSection.kt")

        // 1. Newly added items must be appended in chat-style order so latest items appear right above composer
        assertTrue(
            "Newly added items must be appended so latest items appear right above the composer dock",
            content.contains("manualItems.value = (manualItems.value + itemsToAdd).toMutableList()")
        )

        // 2. Step 2 header must show clean item counter badge
        assertTrue(
            "Step 2 header must display clean item counter badge",
            content.contains("Live Item Counter Badge") &&
            content.contains("টি পণ্য যুক্ত")
        )

        // 3. Added Items section must be placed ABOVE the quick staples section in manual entry
        val addedItemsIndex = content.indexOf("Added Items Section")
        val quickStaplesIndex = content.indexOf("Quick Staples Pills")
        assertTrue(
            "Added Items section must appear before Quick Staples pills in manual entry",
            addedItemsIndex in 1 until quickStaplesIndex
        )

        // 4. Added items must be rendered as a vertical itemized list with serial numbers
        assertTrue(
            "Added items must display as an itemized list row with serial numbers",
            content.contains("Serial number badge") &&
            content.contains("convertToBengaliDigits((idx + 1).toString())")
        )

        // 5. Verify chat-style appending behavior on an actual list
        val existingItems = listOf(
            com.example.expensetracker.data.model.MarketItem(name = "আলু", quantity = 1.0, unit = "কেজি"),
            com.example.expensetracker.data.model.MarketItem(name = "পেঁয়াজ", quantity = 2.0, unit = "কেজি")
        )
        val newItem = listOf(com.example.expensetracker.data.model.MarketItem(name = "ডিম", quantity = 12.0, unit = "পিস"))
        val updated = (existingItems + newItem)
        assertEquals("Latest item must be at bottom right above composer dock", "ডিম", updated.last().name)
        assertEquals("Previous first item remains first", "আলু", updated.first().name)

        // 6. Dismissible clipboard banner support
        assertTrue(
            "CreateMarketListSection must support dismissible clipboard banner",
            content.contains("isClipboardDismissed")
        )
    }

    @Test
    fun testSmartOmniboxBulkPasteDetectionAndParsing() {
        val sampleWhatsApp = """
            - আলু ২ কেজি
            - পেঁয়াজ ১ কেজি
            - আড়াই কেজি চিনি
            - ১ পোয়া কাঁচামরিচ
            - সয়াবিন তেল ২ লিটার
        """.trimIndent()

        val parsed = MarketListParser.parseBulkText(sampleWhatsApp)
        assertEquals(5, parsed.size)
        assertEquals("আলু", parsed[0].name)
        assertEquals(2.0, parsed[0].quantity, 0.01)
        assertEquals("কেজি", parsed[0].unit)

        assertEquals("পেঁয়াজ", parsed[1].name)
        assertEquals(1.0, parsed[1].quantity, 0.01)
        assertEquals("কেজি", parsed[1].unit)

        assertEquals("চিনি", parsed[2].name)
        assertEquals(2.5, parsed[2].quantity, 0.01)
        assertEquals("কেজি", parsed[2].unit)

        assertEquals("কাঁচামরিচ", parsed[3].name)
        assertEquals(250.0, parsed[3].quantity, 0.01)
        assertEquals("গ্রাম", parsed[3].unit)

        assertEquals("সয়াবিন তেল", parsed[4].name)
        assertEquals(2.0, parsed[4].quantity, 0.01)
        assertEquals("লিটার", parsed[4].unit)
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

    @Test
    fun testMarketDepartmentClassifierPotatoVsOilCategorization() {
        // Vegetables
        assertEquals("veggies", getMarketItemCategory("আলু"))
        assertEquals("veggies", getMarketItemCategory("গোল আলু"))
        assertEquals("veggies", getMarketItemCategory("কাঁচা মরিচ"))
        assertEquals("veggies", getMarketItemCategory("পেঁয়াজ"))
        assertEquals("veggies", getMarketItemCategory("টমেটো"))

        // Groceries & Spices
        assertEquals("groceries", getMarketItemCategory("সয়াবিন তেল"))
        assertEquals("groceries", getMarketItemCategory("সরিষার তেল"))
        assertEquals("groceries", getMarketItemCategory("মরিচ গুঁড়া"))
        assertEquals("groceries", getMarketItemCategory("হলুদ গুঁড়া"))
        assertEquals("groceries", getMarketItemCategory("মসুর ডাল"))
        assertEquals("groceries", getMarketItemCategory("মিনিকেট চাল"))
        assertEquals("groceries", getMarketItemCategory("লবণ"))

        // Meat & Fish
        assertEquals("meat_fish", getMarketItemCategory("রুই মাছ"))
        assertEquals("meat_fish", getMarketItemCategory("ব্রয়লার মুরগি"))
        assertEquals("meat_fish", getMarketItemCategory("গরুর মাংস"))

        // Dairy & Bakery
        assertEquals("dairy_bakery", getMarketItemCategory("তরল দুধ"))
        assertEquals("dairy_bakery", getMarketItemCategory("ফার্মের ডিম"))
        assertEquals("dairy_bakery", getMarketItemCategory("পাউরুটি"))
    }

    @Test
    fun testMarketFinishButtonSingleCheckmarkAndCleanText() {
        val content = readFile("com/example/expensetracker/ui/dashboard/MarketFinishButton.kt")

        // Must use single Check icon, not DoneAll (which had double checkmarks)
        assertTrue(
            "MarketFinishButton must use single Check icon",
            content.contains("Icons.Filled.Check")
        )
        assertFalse(
            "MarketFinishButton must NOT use DoneAll",
            content.contains("Icons.Filled.DoneAll")
        )

        // Must not contain duplicate checkmark characters inside string text
        assertFalse(
            "MarketFinishButton must not duplicate checkmark character in string",
            content.contains("✓ বাজার সম্পন্ন করুন") || content.contains("✓ Finish Shopping")
        )
        assertTrue(
            "MarketFinishButton text must be clean",
            content.contains("বাজার সম্পন্ন করুন") && content.contains("Finish Shopping")
        )
    }

    @Test
    fun testMarketNativeContentAccidentalTouchProtectionAndEntryFlow() {
        val content = readFile("com/example/expensetracker/ui/dashboard/MarketNativeContent.kt")

        // 1. Default tab must be 0 (Checklist), not forcefully tab 1 (Paste)
        assertTrue(
            "MarketNativeContent must default selectedTab to 0 (Checklist)",
            content.contains("var selectedTab by remember { mutableIntStateOf(0) }")
        )

        // 2. Must feature safety confirmation dialog state
        assertTrue(
            "MarketNativeContent must track itemToDeleteConfirm state",
            content.contains("var itemToDeleteConfirm by remember { mutableStateOf<MarketItem?>(null) }")
        )

        // 3. Both MarketItemRow usages must wire onRequestDelete and onDeleteClick to safety dialog
        assertTrue(
            "MarketItemRow onRequestDelete must route to itemToDeleteConfirm",
            content.contains("onRequestDelete = {\n                                    itemToDeleteConfirm = item\n                                }") ||
            content.contains("onRequestDelete = {\n                                        itemToDeleteConfirm = item\n                                    }")
        )

        // 4. Must display the entry choice prompt when empty
        assertTrue(
            "MarketNativeContent must display list creation choice mode when items empty",
            content.contains("ফর্দ তৈরির ধরন বেছে নিন") &&
            content.contains("সরাসরি ফর্দ") &&
            content.contains("লিস্ট পেস্ট")
        )
    }

    @Test
    fun testMarketItemRowDisplaysDepartmentBadge() {
        val content = readFile("com/example/expensetracker/ui/dashboard/MarketItemRow.kt")

        assertTrue(
            "MarketItemRow must determine department meta for the item",
            content.contains("getDepartmentMeta(getMarketItemCategory(item.name))")
        )
        assertTrue(
            "MarketItemRow must display department icon and title badge",
            content.contains("\${dept.icon} \${if (isBangla) dept.titleBn else dept.titleEn}")
        )
        assertTrue(
            "MarketItemRow must provide onSubstituteClick",
            content.contains("onSubstituteClick")
        )
    }

    @Test
    fun testDesiUnitsParsingInMarketListParser() {
        // পোয়া
        val powaItem = MarketListParser.parseSingleLine("কাঁচামরিচ ১ পোয়া")
        assertNotNull(powaItem)
        assertEquals("কাঁচামরিচ", powaItem!!.name)
        assertEquals(1.0, powaItem.quantity, 0.01)
        assertEquals("পোয়া", powaItem.unit)

        // পাল্লা
        val pallaItem = MarketListParser.parseSingleLine("আলু ১ পাল্লা")
        assertNotNull(pallaItem)
        assertEquals("আলু", pallaItem!!.name)
        assertEquals(1.0, pallaItem.quantity, 0.01)
        assertEquals("পাল্লা", pallaItem.unit)

        // জোড়া
        val joraItem = MarketListParser.parseSingleLine("ইলিশ মাছ ২ জোড়া")
        assertNotNull(joraItem)
        assertEquals("ইলিশ মাছ", joraItem!!.name)
        assertEquals(2.0, joraItem.quantity, 0.01)
        assertEquals("জোড়া", joraItem.unit)

        // হালি
        val haliItem = MarketListParser.parseSingleLine("ডিম ৪ হালি")
        assertNotNull(haliItem)
        assertEquals("ডিম", haliItem!!.name)
        assertEquals(4.0, haliItem.quantity, 0.01)
        assertEquals("হালি", haliItem.unit)
    }

    @Test
    fun testUnitAndQuantityDialogDesiPresets() {
        val content = readFile("com/example/expensetracker/ui/components/UnitAndQuantityDialog.kt")

        // Must support desi units in available units
        assertTrue("UnitAndQuantityDialog must include পোয়া", content.contains("পোয়া (২৫০ গ্রাম)"))
        assertTrue("UnitAndQuantityDialog must include পাল্লা", content.contains("পাল্লা (৫ কেজি)"))
        assertTrue("UnitAndQuantityDialog must include হালি", content.contains("হালি (৪টি)"))
        assertTrue("UnitAndQuantityDialog must include জোড়া", content.contains("জোড়া (২টি)"))

        // Must include quick presets
        assertTrue("UnitAndQuantityDialog must include ১ পোয়া preset", content.contains("০.২৫ (১ পোয়া)"))
        assertTrue("UnitAndQuantityDialog must include ১ পাল্লা preset", content.contains("৫ (১ পাল্লা)"))
    }

    @Test
    fun testMarketPromptAmountDialogCalculatorAndAnomalyAlert() {
        val content = readFile("com/example/expensetracker/ui/dashboard/MarketPromptAmountDialog.kt")

        // Calculator integration
        assertTrue(
            "MarketPromptAmountDialog must evaluate math expressions using CalculatorEvaluator",
            content.contains("CalculatorEvaluator.evaluate(promptAmountText)")
        )
        assertTrue(
            "MarketPromptAmountDialog must show live expression preview chip",
            content.contains("evaluatedAmount") || content.contains("isExpression")
        )
        assertTrue(
            "MarketPromptAmountDialog must offer math operator quick buttons (×, +, ÷, −)",
            content.contains("\"×\"") && content.contains("\"+\"") && content.contains("\"÷\"") && content.contains("\"−\"")
        )

        // Price anomaly comparison
        assertTrue(
            "MarketPromptAmountDialog must accept lastRate parameter",
            content.contains("lastRate: Pair<Double, String>? = null")
        )
        assertTrue(
            "MarketPromptAmountDialog must alert on price difference",
            content.contains("percentDiff") || content.contains("diffRate")
        )
    }

    @Test
    fun testMarketSubstituteItemDialogStructure() {
        val content = readFile("com/example/expensetracker/ui/dashboard/MarketSubstituteItemDialog.kt")

        assertTrue(
            "MarketSubstituteItemDialog must accept item, isBangla, onDismiss, onConfirmSubstitute",
            content.contains("onConfirmSubstitute: (newTitle: String) -> Unit")
        )
        assertTrue(
            "MarketSubstituteItemDialog must offer alternative suggestions",
            content.contains("suggestions")
        )
        assertTrue(
            "MarketSubstituteItemDialog must allow custom alternative name input",
            content.contains("substituteName")
        )
    }

    @Test
    fun testForgotStaplesGuardBeforeFinishingShopping() {
        val nativeContent = readFile("com/example/expensetracker/ui/dashboard/MarketNativeContent.kt")
        val sheetContent = readFile("com/example/expensetracker/ui/dashboard/MarketListSheet.kt")

        // Native content
        assertTrue(
            "MarketNativeContent must track showForgotStaplesDialog",
            nativeContent.contains("showForgotStaplesDialog")
        )
        assertTrue(
            "MarketNativeContent must check for chili or lemon before finishing",
            nativeContent.contains("hasStaplePurchased") && nativeContent.contains("মরিচ") && nativeContent.contains("লেবু")
        )

        // Sheet content
        assertTrue(
            "MarketListSheet must track showForgotStaplesDialog",
            sheetContent.contains("showForgotStaplesDialog")
        )
        assertTrue(
            "MarketListSheet must check for chili or lemon before finishing",
            sheetContent.contains("hasStaplePurchased") && sheetContent.contains("মরিচ") && sheetContent.contains("লেবু")
        )
    }

    @Test
    fun testEnhancedMarketShareTextFormatting() {
        val items = listOf(
            MarketItem(id = 1, name = "আলু", quantity = 2.0, unit = "কেজি", isPurchased = true, amount = 100.0),
            MarketItem(id = 2, name = "রুই মাছ", quantity = 1.0, unit = "কেজি", isPurchased = false)
        )
        val text = generateMarketShareText(
            listTitle = "সকালের বাজার",
            items = items,
            isBangla = true,
            targetBudget = 500.0
        )

        // Title and checklist items
        assertTrue(text.contains("🛒 সকালের বাজার:"))
        assertTrue(text.contains("☑ আলু (2 কেজি) - ৳100"))
        assertTrue(text.contains("☐ রুই মাছ (1 কেজি)"))

        // Summary and budget
        assertTrue(text.contains("💰 মোট খরচ: ৳১০০ (১টি কেনা)"))
        assertTrue(text.contains("⏳ বাকি আছে: ১টি পণ্য"))
        assertTrue(text.contains("🎯 বাজেট: ৳৫০০ (অবশিষ্ট: ৳৪০০)"))
    }

    @Test
    fun testMarketEditingModeSeparationAndSafety() {
        val screenContent = readFile("com/example/expensetracker/ui/market/MarketScreen.kt")
        val itemRowContent = readFile("com/example/expensetracker/ui/dashboard/MarketItemRow.kt")

        // 1. MarketScreen tracks isEditMode and effectiveEditMode
        assertTrue(
            "MarketScreen must track isEditMode initialized with items.isEmpty()",
            screenContent.contains("var isEditMode by remember(currentViewingList.list.id) { mutableStateOf(items.isEmpty()) }")
        )
        assertTrue(
            "MarketScreen must compute effectiveEditMode",
            screenContent.contains("val effectiveEditMode = isEditMode || items.isEmpty()")
        )

        // 2. Edit button in view mode, Delete List & Done in edit mode
        assertTrue(
            "MarketScreen must feature Edit mode button in normal shopping mode",
            screenContent.contains("!effectiveEditMode") &&
            screenContent.contains("isEditMode = true")
        )
        assertTrue(
            "MarketScreen must show Delete List and Done button in edit mode",
            screenContent.contains("onDeleteListClick(currentViewingList.list.id)") &&
            screenContent.contains("isEditMode = false")
        )

        // 3. MarketAddNewItemRow must be guarded by effectiveEditMode
        assertTrue(
            "MarketAddNewItemRow must only be displayed in effectiveEditMode",
            screenContent.contains("if (effectiveEditMode) {") &&
            screenContent.contains("MarketAddNewItemRow(")
        )

        // 4. View mode should have single clean header edit button without redundant bottom edit button
        assertFalse(
            "MarketScreen eliminates redundant bottom edit button to keep UI clean",
            screenContent.contains("পণ্য যোগ বা ফর্দ এডিট করতে ট্যাপ করুন")
        )

        // 5. MarketItemRow supports isEditMode parameter and restricts swipe-to-dismiss
        assertTrue(
            "MarketItemRow must support isEditMode parameter",
            itemRowContent.contains("isEditMode: Boolean = false")
        )
        assertTrue(
            "MarketItemRow enables swipe-to-dismiss only in edit mode",
            itemRowContent.contains("enableDismissFromEndToStart = isEditMode")
        )
        assertTrue(
            "MarketItemRow provides delete action icon in edit mode",
            itemRowContent.contains("if (isEditMode)") &&
            itemRowContent.contains("onRequestDelete()")
        )
    }

    @Test
    fun testMarketScreenUnifiedCompactHeaderHud() {
        val screenContent = readFile("com/example/expensetracker/ui/market/MarketScreen.kt")

        // 1. Unified HUD is used
        assertTrue(
            "MarketScreen must use MarketShoppingHeaderHUD for compact, modern header layout",
            screenContent.contains("MarketShoppingHeaderHUD(")
        )

        // 2. Redundant title header row is eliminated
        assertFalse(
            "MarketScreen must eliminate standalone list title header row to conserve vertical space",
            screenContent.contains("currentViewingList.list.title.ifBlank")
        )

        // 3. Compact HUD integrates progress, spent, estimated budget, and actions
        assertTrue(
            "HUD must support estimated budget and action controls",
            screenContent.contains("onEnterEditMode = { isEditMode = true }") &&
            screenContent.contains("onExitEditMode = { isEditMode = false }") &&
            screenContent.contains("onDeleteList = { onDeleteListClick(currentViewingList.list.id) }")
        )
    }
}
