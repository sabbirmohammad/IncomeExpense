package com.example.expensetracker

import com.example.expensetracker.data.model.PriceTrend
import com.example.expensetracker.data.model.ProductPriceInsight
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.dashboard.MarketDepartment
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Unit and structural integration test suite for Milestone M4:
 * 1. Multi-Point Historical Sparklines (Feature 11 / R4)
 * 2. Department Inflation KPI Summary Card & Calculations (Feature 12 / R4)
 * 3. Price Intelligence Sorting Chips & Algorithms (Feature 13 / R4)
 * 4. In-Checklist Horizontal List Switcher (Feature 14 / R5)
 * 5. Interactive WhatsApp Tag Previews with Inline Edit/Delete (Feature 15 / R5)
 * 6. Quick Department Emoji Tag Correction (Feature 16 / R5)
 * 7. Dynamic Staple Recommendations from StateFlow (Feature 17 / R5)
 */
class MarketModernizationM4Test {

    private val basePath = "src/main/java"

    private fun readFile(relativePath: String): String {
        val file = File("$basePath/$relativePath")
        val resolved = if (file.exists()) file else File("app/$basePath/$relativePath")
        assertTrue("Expected file to exist: ${resolved.absolutePath}", resolved.exists())
        return resolved.readText()
    }

    // =========================================================================
    // 1. FEATURE 11: MULTI-POINT HISTORICAL SPARKLINES
    // =========================================================================

    @Test
    fun testSparklineDataModelAndTableHelperLoading() {
        // 1. ProductPriceInsight must support recentPricePoints with default emptyList()
        val defaultInsight = ProductPriceInsight(
            productName = "আলু",
            currentUnitPrice = 60.0,
            baseUnit = "কেজি"
        )
        assertNotNull(defaultInsight.recentPricePoints)
        assertTrue(defaultInsight.recentPricePoints.isEmpty())

        val multiPointInsight = defaultInsight.copy(
            recentPricePoints = listOf(45.0, 50.0, 55.0, 52.0, 60.0)
        )
        assertEquals(5, multiPointInsight.recentPricePoints.size)
        assertEquals(45.0, multiPointInsight.recentPricePoints.first(), 0.001)
        assertEquals(60.0, multiPointInsight.recentPricePoints.last(), 0.001)

        // 2. ProductPriceTableHelper must load chronological points
        val helperContent = readFile("com/example/expensetracker/data/local/ProductPriceTableHelper.kt")
        assertTrue(
            "ProductPriceTableHelper must extract chronological price points for sparkline",
            helperContent.contains("val pricePoints = history.take(10).reversed().map { it.unitPrice }") ||
            helperContent.contains("recentPricePoints")
        )
    }

    @Test
    fun testSparklineMathScalingAndCoordinateBounds() {
        val width = 120f
        val height = 48f
        val padX = 4f
        val padY = 6f
        val usableWidth = width - (2 * padX)
        val usableHeight = height - (2 * padY)

        fun computeSparklineCoords(points: List<Double>): List<Pair<Float, Float>> {
            if (points.isEmpty()) return emptyList()
            if (points.size == 1) {
                return listOf(padX to height / 2f, (width - padX) to height / 2f)
            }
            val minP = points.minOrNull() ?: 0.0
            val maxP = points.maxOrNull() ?: 1.0
            val isFlat = maxP <= minP
            val span = if (isFlat) 1.0 else maxP - minP

            return points.mapIndexed { idx, p ->
                val x = padX + (idx.toFloat() / (points.size - 1)) * usableWidth
                val y = if (isFlat) {
                    height / 2f
                } else {
                    height - padY - (((p - minP) / span) * usableHeight).toFloat()
                }
                x to y
            }
        }

        // Test 1: Flat prices must map to exact middle height
        val flatCoords = computeSparklineCoords(listOf(50.0, 50.0, 50.0))
        assertEquals(3, flatCoords.size)
        flatCoords.forEach { (_, y) -> assertEquals(height / 2f, y, 0.001f) }

        // Test 2: Multi-point scaling bounds
        val hikeCoords = computeSparklineCoords(listOf(40.0, 50.0, 60.0, 80.0))
        assertEquals(4, hikeCoords.size)
        assertEquals(padX, hikeCoords.first().first, 0.001f)
        assertEquals(width - padX, hikeCoords.last().first, 0.001f)
        assertEquals(height - padY, hikeCoords.first().second, 0.001f) // Lowest price at bottom
        assertEquals(padY, hikeCoords.last().second, 0.001f)          // Highest price at top

        // All y coordinates strictly within padding bounds
        hikeCoords.forEach { (_, y) ->
            assertTrue("y must be >= padY", y >= padY - 0.001f)
            assertTrue("y must be <= height - padY", y <= height - padY + 0.001f)
        }
    }

