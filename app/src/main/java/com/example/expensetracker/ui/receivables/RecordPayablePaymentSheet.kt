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
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.example.expensetracker.data.model.Payable
import androidx.compose.material.icons.filled.Calculate
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.SmallElementRadius
import com.example.expensetracker.ui.components.CalculatorEvaluator
import com.example.expensetracker.ui.components.CalculatorPreviewBadge
import com.example.expensetracker.ui.components.ExpandableCalculatorKeypadBar
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.rememberAppHaptics
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordPayablePaymentSheet(
    payable: Payable,
    accounts: List<Account>,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onConfirm: (amount: Double, accountId: Long?, note: String) -> Unit
) {
    val strings = LocalAppStrings.current
    val appColors = LocalAppColors.current
    val haptics = rememberAppHaptics()
    var amountText by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    val selectedAccountId by remember { mutableStateOf(accounts.firstOrNull { it.isDefault }?.id ?: accounts.firstOrNull()?.id) }
    var deductFromAccount by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showCalculator by remember { mutableStateOf(false) }
    val hasOperators = remember(amountText) {
        amountText.any { it in listOf('+', '-', '*', '/', '×', '÷', '−', '—') }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = appColors.surface,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .imePadding()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = strings.recordPayablePayment,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = appColors.textPrimary
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Outlined.Close, contentDescription = "Close", tint = appColors.textSecondary)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            PayablePaymentInfoCard(
                personName = payable.personName,
                remainingAmount = payable.remainingAmount,
                currencySymbol = currencySymbol,
                remainingToPayLabel = strings.remainingToPay
            )

            Spacer(modifier = Modifier.height(12.dp))

            PayablePaymentQuickChips(
                remainingAmount = payable.remainingAmount,
                isBangla = strings.isBangla,
                onAmountSelected = {
                    amountText = it
                    errorMessage = null
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = amountText,
                onValueChange = {
                    amountText = it
                    errorMessage = null
                },
                label = { Text(strings.amount) },
                trailingIcon = {
                    IconButton(onClick = { showCalculator = !showCalculator }) {
                        Icon(
                            imageVector = Icons.Filled.Calculate,
                            contentDescription = "Calculator",
                            tint = if (showCalculator || hasOperators) appColors.accent else appColors.textSecondary
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = appColors.accent,
                    unfocusedBorderColor = appColors.border
                )
            )

            CalculatorPreviewBadge(
                expression = amountText,
                currencySymbol = currencySymbol,
                onApply = { evaluated ->
                    amountText = if (evaluated % 1.0 == 0.0) evaluated.toLong().toString() else String.format(Locale.US, "%.2f", evaluated)
                    showCalculator = false
                }
            )

            ExpandableCalculatorKeypadBar(
                expanded = showCalculator,
                currentExpression = amountText,
                onExpressionChanged = {
                    amountText = it
                    errorMessage = null
                },
                onApplyResult = { evaluated ->
                    amountText = if (evaluated % 1.0 == 0.0) evaluated.toLong().toString() else String.format(Locale.US, "%.2f", evaluated)
                    showCalculator = false
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text(strings.note) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = appColors.accent,
                    unfocusedBorderColor = appColors.border
                )
            )

            if (accounts.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                PayableDeductAccountToggle(
                    checked = deductFromAccount,
                    onCheckedChange = { deductFromAccount = it },
                    isBangla = strings.isBangla
                )
            }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage!!,
                    color = appColors.expense,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            val buttonContentColor = if (appColors.isDark) Color(0xFF052B14) else Color.White

            Button(
                onClick = {
                    haptics.mediumClick()
                    val validAmount = CalculatorEvaluator.evaluate(amountText)
                    if (validAmount == null || validAmount <= 0.0) {
                        errorMessage = strings.invalidAmountError
                        return@Button
                    }
                    if (validAmount > payable.remainingAmount + 0.001) {
                        errorMessage = strings.repaymentExceedError
                        return@Button
                    }

                    onConfirm(
                        validAmount,
                        if (deductFromAccount) selectedAccountId else null,
                        note.trim()
                    )
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
                    text = strings.confirm,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = buttonContentColor
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
