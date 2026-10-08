package com.example.expensetracker.ui.reports

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.CategorySpend
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorBorder
import com.example.expensetracker.theme.ColorSurface
import com.example.expensetracker.theme.ColorTextMuted
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.ui.components.CategoryIconBadge
import com.example.expensetracker.ui.components.Strings
import com.example.expensetracker.ui.components.formatCurrency
import java.util.Locale

@Composable
fun ReportCategoryBreakdownCard(
    categorySpends: List<CategorySpend>,
    currencySymbol: String,
    isPrivacyMode: Boolean,
    strings: Strings,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = ColorSurface,
        border = BorderStroke(1.dp, ColorBorder)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = strings.categoryBreakdown,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = ColorTextPrimary
            )

            categorySpends.take(5).forEach { cat ->
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            CategoryIconBadge(
                                iconName = cat.categoryIcon,
                                colorHex = cat.categoryColor,
                                size = 26.dp
                            )
                            Text(
                                text = cat.categoryName,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = ColorTextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = String.format(Locale.US, "%.0f%%", cat.percentage * 100),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ColorTextMuted
                            )
                            Text(
                                text = if (isPrivacyMode) "••••" else formatCurrency(cat.totalAmount, currencySymbol),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = ColorTextPrimary
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(ColorBorder)
                    ) {
                        val barColor = try {
                            Color(android.graphics.Color.parseColor(cat.categoryColor))
                        } catch (_: Exception) {
                            ColorAccent
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(cat.percentage)
                                .height(5.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(barColor)
                        )
                    }
                }
            }
        }
    }
}
