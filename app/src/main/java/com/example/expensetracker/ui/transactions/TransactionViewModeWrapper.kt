package com.example.expensetracker.ui.transactions

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.ui.components.AppHaptics
import com.example.expensetracker.ui.components.Strings
import java.text.SimpleDateFormat

@Composable
internal fun TransactionViewModeWrapper(
    transaction: Transaction,
    state: AddEditTransactionState,
    accounts: List<Account>,
    typeAccentColor: Color,
    currencySymbol: String,
    strings: Strings,
    haptics: AppHaptics,
    dateOnlyFormat: SimpleDateFormat,
    dayOfWeekFormat: SimpleDateFormat,
    timeOnlyFormat: SimpleDateFormat
) {
    TransactionViewModeContent(
        transaction = transaction,
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
        onViewReceipt = {
            if (state.receiptBitmap != null) {
                state.showFullReceiptPreview = true
            } else {
                state.errorMessage = if (strings.isBangla) "রশিদ লোড করা সম্ভব হয়নি" else "Could not load receipt"
                haptics.warningFeedback()
            }
        }
    )
}
