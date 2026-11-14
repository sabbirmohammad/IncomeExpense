package com.example.expensetracker.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.MarketCommodityUiModel
import com.example.expensetracker.theme.*
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.rememberAppHaptics

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketCommodityStockRow(
    commodity: MarketCommodityUiModel,
    isBangla: Boolean,
    currencySymbol: String = "৳",
    onAddToList: (() -> Unit)? = null,
    onClick: () -> Unit
) {
    val appColors = LocalAppColors.current
    val haptics = rememberAppHaptics()

    val currentRateFormatted = if (commodity.currentPrice % 1.0 == 0.0) commodity.currentPrice.toInt().toString() else "%.1f".format(commodity.currentPrice)
    val currentRateText = if (isBangla) convertToBengaliDigits(currentRateFormatted) else currentRateFormatted

    val subtitleText = if (commodity.isBenchmarkOnly || commodity.previousPrice == null) {
        if (isBangla) "বাজার রেফারেন্স রেট • ${commodity.englishName}" else "Market Reference Rate • ${commodity.englishName}"
    } else {
        val avgFormatted = if (commodity.benchmarkPrice % 1.0 == 0.0) commodity.benchmarkPrice.toInt().toString() else "%.1f".format(commodity.benchmarkPrice)
        val avgText = if (isBangla) convertToBengaliDigits(avgFormatted) else avgFormatted
        val countText = if (isBangla) convertToBengaliDigits(commodity.purchaseCount.toString()) else commodity.purchaseCount.toString()
        if (isBangla) "গড় $currencySymbol$avgText • $countText বার" else "Avg $currencySymbol$avgText • $countText times"
    }

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.StartToEnd) {
                haptics.mediumClick()
                onAddToList?.invoke()
                false
            } else false
        }
    )

    val addToListLabel = if (isBangla) "তালিকায় যোগ করুন" else "Add to list"

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromEndToStart = false,
        enableDismissFromStartToEnd = onAddToList != null,
        backgroundContent = {
            CommoditySwipeAddBackground(dismissState = dismissState, addToListLabel = addToListLabel)
        },
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                if (onAddToList != null) {
                    customActions = listOf(CustomAccessibilityAction(label = addToListLabel) {
                        haptics.mediumClick(); onAddToList(); true
                    })
                }
            }
    ) {
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = appColors.cardBackground,
            border = BorderStroke(HairlineInkStroke, appColors.divider),
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 72.dp)
                .minimumInteractiveComponentSize()
                .clip(RoundedCornerShape(14.dp))
                .clickable { onClick() }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(appColors.accentContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = commodity.name.trim().take(1).uppercase(),
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp, fontWeight = WeightBold),
                        color = appColors.accent
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = commodity.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = WeightBold, fontSize = 14.5.sp),
                            color = appColors.textPrimary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        if (commodity.isBenchmarkOnly) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = appColors.accent.copy(alpha = 0.12f),
                                border = BorderStroke(HairlineInkStroke, appColors.accent.copy(alpha = 0.3f))
                            ) {
                                Text(
                                    text = if (isBangla) "বাজার দর" else "Standard",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp, fontWeight = WeightBold),
                                    color = appColors.accent,
                                    maxLines = 1,
                                    softWrap = false,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = subtitleText,
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

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ProductPriceSparkline(
                        points = commodity.recentPricePoints,
                        trend = commodity.priceTrend,
                        showArrow = false,
                        modifier = Modifier.width(36.dp).height(22.dp)
                    )

                    CommodityRateAndTrendColumn(
                        commodity = commodity,
                        currentRateText = currentRateText,
                        currencySymbol = currencySymbol,
                        isBangla = isBangla
                    )
                }
            }
        }
    }
}
