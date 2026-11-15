package com.example.expensetracker.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.RadiusButton
import com.example.expensetracker.theme.ShapeButton
import com.example.expensetracker.theme.ShapePill
import com.example.expensetracker.ui.components.LocalAppStrings

/**
 * PulseSidePanel:
 * Permanent left side panel for Expanded screens (width >= 840dp and heightClass != Compact):
 * - 280dp wide, surface background
 * - Top: Wide pill "＋ Add / ＋ যোগ করুন" button
 * - Middle: 4 destination rows with icons, localized titles, and status badges
 * - Bottom: Profile / Settings slot
 * - Role.Tab semantics, >=48dp touch targets
 */
@Composable
fun PulseSidePanel(
    selectedTab: PulseTab,
    onTabSelected: (PulseTab) -> Unit,
    onAddClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier,
    userName: String = "",
    hasPlanAttention: Boolean = false,
    isBazaarTripActive: Boolean = false
) {
    val appColors = LocalAppColors.current
    val strings = LocalAppStrings.current

    Surface(
        modifier = modifier
            .width(280.dp)
            .fillMaxHeight(),
        color = appColors.surface,
        tonalElevation = if (appColors.isDark) 2.dp else 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                // App Branding / Title
                Text(
                    text = if (strings.isBangla) "হিসাব কিতাব" else "Expense Tracker",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = appColors.textPrimary,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Wide Pill Add Button
                Surface(
                    onClick = onAddClick,
                    shape = ShapeButton,
                    color = appColors.accent,
                    contentColor = appColors.onAccent,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .minimumInteractiveComponentSize()
                        .semantics {
                            role = Role.Button
                            contentDescription = strings.addTransactionContentDescription
                        }
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (strings.isBangla) "＋ যোগ করুন" else "＋ Add",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // 4 Navigation Rows
                PulseSidePanelRow(
                    tab = PulseTab.HOME,
                    isSelected = selectedTab == PulseTab.HOME,
                    onClick = { onTabSelected(PulseTab.HOME) }
                )

                Spacer(modifier = Modifier.height(8.dp))

                PulseSidePanelRow(
                    tab = PulseTab.MONEY,
                    isSelected = selectedTab == PulseTab.MONEY,
                    onClick = { onTabSelected(PulseTab.MONEY) }
                )

                Spacer(modifier = Modifier.height(8.dp))

                PulseSidePanelRow(
                    tab = PulseTab.BAZAAR,
                    isSelected = selectedTab == PulseTab.BAZAAR,
                    onClick = { onTabSelected(PulseTab.BAZAAR) },
                    hasBadge = isBazaarTripActive,
                    badgeColor = appColors.income,
                    badgeDescription = strings.badgeBazaarActiveDescription
                )

                Spacer(modifier = Modifier.height(8.dp))

                PulseSidePanelRow(
                    tab = PulseTab.PLAN,
                    isSelected = selectedTab == PulseTab.PLAN,
                    onClick = { onTabSelected(PulseTab.PLAN) },
                    hasBadge = hasPlanAttention,
                    badgeColor = appColors.expense,
                    badgeDescription = strings.badgePlanAttentionDescription
                )
            }

            // Bottom Profile / Settings Row
            Surface(
                onClick = onProfileClick,
                shape = ShapeButton,
                color = appColors.surfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .minimumInteractiveComponentSize()
                    .semantics {
                        role = Role.Button
                        contentDescription = strings.navSettings
                    }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(appColors.accent.copy(alpha = 0.16f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Person,
                                contentDescription = null,
                                tint = appColors.accent,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (userName.isNotBlank()) userName else strings.navSettings,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            ),
                            color = appColors.textPrimary
                        )
                    }

                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = null,
                        tint = appColors.textMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun PulseSidePanelRow(
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

    Surface(
        onClick = onClick,
        shape = ShapeButton,
        color = if (isSelected) appColors.accent.copy(alpha = 0.14f) else Color.Transparent,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .minimumInteractiveComponentSize()
            .semantics {
                role = Role.Tab
                selected = isSelected
                contentDescription = itemContentDescription
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isSelected) tab.activeIcon else tab.inactiveIcon,
                    contentDescription = null,
                    tint = if (isSelected) appColors.accent else appColors.textSecondary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(14.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 15.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    ),
                    color = if (isSelected) appColors.accent else appColors.textSecondary
                )
            }

            if (hasBadge) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(badgeColor)
                )
            }
        }
    }
}
