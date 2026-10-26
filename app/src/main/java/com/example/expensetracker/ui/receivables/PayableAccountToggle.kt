package com.example.expensetracker.ui.receivables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.CardCornerRadius
import com.example.expensetracker.theme.LocalAppColors

@Composable
fun PayableDepositAccountToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    isBangla: Boolean,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(CardCornerRadius))
            .background(appColors.border.copy(alpha = 0.2f))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = if (isBangla) "ধার নেওয়া টাকা অ্যাকাউন্টে জমা করবেন?" else "Deposit borrowed loan into account?",
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.5.sp, fontWeight = FontWeight.Medium),
                color = appColors.textPrimary
            )
            Text(
                text = if (isBangla) "অ্যাকাউন্টে ইনকাম লেনদেন রেকর্ড হবে" else "Will log an income transaction in account",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.5.sp),
                color = appColors.textSecondary
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = appColors.accent, checkedTrackColor = appColors.accent.copy(alpha = 0.5f))
        )
    }
}

@Composable
fun PayableDeductAccountToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    isBangla: Boolean,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(CardCornerRadius))
            .background(appColors.border.copy(alpha = 0.2f))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = if (isBangla) "অ্যাকাউন্ট থেকে টাকা কাটবেন?" else "Deduct from account?",
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp, fontWeight = FontWeight.Medium),
                color = appColors.textPrimary
            )
            Text(
                text = if (isBangla) "খরচ লেনদেন হিসেবে সংরক্ষিত হবে" else "Will log an expense transaction",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                color = appColors.textSecondary
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = appColors.accent, checkedTrackColor = appColors.accent.copy(alpha = 0.5f))
        )
    }
}
