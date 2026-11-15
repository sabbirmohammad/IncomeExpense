package com.example.expensetracker.ui.dashboard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.DailySpend
import com.example.expensetracker.data.model.DebtBookSummary
import com.example.expensetracker.data.model.FinancialSummary
import com.example.expensetracker.data.model.MarketListWithItems
import com.example.expensetracker.data.model.ReceivablesSummary
import com.example.expensetracker.data.model.RecurringBill
import com.example.expensetracker.data.model.SavingsGoal
import com.example.expensetracker.data.model.TimeFilter
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.UserProfile

internal class DashboardScreenState(
    val summary: FinancialSummary,
    val transactions: List<Transaction>,
    val userProfile: UserProfile,
    val timeFilter: TimeFilter,
    val accounts: List<Account>,
    val receivablesSummary: ReceivablesSummary,
    val debtBookSummary: DebtBookSummary,
    val savingsGoals: List<SavingsGoal>,
    val recurringBills: List<RecurringBill>,
    val activeMarketList: MarketListWithItems?,
    val activeMarketLists: List<MarketListWithItems>,
    val recentDailySpends: List<DailySpend>,
    val isPrivacyMode: Boolean
)

@Composable
internal fun rememberDashboardScreenState(viewModel: DashboardViewModel): DashboardScreenState {
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
    val isPrivacyMode by viewModel.isPrivacyMode.collectAsStateWithLifecycle()

    return DashboardScreenState(
        summary = summary,
        transactions = transactions,
        userProfile = userProfile,
        timeFilter = timeFilter,
        accounts = accounts,
        receivablesSummary = receivablesSummary,
        debtBookSummary = debtBookSummary,
        savingsGoals = savingsGoals,
        recurringBills = recurringBills,
        activeMarketList = activeMarketList,
        activeMarketLists = activeMarketLists,
        recentDailySpends = recentDailySpends,
        isPrivacyMode = isPrivacyMode
    )
}
