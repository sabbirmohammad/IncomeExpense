package com.example.expensetracker.ui.analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.share.ShareExpenseSheet
import com.example.expensetracker.ui.share.SharePeriodType

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel,
    modifier: Modifier = Modifier,
    onNavigateToCategoryTransactions: (Long) -> Unit = {},
    onNavigateToSettings: (() -> Unit)? = null,
    onNavigateBack: (() -> Unit)? = null,
    onMenuClick: (() -> Unit)? = null
) {
    val strings = LocalAppStrings.current
    var showShareSheet by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize().background(LocalAppColors.current.background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            AnalyticsTopBar(
                title = strings.analyticsTitle,
                onMenuClick = onMenuClick,
                onNavigateBack = onNavigateBack,
                onShareClick = { showShareSheet = true },
                onNavigateToSettings = onNavigateToSettings
            )

            AnalyticsContent(
                viewModel = viewModel,
                onNavigateToCategoryTransactions = onNavigateToCategoryTransactions,
                modifier = Modifier.weight(1f)
            )
        }

        if (showShareSheet) {
            ShareExpenseSheet(
                initialPeriod = SharePeriodType.THIS_MONTH,
                onDismiss = { showShareSheet = false }
            )
        }
    }
}
