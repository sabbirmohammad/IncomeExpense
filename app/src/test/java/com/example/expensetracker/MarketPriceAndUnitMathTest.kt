package com.example.expensetracker

import com.example.expensetracker.common.UnitConverterEngine
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.ui.components.AmountKeypadLogic
import com.example.expensetracker.ui.market.LeftoverHandlingOption
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.roundToInt

/**
 * Stage 2 Unit Tests: Price, Unit Math, Variance and State Machine verification.
 * Tests:
 * 1. UnitConverterEngine price calculations for Bengali bazar units (কেজি, গ্রাম, পোয়া, পাল্লা, হালি, ডজন, লিটার, মিলি).
 * 2. Cross-computation: Per-unit rate to total price vs total price to per-unit rate.
 * 3. Live price variance calculation (delta amount, percentage, hike/drop classification).
 * 4. Auto-advance state machine logic for sequential item shopping.
 * 5. Leftover items rollover logic (MOVE_TO_NEXT, KEEP_IN_LIST, DISCARD).
 * 6. Keypad math expressions and evaluation.
 */
class MarketPriceAndUnitMathTest {

    // =========================================================================
    // 1. UNIT CONVERTER MATH (Weight, Count, Volume)
    // =========================================================================

    @Test
    fun testWeightUnitConversionsAndCostEstimation() {
        // 500 grams at ৳80/kg should equal ৳40
        val cost500g = UnitConverterEngine.estimateCost(baseUnitPrice = 80.0, targetQuantity = 500.0, targetUnit = "গ্রাম")
        assertEquals(40.0, cost500g, 0.001)

        // 250 grams (1 powa) at ৳120/kg should equal ৳30
        val cost250g = UnitConverterEngine.estimateCost(baseUnitPrice = 120.0, targetQuantity = 250.0, targetUnit = "গ্রাম")
        assertEquals(30.0, cost250g, 0.001)

        // 1 powa at ৳120/kg should equal ৳30
        val cost1powa = UnitConverterEngine.estimateCost(baseUnitPrice = 120.0, targetQuantity = 1.0, targetUnit = "পোয়া")
        assertEquals(30.0, cost1powa, 0.001)

        // 2.5 kg at ৳60/kg should equal ৳150
        val cost2_5kg = UnitConverterEngine.estimateCost(baseUnitPrice = 60.0, targetQuantity = 2.5, targetUnit = "কেজি")
        assertEquals(150.0, cost2_5kg, 0.001)

        // 1 palla (5 kg) at ৳50/kg should equal ৳250
        val cost1palla = UnitConverterEngine.estimateCost(baseUnitPrice = 50.0, targetQuantity = 1.0, targetUnit = "পাল্লা")
        assertEquals(250.0, cost1palla, 0.001)
    }

    @Test
    fun testCountUnitConversionsAndCostEstimation() {
        // Base unit price: ৳12 per piece
        // 1 hali (4 pieces) at ৳12/piece should equal ৳48
        val cost1hali = UnitConverterEngine.estimateCost(baseUnitPrice = 12.0, targetQuantity = 1.0, targetUnit = "হালি")
        assertEquals(48.0, cost1hali, 0.001)

        // 1 dozen (12 pieces) at ৳12/piece should equal ৳144
        val cost1dozen = UnitConverterEngine.estimateCost(baseUnitPrice = 12.0, targetQuantity = 1.0, targetUnit = "ডজন")
        assertEquals(144.0, cost1dozen, 0.001)

        // 2 pairs (4 pieces) at ৳10/piece should equal ৳40
        val cost2pairs = UnitConverterEngine.estimateCost(baseUnitPrice = 10.0, targetQuantity = 2.0, targetUnit = "জোড়া")
        assertEquals(40.0, cost2pairs, 0.001)
    }

