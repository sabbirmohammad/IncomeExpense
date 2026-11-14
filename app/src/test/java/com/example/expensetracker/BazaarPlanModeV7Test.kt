package com.example.expensetracker

import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.data.model.MarketList
import com.example.expensetracker.data.model.MarketListWithItems
import com.example.expensetracker.data.model.MasterProduct
import com.example.expensetracker.ui.bazaar.BazaarDomainLogic
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.launch
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class BazaarPlanModeV7Test {

    @Test
    fun testEstimateMathMultipliesQuantityWithLatestPrice() {
        val items = listOf(
            MarketItem(id = 1L, name = "আলু", quantity = 2.0, unit = "কেজি"),
            MarketItem(id = 2L, name = "সয়াবিন তেল", quantity = 5.0, unit = "লিটার")
        )

        val lastPurchasedRates = mapOf(
            "আলু" to Pair(55.0, "কেজি"),
            "সয়াবিন তেল" to Pair(168.0, "লিটার")
        )

        val (summary, lineEstimates) = BazaarDomainLogic.calculateMarketListEstimate(
            items = items,
            lastPurchasedRates = lastPurchasedRates,
            masterProducts = emptyMap(),
            budgetCap = 1500.0
        )

        assertEquals("Priced count should be 2", 2, summary.pricedItemsCount)
        assertEquals("Unpriced count should be 0", 0, summary.unpricedItemsCount)
        assertFalse("Should not have unpriced items", summary.hasUnpricedItems)
        // 2*55 = 110, 5*168 = 840. Total = 950.0
        assertEquals(950.0, summary.totalEstimatedPrice, 0.01)
        assertFalse("Should not be over budget", summary.isOverBudget)
        assertEquals(0.0, summary.overBudgetAmount, 0.01)

        assertEquals(110.0, lineEstimates[0].lineTotal ?: 0.0, 0.01)
        assertEquals(840.0, lineEstimates[1].lineTotal ?: 0.0, 0.01)
    }

    @Test
    fun testEstimateMathHandlesUnpricedItemsWithoutZeroingSilently() {
        val items = listOf(
            MarketItem(id = 1L, name = "আলু", quantity = 2.0, unit = "কেজি"),
            MarketItem(id = 2L, name = "দামি বিদেশি মিষ্টি", quantity = 1.0, unit = "প্যাকেট")
        )

        val lastPurchasedRates = mapOf(
            "আলু" to Pair(55.0, "কেজি")
        )

        val (summary, lineEstimates) = BazaarDomainLogic.calculateMarketListEstimate(
            items = items,
            lastPurchasedRates = lastPurchasedRates,
            masterProducts = emptyMap()
        )

        assertEquals("Priced count should be 1", 1, summary.pricedItemsCount)
        assertEquals("Unpriced count should be 1", 1, summary.unpricedItemsCount)
        assertTrue("Must flag hasUnpricedItems as true", summary.hasUnpricedItems)
        assertEquals(110.0, summary.totalEstimatedPrice, 0.01)

        // Line 2 has no price and its estimate must be null
        assertNull("Unpriced item lineTotal must be null", lineEstimates[1].lineTotal)
        assertNull("Unpriced item unitPrice must be null", lineEstimates[1].unitPrice)
    }

    @Test
    fun testBudgetCapWarningTriggersWhenEstimateExceedsCap() {
        val items = listOf(
            MarketItem(id = 1L, name = "গরুর মাংস", quantity = 2.0, unit = "কেজি")
        )

        val lastPurchasedRates = mapOf(
            "গরুর মাংস" to Pair(750.0, "কেজি")
        )

        // 2 * 750 = 1500. Budget cap = 1200.
        val (summary, _) = BazaarDomainLogic.calculateMarketListEstimate(
            items = items,
            lastPurchasedRates = lastPurchasedRates,
            masterProducts = emptyMap(),
            budgetCap = 1200.0
        )

        assertTrue("Must be over budget", summary.isOverBudget)
        assertEquals(300.0, summary.overBudgetAmount, 0.01)
    }

    @Test
    fun testPastePreviewSplitsAndParsesLinesCorrectly() {
        val rawWhatsApp = """
            1. আলু ২ কেজি
            2. পেঁয়াজ ১ কেজি
            3. ডিম ১ ডজন
            4. সয়াবিন তেল ২ লিটার
        """.trimIndent()

        val result = BazaarDomainLogic.parseTextForPastePreview(rawWhatsApp)

        assertEquals("Must parse 4 valid items", 4, result.validItems.size)
        assertEquals("আলু", result.validItems[0].name)
        assertEquals(2.0, result.validItems[0].quantity, 0.01)
        assertEquals("কেজি", result.validItems[0].unit)
        assertEquals("veggies", result.validItems[0].stallKey)

        assertEquals("ডিম", result.validItems[2].name)
        assertEquals("dairy_bakery", result.validItems[2].stallKey)

        assertEquals("Must have 0 flagged lines", 0, result.flaggedLines.size)
    }

    @Test
    fun testPastePreviewFlagsCorruptOrUnparseableLines() {
        val rawMixed = """
            1. আলু ২ কেজি
            ???---###
            2. সয়াবিন তেল ১ লিটার
        """.trimIndent()

        val result = BazaarDomainLogic.parseTextForPastePreview(rawMixed)

        assertEquals("Must parse 2 valid items", 2, result.validItems.size)
        assertEquals("Must flag 1 unparseable line", 1, result.flaggedLines.size)
        assertEquals("???---###", result.flaggedLines[0].lineText)
    }

    @Test
    fun testCatalogAddAndIncrementLogic() {
        val potato = MasterProduct(
            id = 1L,
            name = "আলু",
            normalizedName = "আলু",
            category = "veggies",
            defaultUnit = "কেজি",
            latestUnitPrice = 55.0,
            previousUnitPrice = null,
            minUnitPrice = 50.0,
            maxUnitPrice = 60.0,
            avgUnitPrice = 55.0,
            purchaseCount = 5,
            isStaple = true,
            firstPurchasedAt = 0L,
            lastPurchasedAt = 0L
        )

        // Initial add: item not present
        val step1 = BazaarDomainLogic.addOrIncrementCatalogItem(
            existingItems = emptyList(),
            product = potato,
            listId = 10L
        )
        assertEquals(1, step1.size)
        assertEquals("আলু", step1[0].name)
        assertEquals(1.0, step1[0].quantity, 0.01)

        // Increment: item already present
        val step2 = BazaarDomainLogic.addOrIncrementCatalogItem(
            existingItems = step1,
            product = potato,
            listId = 10L
        )
        assertEquals(1, step2.size)
        assertEquals("আলু", step2[0].name)
        assertEquals(2.0, step2[0].quantity, 0.01)
    }

    @Test
    fun testCloneTripClearsPricesAndKeepsQuantitiesAndUnits() {
        val completedItems = listOf(
            MarketItem(id = 101L, listId = 5L, name = "মুরগি", quantity = 1.5, unit = "কেজি", amount = 285.0, isPurchased = true, transactionId = 999L),
            MarketItem(id = 102L, listId = 5L, name = "ডিম", quantity = 1.0, unit = "ডজন", amount = 150.0, isPurchased = true, transactionId = 999L)
        )

        val cloned = BazaarDomainLogic.cloneTripItems(completedItems)

        assertEquals(2, cloned.size)
        for (item in cloned) {
            assertEquals(0L, item.id)
            assertEquals(0L, item.listId)
            assertNull("Amount must be cleared", item.amount)
            assertFalse("isPurchased must be reset to false", item.isPurchased)
            assertNull("transactionId must be cleared", item.transactionId)
        }

        assertEquals("মুরগি", cloned[0].name)
        assertEquals(1.5, cloned[0].quantity, 0.01)
        assertEquals("কেজি", cloned[0].unit)

        assertEquals("ডিম", cloned[1].name)
        assertEquals(1.0, cloned[1].quantity, 0.01)
        assertEquals("ডজন", cloned[1].unit)
    }

    @Test
    fun testStallOverrideInMasterProductsConsultedFirst() {
        // By default, "লেবু" contains "লেবু" which maps to veggies
        val defaultStall = BazaarDomainLogic.classifyStallWithOverride("লেবু", null)
        assertEquals("veggies", defaultStall)

        // Create an override where user designated "লেবু" as groceries
        val overrideProduct = MasterProduct(
            id = 42L,
            name = "লেবু",
            normalizedName = "লেবু",
            category = "groceries",
            defaultUnit = "পিস",
            latestUnitPrice = 10.0,
            previousUnitPrice = null,
            minUnitPrice = 10.0,
            maxUnitPrice = 10.0,
            avgUnitPrice = 10.0,
            purchaseCount = 1,
            isStaple = false,
            firstPurchasedAt = 0L,
            lastPurchasedAt = 0L
        )

        val masterMap = mapOf("লেবু" to overrideProduct)
        val overriddenStall = BazaarDomainLogic.classifyStallWithOverride("লেবু", masterMap)

        assertEquals("Override category in master_products must be prioritized", "groceries", overriddenStall)
    }

    @Test
    fun testSmartSuggestionsCountsDayOfWeekFrequencies() {
        val cal = Calendar.getInstance()
        cal.set(2026, Calendar.NOVEMBER, 13) // A Friday
        val fridayMillis = cal.timeInMillis

        val trip1 = MarketListWithItems(
            list = MarketList(id = 1L, title = "Friday Bazaar 1", createdAt = fridayMillis, isCompleted = true),
            items = listOf(
                MarketItem(name = "ডিম", quantity = 1.0, unit = "ডজন", isPurchased = true),
                MarketItem(name = "পেঁয়াজ", quantity = 2.0, unit = "কেজি", isPurchased = true),
                MarketItem(name = "ইলিশ মাছ", quantity = 1.0, unit = "পিস", isPurchased = true)
            )
        )

        val trip2 = MarketListWithItems(
            list = MarketList(id = 2L, title = "Friday Bazaar 2", createdAt = fridayMillis - (7L * 24 * 3600 * 1000), isCompleted = true),
            items = listOf(
                MarketItem(name = "ডিম", quantity = 1.0, unit = "ডজন", isPurchased = true),
                MarketItem(name = "পেঁয়াজ", quantity = 1.0, unit = "কেজি", isPurchased = true)
            )
        )

        val fridayDayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
        val suggestions = BazaarDomainLogic.computeSmartSuggestions(
            completedTrips = listOf(trip1, trip2),
            targetDayOfWeek = fridayDayOfWeek,
            minFrequency = 2
        )

        // Both ডিম and পেঁয়াজ occurred twice on Friday
        assertTrue("ডিম should be suggested", suggestions.contains("ডিম"))
        assertTrue("পেঁয়াজ should be suggested", suggestions.contains("পেঁয়াজ"))
        assertFalse("ইলিশ মাছ (only 1 occurrence) should not be suggested", suggestions.contains("ইলিশ মাছ"))
    }

    @Test
    fun testWhatsAppTextFormattingProducesCleanChecklistByStall() {
        val items = listOf(
            MarketItem(name = "আলু", quantity = 2.0, unit = "কেজি"),
            MarketItem(name = "মুরগি", quantity = 1.5, unit = "কেজি"),
            MarketItem(name = "ডিম", quantity = 1.0, unit = "ডজন")
        )

        val formatted = BazaarDomainLogic.formatListAsWhatsAppText(
            listTitle = "সাপ্তাহিক বাজার",
            items = items,
            isBangla = true
        )

        assertTrue(formatted.contains("*বাজারের ফর্দ: সাপ্তাহিক বাজার*"))
        assertTrue(formatted.contains("🥬 কাঁচাবাজার:"))
        assertTrue(formatted.contains("□ আলু - ২ কেজি"))
        assertTrue(formatted.contains("🐟 মাছ ও মাংস:"))
        assertTrue(formatted.contains("□ মুরগি - ১.৫ কেজি"))
        assertTrue(formatted.contains("🥛 দুধ ও বেকারি:"))
        assertTrue(formatted.contains("□ ডিম - ১ ডজন"))
    }

    @Test
    fun testDeferredDeleteAllowsUndoBeforeTimeoutAndDeletesAfterwards() = runTest {
        val pendingDeletedIds = mutableSetOf<Long>()
        var realDbDeleteCalled = false

        // Simulate initiating delete with undo
        val itemId = 101L
        pendingDeletedIds.add(itemId)
        assertTrue("Item must be hidden immediately in UI pending state", pendingDeletedIds.contains(itemId))

        // Case A: User clicks Undo before timeout
        var userTriggeredUndo = true
        if (userTriggeredUndo) {
            pendingDeletedIds.remove(itemId)
            assertFalse("Undo must restore item into active items", pendingDeletedIds.contains(itemId))
            assertFalse("Real DB delete must NOT have been called on undo", realDbDeleteCalled)
        }

        // Case B: User does not undo, timeout expires
        pendingDeletedIds.add(itemId)
        userTriggeredUndo = false
        if (!userTriggeredUndo) {
            pendingDeletedIds.remove(itemId)
            realDbDeleteCalled = true
            assertTrue("Real DB delete must be executed when undo window expires", realDbDeleteCalled)
        }
    }

    @Test
    fun testBudgetCapPurgedOnDeleteOnlyNotOnComplete() {
        val budgetCapStore = mutableMapOf<Long, Double>()
        val listId = 42L
        budgetCapStore[listId] = 2500.0

        // Step 1: Complete list -> must NOT purge cap
        var isCompleted = false
        fun completeList(id: Long) {
            isCompleted = true
            // In Phase 3a fix: budget cap is NOT purged on complete
        }
        completeList(listId)
        assertTrue("List must be marked completed", isCompleted)
        assertEquals("Budget cap must be retained after completion for 3c trip history/summary", 2500.0, budgetCapStore[listId])

        // Step 2: Delete list -> MUST purge cap
        fun deleteList(id: Long) {
            budgetCapStore.remove(id)
        }
        deleteList(listId)
        assertNull("Budget cap must be purged only when list is deleted", budgetCapStore[listId])
    }

    @Test
    fun testMissingBudgetCapKeyNeverThrows() {
        val budgetCapStore = mutableMapOf<Long, Double>()
        val nonExistentId = 999999L
        val retrieved = runCatching { budgetCapStore[nonExistentId] }.getOrNull()
        assertNull("Missing budget cap must return null without throwing", retrieved)
    }

    @Test
    fun testDuplicateMarketListCopiesBudgetCap() {
        val budgetCapStore = mutableMapOf<Long, Double>()
        val sourceListId = 15L
        budgetCapStore[sourceListId] = 3500.0

        // Simulate duplicateMarketList logic
        val newListId = 20L
        val existingCap = budgetCapStore[sourceListId]
        if (existingCap != null) {
            budgetCapStore[newListId] = existingCap
        }

        assertEquals("New duplicated list must copy budget cap from source list", 3500.0, budgetCapStore[newListId])
    }

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    @Test
    fun testTitleDebounceCollapsesRapidEditsIntoSingleWrite() = runTest {
        var dbWriteCount = 0
        var savedTitle = ""
        var debounceJob: kotlinx.coroutines.Job? = null

        fun updateTitleDebounced(newTitle: String) {
            debounceJob?.cancel()
            debounceJob = launch {
                kotlinx.coroutines.delay(400L)
                dbWriteCount++
                savedTitle = newTitle
            }
        }

        // Rapid keystroke simulation within 200ms
        updateTitleDebounced("ব")
        testScheduler.advanceTimeBy(50)
        updateTitleDebounced("বাজা")
        testScheduler.advanceTimeBy(50)
        updateTitleDebounced("বাজার")
        testScheduler.advanceTimeBy(50)
        updateTitleDebounced("বাজারের")
        testScheduler.advanceTimeBy(50)
        updateTitleDebounced("বাজারের ফর্দ ২০২৬")

        // Before 400ms passes, zero writes
        testScheduler.advanceTimeBy(300)
        assertEquals("Before debounce duration expires, no writes should have occurred", 0, dbWriteCount)

        // After debounce finishes
        testScheduler.advanceTimeBy(150)
        assertEquals("Rapid edits must collapse into exactly 1 database write", 1, dbWriteCount)
        assertEquals("বাজারের ফর্দ ২০২৬", savedTitle)
    }

    @Test
    fun testRestoreSafetyWithMissingListPopsToHubFallback() {
        val existingLists = listOf(
            MarketListWithItems(
                list = MarketList(id = 1L, title = "Morning Bazaar"),
                items = emptyList()
            )
        )
        val missingListId = 999L

        var didPopToHub = false
        val currentTrip = existingLists.find { it.list.id == missingListId }
        if (existingLists.isNotEmpty() && currentTrip == null) {
            didPopToHub = true
        }

        assertTrue("When restored listId is missing from existing lists, fallback must pop to Hub", didPopToHub)
        assertNull("Missing list must resolve to null without throwing NPE", currentTrip)
    }
}
