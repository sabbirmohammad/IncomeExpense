package com.example.expensetracker.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.ui.accounts.AccountsScreen
import com.example.expensetracker.ui.accounts.AccountsViewModel
import com.example.expensetracker.ui.analytics.AnalyticsScreen
import com.example.expensetracker.ui.analytics.AnalyticsViewModel
import com.example.expensetracker.ui.bazaar.BazaarHubScreen
import com.example.expensetracker.ui.bazaar.BazaarPlanScreen
import com.example.expensetracker.ui.bazaar.BazaarViewModel
import com.example.expensetracker.ui.bills.BillsScreen
import com.example.expensetracker.ui.bills.BillsViewModel
import com.example.expensetracker.ui.budget.BudgetScreen
import com.example.expensetracker.ui.budget.BudgetViewModel
import com.example.expensetracker.ui.dashboard.DashboardScreen
import com.example.expensetracker.ui.dashboard.DashboardViewModel
import com.example.expensetracker.ui.ledger.LedgerScreen
import com.example.expensetracker.ui.market.MarketScreen
import com.example.expensetracker.ui.money.MoneyScreen
import com.example.expensetracker.ui.plan.PlanScreen
import com.example.expensetracker.ui.receivables.ReceivablesScreen
import com.example.expensetracker.ui.receivables.ReceivablesViewModel
import com.example.expensetracker.ui.reports.ReportsScreen
import com.example.expensetracker.ui.reports.ReportsViewModel
import com.example.expensetracker.ui.settings.SettingsScreen
import com.example.expensetracker.ui.settings.SettingsViewModel
import com.example.expensetracker.ui.transactions.TransactionsViewModel

/**
 * NavigationScreenHost (Pulse Design Language Architecture):
 * - Primary entrypoint taking [AdaptiveNavigationState]:
 *     PushedScreen (e.g. Settings, Bazaar Plan) or PulseTab (HOME, MONEY, BAZAAR, PLAN)
 * - Retains backward-compatible overload for legacy tests & callers.
 */
@Composable
fun NavigationScreenHost(
    navState: AdaptiveNavigationState,
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
    val bazaarViewModel: BazaarViewModel = viewModel()
    var activeShoppingTripId by remember { mutableStateOf<Long?>(null) }

    BackHandler(enabled = activeShoppingTripId != null) {
        activeShoppingTripId = null
    }

    val pushed = navState.pushedScreen
    if (pushed != null) {
        when (pushed) {
            PushedScreen.SETTINGS -> {
                val settingsViewModel: SettingsViewModel = viewModel()
                SettingsScreen(
                    viewModel = settingsViewModel,
                    onNavigateBack = { navState.popScreen() }
                )
            }
            is PushedScreen.BAZAAR_PLAN -> {
                BazaarPlanScreen(
                    listId = pushed.listId,
                    bazaarViewModel = bazaarViewModel,
                    onNavigateBack = { navState.popScreen() },
                    onStartShopping = { shoppingListId ->
                        navState.popScreen()
                        activeShoppingTripId = shoppingListId
                    }
                )
            }
        }
        return
    }

    AnimatedContent(
        targetState = navState.selectedTab,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "PulseScreenTransition",
        modifier = modifier.fillMaxSize()
    ) { targetTab ->
        when (targetTab) {
            PulseTab.HOME -> DashboardScreen(
                viewModel = dashboardViewModel,
                onNavigateToTransactions = {
                    transactionsViewModel.setCategoryFilter(null)
                    navState.navigate(NavigationItem.TRANSACTIONS)
                },
                onNavigateToSettings = { navState.pushScreen(PushedScreen.SETTINGS) },
                onOpenAddTransaction = { onOpenAddTransaction(TransactionType.EXPENSE, null) },
                onEditTransaction = onEditTransaction,
                onNavigateToAnalytics = { navState.navigate(NavigationItem.ANALYTICS) },
                onNavigateToAccounts = { navState.navigate(NavigationItem.WALLETS) },
                onNavigateToReceivables = { navState.navigate(NavigationItem.RECEIVABLES) },
                onNavigateToBills = { navState.navigate(NavigationItem.BILLS) },
                onNavigateToBudgets = { navState.navigate(NavigationItem.BUDGETS) },
                onSplitBillClick = onSplitBillClick,
                onOpenAddExpense = { onOpenAddTransaction(TransactionType.EXPENSE, null) },
                onOpenAddIncome = { onOpenAddTransaction(TransactionType.INCOME, null) },
                onTransferClick = onTransferClick,
                onOpenMarketList = onOpenMarketList
            )
            PulseTab.MONEY -> {
                MoneyScreen(
                    onEditTransaction = onEditTransaction,
                    selectedSegment = navState.moneySegment,
                    onSegmentSelected = { navState.selectMoneySegment(it) },
                    onAddTransactionForDate = { dateMillis -> onOpenAddTransaction(TransactionType.EXPENSE, dateMillis) },
                    transactionsViewModel = transactionsViewModel
                )
            }
            PulseTab.BAZAAR -> {
                if (activeShoppingTripId != null) {
                    MarketScreen(dashboardViewModel = dashboardViewModel)
                } else {
                    BazaarHubScreen(
                        bazaarViewModel = bazaarViewModel,
                        onOpenPlanMode = { listId -> navState.pushScreen(PushedScreen.BAZAAR_PLAN(listId)) },
                        onResumeShopping = { listId -> activeShoppingTripId = listId }
                    )
                }
            }
            PulseTab.PLAN -> {
                PlanScreen(
                    selectedSegment = navState.planSegment,
                    onSegmentSelected = { navState.selectPlanSegment(it) }
                )
            }
        }
    }
}

/**
 * Backward-compatible NavigationScreenHost taking [NavigationItem].
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
                    modifier = Modifier,
                    selectedSegment = PlanSegment.BUDGETS,
                    onSegmentSelected = {}
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
