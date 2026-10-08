package com.example.expensetracker.ui.transactions

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.expensetracker.ui.components.Strings

@Composable
fun TransactionSheetAllDialogs(
    showDeleteConfirmDialog: Boolean,
    existingTransactionTitle: String,
    onDeleteConfirm: () -> Unit,
    onDismissDeleteConfirm: () -> Unit,
    showDatePickerDialog: Boolean,
    dateMillis: Long,
    onDateSelected: (Long) -> Unit,
    onDismissDatePicker: () -> Unit,
    showTimePickerDialog: Boolean,
    onTimeSelected: (Long) -> Unit,
    onDismissTimePicker: () -> Unit,
    showFullReceiptPreview: Boolean,
    receiptBitmap: Bitmap?,
    onRemoveReceipt: () -> Unit,
    onDismissReceiptPreview: () -> Unit,
    showDiscardDialog: Boolean,
    onConfirmDiscard: () -> Unit,
    onDismissDiscard: () -> Unit,
    typeAccentColor: Color,
    strings: Strings
) {
    TransactionDeleteConfirmDialog(
        show = showDeleteConfirmDialog,
        title = existingTransactionTitle,
        strings = strings,
        onConfirm = onDeleteConfirm,
        onDismiss = onDismissDeleteConfirm
    )

    TransactionDatePickerDialog(
        show = showDatePickerDialog,
        dateMillis = dateMillis,
        typeAccentColor = typeAccentColor,
        strings = strings,
        onDismiss = onDismissDatePicker,
        onDateSelected = onDateSelected
    )

    TransactionTimePickerDialog(
        show = showTimePickerDialog,
        dateMillis = dateMillis,
        typeAccentColor = typeAccentColor,
        strings = strings,
        onDismiss = onDismissTimePicker,
        onTimeSelected = onTimeSelected
    )

    TransactionFullReceiptPreviewDialog(
        show = showFullReceiptPreview && receiptBitmap != null,
        receiptBitmap = receiptBitmap,
        typeAccentColor = typeAccentColor,
        strings = strings,
        onRemoveReceipt = onRemoveReceipt,
        onDismiss = onDismissReceiptPreview
    )

    TransactionDiscardDialog(
        show = showDiscardDialog,
        strings = strings,
        onConfirmDiscard = onConfirmDiscard,
        onDismiss = onDismissDiscard
    )
}
