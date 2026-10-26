package com.example.expensetracker.ui.receivables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.DebtBookSummary
import com.example.expensetracker.data.model.PayablesSummary
import com.example.expensetracker.data.model.ReceivablesSummary
import com.example.expensetracker.theme.CardCornerRadius
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.ScreenPaddingHorizontal
import com.example.expensetracker.theme.SmallElementRadius
import com.example.expensetracker.ui.components.DateTimeHelper
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.formatCurrency
import com.example.expensetracker.ui.components.rememberAppHaptics

@Composable
fun ReceivablesTabsAndNetBanner(
    selectedTab: DebtLedgerTab,
    onTabSelected: (DebtLedgerTab) -> Unit,
    summary: ReceivablesSummary,
    payablesSummary: PayablesSummary,
    debtBookSummary: DebtBookSummary,
    isPrivacyMode: Boolean,
    currencySymbol: String,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val haptics = rememberAppHaptics()
    val appColors = LocalAppColors.current

    Column(modifier = modifier.fillMaxWidth()) {
        // Segmented Control (Tabs)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ScreenPaddingHorizontal, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Tab 1: Receivables
            Surface(
                shape = RoundedCornerShape(SmallElementRadius),
                color = if (selectedTab == DebtLedgerTab.RECEIVABLES) appColors.accent else appColors.surface,
                border = if (selectedTab == DebtLedgerTab.RECEIVABLES) null else BorderStroke(1.dp, appColors.border),
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        haptics.lightTick()
                        onTabSelected(DebtLedgerTab.RECEIVABLES)
                    }
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 9.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = strings.tabReceivables,
                        fontSize = 12.5.sp,
                        fontWeight = if (selectedTab == DebtLedgerTab.RECEIVABLES) FontWeight.Bold else FontWeight.Medium,
                        color = if (selectedTab == DebtLedgerTab.RECEIVABLES) Color.White else appColors.textSecondary
                    )
                    if (summary.pendingCount > 0) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(if (selectedTab == DebtLedgerTab.RECEIVABLES) Color.White.copy(alpha = 0.25f) else appColors.accent.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = DateTimeHelper.formatCounter(summary.pendingCount, strings.isBangla),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedTab == DebtLedgerTab.RECEIVABLES) Color.White else appColors.accent
                            )
                        }
                    }
                }
            }

            // Tab 2: Payables
            Surface(
                shape = RoundedCornerShape(SmallElementRadius),
                color = if (selectedTab == DebtLedgerTab.PAYABLES) appColors.accent else appColors.surface,
                border = if (selectedTab == DebtLedgerTab.PAYABLES) null else BorderStroke(1.dp, appColors.border),
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        haptics.lightTick()
                        onTabSelected(DebtLedgerTab.PAYABLES)
                    }
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 9.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = strings.tabPayables,
                        fontSize = 12.5.sp,
                        fontWeight = if (selectedTab == DebtLedgerTab.PAYABLES) FontWeight.Bold else FontWeight.Medium,
                        color = if (selectedTab == DebtLedgerTab.PAYABLES) Color.White else appColors.textSecondary
                    )
                    if (payablesSummary.pendingCount > 0) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(if (selectedTab == DebtLedgerTab.PAYABLES) Color.White.copy(alpha = 0.25f) else appColors.expense.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = DateTimeHelper.formatCounter(payablesSummary.pendingCount, strings.isBangla),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedTab == DebtLedgerTab.PAYABLES) Color.White else appColors.expense
                            )
                        }
                    }
                }
            }
        }

        // Net Position Banner
        Surface(
            shape = RoundedCornerShape(CardCornerRadius),
            color = appColors.surface,
            border = BorderStroke(1.dp, appColors.border),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ScreenPaddingHorizontal, vertical = 4.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(
                                if (debtBookSummary.netBalance > 0) appColors.income
                                else if (debtBookSummary.netBalance < 0) appColors.expense
                                else appColors.textSecondary
                            )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = strings.netPosition,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                        color = appColors.textSecondary
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val netText = if (isPrivacyMode) {
                        "••••••"
                    } else {
                        val prefix = if (debtBookSummary.netBalance > 0) "+" else ""
                        "$prefix ${formatCurrency(debtBookSummary.netBalance, currencySymbol)}"
                    }
                    Text(
                        text = netText,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = if (debtBookSummary.netBalance > 0) appColors.income
                        else if (debtBookSummary.netBalance < 0) appColors.expense
                        else appColors.textPrimary
                    )
                }
            }
        }
    }
}
