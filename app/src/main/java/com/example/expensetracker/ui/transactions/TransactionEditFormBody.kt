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
    onLaunchPhotoPicker: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            TransactionAiMagicFillTrigger(
                showAiMagicFill = state.showAiMagicFill,
                strings = state.strings,
                onClick = {
                    state.haptics.lightTick()
                    state.showAiMagicFill = !state.showAiMagicFill
                },
                modifier = Modifier.weight(1f)
            )
            TransactionReceiptTrigger(
                isReceiptAttached = state.receiptImagePath != null,
                isBangla = state.strings.isBangla,
                onClick = {
                    state.haptics.lightTick()
                    if (state.receiptImagePath != null) state.showFullReceiptPreview = true
                    else onLaunchPhotoPicker()
                },
                modifier = Modifier.weight(1f)
            )
        }

        TransactionAiMagicFillContent(
            visible = state.showAiMagicFill,
            inputText = state.aiMagicInputText,
            onInputTextChange = { state.aiMagicInputText = it },
            isParsing = state.isAiMagicParsing,
            strings = state.strings,
            onParseClick = { state.parseAiMagic() }
        )

        TransactionReceiptPreviewBar(
            visible = state.receiptImagePath != null,
            receiptBitmap = state.receiptBitmap,
            isScanningReceipt = state.isScanningReceipt,
            scanStatusMessage = state.scanStatusMessage,
            typeAccentColor = typeAccentColor,
            strings = state.strings,
            onViewReceipt = { state.showFullReceiptPreview = true },
            onRemoveReceipt = {
                state.haptics.lightTick()
                ReceiptImageHelper.deleteReceiptImage(state.context, state.receiptImagePath)
                state.receiptImagePath = null
                state.receiptBitmap = null
                state.scanStatusMessage = null
            },
            onScanReceiptClick = { state.scanReceipt() }
        )

        Spacer(modifier = Modifier.height(12.dp))
        TransactionTypeSwitcher(
            selectedType = state.selectedType,
            isExpense = state.isExpense,
            onTypeSelected = { newType ->
                state.selectedType = newType
                state.selectedCategory = state.categories.firstOrNull { it.type == newType }
            },
            strings = state.strings
        )

        Spacer(modifier = Modifier.height(14.dp))
        TransactionAmountCard(
            amountText = state.amountText,
            onAmountChange = {
                state.amountText = it
                state.errorMessage = null
            },
            isExpense = state.isExpense,
            typeAccentColor = typeAccentColor,
            currencySymbol = currencySymbol,
            showCalculatorBar = state.showCalculatorBar,
            onToggleCalculator = { state.showCalculatorBar = !state.showCalculatorBar },
            errorMessage = state.errorMessage,
            amountFocusRequester = state.amountFocusRequester,
            strings = state.strings
        )

        Spacer(modifier = Modifier.height(14.dp))
        TransactionNoteAndTitleSection(
            title = state.title,
            onTitleChange = { state.title = it },
            notes = state.notes,
            onNotesChange = { state.notes = it },
            typeAccentColor = typeAccentColor,
            strings = state.strings
        )

        Spacer(modifier = Modifier.height(14.dp))
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
