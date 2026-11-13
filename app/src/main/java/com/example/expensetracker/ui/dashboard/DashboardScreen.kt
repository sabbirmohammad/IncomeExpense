package com.example.expensetracker.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.AssignmentReturn
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.expensetracker.data.model.AccountType
import com.example.expensetracker.data.model.BillPaymentStatus
import com.example.expensetracker.data.model.TimeFilter
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.theme.CardGap
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.ScreenPaddingHorizontal
import com.example.expensetracker.ui.components.ActiveMarketListDashboardCard
import com.example.expensetracker.ui.components.FeedbackStrings
import com.example.expensetracker.ui.components.FeedbackVisualType
import com.example.expensetracker.ui.components.HeroBalanceCard
import com.example.expensetracker.ui.components.HomeTopBar
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.LocalFeedbackController
import com.example.expensetracker.ui.components.SafeToSpendRow
import com.example.expensetracker.ui.components.TransactionFeedbackData
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.formatCurrency
import com.example.expensetracker.ui.components.rememberAppHaptics
import com.example.expensetracker.ui.share.ShareExpenseSheet
import com.example.expensetracker.ui.share.SharePeriodType
import java.util.Calendar

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
    val summary by viewModel.summary.collectAsStateWithLifecycle()
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val timeFilter by viewModel.selectedTimeFilter.collectAsStateWithLifecycle()
    val accounts by viewModel.accounts.collectAsStateWithLifecycle()
    val receivablesSummary by viewModel.receivablesSummary.collectAsStateWithLifecycle()
    val debtBookSummary by viewModel.debtBookSummary.collectAsStateWithLifecycle()
    val savingsGoals by viewModel.savingsGoals.collectAsStateWithLifecycle()
    val recurringBills by viewModel.recurringBills.collectAsStateWithLifecycle()
    val activeMarketList by viewModel.activeMarketList.collectAsStateWithLifecycle()
    val activeMarketLists by viewModel.activeMarketLists.collectAsStateWithLifecycle()
    val recentDailySpends by viewModel.recentDailySpends.collectAsStateWithLifecycle()
    var selectedDashboardListId by remember { mutableStateOf<Long?>(null) }
    val isPrivacyMode by viewModel.isPrivacyMode.collectAsStateWithLifecycle()
    val strings = LocalAppStrings.current
    val appColors = LocalAppColors.current
    val haptics = rememberAppHaptics()
    val feedbackController = LocalFeedbackController.current

    var showShareSheet by remember { mutableStateOf(false) }

    // Toggle for spending wallet view on hero balance
    var showWalletAsHero by rememberSaveable { mutableStateOf(false) }

    // Below-the-fold collapsible state (collapsed by default, remembers state)
    var isMarketExpanded by rememberSaveable { mutableStateOf(false) }
    var isDebtsExpanded by rememberSaveable { mutableStateOf(false) }
    var isSavingsExpanded by rememberSaveable { mutableStateOf(false) }
    var isSpendChartExpanded by rememberSaveable { mutableStateOf(false) }

    val totalWalletBalance = remember(accounts) {
        accounts.filter { it.type != AccountType.BANK }.sumOf { it.currentBalance }
    }
    val isBn = userProfile.appLanguage == "bn"
    val useBengaliNumerals = userProfile.useBengaliNumerals || isBn

    // Safe to spend daily calculation
    val safeSpendDaily = remember(summary) {
        if (summary.budgetRemaining > 0) {
            val cal = Calendar.getInstance()
            val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
            val currentDay = cal.get(Calendar.DAY_OF_MONTH)
            val remainingDays = (daysInMonth - currentDay + 1).coerceAtLeast(1)
            summary.budgetRemaining / remainingDays
        } else 0.0
    }

    val dueSoonBills = remember(recurringBills) {
        recurringBills.filter { bill ->
            bill.isActive && (bill.getPaymentStatus() == BillPaymentStatus.DUE_SOON || bill.getPaymentStatus() == BillPaymentStatus.OVERDUE)
        }
    }

    val listState = rememberLazyListState()
    val showHairline by remember {
        derivedStateOf { listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 8 }
    }

    Column(modifier = Modifier.fillMaxSize().background(appColors.background)) {
        // Pinned HomeTopBar with greeting, avatar -> Settings, privacy toggle, notifications, and scroll hairline
        HomeTopBar(
            userName = userProfile.effectiveFirstName,
            onAvatarClick = onNavigateToSettings,
            avatarIndex = userProfile.avatarIndex,
            initials = userProfile.initials,
            isPrivacyMode = isPrivacyMode,
            onTogglePrivacyMode = { viewModel.togglePrivacyMode() },
            onNotificationClick = if (dueSoonBills.isNotEmpty() && onNavigateToBills != null) onNavigateToBills else null,
            hasUnreadNotifications = dueSoonBills.isNotEmpty(),
            showHairline = showHairline
        )

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize()
        ) {
            // 1. HeroBalanceCard (target <= 200dp, left-aligned, Total balance default, spending wallet secondary line)
            item(key = "hero_balance") {
                Spacer(modifier = Modifier.height(6.dp))
                val heroBalance = if (showWalletAsHero && totalWalletBalance > 0) totalWalletBalance else summary.netBalance
                val secondaryWallet = if (showWalletAsHero) summary.netBalance else (if (totalWalletBalance > 0) totalWalletBalance else null)

                HeroBalanceCard(
                    totalBalance = heroBalance,
                    totalIncome = summary.totalIncome,
                    totalExpense = summary.totalExpense,
                    budgetAmount = summary.budgetAmount,
                    budgetSpent = summary.budgetSpent,
                    currencySymbol = userProfile.currencySymbol,
                    spendingWalletBalance = secondaryWallet,
                    isPrivacyMode = isPrivacyMode,
                    useBengaliNumerals = useBengaliNumerals,
                    onIncomeClick = onOpenAddIncome ?: onOpenAddTransaction,
                    onExpenseClick = onOpenAddExpense ?: onOpenAddTransaction,
                    onToggleWallet = { showWalletAsHero = !showWalletAsHero }
                )
            }

            // 2. Safe to spend today row
            item(key = "safe_to_spend") {
                Spacer(modifier = Modifier.height(4.dp))
                SafeToSpendRow(
                    dailySafeAmount = safeSpendDaily,
                    hasBudget = summary.budgetAmount > 0,
                    currencySymbol = userProfile.currencySymbol,
                    useBengaliNumerals = useBengaliNumerals,
                    isPrivacyMode = isPrivacyMode,
                    onClick = onNavigateToBudgets
                )
            }

            // 3. 4 Quick Actions (uniform accentContainer tint, touch targets >= 48dp, no outer container card)
            item(key = "quick_actions") {
                Spacer(modifier = Modifier.height(8.dp))
                DashboardQuickActionsDock(
                    isBn = isBn,
                    haptics = haptics,
                    onNavigateToAccounts = onNavigateToAccounts,
                    onNavigateToBills = onNavigateToBills,
                    onNavigateToReceivables = onNavigateToReceivables,
                    onNavigateToBudgets = onNavigateToBudgets,
                    onOpenMarketList = onOpenMarketList,
                    onAddExpense = onOpenAddExpense ?: onOpenAddTransaction,
                    onAddIncome = onOpenAddIncome ?: onOpenAddTransaction,
                    onTransfer = onTransferClick ?: onNavigateToAccounts
                )
            }

            // 4. Conditional upcoming bills banner (only if due <= 4 days / overdue)
            if (dueSoonBills.isNotEmpty() && onNavigateToBills != null) {
                item(key = "upcoming_bills") {
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

            // 5. Recent transactions section (at least 3 rows visible above the fold on a 6" screen)
            item(key = "recent_transactions") {
                Spacer(modifier = Modifier.height(14.dp))
                DashboardRecentTransactionsSection(
                    transactions = transactions,
                    userProfile = userProfile,
                    isPrivacyMode = isPrivacyMode,
                    onNavigateToTransactions = onNavigateToTransactions,
                    onOpenAddTransaction = onOpenAddTransaction,
                    onEditTransaction = onEditTransaction
                )
            }

            // 6. New User "Get started" checklist (only if transactions are empty)
            if (transactions.isEmpty()) {
                item(key = "get_started_checklist") {
                    Spacer(modifier = Modifier.height(CardGap))
                    DashboardGetStartedChecklist(
                        hasAccount = accounts.isNotEmpty(),
                        hasExpense = transactions.isNotEmpty(),
                        hasBudget = summary.budgetAmount > 0,
                        onAddAccount = onNavigateToAccounts,
                        onAddExpense = onOpenAddExpense ?: onOpenAddTransaction,
                        onSetBudget = onNavigateToBudgets
                    )
                }
            }

            // 7. Below-the-fold collapsible widgets (each collapsible, remembers state, hidden if no data)
            val effectiveMarketList = if (activeMarketLists.isNotEmpty()) {
                activeMarketLists.find { it.list.id == selectedDashboardListId } ?: activeMarketLists.first()
            } else {
                activeMarketList
            }

            // Active Market List collapsible
            if (onOpenMarketList != null && effectiveMarketList != null && effectiveMarketList.items.isNotEmpty()) {
                item(key = "market_list_section") {
                    Spacer(modifier = Modifier.height(CardGap))
                    val marketMetric = "${effectiveMarketList.purchasedItems}/${effectiveMarketList.totalItems} ${if (isBn) "পণ্য" else "items"} · ${if (isPrivacyMode) "••••" else formatCurrency(effectiveMarketList.totalSpent, userProfile.currencySymbol, useBengaliNumerals)}"

                    DashboardCollapsibleSection(
                        icon = Icons.Outlined.ShoppingCart,
                        title = effectiveMarketList.list.title.ifBlank { if (isBn) "বাজার ফর্দ" else "Market List" },
                        metricText = marketMetric,
                        isExpanded = isMarketExpanded,
                        onToggleExpand = { isMarketExpanded = !isMarketExpanded },
                        onOpenFull = onOpenMarketList
                    ) {
                        // Switcher chips if multiple active lists
                        if (activeMarketLists.size > 1) {
                            LazyRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                items(activeMarketLists, key = { it.list.id }) { aList ->
                                    val isSelected = aList.list.id == effectiveMarketList.list.id
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isSelected) appColors.accent.copy(alpha = 0.15f) else appColors.surface,
                                        border = BorderStroke(1.dp, if (isSelected) appColors.accent else appColors.border),
                                        modifier = Modifier.clickable {
                                            haptics.lightTick()
                                            selectedDashboardListId = aList.list.id
                                        }
                                    ) {
                                        Text(
                                            text = aList.list.title,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 11.5.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                            ),
                                            color = if (isSelected) appColors.accent else appColors.textSecondary,
                                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                                        )
                                    }
                                }
                            }
                        }

                        ActiveMarketListDashboardCard(
                            marketListWithItems = effectiveMarketList,
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
                            }
                        )
                    }
                }
            }

            // Debts snapshot collapsible
            if (debtBookSummary.totalReceivable > 0 || debtBookSummary.totalPayable > 0 || receivablesSummary.remainingDue > 0) {
                item(key = "debts_section") {
                    Spacer(modifier = Modifier.height(CardGap))
                    val netFormatted = if (isPrivacyMode) "••••" else formatCurrency(debtBookSummary.netBalance, userProfile.currencySymbol, useBengaliNumerals)
                    val debtMetric = "${strings.netPosition}: $netFormatted"

                    DashboardCollapsibleSection(
                        icon = Icons.AutoMirrored.Outlined.AssignmentReturn,
                        title = strings.debtsLedgerTitle,
                        metricText = debtMetric,
                        metricColor = if (debtBookSummary.netBalance >= 0) appColors.income else appColors.expense,
                        isExpanded = isDebtsExpanded,
                        onToggleExpand = { isDebtsExpanded = !isDebtsExpanded },
                        onOpenFull = onNavigateToReceivables
                    ) {
                        DashboardDebtsLedgerWidget(
                            debtBookSummary = debtBookSummary,
                            currencySymbol = userProfile.currencySymbol,
                            isPrivacyMode = isPrivacyMode,
                            onNavigateToReceivables = onNavigateToReceivables,
                            modifier = Modifier.padding(0.dp)
                        )
                    }
                }
            }

            // Savings goals collapsible
            if (savingsGoals.isNotEmpty()) {
                item(key = "savings_section") {
                    Spacer(modifier = Modifier.height(CardGap))
                    val completedGoalsCount = savingsGoals.count { it.isCompleted }
                    val savingsMetric = if (isBn) {
                        "${convertToBengaliDigits(completedGoalsCount.toString())}/${convertToBengaliDigits(savingsGoals.size.toString())}টি অর্জিত"
                    } else {
                        "$completedGoalsCount of ${savingsGoals.size} reached"
                    }

                    DashboardCollapsibleSection(
                        icon = Icons.Outlined.Savings,
                        title = strings.savingsGoalsTitle,
                        metricText = savingsMetric,
                        metricColor = appColors.income,
                        isExpanded = isSavingsExpanded,
                        onToggleExpand = { isSavingsExpanded = !isSavingsExpanded },
                        onOpenFull = onNavigateToBudgets
                    ) {
                        DashboardSavingsGoalsWidget(
                            savingsGoals = savingsGoals,
                            currencySymbol = userProfile.currencySymbol,
                            isPrivacyMode = isPrivacyMode,
                            onNavigateToBudgets = onNavigateToBudgets,
                            modifier = Modifier.padding(0.dp)
                        )
                    }
                }
            }

            // 7-day spend collapsible
            if (recentDailySpends.isNotEmpty()) {
                item(key = "spend_chart_section") {
                    Spacer(modifier = Modifier.height(CardGap))
                    val total7Day = recentDailySpends.sumOf { it.expense }
                    val spend7DayMetric = if (isPrivacyMode) "••••" else formatCurrency(total7Day, userProfile.currencySymbol, useBengaliNumerals)

                    DashboardCollapsibleSection(
                        icon = Icons.Outlined.BarChart,
                        title = strings.sevenDaySpend,
                        metricText = spend7DayMetric,
                        metricColor = appColors.textPrimary,
                        isExpanded = isSpendChartExpanded,
                        onToggleExpand = { isSpendChartExpanded = !isSpendChartExpanded },
                        onOpenFull = onNavigateToAnalytics
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            recentDailySpends.takeLast(7).forEach { day ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = day.dayLabel,
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                        color = appColors.textSecondary
                                    )
                                    val amtStr = if (isPrivacyMode) "••••" else formatCurrency(day.expense, userProfile.currencySymbol, useBengaliNumerals)
                                    Text(
                                        text = amtStr,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            fontFeatureSettings = "tnum"
                                        ),
                                        color = appColors.textPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item(key = "bottom_spacer") {
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
