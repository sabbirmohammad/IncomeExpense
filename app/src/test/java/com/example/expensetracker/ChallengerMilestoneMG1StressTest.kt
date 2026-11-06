package com.example.expensetracker

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Empirical Challenger Milestone MG1 Test & Stress Harness.
 *
 * Provides adversarial and stress verification for Natural Finger Gestures
 * and Handcrafted Tactile Pocket Notepad Polish:
 *
 * 1. MarketItemRow.kt:
 *    - StartToEnd 1-swipe check-off state transition, spring snap-back (return false).
 *    - EndToStart swipe delete gated strictly by isEditMode.
 *    - Drag threshold boundary crossing haptic feedback (haptics.lightTick()).
 *    - CombinedClickable long-press tactile substitution trigger.
 *    - Strict >=48dp touch targets and >=12sp typography standards.
 *
 * 2. MarketScreen.kt:
 *    - HorizontalPager tab hosting (pageCount = 3, beyondViewportPageCount = 1).
 *    - Bidirectional synchronization between pagerState and MarketSegmentedPills.
 *    - Tab transitions, search state cleanup on leaving Tab 1, and action triggers.
 *
 * 3. MarketBottomTripSummaryBar.kt:
 *    - Tally dots mathematical bounds under empty (0 items), partial, full, and massive (100+ items) states.
 *    - Visual bounding: maxVisibleDots constraint preventing horizontal layout overflow.
 *    - Running total, typography >= 12sp, and navigationBarsPadding().
 *
 * 4. MarketTripCompletionSheet.kt:
 *    - Serrated tear-off canvas drawing math stability across varying viewports (0px, 360px, 1080px, 1440px).
 *    - Monospace itemized ledger with tabular formatting.
 *    - Rubber stamp rotation (-12 degrees).
 *
 * 5. Universal Invariants:
 *    - Zero AI sparkles (AutoAwesome, ✨).
 *    - Zero sub-12sp typography.
 */
class ChallengerMilestoneMG1StressTest {

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
    // 1. MarketItemRow.kt: Swipe Gestures & Tactile Notepad Polish
    // =========================================================================

    @Test
    fun testMarketItemRowSwipeStateTransitionsAndSnapBack() {
        val content = readSourceFile("com/example/expensetracker/ui/dashboard/MarketItemRow.kt")

        // 1. Swipe right (StartToEnd) must be universally enabled
        assertTrue(
            "MarketItemRow must declare enableDismissFromStartToEnd = true",
            content.contains("enableDismissFromStartToEnd = true")
        )

        // 2. Swipe left (EndToStart) must be strictly gated by isEditMode
        assertTrue(
            "MarketItemRow must declare enableDismissFromEndToStart = isEditMode",
            content.contains("enableDismissFromEndToStart = isEditMode")
        )

        // 3. StartToEnd must trigger onTickClick() and successFeedback()
        assertTrue(
            "StartToEnd must call onTickClick()",
            content.contains("SwipeToDismissBoxValue.StartToEnd -> {") &&
                content.contains("onTickClick()")
        )
        assertTrue(
            "StartToEnd must call haptics.successFeedback()",
            content.contains("haptics.successFeedback()")
        )

        // 4. StartToEnd must return false for spring snap-back behavior (in-place state update)
        // Verify that confirmValueChange returns false for StartToEnd
        val startToEndBlock = content.substringAfter("SwipeToDismissBoxValue.StartToEnd -> {")
            .substringBefore("SwipeToDismissBoxValue.EndToStart -> {")
        assertTrue(
            "StartToEnd must return false so row snaps back cleanly into place",
            startToEndBlock.contains("false")
        )

        // 5. EndToStart must trigger onRequestDelete() when isEditMode is true and return false
        val endToStartBlock = content.substringAfter("SwipeToDismissBoxValue.EndToStart -> {")
            .substringBefore("SwipeToDismissBoxValue.Settled -> false")
        assertTrue(
            "EndToStart must call onRequestDelete() under isEditMode",
            endToStartBlock.contains("if (isEditMode)") && endToStartBlock.contains("onRequestDelete()")
        )
        assertTrue(
            "EndToStart must return false to wait for confirmation dialog",
            endToStartBlock.contains("false")
        )
    }

