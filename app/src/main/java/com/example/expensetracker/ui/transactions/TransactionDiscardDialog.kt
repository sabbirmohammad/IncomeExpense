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
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.SmallElementRadius
import com.example.expensetracker.ui.components.Strings

@Composable
fun TransactionDiscardDialog(
    show: Boolean,
    strings: Strings,
    onConfirmDiscard: () -> Unit,
    onDismiss: () -> Unit
) {
    if (!show) return

    val appColors = LocalAppColors.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = strings.discardDialogTitle,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = appColors.textPrimary
            )
        },
        text = {
            Text(
                text = strings.discardDialogMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = appColors.textSecondary
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    onDismiss()
                    onConfirmDiscard()
                },
                colors = ButtonDefaults.buttonColors(containerColor = appColors.expense),
                shape = RoundedCornerShape(SmallElementRadius),
                modifier = Modifier.height(48.dp)
            ) {
                Text(strings.discard, color = Color.White)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.height(48.dp)
            ) {
                Text(strings.keepEditing, color = appColors.textPrimary)
            }
        },
        containerColor = appColors.surface,
        shape = RoundedCornerShape(16.dp)
    )
}
