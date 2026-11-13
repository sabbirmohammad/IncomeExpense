package com.example.expensetracker

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class Phase9AccessibilityQAV7Test {

    private val basePath: String = if (File("src/main/java").exists()) "src/main/java" else "app/src/main/java"

    private fun readFile(relativePath: String): String {
        val file = File("$basePath/$relativePath")
        assertTrue("Target file ${file.path} must exist", file.exists())
        return file.readText()
    }

    @Test
    fun testTouchTargetsMeetStandard() {
        // 1. MarketSegmentedPills: MarketTabPill has minimumInteractiveComponentSize()
        val marketContent = readFile("com/example/expensetracker/ui/market/MarketScreen.kt")
        assertTrue(
            "MarketTabPill must declare minimumInteractiveComponentSize()",
            marketContent.contains("minimumInteractiveComponentSize()")
        )
        assertTrue(
            "MarketSegmentedPills must declare height(42.dp)",
            marketContent.contains("height(42.dp)")
        )

        // 2. AccountCardMenu: items declare minimumInteractiveComponentSize()
        val accountMenuContent = readFile("com/example/expensetracker/ui/accounts/AccountCardMenu.kt")
        assertTrue(
            "AccountCardMenu must enforce minimumInteractiveComponentSize()",
            accountMenuContent.contains("minimumInteractiveComponentSize()")
        )

        // 3. FilterChips: PillFilterChip and DropdownFilterChip have minimumInteractiveComponentSize()
        val filterChipsContent = readFile("com/example/expensetracker/ui/components/FilterChips.kt")
        assertTrue(
            "FilterChips must enforce minimumInteractiveComponentSize()",
            filterChipsContent.contains("minimumInteractiveComponentSize()")
        )

        // 4. SettingsProfileCard: Edit button has minimumInteractiveComponentSize()
        val profileContent = readFile("com/example/expensetracker/ui/settings/SettingsProfileCard.kt")
        assertTrue(
            "SettingsProfileCard edit button must enforce minimumInteractiveComponentSize()",
            profileContent.contains("minimumInteractiveComponentSize()")
        )
    }

    @Test
    fun testContentDescriptionsTalkBackSupport() {
        // OneUiComponents OneUiEmptyState
        val oneUiContent = readFile("com/example/expensetracker/ui/components/OneUiComponents.kt")
        assertTrue(
            "OneUiEmptyState icon must provide non-null contentDescription",
            oneUiContent.contains("contentDescription = headline")
        )

        // Sparkline
        val sparklineContent = readFile("com/example/expensetracker/ui/components/Sparkline.kt")
        assertTrue(
            "Sparkline must declare semantics contentDescription",
            sparklineContent.contains("contentDescription = \"Trend sparkline")
        )

        // DonutChart
        val donutContent = readFile("com/example/expensetracker/ui/components/AnalyticsCharts.kt")
        assertTrue(
            "DonutChart must declare semantics contentDescription",
            donutContent.contains("accessibilitySummary") && donutContent.contains("Donut chart")
        )

        // PaymentMethodDonutChart
        val pmDonutContent = readFile("com/example/expensetracker/ui/components/PaymentMethodDonutChart.kt")
        assertTrue(
            "PaymentMethodDonutChart must declare semantics contentDescription",
            pmDonutContent.contains("accessibilitySummary") && pmDonutContent.contains("Payment methods donut chart")
        )

        // SpendingBarChart
        val barChartContent = readFile("com/example/expensetracker/ui/components/SpendingBarChart.kt")
        assertTrue(
            "SpendingBarChart must declare semantics contentDescription",
            barChartContent.contains("a11ySummary") && barChartContent.contains("Spending trend for")
        )

        // Switches TalkBack stateDescription
        val syncCardContent = readFile("com/example/expensetracker/ui/components/SyncStatusCard.kt")
        assertTrue(
            "SyncStatusCard switch must declare stateDescription",
            syncCardContent.contains("stateDescription =")
        )

        val bengaliSwitchContent = readFile("com/example/expensetracker/ui/settings/SettingsBengaliNumeralsCard.kt")
        assertTrue(
            "SettingsBengaliNumeralsCard switch must declare stateDescription",
            bengaliSwitchContent.contains("stateDescription =")
        )
    }

    @Test
    fun testC8TruncationAndTabularFiguresRules() {
        // Tabular numbers across key screens
        val dashboardContent = readFile("com/example/expensetracker/ui/dashboard/DashboardScreen.kt")
        assertTrue("DashboardScreen must use tnum for amounts", dashboardContent.contains("fontFeatureSettings = \"tnum\""))

        val txContent = readFile("com/example/expensetracker/ui/transactions/TransactionsScreen.kt")
        assertTrue("TransactionsScreen must import or use tnum", txContent.contains("tnum") || txContent.contains("TransactionGroupCard"))

        val accContent = readFile("com/example/expensetracker/ui/accounts/AccountCardItem.kt")
        assertTrue("AccountCardItem must use tnum", accContent.contains("fontFeatureSettings = \"tnum\""))

        val budgetContent = readFile("com/example/expensetracker/ui/budget/CategoryBudgetCard.kt")
        assertTrue("CategoryBudgetCard must use tnum", budgetContent.contains("fontFeatureSettings = \"tnum\""))

        val savingsContent = readFile("com/example/expensetracker/ui/budget/SavingsGoalsTabContent.kt")
        assertTrue("SavingsGoalsTabContent must use tnum", savingsContent.contains("fontFeatureSettings = \"tnum\""))

        val syncContent = readFile("com/example/expensetracker/ui/components/SyncStatusCard.kt")
        assertTrue("SyncStatusCard must use tnum", syncContent.contains("fontFeatureSettings = \"tnum\""))
    }

    @Test
    fun testBottomBarPaddingSafety() {
        val dashboardContent = readFile("com/example/expensetracker/ui/dashboard/DashboardScreen.kt")
        assertTrue("DashboardScreen must include 100dp bottom spacer", dashboardContent.contains("100.dp"))

        val txContent = readFile("com/example/expensetracker/ui/transactions/TransactionsScreen.kt")
        assertTrue("TransactionsScreen must include 100dp bottom spacer", txContent.contains("100.dp"))

        val accContent = readFile("com/example/expensetracker/ui/accounts/AccountsScreen.kt")
        assertTrue("AccountsScreen must include 100dp bottom spacer", accContent.contains("100.dp"))

        val billsContent = readFile("com/example/expensetracker/ui/bills/BillsScreen.kt")
        assertTrue("BillsScreen must include 100dp bottom contentPadding", billsContent.contains("bottom = 100.dp"))

        val recContent = readFile("com/example/expensetracker/ui/receivables/ReceivablesScreen.kt")
        assertTrue("ReceivablesScreen must include 100dp bottom spacer", recContent.contains("100.dp"))
    }

    @Test
    fun testStableLazyColumnKeysForPerformance() {
        val dashboardContent = readFile("com/example/expensetracker/ui/dashboard/DashboardScreen.kt")
        assertTrue("DashboardScreen must declare key for hero balance", dashboardContent.contains("key = \"hero_balance\""))
        assertTrue("DashboardScreen must declare key for quick actions", dashboardContent.contains("key = \"quick_actions\""))

        val accContent = readFile("com/example/expensetracker/ui/accounts/AccountsScreen.kt")
        assertTrue("AccountsScreen must declare key for hero card", accContent.contains("key = \"hero_card\""))
        assertTrue("AccountsScreen items must use stable key", accContent.contains("items(accounts, key = { it.id })"))

        val billsContent = readFile("com/example/expensetracker/ui/bills/BillsScreen.kt")
        assertTrue("BillsScreen items must use stable key", billsContent.contains("items(filteredBills, key = { it.id })"))

        val budgetContent = readFile("com/example/expensetracker/ui/budget/BudgetListTabContent.kt")
        assertTrue("BudgetListTabContent items must use stable key", budgetContent.contains("items(categoryBudgetStatuses, key = { it.budget.id })"))

        val savingsContent = readFile("com/example/expensetracker/ui/budget/SavingsGoalsTabContent.kt")
        assertTrue("SavingsGoalsTabContent items must use stable key", savingsContent.contains("items(savingsGoals, key = { it.id })"))
    }

    @Test
    fun testVersionSyncScriptExists() {
        val scriptFile = if (File("scripts/bump_version.sh").exists()) File("scripts/bump_version.sh") else File("../scripts/bump_version.sh")
        assertTrue("bump_version.sh script must exist", scriptFile.exists())
        val scriptContent = scriptFile.readText()
        assertTrue("bump_version.sh must handle major bump", scriptContent.contains("major"))
        assertTrue("bump_version.sh must handle minor bump", scriptContent.contains("minor"))
        assertTrue("bump_version.sh must handle patch bump", scriptContent.contains("patch"))
        assertTrue("bump_version.sh must update build.gradle.kts", scriptContent.contains("build.gradle.kts"))
        assertTrue("bump_version.sh must update AppConstants.kt", scriptContent.contains("AppConstants.kt"))
    }
}
