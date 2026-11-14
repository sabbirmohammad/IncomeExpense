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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.Receivable
import com.example.expensetracker.theme.CardCornerRadius
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.LocalAppStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditReceivableSheet(
    receivable: Receivable?,
    accounts: List<Account>,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onSave: (String, String, Double, Long?, String, Long?) -> Unit
) {
    var personName by remember { mutableStateOf(receivable?.personName ?: "") }
    var phoneNumber by remember { mutableStateOf(receivable?.phoneNumber ?: "") }
    var amountText by remember {
        mutableStateOf(receivable?.amount?.let { if (it % 1.0 == 0.0) it.toLong().toString() else it.toString() } ?: "")
    }
    var dueDateMillis by remember { mutableStateOf(receivable?.dueDateMillis) }
    var notes by remember { mutableStateOf(receivable?.notes ?: "") }
    var deductFromAccount by remember { mutableStateOf(false) }
    var selectedAccountId by remember { mutableStateOf(accounts.firstOrNull()?.id) }
    var showDatePicker by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val strings = LocalAppStrings.current
    val appColors = LocalAppColors.current

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
                    text = if (receivable != null) (if (strings.isBangla) "পাওনা হিসাব পরিবর্তন" else "Edit Receivable") else strings.addReceivable,
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp, fontWeight = FontWeight.SemiBold),
                    color = appColors.textPrimary
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Outlined.Close, null, tint = appColors.textSecondary)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            ReceivablePersonAndPhoneFields(
                personName = personName,
                onPersonNameChange = { personName = it },
                phoneNumber = phoneNumber,
                onPhoneNumberChange = { phoneNumber = it }
            )

            Spacer(modifier = Modifier.height(12.dp))

            ReceivableAmountInputSection(
                amountText = amountText,
                onAmountChange = { amountText = it },
                currencySymbol = currencySymbol,
                label = "${strings.amount} ($currencySymbol)"
            )

            Spacer(modifier = Modifier.height(12.dp))

            AddEditReceivableDueDateRow(
                dueDateMillis = dueDateMillis,
                onPickDate = { showDatePicker = true },
                onClearDate = { dueDateMillis = null }
            )

            Spacer(modifier = Modifier.height(12.dp))

            ReceivableNoteField(
                notes = notes,
                onNotesChange = { notes = it }
            )

            if (receivable == null && accounts.isNotEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))
                ReceivableDeductAccountSection(
                    deductFromAccount = deductFromAccount,
                    onDeductToggle = { deductFromAccount = it },
                    accounts = accounts,
                    selectedAccountId = selectedAccountId,
                    currencySymbol = currencySymbol,
                    onAccountSelected = { selectedAccountId = it }
                )
            }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(errorMessage!!, color = appColors.expense, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))

            AddEditReceivableSaveButton(
                personName = personName,
                amountText = amountText,
                phoneNumber = phoneNumber,
                dueDateMillis = dueDateMillis,
                notes = notes,
                deductFromAccount = deductFromAccount,
                selectedAccountId = selectedAccountId,
                onValidationError = { errorMessage = it },
                onSave = onSave
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showDatePicker) {
        ReceivableDatePickerDialog(
            initialDateMillis = dueDateMillis,
            onDateSelected = { dueDateMillis = it },
            onDismiss = { showDatePicker = false }
        )
    }
}