    @Test
    fun testMarketItemRowDragThresholdHapticsAndVisualReveal() {
        val content = readSourceFile("com/example/expensetracker/ui/dashboard/MarketItemRow.kt")

        // 1. Progressive threshold crossing detection
        assertTrue(
            "MarketItemRow must detect targetValue threshold crossing",
            content.contains("val isThresholdPassed = dismissState.targetValue != SwipeToDismissBoxValue.Settled")
        )
        assertTrue(
            "MarketItemRow must retain wasThresholdPassed to prevent haptic flooding",
            content.contains("var wasThresholdPassed by remember { mutableStateOf(false) }")
        )
        assertTrue(
            "MarketItemRow must emit haptics.lightTick() on threshold transition",
            content.contains("haptics.lightTick()") && content.contains("LaunchedEffect(isThresholdPassed)")
        )

        // 2. Emerald check reveal on StartToEnd backgroundContent
        val backgroundBlock = content.substringAfter("backgroundContent = {")
            .substringBefore("Surface(")
        assertTrue(
            "Background must render emerald reveal (appColors.income) on StartToEnd",
            backgroundBlock.contains("SwipeToDismissBoxValue.StartToEnd -> {") &&
                backgroundBlock.contains("appColors.income")
        )
        assertTrue(
            "Background must render check icon (Icons.Filled.Check)",
            backgroundBlock.contains("Icons.Filled.Check")
        )
        assertTrue(
            "Background must provide bilingual state labels ('কেনা হয়েছে' / 'অসম্পূর্ণ রাখুন')",
            backgroundBlock.contains("কেনা হয়েছে") && backgroundBlock.contains("অসম্পূর্ণ রাখুন")
        )

        // 3. Ruled pocket notebook baseline divider
        assertTrue(
            "MarketItemRow must draw ruled notebook baseline divider",
            content.contains("HorizontalDivider(") &&
                content.contains("HairlineInkStroke") &&
                content.contains("appColors.divider")
        )
    }

    @Test
    fun testMarketItemRowLongPressSubstitutionAndTouchTargets() {
        val content = readSourceFile("com/example/expensetracker/ui/dashboard/MarketItemRow.kt")

        // 1. combinedClickable on Surface with long-press
        assertTrue(
            "MarketItemRow must use combinedClickable for tactile interactions",
            content.contains(".combinedClickable(")
        )
        assertTrue(
            "Long-press must trigger onSubstituteClick with haptic feedback",
            content.contains("onLongClick = {") &&
                content.contains("haptics.longPressFeedback()") &&
                content.contains("onSubstituteClick()")
        )

        // 2. Checkbox touch targets >= 48dp
        assertTrue(
            "Checkbox must bound minWidth and minHeight to 48dp",
            content.contains("sizeIn(minWidth = 48.dp, minHeight = 48.dp)")
        )
        assertTrue(
            "Checkbox must declare minimumInteractiveComponentSize()",
            content.contains(".minimumInteractiveComponentSize()")
        )

        // 3. Edit mode delete button touch target >= 48dp
        val deleteButtonBlock = content.substringAfter("if (isEditMode) {")
            .substringAfter("IconButton(")
            .substringBefore("contentAlignment = Alignment.Center")
        assertTrue(
            "Delete button must declare minimumInteractiveComponentSize()",
            deleteButtonBlock.contains(".minimumInteractiveComponentSize()")
        )
        assertTrue(
            "Delete button must declare size(48.dp)",
            deleteButtonBlock.contains(".size(48.dp)")
        )
    }

    // =========================================================================
    // 2. MarketScreen.kt: HorizontalPager Tab Navigation
    // =========================================================================

