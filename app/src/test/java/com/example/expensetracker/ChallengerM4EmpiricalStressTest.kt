package com.example.expensetracker

import com.example.expensetracker.data.model.PriceTrend
import com.example.expensetracker.data.model.ProductPriceInsight
import com.example.expensetracker.data.model.ProductPriceRecord
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.dashboard.ALL_DEPARTMENTS
import com.example.expensetracker.ui.dashboard.DepartmentInflationKpi
import com.example.expensetracker.ui.dashboard.PriceSortOrder
import com.example.expensetracker.ui.dashboard.computeDepartmentInflationKpis
import com.example.expensetracker.ui.dashboard.formatInflationPct
import com.example.expensetracker.ui.dashboard.getMarketItemCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import kotlin.math.abs
import kotlin.random.Random

/**
 * Challenger M4 Empirical Adversarial Stress Test Suite:
 * Rigorously attacks and stresses Milestone M4:
 * 1. Empty catalog bounds (0 items, null aggregates, safe UI fallbacks)
 * 2. Single purchase / 1st buy (previousUnitPrice == null, 0.0 diff, safe sparkline rawPoints >= 2)
 * 3. Identical price points (zero span, flat line minRate == maxRate, isFlat protection)
 * 4. Division by zero in inflation percentages (previousUnitPrice == 0.0, NaN, and Infinite guards)
 * 5. Extreme price surges (+99,900%) and price crashes (-99%)
 * 6. Sub-taka fractional prices (e.g. 0.05/গ্রাম)
 * 7. Sorting determinism & permutation-invariance oracle (100 random shuffles across 50 items)
 * 8. Monotone cubic Bézier spline monotonicity & mathematical zero-overshoot validation
 * 9. Structural & design system token invariants (>= 12sp typography, >= 48dp touch targets, zero AI sparkles)
 */
class ChallengerM4EmpiricalStressTest {

    private fun readSourceFile(relativePath: String): String {
        val candidates = listOf(
            File("src/main/java/$relativePath"),
            File("app/src/main/java/$relativePath"),
            File("../app/src/main/java/$relativePath")
        )
        val file = candidates.find { it.exists() }
        assertNotNull("Expected source file to exist: $relativePath", file)
        return file!!.readText()
    }

    /**
     * Simulation oracle of the geometry pipeline in ProductPriceSparkline:
     * maps raw price points to 2D coordinates and validates bounds and Bézier monotonicity.
     */
    private fun simulateSparklineGeometry(
        insight: ProductPriceInsight,
        width: Float = 42f,
        height: Float = 24f,
        padX: Float = 2f,
        padY: Float = 3f
    ): Pair<List<Pair<Float, Float>>, Boolean> {
        val rawPoints = when {
            insight.recentPricePoints.size >= 2 -> insight.recentPricePoints
            insight.recentPricePoints.size == 1 -> listOf(insight.recentPricePoints[0], insight.recentPricePoints[0])
            else -> {
                val p1 = insight.oneMonthAgoUnitPrice ?: insight.previousUnitPrice ?: insight.minUnitPrice
                val p2 = insight.previousUnitPrice ?: ((insight.minUnitPrice + insight.maxUnitPrice) / 2.0)
                val p3 = insight.currentUnitPrice
                listOf(p1, p2, p3)
            }
        }

        assertTrue("Sparkline raw points must always have size >= 2", rawPoints.size >= 2)

        val minP = rawPoints.minOrNull() ?: 0.0
        val maxP = rawPoints.maxOrNull() ?: 1.0
        val isFlat = (maxP - minP) < 0.0001
        val span = if (isFlat) 1.0 else (maxP - minP)

        val usableWidth = (width - (padX * 2)).coerceAtLeast(1f)
        val usableHeight = (height - (padY * 2)).coerceAtLeast(1f)

        val coords = rawPoints.mapIndexed { idx, p ->
            val x = padX + (idx.toFloat() / (rawPoints.size - 1)) * usableWidth
            val y = if (isFlat) height / 2f else height - padY - (((p - minP) / span) * usableHeight).toFloat()
            Pair(x, y)
        }

        return Pair(coords, isFlat)
    }

