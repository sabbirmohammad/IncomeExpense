package com.example.expensetracker

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Empirical adversarial test suite for Phase 2 Iteration 2 re-verification of the 10 touch targets across 9 files.
 */
class ChallengerPhase2Iteration2TouchTargetTest {

    private val basePath: String = if (File("src/main/java").exists()) "src/main/java" else "app/src/main/java"

    private fun readFile(relativePath: String): String {
        val file = File("$basePath/$relativePath")
        assertTrue("Target file ${file.path} must exist", file.exists())
        return file.readText()
    }

    // -------------------------------------------------------------------------
    // 1. BalanceCardHeaderRow.kt (Wallet toggle and Privacy toggle)
    // -------------------------------------------------------------------------
    @Test
    fun testBalanceCardHeaderRowTouchTargets() {
        val content = readFile("com/example/expensetracker/ui/components/BalanceCardHeaderRow.kt")

        // 1a. Wallet toggle pill
        assertTrue(
            "Wallet toggle Surface must apply minimumInteractiveComponentSize()",
            content.contains("Modifier\n                        .minimumInteractiveComponentSize()\n                        .clickable") ||
                    content.contains(".minimumInteractiveComponentSize()")
        )
        assertTrue(
            "Wallet toggle pill must preserve internal padding (12.dp horizontal, 6.dp vertical)",
            content.contains("Modifier.padding(horizontal = 12.dp, vertical = 6.dp)")
        )

        // 1b. Privacy toggle button
        assertFalse(
            "Privacy toggle must NOT have hardcoded sub-48dp .size(36.dp)",
            content.contains(".size(36.dp)")
        )
        assertTrue(
            "Privacy toggle Box must enforce sizeIn min 48dp x 48dp",
            content.contains("minWidth = 48.dp, minHeight = 48.dp")
        )
        assertTrue(
            "Privacy toggle Box must apply minimumInteractiveComponentSize()",
            content.contains("minimumInteractiveComponentSize()")
        )
        assertTrue(
            "Privacy toggle Icon glyph size must remain 16dp to avoid visual distortion",
            content.contains("Modifier.size(16.dp)")
        )
    }

    // -------------------------------------------------------------------------
    // 2. SettingsThemeCard.kt (Theme selector pills)
    // -------------------------------------------------------------------------
    @Test
    fun testSettingsThemeCardPillsTouchTargets() {
        val content = readFile("com/example/expensetracker/ui/settings/SettingsThemeCard.kt")

        assertTrue(
            "Theme selector pills must enforce minimumInteractiveComponentSize()",
            content.contains(".minimumInteractiveComponentSize()")
        )
        assertTrue(
            "Theme selector pills must retain .weight(1f) distribution",
            content.contains(".weight(1f)")
        )
        assertTrue(
            "Theme selector pills must have .clickable",
            content.contains(".clickable")
        )
        assertTrue(
            "Theme selector pills must maintain internal vertical padding",
            content.contains("padding(vertical = 9.dp)")
        )
    }

    // -------------------------------------------------------------------------
    // 3. AnalyticsBreakdownToggle.kt (Breakdown mode toggle)
    // -------------------------------------------------------------------------
    @Test
    fun testAnalyticsBreakdownToggleTouchTargets() {
        val content = readFile("com/example/expensetracker/ui/analytics/AnalyticsBreakdownToggle.kt")

        assertTrue(
            "Breakdown toggle Box must enforce minimumInteractiveComponentSize()",
            content.contains(".minimumInteractiveComponentSize()")
        )
        assertTrue(
            "Breakdown toggle Box must have .clickable",
            content.contains(".clickable { onModeChange(mode) }")
        )
        assertTrue(
            "Breakdown toggle Box must retain .weight(1f)",
            content.contains(".weight(1f)")
        )
    }

    // -------------------------------------------------------------------------
    // 4. MarketTripCardHeader.kt (Options menu IconButton)
    // -------------------------------------------------------------------------
    @Test
    fun testMarketTripCardHeaderOptionsIconButton() {
        val content = readFile("com/example/expensetracker/ui/transactions/MarketTripCardHeader.kt")

        assertFalse(
            "MarketTripCardHeader options menu IconButton must NOT have hardcoded .size(32.dp)",
            content.contains(".size(32.dp)")
        )
        assertTrue(
            "MarketTripCardHeader options menu IconButton must be present",
            content.contains("IconButton(\n                    onClick = { onShowOptionsMenu(true) }")
        )
        assertTrue(
            "MarketTripCardHeader options Icon size must remain 18dp",
            content.contains("Modifier.size(18.dp)")
        )
    }

