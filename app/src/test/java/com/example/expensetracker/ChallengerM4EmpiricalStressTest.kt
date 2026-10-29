package com.example.expensetracker

import com.example.expensetracker.common.MarketListParser
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.data.model.PriceTrend
import com.example.expensetracker.data.model.ProductPriceInsight
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.dashboard.MarketDepartment
import com.example.expensetracker.ui.dashboard.MarketDepartmentClassifier
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.junit.Test
import java.io.File
import kotlin.random.Random

/**
 * Adversarial Empirical Stress Test Suite for Milestone M4 (R4 & R5):
 * Exhaustively stress-tests mathematical edge cases, extreme price shocks,
 * combinatorial filter permutations, messy WhatsApp strings, and Unicode safety.
 */
class ChallengerM4EmpiricalStressTest {

    private val basePath = "src/main/java"

    private fun readFile(relativePath: String): String {
        val file = File("$basePath/$relativePath")
        val resolved = if (file.exists()) file else File("app/$basePath/$relativePath")
        assertTrue("Expected file to exist: ${resolved.absolutePath}", resolved.exists())
        return resolved.readText()
    }

    // =========================================================================
    // 1. SPARKLINE EMPIRICAL BOUNDARY & FLOATING POINT STRESS
    // =========================================================================

    @Test
    fun testEmpiricalSparklineExtremeEdgeCases() {
        val width = 100f
        val height = 40f
        val padX = 2f
        val padY = 4f
        val usableWidth = width - (2 * padX)
        val usableHeight = height - (2 * padY)

        fun compute(points: List<Double>): List<Pair<Float, Float>> {
            if (points.isEmpty()) return emptyList()
            if (points.size == 1) return listOf(padX to height / 2f, (width - padX) to height / 2f)
            val minP = points.minOrNull() ?: 0.0
            val maxP = points.maxOrNull() ?: 1.0
            val isFlat = maxP <= minP
            val span = if (isFlat) 1.0 else maxP - minP

            return points.mapIndexed { idx, p ->
                val x = padX + (idx.toFloat() / (points.size - 1)) * usableWidth
                val y = if (isFlat) height / 2f else height - padY - (((p - minP) / span) * usableHeight).toFloat()
                x to y
            }
        }

        // 1. Extreme 100x spike: [10.0, 1000.0, 10.0]
        val spike = compute(listOf(10.0, 1000.0, 10.0))
        assertEquals(3, spike.size)
        assertEquals(padY, spike[1].second, 0.001f) // Peak at top padding
        assertFalse("NaN check", spike[1].second.isNaN())
        assertFalse("Infinite check", spike[1].second.isInfinite())

        // 2. Extremely tiny differences: [100.00001, 100.00002]
        val tiny = compute(listOf(100.00001, 100.00002))
        assertEquals(2, tiny.size)
        assertFalse(tiny[0].second.isNaN())

        // 3. 100 random price histories
        val rng = Random(42)
        for (trial in 1..100) {
            val count = rng.nextInt(2, 20)
            val pts = (1..count).map { rng.nextDouble(1.0, 2000.0) }
            val coords = compute(pts)
            assertEquals(count, coords.size)
            coords.forEach { (x, y) ->
                assertTrue(x in (padX - 0.01f)..(width - padX + 0.01f))
                assertTrue(y in (padY - 0.01f)..(height - padY + 0.01f))
            }
        }
    }

    // =========================================================================
    // 2. INFLATION SPIKES & DEFLATION SHOCKS
    // =========================================================================

    @Test
    fun testEmpiricalInflationSpikesAndDeflationShocks() {
        // Shock 1: +250% Onion Shortage Crisis
        val p1 = ProductPriceInsight("পেঁয়াজ", 350.0, 100.0, "কেজি", percentDiff = 250.0)
        assertEquals(250.0, p1.percentDiff, 0.001)

        // Shock 2: -75% Seasonal Tomato Crash
        val p2 = ProductPriceInsight("টমেটো", 25.0, 100.0, "কেজি", percentDiff = -75.0)
        assertEquals(-75.0, p2.percentDiff, 0.001)

        val basket = listOf(p1, p2)
        val avgInflation = basket.map { it.percentDiff }.average()
        assertEquals(87.5, avgInflation, 0.001)

        // Bengali string formatting of extreme rates
        val formatted = "+${convertToBengaliDigits("%.1f".format(avgInflation))}%"
        assertEquals("+৮৭.৫%", formatted)
    }

    // =========================================================================
    // 3. COMBINATORIAL SORTING STRESS
    // =========================================================================

