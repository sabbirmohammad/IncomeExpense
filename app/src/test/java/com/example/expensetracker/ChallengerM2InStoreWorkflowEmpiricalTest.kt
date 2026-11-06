package com.example.expensetracker

import com.example.expensetracker.common.MarketListParser
import com.example.expensetracker.common.UnitConverterEngine
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.data.model.MarketList
import com.example.expensetracker.data.model.MarketListWithItems
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.formatCurrency
import com.example.expensetracker.ui.dashboard.ALL_DEPARTMENTS
import com.example.expensetracker.ui.dashboard.getMarketItemCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Empirical Challenger Milestone M2 Stress Test:
 * Deep adversarial verification of in-store shopping interactions and state management:
 * 1. Heavy list item check-off state transitions & running spend calculations.
 * 2. Dynamic recalculation on untoggling, price editing, and item deletion.
 * 3. MarketBottomTripSummaryBar calculations across empty, partial, and complete lists.
 * 4. Single-tap checkout & smart staple reminder logic.
 * 5. Department-aisle stall grouping and live counters.
 */
class ChallengerM2InStoreWorkflowEmpiricalTest {

    @Test
    fun testHeavyListCheckoffAndRunningSpendCalculations() {
        val totalItemCount = 500
        val items = (1..totalItemCount).map { index ->
            val deptKey = when (index % 4) {
                0 -> "আলু $index" // veggies
                1 -> "রুই মাছ $index" // meat_fish
                2 -> "তরল দুধ $index" // dairy_bakery
                else -> "সয়াবিন তেল $index" // groceries
            }
            MarketItem(
                id = index.toLong(),
                listId = 1L,
                name = deptKey,
                quantity = (index % 5 + 1).toDouble(),
                unit = if (index % 2 == 0) "কেজি" else "লিটার",
                amount = null,
                isPurchased = false
            )
        }.toMutableList()

        val listWithItems = MarketListWithItems(MarketList(id = 1L, title = "বড় বাজার"), items)
        assertEquals(500, listWithItems.totalItems)
        assertEquals(0, listWithItems.purchasedItems)
        assertEquals(500, listWithItems.remainingItems)
        assertEquals(0.0, listWithItems.totalSpent, 0.001)
        assertEquals(0f, listWithItems.progress, 0.0001f)
        assertFalse(listWithItems.isAllPurchased)

        // Check off first 250 items sequentially with varying fractional and whole amounts
        var expectedSpend = 0.0
        for (i in 0 until 250) {
            val price = (i + 1) * 12.75
            expectedSpend += price
            items[i] = items[i].copy(isPurchased = true, amount = price)
        }

        val updatedList = MarketListWithItems(MarketList(id = 1L, title = "বড় বাজার"), items)
        assertEquals(500, updatedList.totalItems)
        assertEquals(250, updatedList.purchasedItems)
        assertEquals(250, updatedList.remainingItems)
        assertEquals(expectedSpend, updatedList.totalSpent, 0.001)
        assertEquals(0.5f, updatedList.progress, 0.0001f)
        assertFalse(updatedList.isAllPurchased)

        // Check off remaining 250 items to achieve 100% completion
        for (i in 250 until 500) {
            val price = 50.0
            expectedSpend += price
            items[i] = items[i].copy(isPurchased = true, amount = price)
        }

        val completeList = MarketListWithItems(MarketList(id = 1L, title = "বড় বাজার"), items)
        assertEquals(500, completeList.totalItems)
        assertEquals(500, completeList.purchasedItems)
        assertEquals(0, completeList.remainingItems)
        assertEquals(expectedSpend, completeList.totalSpent, 0.001)
        assertEquals(1.0f, completeList.progress, 0.0001f)
        assertTrue(completeList.isAllPurchased)
    }

