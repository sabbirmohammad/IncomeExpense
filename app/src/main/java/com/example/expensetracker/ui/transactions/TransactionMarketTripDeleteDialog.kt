package com.example.expensetracker.ui.transactions

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.expensetracker.theme.LocalAppColors

@Composable
fun TransactionMarketTripDeleteDialog(
    isBangla: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val appColors = LocalAppColors.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isBangla) "বাজার রেকর্ড মুছে ফেলবেন?" else "Delete Market Record?",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = appColors.textPrimary
            )
        },
        text = {
            Text(
                text = if (isBangla) "এই বাজার ফর্দটি এবং এর সমস্ত বিবরণ লেনদেন থেকে মুছে যাবে।"
                else "This market trip record and all its items will be removed from transactions.",
                style = MaterialTheme.typography.bodyMedium,
                color = appColors.textSecondary
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = if (isBangla) "মুছে ফেলুন" else "Delete",
                    color = appColors.expense,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = if (isBangla) "বাতিল" else "Cancel",
                    color = appColors.textSecondary
                )
            }
        },
        containerColor = appColors.surface,
        shape = RoundedCornerShape(16.dp)
    )
}
