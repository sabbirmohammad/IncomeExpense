package com.example.expensetracker.ui.budget

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Budget
import com.example.expensetracker.data.model.SavingsGoal
import com.example.expensetracker.data.model.getLocalizedCategoryName
import com.example.expensetracker.theme.CardCornerRadius
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.SmallElementRadius
import com.example.expensetracker.ui.components.LocalAppStrings

@Composable
fun DeleteBudgetConfirmDialog(
    targetBudget: Budget,
    onDismiss: () -> Unit,
    onConfirmDelete: () -> Unit
) {
    val strings = LocalAppStrings.current
    val appColors = LocalAppColors.current

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(CardCornerRadius),
        containerColor = appColors.surface,
        titleContentColor = appColors.textPrimary,
        textContentColor = appColors.textSecondary,
        title = {
            Text(
                text = if (strings.isBangla) "বাজেট মুছে ফেলবেন?" else "Delete budget?",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            )
        },
        text = {
            Text(
                text = if (strings.isBangla)
                    "আপনি কি নিশ্চিতভাবে ${getLocalizedCategoryName(targetBudget.categoryName, strings.isBangla)}-এর বাজেট সীমা মুছে ফেলতে চান?"
                else
                    "Are you sure you want to remove the budget limit for ${targetBudget.categoryName}?",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp)
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirmDelete,
                colors = ButtonDefaults.buttonColors(containerColor = appColors.dangerExpense),
                shape = RoundedCornerShape(SmallElementRadius)
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

@Composable
fun DeleteGoalConfirmDialog(
    targetGoal: SavingsGoal,
    onDismiss: () -> Unit,
    onConfirmDelete: () -> Unit
) {
    val strings = LocalAppStrings.current
    val appColors = LocalAppColors.current

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(CardCornerRadius),
        containerColor = appColors.surface,
        titleContentColor = appColors.textPrimary,
        textContentColor = appColors.textSecondary,
        title = {
            Text(
                text = "${strings.deleteGoalConfirm} (${targetGoal.title})",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            )
        },
        text = {
            Text(
                text = strings.deleteGoalMessage,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp)
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirmDelete,
                colors = ButtonDefaults.buttonColors(containerColor = appColors.dangerExpense),
                shape = RoundedCornerShape(SmallElementRadius)
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
