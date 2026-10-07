package com.example.expensetracker.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.AssignmentReturn
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.outlined.AssignmentReturn
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorAccentPressed
import com.example.expensetracker.theme.ColorBackground
import com.example.expensetracker.theme.ColorBorder
import com.example.expensetracker.theme.ColorNavInactive
import com.example.expensetracker.theme.ColorSurface
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.ColorTextMuted
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.theme.ColorTextSecondary
import com.example.expensetracker.ui.accounts.AccountsScreen
import com.example.expensetracker.ui.accounts.AccountsViewModel
import com.example.expensetracker.ui.accounts.TransferFundsSheet
import com.example.expensetracker.ui.analytics.AnalyticsScreen
import com.example.expensetracker.ui.analytics.AnalyticsViewModel
import com.example.expensetracker.ui.budget.BudgetScreen
import com.example.expensetracker.ui.budget.BudgetViewModel
import com.example.expensetracker.ui.components.HairlineDivider
import com.example.expensetracker.ui.dashboard.DashboardScreen
import com.example.expensetracker.ui.dashboard.DashboardViewModel
import com.example.expensetracker.ui.dashboard.MarketListSheet
import com.example.expensetracker.ui.receivables.ReceivablesScreen
import com.example.expensetracker.ui.receivables.ReceivablesViewModel
import com.example.expensetracker.ui.settings.SettingsScreen
import com.example.expensetracker.ui.settings.SettingsViewModel
import com.example.expensetracker.ui.transactions.AddEditTransactionSheet
import com.example.expensetracker.ui.transactions.TransactionsScreen
import com.example.expensetracker.ui.transactions.TransactionsViewModel
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.runtime.CompositionLocalProvider
import com.example.expensetracker.data.model.AppLanguage
import com.example.expensetracker.ui.bills.BillsScreen
import com.example.expensetracker.ui.bills.BillsViewModel
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.rememberAppHaptics
import com.example.expensetracker.ui.components.rememberAppStrings
import com.example.expensetracker.ui.receivables.BillSplitterSheet
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.ui.platform.LocalConfiguration
import kotlinx.coroutines.launch
import com.example.expensetracker.ui.components.FeedbackVisualType
import com.example.expensetracker.ui.components.FeedbackStrings
import com.example.expensetracker.ui.components.LocalFeedbackController
import com.example.expensetracker.ui.components.MiddleScreenConfirmationOverlay
import com.example.expensetracker.ui.components.TransactionFeedbackController
import com.example.expensetracker.ui.components.TransactionFeedbackData
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.UpcomingBudgetPromptDialog
import com.example.expensetracker.data.repository.ExpenseRepository
import java.util.Calendar

import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.outlined.Assessment
import com.example.expensetracker.ui.reports.ReportsScreen
import com.example.expensetracker.ui.reports.ReportsViewModel

enum class NavigationItem(
    val title: String,
    val activeIcon: ImageVector,
    val inactiveIcon: ImageVector
) {
    HOME("Home", Icons.Filled.Home, Icons.Outlined.Home),
    TRANSACTIONS("Transactions", Icons.AutoMirrored.Filled.ReceiptLong, Icons.AutoMirrored.Outlined.ReceiptLong),
    ANALYTICS("Analytics", Icons.Filled.PieChart, Icons.Outlined.PieChart),
    ACCOUNTS("Accounts & MFS", Icons.Filled.AccountBalance, Icons.Outlined.AccountBalance),
    BILLS("Bills & Subscriptions", Icons.Filled.Payments, Icons.Outlined.Payments),
    RECEIVABLES("Money Owed", Icons.AutoMirrored.Filled.AssignmentReturn, Icons.AutoMirrored.Outlined.AssignmentReturn),
    BUDGETS("Budgets", Icons.Filled.AccountBalanceWallet, Icons.Outlined.AccountBalanceWallet),
    REPORTS("Reports", Icons.Filled.Assessment, Icons.Outlined.Assessment),
    SETTINGS("Settings", Icons.Filled.Settings, Icons.Outlined.Settings);

    val icon: ImageVector get() = inactiveIcon
}

