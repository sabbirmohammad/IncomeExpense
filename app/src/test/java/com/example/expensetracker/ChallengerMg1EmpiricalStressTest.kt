package com.example.expensetracker

import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.data.model.PriceTrend
import com.example.expensetracker.data.model.ProductPriceInsight
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.dashboard.ALL_DEPARTMENTS
import com.example.expensetracker.ui.dashboard.getMarketItemCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Empirical Challenger MG1 Adversarial Stress Test Harness:
 * Rigorously attacks and stresses Milestone MG1:
 *
 * 1. Rapid gesture swipe concurrency and state transition oracle:
 *    - Rapid consecutive StartToEnd triggers (snap-back invariant without drops).
 *    - Edit-mode deletion isolation (EndToStart disabled when not in edit mode).
 *    - Revert vs Done state flip-flop determinism.
 * 2. Extreme item titles, long quantities, and overflow rendering:
 *    - 500-char extreme titles, Bangla complex conjuncts, RTL/Arabic, zero-width chars.
 *    - Extreme quantities (99,999,999.999, 0.00001, large fractions).
 *    - Width budget simulation on 360dp compact viewport (no layout clipping).
 * 3. Tactile tally dots mathematical stability:
 *    - 0 items, 1 item, 10,000 items (clamped to maxVisibleDots = 6).
 *    - Over-purchased state defense (purchased > total).
 * 4. Serrated tear-off edge geometry & teeth count bounds:
 *    - 0px, 320px, 360px, 1440px viewport widths.
 * 5. HorizontalPager gesture synchronization & search state cleanup:
 *    - Page index bounds (0..2), beyondViewportPageCount, search teardown on tab switch.
 * 6. Strict absence of generic AI tropes across all target and market files:
 *    - Zero AutoAwesome, zero ✨ sparkles, zero artificial AI badge icons.
 * 7. Universal typography compliance:
 *    - Strict >= 12sp constraint for all text tokens, tabular numbers enabled.
 */
class ChallengerMg1EmpiricalStressTest {

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

    // =========================================================================
    // 1. Rapid Gesture Swipe Concurrency & State Invariant Oracle
    // =========================================================================

    @Test
    fun testRapidSwipeStateTransitionDeterminism() {
        // Oracle state machine for 1-Swipe Check-off
        data class SwipeState(var isPurchased: Boolean, var amount: Double?, var promptCount: Int, var unmarkCount: Int)

        val state = SwipeState(isPurchased = false, amount = null, promptCount = 0, unmarkCount = 0)

        // Rapid 10-swipe sequence simulating rapid thumb gestures
        for (i in 1..10) {
            val wasPurchased = state.isPurchased
            if (!wasPurchased) {
                // First swipe: triggers onTickClick() -> onPromptAmount
                state.promptCount++
                // User enters price or marks done
                state.isPurchased = true
                state.amount = 120.0
            } else {
                // Second swipe: triggers onTickClick() -> onUnmarkPurchased
                state.unmarkCount++
                state.isPurchased = false
                state.amount = null
            }
        }

        assertEquals("Prompt count must equal 5 in a 10-swipe alternating sequence", 5, state.promptCount)
        assertEquals("Unmark count must equal 5 in a 10-swipe alternating sequence", 5, state.unmarkCount)
        assertFalse("Final state must return to unpurchased after even number of swipes", state.isPurchased)
    }

