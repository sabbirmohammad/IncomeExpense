package com.example.expensetracker

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import kotlin.math.pow

class Phase1P0EnhancementsTest {

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

    private fun relativeLuminance(r: Int, g: Int, b: Int): Double {
        fun channel(c: Int): Double {
            val s = c / 255.0
            return if (s <= 0.03928) s / 12.92 else ((s + 0.055) / 1.055).pow(2.4)
        }
        val rLin = channel(r)
        val gLin = channel(g)
        val bLin = channel(b)
        return 0.2126 * rLin + 0.7152 * gLin + 0.0722 * bLin
    }

    private fun contrastRatio(l1: Double, l2: Double): Double {
        val lighter = maxOf(l1, l2)
        val darker = minOf(l1, l2)
        return (lighter + 0.05) / (darker + 0.05)
    }

    @Test
    fun testWcagContrastRatioDarkGreenOnMintIncome() {
        // ColorSuccessIncome: #3DDC97
        val lumIncome = relativeLuminance(0x3D, 0xDC, 0x97)
        // White: #FFFFFF
        val lumWhite = relativeLuminance(0xFF, 0xFF, 0xFF)
        // Dark green: #052B14
        val lumDarkGreen = relativeLuminance(0x05, 0x2B, 0x14)

        val whiteContrast = contrastRatio(lumWhite, lumIncome)
        val darkGreenContrast = contrastRatio(lumIncome, lumDarkGreen)

        // White on bright mint fails WCAG AA (needs >= 4.5:1)
        assertTrue(
            "White on mint green fails WCAG AA contrast ($whiteContrast < 4.5)",
            whiteContrast < 3.0
        )
        // Dark green #052B14 provides robust contrast >= 7.0:1 (passes WCAG AAA)
        assertTrue(
            "Dark green #052B14 must exceed WCAG AAA contrast ratio of 7.0 ($darkGreenContrast)",
            darkGreenContrast >= 7.0
        )
    }

    @Test
    fun testMarketFinishButtonDarkModeContrast() {
        val file = findSourceFile("src/main/java/com/example/expensetracker/ui/dashboard/MarketFinishButton.kt")
        assertTrue("MarketFinishButton.kt must exist", file.exists())
        val content = file.readText()

        assertTrue(
            "MarketFinishButton must declare adaptive contentColor for dark mode contrast",
            content.contains("if (appColors.isDark) Color(0xFF052B14) else Color.White")
        )
        assertTrue(
            "MarketFinishButton ButtonDefaults must specify contentColor",
            content.contains("contentColor = contentColor")
        )
        assertTrue(
            "MarketFinishButton Icon must use adaptive contentColor tint",
            content.contains("tint = contentColor")
        )
        assertTrue(
            "MarketFinishButton Text must use adaptive contentColor",
            content.contains("color = contentColor")
        )
    }

    @Test
    fun testPayBillSheetDarkModeContrast() {
        val file = findSourceFile("src/main/java/com/example/expensetracker/ui/bills/PayBillSheet.kt")
        assertTrue("PayBillSheet.kt must exist", file.exists())
        val content = file.readText()

        assertTrue(
            "PayBillSheet must declare adaptive buttonContentColor for dark mode contrast",
            content.contains("val buttonContentColor = if (appColors.isDark) Color(0xFF052B14) else Color.White")
        )
        assertTrue(
            "PayBillSheet Button colors must set contentColor = buttonContentColor",
            content.contains("contentColor = buttonContentColor")
        )
        assertTrue(
            "PayBillSheet Button icon tint must be buttonContentColor",
            content.contains("tint = buttonContentColor")
        )
        assertTrue(
            "PayBillSheet Button text color must be buttonContentColor",
            content.contains("color = buttonContentColor")
        )
    }

