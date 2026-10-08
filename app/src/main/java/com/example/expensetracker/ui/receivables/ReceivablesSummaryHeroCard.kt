package com.example.expensetracker.ui.receivables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.ReceivablesSummary
import com.example.expensetracker.theme.ColorDangerExpense
import com.example.expensetracker.theme.ColorSuccessIncome
import com.example.expensetracker.theme.ColorTextMuted
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.ui.components.AppCard
import com.example.expensetracker.ui.components.HairlineDivider
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.formatCurrency

@Composable
fun ReceivablesSummaryHeroCard(
    summary: ReceivablesSummary,
    currencySymbol: String,
    isPrivacyMode: Boolean,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current

    AppCard(
        modifier = modifier.fillMaxWidth(),
        padding = 18.dp
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = strings.remainingDue,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = ColorTextMuted
                )
                if (summary.pendingCount > 0) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(ColorDangerExpense.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${summary.pendingCount} ${strings.pendingBadge}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ColorDangerExpense
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (isPrivacyMode) "••••••" else formatCurrency(summary.remainingDue, currencySymbol),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = ColorTextPrimary
            )

            Spacer(modifier = Modifier.height(16.dp))
            HairlineDivider()
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(strings.totalLent, fontSize = 11.sp, color = ColorTextMuted)
                    Text(
                        text = if (isPrivacyMode) "••••" else formatCurrency(summary.totalLent, currencySymbol),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ColorTextPrimary
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(strings.totalCollected, fontSize = 11.sp, color = ColorTextMuted)
                    Text(
                        text = if (isPrivacyMode) "••••" else formatCurrency(summary.totalCollected, currencySymbol),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ColorSuccessIncome
                    )
                }
            }
        }
    }
}
