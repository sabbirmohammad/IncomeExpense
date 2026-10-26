package com.example.expensetracker.ui.share

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.ui.components.DateTimeHelper
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.theme.LocalAppColors

@Composable
fun ExpenseShareSlipCard(
    periodTitle: String,
    periodSubtitle: String,
    userName: String,
    currencySymbol: String,
    totalExpense: Double,
    totalIncome: Double,
    transactions: List<Transaction>,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val strings = LocalAppStrings.current
    val generatedTime = remember(strings.isBangla) {
        DateTimeHelper.formatFullDateTime(System.currentTimeMillis(), strings.isBangla)
    }

    val backgroundBrush = remember(appColors.isDark) {
        if (appColors.isDark) {
            Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF161C2C),
                    Color(0xFF101422),
                    Color(0xFF0C0F1A)
                )
            )
        } else {
            Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFFFFFFF),
                    Color(0xFFF8FAFC),
                    Color(0xFFF1F5F9)
                )
            )
        }
    }

    Box(
        modifier = modifier
            .width(360.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundBrush)
            .border(1.dp, if (appColors.isDark) Color(0xFF283045) else appColors.border, RoundedCornerShape(20.dp))
            .padding(20.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header & Financial Summary
            SlipHeaderSummarySection(
                periodTitle = periodTitle,
                periodSubtitle = periodSubtitle,
                userName = userName,
                currencySymbol = currencySymbol,
                totalExpense = totalExpense,
                totalIncome = totalIncome,
                strings = strings
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Ticket Perforation / Dashed Divider
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .drawBehind {
                        drawLine(
                            color = if (appColors.isDark) Color(0xFF2E3852) else appColors.borderDashed,
                            start = Offset(0f, 0f),
                            end = Offset(size.width, 0f),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f),
                            strokeWidth = 2f
                        )
                    }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Itemized Transactions List
            SlipItemizedTransactionsList(
                transactions = transactions,
                currencySymbol = currencySymbol,
                strings = strings
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Footer Information
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(0.5.dp)
                    .background(if (appColors.isDark) Color(0xFF232B3E) else appColors.border)
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = generatedTime,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    color = appColors.textMuted
                )
                Text(
                    text = if (strings.isBangla) "${strings.appName} দ্বারা তৈরি" else "Generated by Expense Tracker",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = appColors.accent.copy(alpha = 0.8f)
                )
            }
        }
    }
}
