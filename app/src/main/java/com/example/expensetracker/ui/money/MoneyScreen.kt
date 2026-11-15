package com.example.expensetracker.ui.money

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.accounts.AccountsContent
import com.example.expensetracker.ui.accounts.AccountsViewModel
import com.example.expensetracker.ui.analytics.AnalyticsContent
import com.example.expensetracker.ui.analytics.AnalyticsViewModel
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.PulseSegmented
import com.example.expensetracker.ui.navigation.MoneySegment
import com.example.expensetracker.ui.reports.ReportsViewModel
import com.example.expensetracker.ui.transactions.TransactionsContent
import com.example.expensetracker.ui.transactions.TransactionsViewModel

/**
 * MoneyScreen:
 * Pulse Tab 2 (Money / টাকা):
 * - Segments: Activity (লেনদেন) · Wallets (ওয়ালেট) · Insights (ইনসাইটস)
 * - Hosted via PulseSegmented
 * - Segment 0: TransactionsContent
 * - Segment 1: AccountsContent
 * - Segment 2: AnalyticsContent
 */
@Composable
fun MoneyScreen(
    onEditTransaction: (Transaction) -> Unit,
    modifier: Modifier = Modifier,
    selectedSegment: MoneySegment = MoneySegment.ACTIVITY,
    onSegmentSelected: (MoneySegment) -> Unit = {},
    onAddTransactionForDate: ((Long) -> Unit)? = null,
    transactionsViewModel: TransactionsViewModel = viewModel(),
    accountsViewModel: AccountsViewModel = viewModel(),
    analyticsViewModel: AnalyticsViewModel = viewModel(),
    reportsViewModel: ReportsViewModel = viewModel()
) {
    val appColors = LocalAppColors.current
    val strings = LocalAppStrings.current

    val segmentTitles = remember(strings.moneySegmentActivity, strings.moneySegmentWallets, strings.moneySegmentInsights) {
        listOf(
            strings.moneySegmentActivity,
            strings.moneySegmentWallets,
            strings.moneySegmentInsights
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(appColors.background)
    ) {
        // Pulse Segmented Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            PulseSegmented(
                items = segmentTitles,
                selectedIndex = selectedSegment.index,
                onItemSelected = { index ->
                    onSegmentSelected(MoneySegment.fromIndex(index))
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Active Segment Body
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            when (selectedSegment) {
                MoneySegment.ACTIVITY -> {
                    TransactionsContent(
                        viewModel = transactionsViewModel,
                        onEditTransaction = onEditTransaction,
                        onAddTransactionForDate = onAddTransactionForDate
                    )
                }
                MoneySegment.WALLETS -> {
                    AccountsContent(
                        viewModel = accountsViewModel
                    )
                }
                MoneySegment.INSIGHTS -> {
                    AnalyticsContent(
                        viewModel = analyticsViewModel,
                        onNavigateToCategoryTransactions = { categoryId ->
                            transactionsViewModel.setCategoryFilter(categoryId)
                            onSegmentSelected(MoneySegment.ACTIVITY)
                        }
                    )
                }
            }
        }
    }
}
