package com.example.expensetracker.ui.market

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.expensetracker.theme.LocalAppColors

@Composable
fun MarketItemStatsRow(
    minRate: Double,
    avgRate: Double,
    maxRate: Double,
    unit: String,
    currencySymbol: String,
    isBangla: Boolean,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StatMetricCard(
            label = if (isBangla) "সর্বনিম্ন" else "Min",
            rate = minRate,
            unit = unit,
            currencySymbol = currencySymbol,
            color = appColors.income,
            isBangla = isBangla,
            modifier = Modifier.weight(1f)
        )
        StatMetricCard(
            label = if (isBangla) "গড় দর" else "Average",
            rate = avgRate,
            unit = unit,
            currencySymbol = currencySymbol,
            color = appColors.accent,
            isBangla = isBangla,
            modifier = Modifier.weight(1f)
        )
        StatMetricCard(
            label = if (isBangla) "সর্বোচ্চ" else "Max",
            rate = maxRate,
            unit = unit,
            currencySymbol = currencySymbol,
            color = appColors.expense,
            isBangla = isBangla,
            modifier = Modifier.weight(1f)
        )
    }
}
