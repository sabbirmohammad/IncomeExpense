package com.example.expensetracker.ui.reports

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.ui.Modifier
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.AppHaptics
import com.example.expensetracker.ui.components.DropdownFilterChip
import com.example.expensetracker.ui.components.Strings

@Composable
fun ReportTypeFilterChip(
    selectedType: TransactionType?,
    strings: Strings,
    onSelectType: (TransactionType?) -> Unit,
    haptics: AppHaptics,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    var showTypeMenu by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        val activeTypeName = when (selectedType) {
            TransactionType.EXPENSE -> strings.onlyExpenses
            TransactionType.INCOME -> strings.onlyIncomes
            null -> strings.allTypes
        }
        val typeColor = when (selectedType) {
            TransactionType.EXPENSE -> appColors.expense
            TransactionType.INCOME -> appColors.income
            else -> appColors.accent
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
                    if (selectedType == null) Icon(Icons.Default.Check, null, tint = appColors.accent)
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
                    if (selectedType == TransactionType.EXPENSE) Icon(Icons.Default.Check, null, tint = appColors.expense)
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
                    if (selectedType == TransactionType.INCOME) Icon(Icons.Default.Check, null, tint = appColors.income)
                }
            )
        }
    }
}
