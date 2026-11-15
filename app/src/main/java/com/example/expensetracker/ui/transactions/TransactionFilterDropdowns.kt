package com.example.expensetracker.ui.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.expensetracker.data.model.TimeFilter
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.DropdownFilterChip
import com.example.expensetracker.ui.components.Strings
import com.example.expensetracker.ui.components.rememberAppHaptics

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
