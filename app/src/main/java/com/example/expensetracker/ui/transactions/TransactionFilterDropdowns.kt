package com.example.expensetracker.ui.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
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
import com.example.expensetracker.data.model.TimeFilter
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorDangerExpense
import com.example.expensetracker.theme.ColorSuccessIncome
import com.example.expensetracker.theme.ColorSurface
import com.example.expensetracker.theme.ColorTextPrimary
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
    var showTypeMenu by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        val typeInfo = when {
            isMarketTripsView -> FilterTypeInfo(strings.marketTripsFilter, Icons.Outlined.ShoppingCart, Color(0xFF2EC4B6), true)
            selectedType == TransactionType.EXPENSE -> FilterTypeInfo(strings.expensesFilter, Icons.Outlined.ArrowDownward, ColorDangerExpense, true)
            selectedType == TransactionType.INCOME -> FilterTypeInfo(strings.incomesFilter, Icons.Outlined.ArrowUpward, ColorSuccessIncome, true)
            else -> FilterTypeInfo(if (strings.isBangla) "সকল ধরন" else "All Types", Icons.Outlined.FilterList, ColorAccent, false)
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
            modifier = Modifier.background(ColorSurface)
        ) {
            DropdownMenuItem(
                text = { Text(if (strings.isBangla) "সব লেনদেন" else "All Transactions") },
                leadingIcon = { Icon(Icons.Outlined.ReceiptLong, contentDescription = null, tint = ColorTextPrimary) },
                onClick = {
                    haptics.lightTick()
                    onMarketTripsViewChanged(false)
                    onTypeFilterChanged(null)
                    showTypeMenu = false
                }
            )
            DropdownMenuItem(
                text = { Text(strings.expensesFilter, color = ColorDangerExpense) },
                leadingIcon = { Icon(Icons.Outlined.ArrowDownward, contentDescription = null, tint = ColorDangerExpense) },
                onClick = {
                    haptics.lightTick()
                    onMarketTripsViewChanged(false)
                    onTypeFilterChanged(TransactionType.EXPENSE)
                    showTypeMenu = false
                }
            )
            DropdownMenuItem(
                text = { Text(strings.incomesFilter, color = ColorSuccessIncome) },
                leadingIcon = { Icon(Icons.Outlined.ArrowUpward, contentDescription = null, tint = ColorSuccessIncome) },
                onClick = {
                    haptics.lightTick()
                    onMarketTripsViewChanged(false)
                    onTypeFilterChanged(TransactionType.INCOME)
                    showTypeMenu = false
                }
            )
            DropdownMenuItem(
                text = { Text(if (strings.isBangla) "বাজারের ফর্দ ও হিস্ট্রি" else "Market Trips", color = Color(0xFF2EC4B6)) },
                leadingIcon = { Icon(Icons.Outlined.ShoppingCart, contentDescription = null, tint = Color(0xFF2EC4B6)) },
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
    var showTimeMenu by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        val (timeText, isTimeActive) = when (selectedTimeFilter) {
            TimeFilter.ALL -> Pair(strings.allTime, false)
            TimeFilter.THIS_MONTH -> Pair(strings.thisMonth, true)
            TimeFilter.THIS_WEEK -> Pair(strings.thisWeek, true)
            TimeFilter.TODAY -> Pair(strings.today, true)
            TimeFilter.THIS_YEAR -> Pair(strings.thisYear, true)
            TimeFilter.CUSTOM -> Pair(strings.customDateRange, true)
        }

        DropdownFilterChip(
            text = timeText,
            isActive = isTimeActive,
            icon = Icons.Outlined.CalendarToday,
            activeColor = ColorAccent,
            onClick = {
                haptics.lightTick()
                showTimeMenu = true
            }
        )

        DropdownMenu(
            expanded = showTimeMenu,
            onDismissRequest = { showTimeMenu = false },
            modifier = Modifier.background(ColorSurface)
        ) {
            DropdownMenuItem(
                text = { Text(strings.allTime) },
                onClick = {
                    haptics.lightTick()
                    onTimeFilterChanged(TimeFilter.ALL)
                    showTimeMenu = false
                }
            )
            DropdownMenuItem(
                text = { Text(strings.thisMonth) },
                onClick = {
                    haptics.lightTick()
                    onTimeFilterChanged(TimeFilter.THIS_MONTH)
                    showTimeMenu = false
                }
            )
            DropdownMenuItem(
                text = { Text(strings.thisWeek) },
                onClick = {
                    haptics.lightTick()
                    onTimeFilterChanged(TimeFilter.THIS_WEEK)
                    showTimeMenu = false
                }
            )
            DropdownMenuItem(
                text = { Text(strings.today) },
                onClick = {
                    haptics.lightTick()
                    onTimeFilterChanged(TimeFilter.TODAY)
                    showTimeMenu = false
                }
            )
            DropdownMenuItem(
                text = { Text(strings.thisYear) },
                onClick = {
                    haptics.lightTick()
                    onTimeFilterChanged(TimeFilter.THIS_YEAR)
                    showTimeMenu = false
                }
            )
            DropdownMenuItem(
                text = { Text(strings.customDateRange) },
                onClick = {
                    haptics.lightTick()
                    showTimeMenu = false
                    onOpenCustomDateRange()
                }
            )
        }
    }
}
