package com.example.expensetracker.ui.accounts

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
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.theme.CardCornerRadius
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.LocalAppStrings

@Composable
fun AccountDeleteConfirmationDialog(
    account: Account,
    canDelete: Boolean = true,
    onDismiss: () -> Unit,
    onConfirmDelete: () -> Unit
) {
    val strings = LocalAppStrings.current
    val appColors = LocalAppColors.current
    if (!canDelete) {
        AlertDialog(
            onDismissRequest = onDismiss,
            shape = RoundedCornerShape(CardCornerRadius),
            containerColor = appColors.surface,
            title = {
                Text(
                    text = if (strings.isBangla) "অ্যাকাউন্ট মুছে ফেলা যাবে না" else "Cannot Delete Account",
                    color = appColors.textPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            },
            text = {
                Text(
                    text = if (strings.isBangla)
                        "অ্যাপে লেনদেন পরিচালনার জন্য অন্তত একটি সক্রিয় অ্যাকাউন্ট থাকতে হবে।"
                    else
                        "At least one active account is required to record and manage transactions.",
                    color = appColors.textSecondary,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = appColors.accent)
                ) {
                    Text(if (strings.isBangla) "বুঝেছি" else "Understood", color = Color.White)
                }
            }
        )
    } else {
        AlertDialog(
            onDismissRequest = onDismiss,
            shape = RoundedCornerShape(CardCornerRadius),
            containerColor = appColors.surface,
            title = {
                Text(
                    text = if (strings.isBangla) "${account.name} মুছে ফেলবেন?" else "Delete ${account.name}?",
                    color = appColors.textPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            },
            text = {
                Text(
                    text = if (strings.isBangla)
                        "আপনি কি নিশ্চিতভাবে এই অ্যাকাউন্টটি মুছে ফেলতে চান? সংশ্লিষ্ট লেনদেনগুলো সংরক্ষিত থাকবে এবং ডিফল্ট অ্যাকাউন্টে স্থানান্তরিত হবে।"
                    else
                        "Are you sure you want to remove this account? Associated transactions will be preserved and reassigned to the default account.",
                    color = appColors.textSecondary,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = onConfirmDelete,
                    colors = ButtonDefaults.buttonColors(containerColor = appColors.dangerExpense)
                ) {
                    Text(strings.delete, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(strings.cancel, color = appColors.textSecondary)
                }
            }
        )
    }
}
