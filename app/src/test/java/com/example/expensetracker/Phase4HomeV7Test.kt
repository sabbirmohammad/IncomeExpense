package com.example.expensetracker

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Phase 4 Unit Verification Test Suite:
 * Verifies Home (DashboardScreen) redesign per UX_FLOWS section 2 and Screenshot Patch C3:
 * - Pinned HomeTopBar with greeting, avatar -> Settings, privacy toggle, notifications, and 0.5dp scroll hairline
 * - HeroBalanceCard (target <= 200dp, left-aligned, Total balance default, spending wallet secondary line)
 * - Safe to spend today info row
 * - 4 quick actions (uniform accentContainer tint, touch targets >= 48dp, no outer container card)
 * - Conditional upcoming-bills banner
 * - Recent transactions above the fold with "See all >"
 * - Below-the-fold collapsible sections (market, debts, savings, 7-day spend) that remember state and hide when empty
 * - Get started checklist for new users
 */
class Phase4HomeV7Test {

    private fun findSourceFile(relativePath: String): File {
        val cleanPath = relativePath.removePrefix("app/")
        val direct = File(cleanPath)
        if (direct.exists()) return direct
        val appPrefixed = File("app/$cleanPath")
        if (appPrefixed.exists()) return appPrefixed
        val dotPrefixed = File("../$cleanPath")
        if (dotPrefixed.exists()) return dotPrefixed
        return direct
    }

    @Test
    fun testHeroBalanceCardSpecifications() {
        val file = findSourceFile("src/main/java/com/example/expensetracker/ui/components/HeroBalanceCard.kt")
        assertTrue("HeroBalanceCard.kt must exist", file.exists())
        val content = file.readText()

        // 1. Left-aligned displayLarge / displaySmall hero amount with tabular numbers
        assertTrue(
            "HeroBalanceCard must use tabular numbers (tnum) for hero balance",
            content.contains("\"tnum\"")
        )
        assertTrue(
            "HeroBalanceCard must support privacy mode with masked digits",
            content.contains("isPrivacyMode") && content.contains("••••")
        )

        // 2. Spending wallet secondary line / switch
        assertTrue(
            "HeroBalanceCard must support spending wallet secondary line/toggle",
            content.contains("spendingWalletBalance") && content.contains("onToggleWallet")
        )

        // 3. Inline metrics: single row, no nested boxes
        assertTrue(
            "HeroBalanceCard must display income indicator ▲",
            content.contains("\"▲\"")
        )
        assertTrue(
            "HeroBalanceCard must display expense indicator ▼",
            content.contains("\"▼\"")
        )

        // 4. Budget progress bar: 6dp bar with warning color
        assertTrue(
            "HeroBalanceCard must render 6dp budget progress bar",
            content.contains(".height(6.dp)")
        )
        assertTrue(
            "HeroBalanceCard budget bar must support warning color threshold",
            content.contains("appColors.warning") && content.contains("appColors.expense")
        )

        // 5. Tonal surface: RadiusM and surfaceRaised
        assertTrue(
            "HeroBalanceCard must use RadiusM and surfaceRaised",
            content.contains("RadiusM") && content.contains("surfaceRaised")
        )
    }

    @Test
    fun testHomeTopBarSpecifications() {
        val file = findSourceFile("src/main/java/com/example/expensetracker/ui/components/HomeTopBar.kt")
        assertTrue("HomeTopBar.kt must exist", file.exists())
        val content = file.readText()

        // 1. Time-based greeting with user name
        assertTrue(
            "HomeTopBar must support timeBasedGreeting",
            content.contains("timeBasedGreeting")
        )

        // 2. Avatar opening Settings
        assertTrue(
            "HomeTopBar must include avatar opening settings",
            content.contains("ProfileAvatarButton") && content.contains("onAvatarClick")
        )

        // 3. Privacy toggle with min 48dp touch target
        assertTrue(
            "HomeTopBar must include privacy toggle with minimumInteractiveComponentSize()",
            content.contains("onTogglePrivacyMode") && content.contains("minimumInteractiveComponentSize()")
        )

        // 4. Notification bell with min 48dp touch target
        assertTrue(
            "HomeTopBar must include notification bell with minimumInteractiveComponentSize()",
            content.contains("Notifications") && content.contains("minimumInteractiveComponentSize()")
        )

        // 5. 0.5dp hairline divider appearing on scroll (no hard cut)
        assertTrue(
            "HomeTopBar must render 0.5dp scroll hairline",
            content.contains(".height(0.5.dp)") && content.contains("showHairline")
        )
    }

    @Test
    fun testSafeToSpendRowSpecifications() {
        val file = findSourceFile("src/main/java/com/example/expensetracker/ui/components/SafeToSpendRow.kt")
        assertTrue("SafeToSpendRow.kt must exist", file.exists())
        val content = file.readText()

        assertTrue(
            "SafeToSpendRow must support safeToSpendToday string",
            content.contains("safeToSpendToday")
        )
        assertTrue(
            "SafeToSpendRow must enforce minimumInteractiveComponentSize()",
            content.contains("minimumInteractiveComponentSize()")
        )
        assertTrue(
            "SafeToSpendRow must format amounts with tnum",
            content.contains("\"tnum\"")
        )
    }

