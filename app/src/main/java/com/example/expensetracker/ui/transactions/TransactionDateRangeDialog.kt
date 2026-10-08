package com.example.expensetracker.ui.transactions

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.Strings
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDateRangeDialog(
    strings: Strings,
    onDismiss: () -> Unit,
    onConfirmDateRange: (startDateMillis: Long, endDateMillis: Long) -> Unit
) {
    val dateRangePickerState = rememberDateRangePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val start = dateRangePickerState.selectedStartDateMillis
                    val end = dateRangePickerState.selectedEndDateMillis ?: start
                    if (start != null && end != null) {
                        val endCal = Calendar.getInstance().apply {
                            timeInMillis = end
                            set(Calendar.HOUR_OF_DAY, 23)
                            set(Calendar.MINUTE, 59)
                            set(Calendar.SECOND, 59)
                        }
                        onConfirmDateRange(start, endCal.timeInMillis)
                    }
                    onDismiss()
                }
            ) {
                Text(strings.confirm, color = ColorAccent)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(strings.cancel, color = LocalAppColors.current.textSecondary)
            }
        }
    ) {
        DateRangePicker(
            state = dateRangePickerState,
            title = {
                Text(
                    text = strings.selectDateRange,
                    modifier = Modifier.padding(start = 24.dp, end = 12.dp, top = 16.dp),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = LocalAppColors.current.textPrimary
                )
            }
        )
    }
}
