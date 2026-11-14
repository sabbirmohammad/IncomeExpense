package com.example.expensetracker.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import com.example.expensetracker.theme.ShapePill
import com.example.expensetracker.ui.components.LocalAppStrings

/**
 * PulseNavigationRail:
 * Left navigation rail for Medium screens (600..839dp) and landscape phones:
 * - 80dp wide, surface background
 * - Top: prominent Add button in brand Mango accent
 * - Center: 4 destination tabs with icons, labels, and status badges
 * - Role.Tab semantics, >=48dp touch targets
 */
@Composable
fun PulseNavigationRail(
    selectedTab: PulseTab,
    onTabSelected: (PulseTab) -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier,
    hasPlanAttention: Boolean = false,
    isBazaarTripActive: Boolean = false
) {
    val appColors = LocalAppColors.current
    val strings = LocalAppStrings.current

    Surface(
        modifier = modifier
            .width(80.dp)
            .fillMaxHeight(),
        color = appColors.surface,
        tonalElevation = if (appColors.isDark) 3.dp else 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Top Add Button
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .shadow(elevation = 4.dp, shape = CircleShape)
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
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // 4 Tabs
            PulseRailTabItem(
                tab = PulseTab.HOME,
                isSelected = selectedTab == PulseTab.HOME,
                onClick = { onTabSelected(PulseTab.HOME) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            PulseRailTabItem(
                tab = PulseTab.MONEY,
                isSelected = selectedTab == PulseTab.MONEY,
                onClick = { onTabSelected(PulseTab.MONEY) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            PulseRailTabItem(
                tab = PulseTab.BAZAAR,
                isSelected = selectedTab == PulseTab.BAZAAR,
                onClick = { onTabSelected(PulseTab.BAZAAR) },
                hasBadge = isBazaarTripActive,
                badgeColor = appColors.income,
                badgeDescription = strings.badgeBazaarActiveDescription
            )

            Spacer(modifier = Modifier.height(16.dp))

            PulseRailTabItem(
                tab = PulseTab.PLAN,
                isSelected = selectedTab == PulseTab.PLAN,
                onClick = { onTabSelected(PulseTab.PLAN) },
                hasBadge = hasPlanAttention,
                badgeColor = appColors.expense,
                badgeDescription = strings.badgePlanAttentionDescription
            )
        }
    }
}

@Composable
private fun PulseRailTabItem(
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
            .padding(vertical = 6.dp, horizontal = 4.dp)
            .semantics {
                role = Role.Tab
                selected = isSelected
                contentDescription = itemContentDescription
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            Box(
                modifier = Modifier
                    .clip(ShapePill)
                    .background(if (isSelected) appColors.accent.copy(alpha = 0.16f) else Color.Transparent)
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isSelected) tab.activeIcon else tab.inactiveIcon,
                    contentDescription = null,
                    tint = if (isSelected) appColors.accent else appColors.textSecondary,
                    modifier = Modifier.size(24.dp)
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

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            ),
            color = if (isSelected) appColors.accent else appColors.textSecondary,
            maxLines = 1
        )
    }
}
