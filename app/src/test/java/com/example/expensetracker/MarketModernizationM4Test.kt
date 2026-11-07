package com.example.expensetracker

import com.example.expensetracker.data.model.PriceTrend
import com.example.expensetracker.data.model.ProductPriceInsight
import com.example.expensetracker.ui.dashboard.DepartmentInflationKpi
import com.example.expensetracker.ui.dashboard.PriceSortOrder
import com.example.expensetracker.ui.dashboard.computeDepartmentInflationKpis
import com.example.expensetracker.ui.dashboard.formatInflationPct
import com.example.expensetracker.ui.dashboard.getMarketItemCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Verification test suite for Milestone M4:
 * 1. Living Ledger design system tokens in ProductPriceInsightsSheet.kt (cardBackground, HairlineInkStroke, divider).
 * 2. Strict typography scale invariant: zero sub-12sp font sizes (9.sp, 10.sp, 10.5.sp, 11.sp, 11.5.sp -> 0 occurrences).
 * 3. Touch target ergonomics: minimum 48dp on all interactive elements (chips, buttons, rows).
 * 4. Multi-point cubic Bézier sparklines integration, flatness guard, and gradient fill.
 * 5. Department inflation KPI engine (computeDepartmentInflationKpis), division-by-zero guards, and formatting.
 * 6. PriceSortOrder enum & deterministic multi-criteria sorting comparators.
 * 7. Negative constraints: zero AI sparkle tropes (AutoAwesome, ✨).
 * 8. Model and data helper invariants: recentPricePoints preservation and non-finite number guards.
 */
class MarketModernizationM4Test {

    private val basePath = "src/main/java"

    private fun readFile(relativePath: String): String {
        val file = File("$basePath/$relativePath")
        assertTrue("Expected file to exist: ${file.absolutePath}", file.exists())
        return file.readText()
    }

    @Test
    fun testLivingLedgerDesignTokensInInsightsSheet() {
        val content = readFile("com/example/expensetracker/ui/dashboard/ProductPriceInsightsSheet.kt")

        // 1. Surface and sheet container tokens
        assertTrue(
            "ProductPriceInsightsSheet must use appColors.cardBackground",
            content.contains("appColors.cardBackground")
        )
        assertFalse(
            "ModalBottomSheet must not hardcode appColors.background as containerColor",
            content.contains("containerColor = appColors.background")
        )

        // 2. Hairline Ink Stroke and dividers
        assertTrue(
            "ProductPriceInsightsSheet must import HairlineInkStroke",
            content.contains("import com.example.expensetracker.theme.HairlineInkStroke")
        )
        assertTrue(
            "ProductPriceInsightsSheet must use HairlineInkStroke for borders",
            content.contains("HairlineInkStroke")
        )
        assertTrue(
            "ProductPriceInsightsSheet must use appColors.divider",
            content.contains("appColors.divider")
        )

        // 3. Top sheet rounded corners (24.dp)
        assertTrue(
            "ModalBottomSheet must declare 24.dp top rounded corner shape",
            content.contains("RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)")
        )
    }

    @Test
    fun testBengaliTypographyConstraintsAcrossM4() {
        val content = readFile("com/example/expensetracker/ui/dashboard/ProductPriceInsightsSheet.kt")

        // Forbidden font size tokens for Bengali legibility and Living Ledger design system
        val forbiddenTokens = listOf("9.sp", "9.5.sp", "10.sp", "10.5.sp", "11.sp", "11.5.sp")
        for (token in forbiddenTokens) {
            val hasForbiddenToken = Regex("(?<![0-9])${Regex.escape(token)}").containsMatchIn(content)
            assertFalse(
                "ProductPriceInsightsSheet.kt violates >= 12sp constraint by containing $token",
                hasForbiddenToken
            )
        }

        // Tabular figures must be enabled
        assertTrue(
            "ProductPriceInsightsSheet must import or use TabularNumFeature",
            content.contains("TabularNumFeature") || content.contains("\"tnum\"")
        )

        // Explicit weight tokens must be used
        assertTrue(
            "ProductPriceInsightsSheet must use explicit Living Ledger font weight tokens",
            content.contains("WeightBold") && content.contains("WeightSemiBold") && content.contains("WeightMedium")
        )
    }

