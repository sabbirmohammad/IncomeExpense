package com.example.expensetracker

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class Phase3MicroInteractionsAndPolishTest {

    private fun findSourceFile(subpath: String): File {
        val normalized = subpath.removePrefix("app/")
        val candidates = listOf(
            File(normalized),
            File("app", normalized),
            File(System.getProperty("user.dir"), normalized),
            File(System.getProperty("user.dir"), "app/$normalized"),
            File("../$normalized")
        )
        return candidates.firstOrNull { it.exists() } ?: File(normalized)
    }

    @Test
    fun testCalculatorToggleButtonHapticsAndTouchTarget() {
        val file = findSourceFile("app/src/main/java/com/example/expensetracker/ui/components/InPlaceCalculator.kt")
        assertTrue("InPlaceCalculator.kt must exist", file.exists())
        val content = file.readText()

        val toggleFnIndex = content.indexOf("fun CalculatorToggleButton")
        assertTrue("Must define CalculatorToggleButton", toggleFnIndex != -1)
        val toggleSnippet = content.substring(toggleFnIndex, toggleFnIndex + 1000)

        assertTrue(
            "CalculatorToggleButton must use minimumInteractiveComponentSize for 48dp target",
            toggleSnippet.contains("minimumInteractiveComponentSize()")
        )
        assertTrue(
            "CalculatorToggleButton must call lightTick haptics on click",
            toggleSnippet.contains("haptics.lightTick()")
        )
    }

    @Test
    fun testCalculatorPreviewBadgeHaptics() {
        val file = findSourceFile("app/src/main/java/com/example/expensetracker/ui/components/InPlaceCalculator.kt")
        assertTrue("InPlaceCalculator.kt must exist", file.exists())
        val content = file.readText()

        val badgeFnIndex = content.indexOf("fun CalculatorPreviewBadge")
        assertTrue("Must define CalculatorPreviewBadge", badgeFnIndex != -1)
        val badgeSnippet = content.substring(badgeFnIndex, badgeFnIndex + 1500)

        assertTrue(
            "CalculatorPreviewBadge must call mediumClick haptics when applying calculation",
            badgeSnippet.contains("haptics.mediumClick()")
        )
    }

    @Test
    fun testCalculatorKeypadBarEqualsContrast() {
        val file = findSourceFile("app/src/main/java/com/example/expensetracker/ui/components/InPlaceCalculator.kt")
        assertTrue("InPlaceCalculator.kt must exist", file.exists())
        val content = file.readText()

        assertTrue(
            "Equals button must have adaptive dark mode contrast when accentColor is Income",
            content.contains("if (accentColor == appColors.income && appColors.isDark) Color(0xFF052B14) else Color.White")
        )
    }

    @Test
    fun testQuickChipsTactileHaptics() {
        val receivableChips = findSourceFile("app/src/main/java/com/example/expensetracker/ui/receivables/ReceivablePaymentQuickChips.kt")
        assertTrue("ReceivablePaymentQuickChips.kt must exist", receivableChips.exists())
        val recContent = receivableChips.readText()
        assertTrue(
            "ReceivablePaymentQuickChips must call lightTick on click",
            recContent.contains("haptics.lightTick()")
        )

        val contributionChips = findSourceFile("app/src/main/java/com/example/expensetracker/ui/savings/ContributionQuickChips.kt")
        assertTrue("ContributionQuickChips.kt must exist", contributionChips.exists())
        val contContent = contributionChips.readText()
        assertTrue(
            "ContributionQuickChips must call lightTick on click",
            contContent.contains("haptics.lightTick()")
        )

        val goalAmount = findSourceFile("app/src/main/java/com/example/expensetracker/ui/savings/GoalTargetAmountSection.kt")
        assertTrue("GoalTargetAmountSection.kt must exist", goalAmount.exists())
        val goalContent = goalAmount.readText()
        assertTrue(
            "GoalTargetAmountSection must call lightTick when toggling calculator",
            goalContent.contains("haptics.lightTick()")
        )
    }

    @Test
    fun testEmptyStateCardHapticsAndAutoMirroredIcon() {
        val file = findSourceFile("app/src/main/java/com/example/expensetracker/ui/components/OneUiComponents.kt")
        assertTrue("OneUiComponents.kt must exist", file.exists())
        val content = file.readText()

        val emptyCardIndex = content.indexOf("fun EmptyStateCard")
        assertTrue("Must define EmptyStateCard", emptyCardIndex != -1)
        val emptySnippet = content.substring(emptyCardIndex)

        assertTrue(
            "EmptyStateCard CTA button must invoke mediumClick haptics",
            emptySnippet.contains("haptics.mediumClick()")
        )
        assertTrue(
            "EmptyStateCard default icon must use AutoMirrored ReceiptLong",
            emptySnippet.contains("Icons.AutoMirrored.Outlined.ReceiptLong")
        )
    }
}
