package com.example.expensetracker.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.RadiusPill
import com.example.expensetracker.theme.ShapePill
import com.example.expensetracker.ui.components.LocalAppStrings

/**
 * PulseDock:
 * Compact floating bottom dock for Pulse Design Language:
 * - Height: 68dp, RadiusPill (999dp), horizontal margin 16dp, bottom margin 12dp
 * - Layout: [Home][Money][ ＋ ][Bazaar][Plan]
 * - Central ＋ is 56dp raised circle (-8dp) in Brand Mango accent
 * - Role.Tab semantics, >=48dp touch targets, localized accessibility descriptions
 */
@Composable
fun PulseDock(
    selectedTab: PulseTab,
    onTabSelected: (PulseTab) -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier,
    hasPlanAttention: Boolean = false,
    isBazaarTripActive: Boolean = false
) {
    val appColors = LocalAppColors.current
    val strings = LocalAppStrings.current

    val dockShadowModifier = if (!appColors.isDark) {
        Modifier.shadow(elevation = 12.dp, shape = ShapePill, clip = false, ambientColor = Color.Black.copy(alpha = 0.06f), spotColor = Color.Black.copy(alpha = 0.12f))
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .then(dockShadowModifier),
            shape = ShapePill,
            color = appColors.surface,
            tonalElevation = if (appColors.isDark) 4.dp else 0.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp)
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tab 1: Home
                PulseDockTabItem(
                    tab = PulseTab.HOME,
                    isSelected = selectedTab == PulseTab.HOME,
                    onClick = { onTabSelected(PulseTab.HOME) }
                )

                // Tab 2: Money
                PulseDockTabItem(
                    tab = PulseTab.MONEY,
                    isSelected = selectedTab == PulseTab.MONEY,
                    onClick = { onTabSelected(PulseTab.MONEY) }
                )

                // Central Add Spacer placeholder (56dp + padding)
                SpacerPlaceholderForAddButton()

                // Tab 3: Bazaar
                PulseDockTabItem(
                    tab = PulseTab.BAZAAR,
                    isSelected = selectedTab == PulseTab.BAZAAR,
                    onClick = { onTabSelected(PulseTab.BAZAAR) },
                    hasBadge = isBazaarTripActive,
                    badgeColor = appColors.income,
                    badgeDescription = strings.badgeBazaarActiveDescription
                )

                // Tab 4: Plan
                PulseDockTabItem(
                    tab = PulseTab.PLAN,
                    isSelected = selectedTab == PulseTab.PLAN,
                    onClick = { onTabSelected(PulseTab.PLAN) },
                    hasBadge = hasPlanAttention,
                    badgeColor = appColors.expense,
                    badgeDescription = strings.badgePlanAttentionDescription
                )
            }
        }

        // Center Raised Add Button (56dp circle, raised -8dp)
        Box(
            modifier = Modifier
                .offset(y = (-8).dp)
                .size(56.dp)
                .shadow(elevation = 6.dp, shape = CircleShape)
                .clip(CircleShape)
                .background(appColors.accent)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onAddClick
                )
                .semantics {
                    role = Role.Button
                    contentDescription = strings.addTransactionContentDescription
                }
                .minimumInteractiveComponentSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = null,
                tint = appColors.onAccent,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun SpacerPlaceholderForAddButton() {
    Box(modifier = Modifier.width(60.dp))
}

@Composable
private fun PulseDockTabItem(
    tab: PulseTab,
    isSelected: Boolean,
    onClick: () -> Unit,
    hasBadge: Boolean = false,
    badgeColor: Color = Color.Unspecified,
    badgeDescription: String? = null
) {
    val appColors = LocalAppColors.current
    val strings = LocalAppStrings.current
    val title = tab.getLocalizedTitle(strings)

    val itemContentDescription = if (hasBadge && badgeDescription != null) {
        "$title, $badgeDescription"
    } else {
        title
    }

    Column(
        modifier = Modifier
            .minimumInteractiveComponentSize()
            .clip(ShapePill)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .semantics {
                role = Role.Tab
                selected = isSelected
                contentDescription = itemContentDescription
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            Box(
                modifier = Modifier
                    .clip(ShapePill)
                    .background(if (isSelected) appColors.accent.copy(alpha = 0.16f) else Color.Transparent)
                    .padding(horizontal = 14.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isSelected) tab.activeIcon else tab.inactiveIcon,
                    contentDescription = null,
                    tint = if (isSelected) appColors.accent else appColors.textSecondary,
                    modifier = Modifier.size(22.dp)
                )
            }

            if (hasBadge) {
                Box(
                    modifier = Modifier
                        .offset(x = 2.dp, y = (-2).dp)
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(badgeColor)
                )
            }
        }

        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            ),
            color = if (isSelected) appColors.accent else appColors.textSecondary,
            maxLines = 1
        )
    }
}
