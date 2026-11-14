package com.example.expensetracker.ui.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.ui.accounts.AccountsScreen
import com.example.expensetracker.ui.accounts.AccountsViewModel
import com.example.expensetracker.ui.analytics.AnalyticsScreen
import com.example.expensetracker.ui.analytics.AnalyticsViewModel
import com.example.expensetracker.ui.bills.BillsScreen
import com.example.expensetracker.ui.bills.BillsViewModel
import com.example.expensetracker.ui.budget.BudgetScreen
import com.example.expensetracker.ui.budget.BudgetViewModel
import com.example.expensetracker.ui.dashboard.DashboardScreen
import com.example.expensetracker.ui.dashboard.DashboardViewModel
import com.example.expensetracker.ui.ledger.LedgerScreen
import com.example.expensetracker.ui.market.MarketScreen
import com.example.expensetracker.ui.plan.PlanScreen
import com.example.expensetracker.ui.receivables.ReceivablesScreen
import com.example.expensetracker.ui.receivables.ReceivablesViewModel
import com.example.expensetracker.ui.reports.ReportsScreen
import com.example.expensetracker.ui.reports.ReportsViewModel
import com.example.expensetracker.ui.settings.SettingsScreen
import com.example.expensetracker.ui.settings.SettingsViewModel
import com.example.expensetracker.ui.transactions.TransactionsViewModel

/**
 * NavigationScreenHost (Living Ledger 2.0 5-Destination Architecture):
 * - 5 Top-level Destinations: HOME, LEDGER, BAZAAR, WALLETS, PLAN
 * - LEDGER hosts Transactions · Insights (Analytics) · Reports via segmented control
 * - PLAN hosts Budgets · Goals · Bills · Debts via segmented control
 * - Settings is a pushed screen with back navigation returning to HOME.
 */
