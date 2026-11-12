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
    fun testNavigationItemMoneyEnumAndLocalization() {
        val moneyItem = NavigationItem.valueOf("MONEY")
        assertNotNull("NavigationItem.MONEY must exist", moneyItem)
        assertEquals("Title must be Money", "Money", moneyItem.title)

        val banglaStrings = Strings(isBangla = true)
        val englishStrings = Strings(isBangla = false)

        assertEquals("Bangla title for MONEY must be 'হিসাব'", "হিসাব", moneyItem.getLocalizedTitle(banglaStrings))
        assertEquals("English title for MONEY must be 'Money'", "Money", moneyItem.getLocalizedTitle(englishStrings))
    }

    @Test
    fun test3DScaffoldAndSideDrawerRemoved() {
        val scaffoldFile = findSourceFile("app/src/main/java/com/example/expensetracker/ui/navigation/Navigation3DScaffold.kt")
        val drawerFile = findSourceFile("app/src/main/java/com/example/expensetracker/ui/navigation/SideMenuDrawer.kt")
        assertFalse("Navigation3DScaffold.kt must be deleted in v7", scaffoldFile.exists())
        assertFalse("SideMenuDrawer.kt must be deleted in v7", drawerFile.exists())
    }

    @Test
    fun testModernBottomNavBarHas5SlotsWithOpaqueSurfaceAndSolidEmeraldPlus() {
        val navBarFile = findSourceFile("app/src/main/java/com/example/expensetracker/ui/navigation/ModernBottomNavBar.kt")
        assertTrue("ModernBottomNavBar.kt must exist", navBarFile.exists())
        val content = navBarFile.readText()

        // 5 Slots verified
        assertTrue("Slot 1 Home present", content.contains("NavigationItem.HOME"))
        assertTrue("Slot 2 Transactions present", content.contains("NavigationItem.TRANSACTIONS"))
        assertTrue("Slot 3 Center + present", content.contains("onAddClick"))
        assertTrue("Slot 4 Market present", content.contains("NavigationItem.MARKET"))
        assertTrue("Slot 5 Money present", content.contains("NavigationItem.MONEY") && content.contains("strings.navMoney"))

        // C2 Invariant: solid emerald background (no blue gradient), opaque surface
        assertTrue("Solid emerald accent used for +", content.contains(".background(appColors.accent)"))
        assertFalse("Blue gradient must be removed from +", content.contains("Color(0xFF2563EB)"))
        assertTrue("Hairline border present", content.contains("HairlineInkStroke"))
    }

    @Test
    fun testMoneyHubScreenExistsAndBindsToScreenHost() {
        val hubFile = findSourceFile("app/src/main/java/com/example/expensetracker/ui/money/MoneyHubScreen.kt")
        assertTrue("MoneyHubScreen.kt must exist", hubFile.exists())

        val screenHostFile = findSourceFile("app/src/main/java/com/example/expensetracker/ui/navigation/NavigationScreenHost.kt")
        assertTrue("NavigationScreenHost.kt must exist", screenHostFile.exists())
        val content = screenHostFile.readText()

        assertTrue(
            "NavigationScreenHost must bind NavigationItem.MONEY to MoneyHubScreen",
            content.contains("NavigationItem.MONEY -> {") && content.contains("MoneyHubScreen(")
        )
    }

    @Test
    fun testHierarchicalBackNavigationToParentMoneyHub() {
        val screenHostFile = findSourceFile("app/src/main/java/com/example/expensetracker/ui/navigation/NavigationScreenHost.kt")
        val content = screenHostFile.readText()

        // Secondary Money screens must return to Money hub
        assertTrue(content.contains("AccountsScreen(") && content.contains("onNavigateBack = { onNavigate(NavigationItem.MONEY) }"))
        assertTrue(content.contains("BudgetScreen(") && content.contains("onNavigateBack = { onNavigate(NavigationItem.MONEY) }"))
        assertTrue(content.contains("BillsScreen(") && content.contains("onNavigateBack = { onNavigate(NavigationItem.MONEY) }"))
        assertTrue(content.contains("ReceivablesScreen(") && content.contains("onNavigateBack = { onNavigate(NavigationItem.MONEY) }"))
        assertTrue(content.contains("AnalyticsScreen(") && content.contains("onNavigateBack = { onNavigate(NavigationItem.MONEY) }"))
        assertTrue(content.contains("ReportsScreen(") && content.contains("onNavigateBack = { onNavigate(NavigationItem.MONEY) }"))

        // Settings returns to Home
        assertTrue(content.contains("SettingsScreen(") && content.contains("onNavigateBack = { onNavigate(NavigationItem.HOME) }"))
    }
}
