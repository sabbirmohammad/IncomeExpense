package com.example.expensetracker.ui.receivables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.Payable
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.SmallElementRadius
import com.example.expensetracker.ui.components.CalculatorEvaluator
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.rememberAppHaptics

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
            RecordPayablePaymentHeader(onDismiss = onDismiss)

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

            PayablePaymentAmountField(
                amountText = amountText,
                onAmountChange = {
                    amountText = it
                    errorMessage = null
                },
                showCalculator = showCalculator,
                onToggleCalculator = { showCalculator = !showCalculator },
                currencySymbol = currencySymbol,
                onCalculatorClose = { showCalculator = false }
            )

            Spacer(modifier = Modifier.height(10.dp))

            PayablePaymentNoteSection(
                note = note,
                onNoteChange = { note = it },
                hasAccounts = accounts.isNotEmpty(),
                deductFromAccount = deductFromAccount,
                onDeductChange = { deductFromAccount = it }
            )

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
