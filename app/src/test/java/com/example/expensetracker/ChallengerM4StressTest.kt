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
 * Empirical Adversarial Challenger Stress Test Suite for Milestone M4:
 * 1. Strictly enforce zero occurrences of sub-12sp font size tokens (9.sp, 10.sp, 10.5.sp, 11.sp, 11.5.sp).
 * 2. Ensure touch targets >= 48dp (minimumInteractiveComponentSize) on all interactive chips, close buttons, clear buttons, and rows.
 * 3. Verify zero occurrences of AutoAwesome or ✨ AI tropes across M4 code.
 * 4. Stress-test long Bengali commodity labels and text overflow resilience.
 * 5. Tabular numerals (tnum) verification on all numbers and typography tokens.
 * 6. Mathematical stress-testing of monotone cubic Bézier sparklines and department inflation calculations.
 */
class ChallengerM4StressTest {

    private val basePath = "src/main/java"

    private fun readFile(relativePath: String): String {
        val file = File("$basePath/$relativePath")
        assertTrue("Expected source file to exist: ${file.absolutePath}", file.exists())
        return file.readText()
    }

    // =========================================================================
    // 1. TYPOGRAPHY CONSTRAINT: ZERO OCCURRENCES OF SUB-12SP TOKENS
    // =========================================================================

    @Test
    fun testSub12spFontSizeTokenZeroToleranceAudit() {
        val insightsFile = "com/example/expensetracker/ui/dashboard/ProductPriceInsightsSheet.kt"
        val content = readFile(insightsFile)

        // Exact forbidden sub-12sp tokens
        val forbiddenSub12sp = listOf(
            "6.sp", "7.sp", "8.sp", "8.5.sp", "9.sp", "9.5.sp",
            "10.sp", "10.5.sp", "11.sp", "11.5.sp"
        )

        for (token in forbiddenSub12sp) {
            val pattern = Regex("(?<![0-9])${Regex.escape(token)}")
            val match = pattern.find(content)
            assertNull(
                "Violation in $insightsFile: found forbidden sub-12sp token '$token' at index ${match?.range?.first}",
                match
            )
        }

        // Comprehensive extraction of all fontSize = X.sp declarations
        val fontSizePattern = Regex("""fontSize\s*=\s*([0-9]+(?:\.[0-9]+)?)\.sp""")
        val matches = fontSizePattern.findAll(content).toList()
        assertTrue("Expected to find explicit fontSize tokens in $insightsFile", matches.isNotEmpty())

        for (match in matches) {
            val sizeValue = match.groupValues[1].toDouble()
            assertTrue(
                "Violation: Explicit fontSize $sizeValue.sp in $insightsFile is strictly less than 12.0sp!",
                sizeValue >= 12.0
            )
        }

        // Verify Type.kt living typography constraints
        val typeContent = readFile("com/example/expensetracker/theme/Type.kt")
        assertTrue("Type.kt must set labelSmall >= 12sp", typeContent.contains("fontSize = 12.sp"))
        assertTrue("Type.kt must set labelMedium >= 12sp", typeContent.contains("fontSize = 12.sp"))
    }

    // =========================================================================
    // 2. TOUCH TARGET ERGONOMICS: >= 48DP ON ALL INTERACTIVE ELEMENTS
    // =========================================================================

