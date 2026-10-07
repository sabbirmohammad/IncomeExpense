package com.example.expensetracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.DailySpend
import com.example.expensetracker.data.model.FinancialSummary
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorSuccessIncome
import com.example.expensetracker.theme.ColorTextMuted
import com.example.expensetracker.theme.ColorWarningBills
import java.util.Calendar

/**
 * 7-Day Spending Velocity Mini Bar Chart
 */
@Composable
fun DailyVelocityChartSection(
    dailySpends: List<DailySpend>,
    currencySymbol: String,
    isBangla: Boolean,
    modifier: Modifier = Modifier
) {
    val maxSpend = remember(dailySpends) {
        dailySpends.maxOfOrNull { it.expense }?.coerceAtLeast(1.0) ?: 1.0
    }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isBangla) "৭ দিনের খরচের গতিপ্রকৃতি" else "7-Day Spending Velocity",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = ColorTextMuted,
                    fontSize = 11.sp
                )
            )
            val peakDay = remember(dailySpends) { dailySpends.maxByOrNull { it.expense } }
            if (peakDay != null && peakDay.expense > 0) {
                Text(
                    text = if (isBangla) "সর্বোচ্চ: ${formatCurrency(peakDay.expense, currencySymbol)}" else "Peak: ${formatCurrency(peakDay.expense, currencySymbol)}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = ColorTextMuted.copy(alpha = 0.7f),
                        fontSize = 10.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White.copy(alpha = 0.02f))
                .padding(horizontal = 6.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            dailySpends.takeLast(7).forEach { day ->
                val ratio = (day.expense / maxSpend).toFloat().coerceIn(0.05f, 1f)
                val isPeak = day.expense > 0 && day.expense >= maxSpend * 0.95
                val barColor = when {
                    isPeak -> ColorWarningBills
                    day.expense > 0 -> ColorAccent
                    else -> Color.White.copy(alpha = 0.10f)
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .width(14.dp)
                            .height((30 * ratio).dp)
                            .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                            .background(barColor)
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = day.dayLabel.take(2),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 9.sp,
                            color = if (isPeak) ColorWarningBills else ColorTextMuted
                        ),
                        maxLines = 1
                    )
                }
            }
        }
    }
}

/**
 * Visual Budget Pace vs Calendar Days Gauge
 */
@Composable
fun BudgetPaceGaugeSection(
    summary: FinancialSummary,
    currencySymbol: String,
    isBangla: Boolean,
    modifier: Modifier = Modifier
) {
    val calendar = remember { Calendar.getInstance() }
    val dayOfMonth = remember { calendar.get(Calendar.DAY_OF_MONTH) }
    val daysInMonth = remember { calendar.getActualMaximum(Calendar.DAY_OF_MONTH) }
    val monthProgress = remember(dayOfMonth, daysInMonth) {
        ((dayOfMonth.toFloat() / daysInMonth.toFloat()) * 100).toInt().coerceIn(1, 100)
    }
    val budgetSpentPct = remember(summary.budgetPercent) {
        (summary.budgetPercent * 100).toInt()
    }

    val isOverPace = budgetSpentPct > monthProgress + 8 || summary.budgetOverspent > 0
    val statusText = when {
        isOverPace -> if (isBangla) "বাজেট গতি বেশি" else "Fast Burn"
        else -> if (isBangla) "পেস স্বাভাবিক" else "On Track"
    }
    val statusColor = if (isOverPace) ColorWarningBills else ColorSuccessIncome

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isBangla) "মাসিক বাজেট পেস ট্র্যাকার" else "Monthly Budget Pace",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = ColorTextMuted,
                    fontSize = 11.sp
                )
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(statusColor)
                )
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = statusColor,
                        fontSize = 10.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.White.copy(alpha = 0.06f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = (monthProgress / 100f).coerceIn(0.01f, 1f))
                    .fillMaxHeight()
                    .background(ColorTextMuted.copy(alpha = 0.15f))
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = (budgetSpentPct / 100f).coerceIn(0.01f, 1f))
                    .fillMaxHeight()
                    .background(statusColor)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = if (isBangla) "মাসের $monthProgress% অতিক্রান্ত" else "$monthProgress% month elapsed",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = ColorTextMuted)
            )
            Text(
                text = if (isBangla) "বাজেট ব্যবহৃত $budgetSpentPct%" else "$budgetSpentPct% budget consumed",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = statusColor)
            )
        }
    }
}
