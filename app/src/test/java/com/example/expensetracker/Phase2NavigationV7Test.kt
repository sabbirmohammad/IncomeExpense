package com.example.expensetracker

import com.example.expensetracker.ui.components.Strings
import com.example.expensetracker.ui.navigation.NavigationItem
import com.example.expensetracker.ui.navigation.PulseTab
import com.example.expensetracker.ui.navigation.getLocalizedTitle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class Phase2NavigationV7Test {

    private fun findSourceFile(relativePath: String): File {
        val clean = relativePath.removePrefix("app/")
        val direct = File(clean)
        if (direct.exists()) return direct
        val appPrefixed = File("app", clean)
        if (appPrefixed.exists()) return appPrefixed
        return direct
    }

    @Test
    fun testNavigationItem5DestinationsEnumAndLocalization() {
        val home = NavigationItem.valueOf("HOME")
        val ledger = NavigationItem.valueOf("LEDGER")
        val bazaar = NavigationItem.valueOf("BAZAAR")
        val wallets = NavigationItem.valueOf("WALLETS")
        val plan = NavigationItem.valueOf("PLAN")

        assertNotNull(home)
        assertNotNull(ledger)
        assertNotNull(bazaar)
        assertNotNull(wallets)
        assertNotNull(plan)

        val banglaStrings = Strings(isBangla = true)
        val englishStrings = Strings(isBangla = false)

        assertEquals("হোম", home.getLocalizedTitle(banglaStrings))
        assertEquals("Home", home.getLocalizedTitle(englishStrings))

        assertEquals("খাতা", ledger.getLocalizedTitle(banglaStrings))
        assertEquals("Ledger", ledger.getLocalizedTitle(englishStrings))

        assertEquals("বাজার", bazaar.getLocalizedTitle(banglaStrings))
        assertEquals("Bazaar", bazaar.getLocalizedTitle(englishStrings))

        assertEquals("ওয়ালেট", wallets.getLocalizedTitle(banglaStrings))
        assertEquals("Wallets", wallets.getLocalizedTitle(englishStrings))

        assertEquals("পরিকল্পনা", plan.getLocalizedTitle(banglaStrings))
        assertEquals("Plan", plan.getLocalizedTitle(englishStrings))
    }

    @Test
    fun test3DScaffoldAndSideDrawerAndGlobalActionChooserRemoved() {
        val scaffoldFile = findSourceFile("app/src/main/java/com/example/expensetracker/ui/navigation/Navigation3DScaffold.kt")
        val drawerFile = findSourceFile("app/src/main/java/com/example/expensetracker/ui/navigation/SideMenuDrawer.kt")
        val chooserFile = findSourceFile("app/src/main/java/com/example/expensetracker/ui/navigation/GlobalActionChooserSheet.kt")

        assertFalse("Navigation3DScaffold.kt must be deleted in v7", scaffoldFile.exists())
        assertFalse("SideMenuDrawer.kt must be deleted in v7", drawerFile.exists())
        assertFalse("GlobalActionChooserSheet.kt must be deleted in v7", chooserFile.exists())
    }

    @Test
    fun testPulseDockHas4TabsWithCenterAddButtonAndBadges() {
        val legacyNavBar = findSourceFile("app/src/main/java/com/example/expensetracker/ui/navigation/ModernBottomNavBar.kt")
        assertFalse("ModernBottomNavBar.kt must be deleted in v7 Phase 2", legacyNavBar.exists())

        val pulseDockFile = findSourceFile("app/src/main/java/com/example/expensetracker/ui/navigation/PulseDock.kt")
        assertTrue("PulseDock.kt must exist", pulseDockFile.exists())
        val content = pulseDockFile.readText()

        // 4 Tabs verified
        assertTrue("Tab Home present", content.contains("PulseTab.HOME"))
        assertTrue("Tab Money present", content.contains("PulseTab.MONEY"))
        assertTrue("Tab Bazaar present", content.contains("PulseTab.BAZAAR"))
        assertTrue("Tab Plan present", content.contains("PulseTab.PLAN"))

        // Spec invariants: 68dp floating dock, ShapePill, center 56dp raised Mango Add button
        assertTrue("68dp dock height", content.contains("68.dp"))
        assertTrue("ShapePill used for dock", content.contains("ShapePill"))
        assertTrue("Center 56dp Add button", content.contains("56.dp") && content.contains("Icons.Filled.Add"))
        assertTrue("Status badges support", content.contains("hasPlanAttention") && content.contains("isBazaarTripActive"))
    }

    @Test
    fun testAppAddFabAndScaffoldReplacedByAdaptiveScaffold() {
        val fabFile = findSourceFile("app/src/main/java/com/example/expensetracker/ui/components/AppAddFab.kt")
        assertFalse("AppAddFab.kt must be deleted in v7 Phase 2", fabFile.exists())

        val scaffoldFile = findSourceFile("app/src/main/java/com/example/expensetracker/ui/components/AppScaffold.kt")
        assertFalse("AppScaffold.kt must be deleted in v7 Phase 2", scaffoldFile.exists())

        val adaptiveScaffoldFile = findSourceFile("app/src/main/java/com/example/expensetracker/ui/navigation/AdaptiveScaffold.kt")
        assertTrue("AdaptiveScaffold.kt must exist", adaptiveScaffoldFile.exists())

        val railFile = findSourceFile("app/src/main/java/com/example/expensetracker/ui/navigation/PulseNavigationRail.kt")
        assertTrue("PulseNavigationRail.kt must exist", railFile.exists())

        val panelFile = findSourceFile("app/src/main/java/com/example/expensetracker/ui/navigation/PulseSidePanel.kt")
        assertTrue("PulseSidePanel.kt must exist", panelFile.exists())
    }

    @Test
    fun testNavigationScreenHostBindsTopLevelDestinations() {
        val screenHostFile = findSourceFile("app/src/main/java/com/example/expensetracker/ui/navigation/NavigationScreenHost.kt")
        assertTrue("NavigationScreenHost.kt must exist", screenHostFile.exists())
        val content = screenHostFile.readText()

        assertTrue(
            "NavigationScreenHost must bind PulseTab.HOME to DashboardScreen",
            content.contains("PulseTab.HOME -> DashboardScreen(")
        )
        assertTrue(
            "NavigationScreenHost must bind PulseTab.MONEY to MoneyScreen",
            content.contains("PulseTab.MONEY -> {") && content.contains("MoneyScreen(")
        )
        assertTrue(
            "NavigationScreenHost must bind PulseTab.BAZAAR to MarketScreen",
            content.contains("PulseTab.BAZAAR -> {") && content.contains("MarketScreen(")
        )
        assertTrue(
            "NavigationScreenHost must bind PulseTab.PLAN to PlanScreen",
            content.contains("PulseTab.PLAN -> {") && content.contains("PlanScreen(")
        )
        assertTrue(
            "NavigationScreenHost must bind NavigationItem.LEDGER to LedgerScreen",
            content.contains("NavigationItem.LEDGER -> {") && content.contains("LedgerScreen(")
        )
        assertTrue(
            "NavigationScreenHost must bind NavigationItem.PLAN to PlanScreen",
            content.contains("NavigationItem.PLAN -> {") && content.contains("PlanScreen(")
        )
    }

    @Test
    fun testBackNavigationReturnsToHome() {
        val appNavFile = findSourceFile("app/src/main/java/com/example/expensetracker/ui/navigation/AppNavigation.kt")
        val content = appNavFile.readText()

        assertTrue(
            "BackHandler must delegate to AdaptiveNavigationState.handleBackPress()",
            content.contains("navState.handleBackPress()") &&
                    content.contains("navState.selectedTab != PulseTab.HOME")
        )
    }
}