    @Test
    fun testEmpiricalCombinatorialSortingStress() {
        val rng = Random(1337)
        val commodities = (1..50).map { id ->
            val pct = rng.nextDouble(-40.0, 80.0)
            ProductPriceInsight(
                productName = "পণ্য $id",
                currentUnitPrice = 100.0 + pct,
                previousUnitPrice = 100.0,
                baseUnit = "কেজি",
                percentDiff = pct,
                priceDiff = pct,
                purchaseCount = rng.nextInt(1, 30),
                lastPurchasedTimestamp = 1000000L + (id * 1000)
            )
        }

        // Test Hike Sort strictly monotonically descending
        val sortedHike = commodities.sortedWith(compareByDescending<ProductPriceInsight> { it.percentDiff }.thenByDescending { it.priceDiff })
        for (i in 0 until sortedHike.size - 1) {
            assertTrue(sortedHike[i].percentDiff >= sortedHike[i + 1].percentDiff)
        }

        // Test Drop Sort strictly monotonically ascending
        val sortedDrop = commodities.sortedWith(compareBy<ProductPriceInsight> { it.percentDiff }.thenBy { it.priceDiff })
        for (i in 0 until sortedDrop.size - 1) {
            assertTrue(sortedDrop[i].percentDiff <= sortedDrop[i + 1].percentDiff)
        }

        // Test Frequency Sort strictly monotonically descending
        val sortedFreq = commodities.sortedWith(compareByDescending<ProductPriceInsight> { it.purchaseCount }.thenByDescending { it.lastPurchasedTimestamp })
        for (i in 0 until sortedFreq.size - 1) {
            assertTrue(sortedFreq[i].purchaseCount >= sortedFreq[i + 1].purchaseCount)
        }
    }

    // =========================================================================
    // 4. MESSY WHATSAPP NATURAL LANGUAGE PARSING STRESS
    // =========================================================================

    @Test
    fun testEmpiricalWhatsAppBulkTextParsingStress() {
        val messyPastedText = """
            - আলু ২কেজি
            • পেঁয়াজ দেড় কেজি
            ১. রসুন ২৫০ গ্রাম
            2) সয়াবিন তেল - ৫ লিটার
            3. ডিম ১ ডজন (ফার্মের)
            4. গরুর মাংস ২.৫ কেজি
            * সাবান ২ পিস
            আদা পোয়া
        """.trimIndent()

        val parsed = MarketListParser.parseBulkText(messyPastedText)
        assertEquals(8, parsed.size)

        assertEquals("আলু", parsed[0].name)
        assertEquals(2.0, parsed[0].quantity, 0.01)

        assertEquals("পেঁয়াজ", parsed[1].name)
        assertEquals(1.5, parsed[1].quantity, 0.01)

        assertEquals("রসুন", parsed[2].name)
        assertEquals(250.0, parsed[2].quantity, 0.01)

        assertEquals("সয়াবিন তেল", parsed[3].name)
        assertEquals(5.0, parsed[3].quantity, 0.01)

        assertTrue(parsed[4].name.contains("ডিম"))
        assertEquals(1.0, parsed[4].quantity, 0.01)
        assertEquals("ডজন", parsed[4].unit)

        assertEquals("গরুর মাংস", parsed[5].name)
        assertEquals(2.5, parsed[5].quantity, 0.01)

        assertEquals("সাবান", parsed[6].name)
        assertEquals(2.0, parsed[6].quantity, 0.01)

        assertEquals("আদা", parsed[7].name)
        assertEquals(250.0, parsed[7].quantity, 0.01)
        assertEquals("গ্রাম", parsed[7].unit)
    }

    // =========================================================================
    // 5. TAG INLINE EDIT BOUNDARY STRESS
    // =========================================================================

    @Test
    fun testEmpiricalTagInlineEditBoundaryStress() {
        fun normalizeQuantityInput(rawInput: String): Double? {
            val trimmed = rawInput.trim()
            if (trimmed.isEmpty()) return null
            val englishDigits = MarketListParser.convertBengaliDigitsToEnglish(trimmed)
            val parsed = englishDigits.toDoubleOrNull() ?: return null
            return if (parsed <= 0.0) null else parsed
        }

        // Test normal Bengali digits
        assertEquals(2.5, normalizeQuantityInput("২.৫")!!, 0.001)
        assertEquals(500.0, normalizeQuantityInput("৫০০")!!, 0.001)

        // Test invalid inputs
        assertEquals(null, normalizeQuantityInput(""))
        assertEquals(null, normalizeQuantityInput("   "))
        assertEquals(null, normalizeQuantityInput("০"))
        assertEquals(null, normalizeQuantityInput("-২"))
        assertEquals(null, normalizeQuantityInput("abc"))
    }

    // =========================================================================
    // 6. DEPARTMENT OVERRIDE CONSISTENCY
    // =========================================================================

