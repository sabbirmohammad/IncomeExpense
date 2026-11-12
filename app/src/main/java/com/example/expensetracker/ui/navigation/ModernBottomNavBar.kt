package com.example.expensetracker.ui.navigation

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.example.expensetracker.theme.HairlineInkStroke
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.Strings
import com.example.expensetracker.ui.components.V7BottomBarHeight
import com.example.expensetracker.ui.components.rememberAppHaptics

/**
 * V7 Refined Bottom Navigation Bar (replaces/extends ModernBottomNavBar):
 * 5 slots: Home · Transactions · + · Market · Money
 * - Opaque surface background with 0.5dp top hairline divider (per C2 patch)
 * - Solid emerald center + button (no blue gradient), 56dp hit target with 6dp shadow
 * - One-tap action for + without mandatory long-press
 * - Selected tab in accentContainer pill with always-visible labels
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ModernBottomNavBar(
    selectedItem: NavigationItem,
    onItemSelected: (NavigationItem) -> Unit,
    onAddClick: () -> Unit,
    onAddLongClick: () -> Unit = {},
    onMoreClick: () -> Unit = { onItemSelected(NavigationItem.MONEY) },
    strings: Strings,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val isMoreSelected = selectedItem == NavigationItem.MONEY ||
            selectedItem == NavigationItem.ANALYTICS ||
            selectedItem == NavigationItem.ACCOUNTS ||
            selectedItem == NavigationItem.BILLS ||
            selectedItem == NavigationItem.RECEIVABLES ||
            selectedItem == NavigationItem.BUDGETS ||
            selectedItem == NavigationItem.REPORTS

    val haptics = rememberAppHaptics()
    val addInteractionSource = remember { MutableInteractionSource() }
    val isAddPressed by addInteractionSource.collectIsPressedAsState()
    val addScale by animateFloatAsState(
        targetValue = if (isAddPressed) 0.90f else 1.0f,
        animationSpec = spring(dampingRatio = 0.75f, stiffness = Spring.StiffnessMediumLow),
        label = "navAddScale"
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = appColors.surface, // Fully opaque per C2
        shadowElevation = 8.dp
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // Top 0.5dp hairline border per C2
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
                    .height(V7BottomBarHeight)
                    .padding(horizontal = 6.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. Home
                BottomNavItemButton(
                    selected = selectedItem == NavigationItem.HOME,
                    label = strings.navHome,
                    activeIcon = Icons.Filled.Home,
                    inactiveIcon = Icons.Outlined.Home,
                    onClick = { onItemSelected(NavigationItem.HOME) }
                )

                // 2. Transactions
                BottomNavItemButton(
                    selected = selectedItem == NavigationItem.TRANSACTIONS,
                    label = strings.navTransactions,
                    activeIcon = Icons.AutoMirrored.Filled.ReceiptLong,
                    inactiveIcon = Icons.AutoMirrored.Outlined.ReceiptLong,
                    onClick = { onItemSelected(NavigationItem.TRANSACTIONS) }
                )

                // 3. Center + (Solid Emerald per C2, 56dp hit area)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .size(52.dp)
                            .graphicsLayer {
                                scaleX = addScale
                                scaleY = addScale
                            }
                            .shadow(
                                elevation = 6.dp,
                                shape = CircleShape,
                                ambientColor = appColors.accent.copy(alpha = 0.3f),
                                spotColor = appColors.accent
                            )
                            .clip(CircleShape)
                            .background(appColors.accent) // Solid Emerald (blue gradient removed per C2)
                            .combinedClickable(
                                interactionSource = addInteractionSource,
                                indication = null,
                                onClick = {
                                    haptics.mediumClick()
                                    onAddClick()
                                },
                                onLongClick = {
                                    haptics.longPressFeedback()
                                    onAddLongClick()
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = if (strings.isBangla) "নতুন কিছু যুক্ত করুন (ব্যয়, আয়, বাজার)" else "Add New (Expense, Income, Market)",
                            tint = appColors.onAccent,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }

                // 4. Market
                BottomNavItemButton(
                    selected = selectedItem == NavigationItem.MARKET,
                    label = strings.navMarket,
                    activeIcon = Icons.Filled.ShoppingCart,
                    inactiveIcon = Icons.Outlined.ShoppingCart,
                    onClick = { onItemSelected(NavigationItem.MARKET) }
                )

                // 5. Money (Replaces Drawer Menu per UX_FLOWS Section 1 & C2)
                BottomNavItemButton(
                    selected = isMoreSelected,
                    label = strings.navMoney,
                    activeIcon = Icons.Filled.AccountBalanceWallet,
                    inactiveIcon = Icons.Outlined.AccountBalanceWallet,
                    onClick = {
                        onItemSelected(NavigationItem.MONEY)
                    }
                )
            }
        }
    }
}

/**
 * Alias for ModernBottomNavBar per COMPONENT_SPEC.md
 */
@Composable
fun AppBottomBar(
    selectedItem: NavigationItem,
    onItemSelected: (NavigationItem) -> Unit,
    onAddClick: () -> Unit,
    onAddLongClick: () -> Unit = {},
    strings: Strings,
    modifier: Modifier = Modifier
) {
    ModernBottomNavBar(
        selectedItem = selectedItem,
        onItemSelected = onItemSelected,
        onAddClick = onAddClick,
        onAddLongClick = onAddLongClick,
        onMoreClick = { onItemSelected(NavigationItem.MONEY) },
        strings = strings,
        modifier = modifier
    )
}