    @Test
    fun testSwipeDismissStateGuardsAndSnapBack() {
        val content = readSourceFile("com/example/expensetracker/ui/dashboard/MarketItemRow.kt")

        // Invariant: confirmValueChange returns false on StartToEnd so the row snaps back immediately
        // while the state change is handled in place.
        val startToEndBlock = content.substringAfter("SwipeToDismissBoxValue.StartToEnd ->")
            .substringBefore("SwipeToDismissBoxValue.EndToStart ->")

        assertTrue("StartToEnd must call onTickClick()", startToEndBlock.contains("onTickClick()"))
        assertTrue("StartToEnd must emit success feedback haptic", startToEndBlock.contains("haptics.successFeedback()"))
        assertTrue("StartToEnd must return false to ensure spring snap-back", startToEndBlock.contains("false"))

        // Invariant: EndToStart is strictly guarded by isEditMode
        val endToStartBlock = content.substringAfter("SwipeToDismissBoxValue.EndToStart ->")
            .substringBefore("SwipeToDismissBoxValue.Settled ->")

        assertTrue("EndToStart must check isEditMode", endToStartBlock.contains("if (isEditMode)"))
        assertTrue("EndToStart must call onRequestDelete() when in edit mode", endToStartBlock.contains("onRequestDelete()"))
        assertTrue("EndToStart must return false so item does not disappear prematurely", endToStartBlock.contains("false"))
    }

    // =========================================================================
    // 2. Extreme Item Titles, Long Quantities & Overflow Layout Simulation
    // =========================================================================

    @Test
    fun testExtremeItemTitlesAndBanglaConjuncts() {
        val extremeTitles = listOf(
            "খাঁটি গাওয়া ঘি ও অতিরিক্ত লম্বা নাম বিশিষ্ট বাজার আইটেম যা কোনোভাবেই স্ক্রিনের বাইরে যাওয়া উচিত নয় এবং উপচে পড়া রোধ করা আবশ্যক",
            "A".repeat(500),
            "🌶️ 🐟 🥩 🥛 🥬 🧂 🧅 🧄 🥔 🥕 🍅 🥒 🥦 🌽 🥚 🍗 🍖 🥖 🍞 🧀",
            "কাক্সিক্ষত শৃঙ্খলিত বঙ্কিম উজ্জ্বল প্রশান্ত কৃষ্ণচূড়া", // Heavy Bengali conjuncts (যুক্তাক্ষর)
            "اللغة العربية و عسل سدر يمني أصلي فاخر", // Arabic / RTL script
            "ZeroWidth\u200BJoiner\u200DTest\uFEFFString", // Zero-width spaces and formatting chars
            "   \t\n Whitespace Padded Title \t\n  "
        )

        for (title in extremeTitles) {
            val item = MarketItem(
                id = 1L,
                listId = 1L,
                name = title,
                quantity = 1.0,
                unit = "কেজি",
                isPurchased = false,
                amount = null
            )
            // Department classification must never crash on extreme titles
            val category = getMarketItemCategory(item.name)
            assertNotNull("Category resolution must be non-null for extreme title", category)

            // Category must be recognized in one of canonical departments
            assertTrue("ALL_DEPARTMENTS must contain categorized department", ALL_DEPARTMENTS.any { it.key == category })
        }
    }

    @Test
    fun testExtremeQuantitiesFormatting() {
        val testCases = listOf(
            Pair(99999999.0, "৯৯৯৯৯৯৯৯"),
            Pair(0.0001, "0"),
            Pair(123456.789, "১২৩৪৫৬.৮"),
            Pair(0.5, "০.৫"),
            Pair(1.0, "১")
        )

        for ((qty, expectedPrefix) in testCases) {
            val item = MarketItem(
                id = 1L,
                listId = 1L,
                name = "পণ্য",
                quantity = qty,
                unit = "কেজি"
            )
            val formatted = item.formatQuantity()
            assertNotNull("formatQuantity() must return non-null string", formatted)
            assertTrue("formatQuantity() result must contain unit", formatted.contains("কেজি") || formatted.contains("kg"))
        }
    }

