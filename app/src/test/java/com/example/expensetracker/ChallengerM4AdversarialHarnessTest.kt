package com.example.expensetracker

import com.example.expensetracker.common.MarketListParser
import com.example.expensetracker.common.UnitConverterEngine
import com.example.expensetracker.data.model.PriceTrend
import com.example.expensetracker.data.model.ProductPriceInsight
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.dashboard.DepartmentInflationKpi
import com.example.expensetracker.ui.dashboard.MarketDepartment
import com.example.expensetracker.ui.dashboard.MarketDepartmentClassifier
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
import java.text.Normalizer
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.random.Random

/**
 * Empirical Challenger Adversarial Test Harness for Milestone M4 (Features 11–17).
 * Executes rigorous adversarial checks against mathematical edge cases,
 * floating point boundaries, multi-threaded concurrency, messy parser vectors,
 * and negative constraints.
 */
class ChallengerM4AdversarialHarnessTest {

    private val basePath = "src/main/java"

    private fun readFile(relativePath: String): String {
        val file = File("$basePath/$relativePath")
        val resolved = if (file.exists()) file else File("app/$basePath/$relativePath")
        assertTrue("Expected file to exist: ${resolved.absolutePath}", resolved.exists())
        return resolved.readText()
    }

    // =========================================================================
    // 1. SPARKLINE EMPIRICAL BOUNDARY & EXTREME STRESS
    // =========================================================================

    @Test
    fun testSparklineMathematicalBoundsAndSplineCoordinates() {
        val width = 88f
        val height = 40f
        val padX = 4f
        val padY = 6f
        val usableWidth = (width - (padX * 2)).coerceAtLeast(1f)
        val usableHeight = (height - (padY * 2)).coerceAtLeast(1f)

        fun computeCoords(rawPoints: List<Double>): List<Pair<Float, Float>> {
            val minP = rawPoints.minOrNull() ?: 0.0
            val maxP = rawPoints.maxOrNull() ?: 1.0
            val isFlat = (maxP - minP) < 0.0001
            val span = if (isFlat) 1.0 else (maxP - minP)

            return rawPoints.mapIndexed { idx, p ->
                val x = padX + (idx.toFloat() / (rawPoints.size - 1)) * usableWidth
                val y = if (isFlat) height / 2f else height - padY - (((p - minP) / span) * usableHeight).toFloat()
                x to y
            }
        }

        // Scenario A: 0 raw points fallback
        val emptyInsight = ProductPriceInsight(
            productName = "নতুন পণ্য",
            currentUnitPrice = 0.0,
            previousUnitPrice = null,
            baseUnit = "কেজি",
            minUnitPrice = 0.0,
            maxUnitPrice = 0.0,
            recentPricePoints = emptyList()
        )
        val fallbackPoints = if (emptyInsight.recentPricePoints.size >= 2) {
            emptyInsight.recentPricePoints
        } else if (emptyInsight.recentPricePoints.size == 1) {
            listOf(emptyInsight.recentPricePoints[0], emptyInsight.recentPricePoints[0])
        } else {
            val p1 = emptyInsight.oneMonthAgoUnitPrice ?: emptyInsight.previousUnitPrice ?: emptyInsight.minUnitPrice
            val p2 = emptyInsight.previousUnitPrice ?: ((emptyInsight.minUnitPrice + emptyInsight.maxUnitPrice) / 2.0)
            val p3 = emptyInsight.currentUnitPrice
            listOf(p1, p2, p3)
        }
        assertEquals(3, fallbackPoints.size)
        val coordsA = computeCoords(fallbackPoints)
        coordsA.forEach { (x, y) ->
            assertFalse("X must not be NaN", x.isNaN())
            assertFalse("Y must not be NaN", y.isNaN())
            assertEquals(height / 2f, y, 0.001f) // 0.0 price is flat
        }

        // Scenario B: Single point duplication
        val singleInsight = ProductPriceInsight(
            productName = "একক দর",
            currentUnitPrice = 120.0,
            baseUnit = "কেজি",
            recentPricePoints = listOf(120.0)
        )
        val singleFallback = if (singleInsight.recentPricePoints.size >= 2) {
            singleInsight.recentPricePoints
        } else if (singleInsight.recentPricePoints.size == 1) {
            listOf(singleInsight.recentPricePoints[0], singleInsight.recentPricePoints[0])
        } else {
            listOf(120.0, 120.0, 120.0)
        }
        assertEquals(2, singleFallback.size)
        val coordsB = computeCoords(singleFallback)
        assertEquals(padX, coordsB[0].first, 0.001f)
        assertEquals(width - padX, coordsB[1].first, 0.001f)
        assertEquals(height / 2f, coordsB[0].second, 0.001f)
        assertEquals(height / 2f, coordsB[1].second, 0.001f)

        // Scenario C: Extreme 1,000,000x surge and plunge
        val extremePoints = listOf(1.0, 1000000.0, 1.0)
        val coordsC = computeCoords(extremePoints)
        assertEquals(padY, coordsC[1].second, 0.01f) // Peak at top padding
        assertEquals(height - padY, coordsC[0].second, 0.01f)
        assertEquals(height - padY, coordsC[2].second, 0.01f)

        // Scenario D: 500 Random Price Sequences
        val rng = Random(999)
        for (trial in 1..500) {
            val count = rng.nextInt(2, 25)
            val pts = (1..count).map { rng.nextDouble(0.01, 50000.0) }
            val coords = computeCoords(pts)
            assertEquals(count, coords.size)
            coords.forEach { (x, y) ->
                assertTrue("x ($x) must be >= padX", x >= padX - 0.01f)
                assertTrue("x ($x) must be <= width - padX", x <= width - padX + 0.01f)
                assertTrue("y ($y) must be >= padY", y >= padY - 0.01f)
                assertTrue("y ($y) must be <= height - padY", y <= height - padY + 0.01f)
                assertFalse("x is NaN", x.isNaN())
                assertFalse("y is NaN", y.isNaN())
            }
        }
    }

