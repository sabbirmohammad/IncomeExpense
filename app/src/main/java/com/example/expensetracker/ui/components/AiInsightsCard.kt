package com.example.expensetracker.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.example.expensetracker.data.model.AiInsightItem
import com.example.expensetracker.data.model.CategorySpend
import com.example.expensetracker.data.model.DailySpend
import com.example.expensetracker.data.model.FinancialSummary
import com.example.expensetracker.theme.CardCornerRadius
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorBorder
import com.example.expensetracker.theme.ColorSurface
import com.example.expensetracker.theme.ColorTextMuted
import com.example.expensetracker.theme.ColorTextSecondary
import com.example.expensetracker.theme.ColorWarningBills

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

    val validDays = remember(recentDailySpends) { recentDailySpends.filter { it.expense > 0 } }
    val avgDailySpend = remember(validDays) {
        if (validDays.isNotEmpty()) validDays.map { it.expense }.average() else 0.0
    }

    val topCat = remember(categorySpends) {
        categorySpends.filter { !it.categoryName.equals("Loan & Debt Given", ignoreCase = true) }
            .maxByOrNull { it.totalAmount }
    }
    val topCatPct = remember(totalExpense, topCat) {
        if (totalExpense > 0 && topCat != null) {
            ((topCat.totalAmount / totalExpense) * 100).toInt().coerceIn(1, 100)
        } else 0
    }

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
            AiInsightsHeader(
                title = strings.aiInsightsTitle,
                actionLabel = strings.actionAiAdvisor,
                isLoading = isLoading,
                onRefresh = onRefresh,
                onOpenAdvisorChat = onOpenAdvisorChat,
                haptics = haptics
            )

            Spacer(modifier = Modifier.height(14.dp))

            AiInsightsKpiStrip(
                avgDailySpend = avgDailySpend,
                topCat = topCat,
                topCatPct = topCatPct,
                summary = summary,
                savingsRate = savingsRate,
                currencySymbol = currencySymbol,
                isBangla = strings.isBangla
            )

            if (totalExpense > 0 && categorySpends.isNotEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))
                CategoryDistributionSection(
                    categorySpends = categorySpends,
                    totalExpense = totalExpense,
                    isBangla = strings.isBangla
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            AiInsightsCapsule(
                insights = insights,
                totalExpense = totalExpense,
                topCatName = topCat?.categoryName,
                isBangla = strings.isBangla,
                onClick = {
                    haptics.lightTick()
                    onOpenAdvisorChat()
                }
            )
        }
    }
}

@Composable
private fun AiInsightsCapsule(
    insights: List<AiInsightItem>,
    totalExpense: Double,
    topCatName: String?,
    isBangla: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
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
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
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
                    if (isBangla) "খরচের গতি স্বাভাবিক • শীর্ষ খাত: ${topCatName ?: "সাধারণ"} • বাজেট সুরক্ষিত"
                    else "Spending velocity normal • Top sector: ${topCatName ?: "General"} • Budget safe"
                } else {
                    if (isBangla) "চলতি মাসে কোনো খরচ নেই • বাজেট ১০০% অক্ষত • ক্যাশফ্লো স্থিতিশীল"
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
