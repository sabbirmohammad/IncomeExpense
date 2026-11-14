package com.example.expensetracker.ui.transactions

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.TimeFilter
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.AppChip
import com.example.expensetracker.ui.components.LocalAppStrings

@Composable
fun TransactionFilterTimeSection(
    selectedTimeFilter: TimeFilter,
    onTimeFilterSelected: (TimeFilter) -> Unit,
    onOpenCustomDateRange: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val strings = LocalAppStrings.current

    Column(modifier = modifier) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = strings.timeFilter,
            style = MaterialTheme.typography.labelMedium.copy(fontSize = 13.sp, fontWeight = FontWeight.SemiBold),
            color = appColors.textSecondary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AppChip(
                label = strings.thisMonth,
                selected = selectedTimeFilter == TimeFilter.THIS_MONTH,
                onClick = { onTimeFilterSelected(TimeFilter.THIS_MONTH) }
            )
            AppChip(
                label = strings.today,
                selected = selectedTimeFilter == TimeFilter.TODAY,
                onClick = { onTimeFilterSelected(TimeFilter.TODAY) }
            )
            AppChip(
                label = strings.thisWeek,
                selected = selectedTimeFilter == TimeFilter.THIS_WEEK,
                onClick = { onTimeFilterSelected(TimeFilter.THIS_WEEK) }
            )
            AppChip(
                label = strings.thisYear,
                selected = selectedTimeFilter == TimeFilter.THIS_YEAR,
                onClick = { onTimeFilterSelected(TimeFilter.THIS_YEAR) }
            )
            AppChip(
                label = strings.allTime,
                selected = selectedTimeFilter == TimeFilter.ALL,
                onClick = { onTimeFilterSelected(TimeFilter.ALL) }
            )
            AppChip(
                label = strings.customRange,
                selected = selectedTimeFilter == TimeFilter.CUSTOM,
                onClick = {
                    onTimeFilterSelected(TimeFilter.CUSTOM)
                    onOpenCustomDateRange()
                }
            )
        }
    }
}
