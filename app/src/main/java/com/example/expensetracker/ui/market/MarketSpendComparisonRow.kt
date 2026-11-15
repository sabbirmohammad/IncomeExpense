package com.example.expensetracker.ui.market

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.formatCurrency

fun formatMarketProgressCountText(
    totalCount: Int,
    purchasedCount: Int,
    isBangla: Boolean
): String {
    if (totalCount == 0) {
        return if (isBangla) "ফর্দে কোনো পণ্য নেই" else "No items in list"
    }
    val cTotal = if (isBangla) convertToBengaliDigits(totalCount.toString()) else totalCount.toString()
    val cBought = if (isBangla) convertToBengaliDigits(purchasedCount.toString()) else purchasedCount.toString()
    val percent = ((purchasedCount.toFloat() / totalCount) * 100).toInt()
    val pDigits = if (isBangla) convertToBengaliDigits(percent.toString()) else percent.toString()
    return if (isBangla) "$cBought/${cTotal}টি কেনা ($pDigits%)" else "$cBought/$cTotal bought ($pDigits%)"
}

@Composable
fun MarketSpendComparisonRow(
    totalSpent: Double,
    estimatedBudget: Double?,
    currencySymbol: String,
    isOverspent: Boolean,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val strings = LocalAppStrings.current

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "${strings.marketSpent}: ${formatCurrency(totalSpent, currencySymbol)}",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFeatureSettings = "tnum"
                ),
                color = if (isOverspent) appColors.warning else appColors.income
            )

            if (estimatedBudget != null && estimatedBudget > 0) {
                Text(
                    text = "•",
                    fontSize = 12.sp,
                    color = appColors.textMuted
                )
                Text(
                    text = "${strings.marketEstimated}: ${formatCurrency(estimatedBudget, currencySymbol)}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        fontFeatureSettings = "tnum"
                    ),
                    color = appColors.textSecondary
                )
            }
        }
    }
}