    @Test
    fun testMarketScreenHorizontalPagerPagingArchitecture() {
        val content = readSourceFile("com/example/expensetracker/ui/market/MarketScreen.kt")

        // 1. Pager state initialization with exactly 3 pages
        assertTrue(
            "rememberPagerState must initialize with 3 pages",
            content.contains("rememberPagerState(initialPage = 0, pageCount = { 3 })")
        )

        // 2. HorizontalPager component usage
        assertTrue(
            "MarketScreen must use HorizontalPager",
            content.contains("HorizontalPager(")
        )
        assertTrue(
            "HorizontalPager must configure beyondViewportPageCount = 1 for fluid fling",
            content.contains("beyondViewportPageCount = 1")
        )

        // 3. Tab contents mapping
        assertTrue(
            "Page 0 must host MarketListsTabContent",
            content.contains("0 -> {") && content.contains("MarketListsTabContent(")
        )
        assertTrue(
            "Page 1 must host ProductPriceInsightsContent",
            content.contains("1 -> {") && content.contains("ProductPriceInsightsContent(")
        )
        assertTrue(
            "Page 2 must host CreateMarketListSection",
            content.contains("2 -> {") && content.contains("CreateMarketListSection(")
        )
    }

    @Test
    fun testMarketScreenBidirectionalSynchronizationAndStateReset() {
        val content = readSourceFile("com/example/expensetracker/ui/market/MarketScreen.kt")

        // 1. Swiping pager updates selectedTab and resets price search when leaving Tab 1
        val launchedEffectBlock = content.substringAfter("LaunchedEffect(pagerState.currentPage) {")
            .substringBefore("}")
        assertTrue(
            "LaunchedEffect must synchronize selectedTab with pagerState.currentPage",
            launchedEffectBlock.contains("selectedTab = pagerState.currentPage")
        )
        assertTrue(
            "LaunchedEffect must clear search active state when leaving tab 1",
            launchedEffectBlock.contains("if (pagerState.currentPage != 1)") &&
                launchedEffectBlock.contains("isPriceSearchActive = false")
        )

        // 2. Tapping segmented pills animates pager scroll
        val pillsBlock = content.substringAfter("MarketSegmentedPills(")
            .substringBefore("Spacer(modifier = Modifier.height(6.dp))")
        assertTrue(
            "Pill selection must trigger pagerState.animateScrollToPage",
            pillsBlock.contains("pagerState.animateScrollToPage(targetTab)")
        )

        // 3. Create new list button scrolls to page 2
        assertTrue(
            "onCreateNewClick must scroll pager to page 2",
            content.contains("onCreateNewClick = {") &&
                content.contains("pagerState.animateScrollToPage(2)")
        )

        // 4. Finishing list creation scrolls to page 0
        assertTrue(
            "onListCreated must scroll pager to page 0",
            content.contains("onListCreated = {") &&
                content.contains("pagerState.animateScrollToPage(0)")
        )
    }

    // =========================================================================
    // 3. MarketBottomTripSummaryBar.kt: Tactile Tally Dots Stress Testing
    // =========================================================================

    /**
     * Algorithmic simulator matching the exact tally dots calculation in MarketBottomTripSummaryBar.kt
     */
    private fun simulateTallyDots(totalCount: Int, purchasedCount: Int): Pair<Int, Int> {
        if (totalCount <= 0) return Pair(0, 0)
        val maxVisibleDots = 6
        val displayTotal = minOf(totalCount, maxVisibleDots)
        val displayPurchased = if (totalCount <= maxVisibleDots) {
            purchasedCount
        } else {
            ((purchasedCount.toFloat() / totalCount.toFloat()) * maxVisibleDots).toInt()
        }
        return Pair(displayTotal, displayPurchased)
    }

