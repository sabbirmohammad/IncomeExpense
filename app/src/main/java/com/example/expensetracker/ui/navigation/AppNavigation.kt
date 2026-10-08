package com.example.expensetracker.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expensetracker.data.model.AppLanguage
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.data.repository.*
import com.example.expensetracker.ui.accounts.AccountsViewModel
import com.example.expensetracker.ui.analytics.AnalyticsViewModel
import com.example.expensetracker.ui.bills.BillsViewModel
import com.example.expensetracker.ui.budget.BudgetViewModel
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.LocalFeedbackController
import com.example.expensetracker.ui.components.MiddleScreenConfirmationOverlay
import com.example.expensetracker.ui.components.TransactionFeedbackController
import com.example.expensetracker.ui.components.rememberAppStrings
import com.example.expensetracker.ui.dashboard.DashboardViewModel
import com.example.expensetracker.ui.receivables.ReceivablesViewModel
import com.example.expensetracker.ui.reports.ReportsViewModel
import com.example.expensetracker.ui.settings.SettingsViewModel
import com.example.expensetracker.ui.transactions.TransactionsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppNavigation(
    triggerBudgetPromptFromNotification: Boolean = false,
    onBudgetPromptHandled: () -> Unit = {}
) {
    var selectedItem by remember { mutableStateOf(NavigationItem.HOME) }
    var isDrawerOpen by remember { mutableStateOf(false) }

    val dashboardViewModel: DashboardViewModel = viewModel()
    val transactionsViewModel: TransactionsViewModel = viewModel()
    val analyticsViewModel: AnalyticsViewModel = viewModel()
    val budgetViewModel: BudgetViewModel = viewModel()
    val settingsViewModel: SettingsViewModel = viewModel()
    val accountsViewModel: AccountsViewModel = viewModel()
    val receivablesViewModel: ReceivablesViewModel = viewModel()
    val billsViewModel: BillsViewModel = viewModel()
    val reportsViewModel: ReportsViewModel = viewModel()

    val categories by dashboardViewModel.categories.collectAsState()
    val userProfile by dashboardViewModel.userProfile.collectAsState()
    val summary by dashboardViewModel.summary.collectAsState()
    val accounts by dashboardViewModel.accounts.collectAsState()
    val activeMarketList by dashboardViewModel.activeMarketList.collectAsState()
    val budgetStatuses by budgetViewModel.budgetStatuses.collectAsState()

    val context = LocalContext.current
    val repository = remember { ExpenseRepository.getInstance(context) }
    var showUpcomingBudgetPrompt by remember { mutableStateOf(false) }

    LaunchedEffect(triggerBudgetPromptFromNotification) {
        if (triggerBudgetPromptFromNotification) {
            showUpcomingBudgetPrompt = true
            onBudgetPromptHandled()
        } else {
            val shouldShow = repository.checkShouldShowUpcomingBudgetPrompt()
            if (shouldShow) {
                showUpcomingBudgetPrompt = true
            }
        }
    }

    var showGlobalAddSheet by remember { mutableStateOf(false) }
    var showTransferSheet by remember { mutableStateOf(false) }
    var showBillSplitterSheet by remember { mutableStateOf(false) }
    var showMarketListSheet by remember { mutableStateOf(false) }
    var transactionToEdit by remember { mutableStateOf<Transaction?>(null) }
    var initialDateForAdd by remember { mutableStateOf<Long?>(null) }
    var initialTypeForAdd by remember { mutableStateOf(TransactionType.EXPENSE) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    BackHandler(enabled = isDrawerOpen) {
        isDrawerOpen = false
    }

    BackHandler(enabled = !isDrawerOpen && (selectedItem == NavigationItem.SETTINGS || selectedItem == NavigationItem.ACCOUNTS || selectedItem == NavigationItem.RECEIVABLES || selectedItem == NavigationItem.BILLS || selectedItem == NavigationItem.BUDGETS || selectedItem == NavigationItem.ANALYTICS || selectedItem == NavigationItem.REPORTS)) {
        selectedItem = NavigationItem.HOME
    }

    val strings = rememberAppStrings(AppLanguage.fromCode(userProfile.appLanguage))
    val feedbackController = remember { TransactionFeedbackController() }

    CompositionLocalProvider(
        LocalAppStrings provides strings,
        LocalFeedbackController provides feedbackController
    ) {
        Navigation3DScaffold(
            isDrawerOpen = isDrawerOpen,
            onCloseDrawer = { isDrawerOpen = false },
            onOpenDrawer = { isDrawerOpen = true },
            drawerContent = {
                SideMenuDrawer(
                    selectedItem = selectedItem,
                    onItemSelected = { item ->
                        selectedItem = item
                        isDrawerOpen = false
                    },
                    userProfile = userProfile,
                    totalBalance = accounts.sumOf { it.currentBalance }
                )
            },
            mainContent = { drawerProgress ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .widthIn(max = 840.dp)
                        .align(Alignment.TopCenter)
                ) {
                    NavigationScreenHost(
                        selectedItem = selectedItem,
                        onNavigate = { selectedItem = it },
                        onOpenMenu = { isDrawerOpen = true },
                        onOpenAddTransaction = { type, dateMillis ->
                            transactionToEdit = null
                            initialDateForAdd = dateMillis
                            initialTypeForAdd = type
                            showGlobalAddSheet = true
                        },
                        onEditTransaction = { tx ->
                            transactionToEdit = tx
                            initialDateForAdd = null
                            initialTypeForAdd = tx.type
                            showGlobalAddSheet = true
                        },
                        onSplitBillClick = { showBillSplitterSheet = true },
                        onTransferClick = { showTransferSheet = true },
                        onOpenMarketList = { showMarketListSheet = true },
                        dashboardViewModel = dashboardViewModel,
                        transactionsViewModel = transactionsViewModel,
                        analyticsViewModel = analyticsViewModel,
                        budgetViewModel = budgetViewModel,
                        settingsViewModel = settingsViewModel,
                        accountsViewModel = accountsViewModel,
                        receivablesViewModel = receivablesViewModel,
                        billsViewModel = billsViewModel,
                        reportsViewModel = reportsViewModel
                    )

                    ModernBottomNavBar(
                        selectedItem = selectedItem,
                        onItemSelected = { item -> selectedItem = item },
                        onAddClick = {
                            transactionToEdit = null
                            initialDateForAdd = null
                            initialTypeForAdd = TransactionType.EXPENSE
                            showGlobalAddSheet = true
                        },
                        onMoreClick = { isDrawerOpen = !isDrawerOpen },
                        strings = strings,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .graphicsLayer {
                                val barAlpha = (1f - drawerProgress * 2.5f).coerceIn(0f, 1f)
                                alpha = barAlpha
                                translationY = 30.dp.toPx() * drawerProgress
                            }
                    )
                }
            }
        )

        NavigationTransactionSheets(
            showGlobalAddSheet = showGlobalAddSheet,
            showBillSplitterSheet = showBillSplitterSheet,
            showTransferSheet = showTransferSheet,
            transactionToEdit = transactionToEdit,
            initialDateForAdd = initialDateForAdd,
            initialTypeForAdd = initialTypeForAdd,
            categories = categories,
            accounts = accounts,
            userProfile = userProfile,
            sheetState = sheetState,
            scope = scope,
            strings = strings,
            feedbackController = feedbackController,
            dashboardViewModel = dashboardViewModel,
            onCloseAddSheet = {
                showGlobalAddSheet = false
                transactionToEdit = null
                initialDateForAdd = null
                initialTypeForAdd = TransactionType.EXPENSE
            },
            onCloseBillSplitterSheet = { showBillSplitterSheet = false },
            onCloseTransferSheet = { showTransferSheet = false }
        )

        NavigationUtilitySheets(
            showMarketListSheet = showMarketListSheet,
            activeMarketList = activeMarketList,
            showUpcomingBudgetPrompt = showUpcomingBudgetPrompt,
            summary = summary,
            budgetStatuses = budgetStatuses,
            userProfile = userProfile,
            strings = strings,
            feedbackController = feedbackController,
            dashboardViewModel = dashboardViewModel,
            repository = repository,
            scope = scope,
            onCloseMarketList = { showMarketListSheet = false },
            onCloseBudgetPrompt = { showUpcomingBudgetPrompt = false },
            onNavigateToBudgets = { selectedItem = NavigationItem.BUDGETS }
        )

        MiddleScreenConfirmationOverlay(controller = feedbackController)
    }
}
