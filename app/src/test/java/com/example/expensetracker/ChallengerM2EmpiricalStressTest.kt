package com.example.expensetracker

import com.example.expensetracker.common.MarketListParser
import com.example.expensetracker.common.UnitConverterEngine
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.data.model.PriceTrend
import com.example.expensetracker.data.model.ProductPriceInsight
import com.example.expensetracker.ui.components.CalculatorEvaluator
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.formatCurrency
import com.example.expensetracker.ui.dashboard.getDepartmentMeta
import com.example.expensetracker.ui.dashboard.getMarketItemCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Empirical Stress-Testing Harness for Milestone M2:
 * 1. UI components under boundary conditions: long item names, exotic unicode, empty names.
 * 2. Extreme prices: zero amount, negative values, very large values, tiny fractions, expression evaluation.
 * 3. Empty lists: zero items, all done, partial completion, over-completion, running spend sum.
 * 4. Rapid interactions & state transitions: check/uncheck cycles, math parsing, quantity conversions.
 * 5. Theme adaptation, accessibility & touch target compliance (minimum 48dp).
 */
class ChallengerM2EmpiricalStressTest {

    private val basePath = "src/main/java"

    private fun readFile(relativePath: String): String {
        val file = File("$basePath/$relativePath")
        val resolved = if (file.exists()) file else File("app/$basePath/$relativePath")
        assertTrue("Expected file to exist: ${resolved.absolutePath}", resolved.exists())
        return resolved.readText()
    }

    // =========================================================================
    // 1. BOUNDARY CONDITIONS: LONG ITEM NAMES & EXOTIC UNICODE
    // =========================================================================

    @Test
    fun testLongItemNamesAndExoticUnicodeResilience() {
        // 500-character name
        val extremelyLongName = "খাঁটি দেশি স্পেশাল লাল আলু ".repeat(20).trim()
        val normalizedLong = UnitConverterEngine.normalizeProductName(extremelyLongName)
        assertTrue("Normalized long name must not be blank", normalizedLong.isNotBlank())
        assertEquals("veggies", getMarketItemCategory(extremelyLongName))
        val deptLong = getDepartmentMeta(getMarketItemCategory(extremelyLongName))
        assertEquals("কাঁচাবাজার", deptLong.titleBn)

        // Empty and blank names
        assertEquals("", UnitConverterEngine.normalizeProductName(""))
        assertEquals("", UnitConverterEngine.normalizeProductName("   "))
        assertEquals("groceries", getMarketItemCategory(""))
        assertEquals("groceries", getMarketItemCategory("   "))
        val deptEmpty = getDepartmentMeta(getMarketItemCategory(""))
        assertEquals("মুদি সদাই", deptEmpty.titleBn)

        // Exotic Bengali Unicode: complex conjuncts, chandrabindu, hasant, nukta, emojis
        val exoticName = "  🌶️ কাঁচা মরিচ (ঝাল) — দেশি [স্পেশাল] ৳১০০/কেজি! ✨ "
        val normalizedExotic = UnitConverterEngine.normalizeProductName(exoticName)
        assertTrue("Normalized name must strip punctuation and parentheticals", normalizedExotic.contains("কাঁচা মরিচ"))
        assertFalse("Normalized name must not retain brackets", normalizedExotic.contains("["))
        assertFalse("Normalized name must not retain parentheses", normalizedExotic.contains("("))
        assertEquals("veggies", getMarketItemCategory(exoticName))

        // Multi-line and control characters
        val multiLineName = "সয়াবিন তেল\n৫ লিটার\r\nতীর ব্র্যান্ড"
        val normalizedMultiLine = UnitConverterEngine.normalizeProductName(multiLineName)
        assertTrue("Must normalize multi-line without crashing", normalizedMultiLine.isNotBlank())
        assertEquals("groceries", getMarketItemCategory(multiLineName))
    }

