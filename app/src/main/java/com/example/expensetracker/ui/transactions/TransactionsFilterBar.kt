package com.example.expensetracker.ui.transactions

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.TimeFilter
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.Strings
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.rememberAppHaptics

@Composable
fun TransactionsFilterBar(
    searchQuery: String, onSearchQueryChanged: (String) -> Unit,
    isSearchActive: Boolean, onSearchActiveChanged: (Boolean) -> Unit,
    selectedType: TransactionType?, onTypeFilterChanged: (TransactionType?) -> Unit,
    isMarketTripsView: Boolean, onMarketTripsViewChanged: (Boolean) -> Unit,
    selectedTimeFilter: TimeFilter, onTimeFilterChanged: (TimeFilter) -> Unit,
    accounts: List<Account> = emptyList(), selectedAccountId: Long? = null,
    onAccountFilterChanged: (Long?) -> Unit = {}, onOpenCustomDateRange: () -> Unit = {},
    onResetFilters: () -> Unit = {}, activeFilterCount: Int = 0, strings: Strings,
    onOpenFilterSheet: (() -> Unit)? = null, modifier: Modifier = Modifier
) {
    val haptics = rememberAppHaptics()
    val appColors = LocalAppColors.current

    Column(modifier = modifier.fillMaxWidth()) {
        TransactionsSearchBar(
            searchQuery = searchQuery,
            onSearchQueryChanged = onSearchQueryChanged,
            isSearchActive = isSearchActive,
            onSearchActiveChanged = onSearchActiveChanged,
            strings = strings
        )

        if (!isSearchActive && searchQuery.isBlank()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = appColors.surface,
                    border = BorderStroke(1.dp, appColors.border),
                    modifier = Modifier.minimumInteractiveComponentSize().clickable {
                        haptics.lightTick()
                        onSearchActiveChanged(true)
                    }
                ) {
                    Box(modifier = Modifier.padding(horizontal = 11.dp, vertical = 8.dp), contentAlignment = Alignment.Center) {
                        Icon(imageVector = Icons.Outlined.Search, contentDescription = "Search", tint = appColors.textPrimary, modifier = Modifier.size(18.dp))
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (activeFilterCount > 0) appColors.accentContainer else appColors.surface,
                    border = BorderStroke(1.dp, if (activeFilterCount > 0) appColors.accent.copy(alpha = 0.5f) else appColors.border),
                    modifier = Modifier.minimumInteractiveComponentSize().clickable { haptics.lightTick(); onOpenFilterSheet?.invoke() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(imageVector = Icons.Outlined.FilterList, contentDescription = "Filters", tint = if (activeFilterCount > 0) appColors.accent else appColors.textPrimary, modifier = Modifier.size(18.dp))
                        Text(text = strings.filters, style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold), color = if (activeFilterCount > 0) appColors.accent else appColors.textPrimary)
                        if (activeFilterCount > 0) {
                            Box(modifier = Modifier.size(18.dp).clip(CircleShape).background(appColors.accent), contentAlignment = Alignment.Center) {
                                val countStr = if (strings.isBangla) convertToBengaliDigits(activeFilterCount.toString()) else activeFilterCount.toString()
                                Text(text = countStr, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = appColors.onAccent)
                            }
                        }
                    }
                }

                if (selectedType != null && !isMarketTripsView) {
                    RemovableFilterChip(if (selectedType == TransactionType.EXPENSE) strings.expense else strings.income, { onTypeFilterChanged(null) })
                }

                if (isMarketTripsView) {
                    RemovableFilterChip(if (strings.isBangla) "বাজার ফর্দ" else "Market Trips", { onMarketTripsViewChanged(false) })
                }

                if (selectedTimeFilter != TimeFilter.THIS_MONTH) {
                    val timeLabel = when (selectedTimeFilter) {
                        TimeFilter.TODAY -> strings.today
                        TimeFilter.THIS_WEEK -> strings.thisWeek
                        TimeFilter.THIS_YEAR -> strings.thisYear
                        TimeFilter.ALL -> strings.allTime
                        TimeFilter.CUSTOM -> strings.customRange
                        else -> strings.thisMonth
                    }
                    RemovableFilterChip(timeLabel, { onTimeFilterChanged(TimeFilter.THIS_MONTH) })
                }

                if (selectedAccountId != null) {
                    val accountName = accounts.find { it.id == selectedAccountId }?.name ?: strings.navAccounts
                    RemovableFilterChip(accountName, { onAccountFilterChanged(null) })
                }

                if (activeFilterCount > 0) {
                    Text(
                        text = strings.clearFilters,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.5.sp, fontWeight = FontWeight.Medium),
                        color = appColors.accent,
                        modifier = Modifier.clickable { haptics.lightTick(); onResetFilters() }.padding(horizontal = 6.dp, vertical = 8.dp)
                    )
                }

                if (false) {
                    TransactionTypeFilterDropdown(
                        selectedType = selectedType,
                        onTypeFilterChanged = onTypeFilterChanged,
                        isMarketTripsView = isMarketTripsView,
                        onMarketTripsViewChanged = onMarketTripsViewChanged,
                        strings = strings
                    )
                }
            }
        }
    }
}

@Composable
fun RemovableFilterChip(
    label: String,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = appColors.surfaceRaised,
        border = BorderStroke(0.5.dp, appColors.border),
        modifier = modifier.minimumInteractiveComponentSize().clickable { onRemove() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = label, style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp, fontWeight = FontWeight.Medium), color = appColors.textPrimary)
            Icon(imageVector = Icons.Outlined.Close, contentDescription = "Remove filter", tint = appColors.textSecondary, modifier = Modifier.size(14.dp))
        }
    }
}