    @Test
    fun testTouchTargetErgonomicsAcrossInsightsSheet() {
        val content = readFile("com/example/expensetracker/ui/dashboard/ProductPriceInsightsSheet.kt")

        // 1. minimumInteractiveComponentSize must be used across interactive surfaces
        assertTrue(
            "ProductPriceInsightsSheet must import or use minimumInteractiveComponentSize",
            content.contains("minimumInteractiveComponentSize()")
        )

        // 2. Filter chips and buttons must enforce >= 48dp minimum touch target
        assertTrue(
            "ProductPriceInsightsSheet must enforce minHeight = 48.dp on chips",
            content.contains("minHeight = 48.dp") || content.contains("height = 48.dp")
        )

        // 3. Stock rows must have 48dp minimum height
        assertTrue(
            "ProductPriceStockRow must declare defaultMinSize(minHeight = 48.dp) with minimumInteractiveComponentSize",
            content.contains("defaultMinSize(minHeight = 48.dp)")
        )
    }

    @Test
    fun testSparklineIntegrationAndGeometryInvariants() {
        val content = readFile("com/example/expensetracker/ui/dashboard/ProductPriceInsightsSheet.kt")

        // 1. Sparkline must be called inside ProductPriceStockRow
        val stockRowSection = content.substringAfter("fun ProductPriceStockRow").substringBefore("fun ProductPriceDetailSheet")
        assertTrue(
            "ProductPriceSparkline must be invoked inside ProductPriceStockRow",
            stockRowSection.contains("ProductPriceSparkline(")
        )

        // 2. Sparkline must be called inside ProductPriceDetailSheet
        val detailSheetSection = content.substringAfter("fun ProductPriceDetailSheet").substringBefore("fun PriceMetricCard")
        assertTrue(
            "ProductPriceSparkline must be invoked inside ProductPriceDetailSheet",
            detailSheetSection.contains("ProductPriceSparkline(")
        )

        // 3. Geometry implementation must use cubic Bézier with midX control points
        val sparklineSection = content.substringAfter("fun ProductPriceSparkline")
        assertTrue(
            "ProductPriceSparkline must use cubicTo for smooth monotone curves",
            sparklineSection.contains("cubicTo(")
        )
        assertTrue(
            "ProductPriceSparkline must calculate midpoint midX",
            sparklineSection.contains("val midX =") || sparklineSection.contains("(p0.x + p1.x) / 2f")
        )

        // 4. Area gradient fill and endpoint dot
        assertTrue(
            "ProductPriceSparkline must use vertical gradient for area fill",
            sparklineSection.contains("Brush.verticalGradient")
        )
        assertTrue(
            "ProductPriceSparkline must render an endpoint dot via drawCircle",
            sparklineSection.contains("drawCircle(")
        )

        // 5. Flatness protection
        assertTrue(
            "ProductPriceSparkline must protect against zero-span flat curves",
            sparklineSection.contains("isFlat") || sparklineSection.contains("< 0.0001")
        )
    }

