package com.example.expensetracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.example.expensetracker.data.model.CategorySpend
import com.example.expensetracker.theme.ColorTextMuted
import com.example.expensetracker.theme.ColorTextSecondary

val AiSegmentColors = listOf(
    Color(0xFF38BDF8), // Sky Blue
    Color(0xFFF472B6), // Rose Pink
    Color(0xFF34D399), // Emerald
    Color(0xFFA78BFA), // Violet
    Color(0xFFFBBF24), // Amber
    Color(0xFF94A3B8)  // Muted Slate
)

/**
 * Proportional Segmented Category Expense Distribution Bar
 */
@Composable
fun CategoryDistributionSection(
    categorySpends: List<CategorySpend>,
    totalExpense: Double,
    isBangla: Boolean,
    modifier: Modifier = Modifier
) {
    val topItems = remember(categorySpends) {
        categorySpends.sortedByDescending { it.totalAmount }.take(4)
    }
    val topTotal = remember(topItems) { topItems.sumOf { it.totalAmount } }
    val remainingAmount = remember(totalExpense, topTotal) {
        (totalExpense - topTotal).coerceAtLeast(0.0)
    }

    Column(modifier = modifier) {
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
                val color = AiSegmentColors[index % AiSegmentColors.size]
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
                        .background(AiSegmentColors.last())
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
                val color = AiSegmentColors[index % AiSegmentColors.size]
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
                            .background(AiSegmentColors.last())
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
