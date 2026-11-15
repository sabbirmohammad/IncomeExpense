package com.example.expensetracker.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expensetracker.data.model.AppLanguage
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.data.repository.ExpenseRepository
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.LocalFeedbackController
import com.example.expensetracker.ui.components.TransactionFeedbackController
import com.example.expensetracker.ui.components.rememberAppStrings
import com.example.expensetracker.ui.dashboard.DashboardViewModel
import com.example.expensetracker.ui.onboarding.OnboardingScreen

/**
 * Main App Navigation (Pulse Design Language Architecture):
 * - 4 Top-level destinations: Home, Money, Bazaar, Plan
 * - Adaptive Navigation Shell: PulseDock (Compact), PulseNavigationRail (Medium / landscape), PulseSidePanel (Expanded)
 * - Center raised Mango Add button (1-tap expense creation)
 * - State holder with persistence across rotation, resize, and process death.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppNavigation(
    triggerBudgetPromptFromNotification: Boolean = false,
    onBudgetPromptHandled: () -> Unit = {}
) {
    val navState = rememberAdaptiveNavigationState()
    val dashboardViewModel: DashboardViewModel = viewModel()

    val categories by dashboardViewModel.categories.collectAsStateWithLifecycle()
    val userProfile by dashboardViewModel.userProfile.collectAsStateWithLifecycle()
    val summary by dashboardViewModel.summary.collectAsStateWithLifecycle()
    val accounts by dashboardViewModel.accounts.collectAsStateWithLifecycle()
    val activeMarketList by dashboardViewModel.activeMarketList.collectAsStateWithLifecycle()
    val activeMarketLists by dashboardViewModel.activeMarketLists.collectAsStateWithLifecycle()
    val recurringBills by dashboardViewModel.recurringBills.collectAsStateWithLifecycle()
    val receivables by dashboardViewModel.receivables.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val repository = remember { ExpenseRepository.getInstance(context) }
    var showUpcomingBudgetPrompt by rememberUpcomingBudgetPromptState(repository, triggerBudgetPromptFromNotification, onBudgetPromptHandled)

    var showGlobalAddSheet by remember { mutableStateOf(false) }
    var showTransferSheet by remember { mutableStateOf(false) }
    var showBillSplitterSheet by remember { mutableStateOf(false) }
    var showMarketListSheet by remember { mutableStateOf(false) }
    var transactionToEdit by remember { mutableStateOf<Transaction?>(null) }
    var initialDateForAdd by remember { mutableStateOf<Long?>(null) }
    var initialTypeForAdd by remember { mutableStateOf(TransactionType.EXPENSE) }
    var initialOpenMarketForAdd by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    // Status badges dynamically derived from live repository StateFlows (never saved in Saver)
    val hasOverdueBills = remember(recurringBills) {
        recurringBills.any { it.isActive && it.getPaymentStatus() == com.example.expensetracker.data.model.BillPaymentStatus.OVERDUE }
    }
    val hasOverdueDebts = remember(receivables) {
        val now = System.currentTimeMillis()
        receivables.any { !it.isSettled && it.dueDateMillis != null && it.dueDateMillis <= now }
    }
    val hasBudgetOverspent = summary.budgetOverspent > 0

    navState.hasPlanAttention = hasOverdueBills || hasOverdueDebts || hasBudgetOverspent
    navState.isBazaarTripActive = activeMarketLists.isNotEmpty() || (activeMarketList?.items?.any { !it.isPurchased } == true)

    // Notification trigger handling
    LaunchedEffect(triggerBudgetPromptFromNotification) {
        if (triggerBudgetPromptFromNotification) {
            navState.handleNotificationExtra("destination", "budget")
        }
    }

    // Back Navigation: Pushed screen -> pop; non-Home destination -> HOME; on HOME -> exit
    BackHandler(enabled = navState.pushedScreen != null || navState.selectedTab != PulseTab.HOME) {
        navState.handleBackPress()
    }

    val isOnboardingCompleted by repository.isOnboardingCompleted.collectAsStateWithLifecycle()
    val strings = rememberAppStrings(AppLanguage.fromCode(userProfile.appLanguage))
    val feedbackController = remember { TransactionFeedbackController() }

    CompositionLocalProvider(
        LocalAppStrings provides strings,
        LocalFeedbackController provides feedbackController
    ) {
        val bazaarViewModel: com.example.expensetracker.ui.bazaar.BazaarViewModel = viewModel()

        if (!isOnboardingCompleted) {
            OnboardingScreen(onFinish = { repository.setOnboardingCompleted(true) })
        } else {
            AdaptiveScaffold(
                state = navState,
                onOpenAdd = {
                    if (navState.selectedTab == PulseTab.BAZAAR) {
                        bazaarViewModel.startNewList { newListId ->
                            navState.pushScreen(PushedScreen.BAZAAR_PLAN(newListId))
                        }
                    } else {
                        transactionToEdit = null
                        initialDateForAdd = null
                        initialTypeForAdd = TransactionType.EXPENSE
                        initialOpenMarketForAdd = false
                        showGlobalAddSheet = true
                    }
                },
                onOpenSettings = {
                    navState.pushScreen(PushedScreen.SETTINGS)
                },
                userName = userProfile.name
            ) { _ ->
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    NavigationScreenHost(
                        navState = navState,
                        onOpenAddTransaction = { type, dateMillis ->
                            transactionToEdit = null
                            initialDateForAdd = dateMillis
                            initialTypeForAdd = type
                            initialOpenMarketForAdd = false
                            showGlobalAddSheet = true
                        },
                        onEditTransaction = { tx ->
                            transactionToEdit = tx
                            initialDateForAdd = null
                            initialTypeForAdd = tx.type
                            initialOpenMarketForAdd = false
                            showGlobalAddSheet = true
                        },
                        onSplitBillClick = { showBillSplitterSheet = true },
                        onTransferClick = { showTransferSheet = true },
                        onOpenMarketList = {
                            transactionToEdit = null
                            initialDateForAdd = null
                            initialTypeForAdd = TransactionType.EXPENSE
                            initialOpenMarketForAdd = true
                            showGlobalAddSheet = true
                        },
                        dashboardViewModel = dashboardViewModel
                    )
                }
            }

            AppNavigationSheets(
                showGlobalAddSheet = showGlobalAddSheet,
                showBillSplitterSheet = showBillSplitterSheet,
                showTransferSheet = showTransferSheet,
                transactionToEdit = transactionToEdit,
                initialDateForAdd = initialDateForAdd,
                initialTypeForAdd = initialTypeForAdd,
                initialOpenMarketForAdd = initialOpenMarketForAdd,
                categories = categories,
                accounts = accounts,
                userProfile = userProfile,
                sheetState = sheetState,
                scope = scope,
                strings = strings,
                feedbackController = feedbackController,
                dashboardViewModel = dashboardViewModel,
                showMarketListSheet = showMarketListSheet,
                activeMarketList = activeMarketList,
                showUpcomingBudgetPrompt = showUpcomingBudgetPrompt,
                summary = summary,
                repository = repository,
                onCloseAddSheet = {
                    showGlobalAddSheet = false
                    transactionToEdit = null
                    initialDateForAdd = null
                    initialTypeForAdd = TransactionType.EXPENSE
                    initialOpenMarketForAdd = false
                },
                onCloseBillSplitterSheet = { showBillSplitterSheet = false },
                onCloseTransferSheet = { showTransferSheet = false },
                onOpenMarketListForAdd = { initialOpenMarketForAdd = true; showGlobalAddSheet = true },
                onCloseMarketList = { showMarketListSheet = false },
                onCloseBudgetPrompt = { showUpcomingBudgetPrompt = false },
                onNavigateToBudgets = { navState.navigate(NavigationItem.BUDGETS) }
            )
        }
    }
}
