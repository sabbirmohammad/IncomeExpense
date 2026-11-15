package com.example.expensetracker.ui.receivables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.DebtBookSummary
import com.example.expensetracker.data.model.PayablesSummary
import com.example.expensetracker.data.model.ReceivablesSummary
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.RadiusM
import com.example.expensetracker.theme.ScreenPaddingHorizontal
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.formatCurrency

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
    val appColors = LocalAppColors.current

    Column(modifier = modifier.fillMaxWidth()) {
        // Segmented Control (Tabs)
        DebtLedgerSegmentedTabs(
            selectedTab = selectedTab,
            onTabSelected = onTabSelected,
            summary = summary,
            payablesSummary = payablesSummary
        )

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
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
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
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFeatureSettings = "tnum"
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