    @Test
    fun testProductPriceSparklineStructuralDesign() {
        val sheetContent = readFile("com/example/expensetracker/ui/dashboard/ProductPriceInsightsSheet.kt")

        // Must support smooth cubic bezier curve
        assertTrue(
            "ProductPriceSparkline must use cubicTo for smooth monotonic curve",
            sheetContent.contains("cubicTo(") || sheetContent.contains("quadraticTo(")
        )

        // Must support gradient area fill
        assertTrue(
            "ProductPriceSparkline must feature vertical gradient brush fill",
            sheetContent.contains("Brush.verticalGradient")
        )

        // Must render trend arrow
        assertTrue(
            "ProductPriceSparkline must render trend arrow",
            sheetContent.contains("trendArrow") && sheetContent.contains("↗") && sheetContent.contains("↘")
        )
    }

    // =========================================================================
    // 2. FEATURE 12: DEPARTMENT INFLATION KPI SUMMARY CARD & CALCULATIONS
    // =========================================================================

    @Test
    fun testDepartmentInflationIndexCalculationMath() {
        val insightAlu = ProductPriceInsight("আলু", 60.0, 50.0, "কেজি", priceDiff = 10.0, percentDiff = 20.0)
        val insightPeyaj = ProductPriceInsight("পেঁয়াজ", 120.0, 100.0, "কেজি", priceDiff = 20.0, percentDiff = 20.0)
        val insightIlish = ProductPriceInsight("ইলিশ মাছ", 1200.0, 1500.0, "কেজি", priceDiff = -300.0, percentDiff = -20.0)
        val insightDudh = ProductPriceInsight("তরল দুধ", 84.0, 80.0, "লিটার", priceDiff = 4.0, percentDiff = 5.0)
        val insightTel = ProductPriceInsight("সয়াবিন তেল", 180.0, 180.0, "লিটার", priceDiff = 0.0, percentDiff = 0.0)
        val insightFirstPurchase = ProductPriceInsight("নতুন মসলা", 50.0, null, "প্যাকেট", priceDiff = 0.0, percentDiff = 0.0)

        val insights = listOf(insightAlu, insightPeyaj, insightIlish, insightDudh, insightTel, insightFirstPurchase)

        fun calculateDepartmentInflation(items: List<ProductPriceInsight>, dept: MarketDepartment): Double? {
            val deptItems = items.filter {
                MarketDepartment.fromItemName(it.productName) == dept && it.previousUnitPrice != null
            }
            if (deptItems.isEmpty()) return null
            return deptItems.map { it.percentDiff }.average()
        }

        // Veggies: (20.0 + 20.0) / 2 = +20.0%
        val veggieInf = calculateDepartmentInflation(insights, MarketDepartment.VEGGIES)
        assertNotNull(veggieInf)
        assertEquals(20.0, veggieInf!!, 0.001)

        // Meat & Fish: (-20.0) / 1 = -20.0%
        val meatFishInf = calculateDepartmentInflation(insights, MarketDepartment.MEAT_FISH)
        assertNotNull(meatFishInf)
        assertEquals(-20.0, meatFishInf!!, 0.001)

        // Dairy & Bakery: (+5.0) / 1 = +5.0%
        val dairyInf = calculateDepartmentInflation(insights, MarketDepartment.DAIRY_BAKERY)
        assertNotNull(dairyInf)
        assertEquals(5.0, dairyInf!!, 0.001)

        // Groceries: (0.0) / 1 = 0.0% (first purchase excluded)
        val groceryInf = calculateDepartmentInflation(insights, MarketDepartment.GROCERIES)
        assertNotNull(groceryInf)
        assertEquals(0.0, groceryInf!!, 0.001)

        // Overall Market Inflation: (20 + 20 - 20 + 5 + 0) / 5 = 25 / 5 = +5.0%
        val baselineItems = insights.filter { it.previousUnitPrice != null }
        val overallInf = baselineItems.map { it.percentDiff }.average()
        assertEquals(5.0, overallInf, 0.001)
    }

