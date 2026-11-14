package com.example.expensetracker.ui.transactions

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.ui.components.Strings

@Composable
internal fun TransactionSheetHeaderHost(
    existingTransaction: Transaction?,
    state: AddEditTransactionState,
    typeAccentColor: Color,
    strings: Strings,
    isMarketActive: Boolean,
    onDelete: ((Long) -> Unit)?
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
            } else {
                state.isEditMode = false
            }
        },
        isMarketActive = isMarketActive
    )
}
