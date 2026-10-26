package com.example.expensetracker.ui.savings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.SmallElementRadius
import com.example.expensetracker.ui.components.DateTimeHelper
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.convertToBengaliDigits
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun GoalDateSelectorRow(
    targetDateMillis: Long?,
    onOpenDatePicker: () -> Unit,
    onClearDate: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val appColors = LocalAppColors.current
    val dateFormat = remember(strings.isBangla) {
        SimpleDateFormat("dd MMM yyyy", DateTimeHelper.getLocale(strings.isBangla))
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Text("${strings.targetDate} (${strings.optional})", fontSize = 12.sp, color = appColors.textSecondary)
        Spacer(modifier = Modifier.height(4.dp))
        Surface(
            shape = RoundedCornerShape(SmallElementRadius),
            color = appColors.surfaceVariant,
            border = BorderStroke(1.dp, appColors.border),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onOpenDatePicker() }
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.CalendarToday, null, tint = appColors.accent, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (targetDateMillis != null) {
                            val dateStr = dateFormat.format(Date(targetDateMillis))
                            if (strings.isBangla) convertToBengaliDigits(dateStr) else dateStr
                        } else (if (strings.isBangla) "টার্গেট তারিখ বেছে নিন" else "Pick a target date"),
                        fontSize = 13.sp,
                        color = if (targetDateMillis != null) appColors.textPrimary else appColors.textMuted
                    )
                }
                if (targetDateMillis != null) {
                    Text(
                        text = strings.clear,
                        fontSize = 11.sp,
                        color = appColors.expense,
                        modifier = Modifier.clickable { onClearDate() }
                    )
                }
            }
        }
    }
}
