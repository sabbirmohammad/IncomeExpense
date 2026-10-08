package com.example.expensetracker.ui.transactions

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EditCalendar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.ColorBorder
import com.example.expensetracker.theme.ColorSurface
import com.example.expensetracker.theme.ColorTextSecondary
import com.example.expensetracker.ui.components.Strings
import com.example.expensetracker.ui.components.rememberAppHaptics
import java.util.Calendar

@Composable
fun TransactionDateShortcutsRow(
    dateMillis: Long,
    typeAccentColor: Color,
    onDateMillisChange: (Long) -> Unit,
    onOpenDatePicker: () -> Unit,
    strings: Strings,
    modifier: Modifier = Modifier
) {
    val haptics = rememberAppHaptics()

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val isToday = isSameDay(dateMillis, System.currentTimeMillis())
        Surface(
            shape = RoundedCornerShape(6.dp),
            color = if (isToday) typeAccentColor.copy(alpha = 0.15f) else ColorSurface,
            border = BorderStroke(1.dp, if (isToday) typeAccentColor else ColorBorder),
            modifier = Modifier.weight(1f).clickable {
                haptics.lightTick()
                val nowCal = Calendar.getInstance()
                val cur = Calendar.getInstance().apply { timeInMillis = dateMillis }
                cur.set(Calendar.YEAR, nowCal.get(Calendar.YEAR))
                cur.set(Calendar.DAY_OF_YEAR, nowCal.get(Calendar.DAY_OF_YEAR))
                onDateMillisChange(cur.timeInMillis)
            }
        ) {
            Text(
                text = strings.today,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    fontWeight = if (isToday) FontWeight.SemiBold else FontWeight.Normal
                ),
                color = if (isToday) typeAccentColor else ColorTextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 6.dp)
            )
        }

        val yesterdayMillis = remember {
            Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }.timeInMillis
        }
        val isYesterday = isSameDay(dateMillis, yesterdayMillis)
        Surface(
            shape = RoundedCornerShape(6.dp),
            color = if (isYesterday) typeAccentColor.copy(alpha = 0.15f) else ColorSurface,
            border = BorderStroke(1.dp, if (isYesterday) typeAccentColor else ColorBorder),
            modifier = Modifier.weight(1.1f).clickable {
                haptics.lightTick()
                val yestCal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
                val cur = Calendar.getInstance().apply { timeInMillis = dateMillis }
                cur.set(Calendar.YEAR, yestCal.get(Calendar.YEAR))
                cur.set(Calendar.DAY_OF_YEAR, yestCal.get(Calendar.DAY_OF_YEAR))
                onDateMillisChange(cur.timeInMillis)
            }
        ) {
            Text(
                text = strings.yesterday,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    fontWeight = if (isYesterday) FontWeight.SemiBold else FontWeight.Normal
                ),
                color = if (isYesterday) typeAccentColor else ColorTextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 6.dp)
            )
        }

        Surface(
            shape = RoundedCornerShape(6.dp),
            color = ColorSurface,
            border = BorderStroke(1.dp, ColorBorder),
            modifier = Modifier.weight(1f).clickable {
                haptics.lightTick()
                onOpenDatePicker()
            }
        ) {
            Row(
                modifier = Modifier.padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.EditCalendar,
                    contentDescription = null,
                    tint = ColorTextSecondary,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = strings.calendar,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = ColorTextSecondary
                )
            }
        }

        Surface(
            shape = RoundedCornerShape(6.dp),
            color = ColorSurface,
            border = BorderStroke(1.dp, ColorBorder),
            modifier = Modifier.weight(0.9f).clickable {
                haptics.lightTick()
                onDateMillisChange(System.currentTimeMillis())
            }
        ) {
            Text(
                text = strings.now,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = ColorTextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 6.dp)
            )
        }
    }
}

private fun isSameDay(millis1: Long, millis2: Long): Boolean {
    val cal1 = Calendar.getInstance().apply { timeInMillis = millis1 }
    val cal2 = Calendar.getInstance().apply { timeInMillis = millis2 }
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}
