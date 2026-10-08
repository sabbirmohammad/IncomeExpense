package com.example.expensetracker.ui.receivables

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorTextSecondary
import com.example.expensetracker.ui.components.LocalAppStrings
import java.util.Calendar
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceivableDatePickerDialog(
    initialDateMillis: Long?,
    onDateSelected: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    val strings = LocalAppStrings.current
    val initialUtcMillis = remember(initialDateMillis) {
        val localCal = Calendar.getInstance().apply {
            timeInMillis = initialDateMillis ?: System.currentTimeMillis()
        }
        Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            clear()
            set(
                localCal.get(Calendar.YEAR),
                localCal.get(Calendar.MONTH),
                localCal.get(Calendar.DAY_OF_MONTH)
            )
        }.timeInMillis
    }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialUtcMillis
    )
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                datePickerState.selectedDateMillis?.let { selectedUtcMillis ->
                    val utcCal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                        timeInMillis = selectedUtcMillis
                    }
                    val selYear = utcCal.get(Calendar.YEAR)
                    val selMonth = utcCal.get(Calendar.MONTH)
                    val selDay = utcCal.get(Calendar.DAY_OF_MONTH)

                    val updatedLocal = Calendar.getInstance().apply {
                        set(Calendar.YEAR, selYear)
                        set(Calendar.MONTH, selMonth)
                        set(Calendar.DAY_OF_MONTH, selDay)
                        set(Calendar.HOUR_OF_DAY, 12)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }
                    onDateSelected(updatedLocal.timeInMillis)
                }
                onDismiss()
            }) {
                Text(strings.confirm, color = ColorAccent)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(strings.cancel, color = ColorTextSecondary)
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}