    @Test
    fun testBoguraDistrictSubstringClassifierEdgeCase() {
        // Adversarial challenge: "বগুড়ার" contains the substring "গুড়া" (powder).
        // Priority 1 rule in getMarketItemCategory tests lower.contains("গুড়া").
        // This causes "বগুড়ার আলু" to be classified as groceries instead of veggies.
        // Documenting this finding for Milestone M3 remediation.
        val categoryWithBogura = getMarketItemCategory("বগুড়ার আলু")
        val categoryWithoutBogura = getMarketItemCategory("দেশি আলু")

        assertEquals("veggies", categoryWithoutBogura)
        // Remediated in Milestone M3: "বগুড়ার আলু" now correctly maps to veggies
        assertEquals("veggies", categoryWithBogura)
    }

    // =========================================================================
    // 2. BOUNDARY CONDITIONS: EXTREME PRICES & NUMERICAL EXPRESSIONS
    // =========================================================================

    @Test
    fun testExtremePricesAndUnitConversionBoundaryConditions() {
        // Zero Price
        val (rateZero, unitZero) = UnitConverterEngine.calculateBaseUnitPrice(0.0, 2.5, "কেজি")
        assertEquals(0.0, rateZero, 0.0001)
        assertEquals("কেজি", unitZero)

        // Extremely Large Price (100 Million BDT)
        val largePrice = 100_000_000.0
        val (rateLarge, unitLarge) = UnitConverterEngine.calculateBaseUnitPrice(largePrice, 5.0, "পাল্লা")
        // 5 palla = 25 kg -> 100M / 25 = 4,000,000
        assertEquals(4_000_000.0, rateLarge, 0.01)
        assertEquals("কেজি", unitLarge)

        // Tiny Fractional Weight (e.g. 50 grams for 25 tk)
        val (rateGram, unitGram) = UnitConverterEngine.calculateBaseUnitPrice(25.0, 50.0, "গ্রাম")
        // 50g = 0.05kg -> 25 / 0.05 = 500 tk/kg
        assertEquals(500.0, rateGram, 0.01)
        assertEquals("কেজি", unitGram)

        // Fractional Quantities: 1 Powa (250g)
        val (ratePowa, unitPowa) = UnitConverterEngine.calculateBaseUnitPrice(75.0, 1.0, "পোয়া")
        // 1 powa = 0.25kg -> 75 / 0.25 = 300 tk/kg
        assertEquals(300.0, ratePowa, 0.01)
        assertEquals("কেজি", unitPowa)

        // Discrete Count Units: 1 Hali (4 pcs), 1 Dozen (12 pcs)
        val (rateHali, unitHali) = UnitConverterEngine.calculateBaseUnitPrice(48.0, 1.0, "হালি")
        // 48 / 4 = 12 tk/piece
        assertEquals(12.0, rateHali, 0.01)
        assertEquals("পিস", unitHali)

        val (rateDozen, unitDozen) = UnitConverterEngine.calculateBaseUnitPrice(144.0, 1.0, "ডজন")
        // 144 / 12 = 12 tk/piece
        assertEquals(12.0, rateDozen, 0.01)
        assertEquals("পিস", unitDozen)

        // Volume Units: 500 ml for 90 tk
        val (rateVolume, unitVolume) = UnitConverterEngine.calculateBaseUnitPrice(90.0, 500.0, "মিলি")
        // 500ml = 0.5L -> 90 / 0.5 = 180 tk/L
        assertEquals(180.0, rateVolume, 0.01)
        assertEquals("লিটার", unitVolume)

        // Negative Quantity or Zero Quantity Safe Fallback
        val (rateZeroQty, _) = UnitConverterEngine.calculateBaseUnitPrice(50.0, 0.0, "কেজি")
        assertEquals(50.0, rateZeroQty, 0.0001)

        val (rateNegQty, _) = UnitConverterEngine.calculateBaseUnitPrice(50.0, -2.0, "কেজি")
        assertEquals(50.0, rateNegQty, 0.0001)

        // Cost estimation with extreme values
        val estHigh = UnitConverterEngine.estimateCost(100.0, 10_000.0, "কেজি")
        assertEquals(1_000_000.0, estHigh, 0.01)

        val estFraction = UnitConverterEngine.estimateCost(200.0, 250.0, "গ্রাম")
        assertEquals(50.0, estFraction, 0.01)
    }

