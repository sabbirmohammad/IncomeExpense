package com.example.expensetracker.ui.transactions

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.CardCornerRadius
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.SmallElementRadius
import com.example.expensetracker.ui.components.Strings

@Composable
fun TransactionDeleteConfirmDialog(
    show: Boolean,
    title: String,
    strings: Strings,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (!show) return

    val appColors = LocalAppColors.current

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(CardCornerRadius),
        containerColor = appColors.surface,
        titleContentColor = appColors.textPrimary,
        textContentColor = appColors.textSecondary,
        title = {
            Text(
                text = strings.deleteTransactionTitle,
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp, fontWeight = FontWeight.Medium)
            )
        },
        text = {
            Text(
                text = if (strings.isBangla) "\"$title\" লেনদেনটি মুছে ফেলতে চান?"
                else "Are you sure you want to delete \"$title\"?",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp)
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    onDismiss()
                    onConfirm()
                },
                colors = ButtonDefaults.buttonColors(containerColor = appColors.expense),
                shape = RoundedCornerShape(SmallElementRadius),
                modifier = Modifier.height(48.dp)
            ) {
                Text(strings.delete, color = Color.White)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.height(48.dp)
            ) {
                Text(strings.cancel, color = appColors.textSecondary)
            }
        }
    )
}
