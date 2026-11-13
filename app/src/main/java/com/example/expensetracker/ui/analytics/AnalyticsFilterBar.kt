package com.example.expensetracker.ui.analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.expensetracker.data.model.TimeFilter
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.theme.HairlineInkStroke
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.AppChip
import com.example.expensetracker.ui.components.AppChipVariant
import com.example.expensetracker.ui.components.AppHaptics
import com.example.expensetracker.ui.components.DropdownFilterChip
import com.example.expensetracker.ui.components.Strings

@Composable
fun AnalyticsFilterBar(
    transactionType: TransactionType,
    timeFilter: TimeFilter,
    strings: Strings,
    haptics: AppHaptics,
    onTypeChange: (TransactionType) -> Unit,
    onTimeFilterChange: (TimeFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    var showTypeMenu by remember { mutableStateOf(false) }

    val periodList = remember(strings) {
        listOf(
            TimeFilter.TODAY to strings.today,
            TimeFilter.THIS_WEEK to strings.thisWeek,
            TimeFilter.THIS_MONTH to strings.thisMonth,
            TimeFilter.THIS_YEAR to strings.thisYear,
            TimeFilter.ALL to strings.allTime
        )
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 1. Type Dropdown Chip
        Box {
            val isExpense = transactionType == TransactionType.EXPENSE
            val typeText = if (isExpense) strings.expense else strings.income
            val typeIcon = if (isExpense) Icons.Outlined.ArrowDownward else Icons.Outlined.ArrowUpward
            val typeColor = if (isExpense) appColors.expense else appColors.income

            DropdownFilterChip(
                text = typeText,
                isActive = true,
                icon = typeIcon,
                activeColor = typeColor,
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
                    text = {
                        Text(
                            text = strings.expense,
                            color = appColors.expense,
                            fontWeight = if (isExpense) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.ArrowDownward,
                            contentDescription = null,
                            tint = appColors.expense,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    onClick = {
                        haptics.lightTick()
                        onTypeChange(TransactionType.EXPENSE)
                        showTypeMenu = false
                    }
                )
                DropdownMenuItem(
                    text = {
                        Text(
                            text = strings.income,
                            color = appColors.income,
                            fontWeight = if (!isExpense) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.ArrowUpward,
                            contentDescription = null,
                            tint = appColors.income,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    onClick = {
                        haptics.lightTick()
                        onTypeChange(TransactionType.INCOME)
                        showTypeMenu = false
                    }
                )
            }
        }

        VerticalDivider(
            modifier = Modifier
                .height(24.dp)
                .width(HairlineInkStroke),
            color = appColors.border
        )

        // 2. Direct Period Chips: Today, Week, Month, Year, All
        periodList.forEach { (filter, label) ->
            AppChip(
                label = label,
                selected = timeFilter == filter,
                onClick = { onTimeFilterChange(filter) },
                variant = AppChipVariant.Choice
            )
        }
    }
}