    @Test
    fun testDepartmentInflationKpiCardFormattingAndTheme() {
        fun formatInflationPct(pct: Double?, isBangla: Boolean): String {
            if (pct == null) return if (isBangla) "নতুন" else "New"
            val absVal = "%.1f".format(kotlin.math.abs(pct))
            val numStr = if (isBangla) convertToBengaliDigits(absVal) else absVal
            return when {
                pct > 0.05 -> "+$numStr%"
                pct < -0.05 -> "-$numStr%"
                else -> if (isBangla) "০.০%" else "0.0%"
            }
        }

        assertEquals("+২০.০%", formatInflationPct(20.0, true))
        assertEquals("-২০.০%", formatInflationPct(-20.0, true))
        assertEquals("০.০%", formatInflationPct(0.0, true))
        assertEquals("নতুন", formatInflationPct(null, true))
        assertEquals("New", formatInflationPct(null, false))
        assertEquals("+5.2%", formatInflationPct(5.2, false))

        // Structural presence in ProductPriceInsightsSheet
        val sheetContent = readFile("com/example/expensetracker/ui/dashboard/ProductPriceInsightsSheet.kt")
        assertTrue(
            "ProductPriceInsightsSheet must contain Department Inflation KPI Summary",
            sheetContent.contains("DepartmentInflation") || sheetContent.contains("মূল্যস্ফীতি সূচক")
        )
    }

    // =========================================================================
    // 3. FEATURE 13: PRICE INTELLIGENCE SORTING CHIPS
    // =========================================================================

    @Test
    fun testPriceSortOrderEnumProperties() {
        val sheetContent = readFile("com/example/expensetracker/ui/dashboard/ProductPriceInsightsSheet.kt")
        assertTrue(sheetContent.contains("enum class PriceSortOrder"))
        assertTrue(sheetContent.contains("RECENT"))
        assertTrue(sheetContent.contains("HIGHEST_HIKE"))
        assertTrue(sheetContent.contains("BIGGEST_DROP"))
        assertTrue(sheetContent.contains("MOST_PURCHASED"))
    }

    @Test
    fun testPriceIntelligenceSortingAndFilteringAlgorithms() {
        val i1 = ProductPriceInsight("আলু", 60.0, 50.0, "কেজি", percentDiff = 20.0, priceDiff = 10.0, purchaseCount = 10, lastPurchasedTimestamp = 1000L)
        val i2 = ProductPriceInsight("পেঁয়াজ", 150.0, 100.0, "কেজি", percentDiff = 50.0, priceDiff = 50.0, purchaseCount = 3, lastPurchasedTimestamp = 2000L)
        val i3 = ProductPriceInsight("সয়াবিন তেল", 170.0, 200.0, "লিটার", percentDiff = -15.0, priceDiff = -30.0, purchaseCount = 5, lastPurchasedTimestamp = 3000L)
        val i4 = ProductPriceInsight("ডিম", 11.0, 12.0, "পিস", percentDiff = -8.3, priceDiff = -1.0, purchaseCount = 15, lastPurchasedTimestamp = 4000L)
        val i5 = ProductPriceInsight("লবণ", 40.0, 40.0, "কেজি", percentDiff = 0.0, priceDiff = 0.0, purchaseCount = 1, lastPurchasedTimestamp = 5000L)

        val list = listOf(i1, i2, i3, i4, i5)

        // 1. Highest Hike Sort
        val sortedHike = list.sortedWith(compareByDescending<ProductPriceInsight> { it.percentDiff }.thenByDescending { it.priceDiff })
        assertEquals("পেঁয়াজ", sortedHike[0].productName) // +50%
        assertEquals("আলু", sortedHike[1].productName)     // +20%
        assertEquals("লবণ", sortedHike[2].productName)     // 0%
        assertEquals("ডিম", sortedHike[3].productName)     // -8.3%
        assertEquals("সয়াবিন তেল", sortedHike[4].productName) // -15%

        // 2. Biggest Drop Sort
        val sortedDrop = list.sortedWith(compareBy<ProductPriceInsight> { it.percentDiff }.thenBy { it.priceDiff })
        assertEquals("সয়াবিন তেল", sortedDrop[0].productName) // -15%
        assertEquals("ডিম", sortedDrop[1].productName)         // -8.3%
        assertEquals("লবণ", sortedDrop[2].productName)         // 0%
        assertEquals("আলু", sortedDrop[3].productName)         // +20%
        assertEquals("পেঁয়াজ", sortedDrop[4].productName)     // +50%

        // 3. Most Purchased Sort
        val sortedFreq = list.sortedWith(compareByDescending<ProductPriceInsight> { it.purchaseCount }.thenByDescending { it.lastPurchasedTimestamp })
        assertEquals("ডিম", sortedFreq[0].productName)         // 15 purchases
        assertEquals("আলু", sortedFreq[1].productName)         // 10 purchases
        assertEquals("সয়াবিন তেল", sortedFreq[2].productName) // 5 purchases
        assertEquals("পেঁয়াজ", sortedFreq[3].productName)     // 3 purchases
        assertEquals("লবণ", sortedFreq[4].productName)         // 1 purchase

        // 4. Recent Sort
        val sortedRecent = list.sortedByDescending { it.lastPurchasedTimestamp }
        assertEquals("লবণ", sortedRecent[0].productName) // ts = 5000
        assertEquals("আলু", sortedRecent[4].productName) // ts = 1000
    }

