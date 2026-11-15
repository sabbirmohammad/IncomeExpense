package com.example.expensetracker.ui.savings

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.SavingsGoal
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.formatCurrency
import com.example.expensetracker.ui.components.globalBengaliNumeralsEnabled
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun SavingsGoalTargetDateOrRemaining(
    goal: SavingsGoal,
    isPrivacyMode: Boolean,
    currencySymbol: String,
    dateFormat: SimpleDateFormat,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val strings = LocalAppStrings.current

    if (goal.targetDateMillis != null) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.CalendarToday,
                contentDescription = null,
                tint = appColors.textMuted,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            val formattedDate = dateFormat.format(Date(goal.targetDateMillis))
            Text(
                text = if (strings.isBangla) convertToBengaliDigits(formattedDate) else formattedDate,
                fontSize = 11.sp,
                color = appColors.textSecondary
            )
        }
    } else {
        Text(
            text = if (goal.remainingAmount > 0) {
                "${if (strings.isBangla) "বাকি" else "Left"}: ${if (isPrivacyMode) "••••" else formatCurrency(goal.remainingAmount, currencySymbol, globalBengaliNumeralsEnabled || strings.isBangla)}"
            } else {
                strings.goalReachedCelebration
            },
            fontSize = 11.sp,
            color = if (goal.remainingAmount <= 0) appColors.income else appColors.textMuted,
            modifier = modifier
        )
    }
}
