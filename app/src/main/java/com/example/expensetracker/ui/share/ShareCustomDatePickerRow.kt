package com.example.expensetracker.ui.share

import android.app.DatePickerDialog
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorBorder
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.theme.ColorTextSecondary
import com.example.expensetracker.ui.components.Strings
import com.example.expensetracker.ui.components.convertToBengaliDigits
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

@Composable
fun ShareCustomDatePickerRow(
    context: Context,
    customStartDateMillis: Long,
    customEndDateMillis: Long,
    rangeDateFormat: SimpleDateFormat,
    strings: Strings,
    onStartDateChange: (Long) -> Unit,
    onEndDateChange: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Start Date Button
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF1A2033))
                .border(1.dp, ColorBorder, RoundedCornerShape(10.dp))
                .clickable {
                    val c = Calendar.getInstance().apply { timeInMillis = customStartDateMillis }
                    DatePickerDialog(
                        context,
                        { _, year, month, dayOfMonth ->
                            val newCal = Calendar.getInstance().apply {
                                set(year, month, dayOfMonth, 0, 0, 0)
                                set(Calendar.MILLISECOND, 0)
                            }
                            val startMillis = newCal.timeInMillis
                            onStartDateChange(startMillis)
                            if (startMillis > customEndDateMillis) {
                                onEndDateChange(startMillis + (24 * 60 * 60 * 1000 - 1))
                            }
                        },
                        c.get(Calendar.YEAR),
                        c.get(Calendar.MONTH),
                        c.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Column {
                Text(
                    text = "${strings.date} (From)",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = ColorTextSecondary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.CalendarToday,
                        contentDescription = null,
                        tint = ColorAccent,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    val s = rangeDateFormat.format(Date(customStartDateMillis))
                    Text(
                        text = if (strings.isBangla) convertToBengaliDigits(s) else s,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = ColorTextPrimary
                    )
                }
            }
        }

        // End Date Button
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF1A2033))
                .border(1.dp, ColorBorder, RoundedCornerShape(10.dp))
                .clickable {
                    val c = Calendar.getInstance().apply { timeInMillis = customEndDateMillis }
                    DatePickerDialog(
                        context,
                        { _, year, month, dayOfMonth ->
                            val newCal = Calendar.getInstance().apply {
                                set(year, month, dayOfMonth, 23, 59, 59)
                                set(Calendar.MILLISECOND, 999)
                            }
                            val endMillis = newCal.timeInMillis
                            onEndDateChange(endMillis)
                            if (endMillis < customStartDateMillis) {
                                onStartDateChange(endMillis - (24 * 60 * 60 * 1000 - 1))
                            }
                        },
                        c.get(Calendar.YEAR),
                        c.get(Calendar.MONTH),
                        c.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Column {
                Text(
                    text = "${strings.date} (To)",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = ColorTextSecondary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.CalendarToday,
                        contentDescription = null,
                        tint = ColorAccent,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    val e = rangeDateFormat.format(Date(customEndDateMillis))
                    Text(
                        text = if (strings.isBangla) convertToBengaliDigits(e) else e,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = ColorTextPrimary
                    )
                }
            }
        }
    }
}
