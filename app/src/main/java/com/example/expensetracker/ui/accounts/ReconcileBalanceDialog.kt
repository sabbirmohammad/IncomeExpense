package com.example.expensetracker.ui.accounts

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.ui.components.convertToEnglishDigits
import com.example.expensetracker.ui.components.formatCurrency
import com.example.expensetracker.theme.CardCornerRadius
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.LocalAppStrings

@Composable
fun ReconcileBalanceDialog(
    account: Account,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onConfirm: (Double, String) -> Unit
) {
    val strings = LocalAppStrings.current
    val appColors = LocalAppColors.current
    var newBalanceText by remember { mutableStateOf("") }
    var noteText by remember { mutableStateOf("") }

    val currentBal = account.currentBalance
    val parsedNewBal = convertToEnglishDigits(newBalanceText.trim()).toDoubleOrNull()
    val diff = if (parsedNewBal != null) parsedNewBal - currentBal else null

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(CardCornerRadius),
        containerColor = appColors.surface,
        title = {
            Column {
                Text(
                    text = strings.reconcileBalance,
                    color = appColors.textPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = account.name,
                    color = appColors.textSecondary,
                    fontSize = 13.sp
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = appColors.background,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (strings.isBangla) "অ্যাপের বর্তমান ব্যালেন্স" else "Current App Balance",
                            color = appColors.textSecondary,
                            fontSize = 12.sp
                        )
                        Text(
                            text = formatCurrency(currentBal, currencySymbol),
                            color = appColors.textPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = newBalanceText,
                    onValueChange = { newBalanceText = it },
                    label = { Text(strings.actualCurrentBalance) },
                    placeholder = { Text(if (strings.isBangla) "০.০০" else "0.00") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = appColors.textPrimary,
                        unfocusedTextColor = appColors.textPrimary,
                        focusedBorderColor = appColors.accent,
                        unfocusedBorderColor = appColors.border,
                        focusedLabelColor = appColors.accent,
                        unfocusedLabelColor = appColors.textMuted
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                if (diff != null) {
                    ReconcileDifferenceCard(
                        diff = diff,
                        currencySymbol = currencySymbol
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    label = { Text(if (strings.isBangla) "নোট (ঐচ্ছিক)" else "Note (Optional)") },
                    placeholder = { Text(strings.reconciliationNote) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = appColors.textPrimary,
                        unfocusedTextColor = appColors.textPrimary,
                        focusedBorderColor = appColors.accent,
                        unfocusedBorderColor = appColors.border,
                        focusedLabelColor = appColors.accent,
                        unfocusedLabelColor = appColors.textMuted
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (parsedNewBal != null) {
                        onConfirm(parsedNewBal, noteText)
                    }
                },
                enabled = parsedNewBal != null && kotlin.math.abs(parsedNewBal - currentBal) > 0.001,
                colors = ButtonDefaults.buttonColors(containerColor = appColors.accent)
            ) {
                Text(strings.confirm, color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(strings.cancel, color = appColors.textSecondary)
            }
        }
    )
}