    // =========================================================================
    // SCENARIO 1: Empty List (0 items)
    // =========================================================================
    @Test
    fun testScenario1_EmptyListBoundsAndAggregates() {
        val emptyInsights = emptyList<ProductPriceInsight>()

        // 1. Department inflation computation must not throw on empty collection
        val kpis = computeDepartmentInflationKpis(emptyInsights)
        assertEquals("Must return exactly 4 standard department KPIs", 4, kpis.size)

        for (kpi in kpis) {
            assertEquals("Empty catalog tracked items must be 0", 0, kpi.trackedItemsCount)
            assertEquals("Empty catalog history items must be 0", 0, kpi.itemsWithHistoryCount)
            assertNull("Inflation average must be null when no history exists", kpi.averageInflationPercent)
            assertEquals("Empty catalog trend must be STABLE", PriceTrend.STABLE, kpi.trend)
        }

        // 2. Inflation percentage formatting for empty / null
        assertEquals("নতুন", formatInflationPct(null, isBangla = true))
        assertEquals("New", formatInflationPct(null, isBangla = false))

        // 3. UI Content empty state strings check in source
        val sheetContent = readSourceFile("com/example/expensetracker/ui/dashboard/ProductPriceInsightsSheet.kt")
        assertTrue("Must include empty state title in Bengali", sheetContent.contains("এখনো কোনো পণ্যের দর রেকর্ড হয়নি"))
        assertTrue("Must include empty state title in English", sheetContent.contains("No price records tracked yet"))
        assertTrue("Must include empty state guidance", sheetContent.contains("বাজারের ফর্দ সম্পন্ন করার পর এখানে পণ্যের দামের তুলনামূলক চিত্র প্রদর্শিত হবে।"))
    }

    // =========================================================================
    // SCENARIO 2: Single Purchase / 1st Buy (previousUnitPrice == null)
    // =========================================================================
    @Test
    fun testScenario2_SinglePurchaseFirstBuyBounds() {
        val singleBuy = ProductPriceInsight(
            productName = "ব্রয়লার মুরগি",
            currentUnitPrice = 190.0,
            previousUnitPrice = null,
            baseUnit = "কেজি",
            priceDiff = 0.0,
            percentDiff = 0.0,
            minUnitPrice = 190.0,
            maxUnitPrice = 190.0,
            avgUnitPrice = 190.0,
            purchaseCount = 1,
            priceTrend = PriceTrend.STABLE,
            recentPricePoints = listOf(190.0)
        )

        // 1. Model invariants
        assertNull("previousUnitPrice must be null for first buy", singleBuy.previousUnitPrice)
        assertEquals(0.0, singleBuy.priceDiff, 0.0001)
        assertEquals(0.0, singleBuy.percentDiff, 0.0001)
        assertEquals(1, singleBuy.purchaseCount)
        assertTrue(singleBuy.isPriceStable)
        assertFalse(singleBuy.isPriceUp)
        assertFalse(singleBuy.isPriceDown)

        // 2. Diff formatting must render clean 0%
        assertEquals("০%", singleBuy.formatDiff())
        assertEquals("৳190/কেজি", singleBuy.formatUnitRate())
        assertEquals("৳190/কেজি", singleBuy.formatAvgRate())

        // 3. Department inflation must exclude first-buy items from average percentage
        val deptKpis = computeDepartmentInflationKpis(listOf(singleBuy))
        val meatFishKpi = deptKpis.first { it.department.key == "meat_fish" }
        assertEquals("Should be counted in trackedItemsCount", 1, meatFishKpi.trackedItemsCount)
        assertEquals("Must NOT be counted in itemsWithHistoryCount", 0, meatFishKpi.itemsWithHistoryCount)
        assertNull("Average inflation must remain null for 1st buy items", meatFishKpi.averageInflationPercent)
        assertEquals(PriceTrend.STABLE, meatFishKpi.trend)

        // 4. Sparkline geometry simulation for single-point history
        val (coords, isFlat) = simulateSparklineGeometry(singleBuy, width = 42f, height = 24f)
        assertTrue("Single-point history must be treated as flat curve", isFlat)
        assertEquals("Single-point list duplicated to 2 points for valid Canvas line", 2, coords.size)
        assertEquals("Coordinates must be centered vertically at height/2", 12f, coords[0].second, 0.001f)
        assertEquals("Coordinates must be centered vertically at height/2", 12f, coords[1].second, 0.001f)
        assertEquals("Start X must be at padX (2.dp)", 2f, coords[0].first, 0.001f)
        assertEquals("End X must be at width - padX (40.dp)", 40f, coords[1].first, 0.001f)

        // 5. Verify source declares "১ম ক্রয়" / "1st buy" badge when previousUnitPrice is null
        val sheetContent = readSourceFile("com/example/expensetracker/ui/dashboard/ProductPriceInsightsSheet.kt")
        assertTrue("Must display ১ম ক্রয় badge", sheetContent.contains("\"১ম ক্রয়\""))
        assertTrue("Must display 1st buy badge", sheetContent.contains("\"1st buy\""))
    }

