package com.example.expensetracker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

/**
 * State holder for Pulse adaptive navigation.
 * Persists tab selection, inner segments, pushed-screen stack, and badge states across:
 * - Device rotation
 * - Window size class resize (Compact <-> Medium <-> Expanded)
 * - Process death via rememberSaveable
 */
@Stable
class AdaptiveNavigationState(
    initialTab: PulseTab = PulseTab.HOME,
    initialMoneySegment: MoneySegment = MoneySegment.ACTIVITY,
    initialPlanSegment: PlanSegment = PlanSegment.BUDGETS,
    initialPushedScreen: PushedScreen? = null,
    initialHasPlanAttention: Boolean = false,
    initialIsBazaarTripActive: Boolean = false
) {
    var selectedTab by mutableStateOf(initialTab)
    var moneySegment by mutableStateOf(initialMoneySegment)
    var planSegment by mutableStateOf(initialPlanSegment)
    var pushedScreen by mutableStateOf(initialPushedScreen)

    // Badges
    var hasPlanAttention by mutableStateOf(initialHasPlanAttention)
    var isBazaarTripActive by mutableStateOf(initialIsBazaarTripActive)

    val isTopLevel: Boolean
        get() = pushedScreen == null

    /**
     * Handles system back navigation:
     * 1. If a pushed screen (e.g. Settings) is active -> pop back to previous tab
     * 2. If on a secondary tab (Money, Bazaar, Plan) -> navigate to Home
     * 3. If on Home -> returns false so default system back exits the app
     */
    fun handleBackPress(): Boolean {
        if (pushedScreen != null) {
            pushedScreen = null
            return true
        }
        if (selectedTab != PulseTab.HOME) {
            selectedTab = PulseTab.HOME
            return true
        }
        return false
    }

    fun navigateToTab(tab: PulseTab) {
        pushedScreen = null
        if (selectedTab == tab) {
            // Re-tapping active tab resets its inner segment to default
            when (tab) {
                PulseTab.MONEY -> moneySegment = MoneySegment.ACTIVITY
                PulseTab.PLAN -> planSegment = PlanSegment.BUDGETS
                else -> {}
            }
        } else {
            selectedTab = tab
        }
    }

    fun selectTab(tab: PulseTab) = navigateToTab(tab)

    fun selectMoneySegment(seg: MoneySegment) {
        moneySegment = seg
    }

    fun selectPlanSegment(seg: PlanSegment) {
        planSegment = seg
    }

    fun pushScreen(screen: PushedScreen) {
        pushedScreen = screen
    }

    fun popPushedScreen() {
        pushedScreen = null
    }

    fun popScreen() = popPushedScreen()

    /**
     * Maps legacy NavigationItem calls to target Pulse tab, segment, or pushed screen.
     */
    fun navigate(item: NavigationItem) {
        if (item == NavigationItem.SETTINGS) {
            pushedScreen = PushedScreen.SETTINGS
            return
        }
        pushedScreen = null
        val (tab, segmentIdx) = item.toPulseTarget()
        selectedTab = tab
        if (segmentIdx != null) {
            when (tab) {
                PulseTab.MONEY -> moneySegment = MoneySegment.fromIndex(segmentIdx)
                PulseTab.PLAN -> planSegment = PlanSegment.fromIndex(segmentIdx)
                else -> {}
            }
        }
    }

    /**
     * Handles notification deep link extras.
     */
    fun handleNotificationExtra(key: String?, value: String? = null) {
        val target = when {
            key.equals("destination", ignoreCase = true) ||
            key.equals("screen", ignoreCase = true) ||
            key.equals("target", ignoreCase = true) -> value?.lowercase() ?: ""
            else -> key?.lowercase() ?: ""
        }

        when (target) {
            "bills", "bills_due", "bill_reminder" -> {
                pushedScreen = null
                selectedTab = PulseTab.PLAN
                planSegment = PlanSegment.BILLS
            }
            "budget", "budget_prompt", "budget_alert" -> {
                pushedScreen = null
                selectedTab = PulseTab.PLAN
                planSegment = PlanSegment.BUDGETS
            }
            "goals", "savings_goals" -> {
                pushedScreen = null
                selectedTab = PulseTab.PLAN
                planSegment = PlanSegment.GOALS
            }
            "debts", "receivables" -> {
                pushedScreen = null
                selectedTab = PulseTab.PLAN
                planSegment = PlanSegment.DEBTS
            }
            "market", "bazaar" -> {
                pushedScreen = null
                selectedTab = PulseTab.BAZAAR
            }
            "transactions", "activity" -> {
                pushedScreen = null
                selectedTab = PulseTab.MONEY
                moneySegment = MoneySegment.ACTIVITY
            }
            "wallets", "accounts" -> {
                pushedScreen = null
                selectedTab = PulseTab.MONEY
                moneySegment = MoneySegment.WALLETS
            }
            "insights", "reports", "analytics" -> {
                pushedScreen = null
                selectedTab = PulseTab.MONEY
                moneySegment = MoneySegment.INSIGHTS
            }
            "settings" -> {
                pushedScreen = PushedScreen.SETTINGS
            }
        }
    }

    companion object {
        val Saver: Saver<AdaptiveNavigationState, Any> = listSaver(
            save = { state ->
                listOf(
                    state.selectedTab.name,
                    state.moneySegment.name,
                    state.planSegment.name,
                    state.pushedScreen?.name ?: "",
                    state.hasPlanAttention,
                    state.isBazaarTripActive
                )
            },
            restore = { list ->
                val tab = runCatching { PulseTab.valueOf(list[0] as String) }.getOrDefault(PulseTab.HOME)
                val moneySeg = runCatching { MoneySegment.valueOf(list[1] as String) }.getOrDefault(MoneySegment.ACTIVITY)
                val planSeg = runCatching { PlanSegment.valueOf(list[2] as String) }.getOrDefault(PlanSegment.BUDGETS)
                val pushedStr = list[3] as String
                val pushed = if (pushedStr.isNotBlank()) runCatching { PushedScreen.valueOf(pushedStr) }.getOrNull() else null
                val hasAttention = if (list.size > 4) (list[4] as? Boolean) ?: false else false
                val isBazaarActive = if (list.size > 5) (list[5] as? Boolean) ?: false else false
                AdaptiveNavigationState(tab, moneySeg, planSeg, pushed, hasAttention, isBazaarActive)
            }
        )
    }
}

@Composable
fun rememberAdaptiveNavigationState(
    initialTab: PulseTab = PulseTab.HOME,
    initialMoneySegment: MoneySegment = MoneySegment.ACTIVITY,
    initialPlanSegment: PlanSegment = PlanSegment.BUDGETS
): AdaptiveNavigationState {
    return rememberSaveable(saver = AdaptiveNavigationState.Saver) {
        AdaptiveNavigationState(initialTab, initialMoneySegment, initialPlanSegment)
    }
}
