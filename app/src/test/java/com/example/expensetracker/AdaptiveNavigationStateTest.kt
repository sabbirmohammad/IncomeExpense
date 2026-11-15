package com.example.expensetracker

import androidx.compose.runtime.saveable.SaverScope
import com.example.expensetracker.ui.navigation.AdaptiveNavigationState
import com.example.expensetracker.ui.navigation.MoneySegment
import com.example.expensetracker.ui.navigation.NavigationItem
import com.example.expensetracker.ui.navigation.PlanSegment
import com.example.expensetracker.ui.navigation.PulseTab
import com.example.expensetracker.ui.navigation.PushedScreen
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AdaptiveNavigationStateTest {

    private val dummySaverScope = object : SaverScope {
        override fun canBeSaved(value: Any): Boolean = true
    }

    @Test
    fun testInitialStateDefaults() {
        val state = AdaptiveNavigationState()
        assertEquals("Initial tab must be HOME", PulseTab.HOME, state.selectedTab)
        assertEquals("Initial money segment must be ACTIVITY", MoneySegment.ACTIVITY, state.moneySegment)
        assertEquals("Initial plan segment must be BUDGETS", PlanSegment.BUDGETS, state.planSegment)
        assertNull("Initial pushedScreen must be null", state.pushedScreen)
        assertFalse("Initial hasPlanAttention must be false", state.hasPlanAttention)
        assertFalse("Initial isBazaarTripActive must be false", state.isBazaarTripActive)
    }

    @Test
    fun testSaverPersistsAndRestoresStateAcrossProcessDeath() {
        val original = AdaptiveNavigationState(
            initialTab = PulseTab.PLAN,
            initialMoneySegment = MoneySegment.INSIGHTS,
            initialPlanSegment = PlanSegment.DEBTS,
            initialPushedScreen = PushedScreen.SETTINGS
        ).apply {
            hasPlanAttention = true
            isBazaarTripActive = true
        }

        // Save
        val saved = with(AdaptiveNavigationState.Saver) {
            dummySaverScope.save(original)
        }
        assertNotNull("Saved state representation must not be null", saved)

        // Restore
        val restored = AdaptiveNavigationState.Saver.restore(saved!!)
        assertNotNull("Restored state must not be null", restored)
        assertEquals("Restored tab must match", PulseTab.PLAN, restored!!.selectedTab)
        assertEquals("Restored money segment must match", MoneySegment.INSIGHTS, restored.moneySegment)
        assertEquals("Restored plan segment must match", PlanSegment.DEBTS, restored.planSegment)
        assertEquals("Restored pushedScreen must match", PushedScreen.SETTINGS, restored.pushedScreen)

        // Badges are NOT persisted in Saver (derived dynamically at runtime from repository StateFlows)
        assertFalse("Restored hasPlanAttention must be false (not persisted)", restored.hasPlanAttention)
        assertFalse("Restored isBazaarTripActive must be false (not persisted)", restored.isBazaarTripActive)
    }

    @Test
    fun testSaverPersistsAndRestoresBazaarPlanScreen() {
        val original = AdaptiveNavigationState(
            initialTab = PulseTab.BAZAAR,
            initialPushedScreen = PushedScreen.BAZAAR_PLAN(42L)
        )

        val saved = with(AdaptiveNavigationState.Saver) {
            dummySaverScope.save(original)
        }
        assertNotNull(saved)

        val restored = AdaptiveNavigationState.Saver.restore(saved!!)
        assertNotNull(restored)
        assertEquals(PulseTab.BAZAAR, restored!!.selectedTab)
        assertEquals(PushedScreen.BAZAAR_PLAN(42L), restored.pushedScreen)
    }

    @Test
    fun testSaverPersistsAndRestoresBazaarShopScreen() {
        val original = AdaptiveNavigationState(
            initialTab = PulseTab.BAZAAR,
            initialPushedScreen = PushedScreen.BAZAAR_SHOP(108L)
        )

        val saved = with(AdaptiveNavigationState.Saver) {
            dummySaverScope.save(original)
        }
        assertNotNull(saved)

        val restored = AdaptiveNavigationState.Saver.restore(saved!!)
        assertNotNull(restored)
        assertEquals(PulseTab.BAZAAR, restored!!.selectedTab)
        assertEquals(PushedScreen.BAZAAR_SHOP(108L), restored.pushedScreen)
        assertFalse("Must not be top level when BAZAAR_SHOP is pushed (dock hidden)", restored.isTopLevel)
    }

    @Test
    fun testSaverPersistsAndRestoresBazaarDoneScreen() {
        val original = AdaptiveNavigationState(
            initialTab = PulseTab.BAZAAR,
            initialPushedScreen = PushedScreen.BAZAAR_DONE(205L)
        )

        val saved = with(AdaptiveNavigationState.Saver) {
            dummySaverScope.save(original)
        }
        assertNotNull(saved)

        val restored = AdaptiveNavigationState.Saver.restore(saved!!)
        assertNotNull(restored)
        assertEquals(PulseTab.BAZAAR, restored!!.selectedTab)
        assertEquals(PushedScreen.BAZAAR_DONE(205L), restored.pushedScreen)
        assertFalse("Must not be top level when BAZAAR_DONE is pushed (dock hidden)", restored.isTopLevel)
    }

    @Test
    fun testSaverPersistsAndRestoresBazaarHistoryScreen() {
        val original = AdaptiveNavigationState(
            initialTab = PulseTab.BAZAAR,
            initialPushedScreen = PushedScreen.BAZAAR_HISTORY
        )

        val saved = with(AdaptiveNavigationState.Saver) {
            dummySaverScope.save(original)
        }
        assertNotNull(saved)

        val restored = AdaptiveNavigationState.Saver.restore(saved!!)
        assertNotNull(restored)
        assertEquals(PulseTab.BAZAAR, restored!!.selectedTab)
        assertEquals(PushedScreen.BAZAAR_HISTORY, restored.pushedScreen)
        assertFalse("Must not be top level when BAZAAR_HISTORY is pushed (dock hidden)", restored.isTopLevel)
    }

    @Test
    fun testSaverPersistsAndRestoresBazaarTripDetailScreen() {
        val original = AdaptiveNavigationState(
            initialTab = PulseTab.BAZAAR,
            initialPushedScreen = PushedScreen.BAZAAR_TRIP_DETAIL(309L)
        )

        val saved = with(AdaptiveNavigationState.Saver) {
            dummySaverScope.save(original)
        }
        assertNotNull(saved)

        val restored = AdaptiveNavigationState.Saver.restore(saved!!)
        assertNotNull(restored)
        assertEquals(PulseTab.BAZAAR, restored!!.selectedTab)
        assertEquals(PushedScreen.BAZAAR_TRIP_DETAIL(309L), restored.pushedScreen)
        assertFalse("Must not be top level when BAZAAR_TRIP_DETAIL is pushed (dock hidden)", restored.isTopLevel)
    }

    @Test
    fun testTabSwitchingAndReTapResetsSegment() {
        val state = AdaptiveNavigationState()

        // 1. Switch to Money
        state.selectTab(PulseTab.MONEY)
        assertEquals(PulseTab.MONEY, state.selectedTab)
        assertEquals(MoneySegment.ACTIVITY, state.moneySegment)

        // 2. Change inner segment to Wallets
        state.selectMoneySegment(MoneySegment.WALLETS)
        assertEquals(MoneySegment.WALLETS, state.moneySegment)

        // 3. Re-tapping Money tab resets inner segment to default (ACTIVITY)
        state.selectTab(PulseTab.MONEY)
        assertEquals(MoneySegment.ACTIVITY, state.moneySegment)

        // 4. Switch to Plan
        state.selectTab(PulseTab.PLAN)
        assertEquals(PulseTab.PLAN, state.selectedTab)
        assertEquals(PlanSegment.BUDGETS, state.planSegment)

        // 5. Change inner segment to Goals
        state.selectPlanSegment(PlanSegment.GOALS)
        assertEquals(PlanSegment.GOALS, state.planSegment)

        // 6. Re-tapping Plan tab resets inner segment to default (BUDGETS)
        state.selectTab(PulseTab.PLAN)
        assertEquals(PlanSegment.BUDGETS, state.planSegment)
    }

    @Test
    fun testBackPressHierarchy() {
        val state = AdaptiveNavigationState()

        // At HOME with no pushed screen -> back returns false (allow exit)
        assertFalse("At HOME with no pushed screen, back must return false", state.handleBackPress())

        // Navigate to Money -> back redirects to HOME and returns true
        state.selectTab(PulseTab.MONEY)
        assertTrue("From Money, back must return true", state.handleBackPress())
        assertEquals("Active tab after back must be HOME", PulseTab.HOME, state.selectedTab)

        // Pushed screen (Settings) takes priority over tab
        state.selectTab(PulseTab.BAZAAR)
        state.pushScreen(PushedScreen.SETTINGS)
        assertEquals(PushedScreen.SETTINGS, state.pushedScreen)

        assertTrue("From Settings, back must return true", state.handleBackPress())
        assertNull("Pushed screen must be cleared", state.pushedScreen)
        assertEquals("Active tab must remain BAZAAR", PulseTab.BAZAAR, state.selectedTab)

        // Next back press returns from BAZAAR to HOME
        assertTrue(state.handleBackPress())
        assertEquals(PulseTab.HOME, state.selectedTab)

        // Final back press returns false
        assertFalse(state.handleBackPress())
    }

    @Test
    fun testLegacyNavigationItemAliasesMapping() {
        val state = AdaptiveNavigationState()

        // Home
        state.navigate(NavigationItem.HOME)
        assertEquals(PulseTab.HOME, state.selectedTab)

        // Transactions / Ledger -> Money (Activity)
        state.navigate(NavigationItem.TRANSACTIONS)
        assertEquals(PulseTab.MONEY, state.selectedTab)
        assertEquals(MoneySegment.ACTIVITY, state.moneySegment)

        state.navigate(NavigationItem.LEDGER)
        assertEquals(PulseTab.MONEY, state.selectedTab)
        assertEquals(MoneySegment.ACTIVITY, state.moneySegment)

        // Wallets / Accounts -> Money (Wallets)
        state.navigate(NavigationItem.WALLETS)
        assertEquals(PulseTab.MONEY, state.selectedTab)
        assertEquals(MoneySegment.WALLETS, state.moneySegment)

        state.navigate(NavigationItem.ACCOUNTS)
        assertEquals(PulseTab.MONEY, state.selectedTab)
        assertEquals(MoneySegment.WALLETS, state.moneySegment)

        // Analytics / Reports -> Money (Insights)
        state.navigate(NavigationItem.ANALYTICS)
        assertEquals(PulseTab.MONEY, state.selectedTab)
        assertEquals(MoneySegment.INSIGHTS, state.moneySegment)

        state.navigate(NavigationItem.REPORTS)
        assertEquals(PulseTab.MONEY, state.selectedTab)
        assertEquals(MoneySegment.INSIGHTS, state.moneySegment)

        // Bazaar / Market -> Bazaar
        state.navigate(NavigationItem.BAZAAR)
        assertEquals(PulseTab.BAZAAR, state.selectedTab)

        state.navigate(NavigationItem.MARKET)
        assertEquals(PulseTab.BAZAAR, state.selectedTab)

        // Plan / Budgets / Bills / Receivables -> Plan segments
        state.navigate(NavigationItem.PLAN)
        assertEquals(PulseTab.PLAN, state.selectedTab)
        assertEquals(PlanSegment.BUDGETS, state.planSegment)

        state.navigate(NavigationItem.BUDGETS)
        assertEquals(PulseTab.PLAN, state.selectedTab)
        assertEquals(PlanSegment.BUDGETS, state.planSegment)

        state.navigate(NavigationItem.BILLS)
        assertEquals(PulseTab.PLAN, state.selectedTab)
        assertEquals(PlanSegment.BILLS, state.planSegment)

        state.navigate(NavigationItem.RECEIVABLES)
        assertEquals(PulseTab.PLAN, state.selectedTab)
        assertEquals(PlanSegment.DEBTS, state.planSegment)

        // Settings -> Pushed Screen
        state.navigate(NavigationItem.SETTINGS)
        assertEquals(PushedScreen.SETTINGS, state.pushedScreen)
    }

    @Test
    fun testNotificationExtrasDeepLinks() {
        val state = AdaptiveNavigationState()

        // Destination: bills
        state.handleNotificationExtra("destination", "bills")
        assertEquals(PulseTab.PLAN, state.selectedTab)
        assertEquals(PlanSegment.BILLS, state.planSegment)

        // Destination: budget
        state.handleNotificationExtra("destination", "budget")
        assertEquals(PulseTab.PLAN, state.selectedTab)
        assertEquals(PlanSegment.BUDGETS, state.planSegment)

        // Destination: goals
        state.handleNotificationExtra("destination", "goals")
        assertEquals(PulseTab.PLAN, state.selectedTab)
        assertEquals(PlanSegment.GOALS, state.planSegment)

        // Destination: debts / receivables
        state.handleNotificationExtra("destination", "receivables")
        assertEquals(PulseTab.PLAN, state.selectedTab)
        assertEquals(PlanSegment.DEBTS, state.planSegment)

        // Destination: market / bazaar
        state.handleNotificationExtra("destination", "market")
        assertEquals(PulseTab.BAZAAR, state.selectedTab)

        // Screen: bill_reminder
        state.handleNotificationExtra("screen", "bill_reminder")
        assertEquals(PulseTab.PLAN, state.selectedTab)
        assertEquals(PlanSegment.BILLS, state.planSegment)

        // Screen: budget_alert
        state.handleNotificationExtra("screen", "budget_alert")
        assertEquals(PulseTab.PLAN, state.selectedTab)
        assertEquals(PlanSegment.BUDGETS, state.planSegment)

        // Screen: settings
        state.handleNotificationExtra("destination", "settings")
        assertEquals(PushedScreen.SETTINGS, state.pushedScreen)
    }
}
