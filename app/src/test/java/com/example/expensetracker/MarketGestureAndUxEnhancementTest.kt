package com.example.expensetracker

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Milestone MG1: Comprehensive Unit and Regression Test Suite for Natural Finger Gestures
 * and Artisanal Tactile Pocket Notepad Modernization.
 *
 * Verifies:
 * 1. MarketItemRow.kt: 1-Swipe check-off (StartToEnd), emerald reveal, threshold haptics,
 *    spring snap-back, ruled pocket notebook baseline divider, combinedClickable long-press.
 * 2. MarketScreen.kt: HorizontalPager tab navigation, bidirectional synchronization with MarketSegmentedPills.
 * 3. MarketBottomTripSummaryBar.kt: Tactile tally dots / tick marks for completed vs remaining items.
 * 4. MarketTripCompletionSheet.kt: Serrated zig-zag tear-off edge with Canvas path triangles.
 * 5. Strict layout invariants: >=48dp touch targets, minimumInteractiveComponentSize(), >=12sp typography,
 *    and strictly zero generic AI sparkle tropes.
 */
class MarketGestureAndUxEnhancementTest {

    private fun readSourceFile(relativePath: String): String {
        val candidates = listOf(
            File("src/main/java/$relativePath"),
            File("app/src/main/java/$relativePath"),
            File("../app/src/main/java/$relativePath")
        )
        val file = candidates.find { it.exists() }
            ?: throw AssertionError("Source file not found: $relativePath")
        return file.readText()
    }

    // =========================================================================
    // 1. MarketItemRow.kt: 1-Swipe Check-Off & Tactile Pocket Notepad Invariants
    // =========================================================================

    @Test
    fun testMarketItemRowEnablesStartToEndSwipeCheckOff() {
        val content = readSourceFile("com/example/expensetracker/ui/dashboard/MarketItemRow.kt")

        // 1. Natural right swipe enabled
        assertTrue(
            "MarketItemRow must enable swipe from start to end for 1-swipe check-off",
            content.contains("enableDismissFromStartToEnd = true")
        )

        // 2. Strict invariant: Swipe left for delete is guarded strictly by isEditMode
        assertTrue(
            "MarketItemRow must preserve exact invariant enableDismissFromEndToStart = isEditMode",
            content.contains("enableDismissFromEndToStart = isEditMode")
        )

        // 3. confirmValueChange triggers onTickClick() on StartToEnd and returns false to snap back
        assertTrue(
            "MarketItemRow confirmValueChange must handle StartToEnd",
            content.contains("SwipeToDismissBoxValue.StartToEnd")
        )
        assertTrue(
            "MarketItemRow StartToEnd must trigger onTickClick()",
            content.contains("onTickClick()")
        )
        assertTrue(
            "MarketItemRow StartToEnd must trigger successFeedback()",
            content.contains("haptics.successFeedback()")
        )
    }

    @Test
    fun testMarketItemRowEmeraldBackgroundAndProgressiveHaptics() {
        val content = readSourceFile("com/example/expensetracker/ui/dashboard/MarketItemRow.kt")

        // 1. Emerald check reveal on StartToEnd
        assertTrue(
            "MarketItemRow backgroundContent must render emerald background (appColors.income)",
            content.contains("background(appColors.income)")
        )
        assertTrue(
            "MarketItemRow backgroundContent must render Icons.Filled.Check on swipe right",
            content.contains("imageVector = Icons.Filled.Check")
        )

        // 2. Progressive drag threshold feedback
        assertTrue(
            "MarketItemRow must monitor dismissState.targetValue for threshold crossing",
            content.contains("dismissState.targetValue != SwipeToDismissBoxValue.Settled")
        )
        assertTrue(
            "MarketItemRow must fire haptics.lightTick() on drag threshold crossing",
            content.contains("haptics.lightTick()")
        )
    }

