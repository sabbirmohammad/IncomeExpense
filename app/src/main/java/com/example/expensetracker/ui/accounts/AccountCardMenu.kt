package com.example.expensetracker.ui.accounts

import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.LocalAppStrings

@Composable
fun AccountCardMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onClick: () -> Unit,
    onReconcile: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val strings = LocalAppStrings.current
    val appColors = LocalAppColors.current
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = Modifier.background(appColors.surface)
    ) {
        DropdownMenuItem(
            text = { Text(if (strings.isBangla) "কার্ড ও ইতিহাস দেখুন" else "View Card & History", color = appColors.textPrimary) },
            leadingIcon = { Icon(Icons.Outlined.History, null, tint = appColors.textSecondary) },
            onClick = {
                onDismissRequest()
                onClick()
            }
        )
        DropdownMenuItem(
            text = { Text(strings.reconcileBalance, color = appColors.accent) },
            leadingIcon = { Icon(Icons.Outlined.AccountBalance, null, tint = appColors.accent) },
            onClick = {
                onDismissRequest()
                onReconcile()
            }
        )
        DropdownMenuItem(
            text = { Text(strings.editAccount, color = appColors.textPrimary) },
            leadingIcon = { Icon(Icons.Outlined.Edit, null, tint = appColors.textSecondary) },
            onClick = {
                onDismissRequest()
                onEdit()
            }
        )
        DropdownMenuItem(
            text = { Text(strings.delete, color = appColors.dangerExpense) },
            leadingIcon = { Icon(Icons.Outlined.Delete, null, tint = appColors.dangerExpense) },
            onClick = {
                onDismissRequest()
                onDelete()
            }
        )
    }
}