    @Test
    fun testTransactionBottomActionBarIncomeContrast() {
        val file = findSourceFile("src/main/java/com/example/expensetracker/ui/transactions/TransactionBottomActionBar.kt")
        assertTrue("TransactionBottomActionBar.kt must exist", file.exists())
        val content = file.readText()

        assertTrue(
            "TransactionBottomActionBar must detect income condition",
            content.contains("val isIncome = !isExpense || typeAccentColor == appColors.income")
        )
        assertTrue(
            "TransactionBottomActionBar must declare adaptive actionContentColor for dark mode income",
            content.contains("val actionContentColor = if (isIncome && appColors.isDark) Color(0xFF052B14) else Color.White")
        )
        assertTrue(
            "TransactionBottomActionBar Save/Update button contentColor must use actionContentColor",
            content.contains("contentColor = if (isAmountValid) actionContentColor else actionContentColor.copy(alpha = 0.6f)")
        )
        assertTrue(
            "TransactionBottomActionBar Text color must use actionContentColor",
            content.contains("color = actionContentColor")
        )
    }

    @Test
    fun testRecordPaymentSheetDarkModeContrast() {
        val file = findSourceFile("src/main/java/com/example/expensetracker/ui/receivables/RecordPaymentSheet.kt")
        assertTrue("RecordPaymentSheet.kt must exist", file.exists())
        val content = file.readText()

        assertTrue(
            "RecordPaymentSheet must declare adaptive buttonContentColor for dark mode contrast",
            content.contains("val buttonContentColor = if (appColors.isDark) Color(0xFF052B14) else Color.White")
        )
        assertTrue(
            "RecordPaymentSheet Button colors must set contentColor = buttonContentColor",
            content.contains("contentColor = buttonContentColor")
        )
        assertTrue(
            "RecordPaymentSheet Button text color must be buttonContentColor",
            content.contains("color = buttonContentColor")
        )
    }

    @Test
    fun testRecordPayablePaymentSheetDarkModeContrast() {
        val file = findSourceFile("src/main/java/com/example/expensetracker/ui/receivables/RecordPayablePaymentSheet.kt")
        assertTrue("RecordPayablePaymentSheet.kt must exist", file.exists())
        val content = file.readText()

        assertTrue(
            "RecordPayablePaymentSheet must declare adaptive buttonContentColor for dark mode contrast",
            content.contains("val buttonContentColor = if (appColors.isDark) Color(0xFF052B14) else Color.White")
        )
        assertTrue(
            "RecordPayablePaymentSheet Button colors must set contentColor = buttonContentColor",
            content.contains("contentColor = buttonContentColor")
        )
        assertTrue(
            "RecordPayablePaymentSheet Button text color must be buttonContentColor",
            content.contains("color = buttonContentColor")
        )
    }

    @Test
    fun test1TapAddTransactionFabWiring() {
        val file = findSourceFile("src/main/java/com/example/expensetracker/ui/navigation/AppNavigation.kt")
        assertTrue("AppNavigation.kt must exist", file.exists())
        val content = file.readText()

        assertTrue(
            "AppNavigation onAddClick must directly trigger showGlobalAddSheet = true with EXPENSE preselected",
            content.contains("onAddClick = {") &&
                    content.contains("initialTypeForAdd = TransactionType.EXPENSE") &&
                    content.contains("showGlobalAddSheet = true")
        )
        assertTrue(
            "AppNavigation onAddLongClick must open GlobalActionChooserSheet",
            content.contains("onAddLongClick = {\n                            showQuickActionChooserSheet = true\n                        }")
        )
    }

    @Test
    fun testTopBarDrawerButtonWiringInNavigationScreenHost() {
        val file = findSourceFile("src/main/java/com/example/expensetracker/ui/navigation/NavigationScreenHost.kt")
        assertTrue("NavigationScreenHost.kt must exist", file.exists())
        val content = file.readText()

        assertTrue(
            "NavigationScreenHost must pass onMenuClick = onOpenMenu to DashboardScreen",
            content.contains("DashboardScreen(") && content.contains("onMenuClick = onOpenMenu")
        )
        assertTrue(
            "NavigationScreenHost must pass onMenuClick = onOpenMenu to TransactionsScreen",
            content.contains("TransactionsScreen(") && content.contains("onMenuClick = onOpenMenu")
        )
        assertTrue(
            "NavigationScreenHost must pass onMenuClick = onOpenMenu to AnalyticsScreen",
            content.contains("AnalyticsScreen(") && content.contains("onMenuClick = onOpenMenu")
        )
        assertTrue(
            "NavigationScreenHost must pass onMenuClick = onOpenMenu to AccountsScreen",
            content.contains("AccountsScreen(") && content.contains("onMenuClick = onOpenMenu")
        )
        assertTrue(
            "NavigationScreenHost must pass onMenuClick = onOpenMenu to BillsScreen",
            content.contains("BillsScreen(") && content.contains("onMenuClick = onOpenMenu")
        )
        assertTrue(
            "NavigationScreenHost must pass onMenuClick = onOpenMenu to ReceivablesScreen",
            content.contains("ReceivablesScreen(") && content.contains("onMenuClick = onOpenMenu")
        )
        assertTrue(
            "NavigationScreenHost must pass onMenuClick = onOpenMenu to BudgetScreen",
            content.contains("BudgetScreen(") && content.contains("onMenuClick = onOpenMenu")
        )
        assertTrue(
            "NavigationScreenHost must pass onMenuClick = onOpenMenu to ReportsScreen",
            content.contains("ReportsScreen(") && content.contains("onMenuClick = onOpenMenu")
        )
        assertTrue(
            "NavigationScreenHost must pass onMenuClick = onOpenMenu to SettingsScreen",
            content.contains("SettingsScreen(") && content.contains("onMenuClick = onOpenMenu")
        )
    }

