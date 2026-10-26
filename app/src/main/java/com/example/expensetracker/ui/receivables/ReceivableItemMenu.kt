package com.example.expensetracker.ui.receivables

import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Paid
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.LocalAppStrings

@Composable
fun ReceivableItemMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    isSettled: Boolean,
    onRecordPayment: () -> Unit,
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
        if (!isSettled) {
            DropdownMenuItem(
                text = { Text(strings.recordPayment, color = appColors.textPrimary) },
                leadingIcon = { Icon(Icons.Outlined.Paid, null, tint = appColors.income) },
                onClick = {
                    onDismissRequest()
                    onRecordPayment()
                }
            )
        }
        DropdownMenuItem(
            text = { Text(if (strings.isBangla) "তথ্য এডিট করুন" else "Edit Details", color = appColors.textPrimary) },
            leadingIcon = { Icon(Icons.Outlined.Edit, null, tint = appColors.textSecondary) },
            onClick = {
                onDismissRequest()
                onEdit()
            }
        )
        DropdownMenuItem(
            text = { Text(strings.delete, color = appColors.expense) },
            leadingIcon = { Icon(Icons.Outlined.Delete, null, tint = appColors.expense) },
            onClick = {
                onDismissRequest()
                onDelete()
            }
        )
    }
}
