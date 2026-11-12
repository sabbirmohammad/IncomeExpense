package com.example.expensetracker.ui.transactions

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.data.model.TimeFilter
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.data.model.getLocalizedCategoryName
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.AppButton
import com.example.expensetracker.ui.components.AppButtonVariant
import com.example.expensetracker.ui.components.AppChip
import com.example.expensetracker.ui.components.AppChipVariant
import com.example.expensetracker.ui.components.HairlineDivider
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.rememberAppHaptics

/**
 * V7 TransactionFilterSheet per UX_FLOWS section 4:
 * - Single filter sheet opened from the "Filters" button
 * - Clean vertical sections for Type, Time Period, Account, and Category
 * - Clear all / Apply actions at bottom
 */
@Composable
fun TransactionFilterSheet(
    selectedType: TransactionType?,
    onTypeSelected: (TransactionType?) -> Unit,
    isMarketTripsView: Boolean,
    onMarketTripsViewChanged: (Boolean) -> Unit,
    selectedTimeFilter: TimeFilter,
    onTimeFilterSelected: (TimeFilter) -> Unit,
    accounts: List<Account>,
    selectedAccountId: Long?,
    onAccountSelected: (Long?) -> Unit,
    categories: List<Category> = emptyList(),
    selectedCategoryId: Long? = null,
    onCategorySelected: (Long?) -> Unit = {},
    onOpenCustomDateRange: () -> Unit,
    onResetAll: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val strings = LocalAppStrings.current
    val haptics = rememberAppHaptics()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Sheet Title & Reset All Action
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = strings.filters,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                color = appColors.textPrimary
            )

            Text(
                text = strings.clearFilters,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = appColors.accent,
                modifier = Modifier.clickable {
                    haptics.lightTick()
                    onResetAll()
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        HairlineDivider()
        Spacer(modifier = Modifier.height(14.dp))

        // 1. Transaction Type Section
        Text(
            text = strings.transactionType,
            style = MaterialTheme.typography.labelMedium.copy(fontSize = 13.sp, fontWeight = FontWeight.SemiBold),
            color = appColors.textSecondary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AppChip(
                label = strings.allTypes,
                selected = selectedType == null && !isMarketTripsView,
                onClick = {
                    onTypeSelected(null)
                    onMarketTripsViewChanged(false)
                }
            )
            AppChip(
                label = strings.expense,
                selected = selectedType == TransactionType.EXPENSE && !isMarketTripsView,
                onClick = {
                    onTypeSelected(TransactionType.EXPENSE)
                    onMarketTripsViewChanged(false)
                }
            )
            AppChip(
                label = strings.income,
                selected = selectedType == TransactionType.INCOME && !isMarketTripsView,
                onClick = {
                    onTypeSelected(TransactionType.INCOME)
                    onMarketTripsViewChanged(false)
                }
            )
            AppChip(
                label = if (strings.isBangla) "বাজার ফর্দসমূহ" else "Market Trips",
                selected = isMarketTripsView,
                onClick = {
                    onMarketTripsViewChanged(true)
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Time Period Section
        Text(
            text = strings.timeFilter,
            style = MaterialTheme.typography.labelMedium.copy(fontSize = 13.sp, fontWeight = FontWeight.SemiBold),
            color = appColors.textSecondary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AppChip(
                label = strings.thisMonth,
                selected = selectedTimeFilter == TimeFilter.THIS_MONTH,
                onClick = { onTimeFilterSelected(TimeFilter.THIS_MONTH) }
            )
            AppChip(
                label = strings.today,
                selected = selectedTimeFilter == TimeFilter.TODAY,
                onClick = { onTimeFilterSelected(TimeFilter.TODAY) }
            )
            AppChip(
                label = strings.thisWeek,
                selected = selectedTimeFilter == TimeFilter.THIS_WEEK,
                onClick = { onTimeFilterSelected(TimeFilter.THIS_WEEK) }
            )
            AppChip(
                label = strings.thisYear,
                selected = selectedTimeFilter == TimeFilter.THIS_YEAR,
                onClick = { onTimeFilterSelected(TimeFilter.THIS_YEAR) }
            )
            AppChip(
                label = strings.allTime,
                selected = selectedTimeFilter == TimeFilter.ALL,
                onClick = { onTimeFilterSelected(TimeFilter.ALL) }
            )
            AppChip(
                label = strings.customRange,
                selected = selectedTimeFilter == TimeFilter.CUSTOM,
                onClick = {
                    onTimeFilterSelected(TimeFilter.CUSTOM)
                    onOpenCustomDateRange()
                }
            )
        }

        // 3. Account Section (if multiple accounts exist)
        if (accounts.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = strings.accountsAndWallets,
                style = MaterialTheme.typography.labelMedium.copy(fontSize = 13.sp, fontWeight = FontWeight.SemiBold),
                color = appColors.textSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AppChip(
                    label = if (strings.isBangla) "সকল ওয়ালেট" else "All Accounts",
                    selected = selectedAccountId == null,
                    onClick = { onAccountSelected(null) }
                )
                accounts.forEach { account ->
                    AppChip(
                        label = account.name,
                        selected = selectedAccountId == account.id,
                        onClick = { onAccountSelected(account.id) }
                    )
                }
            }
        }

        // 4. Category Section (if categories exist)
        if (categories.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = strings.category,
                style = MaterialTheme.typography.labelMedium.copy(fontSize = 13.sp, fontWeight = FontWeight.SemiBold),
                color = appColors.textSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AppChip(
                    label = if (strings.isBangla) "সকল ক্যাটাগরি" else "All Categories",
                    selected = selectedCategoryId == null,
                    onClick = { onCategorySelected(null) }
                )
                categories.forEach { cat ->
                    AppChip(
                        label = getLocalizedCategoryName(cat.name, strings.isBangla),
                        selected = selectedCategoryId == cat.id,
                        onClick = { onCategorySelected(cat.id) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Done button
        AppButton(
            text = if (strings.isBangla) "ফিল্টার প্রয়োগ করুন" else "Apply Filters",
            onClick = onDismiss,
            variant = AppButtonVariant.Primary,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}