    @Test
    fun testCategoryFilterResetAndSelectionInNavigationScreenHost() {
        val file = findSourceFile("src/main/java/com/example/expensetracker/ui/navigation/NavigationScreenHost.kt")
        assertTrue("NavigationScreenHost.kt must exist", file.exists())
        val content = file.readText()

        assertTrue(
            "NavigationScreenHost must reset category filter to null when navigating to Transactions from Dashboard",
            content.contains("transactionsViewModel.setCategoryFilter(null)")
        )
        assertTrue(
            "NavigationScreenHost must forward categoryId filter when navigating to Transactions from Analytics",
            content.contains("transactionsViewModel.setCategoryFilter(categoryId)")
        )
        assertTrue(
            "NavigationScreenHost must provide onNavigateBack to AnalyticsScreen",
            content.contains("onNavigateBack = { onNavigate(NavigationItem.HOME) }")
        )
    }

    @Test
    fun testDashboardMarketActionButtonsDarkModeContrast() {
        val file = findSourceFile("src/main/java/com/example/expensetracker/ui/components/DashboardMarketActionButtons.kt")
        assertTrue("DashboardMarketActionButtons.kt must exist", file.exists())
        val content = file.readText()

        assertTrue(
            "DashboardMarketActionButtons must declare adaptive finishContentColor for dark mode contrast",
            content.contains("val finishContentColor = if (appColors.isDark) Color(0xFF052B14) else Color.White")
        )
        assertTrue(
            "DashboardMarketActionButtons finish Button must set contentColor = finishContentColor",
            content.contains("contentColor = finishContentColor")
        )
        assertTrue(
            "DashboardMarketActionButtons finish Button icon tint must be finishContentColor",
            content.contains("tint = finishContentColor")
        )
        assertTrue(
            "DashboardMarketActionButtons finish Button text color must be finishContentColor",
            content.contains("color = finishContentColor")
        )
    }

    @Test
    fun testDashboardQuickActionsDockWiringInDashboardScreen() {
        val file = findSourceFile("src/main/java/com/example/expensetracker/ui/dashboard/DashboardScreen.kt")
        assertTrue("DashboardScreen.kt must exist", file.exists())
        val content = file.readText()

        assertTrue(
            "DashboardScreen must render DashboardQuickActionsDock",
            content.contains("DashboardQuickActionsDock(")
        )
        assertTrue(
            "DashboardScreen must pass onNavigateToAccounts to dock",
            content.contains("onNavigateToAccounts = onNavigateToAccounts")
        )
        assertTrue(
            "DashboardScreen must pass onNavigateToBills to dock",
            content.contains("onNavigateToBills = onNavigateToBills")
        )
        assertTrue(
            "DashboardScreen must pass onNavigateToReceivables to dock",
            content.contains("onNavigateToReceivables = onNavigateToReceivables")
        )
        assertTrue(
            "DashboardScreen must pass onNavigateToBudgets to dock",
            content.contains("onNavigateToBudgets = onNavigateToBudgets")
        )
        assertTrue(
            "DashboardScreen must pass onOpenMarketList to dock",
            content.contains("onOpenMarketList = onOpenMarketList")
        )
    }
}
