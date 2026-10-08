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
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorDangerExpense
import com.example.expensetracker.theme.ColorSurface
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.theme.ColorTextSecondary
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
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = Modifier.background(ColorSurface)
    ) {
        DropdownMenuItem(
            text = { Text(if (strings.isBangla) "কার্ড ও ইতিহাস দেখুন" else "View Card & History", color = ColorTextPrimary) },
            leadingIcon = { Icon(Icons.Outlined.History, null, tint = ColorTextSecondary) },
            onClick = {
                onDismissRequest()
                onClick()
            }
        )
        DropdownMenuItem(
            text = { Text(strings.reconcileBalance, color = ColorAccent) },
            leadingIcon = { Icon(Icons.Outlined.AccountBalance, null, tint = ColorAccent) },
            onClick = {
                onDismissRequest()
                onReconcile()
            }
        )
        DropdownMenuItem(
            text = { Text(strings.editAccount, color = ColorTextPrimary) },
            leadingIcon = { Icon(Icons.Outlined.Edit, null, tint = ColorTextSecondary) },
            onClick = {
                onDismissRequest()
                onEdit()
            }
        )
        DropdownMenuItem(
            text = { Text(strings.delete, color = ColorDangerExpense) },
            leadingIcon = { Icon(Icons.Outlined.Delete, null, tint = ColorDangerExpense) },
            onClick = {
                onDismissRequest()
                onDelete()
            }
        )
    }
}
