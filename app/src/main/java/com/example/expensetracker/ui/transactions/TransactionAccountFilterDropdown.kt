package com.example.expensetracker.ui.transactions

import androidx.compose.foundation.background
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
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.DropdownFilterChip
import com.example.expensetracker.ui.components.Strings
import com.example.expensetracker.ui.components.rememberAppHaptics

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