fun NavigationItem.getLocalizedTitle(strings: com.example.expensetracker.ui.components.Strings): String = when (this) {
    NavigationItem.HOME -> strings.navHome
    NavigationItem.TRANSACTIONS -> strings.navTransactions
    NavigationItem.ANALYTICS -> strings.navAnalytics
    NavigationItem.ACCOUNTS -> strings.navAccounts
    NavigationItem.BILLS -> strings.navBills
    NavigationItem.RECEIVABLES -> strings.navReceivables
    NavigationItem.BUDGETS -> strings.navBudgets
    NavigationItem.REPORTS -> strings.navReports
    NavigationItem.SETTINGS -> strings.navSettings
}

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

    val context = androidx.compose.ui.platform.LocalContext.current
    val repository = remember { ExpenseRepository.getInstance(context) }
    var showUpcomingBudgetPrompt by remember { mutableStateOf(false) }

    androidx.compose.runtime.LaunchedEffect(triggerBudgetPromptFromNotification) {
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
    val configuration = LocalConfiguration.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    // Back press handling: If drawer open, close drawer first
    BackHandler(enabled = isDrawerOpen) {
        isDrawerOpen = false
    }

    // When on secondary screens and drawer is closed, pressing device back returns to Home
    BackHandler(enabled = !isDrawerOpen && (selectedItem == NavigationItem.SETTINGS || selectedItem == NavigationItem.ACCOUNTS || selectedItem == NavigationItem.RECEIVABLES || selectedItem == NavigationItem.BILLS || selectedItem == NavigationItem.BUDGETS || selectedItem == NavigationItem.ANALYTICS || selectedItem == NavigationItem.REPORTS)) {
        selectedItem = NavigationItem.HOME
    }

    // 3D Slide & Scale Animation Progress (0f = closed, 1f = open)
    val drawerProgress by animateFloatAsState(
        targetValue = if (isDrawerOpen) 1f else 0f,
        animationSpec = spring(
            dampingRatio = 0.82f,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "drawer3DProgress"
    )

    val screenWidth = configuration.screenWidthDp.dp
    val slideOffset = (screenWidth * 0.70f).coerceAtMost(320.dp)

    val translationX = (-slideOffset * drawerProgress).coerceAtMost(0.dp)
    val contentScale = (1f - (0.16f * drawerProgress)).coerceIn(0.5f, 1f)
    val cornerRadius = (26.dp * drawerProgress).coerceAtLeast(0.dp)
    val rotationY = (8f * drawerProgress).coerceAtLeast(0f)
    val elevation = (16.dp * drawerProgress).coerceAtLeast(0.dp)
    val strings = rememberAppStrings(AppLanguage.fromCode(userProfile.appLanguage))
    val feedbackController = remember { TransactionFeedbackController() }

    CompositionLocalProvider(
        LocalAppStrings provides strings,
        LocalFeedbackController provides feedbackController
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF090C14))
        ) {
        // Layer 1: Background Side Menu Drawer
        SideMenuDrawer(
            selectedItem = selectedItem,
            onItemSelected = { item ->
                selectedItem = item
                isDrawerOpen = false
            },
            userProfile = userProfile,
            totalBalance = accounts.sumOf { it.currentBalance }
        )

        // Layer 2: Foreground 3D Scaled Main Screen
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .offset(x = translationX)
                .graphicsLayer {
                    this.scaleX = contentScale
                    this.scaleY = contentScale
                    this.rotationY = rotationY
                    this.cameraDistance = 16 * this.density
                    this.shadowElevation = elevation.toPx()
                    this.clip = drawerProgress > 0.02f
                    this.shape = RoundedCornerShape(cornerRadius)
                },
            shape = RoundedCornerShape(cornerRadius),
            color = LocalAppColors.current.background,
            border = if (drawerProgress > 0.05f) BorderStroke(1.dp, LocalAppColors.current.border.copy(alpha = drawerProgress.coerceIn(0f, 1f))) else null
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
            ) {
                // Responsive Centered Content Container (Caps at 840dp on tablets/wide screens)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .widthIn(max = 840.dp)
                        .align(Alignment.TopCenter)
                ) {
                    // Active Screen Content with Smooth Animated Transitions
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
                        modifier = Modifier.fillMaxSize()
                    ) { targetScreen ->
                        when (targetScreen) {
                            NavigationItem.HOME -> {
                                DashboardScreen(
                                    viewModel = dashboardViewModel,
                                    onNavigateToTransactions = {
                                        transactionsViewModel.setCategoryFilter(null)
                                        selectedItem = NavigationItem.TRANSACTIONS
                                    },
                                    onNavigateToSettings = { selectedItem = NavigationItem.SETTINGS },
                                    onOpenAddTransaction = {
                                        transactionToEdit = null
                                        initialDateForAdd = null
                                        initialTypeForAdd = TransactionType.EXPENSE
                                        showGlobalAddSheet = true
                                    },
                                    onEditTransaction = { tx ->
                                        transactionToEdit = tx
                                        initialDateForAdd = null
                                        initialTypeForAdd = tx.type
                                        showGlobalAddSheet = true
                                    },
                                    onNavigateToAnalytics = {
                                        selectedItem = NavigationItem.ANALYTICS
                                    },
                                    onNavigateToAccounts = {
                                        selectedItem = NavigationItem.ACCOUNTS
                                    },
                                    onNavigateToReceivables = {
                                        selectedItem = NavigationItem.RECEIVABLES
                                    },
                                    onNavigateToBills = {
                                        selectedItem = NavigationItem.BILLS
                                    },
                                    onNavigateToBudgets = {
                                        selectedItem = NavigationItem.BUDGETS
                                    },
                                    onSplitBillClick = {
                                        showBillSplitterSheet = true
                                    },
                                    onOpenAddExpense = {
                                        transactionToEdit = null
                                        initialDateForAdd = null
                                        initialTypeForAdd = TransactionType.EXPENSE
                                        showGlobalAddSheet = true
                                    },
                                    onOpenAddIncome = {
                                        transactionToEdit = null
                                        initialDateForAdd = null
                                        initialTypeForAdd = TransactionType.INCOME
                                        showGlobalAddSheet = true
                                    },
                                    onTransferClick = {
                                        showTransferSheet = true
                                    },
                                    onOpenMarketList = {
                                        showMarketListSheet = true
                                    },
                                    onMenuClick = {
                                        isDrawerOpen = true
                                    }
                                )
                            }
                            NavigationItem.TRANSACTIONS -> {
                                TransactionsScreen(
                                    viewModel = transactionsViewModel,
                                    onEditTransaction = { tx ->
                                        transactionToEdit = tx
                                        initialDateForAdd = null
                                        showGlobalAddSheet = true
                                    },
                                    onAddTransactionForDate = { dateMillis ->
                                        transactionToEdit = null
                                        initialDateForAdd = dateMillis
                                        showGlobalAddSheet = true
                                    },
                                    onNavigateToSettings = { selectedItem = NavigationItem.SETTINGS },
                                    onMenuClick = { isDrawerOpen = true }
                                )
                            }
                            NavigationItem.ANALYTICS -> {
                                AnalyticsScreen(
                                    viewModel = analyticsViewModel,
                                    onNavigateToCategoryTransactions = { categoryId ->
                                        transactionsViewModel.setCategoryFilter(categoryId)
                                        selectedItem = NavigationItem.TRANSACTIONS
                                    },
                                    onNavigateToSettings = { selectedItem = NavigationItem.SETTINGS },
                                    onNavigateBack = { selectedItem = NavigationItem.HOME },
                                    onMenuClick = { isDrawerOpen = true }
                                )
                            }
                            NavigationItem.ACCOUNTS -> {
                                AccountsScreen(
                                    viewModel = accountsViewModel,
                                    onNavigateBack = { selectedItem = NavigationItem.HOME },
                                    onMenuClick = { isDrawerOpen = true }
                                )
                            }
                            NavigationItem.BILLS -> {
                                BillsScreen(
                                    viewModel = billsViewModel,
                                    onNavigateBack = { selectedItem = NavigationItem.HOME },
                                    onMenuClick = { isDrawerOpen = true }
                                )
                            }
                            NavigationItem.RECEIVABLES -> {
                                ReceivablesScreen(
                                    viewModel = receivablesViewModel,
                                    onNavigateBack = { selectedItem = NavigationItem.HOME },
                                    onMenuClick = { isDrawerOpen = true }
                                )
                            }
                            NavigationItem.BUDGETS -> {
                                BudgetScreen(
                                    viewModel = budgetViewModel,
                                    onNavigateToSettings = { selectedItem = NavigationItem.SETTINGS },
                                    onNavigateBack = { selectedItem = NavigationItem.HOME },
                                    onMenuClick = { isDrawerOpen = true }
                                )
                            }
                            NavigationItem.REPORTS -> {
                                ReportsScreen(
                                    viewModel = reportsViewModel,
                                    onNavigateBack = { selectedItem = NavigationItem.HOME },
                                    onMenuClick = { isDrawerOpen = true }
                                )
                            }
                            NavigationItem.SETTINGS -> {
                                SettingsScreen(
                                    viewModel = settingsViewModel,
                                    onNavigateBack = { selectedItem = NavigationItem.HOME },
                                    onMenuClick = { isDrawerOpen = true }
                                )
                            }
                        }
                    }

                    // Modern Docked Bottom Navigation Bar with Central Add Button
                    ModernBottomNavBar(
                        selectedItem = selectedItem,
                        onItemSelected = { item ->
                            selectedItem = item
                        },
                        onAddClick = {
                            transactionToEdit = null
                            initialDateForAdd = null
                            initialTypeForAdd = TransactionType.EXPENSE
                            showGlobalAddSheet = true
                        },
                        onMoreClick = {
                            isDrawerOpen = !isDrawerOpen
                        },
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

                // Right edge drag detector to open 3D drawer
                if (!isDrawerOpen) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .fillMaxHeight()
                            .width(28.dp)
                            .pointerInput(Unit) {
                                detectHorizontalDragGestures { _, dragAmount ->
                                    if (dragAmount < -15) {
                                        isDrawerOpen = true
                                    }
                                }
                            }
                    )
                }

                // Scrim overlay when drawer is open: tapping or dragging right closes drawer
                if (drawerProgress > 0.01f) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(cornerRadius))
                            .background(Color.Black.copy(alpha = (0.35f * drawerProgress).coerceIn(0f, 1f)))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                isDrawerOpen = false
                            }
                            .pointerInput(Unit) {
                                detectHorizontalDragGestures { _, dragAmount ->
                                    if (dragAmount > 15) {
                                        isDrawerOpen = false
                                    }
                                }
                            }
                    )
                }
            }
        }
    }

    // Docked Add button opens this add-transaction flow from anywhere
    if (showGlobalAddSheet) {
        AddEditTransactionSheet(
            sheetState = sheetState,
            existingTransaction = transactionToEdit,
            initialDateMillis = initialDateForAdd,
            initialType = initialTypeForAdd,
            categories = categories,
            accounts = accounts,
            currencySymbol = userProfile.currencySymbol,
            geminiApiKey = userProfile.geminiApiKey,
            onDismiss = {
                scope.launch { sheetState.hide() }.invokeOnCompletion {
                    showGlobalAddSheet = false
                    transactionToEdit = null
                    initialDateForAdd = null
                    initialTypeForAdd = TransactionType.EXPENSE
                }
            },
            onSave = { tx ->
                val isEditing = transactionToEdit != null
                if (isEditing) {
                    dashboardViewModel.updateTransaction(tx)
                } else {
                    dashboardViewModel.addTransaction(tx)
                }

                val catName = categories.find { it.id == tx.categoryId }?.name ?: tx.categoryName
                val accName = accounts.find { it.id == tx.accountId }?.name ?: (tx.accountName ?: "")
                val subtitleInfo = if (accName.isNotBlank()) "$catName · $accName" else catName

                val feedbackType = if (tx.type == TransactionType.INCOME) {
                    FeedbackVisualType.INCOME
                } else {
                    FeedbackVisualType.EXPENSE
                }

                val feedbackTitle = when {
                    isEditing -> FeedbackStrings.transactionUpdated(strings.isBangla)
                    tx.type == TransactionType.INCOME -> FeedbackStrings.incomeAdded(strings.isBangla)
                    else -> FeedbackStrings.expenseRecorded(strings.isBangla)
                }

                showGlobalAddSheet = false
                transactionToEdit = null
                initialDateForAdd = null
                initialTypeForAdd = TransactionType.EXPENSE
                feedbackController.show(
                    TransactionFeedbackData(
                        type = feedbackType,
                        title = feedbackTitle,
                        amount = tx.amount,
                        currencySymbol = userProfile.currencySymbol,
                        subtitle = subtitleInfo
                    )
                )
            },
            onDelete = { id ->
                dashboardViewModel.deleteTransaction(id)
                showGlobalAddSheet = false
                transactionToEdit = null
                initialDateForAdd = null
                initialTypeForAdd = TransactionType.EXPENSE
                feedbackController.show(
                    TransactionFeedbackData(
                        type = FeedbackVisualType.TRANSACTION_DELETED,
                        title = FeedbackStrings.transactionDeleted(strings.isBangla),
                        subtitle = FeedbackStrings.itemDeleted(strings.isBangla)
                    )
                )
            }
        )
    }

    if (showBillSplitterSheet) {
        BillSplitterSheet(
            accounts = accounts,
            categories = categories,
            currencySymbol = userProfile.currencySymbol,
            onDismiss = { showBillSplitterSheet = false },
            onConfirmSplit = { splitData ->
                dashboardViewModel.executeBillSplit(splitData)
                showBillSplitterSheet = false
                val totalPeople = splitData.friends.size + 1
                feedbackController.show(
                    TransactionFeedbackData(
                        type = FeedbackVisualType.BILL_SPLIT,
                        title = FeedbackStrings.billSplit(strings.isBangla),
                        amount = splitData.totalAmount,
                        currencySymbol = userProfile.currencySymbol,
                        subtitle = if (strings.isBangla) "$totalPeople জনের মধ্যে স্প্লিট" else "$totalPeople people split"
                    )
                )
            }
        )
    }

    if (showTransferSheet) {
        TransferFundsSheet(
            accounts = accounts,
            currencySymbol = userProfile.currencySymbol,
            onDismiss = { showTransferSheet = false },
            onTransfer = { fromId, toId, amount, note ->
                dashboardViewModel.transferFunds(fromId, toId, amount, note)
                showTransferSheet = false
                val fromAcc = accounts.find { it.id == fromId }?.name ?: ""
                val toAcc = accounts.find { it.id == toId }?.name ?: ""
                val transferSub = if (fromAcc.isNotBlank() && toAcc.isNotBlank()) "$fromAcc → $toAcc" else note
                feedbackController.show(
                    TransactionFeedbackData(
                        type = FeedbackVisualType.TRANSFER,
                        title = FeedbackStrings.transferSuccess(strings.isBangla),
                        amount = amount,
                        currencySymbol = userProfile.currencySymbol,
                        subtitle = transferSub.ifBlank { null }
                    )
                )
            }
        )
    }

    if (showMarketListSheet) {
        val marketSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        MarketListSheet(
            sheetState = marketSheetState,
            marketListWithItems = activeMarketList,
            currencySymbol = userProfile.currencySymbol,
            isBangla = strings.isBangla,
            geminiApiKey = userProfile.geminiApiKey,
            onDismiss = {
                scope.launch { marketSheetState.hide() }.invokeOnCompletion {
                    showMarketListSheet = false
                }
            },
            onCreateListWithBulk = { title, items ->
                dashboardViewModel.createMarketListWithBulk(title, items)
            },
            onAddItem = { item ->
                dashboardViewModel.addMarketItem(item)
            },
            onUpdateItem = { item ->
                dashboardViewModel.updateMarketItem(item)
            },
            onDeleteItem = { itemId ->
                dashboardViewModel.deleteMarketItem(itemId)
            },
            onMarkPurchased = { item, amount ->
                dashboardViewModel.markMarketItemPurchased(item, amount)
                feedbackController.show(
                    TransactionFeedbackData(
                        type = FeedbackVisualType.EXPENSE,
                        title = FeedbackStrings.expenseRecorded(strings.isBangla),
                        amount = amount,
                        currencySymbol = userProfile.currencySymbol,
                        subtitle = "${item.name} (${item.formatQuantity()}) · ${if (strings.isBangla) "বাজার ও সদাই" else "Groceries"}"
                    )
                )
            },
            onUnmarkPurchased = { item ->
                dashboardViewModel.unmarkMarketItemPurchased(item)
            },
            onCompleteList = { listId ->
                dashboardViewModel.completeActiveMarketList(listId) { result ->
                    if (result.success && result.totalSpent > 0) {
                        feedbackController.show(
                            TransactionFeedbackData(
                                type = FeedbackVisualType.EXPENSE,
                                title = if (strings.isBangla) "বাজার সম্পন্ন হয়েছে!" else "Shopping Completed!",
                                amount = result.totalSpent,
                                currencySymbol = userProfile.currencySymbol,
                                subtitle = if (strings.isBangla) "${convertToBengaliDigits(result.purchasedCount.toString())}টি পণ্য সম্পন্ন · মোট বাজার খরচ"
                                          else "${result.purchasedCount} items purchased · Total spent"
                            )
                        )
                    }
                }
                showMarketListSheet = false
            }
        )
    }

    if (showUpcomingBudgetPrompt) {
        val cal = Calendar.getInstance()
        val currentDay = cal.get(Calendar.DAY_OF_MONTH)
        val maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val (fromMonth, fromYear, toMonth, toYear) = if (currentDay == maxDay) {
            val calNext = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, 1) }
            listOf(cal.get(Calendar.MONTH), cal.get(Calendar.YEAR), calNext.get(Calendar.MONTH), calNext.get(Calendar.YEAR))
        } else {
            val calPrev = Calendar.getInstance().apply { add(Calendar.MONTH, -1) }
            listOf(calPrev.get(Calendar.MONTH), calPrev.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.YEAR))
        }

        val targetKey = "$toYear-$toMonth"
        val currentBudgetAmount = summary.budgetAmount
        val categoryBudgetCount = budgetStatuses.count { it.budget.categoryId != null }

        UpcomingBudgetPromptDialog(
            currentBudgetAmount = currentBudgetAmount,
            categoryBudgetCount = categoryBudgetCount,
            currencySymbol = userProfile.currencySymbol,
            isBangla = strings.isBangla,
            onKeepPreviousBudget = {
                scope.launch {
                    val success = repository.copyBudgets(fromMonth, fromYear, toMonth, toYear)
                    repository.markBudgetPromptHandled(targetKey)
                    showUpcomingBudgetPrompt = false
                    if (success) {
                        feedbackController.show(
                            TransactionFeedbackData(
                                type = FeedbackVisualType.GENERIC_SUCCESS,
                                title = strings.budgetCopiedSuccess,
                                amount = currentBudgetAmount,
                                currencySymbol = userProfile.currencySymbol
                            )
                        )
                    }
                }
            },
            onSetNewBudget = {
                repository.markBudgetPromptHandled(targetKey)
                showUpcomingBudgetPrompt = false
                selectedItem = NavigationItem.BUDGETS
            },
            onDismiss = {
                showUpcomingBudgetPrompt = false
            }
        )
    }

    // Centered Visual Confirmation Overlay (displays on top of all screens)
    MiddleScreenConfirmationOverlay(controller = feedbackController)
}
}

