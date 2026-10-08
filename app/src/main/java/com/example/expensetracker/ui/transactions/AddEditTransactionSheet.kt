package com.example.expensetracker.ui.transactions

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.theme.ColorDangerExpense
import com.example.expensetracker.theme.ColorSuccessIncome
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.DateTimeHelper
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.rememberAppHaptics
import com.example.expensetracker.utils.ReceiptImageHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTransactionSheet(
    sheetState: SheetState,
    existingTransaction: Transaction? = null,
    initialDateMillis: Long? = null,
    initialType: TransactionType = TransactionType.EXPENSE,
    categories: List<Category>,
    accounts: List<Account> = emptyList(),
    currencySymbol: String,
    geminiApiKey: String? = null,
    onDismiss: () -> Unit,
    onSave: (Transaction) -> Unit,
    onDelete: ((Long) -> Unit)? = null
) {
    val haptics = rememberAppHaptics()
    val strings = LocalAppStrings.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val state = remember(existingTransaction, initialType, initialDateMillis) {
        AddEditTransactionState(
            existingTransaction = existingTransaction,
            initialType = initialType,
            initialDateMillis = initialDateMillis,
            categories = categories,
            accounts = accounts,
            geminiApiKey = geminiApiKey,
            strings = strings,
            haptics = haptics,
            context = context,
            scope = scope,
            onDismiss = onDismiss,
            onSave = onSave
        )
    }

    LaunchedEffect(state.receiptImagePath) {
        state.receiptBitmap = state.receiptImagePath?.let { ReceiptImageHelper.loadReceiptBitmap(context, it) }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            scope.launch {
                ReceiptImageHelper.saveReceiptImage(context, uri)?.let {
                    state.receiptImagePath = it
                    haptics.lightTick()
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        if (existingTransaction == null) {
            delay(200)
            try { state.amountFocusRequester.requestFocus() } catch (_: Exception) {}
        }
    }

    val typeAccentColor by animateColorAsState(
        targetValue = if (state.isExpense) ColorDangerExpense else ColorSuccessIncome,
        animationSpec = tween(300),
        label = "typeAccentColor"
    )

    val dateOnlyFormat = remember(strings.isBangla) { SimpleDateFormat("dd MMM yyyy", DateTimeHelper.getLocale(strings.isBangla)) }
    val dayOfWeekFormat = remember(strings.isBangla) { SimpleDateFormat("EEEE", DateTimeHelper.getLocale(strings.isBangla)) }
    val timeOnlyFormat = remember(strings.isBangla) { SimpleDateFormat("hh:mm a", DateTimeHelper.getLocale(strings.isBangla)) }

    ModalBottomSheet(
        onDismissRequest = { state.attemptDismiss() },
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = LocalAppColors.current.surface,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().widthIn(max = 640.dp).navigationBarsPadding().imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TransactionSheetHeader(
                existingTransaction = existingTransaction,
                isEditMode = state.isEditMode,
                isExpense = state.isExpense,
                typeAccentColor = typeAccentColor,
                strings = strings,
                onCloseClick = { state.attemptDismiss() },
                onDeleteClick = onDelete?.let { { state.showDeleteConfirmDialog = true } },
                onCancelEditClick = {
                    if (state.isDirty) {
                        state.pendingDismissAfterDiscard = false
                        state.showDiscardDialog = true
                    } else state.isEditMode = false
                }
            )

            Column(
                modifier = Modifier.fillMaxWidth().weight(1f, fill = false).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                if (existingTransaction != null && !state.isEditMode) {
                    TransactionViewModeContent(
                        transaction = existingTransaction,
                        selectedCategory = state.selectedCategory,
                        matchedAccount = accounts.find { it.id == state.selectedAccountId },
                        selectedPaymentMethod = state.selectedPaymentMethod,
                        dateMillis = state.dateMillis,
                        notes = state.notes,
                        receiptImagePath = state.receiptImagePath,
                        receiptBitmap = state.receiptBitmap,
                        typeAccentColor = typeAccentColor,
                        currencySymbol = currencySymbol,
                        strings = strings,
                        dateOnlyFormat = dateOnlyFormat,
                        dayOfWeekFormat = dayOfWeekFormat,
                        timeOnlyFormat = timeOnlyFormat,
                        onViewReceipt = { state.showFullReceiptPreview = true }
                    )
                } else {
                    TransactionEditFormBody(
                        state = state,
                        typeAccentColor = typeAccentColor,
                        currencySymbol = currencySymbol,
                        dateOnlyFormat = dateOnlyFormat,
                        dayOfWeekFormat = dayOfWeekFormat,
                        timeOnlyFormat = timeOnlyFormat,
                        onLaunchPhotoPicker = {
                            photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        }
                    )
                }
            }

            TransactionBottomActionBar(
                isExistingTransaction = existingTransaction != null,
                isEditMode = state.isEditMode,
                isExpense = state.isExpense,
                typeAccentColor = typeAccentColor,
                amountText = state.amountText,
                currencySymbol = currencySymbol,
                strings = strings,
                onEditClick = { state.isEditMode = true },
                onCancelEditClick = {
                    if (state.isDirty) {
                        state.pendingDismissAfterDiscard = false
                        state.showDiscardDialog = true
                    } else state.isEditMode = false
                },
                onSaveClick = { state.saveCurrentTransaction() }
            )
        }
    }

    TransactionSheetAllDialogs(
        showDeleteConfirmDialog = state.showDeleteConfirmDialog && existingTransaction != null,
        existingTransactionTitle = existingTransaction?.title ?: "",
        onDeleteConfirm = { existingTransaction?.let { onDelete?.invoke(it.id) } },
        onDismissDeleteConfirm = { state.showDeleteConfirmDialog = false },
        showDatePickerDialog = state.showDatePickerDialog,
        dateMillis = state.dateMillis,
        onDateSelected = { state.dateMillis = it },
        onDismissDatePicker = { state.showDatePickerDialog = false },
        showTimePickerDialog = state.showTimePickerDialog,
        onTimeSelected = { state.dateMillis = it },
        onDismissTimePicker = { state.showTimePickerDialog = false },
        showFullReceiptPreview = state.showFullReceiptPreview,
        receiptBitmap = state.receiptBitmap,
        onRemoveReceipt = {
            haptics.lightTick()
            ReceiptImageHelper.deleteReceiptImage(context, state.receiptImagePath)
            state.receiptImagePath = null
            state.receiptBitmap = null
        },
        onDismissReceiptPreview = { state.showFullReceiptPreview = false },
        showDiscardDialog = state.showDiscardDialog,
        onConfirmDiscard = {
            if (existingTransaction != null) {
                state.resetToOriginal()
                if (state.pendingDismissAfterDiscard) onDismiss() else state.isEditMode = false
            } else {
                onDismiss()
            }
        },
        onDismissDiscard = { state.showDiscardDialog = false },
        typeAccentColor = typeAccentColor,
        strings = strings
    )
}
