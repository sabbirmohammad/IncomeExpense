package com.example.expensetracker.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.expensetracker.theme.LocalAppColors

@Composable
fun MarketTargetBudgetDialog(
    targetBudget: Double?,
    isBangla: Boolean,
    onSetTargetBudget: (Double?) -> Unit,
    onDismiss: () -> Unit
) {
    val appColors = LocalAppColors.current
    var budgetInputText by remember(targetBudget) {
        mutableStateOf(targetBudget?.let { if (it % 1.0 == 0.0) it.toInt().toString() else it.toString() } ?: "")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isBangla) "বাজারের টার্গেট বাজেট" else "Market Target Budget",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = if (isBangla) "আজকের বাজারের জন্য আনুমানিক কত টাকার বাজেট রাখতে চান?"
                    else "Set an estimated target budget for today's market trip:",
                    style = MaterialTheme.typography.bodySmall,
                    color = appColors.textSecondary
                )
                OutlinedTextField(
                    value = budgetInputText,
                    onValueChange = { budgetInputText = it },
                    placeholder = { Text(if (isBangla) "যেমন: ২০০০" else "e.g. 2000") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val parsed = budgetInputText.toDoubleOrNull()
                    onSetTargetBudget(parsed?.takeIf { it > 0 })
                    onDismiss()
                }
            ) {
                Text(if (isBangla) "সংরক্ষণ" else "Save")
            }
        },
        dismissButton = {
            Row {
                if (targetBudget != null && targetBudget > 0) {
                    TextButton(
                        onClick = {
                            onSetTargetBudget(null)
                            budgetInputText = ""
                            onDismiss()
                        }
                    ) {
                        Text(if (isBangla) "বাজেট মুছুন" else "Clear", color = appColors.expense)
                    }
                }
                TextButton(onClick = onDismiss) {
                    Text(if (isBangla) "বাতিল" else "Cancel")
                }
            }
        }
    )
}
