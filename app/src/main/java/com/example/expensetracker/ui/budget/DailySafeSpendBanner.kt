package com.example.expensetracker.ui.budget

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.formatCurrency
import java.util.Calendar
import com.example.expensetracker.theme.LocalAppColors

@Composable
fun DailySafeSpendBanner(
    overallStatus: CategoryBudgetStatus,
    currencySymbol: String,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val strings = LocalAppStrings.current
    val calendar = remember { Calendar.getInstance() }
    val maxDays = remember { calendar.getActualMaximum(Calendar.DAY_OF_MONTH) }
    val curDay = remember { calendar.get(Calendar.DAY_OF_MONTH) }
    val daysLeft = (maxDays - curDay + 1).coerceAtLeast(1)
    val dailySafeSpend = (overallStatus.remainingAmount / daysLeft).coerceAtLeast(0.0)

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (overallStatus.isOverBudget) appColors.expense.copy(alpha = 0.1f) else appColors.accent.copy(alpha = 0.08f),
        border = BorderStroke(1.dp, if (overallStatus.isOverBudget) appColors.expense.copy(alpha = 0.25f) else appColors.accent.copy(alpha = 0.2f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 7.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.CalendarToday,
                    contentDescription = null,
                    tint = if (overallStatus.isOverBudget) appColors.expense else appColors.accent,
                    modifier = Modifier.size(13.dp)
                )
                Text(
                    text = if (strings.isBangla) "দৈনিক নিরাপদ খরচ:" else "Daily Safe Spend:",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                    color = appColors.textSecondary
                )
            }
            Text(
                text = if (overallStatus.isOverBudget)
                    (if (strings.isBangla) "বাজেট শেষ (আর $daysLeft দিন)" else "Exceeded ($daysLeft days left)")
                else
                    "${formatCurrency(dailySafeSpend, currencySymbol)}/${if (strings.isBangla) "দিন (আর $daysLeft দিন)" else "day ($daysLeft days left)"}",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                color = if (overallStatus.isOverBudget) appColors.expense else appColors.income
            )
        }
    }
}
