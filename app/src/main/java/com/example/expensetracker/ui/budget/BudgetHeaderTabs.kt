package com.example.expensetracker.ui.budget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.DrawerMenuButton
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.ProfileAvatarButton
import com.example.expensetracker.ui.components.rememberAppHaptics

@Composable
fun BudgetHeaderTabs(
    selectedTab: BudgetScreenTab,
    isPrivacyMode: Boolean,
    savingsGoalsCount: Int,
    onMenuClick: (() -> Unit)?,
    onNavigateBack: (() -> Unit)?,
    onNavigateToSettings: (() -> Unit)?,
    onTogglePrivacy: () -> Unit,
    onTabSelected: (BudgetScreenTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val haptics = rememberAppHaptics()
    val appColors = LocalAppColors.current

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                if (onMenuClick != null) {
                    DrawerMenuButton(onClick = onMenuClick)
                } else if (onNavigateBack != null) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Back",
                            tint = appColors.textPrimary
                        )
                    }
                }
                Text(
                    text = if (selectedTab == BudgetScreenTab.BUDGETS) strings.budgetTitle else strings.savingsGoalsTitle,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = appColors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = {
                        haptics.lightTick()
                        onTogglePrivacy()
                    }
                ) {
                    Icon(
                        imageVector = if (isPrivacyMode) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                        contentDescription = "Toggle Privacy",
                        tint = if (isPrivacyMode) appColors.accent else appColors.textSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                if (onNavigateToSettings != null) {
                    Spacer(modifier = Modifier.width(4.dp))
                    ProfileAvatarButton(onClick = onNavigateToSettings)
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Segmented Tabs: Budgets vs Savings Goals
        BudgetSegmentedTabBar(
            selectedTab = selectedTab,
            savingsGoalsCount = savingsGoalsCount,
            onTabSelected = onTabSelected
        )
    }
}