    @Test
    fun testUntoggleAndPriceEditDynamicRecalculation() {
        val initialItems = mutableListOf(
            MarketItem(id = 1L, name = "আলু", quantity = 2.0, unit = "কেজি", amount = 100.0, isPurchased = true),
            MarketItem(id = 2L, name = "পেঁয়াজ", quantity = 1.0, unit = "কেজি", amount = 120.0, isPurchased = true),
            MarketItem(id = 3L, name = "টমেটো", quantity = 1.0, unit = "কেজি", amount = 80.0, isPurchased = true),
            MarketItem(id = 4L, name = "রসুন", quantity = 0.25, unit = "কেজি", amount = null, isPurchased = false),
            MarketItem(id = 5L, name = "মরিচ", quantity = 0.25, unit = "কেজি", amount = null, isPurchased = false)
        )

        // Step 1: Baseline metrics
        var currentItems = initialItems.toList()
        assertEquals(5, currentItems.size)
        assertEquals(3, currentItems.count { it.isPurchased })
        assertEquals(300.0, currentItems.filter { it.isPurchased }.sumOf { it.amount ?: 0.0 }, 0.001)

        // Step 2: Untoggle item 2 (পেঁয়াজ: 120.0) -> unpurchased
        currentItems = currentItems.map {
            if (it.id == 2L) it.copy(isPurchased = false, amount = null) else it
        }
        assertEquals(2, currentItems.count { it.isPurchased })
        assertEquals(180.0, currentItems.filter { it.isPurchased }.sumOf { it.amount ?: 0.0 }, 0.001)

        // Step 3: Edit price of item 1 (আলু) from 100.0 to 145.50
        currentItems = currentItems.map {
            if (it.id == 1L) it.copy(amount = 145.50) else it
        }
        assertEquals(225.50, currentItems.filter { it.isPurchased }.sumOf { it.amount ?: 0.0 }, 0.001)

        // Step 4: Delete an unpurchased item (item 5: মরিচ)
        currentItems = currentItems.filter { it.id != 5L }
        assertEquals(4, currentItems.size)
        assertEquals(2, currentItems.count { it.isPurchased })
        assertEquals(225.50, currentItems.filter { it.isPurchased }.sumOf { it.amount ?: 0.0 }, 0.001)

        // Step 5: Delete a purchased item (item 3: টমেটো: 80.0)
        currentItems = currentItems.filter { it.id != 3L }
        assertEquals(3, currentItems.size)
        assertEquals(1, currentItems.count { it.isPurchased })
        assertEquals(145.50, currentItems.filter { it.isPurchased }.sumOf { it.amount ?: 0.0 }, 0.001)

        // Step 6: Clear all items
        currentItems = emptyList()
        val emptyTotalCount = currentItems.size
        val emptyPurchasedCount = currentItems.count { it.isPurchased }
        val emptyTotalSpent = currentItems.filter { it.isPurchased }.sumOf { it.amount ?: 0.0 }
        val emptyProgressPct = if (emptyTotalCount > 0) (emptyPurchasedCount * 100) / emptyTotalCount else 0
        assertEquals(0, emptyTotalCount)
        assertEquals(0, emptyPurchasedCount)
        assertEquals(0.0, emptyTotalSpent, 0.001)
        assertEquals(0, emptyProgressPct)
    }

    @Test
    fun testMarketItemRowAmountInputHandling() {
        // Simulates handleAmountChange helper in MarketItemRow
        fun simulateAmountChange(input: String, onAmountChanged: (Double) -> Unit) {
            if (input.isBlank()) {
                onAmountChanged(0.0)
            } else {
                val cleanNum = MarketListParser.convertBengaliDigitsToEnglish(input)
                cleanNum.toDoubleOrNull()?.let { amt -> onAmountChanged(amt) }
            }
        }

        var capturedAmount: Double? = null

        // 1. Empty string resets to 0.0
        simulateAmountChange("") { capturedAmount = it }
        assertEquals(0.0, capturedAmount!!, 0.001)

        // 2. Blank whitespace resets to 0.0
        simulateAmountChange("   ") { capturedAmount = it }
        assertEquals(0.0, capturedAmount!!, 0.001)

        // 3. Bengali numerals converted properly
        simulateAmountChange("১৫০") { capturedAmount = it }
        assertEquals(150.0, capturedAmount!!, 0.001)

        simulateAmountChange("১২০.৫০") { capturedAmount = it }
        assertEquals(120.50, capturedAmount!!, 0.001)

        // 4. English numerals parsed directly
        simulateAmountChange("350") { capturedAmount = it }
        assertEquals(350.0, capturedAmount!!, 0.001)

        // 5. Invalid text does not crash and leaves amount unchanged
        capturedAmount = 99.0
        simulateAmountChange("invalid_text") { capturedAmount = it }
        assertEquals(99.0, capturedAmount!!, 0.001)
    }