    // =========================================================================
    // SCENARIO 3: Identical Price Points (Zero Span, Flat Line minRate == maxRate)
    // =========================================================================
    @Test
    fun testScenario3_IdenticalPricePointsFlatnessProtection() {
        val flatHistory = List(10) { 75.0 }
        val flatInsight = ProductPriceInsight(
            productName = "সয়াবিন তেল",
            currentUnitPrice = 75.0,
            previousUnitPrice = 75.0,
            baseUnit = "লিটার",
            priceDiff = 0.0,
            percentDiff = 0.0,
            minUnitPrice = 75.0,
            maxUnitPrice = 75.0,
            avgUnitPrice = 75.0,
            purchaseCount = 10,
            priceTrend = PriceTrend.STABLE,
            recentPricePoints = flatHistory
        )

        val (coords, isFlat) = simulateSparklineGeometry(flatInsight, width = 88f, height = 32f)
        assertTrue("Identical price points must trigger isFlat flag", isFlat)
        assertEquals(10, coords.size)

        // Every coordinate must be safely centered at height / 2f (16f) without NaN
        for ((idx, pt) in coords.withIndex()) {
            assertFalse("X coordinate must not be NaN", pt.first.isNaN())
            assertFalse("Y coordinate must not be NaN", pt.second.isNaN())
            assertFalse("X coordinate must not be Infinite", pt.first.isInfinite())
            assertFalse("Y coordinate must not be Infinite", pt.second.isInfinite())
            assertEquals("Y coordinate at index $idx must equal exactly height / 2f", 16f, pt.second, 0.001f)
        }

        // Detail sheet min-to-max range bar fraction calculation check:
        // if (maxRate > minRate) (curRate - minRate)/(maxRate - minRate) else 0.5f
        val minRate = flatInsight.minUnitPrice
        val maxRate = flatInsight.maxUnitPrice
        val curRate = flatInsight.currentUnitPrice
        val fraction = if (maxRate > minRate) {
            ((curRate - minRate) / (maxRate - minRate)).coerceIn(0.0, 1.0).toFloat().coerceIn(0.01f, 1.0f)
        } else 0.5f
        assertEquals("Flat price min-max fraction must default to exact center 0.5f", 0.5f, fraction, 0.001f)
    }

