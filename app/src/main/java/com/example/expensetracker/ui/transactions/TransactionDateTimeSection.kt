package com.example.expensetracker.ui.transactions

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.HairlineInkStroke
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.RadiusS
import com.example.expensetracker.ui.components.Strings
import com.example.expensetracker.ui.components.rememberAppHaptics
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

/**
 * V7 Transaction Date & Time Chips:
 * C4 rule: Date/time = chips: [Today] [Yesterday] [Pick date] and a time chip; not a large box.
 * All chips have >= 48dp minimum touch targets.
 */
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
    val appColors = LocalAppColors.current
    val haptics = rememberAppHaptics()

    val isToday = remember(dateMillis) {
        isSameDay(dateMillis, System.currentTimeMillis())
    }
    val yesterdayMillis = remember {
        Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }.timeInMillis
    }
    val isYesterday = remember(dateMillis) {
        isSameDay(dateMillis, yesterdayMillis)
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = strings.date,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, fontWeight = FontWeight.SemiBold),
            color = appColors.textSecondary
        )
        Spacer(modifier = Modifier.height(6.dp))

        val scrollState = rememberScrollState()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Today Chip
            DateTimeChip(
                label = strings.today,
                isSelected = isToday,
                activeColor = typeAccentColor,
                onClick = {
                    haptics.lightTick()
                    val nowCal = Calendar.getInstance()
                    val cur = Calendar.getInstance().apply { timeInMillis = dateMillis }
                    cur.set(Calendar.YEAR, nowCal.get(Calendar.YEAR))
                    cur.set(Calendar.DAY_OF_YEAR, nowCal.get(Calendar.DAY_OF_YEAR))
                    onDateMillisChange(cur.timeInMillis)
                }
            )

            // 2. Yesterday Chip
            DateTimeChip(
                label = strings.yesterday,
                isSelected = isYesterday,
                activeColor = typeAccentColor,
                onClick = {
                    haptics.lightTick()
                    val yestCal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
                    val cur = Calendar.getInstance().apply { timeInMillis = dateMillis }
                    cur.set(Calendar.YEAR, yestCal.get(Calendar.YEAR))
                    cur.set(Calendar.DAY_OF_YEAR, yestCal.get(Calendar.DAY_OF_YEAR))
                    onDateMillisChange(cur.timeInMillis)
                }
            )

            // 3. Date Picker Chip (shows current formatted date)
            val dateLabel = remember(dateMillis) {
                dateOnlyFormat.format(Date(dateMillis))
            }
            Surface(
                shape = RoundedCornerShape(RadiusS),
                color = if (!isToday && !isYesterday) appColors.accentContainer else appColors.surfaceVariant,
                border = if (!isToday && !isYesterday) {
                    BorderStroke(1.5.dp, typeAccentColor)
                } else if (appColors.isDark) null else {
                    BorderStroke(HairlineInkStroke, appColors.border)
                },
                modifier = Modifier
                    .defaultMinSize(minHeight = 44.dp)
                    .minimumInteractiveComponentSize()
                    .clickable {
                        haptics.lightTick()
                        onOpenDatePicker()
                    }
                    .semantics {
                        role = Role.Button
                        contentDescription = "Date $dateLabel"
                    }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CalendarToday,
                        contentDescription = null,
                        tint = if (!isToday && !isYesterday) typeAccentColor else appColors.textSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = dateLabel,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = if (!isToday && !isYesterday) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp,
                            fontFeatureSettings = "tnum"
                        ),
                        color = if (!isToday && !isYesterday) appColors.textPrimary else appColors.textSecondary
                    )
                }
            }

            // 4. Time Picker Chip (shows current formatted time)
            val timeLabel = remember(dateMillis) {
                timeOnlyFormat.format(Date(dateMillis))
            }
            Surface(
                shape = RoundedCornerShape(RadiusS),
                color = appColors.surfaceVariant,
                border = if (appColors.isDark) null else BorderStroke(HairlineInkStroke, appColors.border),
                modifier = Modifier
                    .defaultMinSize(minHeight = 44.dp)
                    .minimumInteractiveComponentSize()
                    .clickable {
                        haptics.lightTick()
                        onOpenTimePicker()
                    }
                    .semantics {
                        role = Role.Button
                        contentDescription = "Time $timeLabel"
                    }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Schedule,
                        contentDescription = null,
                        tint = appColors.textSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = timeLabel,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp,
                            fontFeatureSettings = "tnum"
                        ),
                        color = appColors.textSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun DateTimeChip(
    label: String,
    isSelected: Boolean,
    activeColor: Color,
    onClick: () -> Unit
) {
    val appColors = LocalAppColors.current
    val backgroundColor = if (isSelected) appColors.accentContainer else appColors.surfaceVariant
    val borderStroke = when {
        isSelected -> BorderStroke(1.5.dp, activeColor)
        appColors.isDark -> null
        else -> BorderStroke(HairlineInkStroke, appColors.border)
    }

    Surface(
        shape = RoundedCornerShape(RadiusS),
        color = backgroundColor,
        border = borderStroke,
        modifier = Modifier
            .defaultMinSize(minHeight = 44.dp)
            .minimumInteractiveComponentSize()
            .clickable { onClick() }
            .semantics {
                role = Role.Button
                selected = isSelected
                contentDescription = "$label, ${if (isSelected) "selected" else "not selected"}"
            }
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 13.sp
                ),
                color = if (isSelected) appColors.textPrimary else appColors.textSecondary
            )
        }
    }
}

private fun isSameDay(time1: Long, time2: Long): Boolean {
    val cal1 = Calendar.getInstance().apply { timeInMillis = time1 }
    val cal2 = Calendar.getInstance().apply { timeInMillis = time2 }
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}
