package com.example.expensetracker.ui.accounts

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.example.expensetracker.theme.CardCornerRadius
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorDangerExpense
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.SmallElementRadius
import com.example.expensetracker.ui.components.CalculatorKeypadBar
import com.example.expensetracker.ui.components.CalculatorPreviewBadge
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.formatCurrency
import com.example.expensetracker.ui.components.rememberAppHaptics
import com.example.expensetracker.ui.components.toParsedAmountOrNull
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferFundsSheet(
    accounts: List<Account>,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onTransfer: (Long, Long, Double, String) -> Unit
) {
    val strings = LocalAppStrings.current
    val haptics = rememberAppHaptics()
    var fromAccountId by remember { mutableStateOf(accounts.firstOrNull()?.id ?: 0L) }
    var toAccountId by remember { mutableStateOf(accounts.getOrNull(1)?.id ?: accounts.firstOrNull()?.id ?: 0L) }
    var amountText by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val colors = LocalAppColors.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = colors.surface,
        shape = RoundedCornerShape(topStart = CardCornerRadius, topEnd = CardCornerRadius)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 640.dp)
                .navigationBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = strings.transferFunds,
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp, fontWeight = FontWeight.SemiBold),
                    color = colors.textPrimary
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Outlined.Close, contentDescription = "Close", tint = colors.textSecondary)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Source Account (From)
            Text(strings.fromAccount, fontSize = 12.sp, color = colors.textSecondary)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                accounts.forEach { acc ->
                    val isSelected = fromAccountId == acc.id
                    Surface(
                        shape = RoundedCornerShape(SmallElementRadius),
                        color = if (isSelected) ColorAccent else colors.surfaceVariant,
                        border = if (isSelected) null else BorderStroke(1.dp, colors.border),
                        modifier = Modifier.clickable {
                            haptics.lightTick()
                            fromAccountId = acc.id
                            if (toAccountId == acc.id) {
                                toAccountId = accounts.firstOrNull { it.id != acc.id }?.id ?: 0L
                            }
                        }
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                            Text(
                                text = acc.getLocalizedName(strings.isBangla),
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (isSelected) Color.White else colors.textPrimary
                            )
                            Text(
                                text = formatCurrency(acc.currentBalance, currencySymbol),
                                fontSize = 10.sp,
                                color = if (isSelected) Color.White.copy(alpha = 0.8f) else colors.textMuted
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Destination Account (To)
            Text(strings.toAccount, fontSize = 12.sp, color = colors.textSecondary)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                accounts.filter { it.id != fromAccountId }.forEach { acc ->
                    val isSelected = toAccountId == acc.id
                    Surface(
                        shape = RoundedCornerShape(SmallElementRadius),
                        color = if (isSelected) Color(0xFF3DDC97) else colors.surfaceVariant,
                        border = if (isSelected) null else BorderStroke(1.dp, colors.border),
                        modifier = Modifier.clickable {
                            haptics.lightTick()
                            toAccountId = acc.id
                        }
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                            Text(
                                text = acc.getLocalizedName(strings.isBangla),
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (isSelected) Color.White else colors.textPrimary
                            )
                            Text(
                                text = formatCurrency(acc.currentBalance, currencySymbol),
                                fontSize = 10.sp,
                                color = if (isSelected) Color.White.copy(alpha = 0.8f) else colors.textMuted
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Amount Input
            Text("${strings.amount} ($currencySymbol)", fontSize = 12.sp, color = colors.textSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = amountText,
                onValueChange = {
                    amountText = it.filter { c -> c.isDigit() || c == '.' || c in "+-×÷/* " }
                    errorMessage = null
                },
                placeholder = { Text(if (strings.isBangla) "০.০০" else "0.00", color = colors.textMuted) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(SmallElementRadius),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = colors.surfaceVariant,
                    unfocusedContainerColor = colors.surfaceVariant,
                    focusedBorderColor = ColorAccent,
                    unfocusedBorderColor = colors.border,
                    focusedTextColor = colors.textPrimary,
                    unfocusedTextColor = colors.textPrimary
                ),
                modifier = Modifier.fillMaxWidth()
            )

            CalculatorPreviewBadge(
                expression = amountText,
                currencySymbol = currencySymbol,
                onApply = { evaluated ->
                    haptics.successFeedback()
                    amountText = if (evaluated % 1.0 == 0.0) evaluated.toLong().toString() else String.format(Locale.US, "%.2f", evaluated)
                }
            )

            Spacer(modifier = Modifier.height(4.dp))

            CalculatorKeypadBar(
                currentExpression = amountText,
                onExpressionChanged = { amountText = it },
                onApplyResult = { evaluated ->
                    haptics.successFeedback()
                    amountText = if (evaluated % 1.0 == 0.0) evaluated.toLong().toString() else String.format(Locale.US, "%.2f", evaluated)
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Note Field
            Text(strings.note, fontSize = 12.sp, color = colors.textSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                placeholder = { Text(if (strings.isBangla) "যেমন: ব্যাংক থেকে বিকাশ, এটিএম উত্তোলন" else "e.g. Bank to bKash add money, ATM withdrawal", color = colors.textMuted) },
                singleLine = true,
                shape = RoundedCornerShape(SmallElementRadius),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = colors.surfaceVariant,
                    unfocusedContainerColor = colors.surfaceVariant,
                    focusedBorderColor = ColorAccent,
                    unfocusedBorderColor = colors.border,
                    focusedTextColor = colors.textPrimary,
                    unfocusedTextColor = colors.textPrimary
                ),
                modifier = Modifier.fillMaxWidth()
            )

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(errorMessage!!, color = ColorDangerExpense, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))

            val isAmountValid = (amountText.toParsedAmountOrNull() ?: 0.0) > 0.0

            Button(
                onClick = {
                    val amount = amountText.toParsedAmountOrNull()
                    if (amount == null || amount <= 0.0) {
                        errorMessage = if (strings.isBangla) "সঠিক ট্রান্সফার পরিমাণ লিখুন" else "Please enter a valid transfer amount"
                        haptics.warningFeedback()
                        return@Button
                    }
                    if (fromAccountId == toAccountId) {
                        errorMessage = strings.selectDifferentAccounts
                        haptics.warningFeedback()
                        return@Button
                    }
                    haptics.successFeedback()
                    onTransfer(fromAccountId, toAccountId, amount, note)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(SmallElementRadius),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isAmountValid) ColorAccent else ColorAccent.copy(alpha = 0.55f)
                )
            ) {
                Text(
                    text = strings.confirmTransfer,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
