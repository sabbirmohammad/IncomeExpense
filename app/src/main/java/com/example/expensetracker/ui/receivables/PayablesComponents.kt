package com.example.expensetracker.ui.receivables

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.Payable
import com.example.expensetracker.data.model.PayableStatus
import com.example.expensetracker.data.model.PayablesSummary
import com.example.expensetracker.theme.CardCornerRadius
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorBorder
import com.example.expensetracker.theme.ColorDangerExpense
import com.example.expensetracker.theme.ColorSuccessIncome
import com.example.expensetracker.theme.ColorSurface
import com.example.expensetracker.theme.ColorTextMuted
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.theme.ColorTextSecondary
import com.example.expensetracker.theme.SmallElementRadius
import com.example.expensetracker.ui.components.AppCard
import com.example.expensetracker.ui.components.CalculatorEvaluator
import com.example.expensetracker.ui.components.CalculatorKeypadBar
import com.example.expensetracker.ui.components.CalculatorPreviewBadge
import com.example.expensetracker.ui.components.DateTimeHelper
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.formatCurrency
import com.example.expensetracker.ui.components.rememberAppHaptics
import com.example.expensetracker.ui.components.toParsedAmountOrNull
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Summary Hero Card for Payables (দেনা টাকা)
 */
@Composable
fun PayablesSummaryCard(
    summary: PayablesSummary,
    currencySymbol: String,
    isPrivacyMode: Boolean,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val progress = if (summary.totalBorrowed > 0) {
        (summary.totalRepaid / summary.totalBorrowed).toFloat().coerceIn(0f, 1f)
    } else 0f

    AppCard(
        modifier = modifier.fillMaxWidth(),
        padding = 18.dp
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = strings.remainingToPay,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = ColorTextSecondary
                )
                Surface(
                    shape = CircleShape,
                    color = ColorDangerExpense.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = if (strings.isBangla) "${summary.pendingCount} জন পাওনাদার" else "${summary.pendingCount} Creditors",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ColorDangerExpense,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (isPrivacyMode) "••••••" else formatCurrency(summary.remainingDue, currencySymbol),
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = ColorDangerExpense
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Linear Progress of Repayments
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = ColorSuccessIncome,
                trackColor = ColorBorder
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = strings.totalBorrowed,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = ColorTextSecondary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isPrivacyMode) "••••" else formatCurrency(summary.totalBorrowed, currencySymbol),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = ColorTextPrimary
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = strings.totalRepaid,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = ColorTextSecondary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isPrivacyMode) "••••" else formatCurrency(summary.totalRepaid, currencySymbol),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = ColorSuccessIncome
                    )
                }
            }
        }
    }
}

/**
 * Single Payable Row Item
 */
