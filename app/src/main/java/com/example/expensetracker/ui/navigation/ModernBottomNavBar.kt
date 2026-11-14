package com.example.expensetracker.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.expensetracker.theme.HairlineInkStroke
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.Strings

// Spec: Pill indicator behind icon is 56x28dp
val NavPillWidth: Dp = 56.dp
val NavPillHeight: Dp = 28.dp

/**
 * ModernBottomNavBar:
 * Living Ledger 2.0 Strict 5-Destination Top-Level Bottom Navigation Bar:
 * 1. Home (NavigationItem.HOME)
 * 2. Ledger (NavigationItem.LEDGER)
 * 3. Bazaar (NavigationItem.BAZAAR)
 * 4. Wallets (NavigationItem.WALLETS)
 * 5. Plan (NavigationItem.PLAN)
 */
@Composable
fun ModernBottomNavBar(
    selectedItem: NavigationItem,
    onItemSelected: (NavigationItem) -> Unit,
    strings: Strings,
    modifier: Modifier = Modifier,
    onReselectItem: ((NavigationItem) -> Unit)? = null,
    planBadge: Boolean = false,
    bazaarBadgeCount: Int = 0,
    onAddClick: (() -> Unit)? = null,
    onAddLongClick: (() -> Unit)? = null,
    onMoreClick: (() -> Unit)? = null
) {
    val appColors = LocalAppColors.current

    val activeCanonical = when (selectedItem) {
        NavigationItem.HOME -> NavigationItem.HOME
        NavigationItem.LEDGER, NavigationItem.TRANSACTIONS, NavigationItem.ANALYTICS, NavigationItem.REPORTS -> NavigationItem.LEDGER
        NavigationItem.BAZAAR, NavigationItem.MARKET -> NavigationItem.BAZAAR
        NavigationItem.WALLETS, NavigationItem.ACCOUNTS, NavigationItem.MONEY -> NavigationItem.WALLETS
        NavigationItem.PLAN, NavigationItem.BUDGETS, NavigationItem.BILLS, NavigationItem.RECEIVABLES -> NavigationItem.PLAN
        else -> selectedItem
    }

    fun selectOrReselect(item: NavigationItem) {
        if (activeCanonical == item) onReselectItem?.invoke(item)
        else onItemSelected(item)
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = appColors.surface,
        shadowElevation = 0.dp
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter),
                thickness = HairlineInkStroke,
                color = appColors.border
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .height(64.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. Home
                NavBarTabItem(
                    selected = activeCanonical == NavigationItem.HOME,
                    label = strings.navHome,
                    activeIcon = Icons.Filled.Home,
                    inactiveIcon = Icons.Outlined.Home,
                    onClick = { selectOrReselect(NavigationItem.HOME) },
                    modifier = Modifier.weight(1f)
                )

                // 2. Ledger
                NavBarTabItem(
                    selected = activeCanonical == NavigationItem.LEDGER,
                    label = strings.navLedger,
                    activeIcon = Icons.AutoMirrored.Filled.ReceiptLong,
                    inactiveIcon = Icons.AutoMirrored.Outlined.ReceiptLong,
                    onClick = { selectOrReselect(NavigationItem.LEDGER) },
                    modifier = Modifier.weight(1f)
                )

                // 3. Bazaar
                NavBarTabItem(
                    selected = activeCanonical == NavigationItem.BAZAAR,
                    label = strings.navBazaar,
                    activeIcon = Icons.Filled.ShoppingCart,
                    inactiveIcon = Icons.Outlined.ShoppingCart,
                    badgeCount = bazaarBadgeCount,
                    isBangla = strings.isBangla,
                    onClick = { selectOrReselect(NavigationItem.BAZAAR) },
                    modifier = Modifier.weight(1f)
                )

                // 4. Wallets
                NavBarTabItem(
                    selected = activeCanonical == NavigationItem.WALLETS,
                    label = strings.navWallets,
                    activeIcon = Icons.Filled.AccountBalanceWallet,
                    inactiveIcon = Icons.Outlined.AccountBalanceWallet,
                    onClick = { selectOrReselect(NavigationItem.WALLETS) },
                    modifier = Modifier.weight(1f)
                )

                // 5. Plan
                NavBarTabItem(
                    selected = activeCanonical == NavigationItem.PLAN,
                    label = strings.navPlan,
                    activeIcon = Icons.Filled.Payments,
                    inactiveIcon = Icons.Outlined.Payments,
                    hasDotBadge = planBadge,
                    onClick = { selectOrReselect(NavigationItem.PLAN) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun AppBottomBar(
    selectedItem: NavigationItem,
    onItemSelected: (NavigationItem) -> Unit,
    strings: Strings,
    modifier: Modifier = Modifier,
    onReselectItem: ((NavigationItem) -> Unit)? = null,
    planBadge: Boolean = false,
    bazaarBadgeCount: Int = 0,
    onAddClick: (() -> Unit)? = null,
    onAddLongClick: (() -> Unit)? = null
) {
    ModernBottomNavBar(
        selectedItem = selectedItem,
        onItemSelected = onItemSelected,
        strings = strings,
        modifier = modifier,
        onReselectItem = onReselectItem,
        planBadge = planBadge,
        bazaarBadgeCount = bazaarBadgeCount,
        onAddClick = onAddClick,
        onAddLongClick = onAddLongClick
    )
}