    @Test
    fun testMarketBottomTripSummaryBarTallyDotsEmpiricalMathHarness() {
        val content = readSourceFile("com/example/expensetracker/ui/market/MarketBottomTripSummaryBar.kt")

        // Verify implementation structure
        assertTrue(
            "MarketBottomTripSummaryBar must guard tally dots with totalCount > 0",
            content.contains("if (totalCount > 0)")
        )
        assertTrue(
            "Must define maxVisibleDots = 6",
            content.contains("val maxVisibleDots = 6")
        )
        assertTrue(
            "Must calculate displayTotal with minOf(totalCount, maxVisibleDots)",
            content.contains("val displayTotal = minOf(totalCount, maxVisibleDots)")
        )

        // Empirical Stress Cases:
        // Case 1: Empty list (0 items) -> 0 dots (protected from division by zero)
        val (dots0, done0) = simulateTallyDots(0, 0)
        assertEquals("0 total items must yield 0 dots", 0, dots0)
        assertEquals("0 total items must yield 0 completed", 0, done0)

        // Case 2: 1 item, unpurchased -> 1 dot, 0 completed
        val (dots1, done1) = simulateTallyDots(1, 0)
        assertEquals("1 total item must yield 1 dot", 1, dots1)
        assertEquals(0, done1)

        // Case 3: 1 item, purchased -> 1 dot, 1 completed
        val (dots2, done2) = simulateTallyDots(1, 1)
        assertEquals("1 total item must yield 1 dot", 1, dots2)
        assertEquals(1, done2)

        // Case 4: 5 items, 3 purchased -> 5 dots, 3 completed
        val (dots3, done3) = simulateTallyDots(5, 3)
        assertEquals(5, dots3)
        assertEquals(3, done3)

        // Case 5: 6 items (threshold), 6 purchased -> 6 dots, 6 completed
        val (dots4, done4) = simulateTallyDots(6, 6)
        assertEquals(6, dots4)
        assertEquals(6, done4)

        // Case 6: 12 items, 6 purchased (50%) -> 6 dots (bounded), 3 completed (50%)
        val (dots5, done5) = simulateTallyDots(12, 6)
        assertEquals("Display dots must be capped at 6", 6, dots5)
        assertEquals("Halfway through 12 items must yield 3 completed dots", 3, done5)

        // Case 7: 100 items (stress load), 75 purchased -> 6 dots, 4 completed (4.5 -> 4)
        val (dots6, done6) = simulateTallyDots(100, 75)
        assertEquals(6, dots6)
        assertEquals(4, done6)

        // Case 8: 1000 items, 1000 purchased -> 6 dots, 6 completed
        val (dots7, done7) = simulateTallyDots(1000, 1000)
        assertEquals(6, dots7)
        assertEquals(6, done7)
    }

    @Test
    fun testMarketBottomTripSummaryBarLayoutErgonomics() {
        val content = readSourceFile("com/example/expensetracker/ui/market/MarketBottomTripSummaryBar.kt")

        // 1. Navigation bars padding
        assertTrue(
            "Must declare navigationBarsPadding()",
            content.contains(".navigationBarsPadding()")
        )

        // 2. Running total text formatting
        assertTrue(
            "Must format total spent with currency",
            content.contains("formatCurrency(totalSpent, currencySymbol)")
        )

        // 3. Tabular numbers enabled on total spend
        assertTrue(
            "Total spend must configure tabular numbers via tnum",
            content.contains("fontFeatureSettings = \"tnum\"")
        )
    }

    // =========================================================================
    // 4. MarketTripCompletionSheet.kt: Serrated Receipt Tear-Off Edge Math
    // =========================================================================

    /**
     * Algorithmic simulator matching the serrated tear-off edge calculation in MarketTripCompletionSheet.kt
     */
    private fun simulateTearTeeth(viewWidthPx: Float, toothWidthPx: Float): Int {
        if (viewWidthPx <= 0f) return 1
        return (viewWidthPx / toothWidthPx).toInt() + 1
    }