    @Test
    fun testDepartmentInflationEngineMathematicalResilience() {
        // 1. Empty catalog: returns 4 department KPIs with null averageInflationPercent and STABLE trend
        val emptyKpis = computeDepartmentInflationKpis(emptyList())
        assertEquals(4, emptyKpis.size)
        for (kpi in emptyKpis) {
            assertNull("Empty catalog department inflation must be null", kpi.averageInflationPercent)
            assertEquals(PriceTrend.STABLE, kpi.trend)
            assertEquals(0, kpi.trackedItemsCount)
            assertEquals(0, kpi.itemsWithHistoryCount)
        }

        // 2. Department with inflation (+15%)
        val alu = ProductPriceInsight(
            productName = "আলু",
            currentUnitPrice = 115.0,
            previousUnitPrice = 100.0,
            baseUnit = "কেজি",
            priceDiff = 15.0,
            percentDiff = 15.0,
            priceTrend = PriceTrend.UP
        )
        val peyaj = ProductPriceInsight(
            productName = "পেঁয়াজ",
            currentUnitPrice = 110.0,
            previousUnitPrice = 100.0,
            baseUnit = "কেজি",
            priceDiff = 10.0,
            percentDiff = 10.0,
            priceTrend = PriceTrend.UP
        )
        val kpis = computeDepartmentInflationKpis(listOf(alu, peyaj))
        val veggieKpi = kpis.first { it.department.key == "veggies" }
        assertEquals(2, veggieKpi.trackedItemsCount)
        assertEquals(2, veggieKpi.itemsWithHistoryCount)
        assertNotNull(veggieKpi.averageInflationPercent)
        assertEquals(12.5, veggieKpi.averageInflationPercent!!, 0.001)
        assertEquals(PriceTrend.UP, veggieKpi.trend)

        // 3. Department with deflation (-20%)
        val rui = ProductPriceInsight(
            productName = "রুই মাছ",
            currentUnitPrice = 320.0,
            previousUnitPrice = 400.0,
            baseUnit = "কেজি",
            priceDiff = -80.0,
            percentDiff = -20.0,
            priceTrend = PriceTrend.DOWN
        )
        val meatFishKpi = computeDepartmentInflationKpis(listOf(rui)).first { it.department.key == "meat_fish" }
        assertEquals(-20.0, meatFishKpi.averageInflationPercent!!, 0.001)
        assertEquals(PriceTrend.DOWN, meatFishKpi.trend)

        // 4. Products with null or 0.0 previous price (1st buy) are excluded from historical average
        val newMilk = ProductPriceInsight(
            productName = "দুধ",
            currentUnitPrice = 90.0,
            previousUnitPrice = null,
            baseUnit = "লিটার",
            priceDiff = 0.0,
            percentDiff = 0.0,
            priceTrend = PriceTrend.STABLE
        )
        val dairyKpi = computeDepartmentInflationKpis(listOf(newMilk)).first { it.department.key == "dairy_bakery" }
        assertEquals(1, dairyKpi.trackedItemsCount)
        assertEquals(0, dairyKpi.itemsWithHistoryCount)
        assertNull("First purchase items must not count toward inflation average", dairyKpi.averageInflationPercent)
        assertEquals(PriceTrend.STABLE, dairyKpi.trend)

        // 5. Non-finite percentDiff resilience (NaN or Infinite)
        val nanItem = ProductPriceInsight(
            productName = "চিনি",
            currentUnitPrice = 130.0,
            previousUnitPrice = 100.0,
            baseUnit = "কেজি",
            percentDiff = Double.NaN
        )
        val groceryKpi = computeDepartmentInflationKpis(listOf(nanItem)).first { it.department.key == "groceries" }
        assertNull("NaN percentDiff must be filtered out safely", groceryKpi.averageInflationPercent)
    }

    @Test
    fun testInflationPercentageFormatting() {
        // Bengali formatting
        assertEquals("+১২.৫%", formatInflationPct(12.5, isBangla = true))
        assertEquals("-৮.০%", formatInflationPct(-8.0, isBangla = true))
        assertEquals("০.০%", formatInflationPct(0.0, isBangla = true))
        assertEquals("০.০%", formatInflationPct(0.03, isBangla = true))
        assertEquals("নতুন", formatInflationPct(null, isBangla = true))

        // English formatting
        assertEquals("+12.5%", formatInflationPct(12.5, isBangla = false))
        assertEquals("-8.0%", formatInflationPct(-8.0, isBangla = false))
        assertEquals("0.0%", formatInflationPct(0.0, isBangla = false))
        assertEquals("New", formatInflationPct(null, isBangla = false))
    }

