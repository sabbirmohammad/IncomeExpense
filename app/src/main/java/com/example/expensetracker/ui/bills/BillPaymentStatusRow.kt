package com.example.expensetracker.ui.bills

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.BillPaymentStatus
import com.example.expensetracker.data.model.RecurringBill
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.theme.LocalAppColors

@Composable
fun BillPaymentStatusRow(
    bill: RecurringBill,
    onPayNow: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val strings = LocalAppStrings.current
    val paymentStatus = bill.getPaymentStatus()
    val daysUntilDue = bill.getDaysUntilDue()
    val isDueSoon = paymentStatus == BillPaymentStatus.DUE_SOON
    val isOverdue = paymentStatus == BillPaymentStatus.OVERDUE
    val now = System.currentTimeMillis()
    val isPaidRecently = bill.lastPaidDateMillis != null && (now - bill.lastPaidDateMillis < 28L * 24 * 60 * 60 * 1000) && bill.nextDueDateMillis > now

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val (statusText, statusBg, statusColor) = when {
            isOverdue -> Triple(strings.overdueBadgeText, appColors.expense.copy(alpha = 0.15f), appColors.expense)
            isDueSoon -> Triple(
                if (daysUntilDue == 0) (if (strings.isBangla) "আজই প্রদেয়" else "Due Today")
                else (if (strings.isBangla) "$daysUntilDue দিনের মধ্যে প্রদেয়" else "Due in $daysUntilDue days"),
                appColors.warning.copy(alpha = 0.15f),
                appColors.warning
            )
            isPaidRecently -> Triple(
                strings.paidThisMonth,
                appColors.income.copy(alpha = 0.15f),
                appColors.income
            )
            else -> Triple(
                if (strings.isBangla) "আসন্ন (${bill.dueDay} তারিখ)" else "Upcoming (Day ${bill.dueDay})",
                appColors.border.copy(alpha = 0.4f),
                appColors.textSecondary
            )
        }

        Surface(
            shape = RoundedCornerShape(6.dp),
            color = statusBg,
            border = BorderStroke(1.dp, statusColor.copy(alpha = 0.3f)),
            modifier = Modifier.weight(1f, fill = false)
        ) {
            Text(
                text = statusText,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                color = statusColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        if (isPaidRecently) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = appColors.income.copy(alpha = 0.12f),
                border = BorderStroke(1.dp, appColors.income.copy(alpha = 0.25f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = appColors.income,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = if (strings.isBangla) "পরিশোধিত" else "Paid",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = appColors.income
                    )
                }
            }
        } else {
            Button(
                onClick = onPayNow,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isOverdue) appColors.expense else if (isDueSoon) appColors.warning else appColors.accent
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier
                    .defaultMinSize(minHeight = 36.dp)
                    .minimumInteractiveComponentSize()
            ) {
                Icon(
                    imageVector = Icons.Filled.Payment,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = strings.payNow,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