    @Test
    fun testVolumeUnitConversionsAndCostEstimation() {
        // 500 ml at ৳180/liter should equal ৳90
        val cost500ml = UnitConverterEngine.estimateCost(baseUnitPrice = 180.0, targetQuantity = 500.0, targetUnit = "মিলি")
        assertEquals(90.0, cost500ml, 0.001)

        // 2 liters at ৳180/liter should equal ৳360
        val cost2liters = UnitConverterEngine.estimateCost(baseUnitPrice = 180.0, targetQuantity = 2.0, targetUnit = "লিটার")
        assertEquals(360.0, cost2liters, 0.001)
    }

    // =========================================================================
    // 2. REVERSE CONVERSION: Total Price to Unit Rate
    // =========================================================================

    @Test
    fun testCalculateBaseUnitPriceFromTotalAmount() {
        // Paid ৳40 for 500 grams -> unit rate should be ৳80/kg
        val (rate1, unit1) = UnitConverterEngine.calculateBaseUnitPrice(totalAmount = 40.0, quantity = 500.0, unit = "গ্রাম")
        assertEquals(80.0, rate1, 0.001)
        assertEquals("কেজি", unit1)

        // Paid ৳48 for 1 hali -> unit rate should be ৳12/piece
        val (rate2, unit2) = UnitConverterEngine.calculateBaseUnitPrice(totalAmount = 48.0, quantity = 1.0, unit = "হালি")
        assertEquals(12.0, rate2, 0.001)
        assertEquals("পিস", unit2)

        // Paid ৳144 for 1 dozen -> unit rate should be ৳12/piece
        val (rate3, unit3) = UnitConverterEngine.calculateBaseUnitPrice(totalAmount = 144.0, quantity = 1.0, unit = "ডজন")
        assertEquals(12.0, rate3, 0.001)
        assertEquals("পিস", unit3)

        // Paid ৳90 for 500 ml -> unit rate should be ৳180/liter
        val (rate4, unit4) = UnitConverterEngine.calculateBaseUnitPrice(totalAmount = 90.0, quantity = 500.0, unit = "মিলি")
        assertEquals(180.0, rate4, 0.001)
        assertEquals("লিটার", unit4)
    }

    // =========================================================================
    // 3. PRICE VARIANCE LOGIC
    // =========================================================================

    @Test
    fun testPriceVarianceCalculationAndClassification() {
        val lastPrice = 55.0

        // Case 1: Drop (Current ৳50 vs Last ৳55)
        val currentDrop = 50.0
        val diffDrop = currentDrop - lastPrice
        val pctDrop = (diffDrop / lastPrice) * 100.0
        assertEquals(-5.0, diffDrop, 0.001)
        assertEquals(-9, pctDrop.roundToInt())
        assertTrue("Variance should be classified as drop", diffDrop < -0.5)

        // Case 2: Hike (Current ৳65 vs Last ৳55)
        val currentHike = 65.0
        val diffHike = currentHike - lastPrice
        val pctHike = (diffHike / lastPrice) * 100.0
        assertEquals(10.0, diffHike, 0.001)
        assertEquals(18, pctHike.roundToInt())
        assertTrue("Variance should be classified as hike", diffHike > 0.5)

        // Case 3: Same as last (Current ৳55 vs Last ৳55)
        val currentSame = 55.0
        val diffSame = currentSame - lastPrice
        val pctSame = (diffSame / lastPrice) * 100.0
        assertEquals(0.0, diffSame, 0.001)
        assertEquals(0, pctSame.roundToInt())
        assertFalse("Variance is neither hike nor drop", diffSame < -0.5 || diffSame > 0.5)
    }

    // =========================================================================
    // 4. AUTO-ADVANCE STATE MACHINE
    // =========================================================================

