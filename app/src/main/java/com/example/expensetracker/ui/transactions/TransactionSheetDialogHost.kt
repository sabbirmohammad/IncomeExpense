package com.example.expensetracker.ui.transactions

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.ui.components.Strings
import com.example.expensetracker.utils.ReceiptImageHelper

@Composable
internal fun TransactionSheetDialogHost(
    state: AddEditTransactionState,
    existingTransaction: Transaction?,
    context: Context,
    typeAccentColor: Color,
    strings: Strings,
    pickers: TransactionReceiptPickers,
    onDelete: ((Long) -> Unit)?,
    onDismiss: () -> Unit
) {
    TransactionSheetAllDialogs(
        showDeleteConfirmDialog = state.showDeleteConfirmDialog && existingTransaction != null,
        existingTransactionTitle = existingTransaction?.title ?: "",
        onDeleteConfirm = {
            existingTransaction?.let { tx ->
                if (!tx.receiptImagePath.isNullOrBlank()) {
                    ReceiptImageHelper.deleteReceiptImage(context, tx.receiptImagePath)
                }
                if (state.receiptImagePath != null && state.receiptImagePath != tx.receiptImagePath) {
                    ReceiptImageHelper.deleteReceiptImage(context, state.receiptImagePath)
                }
                onDelete?.invoke(tx.id)
            }
        },
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
            if (state.receiptImagePath != null && state.receiptImagePath != existingTransaction?.receiptImagePath) {
                ReceiptImageHelper.deleteReceiptImage(context, state.receiptImagePath)
            }
            state.receiptImagePath = null
            state.receiptBitmap = null
            if (existingTransaction != null) {
                state.isEditMode = true
            }
        },
        onDismissReceiptPreview = { state.showFullReceiptPreview = false },
        showDiscardDialog = state.showDiscardDialog,
        onConfirmDiscard = {
            if (existingTransaction != null) {
                if (state.receiptImagePath != null && state.receiptImagePath != existingTransaction.receiptImagePath) {
                    ReceiptImageHelper.deleteReceiptImage(context, state.receiptImagePath)
                }
                state.resetToOriginal()
                if (state.pendingDismissAfterDiscard) onDismiss() else state.isEditMode = false
            } else {
                if (state.receiptImagePath != null) {
                    ReceiptImageHelper.deleteReceiptImage(context, state.receiptImagePath)
                }
                onDismiss()
            }
        },
        onDismissDiscard = { state.showDiscardDialog = false },
        typeAccentColor = typeAccentColor,
        strings = strings,
        showReceiptSourceChooser = pickers.showSourceChooser,
        onSelectCamera = pickers.onLaunchCamera,
        onSelectGallery = pickers.onLaunchGallery,
        onDismissReceiptSourceChooser = pickers.onDismissSourceChooser
    )
}
