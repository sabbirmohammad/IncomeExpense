package com.example.expensetracker.ui.budget

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorBorder
import com.example.expensetracker.theme.ColorSurface
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.theme.ColorTextSecondary
import com.example.expensetracker.ui.components.DrawerMenuButton
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.ProfileAvatarButton
import com.example.expensetracker.ui.components.convertToBengaliDigits
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
                            tint = ColorTextPrimary
                        )
                    }
                }
                Text(
                    text = if (selectedTab == BudgetScreenTab.BUDGETS) strings.budgetTitle else strings.savingsGoalsTitle,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = ColorTextPrimary,
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
                        tint = if (isPrivacyMode) ColorAccent else ColorTextSecondary,
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
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = ColorSurface,
            border = BorderStroke(1.dp, ColorBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Tab 1: Budgets
                val isBudgetsSelected = selectedTab == BudgetScreenTab.BUDGETS
                Surface(
                    shape = RoundedCornerShape(9.dp),
                    color = if (isBudgetsSelected) ColorAccent else Color.Transparent,
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            haptics.lightTick()
                            onTabSelected(BudgetScreenTab.BUDGETS)
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = strings.budgetTitle,
                            fontSize = 13.sp,
                            fontWeight = if (isBudgetsSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isBudgetsSelected) Color.White else ColorTextSecondary
                        )
                    }
                }

                // Tab 2: Savings Goals
                val isSavingsSelected = selectedTab == BudgetScreenTab.SAVINGS_GOALS
                Surface(
                    shape = RoundedCornerShape(9.dp),
                    color = if (isSavingsSelected) ColorAccent else Color.Transparent,
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            haptics.lightTick()
                            onTabSelected(BudgetScreenTab.SAVINGS_GOALS)
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = strings.savingsGoalsTitle,
                            fontSize = 13.sp,
                            fontWeight = if (isSavingsSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSavingsSelected) Color.White else ColorTextSecondary
                        )
                        if (savingsGoalsCount > 0) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(if (isSavingsSelected) Color.White.copy(alpha = 0.25f) else ColorAccent.copy(alpha = 0.15f))
                                    .padding(horizontal = 6.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = if (strings.isBangla) convertToBengaliDigits(savingsGoalsCount.toString()) else savingsGoalsCount.toString(),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSavingsSelected) Color.White else ColorAccent
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