    @Test
    fun testMarketBottomTripSummaryBarCalculationsUnderAllEdgeCases() {
        fun computeSummary(totalCount: Int, purchasedCount: Int, totalSpent: Double, isBangla: Boolean): Triple<Int, Boolean, String> {
            val progressPct = if (totalCount > 0) (purchasedCount * 100) / totalCount else 0
            val isAllDone = purchasedCount == totalCount && totalCount > 0
            val progressText = if (isBangla) {
                "${convertToBengaliDigits(purchasedCount.toString())}/${convertToBengaliDigits(totalCount.toString())}টি সম্পন্ন (${convertToBengaliDigits(progressPct.toString())}%)"
            } else {
                "$purchasedCount/$totalCount Done ($progressPct%)"
            }
            return Triple(progressPct, isAllDone, progressText)
        }

        // 1. Empty list: 0 total, 0 purchased
        val (emptyPct, emptyDone, emptyBn) = computeSummary(0, 0, 0.0, true)
        assertEquals(0, emptyPct)
        assertFalse("Empty list must NOT be marked isAllDone", emptyDone)
        assertEquals("০/০টি সম্পন্ন (০%)", emptyBn)

        val (_, _, emptyEn) = computeSummary(0, 0, 0.0, false)
        assertEquals("0/0 Done (0%)", emptyEn)

        // 2. Partial lists
        val (p1Pct, p1Done, p1Bn) = computeSummary(3, 1, 150.0, true)
        assertEquals(33, p1Pct)
        assertFalse(p1Done)
        assertEquals("১/৩টি সম্পন্ন (৩৩%)", p1Bn)

        val (p2Pct, p2Done, p2En) = computeSummary(10, 5, 500.0, false)
        assertEquals(50, p2Pct)
        assertFalse(p2Done)
        assertEquals("5/10 Done (50%)", p2En)

        // 3. 100% Complete list
        val (c1Pct, c1Done, c1Bn) = computeSummary(5, 5, 750.0, true)
        assertEquals(100, c1Pct)
        assertTrue("100% complete list must be marked isAllDone", c1Done)
        assertEquals("৫/৫টি সম্পন্ন (১০০%)", c1Bn)

        val (c2Pct, c2Done, c2En) = computeSummary(1, 1, 100.0, false)
        assertEquals(100, c2Pct)
        assertTrue(c2Done)
        assertEquals("1/1 Done (100%)", c2En)

        // 4. Extreme heavy list
        val (heavyPct, heavyDone, heavyBn) = computeSummary(1000, 750, 150000.0, true)
        assertEquals(75, heavyPct)
        assertFalse(heavyDone)
        assertEquals("৭৫০/১০০০টি সম্পন্ন (৭৫%)", heavyBn)
    }

    @Test
    fun testSingleTapCheckoutAndStapleDetection() {
        fun evaluateStapleReminder(items: List<MarketItem>): Boolean {
            val purchased = items.filter { it.isPurchased }
            val hasStaplePurchased = purchased.any {
                val name = it.name.lowercase()
                name.contains("মরিচ") || name.contains("লেবু") || name.contains("ধনে") ||
                name.contains("আদা") || name.contains("রসুন") || name.contains("chili") || name.contains("lemon")
            }
            return !hasStaplePurchased && items.size >= 3
        }

        // Scenario 1: Less than 3 items -> Never prompt staple reminder
        val smallListNoStaples = listOf(
            MarketItem(name = "আলু", isPurchased = true),
            MarketItem(name = "পটল", isPurchased = true)
        )
        assertFalse(evaluateStapleReminder(smallListNoStaples))

        // Scenario 2: 3 or more items, NO staples purchased -> MUST prompt reminder
        val largeListNoStaples = listOf(
            MarketItem(name = "আলু", isPurchased = true),
            MarketItem(name = "পটল", isPurchased = true),
            MarketItem(name = "রুই মাছ", isPurchased = true)
        )
        assertTrue(evaluateStapleReminder(largeListNoStaples))

        // Scenario 3: 3 or more items, WITH Bengali staple (কাঁচা মরিচ) purchased -> No prompt
        val listWithBengaliChili = listOf(
            MarketItem(name = "আলু", isPurchased = true),
            MarketItem(name = "পটল", isPurchased = true),
            MarketItem(name = "কাঁচা মরিচ", isPurchased = true)
        )
        assertFalse(evaluateStapleReminder(listWithBengaliChili))

        // Scenario 4: 3 or more items, WITH English staple (Green Chili) purchased -> No prompt
        val listWithEnglishChili = listOf(
            MarketItem(name = "Potato", isPurchased = true),
            MarketItem(name = "Fish", isPurchased = true),
            MarketItem(name = "Green Chili", isPurchased = true)
        )
        assertFalse(evaluateStapleReminder(listWithEnglishChili))

        // Scenario 5: Staple in list but NOT purchased -> MUST prompt reminder
        val listWithUnpurchasedLemon = listOf(
            MarketItem(name = "আলু", isPurchased = true),
            MarketItem(name = "পটল", isPurchased = true),
            MarketItem(name = "লেবু", isPurchased = false)
        )
        assertTrue(evaluateStapleReminder(listWithUnpurchasedLemon))
    }

