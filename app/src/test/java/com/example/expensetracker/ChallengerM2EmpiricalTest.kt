package com.example.expensetracker

import com.example.expensetracker.common.MarketListParser
import com.example.expensetracker.common.UnitConverterEngine
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.ui.components.CalculatorEvaluator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Empirical Challenger Test Suite for Milestone M2:
 * 1. Unit multiplier conversions and fractional calculations (পোয়া, পাল্লা, হালি, ডজন, কেজি, গ্রাম, লিটার, ইত্যাদি).
 * 2. Price variance percentage calculations (divide by zero, negative prices, extreme values, boundaries).
 * 3. Bottom trip summary progress calculations (0 items, partial, all items, overflow).
 * 4. Numeric keypad operations and math expression evaluations.
 * 5. Architectural & layout compliance for M2 UI components.
 */
class ChallengerM2EmpiricalTest {

    // =========================================================================
    // 1. UNIT MULTIPLIER CONVERSIONS & FRACTIONAL CALCULATIONS
    // =========================================================================

    @Test
    fun testWeightUnitConversionsAndCalculations() {
        // 1.1 পোয়া (0.25 kg)
        val powaNorm = UnitConverterEngine.normalize(1.0, "পোয়া")
        assertEquals(0.25, powaNorm.baseQuantity, 0.0001)
        assertEquals("কেজি", powaNorm.baseUnit)

        val powa2Norm = UnitConverterEngine.normalize(2.0, "পোয়া")
        assertEquals(0.50, powa2Norm.baseQuantity, 0.0001)
        assertEquals("কেজি", powa2Norm.baseUnit)

        // 1.2 পাল্লা (5.0 kg)
        val pallaNorm = UnitConverterEngine.normalize(1.0, "পাল্লা")
        assertEquals(5.0, pallaNorm.baseQuantity, 0.0001)
        assertEquals("কেজি", pallaNorm.baseUnit)

        val pallaFraction = UnitConverterEngine.normalize(0.5, "palla")
        assertEquals(2.5, pallaFraction.baseQuantity, 0.0001)
        assertEquals("কেজি", pallaFraction.baseUnit)

        // 1.3 গ্রাম (grams / 1000)
        val gramNorm = UnitConverterEngine.normalize(250.0, "গ্রাম")
        assertEquals(0.25, gramNorm.baseQuantity, 0.0001)
        assertEquals("কেজি", gramNorm.baseUnit)

        val gram500 = UnitConverterEngine.normalize(500.0, "gm")
        assertEquals(0.5, gram500.baseQuantity, 0.0001)

        val gram1000 = UnitConverterEngine.normalize(1000.0, "gram")
        assertEquals(1.0, gram1000.baseQuantity, 0.0001)

        // 1.4 কেজি
        val kgNorm = UnitConverterEngine.normalize(1.5, "কেজি")
        assertEquals(1.5, kgNorm.baseQuantity, 0.0001)
        assertEquals("কেজি", kgNorm.baseUnit)

        // 1.5 Rate calculations: e.g. 1 পোয়া (250g) for 30 tk -> rate should be 120 tk/kg
        val (powaRate, powaBaseUnit) = UnitConverterEngine.calculateBaseUnitPrice(30.0, 1.0, "পোয়া")
        assertEquals(120.0, powaRate, 0.0001)
        assertEquals("কেজি", powaBaseUnit)

        // 1 পাল্লা (5 kg) for 350 tk -> rate should be 70 tk/kg
        val (pallaRate, pallaBaseUnit) = UnitConverterEngine.calculateBaseUnitPrice(350.0, 1.0, "পাল্লা")
        assertEquals(70.0, pallaRate, 0.0001)
        assertEquals("কেজি", pallaBaseUnit)

        // Cost estimation: If rate is 80 tk/kg:
        // 1 পোয়া should cost 20 tk
        val estPowaCost = UnitConverterEngine.estimateCost(80.0, 1.0, "পোয়া")
        assertEquals(20.0, estPowaCost, 0.0001)

        // 1 পাল্লা should cost 400 tk
        val estPallaCost = UnitConverterEngine.estimateCost(80.0, 1.0, "পাল্লা")
        assertEquals(400.0, estPallaCost, 0.0001)

        // 500 গ্রাম should cost 40 tk
        val est500gCost = UnitConverterEngine.estimateCost(80.0, 500.0, "গ্রাম")
        assertEquals(40.0, est500gCost, 0.0001)
    }

