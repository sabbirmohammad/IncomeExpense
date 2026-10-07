package com.example.expensetracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.TrendingDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.CategorySpend
import com.example.expensetracker.data.model.FinancialSummary
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorBorder
import com.example.expensetracker.theme.ColorDangerExpense
import com.example.expensetracker.theme.ColorSuccessIncome
import com.example.expensetracker.theme.ColorTextMuted
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.theme.ColorWarningBills

@Composable
fun MetricKpiChip(
    icon: ImageVector,
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

@Composable
fun AiInsightsKpiStrip(
    avgDailySpend: Double,
    topCat: CategorySpend?,
    topCatPct: Int,
    summary: FinancialSummary?,
    savingsRate: Int,
    currencySymbol: String,
    isBangla: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        MetricKpiChip(
            icon = Icons.Outlined.Speed,
            label = if (isBangla) "দৈনিক গড়" else "Daily Avg",
            value = formatCurrency(avgDailySpend, currencySymbol),
            accentColor = ColorAccent,
            modifier = Modifier.weight(1f)
        )

        MetricKpiChip(
            icon = Icons.Outlined.PieChart,
            label = if (isBangla) "শীর্ষ খাত" else "Top Sector",
            value = if (topCat != null && topCatPct > 0) "${topCat.categoryName} ($topCatPct%)" else "—",
            accentColor = ColorWarningBills,
            modifier = Modifier.weight(1.2f)
        )

        val hasBudget = (summary?.budgetAmount ?: 0.0) > 0
        val (m3Label, m3Val, m3Color) = if (hasBudget) {
            val spentPct = ((summary?.budgetPercent ?: 0f) * 100).toInt()
            Triple(
                if (isBangla) "বাজেট ব্যবহৃত" else "Budget Used",
                "$spentPct%",
                if (spentPct > 100) ColorDangerExpense else if (spentPct > 80) ColorWarningBills else ColorSuccessIncome
            )
        } else {
            Triple(
                if (isBangla) "সঞ্চয় হার" else "Savings Rate",
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
}
