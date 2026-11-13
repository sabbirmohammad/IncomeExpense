package com.example.expensetracker

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class Phase6MoneyScreensV7Test {

    private fun findSourceFile(relativePath: String): File {
        val root = File(".")
        val direct = File(root, relativePath)
        if (direct.exists()) return direct

        val candidate = File(root, relativePath.removePrefix("app/"))
        if (candidate.exists()) return candidate

        val fromApp = File(root, "app/$relativePath")
        if (fromApp.exists()) return fromApp

        throw IllegalArgumentException("Could not find source file: $relativePath")
    }

    @Test
    fun testAccountsScreenTokensAndHero() {
        val heroFile = findSourceFile("app/src/main/java/com/example/expensetracker/ui/accounts/AccountsHeroCard.kt")
        val heroContent = heroFile.readText()

        assertTrue("AccountsHeroCard must use surfaceRaised", heroContent.contains("surfaceRaised"))
        assertTrue("AccountsHeroCard must use RadiusM", heroContent.contains("RadiusM"))
        assertTrue("AccountsHeroCard must use displaySmall typography", heroContent.contains("displaySmall"))
        assertTrue("AccountsHeroCard must enforce tabular figures tnum", heroContent.contains("""fontFeatureSettings = "tnum""""))

        val quickActionsFile = findSourceFile("app/src/main/java/com/example/expensetracker/ui/accounts/AccountsQuickActionsRow.kt")
        val quickActionsContent = quickActionsFile.readText()
        assertTrue("AccountsQuickActionsRow must have 48dp height buttons", quickActionsContent.contains("height(48.dp)"))
        assertTrue("AccountsQuickActionsRow must use accentContainer tint", quickActionsContent.contains("accentContainer"))

        val cardItemFile = findSourceFile("app/src/main/java/com/example/expensetracker/ui/accounts/AccountCardItem.kt")
        val cardItemContent = cardItemFile.readText()
        assertTrue("AccountCardItem must use surfaceRaised", cardItemContent.contains("surfaceRaised"))
        assertTrue("AccountCardItem must use RadiusM", cardItemContent.contains("RadiusM"))
        assertTrue("AccountCardItem balance must use tabular figures tnum", cardItemContent.contains("""fontFeatureSettings = "tnum""""))

        val digitalCardFile = findSourceFile("app/src/main/java/com/example/expensetracker/ui/accounts/DigitalAccountCard.kt")
        val digitalCardContent = digitalCardFile.readText()
        assertTrue("DigitalAccountCard must use 16dp radius", digitalCardContent.contains("RoundedCornerShape(16.dp)"))
        assertTrue("DigitalAccountCard must format balance with tnum", digitalCardContent.contains("""fontFeatureSettings = "tnum""""))
    }

    @Test
    fun testBudgetsScreenTokensAndStatus() {
        val overallBudgetFile = findSourceFile("app/src/main/java/com/example/expensetracker/ui/budget/OverallBudgetCard.kt")
        val overallContent = overallBudgetFile.readText()

        assertTrue("OverallBudgetCard must use surfaceRaised", overallContent.contains("surfaceRaised"))
        assertTrue("OverallBudgetCard must use RadiusM", overallContent.contains("RadiusM"))
        assertTrue("OverallBudgetCard must define explicit status badge Safe", overallContent.contains("budgetStatusSafe"))
        assertTrue("OverallBudgetCard must define explicit status badge Warning", overallContent.contains("budgetStatusWarning"))
        assertTrue("OverallBudgetCard must define explicit status badge Over", overallContent.contains("budgetStatusOver"))
        assertTrue("OverallBudgetCard amounts must use tabular figures tnum", overallContent.contains("""fontFeatureSettings = "tnum""""))
        assertTrue("OverallBudgetCard Set Monthly Budget button must be 48dp", overallContent.contains("height(48.dp)"))

        val categoryBudgetFile = findSourceFile("app/src/main/java/com/example/expensetracker/ui/budget/CategoryBudgetCard.kt")
        val categoryContent = categoryBudgetFile.readText()
        assertTrue("CategoryBudgetCard must use surfaceRaised", categoryContent.contains("surfaceRaised"))
        assertTrue("CategoryBudgetCard must use RadiusM", categoryContent.contains("RadiusM"))
        assertTrue("CategoryBudgetCard must have explicit status text", categoryContent.contains("statusText"))
        assertTrue("CategoryBudgetCard amounts must use tabular figures tnum", categoryContent.contains("""fontFeatureSettings = "tnum""""))

        val savingsGoalsTabFile = findSourceFile("app/src/main/java/com/example/expensetracker/ui/budget/SavingsGoalsTabContent.kt")
        val savingsTabContent = savingsGoalsTabFile.readText()
        assertTrue("SavingsGoalsTabContent must offer Emergency Fund template", savingsTabContent.contains("templateEmergencyFund"))
        assertTrue("SavingsGoalsTabContent must offer Eid Savings template", savingsTabContent.contains("templateEidSavings"))
        assertTrue("SavingsGoalsTabContent must offer Education template", savingsTabContent.contains("templateEducation"))
        assertTrue("SavingsGoalsTabContent must offer Travel template", savingsTabContent.contains("templateTravel"))

        val savingsSummaryFile = findSourceFile("app/src/main/java/com/example/expensetracker/ui/savings/SavingsSummaryCard.kt")
        assertTrue("SavingsSummaryCard must use tabular figures tnum", savingsSummaryFile.readText().contains("""fontFeatureSettings = "tnum""""))

        val savingsGoalCardFile = findSourceFile("app/src/main/java/com/example/expensetracker/ui/savings/SavingsGoalCard.kt")
        assertTrue("SavingsGoalCard must use tabular figures tnum", savingsGoalCardFile.readText().contains("""fontFeatureSettings = "tnum""""))
    }

    @Test
    fun testBillsScreenUpcomingPaidAllAndTemplates() {
        val billsScreenFile = findSourceFile("app/src/main/java/com/example/expensetracker/ui/bills/BillsScreen.kt")
        val billsContent = billsScreenFile.readText()

        assertTrue("BillsScreen must include Upcoming segment", billsContent.contains("strings.tabUpcoming"))
        assertTrue("BillsScreen must include Paid segment", billsContent.contains("strings.tabPaid"))
        assertTrue("BillsScreen must include All segment", billsContent.contains("strings.tabAll"))
        assertTrue("BillsScreen must include SegmentedControl", billsContent.contains("SegmentedControl("))

        // Invariants from ChallengerPhase2EmpiricalTest
        assertTrue(
            "BillsScreen must declare billToDelete state",
            billsContent.contains("var billToDelete by remember { mutableStateOf<RecurringBill?>(null) }")
        )
        assertTrue(
            "BillsScreen onDelete must set billToDelete instead of direct deletion",
            billsContent.contains("billToDelete = bill")
        )
        assertTrue(
            "BillsScreen must invoke BillDeleteConfirmDialog",
            billsContent.contains("BillDeleteConfirmDialog(")
        )

        // Template chips in empty state
        assertTrue("BillsScreen must provide electricity template", billsContent.contains("templateElectricity"))
        assertTrue("BillsScreen must provide gas template", billsContent.contains("templateGas"))
        assertTrue("BillsScreen must provide wifi template", billsContent.contains("templateWifi"))
        assertTrue("BillsScreen must provide rent template", billsContent.contains("templateRent"))
        assertTrue("BillsScreen must provide recharge template", billsContent.contains("templateRecharge"))
        assertTrue("BillsScreen must provide subscription template", billsContent.contains("templateSubscription"))

        val heroFile = findSourceFile("app/src/main/java/com/example/expensetracker/ui/bills/BillsSummaryHeroCard.kt")
        val heroContent = heroFile.readText()
        assertTrue("BillsSummaryHeroCard must use surfaceRaised", heroContent.contains("surfaceRaised"))
        assertTrue("BillsSummaryHeroCard must use RadiusM", heroContent.contains("RadiusM"))
        assertTrue("BillsSummaryHeroCard must use displaySmall", heroContent.contains("displaySmall"))
        assertTrue("BillsSummaryHeroCard must use tabular figures tnum", heroContent.contains("""fontFeatureSettings = "tnum""""))

        val addButtonFile = findSourceFile("app/src/main/java/com/example/expensetracker/ui/bills/BillsHeaderComponents.kt")
        val addButtonContent = addButtonFile.readText()
        assertTrue("AddRecurringBillButton must be 48dp", addButtonContent.contains("height(48.dp)"))
        assertTrue("AddRecurringBillButton must use accentContainer", addButtonContent.contains("accentContainer"))
    }

    @Test
    fun testMoneyOwedScreenNetBannerAndTokens() {
        val bannerFile = findSourceFile("app/src/main/java/com/example/expensetracker/ui/receivables/ReceivablesTabsAndNetBanner.kt")
        val bannerContent = bannerFile.readText()

        assertTrue("ReceivablesTabsAndNetBanner must use surfaceRaised", bannerContent.contains("surfaceRaised"))
        assertTrue("ReceivablesTabsAndNetBanner net balance must use tabular figures tnum", bannerContent.contains("""fontFeatureSettings = "tnum""""))

        val topBarFile = findSourceFile("app/src/main/java/com/example/expensetracker/ui/receivables/ReceivablesTopBar.kt")
        val topBarContent = topBarFile.readText()
        assertTrue("ReceivablesTopBar Split Bill button must have minimumInteractiveComponentSize", topBarContent.contains("minimumInteractiveComponentSize()"))
        assertTrue("ReceivablesTopBar Split Bill must use accentContainer", topBarContent.contains("accentContainer"))

        val heroFile = findSourceFile("app/src/main/java/com/example/expensetracker/ui/receivables/ReceivablesSummaryHeroCard.kt")
        assertTrue("ReceivablesSummaryHeroCard must use tabular figures tnum", heroFile.readText().contains("""fontFeatureSettings = "tnum""""))

        val payablesSummaryFile = findSourceFile("app/src/main/java/com/example/expensetracker/ui/receivables/PayablesSummaryCard.kt")
        assertTrue("PayablesSummaryCard must use tabular figures tnum", payablesSummaryFile.readText().contains("""fontFeatureSettings = "tnum""""))

        val recCardFile = findSourceFile("app/src/main/java/com/example/expensetracker/ui/receivables/ReceivableItemCard.kt")
        assertTrue("ReceivableItemCard must use tabular figures tnum", recCardFile.readText().contains("""fontFeatureSettings = "tnum""""))

        val payableRowFile = findSourceFile("app/src/main/java/com/example/expensetracker/ui/receivables/PayableRowItem.kt")
        assertTrue("PayableRowItem must use tabular figures tnum", payableRowFile.readText().contains("""fontFeatureSettings = "tnum""""))
    }
}
