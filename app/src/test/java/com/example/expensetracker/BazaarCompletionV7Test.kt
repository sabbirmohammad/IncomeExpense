package com.example.expensetracker

import androidx.compose.runtime.saveable.SaverScope
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.data.model.MasterProduct
import com.example.expensetracker.ui.bazaar.BazaarShopLogic
import com.example.expensetracker.ui.bazaar.HistoricalPriceEntry
import com.example.expensetracker.ui.navigation.AdaptiveNavigationState
import com.example.expensetracker.ui.navigation.PulseTab
import com.example.expensetracker.ui.navigation.PushedScreen
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BazaarCompletionV7Test {

    private val dummySaverScope = object : SaverScope {
        override fun canBeSaved(value: Any): Boolean = true
    }

    // =========================================================================
    // 1. Checkout Financial Formulas & Edge Cases (Amendment A, B, G)
    // =========================================================================

    @Test
    fun testCheckoutExpenseAmountFormulas() {
        val items = listOf(
            MarketItem(id = 1, name = "আলু", quantity = 2.0, unit = "কেজি", amount = 80.0, isPurchased = true),
            MarketItem(id = 2, name = "পেঁয়াজ", quantity = 1.0, unit = "কেজি", amount = 120.0, isPurchased = true),
            MarketItem(id = 3, name = "লেবু", quantity = 4.0, unit = "পিস", amount = null, isPurchased = false)
        )

        // Case 1: Default total (no edit) and no other costs -> exact item sum (80 + 120 = 200)
        val defaultSummary = BazaarShopLogic.calculateCheckoutSummary(items, customTotal = null, otherCosts = 0.0)
        assertEquals(2, defaultSummary.purchasedCount)
        assertEquals(0, defaultSummary.unpricedCount)
        assertEquals(200.0, defaultSummary.itemSum, 0.001)
        assertEquals(200.0, defaultSummary.editedTotal, 0.001)
        assertEquals(0.0, defaultSummary.otherCosts, 0.001)
        assertEquals(0.0, defaultSummary.adjustment, 0.001)
        assertEquals(200.0, defaultSummary.finalExpenseAmount, 0.001)
        assertTrue(defaultSummary.isCompleteEnabled)
        assertFalse(defaultSummary.requiresZeroConfirm)

        // Case 2: User negotiated discount (total edited to 190) + other costs (rickshaw 40)
        val discountSummary = BazaarShopLogic.calculateCheckoutSummary(items, customTotal = 190.0, otherCosts = 40.0)
        assertEquals(200.0, discountSummary.itemSum, 0.001)
        assertEquals(190.0, discountSummary.editedTotal, 0.001)
        assertEquals(40.0, discountSummary.otherCosts, 0.001)
        assertEquals(-10.0, discountSummary.adjustment, 0.001) // 190 - 200 = -10
        assertEquals(230.0, discountSummary.finalExpenseAmount, 0.001) // 190 + 40 = 230
        assertTrue(discountSummary.isCompleteEnabled)

        // Case 3: User pays extra round-off adjustment (total edited to 205) + no other costs
        val extraSummary = BazaarShopLogic.calculateCheckoutSummary(items, customTotal = 205.0, otherCosts = 0.0)
        assertEquals(200.0, extraSummary.itemSum, 0.001)
        assertEquals(205.0, extraSummary.editedTotal, 0.001)
        assertEquals(5.0, extraSummary.adjustment, 0.001) // 205 - 200 = +5
        assertEquals(205.0, extraSummary.finalExpenseAmount, 0.001)
    }

    @Test
    fun testCheckoutZeroItemsBoughtBehavior() {
        val allUnpurchased = listOf(
            MarketItem(id = 1, name = "আলু", quantity = 2.0, unit = "কেজি", amount = null, isPurchased = false),
            MarketItem(id = 2, name = "পেঁয়াজ", quantity = 1.0, unit = "কেজি", amount = null, isPurchased = false)
        )

        // Rule A1: 0 items bought AND 0 other costs -> MUST disable complete button and show warning
        val zeroZeroSummary = BazaarShopLogic.calculateCheckoutSummary(allUnpurchased, customTotal = null, otherCosts = 0.0)
        assertEquals(0, zeroZeroSummary.purchasedCount)
        assertFalse("Must not allow completing trip with 0 items and 0 other costs", zeroZeroSummary.isCompleteEnabled)
        assertNotNull(zeroZeroSummary.validationMessage)

        // Rule A2: 0 items bought BUT other costs > 0 -> Allowed!
        val otherCostsOnlySummary = BazaarShopLogic.calculateCheckoutSummary(allUnpurchased, customTotal = null, otherCosts = 60.0)
        assertEquals(0, otherCostsOnlySummary.purchasedCount)
        assertEquals(60.0, otherCostsOnlySummary.otherCosts, 0.001)
        assertEquals(60.0, otherCostsOnlySummary.finalExpenseAmount, 0.001)
        assertTrue("Must allow other-costs-only trip completion", otherCostsOnlySummary.isCompleteEnabled)
        assertFalse(otherCostsOnlySummary.requiresZeroConfirm)

        // Rule A3: Items bought, but user sets edited total = 0 and other costs = 0 -> Requires confirmation
        val oneBought = listOf(
            MarketItem(id = 1, name = "আলু", quantity = 2.0, unit = "কেজি", amount = 80.0, isPurchased = true)
        )
        val zeroTotalSummary = BazaarShopLogic.calculateCheckoutSummary(oneBought, customTotal = 0.0, otherCosts = 0.0)
        assertEquals(1, zeroTotalSummary.purchasedCount)
        assertEquals(0.0, zeroTotalSummary.editedTotal, 0.001)
        assertEquals(0.0, zeroTotalSummary.finalExpenseAmount, 0.001)
        assertTrue(zeroTotalSummary.requiresZeroConfirm)

        // Negative values are blocked
        val negativeSummary = BazaarShopLogic.calculateCheckoutSummary(oneBought, customTotal = -50.0, otherCosts = 0.0)
        assertFalse("Negative totals must be blocked", negativeSummary.isCompleteEnabled)
    }

    @Test
    fun testCheckoutUnpricedItemsHandling() {
        // Items marked purchased without price (legacy amount == null or 0)
        val itemsWithUnpriced = listOf(
            MarketItem(id = 1, name = "আলু", quantity = 2.0, unit = "কেজি", amount = 80.0, isPurchased = true),
            MarketItem(id = 2, name = "কাঁচা মরিচ", quantity = 100.0, unit = "গ্রাম", amount = null, isPurchased = true),
            MarketItem(id = 3, name = "ধনেপাতা", quantity = 1.0, unit = "আঁটি", amount = 0.0, isPurchased = true),
            MarketItem(id = 4, name = "টমেটো", quantity = 1.0, unit = "কেজি", amount = null, isPurchased = false)
        )

        val summary = BazaarShopLogic.calculateCheckoutSummary(itemsWithUnpriced, customTotal = null, otherCosts = 10.0)
        assertEquals(3, summary.purchasedCount)
        assertEquals(2, summary.unpricedCount) // কাঁচা মরিচ (null) and ধনেপাতা (0.0)
        assertEquals(80.0, summary.itemSum, 0.001) // Only আলু contributes
        assertEquals(90.0, summary.finalExpenseAmount, 0.001) // 80 + 10 = 90
    }

    // =========================================================================
    // 2. Done Screen Insights Derivation (Amendment C & D)
    // =========================================================================

    @Test
    fun testDoneScreenInsightsDerivation() {
        val now = 1700000000000L
        val txId = 501L

        // Trip items with potato bought twice (two lines) and brand new dragon fruit
        val tripItems = listOf(
            MarketItem(id = 1, name = "আলু", quantity = 1.0, unit = "কেজি", amount = 30.0, isPurchased = true),
            MarketItem(id = 2, name = "আলু", quantity = 2.0, unit = "কেজি", amount = 60.0, isPurchased = true), // Combined potato: 3kg for 90 = 30/kg
            MarketItem(id = 3, name = "পেঁয়াজ", quantity = 1.0, unit = "কেজি", amount = 140.0, isPurchased = true), // 140/kg
            MarketItem(id = 4, name = "ড্রাগন ফল", quantity = 1.0, unit = "পিস", amount = 250.0, isPurchased = true) // First ever
        )

        // Historical prices before this trip
        val historicalPrices = listOf(
            HistoricalPriceEntry(productName = "আলু", unitPrice = 40.0, timestamp = now - 100000, transactionId = 400L), // previous was 40/kg -> this trip 30/kg is CHEAPER
            HistoricalPriceEntry(productName = "পেঁয়াজ", unitPrice = 120.0, timestamp = now - 100000, transactionId = 401L), // previous was 120/kg -> this trip 140/kg is PRICIER
            // Current trip's own recorded prices
            HistoricalPriceEntry(productName = "আলু", unitPrice = 30.0, timestamp = now, transactionId = txId),
            HistoricalPriceEntry(productName = "পেঁয়াজ", unitPrice = 140.0, timestamp = now, transactionId = txId),
            HistoricalPriceEntry(productName = "ড্রাগন ফল", unitPrice = 250.0, timestamp = now, transactionId = txId)
        )

        val insights = BazaarShopLogic.calculateTripPriceInsights(
            tripItems = tripItems,
            historicalPrices = historicalPrices,
            budgetCap = 600.0,
            totalSpent = 480.0,
            tripTimestamp = now,
            tripTransactionId = txId
        )

        // Potato: combined 3kg for 90 = 30/kg vs previous 40 -> 1 CHEAPER
        assertEquals(1, insights.cheaperCount)
        // Onion: 140 vs previous 120 -> 1 PRICIER
        assertEquals(1, insights.pricierCount)
        // Dragon fruit: first ever -> not counted in cheaper or pricier
        assertTrue("This trip is the latest purchase for all commodities", insights.isLatestTrip)
        assertEquals(120.0, insights.budgetDelta!!, 0.001) // 600 - 480 = 120 under budget
    }

    @Test
    fun testDoneScreenInsightsHiddenWhenLaterTripOverwrites() {
        val tripTime = 1700000000000L
        val txId = 501L
        val laterTime = tripTime + 200000L // Later trip occurred

        val tripItems = listOf(
            MarketItem(id = 1, name = "টমেটো", quantity = 1.0, unit = "কেজি", amount = 80.0, isPurchased = true)
        )

        val historyWithLaterTrip = listOf(
            HistoricalPriceEntry(productName = "টমেটো", unitPrice = 100.0, timestamp = tripTime - 10000, transactionId = 400L),
            HistoricalPriceEntry(productName = "টমেটো", unitPrice = 80.0, timestamp = tripTime, transactionId = txId),
            HistoricalPriceEntry(productName = "টমেটো", unitPrice = 90.0, timestamp = laterTime, transactionId = 600L) // Newer purchase exists!
        )

        val insights = BazaarShopLogic.calculateTripPriceInsights(
            tripItems = tripItems,
            historicalPrices = historyWithLaterTrip,
            budgetCap = 100.0,
            totalSpent = 80.0,
            tripTimestamp = tripTime,
            tripTransactionId = txId
        )

        assertFalse("Must flag isLatestTrip = false when newer purchase exists in history", insights.isLatestTrip)
    }

    // =========================================================================
    // 3. Backdated Trips Invariant (Amendment D)
    // =========================================================================

    @Test
    fun testBackdatedTripTimestampContract() {
        val existing = MasterProduct(
            id = 10,
            name = "আলু",
            normalizedName = "আলু",
            category = "veggies",
            defaultUnit = "কেজি",
            latestUnitPrice = 45.0,
            previousUnitPrice = 40.0,
            minUnitPrice = 35.0,
            maxUnitPrice = 50.0,
            avgUnitPrice = 42.0,
            purchaseCount = 5,
            isStaple = true,
            firstPurchasedAt = 1000L,
            lastPurchasedAt = 5000L
        )

        // Case 1: Newer trip (timestamp 6000 >= lastPurchasedAt 5000)
        // Must update latestUnitPrice, previousUnitPrice, and lastPurchasedAt
        val newerTimestamp = 6000L
        val newerUnitPrice = 48.0
        val isNewer = newerTimestamp >= existing.lastPurchasedAt
        val newLatest1 = if (isNewer) newerUnitPrice else existing.latestUnitPrice
        val newPrev1 = if (isNewer) existing.latestUnitPrice else existing.previousUnitPrice
        val newLastPurchasedAt1 = if (isNewer) newerTimestamp else existing.lastPurchasedAt

        assertTrue(isNewer)
        assertEquals(48.0, newLatest1, 0.001)
        assertEquals(45.0, newPrev1!!, 0.001)
        assertEquals(6000L, newLastPurchasedAt1)

        // Case 2: Backdated trip (timestamp 3000 < lastPurchasedAt 5000)
        // Must NOT overwrite latestUnitPrice, previousUnitPrice, or lastPurchasedAt
        val backdatedTimestamp = 3000L
        val backdatedUnitPrice = 38.0
        val isBackdatedLatest = backdatedTimestamp >= existing.lastPurchasedAt
        val newLatest2 = if (isBackdatedLatest) backdatedUnitPrice else existing.latestUnitPrice
        val newPrev2 = if (isBackdatedLatest) existing.latestUnitPrice else existing.previousUnitPrice
        val newLastPurchasedAt2 = if (isBackdatedLatest) backdatedTimestamp else existing.lastPurchasedAt

        assertFalse(isBackdatedLatest)
        assertEquals("Backdated trip must NOT overwrite latestUnitPrice", 45.0, newLatest2, 0.001)
        assertEquals("Backdated trip must NOT overwrite previousUnitPrice", 40.0, newPrev2!!, 0.001)
        assertEquals("Backdated trip must NOT overwrite lastPurchasedAt", 5000L, newLastPurchasedAt2)
    }

    // =========================================================================
    // 4. Budget Delta & Cap Handling (Amendment B & H)
    // =========================================================================

    @Test
    fun testDoneScreenBudgetDeltaCalculation() {
        // Under budget: cap 1500, spent 1420 -> delta = +80
        val underBudget = BazaarShopLogic.calculateTripPriceInsights(
            tripItems = emptyList(),
            historicalPrices = emptyList(),
            budgetCap = 1500.0,
            totalSpent = 1420.0,
            tripTimestamp = 1000L,
            tripTransactionId = null
        )
        assertEquals(80.0, underBudget.budgetDelta!!, 0.001)

        // Over budget: cap 1000, spent 1080 -> delta = -80
        val overBudget = BazaarShopLogic.calculateTripPriceInsights(
            tripItems = emptyList(),
            historicalPrices = emptyList(),
            budgetCap = 1000.0,
            totalSpent = 1080.0,
            tripTimestamp = 1000L,
            tripTransactionId = null
        )
        assertEquals(-80.0, overBudget.budgetDelta!!, 0.001)

        // No budget cap -> delta is null
        val noCap = BazaarShopLogic.calculateTripPriceInsights(
            tripItems = emptyList(),
            historicalPrices = emptyList(),
            budgetCap = null,
            totalSpent = 850.0,
            tripTimestamp = 1000L,
            tripTransactionId = null
        )
        assertNull(noCap.budgetDelta)
    }

    // =========================================================================
    // 5. Adaptive Navigation Saver Round-Trip for BAZAAR_DONE (Amendment H)
    // =========================================================================

    @Test
    fun testSaverRoundTripBazaarDone() {
        val original = AdaptiveNavigationState(
            initialTab = PulseTab.BAZAAR,
            initialPushedScreen = PushedScreen.BAZAAR_DONE(333L)
        )

        val saved = with(AdaptiveNavigationState.Saver) {
            dummySaverScope.save(original)
        }
        assertNotNull(saved)

        val restored = AdaptiveNavigationState.Saver.restore(saved!!)
        assertNotNull(restored)
        assertEquals(PulseTab.BAZAAR, restored!!.selectedTab)
        assertEquals(PushedScreen.BAZAAR_DONE(333L), restored.pushedScreen)
        assertFalse("Must not be top level when BAZAAR_DONE is pushed", restored.isTopLevel)
    }
}