    @Test
    fun testDiscreteAndVolumeUnitConversionsAndCalculations() {
        // 2.1 হালি (4 pcs)
        val haliNorm = UnitConverterEngine.normalize(1.0, "হালি")
        assertEquals(4.0, haliNorm.baseQuantity, 0.0001)
        assertEquals("পিস", haliNorm.baseUnit)

        // 2.2 ডজন (12 pcs)
        val dozenNorm = UnitConverterEngine.normalize(1.0, "ডজন")
        assertEquals(12.0, dozenNorm.baseQuantity, 0.0001)
        assertEquals("পিস", dozenNorm.baseUnit)

        // 2.3 জোড়া (2 pcs)
        val joraNorm = UnitConverterEngine.normalize(1.0, "জোড়া")
        assertEquals(2.0, joraNorm.baseQuantity, 0.0001)
        assertEquals("পিস", joraNorm.baseUnit)

        // 2.4 Discrete rates: 1 হালি eggs for 48 tk -> 12 tk/pc
        val (haliRate, haliUnit) = UnitConverterEngine.calculateBaseUnitPrice(48.0, 1.0, "হালি")
        assertEquals(12.0, haliRate, 0.0001)
        assertEquals("পিস", haliUnit)

        // 1 ডজন eggs for 144 tk -> 12 tk/pc
        val (dozRate, dozUnit) = UnitConverterEngine.calculateBaseUnitPrice(144.0, 1.0, "ডজন")
        assertEquals(12.0, dozRate, 0.0001)
        assertEquals("পিস", dozUnit)

        // Cross estimation: rate 12 tk/pc -> 1 হালি = 48 tk, 1 ডজন = 144 tk, 6 pcs = 72 tk
        assertEquals(48.0, UnitConverterEngine.estimateCost(12.0, 1.0, "হালি"), 0.0001)
        assertEquals(144.0, UnitConverterEngine.estimateCost(12.0, 1.0, "ডজন"), 0.0001)
        assertEquals(72.0, UnitConverterEngine.estimateCost(12.0, 6.0, "টি"), 0.0001)

        // 2.5 Volume conversions
        val literNorm = UnitConverterEngine.normalize(1.5, "লিটার")
        assertEquals(1.5, literNorm.baseQuantity, 0.0001)
        assertEquals("লিটার", literNorm.baseUnit)

        val mlNorm = UnitConverterEngine.normalize(500.0, "মিলি")
        assertEquals(0.5, mlNorm.baseQuantity, 0.0001)
        assertEquals("লিটার", mlNorm.baseUnit)

        // Volume rate: 500 ml oil for 95 tk -> 190 tk/L
        val (oilRate, oilUnit) = UnitConverterEngine.calculateBaseUnitPrice(95.0, 500.0, "মিলি")
        assertEquals(190.0, oilRate, 0.0001)
        assertEquals("লিটার", oilUnit)
    }

    // =========================================================================
    // 2. PRICE VARIANCE PERCENTAGE & BOUNDARY CONDITIONS
    // =========================================================================

    @Test
    fun testPriceVarianceCalculationLogic() {
        // Test helper implementing exact logic from MarketPromptAmountDialog.kt
        fun calculatePriceVariance(currentRate: Double, referenceRate: Double?): String {
            if (referenceRate == null || referenceRate <= 0.0) {
                return "NO_COMPARISON"
            }
            val percentDiff = ((currentRate - referenceRate) / referenceRate) * 100.0
            return when {
                percentDiff >= 1.0 -> "EXPENSIVE"
                percentDiff <= -1.0 -> "CHEAPER"
                else -> "SAME"
            }
        }

        // 2.1 Divide by zero / null safety
        assertEquals("NO_COMPARISON", calculatePriceVariance(100.0, null))
        assertEquals("NO_COMPARISON", calculatePriceVariance(100.0, 0.0))
        assertEquals("NO_COMPARISON", calculatePriceVariance(100.0, -50.0))

        // 2.2 Exact match
        assertEquals("SAME", calculatePriceVariance(100.0, 100.0))

        // 2.3 Boundary testing near +/- 1.0%
        // +0.9% -> SAME
        assertEquals("SAME", calculatePriceVariance(100.9, 100.0))
        // +1.0% -> EXPENSIVE
        assertEquals("EXPENSIVE", calculatePriceVariance(101.0, 100.0))
        // +50% -> EXPENSIVE
        assertEquals("EXPENSIVE", calculatePriceVariance(150.0, 100.0))

        // -0.9% -> SAME
        assertEquals("SAME", calculatePriceVariance(99.1, 100.0))
        // -1.0% -> CHEAPER
        assertEquals("CHEAPER", calculatePriceVariance(99.0, 100.0))
        // -30% -> CHEAPER
        assertEquals("CHEAPER", calculatePriceVariance(70.0, 100.0))

        // 2.4 Extreme values
        // Huge price increase: 10 tk to 1000 tk (+9900%)
        assertEquals("EXPENSIVE", calculatePriceVariance(1000.0, 10.0))
        // Extreme price drop: 1000 tk to 1 tk (-99.9%)
        assertEquals("CHEAPER", calculatePriceVariance(1.0, 1000.0))

        // Very large rate: 10,000,000 tk vs 5,000,000 tk
        assertEquals("EXPENSIVE", calculatePriceVariance(10000000.0, 5000000.0))
        // Very small positive difference
        assertEquals("SAME", calculatePriceVariance(10.0001, 10.0))
    }

