package com.example.expensetracker.ui.accounts

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.theme.CardCornerRadius
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.SmallElementRadius
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.rememberAppHaptics
import com.example.expensetracker.ui.components.toParsedAmountOrNull

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferFundsSheet(
    accounts: List<Account>,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onTransfer: (Long, Long, Double, String) -> Unit
) {
    val appColors = LocalAppColors.current
    val strings = LocalAppStrings.current
    val haptics = rememberAppHaptics()
    var fromAccountId by remember { mutableStateOf(accounts.firstOrNull()?.id ?: 0L) }
    var toAccountId by remember { mutableStateOf(accounts.getOrNull(1)?.id ?: accounts.firstOrNull()?.id ?: 0L) }
    var amountText by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showCalculator by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = appColors.surface,
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
                    color = appColors.textPrimary
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Outlined.Close, contentDescription = "Close", tint = appColors.textSecondary)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Source Account (From)
            Text(strings.fromAccount, fontSize = 12.sp, color = appColors.textSecondary)
            Spacer(modifier = Modifier.height(6.dp))
            TransferAccountPickerRow(
                accounts = accounts,
                selectedAccountId = fromAccountId,
                onAccountSelected = { id ->
                    fromAccountId = id
                    if (toAccountId == id) {
                        toAccountId = accounts.firstOrNull { it.id != id }?.id ?: 0L
                    }
                },
                currencySymbol = currencySymbol,
                isBangla = strings.isBangla,
                haptics = haptics,
                selectedColor = appColors.accent
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Destination Account (To)
            Text(strings.toAccount, fontSize = 12.sp, color = appColors.textSecondary)
            Spacer(modifier = Modifier.height(6.dp))

            TransferAccountPickerRow(
                accounts = accounts.filter { it.id != fromAccountId },
                selectedAccountId = toAccountId,
                onAccountSelected = { id -> toAccountId = id },
                currencySymbol = currencySymbol,
                isBangla = strings.isBangla,
                haptics = haptics,
                selectedColor = appColors.income
            )

            Spacer(modifier = Modifier.height(14.dp))

            TransferAmountInputSection(
                amountText = amountText,
                onAmountChange = {
                    amountText = it
                    errorMessage = null
                },
                showCalculator = showCalculator,
                onToggleCalculator = { showCalculator = !showCalculator },
                currencySymbol = currencySymbol,
                haptics = haptics
            )

            Spacer(modifier = Modifier.height(12.dp))

            TransferNoteField(
                note = note,
                onNoteChange = { note = it }
            )

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(errorMessage!!, color = appColors.expense, fontSize = 12.sp)
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
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(SmallElementRadius),
                colors = ButtonDefaults.buttonColors(containerColor = if (isAmountValid) appColors.accent else appColors.accent.copy(alpha = 0.55f))
            ) {
                Text(text = strings.confirmTransfer, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
