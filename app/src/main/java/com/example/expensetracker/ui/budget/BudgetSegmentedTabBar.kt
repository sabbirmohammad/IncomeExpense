package com.example.expensetracker.ui.budget

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.rememberAppHaptics

@Composable
fun BudgetSegmentedTabBar(
    selectedTab: BudgetScreenTab,
    savingsGoalsCount: Int,
    onTabSelected: (BudgetScreenTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val haptics = rememberAppHaptics()
    val appColors = LocalAppColors.current

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = appColors.surfaceRaised,
        border = if (appColors.isDark) null else BorderStroke(0.5.dp, appColors.border),
        modifier = modifier.fillMaxWidth()
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
                color = if (isBudgetsSelected) appColors.accent else Color.Transparent,
                modifier = Modifier
                    .weight(1f)
                    .minimumInteractiveComponentSize()
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
                        color = if (isBudgetsSelected) Color.White else appColors.textSecondary
                    )
                }
            }

            // Tab 2: Savings Goals
            val isSavingsSelected = selectedTab == BudgetScreenTab.SAVINGS_GOALS
            Surface(
                shape = RoundedCornerShape(9.dp),
                color = if (isSavingsSelected) appColors.accent else Color.Transparent,
                modifier = Modifier
                    .weight(1f)
                    .minimumInteractiveComponentSize()
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
                        color = if (isSavingsSelected) Color.White else appColors.textSecondary
                    )
                    if (savingsGoalsCount > 0) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(if (isSavingsSelected) Color.White.copy(alpha = 0.25f) else appColors.accent.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = if (strings.isBangla) convertToBengaliDigits(savingsGoalsCount.toString()) else savingsGoalsCount.toString(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSavingsSelected) Color.White else appColors.accent
                            )
                        }
                    }
                }
            }
        }
    }
}
