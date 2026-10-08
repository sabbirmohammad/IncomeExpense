package com.example.expensetracker.ui.reports

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
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
import com.example.expensetracker.data.model.FinancialSummary
import com.example.expensetracker.theme.ColorBorder
import com.example.expensetracker.theme.ColorDangerExpense
import com.example.expensetracker.theme.ColorSuccessIncome
import com.example.expensetracker.theme.ColorSurface
import com.example.expensetracker.theme.ColorTextMuted
import com.example.expensetracker.ui.components.Strings
import com.example.expensetracker.ui.components.formatCurrency

@Composable
fun ReportSummaryKpiSection(
    summary: FinancialSummary,
    currencySymbol: String,
    isPrivacyMode: Boolean,
    strings: Strings
) {
    val savingsRate = if (summary.totalIncome > 0) {
        ((summary.netBalance / summary.totalIncome) * 100).toInt().coerceIn(-999, 100)
    } else 0

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ReportKpiCard(
                title = strings.totalIncomeLabel,
                amount = summary.totalIncome,
                currencySymbol = currencySymbol,
                isPrivacyMode = isPrivacyMode,
                accentColor = ColorSuccessIncome,
                icon = Icons.Outlined.ArrowUpward,
                modifier = Modifier.weight(1f)
            )

            ReportKpiCard(
                title = strings.totalExpenseLabel,
                amount = summary.totalExpense,
                currencySymbol = currencySymbol,
                isPrivacyMode = isPrivacyMode,
                accentColor = ColorDangerExpense,
                icon = Icons.Outlined.ArrowDownward,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ReportKpiCard(
                title = strings.netCashFlowLabel,
                amount = summary.netBalance,
                currencySymbol = currencySymbol,
                isPrivacyMode = isPrivacyMode,
                accentColor = if (summary.netBalance >= 0) Color(0xFF3B82F6) else ColorDangerExpense,
                icon = if (summary.netBalance >= 0) Icons.Outlined.ArrowUpward else Icons.Outlined.ArrowDownward,
                modifier = Modifier.weight(1f)
            )

            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                color = ColorSurface,
                border = BorderStroke(1.dp, ColorBorder)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = strings.savingsRateLabel,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = ColorTextMuted
                    )
                    Text(
                        text = if (isPrivacyMode) "••%" else "$savingsRate%",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (savingsRate >= 0) ColorSuccessIncome else ColorDangerExpense
                    )
                }
            }
        }
    }
}

@Composable
fun ReportKpiCard(
    title: String,
    amount: Double,
    currencySymbol: String,
    isPrivacyMode: Boolean,
    accentColor: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = ColorSurface,
        border = BorderStroke(1.dp, ColorBorder)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(13.dp)
                )
                Text(
                    text = title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = ColorTextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Text(
                text = if (isPrivacyMode) "••••••" else formatCurrency(amount, currencySymbol),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = accentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