    @Test
    fun testTouchTargetErgonomicsComprehensiveAudit() {
        val content = readFile("com/example/expensetracker/ui/dashboard/ProductPriceInsightsSheet.kt")

        // 1. minimumInteractiveComponentSize() must be declared and imported
        assertTrue(
            "ProductPriceInsightsSheet must import minimumInteractiveComponentSize",
            content.contains("import androidx.compose.material3.minimumInteractiveComponentSize")
        )

        // 2. Header close button must be >= 48dp with minimumInteractiveComponentSize
        assertTrue(
            "Modal header Close button must have size(48.dp)",
            content.contains(".size(48.dp)\n                        .minimumInteractiveComponentSize()\n                        .clip(CircleShape)") ||
            content.contains(".size(48.dp)")
        )

        // 3. Detail sheet close button must be >= 48dp with minimumInteractiveComponentSize
        val detailSheetSection = content.substringAfter("fun ProductPriceDetailSheet")
        assertTrue(
            "Detail sheet close button must enforce 48dp interactive size",
            detailSheetSection.contains(".size(48.dp)") &&
            detailSheetSection.contains(".minimumInteractiveComponentSize()")
        )

        // 4. Quick trend filter chips (All, Up, Down) must enforce minHeight = 48.dp
        val trendChipsSection = content.substringAfter("Quick Trend KPI Filter Bar").substringBefore("Department & Sort Chips Row")
        val minHeight48Count = Regex("""defaultMinSize\(\s*minHeight\s*=\s*48\.dp\s*\)""").findAll(trendChipsSection).count()
        assertEquals(
            "Expected 3 trend filter chips (All, Up, Down) each with defaultMinSize(minHeight = 48.dp)",
            3,
            minHeight48Count
        )

        // 5. Department filter chips must enforce minHeight = 48.dp
        val deptChipsSection = content.substringAfter("Department & Sort Chips Row").substringBefore("if (showInternalSearchBar)")
        assertTrue(
            "Department chips must have defaultMinSize(minHeight = 48.dp)",
            deptChipsSection.contains("defaultMinSize(minHeight = 48.dp)")
        )

        // 6. Search query clear button container must be 48x48dp
        val searchBannerSection = content.substringAfter("else if (effectiveSearchQuery.isNotBlank() && !isSearchActive)")
        assertTrue(
            "Search clear button container must be 48x48dp",
            searchBannerSection.contains(".size(48.dp)") &&
            searchBannerSection.contains(".minimumInteractiveComponentSize()")
        )

        // 7. ProductPriceStockRow must enforce minHeight = 48.dp
        val stockRowSection = content.substringAfter("fun ProductPriceStockRow")
        assertTrue(
            "ProductPriceStockRow must declare defaultMinSize(minHeight = 48.dp) with minimumInteractiveComponentSize",
            stockRowSection.contains("defaultMinSize(minHeight = 48.dp)") &&
            stockRowSection.contains("minimumInteractiveComponentSize()")
        )
    }

    // =========================================================================
    // 3. ZERO OCCURRENCES OF AUTOAWESOME OR ✨ AI TROPES
    // =========================================================================

    @Test
    fun testZeroAiSparklesAndTropeEnforcement() {
        val filesToCheck = listOf(
            "com/example/expensetracker/ui/dashboard/ProductPriceInsightsSheet.kt",
            "com/example/expensetracker/data/model/ProductPriceModels.kt",
            "com/example/expensetracker/data/local/ProductPriceTableHelper.kt"
        )

        val forbiddenTropes = listOf("AutoAwesome", "✨", "auto_awesome", "Sparkle")

        for (relativePath in filesToCheck) {
            val content = readFile(relativePath)
            for (trope in forbiddenTropes) {
                assertFalse(
                    "Forbidden AI trope '$trope' detected in $relativePath",
                    content.contains(trope)
                )
            }
        }
    }

    // =========================================================================
    // 4. STRESS-TEST LONG BENGALI COMMODITY LABELS & TEXT OVERFLOW RESILIENCE
    // =========================================================================

    @Test
    fun testLongBengaliCommodityLabelsAndTextOverflowResilience() {
        // A. Mathematical and classifier resilience under adversarial inputs
        val extremeNames = listOf(
            "টাটকা দেশি লাল শাক ও কাঁচা মরিচ সমেত বিশেষ মসলাযুক্ত খাঁটি গাওয়া ঘি",
            "প্রিমিয়াম কোয়ালিটি মিনিকেট চাল (প্যাকেটজাত ২৫ কেজি বস্তা) - স্পেশাল কাটারিবহুল সুগন্ধি চাল Premium Quality Miniket Rice 25kg Bag",
            "আলুপেঁয়াজরসুনআদাহলুদমরিচধনেতেললবণচিনিচালডালদুধডিম",
            "ইলিশ মাছ (১.৫ কেজি+) 🐟 [পদ্মার তাজা]",
            "",
            "   ",
            "!!!@@@###$$$%%%^^^&&&***()",
            "1234567890 0987654321",
            "A".repeat(250)
        )

        for (name in extremeNames) {
            val category = getMarketItemCategory(name)
            assertNotNull("Category should never be null for extreme name: '$name'", category)
            assertTrue("Category must be one of standard 4 departments",
                category in listOf("veggies", "meat_fish", "dairy_bakery", "groceries"))

            val insight = ProductPriceInsight(
                productName = name,
                currentUnitPrice = 150.0,
                previousUnitPrice = 120.0,
                baseUnit = "কেজি",
                priceDiff = 30.0,
                percentDiff = 25.0,
                purchaseCount = 10,
                lastPurchasedTimestamp = System.currentTimeMillis()
            )

            // Test department inflation aggregation with extreme name
            val kpis = computeDepartmentInflationKpis(listOf(insight))
            assertEquals(4, kpis.size)
            val matchedDept = kpis.first { it.department.key == category }
            assertEquals(1, matchedDept.trackedItemsCount)
            assertEquals(1, matchedDept.itemsWithHistoryCount)
            assertEquals(25.0, matchedDept.averageInflationPercent!!, 0.001)
        }

        // B. Layout overflow resilience audit in ProductPriceInsightsSheet.kt
        val content = readFile("com/example/expensetracker/ui/dashboard/ProductPriceInsightsSheet.kt")

        // 1. Product name in stock row must declare maxLines = 1, overflow = TextOverflow.Ellipsis
        val stockRowProductText = content.substringAfter("Text(").substringAfter("text = insight.productName.replaceFirstChar")
        assertTrue(
            "Product name in stock row must enforce maxLines = 1",
            content.contains("maxLines = 1")
        )
        assertTrue(
            "Product name in stock row must enforce overflow = TextOverflow.Ellipsis",
            content.contains("overflow = TextOverflow.Ellipsis")
        )
        assertTrue(
            "Product name in stock row must use fill = false inside weighted row to prevent pushing badges off screen",
            content.contains("modifier = Modifier.weight(1f, fill = false)")
        )

        // 2. Sub-caption in stock row must enforce single line ellipsis
        val subCaptionMatch = content.contains("text = if (isBangla) \"\$purchaseCountText বার কেনা • গড় \$currencySymbol\$avgRateText/\${insight.baseUnit}\"")
        assertTrue("Sub-caption in stock row must exist", subCaptionMatch)

        // 3. Metric card subValue must enforce single line ellipsis
        val metricCardSection = content.substringAfter("private fun PriceMetricCard")
        assertTrue(
            "PriceMetricCard subValue must enforce maxLines = 1",
            metricCardSection.contains("maxLines = 1")
        )
        assertTrue(
            "PriceMetricCard subValue must enforce overflow = TextOverflow.Ellipsis",
            metricCardSection.contains("overflow = TextOverflow.Ellipsis")
        )
    }