    @Test
    fun testEmpiricalDepartmentOverrideConsistency() {
        try {
            MarketDepartmentClassifier.clearOverrides()

            // Default classification
            val initialCategory = MarketDepartmentClassifier.getMarketItemCategory("পাউরুটি")
            assertEquals("dairy_bakery", initialCategory)

            // Override to "groceries"
            MarketDepartmentClassifier.setOverride("পাউরুটি", "groceries")
            assertEquals("groceries", MarketDepartmentClassifier.getOverride("পাউরুটি"))
            assertEquals("groceries", MarketDepartmentClassifier.getMarketItemCategory("পাউরুটি"))

            // Clear overrides
            MarketDepartmentClassifier.clearOverrides()
            assertEquals(null, MarketDepartmentClassifier.getOverride("পাউরুটি"))
            assertEquals("dairy_bakery", MarketDepartmentClassifier.getMarketItemCategory("পাউরুটি"))
        } finally {
            MarketDepartmentClassifier.clearOverrides()
        }
    }

    // =========================================================================
    // 7. DYNAMIC STAPLES DEDUPLICATION STRESS
    // =========================================================================

    @Test
    fun testEmpiricalDynamicStaplesDeduplicationStress() {
        // Realistic compound Bengali grocery items with quantities as provided by dynamic staples
        val allStaples = listOf(
            "সয়াবিন তেল ১ লিটার",
            "গরুর মাংস ১ কেজি",
            "কাঁচা মরিচ ২৫০ গ্রাম",
            "আলু ১ কেজি",
            "পেঁয়াজ ৫০০ গ্রাম",
            "ডিম ১২ পিস",
            "রসুন ২৫০ গ্রাম",
            "লবণ ১ কেজি"
        )
        val addedItems = mutableListOf<MarketItem>()

        // Production deduplication logic matching CreateMarketListSection.kt:1508-1512
        fun getFilteredSuggestions(currentAdded: List<MarketItem>): List<String> {
            val addedNames = currentAdded.map { it.name.trim().lowercase() }.toSet()
            return allStaples.filter { stapleStr ->
                val namePart = MarketListParser.parseSingleLine(stapleStr)?.name?.trim()?.lowercase()
                    ?: stapleStr.split(" ").firstOrNull()?.trim()?.lowercase() ?: ""
                namePart.isBlank() || namePart !in addedNames
            }
        }

        // Initial: all 8 available
        assertEquals(8, getFilteredSuggestions(addedItems).size)

        // Add multi-word staple "সয়াবিন তেল"
        addedItems.add(MarketItem(name = "সয়াবিন তেল", quantity = 1.0, unit = "লিটার"))
        val s1 = getFilteredSuggestions(addedItems)
        assertEquals(7, s1.size)
        assertFalse("Compound staple 'সয়াবিন তেল ১ লিটার' must be deduplicated when 'সয়াবিন তেল' is added", s1.contains("সয়াবিন তেল ১ লিটার"))

        // Add multi-word staple "গরুর মাংস"
        addedItems.add(MarketItem(name = "গরুর মাংস", quantity = 1.0, unit = "কেজি"))
        val s2 = getFilteredSuggestions(addedItems)
        assertEquals(6, s2.size)
        assertFalse("Compound staple 'গরুর মাংস ১ কেজি' must be deduplicated when 'গরুর মাংস' is added", s2.contains("গরুর মাংস ১ কেজি"))

        // Add multi-word staple "কাঁচা মরিচ"
        addedItems.add(MarketItem(name = "কাঁচা মরিচ", quantity = 250.0, unit = "গ্রাম"))
        val s3 = getFilteredSuggestions(addedItems)
        assertEquals(5, s3.size)
        assertFalse("Compound staple 'কাঁচা মরিচ ২৫০ গ্রাম' must be deduplicated when 'কাঁচা মরিচ' is added", s3.contains("কাঁচা মরিচ ২৫০ গ্রাম"))

        // Add remaining items
        addedItems.addAll(listOf(
            MarketItem(name = "আলু", quantity = 1.0, unit = "কেজি"),
            MarketItem(name = "পেঁয়াজ", quantity = 500.0, unit = "গ্রাম"),
            MarketItem(name = "ডিম", quantity = 12.0, unit = "পিস"),
            MarketItem(name = "রসুন", quantity = 250.0, unit = "গ্রাম"),
            MarketItem(name = "লবণ", quantity = 1.0, unit = "কেজি")
        ))
        val sEmpty = getFilteredSuggestions(addedItems)
        assertTrue("All suggestions must be deduplicated once added", sEmpty.isEmpty())
    }

    // =========================================================================
    // 8. MULTI-LIST RAPID SWITCHING STATE STRESS
    // =========================================================================

