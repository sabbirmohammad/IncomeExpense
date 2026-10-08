package com.example.expensetracker.ui.transactions

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.CardCornerRadius
import com.example.expensetracker.theme.ColorBorder
import com.example.expensetracker.theme.ColorSurface
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.theme.ColorTextSecondary
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
                Text(strings.cancel, color = ColorTextSecondary)
            }
        },
        shape = RoundedCornerShape(CardCornerRadius),
        colors = DatePickerDefaults.colors(
            containerColor = ColorSurface,
            titleContentColor = ColorTextPrimary,
            headlineContentColor = ColorTextPrimary,
            weekdayContentColor = ColorTextSecondary,
            subheadContentColor = ColorTextSecondary,
            yearContentColor = ColorTextPrimary,
            currentYearContentColor = typeAccentColor,
            selectedYearContentColor = Color.White,
            selectedYearContainerColor = typeAccentColor,
            dayContentColor = ColorTextPrimary,
            selectedDayContentColor = Color.White,
            selectedDayContainerColor = typeAccentColor,
            todayContentColor = typeAccentColor,
            todayDateBorderColor = typeAccentColor
        )
    ) {
        DatePicker(
            state = datePickerState,
            colors = DatePickerDefaults.colors(
                containerColor = ColorSurface,
                titleContentColor = ColorTextPrimary,
                headlineContentColor = ColorTextPrimary,
                weekdayContentColor = ColorTextSecondary,
                subheadContentColor = ColorTextSecondary,
                yearContentColor = ColorTextPrimary,
                currentYearContentColor = typeAccentColor,
                selectedYearContentColor = Color.White,
                selectedYearContainerColor = typeAccentColor,
                dayContentColor = ColorTextPrimary,
                selectedDayContentColor = Color.White,
                selectedDayContainerColor = typeAccentColor,
                todayContentColor = typeAccentColor,
                todayDateBorderColor = typeAccentColor
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionTimePickerDialog(
    show: Boolean,
    dateMillis: Long,
    typeAccentColor: Color,
    strings: Strings,
    onDismiss: () -> Unit,
    onTimeSelected: (Long) -> Unit
) {
    if (!show) return

    val timePickerCal = remember(dateMillis) {
        Calendar.getInstance().apply { timeInMillis = dateMillis }
    }
    val timePickerState = rememberTimePickerState(
        initialHour = timePickerCal.get(Calendar.HOUR_OF_DAY),
        initialMinute = timePickerCal.get(Calendar.MINUTE),
        is24Hour = false
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(CardCornerRadius),
        containerColor = ColorSurface,
        title = {
            Text(
                text = strings.selectTime,
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp, fontWeight = FontWeight.Medium),
                color = ColorTextPrimary
            )
        },
        text = {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                TimePicker(
                    state = timePickerState,
                    colors = TimePickerDefaults.colors(
                        clockDialColor = ColorBorder,
                        clockDialSelectedContentColor = Color.White,
                        clockDialUnselectedContentColor = ColorTextPrimary,
                        selectorColor = typeAccentColor,
                        containerColor = ColorSurface,
                        periodSelectorBorderColor = ColorBorder,
                        periodSelectorSelectedContainerColor = typeAccentColor,
                        periodSelectorUnselectedContainerColor = ColorSurface,
                        periodSelectorSelectedContentColor = Color.White,
                        periodSelectorUnselectedContentColor = ColorTextSecondary,
                        timeSelectorSelectedContainerColor = typeAccentColor,
                        timeSelectorUnselectedContainerColor = ColorBorder,
                        timeSelectorSelectedContentColor = Color.White,
                        timeSelectorUnselectedContentColor = ColorTextPrimary
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val updated = Calendar.getInstance().apply {
                        timeInMillis = dateMillis
                        set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                        set(Calendar.MINUTE, timePickerState.minute)
                    }
                    onTimeSelected(updated.timeInMillis)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = typeAccentColor),
                shape = RoundedCornerShape(SmallElementRadius)
            ) {
                Text(strings.setTime, color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(strings.cancel, color = ColorTextSecondary)
            }
        }
    )
}
