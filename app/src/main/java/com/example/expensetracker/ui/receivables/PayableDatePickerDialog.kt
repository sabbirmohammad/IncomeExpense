package com.example.expensetracker.ui.receivables

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.expensetracker.theme.CardCornerRadius
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.LocalAppStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PayableDatePickerDialog(
    initialDateMillis: Long?,
    onConfirm: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val strings = LocalAppStrings.current
    val appColors = LocalAppColors.current
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDateMillis ?: System.currentTimeMillis()
    )
    val pickerColors = DatePickerDefaults.colors(
        containerColor = appColors.surface,
        titleContentColor = appColors.textPrimary,
        headlineContentColor = appColors.textPrimary,
        weekdayContentColor = appColors.textSecondary,
        subheadContentColor = appColors.textSecondary,
        yearContentColor = appColors.textPrimary,
        currentYearContentColor = appColors.accent,
        selectedYearContentColor = Color.White,
        selectedYearContainerColor = appColors.accent,
        dayContentColor = appColors.textPrimary,
        selectedDayContentColor = Color.White,
        selectedDayContainerColor = appColors.accent,
        todayContentColor = appColors.accent,
        todayDateBorderColor = appColors.accent
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(datePickerState.selectedDateMillis)
                }
            ) {
                Text(strings.confirm, color = appColors.accent)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(strings.cancel, color = appColors.textSecondary)
            }
        },
        shape = RoundedCornerShape(CardCornerRadius),
        colors = pickerColors
    ) {
        DatePicker(
            state = datePickerState,
            colors = pickerColors
        )
    }
}
