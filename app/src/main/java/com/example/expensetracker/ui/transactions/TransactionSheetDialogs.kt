package com.example.expensetracker.ui.transactions

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.CardCornerRadius
import com.example.expensetracker.theme.ColorDangerExpense
import com.example.expensetracker.theme.ColorSurface
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.theme.ColorTextSecondary
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

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(CardCornerRadius),
        containerColor = ColorSurface,
        titleContentColor = ColorTextPrimary,
        textContentColor = ColorTextSecondary,
        title = {
            Text(
                text = strings.deleteTransactionTitle,
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp, fontWeight = FontWeight.Medium)
            )
        },
        text = {
            Text(
                text = if (strings.isBangla) "\"$title\" লেনদেনটি স্থায়ীভাবে মুছে ফেলতে চান? এটি আর ফিরিয়ে আনা যাবে না।"
                else "Are you sure you want to permanently delete \"$title\"? This action cannot be undone.",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp)
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    onDismiss()
                    onConfirm()
                },
                colors = ButtonDefaults.buttonColors(containerColor = ColorDangerExpense),
                shape = RoundedCornerShape(SmallElementRadius)
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

@Composable
fun TransactionFullReceiptPreviewDialog(
    show: Boolean,
    receiptBitmap: Bitmap?,
    typeAccentColor: Color,
    strings: Strings,
    onRemoveReceipt: () -> Unit,
    onDismiss: () -> Unit
) {
    if (!show || receiptBitmap == null) return

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = typeAccentColor),
                shape = RoundedCornerShape(SmallElementRadius)
            ) {
                Text(strings.cancel, color = Color.White)
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onRemoveReceipt()
                    onDismiss()
                }
            ) {
                Text(strings.removeReceipt, color = ColorDangerExpense)
            }
        },
        title = {
            Text(
                text = strings.viewReceipt,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = ColorTextPrimary
            )
        },
        text = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(360.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black.copy(alpha = 0.05f)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    bitmap = receiptBitmap.asImageBitmap(),
                    contentDescription = "Full Receipt",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }
        },
        shape = RoundedCornerShape(CardCornerRadius),
        containerColor = ColorSurface
    )
}

@Composable
fun TransactionDiscardDialog(
    show: Boolean,
    strings: Strings,
    onConfirmDiscard: () -> Unit,
    onDismiss: () -> Unit
) {
    if (!show) return

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = strings.discardDialogTitle,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = LocalAppColors.current.textPrimary
            )
        },
        text = {
            Text(
                text = strings.discardDialogMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = LocalAppColors.current.textSecondary
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    onDismiss()
                    onConfirmDiscard()
                },
                colors = ButtonDefaults.buttonColors(containerColor = ColorDangerExpense),
                shape = RoundedCornerShape(SmallElementRadius)
            ) {
                Text(strings.discard, color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(strings.keepEditing, color = LocalAppColors.current.textPrimary)
            }
        },
        containerColor = LocalAppColors.current.surface,
        shape = RoundedCornerShape(16.dp)
    )
}