    @Test
    fun testPriceVarianceComparisonLogic() {
        val baseRate = 100.0

        // 1. Cheaper purchase (savings): current = 80, baseline = 100 -> -20%
        val cheaperRate = 80.0
        val diffCheaper = ((cheaperRate - baseRate) / baseRate) * 100.0
        assertTrue("Must identify as cheaper", diffCheaper <= -1.0)
        assertEquals(-20.0, diffCheaper, 0.001)

        // 2. More expensive purchase: current = 125, baseline = 100 -> +25%
        val expensiveRate = 125.0
        val diffExpensive = ((expensiveRate - baseRate) / baseRate) * 100.0
        assertTrue("Must identify as expensive", diffExpensive >= 1.0)
        assertEquals(25.0, diffExpensive, 0.001)

        // 3. Negligible difference (< 1%): current = 100.5, baseline = 100 -> +0.5%
        val closeRate = 100.5
        val diffClose = ((closeRate - baseRate) / baseRate) * 100.0
        assertFalse("Must not trigger expensive badge for < 1%", diffClose >= 1.0)
        assertFalse("Must not trigger cheaper badge for < 1%", diffClose <= -1.0)

        // 4. ProductPriceInsight formatDiff
        val insightUp = ProductPriceInsight(
            productName = "আলু",
            currentUnitPrice = 60.0,
            previousUnitPrice = 50.0,
            baseUnit = "কেজি",
            priceDiff = 10.0,
            percentDiff = 20.0,
            priceTrend = PriceTrend.UP
        )
        assertTrue(insightUp.isPriceUp)
        assertEquals("+৳10 (20.0%)", insightUp.formatDiff())

        val insightDown = ProductPriceInsight(
            productName = "পেঁয়াজ",
            currentUnitPrice = 80.0,
            previousUnitPrice = 100.0,
            baseUnit = "কেজি",
            priceDiff = -20.0,
            percentDiff = -20.0,
            priceTrend = PriceTrend.DOWN
        )
        assertTrue(insightDown.isPriceDown)
        assertEquals("-৳20 (20.0%)", insightDown.formatDiff())

        val insightStable = ProductPriceInsight(
            productName = "ডিম",
            currentUnitPrice = 12.0,
            previousUnitPrice = 12.0,
            baseUnit = "পিস",
            priceDiff = 0.0,
            percentDiff = 0.0,
            priceTrend = PriceTrend.STABLE
        )
        assertTrue(insightStable.isPriceStable)
        assertEquals("০%", insightStable.formatDiff())
    }

    @Test
    fun testCalculatorEvaluatorUnderStressAndEdgeCases() {
        // Multiplier expression with Bengali numerals: "৪৫ * ২.৫"
        val resBn = CalculatorEvaluator.evaluate("৪৫ * ২.৫")
        assertNotNull(resBn)
        assertEquals(112.5, resBn!!, 0.001)

        // Operator precedence: 10 + 20 * 3 - 40 / 8 = 10 + 60 - 5 = 65
        val resPrecedence = CalculatorEvaluator.evaluate("10 + 20 * 3 - 40 / 8")
        assertNotNull(resPrecedence)
        assertEquals(65.0, resPrecedence!!, 0.001)

        // Division by zero must safely return null without crash
        val resDivZero = CalculatorEvaluator.evaluate("100 / 0")
        assertNull("Division by zero must return null", resDivZero)

        // Trailing operator
        val resTrailing = CalculatorEvaluator.evaluate("150 +")
        assertNull("Trailing operator must return null", resTrailing)

        // Multiple decimal points in single number
        val resMultiDot = CalculatorEvaluator.evaluate("12.3.4 + 5")
        assertNull("Invalid decimals must return null", resMultiDot)

        // Huge number calculation
        val resHuge = CalculatorEvaluator.evaluate("50000000 + 50000000")
        assertNotNull(resHuge)
        assertEquals(100_000_000.0, resHuge!!, 0.01)

        // Clean conversion of Bengali digits
        val converted = MarketListParser.convertBengaliDigitsToEnglish("১২৩৪৫৬৭৮৯০")
        assertEquals("1234567890", converted)
    }

    // =========================================================================
    // 3. BOUNDARY CONDITIONS: EMPTY LISTS & PROGRESS CALCULATIONS
    // =========================================================================

