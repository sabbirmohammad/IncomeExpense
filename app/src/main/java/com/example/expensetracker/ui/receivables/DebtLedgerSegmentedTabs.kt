package com.example.expensetracker.ui.receivables

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
import com.example.expensetracker.data.model.PayablesSummary
import com.example.expensetracker.data.model.ReceivablesSummary
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.RadiusS
import com.example.expensetracker.theme.ScreenPaddingHorizontal
import com.example.expensetracker.ui.components.DateTimeHelper
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.rememberAppHaptics

@Composable
fun DebtLedgerSegmentedTabs(
    selectedTab: DebtLedgerTab,
    onTabSelected: (DebtLedgerTab) -> Unit,
    summary: ReceivablesSummary,
    payablesSummary: PayablesSummary,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val haptics = rememberAppHaptics()
    val appColors = LocalAppColors.current

    Row(
        modifier = modifier
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
}