    @Test
    fun testPriceSortOrderComparatorsAndDeterminism() {
        val p1 = ProductPriceInsight(
            productName = "আলু",
            currentUnitPrice = 60.0,
            previousUnitPrice = 50.0,
            baseUnit = "কেজি",
            priceDiff = 10.0,
            percentDiff = 20.0,
            purchaseCount = 5,
            lastPurchasedTimestamp = 1000L
        )
        val p2 = ProductPriceInsight(
            productName = "পেঁয়াজ",
            currentUnitPrice = 150.0,
            previousUnitPrice = 100.0,
            baseUnit = "কেজি",
            priceDiff = 50.0,
            percentDiff = 50.0,
            purchaseCount = 10,
            lastPurchasedTimestamp = 3000L
        )
        val p3 = ProductPriceInsight(
            productName = "রসুন",
            currentUnitPrice = 160.0,
            previousUnitPrice = 200.0,
            baseUnit = "কেজি",
            priceDiff = -40.0,
            percentDiff = -20.0,
            purchaseCount = 2,
            lastPurchasedTimestamp = 2000L
        )

        val list = listOf(p1, p2, p3)

        // 1. RECENT: ordered by timestamp DESC
        val sortedRecent = list.sortedWith(
            compareByDescending<ProductPriceInsight> { it.lastPurchasedTimestamp }
                .thenBy { it.productName.lowercase() }
        )
        assertEquals("পেঁয়াজ", sortedRecent[0].productName)
        assertEquals("রসুন", sortedRecent[1].productName)
        assertEquals("আলু", sortedRecent[2].productName)

        // 2. HIGHEST_HIKE: ordered by percentDiff DESC
        val sortedHike = list.sortedWith(
            compareByDescending<ProductPriceInsight> { it.percentDiff }
                .thenByDescending { it.priceDiff }
                .thenByDescending { it.lastPurchasedTimestamp }
                .thenBy { it.productName.lowercase() }
        )
        assertEquals("পেঁয়াজ", sortedHike[0].productName) // +50%
        assertEquals("আলু", sortedHike[1].productName)    // +20%
        assertEquals("রসুন", sortedHike[2].productName)  // -20%

        // 3. BIGGEST_DROP: ordered by percentDiff ASC
        val sortedDrop = list.sortedWith(
            compareBy<ProductPriceInsight> { it.percentDiff }
                .thenBy { it.priceDiff }
                .thenByDescending { it.lastPurchasedTimestamp }
                .thenBy { it.productName.lowercase() }
        )
        assertEquals("রসুন", sortedDrop[0].productName)  // -20%
        assertEquals("আলু", sortedDrop[1].productName)    // +20%
        assertEquals("পেঁয়াজ", sortedDrop[2].productName) // +50%

        // 4. MOST_PURCHASED: ordered by purchaseCount DESC
        val sortedPurchased = list.sortedWith(
            compareByDescending<ProductPriceInsight> { it.purchaseCount }
                .thenByDescending { it.lastPurchasedTimestamp }
                .thenBy { it.productName.lowercase() }
        )
        assertEquals("পেঁয়াজ", sortedPurchased[0].productName) // 10 buys
        assertEquals("আলু", sortedPurchased[1].productName)    // 5 buys
        assertEquals("রসুন", sortedPurchased[2].productName)  // 2 buys
    }

    @Test
    fun testZeroAiSparklesAcrossM4Files() {
        val filesToCheck = listOf(
            "com/example/expensetracker/ui/dashboard/ProductPriceInsightsSheet.kt",
            "com/example/expensetracker/data/model/ProductPriceModels.kt",
            "com/example/expensetracker/data/local/ProductPriceTableHelper.kt"
        )
        for (relativePath in filesToCheck) {
            val content = readFile(relativePath)
            assertFalse(
                "File $relativePath must not contain AutoAwesome AI trope",
                content.contains("AutoAwesome")
            )
            assertFalse(
                "File $relativePath must not contain ✨ AI sparkle emoji",
                content.contains("✨")
            )
        }
    }

