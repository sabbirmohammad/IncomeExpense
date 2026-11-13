package com.example.expensetracker.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.HairlineInkStroke
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.TabularNumFeature
import com.example.expensetracker.ui.components.Strings
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.rememberAppHaptics

/**
 * ModernBottomNavBar:
 * Living Ledger 2.0 Strict 5-Destination Top-Level Bottom Navigation Bar:
 * 1. Home (হোম)
 * 2. Ledger (খাতা)
 * 3. Bazaar (বাজার)
 * 4. Wallets (ওয়ালেট)
 * 5. Plan (পরিকল্পনা)
 *
 * Spec Highlights:
 * - Standard 5-item bar (height 64dp + system bottom inset)
 * - Background surface, 0.5dp top hairline ink border, flat elevation (0dp)
 * - Selected indicator: 56x28dp pill behind icon with accent at 14% alpha
 * - Selected tint = accent; unselected tint = textSecondary
 * - Label always visible (12sp, SemiBold when selected, Medium otherwise)
 * - Touch target per item: (full width / 5) x 64dp (>= 48x48dp)
 * - Accessible Role.Tab semantics
 * - Badge support: terracotta dot on Plan, numeric counter on Bazaar
 * - Reselecting active tab scrolls to top / resets inner segment
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
    // Optional callbacks kept for backwards compatibility with legacy callers
    onAddClick: (() -> Unit)? = null,
    onAddLongClick: (() -> Unit)? = null,
    onMoreClick: (() -> Unit)? = null
) {
    val appColors = LocalAppColors.current

    // Normalizing mapped aliases to the canonical 5 destinations
    val activeCanonical = when (selectedItem) {
        NavigationItem.HOME -> NavigationItem.HOME
        NavigationItem.LEDGER, NavigationItem.TRANSACTIONS, NavigationItem.ANALYTICS, NavigationItem.REPORTS -> NavigationItem.LEDGER
        NavigationItem.BAZAAR, NavigationItem.MARKET -> NavigationItem.BAZAAR
        NavigationItem.WALLETS, NavigationItem.ACCOUNTS, NavigationItem.MONEY -> NavigationItem.WALLETS
        NavigationItem.PLAN, NavigationItem.BUDGETS, NavigationItem.BILLS, NavigationItem.RECEIVABLES -> NavigationItem.PLAN
        else -> selectedItem
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = appColors.surface,
        shadowElevation = 0.dp
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // Top 0.5dp hairline border
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter),
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
                    onClick = {
                        if (activeCanonical == NavigationItem.HOME) onReselectItem?.invoke(NavigationItem.HOME)
                        else onItemSelected(NavigationItem.HOME)
                    },
                    modifier = Modifier.weight(1f)
                )

                // 2. Ledger (খাতা)
                NavBarTabItem(
                    selected = activeCanonical == NavigationItem.LEDGER,
                    label = strings.navLedger,
                    activeIcon = Icons.AutoMirrored.Filled.ReceiptLong,
                    inactiveIcon = Icons.AutoMirrored.Outlined.ReceiptLong,
                    onClick = {
                        if (activeCanonical == NavigationItem.LEDGER) onReselectItem?.invoke(NavigationItem.LEDGER)
                        else onItemSelected(NavigationItem.LEDGER)
                    },
                    modifier = Modifier.weight(1f)
                )

                // 3. Bazaar (বাজার)
                NavBarTabItem(
                    selected = activeCanonical == NavigationItem.BAZAAR,
                    label = strings.navBazaar,
                    activeIcon = Icons.Filled.ShoppingCart,
                    inactiveIcon = Icons.Outlined.ShoppingCart,
                    badgeCount = bazaarBadgeCount,
                    isBangla = strings.isBangla,
                    onClick = {
                        if (activeCanonical == NavigationItem.BAZAAR) onReselectItem?.invoke(NavigationItem.BAZAAR)
                        else onItemSelected(NavigationItem.BAZAAR)
                    },
                    modifier = Modifier.weight(1f)
                )

                // 4. Wallets (ওয়ালেট)
                NavBarTabItem(
                    selected = activeCanonical == NavigationItem.WALLETS,
                    label = strings.navWallets,
                    activeIcon = Icons.Filled.AccountBalanceWallet,
                    inactiveIcon = Icons.Outlined.AccountBalanceWallet,
                    onClick = {
                        if (activeCanonical == NavigationItem.WALLETS) onReselectItem?.invoke(NavigationItem.WALLETS)
                        else onItemSelected(NavigationItem.WALLETS)
                    },
                    modifier = Modifier.weight(1f)
                )

                // 5. Plan (পরিকল্পনা)
                NavBarTabItem(
                    selected = activeCanonical == NavigationItem.PLAN,
                    label = strings.navPlan,
                    activeIcon = Icons.Filled.Payments,
                    inactiveIcon = Icons.Outlined.Payments,
                    hasDotBadge = planBadge,
                    onClick = {
                        if (activeCanonical == NavigationItem.PLAN) onReselectItem?.invoke(NavigationItem.PLAN)
                        else onItemSelected(NavigationItem.PLAN)
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun NavBarTabItem(
    selected: Boolean,
    label: String,
    activeIcon: ImageVector,
    inactiveIcon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    hasDotBadge: Boolean = false,
    badgeCount: Int = 0,
    isBangla: Boolean = false
) {
    val appColors = LocalAppColors.current
    val haptics = rememberAppHaptics()
    val interactionSource = remember { MutableInteractionSource() }

    val iconColor = if (selected) appColors.accent else appColors.textSecondary
    val textColor = if (selected) appColors.accent else appColors.textSecondary
    val fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium

    Box(
        modifier = modifier
            .fillMaxHeight()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    haptics.lightTick()
                    onClick()
                }
            )
            .semantics {
                role = Role.Tab
                this.selected = selected
                stateDescription = if (selected) "Selected" else "Not selected"
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icon + Pill Indicator Zone
            Box(
                modifier = Modifier
                    .size(width = 56.dp, height = 28.dp),
                contentAlignment = Alignment.Center
            ) {
                // Pill indicator behind icon (56x28dp, accent at 14% alpha)
                if (selected) {
                    Box(
                        modifier = Modifier
                            .size(width = 56.dp, height = 28.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(appColors.accent.copy(alpha = 0.14f))
                    )
                }

                // Tab Icon
                Icon(
                    imageVector = if (selected) activeIcon else inactiveIcon,
                    contentDescription = label,
                    tint = iconColor,
                    modifier = Modifier.size(22.dp)
                )

                // Optional Terracotta Dot Badge (e.g. overdue bills on Plan)
                if (hasDotBadge) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = (-8).dp, y = 2.dp)
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(appColors.expense)
                    )
                }

                // Optional Numeric Counter Badge (e.g. pending items on Bazaar)
                if (badgeCount > 0) {
                    val countText = if (badgeCount > 9) "9+" else badgeCount.toString()
                    val displayCount = if (isBangla) convertToBengaliDigits(countText) else countText
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = (-4).dp, y = 0.dp)
                            .defaultMinSize(minWidth = 14.dp, minHeight = 14.dp)
                            .clip(CircleShape)
                            .background(appColors.expense)
                            .padding(horizontal = 3.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = displayCount,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                fontFeatureSettings = TabularNumFeature
                            ),
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(3.dp))

            // Label
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.5.sp,
                    fontWeight = fontWeight
                ),
                color = textColor,
                maxLines = 1
            )
        }
    }
}

/**
 * AppBottomBar alias matching ModernBottomNavBar for callers
 */
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
