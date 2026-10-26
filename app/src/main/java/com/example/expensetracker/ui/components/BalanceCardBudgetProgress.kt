package com.example.expensetracker.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.LocalAppColors

@Composable
fun BalanceCardBudgetProgress(
    budgetAmount: Double,
    budgetSpent: Double,
    currencySymbol: String,
    useBengaliNumerals: Boolean,
    strings: Strings,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    if (budgetAmount <= 0) return

    val usedRatio = (budgetSpent / budgetAmount).toFloat()
    val usedPct = (usedRatio * 100).toInt()
    val formattedPct = if (useBengaliNumerals) convertToBengaliDigits(usedPct.toString()) else usedPct.toString()
    val progressFillColor = when {
        usedRatio > 0.95f -> appColors.expense
        usedRatio > 0.8f -> appColors.warning
        else -> appColors.income
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${strings.navBudgets} · $formattedPct% ${strings.budgetUsedPercent}",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = appColors.textSecondary
            )
            Text(
                text = "${formatCurrency(budgetSpent, currencySymbol, useBengaliNumerals)} / ${formatCurrency(budgetAmount, currencySymbol, useBengaliNumerals)}",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = appColors.textMuted
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        AppProgressBar(
            progress = usedRatio,
            height = 8.dp,
            fillColor = progressFillColor
        )
    }
}
