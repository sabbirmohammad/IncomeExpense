package com.example.expensetracker.ui.analytics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material.icons.outlined.TrendingDown
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.AnalyticsOverview
import com.example.expensetracker.data.model.UserProfile
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorDangerExpense
import com.example.expensetracker.theme.ColorSuccessIncome
import com.example.expensetracker.theme.ColorTextMuted
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.theme.ColorTextSecondary
import com.example.expensetracker.theme.ColorWarningBills
import com.example.expensetracker.ui.components.AppCard
import com.example.expensetracker.ui.components.Strings
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.formatCurrency
import java.util.Locale

@Composable
fun AnalyticsKpiStrip(
    overview: AnalyticsOverview,
    userProfile: UserProfile,
    strings: Strings,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Card 1: Daily Average
        AppCard(modifier = Modifier.weight(1f), padding = 12.dp) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.TrendingDown,
                        contentDescription = null,
                        tint = ColorAccent,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = if (strings.isBangla) "দৈনিক গড়" else "Daily Avg",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        color = ColorTextSecondary
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatCurrency(overview.dailyAverageSpend, userProfile.currencySymbol),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = ColorTextPrimary,
                    maxLines = 1
                )
                Text(
                    text = if (strings.isBangla) "প্রতিদিন" else "per day",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = ColorTextMuted
                )
            }
        }

        // Card 2: Savings Rate KPI
        val savingsRate = overview.savingsRate
        val savingsColor = when {
            savingsRate >= 20 -> ColorSuccessIncome
            savingsRate >= 5 -> ColorWarningBills
            else -> ColorDangerExpense
        }
        val savingsLabel = when {
            savingsRate >= 20 -> if (strings.isBangla) "স্বাস্থ্যকর" else "Healthy"
            savingsRate >= 5 -> if (strings.isBangla) "মাঝারি" else "Moderate"
            else -> if (strings.isBangla) "কম" else "Low"
        }

        AppCard(modifier = Modifier.weight(1f), padding = 12.dp) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Savings,
                        contentDescription = null,
                        tint = savingsColor,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = if (strings.isBangla) "সঞ্চয়ের হার" else "Savings Rate",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        color = ColorTextSecondary
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${if (strings.isBangla) convertToBengaliDigits(savingsRate.toString()) else savingsRate}%",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = savingsColor
                )
                Text(
                    text = savingsLabel,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = savingsColor
                )
            }
        }

        // Card 3: Period Comparison Trend
        val changePct = overview.periodChangePercent
        val trendText = if (changePct != null) {
            val raw = String.format(Locale.US, "%.1f%%", Math.abs(changePct))
            val formatted = if (strings.isBangla) convertToBengaliDigits(raw) else raw
            if (changePct > 0) "+$formatted" else "-$formatted"
        } else {
            if (strings.isBangla) "স্থিতিশীল" else "Stable"
        }

        val trendColor = if (overview.isPeriodChangeFavorable) ColorSuccessIncome else ColorDangerExpense

        AppCard(modifier = Modifier.weight(1f), padding = 12.dp) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = if (overview.isPeriodChangeFavorable) Icons.Outlined.TrendingDown else Icons.Outlined.TrendingUp,
                        contentDescription = null,
                        tint = trendColor,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = if (strings.isBangla) "পূর্ববর্তী সময়ের তুলনায়" else "Vs Previous",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        color = ColorTextSecondary
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = trendText,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = trendColor,
                    maxLines = 1
                )
                Text(
                    text = if (changePct != null) {
                        if (overview.isPeriodChangeFavorable) (if (strings.isBangla) "অনুকূল" else "Favorable")
                        else (if (strings.isBangla) "বেশি খরচ" else "Higher")
                    } else {
                        if (strings.isBangla) "পূর্ববর্তী তথ্য নেই" else "No past data"
                    },
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = ColorTextMuted
                )
            }
        }
    }
}
