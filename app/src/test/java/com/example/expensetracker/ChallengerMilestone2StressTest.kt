package com.example.expensetracker

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Challenger Milestone 2 Stress Test:
 * Empirical verification of Handcrafted Market Checklist, Department-Aisle Stall Organization,
 * and Persistent In-Store Shopping Dock.
 */
class ChallengerMilestone2StressTest {

    private fun readFile(relativePath: String): String {
        val candidates = listOf(
            File("src/main/java/$relativePath"),
            File("app/src/main/java/$relativePath"),
            File("../app/src/main/java/$relativePath")
        )
        val file = candidates.find { it.exists() }
        assertTrue("Expected file to exist for $relativePath", file != null && file.exists())
        return file!!.readText()
    }

    @Test
    fun testMarketDepartmentSectionHeaderInvariants() {
        val content = readFile("com/example/expensetracker/ui/market/MarketDepartmentSectionHeader.kt")

        // 1. Department header composable definition
        assertTrue(
            "MarketDepartmentSectionHeader must accept dept, completedCount, totalCount, isExpanded, onToggleExpand, isBangla",
            content.contains("fun MarketDepartmentSectionHeader(") &&
                    content.contains("dept: DepartmentMeta") &&
                    content.contains("completedCount: Int") &&
                    content.contains("totalCount: Int") &&
                    content.contains("isExpanded: Boolean") &&
                    content.contains("onToggleExpand: () -> Unit") &&
                    content.contains("isBangla: Boolean")
        )

        // 2. Live progress counter and Bengali digit support
        assertTrue(
            "MarketDepartmentSectionHeader must format live progress with Bengali numerals",
            content.contains("convertToBengaliDigits(completedCount.toString())") &&
                    content.contains("convertToBengaliDigits(totalCount.toString())") &&
                    content.contains("টি সম্পন্ন")
        )

        // 3. Smooth animated chevron arrow rotation
        assertTrue(
            "MarketDepartmentSectionHeader must animate chevron rotation on collapse/expand",
            content.contains("arrowRotation") &&
                    content.contains("rotate(arrowRotation)") &&
                    content.contains("Icons.Filled.KeyboardArrowDown")
        )

        // 4. Living Ledger design tokens and 48dp touch target
        assertTrue(
            "MarketDepartmentSectionHeader must use cardBackground, HairlineInkStroke, and minimumInteractiveComponentSize",
            content.contains("cardBackground") &&
                    content.contains("HairlineInkStroke") &&
                    content.contains("divider") &&
                    content.contains("minimumInteractiveComponentSize()")
        )
    }

    @Test
    fun testMarketBottomTripSummaryBarInvariants() {
        val content = readFile("com/example/expensetracker/ui/market/MarketBottomTripSummaryBar.kt")

        // 1. Persistent summary bar composable definition
        assertTrue(
            "MarketBottomTripSummaryBar must accept totalSpent, totalCount, purchasedCount, currencySymbol, isBangla, onFinishShopping",
            content.contains("fun MarketBottomTripSummaryBar(") &&
                    content.contains("totalSpent: Double") &&
                    content.contains("totalCount: Int") &&
                    content.contains("purchasedCount: Int") &&
                    content.contains("currencySymbol: String") &&
                    content.contains("isBangla: Boolean") &&
                    content.contains("onFinishShopping: () -> Unit")
        )

        // 2. Running total spend and tabular numbers
        assertTrue(
            "MarketBottomTripSummaryBar must format total spent with formatCurrency and tnum",
            content.contains("formatCurrency(totalSpent, currencySymbol)") &&
                    content.contains("\"tnum\"")
        )

        // 3. Embedded MarketFinishButton
        assertTrue(
            "MarketBottomTripSummaryBar must embed MarketFinishButton",
            content.contains("MarketFinishButton(")
        )

        // 4. Navigation bar padding for ergonomic thumb-zone docking
        assertTrue(
            "MarketBottomTripSummaryBar must apply navigationBarsPadding",
            content.contains("navigationBarsPadding()")
        )
    }

    @Test
    fun testMarketItemRowLivingLedgerOverhaul() {
        val content = readFile("com/example/expensetracker/ui/dashboard/MarketItemRow.kt")

        // 1. Living Ledger tokens replacing card soup
        assertTrue(
            "MarketItemRow must use cardBackground and HairlineInkStroke",
            content.contains("appColors.cardBackground") &&
                    content.contains("HairlineInkStroke") &&
                    content.contains("appColors.divider")
        )

        // 2. Spring-animated pencil strikethrough upon check-off
        assertTrue(
            "MarketItemRow must animate strikethrough using spring",
            content.contains("strikeProgress") &&
                    content.contains("spring(") &&
                    content.contains("drawLine(")
        )

        // 3. Tactile feedback with AppHaptics.mediumClick()
        assertTrue(
            "MarketItemRow must trigger tactile mediumClick on check-off and delete",
            content.contains("haptics.mediumClick()")
        )

        // 4. Circular checkbox touch target size
        assertTrue(
            "MarketItemRow checkbox must have minWidth = 48.dp, minHeight = 48.dp and minimumInteractiveComponentSize",
            content.contains("minWidth = 48.dp, minHeight = 48.dp") &&
                    content.contains("minimumInteractiveComponentSize()")
        )

        // 5. Zero AI sparkles
        assertFalse("MarketItemRow must have strictly zero AutoAwesome icons", content.contains("AutoAwesome"))
        assertFalse("MarketItemRow must have strictly zero sparkle emojis", content.contains("✨"))
    }

    @Test
    fun testMarketScreenDepartmentStallAndDockIntegration() {
        val screenContent = readFile("com/example/expensetracker/ui/market/MarketScreen.kt")

        // 1. Department stall headers integration
        assertTrue(
            "MarketScreen must integrate MarketDepartmentSectionHeader",
            screenContent.contains("MarketDepartmentSectionHeader(") &&
                    screenContent.contains("getMarketItemCategory(it.name)") &&
                    screenContent.contains("ALL_DEPARTMENTS")
        )

        // 2. Collapsible state tracking
        assertTrue(
            "MarketScreen must track collapsed department states with mutableStateMapOf",
            screenContent.contains("collapsedDepts") &&
                    screenContent.contains("mutableStateMapOf")
        )

        // 3. Persistent In-Store Shopping Dock integration
        assertTrue(
            "MarketScreen must integrate MarketBottomTripSummaryBar with embedded MarketFinishButton",
            screenContent.contains("MarketBottomTripSummaryBar(") &&
                    screenContent.contains("MarketFinishButton(")
        )

        // 4. Structural test invariant preservation
        assertTrue(
            "MarketScreen must preserve MarketShoppingHeaderHUD",
            screenContent.contains("MarketShoppingHeaderHUD(")
        )
        assertTrue(
            "MarketScreen must preserve effectiveEditMode",
            screenContent.contains("val effectiveEditMode = isEditMode || items.isEmpty()")
        )
        assertTrue(
            "MarketScreen must pass matched insight via UnitConverterEngine",
            screenContent.contains("priceInsight = UnitConverterEngine.findMatchingInsight(item.name, productPriceInsights)")
        )
    }
}