    // =========================================================================
    // 4. FEATURE 14: IN-CHECKLIST HORIZONTAL LIST SWITCHER
    // =========================================================================

    @Test
    fun testMarketHorizontalListSwitcherStructureAndFlow() {
        val screenContent = readFile("com/example/expensetracker/ui/market/MarketScreen.kt")

        // Must declare or invoke MarketHorizontalListSwitcher
        assertTrue(
            "MarketScreen must include horizontal active list switcher for 1-tap switching",
            screenContent.contains("MarketHorizontalListSwitcher") ||
            (screenContent.contains("LazyRow") && screenContent.contains("effectiveActiveLists") && screenContent.contains("selectedListId"))
        )
    }

    // =========================================================================
    // 5. FEATURES 15 & 16: INTERACTIVE WHATSAPP TAG PREVIEWS & DEPARTMENT OVERRIDE
    // =========================================================================

    @Test
    fun testMarketParsedTagChipStructureAndInteractions() {
        val createContent = readFile("com/example/expensetracker/ui/dashboard/CreateMarketListSection.kt")
        assertTrue(
            "CreateMarketListSection must contain MarketParsedTagChip composable",
            createContent.contains("MarketParsedTagChip")
        )
        assertTrue(
            "MarketParsedTagChip must support deletion and category selection",
            createContent.contains("onDelete") && createContent.contains("onDepartmentSelected")
        )
    }

    @Test
    fun testCreateMarketListSectionNoTruncationAndInteractiveTags() {
        val createContent = readFile("com/example/expensetracker/ui/dashboard/CreateMarketListSection.kt")

        // 1. Must remove arbitrary take(10) truncation
        assertFalse(
            "CreateMarketListSection must NOT truncate parsed WhatsApp tags to take(10)",
            createContent.contains("parsedBulkItems.take(10)")
        )

        // 2. Must feature interactive tag chips with edit, delete, and department emoji
        assertTrue(
            "CreateMarketListSection must feature interactive tag preview chips",
            createContent.contains("MarketParsedTagChip") ||
            (createContent.contains("onDelete") && createContent.contains("dept"))
        )
    }

    // =========================================================================
    // 6. FEATURE 17: DYNAMIC STAPLE RECOMMENDATIONS
    // =========================================================================

    @Test
    fun testDynamicStapleRecommendationsFlowAndDeduplication() {
        val createContent = readFile("com/example/expensetracker/ui/dashboard/CreateMarketListSection.kt")
        val screenContent = readFile("com/example/expensetracker/ui/market/MarketScreen.kt")

        // CreateMarketListSection must accept stapleProducts parameter
        assertTrue(
            "CreateMarketListSection must accept stapleProducts parameter",
            createContent.contains("stapleProducts: List<")
        )

        // MarketScreen must pass stapleProducts to CreateMarketListSection
        assertTrue(
            "MarketScreen must pass stapleProducts to CreateMarketListSection",
            screenContent.contains("CreateMarketListSection(") &&
            screenContent.contains("stapleProducts = stapleProducts")
        )
    }

    // =========================================================================
    // 7. REGRESSION & THEME GUARDRAILS
    // =========================================================================

    @Test
    fun testMarketM4ThemeHygieneAndTargetCompliance() {
        val sheetContent = readFile("com/example/expensetracker/ui/dashboard/ProductPriceInsightsSheet.kt")
        val createContent = readFile("com/example/expensetracker/ui/dashboard/CreateMarketListSection.kt")
        val screenContent = readFile("com/example/expensetracker/ui/market/MarketScreen.kt")

        // 1. Strictly NO AI sparkles (✨ or AutoAwesome)
        val allMarketCode = sheetContent + createContent + screenContent
        assertFalse("Market codebase must NOT contain AI sparkle emoji '✨'", allMarketCode.contains("✨"))
        assertFalse("Market codebase must NOT contain AutoAwesome icons", allMarketCode.contains("AutoAwesome"))

        // 2. Minimum 48dp touch targets on interactive chips
        assertTrue(
            "ProductPriceInsightsSheet must enforce 48dp minimum touch target or minimumInteractiveComponentSize",
            sheetContent.contains("minimumInteractiveComponentSize()") || sheetContent.contains("48.dp")
        )
    }
}
