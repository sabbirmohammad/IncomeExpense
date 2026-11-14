package com.example.expensetracker.ui.savings

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import com.example.expensetracker.theme.CardCornerRadius
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.LocalAppStrings
import java.util.Calendar
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalDatePickerDialog(
    targetDateMillis: Long?,
    onDismiss: () -> Unit,
    onDateConfirmed: (Long) -> Unit
) {
    val strings = LocalAppStrings.current
    val appColors = LocalAppColors.current
    val initialUtcMillis = remember(targetDateMillis) {
        val localCal = Calendar.getInstance().apply {
            timeInMillis = targetDateMillis ?: (System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000)
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
            TextButton(onClick = {
                datePickerState.selectedDateMillis?.let { selectedUtcMillis ->
                    val utcCal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                        timeInMillis = selectedUtcMillis
                    }
                    val updatedLocal = Calendar.getInstance().apply {
                        set(Calendar.YEAR, utcCal.get(Calendar.YEAR))
                        set(Calendar.MONTH, utcCal.get(Calendar.MONTH))
                        set(Calendar.DAY_OF_MONTH, utcCal.get(Calendar.DAY_OF_MONTH))
                        set(Calendar.HOUR_OF_DAY, 23)
                        set(Calendar.MINUTE, 59)
                        set(Calendar.SECOND, 59)
                        set(Calendar.MILLISECOND, 999)
                    }
                    onDateConfirmed(updatedLocal.timeInMillis)
                }
                onDismiss()
            }) {
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
