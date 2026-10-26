package com.example.expensetracker.ui.receivables

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Payable
import com.example.expensetracker.theme.SmallElementRadius
import com.example.expensetracker.ui.components.DateTimeHelper
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.rememberAppHaptics
import java.text.SimpleDateFormat
import java.util.Date
import com.example.expensetracker.theme.LocalAppColors

@Composable
fun PayableItemBottomBar(
    payable: Payable,
    onRecordPayment: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val context = LocalContext.current
    val strings = LocalAppStrings.current
    val haptics = rememberAppHaptics()
    val isOverdue = payable.dueDateMillis != null &&
            payable.dueDateMillis < System.currentTimeMillis() &&
            !payable.isSettled
    val dateFormat = remember(strings.isBangla) { SimpleDateFormat("dd MMM, yyyy", DateTimeHelper.getLocale(strings.isBangla)) }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (payable.dueDateMillis != null) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.CalendarToday,
                    contentDescription = null,
                    tint = if (isOverdue) appColors.expense else appColors.textSecondary,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                val formattedDueDate = dateFormat.format(Date(payable.dueDateMillis))
                val displayDueDate = if (strings.isBangla) convertToBengaliDigits(formattedDueDate) else formattedDueDate
                Text(
                    text = "${strings.dueDate}: $displayDueDate",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = if (isOverdue) appColors.expense else appColors.textSecondary,
                    fontWeight = if (isOverdue) FontWeight.SemiBold else FontWeight.Normal
                )
            }
        } else {
            Spacer(modifier = Modifier.width(1.dp))
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (payable.phoneNumber.isNotBlank()) {
                IconButton(
                    onClick = {
                        haptics.lightTick()
                        try {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${payable.phoneNumber}"))
                            context.startActivity(intent)
                        } catch (_: Exception) {}
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Call,
                        contentDescription = strings.callPerson,
                        tint = appColors.accent,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
            }

            if (!payable.isSettled) {
                Surface(
                    shape = RoundedCornerShape(SmallElementRadius),
                    color = appColors.income.copy(alpha = 0.15f),
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .clickable {
                            haptics.mediumClick()
                            onRecordPayment()
                        }
                ) {
                    Text(
                        text = strings.recordPayablePayment,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = appColors.income,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }
        }
    }
}
