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
import androidx.compose.material3.minimumInteractiveComponentSize
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
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val appColors = LocalAppColors.current
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = modifier.background(appColors.surface)
    ) {
        DropdownMenuItem(
            text = { Text(if (strings.isBangla) "কার্ড ও ইতিহাস দেখুন" else "View Card & History", color = appColors.textPrimary) },
            leadingIcon = { Icon(Icons.Outlined.History, contentDescription = "History", tint = appColors.textSecondary) },
            onClick = {
                onDismissRequest()
                onClick()
            },
            modifier = Modifier.minimumInteractiveComponentSize()
        )
        DropdownMenuItem(
            text = { Text(strings.reconcileBalance, color = appColors.accent) },
            leadingIcon = { Icon(Icons.Outlined.AccountBalance, contentDescription = strings.reconcileBalance, tint = appColors.accent) },
            onClick = {
                onDismissRequest()
                onReconcile()
            },
            modifier = Modifier.minimumInteractiveComponentSize()
        )
        DropdownMenuItem(
            text = { Text(strings.editAccount, color = appColors.textPrimary) },
            leadingIcon = { Icon(Icons.Outlined.Edit, contentDescription = strings.editAccount, tint = appColors.textSecondary) },
            onClick = {
                onDismissRequest()
                onEdit()
            },
            modifier = Modifier.minimumInteractiveComponentSize()
        )
        DropdownMenuItem(
            text = { Text(strings.delete, color = appColors.dangerExpense) },
            leadingIcon = { Icon(Icons.Outlined.Delete, contentDescription = strings.delete, tint = appColors.dangerExpense) },
            onClick = {
                onDismissRequest()
                onDelete()
            },
            modifier = Modifier.minimumInteractiveComponentSize()
        )
    }
}