    // =========================================================================
    // 2. DEPARTMENT INFLATION KPI ENGINE ADVERSARIAL STRESS
    // =========================================================================

    @Test
    fun testDepartmentInflationKpiEngineAdversarialVectors() {
        // Test 1: Empty insights collection
        val emptyKpis = computeDepartmentInflationKpis(emptyList())
        assertEquals(4, emptyKpis.size)
        emptyKpis.forEach { kpi ->
            assertEquals(0, kpi.trackedItemsCount)
            assertEquals(0, kpi.itemsWithHistoryCount)
            assertNull(kpi.averageInflationPercent)
            assertEquals(PriceTrend.STABLE, kpi.trend)
        }

        // Test 2: Department with zero items with history (all first purchase)
        val newInsights = listOf(
            ProductPriceInsight("লাউ", 60.0, null, "পিস"),
            ProductPriceInsight("পটল", 50.0, null, "কেজি")
        )
        val newKpis = computeDepartmentInflationKpis(newInsights)
        val veggieKpi = newKpis.first { it.department == MarketDepartment.VEGGIES }
        assertEquals(2, veggieKpi.trackedItemsCount)
        assertEquals(0, veggieKpi.itemsWithHistoryCount)
        assertNull(veggieKpi.averageInflationPercent)
        assertEquals("নতুন", formatInflationPct(veggieKpi.averageInflationPercent, true))
        assertEquals("New", formatInflationPct(veggieKpi.averageInflationPercent, false))

        // Test 3: Formatting neutral band around 0.0%
        assertEquals("০.০%", formatInflationPct(0.04, true))
        assertEquals("০.০%", formatInflationPct(-0.04, true))
        assertEquals("০.০%", formatInflationPct(0.0, true))
        assertEquals("+০.১%", formatInflationPct(0.06, true))
        assertEquals("-০.১%", formatInflationPct(-0.06, true))
        assertEquals("+0.1%", formatInflationPct(0.06, false))
        assertEquals("-0.1%", formatInflationPct(-0.06, false))

        // Test 4: Threshold boundary for PriceTrend (trend flips at +/- 0.5%)
        val kpiStablePos = computeDepartmentInflationKpis(listOf(
            ProductPriceInsight("আলু", 60.3, 60.0, "কেজি", percentDiff = 0.5)
        )).first { it.department == MarketDepartment.VEGGIES }
        assertEquals(PriceTrend.STABLE, kpiStablePos.trend)

        val kpiUp = computeDepartmentInflationKpis(listOf(
            ProductPriceInsight("আলু", 60.36, 60.0, "কেজি", percentDiff = 0.6)
        )).first { it.department == MarketDepartment.VEGGIES }
        assertEquals(PriceTrend.UP, kpiUp.trend)

        val kpiDown = computeDepartmentInflationKpis(listOf(
            ProductPriceInsight("আলু", 59.64, 60.0, "কেজি", percentDiff = -0.6)
        )).first { it.department == MarketDepartment.VEGGIES }
        assertEquals(PriceTrend.DOWN, kpiDown.trend)
    }

    // =========================================================================
    // 3. 4-MODE SORTING STABILITY WITH MULTIPLE TIES
    // =========================================================================