@Composable
fun NavigationScreenHost(
    selectedItem: NavigationItem,
    onNavigate: (NavigationItem) -> Unit,
    onOpenAddTransaction: (TransactionType, Long?) -> Unit,
    onEditTransaction: (Transaction) -> Unit,
    onSplitBillClick: () -> Unit,
    onTransferClick: () -> Unit,
    onOpenMarketList: () -> Unit,
    dashboardViewModel: DashboardViewModel,
    modifier: Modifier = Modifier,
    onOpenMenu: () -> Unit = {}
) {
    val transactionsViewModel: TransactionsViewModel = viewModel()

    AnimatedContent(
        targetState = selectedItem,
        transitionSpec = { navigationScreenTransitionSpec() },
        label = "ScreenTransition",
        modifier = modifier.fillMaxSize()
    ) { targetScreen ->
        when (targetScreen) {
            NavigationItem.HOME -> DashboardScreen(
                viewModel = dashboardViewModel,
                onNavigateToTransactions = {
                    transactionsViewModel.setCategoryFilter(null)
                    onNavigate(NavigationItem.LEDGER)
                },
                onNavigateToSettings = { onNavigate(NavigationItem.SETTINGS) },
                onOpenAddTransaction = { onOpenAddTransaction(TransactionType.EXPENSE, null) },
                onEditTransaction = onEditTransaction,
                onNavigateToAnalytics = { onNavigate(NavigationItem.ANALYTICS) },
                onNavigateToAccounts = { onNavigate(NavigationItem.WALLETS) },
                onNavigateToReceivables = { onNavigate(NavigationItem.RECEIVABLES) },
                onNavigateToBills = { onNavigate(NavigationItem.BILLS) },
                onNavigateToBudgets = { onNavigate(NavigationItem.BUDGETS) },
                onSplitBillClick = onSplitBillClick,
                onOpenAddExpense = { onOpenAddTransaction(TransactionType.EXPENSE, null) },
                onOpenAddIncome = { onOpenAddTransaction(TransactionType.INCOME, null) },
                onTransferClick = onTransferClick,
                onOpenMarketList = onOpenMarketList
            )
            NavigationItem.LEDGER -> {
                val analyticsViewModel: AnalyticsViewModel = viewModel()
                val reportsViewModel: ReportsViewModel = viewModel()
                LedgerScreen(
                    transactionsViewModel = transactionsViewModel,
                    analyticsViewModel = analyticsViewModel,
                    reportsViewModel = reportsViewModel,
                    onEditTransaction = onEditTransaction,
                    onAddTransactionForDate = { dateMillis -> onOpenAddTransaction(TransactionType.EXPENSE, dateMillis) },
                    initialSegment = 0
                )
            }
            NavigationItem.TRANSACTIONS -> {
                val analyticsViewModel: AnalyticsViewModel = viewModel()
                val reportsViewModel: ReportsViewModel = viewModel()
                LedgerScreen(
                    transactionsViewModel = transactionsViewModel,
                    analyticsViewModel = analyticsViewModel,
                    reportsViewModel = reportsViewModel,
                    onEditTransaction = onEditTransaction,
                    onAddTransactionForDate = { dateMillis -> onOpenAddTransaction(TransactionType.EXPENSE, dateMillis) },
                    initialSegment = 0
                )
            }
            NavigationItem.ANALYTICS -> {
                val analyticsViewModel: AnalyticsViewModel = viewModel()
                AnalyticsScreen(
                    viewModel = analyticsViewModel,
                    onNavigateToCategoryTransactions = { categoryId ->
                        transactionsViewModel.setCategoryFilter(categoryId)
                        onNavigate(NavigationItem.LEDGER)
                    },
                    onNavigateToSettings = { onNavigate(NavigationItem.SETTINGS) },
                    onNavigateBack = { onNavigate(NavigationItem.MONEY) }
                )
            }
            NavigationItem.REPORTS -> {
                val reportsViewModel: ReportsViewModel = viewModel()
                ReportsScreen(
                    viewModel = reportsViewModel,
                    onNavigateBack = { onNavigate(NavigationItem.MONEY) }
                )
            }
            NavigationItem.BAZAAR -> {
                MarketScreen(dashboardViewModel = dashboardViewModel)
            }
            NavigationItem.MARKET -> {
                MarketScreen(dashboardViewModel = dashboardViewModel)
            }
            NavigationItem.WALLETS -> {
                val accountsViewModel: AccountsViewModel = viewModel()
                AccountsScreen(
                    viewModel = accountsViewModel,
                    onNavigateBack = { onNavigate(NavigationItem.HOME) }
                )
            }
            NavigationItem.ACCOUNTS -> {
                val accountsViewModel: AccountsViewModel = viewModel()
                AccountsScreen(
                    viewModel = accountsViewModel,
                    onNavigateBack = { onNavigate(NavigationItem.MONEY) }
                )
            }
            NavigationItem.MONEY -> {
                val accountsViewModel: AccountsViewModel = viewModel()
                AccountsScreen(
                    viewModel = accountsViewModel,
                    onNavigateBack = { onNavigate(NavigationItem.HOME) }
                )
            }
            NavigationItem.PLAN -> {
                val budgetViewModel: BudgetViewModel = viewModel()
                val billsViewModel: BillsViewModel = viewModel()
                val receivablesViewModel: ReceivablesViewModel = viewModel()
                PlanScreen(
                    budgetViewModel = budgetViewModel,
                    billsViewModel = billsViewModel,
                    receivablesViewModel = receivablesViewModel,
                    initialSegment = 0
                )
            }
            NavigationItem.BUDGETS -> {
                val budgetViewModel: BudgetViewModel = viewModel()
                BudgetScreen(
                    viewModel = budgetViewModel,
                    onNavigateToSettings = { onNavigate(NavigationItem.SETTINGS) },
                    onNavigateBack = { onNavigate(NavigationItem.MONEY) }
                )
            }
            NavigationItem.BILLS -> {
                val billsViewModel: BillsViewModel = viewModel()
                BillsScreen(
                    viewModel = billsViewModel,
                    onNavigateBack = { onNavigate(NavigationItem.MONEY) }
                )
            }
            NavigationItem.RECEIVABLES -> {
                val receivablesViewModel: ReceivablesViewModel = viewModel()
                ReceivablesScreen(
                    viewModel = receivablesViewModel,
                    onNavigateBack = { onNavigate(NavigationItem.MONEY) }
                )
            }
            NavigationItem.SETTINGS -> {
                val settingsViewModel: SettingsViewModel = viewModel()
                SettingsScreen(
                    viewModel = settingsViewModel,
                    onNavigateBack = { onNavigate(NavigationItem.HOME) }
                )
            }
        }
    }
}
