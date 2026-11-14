package com.example.expensetracker.ui.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.data.model.FinancialSummary
import com.example.expensetracker.data.model.MarketListWithItems
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.data.model.UserProfile
import com.example.expensetracker.data.repository.ExpenseRepository
import com.example.expensetracker.ui.components.MiddleScreenConfirmationOverlay
import com.example.expensetracker.ui.components.Strings
import com.example.expensetracker.ui.components.TransactionFeedbackController
import com.example.expensetracker.ui.dashboard.DashboardViewModel
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigationSheets(
    showGlobalAddSheet: Boolean,
    showBillSplitterSheet: Boolean,
    showTransferSheet: Boolean,
    transactionToEdit: Transaction?,
    initialDateForAdd: Long?,
    initialTypeForAdd: TransactionType,
    initialOpenMarketForAdd: Boolean,
    categories: List<Category>,
    accounts: List<Account>,
    userProfile: UserProfile,
    sheetState: SheetState,
    scope: CoroutineScope,
    strings: Strings,
    feedbackController: TransactionFeedbackController,
    dashboardViewModel: DashboardViewModel,
    showMarketListSheet: Boolean,
    activeMarketList: MarketListWithItems?,
    showUpcomingBudgetPrompt: Boolean,
    summary: FinancialSummary,
    repository: ExpenseRepository,
    onCloseAddSheet: () -> Unit,
    onCloseBillSplitterSheet: () -> Unit,
    onCloseTransferSheet: () -> Unit,
    onOpenMarketListForAdd: () -> Unit,
    onCloseMarketList: () -> Unit,
    onCloseBudgetPrompt: () -> Unit,
    onNavigateToBudgets: () -> Unit
) {
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
        onCloseAddSheet = onCloseAddSheet,
        onCloseBillSplitterSheet = onCloseBillSplitterSheet,
        onCloseTransferSheet = onCloseTransferSheet,
        onOpenMarketList = onOpenMarketListForAdd
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
        onCloseMarketList = onCloseMarketList,
        onCloseBudgetPrompt = onCloseBudgetPrompt,
        onNavigateToBudgets = onNavigateToBudgets
    )

    MiddleScreenConfirmationOverlay(controller = feedbackController)
}