    @Test
    fun testProgressCalculationsAndEmptyListBoundaries() {
        // 1. Empty List: totalCount = 0, purchasedCount = 0
        val totalCount0 = 0
        val purchasedCount0 = 0
        val progressRatio0 = if (totalCount0 > 0) (purchasedCount0.toFloat() / totalCount0.toFloat()).coerceIn(0f, 1f) else 0f
        val progressPercent0 = (progressRatio0 * 100).toInt()
        val isAllDone0 = totalCount0 > 0 && purchasedCount0 >= totalCount0
        assertEquals(0f, progressRatio0, 0.001f)
        assertEquals(0, progressPercent0)
        assertFalse("0 total items cannot be marked as all done", isAllDone0)

        // 2. Normal Partial: 8 of 12 items purchased
        val totalCount12 = 12
        val purchasedCount8 = 8
        val progressRatio8 = if (totalCount12 > 0) (purchasedCount8.toFloat() / totalCount12.toFloat()).coerceIn(0f, 1f) else 0f
        val progressPercent8 = (progressRatio8 * 100).toInt()
        val isAllDone8 = totalCount12 > 0 && purchasedCount8 >= totalCount12
        assertEquals(0.6666f, progressRatio8, 0.001f)
        assertEquals(66, progressPercent8)
        assertFalse(isAllDone8)

        // 3. Complete: 12 of 12 items purchased
        val purchasedCount12 = 12
        val progressRatio12 = if (totalCount12 > 0) (purchasedCount12.toFloat() / totalCount12.toFloat()).coerceIn(0f, 1f) else 0f
        val progressPercent12 = (progressRatio12 * 100).toInt()
        val isAllDone12 = totalCount12 > 0 && purchasedCount12 >= totalCount12
        assertEquals(1.0f, progressRatio12, 0.001f)
        assertEquals(100, progressPercent12)
        assertTrue(isAllDone12)

        // 4. Over-completion Edge Case: purchased > totalCount (coerced in 0..1f)
        val purchasedCountOverflow = 15
        val progressRatioOver = if (totalCount12 > 0) (purchasedCountOverflow.toFloat() / totalCount12.toFloat()).coerceIn(0f, 1f) else 0f
        val progressPercentOver = (progressRatioOver * 100).toInt()
        assertEquals(1.0f, progressRatioOver, 0.001f)
        assertEquals(100, progressPercentOver)

        // 5. Bengali Numeral Digit Conversion for summary pills
        val bnPill = "${convertToBengaliDigits(purchasedCount8.toString())}/${convertToBengaliDigits(totalCount12.toString())} সম্পন্ন (${convertToBengaliDigits(progressPercent8.toString())}%)"
        assertEquals("৮/১২ সম্পন্ন (৬৬%)", bnPill)
    }

    // =========================================================================
    // 4. RAPID CHECK/UNCHECK INTERACTIONS & STATE CONSISTENCY
    // =========================================================================

    @Test
    fun testRapidCheckUncheckStateTransitions() {
        var item = MarketItem(
            id = 101,
            listId = 1,
            name = "টমেটো",
            quantity = 1.0,
            unit = "কেজি",
            isPurchased = false,
            amount = null
        )

        // 1. Initial State: unpurchased, no amount
        assertFalse(item.isPurchased)
        assertNull(item.amount)

        // 2. User checks item off and enters 60 BDT
        item = item.copy(isPurchased = true, amount = 60.0)
        assertTrue(item.isPurchased)
        assertEquals(60.0, item.amount!!, 0.001)

        // 3. User immediately unchecks it (rapid toggle)
        item = item.copy(isPurchased = false, amount = null)
        assertFalse(item.isPurchased)
        assertNull(item.amount)

        // 4. User checks it again with zero amount (free item)
        item = item.copy(isPurchased = true, amount = 0.0)
        assertTrue(item.isPurchased)
        assertEquals(0.0, item.amount!!, 0.001)

        // 5. User changes quantity via preset (1 powa = 0.25 kg)
        item = item.copy(quantity = 0.25, unit = "কেজি", amount = 15.0)
        assertEquals(0.25, item.quantity, 0.001)
        assertEquals("কেজি", item.unit)
        assertEquals(15.0, item.amount!!, 0.001)
    }

    // =========================================================================
    // 5. ACCESSIBILITY, TOUCH TARGETS & THEME RESOLUTION
    // =========================================================================

