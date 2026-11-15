package com.example.expensetracker.ui.dashboard

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import com.example.expensetracker.data.model.TimeFilter
import com.example.expensetracker.ui.share.ShareExpenseSheet
import com.example.expensetracker.ui.share.SharePeriodType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DashboardShareSheetHost(
    show: Boolean,
    timeFilter: TimeFilter,
    onDismiss: () -> Unit
) {
    if (show) {
        val mappedPeriod = when (timeFilter) {
            TimeFilter.THIS_WEEK -> SharePeriodType.THIS_WEEK
            TimeFilter.THIS_MONTH -> SharePeriodType.THIS_MONTH
            TimeFilter.TODAY -> SharePeriodType.TODAY
            else -> SharePeriodType.THIS_MONTH
        }
        ShareExpenseSheet(
            initialPeriod = mappedPeriod,
            onDismiss = onDismiss
        )
    }
}
