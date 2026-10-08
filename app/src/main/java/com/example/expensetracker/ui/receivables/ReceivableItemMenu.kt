package com.example.expensetracker.ui.receivables

import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.Paid
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorDangerExpense
import com.example.expensetracker.theme.ColorSuccessIncome
import com.example.expensetracker.theme.ColorSurface
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.theme.ColorTextSecondary
import com.example.expensetracker.ui.components.LocalAppStrings

@Composable
fun ReceivableItemMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    isSettled: Boolean,
    onGenerateReminder: () -> Unit,
    onRecordPayment: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val strings = LocalAppStrings.current

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = Modifier.background(ColorSurface)
    ) {
        if (!isSettled) {
            DropdownMenuItem(
                text = { Text(strings.generatePoliteReminder, color = ColorTextPrimary) },
                leadingIcon = { Icon(Icons.Outlined.NotificationsActive, null, tint = ColorAccent) },
                onClick = {
                    onDismissRequest()
                    onGenerateReminder()
                }
            )
        }
        DropdownMenuItem(
            text = { Text(strings.recordPayment, color = ColorTextPrimary) },
            leadingIcon = { Icon(Icons.Outlined.Paid, null, tint = ColorSuccessIncome) },
            onClick = {
                onDismissRequest()
                onRecordPayment()
            }
        )
        DropdownMenuItem(
            text = { Text(if (strings.isBangla) "তথ্য এডিট করুন" else "Edit Details", color = ColorTextPrimary) },
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