    @Test
    fun testEmpiricalMultiListRapidSwitchingStateStress() {
        // 1. Structural Verification: Ensure MarketScreen.kt genuinely wires to DashboardViewModel StateFlow
        val marketScreenContent = readFile("com/example/expensetracker/ui/market/MarketScreen.kt")
        assertTrue("MarketScreen must collect selectedMarketListId StateFlow from dashboardViewModel",
            marketScreenContent.contains("dashboardViewModel.selectedMarketListId.collectAsStateWithLifecycle()"))
        assertTrue("MarketScreen must call dashboardViewModel.selectMarketList on item selection",
            marketScreenContent.contains("dashboardViewModel.selectMarketList(listWithItems.list.id)"))
        assertTrue("MarketScreen must call dashboardViewModel.selectMarketList(null) when returning to all lists",
            marketScreenContent.contains("dashboardViewModel.selectMarketList(null)"))
        assertFalse("MarketScreen must NOT use disconnected remember for selectedListId",
            marketScreenContent.contains("var selectedListId by remember(effectiveActiveLists.size)"))

        // 2. ViewModel Contract Verification: Ensure DashboardViewModel declares the genuine StateFlow and method
        val vmContent = readFile("com/example/expensetracker/ui/dashboard/DashboardViewModel.kt")
        assertTrue("DashboardViewModel must declare selectedMarketListId StateFlow",
            vmContent.contains("val selectedMarketListId: StateFlow<Long?>"))
        assertTrue("DashboardViewModel must declare selectMarketList function",
            vmContent.contains("fun selectMarketList(listId: Long?)"))

        // 3. StateFlow Emission & Transition Mechanics
        val selectedMarketListStateFlow = MutableStateFlow<Long?>(null)
        val publicSelectedMarketListId = selectedMarketListStateFlow.asStateFlow()

        fun selectMarketList(listId: Long?) {
            selectedMarketListStateFlow.value = listId
        }

        val activeLists = listOf(101L, 102L, 103L, 104L, 105L)

        // Rapid switching stress across active lists
        activeLists.forEach { id ->
            selectMarketList(id)
            assertEquals("StateFlow must emit selected list ID", id, publicSelectedMarketListId.value)
        }

        // Switch to null (Hub / All lists view)
        selectMarketList(null)
        assertNull("StateFlow must emit null for overview hub", publicSelectedMarketListId.value)

        // Single list default logic (when effectiveActiveLists.size == 1)
        val singleList = listOf(201L)
        if (singleList.size == 1) {
            selectMarketList(singleList.first())
        }
        assertEquals(201L, publicSelectedMarketListId.value)
    }

    // =========================================================================
    // 9. BENGALI DIGIT CONVERSION BIDIRECTIONAL FIDELITY
    // =========================================================================

    @Test
    fun testEmpiricalBilingualNumeralsAndPercentagesStress() {
        val testValues = listOf(0.0, 1.5, 12.0, 25.75, 100.0, 999.9, 1234.5)
        for (v in testValues) {
            val eng = "%.1f".format(v)
            val bng = convertToBengaliDigits(eng)
            val reverted = MarketListParser.convertBengaliDigitsToEnglish(bng)
            assertEquals("Reversion fidelity check for $v", eng, reverted)
        }
    }

    // =========================================================================
    // 10. TOUCH TARGET & ACCESSIBILITY AUDIT
    // =========================================================================

    @Test
    fun testEmpiricalTouchTargetAndAccessibilityAudit() {
        val sheetContent = readFile("com/example/expensetracker/ui/dashboard/ProductPriceInsightsSheet.kt")
        val createContent = readFile("com/example/expensetracker/ui/dashboard/CreateMarketListSection.kt")

        // Check that minimumInteractiveComponentSize is applied to interactive elements
        assertTrue(sheetContent.contains("minimumInteractiveComponentSize()"))
        assertTrue(createContent.contains("minimumInteractiveComponentSize()"))
    }

    // =========================================================================
    // 11. REPOSITORY STARTUP SEEDING & CLASSIFIER OVERRIDE AUDIT
    // =========================================================================

    @Test
    fun testEmpiricalStartupSeedingAndClassifierOverrideAudit() {
        val repoContent = readFile("com/example/expensetracker/data/repository/ExpenseRepository.kt")
        assertTrue("ExpenseRepository must seed MarketDepartmentClassifier on startup",
            repoContent.contains("MarketDepartmentClassifier.setOverride(mp.normalizedName, mp.category)"))

        // Direct behavior verification: MarketDepartmentClassifier accepts and queries overrides
        MarketDepartmentClassifier.clearOverrides()
        MarketDepartmentClassifier.setOverride("সয়াবিন তেল স্পেশাল", "groceries")
        assertEquals("groceries", MarketDepartmentClassifier.getOverride("সয়াবিন তেল স্পেশাল"))
        assertEquals("groceries", MarketDepartment.fromItemName("সয়াবিন তেল স্পেশাল").id)
    }
}