    // -------------------------------------------------------------------------
    // 5. AccountCardItem.kt (Options menu IconButton)
    // -------------------------------------------------------------------------
    @Test
    fun testAccountCardItemOptionsIconButton() {
        val content = readFile("com/example/expensetracker/ui/accounts/AccountCardItem.kt")

        assertFalse(
            "AccountCardItem options menu IconButton must NOT have hardcoded .size(44.dp)",
            content.contains(".size(44.dp)")
        )
        assertTrue(
            "AccountCardItem options menu IconButton must be present",
            content.contains("IconButton(\n                                onClick = { showMenu = true }")
        )
        assertTrue(
            "AccountCardItem options Icon size must remain 18dp",
            content.contains("Modifier.size(18.dp)")
        )
    }

    // -------------------------------------------------------------------------
    // 6. SettingsCategoryRowItem.kt (Edit and delete IconButtons)
    // -------------------------------------------------------------------------
    @Test
    fun testSettingsCategoryRowItemIconButtons() {
        val content = readFile("com/example/expensetracker/ui/settings/SettingsCategoryRowItem.kt")

        assertFalse(
            "SettingsCategoryRowItem Edit/Delete IconButtons must NOT have hardcoded .size(32.dp)",
            content.contains(".size(32.dp)")
        )
        assertTrue(
            "Edit button Icon must remain 16dp",
            content.contains("Icons.Outlined.Edit") && content.contains("Modifier.size(16.dp)")
        )
        assertTrue(
            "Delete button Icon must remain 16dp",
            content.contains("Icons.Outlined.Delete") && content.contains("Modifier.size(16.dp)")
        )
    }

    // -------------------------------------------------------------------------
    // 7. PayableItemBottomBar.kt (Call button and Record Payment pill)
    // -------------------------------------------------------------------------
    @Test
    fun testPayableItemBottomBarTouchTargets() {
        val content = readFile("com/example/expensetracker/ui/receivables/PayableItemBottomBar.kt")

        // 7a. Call contact IconButton
        assertFalse(
            "Call IconButton must NOT have hardcoded .size(32.dp)",
            content.contains(".size(32.dp)")
        )
        assertTrue(
            "Call Icon size must remain 16dp",
            content.contains("Icons.Filled.Call") && content.contains("Modifier.size(16.dp)")
        )

        // 7b. Record Payment pill
        assertTrue(
            "Record payment Surface must enforce minimumInteractiveComponentSize()",
            content.contains(".minimumInteractiveComponentSize()")
        )
        assertTrue(
            "Record payment Surface must have .clickable",
            content.contains(".clickable {\n                            haptics.mediumClick()\n                            onRecordPayment()")
        )
    }

    // -------------------------------------------------------------------------
    // 8. UnitAndQuantitySections.kt (Stepper IconButtons)
    // -------------------------------------------------------------------------
    @Test
    fun testUnitAndQuantitySectionsStepperIconButtons() {
        val content = readFile("com/example/expensetracker/ui/components/UnitAndQuantitySections.kt")

        assertFalse(
            "UnitAndQuantitySections stepper IconButtons must NOT have hardcoded .size(44.dp)",
            content.contains(".size(44.dp)")
        )
        assertTrue(
            "Decrease stepper must have RoundedCornerShape(10.dp) styling preserved",
            content.contains("Icons.Filled.Remove") && content.contains("RoundedCornerShape(10.dp)")
        )
        assertTrue(
            "Increase stepper must have RoundedCornerShape(10.dp) styling preserved",
            content.contains("Icons.Filled.Add") && content.contains("RoundedCornerShape(10.dp)")
        )
    }

    // -------------------------------------------------------------------------
    // 9. InPlaceCalculator.kt (Keypad buttons height >= 48dp)
    // -------------------------------------------------------------------------
    @Test
    fun testInPlaceCalculatorKeypadButtonsHeight() {
        val content = readFile("com/example/expensetracker/ui/components/InPlaceCalculator.kt")

        assertFalse(
            "InPlaceCalculator keypad buttons must NOT have .height(44.dp) or .size(38.dp)",
            content.contains(".height(44.dp)") || content.contains(".size(38.dp)")
        )
        assertTrue(
            "InPlaceCalculator keypad buttons must have .height(48.dp)",
            content.contains(".height(48.dp)")
        )
        assertTrue(
            "InPlaceCalculator preview badge must apply minimumInteractiveComponentSize()",
            content.contains("CalculatorPreviewBadge") && content.contains(".minimumInteractiveComponentSize()")
        )
    }
}
