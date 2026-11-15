package com.example.expensetracker.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material3.MaterialTheme
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
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.theme.CardGap
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.HeroBalanceCard
import com.example.expensetracker.ui.components.HomeTopBar
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.SafeToSpendRow
import com.example.expensetracker.ui.components.formatCurrency
import com.example.expensetracker.ui.components.rememberAppHaptics

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel, onNavigateToTransactions: () -> Unit, onNavigateToSettings: () -> Unit, onOpenAddTransaction: () -> Unit,
    onEditTransaction: (Transaction) -> Unit, onNavigateToAnalytics: (() -> Unit)? = null, onNavigateToAccounts: (() -> Unit)? = null,
    onNavigateToReceivables: (() -> Unit)? = null, onNavigateToBills: (() -> Unit)? = null, onNavigateToBudgets: (() -> Unit)? = null,
    onSplitBillClick: (() -> Unit)? = null, onOpenAddExpense: (() -> Unit)? = null, onOpenAddIncome: (() -> Unit)? = null,
    onTransferClick: (() -> Unit)? = null, onOpenMarketList: (() -> Unit)? = null, onMenuClick: (() -> Unit)? = null
) {
    val state = rememberDashboardScreenState(viewModel)
    var selectedDashboardListId by remember { mutableStateOf<Long?>(null) }
    val strings = LocalAppStrings.current
    val appColors = LocalAppColors.current
    val haptics = rememberAppHaptics()
    var showShareSheet by remember { mutableStateOf(false) }

    var showWalletAsHero by rememberSaveable { mutableStateOf(false) }
    var isMarketExpanded by rememberSaveable { mutableStateOf(false) }
    var isDebtsExpanded by rememberSaveable { mutableStateOf(false) }
    var isSavingsExpanded by rememberSaveable { mutableStateOf(false) }
    var isSpendChartExpanded by rememberSaveable { mutableStateOf(false) }

    val totalWalletBalance = remember(state.accounts) { DashboardSpendCalculator.calculateWalletBalance(state.accounts) }
    val isBn = state.userProfile.appLanguage == "bn"
    val useBengaliNumerals = state.userProfile.useBengaliNumerals || isBn
    val safeSpendDaily = remember(state.summary) { DashboardSpendCalculator.calculateDailySafeSpend(state.summary.budgetRemaining) }
    val dueSoonBills = remember(state.recurringBills) { DashboardSpendCalculator.filterDueSoonBills(state.recurringBills) }

    val listState = rememberLazyListState()
    val showHairline by remember { derivedStateOf { listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 8 } }

    Column(modifier = Modifier.fillMaxSize().background(appColors.background)) {
        HomeTopBar(
            userName = state.userProfile.effectiveFirstName, onAvatarClick = onNavigateToSettings, avatarIndex = state.userProfile.avatarIndex,
            initials = state.userProfile.initials, isPrivacyMode = state.isPrivacyMode, onTogglePrivacyMode = { viewModel.togglePrivacyMode() },
            onNotificationClick = if (dueSoonBills.isNotEmpty() && onNavigateToBills != null) onNavigateToBills else null,
            hasUnreadNotifications = dueSoonBills.isNotEmpty(), showHairline = showHairline
        )

        LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
            item(key = "hero_balance") {
                Spacer(modifier = Modifier.height(6.dp))
                val heroBalance = if (showWalletAsHero && totalWalletBalance > 0) totalWalletBalance else state.summary.netBalance
                val secondaryWallet = if (showWalletAsHero) state.summary.netBalance else (if (totalWalletBalance > 0) totalWalletBalance else null)
                HeroBalanceCard(
                    totalBalance = heroBalance, totalIncome = state.summary.totalIncome, totalExpense = state.summary.totalExpense,
                    budgetAmount = state.summary.budgetAmount, budgetSpent = state.summary.budgetSpent, currencySymbol = state.userProfile.currencySymbol,
                    spendingWalletBalance = secondaryWallet, isPrivacyMode = state.isPrivacyMode, useBengaliNumerals = useBengaliNumerals,
                    onIncomeClick = onOpenAddIncome ?: onOpenAddTransaction, onExpenseClick = onOpenAddExpense ?: onOpenAddTransaction,
                    onToggleWallet = { showWalletAsHero = !showWalletAsHero }
                )
            }

            item(key = "safe_to_spend") {
                Spacer(modifier = Modifier.height(4.dp))
                SafeToSpendRow(dailySafeAmount = safeSpendDaily, hasBudget = state.summary.budgetAmount > 0, currencySymbol = state.userProfile.currencySymbol, useBengaliNumerals = useBengaliNumerals, isPrivacyMode = state.isPrivacyMode, onClick = onNavigateToBudgets)
            }

            item(key = "quick_actions") {
                Spacer(modifier = Modifier.height(8.dp))
                DashboardQuickActionsDock(
                    isBn = isBn, haptics = haptics,
                    onNavigateToAccounts = onNavigateToAccounts, onNavigateToBills = onNavigateToBills,
                    onNavigateToReceivables = onNavigateToReceivables, onNavigateToBudgets = onNavigateToBudgets,
                    onOpenMarketList = onOpenMarketList,
                    onAddExpense = onOpenAddExpense ?: onOpenAddTransaction, onAddIncome = onOpenAddIncome ?: onOpenAddTransaction,
                    onTransfer = onTransferClick ?: onNavigateToAccounts
                )
            }

            if (dueSoonBills.isNotEmpty() && onNavigateToBills != null) {
                item(key = "upcoming_bills") {
                    Spacer(modifier = Modifier.height(10.dp))
                    UpcomingBillsAlertBanner(dueCount = dueSoonBills.size, titleText = strings.upcomingBillsAlert, subtitleText = strings.billsDueAlertSubtitle, payNowText = strings.payNowArrow, onClick = onNavigateToBills, haptics = haptics)
                }
            }

            item(key = "recent_transactions") {
                Spacer(modifier = Modifier.height(14.dp))
                DashboardRecentTransactionsSection(
                    transactions = state.transactions, userProfile = state.userProfile, isPrivacyMode = state.isPrivacyMode,
                    onNavigateToTransactions = onNavigateToTransactions, onOpenAddTransaction = onOpenAddTransaction, onEditTransaction = onEditTransaction
                )
            }

            if (state.transactions.isEmpty()) {
                item(key = "get_started_checklist") {
                    Spacer(modifier = Modifier.height(CardGap))
                    DashboardGetStartedChecklist(hasAccount = state.accounts.isNotEmpty(), hasExpense = state.transactions.isNotEmpty(), hasBudget = state.summary.budgetAmount > 0, onAddAccount = onNavigateToAccounts, onAddExpense = onOpenAddExpense ?: onOpenAddTransaction, onSetBudget = onNavigateToBudgets)
                }
            }

            val effectiveMarketList = if (state.activeMarketLists.isNotEmpty()) {
                state.activeMarketLists.find { it.list.id == selectedDashboardListId } ?: state.activeMarketLists.first()
            } else state.activeMarketList

            if (onOpenMarketList != null && effectiveMarketList != null && effectiveMarketList.items.isNotEmpty()) {
                item(key = "market_list_section") {
                    Spacer(modifier = Modifier.height(CardGap))
                    DashboardMarketCollapsible(
                        effectiveMarketList = effectiveMarketList, activeMarketLists = state.activeMarketLists,
                        currencySymbol = state.userProfile.currencySymbol, isBn = isBn, isPrivacyMode = state.isPrivacyMode,
                        useBengaliNumerals = useBengaliNumerals, isExpanded = isMarketExpanded,
                        onToggleExpand = { isMarketExpanded = !isMarketExpanded }, onOpenMarketList = onOpenMarketList,
                        selectedDashboardListId = selectedDashboardListId, onSelectListId = { selectedDashboardListId = it },
                        viewModel = viewModel, haptics = haptics
                    )
                }
            }

            if (state.debtBookSummary.totalReceivable > 0 || state.debtBookSummary.totalPayable > 0 || state.receivablesSummary.remainingDue > 0) {
                item(key = "debts_section") {
                    Spacer(modifier = Modifier.height(CardGap))
                    DashboardDebtsCollapsible(
                        debtBookSummary = state.debtBookSummary, receivablesSummary = state.receivablesSummary,
                        currencySymbol = state.userProfile.currencySymbol, useBengaliNumerals = useBengaliNumerals,
                        isPrivacyMode = state.isPrivacyMode, isExpanded = isDebtsExpanded,
                        onToggleExpand = { isDebtsExpanded = !isDebtsExpanded }, onOpenFull = onNavigateToReceivables, strings = strings
                    )
                }
            }

            if (state.savingsGoals.isNotEmpty()) {
                item(key = "savings_section") {
                    Spacer(modifier = Modifier.height(CardGap))
                    DashboardSavingsCollapsible(
                        savingsGoals = state.savingsGoals, currencySymbol = state.userProfile.currencySymbol,
                        isBn = isBn, isPrivacyMode = state.isPrivacyMode, isExpanded = isSavingsExpanded,
                        onToggleExpand = { isSavingsExpanded = !isSavingsExpanded }, onOpenFull = onNavigateToBudgets, strings = strings
                    )
                }
            }

            if (state.recentDailySpends.isNotEmpty()) {
                item(key = "spend_chart_section") {
                    Spacer(modifier = Modifier.height(CardGap))
                    val total7Day = state.recentDailySpends.sumOf { it.expense }
                    val spend7DayMetric = if (state.isPrivacyMode) "••••" else formatCurrency(total7Day, state.userProfile.currencySymbol, useBengaliNumerals)
                    DashboardCollapsibleSection(
                        icon = Icons.Outlined.BarChart, title = strings.sevenDaySpend, metricText = spend7DayMetric,
                        metricColor = appColors.textPrimary, isExpanded = isSpendChartExpanded,
                        onToggleExpand = { isSpendChartExpanded = !isSpendChartExpanded }, onOpenFull = onNavigateToAnalytics
                    ) {
                        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            state.recentDailySpends.takeLast(7).forEach { day ->
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = day.dayLabel, style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp), color = appColors.textSecondary)
                                    val amtStr = if (state.isPrivacyMode) "••••" else formatCurrency(day.expense, state.userProfile.currencySymbol, useBengaliNumerals)
                                    Text(text = amtStr, style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, fontWeight = FontWeight.Medium, fontFeatureSettings = "tnum"), color = appColors.textPrimary)
                                }
                            }
                        }
                    }
                }
            }

            item(key = "bottom_spacer") { Spacer(modifier = Modifier.height(100.dp)) }
        }
    }

    DashboardShareSheetHost(show = showShareSheet, timeFilter = state.timeFilter, onDismiss = { showShareSheet = false })
}
