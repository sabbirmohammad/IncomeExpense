package com.example.expensetracker.ui.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.TimeFilter
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.DropdownFilterChip
import com.example.expensetracker.ui.components.Strings
import com.example.expensetracker.ui.components.rememberAppHaptics

private data class FilterTypeInfo(
    val text: String,
    val icon: ImageVector,
    val color: Color,
    val isActive: Boolean
)

@Composable
fun TransactionTypeFilterDropdown(
    selectedType: TransactionType?,
    isMarketTripsView: Boolean,
    onTypeFilterChanged: (TransactionType?) -> Unit,
    onMarketTripsViewChanged: (Boolean) -> Unit,
    strings: Strings,
    modifier: Modifier = Modifier
) {
    val haptics = rememberAppHaptics()
    val appColors = LocalAppColors.current
    val marketFilterColor = if (appColors.isDark) Color(0xFF2EC4B6) else Color(0xFF0F766E)
    var showTypeMenu by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        val typeInfo = when {
            isMarketTripsView -> FilterTypeInfo(strings.marketTripsFilter, Icons.Outlined.ShoppingCart, marketFilterColor, true)
            selectedType == TransactionType.EXPENSE -> FilterTypeInfo(strings.expensesFilter, Icons.Outlined.ArrowDownward, appColors.expense, true)
            selectedType == TransactionType.INCOME -> FilterTypeInfo(strings.incomesFilter, Icons.Outlined.ArrowUpward, appColors.income, true)
            else -> FilterTypeInfo(if (strings.isBangla) "সকল ধরন" else "All Types", Icons.Outlined.FilterList, appColors.accent, false)
        }

        DropdownFilterChip(
            text = typeInfo.text,
            isActive = typeInfo.isActive,
            icon = typeInfo.icon,
            activeColor = typeInfo.color,
            onClick = {
                haptics.lightTick()
                showTypeMenu = true
            }
        )

        DropdownMenu(
            expanded = showTypeMenu,
            onDismissRequest = { showTypeMenu = false },
            modifier = Modifier.background(appColors.surface)
        ) {
            DropdownMenuItem(
                text = { Text(if (strings.isBangla) "সব লেনদেন" else "All Transactions", color = appColors.textPrimary) },
                leadingIcon = { Icon(Icons.Outlined.ReceiptLong, contentDescription = null, tint = appColors.textPrimary) },
                onClick = {
                    haptics.lightTick()
                    onMarketTripsViewChanged(false)
                    onTypeFilterChanged(null)
                    showTypeMenu = false
                }
            )
            DropdownMenuItem(
                text = { Text(strings.expensesFilter, color = appColors.expense) },
                leadingIcon = { Icon(Icons.Outlined.ArrowDownward, contentDescription = null, tint = appColors.expense) },
                onClick = {
                    haptics.lightTick()
                    onMarketTripsViewChanged(false)
                    onTypeFilterChanged(TransactionType.EXPENSE)
                    showTypeMenu = false
                }
            )
            DropdownMenuItem(
                text = { Text(strings.incomesFilter, color = appColors.income) },
                leadingIcon = { Icon(Icons.Outlined.ArrowUpward, contentDescription = null, tint = appColors.income) },
                onClick = {
                    haptics.lightTick()
                    onMarketTripsViewChanged(false)
                    onTypeFilterChanged(TransactionType.INCOME)
                    showTypeMenu = false
                }
            )
            DropdownMenuItem(
                text = { Text(if (strings.isBangla) "বাজারের ফর্দ ও হিস্ট্রি" else "Market Trips", color = marketFilterColor) },
                leadingIcon = { Icon(Icons.Outlined.ShoppingCart, contentDescription = null, tint = marketFilterColor) },
                onClick = {
                    haptics.lightTick()
                    onMarketTripsViewChanged(true)
                    showTypeMenu = false
                }
            )
        }
    }
}