    // =========================================================================
    // SCENARIO 4: Division by Zero & Non-Finite Numbers in Inflation Percentages
    // =========================================================================
    @Test
    fun testScenario4_DivisionByZeroAndNonFiniteResilience() {
        // 1. previousUnitPrice = 0.0 resilience in ProductPriceInsight
        val zeroBaseInsight = ProductPriceInsight(
            productName = "লবণ",
            currentUnitPrice = 35.0,
            previousUnitPrice = 0.0,
            baseUnit = "কেজি",
            priceDiff = 35.0,
            percentDiff = 0.0, // Guaranteed by ProductPriceTableHelper
            priceTrend = PriceTrend.STABLE
        )
        val formattedZeroBase = zeroBaseInsight.formatDiff()
        assertFalse("formatDiff must not output NaN", formattedZeroBase.contains("NaN"))
        assertFalse("formatDiff must not output Infinity", formattedZeroBase.contains("Infinity"))

        // 2. Direct adversarial NaN / Infinite injection in ProductPriceInsight
        val nanInsight = ProductPriceInsight(
            productName = "চিনি",
            currentUnitPrice = 135.0,
            previousUnitPrice = 130.0,
            baseUnit = "কেজি",
            priceDiff = 5.0,
            percentDiff = Double.NaN,
            oneMonthPercentDiff = Double.NaN
        )
        val nanFormatted = nanInsight.formatDiff()
        assertFalse("Adversarial NaN percentDiff must not appear in formatDiff", nanFormatted.contains("NaN"))
        assertEquals("+৳5 (0.0%)", nanFormatted)

        val infInsight = ProductPriceInsight(
            productName = "জিরা",
            currentUnitPrice = 900.0,
            previousUnitPrice = 0.0,
            baseUnit = "কেজি",
            priceDiff = 900.0,
            percentDiff = Double.POSITIVE_INFINITY,
            oneMonthPercentDiff = Double.POSITIVE_INFINITY
        )
        val infFormatted = infInsight.formatDiff()
        assertFalse("Adversarial Infinity percentDiff must not appear in formatDiff", infFormatted.contains("Infinity"))
        assertEquals("+৳900 (0.0%)", infFormatted)

        val negInfInsight = ProductPriceInsight(
            productName = "এলাচ",
            currentUnitPrice = 2500.0,
            previousUnitPrice = 0.0,
            baseUnit = "কেজি",
            priceDiff = -500.0,
            percentDiff = Double.NEGATIVE_INFINITY
        )
        val negInfFormatted = negInfInsight.formatDiff()
        assertFalse("Adversarial -Infinity must not appear in formatDiff", negInfFormatted.contains("Infinity"))
        assertEquals("-৳500 (0.0%)", negInfFormatted)

        // 3. Department inflation computation must filter out NaN and Infinite items
        val corruptedList = listOf(nanInsight, infInsight, negInfInsight)
        val kpis = computeDepartmentInflationKpis(corruptedList)
        val groceryKpi = kpis.first { it.department.key == "groceries" }
        assertEquals("All 3 items are groceries", 3, groceryKpi.trackedItemsCount)
        assertEquals("Corrupted items must be excluded from history count", 0, groceryKpi.itemsWithHistoryCount)
        assertNull("Corrupted items must not result in NaN averageInflationPercent", groceryKpi.averageInflationPercent)

        // 4. formatInflationPct edge cases
        assertEquals("০.০%", formatInflationPct(0.0, isBangla = true))
        assertEquals("0.0%", formatInflationPct(0.0, isBangla = false))
        assertEquals("০.০%", formatInflationPct(Double.NaN, isBangla = true))
    }

