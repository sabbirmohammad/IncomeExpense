package com.example.expensetracker

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class Phase2P1EnhancementsTest {

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
    fun testOneUiChipsIncomeAndExpenseArrowSemantics() {
        val file = findSourceFile("app/src/main/java/com/example/expensetracker/ui/components/OneUiChips.kt")
        assertTrue("OneUiChips.kt must exist", file.exists())
        val content = file.readText()

        // Income must be ArrowUpward (growth/incoming) and Expense must be ArrowDownward
        assertTrue(
            "Income should map to ArrowUpward and Expense to ArrowDownward",
            content.contains("val icon = if (isIncome) Icons.Outlined.ArrowUpward else Icons.Outlined.ArrowDownward")
        )
        assertFalse(
            "Income must not map to ArrowDownward",
            content.contains("val icon = if (isIncome) Icons.Outlined.ArrowDownward else Icons.Outlined.ArrowUpward")
        )
    }

    @Test
    fun testTransactionTypeSwitcherSemanticsAndContrast() {
        val file = findSourceFile("app/src/main/java/com/example/expensetracker/ui/transactions/TransactionTypeSwitcher.kt")
        assertTrue("TransactionTypeSwitcher.kt must exist", file.exists())
        val content = file.readText()

        // Expense tab must use ArrowDownward
        assertTrue(
            "Expense tab must use ArrowDownward",
            content.contains("imageVector = Icons.Outlined.ArrowDownward")
        )
        // Income tab must use ArrowUpward
        assertTrue(
            "Income tab must use ArrowUpward",
            content.contains("imageVector = Icons.Outlined.ArrowUpward")
        )
        // Adaptive Income active content color for WCAG contrast in dark mode
        assertTrue(
            "Income tab must have dark mode adaptive content color 0xFF052B14",
            content.contains("if (appColors.isDark) Color(0xFF052B14) else Color.White")
        )
    }

    @Test
    fun testGlobalActionChooserSheetArrowSemantics() {
        val file = findSourceFile("app/src/main/java/com/example/expensetracker/ui/navigation/GlobalActionChooserSheet.kt")
        assertTrue("GlobalActionChooserSheet.kt must exist", file.exists())
        val content = file.readText()

        // Add Expense Card must use ArrowDownward
        val expenseIndex = content.indexOf("// 1. Add Expense Card")
        val incomeIndex = content.indexOf("// 2. Add Income Card")
        assertTrue("Must contain Add Expense and Add Income", expenseIndex != -1 && incomeIndex != -1)

        val expenseSnippet = content.substring(expenseIndex, expenseIndex + 250)
        assertTrue(
            "Add Expense card must use ArrowDownward",
            expenseSnippet.contains("Icons.Outlined.ArrowDownward")
        )

        val incomeSnippet = content.substring(incomeIndex, incomeIndex + 250)
        assertTrue(
            "Add Income card must use ArrowUpward",
            incomeSnippet.contains("Icons.Outlined.ArrowUpward")
        )
    }

    @Test
    fun testBillsSummaryHeroCardBengaliNumerals() {
        val file = findSourceFile("app/src/main/java/com/example/expensetracker/ui/bills/BillsSummaryHeroCard.kt")
        assertTrue("BillsSummaryHeroCard.kt must exist", file.exists())
        val content = file.readText()

        assertTrue(
            "Must import convertToBengaliDigits",
            content.contains("import com.example.expensetracker.ui.components.convertToBengaliDigits")
        )
        assertTrue(
            "Must import globalBengaliNumeralsEnabled",
            content.contains("import com.example.expensetracker.ui.components.globalBengaliNumeralsEnabled")
        )
        assertTrue(
            "Must pass isBn to formatCurrency",
            content.contains("formatCurrency(totalMonthlyCommitments, currencySymbol, isBn)")
        )
        assertTrue(
            "Must format active count with convertToBengaliDigits",
            content.contains("val activeCountDisp = if (isBn) convertToBengaliDigits(activeBillsCount.toString())")
        )
        assertTrue(
            "Must format overdue count with convertToBengaliDigits",
            content.contains("val overdueDisp = if (isBn) convertToBengaliDigits(overdueCount.toString())")
        )
    }

    @Test
    fun testAccountsHeroCardBengaliNumerals() {
        val file = findSourceFile("app/src/main/java/com/example/expensetracker/ui/accounts/AccountsHeroCard.kt")
        assertTrue("AccountsHeroCard.kt must exist", file.exists())
        val content = file.readText()

        assertTrue(
            "Must import convertToBengaliDigits",
            content.contains("import com.example.expensetracker.ui.components.convertToBengaliDigits")
        )
        assertTrue(
            "Must import globalBengaliNumeralsEnabled",
            content.contains("import com.example.expensetracker.ui.components.globalBengaliNumeralsEnabled")
        )
        assertTrue(
            "Must pass isBn to formatCurrency for totalNetWorth",
            content.contains("formatCurrency(totalNetWorth, currencySymbol, isBn)")
        )
        assertTrue(
            "Must format account count with convertToBengaliDigits",
            content.contains("val countDisp = if (isBangla || globalBengaliNumeralsEnabled) convertToBengaliDigits(count.toString())")
        )
    }

    @Test
    fun testTouchTargetSizeComplianceAcrossTopBarsAndCards() {
        val txTopBar = findSourceFile("app/src/main/java/com/example/expensetracker/ui/transactions/TransactionsTopBar.kt")
        assertTrue("TransactionsTopBar.kt must exist", txTopBar.exists())
        val txContent = txTopBar.readText()
        assertTrue(
            "TransactionsTopBar must use minimumInteractiveComponentSize for action buttons",
            txContent.contains("minimumInteractiveComponentSize()")
        )

        val analyticsTopBar = findSourceFile("app/src/main/java/com/example/expensetracker/ui/analytics/AnalyticsTopBar.kt")
        assertTrue("AnalyticsTopBar.kt must exist", analyticsTopBar.exists())
        val analyticsContent = analyticsTopBar.readText()
        assertTrue(
            "AnalyticsTopBar must use minimumInteractiveComponentSize for share button",
            analyticsContent.contains("minimumInteractiveComponentSize()")
        )

        val savingsCard = findSourceFile("app/src/main/java/com/example/expensetracker/ui/savings/SavingsGoalCard.kt")
        assertTrue("SavingsGoalCard.kt must exist", savingsCard.exists())
        val savingsContent = savingsCard.readText()
        assertTrue(
            "SavingsGoalCard must use minimumInteractiveComponentSize for contribute button",
            savingsContent.contains("minimumInteractiveComponentSize()")
        )
    }
}