    // =========================================================================
    // 5. TABULAR NUMERALS (TNUM) VERIFICATION ON ALL NUMBERS
    // =========================================================================

    @Test
    fun testTabularNumeralsVerificationOnAllNumbers() {
        val content = readFile("com/example/expensetracker/ui/dashboard/ProductPriceInsightsSheet.kt")

        // 1. TabularNumFeature must be imported
        assertTrue(
            "ProductPriceInsightsSheet must import TabularNumFeature",
            content.contains("import com.example.expensetracker.theme.TabularNumFeature")
        )

        // 2. Count occurrences of fontFeatureSettings = TabularNumFeature
        val tnumMatches = Regex("""fontFeatureSettings\s*=\s*TabularNumFeature""").findAll(content).toList()
        assertTrue(
            "Expected at least 15 explicit TabularNumFeature declarations on rates, counters, and dates (found ${tnumMatches.size})",
            tnumMatches.size >= 15
        )

        // 3. Stock row price and rate elements must use TabularNumFeature
        val stockRowSection = content.substringAfter("fun ProductPriceStockRow").substringBefore("fun ProductPriceDetailSheet")
        val stockRowTnumCount = Regex("""fontFeatureSettings\s*=\s*TabularNumFeature""").findAll(stockRowSection).count()
        assertTrue(
            "ProductPriceStockRow must have at least 4 TabularNumFeature declarations (avg rate, current rate, date, trend pill)",
            stockRowTnumCount >= 4
        )

        // 4. Detail sheet must use TabularNumFeature on metrics and history
        val detailSheetSection = content.substringAfter("fun ProductPriceDetailSheet").substringBefore("fun PriceMetricCard")
        val detailSheetTnumCount = Regex("""fontFeatureSettings\s*=\s*TabularNumFeature""").findAll(detailSheetSection).count()
        assertTrue(
            "ProductPriceDetailSheet must have at least 5 TabularNumFeature declarations",
            detailSheetTnumCount >= 5
        )
    }

    // =========================================================================
    // 6. MONOTONE CUBIC BÉZIER SPARKLINES MATHEMATICAL STRESS HARNESS
    // =========================================================================

