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
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.DebtBookSummary
import com.example.expensetracker.data.model.PayablesSummary
import com.example.expensetracker.data.model.ReceivablesSummary
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.RadiusM
import com.example.expensetracker.theme.RadiusS
import com.example.expensetracker.theme.ScreenPaddingHorizontal
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
                shape = RoundedCornerShape(RadiusS),
                color = if (selectedTab == DebtLedgerTab.RECEIVABLES) appColors.accent else appColors.surfaceRaised,
                border = if (selectedTab == DebtLedgerTab.RECEIVABLES || appColors.isDark) null else BorderStroke(0.5.dp, appColors.border),
                modifier = Modifier
                    .weight(1f)
                    .minimumInteractiveComponentSize()
                    .clickable {
                        haptics.lightTick()
                        onTabSelected(DebtLedgerTab.RECEIVABLES)
                    }
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = strings.tabReceivables,
                        fontSize = 13.sp,
                        fontWeight = if (selectedTab == DebtLedgerTab.RECEIVABLES) FontWeight.Bold else FontWeight.Medium,
                        color = if (selectedTab == DebtLedgerTab.RECEIVABLES) Color.White else appColors.textSecondary
                    )
                    if (summary.pendingCount > 0) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(if (selectedTab == DebtLedgerTab.RECEIVABLES) Color.White.copy(alpha = 0.25f) else appColors.accentContainer)
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
                shape = RoundedCornerShape(RadiusS),
                color = if (selectedTab == DebtLedgerTab.PAYABLES) appColors.accent else appColors.surfaceRaised,
                border = if (selectedTab == DebtLedgerTab.PAYABLES || appColors.isDark) null else BorderStroke(0.5.dp, appColors.border),
                modifier = Modifier
                    .weight(1f)
                    .minimumInteractiveComponentSize()
                    .clickable {
                        haptics.lightTick()
                        onTabSelected(DebtLedgerTab.PAYABLES)
                    }
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = strings.tabPayables,
                        fontSize = 13.sp,
                        fontWeight = if (selectedTab == DebtLedgerTab.PAYABLES) FontWeight.Bold else FontWeight.Medium,
                        color = if (selectedTab == DebtLedgerTab.PAYABLES) Color.White else appColors.textSecondary
                    )
                    if (payablesSummary.pendingCount > 0) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(if (selectedTab == DebtLedgerTab.PAYABLES) Color.White.copy(alpha = 0.25f) else appColors.expenseContainer)
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
            shape = RoundedCornerShape(RadiusM),
            color = appColors.surfaceRaised,
            border = if (appColors.isDark) null else BorderStroke(0.5.dp, appColors.border),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ScreenPaddingHorizontal, vertical = 4.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f, fill = false),
                    verticalAlignment = Alignment.CenterVertically
                ) {
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
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = appColors.textSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

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
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFeatureSettings = "tnum"
                        ),
                        color = if (debtBookSummary.netBalance > 0) appColors.income
                        else if (debtBookSummary.netBalance < 0) appColors.expense
                        else appColors.textPrimary,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }
        }
    }
}