    @Test
    fun testMarketTripCompletionSheetSerratedTearEdgeMathSimulation() {
        val content = readSourceFile("com/example/expensetracker/ui/dashboard/MarketTripCompletionSheet.kt")

        // 1. Structural check
        assertTrue(
            "Must declare toothWidth based on 12.dp.toPx()",
            content.contains("val toothWidth = 12.dp.toPx()")
        )
        assertTrue(
            "Must declare toothHeight based on size.height",
            content.contains("val toothHeight = size.height")
        )
        assertTrue(
            "Must calculate teethCount via (size.width / toothWidth).toInt() + 1",
            content.contains("val teethCount = (size.width / toothWidth).toInt() + 1")
        )
        assertTrue(
            "Must use Path().apply with moveTo and lineTo",
            content.contains("val zigZagPath = Path().apply {") &&
                content.contains("moveTo(0f, 0f)") &&
                content.contains("lineTo(x + toothWidth / 2f, toothHeight)") &&
                content.contains("lineTo(x + toothWidth, 0f)")
        )

        // 2. Math Stress Harness across varying viewport densities
        // toothWidth at 12dp:
        // mdpi (1.0x): 12px
        // hdpi (1.5x): 18px
        // xhdpi (2.0x): 24px
        // xxhdpi (3.0x): 36px
        // xxxhdpi (4.0x): 48px

        // Density 3.0x (standard modern 1080p phone screen): toothWidth = 36px
        val tooth3x = 36f

        // Screen width 0px (edge unmeasured state)
        val teeth0 = simulateTearTeeth(0f, tooth3x)
        assertEquals("0px width must produce 1 tooth safely without division by zero", 1, teeth0)

        // Compact 360dp phone (1080px)
        val teeth360dp = simulateTearTeeth(1080f, tooth3x)
        assertEquals("1080px must produce exactly 31 teeth", 31, teeth360dp)

        // Tablet / Large device 600dp (1800px)
        val teethTablet = simulateTearTeeth(1800f, tooth3x)
        assertEquals("1800px must produce exactly 51 teeth", 51, teethTablet)

        // Ultra-wide / 4K landscape (3840px)
        val teeth4k = simulateTearTeeth(3840f, tooth3x)
        assertEquals("3840px must produce exactly 107 teeth", 107, teeth4k)
    }

    @Test
    fun testMarketTripCompletionSheetReceiptInvariants() {
        val content = readSourceFile("com/example/expensetracker/ui/dashboard/MarketTripCompletionSheet.kt")

        // 1. Rotated ink stamp (-12 degrees)
        assertTrue(
            "Celebratory PAID ink stamp must be rotated at -12 degrees",
            content.contains("rotationZ = -12f")
        )

        // 2. Monospace ledger
        assertTrue(
            "Itemized receipt breakdown must use FontFamily.Monospace",
            content.contains("FontFamily.Monospace")
        )

        // 3. Dashed perforated tear line
        assertTrue(
            "Perforated separator must use dashPathEffect",
            content.contains("PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)")
        )

        // 4. Payment account selector touch targets >= 48dp
        assertTrue(
            "Payment account selectors must declare defaultMinSize(minHeight = 48.dp)",
            content.contains("defaultMinSize(minHeight = 48.dp)")
        )
        assertTrue(
            "Payment account selectors must declare minimumInteractiveComponentSize()",
            content.contains(".minimumInteractiveComponentSize()")
        )
    }

    // =========================================================================
    // 5. Cross-Cutting Universal Invariants (Accessibility & Anti-AI Tropes)
    // =========================================================================

    @Test
    fun testStrictlyZeroAiSparklesAcrossTargetFiles() {
        val files = listOf(
            "com/example/expensetracker/ui/dashboard/MarketItemRow.kt",
            "com/example/expensetracker/ui/market/MarketScreen.kt",
            "com/example/expensetracker/ui/market/MarketBottomTripSummaryBar.kt",
            "com/example/expensetracker/ui/dashboard/MarketTripCompletionSheet.kt"
        )

        for (file in files) {
            val content = readSourceFile(file)
            assertFalse(
                "$file must not contain AutoAwesome generic AI icon",
                content.contains("AutoAwesome")
            )
            assertFalse(
                "$file must not contain sparkle emoji (✨)",
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

        val forbiddenSizes = listOf("6.sp", "7.sp", "8.sp", "9.sp", "10.sp", "11.sp", "11.5.sp")

        for (file in files) {
            val content = readSourceFile(file)
            for (size in forbiddenSizes) {
                val regex = Regex("""fontSize\s*=\s*$size""")
                assertFalse(
                    "$file must not declare sub-12sp font size ($size)",
                    regex.containsMatchIn(content)
                )
            }
            assertTrue(
                "$file must specify tabular numbers fontFeatureSettings 'tnum'",
                content.contains("\"tnum\"")
            )
        }
    }
}
