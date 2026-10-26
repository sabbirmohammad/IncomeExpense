package com.example.expensetracker.ui.reports

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.expensetracker.ui.components.Strings
import com.example.expensetracker.theme.LocalAppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportCustomDateRangeDialogs(
    showStartDatePicker: Boolean,
    showEndDatePicker: Boolean,
    tempStartDate: Long,
    strings: Strings,
    onStartDateSelected: (Long) -> Unit,
    onEndDateSelected: (Long) -> Unit,
    onDismissStartDate: () -> Unit,
    onDismissEndDate: () -> Unit
) {
    val appColors = LocalAppColors.current
    if (showStartDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = tempStartDate)
        DatePickerDialog(
            onDismissRequest = onDismissStartDate,
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { selected ->
                        onStartDateSelected(selected)
                    }
                }) {
                    Text(strings.confirm, color = appColors.accent)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissStartDate) {
                    Text(strings.cancel, color = appColors.textMuted)
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                title = {
                    Text(
                        strings.startDate,
                        modifier = Modifier.padding(start = 24.dp, top = 16.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    }

    if (showEndDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = System.currentTimeMillis())
        DatePickerDialog(
            onDismissRequest = onDismissEndDate,
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { selected ->
                        onEndDateSelected(selected)
                    }
                }) {
                    Text(strings.done, color = appColors.accent)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissEndDate) {
                    Text(strings.cancel, color = appColors.textMuted)
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                title = {
                    Text(
                        strings.endDate,
                        modifier = Modifier.padding(start = 24.dp, top = 16.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    }
}