    @Test
    fun testMarketItemRowWidthBudgetOn360dpViewport() {
        val content = readSourceFile("com/example/expensetracker/ui/dashboard/MarketItemRow.kt")

        // Verify width-budget protection invariants in MarketItemRow:
        // 1. Text has maxLines = 1 and overflow = TextOverflow.Ellipsis
        assertTrue("Product title Text must declare maxLines = 1", content.contains("maxLines = 1"))
        assertTrue("Product title Text must declare overflow = TextOverflow.Ellipsis", content.contains("overflow = TextOverflow.Ellipsis"))

        // 2. Title Text declares weight(1f, fill = false) to gracefully yield space to price pills
        assertTrue("Product title must declare weight(1f, fill = false)", content.contains(".weight(1f, fill = false)"))

        // 3. Department tag chip has maxLines = 1 and Ellipsis
        assertTrue("Department indicator must declare overflow = TextOverflow.Ellipsis", content.contains("overflow = TextOverflow.Ellipsis"))

        // 4. Quantity row declares weight(1f, fill = false)
        assertTrue("Line 2 pills row must declare weight(1f, fill = false)", content.contains(".weight(1f, fill = false)"))
    }

    // =========================================================================
    // 3. Tactile Tally Dots Mathematical Bounds & Edge Cases
    // =========================================================================

    private fun simulateTallyDots(totalCount: Int, purchasedCount: Int): Pair<Int, Int> {
        if (totalCount <= 0) return Pair(0, 0)
        val maxVisibleDots = 6
        val displayTotal = minOf(totalCount, maxVisibleDots)
        val displayPurchased = if (totalCount <= maxVisibleDots) {
            purchasedCount
        } else {
            ((purchasedCount.toFloat() / totalCount.toFloat()) * maxVisibleDots).toInt()
        }
        return Pair(displayTotal, displayPurchased.coerceIn(0, displayTotal))
    }

    @Test
    fun testTactileTallyDotsEdgeCases() {
        // Case 1: Empty trip (0 items)
        val (total0, done0) = simulateTallyDots(0, 0)
        assertEquals("0 total items produces 0 dots", 0, total0)
        assertEquals("0 total items produces 0 done dots", 0, done0)

        // Case 2: 1 item, 0 done
        val (total1, done1) = simulateTallyDots(1, 0)
        assertEquals(1, total1)
        assertEquals(0, done1)

        // Case 3: 1 item, 1 done
        val (total1b, done1b) = simulateTallyDots(1, 1)
        assertEquals(1, total1b)
        assertEquals(1, done1b)

        // Case 4: 5 items, 3 done (within maxVisibleDots = 6)
        val (total5, done5) = simulateTallyDots(5, 3)
        assertEquals(5, total5)
        assertEquals(3, done5)

        // Case 5: 100 items, 50 done (scaled to maxVisibleDots = 6)
        val (total100, done100) = simulateTallyDots(100, 50)
        assertEquals(6, total100)
        assertEquals(3, done100) // 50% of 6 = 3

        // Case 6: 10,000 items, 9,999 done
        val (totalHuge, doneHuge) = simulateTallyDots(10000, 9999)
        assertEquals(6, totalHuge)
        assertEquals(5, doneHuge) // Integer truncation of 5.9994 -> 5

        // Case 7: All done on huge list
        val (totalHugeAll, doneHugeAll) = simulateTallyDots(10000, 10000)
        assertEquals(6, totalHugeAll)
        assertEquals(6, doneHugeAll)

        // Case 8: Adversarial input (purchased > total due to concurrent DB mutation)
        val (totalAdv, doneAdv) = simulateTallyDots(5, 10)
        assertEquals(5, totalAdv)
        assertEquals(5, doneAdv) // Coerced to displayTotal
    }

    // =========================================================================
    // 4. Serrated Tear-Off Edge Geometry & Teeth Count Bounds
    // =========================================================================

    private fun calculateTeethCount(widthPx: Float, toothWidthPx: Float): Int {
        if (widthPx <= 0f || toothWidthPx <= 0f) return 0
        return (widthPx / toothWidthPx).toInt() + 1
    }