@Composable
fun ModernBottomNavBar(
    selectedItem: NavigationItem,
    onItemSelected: (NavigationItem) -> Unit,
    onAddClick: () -> Unit,
    onMoreClick: () -> Unit,
    strings: com.example.expensetracker.ui.components.Strings,
    modifier: Modifier = Modifier
) {
    val isMoreSelected = selectedItem == NavigationItem.ACCOUNTS ||
            selectedItem == NavigationItem.BILLS ||
            selectedItem == NavigationItem.RECEIVABLES ||
            selectedItem == NavigationItem.BUDGETS ||
            selectedItem == NavigationItem.REPORTS ||
            selectedItem == NavigationItem.SETTINGS

    val haptics = rememberAppHaptics()
    val addInteractionSource = remember { MutableInteractionSource() }
    val isAddPressed by addInteractionSource.collectIsPressedAsState()
    val addScale by animateFloatAsState(
        targetValue = if (isAddPressed) 0.88f else 1.0f,
        animationSpec = spring(dampingRatio = 0.75f, stiffness = Spring.StiffnessMediumLow),
        label = "navAddScale"
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = LocalAppColors.current.surface.copy(alpha = 0.98f),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        border = BorderStroke(1.dp, LocalAppColors.current.border.copy(alpha = 0.7f)),
        shadowElevation = 16.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(64.dp)
                .padding(horizontal = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Home
            BottomNavItemButton(
                selected = selectedItem == NavigationItem.HOME,
                label = strings.navHome,
                activeIcon = Icons.Filled.Home,
                inactiveIcon = Icons.Outlined.Home,
                onClick = { onItemSelected(NavigationItem.HOME) }
            )

            // Transactions
            BottomNavItemButton(
                selected = selectedItem == NavigationItem.TRANSACTIONS,
                label = strings.navTransactions,
                activeIcon = Icons.AutoMirrored.Filled.ReceiptLong,
                inactiveIcon = Icons.AutoMirrored.Outlined.ReceiptLong,
                onClick = { onItemSelected(NavigationItem.TRANSACTIONS) }
            )

            // Center Floating Add Button (placed in the center where Analytics was)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .graphicsLayer {
                            scaleX = addScale
                            scaleY = addScale
                        }
                        .shadow(
                            elevation = 6.dp,
                            shape = CircleShape,
                            ambientColor = ColorAccent.copy(alpha = 0.4f),
                            spotColor = ColorAccent
                        )
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(ColorAccent, Color(0xFF2563EB))
                            )
                        )
                        .clickable(
                            interactionSource = addInteractionSource,
                            indication = null
                        ) {
                            haptics.mediumClick()
                            onAddClick()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = strings.addTransaction,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Analytics (Slot 4)
            BottomNavItemButton(
                selected = selectedItem == NavigationItem.ANALYTICS,
                label = strings.navAnalytics,
                activeIcon = Icons.Filled.PieChart,
                inactiveIcon = Icons.Outlined.PieChart,
                onClick = { onItemSelected(NavigationItem.ANALYTICS) }
            )

            // More / Menu (Includes Accounts, Bills, Receivables, Budgets, Settings)
            BottomNavItemButton(
                selected = isMoreSelected,
                label = strings.navMore,
                activeIcon = Icons.Filled.Menu,
                inactiveIcon = Icons.Outlined.Menu,
                onClick = onMoreClick
            )
        }
    }
}

