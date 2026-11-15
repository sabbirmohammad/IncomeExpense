package com.example.expensetracker.ui.transactions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.example.expensetracker.ui.components.DateTimeHelper
import java.text.SimpleDateFormat

internal data class TransactionDateFormats(
    val dateOnly: SimpleDateFormat,
    val dayOfWeek: SimpleDateFormat,
    val timeOnly: SimpleDateFormat
)

@Composable
internal fun rememberTransactionDateFormats(isBangla: Boolean): TransactionDateFormats {
    return remember(isBangla) {
        val locale = DateTimeHelper.getLocale(isBangla)
        TransactionDateFormats(
            dateOnly = SimpleDateFormat("dd MMM yyyy", locale),
            dayOfWeek = SimpleDateFormat("EEEE", locale),
            timeOnly = SimpleDateFormat("hh:mm a", locale)
        )
    }
}
