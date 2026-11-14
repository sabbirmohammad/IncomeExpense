package com.example.expensetracker.ui.transactions

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.AccountType
import com.example.expensetracker.data.model.PaymentMethod
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.AccountChipRow

@Composable
fun TransactionAccountPickerSection(
    state: AddEditTransactionState,
    parsedAmount: Double?,
    currencySymbol: String,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = state.strings.account,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, fontWeight = FontWeight.SemiBold),
            color = appColors.textSecondary
        )
        Spacer(modifier = Modifier.height(6.dp))
        AccountChipRow(
            accounts = state.accounts,
            selectedAccountId = state.selectedAccountId,
            onAccountSelected = { acc ->
                state.selectedAccountId = acc.id
                state.selectedPaymentMethod = when (acc.type) {
                    AccountType.BANK -> PaymentMethod.BANK_TRANSFER
                    AccountType.MFS -> PaymentMethod.BKASH
                    AccountType.CASH -> PaymentMethod.CASH
                }
            },
            isTransfer = state.isTransfer,
            toAccountId = state.toAccountId,
            onToAccountSelected = { acc -> state.toAccountId = acc.id },
            amount = parsedAmount,
            isExpense = state.isExpense,
            currencySymbol = currencySymbol,
            isBangla = state.strings.isBangla
        )
    }
}
