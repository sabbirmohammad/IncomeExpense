package com.example.expensetracker.ui.transactions

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
import androidx.compose.material.icons.outlined.Schedule
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
import com.example.expensetracker.theme.ColorBorder
import com.example.expensetracker.theme.ColorTextMuted
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.theme.ColorTextSecondary
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.SmallElementRadius
import com.example.expensetracker.ui.components.Strings
import com.example.expensetracker.ui.components.rememberAppHaptics
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun TransactionDateTimeSection(
    dateMillis: Long,
    onDateMillisChange: (Long) -> Unit,
    typeAccentColor: Color,
    dateOnlyFormat: SimpleDateFormat,
    dayOfWeekFormat: SimpleDateFormat,
    timeOnlyFormat: SimpleDateFormat,
    onOpenDatePicker: () -> Unit,
    onOpenTimePicker: () -> Unit,
    strings: Strings,
    modifier: Modifier = Modifier
) {
    val haptics = rememberAppHaptics()

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = strings.dateTimeLabel,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = ColorTextSecondary
            )
            Text(
                text = strings.tapToChange,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                color = ColorTextMuted
            )
        }
        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(SmallElementRadius))
                .background(LocalAppColors.current.surfaceVariant)
                .border(1.dp, LocalAppColors.current.border, RoundedCornerShape(SmallElementRadius))
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .weight(1.3f)
                    .clip(RoundedCornerShape(6.dp))
                    .clickable {
                        haptics.lightTick()
                        onOpenDatePicker()
                    }
                    .padding(vertical = 4.dp, horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.CalendarToday,
                    contentDescription = "Pick date",
                    tint = typeAccentColor,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = dateOnlyFormat.format(Date(dateMillis)),
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp, fontWeight = FontWeight.Medium),
                        color = ColorTextPrimary
                    )
                    Text(
                        text = dayOfWeekFormat.format(Date(dateMillis)),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = ColorTextSecondary
                    )
                }
            }

            Box(modifier = Modifier.height(26.dp).width(1.dp).background(ColorBorder))

            Row(
                modifier = Modifier
                    .weight(0.9f)
                    .clip(RoundedCornerShape(6.dp))
                    .clickable {
                        haptics.lightTick()
                        onOpenTimePicker()
                    }
                    .padding(vertical = 4.dp, horizontal = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Icon(
                    imageVector = Icons.Outlined.Schedule,
                    contentDescription = "Pick time",
                    tint = ColorTextSecondary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = timeOnlyFormat.format(Date(dateMillis)),
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp, fontWeight = FontWeight.Normal),
                    color = ColorTextPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        TransactionDateShortcutsRow(
            dateMillis = dateMillis,
            typeAccentColor = typeAccentColor,
            onDateMillisChange = onDateMillisChange,
            onOpenDatePicker = onOpenDatePicker,
            strings = strings
        )
    }
}
