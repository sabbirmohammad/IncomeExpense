package com.example.expensetracker.ui.receivables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Paid
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Receivable
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorBorder
import com.example.expensetracker.theme.ColorDangerExpense
import com.example.expensetracker.theme.ColorSuccessIncome
import com.example.expensetracker.theme.ColorSurface
import com.example.expensetracker.theme.ColorTextMuted
import com.example.expensetracker.theme.ColorTextSecondary
import com.example.expensetracker.ui.components.DateTimeHelper
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.convertToBengaliDigits
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun ReceivableItemBottomBar(
    receivable: Receivable,
    onRecordPayment: () -> Unit,
    onCall: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val dateFormat = remember(strings.isBangla) {
        SimpleDateFormat("d MMM yyyy", DateTimeHelper.getLocale(strings.isBangla))
    }
    val isOverdue = receivable.dueDateMillis != null &&
        System.currentTimeMillis() > receivable.dueDateMillis &&
        !receivable.isSettled

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.weight(1f, fill = false)) {
            if (receivable.dueDateMillis != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.CalendarToday,
                        contentDescription = null,
                        tint = if (isOverdue) ColorDangerExpense else ColorTextMuted,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    val formattedDue = dateFormat.format(Date(receivable.dueDateMillis))
                    val displayDue = if (strings.isBangla) convertToBengaliDigits(formattedDue) else formattedDue
                    Text(
                        text = if (isOverdue) "${strings.overdueBadge} ($displayDue)"
                        else "${strings.dueDate}: $displayDue",
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = if (isOverdue) ColorDangerExpense else ColorTextSecondary
                    )
                }
            } else {
                Text(
                    text = receivable.notes.ifBlank { "" },
                    fontSize = 11.sp,
                    color = ColorTextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (receivable.phoneNumber.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = ColorSurface,
                    border = BorderStroke(1.dp, ColorBorder),
                    modifier = Modifier.clickable { onCall() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Call, null, tint = ColorAccent, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(strings.callPerson, fontSize = 11.sp, color = ColorAccent, fontWeight = FontWeight.Medium)
                    }
                }
            }

            if (!receivable.isSettled) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = ColorSuccessIncome.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, ColorSuccessIncome.copy(alpha = 0.3f)),
                    modifier = Modifier.clickable { onRecordPayment() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.Paid, null, tint = ColorSuccessIncome, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(strings.recordPayment, fontSize = 11.sp, color = ColorSuccessIncome, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
