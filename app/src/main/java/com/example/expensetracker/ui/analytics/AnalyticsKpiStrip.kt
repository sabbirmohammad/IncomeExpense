package com.example.expensetracker.ui.analytics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.TrendingDown
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.AnalyticsOverview
import com.example.expensetracker.data.model.UserProfile
import com.example.expensetracker.theme.LocalAppColors
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
    val appColors = LocalAppColors.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Card 1: Daily Average Spend
        KpiMetricCard(
            modifier = Modifier.weight(1f),
            icon = Icons.AutoMirrored.Outlined.TrendingDown,
            iconTint = appColors.accent,
            title = if (strings.isBangla) "দৈনিক গড়" else "Daily Avg",
            value = formatCurrency(overview.dailyAverageSpend, userProfile.currencySymbol),
            valueColor = appColors.textPrimary,
            subtitle = if (strings.isBangla) "প্রতিদিন ব্যয়" else "per day",
            subtitleColor = appColors.textMuted
        )

        // Card 2: Savings Rate KPI
        val savingsRate = overview.savingsRate
        val savingsColor = when {
            savingsRate >= 20 -> appColors.income
            savingsRate >= 5 -> appColors.warning
            else -> appColors.expense
        }
        val savingsLabel = when {
            savingsRate >= 20 -> if (strings.isBangla) "স্বাস্থ্যকর" else "Healthy"
            savingsRate >= 5 -> if (strings.isBangla) "মাঝারি" else "Moderate"
            else -> if (strings.isBangla) "কম" else "Low"
        }
        KpiMetricCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Outlined.Savings,
            iconTint = savingsColor,
            title = if (strings.isBangla) "সঞ্চয়ের হার" else "Savings Rate",
            value = "${if (strings.isBangla) convertToBengaliDigits(savingsRate.toString()) else savingsRate}%",
            valueColor = savingsColor,
            subtitle = savingsLabel,
            subtitleColor = savingsColor
        )

        // Card 3: Period Comparison Trend
        val changePct = overview.periodChangePercent
        val trendText = if (changePct != null) {
            val raw = String.format(Locale.US, "%.1f%%", Math.abs(changePct))
            val formatted = if (strings.isBangla) convertToBengaliDigits(raw) else raw
            if (changePct > 0) "+$formatted" else "-$formatted"
        } else {
            if (strings.isBangla) "স্থিতিশীল" else "Stable"
        }
        val trendColor = if (overview.isPeriodChangeFavorable) appColors.income else appColors.expense
        val trendLabel = if (changePct != null) {
            if (overview.isPeriodChangeFavorable) (if (strings.isBangla) "অনুকূল" else "Favorable")
            else (if (strings.isBangla) "বেশি ব্যয়" else "Higher")
        } else {
            if (strings.isBangla) "তথ্য নেই" else "No data"
        }
        KpiMetricCard(
            modifier = Modifier.weight(1f),
            icon = if (overview.isPeriodChangeFavorable) Icons.AutoMirrored.Outlined.TrendingDown else Icons.AutoMirrored.Outlined.TrendingUp,
            iconTint = trendColor,
            title = if (strings.isBangla) "আগের তুলনা" else "Vs Previous",
            value = trendText,
            valueColor = trendColor,
            subtitle = trendLabel,
            subtitleColor = appColors.textMuted
        )
    }
}

@Composable
private fun KpiMetricCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    iconTint: Color,
    title: String,
    value: String,
    valueColor: Color,
    subtitle: String,
    subtitleColor: Color
) {
    val appColors = LocalAppColors.current
    AppCard(
        modifier = modifier.fillMaxHeight(),
        padding = 10.dp
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(13.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    color = appColors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.Bold,
                    fontFeatureSettings = "tnum"
                ),
                color = valueColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = subtitleColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