@Composable
fun PayableRowItem(
    payable: Payable,
    currencySymbol: String,
    isPrivacyMode: Boolean,
    onRecordPayment: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val strings = LocalAppStrings.current
    val haptics = rememberAppHaptics()
    var showMenu by remember { mutableStateOf(false) }

    val isOverdue = payable.dueDateMillis != null &&
            payable.dueDateMillis < System.currentTimeMillis() &&
            !payable.isSettled

    val dateFormat = remember(strings.isBangla) { SimpleDateFormat("dd MMM, yyyy", DateTimeHelper.getLocale(strings.isBangla)) }

    AppCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onEdit() },
        padding = 14.dp
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                if (payable.isSettled) ColorSuccessIncome.copy(alpha = 0.15f)
                                else ColorDangerExpense.copy(alpha = 0.15f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (payable.isSettled) Icons.Outlined.Check else Icons.Outlined.Person,
                            contentDescription = null,
                            tint = if (payable.isSettled) ColorSuccessIncome else ColorDangerExpense,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = payable.personName,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = ColorTextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (payable.phoneNumber.isNotBlank()) {
                            Text(
                                text = payable.phoneNumber,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = ColorTextSecondary,
                                maxLines = 1
                            )
                        }
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = if (isPrivacyMode) "••••" else formatCurrency(payable.remainingAmount, currencySymbol),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = if (payable.isSettled) ColorSuccessIncome else ColorDangerExpense
                    )
                    Text(
                        text = if (payable.isSettled) {
                            strings.settledBadge
                        } else {
                            payable.status.getDisplayName(strings.isBangla)
                        },
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                        color = if (payable.isSettled) ColorSuccessIncome else ColorTextSecondary
                    )
                }

                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = "Options",
                            tint = ColorTextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        modifier = Modifier.background(ColorSurface)
                    ) {
                        DropdownMenuItem(
                            text = { Text(if (strings.isBangla) "এডিট করুন" else "Edit", color = ColorTextPrimary) },
                            onClick = {
                                showMenu = false
                                onEdit()
                            },
                            leadingIcon = {
                                Icon(Icons.Outlined.Edit, contentDescription = null, tint = ColorAccent)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(strings.delete, color = ColorDangerExpense) },
                            onClick = {
                                showMenu = false
                                onDelete()
                            },
                            leadingIcon = {
                                Icon(Icons.Outlined.Delete, contentDescription = null, tint = ColorDangerExpense)
                            }
                        )
                    }
                }
            }

            if (payable.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = payable.notes,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                    color = ColorTextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Due Date Badge
                if (payable.dueDateMillis != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.CalendarToday,
                            contentDescription = null,
                            tint = if (isOverdue) ColorDangerExpense else ColorTextSecondary,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        val formattedDueDate = dateFormat.format(Date(payable.dueDateMillis))
                        val displayDueDate = if (strings.isBangla) convertToBengaliDigits(formattedDueDate) else formattedDueDate
                        Text(
                            text = "${strings.dueDate}: $displayDueDate",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = if (isOverdue) ColorDangerExpense else ColorTextSecondary,
                            fontWeight = if (isOverdue) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Quick Call Button
                    if (payable.phoneNumber.isNotBlank()) {
                        IconButton(
                            onClick = {
                                haptics.lightTick()
                                try {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${payable.phoneNumber}"))
                                    context.startActivity(intent)
                                } catch (_: Exception) {}
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Call,
                                contentDescription = strings.callPerson,
                                tint = ColorAccent,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                    }

                    // Record Payment Button
                    if (!payable.isSettled) {
                        Surface(
                            shape = RoundedCornerShape(SmallElementRadius),
                            color = ColorSuccessIncome.copy(alpha = 0.15f),
                            modifier = Modifier.clickable {
                                haptics.mediumClick()
                                onRecordPayment()
                            }
                        ) {
                            Text(
                                text = strings.recordPayablePayment,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = ColorSuccessIncome,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Add / Edit Payable Bottom Sheet
 */
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
    val haptics = rememberAppHaptics()
    val isEdit = payable != null

    var personName by remember { mutableStateOf(payable?.personName ?: "") }
    var phoneNumber by remember { mutableStateOf(payable?.phoneNumber ?: "") }
    var amountText by remember { mutableStateOf(if (payable != null) payable.amount.toInt().toString() else "") }
    var notes by remember { mutableStateOf(payable?.notes ?: "") }
    var dueDateMillis by remember { mutableStateOf(payable?.dueDateMillis) }
    var depositToAccount by remember { mutableStateOf(false) }
    var selectedAccountId by remember { mutableStateOf(accounts.firstOrNull { it.isDefault }?.id ?: accounts.firstOrNull()?.id) }

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

            // Lender Name
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

            // Phone Number
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

            // Amount Field
            OutlinedTextField(
                value = amountText,
                onValueChange = {
                    amountText = it
                    errorMessage = null
                },
                label = { Text(strings.amount) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ColorAccent,
                    unfocusedBorderColor = ColorBorder
                )
            )

            CalculatorPreviewBadge(
                expression = amountText,
                currencySymbol = currencySymbol,
                onApply = { evaluated ->
                    amountText = if (evaluated % 1.0 == 0.0) evaluated.toLong().toString() else String.format(Locale.US, "%.2f", evaluated)
                }
            )

            Spacer(modifier = Modifier.height(4.dp))

            CalculatorKeypadBar(
                currentExpression = amountText,
                onExpressionChanged = {
                    amountText = it
                    errorMessage = null
                },
                onApplyResult = { evaluated ->
                    amountText = if (evaluated % 1.0 == 0.0) evaluated.toLong().toString() else String.format(Locale.US, "%.2f", evaluated)
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Due Date
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

            // Notes
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

            // Deposit to account option (only on new payable creation)
            if (!isEdit && accounts.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(CardCornerRadius))
                        .background(ColorBorder.copy(alpha = 0.2f))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (strings.isBangla) "ধার নেওয়া টাকা অ্যাকাউন্টে জমা করবেন?" else "Deposit borrowed loan into account?",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.5.sp, fontWeight = FontWeight.Medium),
                            color = ColorTextPrimary
                        )
                        Text(
                            text = if (strings.isBangla) "অ্যাকাউন্টে ইনকাম লেনদেন রেকর্ড হবে" else "Will log an income transaction in account",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.5.sp),
                            color = ColorTextSecondary
                        )
                    }
                    Switch(
                        checked = depositToAccount,
                        onCheckedChange = { depositToAccount = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = ColorAccent, checkedTrackColor = ColorAccent.copy(alpha = 0.5f))
                    )
                }
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

            Button(
                onClick = {
                    haptics.mediumClick()
                    val validAmount = CalculatorEvaluator.evaluate(amountText)
                    if (personName.isBlank()) {
                        errorMessage = if (strings.isBangla) "পাওনাদারের নাম লিখুন" else "Enter lender name"
                        return@Button
                    }
                    if (validAmount == null || validAmount <= 0.0) {
                        errorMessage = strings.invalidAmountError
                        return@Button
                    }

                    onSave(
                        personName.trim(),
                        phoneNumber.trim(),
                        validAmount,
                        dueDateMillis,
                        notes.trim(),
                        if (depositToAccount) selectedAccountId else null
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(SmallElementRadius),
                colors = ButtonDefaults.buttonColors(containerColor = ColorAccent)
            ) {
                Text(
                    text = if (isEdit) strings.save else strings.save,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = dueDateMillis ?: System.currentTimeMillis()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        dueDateMillis = datePickerState.selectedDateMillis
                        showDatePicker = false
                    }
                ) {
                    Text(strings.confirm, color = ColorAccent)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(strings.cancel, color = ColorTextSecondary)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

/**
 * Record Repayment for Payable Sheet
 */
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
    val haptics = rememberAppHaptics()
    var amountText by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var selectedAccountId by remember { mutableStateOf(accounts.firstOrNull { it.isDefault }?.id ?: accounts.firstOrNull()?.id) }
    var deductFromAccount by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

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
                    text = strings.recordPayablePayment,
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

            Spacer(modifier = Modifier.height(10.dp))

            // Info Card
            Surface(
                shape = RoundedCornerShape(CardCornerRadius),
                color = ColorBorder.copy(alpha = 0.2f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = payable.personName,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = ColorTextPrimary
                        )
                        Text(
                            text = strings.remainingToPay,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = ColorTextSecondary
                        )
                    }
                    Text(
                        text = formatCurrency(payable.remainingAmount, currencySymbol),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = ColorDangerExpense
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Quick Percentage Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    Pair(if (strings.isBangla) "পূর্ণ পরিশোধ" else "Full", payable.remainingAmount),
                    Pair("50%", payable.remainingAmount * 0.5),
                    Pair("25%", payable.remainingAmount * 0.25)
                ).forEach { (label, amt) ->
                    Surface(
                        shape = RoundedCornerShape(SmallElementRadius),
                        color = ColorAccent.copy(alpha = 0.12f),
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                haptics.lightTick()
                                amountText = amt.toInt().toString()
                                errorMessage = null
                            }
                    ) {
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ColorAccent,
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .fillMaxWidth(),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Amount Field
            OutlinedTextField(
                value = amountText,
                onValueChange = {
                    amountText = it
                    errorMessage = null
                },
                label = { Text(strings.amount) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ColorAccent,
                    unfocusedBorderColor = ColorBorder
                )
            )

            CalculatorPreviewBadge(
                expression = amountText,
                currencySymbol = currencySymbol,
                onApply = { evaluated ->
                    amountText = if (evaluated % 1.0 == 0.0) evaluated.toLong().toString() else String.format(Locale.US, "%.2f", evaluated)
                }
            )

            Spacer(modifier = Modifier.height(4.dp))

            CalculatorKeypadBar(
                currentExpression = amountText,
                onExpressionChanged = {
                    amountText = it
                    errorMessage = null
                },
                onApplyResult = { evaluated ->
                    amountText = if (evaluated % 1.0 == 0.0) evaluated.toLong().toString() else String.format(Locale.US, "%.2f", evaluated)
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Note
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text(strings.note) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ColorAccent,
                    unfocusedBorderColor = ColorBorder
                )
            )

            // Deduct from Account switch
            if (accounts.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(CardCornerRadius))
                        .background(ColorBorder.copy(alpha = 0.2f))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (strings.isBangla) "অ্যাকাউন্ট থেকে টাকা কাটবেন?" else "Deduct from account?",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp, fontWeight = FontWeight.Medium),
                            color = ColorTextPrimary
                        )
                        Text(
                            text = if (strings.isBangla) "খরচ লেনদেন হিসেবে সংরক্ষিত হবে" else "Will log an expense transaction",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                            color = ColorTextSecondary
                        )
                    }
                    Switch(
                        checked = deductFromAccount,
                        onCheckedChange = { deductFromAccount = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = ColorAccent, checkedTrackColor = ColorAccent.copy(alpha = 0.5f))
                    )
                }
            }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage!!,
                    color = ColorDangerExpense,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

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
                colors = ButtonDefaults.buttonColors(containerColor = ColorSuccessIncome)
            ) {
                Text(
                    text = strings.confirm,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
