package com.example.expensetracker.ui.navigation

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.PieChart
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.Strings
import com.example.expensetracker.ui.components.rememberAppHaptics

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ModernBottomNavBar(
    selectedItem: NavigationItem,
    onItemSelected: (NavigationItem) -> Unit,
    onAddClick: () -> Unit,
    onAddLongClick: () -> Unit = {},
    onMoreClick: () -> Unit,
    strings: Strings,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val isMoreSelected = selectedItem == NavigationItem.ACCOUNTS ||
            selectedItem == NavigationItem.BILLS ||
            selectedItem == NavigationItem.RECEIVABLES ||
            selectedItem == NavigationItem.BUDGETS ||
            selectedItem == NavigationItem.REPORTS ||
            selectedItem == NavigationItem.SETTINGS

    val haptics = rememberAppHaptics()
    val addInteractionSource = remember { MutableInteractionSource() }
    val isAddPressed by addInteractionSource.collectIsPressedAsState()
    val addScale by animateFloatAsState(
        targetValue = if (isAddPressed) 0.88f else 1.0f,
        animationSpec = spring(dampingRatio = 0.75f, stiffness = Spring.StiffnessMediumLow),
        label = "navAddScale"
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = LocalAppColors.current.surface.copy(alpha = 0.98f),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        border = BorderStroke(1.dp, LocalAppColors.current.border.copy(alpha = 0.7f)),
        shadowElevation = 16.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(64.dp)
                .padding(horizontal = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItemButton(
                selected = selectedItem == NavigationItem.HOME,
                label = strings.navHome,
                activeIcon = Icons.Filled.Home,
                inactiveIcon = Icons.Outlined.Home,
                onClick = { onItemSelected(NavigationItem.HOME) }
            )

            BottomNavItemButton(
                selected = selectedItem == NavigationItem.TRANSACTIONS,
                label = strings.navTransactions,
                activeIcon = Icons.AutoMirrored.Filled.ReceiptLong,
                inactiveIcon = Icons.AutoMirrored.Outlined.ReceiptLong,
                onClick = { onItemSelected(NavigationItem.TRANSACTIONS) }
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .size(46.dp)
                        .graphicsLayer {
                            scaleX = addScale
                            scaleY = addScale
                        }
                        .shadow(
                            elevation = 6.dp,
                            shape = CircleShape,
                            ambientColor = appColors.accent.copy(alpha = 0.4f),
                            spotColor = appColors.accent
                        )
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(appColors.accent, Color(0xFF2563EB))
                            )
                        )
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
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            BottomNavItemButton(
                selected = selectedItem == NavigationItem.ANALYTICS,
                label = strings.navAnalytics,
                activeIcon = Icons.Filled.PieChart,
                inactiveIcon = Icons.Outlined.PieChart,
                onClick = { onItemSelected(NavigationItem.ANALYTICS) }
            )

            BottomNavItemButton(
                selected = isMoreSelected,
                label = strings.navMore,
                activeIcon = Icons.Filled.Menu,
                inactiveIcon = Icons.Outlined.Menu,
                onClick = onMoreClick
            )
        }
    }
}