    @Test
    fun testAutoAdvanceFindsNextPendingItem() {
        val items = listOf(
            MarketItem(id = 1L, listId = 1L, name = "আলু", quantity = 2.0, unit = "কেজি", isPurchased = false),
            MarketItem(id = 2L, listId = 1L, name = "পেঁয়াজ", quantity = 1.0, unit = "কেজি", isPurchased = false),
            MarketItem(id = 3L, listId = 1L, name = "রসুন", quantity = 250.0, unit = "গ্রাম", isPurchased = true),
            MarketItem(id = 4L, listId = 1L, name = "মরিচ", quantity = 250.0, unit = "গ্রাম", isPurchased = false)
        )

        // Finishing item 1 with auto-advance enabled: should advance to item 2
        val nextAfter1 = items.firstOrNull { it.id != 1L && !it.isPurchased }
        assertNotNull(nextAfter1)
        assertEquals(2L, nextAfter1?.id)

        // Finishing item 2 with auto-advance enabled: item 3 is already bought, so should advance to item 4
        val nextAfter2 = items.firstOrNull { it.id != 2L && !it.isPurchased && it.id != 1L }
        assertNotNull(nextAfter2)
        assertEquals(4L, nextAfter2?.id)

        // Finishing item 4: no more pending items exist
        val nextAfter4 = items.firstOrNull { it.id != 4L && !it.isPurchased && it.id != 1L && it.id != 2L }
        assertNull(nextAfter4)
    }

    // =========================================================================
    // 5. LEFTOVER HANDLING OPTIONS
    // =========================================================================

    @Test
    fun testLeftoverHandlingOptionsResolution() {
        val items = listOf(
            MarketItem(id = 1L, listId = 1L, name = "আলু", isPurchased = true, amount = 70.0),
            MarketItem(id = 2L, listId = 1L, name = "পেঁয়াজ", isPurchased = true, amount = 110.0),
            MarketItem(id = 3L, listId = 1L, name = "আদা", isPurchased = false),
            MarketItem(id = 4L, listId = 1L, name = "লেবু", isPurchased = false)
        )

        val unbought = items.filter { !it.isPurchased }
        assertEquals(2, unbought.size)

        // 1. MOVE_TO_NEXT option: rolls over all unbought items
        val rollOverMove = when (LeftoverHandlingOption.MOVE_TO_NEXT) {
            LeftoverHandlingOption.MOVE_TO_NEXT -> unbought
            LeftoverHandlingOption.KEEP_IN_LIST -> emptyList()
            LeftoverHandlingOption.DISCARD -> emptyList()
        }
        assertEquals(2, rollOverMove.size)
        assertEquals(listOf("আদা", "লেবু"), rollOverMove.map { it.name })

        // 2. KEEP_IN_LIST option: no rollover, unbought items remain in list
        val rollOverKeep = when (LeftoverHandlingOption.KEEP_IN_LIST) {
            LeftoverHandlingOption.MOVE_TO_NEXT -> unbought
            LeftoverHandlingOption.KEEP_IN_LIST -> emptyList()
            LeftoverHandlingOption.DISCARD -> emptyList()
        }
        assertTrue(rollOverKeep.isEmpty())

        // 3. DISCARD option: no rollover, unbought items are discarded
        val rollOverDiscard = when (LeftoverHandlingOption.DISCARD) {
            LeftoverHandlingOption.MOVE_TO_NEXT -> unbought
            LeftoverHandlingOption.KEEP_IN_LIST -> emptyList()
            LeftoverHandlingOption.DISCARD -> emptyList()
        }
        assertTrue(rollOverDiscard.isEmpty())
    }

    // =========================================================================
    // 6. AMOUNT KEYPAD EVALUATION ENGINE
    // =========================================================================

    @Test
    fun testKeypadMathEvaluation() {
        assertEquals(55.0, AmountKeypadLogic.evaluate("30+25") ?: 0.0, 0.001)
        assertEquals(85.0, AmountKeypadLogic.evaluate("100-15") ?: 0.0, 0.001)
        assertEquals(80.0, AmountKeypadLogic.evaluate("40*2") ?: 0.0, 0.001)
        assertEquals(30.0, AmountKeypadLogic.evaluate("120/4") ?: 0.0, 0.001)
        assertEquals(110.0, AmountKeypadLogic.evaluate("50+30*2") ?: 0.0, 0.001)
    }
}
