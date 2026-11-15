package com.example.expensetracker.ui.money

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.AssignmentReturn
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.Space20
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.formatCurrency
import com.example.expensetracker.ui.components.rememberBottomBarPadding
import com.example.expensetracker.ui.dashboard.DashboardViewModel
import com.example.expensetracker.ui.navigation.NavigationItem

data class MoneyHubItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val destination: NavigationItem
)

/**
 * Money Hub Screen (v7 Navigation):
 * Primary hub giving 1-2 tap access to:
 * - Accounts & Wallets
 * - Budgets & Goals
 * - Bills & Subscriptions
 * - Money Owed (ধার ও দেনা)
 * - Insights (Analytics)
 * - Statements & Reports
 * Plus gear icon for Settings.
 */
@Composable
fun MoneyHubScreen(
    dashboardViewModel: DashboardViewModel,
    onNavigate: (NavigationItem) -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current
    val strings = LocalAppStrings.current
    val bottomPadding = rememberBottomBarPadding()

    val accounts by dashboardViewModel.accounts.collectAsStateWithLifecycle()
    val summary by dashboardViewModel.summary.collectAsStateWithLifecycle()
    val debtSummary by dashboardViewModel.debtBookSummary.collectAsStateWithLifecycle()
    val recurringBills by dashboardViewModel.recurringBills.collectAsStateWithLifecycle()
    val topCategory by dashboardViewModel.topCategorySpend.collectAsStateWithLifecycle()
    val isPrivacy by dashboardViewModel.isPrivacyMode.collectAsStateWithLifecycle()

    // 1. Accounts Live Summary
    val totalBalance = accounts.sumOf { it.currentBalance }
    val accountsSummary = if (isPrivacy) "••••" else {
        "${formatCurrency(totalBalance, "৳", strings.isBangla)} · ${accounts.size} ${if (strings.isBangla) "টি ওয়ালেট" else "wallets"}"
    }

    // 2. Budgets Live Summary
    val budgetPercent = summary.budgetPercent.toInt().coerceAtMost(999)
    val safeSpendDaily = if (summary.budgetRemaining > 0) summary.budgetRemaining / 30.0 else 0.0
    val budgetsSummary = if (summary.budgetAmount > 0) {
        "$budgetPercent% ${if (strings.isBangla) "ব্যবহৃত" else "used"} · ${formatCurrency(safeSpendDaily, "৳", strings.isBangla)}/${if (strings.isBangla) "দিন নিরাপদ" else "day safe"}"
    } else {
        if (strings.isBangla) "বাজেট নির্ধারণ করুন" else "Set monthly budget"
    }

    // 3. Bills Live Summary
    val activeBills = recurringBills.filter { it.isActive }
    val monthlyBillsSum = activeBills.sumOf { it.amount }
    val billsSummary = if (activeBills.isNotEmpty()) {
        "${activeBills.size} ${if (strings.isBangla) "টি নিয়মিত বিল" else "active"} · ${formatCurrency(monthlyBillsSum, "৳", strings.isBangla)}/${if (strings.isBangla) "মাস" else "mo"}"
    } else {
        if (strings.isBangla) "বিল যোগ করুন" else "No bills due"
    }

    // 4. Money Owed Live Summary
    val netDebt = debtSummary.totalReceivable - debtSummary.totalPayable
    val netDebtSign = if (netDebt >= 0) "+" else "−"
    val debtSummaryStr = if (isPrivacy) "••••" else {
        "${if (strings.isBangla) "নিট" else "Net"} $netDebtSign${formatCurrency(kotlin.math.abs(netDebt), "৳", strings.isBangla)}"
    }

    // 5. Insights Live Summary
    val insightsSummary = if (topCategory != null) {
        "${if (strings.isBangla) "শীর্ষ" else "Top"}: ${topCategory?.categoryName ?: ""} (${topCategory?.percentage?.toInt() ?: 0}%)"
    } else {
        if (strings.isBangla) "মাসিক ব্যয় বিশ্লেষণ" else "Monthly spending charts"
    }

    // 6. Reports Live Summary
    val reportsSummary = if (strings.isBangla) "পিডিএফ ও সিএসভি এক্সপোর্ট" else "Export PDF / CSV statements"

    val hubItems = listOf(
        MoneyHubItem(strings.navAccounts, accountsSummary, Icons.Filled.AccountBalance, NavigationItem.ACCOUNTS),
        MoneyHubItem(strings.navBudgets, budgetsSummary, Icons.Filled.AccountBalanceWallet, NavigationItem.BUDGETS),
        MoneyHubItem(strings.navBills, billsSummary, Icons.Filled.Payments, NavigationItem.BILLS),
        MoneyHubItem(strings.navReceivables, debtSummaryStr, Icons.AutoMirrored.Filled.AssignmentReturn, NavigationItem.RECEIVABLES),
        MoneyHubItem(strings.navAnalytics, insightsSummary, Icons.Filled.PieChart, NavigationItem.ANALYTICS),
        MoneyHubItem(strings.navReports, reportsSummary, Icons.Filled.Assessment, NavigationItem.REPORTS)
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        MoneyHubHeader(onNavigateToSettings = onNavigateToSettings)

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(start = Space20, end = Space20, top = 4.dp, bottom = bottomPadding),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(hubItems.size) { index ->
                val item = hubItems[index]
                MoneyHubCard(
                    item = item,
                    onClick = { onNavigate(item.destination) }
                )
            }
        }
    }
}