@Composable
fun TransactionTimeFilterDropdown(
    selectedTimeFilter: TimeFilter,
    onTimeFilterChanged: (TimeFilter) -> Unit,
    onOpenCustomDateRange: () -> Unit,
    strings: Strings,
    modifier: Modifier = Modifier
) {
    val haptics = rememberAppHaptics()
    val appColors = LocalAppColors.current
    var showTimeMenu by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        val (timeText, isTimeActive) = when (selectedTimeFilter) {
            TimeFilter.THIS_MONTH -> Pair(strings.thisMonth, false)
            TimeFilter.ALL -> Pair(strings.allTime, true)
            TimeFilter.THIS_WEEK -> Pair(strings.thisWeek, true)
            TimeFilter.TODAY -> Pair(strings.today, true)
            TimeFilter.THIS_YEAR -> Pair(strings.thisYear, true)
            TimeFilter.CUSTOM -> Pair(strings.customDateRange, true)
        }

        DropdownFilterChip(
            text = timeText,
            isActive = isTimeActive,
            icon = Icons.Outlined.CalendarToday,
            activeColor = appColors.accent,
            onClick = {
                haptics.lightTick()
                showTimeMenu = true
            }
        )

        DropdownMenu(
            expanded = showTimeMenu,
            onDismissRequest = { showTimeMenu = false },
            modifier = Modifier.background(appColors.surface)
        ) {
            DropdownMenuItem(
                text = { Text(strings.thisMonth, color = if (selectedTimeFilter == TimeFilter.THIS_MONTH) appColors.accent else appColors.textPrimary) },
                onClick = {
                    haptics.lightTick()
                    onTimeFilterChanged(TimeFilter.THIS_MONTH)
                    showTimeMenu = false
                }
            )
            DropdownMenuItem(
                text = { Text(strings.allTime, color = if (selectedTimeFilter == TimeFilter.ALL) appColors.accent else appColors.textPrimary) },
                onClick = {
                    haptics.lightTick()
                    onTimeFilterChanged(TimeFilter.ALL)
                    showTimeMenu = false
                }
            )
            DropdownMenuItem(
                text = { Text(strings.thisWeek, color = if (selectedTimeFilter == TimeFilter.THIS_WEEK) appColors.accent else appColors.textPrimary) },
                onClick = {
                    haptics.lightTick()
                    onTimeFilterChanged(TimeFilter.THIS_WEEK)
                    showTimeMenu = false
                }
            )
            DropdownMenuItem(
                text = { Text(strings.today, color = if (selectedTimeFilter == TimeFilter.TODAY) appColors.accent else appColors.textPrimary) },
                onClick = {
                    haptics.lightTick()
                    onTimeFilterChanged(TimeFilter.TODAY)
                    showTimeMenu = false
                }
            )
            DropdownMenuItem(
                text = { Text(strings.thisYear, color = if (selectedTimeFilter == TimeFilter.THIS_YEAR) appColors.accent else appColors.textPrimary) },
                onClick = {
                    haptics.lightTick()
                    onTimeFilterChanged(TimeFilter.THIS_YEAR)
                    showTimeMenu = false
                }
            )
            DropdownMenuItem(
                text = { Text(strings.customDateRange, color = if (selectedTimeFilter == TimeFilter.CUSTOM) appColors.accent else appColors.textPrimary) },
                onClick = {
                    haptics.lightTick()
                    showTimeMenu = false
                    onOpenCustomDateRange()
                }
            )
        }
    }
}

@Composable
fun TransactionAccountFilterDropdown(
    accounts: List<Account>,
    selectedAccountId: Long?,
    onAccountFilterChanged: (Long?) -> Unit,
    strings: Strings,
    modifier: Modifier = Modifier
) {
    val haptics = rememberAppHaptics()
    val appColors = LocalAppColors.current
    var showAccountMenu by remember { mutableStateOf(false) }

    val activeAccName = accounts.find { it.id == selectedAccountId }?.name ?: strings.allAccounts
    val isActive = selectedAccountId != null

    Box(modifier = modifier) {
        DropdownFilterChip(
            text = activeAccName,
            isActive = isActive,
            icon = Icons.Outlined.AccountBalance,
            activeColor = appColors.info,
            onClick = {
                haptics.lightTick()
                showAccountMenu = true
            }
        )

        DropdownMenu(
            expanded = showAccountMenu,
            onDismissRequest = { showAccountMenu = false },
            modifier = Modifier.background(appColors.surface)
        ) {
            DropdownMenuItem(
                text = { Text(strings.allAccounts, color = if (selectedAccountId == null) appColors.accent else appColors.textPrimary) },
                leadingIcon = {
                    if (selectedAccountId == null) Icon(Icons.Default.Check, null, tint = appColors.accent)
                },
                onClick = {
                    haptics.lightTick()
                    onAccountFilterChanged(null)
                    showAccountMenu = false
                }
            )
            accounts.forEach { acc ->
                DropdownMenuItem(
                    text = { Text(acc.name, color = if (selectedAccountId == acc.id) appColors.accent else appColors.textPrimary) },
                    leadingIcon = {
                        if (selectedAccountId == acc.id) Icon(Icons.Default.Check, null, tint = appColors.accent)
                    },
                    onClick = {
                        haptics.lightTick()
                        onAccountFilterChanged(acc.id)
                        showAccountMenu = false
                    }
                )
            }
        }
    }
}
