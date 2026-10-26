package com.example.expensetracker.ui.receivables

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
import com.example.expensetracker.data.model.Receivable
import com.example.expensetracker.theme.CardCornerRadius
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.SmallElementRadius
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.formatCurrency
import com.example.expensetracker.ui.components.toParsedAmountOrNull

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordPaymentSheet(
    receivable: Receivable,
    accounts: List<Account>,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onConfirm: (Double, Long?, String) -> Unit
) {
    val strings = LocalAppStrings.current
    val appColors = LocalAppColors.current
    var amountText by remember {
        mutableStateOf(
            receivable.remainingAmount.let {
                if (it % 1.0 == 0.0) it.toLong().toString() else it.toString()
            }
        )
    }
    var selectedAccountId by remember { mutableStateOf(accounts.firstOrNull()?.id) }
    var note by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

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
                    text = strings.recordPayment,
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp, fontWeight = FontWeight.SemiBold),
                    color = appColors.textPrimary
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Outlined.Close, null, tint = appColors.textSecondary)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "${receivable.personName} • ${strings.remainingDue}: ${formatCurrency(receivable.remainingAmount, currencySymbol)}",
                fontSize = 12.sp,
                color = appColors.textSecondary
            )

            Spacer(modifier = Modifier.height(14.dp))

            ReceivableAmountInputSection(
                amountText = amountText,
                onAmountChange = {
                    amountText = it
                    if (errorMessage != null) errorMessage = null
                },
                currencySymbol = currencySymbol,
                label = strings.amount
            )

            Spacer(modifier = Modifier.height(6.dp))

            ReceivablePaymentQuickChips(
                receivable = receivable,
                currencySymbol = currencySymbol,
                onAmountSelected = {
                    amountText = it
                    if (errorMessage != null) errorMessage = null
                }
            )

            if (accounts.isNotEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))
                RecordPaymentAccountSelector(
                    accounts = accounts,
                    selectedAccountId = selectedAccountId,
                    currencySymbol = currencySymbol,
                    onAccountSelected = { selectedAccountId = it }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(strings.note, fontSize = 12.sp, color = appColors.textSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                placeholder = { Text(strings.optional, color = appColors.textMuted) },
                singleLine = true,
                shape = RoundedCornerShape(SmallElementRadius),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = appColors.surfaceVariant,
                    unfocusedContainerColor = appColors.surfaceVariant,
                    focusedBorderColor = appColors.accent,
                    unfocusedBorderColor = appColors.border,
                    focusedTextColor = appColors.textPrimary,
                    unfocusedTextColor = appColors.textPrimary
                ),
                modifier = Modifier.fillMaxWidth()
            )

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(errorMessage!!, color = appColors.expense, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))

            val buttonContentColor = if (appColors.isDark) Color(0xFF052B14) else Color.White

            Button(
                onClick = {
                    val amt = amountText.toParsedAmountOrNull()?.takeIf { it > 0 }
                    if (amt == null || amt <= 0.0) {
                        errorMessage = strings.invalidAmountError
                        return@Button
                    }
                    if (amt > receivable.remainingAmount + 0.01) {
                        errorMessage = "${strings.repaymentExceedError} (${formatCurrency(receivable.remainingAmount, currencySymbol)})"
                        return@Button
                    }
                    onConfirm(amt, selectedAccountId, note)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(SmallElementRadius),
                colors = ButtonDefaults.buttonColors(
                    containerColor = appColors.income,
                    contentColor = buttonContentColor
                )
            ) {
                Text(
                    text = strings.confirmRepayment,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = buttonContentColor
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