@Composable
private fun RowScope.BottomNavItemButton(
    selected: Boolean,
    label: String,
    activeIcon: ImageVector,
    inactiveIcon: ImageVector,
    onClick: () -> Unit
) {
    val haptics = rememberAppHaptics()
    val animatedScale by animateFloatAsState(
        targetValue = if (selected) 1.06f else 1.0f,
        animationSpec = spring(dampingRatio = 0.75f, stiffness = Spring.StiffnessMediumLow),
        label = "navItemScale"
    )
    val iconTint by animateColorAsState(
        targetValue = if (selected) ColorAccent else LocalAppColors.current.navInactive,
        animationSpec = tween(180),
        label = "navIconTint"
    )
    val textTint by animateColorAsState(
        targetValue = if (selected) ColorAccent else LocalAppColors.current.navInactive,
        animationSpec = tween(180),
        label = "navTextTint"
    )

    Column(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                haptics.lightTick()
                onClick()
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .graphicsLayer {
                    scaleX = animatedScale
                    scaleY = animatedScale
                }
                .clip(RoundedCornerShape(14.dp))
                .background(if (selected) ColorAccent.copy(alpha = 0.15f) else Color.Transparent)
                .padding(horizontal = 14.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (selected) activeIcon else inactiveIcon,
                contentDescription = label,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium
            ),
            color = textTint,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}