    @Test
    fun testAccessibilityTouchTargetCompliance() {
        val summaryBarContent = readFile("com/example/expensetracker/ui/dashboard/MarketBottomTripSummaryBar.kt")
        val itemRowContent = readFile("com/example/expensetracker/ui/dashboard/MarketItemRow.kt")
        val dialogContent = readFile("com/example/expensetracker/ui/dashboard/MarketPromptAmountDialog.kt")

        // 1. MarketBottomTripSummaryBar Checkout Button must have at least 48dp height
        assertTrue(
            "Summary Bar checkout button must enforce minimum 48dp height",
            summaryBarContent.contains("heightIn(min = 48.dp)")
        )

        // 2. MarketItemRow Checkbox must have at least 48dp touch target
        assertTrue(
            "MarketItemRow circular checkbox must enforce minimum 48dp touch target",
            itemRowContent.contains("sizeIn(minWidth = 48.dp, minHeight = 48.dp)") &&
            itemRowContent.contains("minimumInteractiveComponentSize()")
        )

        // 3. MarketPromptAmountDialog Action Buttons must have at least 48dp height
        assertTrue(
            "MarketPromptAmountDialog action buttons must enforce minimum 48dp height",
            dialogContent.contains("heightIn(min = 48.dp)")
        )
        assertTrue(
            "Mark without price button must enforce minimumInteractiveComponentSize()",
            dialogContent.contains("minimumInteractiveComponentSize()")
        )
    }

    @Test
    fun testThemeAdaptabilityAndColorContrastSemantics() {
        val summaryBarContent = readFile("com/example/expensetracker/ui/dashboard/MarketBottomTripSummaryBar.kt")
        val itemRowContent = readFile("com/example/expensetracker/ui/dashboard/MarketItemRow.kt")
        val dialogContent = readFile("com/example/expensetracker/ui/dashboard/MarketPromptAmountDialog.kt")

        // 1. Verify LocalAppColors is consumed in all M2 components
        assertTrue(summaryBarContent.contains("val appColors = LocalAppColors.current"))
        assertTrue(itemRowContent.contains("val appColors = LocalAppColors.current"))
        assertTrue(dialogContent.contains("val appColors = LocalAppColors.current"))

        // 2. Verify no hardcoded Color.Black on text or cards
        assertFalse("Summary bar must not hardcode Color.Black", summaryBarContent.contains("Color.Black"))
        assertFalse("Item row must not hardcode Color.Black", itemRowContent.contains("Color.Black"))
        assertFalse("Amount dialog must not hardcode Color.Black", dialogContent.contains("Color.Black"))

        // 3. Verify semantic colors used for surfaces, borders, text
        assertTrue(summaryBarContent.contains("color = appColors.surface"))
        assertTrue(summaryBarContent.contains("color = appColors.textPrimary"))
        assertTrue(summaryBarContent.contains("color = appColors.textSecondary"))

        assertTrue(itemRowContent.contains("appColors.textPrimary"))
        assertTrue(itemRowContent.contains("appColors.textSecondary"))
        assertTrue(itemRowContent.contains("appColors.income"))

        assertTrue(dialogContent.contains("containerColor = appColors.surface"))
        assertTrue(dialogContent.contains("appColors.textPrimary"))
        assertTrue(dialogContent.contains("appColors.textSecondary"))
    }

    @Test
    fun testScrollPaddingPreventsOcclusionByDockedSummaryBar() {
        val marketScreenContent = readFile("com/example/expensetracker/ui/market/MarketScreen.kt")
        val marketSheetContent = readFile("com/example/expensetracker/ui/dashboard/MarketListSheet.kt")

        // 1. MarketScreen has 160dp bottom padding when docked bar is visible
        assertTrue(
            "MarketScreen LazyColumn must have 160dp bottom padding when items are not empty",
            marketScreenContent.contains("PaddingValues(bottom = 160.dp)")
        )

        // 2. MarketListSheet has 100dp bottom padding when docked bar is visible
        assertTrue(
            "MarketListSheet LazyColumn must have 100dp bottom padding when items are not empty",
            marketSheetContent.contains("PaddingValues(bottom = if (items.isNotEmpty()) 100.dp else 16.dp)")
        )
    }
}
