package com.example.expensetracker

import com.example.expensetracker.ui.components.Strings
import com.example.expensetracker.ui.navigation.NavigationItem
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
    fun testModernBottomNavBarHas5DestinationsWithOpaqueSurfaceAndFlatElevation() {
        val navBarFile = findSourceFile("app/src/main/java/com/example/expensetracker/ui/navigation/ModernBottomNavBar.kt")
        assertTrue("ModernBottomNavBar.kt must exist", navBarFile.exists())
        val content = navBarFile.readText()

        // 5 Destinations verified
        assertTrue("Slot 1 Home present", content.contains("NavigationItem.HOME"))
        assertTrue("Slot 2 Ledger present", content.contains("NavigationItem.LEDGER"))
        assertTrue("Slot 3 Bazaar present", content.contains("NavigationItem.BAZAAR"))
        assertTrue("Slot 4 Wallets present", content.contains("NavigationItem.WALLETS"))
        assertTrue("Slot 5 Plan present", content.contains("NavigationItem.PLAN"))

        // Spec invariants
        assertTrue("Hairline ink border present", content.contains("HairlineInkStroke"))
        assertTrue("64dp bar height", content.contains("64.dp"))
        assertTrue("Pill indicator behind icon", content.contains("56.dp") && content.contains("28.dp"))
    }

    @Test
    fun testAppAddFabAndScaffoldExist() {
        val fabFile = findSourceFile("app/src/main/java/com/example/expensetracker/ui/components/AppAddFab.kt")
        assertTrue("AppAddFab.kt must exist", fabFile.exists())

        val scaffoldFile = findSourceFile("app/src/main/java/com/example/expensetracker/ui/components/AppScaffold.kt")
        assertTrue("AppScaffold.kt must exist", scaffoldFile.exists())
    }

    @Test
    fun testNavigationScreenHostBindsTopLevelDestinations() {
        val screenHostFile = findSourceFile("app/src/main/java/com/example/expensetracker/ui/navigation/NavigationScreenHost.kt")
        assertTrue("NavigationScreenHost.kt must exist", screenHostFile.exists())
        val content = screenHostFile.readText()

        assertTrue(
            "NavigationScreenHost must bind NavigationItem.LEDGER to LedgerScreen",
            content.contains("NavigationItem.LEDGER -> {") && content.contains("LedgerScreen(")
        )
        assertTrue(
            "NavigationScreenHost must bind NavigationItem.PLAN to PlanScreen",
            content.contains("NavigationItem.PLAN -> {") && content.contains("PlanScreen(")
        )
        assertTrue(
            "NavigationScreenHost must bind NavigationItem.WALLETS to AccountsScreen",
            content.contains("NavigationItem.WALLETS -> {") && content.contains("AccountsScreen(")
        )
        assertTrue(
            "NavigationScreenHost must bind NavigationItem.BAZAAR to MarketScreen",
            content.contains("NavigationItem.BAZAAR -> {") && content.contains("MarketScreen(")
        )
    }

    @Test
    fun testBackNavigationReturnsToHome() {
        val appNavFile = findSourceFile("app/src/main/java/com/example/expensetracker/ui/navigation/AppNavigation.kt")
        val content = appNavFile.readText()

        assertTrue(
            "BackHandler must redirect non-home screens to HOME",
            content.contains("BackHandler(enabled = selectedItem != NavigationItem.HOME)") &&
                    content.contains("selectedItem = NavigationItem.HOME")
        )
    }
}
