package com.example.expensetracker.ui.receivables

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Receivable
import com.example.expensetracker.theme.CardCornerRadius
import com.example.expensetracker.theme.ColorDangerExpense
import com.example.expensetracker.theme.ColorSurface
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.theme.ColorTextSecondary
import com.example.expensetracker.ui.components.LocalAppStrings

@Composable
fun ReceivableDeleteDialog(
    receivable: Receivable,
    onDismiss: () -> Unit,
    onConfirmDelete: (Receivable) -> Unit
) {
    val strings = LocalAppStrings.current

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(CardCornerRadius),
        containerColor = ColorSurface,
        title = {
            Text(
                text = "${strings.deleteReceivableConfirm} (${receivable.personName})",
                color = ColorTextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        },
        text = {
            Text(
                text = strings.deleteReceivableMessage,
                color = ColorTextSecondary,
                fontSize = 13.sp
            )
        },
        confirmButton = {
            Button(
                onClick = { onConfirmDelete(receivable) },
                colors = ButtonDefaults.buttonColors(containerColor = ColorDangerExpense)
            ) {
                Text(strings.delete, color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(strings.cancel, color = ColorTextSecondary)
            }
        }
    )
}
