package com.example.expensetracker.ui.transactions

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.CardCornerRadius
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.SmallElementRadius
import com.example.expensetracker.ui.components.Strings
import java.util.Calendar

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

    val appColors = LocalAppColors.current
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
        containerColor = appColors.surface,
        title = {
            Text(
                text = strings.selectTime,
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp, fontWeight = FontWeight.Medium),
                color = appColors.textPrimary
            )
        },
        text = {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                TimePicker(
                    state = timePickerState,
                    colors = TimePickerDefaults.colors(
                        clockDialColor = appColors.border,
                        clockDialSelectedContentColor = Color.White,
                        clockDialUnselectedContentColor = appColors.textPrimary,
                        selectorColor = typeAccentColor,
                        containerColor = appColors.surface,
                        periodSelectorBorderColor = appColors.border,
                        periodSelectorSelectedContainerColor = typeAccentColor,
                        periodSelectorUnselectedContainerColor = appColors.surface,
                        periodSelectorSelectedContentColor = Color.White,
                        periodSelectorUnselectedContentColor = appColors.textSecondary,
                        timeSelectorSelectedContainerColor = typeAccentColor,
                        timeSelectorUnselectedContainerColor = appColors.border,
                        timeSelectorSelectedContentColor = Color.White,
                        timeSelectorUnselectedContentColor = appColors.textPrimary
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
                Text(strings.cancel, color = appColors.textSecondary)
            }
        }
    )
}