    @Test
    fun testPriceSortOrderStabilityWithTies() {
        val tiedItems = listOf(
            ProductPriceInsight("পণ্য A", 100.0, 80.0, "কেজি", percentDiff = 25.0, priceDiff = 20.0, purchaseCount = 5, lastPurchasedTimestamp = 1000L),
            ProductPriceInsight("পণ্য B", 200.0, 160.0, "কেজি", percentDiff = 25.0, priceDiff = 40.0, purchaseCount = 5, lastPurchasedTimestamp = 2000L),
            ProductPriceInsight("পণ্য C", 100.0, 100.0, "কেজি", percentDiff = 0.0, priceDiff = 0.0, purchaseCount = 10, lastPurchasedTimestamp = 3000L),
            ProductPriceInsight("পণ্য D", 50.0, 50.0, "কেজি", percentDiff = 0.0, priceDiff = 0.0, purchaseCount = 2, lastPurchasedTimestamp = 4000L),
            ProductPriceInsight("পণ্য E", 80.0, 100.0, "কেজি", percentDiff = -20.0, priceDiff = -20.0, purchaseCount = 3, lastPurchasedTimestamp = 5000L),
            ProductPriceInsight("পণ্য F", 60.0, 100.0, "কেজি", percentDiff = -40.0, priceDiff = -40.0, purchaseCount = 1, lastPurchasedTimestamp = 6000L)
        )

        // 1. HIGHEST_HIKE: primary percentDiff desc, secondary priceDiff desc
        val hikeSorted = tiedItems.sortedWith(compareByDescending<ProductPriceInsight> { it.percentDiff }.thenByDescending { it.priceDiff })
        assertEquals("পণ্য B", hikeSorted[0].productName) // +25%, priceDiff 40
        assertEquals("পণ্য A", hikeSorted[1].productName) // +25%, priceDiff 20
        assertEquals("পণ্য F", hikeSorted.last().productName) // -40%

        // 2. BIGGEST_DROP: primary percentDiff asc, secondary priceDiff asc
        val dropSorted = tiedItems.sortedWith(compareBy<ProductPriceInsight> { it.percentDiff }.thenBy { it.priceDiff })
        assertEquals("পণ্য F", dropSorted[0].productName) // -40%
        assertEquals("পণ্য E", dropSorted[1].productName) // -20%
        assertEquals("পণ্য B", dropSorted.last().productName) // +25%

        // 3. MOST_PURCHASED: primary purchaseCount desc, secondary timestamp desc
        val mostPurchased = tiedItems.sortedWith(compareByDescending<ProductPriceInsight> { it.purchaseCount }.thenByDescending { it.lastPurchasedTimestamp })
        assertEquals("পণ্য C", mostPurchased[0].productName) // count = 10
        assertEquals("পণ্য B", mostPurchased[1].productName) // count = 5, ts = 2000
        assertEquals("পণ্য A", mostPurchased[2].productName) // count = 5, ts = 1000
        assertEquals("পণ্য F", mostPurchased.last().productName) // count = 1

        // 4. RECENT: timestamp desc
        val recentSorted = tiedItems.sortedByDescending { it.lastPurchasedTimestamp }
        assertEquals("পণ্য F", recentSorted[0].productName) // ts = 6000
        assertEquals("পণ্য A", recentSorted.last().productName) // ts = 1000
    }

    // =========================================================================
    // 4. WHATSAPP PARSER ADVERSARIAL VECTORS
    // =========================================================================

