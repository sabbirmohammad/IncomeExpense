package com.example.expensetracker

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class NavigationMarketAndCompactReceiptTest {

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

    @Test
    fun testBottomNavAddButton1TapExpenseAndLongPressChooser() {
        val appNavFile = findSourceFile("src/main/java/com/example/expensetracker/ui/navigation/AppNavigation.kt")
        assertTrue("AppNavigation.kt must exist", appNavFile.exists())
        val content = appNavFile.readText()

        // 1-Tap Add Transaction FAB: onAddClick opens expense add directly
        assertTrue(
            "ModernBottomNavBar onAddClick must open AddEditTransactionSheet for Expense directly",
            content.contains("onAddClick = {\n                            transactionToEdit = null\n                            initialDateForAdd = null\n                            initialTypeForAdd = TransactionType.EXPENSE\n                            initialOpenMarketForAdd = false\n                            showGlobalAddSheet = true\n                        }")
        )
        // Long press opens GlobalActionChooserSheet
        assertTrue(
            "ModernBottomNavBar onAddLongClick must open showQuickActionChooserSheet",
            content.contains("onAddLongClick = {\n                            showQuickActionChooserSheet = true\n                        }")
        )
    }

    @Test
    fun testGlobalActionChooserSheetSupportsDirectNavigation() {
        val appNavFile = findSourceFile("src/main/java/com/example/expensetracker/ui/navigation/AppNavigation.kt")
        assertTrue("AppNavigation.kt must exist", appNavFile.exists())
        val content = appNavFile.readText()

        // Verify Expense, Income, Market, and Transfer options
        assertTrue(
            "onAddExpense must open Expense without bouncing",
            content.contains("initialTypeForAdd = TransactionType.EXPENSE") &&
                    content.contains("initialOpenMarketForAdd = false")
        )
        assertTrue(
            "onAddIncome must open Income without bouncing",
            content.contains("initialTypeForAdd = TransactionType.INCOME") &&
                    content.contains("initialOpenMarketForAdd = false")
        )
        assertTrue(
            "onOpenMarket must open unified sheet with market tab active directly",
            content.contains("initialOpenMarketForAdd = true") &&
                    content.contains("showGlobalAddSheet = true")
        )
        // Verify direct execution without artificial delays or dashboard flash
        assertFalse(
            "AppNavigation must not use delay(120) or pendingChooserAction between sheets to prevent dashboard flash",
            content.contains("delay(120)") || content.contains("pendingChooserAction")
        )
    }

    @Test
    fun testAddEditTransactionSheetSeamlessMarketTabSwitching() {
        val sheetFile = findSourceFile("src/main/java/com/example/expensetracker/ui/transactions/AddEditTransactionSheet.kt")
        assertTrue("AddEditTransactionSheet.kt must exist", sheetFile.exists())
        val content = sheetFile.readText()

        // 1. Must accept initialOpenMarketTab parameter
        assertTrue(
            "AddEditTransactionSheet must accept initialOpenMarketTab",
            content.contains("initialOpenMarketTab: Boolean = false")
        )

        // 2. Must remember and synchronize isMarketActive state
        assertTrue(
            "AddEditTransactionSheet must remember isMarketActive with initialOpenMarketTab key",
            content.contains("var isMarketActive by rememberSaveable(existingTransaction")
        )
        assertTrue(
            "AddEditTransactionSheet must sync isMarketActive via LaunchedEffect(initialOpenMarketTab)",
            content.contains("LaunchedEffect(initialOpenMarketTab)")
        )

        // 3. Must embed MarketNativeContent in-place without sheet dismissal
        assertTrue(
            "AddEditTransactionSheet must embed MarketNativeContent when isMarketActive",
            content.contains("MarketNativeContent(")
        )

        // 4. Must pass isMarketActive to TransactionSheetHeader
        assertTrue(
            "AddEditTransactionSheet must pass isMarketActive to Header",
            content.contains("isMarketActive = isMarketActive")
        )

        // 5. Must hide bottom transaction action bar when market is active
        assertTrue(
            "AddEditTransactionSheet must hide TransactionBottomActionBar when market active",
            content.contains("if (!isMarketActive) {\n                TransactionBottomActionBar(")
        )

        // 6. onOpenMarketList from TransactionEditFormBody must toggle isMarketActive in-place
        assertTrue(
            "onOpenMarketList in EditForm must set isMarketActive = true in-place",
            content.contains("onOpenMarketList = if (existingTransaction == null) { { isMarketActive = true } } else null")
        )
    }

    @Test
    fun testCompactReceiptLauncherReducesClutterAndHeight() {
        val receiptFile = findSourceFile("src/main/java/com/example/expensetracker/ui/transactions/TransactionReceiptSection.kt")
        assertTrue("TransactionReceiptSection.kt must exist", receiptFile.exists())
        val content = receiptFile.readText()

        // 1. Unattached state must be a compact clickable container triggering onChangeReceipt
        assertTrue(
            "Unattached receipt state must launch onChangeReceipt on single click",
            content.contains("onChangeReceipt()")
        )

        // 2. Must NOT contain the old bulky dual full-width 48dp camera + gallery button row
        assertFalse(
            "Must not have separate dual Camera and Gallery buttons taking excessive space",
            content.contains("Row(\n                    modifier = Modifier.fillMaxWidth(),\n                    horizontalArrangement = Arrangement.spacedBy(10.dp)\n                ) {\n                    // 1. Camera Capture Button")
        )

        // 3. Must maintain compact single launcher
        assertTrue(
            "Must use compact rounded 14dp single-button card with Add icon",
            content.contains("RoundedCornerShape(14.dp)") &&
                    content.contains("Icons.Filled.Add")
        )

        // 4. Attached state thumbnail and delete confirm dialog must be preserved
        assertTrue(
            "Attached thumbnail preview must be preserved",
            content.contains("receiptBitmap.asImageBitmap()")
        )
        assertTrue(
            "Delete confirmation dialog must be preserved",
            content.contains("showDeleteConfirmDialog")
        )
    }

    @Test
    fun testGlobalActionChooserSmartContextualDefaults() {
        val chooserFile = findSourceFile("src/main/java/com/example/expensetracker/ui/navigation/GlobalActionChooserSheet.kt")
        assertTrue("GlobalActionChooserSheet.kt must exist", chooserFile.exists())
        val content = chooserFile.readText()

        assertTrue(
            "GlobalActionChooserSheet must track time of day for smart contextual defaults",
            content.contains("Calendar.getInstance().get(Calendar.HOUR_OF_DAY)")
        )
        assertTrue(
            "GlobalActionChooserSheet must detect morning bazaar hours",
            content.contains("isMorningBazaarHours = currentHour in 6..11")
        )
        assertTrue(
            "GlobalActionChooserSheet must provide time-aware greeting",
            content.contains("timeGreeting") && content.contains("শুভ সকাল")
        )
        assertTrue(
            "GlobalActionChooserSheet must highlight contextual actions",
            content.contains("isHighlighted = state.isMarketContextual") &&
                    content.contains("isHighlighted = state.isExpenseSuggested")
        )
    }

    @Test
    fun testCalculateChooserContextualStateMorningHoursNoMarketList() {
        val stateBn = com.example.expensetracker.ui.navigation.calculateChooserContextualState(
            currentHour = 8,
            activeMarketList = null,
            isBangla = true
        )
        assertTrue("8 AM must be morning bazaar hours", stateBn.isMorningBazaarHours)
        assertFalse("No market list means no active trip", stateBn.hasActiveTrip)
        assertTrue("Morning hours make market contextual", stateBn.isMarketContextual)
        assertFalse("Expense should not be suggested during morning bazaar", stateBn.isExpenseSuggested)
        assertTrue("Greeting must be Good Morning in Bangla", stateBn.timeGreeting.contains("শুভ সকাল"))
        org.junit.Assert.assertEquals("প্রস্তাবিত", stateBn.marketBadge)
        assertTrue(stateBn.marketSubtitle.contains("সকালের বাজার"))

        val stateEn = com.example.expensetracker.ui.navigation.calculateChooserContextualState(
            currentHour = 8,
            activeMarketList = null,
            isBangla = false
        )
        assertTrue(stateEn.timeGreeting.contains("Good Morning"))
        org.junit.Assert.assertEquals("Suggested", stateEn.marketBadge)
    }

    @Test
    fun testCalculateChooserContextualStateAfternoonWithActiveTripAndRemainingItems() {
        val list = com.example.expensetracker.data.model.MarketList(id = 1, title = "বাজার")
        val items = listOf(
            com.example.expensetracker.data.model.MarketItem(id = 1, listId = 1, name = "আলু", isPurchased = true),
            com.example.expensetracker.data.model.MarketItem(id = 2, listId = 1, name = "পেঁয়াজ", isPurchased = true),
            com.example.expensetracker.data.model.MarketItem(id = 3, listId = 1, name = "টমেটো", isPurchased = false),
            com.example.expensetracker.data.model.MarketItem(id = 4, listId = 1, name = "ডিম", isPurchased = false)
        )
        val marketWithItems = com.example.expensetracker.data.model.MarketListWithItems(list = list, items = items)

        val stateBn = com.example.expensetracker.ui.navigation.calculateChooserContextualState(
            currentHour = 15,
            activeMarketList = marketWithItems,
            isBangla = true
        )
        assertFalse("3 PM is not morning bazaar hours", stateBn.isMorningBazaarHours)
        assertTrue("Active items exist", stateBn.hasActiveTrip)
        assertTrue("Active trip prioritizes market contextual", stateBn.isMarketContextual)
        assertFalse("Expense should yield to active trip", stateBn.isExpenseSuggested)
        assertTrue("Greeting is afternoon", stateBn.timeGreeting.contains("শুভ দুপুর"))
        org.junit.Assert.assertEquals("চলমান ফর্দ", stateBn.marketBadge)
        org.junit.Assert.assertEquals("২টি কেনা বাকি (৪টির মধ্যে)", stateBn.marketSubtitle)

        val stateEn = com.example.expensetracker.ui.navigation.calculateChooserContextualState(
            currentHour = 15,
            activeMarketList = marketWithItems,
            isBangla = false
        )
        org.junit.Assert.assertEquals("Active Trip", stateEn.marketBadge)
        org.junit.Assert.assertEquals("2 items remaining (of 4)", stateEn.marketSubtitle)
    }

    @Test
    fun testCalculateChooserContextualStateAllItemsPurchasedFinalizeTripBadge() {
        val list = com.example.expensetracker.data.model.MarketList(id = 1, title = "বাজার")
        val items = listOf(
            com.example.expensetracker.data.model.MarketItem(id = 1, listId = 1, name = "মাছ", isPurchased = true),
            com.example.expensetracker.data.model.MarketItem(id = 2, listId = 1, name = "মাংস", isPurchased = true)
        )
        val marketWithItems = com.example.expensetracker.data.model.MarketListWithItems(list = list, items = items)

        val stateBn = com.example.expensetracker.ui.navigation.calculateChooserContextualState(
            currentHour = 11,
            activeMarketList = marketWithItems,
            isBangla = true
        )
        org.junit.Assert.assertEquals("হিসাব চূড়ান্ত করুন", stateBn.marketBadge)
        assertTrue(stateBn.marketSubtitle.contains("সবগুলো পণ্য কেনা সম্পন্ন (২টি)"))

        val stateEn = com.example.expensetracker.ui.navigation.calculateChooserContextualState(
            currentHour = 11,
            activeMarketList = marketWithItems,
            isBangla = false
        )
        org.junit.Assert.assertEquals("Finalize Trip", stateEn.marketBadge)
        org.junit.Assert.assertEquals("All 2 items purchased · Finalize trip", stateEn.marketSubtitle)
    }

    @Test
    fun testCalculateChooserContextualStateNightHoursSuggestsExpense() {
        val stateBn = com.example.expensetracker.ui.navigation.calculateChooserContextualState(
            currentHour = 22,
            activeMarketList = null,
            isBangla = true
        )
        assertFalse(stateBn.isMorningBazaarHours)
        assertFalse(stateBn.isMarketContextual)
        assertTrue("Expense should be suggested at night", stateBn.isExpenseSuggested)
        assertTrue("Night greeting in Bangla", stateBn.timeGreeting.contains("শুভ রাত্রি"))
        org.junit.Assert.assertNull(stateBn.marketBadge)
    }

    @Test
    fun testSpatialContinuityAnimatedTransitionsAndBackHandler() {
        val sheetFile = findSourceFile("src/main/java/com/example/expensetracker/ui/transactions/AddEditTransactionSheet.kt")
        assertTrue("AddEditTransactionSheet.kt must exist", sheetFile.exists())
        val content = sheetFile.readText()

        assertTrue(
            "AddEditTransactionSheet must intercept back gesture when market tab is active and form has been visited",
            content.contains("BackHandler(enabled = isMarketActive && hasVisitedTransactionForm)")
        )
        assertTrue(
            "AddEditTransactionSheet must track hasVisitedTransactionForm to prevent dirty form loss",
            content.contains("var hasVisitedTransactionForm by rememberSaveable")
        )
        assertTrue(
            "AddEditTransactionSheet must animate transitions between transaction and market",
            content.contains("AnimatedContent(") && content.contains("label = \"MarketTransactionTransition\"")
        )

        val navSheetsFile = findSourceFile("src/main/java/com/example/expensetracker/ui/navigation/NavigationTransactionSheets.kt")
        assertTrue("NavigationTransactionSheets.kt must exist", navSheetsFile.exists())
        val navSheetsContent = navSheetsFile.readText()

        assertTrue(
            "NavigationTransactionSheets must pass outer sheetState directly for unified animation",
            navSheetsContent.contains("sheetState = sheetState,")
        )
    }
}