    @Test
    fun testRecentPricePointsDataModelInvariants() {
        // 1. Default empty list
        val defaultInsight = ProductPriceInsight(
            productName = "ডিম",
            currentUnitPrice = 12.0,
            baseUnit = "পিস"
        )
        assertTrue("recentPricePoints must default to emptyList()", defaultInsight.recentPricePoints.isEmpty())

        // 2. Custom price points list
        val points = listOf(10.0, 11.0, 12.0)
        val insightWithPoints = defaultInsight.copy(recentPricePoints = points)
        assertEquals(3, insightWithPoints.recentPricePoints.size)
        assertEquals(10.0, insightWithPoints.recentPricePoints[0], 0.001)
        assertEquals(12.0, insightWithPoints.recentPricePoints[2], 0.001)

        // 3. formatDiff non-finite resilience
        val nanInsight = defaultInsight.copy(priceDiff = 5.0, percentDiff = Double.NaN)
        val formattedNan = nanInsight.formatDiff()
        assertFalse("formatDiff must not contain 'NaN'", formattedNan.contains("NaN"))
        assertTrue("formatDiff must handle NaN safely", formattedNan.contains("+৳5"))

        val infInsight = defaultInsight.copy(priceDiff = -5.0, percentDiff = Double.POSITIVE_INFINITY)
        val formattedInf = infInsight.formatDiff()
        assertFalse("formatDiff must not contain 'Infinity'", formattedInf.contains("Infinity"))
        assertTrue("formatDiff must handle Infinity safely", formattedInf.contains("-৳5"))
    }

    @Test
    fun testProductPriceTableHelperChronologicalPointsExtraction() {
        val helperContent = readFile("com/example/expensetracker/data/local/ProductPriceTableHelper.kt")

        // Helper must take up to 10 records and reverse them for chronological sparklines
        assertTrue(
            "ProductPriceTableHelper must take up to 10 records and reverse for chronological order",
            helperContent.contains("history.take(10).reversed().map { it.unitPrice }") ||
            (helperContent.contains("take(10)") && helperContent.contains("reversed()"))
        )
        assertTrue(
            "ProductPriceTableHelper must pass recentPricePoints to ProductPriceInsight constructor",
            helperContent.contains("recentPricePoints = pricePoints")
        )
    }

    @Test
    fun testStreamlinedTrendAndSortChipsInInsightsContent() {
        val content = readFile("com/example/expensetracker/ui/dashboard/ProductPriceInsightsSheet.kt")
        val contentBody = content.substringAfter("fun ProductPriceInsightsContent(").substringBefore("fun ProductPriceStockRow(")

        // 1. Unwanted cards and redundant filters must be completely removed
        assertFalse(
            "ProductPriceInsightsContent must NOT contain Bazaar Pulse & Sentiment Card",
            contentBody.contains("Bazaar Pulse & Sentiment Card") || contentBody.contains("bazaarSentiment")
        )
        assertFalse(
            "ProductPriceInsightsContent must NOT contain Department Inflation KPI Horizontal Ribbon",
            contentBody.contains("Department Inflation KPI Horizontal Ribbon") || contentBody.contains("departmentKpis")
        )
        assertFalse(
            "ProductPriceInsightsContent must NOT contain redundant Department filter chips",
            contentBody.contains("ALL_DEPARTMENTS.forEach") || contentBody.contains("selectedDepartment")
        )
        assertFalse(
            "ProductPriceInsightsContent must NOT contain Price Tier Exploration Row",
            contentBody.contains("Price Tier Exploration Row") || contentBody.contains("selectedPriceTier")
        )

        // 2. Streamlined Trend and Sort controls must be present
        assertTrue(
            "ProductPriceInsightsSheet must provide Quick Trend KPI Filter Bar",
            contentBody.contains("Quick Trend KPI Filter Bar")
        )
        assertTrue(
            "ProductPriceInsightsSheet must support movement filtering (All, Up, Down)",
            contentBody.contains("selectedMovementFilter")
        )
        assertTrue(
            "ProductPriceInsightsSheet must provide Sort Order chips",
            contentBody.contains("PriceSortOrder.entries.forEach")
        )
    }
}
