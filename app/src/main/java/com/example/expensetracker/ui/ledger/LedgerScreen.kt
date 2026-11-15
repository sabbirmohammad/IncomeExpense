package com.example.expensetracker.ui.ledger

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.analytics.AnalyticsScreen
import com.example.expensetracker.ui.analytics.AnalyticsViewModel
import com.example.expensetracker.ui.components.AppTopBar
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.SegmentedControl
import com.example.expensetracker.ui.reports.ReportsScreen
import com.example.expensetracker.ui.reports.ReportsViewModel
import com.example.expensetracker.ui.transactions.TransactionsScreen
import com.example.expensetracker.ui.transactions.TransactionsViewModel

/**
 * LedgerScreen:
 * Living Ledger 2.0 Tab 2 (খাতা):
 * - Top: AppTopBar (Ledger / খাতা)
 * - 3-Segment SegmentedControl: Transactions · Insights · Reports (লেনদেন · অ্যানালিটিক্স · রিপোর্ট)
 * - Persists selected segment across tab navigation via rememberSaveable
 * - Fully hosts existing high-fidelity modules seamlessly
 */
@Composable
fun LedgerScreen(
    transactionsViewModel: TransactionsViewModel,
    analyticsViewModel: AnalyticsViewModel,
    reportsViewModel: ReportsViewModel,
    onEditTransaction: (Transaction) -> Unit,
    modifier: Modifier = Modifier,
    initialSegment: Int = 0,
    onAddTransactionForDate: ((Long) -> Unit)? = null
) {
    val appColors = LocalAppColors.current
    val strings = LocalAppStrings.current

    var selectedSegment by rememberSaveable { mutableIntStateOf(initialSegment) }

    val segmentTitles = remember(strings.navTransactions, strings.navAnalytics, strings.navReports) {
        listOf(
            strings.navTransactions,
            strings.navAnalytics,
            strings.navReports
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(appColors.background)
    ) {
        // AppTopBar (no back button on top-level destination)
        AppTopBar(
            title = strings.navLedger
        )

        // 3-way Segmented Control
        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
            SegmentedControl(
                items = segmentTitles,
                selectedIndex = selectedSegment,
                onItemSelected = { selectedSegment = it }
            )
        }

        // Active Segment Body
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            when (selectedSegment) {
                0 -> {
                    TransactionsScreen(
                        viewModel = transactionsViewModel,
                        onEditTransaction = onEditTransaction,
                        onAddTransactionForDate = onAddTransactionForDate
                    )
                }
                1 -> {
                    AnalyticsScreen(
                        viewModel = analyticsViewModel,
                        onNavigateToCategoryTransactions = { categoryId ->
                            transactionsViewModel.setCategoryFilter(categoryId)
                            selectedSegment = 0
                        }
                    )
                }
                2 -> {
                    ReportsScreen(
                        viewModel = reportsViewModel
                    )
                }
            }
        }
    }
}