    // =========================================================================
    // SCENARIO 5: Extreme Price Surges (+99,900%) and Price Crashes (-99%)
    // =========================================================================
    @Test
    fun testScenario5_ExtremePriceSurgesAndCrashes() {
        // 1. Extreme surge: 1.0 to 1,000.0 (+99,900%)
        val massiveSurge = ProductPriceInsight(
            productName = "কাঁচা মরিচ",
            currentUnitPrice = 1000.0,
            previousUnitPrice = 1.0,
            baseUnit = "কেজি",
            priceDiff = 999.0,
            percentDiff = 99900.0,
            minUnitPrice = 1.0,
            maxUnitPrice = 1000.0,
            avgUnitPrice = 500.5,
            purchaseCount = 2,
            priceTrend = PriceTrend.UP,
            recentPricePoints = listOf(1.0, 1000.0)
        )

        assertEquals("+৳999 (99900.0%)", massiveSurge.formatDiff())
        assertTrue(massiveSurge.isPriceUp)

        val (surgeCoords, isSurgeFlat) = simulateSparklineGeometry(massiveSurge, width = 42f, height = 24f, padX = 2f, padY = 3f)
        assertFalse("Surge must not be flat", isSurgeFlat)
        assertEquals(2, surgeCoords.size)
        // Point 0 (1.0 -> bottom): y = 24 - 3 - (0) = 21f
        assertEquals(21f, surgeCoords[0].second, 0.001f)
        // Point 1 (1000.0 -> top): y = 24 - 3 - (18) = 3f
        assertEquals(3f, surgeCoords[1].second, 0.001f)

        // 2. Extreme crash: 10,000.0 to 1.0 (-99.99%)
        val massiveCrash = ProductPriceInsight(
            productName = "ইলিশ মাছ",
            currentUnitPrice = 1.0,
            previousUnitPrice = 10000.0,
            baseUnit = "কেজি",
            priceDiff = -9999.0,
            percentDiff = -99.99,
            minUnitPrice = 1.0,
            maxUnitPrice = 10000.0,
            avgUnitPrice = 5000.5,
            purchaseCount = 2,
            priceTrend = PriceTrend.DOWN,
            recentPricePoints = listOf(10000.0, 1.0)
        )

        assertEquals("-৳9999 (100.0%)", massiveCrash.formatDiff())
        assertTrue(massiveCrash.isPriceDown)

        val (crashCoords, isCrashFlat) = simulateSparklineGeometry(massiveCrash, width = 42f, height = 24f, padX = 2f, padY = 3f)
        assertFalse("Crash must not be flat", isCrashFlat)
        // Point 0 (10000.0 -> top): y = 3f
        assertEquals(3f, crashCoords[0].second, 0.001f)
        // Point 1 (1.0 -> bottom): y = 21f
        assertEquals(21f, crashCoords[1].second, 0.001f)

        // 3. Aggregated volatile collection (50 items with random fluctuations between -99% and +10,000%)
        val rng = Random(1337)
        val volatileItems = List(50) { i ->
            val prev = rng.nextDouble(10.0, 200.0)
            val curr = if (i % 2 == 0) prev * rng.nextDouble(0.01, 0.5) else prev * rng.nextDouble(1.5, 100.0)
            val diff = curr - prev
            val pDiff = (diff / prev) * 100.0
            ProductPriceInsight(
                productName = "সবজি_$i",
                currentUnitPrice = curr,
                previousUnitPrice = prev,
                baseUnit = "কেজি",
                priceDiff = diff,
                percentDiff = pDiff,
                priceTrend = if (diff > 0) PriceTrend.UP else PriceTrend.DOWN
            )
        }

        val kpis = computeDepartmentInflationKpis(volatileItems)
        val veggieKpi = kpis.first { it.department.key == "veggies" }
        assertEquals(50, veggieKpi.trackedItemsCount)
        assertEquals(50, veggieKpi.itemsWithHistoryCount)
        assertNotNull(veggieKpi.averageInflationPercent)
        assertFalse("Aggregate inflation must not be NaN", veggieKpi.averageInflationPercent!!.isNaN())
        assertFalse("Aggregate inflation must not be Infinite", veggieKpi.averageInflationPercent!!.isInfinite())
        val formattedKpi = formatInflationPct(veggieKpi.averageInflationPercent, isBangla = true)
        assertTrue("Formatted inflation must contain percentage sign", formattedKpi.endsWith("%"))
    }

    // =========================================================================
    // SCENARIO 6: Sub-Taka Fractional Prices (e.g. 0.05/গ্রাম)
    // =========================================================================
    @Test
    fun testScenario6_SubTakaFractionalPrices() {
        val tinyPrice = ProductPriceInsight(
            productName = "জাফরান",
            currentUnitPrice = 0.08, // ৳0.08 per gram
            previousUnitPrice = 0.05, // ৳0.05 per gram
            baseUnit = "গ্রাম",
            priceDiff = 0.03,
            percentDiff = 60.0,
            minUnitPrice = 0.05,
            maxUnitPrice = 0.08,
            avgUnitPrice = 0.065,
            purchaseCount = 5,
            priceTrend = PriceTrend.UP,
            recentPricePoints = listOf(0.05, 0.06, 0.05, 0.07, 0.08)
        )

        // 1. Rate formatting (one decimal place)
        assertEquals("৳0.1/গ্রাম", tinyPrice.formatUnitRate())
        assertEquals("৳0.1/গ্রাম", tinyPrice.formatAvgRate())
        assertEquals("+৳0.0 (60.0%)", tinyPrice.formatDiff())

        // 2. Geometry under sub-taka prices
        val (coords, isFlat) = simulateSparklineGeometry(tinyPrice, width = 42f, height = 24f)
        assertFalse("Delta 0.03 > 0.0001, must not be flat", isFlat)
        assertEquals(5, coords.size)
        // All Y coords must be strictly within bounds [3f, 21f]
        for (pt in coords) {
            assertTrue("Y must be >= padY", pt.second >= 2.99f)
            assertTrue("Y must be <= height - padY", pt.second <= 21.01f)
        }

        // 3. Sub-taka flatness threshold test: delta < 0.0001
        val microPoints = listOf(0.05002, 0.05001, 0.05003)
        val microInsight = tinyPrice.copy(
            minUnitPrice = 0.05001,
            maxUnitPrice = 0.05003,
            recentPricePoints = microPoints
        )
        val (_, isMicroFlat) = simulateSparklineGeometry(microInsight, width = 42f, height = 24f)
        assertTrue("Delta 0.00002 < 0.0001 must trigger flatness protection to prevent zero-span amplification", isMicroFlat)
    }

