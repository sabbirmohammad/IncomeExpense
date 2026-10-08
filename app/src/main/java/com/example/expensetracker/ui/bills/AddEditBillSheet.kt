package com.example.expensetracker.ui.bills

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.BillFrequency
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.data.model.RecurringBill
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorBorder
import com.example.expensetracker.theme.ColorTextMuted
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.theme.SmallElementRadius
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.rememberAppHaptics

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditBillSheet(
    billToEdit: RecurringBill?,
    categories: List<Category>,
    accounts: List<Account>,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onSave: (RecurringBill) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val strings = LocalAppStrings.current
    val haptics = rememberAppHaptics()

    var title by remember { mutableStateOf(billToEdit?.title ?: "") }
    var amountText by remember {
        mutableStateOf(billToEdit?.amount?.let { if (it % 1.0 == 0.0) it.toLong().toString() else it.toString() } ?: "")
    }
    var selectedCategory by remember {
        mutableStateOf(categories.find { it.id == billToEdit?.categoryId } ?: categories.firstOrNull())
    }
    var selectedAccount by remember {
        mutableStateOf(accounts.find { it.id == billToEdit?.accountId } ?: accounts.firstOrNull { it.isDefault } ?: accounts.firstOrNull())
    }
    var frequency by remember { mutableStateOf(billToEdit?.frequency ?: BillFrequency.MONTHLY) }
    var dueDayText by remember { mutableStateOf(billToEdit?.dueDay?.toString() ?: "5") }
    var notes by remember { mutableStateOf(billToEdit?.notes ?: "") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showCalculatorBar by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF0F131D),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 640.dp)
                .navigationBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(ColorBorder)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = if (billToEdit != null) strings.editBill else strings.addBill,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                ),
                color = ColorTextPrimary
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                    errorMessage = null
                },
                placeholder = { Text(strings.billTitle, color = ColorTextMuted, fontSize = 13.sp) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(SmallElementRadius),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ColorAccent,
                    unfocusedBorderColor = ColorBorder,
                    focusedTextColor = ColorTextPrimary,
                    unfocusedTextColor = ColorTextPrimary,
                    focusedContainerColor = Color(0xFF0F131D),
                    unfocusedContainerColor = Color(0xFF0F131D)
                ),
                textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            AddEditBillAmountSection(
                amountText = amountText,
                onAmountChange = {
                    amountText = it
                    errorMessage = null
                },
                currencySymbol = currencySymbol,
                showCalculatorBar = showCalculatorBar,
                onToggleCalculator = { showCalculatorBar = !showCalculatorBar }
            )

            Spacer(modifier = Modifier.height(14.dp))

            BillFrequencySelector(
                frequency = frequency,
                onFrequencyChange = { frequency = it }
            )

            Spacer(modifier = Modifier.height(14.dp))

            BillDueDayInput(
                dueDayText = dueDayText,
                onDueDayChange = { dueDayText = it }
            )

            Spacer(modifier = Modifier.height(14.dp))

            AddEditBillCategorySelector(
                categories = categories,
                selectedCategory = selectedCategory,
                onSelectCategory = { selectedCategory = it }
            )

            Spacer(modifier = Modifier.height(14.dp))

            AddEditBillAccountSelector(
                accounts = accounts,
                selectedAccount = selectedAccount,
                onSelectAccount = { selectedAccount = it }
            )

            if (accounts.isNotEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))
            }

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                placeholder = { Text(strings.notesOptional, color = ColorTextMuted, fontSize = 13.sp) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(SmallElementRadius),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ColorAccent,
                    unfocusedBorderColor = ColorBorder,
                    focusedTextColor = ColorTextPrimary,
                    unfocusedTextColor = ColorTextPrimary,
                    focusedContainerColor = Color(0xFF0F131D),
                    unfocusedContainerColor = Color(0xFF0F131D)
                ),
                textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            BillSaveSection(
                title = title,
                amountText = amountText,
                dueDayText = dueDayText,
                selectedCategory = selectedCategory,
                selectedAccount = selectedAccount,
                frequency = frequency,
                notes = notes,
                billToEdit = billToEdit,
                errorMessage = errorMessage,
                onErrorMessageChange = { errorMessage = it },
                haptics = haptics,
                onSave = onSave
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
