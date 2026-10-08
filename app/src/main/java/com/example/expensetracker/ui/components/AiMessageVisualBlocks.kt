package com.example.expensetracker.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorBorder
import com.example.expensetracker.theme.ColorDangerExpense
import com.example.expensetracker.theme.ColorSuccessIncome
import com.example.expensetracker.theme.ColorSurface
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.theme.ColorTextSecondary

@Composable
fun AiMetricsBlockView(block: AiContentBlock.Metrics) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(ColorSurface)
            .border(1.dp, ColorBorder, RoundedCornerShape(10.dp))
            .padding(horizontal = 8.dp, vertical = 7.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        block.items.forEachIndexed { idx, (label, value) ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.5.sp,
                        color = ColorTextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                val valueColor = when {
                    label.contains("আয়", ignoreCase = true) || label.contains("Income", ignoreCase = true) -> ColorSuccessIncome
                    label.contains("ব্যয়", ignoreCase = true) || label.contains("Expense", ignoreCase = true) || value.contains("-") -> ColorDangerExpense
                    else -> ColorAccent
                }
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = valueColor
                    )
                )
            }
            if (idx < block.items.size - 1) {
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(22.dp)
                        .background(ColorBorder)
                )
            }
        }
    }
}

@Composable
fun AiChartBlockView(
    block: AiContentBlock.Chart,
    isBangla: Boolean
) {
    val chartColors = listOf(
        Color(0xFF38BDF8),
        Color(0xFFF472B6),
        Color(0xFF34D399),
        Color(0xFFA78BFA),
        Color(0xFFFBBF24),
        Color(0xFF94A3B8)
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(ColorSurface)
            .border(1.dp, ColorBorder, RoundedCornerShape(10.dp))
            .padding(10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.PieChart,
                contentDescription = null,
                tint = ColorAccent,
                modifier = Modifier.size(13.dp)
            )
            Text(
                text = if (isBangla) "ব্যয় বণ্টন গ্রাফ" else "Spend Distribution",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = ColorTextSecondary
                )
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        val totalVal = block.items.sumOf { it.second.toDouble() }.toFloat().coerceAtLeast(1f)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(9.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(ColorBorder)
        ) {
            block.items.forEachIndexed { i, item ->
                val fraction = (item.second / totalVal).coerceIn(0.01f, 1f)
                val segColor = chartColors[i % chartColors.size]
                Box(
                    modifier = Modifier
                        .weight(fraction)
                        .fillMaxHeight()
                        .background(segColor)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            block.items.take(6).forEachIndexed { i, item ->
                val segColor = chartColors[i % chartColors.size]
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(segColor.copy(alpha = 0.12f))
                        .border(0.5.dp, segColor.copy(alpha = 0.28f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 7.dp, vertical = 3.5.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(segColor)
                    )
                    Text(
                        text = "${item.first} ${item.second.toInt()}%",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = ColorTextPrimary
                        ),
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }
        }
    }
}

@Composable
fun AiTableBlockView(block: AiContentBlock.Table) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = ColorSurface,
        border = BorderStroke(1.dp, ColorBorder),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(8.dp)
        ) {
            block.rows.forEachIndexed { rowIdx, rowCells ->
                Row(
                    modifier = Modifier
                        .background(if (rowIdx == 0) ColorAccent.copy(alpha = 0.10f) else Color.Transparent)
                        .padding(vertical = 4.dp, horizontal = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    rowCells.forEach { cell ->
                        Text(
                            text = cell,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.5.sp,
                                fontWeight = if (rowIdx == 0) FontWeight.Bold else FontWeight.Normal,
                                color = if (rowIdx == 0) ColorTextPrimary else ColorTextSecondary
                            ),
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
                if (rowIdx < block.rows.size - 1) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(0.5.dp)
                            .background(ColorBorder.copy(alpha = 0.5f))
                    )
                }
            }
        }
    }
}