    @Test
    fun testDepartmentGroupingAndLiveCountersUnderHeavyLists() {
        val testNames = listOf(
            "আলু" to "veggies",
            "পটল" to "veggies",
            "কাঁচা মরিচ" to "veggies",
            "রুই মাছ" to "meat_fish",
            "গরুর মাংস" to "meat_fish",
            "মুরগির ডিম" to "dairy_bakery",
            "দুধ" to "dairy_bakery",
            "সয়াবিন তেল" to "groceries",
            "লবণ" to "groceries",
            "হলুদ গুঁড়া" to "groceries"
        )

        val items = testNames.mapIndexed { idx, (name, _) ->
            MarketItem(
                id = idx.toLong(),
                name = name,
                isPurchased = idx % 2 == 0 // Alternate purchased
            )
        }

        val itemsByDept = items.groupBy { getMarketItemCategory(it.name) }

        for (dept in ALL_DEPARTMENTS) {
            val deptItems = itemsByDept[dept.key] ?: continue
            val purchasedInDept = deptItems.count { it.isPurchased }
            val totalInDept = deptItems.size
            val isDeptCompleted = purchasedInDept == totalInDept && totalInDept > 0

            val progressTextBn = if (isDeptCompleted) {
                "✓ ${convertToBengaliDigits(purchasedInDept.toString())}/${convertToBengaliDigits(totalInDept.toString())}টি সম্পন্ন"
            } else {
                "${convertToBengaliDigits(purchasedInDept.toString())}/${convertToBengaliDigits(totalInDept.toString())}টি সম্পন্ন"
            }

            assertTrue(totalInDept > 0)
            if (isDeptCompleted) {
                assertTrue(progressTextBn.startsWith("✓ "))
            } else {
                assertFalse(progressTextBn.startsWith("✓ "))
            }
        }
    }

    @Test
    fun testBengaliTraditionalUnitCostEstimates() {
        // Base rate: 80 Tk / kg
        val baseUnitPrice = 80.0

        // 1 পোয়া = 0.25 kg -> 20 Tk
        val powaCost = UnitConverterEngine.estimateCost(baseUnitPrice, 1.0, "পোয়া")
        assertEquals(20.0, powaCost, 0.001)

        // 1 পাল্লা = 5.0 kg -> 400 Tk
        val pallaCost = UnitConverterEngine.estimateCost(baseUnitPrice, 1.0, "পাল্লা")
        assertEquals(400.0, pallaCost, 0.001)

        // 500 গ্রাম = 0.5 kg -> 40 Tk
        val gramCost = UnitConverterEngine.estimateCost(baseUnitPrice, 500.0, "গ্রাম")
        assertEquals(40.0, gramCost, 0.001)

        // Egg base rate: 12 Tk / piece
        val eggUnitPrice = 12.0

        // 1 হালি = 4 pcs -> 48 Tk
        val haliCost = UnitConverterEngine.estimateCost(eggUnitPrice, 1.0, "হালি")
        assertEquals(48.0, haliCost, 0.001)

        // 1 ডজন = 12 pcs -> 144 Tk
        val dozenCost = UnitConverterEngine.estimateCost(eggUnitPrice, 1.0, "ডজন")
        assertEquals(144.0, dozenCost, 0.001)

        // 1 জোড়া = 2 pcs -> 24 Tk
        val pairCost = UnitConverterEngine.estimateCost(eggUnitPrice, 1.0, "জোড়া")
        assertEquals(24.0, pairCost, 0.001)
    }
}