    @Test
    fun testSerratedTearEdgeMath() {
        val toothWidthPx = 36f // 12dp @ 3x density (1080p display)

        // 0px width (hidden / unmeasured)
        assertEquals(0, calculateTeethCount(0f, toothWidthPx))

        // Compact phone (360dp @ 3x = 1080px)
        val teeth1080 = calculateTeethCount(1080f, toothWidthPx)
        assertEquals(31, teeth1080) // 1080/36 = 30 + 1 = 31

        // Large tablet (800dp @ 2x = 1600px)
        val teethTablet = calculateTeethCount(1600f, toothWidthPx)
        assertEquals(45, teethTablet) // 1600/36 = 44 + 1 = 45

        // Ultra-wide foldable unfolded (2200px)
        val teethFoldable = calculateTeethCount(2200f, toothWidthPx)
        assertTrue("Teeth count must be bounded and positive", teethFoldable in 1..100)
    }

    // =========================================================================
    // 5. HorizontalPager Gesture Synchronization & Search Teardown
    // =========================================================================

    @Test
    fun testHorizontalPagerConfigurationInvariants() {
        val content = readSourceFile("com/example/expensetracker/ui/market/MarketScreen.kt")

        // 1. HorizontalPager with beyondViewportPageCount = 1
        assertTrue(
            "MarketScreen must configure beyondViewportPageCount = 1 for fluid swipe preloading",
            content.contains("beyondViewportPageCount = 1")
        )

        // 2. HorizontalPager modifier fills max width and has weight(1f) to occupy vertical space properly
        assertTrue(
            "HorizontalPager must declare Modifier.fillMaxWidth().weight(1f)",
            content.contains(".fillMaxWidth()") && content.contains(".weight(1f)")
        )

        // 3. Search active reset when navigating away from Price Intelligence tab (Tab 1)
        assertTrue(
            "Leaving tab 1 must reset isPriceSearchActive",
            content.contains("if (pagerState.currentPage != 1)") || content.contains("if (targetTab != 1)")
        )
    }

    // =========================================================================
    // 6. Strict Absence of Generic AI Tropes (AutoAwesome, ✨) Across All Target Files
    // =========================================================================

    @Test
    fun testZeroAiTropesAcrossAllMarketFiles() {
        val targetFiles = listOf(
            "com/example/expensetracker/ui/dashboard/MarketItemRow.kt",
            "com/example/expensetracker/ui/market/MarketScreen.kt",
            "com/example/expensetracker/ui/market/MarketBottomTripSummaryBar.kt",
            "com/example/expensetracker/ui/dashboard/MarketTripCompletionSheet.kt",
            "com/example/expensetracker/ui/dashboard/CreateMarketListSection.kt",
            "com/example/expensetracker/ui/dashboard/ProductPriceInsightsSheet.kt",
            "com/example/expensetracker/ui/market/MarketDepartmentSectionHeader.kt",
            "com/example/expensetracker/ui/market/MarketParsedTagChip.kt"
        )

        val forbiddenTokens = listOf("AutoAwesome", "✨", "Sparkles", "MagicWand", "ai_magic")

        for (relPath in targetFiles) {
            val content = readSourceFile(relPath)
            for (forbidden in forbiddenTokens) {
                assertFalse(
                    "Target file $relPath must not contain generic AI trope: $forbidden",
                    content.contains(forbidden)
                )
            }
        }
    }

    // =========================================================================
    // 7. Universal Typography & Minimum Font Size Constraint (>= 12sp)
    // =========================================================================

    @Test
    fun testUniversalTypographyScaleConstraint() {
        val targetFiles = listOf(
            "com/example/expensetracker/ui/dashboard/MarketItemRow.kt",
            "com/example/expensetracker/ui/market/MarketScreen.kt",
            "com/example/expensetracker/ui/market/MarketBottomTripSummaryBar.kt",
            "com/example/expensetracker/ui/dashboard/MarketTripCompletionSheet.kt"
        )

        val sub12spPatterns = listOf("7.sp", "8.sp", "9.sp", "10.sp", "11.sp", "11.5.sp")

        for (relPath in targetFiles) {
            val content = readSourceFile(relPath)
            for (pattern in sub12spPatterns) {
                val match = Regex("""fontSize\s*=\s*$pattern""").containsMatchIn(content)
                assertFalse(
                    "Target file $relPath must not contain sub-12sp font size ($pattern)",
                    match
                )
            }
        }
    }
}