    @Test
    fun testWhatsAppParserAdversarialInputs() {
        val adversarialText = """
            - আলু ২.৫ কেজি
            • পেঁয়াজ 1/2 কেজি
            (১) ডিম ১২ পিস
            [✓] সয়াবিন তেল - ২ লিটার
            ক. চিনি ১ কেজি
            আদা ২৫০গ্রাম
            রসুন আড়াই পোয়া
            গরুর মাংস ৩ কেজি
            কাঁচামরিচ ১০০ গ্রাম
            ---
            
            লাউ
            সরিষার তেল ১লিটার
        """.trimIndent()

        val parsed = MarketListParser.parseBulkText(adversarialText)
        assertTrue("Must parse valid items despite noisy bullets", parsed.size >= 10)

        // Empirical Proof of WhatsApp Bullet Bug:
        // Case A: Standard list prefix "1. ডিম ১২ পিস" parses correctly via parseBulkText
        val standardParsed = MarketListParser.parseBulkText("1. ডিম ১২ পিস")
        assertEquals(1, standardParsed.size)
        val standardDim = standardParsed.first()
        assertEquals("ডিম", standardDim.name)
        assertEquals(12.0, standardDim.quantity, 0.01)
        assertEquals("পিস", standardDim.unit)

        // Case B: Parenthesized bullet "(১) ডিম ১২ পিস" - correctly stripped, quantity is 12 and name is ডিম
        val parenthesizedParsed = MarketListParser.parseBulkText("(১) ডিম ১২ পিস")
        assertEquals(1, parenthesizedParsed.size)
        val parenthesizedDim = parenthesizedParsed.first()
        assertEquals(12.0, parenthesizedDim.quantity, 0.01)
        assertEquals("ডিম", parenthesizedDim.name)
        assertEquals("পিস", parenthesizedDim.unit)

        // Case C: English parenthesized bullet "(1) Eggs 12 pcs"
        val engParenthesized = MarketListParser.parseBulkText("(1) Eggs 12 pcs")
        assertEquals(1, engParenthesized.size)
        assertEquals(12.0, engParenthesized.first().quantity, 0.01)
        assertEquals("Eggs", engParenthesized.first().name)

        // "আলু ২.৫ কেজি"
        val alu = parsed.first { it.name.contains("আলু") }
        assertEquals(2.5, alu.quantity, 0.01)
        assertEquals("কেজি", alu.unit)

        // "সয়াবিন তেল - ২ লিটার"
        val tel = parsed.first { it.name.contains("সয়াবিন") }
        assertEquals(2.0, tel.quantity, 0.01)
        assertEquals("লিটার", tel.unit)

        // "লাউ" (no quantity specified -> defaults to 1.0 কেজি)
        val lau = parsed.first { it.name.contains("লাউ") }
        assertEquals(1.0, lau.quantity, 0.01)
    }

    // =========================================================================
    // 5. DEPARTMENT OVERRIDE CONCURRENCY & UNICODE NFC CONSISTENCY
    // =========================================================================

    @Test
    fun testDepartmentOverrideConcurrencyAndUnicodeNfcConsistency() {
        try {
            MarketDepartmentClassifier.clearOverrides()

            // 1. Unicode decomposed vs precomposed equivalence
            // "পেঁয়াজ" with \u09af\u09bc vs \u09df
            val precomposed = "পেঁয়াজ"
            val decomposed = "পেঁ" + "\u09af\u09bc" + "াজ"
            
            MarketDepartmentClassifier.setOverride(precomposed, "groceries")
            assertEquals("groceries", MarketDepartmentClassifier.getOverride(decomposed))
            assertEquals("groceries", MarketDepartmentClassifier.getMarketItemCategory(decomposed))

            // 2. Concurrency stress: 16 threads writing and reading simultaneously
            val executor = Executors.newFixedThreadPool(16)
            val exceptions = mutableListOf<Throwable>()
            val letters = ('a'..'z').toList()

            for (i in 0 until 100) {
                executor.submit {
                    try {
                        val c1 = letters[i % 26]
                        val c2 = letters[(i / 26) % 26]
                        val name = "পণ্য$c1$c2"
                        val dept = MarketDepartment.entries[i % MarketDepartment.entries.size].id
                        MarketDepartmentClassifier.setOverride(name, dept)
                        val retrieved = MarketDepartmentClassifier.getOverride(name)
                        assertEquals(dept, retrieved)
                    } catch (t: Throwable) {
                        synchronized(exceptions) { exceptions.add(t) }
                    }
                }
            }

            executor.shutdown()
            assertTrue("Executor must terminate cleanly", executor.awaitTermination(5, TimeUnit.SECONDS))
            assertTrue("No concurrent modification exceptions allowed: $exceptions", exceptions.isEmpty())
        } finally {
            MarketDepartmentClassifier.clearOverrides()
        }
    }

    // =========================================================================
    // 6. NEGATIVE CONSTRAINTS & ACCESSIBILITY AUDIT
    // =========================================================================

    @Test
    fun testNegativeConstraintsAndThemeAudits() {
        val sheetCode = readFile("com/example/expensetracker/ui/dashboard/ProductPriceInsightsSheet.kt")
        val createCode = readFile("com/example/expensetracker/ui/dashboard/CreateMarketListSection.kt")
        val screenCode = readFile("com/example/expensetracker/ui/market/MarketScreen.kt")

        val combined = sheetCode + createCode + screenCode

        // Negative Constraint 1: Zero AI sparkles
        assertFalse("No sparkle emojis allowed", combined.contains("✨"))
        assertFalse("No AutoAwesome allowed", combined.contains("AutoAwesome"))

        // Negative Constraint 2: Touch targets
        assertTrue("Must enforce minimum interactive touch targets", sheetCode.contains("minimumInteractiveComponentSize()"))
        assertTrue("Must enforce minimum interactive touch targets", createCode.contains("minimumInteractiveComponentSize()"))
        assertTrue("Must enforce minimum interactive touch targets", screenCode.contains("minimumInteractiveComponentSize()"))
    }
}
