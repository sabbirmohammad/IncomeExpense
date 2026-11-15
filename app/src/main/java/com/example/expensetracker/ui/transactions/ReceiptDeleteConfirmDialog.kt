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
import com.example.expensetracker.theme.CardCornerRadius
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.SmallElementRadius
import com.example.expensetracker.ui.components.Strings

@Composable
fun ReceiptDeleteConfirmDialog(
    show: Boolean,
    strings: Strings,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (!show) return

    val appColors = LocalAppColors.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (strings.isBangla) "রশিদ মুছে ফেলবেন?" else "Remove attached receipt?",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = appColors.textPrimary
            )
        },
        text = {
            Text(
                text = if (strings.isBangla) "সংযুক্ত রশিদের ছবিটি স্থায়ীভাবে মুছে ফেলা হবে। আপনি কি নিশ্চিত?"
                else "The attached receipt image will be removed. Are you sure?",
                style = MaterialTheme.typography.bodyMedium,
                color = appColors.textSecondary
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = appColors.expense),
                shape = RoundedCornerShape(SmallElementRadius),
                modifier = Modifier.height(48.dp)
            ) {
                Text(if (strings.isBangla) "মুছে ফেলুন" else "Remove", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.height(48.dp)
            ) {
                Text(strings.cancel, color = appColors.textSecondary)
            }
        },
        shape = RoundedCornerShape(CardCornerRadius),
        containerColor = appColors.surface
    )
}
