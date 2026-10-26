package com.example.expensetracker.ui.analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.TimeFilter
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.AppCard
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.SpendingBarChart
import com.example.expensetracker.ui.components.rememberAppHaptics
import com.example.expensetracker.ui.share.ShareExpenseSheet
import com.example.expensetracker.ui.share.SharePeriodType

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel,
    onNavigateToCategoryTransactions: (Long) -> Unit = {},
    onNavigateToSettings: (() -> Unit)? = null,
    onNavigateBack: (() -> Unit)? = null,
    onMenuClick: (() -> Unit)? = null
) {
    val appColors = LocalAppColors.current
    val strings = LocalAppStrings.current
    val haptics = rememberAppHaptics()
    val categorySpends by viewModel.categorySpends.collectAsStateWithLifecycle()
    val paymentMethodSpends by viewModel.paymentMethodSpends.collectAsStateWithLifecycle()
    val dailySpends by viewModel.dailySpends.collectAsStateWithLifecycle()
    val summary by viewModel.summary.collectAsStateWithLifecycle()
    val overview by viewModel.overview.collectAsStateWithLifecycle()
    val breakdownMode by viewModel.breakdownMode.collectAsStateWithLifecycle()
    val timeFilter by viewModel.timeFilter.collectAsStateWithLifecycle()
    val transactionType by viewModel.transactionType.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()

    val totalAmount = if (transactionType == TransactionType.EXPENSE) summary.totalExpense else summary.totalIncome

    var showShareSheet by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalAppColors.current.background)
            .padding(horizontal = 16.dp)
    ) {
        AnalyticsTopBar(
            title = strings.analyticsTitle,
            onMenuClick = onMenuClick,
            onNavigateBack = onNavigateBack,
            onShareClick = { showShareSheet = true },
            onNavigateToSettings = onNavigateToSettings
        )

        AnalyticsFilterBar(
            transactionType = transactionType,
            timeFilter = timeFilter,
            strings = strings,
            haptics = haptics,
            onTypeChange = { viewModel.setTransactionType(it) },
            onTimeFilterChange = { viewModel.setTimeFilter(it) }
        )

        Spacer(modifier = Modifier.height(4.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                AnalyticsKpiStrip(
                    overview = overview,
                    userProfile = userProfile,
                    strings = strings
                )
            }

            item {
                AnalyticsBreakdownToggle(
                    breakdownMode = breakdownMode,
                    strings = strings,
                    onModeChange = { viewModel.setBreakdownMode(it) }
                )
            }

            if (breakdownMode == AnalyticsBreakdownMode.CATEGORIES) {
                item {
                    CategoryDonutChartCard(
                        categorySpends = categorySpends,
                        currencySymbol = userProfile.currencySymbol,
                        totalAmount = totalAmount,
                        transactionType = transactionType,
                        strings = strings,
                        onCategoryClick = onNavigateToCategoryTransactions
                    )
                }

                if (categorySpends.isNotEmpty() && totalAmount > 0.0) {
                    item {
                        CategoryRankingCard(
                            categorySpends = categorySpends,
                            userProfile = userProfile,
                            transactionType = transactionType,
                            strings = strings,
                            onCategoryClick = onNavigateToCategoryTransactions
                        )
                    }
                }
            } else {
                item {
                    PaymentMethodDonutChartCard(
                        spends = paymentMethodSpends,
                        currencySymbol = userProfile.currencySymbol,
                        totalAmount = totalAmount,
                        transactionType = transactionType,
                        strings = strings
                    )
                }

                if (paymentMethodSpends.isNotEmpty() && totalAmount > 0.0) {
                    item {
                        PaymentMethodRankingCard(
                            spends = paymentMethodSpends,
                            userProfile = userProfile,
                            strings = strings
                        )
                    }
                }
            }

            item {
                AppCard {
                    Column {
                        Text(
                            text = if (strings.isBangla) "আয় বনাম ব্যয়ের ধারা" else "Income vs expense trend",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            color = appColors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        SpendingBarChart(
                            dailySpends = dailySpends,
                            currencySymbol = userProfile.currencySymbol
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(84.dp))
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