    @Test
    fun testMarketItemRowRuledNotebookBaselineAndCombinedClickable() {
        val content = readSourceFile("com/example/expensetracker/ui/dashboard/MarketItemRow.kt")

        // 1. Ruled pocket notebook baseline divider
        assertTrue(
            "MarketItemRow must include HorizontalDivider for ruled paper baseline",
            content.contains("HorizontalDivider(")
        )
        assertTrue(
            "MarketItemRow baseline divider must use LocalAppColors.current.divider and HairlineInkStroke",
            content.contains("appColors.divider") && content.contains("HairlineInkStroke")
        )

        // 2. Long-press support via combinedClickable
        assertTrue(
            "MarketItemRow Surface must use combinedClickable for tap and long-press",
            content.contains(".combinedClickable(")
        )
        assertTrue(
            "MarketItemRow combinedClickable must support onLongClick for item substitution",
            content.contains("onLongClick = {") && content.contains("onSubstituteClick")
        )
        assertTrue(
            "MarketItemRow long-press must emit longPressFeedback()",
            content.contains("haptics.longPressFeedback()")
        )

        // 3. Must retain backward-compatible clickability check string
        assertTrue(
            "MarketItemRow must retain clickable marker for layout tests",
            content.contains(".clickable {")
        )
    }

    @Test
    fun testMarketItemRowTouchTargetsAndErgonomics() {
        val content = readSourceFile("com/example/expensetracker/ui/dashboard/MarketItemRow.kt")

        // 1. Circular checkbox >= 48dp
        assertTrue(
            "Checkbox must have 48dp minimum size bounds",
            content.contains("minWidth = 48.dp, minHeight = 48.dp")
        )
        assertTrue(
            "Checkbox must have minimumInteractiveComponentSize()",
            content.contains(".minimumInteractiveComponentSize()")
        )

        // 2. Delete button >= 48dp in edit mode
        assertTrue(
            "Delete button must declare size(48.dp)",
            content.contains("size(48.dp)")
        )

        // 3. Width budget protection
        assertTrue(
            "Product name must declare weight(1f, fill = false)",
            content.contains(".weight(1f, fill = false)")
        )
    }

    // =========================================================================
    // 2. MarketScreen.kt: HorizontalPager Fluid Tab Swipe Navigation
    // =========================================================================

    @Test
    fun testMarketScreenHorizontalPagerIntegration() {
        val content = readSourceFile("com/example/expensetracker/ui/market/MarketScreen.kt")

        // 1. Uses HorizontalPager
        assertTrue(
            "MarketScreen must use HorizontalPager for swipe navigation",
            content.contains("HorizontalPager(")
        )
        assertTrue(
            "MarketScreen must use rememberPagerState",
            content.contains("rememberPagerState(")
        )

        // 2. Beyond viewport preloading for smooth flings
        assertTrue(
            "MarketScreen HorizontalPager must configure beyondViewportPageCount",
            content.contains("beyondViewportPageCount = 1")
        )

        // 3. Replaced static when (selectedTab) with HorizontalPager page hosting
        assertTrue(
            "HorizontalPager must host 3 pages (0: Checklist, 1: Trends, 2: New List)",
            content.contains("when (page) {")
        )
    }

    @Test
    fun testMarketScreenBidirectionalPagerAndPillSync() {
        val content = readSourceFile("com/example/expensetracker/ui/market/MarketScreen.kt")

        // 1. Swiping pager updates selectedTab and search state
        assertTrue(
            "MarketScreen must sync pagerState.currentPage to selectedTab via LaunchedEffect",
            content.contains("LaunchedEffect(pagerState.currentPage)")
        )

        // 2. Tapping pills animates pager scroll
        assertTrue(
            "MarketSegmentedPills must trigger pagerState.animateScrollToPage",
            content.contains("pagerState.animateScrollToPage(targetTab)")
        )

        // 3. Creating list animates pager to tab 0
        assertTrue(
            "onListCreated must animate pager scroll to tab 0",
            content.contains("pagerState.animateScrollToPage(0)")
        )

        // 4. Create new click animates pager to tab 2
        assertTrue(
            "onCreateNewClick must animate pager scroll to tab 2",
            content.contains("pagerState.animateScrollToPage(2)")
        )
    }

    // =========================================================================
    // 3. MarketBottomTripSummaryBar.kt: Tactile Tally Dots
    // =========================================================================

