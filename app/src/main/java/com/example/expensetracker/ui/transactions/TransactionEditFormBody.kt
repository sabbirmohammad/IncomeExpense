package com.example.expensetracker.ui.transactions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.expensetracker.utils.ReceiptImageHelper
import java.text.SimpleDateFormat

@Composable
fun TransactionEditFormBody(
    state: AddEditTransactionState,
    typeAccentColor: Color,
    currencySymbol: String,
    dateOnlyFormat: SimpleDateFormat,
    dayOfWeekFormat: SimpleDateFormat,
    timeOnlyFormat: SimpleDateFormat,
    onLaunchPhotoPicker: () -> Unit = {},
    modifier: Modifier = Modifier,
    onLaunchCamera: () -> Unit = onLaunchPhotoPicker,
    onLaunchGallery: () -> Unit = onLaunchPhotoPicker,
    onChangeReceipt: () -> Unit = onLaunchPhotoPicker,
    onOpenMarketList: (() -> Unit)? = null
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Amount Input Card
        TransactionAmountCard(
            amountText = state.amountText,
            onAmountChange = {
                state.amountText = it
                state.errorMessage = null
            },
            isExpense = state.isExpense,
            typeAccentColor = typeAccentColor,
            currencySymbol = currencySymbol,
            errorMessage = state.errorMessage,
            amountFocusRequester = state.amountFocusRequester,
            strings = state.strings
        )

        Spacer(modifier = Modifier.height(14.dp))

        // 3. Category & Account Dropdowns
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            TransactionCategoryDropdown(
                selectedCategory = state.selectedCategory,
                availableCategories = state.availableCategories,
                onSelectCategory = { state.selectedCategory = it },
                showDropdown = state.showCategoryDropdown,
                onShowDropdownChange = { state.showCategoryDropdown = it },
                typeAccentColor = typeAccentColor,
                strings = state.strings,
                modifier = Modifier.weight(1f)
            )
            TransactionAccountDropdown(
                accounts = state.accounts,
                selectedAccountId = state.selectedAccountId,
                onSelectAccount = { acc, method ->
                    state.selectedAccountId = acc.id
                    state.selectedPaymentMethod = method
                },
                selectedPaymentMethod = state.selectedPaymentMethod,
                onSelectPaymentMethod = { state.selectedPaymentMethod = it },
                showDropdown = state.showAccountDropdown,
                onShowDropdownChange = { state.showAccountDropdown = it },
                amountText = state.amountText,
                isExpense = state.isExpense,
                typeAccentColor = typeAccentColor,
                currencySymbol = currencySymbol,
                strings = state.strings,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 5. Title and Notes Section
        TransactionNoteAndTitleSection(
            title = state.title,
            onTitleChange = { state.title = it },
            notes = state.notes,
            onNotesChange = { state.notes = it },
            typeAccentColor = typeAccentColor,
            strings = state.strings
        )

        Spacer(modifier = Modifier.height(14.dp))

        // 6. Receipt / Voucher Section
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

        Spacer(modifier = Modifier.height(14.dp))

        // 7. Date & Time Section
        TransactionDateTimeSection(
            dateMillis = state.dateMillis,
            onDateMillisChange = { state.dateMillis = it },
            typeAccentColor = typeAccentColor,
            dateOnlyFormat = dateOnlyFormat,
            dayOfWeekFormat = dayOfWeekFormat,
            timeOnlyFormat = timeOnlyFormat,
            onOpenDatePicker = { state.showDatePickerDialog = true },
            onOpenTimePicker = { state.showTimePickerDialog = true },
            strings = state.strings
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}
