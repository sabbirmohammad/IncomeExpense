package com.example.expensetracker.ui.dashboard

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.AssignmentReturn
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.DebtBookSummary
import com.example.expensetracker.theme.ScreenPaddingHorizontal
import com.example.expensetracker.ui.components.AppCard
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.formatCurrency
import com.example.expensetracker.theme.LocalAppColors

@Composable
fun DashboardDebtsLedgerWidget(
    debtBookSummary: DebtBookSummary,
    currencySymbol: String,
    isPrivacyMode: Boolean,
    onNavigateToReceivables: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val strings = LocalAppStrings.current
    AppCard(
        modifier = modifier
            .padding(horizontal = ScreenPaddingHorizontal)
            .clickable { onNavigateToReceivables?.invoke() },
        padding = 14.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(appColors.warning.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.AssignmentReturn,
                        contentDescription = null,
                        tint = appColors.warning,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Column {
                    Text(
                        text = strings.debtsLedgerTitle,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = appColors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isPrivacyMode) "••••••"
                        else "${strings.netPosition}: ${formatCurrency(debtBookSummary.netBalance, currencySymbol)}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = if (debtBookSummary.netBalance >= 0) appColors.income else appColors.expense
                    )
                    val subtitleText = if (strings.isBangla) {
                        "পাওনা: ${if (isPrivacyMode) "••••" else formatCurrency(debtBookSummary.totalReceivable, currencySymbol)} · দেনা: ${if (isPrivacyMode) "••••" else formatCurrency(debtBookSummary.totalPayable, currencySymbol)}"
                    } else {
                        "Owed to you: ${if (isPrivacyMode) "••••" else formatCurrency(debtBookSummary.totalReceivable, currencySymbol)} · You owe: ${if (isPrivacyMode) "••••" else formatCurrency(debtBookSummary.totalPayable, currencySymbol)}"
                    }
                    Text(
                        text = subtitleText,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = appColors.textSecondary
                    )
                }
            }

            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = appColors.textSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