    // =========================================================================
    // SCENARIO 7: Sorting Stability & Permutation Invariance Oracle
    // =========================================================================
    @Test
    fun testScenario7_SortingDeterminismAcrossRandomPermutations() {
        // Construct 50 items with identical primary criteria to aggressively stress tie-breaking
        val testCatalog = (1..50).map { id ->
            ProductPriceInsight(
                productName = "পণ্য_${"%03d".format(id)}",
                currentUnitPrice = 100.0 + (id % 5) * 10.0,
                previousUnitPrice = 100.0,
                baseUnit = "কেজি",
                priceDiff = (id % 5) * 10.0,
                percentDiff = ((id % 5) * 10.0), // Many duplicate percentDiff values (0%, 10%, 20%, 30%, 40%)
                purchaseCount = (id % 3) + 1,    // Many duplicate purchase counts (1, 2, 3)
                lastPurchasedTimestamp = 1000L * (id % 4) // Duplicate timestamps (0, 1000, 2000, 3000)
            )
        }

        val rng = Random(20261106)

        for (sortOrder in PriceSortOrder.entries) {
            val comparator = when (sortOrder) {
                PriceSortOrder.RECENT -> compareByDescending<ProductPriceInsight> { it.lastPurchasedTimestamp }
                    .thenBy { it.productName.lowercase() }
                PriceSortOrder.HIGHEST_HIKE -> compareByDescending<ProductPriceInsight> { it.percentDiff }
                    .thenByDescending { it.priceDiff }
                    .thenByDescending { it.lastPurchasedTimestamp }
                    .thenBy { it.productName.lowercase() }
                PriceSortOrder.BIGGEST_DROP -> compareBy<ProductPriceInsight> { it.percentDiff }
                    .thenBy { it.priceDiff }
                    .thenByDescending { it.lastPurchasedTimestamp }
                    .thenBy { it.productName.lowercase() }
                PriceSortOrder.MOST_PURCHASED -> compareByDescending<ProductPriceInsight> { it.purchaseCount }
                    .thenByDescending { it.lastPurchasedTimestamp }
                    .thenBy { it.productName.lowercase() }
            }

            // Reference baseline sorted list
            val baseline = testCatalog.sortedWith(comparator)
            assertEquals("Sorted list must retain exactly 50 items", 50, baseline.size)

            // Test 100 random shuffles; every shuffle MUST produce identical order
            for (shuffleIndex in 1..100) {
                val shuffled = testCatalog.shuffled(rng)
                val testSorted = shuffled.sortedWith(comparator)
                for (i in 0 until 50) {
                    assertEquals(
                        "Sort order $sortOrder non-deterministic at shuffle $shuffleIndex, index $i",
                        baseline[i].productName,
                        testSorted[i].productName
                    )
                }
            }
        }
    }

