package com.example.expensetracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.TrendingDown
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.AiInsightItem
import com.example.expensetracker.data.model.CategorySpend
import com.example.expensetracker.data.model.DailySpend
import com.example.expensetracker.data.model.FinancialSummary
import com.example.expensetracker.theme.CardCornerRadius
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorBorder
import com.example.expensetracker.theme.ColorDangerExpense
import com.example.expensetracker.theme.ColorSuccessIncome
import com.example.expensetracker.theme.ColorSurface
import com.example.expensetracker.theme.ColorTextMuted
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.theme.ColorTextSecondary
import com.example.expensetracker.theme.ColorWarningBills
import com.example.expensetracker.theme.SmallElementRadius
import java.util.Calendar

private val SegmentColors = listOf(
    Color(0xFF38BDF8), // Sky Blue
    Color(0xFFF472B6), // Rose Pink
    Color(0xFF34D399), // Emerald
    Color(0xFFA78BFA), // Violet
    Color(0xFFFBBF24), // Amber
    Color(0xFF94A3B8)  // Muted Slate
)

@Composable
fun AiInsightsCard(
    insights: List<AiInsightItem>,
    isLoading: Boolean,
    onRefresh: () -> Unit,
    onOpenAdvisorChat: () -> Unit,
    summary: FinancialSummary? = null,
    categorySpends: List<CategorySpend> = emptyList(),
    recentDailySpends: List<DailySpend> = emptyList(),
    currencySymbol: String = "৳",
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val haptics = rememberAppHaptics()

    val totalExpense = summary?.totalExpense ?: 0.0
    val totalIncome = summary?.totalIncome ?: 0.0

    // Filter valid daily spends
    val validDays = remember(recentDailySpends) { recentDailySpends.filter { it.expense > 0 } }
    val avgDailySpend = remember(validDays) {
        if (validDays.isNotEmpty()) validDays.map { it.expense }.average() else 0.0
    }

    // Top Category
    val topCat = remember(categorySpends) {
        categorySpends.filter { !it.categoryName.equals("Loan & Debt Given", ignoreCase = true) }
            .maxByOrNull { it.totalAmount }
    }
    val topCatPct = remember(totalExpense, topCat) {
        if (totalExpense > 0 && topCat != null) {
            ((topCat.totalAmount / totalExpense) * 100).toInt().coerceIn(1, 100)
        } else 0
    }

    // Savings Rate
    val savingsRate = remember(totalIncome, totalExpense) {
        if (totalIncome > 0) {
            (((totalIncome - totalExpense) / totalIncome) * 100).toInt()
        } else 0
    }

    Surface(
        shape = RoundedCornerShape(CardCornerRadius),
        color = ColorSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, ColorBorder),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 1. Header (Clean, professional, AI Robot intelligence indicator)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(ColorAccent.copy(alpha = 0.22f), Color(0xFF6366F1).copy(alpha = 0.15f))
                                )
                            )
                            .border(1.dp, ColorAccent.copy(alpha = 0.35f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.SmartToy,
                            contentDescription = "AI Insights",
                            tint = ColorAccent,
                            modifier = Modifier.size(19.dp)
                        )
                    }
                    Text(
                        text = strings.aiInsightsTitle,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = ColorTextPrimary
                        )
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            strokeWidth = 2.dp,
                            color = ColorAccent,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    } else {
                        IconButton(
                            onClick = {
                                haptics.lightTick()
                                onRefresh()
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Refresh,
                                contentDescription = "Refresh",
                                tint = ColorTextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                    }

                    // Professional Redesigned Action Pill: AI Robot icon + "পরামর্শ" / "Advisor"
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        ColorAccent.copy(alpha = 0.18f),
                                        Color(0xFF6366F1).copy(alpha = 0.15f)
                                    )
                                )
                            )
                            .border(
                                1.dp,
                                ColorAccent.copy(alpha = 0.40f),
                                RoundedCornerShape(14.dp)
                            )
                            .clickable {
                                haptics.mediumClick()
                                onOpenAdvisorChat()
                            }
                            .padding(horizontal = 11.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.SmartToy,
                                contentDescription = null,
                                tint = ColorAccent,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = strings.actionAiAdvisor,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = ColorAccent
                                )
                            )
                            Icon(
                                imageVector = Icons.Outlined.ChevronRight,
                                contentDescription = null,
                                tint = ColorAccent.copy(alpha = 0.8f),
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 2. Data-Driven Key Metric Chips (KPI Strip)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Metric 1: Daily Average
                MetricKpiChip(
                    icon = Icons.Outlined.Speed,
                    label = if (strings.isBangla) "দৈনিক গড়" else "Daily Avg",
                    value = formatCurrency(avgDailySpend, currencySymbol),
                    accentColor = ColorAccent,
                    modifier = Modifier.weight(1f)
                )

                // Metric 2: Top Expense Category
                MetricKpiChip(
                    icon = Icons.Outlined.PieChart,
                    label = if (strings.isBangla) "শীর্ষ খাত" else "Top Sector",
                    value = if (topCat != null && topCatPct > 0) "${topCat.categoryName} ($topCatPct%)" else "—",
                    accentColor = ColorWarningBills,
                    modifier = Modifier.weight(1.2f)
                )

                // Metric 3: Budget Status or Savings Rate
                val hasBudget = (summary?.budgetAmount ?: 0.0) > 0
                val (m3Label, m3Val, m3Color) = if (hasBudget) {
                    val spentPct = ((summary?.budgetPercent ?: 0f) * 100).toInt()
                    Triple(
                        if (strings.isBangla) "বাজেট ব্যবহৃত" else "Budget Used",
                        "$spentPct%",
                        if (spentPct > 100) ColorDangerExpense else if (spentPct > 80) ColorWarningBills else ColorSuccessIncome
                    )
                } else {
                    Triple(
                        if (strings.isBangla) "সঞ্চয় হার" else "Savings Rate",
                        "$savingsRate%",
                        if (savingsRate >= 20) ColorSuccessIncome else if (savingsRate >= 0) ColorAccent else ColorDangerExpense
                    )
                }

                MetricKpiChip(
                    icon = if (hasBudget) Icons.Outlined.TrendingDown else Icons.Outlined.Savings,
                    label = m3Label,
                    value = m3Val,
                    accentColor = m3Color,
                    modifier = Modifier.weight(1f)
                )
            }

            // 3. Category Expense Distribution Bar (Segmented Chart)
            if (totalExpense > 0 && categorySpends.isNotEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))
                CategoryDistributionSection(
                    categorySpends = categorySpends,
                    totalExpense = totalExpense,
                    isBangla = strings.isBangla
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 4. Actionable Compact High-Signal AI Intelligence Capsule
            val dynamicInsights = remember(insights) {
                insights.filter {
                    !it.title.contains("বাজেট ও খরচ ট্র্যাকিং") &&
                    !it.title.contains("ক্যাশলেস ও ওয়ালেট ব্যবস্থাপনা") &&
                    !it.title.contains("Budget Discipline") &&
                    !it.title.contains("Wallet Balances")
                }
            }

            val topInsight = dynamicInsights.firstOrNull()
            val isWarning = topInsight?.type == "WARNING"
            val capsuleAccent = if (isWarning) ColorWarningBills else ColorAccent

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = capsuleAccent.copy(alpha = 0.08f),
                border = androidx.compose.foundation.BorderStroke(1.dp, capsuleAccent.copy(alpha = 0.22f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        haptics.lightTick()
                        onOpenAdvisorChat()
                    }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.SmartToy,
                        contentDescription = null,
                        tint = capsuleAccent,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = if (topInsight != null) {
                            "${topInsight.title} · ${topInsight.description.take(60).trimEnd()}…"
                        } else if (totalExpense > 0) {
                            if (strings.isBangla) "খরচের গতি স্বাভাবিক • শীর্ষ খাত: ${topCat?.categoryName ?: "সাধারণ"} • বাজেট সুরক্ষিত"
                            else "Spending velocity normal • Top sector: ${topCat?.categoryName ?: "General"} • Budget safe"
                        } else {
                            if (strings.isBangla) "চলতি মাসে কোনো খরচ নেই • বাজেট ১০০% অক্ষত • ক্যাশফ্লো স্থিতিশীল"
                            else "No expenses this month • Budget 100% intact • Cash flow stable"
                        },
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = ColorTextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Outlined.ChevronRight,
                        contentDescription = null,
                        tint = ColorTextMuted,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

/**
 * Compact KPI Metric Chip for Quick Insights
 */
@Composable
private fun MetricKpiChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White.copy(alpha = 0.03f))
            .border(1.dp, ColorBorder.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
            .padding(horizontal = 8.dp, vertical = 7.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(12.dp)
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        color = ColorTextMuted
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = ColorTextPrimary
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Proportional Segmented Category Expense Distribution Bar
 */
@Composable
private fun CategoryDistributionSection(
    categorySpends: List<CategorySpend>,
    totalExpense: Double,
    isBangla: Boolean
) {
    val topItems = remember(categorySpends) {
        categorySpends.sortedByDescending { it.totalAmount }.take(4)
    }
    val topTotal = remember(topItems) { topItems.sumOf { it.totalAmount } }
    val remainingAmount = remember(totalExpense, topTotal) {
        (totalExpense - topTotal).coerceAtLeast(0.0)
    }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isBangla) "খাতভিত্তিক ব্যয় বণ্টন" else "Expense Distribution",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = ColorTextMuted,
                    fontSize = 11.sp
                )
            )
            Text(
                text = if (isBangla) "সর্বোচ্চ ৪ খাত" else "Top 4 Sectors",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = ColorTextMuted.copy(alpha = 0.7f),
                    fontSize = 10.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Segmented Horizontal Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(7.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.White.copy(alpha = 0.06f))
        ) {
            topItems.forEachIndexed { index, cat ->
                val weight = ((cat.totalAmount / totalExpense) * 100).toFloat().coerceAtLeast(1f)
                val color = SegmentColors[index % SegmentColors.size]
                Box(
                    modifier = Modifier
                        .weight(weight)
                        .fillMaxHeight()
                        .background(color)
                )
                if (index < topItems.size - 1 || remainingAmount > 0) {
                    Spacer(modifier = Modifier.width(1.5.dp))
                }
            }
            if (remainingAmount > 0) {
                val remWeight = ((remainingAmount / totalExpense) * 100).toFloat().coerceAtLeast(1f)
                Box(
                    modifier = Modifier
                        .weight(remWeight)
                        .fillMaxHeight()
                        .background(SegmentColors.last())
                )
            }
        }

        Spacer(modifier = Modifier.height(7.dp))

        // Legend FlowRow to prevent overflowing on smaller screens
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            itemVerticalAlignment = Alignment.CenterVertically
        ) {
            topItems.forEachIndexed { index, cat ->
                val pct = ((cat.totalAmount / totalExpense) * 100).toInt().coerceIn(1, 100)
                val color = SegmentColors[index % SegmentColors.size]
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(color)
                    )
                    Text(
                        text = "${cat.categoryName} $pct%",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            color = ColorTextSecondary
                        ),
                        maxLines = 1
                    )
                }
            }
            if (remainingAmount > 0) {
                val remPct = ((remainingAmount / totalExpense) * 100).toInt().coerceIn(1, 100)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(SegmentColors.last())
                    )
                    Text(
                        text = if (isBangla) "অন্যান্য $remPct%" else "Other $remPct%",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            color = ColorTextSecondary
                        ),
                        maxLines = 1
                    )
                }
            }
        }
    }
}

/**
 * 7-Day Spending Velocity Mini Bar Chart
 */
@Composable
private fun DailyVelocityChartSection(
    dailySpends: List<DailySpend>,
    currencySymbol: String,
    isBangla: Boolean
) {
    val maxSpend = remember(dailySpends) {
        dailySpends.maxOfOrNull { it.expense }?.coerceAtLeast(1.0) ?: 1.0
    }

    Column {
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

        // Micro-bar chart container
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
private fun BudgetPaceGaugeSection(
    summary: FinancialSummary,
    currencySymbol: String,
    isBangla: Boolean
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

    Column {
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

        // Dual progress comparison bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.White.copy(alpha = 0.06f))
        ) {
            // Calendar Days elapsed marker (light dashed or faint background)
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = (monthProgress / 100f).coerceIn(0.01f, 1f))
                    .fillMaxHeight()
                    .background(ColorTextMuted.copy(alpha = 0.15f))
            )
            // Budget Spent bar
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

