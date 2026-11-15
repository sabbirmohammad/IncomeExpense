package com.example.expensetracker.ui.reports

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.AccountBalance
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
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.AppHaptics
import com.example.expensetracker.ui.components.DropdownFilterChip
import com.example.expensetracker.ui.components.Strings

@Composable
fun ReportAccountFilterChip(
    selectedAccountId: Long?,
    accounts: List<Account>,
    strings: Strings,
    onSelectAccount: (Long?) -> Unit,
    haptics: AppHaptics,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    var showAccountMenu by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
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
                    if (selectedAccountId == null) Icon(Icons.Default.Check, null, tint = appColors.accent)
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
                        if (selectedAccountId == acc.id) Icon(Icons.Default.Check, null, tint = appColors.accent)
                    }
                )
            }
        }
    }
}
