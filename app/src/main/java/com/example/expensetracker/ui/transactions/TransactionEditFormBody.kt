package com.example.expensetracker.ui.transactions

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.AccountType
import com.example.expensetracker.data.model.PaymentMethod
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.AccountChipRow
import com.example.expensetracker.ui.components.AmountKeypad
import com.example.expensetracker.ui.components.CategoryPicker
import com.example.expensetracker.ui.components.SegmentedControl
import com.example.expensetracker.ui.components.toParsedAmountOrNull
import com.example.expensetracker.utils.ReceiptImageHelper
import java.text.SimpleDateFormat

/**
 * V7 Transaction Edit Form Body:
 * - 48dp SegmentedControl for [ Expense | Income | Transfer ] (remembers last used).
 * - Compact Amount Card (<= 140dp) with live expression preview.
 * - Inline CategoryPicker (2-row scrollable chips + "More" expands inline).
 * - AccountChipRow with balance (supports Transfer From -> To and balance warning).
 * - Date & Time chips row: [ Today ] [ Yesterday ] [ Pick Date ] [ Time ].
 * - Merged Title + "+ Add note" expansion text button.
 * - Compact 48dp single receipt launcher.
 * - Tactile AmountKeypad with keys >= 56dp and math operators (+ − × ÷).
 */
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
    val appColors = LocalAppColors.current
    val parsedAmount = remember(state.amountText) { state.amountText.toParsedAmountOrNull() }

    Column(modifier = modifier.fillMaxWidth()) {
        // 1. Segmented Control: [ Expense | Income | Transfer ] (48dp height)
        SegmentedControl(
            items = listOf(state.strings.expense, state.strings.income, state.strings.transferTab),
            selectedIndex = when (state.sheetTab) {
                TransactionSheetTab.EXPENSE -> 0
                TransactionSheetTab.INCOME -> 1
                TransactionSheetTab.TRANSFER -> 2
            },
            onItemSelected = { idx ->
                when (idx) {
                    0 -> state.handleTabSelected(TransactionSheetTab.EXPENSE)
                    1 -> state.handleTabSelected(TransactionSheetTab.INCOME)
                    2 -> state.handleTabSelected(TransactionSheetTab.TRANSFER)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 2. Amount Input Card (<= 140dp)
        TransactionAmountCard(
            amountText = state.amountText,
            onAmountChange = { state.handleAmountChange(it) },
            isExpense = state.isExpense,
            isTransfer = state.isTransfer,
            typeAccentColor = typeAccentColor,
            currencySymbol = currencySymbol,
            errorMessage = state.errorMessage,
            amountFocusRequester = state.amountFocusRequester,
            strings = state.strings
        )

        Spacer(modifier = Modifier.height(14.dp))

        // 3. Category Picker (only in Expense or Income mode)
        if (!state.isTransfer) {
            Text(
                text = state.strings.category,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, fontWeight = FontWeight.SemiBold),
                color = appColors.textSecondary
            )
            Spacer(modifier = Modifier.height(6.dp))
            CategoryPicker(
                categories = state.availableCategories,
                selectedCategory = state.selectedCategory,
                onCategorySelected = { state.selectedCategory = it },
                isBangla = state.strings.isBangla
            )
            Spacer(modifier = Modifier.height(14.dp))
        }

        // 4. Account Chip Row (with Transfer mode and insufficient balance warning)
        Text(
            text = state.strings.account,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, fontWeight = FontWeight.SemiBold),
            color = appColors.textSecondary
        )
        Spacer(modifier = Modifier.height(6.dp))
        AccountChipRow(
            accounts = state.accounts,
            selectedAccountId = state.selectedAccountId,
            onAccountSelected = { acc ->
                state.selectedAccountId = acc.id
                state.selectedPaymentMethod = when (acc.type) {
                    AccountType.BANK -> PaymentMethod.BANK_TRANSFER
                    AccountType.MFS -> PaymentMethod.BKASH
                    AccountType.CASH -> PaymentMethod.CASH
                }
            },
            isTransfer = state.isTransfer,
            toAccountId = state.toAccountId,
            onToAccountSelected = { acc -> state.toAccountId = acc.id },
            amount = parsedAmount,
            isExpense = state.isExpense,
            currencySymbol = currencySymbol,
            isBangla = state.strings.isBangla
        )

        Spacer(modifier = Modifier.height(14.dp))

        // 5. Date & Time Shortcuts Section
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

        Spacer(modifier = Modifier.height(14.dp))

        // 6. Merged Title and Notes Section
        TransactionNoteAndTitleSection(
            title = state.title,
            onTitleChange = { state.title = it },
            notes = state.notes,
            onNotesChange = { state.notes = it },
            typeAccentColor = typeAccentColor,
            strings = state.strings
        )

        Spacer(modifier = Modifier.height(14.dp))

        // 7. Receipt / Voucher Section (compact 48dp launcher)
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
            Spacer(modifier = Modifier.height(14.dp))
        }

        // 8. AmountKeypad (4x4 tactile keypad with operators and keys >= 56dp)
        Text(
            text = if (state.strings.isBangla) "কী-প্যাডে পরিমাণ লিখুন" else "Keypad",
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, fontWeight = FontWeight.SemiBold),
            color = appColors.textSecondary
        )
        Spacer(modifier = Modifier.height(6.dp))
        AmountKeypad(
            expression = state.amountText,
            onExpressionChange = { state.handleAmountChange(it) },
            currencySymbol = currencySymbol,
            isBangla = state.strings.isBangla,
            showPreviewLine = false // Already rendered in TransactionAmountCard above
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}
