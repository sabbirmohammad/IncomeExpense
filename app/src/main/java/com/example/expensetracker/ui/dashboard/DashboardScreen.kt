package com.example.expensetracker.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.expensetracker.data.model.AccountType
import com.example.expensetracker.data.model.BillPaymentStatus
import com.example.expensetracker.data.model.TimeFilter
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.theme.CardGap
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.ScreenPaddingHorizontal
import com.example.expensetracker.ui.components.ActiveMarketListDashboardCard
import com.example.expensetracker.ui.components.BalanceCard
import com.example.expensetracker.ui.components.DateTimeHelper
import com.example.expensetracker.ui.components.FeedbackStrings
import com.example.expensetracker.ui.components.FeedbackVisualType
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.LocalFeedbackController
import com.example.expensetracker.ui.components.OneUiHeader
import com.example.expensetracker.ui.components.TransactionFeedbackData
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.rememberAppHaptics
import com.example.expensetracker.ui.share.ShareExpenseSheet
import com.example.expensetracker.ui.share.SharePeriodType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onNavigateToTransactions: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onOpenAddTransaction: () -> Unit,
    onEditTransaction: (Transaction) -> Unit,
    onNavigateToAnalytics: (() -> Unit)? = null,
    onNavigateToAccounts: (() -> Unit)? = null,
    onNavigateToReceivables: (() -> Unit)? = null,
    onNavigateToBills: (() -> Unit)? = null,
    onNavigateToBudgets: (() -> Unit)? = null,
    onSplitBillClick: (() -> Unit)? = null,
    onOpenAddExpense: (() -> Unit)? = null,
    onOpenAddIncome: (() -> Unit)? = null,
    onTransferClick: (() -> Unit)? = null,
    onOpenMarketList: (() -> Unit)? = null,
    onMenuClick: (() -> Unit)? = null
) {
    val summary by viewModel.summary.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val timeFilter by viewModel.selectedTimeFilter.collectAsState()
    val accounts by viewModel.accounts.collectAsState()
    val receivablesSummary by viewModel.receivablesSummary.collectAsState()
    val debtBookSummary by viewModel.debtBookSummary.collectAsState()
    val savingsGoals by viewModel.savingsGoals.collectAsState()
    val recurringBills by viewModel.recurringBills.collectAsState()
    val activeMarketList by viewModel.activeMarketList.collectAsState()
    val isPrivacyMode by viewModel.isPrivacyMode.collectAsState()
    val strings = LocalAppStrings.current
    val haptics = rememberAppHaptics()
    val feedbackController = LocalFeedbackController.current

    var showShareSheet by remember { mutableStateOf(false) }

    val savingsRate = remember(summary) {
        if (summary.totalIncome > 0) {
            (((summary.totalIncome - summary.totalExpense) / summary.totalIncome) * 100).toInt()
        } else 0
    }

    val totalWalletBalance = remember(accounts) { accounts.filter { it.type != AccountType.BANK }.sumOf { it.currentBalance } }
    val isBn = userProfile.appLanguage == "bn"

    Column(modifier = Modifier.fillMaxSize().background(LocalAppColors.current.background)) {
        OneUiHeader(
            userName = userProfile.effectiveFirstName,
            dateText = DateTimeHelper.formatDateHeader(System.currentTimeMillis(), isBn, userProfile.useBengaliNumerals || isBn),
            onAvatarClick = onNavigateToSettings,
            avatarIndex = userProfile.avatarIndex,
            initials = userProfile.initials,
            onMenuClick = onMenuClick
        )

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                BalanceCard(
                    netBalance = summary.netBalance,
                    totalIncome = summary.totalIncome,
                    totalExpense = summary.totalExpense,
                    budgetAmount = summary.budgetAmount,
                    budgetSpent = summary.budgetSpent,
                    currencySymbol = userProfile.currencySymbol,
                    useBengaliNumerals = userProfile.useBengaliNumerals || isBn,
                    savingsRate = savingsRate,
                    totalWalletBalance = totalWalletBalance,
                    isPrivacyMode = isPrivacyMode,
                    onTogglePrivacyMode = { viewModel.togglePrivacyMode() },
                    onIncomeClick = onOpenAddIncome,
                    onExpenseClick = onOpenAddExpense
                )
            }

            item {
                Spacer(modifier = Modifier.height(CardGap))
                DashboardQuickActionsDock(
                    isBn = isBn,
                    haptics = haptics,
                    onOpenMarketList = onOpenMarketList,
                    onNavigateToAccounts = onNavigateToAccounts,
                    onNavigateToBills = onNavigateToBills,
                    onNavigateToReceivables = onNavigateToReceivables,
                    onNavigateToBudgets = onNavigateToBudgets
                )
            }

            val dueSoonBills = recurringBills.filter { bill ->
                bill.isActive && (bill.getPaymentStatus() == BillPaymentStatus.DUE_SOON || bill.getPaymentStatus() == BillPaymentStatus.OVERDUE)
            }
            if (dueSoonBills.isNotEmpty() && onNavigateToBills != null) {
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    UpcomingBillsAlertBanner(
                        dueCount = dueSoonBills.size,
                        titleText = strings.upcomingBillsAlert,
                        subtitleText = strings.billsDueAlertSubtitle,
                        payNowText = strings.payNowArrow,
                        onClick = onNavigateToBills,
                        haptics = haptics
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(18.dp))
                DashboardRecentTransactionsSection(
                    transactions = transactions,
                    userProfile = userProfile,
                    isPrivacyMode = isPrivacyMode,
                    onNavigateToTransactions = onNavigateToTransactions,
                    onOpenAddTransaction = onOpenAddTransaction,
                    onEditTransaction = onEditTransaction
                )
            }

            val activeMarket = activeMarketList
            if (onOpenMarketList != null && activeMarket != null && activeMarket.items.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(CardGap))
                    ActiveMarketListDashboardCard(
                        marketListWithItems = activeMarket,
                        currencySymbol = userProfile.currencySymbol,
                        isBangla = isBn,
                        onOpenFullList = onOpenMarketList,
                        onMarkPurchased = { item, amt ->
                            viewModel.markMarketItemPurchased(item, amt)
                            feedbackController.show(
                                TransactionFeedbackData(
                                    type = FeedbackVisualType.EXPENSE,
                                    title = FeedbackStrings.expenseRecorded(isBn),
                                    amount = amt,
                                    currencySymbol = userProfile.currencySymbol,
                                    subtitle = "${item.name} (${item.formatQuantity()}) · ${if (isBn) "বাজার ও সদাই" else "Groceries"}"
                                )
                            )
                        },
                        onUnmarkPurchased = { item -> viewModel.unmarkMarketItemPurchased(item) },
                        onUpdateItemAmount = { item, amt -> viewModel.updateMarketItem(item.copy(amount = amt)) },
                        onCompleteList = { listId ->
                            viewModel.completeActiveMarketList(listId) { result ->
                                if (result.success && result.totalSpent > 0) {
                                    feedbackController.show(
                                        TransactionFeedbackData(
                                            type = FeedbackVisualType.EXPENSE,
                                            title = if (isBn) "বাজার সম্পন্ন হয়েছে!" else "Shopping Completed!",
                                            amount = result.totalSpent,
                                            currencySymbol = userProfile.currencySymbol,
                                            subtitle = if (isBn) "${convertToBengaliDigits(result.purchasedCount.toString())}টি পণ্য সম্পন্ন · মোট বাজার খরচ"
                                                      else "${result.purchasedCount} items purchased · Total spent"
                                        )
                                    )
                                }
                            }
                        },
                        modifier = Modifier.padding(horizontal = ScreenPaddingHorizontal)
                    )
                }
            }

            if (debtBookSummary.totalReceivable > 0 || debtBookSummary.totalPayable > 0 || receivablesSummary.remainingDue > 0) {
                item {
                    Spacer(modifier = Modifier.height(CardGap))
                    DashboardDebtsLedgerWidget(
                        debtBookSummary = debtBookSummary,
                        currencySymbol = userProfile.currencySymbol,
                        isPrivacyMode = isPrivacyMode,
                        onNavigateToReceivables = onNavigateToReceivables
                    )
                }
            }

            if (savingsGoals.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(CardGap))
                    DashboardSavingsGoalsWidget(
                        savingsGoals = savingsGoals,
                        currencySymbol = userProfile.currencySymbol,
                        isPrivacyMode = isPrivacyMode,
                        onNavigateToBudgets = onNavigateToBudgets
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }

    if (showShareSheet) {
        val mappedPeriod = when (timeFilter) {
            TimeFilter.THIS_WEEK -> SharePeriodType.THIS_WEEK
            TimeFilter.THIS_MONTH -> SharePeriodType.THIS_MONTH
            TimeFilter.TODAY -> SharePeriodType.TODAY
            else -> SharePeriodType.THIS_MONTH
        }
        ShareExpenseSheet(
            initialPeriod = mappedPeriod,
            onDismiss = { showShareSheet = false }
        )
    }
}
