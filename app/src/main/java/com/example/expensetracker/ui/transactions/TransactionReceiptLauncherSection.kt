package com.example.expensetracker.ui.transactions

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.expensetracker.utils.ReceiptImageHelper

@Composable
fun TransactionReceiptLauncherSection(
    state: AddEditTransactionState,
    typeAccentColor: Color,
    onLaunchPhotoPicker: () -> Unit,
    onLaunchCamera: () -> Unit,
    onLaunchGallery: () -> Unit,
    onChangeReceipt: () -> Unit
) {
    if (!state.isTransfer) {
        TransactionReceiptSection(
            receiptImagePath = state.receiptImagePath,
            receiptBitmap = state.receiptBitmap,
            typeAccentColor = typeAccentColor,
            strings = state.strings,
            onLaunchPhotoPicker = {
                state.haptics.lightTick()
                onLaunchPhotoPicker()
            },
            onViewReceipt = {
                if (state.receiptBitmap != null) {
                    state.haptics.lightTick()
                    state.showFullReceiptPreview = true
                } else {
                    state.haptics.warningFeedback()
                    state.errorMessage = if (state.strings.isBangla) "রশিদ লোড করা সম্ভব হয়নি" else "Could not load receipt"
                }
            },
            onRemoveReceipt = {
                if (state.receiptImagePath != null && state.receiptImagePath != state.existingTransaction?.receiptImagePath) {
                    ReceiptImageHelper.deleteReceiptImage(state.context, state.receiptImagePath)
                }
                state.receiptImagePath = null
                state.receiptBitmap = null
            },
            onLaunchCamera = {
                state.haptics.lightTick()
                onLaunchCamera()
            },
            onLaunchGallery = {
                state.haptics.lightTick()
                onLaunchGallery()
            },
            onChangeReceipt = {
                state.haptics.lightTick()
                onChangeReceipt()
            }
        )
    }
}
