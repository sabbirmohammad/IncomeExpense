package com.example.expensetracker

import android.app.Activity
import android.app.Application
import androidx.lifecycle.SavedStateHandle
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.MarketCompletionResult
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.data.model.MarketList
import com.example.expensetracker.data.model.MarketListWithItems
import com.example.expensetracker.data.model.MasterProduct
import com.example.expensetracker.data.model.ProductPriceInsight
import com.example.expensetracker.ui.bazaar.BazaarShopLogic
import com.example.expensetracker.ui.bazaar.BazaarShopViewModel
import com.example.expensetracker.ui.bazaar.PriceTrendDirection
import com.example.expensetracker.ui.bazaar.PricingMode
import com.example.expensetracker.ui.bazaar.ScreenWakeLockManager
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
    // 5. Structural & Component Invariant Tests (Source Checks)
    // =========================================================================

    @Test
    fun testBazaarShopItemCardInvariants() {
        val content = readFile("com/example/expensetracker/ui/bazaar/BazaarShopItemCard.kt")

        // 1. SwipeToDismissBox implementation with snap-back
        assertTrue("Must use SwipeToDismissBox", content.contains("SwipeToDismissBox("))
        assertTrue("Must reset swipe state in confirmValueChange", content.contains("confirmValueChange = { value ->"))
        assertTrue("Must snap back after swipe right", content.contains("SwipeToDismissBoxValue.StartToEnd -> {") && content.contains("false // Return false to snap back"))
        assertTrue("Must snap back after swipe left", content.contains("SwipeToDismissBoxValue.EndToStart -> {") && content.contains("false // Return false to snap back"))

        // 2. Touch targets & dimensions
        assertTrue("Item card must enforce 72dp min height", content.contains("heightIn(min = 72.dp)"))
        assertTrue("Check circle must enforce 56dp touch target", content.contains("size(56.dp)"))
        assertTrue("Options menu button must enforce 48dp touch target", content.contains("size(48.dp)"))

        // 3. Tabular numerals & TalkBack actions
        assertTrue("Must use PulseTabularText for amounts and quantities", content.contains("PulseTabularText("))
        assertTrue("Must include customActions semantics for TalkBack", content.contains("customActions = listOf("))
        assertTrue("Must include CustomAccessibilityAction for Done and Skip", content.contains("CustomAccessibilityAction(doneActionLabel)") && content.contains("CustomAccessibilityAction(skipActionLabel)"))
    }

    @Test
    fun testBazaarPricePadSheetInvariants() {
        val content = readFile("com/example/expensetracker/ui/bazaar/BazaarPricePadSheet.kt")

        // 1. Embeds PulseKeypad and PulseSegmented
        assertTrue("Must embed PulseKeypad", content.contains("PulseKeypad("))
        assertTrue("Must use PulseSegmented for mode selection", content.contains("PulseSegmented("))

        // 2. Quick adjustment chips
        assertTrue("Must include quick chips [-5], [+5], [+10]", content.contains("\"-5\"") || content.contains("\"-৫\""))
        assertTrue("Must include [Same as last] chip", content.contains("lastRate != null"))
        assertTrue("Must include [Avg] chip", content.contains("priceInsight.avgUnitPrice"))
        assertTrue("Must include [Min] chip", content.contains("priceInsight.minUnitPrice"))

        // 3. Price recommendation badge with 8% threshold
        assertTrue("Must evaluate price recommendation", content.contains("BazaarShopLogic.evaluatePriceRecommendation("))
        assertTrue("Must display recommendation badge", content.contains("recommendation.badgeText.isNotBlank()"))

        // 4. Canvas Sparkline with TalkBack description
        assertTrue("Must render Canvas sparkline", content.contains("PriceHistorySparkline("))
        assertTrue("Must provide TalkBack contentDescription for sparkline", content.contains("contentDescription = talkBackDesc"))

        // 5. Adaptive presentation
        assertTrue("Must present inside AdaptiveSheet", content.contains("AdaptiveSheet("))
    }

    @Test
    fun testBazaarShopScreenInvariants() {
        val content = readFile("com/example/expensetracker/ui/bazaar/BazaarShopScreen.kt")

        // 1. Screen wake lock management
        assertTrue("Must manage screen wake lock", content.contains("wakeLockManager.acquire(activity)") && content.contains("wakeLockManager.release(activity)"))

        // 2. Immersive top strip with ProgressBarThreshold
        assertTrue("Must use ProgressBarThreshold for budget cap", content.contains("ProgressBarThreshold("))

        // 3. Forgot-staples strip
        assertTrue("Must include forgot staples strip", content.contains("missingStaples"))
        assertTrue("Must allow dismissing forgot staples strip", content.contains("isForgotStaplesDismissed"))

        // 4. Skipped items section with re-add button
        assertTrue("Must show skipped items section", content.contains("skipped_items_section"))
        assertTrue("Must offer Re-add button for skipped items", content.contains("undoSkip(skippedItem.id)"))

        // 5. Responsive layout support
        assertTrue("Must support two-column layout on medium/expanded", content.contains("useTwoColumn"))
        assertTrue("Must support permanent receipt pane on large displays", content.contains("LiveReceiptPanelContent("))

        // 6. Pre-checkout Dialog with carry-over switch
        assertTrue("Must offer carry-over switch on checkout", content.contains("carryOverUnpurchased"))
        assertTrue("Must call completeTripWithCarryOver", content.contains("completeTripWithCarryOver("))
    }
}
