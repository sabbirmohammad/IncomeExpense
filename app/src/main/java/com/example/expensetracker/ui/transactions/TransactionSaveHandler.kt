package com.example.expensetracker.ui.transactions

import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.ui.components.toParsedAmountOrNull
import com.example.expensetracker.utils.ReceiptImageHelper

internal object TransactionSaveHandler {

    fun save(state: AddEditTransactionState) {
        if (state.isSaving) return
        val evaluated = state.amountText.toParsedAmountOrNull()
        if (evaluated == null || evaluated <= 0.0) {
            state.errorMessage = state.strings.amountGreaterThanZero
            state.haptics.warningFeedback()
            return
        }

        if (state.sheetTab == TransactionSheetTab.TRANSFER) {
            val fromId = state.selectedAccountId ?: state.accounts.firstOrNull()?.id
            val toId = state.toAccountId ?: state.accounts.getOrNull(1)?.id
            if (fromId == null || toId == null || fromId == toId) {
                state.errorMessage = if (state.strings.isBangla) "উৎস ও গন্তব্য হিসেবে ভিন্ন অ্যাকাউন্ট নির্বাচন করুন" else "Select different source and destination accounts"
                state.haptics.warningFeedback()
                return
            }
            state.isSaving = true
            state.haptics.successFeedback()
            state.onTransfer?.invoke(fromId, toId, evaluated, state.notes.ifBlank { state.title })
            state.onDismiss()
            return
        }

        val cat = state.selectedCategory
        if (cat == null) {
            state.errorMessage = state.strings.selectCategoryPrompt
            state.haptics.warningFeedback()
            return
        }
        val effAccId = state.selectedAccountId ?: state.accounts.find { it.isDefault }?.id ?: state.accounts.firstOrNull()?.id
        val matchedAcc = state.accounts.find { it.id == effAccId }

        // Clean up replaced old receipt file from disk if existing transaction had one that changed
        val existing = state.existingTransaction
        if (existing != null &&
            !existing.receiptImagePath.isNullOrBlank() &&
            existing.receiptImagePath != state.receiptImagePath
        ) {
            ReceiptImageHelper.deleteReceiptImage(state.context, existing.receiptImagePath)
        }

        val tx = Transaction(
            id = existing?.id ?: 0L,
            title = state.title.ifBlank { cat.getLocalizedName(state.strings.isBangla) },
            amount = evaluated,
            type = state.selectedType,
            categoryId = cat.id,
            categoryName = cat.name,
            categoryIcon = cat.icon,
            categoryColor = cat.colorHex,
            dateMillis = state.dateMillis,
            paymentMethod = state.selectedPaymentMethod,
            accountId = effAccId,
            accountName = matchedAcc?.name ?: existing?.accountName ?: "Cash in Hand",
            notes = state.notes,
            receiptImagePath = state.receiptImagePath
        )
        state.isSaving = true
        state.haptics.successFeedback()
        state.onSave(tx)
    }
}
