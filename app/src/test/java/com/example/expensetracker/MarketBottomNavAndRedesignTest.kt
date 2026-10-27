package com.example.expensetracker

import com.example.expensetracker.ui.components.Strings
import com.example.expensetracker.ui.navigation.NavigationItem
import com.example.expensetracker.ui.navigation.getLocalizedTitle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class MarketBottomNavAndRedesignTest {

    private fun findSourceFile(relativePath: String): File {
        val direct = File(relativePath)
        if (direct.exists()) return direct
        val appPrefixed = File("app", relativePath)
        if (appPrefixed.exists()) return appPrefixed
        return direct
    }

    @Test
    fun testNavigationItemMarketEnumAndLocalization() {
        val marketItem = NavigationItem.valueOf("MARKET")
        assertNotNull("NavigationItem.MARKET must exist", marketItem)
        assertEquals("Title must be Market", "Market", marketItem.title)

        val banglaStrings = Strings(isBangla = true)
        val englishStrings = Strings(isBangla = false)

        assertEquals("Bangla title for MARKET must be 'বাজার'", "বাজার", marketItem.getLocalizedTitle(banglaStrings))
        assertEquals("English title for MARKET must be 'Market'", "Market", marketItem.getLocalizedTitle(englishStrings))
    }

    @Test
    fun testModernBottomNavBarContainsMarketInPrimaryPosition() {
        val navBarFile = findSourceFile("src/main/java/com/example/expensetracker/ui/navigation/ModernBottomNavBar.kt")
        assertTrue("ModernBottomNavBar.kt must exist", navBarFile.exists())
        val content = navBarFile.readText()

        // Verify Market item button in Bottom Bar
        assertTrue(
            "ModernBottomNavBar must have BottomNavItemButton for NavigationItem.MARKET",
            content.contains("selectedItem == NavigationItem.MARKET") &&
                    content.contains("strings.navMarket") &&
                    content.contains("Icons.Filled.ShoppingCart") &&
                    content.contains("Icons.Outlined.ShoppingCart")
        )

        // Verify isMoreSelected includes NavigationItem.ANALYTICS
        assertTrue(
            "isMoreSelected must include NavigationItem.ANALYTICS",
            content.contains("selectedItem == NavigationItem.ANALYTICS")
        )
    }

    @Test
    fun testSideMenuDrawerIncludesAnalytics() {
        val drawerFile = findSourceFile("src/main/java/com/example/expensetracker/ui/navigation/SideMenuDrawer.kt")
        assertTrue("SideMenuDrawer.kt must exist", drawerFile.exists())
        val content = drawerFile.readText()

        // Verify SideMenuDrawer includes Analytics in drawerMenuItems
        assertTrue(
            "SideMenuDrawer must include NavigationItem.ANALYTICS in drawerMenuItems",
            content.contains("NavigationItem.ANALYTICS")
        )
    }

    @Test
    fun testNavigationScreenHostBindsMarketScreen() {
        val screenHostFile = findSourceFile("src/main/java/com/example/expensetracker/ui/navigation/NavigationScreenHost.kt")
        assertTrue("NavigationScreenHost.kt must exist", screenHostFile.exists())
        val content = screenHostFile.readText()

        assertTrue(
            "NavigationScreenHost must bind NavigationItem.MARKET to MarketScreen",
            content.contains("NavigationItem.MARKET -> {") &&
                    content.contains("MarketScreen(")
        )

        assertTrue(
            "NavigationScreenHost must bind NavigationItem.ANALYTICS to AnalyticsScreen",
            content.contains("NavigationItem.ANALYTICS -> {") &&
                    content.contains("AnalyticsScreen(")
        )
    }

    @Test
    fun testMarketScreenHasComprehensiveRedesignArchitecture() {
        val marketScreenFile = findSourceFile("src/main/java/com/example/expensetracker/ui/market/MarketScreen.kt")
        assertTrue("MarketScreen.kt must exist", marketScreenFile.exists())
        val content = marketScreenFile.readText()

        // 1. Must define MarketScreen composable with dashboardViewModel
        assertTrue(
            "MarketScreen must accept dashboardViewModel",
            content.contains("fun MarketScreen(") &&
                    content.contains("dashboardViewModel: DashboardViewModel")
        )

        // 2. Must support segmented pill tabs
        assertTrue(
            "MarketScreen must have MarketSegmentedPills",
            content.contains("MarketSegmentedPills") &&
                    content.contains("MarketTabPill")
        )

        // 3. Must embed ProductPriceInsightsContent for Tab 1 (Price Trends)
        assertTrue(
            "MarketScreen must embed ProductPriceInsightsContent",
            content.contains("ProductPriceInsightsContent(")
        )

        // 4. Must support Multi-List Hub and Shopping Mode
        assertTrue(
            "MarketScreen must support ActiveMarketListsHubView",
            content.contains("ActiveMarketListsHubView(")
        )
        assertTrue(
            "MarketScreen must support MarketItemRow and MarketFinishButton",
            content.contains("MarketItemRow(") &&
                    content.contains("MarketFinishButton(")
        )

        // 5. Must support CreateMarketListSection for Tab 2 (New List)
        assertTrue(
            "MarketScreen must support CreateMarketListSection",
            content.contains("CreateMarketListSection(")
        )
    }
}
