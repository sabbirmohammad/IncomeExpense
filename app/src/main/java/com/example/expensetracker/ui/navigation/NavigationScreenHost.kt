package com.example.expensetracker.ui.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
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
import com.example.expensetracker.ui.market.MarketScreen
import com.example.expensetracker.ui.receivables.ReceivablesScreen
import com.example.expensetracker.ui.receivables.ReceivablesViewModel
import com.example.expensetracker.ui.reports.ReportsScreen
import com.example.expensetracker.ui.reports.ReportsViewModel
import com.example.expensetracker.ui.settings.SettingsScreen
import com.example.expensetracker.ui.settings.SettingsViewModel
import com.example.expensetracker.ui.transactions.TransactionsScreen
import com.example.expensetracker.ui.transactions.TransactionsViewModel

/**
 * ARCHITECTURAL INVARIANT (NO REDUNDANT TOP-BAR HAMBURGER MENUS):
 *
 * The 3D side navigation drawer is exclusively accessed via the bottom navigation bar's
 * 'More' tab (ModernBottomNavBar) or edge swipe gesture.
 *
 * DO NOT pass `onMenuClick` to any screen component (DashboardScreen, TransactionsScreen, etc.):
 * - On primary screens (Dashboard, Transactions), omitting `onMenuClick` keeps a clean,
 *   uncluttered One UI header without duplicate drawer icons.
 * - On secondary screens (Accounts, Bills, Receivables, Budgets, Reports, Settings),
 *   omitting `onMenuClick` allows standard back-arrow navigation (`onNavigateBack`) to return
 *   to Home, rather than displaying an unnecessary duplicate hamburger button.
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
        transitionSpec = {
            (fadeIn(animationSpec = tween(220, delayMillis = 40)) +
             scaleIn(initialScale = 0.98f, animationSpec = tween(220, delayMillis = 40)))
                .togetherWith(
                    fadeOut(animationSpec = tween(140))
                )
        },
        label = "ScreenTransition",
        modifier = modifier.fillMaxSize()
    ) { targetScreen ->
        when (targetScreen) {
            NavigationItem.HOME -> {
                DashboardScreen(
                    viewModel = dashboardViewModel,
                    onNavigateToTransactions = {
                        transactionsViewModel.setCategoryFilter(null)
                        onNavigate(NavigationItem.TRANSACTIONS)
                    },
                    onNavigateToSettings = { onNavigate(NavigationItem.SETTINGS) },
                    onOpenAddTransaction = {
                        onOpenAddTransaction(TransactionType.EXPENSE, null)
                    },
                    onEditTransaction = onEditTransaction,
                    onNavigateToAnalytics = {
                        onNavigate(NavigationItem.ANALYTICS)
                    },
                    onNavigateToAccounts = {
                        onNavigate(NavigationItem.ACCOUNTS)
                    },
                    onNavigateToReceivables = {
                        onNavigate(NavigationItem.RECEIVABLES)
                    },
                    onNavigateToBills = {
                        onNavigate(NavigationItem.BILLS)
                    },
                    onNavigateToBudgets = {
                        onNavigate(NavigationItem.BUDGETS)
                    },
                    onSplitBillClick = onSplitBillClick,
                    onOpenAddExpense = {
                        onOpenAddTransaction(TransactionType.EXPENSE, null)
                    },
                    onOpenAddIncome = {
                        onOpenAddTransaction(TransactionType.INCOME, null)
                    },
                    onTransferClick = onTransferClick,
                    onOpenMarketList = onOpenMarketList
                )
            }
            NavigationItem.TRANSACTIONS -> {
                TransactionsScreen(
                    viewModel = transactionsViewModel,
                    onEditTransaction = onEditTransaction,
                    onAddTransactionForDate = { dateMillis ->
                        onOpenAddTransaction(TransactionType.EXPENSE, dateMillis)
                    },
                    onNavigateToSettings = { onNavigate(NavigationItem.SETTINGS) }
                )
            }
            NavigationItem.MARKET -> {
                MarketScreen(
                    dashboardViewModel = dashboardViewModel,
                    onNavigateToSettings = { onNavigate(NavigationItem.SETTINGS) }
                )
            }
            NavigationItem.ANALYTICS -> {
                val analyticsViewModel: AnalyticsViewModel = viewModel()
                AnalyticsScreen(
                    viewModel = analyticsViewModel,
                    onNavigateToCategoryTransactions = { categoryId ->
                        transactionsViewModel.setCategoryFilter(categoryId)
                        onNavigate(NavigationItem.TRANSACTIONS)
                    },
                    onNavigateToSettings = { onNavigate(NavigationItem.SETTINGS) },
                    onNavigateBack = { onNavigate(NavigationItem.HOME) }
                )
            }
            NavigationItem.ACCOUNTS -> {
                val accountsViewModel: AccountsViewModel = viewModel()
                AccountsScreen(
                    viewModel = accountsViewModel,
                    onNavigateBack = { onNavigate(NavigationItem.HOME) }
                )
            }
            NavigationItem.BILLS -> {
                val billsViewModel: BillsViewModel = viewModel()
                BillsScreen(
                    viewModel = billsViewModel,
                    onNavigateBack = { onNavigate(NavigationItem.HOME) }
                )
            }
            NavigationItem.RECEIVABLES -> {
                val receivablesViewModel: ReceivablesViewModel = viewModel()
                ReceivablesScreen(
                    viewModel = receivablesViewModel,
                    onNavigateBack = { onNavigate(NavigationItem.HOME) }
                )
            }
            NavigationItem.BUDGETS -> {
                val budgetViewModel: BudgetViewModel = viewModel()
                BudgetScreen(
                    viewModel = budgetViewModel,
                    onNavigateToSettings = { onNavigate(NavigationItem.SETTINGS) },
                    onNavigateBack = { onNavigate(NavigationItem.HOME) }
                )
            }
            NavigationItem.REPORTS -> {
                val reportsViewModel: ReportsViewModel = viewModel()
                ReportsScreen(
                    viewModel = reportsViewModel,
                    onNavigateBack = { onNavigate(NavigationItem.HOME) }
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