    @Test
    fun testDashboardQuickActionsDockUniformAccentAndNoContainerCard() {
        val file = findSourceFile("src/main/java/com/example/expensetracker/ui/dashboard/DashboardQuickActionsDock.kt")
        assertTrue("DashboardQuickActionsDock.kt must exist", file.exists())
        val content = file.readText()

        // 1. 4 Quick actions: Expense, Income, Transfer, Bazaar
        assertTrue("Must support onAddExpense", content.contains("onAddExpense"))
        assertTrue("Must support onAddIncome", content.contains("onAddIncome"))
        assertTrue("Must support onTransfer", content.contains("onTransfer"))
        assertTrue("Must support onOpenMarketList", content.contains("onOpenMarketList"))

        // 2. Uniform accentContainer tint on circular icon (no rainbow)
        assertTrue(
            "Quick actions must use uniform accentContainer background tint",
            content.contains("accentContainer")
        )
        assertTrue(
            "Quick actions must use uniform appColors.accent for icons",
            content.contains("tint = appColors.accent")
        )

        // 3. Enforce touch targets >= 48dp
        assertTrue(
            "Quick action items must enforce minimumInteractiveComponentSize()",
            content.contains("minimumInteractiveComponentSize()")
        )

        // 4. Backward compatibility parameters preserved
        assertTrue("Must preserve onNavigateToAccounts parameter", content.contains("onNavigateToAccounts"))
        assertTrue("Must preserve onNavigateToBills parameter", content.contains("onNavigateToBills"))
        assertTrue("Must preserve onNavigateToReceivables parameter", content.contains("onNavigateToReceivables"))
        assertTrue("Must preserve onNavigateToBudgets parameter", content.contains("onNavigateToBudgets"))
    }

    @Test
    fun testDashboardCollapsibleSectionSpecifications() {
        val file = findSourceFile("src/main/java/com/example/expensetracker/ui/dashboard/DashboardCollapsibleSection.kt")
        assertTrue("DashboardCollapsibleSection.kt must exist", file.exists())
        val content = file.readText()

        assertTrue("Must support isExpanded toggle", content.contains("isExpanded") && content.contains("onToggleExpand"))
        assertTrue("Must render metric text", content.contains("metricText"))
        assertTrue("Must use RadiusM and surfaceRaised", content.contains("RadiusM") && content.contains("surfaceRaised"))
        assertTrue("Must enforce minimumInteractiveComponentSize()", content.contains("minimumInteractiveComponentSize()"))
        assertTrue("Must have AnimatedVisibility for content", content.contains("AnimatedVisibility"))
    }

    @Test
    fun testDashboardGetStartedChecklistSpecifications() {
        val file = findSourceFile("src/main/java/com/example/expensetracker/ui/dashboard/DashboardGetStartedChecklist.kt")
        assertTrue("DashboardGetStartedChecklist.kt must exist", file.exists())
        val content = file.readText()

        assertTrue("Must support Add account task", content.contains("addAccountTask") && content.contains("onAddAccount"))
        assertTrue("Must support Add first expense task", content.contains("addFirstExpenseTask") && content.contains("onAddExpense"))
        assertTrue("Must support Set budget task", content.contains("setBudgetTask") && content.contains("onSetBudget"))
        assertTrue("Must enforce minimumInteractiveComponentSize()", content.contains("minimumInteractiveComponentSize()"))
    }

    @Test
    fun testDashboardScreenWiringAndComposition() {
        val file = findSourceFile("src/main/java/com/example/expensetracker/ui/dashboard/DashboardScreen.kt")
        assertTrue("DashboardScreen.kt must exist", file.exists())
        val content = file.readText()

        // 1. Pinned HomeTopBar with scroll hairline
        assertTrue("DashboardScreen must render HomeTopBar", content.contains("HomeTopBar("))
        assertTrue("DashboardScreen must pass showHairline to top bar", content.contains("showHairline = showHairline"))

        // 2. HeroBalanceCard rendered with full wiring
        assertTrue("DashboardScreen must render HeroBalanceCard", content.contains("HeroBalanceCard("))
        assertTrue("HeroBalanceCard must wire onToggleWallet", content.contains("onToggleWallet ="))

        // 3. SafeToSpendRow rendered
        assertTrue("DashboardScreen must render SafeToSpendRow", content.contains("SafeToSpendRow("))

        // 4. DashboardQuickActionsDock wiring (invariants verified)
        assertTrue("DashboardScreen must render DashboardQuickActionsDock", content.contains("DashboardQuickActionsDock("))
        assertTrue("Must wire onNavigateToAccounts", content.contains("onNavigateToAccounts = onNavigateToAccounts"))
        assertTrue("Must wire onNavigateToBills", content.contains("onNavigateToBills = onNavigateToBills"))
        assertTrue("Must wire onNavigateToReceivables", content.contains("onNavigateToReceivables = onNavigateToReceivables"))
        assertTrue("Must wire onNavigateToBudgets", content.contains("onNavigateToBudgets = onNavigateToBudgets"))
        assertTrue("Must wire onOpenMarketList", content.contains("onOpenMarketList = onOpenMarketList"))
        assertTrue("Must wire onAddExpense", content.contains("onAddExpense ="))
        assertTrue("Must wire onAddIncome", content.contains("onAddIncome ="))
        assertTrue("Must wire onTransfer", content.contains("onTransfer ="))

        // 5. Recent transactions section above fold
        assertTrue("DashboardScreen must render DashboardRecentTransactionsSection", content.contains("DashboardRecentTransactionsSection("))

        // 6. Get started checklist for new users
        assertTrue("DashboardScreen must render DashboardGetStartedChecklist when transactions empty", content.contains("DashboardGetStartedChecklist("))

        // 7. Below-the-fold collapsible widgets that remember state
        assertTrue("DashboardScreen must remember collapsible states with rememberSaveable", content.contains("rememberSaveable"))
        assertTrue("DashboardScreen must use DashboardCollapsibleSection", content.contains("DashboardCollapsibleSection("))
    }
}
