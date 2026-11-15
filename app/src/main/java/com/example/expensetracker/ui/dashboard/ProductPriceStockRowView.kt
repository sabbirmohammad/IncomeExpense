package com.example.expensetracker.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.TrendingDown
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.ProductPriceInsight
import com.example.expensetracker.theme.*
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.DateTimeHelper

@Composable
fun ProductPriceStockRowView(
    insight: ProductPriceInsight,
    isBangla: Boolean,
    currencySymbol: String = "৳",
    onClick: () -> Unit
) {
    val appColors = LocalAppColors.current

    val currentRateFormatted = if (insight.currentUnitPrice % 1.0 == 0.0) insight.currentUnitPrice.toInt().toString() else "%.1f".format(insight.currentUnitPrice)
    val currentRateText = if (isBangla) convertToBengaliDigits(currentRateFormatted) else currentRateFormatted

    val avgRateFormatted = if (insight.avgUnitPrice % 1.0 == 0.0) insight.avgUnitPrice.toInt().toString() else "%.1f".format(insight.avgUnitPrice)
    val avgRateText = if (isBangla) convertToBengaliDigits(avgRateFormatted) else avgRateFormatted

    val purchaseCountText = if (isBangla) convertToBengaliDigits(insight.purchaseCount.toString()) else insight.purchaseCount.toString()

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = appColors.cardBackground,
        border = BorderStroke(HairlineInkStroke, appColors.divider),
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 48.dp)
            .minimumInteractiveComponentSize()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = insight.productName.replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = WeightBold, fontSize = 15.sp),
                        color = appColors.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (insight.purchaseCount <= 1) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = appColors.accent.copy(alpha = 0.12f),
                            border = BorderStroke(HairlineInkStroke, appColors.accent.copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = if (isBangla) "নতুন" else "New",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp, fontWeight = WeightBold),
                                color = appColors.accent,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = if (isBangla) "$purchaseCountText বার কেনা • গড় $currencySymbol$avgRateText/${insight.baseUnit}"
                           else "$purchaseCountText logged • Avg $currencySymbol$avgRateText/${insight.baseUnit}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        fontWeight = WeightRegular,
                        fontFeatureSettings = TabularNumFeature
                    ),
                    color = appColors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ProductPriceSparkline(insight = insight, modifier = Modifier.width(42.dp).height(24.dp))

                Column(horizontalAlignment = Alignment.End) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "$currencySymbol$currentRateText",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = WeightBold,
                                fontSize = 15.sp,
                                fontFeatureSettings = TabularNumFeature
                            ),
                            color = appColors.textPrimary
                        )
                        Text(
                            text = "/${insight.baseUnit}",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp, fontWeight = WeightMedium),
                            color = appColors.textSecondary,
                            modifier = Modifier.padding(bottom = 1.dp)
                        )
                    }

                    val dateText = DateTimeHelper.formatDayGroup(insight.lastPurchasedTimestamp, isBangla)
                    Text(
                        text = dateText,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 12.sp,
                            fontWeight = WeightRegular,
                            fontFeatureSettings = TabularNumFeature
                        ),
                        color = appColors.textSecondary.copy(alpha = 0.75f)
                    )
                }

                if (insight.previousUnitPrice != null) {
                    val isUp = insight.isPriceUp
                    val isDown = insight.isPriceDown
                    val pillBg = if (isUp) appColors.expense.copy(alpha = 0.12f)
                                 else if (isDown) appColors.income.copy(alpha = 0.12f)
                                 else appColors.surfaceVariant
                    val pillColor = if (isUp) appColors.expense else if (isDown) appColors.income else appColors.textSecondary
                    val iconVector = if (isUp) Icons.AutoMirrored.Outlined.TrendingUp
                                     else if (isDown) Icons.AutoMirrored.Outlined.TrendingDown
                                     else null

                    val diffAbs = kotlin.math.abs(insight.priceDiff)
                    val diffFormatted = if (diffAbs % 1.0 == 0.0) diffAbs.toInt().toString() else "%.1f".format(diffAbs)
                    val diffText = if (isBangla) convertToBengaliDigits(diffFormatted) else diffFormatted
                    val safePct = if (insight.percentDiff.isNaN() || insight.percentDiff.isInfinite()) 0.0 else kotlin.math.abs(insight.percentDiff)
                    val pctText = if (isBangla) convertToBengaliDigits("%.1f".format(safePct)) else "%.1f".format(safePct)
                    val sign = if (isUp) "+" else if (isDown) "-" else ""

                    Surface(
                        shape = RoundedCornerShape(8.dp), color = pillBg,
                        border = BorderStroke(HairlineInkStroke, pillColor.copy(alpha = 0.3f)),
                        modifier = Modifier.defaultMinSize(minWidth = 64.dp)
                    ) {
                        Row(modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                            if (iconVector != null) {
                                Icon(imageVector = iconVector, contentDescription = null, tint = pillColor, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                            }
                            Text(
                                text = "$sign$diffText ($pctText%)",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = WeightBold, fontSize = 12.sp, fontFeatureSettings = TabularNumFeature),
                                color = pillColor
                            )
                        }
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(8.dp), color = appColors.surfaceVariant.copy(alpha = 0.6f),
                        border = BorderStroke(HairlineInkStroke, appColors.divider), modifier = Modifier.defaultMinSize(minWidth = 56.dp)
                    ) {
                        Text(
                            text = if (isBangla) "১ম ক্রয়" else "1st buy",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = WeightMedium, fontSize = 12.sp),
                            color = appColors.textSecondary, textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
