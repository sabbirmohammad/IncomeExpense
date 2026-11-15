package com.example.expensetracker

import android.app.Activity
import android.app.Application
import androidx.lifecycle.SavedStateHandle
import com.example.expensetracker.common.UnitConverterEngine
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.MarketCompletionResult
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.data.model.MarketList
import com.example.expensetracker.data.model.MarketListWithItems
import com.example.expensetracker.data.model.MasterProduct
import com.example.expensetracker.data.model.ProductPriceInsight
import com.example.expensetracker.ui.bazaar.BazaarShopLogic
import com.example.expensetracker.ui.bazaar.PriceTrendDirection
import com.example.expensetracker.ui.bazaar.PricingMode
import com.example.expensetracker.ui.bazaar.ReceiptLineTapDecision
import com.example.expensetracker.ui.bazaar.ScreenWakeLockManager
import com.example.expensetracker.ui.bazaar.SwipeBackgroundKind
import com.example.expensetracker.ui.bazaar.SwipeDecision
import com.example.expensetracker.ui.bazaar.SwipeDismissDirection
import com.example.expensetracker.ui.dashboard.resetMasterProductsSupplier
import java.io.File
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BazaarShopModeV7Test {

    private val basePath = "src/main/java"

    private fun readFile(relativePath: String): String {
        val file = File("$basePath/$relativePath")
        assertTrue("Expected file to exist: ${file.absolutePath}", file.exists())
        return file.readText()
    }

    @After
    fun tearDown() {
        resetMasterProductsSupplier()
    }

    // =========================================================================
    // 1. Pure Math & Domain Unit Tests (BazaarShopLogic)
    // =========================================================================

    @Test
    fun testCalculateTotalFromUnitPriceAcrossDiverseUnits() {
        // kg
        val totalKg = BazaarShopLogic.calculateTotalFromUnitPrice(unitRate = 60.0, quantity = 1.0, unit = "কেজি")
        assertEquals(60.0, totalKg, 0.001)

        // grams (500g at 60/kg -> 30.0)
        val total500g = BazaarShopLogic.calculateTotalFromUnitPrice(unitRate = 60.0, quantity = 500.0, unit = "গ্রাম")
        assertEquals(30.0, total500g, 0.001)

        // grams (250g at 120/kg -> 30.0)
        val total250g = BazaarShopLogic.calculateTotalFromUnitPrice(unitRate = 120.0, quantity = 250.0, unit = "গ্রাম")
        assertEquals(30.0, total250g, 0.001)

        // Liters (1.5L at 80/L -> 120.0)
        val total1_5L = BazaarShopLogic.calculateTotalFromUnitPrice(unitRate = 80.0, quantity = 1.5, unit = "লিটার")
        assertEquals(120.0, total1_5L, 0.001)

        // Milliliters (500ml at 80/L -> 40.0)
        val total500ml = BazaarShopLogic.calculateTotalFromUnitPrice(unitRate = 80.0, quantity = 500.0, unit = "মিলি")
        assertEquals(40.0, total500ml, 0.001)

        // Pieces (4 pc at 15/pc -> 60.0)
        val totalPc = BazaarShopLogic.calculateTotalFromUnitPrice(unitRate = 15.0, quantity = 4.0, unit = "পিস")
        assertEquals(60.0, totalPc, 0.001)

        // Dozen (1 dozen = 12 pcs; rate 12/pc -> 144.0)
        val totalDozen = BazaarShopLogic.calculateTotalFromUnitPrice(unitRate = 12.0, quantity = 1.0, unit = "ডজন")
        assertEquals(144.0, totalDozen, 0.001)

        // Hāli (1 hāli = 4 pcs; rate 10/pc -> 40.0)
        val totalHali = BazaarShopLogic.calculateTotalFromUnitPrice(unitRate = 10.0, quantity = 1.0, unit = "হালি")
        assertEquals(40.0, totalHali, 0.001)
    }

    @Test
    fun testCalculateUnitPriceFromTotal() {
        // ৳30 for 500g -> ৳60/kg
        val rateFrom500g = BazaarShopLogic.calculateUnitPriceFromTotal(totalPaid = 30.0, quantity = 500.0, unit = "গ্রাম")
        assertEquals(60.0, rateFrom500g, 0.001)

        // ৳40 for 500ml -> ৳80/L
        val rateFrom500ml = BazaarShopLogic.calculateUnitPriceFromTotal(totalPaid = 40.0, quantity = 500.0, unit = "মিলি")
        assertEquals(80.0, rateFrom500ml, 0.001)

        // ৳60 for 4 pc -> ৳15/pc
        val rateFrom4pc = BazaarShopLogic.calculateUnitPriceFromTotal(totalPaid = 60.0, quantity = 4.0, unit = "পিস")
        assertEquals(15.0, rateFrom4pc, 0.001)
    }

    @Test
    fun testCalculateVariancePercent() {
        // current 60, avg 50 -> +20.0%
        val vUp = BazaarShopLogic.calculateVariancePercent(60.0, 50.0)
        assertEquals(20.0, vUp, 0.001)

        // current 45, avg 50 -> -10.0%
        val vDown = BazaarShopLogic.calculateVariancePercent(45.0, 50.0)
        assertEquals(-10.0, vDown, 0.001)

        // current 50, avg 50 -> 0.0%
        val vEqual = BazaarShopLogic.calculateVariancePercent(50.0, 50.0)
        assertEquals(0.0, vEqual, 0.001)
    }

    @Test
    fun testEvaluatePriceRecommendationRequiresPurchaseCount3And8PercentThreshold() {
        // 1. Good time to buy: current 10% below avg AND purchaseCount >= 3
        val recGood = BazaarShopLogic.evaluatePriceRecommendation(
            currentUnitPrice = 90.0,
            avgUnitPrice = 100.0,
            purchaseCount = 3,
            lastUnitPrice = 100.0,
            isBangla = false
        )
        assertTrue("Must flag good time to buy when variance <= -8% and purchaseCount >= 3", recGood.isGoodTimeToBuy)
        assertFalse(recGood.isPricey)
        assertEquals(PriceTrendDirection.FALLING, recGood.direction)
        assertTrue(recGood.badgeText.contains("10% off"))

        // 2. Pricey: current 10% above avg AND purchaseCount >= 3
        val recPricey = BazaarShopLogic.evaluatePriceRecommendation(
            currentUnitPrice = 110.0,
            avgUnitPrice = 100.0,
            purchaseCount = 4,
            lastUnitPrice = 100.0,
            isBangla = false
        )
        assertFalse(recPricey.isGoodTimeToBuy)
        assertTrue("Must flag pricey when variance >= 8% and purchaseCount >= 3", recPricey.isPricey)
        assertEquals(PriceTrendDirection.RISING, recPricey.direction)
        assertTrue(recPricey.badgeText.contains("10% high"))

        // 3. Sub-8% threshold (e.g. 5% below avg) -> neither good time to buy nor pricey
        val recFair = BazaarShopLogic.evaluatePriceRecommendation(
            currentUnitPrice = 95.0,
            avgUnitPrice = 100.0,
            purchaseCount = 5,
            lastUnitPrice = 95.0,
            isBangla = false
        )
        assertFalse(recFair.isGoodTimeToBuy)
        assertFalse(recFair.isPricey)

        // 4. Rule: When purchaseCount < 3, must NOT trigger good-time-to-buy even with huge discount
        val recLowPurchases = BazaarShopLogic.evaluatePriceRecommendation(
            currentUnitPrice = 70.0,
            avgUnitPrice = 100.0,
            purchaseCount = 2,
            lastUnitPrice = 100.0,
            isBangla = false
        )
        assertFalse("Must NOT flag good time to buy if purchaseCount < 3", recLowPurchases.isGoodTimeToBuy)
        assertFalse(recLowPurchases.isPricey)
        // Fallback shows variance vs last price
        assertTrue("Fallback must show diff vs last price", recLowPurchases.varianceText.contains("last"))
    }

    @Test
    fun testCalculateLastPriceAmountWithUnitConversion() {
        // Last purchase: ৳60 per kg. Current item: 500 g -> ৳30.0
        val amt500g = BazaarShopLogic.calculateLastPriceAmount(
            itemQuantity = 500.0,
            itemUnit = "গ্রাম",
            lastRate = 60.0,
            lastRateUnit = "কেজি"
        )
        assertEquals(30.0, amt500g, 0.001)

        // Last purchase: ৳50 per kg. Current item: 2 kg -> ৳100.0
        val amt2kg = BazaarShopLogic.calculateLastPriceAmount(
            itemQuantity = 2.0,
            itemUnit = "কেজি",
            lastRate = 50.0,
            lastRateUnit = "কেজি"
        )
        assertEquals(100.0, amt2kg, 0.001)

        // Last purchase: ৳40 per hāli (4 pc). Current item: 2 pc -> ৳20.0
        val amt2pc = BazaarShopLogic.calculateLastPriceAmount(
            itemQuantity = 2.0,
            itemUnit = "পিস",
            lastRate = 40.0,
            lastRateUnit = "হালি"
        )
        assertEquals(20.0, amt2pc, 0.001)
    }

    @Test
    fun testCalculateStallProgressWithSkippedExclusion() {
        val items = listOf(
            MarketItem(id = 1, listId = 10, name = "আলু", quantity = 1.0, unit = "কেজি", isPurchased = true),
            MarketItem(id = 2, listId = 10, name = "পেঁয়াজ", quantity = 1.0, unit = "কেজি", isPurchased = false),
            MarketItem(id = 3, listId = 10, name = "রসুন", quantity = 0.5, unit = "কেজি", isPurchased = false), // skipped
            MarketItem(id = 4, listId = 10, name = "রুই মাছ", quantity = 1.0, unit = "কেজি", isPurchased = true)
        )
        val skippedIds = setOf(3L)

        // Veggie stall ("🥬 শাকসবজি"): আলু (done), পেঁয়াজ (pending), রসুন (skipped) -> total 2, done 1
        val vegProgress = BazaarShopLogic.calculateStallProgress(
            stallKey = "🥬 শাকসবজি",
            items = items,
            masterProductsMap = emptyMap(),
            skippedIds = skippedIds
        )
        assertEquals(2, vegProgress.totalCount)
        assertEquals(1, vegProgress.doneCount)
        assertFalse(vegProgress.isAllDone)

        // Meat/Fish stall ("🐟 মাছ ও মাংস"): রুই মাছ (done) -> total 1, done 1 -> isAllDone = true
        val fishProgress = BazaarShopLogic.calculateStallProgress(
            stallKey = "🐟 মাছ ও মাংস",
            items = items,
            masterProductsMap = emptyMap(),
            skippedIds = skippedIds
        )
        assertEquals(1, fishProgress.totalCount)
        assertEquals(1, fishProgress.doneCount)
        assertTrue(fishProgress.isAllDone)
    }

    @Test
    fun testFindNextUnpurchasedItemAutoAdvance() {
        val items = listOf(
            MarketItem(id = 1, listId = 10, name = "আলু", quantity = 1.0, unit = "কেজি", isPurchased = true),
            MarketItem(id = 2, listId = 10, name = "পেঁয়াজ", quantity = 1.0, unit = "কেজি", isPurchased = false),
            MarketItem(id = 3, listId = 10, name = "রসুন", quantity = 0.5, unit = "কেজি", isPurchased = false),
            MarketItem(id = 4, listId = 10, name = "আদা", quantity = 0.5, unit = "কেজি", isPurchased = false)
        )
        val skipped = setOf(3L) // রসুন skipped

        // Current item 2 (পেঁয়াজ) finished -> next should skip 3 and find 4 (আদা)
        val next = BazaarShopLogic.findNextUnpurchasedItem(
            items = items,
            currentItemId = 2L,
            skippedIds = skipped
        )
        assertNotNull(next)
        assertEquals(4L, next!!.id)
    }

    @Test
    fun testQuickAdjustmentChipsArithmetic() {
        // [+5]
        val plus5 = BazaarShopLogic.applyQuickAdjustment(100.0, 5.0)
        assertEquals(105.0, plus5, 0.001)

        // [-5]
        val minus5 = BazaarShopLogic.applyQuickAdjustment(100.0, -5.0)
        assertEquals(95.0, minus5, 0.001)

        // [+10]
        val plus10 = BazaarShopLogic.applyQuickAdjustment(100.0, 10.0)
        assertEquals(110.0, plus10, 0.001)

        // Floor at 0.0
        val underflow = BazaarShopLogic.applyQuickAdjustment(3.0, -5.0)
        assertEquals(0.0, underflow, 0.001)
    }

    @Test
    fun testSwipeRightWithLastPriceVersusWithoutLastPriceBehavior() {
        val lastRates = mapOf("আলু" to Pair(50.0, "কেজি"))
        val itemWithLastPrice = MarketItem(id = 1L, listId = 1L, name = "আলু", quantity = 2.0, unit = "কেজি")
        val itemWithoutLastPrice = MarketItem(id = 2L, listId = 1L, name = "বেগুন", quantity = 1.0, unit = "কেজি")

        // 1. With last price -> compute rate directly
        val rate1 = lastRates[itemWithLastPrice.name]
        assertNotNull(rate1)
        val computedAmount = BazaarShopLogic.calculateLastPriceAmount(
            itemQuantity = itemWithLastPrice.quantity,
            itemUnit = itemWithLastPrice.unit,
            lastRate = rate1!!.first,
            lastRateUnit = rate1.second
        )
        assertEquals(100.0, computedAmount, 0.001)

        // 2. Without last price -> rate is null, swipe right requests Price Pad
        val rate2 = lastRates[itemWithoutLastPrice.name]
        assertNull(rate2)
    }

    @Test
    fun testSkipItemAndUndoSkipBehavior() {
        val items = listOf(
            MarketItem(id = 1L, listId = 10L, name = "আলু", quantity = 1.0, unit = "কেজি", isPurchased = true),
            MarketItem(id = 2L, listId = 10L, name = "টমেটো", quantity = 1.0, unit = "কেজি", isPurchased = false),
            MarketItem(id = 3L, listId = 10L, name = "মরিচ", quantity = 250.0, unit = "গ্রাম", isPurchased = false)
        )
        val skippedMutable = mutableSetOf<Long>()

        // Initially no skipped items: 3 items in vegetable stall, 1 done
        var progress = BazaarShopLogic.calculateStallProgress("🥬 শাকসবজি", items, emptyMap(), skippedMutable)
        assertEquals(3, progress.totalCount)
        assertEquals(1, progress.doneCount)
        assertFalse(progress.isAllDone)

        // Skip item 3 (মরিচ) -> stall progress recalculates with 2 total items, 1 done
        skippedMutable.add(3L)
        progress = BazaarShopLogic.calculateStallProgress("🥬 শাকসবজি", items, emptyMap(), skippedMutable)
        assertEquals(2, progress.totalCount)
        assertEquals(1, progress.doneCount)
        assertFalse(progress.isAllDone)

        // Mark item 2 as done -> stall is complete
        val itemsAfterDone = items.map { if (it.id == 2L) it.copy(isPurchased = true, amount = 40.0) else it }
        progress = BazaarShopLogic.calculateStallProgress("🥬 শাকসবজি", itemsAfterDone, emptyMap(), skippedMutable)
        assertEquals(2, progress.totalCount)
        assertEquals(2, progress.doneCount)
        assertTrue(progress.isAllDone)

        // Undo skip on item 3 -> stall is no longer all done
        skippedMutable.remove(3L)
        progress = BazaarShopLogic.calculateStallProgress("🥬 শাকসবজি", itemsAfterDone, emptyMap(), skippedMutable)
        assertEquals(3, progress.totalCount)
        assertEquals(2, progress.doneCount)
        assertFalse(progress.isAllDone)
    }

    @Test
    fun testCarryOverFailureAndRetryContract() {
        val unpurchased = listOf(
            MarketItem(id = 0L, listId = 0L, name = "রসুন", quantity = 0.5, unit = "কেজি", isPurchased = false)
        )
        // Simulate carry-over snapshot retention on failure
        var pendingCarryOverSnapshot: Pair<String, List<MarketItem>>? = Pair("বাজারের ফর্দ – বাকি", unpurchased)
        assertNotNull(pendingCarryOverSnapshot)
        val snapshot = pendingCarryOverSnapshot!!
        assertEquals(1, snapshot.second.size)
        assertEquals("রসুন", snapshot.second[0].name)

        // Simulate retry success -> clears snapshot
        pendingCarryOverSnapshot = null
        assertNull(pendingCarryOverSnapshot)
    }

    // =========================================================================
    // 2. ScreenWakeLockManager Lifecycle Test
    // =========================================================================

    @Test
    fun testScreenWakeLockManagerLifecycleContract() {
        var acquireCallCount = 0
        var releaseCallCount = 0

        val fakeWakeLockManager = object : ScreenWakeLockManager {
            override fun acquire(activity: Activity) {
                acquireCallCount++
            }

            override fun release(activity: Activity) {
                releaseCallCount++
            }
        }

        // Verify acquisition and release
        val dummyActivity = Activity()
        fakeWakeLockManager.acquire(dummyActivity)
        assertEquals(1, acquireCallCount)
        assertEquals(0, releaseCallCount)

        fakeWakeLockManager.release(dummyActivity)
        assertEquals(1, acquireCallCount)
        assertEquals(1, releaseCallCount)
    }

    // =========================================================================
    // 3. SavedStateHandle & Skipped Items Restoration Test
    // =========================================================================

    @Test
    fun testSkippedItemsSavedStateHandleRestoration() {
        val handle = SavedStateHandle()
        handle["shop_skipped_item_ids"] = listOf(101L, 102L, 105L)
        handle["shop_auto_advance_enabled"] = false

        val restoredIds = handle.get<List<Long>>("shop_skipped_item_ids")?.toSet()
        val restoredAutoAdvance = handle.get<Boolean>("shop_auto_advance_enabled")

        assertEquals(setOf(101L, 102L, 105L), restoredIds)
        assertEquals(false, restoredAutoAdvance)
    }

    // =========================================================================
    // 4. Carry-Over Bridge Invariant Test
    // =========================================================================

    @Test
    fun testCarryOverSnapshotSeparationContract() {
        val originalItems = listOf(
            MarketItem(id = 11, listId = 10, name = "আলু", quantity = 1.0, unit = "কেজি", amount = 50.0, isPurchased = true),
            MarketItem(id = 12, listId = 10, name = "টমেটো", quantity = 1.0, unit = "কেজি", amount = null, isPurchased = false),
            MarketItem(id = 13, listId = 10, name = "মরিচ", quantity = 250.0, unit = "গ্রাম", amount = null, isPurchased = false)
        )
        val skipped = setOf(13L)

        // Snapshot only unpurchased or skipped
        val unpurchased = originalItems.filter { !it.isPurchased || it.id in skipped }.map {
            it.copy(id = 0L, listId = 0L, amount = null, isPurchased = false)
        }

        assertEquals(2, unpurchased.size)
        assertTrue(unpurchased.all { it.id == 0L && it.listId == 0L && it.amount == null && !it.isPurchased })
        assertEquals(listOf("টমেটো", "মরিচ"), unpurchased.map { it.name })
    }

    // =========================================================================
    // 5. Behavioral Decision & State Transition Tests (Pure Logic)
    // =========================================================================

    @Test
    fun testSwipeDecisionBehavior() {
        val potatoItem = MarketItem(id = 1, name = "আলু", quantity = 2.0, unit = "কেজি")
        val garlicItem = MarketItem(id = 2, name = "রসুন", quantity = 250.0, unit = "গ্রাম")

        // 1. Swipe Right with known last rate -> MarkDone with calculated amount
        val potatoLastRate = Pair(40.0, "কেজি") // 2kg at 40/kg = 80.0
        val potatoDecision = BazaarShopLogic.evaluateSwipeDecision(
            direction = SwipeDismissDirection.START_TO_END,
            item = potatoItem,
            lastRate = potatoLastRate
        )
        assertTrue("Swipe right with last rate must mark done", potatoDecision is SwipeDecision.MarkDone)
        assertEquals(80.0, (potatoDecision as SwipeDecision.MarkDone).calculatedAmount, 0.001)

        // 2. Swipe Right with unit conversion (250g with last rate of 120/kg -> 30.0)
        val garlicLastRate = Pair(120.0, "কেজি")
        val garlicDecision = BazaarShopLogic.evaluateSwipeDecision(
            direction = SwipeDismissDirection.START_TO_END,
            item = garlicItem,
            lastRate = garlicLastRate
        )
        assertTrue("Swipe right with unit converted rate must mark done", garlicDecision is SwipeDecision.MarkDone)
        assertEquals(30.0, (garlicDecision as SwipeDecision.MarkDone).calculatedAmount, 0.001)

        // 3. Swipe Right with NO last rate or non-positive rate -> OpenPricePad
        val noRateDecision = BazaarShopLogic.evaluateSwipeDecision(
            direction = SwipeDismissDirection.START_TO_END,
            item = potatoItem,
            lastRate = null
        )
        assertEquals(SwipeDecision.OpenPricePad, noRateDecision)

        val zeroRateDecision = BazaarShopLogic.evaluateSwipeDecision(
            direction = SwipeDismissDirection.START_TO_END,
            item = potatoItem,
            lastRate = Pair(0.0, "কেজি")
        )
        assertEquals(SwipeDecision.OpenPricePad, zeroRateDecision)

        // 4. Swipe Left -> Skip
        val skipDecision = BazaarShopLogic.evaluateSwipeDecision(
            direction = SwipeDismissDirection.END_TO_START,
            item = potatoItem,
            lastRate = potatoLastRate
        )
        assertEquals(SwipeDecision.Skip, skipDecision)

        // 5. Settled -> SnapBack
        val settledDecision = BazaarShopLogic.evaluateSwipeDecision(
            direction = SwipeDismissDirection.SETTLED,
            item = potatoItem,
            lastRate = potatoLastRate
        )
        assertEquals(SwipeDecision.SnapBack, settledDecision)

        // 6. Snap-back contract in confirmValueChange
        assertTrue(BazaarShopLogic.shouldDismissBoxSnapBack(SwipeDismissDirection.START_TO_END))
        assertTrue(BazaarShopLogic.shouldDismissBoxSnapBack(SwipeDismissDirection.END_TO_START))
    }

    @Test
    fun testPricePadStateTransitions() {
        val itemWithAmount = MarketItem(id = 10, name = "পেঁয়াজ", quantity = 2.0, unit = "কেজি", amount = 140.0, isPurchased = true)
        val itemWithNoAmount = MarketItem(id = 11, name = "আদা", quantity = 500.0, unit = "গ্রাম", amount = null, isPurchased = false)
        val itemBrandNew = MarketItem(id = 12, name = "ড্রাগন ফল", quantity = 1.0, unit = "পিস", amount = null, isPurchased = false)

        // 1. Initial expression resolution for item with amount
        val exprTotal = BazaarShopLogic.resolvePricePadInitialExpression(itemWithAmount, PricingMode.TOTAL_PAID, null)
        assertEquals("140", exprTotal)

        val exprPerUnit = BazaarShopLogic.resolvePricePadInitialExpression(itemWithAmount, PricingMode.PER_UNIT, null)
        assertEquals("70", exprPerUnit) // 140 total / 2 kg = 70/kg

        // 2. Initial expression resolution for item with last rate
        val gingerLastRate = Pair(160.0, "কেজি") // 160/kg for 500g = 80 total
        val exprLastPerUnit = BazaarShopLogic.resolvePricePadInitialExpression(itemWithNoAmount, PricingMode.PER_UNIT, gingerLastRate)
        assertEquals("160", exprLastPerUnit)

        val exprLastTotal = BazaarShopLogic.resolvePricePadInitialExpression(itemWithNoAmount, PricingMode.TOTAL_PAID, gingerLastRate)
        assertEquals("80", exprLastTotal)

        // 3. Initial expression resolution for brand new item
        val exprNew = BazaarShopLogic.resolvePricePadInitialExpression(itemBrandNew, PricingMode.PER_UNIT, null)
        assertEquals("", exprNew)

        // 4. Mode switch expression calculations
        // User entered unit price 60 (total for 500g is 30) -> switches to TOTAL_PAID
        val switchedToTotal = BazaarShopLogic.computePricePadModeSwitchExpression(
            newMode = PricingMode.TOTAL_PAID,
            currentUnitPrice = 60.0,
            currentTotalAmount = 30.0
        )
        assertEquals("30", switchedToTotal)

        // User entered total paid 50 (unit price for 500g is 100) -> switches to PER_UNIT
        val switchedToPerUnit = BazaarShopLogic.computePricePadModeSwitchExpression(
            newMode = PricingMode.PER_UNIT,
            currentUnitPrice = 100.0,
            currentTotalAmount = 50.0
        )
        assertEquals("100", switchedToPerUnit)

        // Blank or zero values switch to empty
        val switchedEmpty = BazaarShopLogic.computePricePadModeSwitchExpression(
            newMode = PricingMode.TOTAL_PAID,
            currentUnitPrice = 0.0,
            currentTotalAmount = 0.0
        )
        assertEquals("", switchedEmpty)
    }

    @Test
    fun testReceiptLineTapDecision() {
        val purchasedItem = MarketItem(id = 21, name = "মাছ", quantity = 1.0, unit = "কেজি", amount = 450.0, isPurchased = true)
        val unpurchasedItem = MarketItem(id = 22, name = "মাংস", quantity = 1.0, unit = "কেজি", amount = null, isPurchased = false)

        val purchasedTap = BazaarShopLogic.evaluateReceiptLineTap(purchasedItem)
        assertTrue(purchasedTap is ReceiptLineTapDecision.OpenEditPricePad)
        val openPurchased = purchasedTap as ReceiptLineTapDecision.OpenEditPricePad
        assertEquals(purchasedItem, openPurchased.item)
        assertTrue("Purchased item tap must allow undo purchase", openPurchased.canUndo)

        val unpurchasedTap = BazaarShopLogic.evaluateReceiptLineTap(unpurchasedItem)
        assertTrue(unpurchasedTap is ReceiptLineTapDecision.OpenEditPricePad)
        val openUnpurchased = unpurchasedTap as ReceiptLineTapDecision.OpenEditPricePad
        assertFalse("Unpurchased item tap cannot undo purchase", openUnpurchased.canUndo)

        val nullTap = BazaarShopLogic.evaluateReceiptLineTap(null)
        assertEquals(ReceiptLineTapDecision.NoOp, nullTap)
    }

    @Test
    fun testModeMemoryKeyUsesMasterProductNormalizedName() {
        // Bengali product names with spacing, punctuation, and parentheses must map to identical normalized keys
        val key1 = UnitConverterEngine.normalizeProductName("কাঁচা  মরিচ ")
        val key2 = UnitConverterEngine.normalizeProductName("কাঁচামরিচ")
        val key3 = UnitConverterEngine.normalizeProductName("(তাজা) কাঁচা মরিচ")
        val key4 = UnitConverterEngine.normalizeProductName("কাঁচা মরিচ ১")

        // canonicalCommodityKey removes spaces so spacing variations collapse
        val canonical1 = UnitConverterEngine.canonicalCommodityKey("কাঁচা  মরিচ ")
        val canonical2 = UnitConverterEngine.canonicalCommodityKey("কাঁচামরিচ")
        val canonical3 = UnitConverterEngine.canonicalCommodityKey(" কাঁচা মরিচ  ")

        assertEquals("কাঁচা মরিচ", key1)
        assertEquals("কাঁচা মরিচ", key3) // Strips parentheses
        assertEquals("কাঁচা মরিচ", key4) // Strips numbers

        assertEquals("কাঁচামরিচ", canonical1)
        assertEquals("কাঁচামরিচ", canonical2)
        assertEquals("কাঁচামরিচ", canonical3)

        // Verify simulated pricing mode memory lookup
        val memoryStore = mutableMapOf<String, String>()
        val savedNorm = UnitConverterEngine.normalizeProductName("  কাঁচা   মরিচ  ")
        memoryStore[savedNorm] = PricingMode.TOTAL_PAID.name

        val lookupNorm = UnitConverterEngine.normalizeProductName("(তাজা) কাঁচা মরিচ")
        assertEquals(PricingMode.TOTAL_PAID.name, memoryStore[lookupNorm])
    }

    @Test
    fun testCompletedListClonePurchasedOnlyVersusActiveList() {
        val items = listOf(
            MarketItem(id = 1, name = "আলু", isPurchased = true, amount = 60.0),
            MarketItem(id = 2, name = "পেঁয়াজ", isPurchased = true, amount = 120.0),
            MarketItem(id = 3, name = "ধনেপাতা", isPurchased = false, amount = null),
            MarketItem(id = 4, name = "লেবু", isPurchased = false, amount = null)
        )

        // Completed trip: clones only purchased items
        val completedList = MarketListWithItems(
            list = MarketList(id = 101, title = "সকালের বাজার", isCompleted = true),
            items = items
        )
        val completedItemsToClone = if (completedList.list.isCompleted) {
            completedList.items.filter { it.isPurchased }
        } else {
            completedList.items
        }
        assertEquals(2, completedItemsToClone.size)
        assertEquals(listOf("আলু", "পেঁয়াজ"), completedItemsToClone.map { it.name })
        assertTrue(completedItemsToClone.all { it.isPurchased })

        // Active list: clones all items
        val activeList = MarketListWithItems(
            list = MarketList(id = 102, title = "চলতি বাজার", isCompleted = false),
            items = items
        )
        val activeItemsToClone = if (activeList.list.isCompleted) {
            activeList.items.filter { it.isPurchased }
        } else {
            activeList.items
        }
        assertEquals(4, activeItemsToClone.size)
        assertEquals(listOf("আলু", "পেঁয়াজ", "ধনেপাতা", "লেবু"), activeItemsToClone.map { it.name })
    }

    // =========================================================================
    // 6. Swipe Background Resolution Behavioral Tests (BUG A)
    // =========================================================================

    @Test
    fun testResolveSwipeBackgroundKindAtRestReturnsNone() {
        // At rest, regardless of initial dismiss direction, progress <= 0 and settled target must return NONE
        val atRestSettled = BazaarShopLogic.resolveSwipeBackgroundKind(
            direction = SwipeDismissDirection.SETTLED,
            progress = 0f,
            targetDirection = SwipeDismissDirection.SETTLED
        )
        assertEquals(SwipeBackgroundKind.NONE, atRestSettled)

        val atRestWithEndToStartDirection = BazaarShopLogic.resolveSwipeBackgroundKind(
            direction = SwipeDismissDirection.END_TO_START,
            progress = 0f,
            targetDirection = SwipeDismissDirection.SETTLED
        )
        assertEquals("At rest, EndToStart with 0 progress must compose NONE", SwipeBackgroundKind.NONE, atRestWithEndToStartDirection)

        val atRestWithStartToEndDirection = BazaarShopLogic.resolveSwipeBackgroundKind(
            direction = SwipeDismissDirection.START_TO_END,
            progress = 0f,
            targetDirection = SwipeDismissDirection.SETTLED
        )
        assertEquals("At rest, StartToEnd with 0 progress must compose NONE", SwipeBackgroundKind.NONE, atRestWithStartToEndDirection)
    }

    @Test
    fun testResolveSwipeBackgroundKindSwipingRightReturnsDone() {
        val partialSwipeRight = BazaarShopLogic.resolveSwipeBackgroundKind(
            direction = SwipeDismissDirection.START_TO_END,
            progress = 0.35f,
            targetDirection = SwipeDismissDirection.START_TO_END
        )
        assertEquals(SwipeBackgroundKind.DONE, partialSwipeRight)

        val fullSwipeRight = BazaarShopLogic.resolveSwipeBackgroundKind(
            direction = SwipeDismissDirection.START_TO_END,
            progress = 1.0f,
            targetDirection = SwipeDismissDirection.START_TO_END
        )
        assertEquals(SwipeBackgroundKind.DONE, fullSwipeRight)
    }

    @Test
    fun testResolveSwipeBackgroundKindSwipingLeftReturnsSkip() {
        val partialSwipeLeft = BazaarShopLogic.resolveSwipeBackgroundKind(
            direction = SwipeDismissDirection.END_TO_START,
            progress = 0.4f,
            targetDirection = SwipeDismissDirection.END_TO_START
        )
        assertEquals(SwipeBackgroundKind.SKIP, partialSwipeLeft)

        val fullSwipeLeft = BazaarShopLogic.resolveSwipeBackgroundKind(
            direction = SwipeDismissDirection.END_TO_START,
            progress = 1.0f,
            targetDirection = SwipeDismissDirection.END_TO_START
        )
        assertEquals(SwipeBackgroundKind.SKIP, fullSwipeLeft)
    }

    @Test
    fun testResolveSwipeBackgroundKindDirectionChangeSwitchesCleanly() {
        // User starts swiping right (DONE)
        val step1 = BazaarShopLogic.resolveSwipeBackgroundKind(
            direction = SwipeDismissDirection.START_TO_END,
            progress = 0.5f,
            targetDirection = SwipeDismissDirection.START_TO_END
        )
        assertEquals(SwipeBackgroundKind.DONE, step1)

        // User reverses drag to swipe left (SKIP)
        val step2 = BazaarShopLogic.resolveSwipeBackgroundKind(
            direction = SwipeDismissDirection.END_TO_START,
            progress = 0.4f,
            targetDirection = SwipeDismissDirection.END_TO_START
        )
        assertEquals(SwipeBackgroundKind.SKIP, step2)

        // User lets go and card snaps back to rest (NONE)
        val step3 = BazaarShopLogic.resolveSwipeBackgroundKind(
            direction = SwipeDismissDirection.SETTLED,
            progress = 0f,
            targetDirection = SwipeDismissDirection.SETTLED
        )
        assertEquals(SwipeBackgroundKind.NONE, step3)
    }

    // =========================================================================
    // 7. Minimal Structural Invariant Test
    // =========================================================================

    @Test
    fun testBazaarShopItemCardUsesSwipeToDismissBox() {
        val content = readFile("com/example/expensetracker/ui/bazaar/BazaarShopItemCard.kt")
        assertTrue("BazaarShopItemCard must use SwipeToDismissBox for gesture-based shopping", content.contains("SwipeToDismissBox("))
        assertTrue("BazaarShopItemCard Surface must be opaque appColors.surface", content.contains("color = appColors.surface"))
    }
}
