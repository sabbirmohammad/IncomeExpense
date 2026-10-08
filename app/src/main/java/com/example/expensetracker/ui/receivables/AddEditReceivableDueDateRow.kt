package com.example.expensetracker.ui.receivables

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
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorBackground
import com.example.expensetracker.theme.ColorBorder
import com.example.expensetracker.theme.ColorDangerExpense
import com.example.expensetracker.theme.ColorTextMuted
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.theme.ColorTextSecondary
import com.example.expensetracker.theme.SmallElementRadius
import com.example.expensetracker.ui.components.DateTimeHelper
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.convertToBengaliDigits
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun AddEditReceivableDueDateRow(
    dueDateMillis: Long?,
    onPickDate: () -> Unit,
    onClearDate: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val dateFormat = remember(strings.isBangla) {
        SimpleDateFormat("dd MMM yyyy", DateTimeHelper.getLocale(strings.isBangla))
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Text("${strings.dueDate} (${strings.optional})", fontSize = 12.sp, color = ColorTextSecondary)
        Spacer(modifier = Modifier.height(4.dp))
        Surface(
            shape = RoundedCornerShape(SmallElementRadius),
            color = ColorBackground,
            border = BorderStroke(1.dp, ColorBorder),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onPickDate() }
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.CalendarToday, null, tint = ColorAccent, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (dueDateMillis != null) {
                            val d = dateFormat.format(Date(dueDateMillis))
                            if (strings.isBangla) convertToBengaliDigits(d) else d
                        } else (if (strings.isBangla) "তারিখ নির্ধারণ করুন" else "Set a due date"),
                        fontSize = 13.sp,
                        color = if (dueDateMillis != null) ColorTextPrimary else ColorTextMuted
                    )
                }
                if (dueDateMillis != null) {
                    Text(
                        text = strings.clear,
                        fontSize = 11.sp,
                        color = ColorDangerExpense,
                        modifier = Modifier.clickable { onClearDate() }
                    )
                }
            }
        }
    }
}
