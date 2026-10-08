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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.Receivable
import com.example.expensetracker.theme.CardCornerRadius
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorBackground
import com.example.expensetracker.theme.ColorBorder
import com.example.expensetracker.theme.ColorDangerExpense
import com.example.expensetracker.theme.ColorSurface
import com.example.expensetracker.theme.ColorTextMuted
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.theme.ColorTextSecondary
import com.example.expensetracker.theme.SmallElementRadius
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

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = ColorSurface,
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
                    color = ColorTextPrimary
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Outlined.Close, null, tint = ColorTextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(strings.personName, fontSize = 12.sp, color = ColorTextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = personName,
                onValueChange = { personName = it },
                placeholder = { Text(if (strings.isBangla) "যেমন: করিম আহমেদ, বন্ধু তানভীর" else "e.g. Karim Ahmed, Friend Tanvir", color = ColorTextMuted) },
                singleLine = true,
                leadingIcon = { Icon(Icons.Outlined.Person, null, tint = ColorTextSecondary, modifier = Modifier.size(18.dp)) },
                shape = RoundedCornerShape(SmallElementRadius),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = ColorBackground,
                    unfocusedContainerColor = ColorBackground,
                    focusedBorderColor = ColorAccent,
                    unfocusedBorderColor = ColorBorder,
                    focusedTextColor = ColorTextPrimary,
                    unfocusedTextColor = ColorTextPrimary
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text("${strings.phoneNumber} (${strings.optional})", fontSize = 12.sp, color = ColorTextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                placeholder = { Text("017XXXXXXXX", color = ColorTextMuted) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                leadingIcon = { Icon(Icons.Outlined.Phone, null, tint = ColorTextSecondary, modifier = Modifier.size(18.dp)) },
                shape = RoundedCornerShape(SmallElementRadius),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = ColorBackground,
                    unfocusedContainerColor = ColorBackground,
                    focusedBorderColor = ColorAccent,
                    unfocusedBorderColor = ColorBorder,
                    focusedTextColor = ColorTextPrimary,
                    unfocusedTextColor = ColorTextPrimary
                ),
                modifier = Modifier.fillMaxWidth()
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

            Text("${strings.note} (${strings.optional})", fontSize = 12.sp, color = ColorTextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                placeholder = { Text(if (strings.isBangla) "যেমন: প্রোজেক্টের অগ্রিম, জরুরি ধার" else "e.g. Project advance, Emergency loan", color = ColorTextMuted) },
                maxLines = 2,
                shape = RoundedCornerShape(SmallElementRadius),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = ColorBackground,
                    unfocusedContainerColor = ColorBackground,
                    focusedBorderColor = ColorAccent,
                    unfocusedBorderColor = ColorBorder,
                    focusedTextColor = ColorTextPrimary,
                    unfocusedTextColor = ColorTextPrimary
                ),
                modifier = Modifier.fillMaxWidth()
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
                Text(errorMessage!!, color = ColorDangerExpense, fontSize = 12.sp)
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
