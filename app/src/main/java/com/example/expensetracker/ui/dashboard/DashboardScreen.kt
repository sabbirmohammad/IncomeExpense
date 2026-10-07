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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.BillPaymentStatus
import com.example.expensetracker.data.model.TimeFilter
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.data.model.UserProfile
import com.example.expensetracker.data.model.getLocalizedAccountName
import com.example.expensetracker.data.model.getLocalizedCategoryName
import com.example.expensetracker.theme.CardGap
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorBackground
import com.example.expensetracker.theme.ColorBorder
import com.example.expensetracker.theme.ColorDangerExpense
import com.example.expensetracker.theme.ColorSuccessIncome
import com.example.expensetracker.theme.ColorSurface
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.ColorTextSecondary
import com.example.expensetracker.theme.ColorWarningBills
import com.example.expensetracker.theme.ScreenPaddingHorizontal
import com.example.expensetracker.theme.SmallElementRadius
import com.example.expensetracker.ui.components.AppCard
import com.example.expensetracker.ui.components.BalanceCard
import com.example.expensetracker.ui.components.CategoryIconBadge
import com.example.expensetracker.ui.components.CategoryIconHelper
import com.example.expensetracker.ui.components.EmptyStateCard
import com.example.expensetracker.ui.components.HairlineDivider
import com.example.expensetracker.ui.components.ListRow
import com.example.expensetracker.ui.components.OneUiHeader
import com.example.expensetracker.ui.components.FeedbackVisualType
import com.example.expensetracker.ui.components.FeedbackStrings
import com.example.expensetracker.ui.components.LocalFeedbackController
import com.example.expensetracker.ui.components.TransactionFeedbackData
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.formatCurrency
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.automirrored.outlined.AssignmentReturn
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Handshake
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.Smartphone
import androidx.compose.material.icons.outlined.TipsAndUpdates
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.AccountType
import com.example.expensetracker.data.model.ReceivablesSummary
import com.example.expensetracker.data.model.RecurringBill
import com.example.expensetracker.ui.components.BrandIdentityHelper
import com.example.expensetracker.ui.components.ActiveMarketListDashboardCard
import com.example.expensetracker.ui.components.CreateMarketListPromptCard
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.SpendingBarChart
import com.example.expensetracker.ui.components.rememberAppHaptics
import com.example.expensetracker.ui.share.ShareExpenseSheet
import com.example.expensetracker.ui.share.SharePeriodType

