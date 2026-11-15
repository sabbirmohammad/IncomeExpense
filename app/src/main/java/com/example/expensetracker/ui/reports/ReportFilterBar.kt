package com.example.expensetracker.ui.reports

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.ui.components.DropdownFilterChip
import com.example.expensetracker.ui.components.Strings
import com.example.expensetracker.ui.components.rememberAppHaptics
import com.example.expensetracker.theme.LocalAppColors

@Composable
fun ReportFilterBar(
    selectedPeriod: ReportPeriod,
    selectedAccountId: Long?,
    selectedType: TransactionType?,
    accounts: List<Account>,
    strings: Strings,
    onSelectPeriod: (ReportPeriod) -> Unit,
    onSelectAccount: (Long?) -> Unit,
    onSelectType: (TransactionType?) -> Unit,
    onCustomRangeSelected: (Long, Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val haptics = rememberAppHaptics()
    var showPeriodMenu by remember { mutableStateOf(false) }
    var showAccountMenu by remember { mutableStateOf(false) }
    var showTypeMenu by remember { mutableStateOf(false) }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var tempStartDate by remember { mutableStateOf(System.currentTimeMillis()) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Period Filter Dropdown
        Box {
            DropdownFilterChip(
                text = selectedPeriod.getDisplayName(strings.isBangla),
                isActive = selectedPeriod != ReportPeriod.THIS_MONTH,
                icon = Icons.Outlined.CalendarToday,
                activeColor = appColors.accent,
                onClick = {
                    haptics.lightTick()
                    showPeriodMenu = true
                }
            )
            DropdownMenu(
                expanded = showPeriodMenu,
                onDismissRequest = { showPeriodMenu = false }
            ) {
                ReportPeriod.entries.forEach { period ->
                    val isSelected = selectedPeriod == period
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = period.getDisplayName(strings.isBangla),
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) appColors.accent else appColors.textPrimary
                            )
                        },
                        onClick = {
                            haptics.lightTick()
                            showPeriodMenu = false
                            if (period == ReportPeriod.CUSTOM) {
                                showStartDatePicker = true
                            } else {
                                onSelectPeriod(period)
                            }
                        },
                        leadingIcon = {
                            if (isSelected) {
                                Icon(Icons.Default.Check, null, tint = appColors.accent)
                            }
                        }
                    )
                }
            }
        }

        // Account Filter Dropdown
        ReportAccountFilterChip(
            selectedAccountId = selectedAccountId,
            accounts = accounts,
            strings = strings,
            onSelectAccount = onSelectAccount,
            haptics = haptics
        )

        // Type Filter Dropdown
        ReportTypeFilterChip(
            selectedType = selectedType,
            strings = strings,
            onSelectType = onSelectType,
            haptics = haptics
        )
    }

    ReportCustomDateRangeDialogs(
        showStartDatePicker = showStartDatePicker,
        showEndDatePicker = showEndDatePicker,
        tempStartDate = tempStartDate,
        strings = strings,
        onStartDateSelected = { selected ->
            tempStartDate = selected
            showStartDatePicker = false
            showEndDatePicker = true
        },
        onEndDateSelected = { selected ->
            showEndDatePicker = false
            onCustomRangeSelected(tempStartDate, selected)
        },
        onDismissStartDate = { showStartDatePicker = false },
        onDismissEndDate = { showEndDatePicker = false }
    )
}
