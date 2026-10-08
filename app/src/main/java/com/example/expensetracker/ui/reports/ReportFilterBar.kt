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
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorDangerExpense
import com.example.expensetracker.theme.ColorSuccessIncome
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.ui.components.DropdownFilterChip
import com.example.expensetracker.ui.components.Strings
import com.example.expensetracker.ui.components.rememberAppHaptics

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
                activeColor = ColorAccent,
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
                                color = if (isSelected) ColorAccent else ColorTextPrimary
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
                                Icon(Icons.Default.Check, null, tint = ColorAccent)
                            }
                        }
                    )
                }
            }
        }

        // Account Filter Dropdown
        Box {
            val activeAccName = accounts.find { it.id == selectedAccountId }?.name ?: strings.allAccounts
            DropdownFilterChip(
                text = activeAccName,
                isActive = selectedAccountId != null,
                icon = Icons.Outlined.AccountBalance,
                activeColor = Color(0xFF3B8FF0),
                onClick = {
                    haptics.lightTick()
                    showAccountMenu = true
                }
            )
            DropdownMenu(
                expanded = showAccountMenu,
                onDismissRequest = { showAccountMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text(strings.allAccounts) },
                    onClick = {
                        haptics.lightTick()
                        onSelectAccount(null)
                        showAccountMenu = false
                    },
                    leadingIcon = {
                        if (selectedAccountId == null) Icon(Icons.Default.Check, null, tint = ColorAccent)
                    }
                )
                accounts.forEach { acc ->
                    DropdownMenuItem(
                        text = { Text(acc.name) },
                        onClick = {
                            haptics.lightTick()
                            onSelectAccount(acc.id)
                            showAccountMenu = false
                        },
                        leadingIcon = {
                            if (selectedAccountId == acc.id) Icon(Icons.Default.Check, null, tint = ColorAccent)
                        }
                    )
                }
            }
        }

        // Type Filter Dropdown
        Box {
            val activeTypeName = when (selectedType) {
                TransactionType.EXPENSE -> strings.onlyExpenses
                TransactionType.INCOME -> strings.onlyIncomes
                null -> strings.allTypes
            }
            val typeColor = when (selectedType) {
                TransactionType.EXPENSE -> ColorDangerExpense
                TransactionType.INCOME -> ColorSuccessIncome
                else -> ColorAccent
            }
            DropdownFilterChip(
                text = activeTypeName,
                isActive = selectedType != null,
                icon = Icons.Outlined.FilterList,
                activeColor = typeColor,
                onClick = {
                    haptics.lightTick()
                    showTypeMenu = true
                }
            )
            DropdownMenu(
                expanded = showTypeMenu,
                onDismissRequest = { showTypeMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text(strings.allTypes) },
                    onClick = {
                        haptics.lightTick()
                        onSelectType(null)
                        showTypeMenu = false
                    },
                    leadingIcon = {
                        if (selectedType == null) Icon(Icons.Default.Check, null, tint = ColorAccent)
                    }
                )
                DropdownMenuItem(
                    text = { Text(strings.onlyExpenses) },
                    onClick = {
                        haptics.lightTick()
                        onSelectType(TransactionType.EXPENSE)
                        showTypeMenu = false
                    },
                    leadingIcon = {
                        if (selectedType == TransactionType.EXPENSE) Icon(Icons.Default.Check, null, tint = ColorDangerExpense)
                    }
                )
                DropdownMenuItem(
                    text = { Text(strings.onlyIncomes) },
                    onClick = {
                        haptics.lightTick()
                        onSelectType(TransactionType.INCOME)
                        showTypeMenu = false
                    },
                    leadingIcon = {
                        if (selectedType == TransactionType.INCOME) Icon(Icons.Default.Check, null, tint = ColorSuccessIncome)
                    }
                )
            }
        }
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