import com.example.expensetracker.ui.components.DateTimeHelper
import androidx.compose.material.icons.filled.SwapHoriz

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
    val recentDailySpends by viewModel.recentDailySpends.collectAsState()
    val accounts by viewModel.accounts.collectAsState()
    val receivablesSummary by viewModel.receivablesSummary.collectAsState()
    val debtBookSummary by viewModel.debtBookSummary.collectAsState()
    val savingsGoals by viewModel.savingsGoals.collectAsState()
    val recurringBills by viewModel.recurringBills.collectAsState()
    val activeMarketList by viewModel.activeMarketList.collectAsState()
    val topExpenseCategory by viewModel.topCategorySpend.collectAsState()
    val categorySpends by viewModel.categorySpends.collectAsState()
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
        // 1. Header row: menu button + greeting + date + circular avatar button (opens profile & settings)
        OneUiHeader(
            userName = userProfile.effectiveFirstName,
            dateText = DateTimeHelper.formatDateHeader(System.currentTimeMillis(), isBn, userProfile.useBengaliNumerals || isBn),
            onAvatarClick = onNavigateToSettings,
            avatarIndex = userProfile.avatarIndex,
            initials = userProfile.initials,
            onMenuClick = onMenuClick
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {

            // 1. Balance card with hero value, two metric chips, and budget progress bar
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

            // 3. Modern Quick Action Dock (Market, Accounts, Bills, Debts, Budget)
            item {
                Spacer(modifier = Modifier.height(CardGap))
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = LocalAppColors.current.surface,
                    border = BorderStroke(1.dp, LocalAppColors.current.border),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = ScreenPaddingHorizontal)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (onOpenMarketList != null) {
                            QuickActionItem(
                                icon = Icons.Outlined.ShoppingCart,
                                label = if (isBn) "বাজার সদাই" else "Market",
                                tintColor = Color(0xFF00A8FF),
                                onClick = {
                                    haptics.lightTick()
                                    onOpenMarketList()
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        if (onNavigateToAccounts != null) {
                            QuickActionItem(
                                icon = Icons.Outlined.AccountBalance,
                                label = if (isBn) "অ্যাকাউন্টস" else "Accounts",
                                tintColor = Color(0xFF00B894),
                                onClick = {
                                    haptics.mediumClick()
                                    onNavigateToAccounts()
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        if (onNavigateToBills != null) {
                            QuickActionItem(
                                icon = Icons.Outlined.ReceiptLong,
                                label = if (isBn) "বিল পে" else "Bills",
                                tintColor = Color(0xFFF7941D),
                                onClick = {
                                    haptics.mediumClick()
                                    onNavigateToBills()
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        if (onNavigateToReceivables != null) {
                            QuickActionItem(
                                icon = Icons.Outlined.Handshake,
                                label = if (isBn) "ধার-দেনা" else "Debts",
                                tintColor = Color(0xFF9C27B0),
                                onClick = {
                                    haptics.mediumClick()
                                    onNavigateToReceivables()
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        if (onNavigateToBudgets != null) {
                            QuickActionItem(
                                icon = Icons.Outlined.Savings,
                                label = if (isBn) "বাজেট" else "Budget",
                                tintColor = Color(0xFFE84393),
                                onClick = {
                                    haptics.mediumClick()
                                    onNavigateToBudgets()
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Upcoming Bills Alert Banner
            val dueSoonBills = recurringBills.filter { bill ->
                bill.isActive && (bill.getPaymentStatus() == BillPaymentStatus.DUE_SOON || bill.getPaymentStatus() == BillPaymentStatus.OVERDUE)
            }
            if (dueSoonBills.isNotEmpty() && onNavigateToBills != null) {
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF7941D).copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, Color(0xFFF7941D).copy(alpha = 0.35f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = ScreenPaddingHorizontal)
                            .clickable {
                                haptics.mediumClick()
                                onNavigateToBills()
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.NotificationsActive,
                                    contentDescription = null,
                                    tint = Color(0xFFF7941D),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = strings.upcomingBillsAlert,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ColorTextPrimary
                                    )
                                    Text(
                                        text = "${dueSoonBills.size} ${strings.billsDueAlertSubtitle}",
                                        fontSize = 10.sp,
                                        color = ColorTextSecondary
                                    )
                                }
                            }
                            Text(
                                text = strings.payNowArrow,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFF7941D)
                            )
                        }
                    }
                }
            }



            // 6. Recent Transactions (Elevated priority, right below Accounts)
            item {
                Spacer(modifier = Modifier.height(18.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = ScreenPaddingHorizontal, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = strings.recentTransactions,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = ColorTextPrimary
                    )

                    Text(
                        text = strings.seeAll,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = ColorAccent,
                        modifier = Modifier.clickable { onNavigateToTransactions() }
                    )
                }
            }

            val recentTxs = transactions.take(4)
            if (recentTxs.isEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    EmptyStateCard(
                        icon = Icons.Outlined.ReceiptLong,
                        headline = strings.logFirstTransaction,
                        explanation = strings.startTrackingSubtitle,
                        buttonText = strings.addTransaction,
                        onButtonClick = onOpenAddTransaction,
                        modifier = Modifier.padding(horizontal = ScreenPaddingHorizontal)
                    )
                }
            } else {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    AppCard(
                        modifier = Modifier.padding(horizontal = ScreenPaddingHorizontal),
                        padding = 16.dp
                    ) {
                        Column {
                            recentTxs.forEachIndexed { index, tx ->
                                val isIncome = tx.type == TransactionType.INCOME
                                val amountFormatted = if (isPrivacyMode) {
                                    "${if (isIncome) "+" else "-"} ••••"
                                } else {
                                    "${if (isIncome) "+" else "-"} ${formatCurrency(tx.amount, userProfile.currencySymbol, userProfile.useBengaliNumerals || strings.isBangla)}"
                                }
                                val amountColor = if (isIncome) ColorSuccessIncome else ColorDangerExpense

                                ListRow(
                                    iconName = tx.categoryIcon,
                                    categoryColorHex = tx.categoryColor,
                                    title = tx.title,
                                    meta = "${getLocalizedCategoryName(tx.categoryName, strings.isBangla)} • ${DateTimeHelper.formatItemDateTime(tx.dateMillis, strings.isBangla, userProfile.useBengaliNumerals || strings.isBangla)}",
                                    amountText = amountFormatted,
                                    amountColor = amountColor,
                                    onClick = { onEditTransaction(tx) },
                                    paymentMethod = tx.paymentMethod,
                                    paymentMethodName = getLocalizedAccountName(tx.accountName, strings.isBangla),
                                    hasReceipt = tx.receiptImagePath != null
                                )

                                if (index < recentTxs.lastIndex) {
                                    HairlineDivider()
                                }
                            }
                        }
                    }
                }
            }

            // 7. Active Market / Grocery Checklist (Shown only when there are active items)
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
                        onUnmarkPurchased = { item ->
                            viewModel.unmarkMarketItemPurchased(item)
                        },
                        onUpdateItemAmount = { item, amt ->
                            viewModel.updateMarketItem(item.copy(amount = amt))
                        },
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


            // 9. Debt & Receivables Ledger Widget (দেনা-পাওনা খতিয়ান)
            if (debtBookSummary.totalReceivable > 0 || debtBookSummary.totalPayable > 0 || receivablesSummary.remainingDue > 0) {
                item {
                    Spacer(modifier = Modifier.height(CardGap))
                    AppCard(
                        modifier = Modifier
                            .padding(horizontal = ScreenPaddingHorizontal)
                            .clickable { onNavigateToReceivables?.invoke() },
                        padding = 14.dp
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(ColorWarningBills.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Outlined.AssignmentReturn,
                                        contentDescription = null,
                                        tint = ColorWarningBills,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }

                                Column {
                                    Text(
                                        text = strings.debtsLedgerTitle,
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium
                                        ),
                                        color = ColorTextSecondary
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = if (isPrivacyMode) "••••••"
                                        else "${strings.netPosition}: ${formatCurrency(debtBookSummary.netBalance, userProfile.currencySymbol)}",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = if (debtBookSummary.netBalance >= 0) ColorSuccessIncome else ColorDangerExpense
                                    )
                                    val subtitleText = if (strings.isBangla) {
                                        "পাওনা: ${if (isPrivacyMode) "••••" else formatCurrency(debtBookSummary.totalReceivable, userProfile.currencySymbol)} · দেনা: ${if (isPrivacyMode) "••••" else formatCurrency(debtBookSummary.totalPayable, userProfile.currencySymbol)}"
                                    } else {
                                        "Owed to you: ${if (isPrivacyMode) "••••" else formatCurrency(debtBookSummary.totalReceivable, userProfile.currencySymbol)} · You owe: ${if (isPrivacyMode) "••••" else formatCurrency(debtBookSummary.totalPayable, userProfile.currencySymbol)}"
                                    }
                                    Text(
                                        text = subtitleText,
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                        color = ColorTextSecondary
                                    )
                                }
                            }

                            Icon(
                                imageVector = Icons.Outlined.ChevronRight,
                                contentDescription = null,
                                tint = ColorTextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // 10. Savings Goals Widget (সঞ্চয় লক্ষ্যমাত্রা)
            if (savingsGoals.isNotEmpty()) {
                item {
                    val totalSavedGoals = remember(savingsGoals) { savingsGoals.sumOf { it.savedAmount } }
                    val totalTargetGoals = remember(savingsGoals) { savingsGoals.sumOf { it.targetAmount } }
                    val overallGoalProgress = if (totalTargetGoals > 0) ((totalSavedGoals / totalTargetGoals) * 100).toFloat().coerceIn(0f, 100f) else 0f
                    val completedGoalsCount = remember(savingsGoals) { savingsGoals.count { it.isCompleted } }

                    Spacer(modifier = Modifier.height(CardGap))
                    AppCard(
                        modifier = Modifier
                            .padding(horizontal = ScreenPaddingHorizontal)
                            .clickable { onNavigateToBudgets?.invoke() },
                        padding = 14.dp
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(CircleShape)
                                            .background(ColorAccent.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.Savings,
                                            contentDescription = null,
                                            tint = ColorAccent,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }

                                    Column {
                                        Text(
                                            text = strings.savingsGoalsTitle,
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Medium
                                            ),
                                            color = ColorTextSecondary
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = if (isPrivacyMode) "••••••"
                                            else "${formatCurrency(totalSavedGoals, userProfile.currencySymbol)} / ${formatCurrency(totalTargetGoals, userProfile.currencySymbol)}",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = ColorSuccessIncome
                                        )
                                        Text(
                                            text = if (strings.isBangla) {
                                                "${convertToBengaliDigits(completedGoalsCount.toString())}টি অর্জিত · ${convertToBengaliDigits(savingsGoals.size.toString())}টি মোট লক্ষ্য"
                                            } else {
                                                "$completedGoalsCount reached · ${savingsGoals.size} total goal(s)"
                                            },
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                            color = ColorTextSecondary
                                        )
                                    }
                                }

                                Icon(
                                    imageVector = Icons.Outlined.ChevronRight,
                                    contentDescription = null,
                                    tint = ColorTextSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            androidx.compose.material3.LinearProgressIndicator(
                                progress = { overallGoalProgress / 100f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(5.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = ColorSuccessIncome,
                                trackColor = ColorBorder
                            )
                        }
                    }
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

/**
 * Smart insight card component:
 * Icon badge (38x38px, 10px radius, ~15% tint) + short headline (15px / 500) + one sentence (12px / 400).
 */
@Composable
private fun HomeInsightCard(
    icon: ImageVector,
    iconColor: Color,
    headline: String,
    sentence: String,
    modifier: Modifier = Modifier
) {
    AppCard(
        modifier = modifier,
        padding = 14.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(SmallElementRadius))
                    .background(iconColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = headline,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = ColorTextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = sentence,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = ColorTextSecondary,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
private fun QuickActionItem(
    icon: ImageVector,
    label: String,
    tintColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp, horizontal = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(tintColor.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = tintColor,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Medium
            ),
            color = LocalAppColors.current.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