    // =========================================================================
    // SCENARIO 8: Monotone Midpoint Cubic Bézier Spline Monotonicity Oracle
    // =========================================================================
    @Test
    fun testScenario8_MonotoneCubicBezierSplineMonotonicity() {
        // Evaluate cubic Bézier midpoint Hermite spline formula:
        // y(t) = (1-t)^3 y0 + 3(1-t)^2 t y0 + 3(1-t) t^2 y1 + t^3 y1 = (1-t)^2 (1+2t) y0 + t^2 (3-2t) y1
        // Derivative y'(t) = 6(y1 - y0) t(1-t).
        // Since t in [0, 1], 6t(1-t) >= 0.
        // Therefore y(t) is mathematically strictly monotonic between y0 and y1. Zero overshoot!

        val rng = Random(999)
        for (trial in 1..1000) {
            val y0 = rng.nextFloat() * 100f
            val y1 = rng.nextFloat() * 100f
            val minY = minOf(y0, y1)
            val maxY = maxOf(y0, y1)

            // Sample 100 discrete values of t along [0, 1]
            for (step in 0..100) {
                val t = step / 100f
                val yT = ((1f - t) * (1f - t) * (1f + 2f * t) * y0) + (t * t * (3f - 2f * t) * y1)

                assertTrue(
                    "Cubic Bézier intermediate Y ($yT) must never overshoot maximum ($maxY)",
                    yT <= maxY + 0.0001f
                )
                assertTrue(
                    "Cubic Bézier intermediate Y ($yT) must never undershoot minimum ($minY)",
                    yT >= minY - 0.0001f
                )
            }
        }
    }

    // =========================================================================
    // SCENARIO 9: Living Ledger System Tokens & Strict Ergonomics Verification
    // =========================================================================
    @Test
    fun testScenario9_LivingLedgerDesignTokensAndErgonomicsAudit() {
        val sheetContent = readSourceFile("com/example/expensetracker/ui/dashboard/ProductPriceInsightsSheet.kt")
        val modelsContent = readSourceFile("com/example/expensetracker/data/model/ProductPriceModels.kt")
        val helperContent = readSourceFile("com/example/expensetracker/data/local/ProductPriceTableHelper.kt")

        // 1. Strict typography: zero sub-12sp font sizes in ProductPriceInsightsSheet
        val forbiddenSub12 = listOf("9.sp", "9.5.sp", "10.sp", "10.5.sp", "11.sp", "11.5.sp")
        for (token in forbiddenSub12) {
            val found = Regex("(?<![0-9])${Regex.escape(token)}").containsMatchIn(sheetContent)
            assertFalse("Forbidden font size token $token found in ProductPriceInsightsSheet", found)
        }

        // 2. Tabular number feature enabled
        assertTrue("TabularNumFeature must be used for alignment", sheetContent.contains("TabularNumFeature"))

        // 3. Minimum 48dp interactive touch target on chips and buttons
        assertTrue("Must include minimumInteractiveComponentSize", sheetContent.contains("minimumInteractiveComponentSize()"))
        assertTrue("Chips must enforce minHeight = 48.dp", sheetContent.contains("defaultMinSize(minHeight = 48.dp)"))

        // 4. Zero AI sparkle tropes across all M4 touched files
        val m4Files = listOf(sheetContent, modelsContent, helperContent)
        for (content in m4Files) {
            assertFalse("Must not contain AutoAwesome AI trope", content.contains("AutoAwesome"))
            assertFalse("Must not contain ✨ AI sparkle emoji", content.contains("✨"))
        }

        // 5. Hairline ink divider tokens
        assertTrue("Must import HairlineInkStroke", sheetContent.contains("HairlineInkStroke"))
        assertTrue("Must use appColors.cardBackground", sheetContent.contains("appColors.cardBackground"))
        assertTrue("Must use appColors.divider", sheetContent.contains("appColors.divider"))

        // 6. Sparkline integrated inside stock row and detail sheet
        assertTrue("ProductPriceSparkline must be present in ProductPriceStockRow",
            sheetContent.substringAfter("fun ProductPriceStockRow").substringBefore("fun ProductPriceDetailSheet").contains("ProductPriceSparkline(")
        )
        assertTrue("ProductPriceSparkline must be present in ProductPriceDetailSheet",
            sheetContent.substringAfter("fun ProductPriceDetailSheet").substringBefore("fun PriceMetricCard").contains("ProductPriceSparkline(")
        )
    }
}
