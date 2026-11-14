package com.example.expensetracker.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.PriceTrend
import com.example.expensetracker.data.model.ProductPriceInsight
import com.example.expensetracker.theme.*
import com.example.expensetracker.ui.components.convertToBengaliDigits

@Composable
fun ProductPriceDetailStatsSection(
    insight: ProductPriceInsight,
    isBangla: Boolean,
    currencySymbol: String,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current

    Column(modifier = modifier) {
        // Expanded Historical Sparkline
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = appColors.cardBackground,
            border = BorderStroke(HairlineInkStroke, appColors.divider),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = if (isBangla) "মূল্য পরিবর্তনের গতিপ্রকৃতি" else "Price Trajectory",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = WeightSemiBold, fontSize = 12.sp),
                        color = appColors.textSecondary
                    )
                    Text(
                        text = if (isBangla) "সর্বশেষ ১০টি রেকর্ড" else "Last 10 records",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp, fontFeatureSettings = TabularNumFeature),
                        color = appColors.textSecondary.copy(alpha = 0.7f)
                    )
                }

                ProductPriceSparkline(insight = insight, modifier = Modifier.width(88.dp).height(32.dp))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 4 Key Metrics Grid (2 x 2)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            PriceMetricCard(
                title = if (isBangla) "সর্বশেষ দর" else "Latest Rate",
                value = insight.formatUnitRate(),
                subValue = if (insight.previousUnitPrice != null) insight.formatDiff() else (if (isBangla) "প্রথম ক্রয়" else "First buy"),
                subColor = if (insight.isPriceUp) appColors.expense else if (insight.isPriceDown) appColors.income else appColors.textSecondary,
                modifier = Modifier.weight(1f)
            )

            PriceMetricCard(
                title = if (isBangla) "গড় দর" else "Average Rate",
                value = insight.formatAvgRate(),
                subValue = if (isBangla) "${convertToBengaliDigits(insight.purchaseCount.toString())} বার ক্রয়ের গড়" else "Based on ${insight.purchaseCount} buys",
                subColor = appColors.accent,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            val minFormatted = if (insight.minUnitPrice % 1.0 == 0.0) insight.minUnitPrice.toInt().toString() else "%.1f".format(insight.minUnitPrice)
            val minText = if (isBangla) convertToBengaliDigits(minFormatted) else minFormatted
            PriceMetricCard(
                title = if (isBangla) "সর্বনিম্ন দর" else "Lowest Rate",
                value = "$currencySymbol$minText/${insight.baseUnit}",
                subValue = if (isBangla) "সর্বকালের সর্বনিম্ন" else "All-time low",
                subColor = appColors.income,
                modifier = Modifier.weight(1f)
            )

            val maxFormatted = if (insight.maxUnitPrice % 1.0 == 0.0) insight.maxUnitPrice.toInt().toString() else "%.1f".format(insight.maxUnitPrice)
            val maxText = if (isBangla) convertToBengaliDigits(maxFormatted) else maxFormatted
            PriceMetricCard(
                title = if (isBangla) "সর্বোচ্চ দর" else "Highest Rate",
                value = "$currencySymbol$maxText/${insight.baseUnit}",
                subValue = if (isBangla) "সর্বকালের সর্বোচ্চ" else "All-time peak",
                subColor = appColors.expense,
                modifier = Modifier.weight(1f)
            )
        }

        val minRate = insight.minUnitPrice
        val maxRate = insight.maxUnitPrice
        val curRate = insight.currentUnitPrice

        if (minRate > 0.0 && maxRate >= minRate) {
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = appColors.cardBackground,
                border = BorderStroke(HairlineInkStroke, appColors.divider),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isBangla) "দরের পরিসীমা (Min - Max Range)" else "Price Range (Min - Max)",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = WeightSemiBold, fontSize = 12.sp),
                            color = appColors.textPrimary
                        )
                        insight.oneMonthPercentDiff?.let { mPct ->
                            val safeMPct = if (mPct.isNaN() || mPct.isInfinite()) 0.0 else kotlin.math.abs(mPct)
                            val mPctAbs = "%.1f".format(safeMPct)
                            val mPctText = if (isBangla) convertToBengaliDigits(mPctAbs) else mPctAbs
                            val mSign = if (mPct > 0) "+ " else "- "
                            val mColor = if (mPct > 0) appColors.expense else appColors.income
                            Text(
                                text = if (isBangla) "৩০ দিনে: $mSign$mPctText%" else "30d: $mSign$mPctText%",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = WeightBold,
                                    fontSize = 12.sp,
                                    fontFeatureSettings = TabularNumFeature
                                ),
                                color = mColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val fraction = if (maxRate > minRate) {
                        ((curRate - minRate) / (maxRate - minRate)).coerceIn(0.0, 1.0).toFloat().coerceIn(0.01f, 1.0f)
                    } else 0.5f

                    Box(
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)).background(appColors.divider.copy(alpha = 0.35f))
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize().background(
                                Brush.horizontalGradient(listOf(appColors.income.copy(alpha = 0.45f), appColors.accent.copy(alpha = 0.3f), appColors.expense.copy(alpha = 0.45f)))
                            )
                        )
                        Box(modifier = Modifier.fillMaxWidth(fraction).fillMaxHeight(), contentAlignment = Alignment.CenterEnd) {
                            Box(
                                modifier = Modifier.size(8.dp).clip(CircleShape).background(
                                    when (insight.priceTrend) {
                                        PriceTrend.UP -> appColors.expense
                                        PriceTrend.DOWN -> appColors.income
                                        PriceTrend.STABLE -> appColors.accent
                                    }
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
