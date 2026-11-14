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
        val legacyNavBar = findSourceFile("src/main/java/com/example/expensetracker/ui/navigation/ModernBottomNavBar.kt")
        assertFalse("ModernBottomNavBar.kt must be deleted in v7 Phase 2", legacyNavBar.exists())

        val dockFile = findSourceFile("src/main/java/com/example/expensetracker/ui/navigation/PulseDock.kt")
        assertTrue("PulseDock.kt must exist", dockFile.exists())
        val content = dockFile.readText()

        val destFile = findSourceFile("src/main/java/com/example/expensetracker/ui/navigation/PulseNavDestinations.kt")
        assertTrue("PulseNavDestinations.kt must exist", destFile.exists())
        val destContent = destFile.readText()

        // Verify Bazaar item in Pulse Dock
        assertTrue(
            "PulseDock must have tab for BAZAAR with shopping cart icon",
            content.contains("PulseTab.BAZAAR") &&
                    destContent.contains("PulseTab") &&
                    destContent.contains("Icons.Filled.ShoppingCart") &&
                    destContent.contains("Icons.Outlined.ShoppingCart")
        )
    }

    @Test
    fun testMoneyHubIncludesAnalytics() {
        val moneyHubFile = findSourceFile("src/main/java/com/example/expensetracker/ui/money/MoneyHubScreen.kt")
        assertTrue("MoneyHubScreen.kt must exist", moneyHubFile.exists())
        val content = moneyHubFile.readText()

        // Verify MoneyHubScreen includes Analytics in hub items (replacing legacy drawer)
        assertTrue(
            "MoneyHubScreen must include NavigationItem.ANALYTICS in hub items",
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

        // 4. Must support Multi-List Hub / Switcher and Shopping Mode
        assertTrue(
            "MarketScreen must support active lists switcher or hub",
            content.contains("ActiveMarketListsHubView(") || content.contains("effectiveActiveLists")
        )
        assertTrue(
            "MarketScreen must support MarketItemRow and MarketSummaryHeader or MarketFinishButton",
            content.contains("MarketItemRow(") &&
                    (content.contains("MarketFinishButton(") || content.contains("MarketSummaryHeader("))
        )

        // 5. Must support CreateMarketListSection for Tab 2 (New List)
        assertTrue(
            "MarketScreen must support CreateMarketListSection",
            content.contains("CreateMarketListSection(")
        )
    }
}