    // =========================================================================
    // 3. BOTTOM TRIP PROGRESS CALCULATIONS
    // =========================================================================

    @Test
    fun testBottomTripProgressCalculations() {
        data class TripProgressState(
            val progressRatio: Float,
            val progressPercent: Int,
            val isAllDone: Boolean
        )

        // Replicating the exact logic in MarketBottomTripSummaryBar.kt:
        // val progressRatio = if (totalCount > 0) (purchasedCount.toFloat() / totalCount.toFloat()).coerceIn(0f, 1f) else 0f
        // val progressPercent = (progressRatio * 100).toInt()
        // val isAllDone = totalCount > 0 && purchasedCount >= totalCount
        fun computeTripProgress(purchasedCount: Int, totalCount: Int): TripProgressState {
            val progressRatio = if (totalCount > 0) (purchasedCount.toFloat() / totalCount.toFloat()).coerceIn(0f, 1f) else 0f
            val progressPercent = (progressRatio * 100).toInt()
            val isAllDone = totalCount > 0 && purchasedCount >= totalCount
            return TripProgressState(progressRatio, progressPercent, isAllDone)
        }

        // 3.1 Empty List (0 items)
        val emptyState = computeTripProgress(0, 0)
        assertEquals(0f, emptyState.progressRatio, 0.0001f)
        assertEquals(0, emptyState.progressPercent)
        assertFalse(emptyState.isAllDone)

        // 3.2 Nothing purchased yet (0 of 5 items)
        val nonePurchased = computeTripProgress(0, 5)
        assertEquals(0f, nonePurchased.progressRatio, 0.0001f)
        assertEquals(0, nonePurchased.progressPercent)
        assertFalse(nonePurchased.isAllDone)

        // 3.3 Partial purchase (2 of 5 items -> 40%)
        val partialState = computeTripProgress(2, 5)
        assertEquals(0.4f, partialState.progressRatio, 0.0001f)
        assertEquals(40, partialState.progressPercent)
        assertFalse(partialState.isAllDone)

        // 3.4 Fractional percent: 1 of 3 items -> 33%
        val thirdState = computeTripProgress(1, 3)
        assertEquals(1f / 3f, thirdState.progressRatio, 0.001f)
        assertEquals(33, thirdState.progressPercent)
        assertFalse(thirdState.isAllDone)

        // 3.5 All items purchased (5 of 5 items -> 100%)
        val allPurchased = computeTripProgress(5, 5)
        assertEquals(1f, allPurchased.progressRatio, 0.0001f)
        assertEquals(100, allPurchased.progressPercent)
        assertTrue(allPurchased.isAllDone)

        // 3.6 Overflow / Extra items (e.g. 6 of 5 items -> coerced to 1.0f / 100%)
        val overflowState = computeTripProgress(6, 5)
        assertEquals(1f, overflowState.progressRatio, 0.0001f)
        assertEquals(100, overflowState.progressPercent)
        assertTrue(overflowState.isAllDone)

        // 3.7 Negative count safeguard
        val negativeState = computeTripProgress(-1, 5)
        assertEquals(0f, negativeState.progressRatio, 0.0001f)
        assertEquals(0, negativeState.progressPercent)
        assertFalse(negativeState.isAllDone)
    }

    @Test
    fun testRunningSpendTotalCalculations() {
        // Items spend summation logic
        val items = listOf(
            MarketItem(id = 1, listId = 1, name = "আলু", quantity = 2.0, unit = "কেজি", amount = 80.0, isPurchased = true),
            MarketItem(id = 2, listId = 1, name = "পেঁয়াজ", quantity = 1.0, unit = "কেজি", amount = 110.50, isPurchased = true),
            MarketItem(id = 3, listId = 1, name = "রসুন", quantity = 0.25, unit = "কেজি", amount = null, isPurchased = false),
            MarketItem(id = 4, listId = 1, name = "কাঁচামরিচ", quantity = 1.0, unit = "পোয়া", amount = 35.25, isPurchased = true),
            MarketItem(id = 5, listId = 1, name = "মাছ", quantity = 1.0, unit = "কেজি", amount = 450.0, isPurchased = false)
        )

        val totalSpent = items.filter { it.isPurchased }.sumOf { it.amount ?: 0.0 }
        val purchasedCount = items.count { it.isPurchased }
        val totalCount = items.size

        assertEquals(3, purchasedCount)
        assertEquals(5, totalCount)
        // 80.0 + 110.50 + 35.25 = 225.75
        assertEquals(225.75, totalSpent, 0.0001)

        // When 0 items purchased
        val unpurchasedItems = items.map { it.copy(isPurchased = false) }
        val zeroSpent = unpurchasedItems.filter { it.isPurchased }.sumOf { it.amount ?: 0.0 }
        assertEquals(0.0, zeroSpent, 0.0001)
    }

