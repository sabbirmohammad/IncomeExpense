package com.example.expensetracker.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.expensetracker.ui.components.Strings

/**
 * Pulse 4 Core Destinations:
 * - Home (হোম)
 * - Money (টাকা)
 * - Bazaar (বাজার)
 * - Plan (পরিকল্পনা)
 */
enum class PulseTab(
    val title: String,
    val activeIcon: ImageVector,
    val inactiveIcon: ImageVector
) {
    HOME("Home", Icons.Filled.Home, Icons.Outlined.Home),
    MONEY("Money", Icons.Filled.AccountBalanceWallet, Icons.Outlined.AccountBalanceWallet),
    BAZAAR("Bazaar", Icons.Filled.ShoppingCart, Icons.Outlined.ShoppingCart),
    PLAN("Plan", Icons.Filled.Payments, Icons.Outlined.Payments);

    fun getLocalizedTitle(strings: Strings): String = when (this) {
        HOME -> strings.navHome
        MONEY -> strings.navMoney
        BAZAAR -> strings.navBazaar
        PLAN -> strings.navPlan
    }
}

/**
 * Money Tab Inner Segments:
 * - Activity (লেনদেন)
 * - Wallets (ওয়ালেট)
 * - Insights (ইনসাইটস + স্টেটমেন্ট)
 */
enum class MoneySegment(val index: Int) {
    ACTIVITY(0),
    WALLETS(1),
    INSIGHTS(2);

    companion object {
        fun fromIndex(index: Int): MoneySegment = values().find { it.index == index } ?: ACTIVITY
    }
}

/**
 * Plan Tab Inner Segments:
 * - Budgets (বাজেট)
 * - Bills (বিল)
 * - Goals (লক্ষ্য)
 * - Debts (ধার ও দেনা)
 */
enum class PlanSegment(val index: Int) {
    BUDGETS(0),
    BILLS(1),
    GOALS(2),
    DEBTS(3);

    companion object {
        fun fromIndex(index: Int): PlanSegment = values().find { it.index == index } ?: BUDGETS
    }
}

/**
 * Pushed screens that slide over top-level tabs and hide the navigation dock/rail.
 */
sealed interface PushedScreen {
    object SETTINGS : PushedScreen
    data class BAZAAR_PLAN(val listId: Long) : PushedScreen
}

/**
 * Maps legacy NavigationItem values into target PulseTab and optional segment index.
 */
fun NavigationItem.toPulseTarget(): Pair<PulseTab, Int?> = when (this) {
    NavigationItem.HOME -> PulseTab.HOME to null
    NavigationItem.MONEY -> PulseTab.MONEY to MoneySegment.WALLETS.index
    NavigationItem.BAZAAR, NavigationItem.MARKET -> PulseTab.BAZAAR to null
    NavigationItem.PLAN -> PulseTab.PLAN to PlanSegment.BUDGETS.index

    // Money Segments
    NavigationItem.LEDGER, NavigationItem.TRANSACTIONS -> PulseTab.MONEY to MoneySegment.ACTIVITY.index
    NavigationItem.WALLETS, NavigationItem.ACCOUNTS -> PulseTab.MONEY to MoneySegment.WALLETS.index
    NavigationItem.ANALYTICS, NavigationItem.REPORTS -> PulseTab.MONEY to MoneySegment.INSIGHTS.index

    // Plan Segments
    NavigationItem.BUDGETS -> PulseTab.PLAN to PlanSegment.BUDGETS.index
    NavigationItem.BILLS -> PulseTab.PLAN to PlanSegment.BILLS.index
    NavigationItem.RECEIVABLES -> PulseTab.PLAN to PlanSegment.DEBTS.index

    NavigationItem.SETTINGS -> PulseTab.HOME to null // Handled as pushed screen
}
