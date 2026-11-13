package com.example.expensetracker.ui.market

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.automirrored.outlined.TrendingDown
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.MarketCommodityUiModel
import com.example.expensetracker.theme.HairlineInkStroke
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.TabularNumFeature
import com.example.expensetracker.theme.WeightBold
import com.example.expensetracker.theme.WeightMedium
import com.example.expensetracker.ui.components.convertToBengaliDigits

/**
 * Hero Price Card displaying latest rate and live delta.
 */
@Composable
fun HeroPriceCard(
    commodity: MarketCommodityUiModel,
    currencySymbol: String,
    isBangla: Boolean
) {
    val appColors = LocalAppColors.current

    val currentRateFormatted = if (commodity.currentPrice % 1.0 == 0.0) {
        commodity.currentPrice.toInt().toString()
    } else {
        "%.1f".format(commodity.currentPrice)
    }
    val currentRateText = if (isBangla) convertToBengaliDigits(currentRateFormatted) else currentRateFormatted

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = appColors.cardBackground,
        border = BorderStroke(HairlineInkStroke, appColors.divider),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = if (isBangla) "সর্বশেষ ক্রয় দর" else "Latest Recorded Price",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = WeightMedium,
                    fontSize = 12.sp
                ),
                color = appColors.textSecondary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            SpanStyle(
                                color = appColors.textPrimary,
                                fontSize = 28.sp,
                                fontWeight = WeightBold
                            )
                        ) {
                            append("$currencySymbol$currentRateText")
                        }
                        withStyle(
                            SpanStyle(
                                color = appColors.textSecondary,
                                fontSize = 14.sp,
                                fontWeight = WeightMedium
                            )
                        ) {
                            append(" / ${commodity.defaultUnit}")
                        }
                    },
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontFeatureSettings = TabularNumFeature
                    ),
                    maxLines = 1,
                    softWrap = false,
                    modifier = Modifier.padding(bottom = 2.dp)
                )

                // Trend badge
                if (!commodity.isBenchmarkOnly && commodity.previousPrice != null) {
                    val isUp = commodity.isPriceUp
                    val isDown = commodity.isPriceDown
                    val pillColor = if (isUp) appColors.expense else if (isDown) appColors.income else appColors.textSecondary
                    val pillBg = if (isUp) appColors.expense.copy(alpha = 0.12f)
                    else if (isDown) appColors.income.copy(alpha = 0.12f)
                    else appColors.surfaceVariant

                    val icon = if (isUp) Icons.AutoMirrored.Outlined.TrendingUp else Icons.AutoMirrored.Outlined.TrendingDown

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = pillBg,
                        border = BorderStroke(HairlineInkStroke, pillColor.copy(alpha = 0.35f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(imageVector = icon, contentDescription = null, tint = pillColor, modifier = Modifier.size(14.dp))
                            Text(
                                text = commodity.formatDiff(isBangla),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = WeightBold,
                                    fontSize = 12.sp,
                                    fontFeatureSettings = TabularNumFeature
                                ),
                                color = pillColor
                            )
                        }
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = appColors.surfaceVariant.copy(alpha = 0.6f)
                    ) {
                        Text(
                            text = if (isBangla) "১ম ক্রয়" else "1st buy",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = WeightMedium,
                                fontSize = 12.sp
                            ),
                            color = appColors.textSecondary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            val countText = if (isBangla) convertToBengaliDigits(commodity.purchaseCount.toString()) else commodity.purchaseCount.toString()
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isBangla) "মোট $countText বার ক্রয় রেকর্ড করা হয়েছে"
                else "Logged $countText total purchases",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 12.sp,
                    fontFeatureSettings = TabularNumFeature
                ),
                color = appColors.textSecondary
            )
        }
    }
}

@Composable
fun StatMetricCard(
    label: String,
    rate: Double,
    unit: String,
    currencySymbol: String,
    color: Color,
    isBangla: Boolean,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current

    val formatted = if (rate % 1.0 == 0.0) rate.toInt().toString() else "%.1f".format(rate)
    val rateText = if (isBangla) convertToBengaliDigits(formatted) else formatted

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = appColors.cardBackground,
        border = BorderStroke(HairlineInkStroke, appColors.divider),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = WeightMedium,
                    fontSize = 12.sp
                ),
                color = appColors.textSecondary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "$currencySymbol$rateText",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = WeightBold,
                    fontSize = 15.sp,
                    fontFeatureSettings = TabularNumFeature
                ),
                color = color
            )
            Text(
                text = "/$unit",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp),
                color = appColors.textSecondary.copy(alpha = 0.7f)
            )
        }
    }
}