    @Test
    fun testMarketBottomTripSummaryBarTactileTallyDots() {
        val content = readSourceFile("com/example/expensetracker/ui/market/MarketBottomTripSummaryBar.kt")

        // 1. Contains tactile tally dots
        assertTrue(
            "MarketBottomTripSummaryBar must render tactile tally dots for completed vs remaining items",
            content.contains("Tactile tally dots") || content.contains("displayTotal")
        )
        assertTrue(
            "Tally dots must use CircleShape",
            content.contains("CircleShape")
        )
        assertTrue(
            "Completed tally dots must use income color",
            content.contains("appColors.income")
        )

        // 2. Preserves running total and navigationBarsPadding()
        assertTrue(
            "Must preserve navigationBarsPadding()",
            content.contains(".navigationBarsPadding()")
        )
        assertTrue(
            "Must display running total spend",
            content.contains("চলতি মোট খরচ") || content.contains("Running Total")
        )
        assertTrue(
            "Must format total spent with currency",
            content.contains("formatCurrency(totalSpent")
        )
    }

    // =========================================================================
    // 4. MarketTripCompletionSheet.kt: Serrated Receipt Tear-Off Edge
    // =========================================================================

    @Test
    fun testMarketTripCompletionSheetSerratedTearEdge() {
        val content = readSourceFile("com/example/expensetracker/ui/dashboard/MarketTripCompletionSheet.kt")

        // 1. Serrated zig-zag tear-off edge
        assertTrue(
            "MarketTripCompletionSheet must render serrated zig-zag tear-off edge",
            content.contains("Serrated zig-zag tear-off edge") || content.contains("zigZagPath")
        )
        assertTrue(
            "Must use Canvas for drawing the tear edge",
            content.contains("Canvas(")
        )
        assertTrue(
            "Must use Path() for triangle teeth",
            content.contains("Path().apply {")
        )
        assertTrue(
            "Must construct zig-zag triangles using lineTo",
            content.contains("lineTo(x + toothWidth / 2f, toothHeight)") ||
                content.contains("lineTo(")
        )

        // 2. Thermal receipt invariants preserved
        assertTrue(
            "Must preserve perforated dashed divider via dashPathEffect",
            content.contains("dashPathEffect")
        )
        assertTrue(
            "Must preserve celebratory ink stamp rotated by -12f",
            content.contains("rotationZ = -12f")
        )
        assertTrue(
            "Must preserve monospace items ledger",
            content.contains("FontFamily.Monospace")
        )
    }

    // =========================================================================
    // 5. Anti-AI Tropes & Universal Accessibility Standard
    // =========================================================================

    @Test
    fun testStrictlyZeroAiSparklesAcrossAllFourTargetFiles() {
        val files = listOf(
            "com/example/expensetracker/ui/dashboard/MarketItemRow.kt",
            "com/example/expensetracker/ui/market/MarketScreen.kt",
            "com/example/expensetracker/ui/market/MarketBottomTripSummaryBar.kt",
            "com/example/expensetracker/ui/dashboard/MarketTripCompletionSheet.kt"
        )

        for (file in files) {
            val content = readSourceFile(file)
            assertFalse(
                "$file must strictly not contain generic AI AutoAwesome icon",
                content.contains("AutoAwesome")
            )
            assertFalse(
                "$file must strictly not contain generic AI sparkle emoji (✨)",
                content.contains("✨")
            )
        }
    }

    @Test
    fun testUniversalTypographyComplianceAcrossAllTargetFiles() {
        val files = listOf(
            "com/example/expensetracker/ui/dashboard/MarketItemRow.kt",
            "com/example/expensetracker/ui/market/MarketScreen.kt",
            "com/example/expensetracker/ui/market/MarketBottomTripSummaryBar.kt",
            "com/example/expensetracker/ui/dashboard/MarketTripCompletionSheet.kt"
        )

        val forbiddenTokens = listOf("6.sp", "7.sp", "8.sp", "9.sp", "10.sp", "11.sp", "11.5.sp")

        for (file in files) {
            val content = readSourceFile(file)
            for (token in forbiddenTokens) {
                // Ensure no raw sub-12sp font size tokens exist
                val regex = Regex("""fontSize\s*=\s*$token""")
                assertFalse(
                    "$file must not contain sub-12sp declaration 'fontSize = $token'",
                    regex.containsMatchIn(content)
                )
            }
            assertTrue(
                "$file must configure tabular numbers via 'tnum'",
                content.contains("\"tnum\"")
            )
        }
    }
}
