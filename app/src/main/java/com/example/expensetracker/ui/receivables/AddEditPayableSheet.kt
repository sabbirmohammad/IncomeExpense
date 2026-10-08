package com.example.expensetracker.ui.receivables

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
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
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.example.expensetracker.data.model.Payable
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorBorder
import com.example.expensetracker.theme.ColorDangerExpense
import com.example.expensetracker.theme.ColorSurface
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.theme.ColorTextSecondary
import com.example.expensetracker.ui.components.DateTimeHelper
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.convertToBengaliDigits
import java.text.SimpleDateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditPayableSheet(
    payable: Payable?,
    accounts: List<Account>,
    currencySymbol: String = "৳",
    onDismiss: () -> Unit,
    onSave: (personName: String, phoneNumber: String, amount: Double, dueDateMillis: Long?, notes: String, depositToAccountId: Long?) -> Unit
) {
    val strings = LocalAppStrings.current
    val isEdit = payable != null

    var personName by remember { mutableStateOf(payable?.personName ?: "") }
    var phoneNumber by remember { mutableStateOf(payable?.phoneNumber ?: "") }
    var amountText by remember { mutableStateOf(if (payable != null) payable.amount.toInt().toString() else "") }
    var notes by remember { mutableStateOf(payable?.notes ?: "") }
    var dueDateMillis by remember { mutableStateOf(payable?.dueDateMillis) }
    var depositToAccount by remember { mutableStateOf(false) }
    val selectedAccountId by remember { mutableStateOf(accounts.firstOrNull { it.isDefault }?.id ?: accounts.firstOrNull()?.id) }

    var showDatePicker by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val dateFormat = remember(strings.isBangla) { SimpleDateFormat("dd MMM, yyyy", DateTimeHelper.getLocale(strings.isBangla)) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = ColorSurface,
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
                    text = if (isEdit) (if (strings.isBangla) "দেনা হিসাব পরিবর্তন" else "Edit Payable") else strings.addPayable,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = ColorTextPrimary
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Outlined.Close, contentDescription = "Close", tint = ColorTextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = personName,
                onValueChange = {
                    personName = it
                    errorMessage = null
                },
                label = { Text(strings.lenderName) },
                leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null, tint = ColorAccent) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ColorAccent,
                    unfocusedBorderColor = ColorBorder
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text(strings.phoneNumber) },
                leadingIcon = { Icon(Icons.Outlined.Phone, contentDescription = null, tint = ColorTextSecondary) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ColorAccent,
                    unfocusedBorderColor = ColorBorder
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            PayableAmountInputSection(
                amountText = amountText,
                onAmountChange = {
                    amountText = it
                    errorMessage = null
                },
                currencySymbol = currencySymbol,
                label = strings.amount
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = dueDateMillis?.let {
                    val d = dateFormat.format(Date(it))
                    if (strings.isBangla) convertToBengaliDigits(d) else d
                } ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text(strings.dueDate) },
                leadingIcon = { Icon(Icons.Outlined.CalendarToday, contentDescription = null, tint = ColorAccent) },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true },
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = ColorTextPrimary,
                    disabledBorderColor = ColorBorder,
                    disabledLabelColor = ColorTextSecondary,
                    disabledLeadingIconColor = ColorAccent
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text(strings.note) },
                singleLine = false,
                maxLines = 3,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ColorAccent,
                    unfocusedBorderColor = ColorBorder
                )
            )

            if (!isEdit && accounts.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                PayableDepositAccountToggle(
                    checked = depositToAccount,
                    onCheckedChange = { depositToAccount = it },
                    isBangla = strings.isBangla
                )
            }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage!!,
                    color = ColorDangerExpense,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            AddEditPayableSaveButton(
                personName = personName,
                phoneNumber = phoneNumber,
                amountText = amountText,
                dueDateMillis = dueDateMillis,
                notes = notes,
                depositToAccount = depositToAccount,
                selectedAccountId = selectedAccountId,
                onErrorMessage = { errorMessage = it },
                onSave = onSave
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showDatePicker) {
        PayableDatePickerDialog(
            initialDateMillis = dueDateMillis,
            onConfirm = {
                dueDateMillis = it
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }
}
