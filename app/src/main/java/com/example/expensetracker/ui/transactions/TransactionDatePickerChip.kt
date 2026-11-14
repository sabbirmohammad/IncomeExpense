package com.example.expensetracker.ui.transactions

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
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
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.HairlineInkStroke
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.RadiusS
import com.example.expensetracker.ui.components.rememberAppHaptics
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun TransactionDatePickerChip(
    dateMillis: Long,
    isCustomDate: Boolean,
    typeAccentColor: Color,
    dateOnlyFormat: SimpleDateFormat,
    onOpenDatePicker: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val haptics = rememberAppHaptics()
    val dateLabel = remember(dateMillis) { dateOnlyFormat.format(Date(dateMillis)) }

    Surface(
        shape = RoundedCornerShape(RadiusS),
        color = if (isCustomDate) appColors.accentContainer else appColors.surfaceVariant,
        border = if (isCustomDate) {
            BorderStroke(1.5.dp, typeAccentColor)
        } else if (appColors.isDark) null else {
            BorderStroke(HairlineInkStroke, appColors.border)
        },
        modifier = modifier
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
                tint = if (isCustomDate) typeAccentColor else appColors.textSecondary,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = dateLabel,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (isCustomDate) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 13.sp,
                    fontFeatureSettings = "tnum"
                ),
                color = if (isCustomDate) appColors.textPrimary else appColors.textSecondary
            )
        }
    }
}
