package com.example.expensetracker.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expensetracker.data.model.AppLanguage
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.data.repository.ExpenseRepository
import com.example.expensetracker.data.repository.checkShouldShowUpcomingBudgetPrompt
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.LocalFeedbackController
import com.example.expensetracker.ui.components.MiddleScreenConfirmationOverlay
import com.example.expensetracker.ui.components.TransactionFeedbackController
import com.example.expensetracker.ui.components.rememberAppStrings
import com.example.expensetracker.ui.dashboard.DashboardViewModel
import com.example.expensetracker.ui.onboarding.OnboardingScreen

import com.example.expensetracker.ui.components.AppAddFab
import com.example.expensetracker.ui.components.AppScaffold
import androidx.compose.foundation.layout.padding

/**
 * Main App Navigation (Living Ledger 2.0 Architecture):
 * - 5 Top-level destinations: Home, Ledger, Bazaar, Wallets, Plan.
 * - Flat 64dp bottom bar + contextual AppAddFab.
 * - System back redirects to Home before exiting.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppNavigation(
    triggerBudgetPromptFromNotification: Boolean = false,
    onBudgetPromptHandled: () -> Unit = {}
) {
    var selectedItem by remember { mutableStateOf(NavigationItem.HOME) }

    val dashboardViewModel: DashboardViewModel = viewModel()

    val categories by dashboardViewModel.categories.collectAsStateWithLifecycle()
    val userProfile by dashboardViewModel.userProfile.collectAsStateWithLifecycle()
    val summary by dashboardViewModel.summary.collectAsStateWithLifecycle()
    val accounts by dashboardViewModel.accounts.collectAsStateWithLifecycle()
    val activeMarketList by dashboardViewModel.activeMarketList.collectAsStateWithLifecycle()

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
    var initialOpenMarketForAdd by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    val isTopLevelScreen = selectedItem == NavigationItem.HOME ||
            selectedItem == NavigationItem.LEDGER ||
            selectedItem == NavigationItem.BAZAAR ||
            selectedItem == NavigationItem.WALLETS ||
            selectedItem == NavigationItem.PLAN ||
            selectedItem == NavigationItem.TRANSACTIONS ||
            selectedItem == NavigationItem.MARKET ||
            selectedItem == NavigationItem.MONEY ||
            selectedItem == NavigationItem.ACCOUNTS

    // Back Navigation: Non-Home destination -> HOME; on HOME -> exit
    BackHandler(enabled = selectedItem != NavigationItem.HOME) {
        selectedItem = NavigationItem.HOME
    }

    val isOnboardingCompleted by repository.isOnboardingCompleted.collectAsStateWithLifecycle()

    val strings = rememberAppStrings(AppLanguage.fromCode(userProfile.appLanguage))
    val feedbackController = remember { TransactionFeedbackController() }
    val colors = LocalAppColors.current

    CompositionLocalProvider(
        LocalAppStrings provides strings,
        LocalFeedbackController provides feedbackController
    ) {
        if (!isOnboardingCompleted) {
            OnboardingScreen(
                onFinish = { repository.setOnboardingCompleted(true) }
            )
        } else {
            AppScaffold(
                bottomBar = {
                    AnimatedVisibility(
                        visible = isTopLevelScreen,
                        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                    ) {
                        ModernBottomNavBar(
                            selectedItem = selectedItem,
                            onItemSelected = { item -> selectedItem = item },
                            strings = strings
                        )
                    }
                },
                fab = {
                    AnimatedVisibility(
                        visible = isTopLevelScreen,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        val fabLabel = when (selectedItem) {
                            NavigationItem.BAZAAR, NavigationItem.MARKET -> if (strings.isBangla) "＋ নতুন তালিকা" else "＋ New list"
                            else -> if (strings.isBangla) "＋ যোগ করুন" else "＋ Add"
                        }
                        AppAddFab(
                            label = fabLabel,
                            onClick = {
                                when (selectedItem) {
                                    NavigationItem.BAZAAR, NavigationItem.MARKET -> {
                                        transactionToEdit = null
                                        initialDateForAdd = null
                                        initialTypeForAdd = TransactionType.EXPENSE
                                        initialOpenMarketForAdd = true
                                        showGlobalAddSheet = true
                                    }
                                    else -> {
                                        transactionToEdit = null
                                        initialDateForAdd = null
                                        initialTypeForAdd = TransactionType.EXPENSE
                                        initialOpenMarketForAdd = false
                                        showGlobalAddSheet = true
                                    }
                                }
                            }
                        )
                    }
                }
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .widthIn(max = 840.dp)
                ) {
                    NavigationScreenHost(
                        selectedItem = selectedItem,
                        onNavigate = { selectedItem = it },
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

            NavigationTransactionSheets(
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
                onCloseAddSheet = {
                    showGlobalAddSheet = false
                    transactionToEdit = null
                    initialDateForAdd = null
                    initialTypeForAdd = TransactionType.EXPENSE
                    initialOpenMarketForAdd = false
                },
                onCloseBillSplitterSheet = { showBillSplitterSheet = false },
                onCloseTransferSheet = { showTransferSheet = false },
                onOpenMarketList = {
                    initialOpenMarketForAdd = true
                    showGlobalAddSheet = true
                }
            )

            NavigationUtilitySheets(
                showMarketListSheet = showMarketListSheet,
                activeMarketList = activeMarketList,
                showUpcomingBudgetPrompt = showUpcomingBudgetPrompt,
                summary = summary,
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
}
