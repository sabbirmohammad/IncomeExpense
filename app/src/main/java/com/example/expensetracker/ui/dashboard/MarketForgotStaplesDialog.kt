package com.example.expensetracker.ui.dashboard

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.expensetracker.theme.AppColors

@Composable
fun MarketForgotStaplesDialog(
    isBangla: Boolean,
    onConfirmFinish: () -> Unit,
    onDismiss: () -> Unit,
    appColors: AppColors
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isBangla) "কাঁচামরিচ বা লেবু কি নিয়েছেন?" else "Forgot Chili or Lemon?",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = appColors.textPrimary
            )
        },
        text = {
            Text(
                text = if (isBangla)
                    "ফর্দে কোনো কাঁচামরিচ, ধনেপাতা বা লেবু কেনা চিহ্নিত নেই। আপনি কি এগুলো নিয়েছেন নাকি ফর্দ এখনই সম্পন্ন করবেন?"
                else
                    "No chili, lemon, or coriander was marked as purchased. Did you already buy them or want to finish now?",
                style = MaterialTheme.typography.bodyMedium,
                color = appColors.textSecondary
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirmFinish,
                colors = ButtonDefaults.buttonColors(containerColor = appColors.income),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = if (isBangla) "হ্যাঁ, সম্পন্ন করুন" else "Yes, Finish Now",
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = if (isBangla) "না, ফর্দে যোগ করব" else "Add to List",
                    color = appColors.accent
                )
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = appColors.surface
    )
}
