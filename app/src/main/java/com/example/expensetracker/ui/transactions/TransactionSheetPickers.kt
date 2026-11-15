package com.example.expensetracker.ui.transactions

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.example.expensetracker.theme.SmallElementRadius
import com.example.expensetracker.ui.components.Strings
import java.util.Calendar
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDatePickerDialog(
    show: Boolean,
    dateMillis: Long,
    typeAccentColor: Color,
    strings: Strings,
    onDismiss: () -> Unit,
    onDateSelected: (Long) -> Unit
) {
    if (!show) return

    val appColors = LocalAppColors.current
    val localCal = remember(dateMillis) {
        Calendar.getInstance().apply { timeInMillis = dateMillis }
    }
    val initialUtcMillis = remember(dateMillis) {
        Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            clear()
            set(localCal.get(Calendar.YEAR), localCal.get(Calendar.MONTH), localCal.get(Calendar.DAY_OF_MONTH))
        }.timeInMillis
    }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialUtcMillis)

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    datePickerState.selectedDateMillis?.let { selectedUtcMillis ->
                        val utcCal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                            timeInMillis = selectedUtcMillis
                        }
                        val updatedLocal = Calendar.getInstance().apply {
                            timeInMillis = dateMillis
                            set(Calendar.YEAR, utcCal.get(Calendar.YEAR))
                            set(Calendar.MONTH, utcCal.get(Calendar.MONTH))
                            set(Calendar.DAY_OF_MONTH, utcCal.get(Calendar.DAY_OF_MONTH))
                        }
                        onDateSelected(updatedLocal.timeInMillis)
                    }
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = typeAccentColor),
                shape = RoundedCornerShape(SmallElementRadius)
            ) {
                Text(strings.selectDatePrompt, color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(strings.cancel, color = appColors.textSecondary)
            }
        },
        shape = RoundedCornerShape(CardCornerRadius),
        colors = DatePickerDefaults.colors(
            containerColor = appColors.surface,
            titleContentColor = appColors.textPrimary,
            headlineContentColor = appColors.textPrimary,
            weekdayContentColor = appColors.textSecondary,
            subheadContentColor = appColors.textSecondary,
            yearContentColor = appColors.textPrimary,
            currentYearContentColor = typeAccentColor,
            selectedYearContentColor = Color.White,
            selectedYearContainerColor = typeAccentColor,
            dayContentColor = appColors.textPrimary,
            selectedDayContentColor = Color.White,
            selectedDayContainerColor = typeAccentColor,
            todayContentColor = typeAccentColor,
            todayDateBorderColor = typeAccentColor
        )
    ) {
        DatePicker(
            state = datePickerState,
            colors = DatePickerDefaults.colors(
                containerColor = appColors.surface,
                titleContentColor = appColors.textPrimary,
                headlineContentColor = appColors.textPrimary,
                weekdayContentColor = appColors.textSecondary,
                subheadContentColor = appColors.textSecondary,
                yearContentColor = appColors.textPrimary,
                currentYearContentColor = typeAccentColor,
                selectedYearContentColor = Color.White,
                selectedYearContainerColor = typeAccentColor,
                dayContentColor = appColors.textPrimary,
                selectedDayContentColor = Color.White,
                selectedDayContainerColor = typeAccentColor,
                todayContentColor = typeAccentColor,
                todayDateBorderColor = typeAccentColor
            )
        )
    }
}