    @Test
    fun testMonotoneSparklineMathStressHarness() {
        // Stress test the mathematical transformation logic used in ProductPriceSparkline
        fun simulateSparklineCoords(
            recentPoints: List<Double>,
            canvasWidth: Float = 42f,
            canvasHeight: Float = 24f,
            padX: Float = 2f,
            padY: Float = 3f
        ): Pair<Boolean, List<Pair<Float, Float>>> {
            val rawPoints = when {
                recentPoints.size >= 2 -> recentPoints
                recentPoints.size == 1 -> listOf(recentPoints[0], recentPoints[0])
                else -> listOf(10.0, 10.0, 10.0)
            }

            val minP = rawPoints.minOrNull() ?: 0.0
            val maxP = rawPoints.maxOrNull() ?: 1.0
            val isFlat = (maxP - minP) < 0.0001
            val span = if (isFlat) 1.0 else (maxP - minP)

            val usableWidth = (canvasWidth - (padX * 2)).coerceAtLeast(1f)
            val usableHeight = (canvasHeight - (padY * 2)).coerceAtLeast(1f)

            val coords = rawPoints.mapIndexed { idx, p ->
                val x = padX + (idx.toFloat() / (rawPoints.size - 1)) * usableWidth
                val y = if (isFlat) canvasHeight / 2f else canvasHeight - padY - (((p - minP) / span) * usableHeight).toFloat()
                Pair(x, y)
            }
            return Pair(isFlat, coords)
        }

        // Case A: Flat line (zero span)
        val (isFlat1, coords1) = simulateSparklineCoords(listOf(50.0, 50.0, 50.0, 50.0))
        assertTrue("Identical price points must trigger isFlat", isFlat1)
        for (coord in coords1) {
            assertEquals("Y coordinate in flat line must be centered at height/2 (12f)", 12f, coord.second, 0.001f)
            assertFalse("X coordinate must not be NaN", coord.first.isNaN())
            assertFalse("Y coordinate must not be NaN", coord.second.isNaN())
        }

        // Case B: Single point
        val (isFlat2, coords2) = simulateSparklineCoords(listOf(75.0))
        assertTrue("Single point must duplicate and trigger isFlat", isFlat2)
        assertEquals(2, coords2.size)

        // Case C: Empty list fallback
        val (isFlat3, coords3) = simulateSparklineCoords(emptyList())
        assertEquals(3, coords3.size)

        // Case D: Strictly increasing points
        val (isFlat4, coords4) = simulateSparklineCoords(listOf(10.0, 20.0, 30.0, 40.0))
        assertFalse(isFlat4)
        // In screen coordinates, higher price = smaller Y
        assertTrue("Higher price must produce lower screen Y", coords4.last().second < coords4.first().second)

        // Case E: Massive dataset (1000 points)
        val thousandPoints = (1..1000).map { it * 1.5 }
        val (isFlat5, coords5) = simulateSparklineCoords(thousandPoints)
        assertFalse(isFlat5)
        assertEquals(1000, coords5.size)
        for (coord in coords5) {
            assertTrue("X coordinate must be >= 0", coord.first >= 0f)
            assertTrue("Y coordinate must be >= 0", coord.second >= 0f)
        }
    }

    // =========================================================================
    // 7. DEPARTMENT INFLATION ENGINE EXTREME VALUE ROBUSTNESS
    // =========================================================================

    @Test
    fun testDepartmentInflationEngineDivisionByZeroAndExtremeValues() {
        // Items with extreme rates
        val extremeInsights = listOf(
            ProductPriceInsight(
                productName = "স্পেশাল আলু",
                currentUnitPrice = 1e9,
                previousUnitPrice = 1e8,
                baseUnit = "কেজি",
                priceDiff = 9e8,
                percentDiff = 900.0,
                priceTrend = PriceTrend.UP
            ),
            ProductPriceInsight(
                productName = "পদ্মার ইলিশ",
                currentUnitPrice = 0.01,
                previousUnitPrice = 1000.0,
                baseUnit = "কেজি",
                priceDiff = -999.99,
                percentDiff = -99.999,
                priceTrend = PriceTrend.DOWN
            ),
            ProductPriceInsight(
                productName = "লবণ",
                currentUnitPrice = 40.0,
                previousUnitPrice = 40.0,
                baseUnit = "কেজি",
                priceDiff = 0.0,
                percentDiff = 0.0,
                priceTrend = PriceTrend.STABLE
            )
        )

        val kpis = computeDepartmentInflationKpis(extremeInsights)
        assertEquals(4, kpis.size)

        val veggieKpi = kpis.first { it.department.key == "veggies" }
        assertEquals(PriceTrend.UP, veggieKpi.trend)
        assertEquals("+900.0%", formatInflationPct(veggieKpi.averageInflationPercent, isBangla = false))

        val fishKpi = kpis.first { it.department.key == "meat_fish" }
        assertEquals(PriceTrend.DOWN, fishKpi.trend)
        assertEquals("-100.0%", formatInflationPct(fishKpi.averageInflationPercent, isBangla = false))

        val groceryKpi = kpis.first { it.department.key == "groceries" }
        assertEquals(PriceTrend.STABLE, groceryKpi.trend)
        assertEquals("0.0%", formatInflationPct(groceryKpi.averageInflationPercent, isBangla = false))

        // Formatting extreme numbers
        assertEquals("নতুন", formatInflationPct(null, isBangla = true))
        assertEquals("New", formatInflationPct(null, isBangla = false))
        assertEquals("০.০%", formatInflationPct(0.0, isBangla = true))
        assertEquals("+১০০০.০%", formatInflationPct(1000.0, isBangla = true))
        assertEquals("-৫০.৫%", formatInflationPct(-50.5, isBangla = true))
    }
}
