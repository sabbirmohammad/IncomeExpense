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
import com.example.expensetracker.ui.components.CategoryPicker
import com.example.expensetracker.ui.components.toParsedAmountOrNull
import com.example.expensetracker.utils.ReceiptImageHelper
import java.text.SimpleDateFormat

/**
 * V7 Transaction Edit Form Body:
 * - Compact Amount Card (<= 140dp) with native numeric keyboard entry and live expression evaluation.
 * - Inline CategoryPicker (2-row scrollable chips + "More" expands inline).
 * - AccountChipRow with balance (supports Transfer From -> To and balance warning).
 * - Date & Time chips row: [ Today ] [ Yesterday ] [ Pick Date ] [ Time ].
 * - Merged Title + "+ Add note" expansion text button.
 * - Compact 48dp single receipt launcher.
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
        // 1. Amount Input Card (<= 140dp) with native keypad
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

        // 2. Category Picker (only in Expense or Income mode)
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

        // 3. Account Chip Row (with Transfer mode and insufficient balance warning)
        TransactionAccountPickerSection(
            state = state,
            parsedAmount = parsedAmount,
            currencySymbol = currencySymbol
        )

        Spacer(modifier = Modifier.height(14.dp))

        // 4. Date & Time Shortcuts Section
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

        // 5. Merged Title and Notes Section
        TransactionNoteAndTitleSection(
            title = state.title,
            onTitleChange = { state.title = it },
            notes = state.notes,
            onNotesChange = { state.notes = it },
            typeAccentColor = typeAccentColor,
            strings = state.strings
        )

        Spacer(modifier = Modifier.height(14.dp))

        // 6. Receipt / Voucher Section (compact 48dp launcher)
        TransactionReceiptLauncherSection(
            state = state,
            typeAccentColor = typeAccentColor,
            onLaunchPhotoPicker = onLaunchPhotoPicker,
            onLaunchCamera = onLaunchCamera,
            onLaunchGallery = onLaunchGallery,
            onChangeReceipt = onChangeReceipt
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}