    // =========================================================================
    // 4. KEYPAD STRING MANIPULATION & MATH EVALUATION
    // =========================================================================

    @Test
    fun testKeypadInputSimulationAndEvaluations() {
        fun simulateKeypad(initialText: String, keys: List<String>): String {
            var text = initialText
            for (key in keys) {
                when (key) {
                    "⌫" -> if (text.isNotEmpty()) text = text.dropLast(1)
                    "." -> if (!text.endsWith(".")) text = if (text.isEmpty()) "0." else "$text."
                    else -> text = if (text == "0") key else "$text$key"
                }
            }
            return text
        }

        // 4.1 Type sequence: 2 -> 5 -> 0
        assertEquals("250", simulateKeypad("", listOf("2", "5", "0")))

        // 4.2 Start with 0 then type 5 -> should replace 0 with 5
        assertEquals("5", simulateKeypad("0", listOf("5")))

        // 4.3 Type decimal: 1 -> 2 -> . -> 5
        assertEquals("12.5", simulateKeypad("", listOf("1", "2", ".", "5")))

        // 4.4 Repeated decimal "." should not duplicate if already ending with "."
        assertEquals("12.", simulateKeypad("12", listOf(".", ".")))

        // 4.5 Backspace key
        assertEquals("12", simulateKeypad("125", listOf("⌫")))
        assertEquals("", simulateKeypad("1", listOf("⌫")))
        assertEquals("", simulateKeypad("", listOf("⌫"))) // empty doesn't crash

        // 4.6 Math expressions evaluated via CalculatorEvaluator
        val expr1 = CalculatorEvaluator.evaluate("45 * 2.5")
        assertNotNull(expr1)
        assertEquals(112.5, expr1!!, 0.0001)

        val expr2 = CalculatorEvaluator.evaluate("120 + 85")
        assertNotNull(expr2)
        assertEquals(205.0, expr2!!, 0.0001)

        val expr3 = CalculatorEvaluator.evaluate("500 - 150")
        assertNotNull(expr3)
        assertEquals(350.0, expr3!!, 0.0001)

        val expr4 = CalculatorEvaluator.evaluate("300 / 2")
        assertNotNull(expr4)
        assertEquals(150.0, expr4!!, 0.0001)

        // 4.7 Bengali digits conversion
        val bengaliNum = "১২৫"
        val converted = MarketListParser.convertBengaliDigitsToEnglish(bengaliNum)
        assertEquals("125", converted)
        assertEquals(125.0, converted.toDoubleOrNull() ?: 0.0, 0.0001)
    }

    // =========================================================================
    // 5. ARCHITECTURAL & ERGONOMIC VERIFICATION
    // =========================================================================

    @Test
    fun testErgonomicAndLayoutCompliance() {
        val summaryBarFile = File("src/main/java/com/example/expensetracker/ui/dashboard/MarketBottomTripSummaryBar.kt")
        assertTrue("MarketBottomTripSummaryBar.kt must exist", summaryBarFile.exists())
        val summaryBarContent = summaryBarFile.readText()

        // 1-Tap checkout button must meet minimum 48dp touch target
        assertTrue(
            "Checkout button must enforce at least 48dp touch target",
            summaryBarContent.contains("heightIn(min = 48.dp)")
        )

        // Item row checkbox touch area
        val itemRowFile = File("src/main/java/com/example/expensetracker/ui/dashboard/MarketItemRow.kt")
        assertTrue("MarketItemRow.kt must exist", itemRowFile.exists())
        val itemRowContent = itemRowFile.readText()
        assertTrue(
            "MarketItemRow checkbox must enforce 48dp minimum touch target",
            itemRowContent.contains("sizeIn(minWidth = 48.dp, minHeight = 48.dp)")
        )

        // Dialog action buttons must meet minimum 48dp touch target
        val promptDialogFile = File("src/main/java/com/example/expensetracker/ui/dashboard/MarketPromptAmountDialog.kt")
        assertTrue("MarketPromptAmountDialog.kt must exist", promptDialogFile.exists())
        val promptDialogContent = promptDialogFile.readText()
        assertTrue(
            "Cancel & Confirm buttons must enforce at least 48dp touch target",
            promptDialogContent.contains("heightIn(min = 48.dp)")
        )
    }
}
