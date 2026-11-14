package com.example.expensetracker.ui.market

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.MarketCommodityUiModel
import com.example.expensetracker.data.model.PriceTrend
import com.example.expensetracker.data.model.ProductPriceRecord
import com.example.expensetracker.theme.HairlineInkStroke
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.WeightSemiBold

/**
 * Interactive Price Chart with tap tooltip.
 */
@Composable
fun InteractivePriceChartCard(
    records: List<ProductPriceRecord>,
    commodity: MarketCommodityUiModel,
    currencySymbol: String,
    isBangla: Boolean
) {
    val appColors = LocalAppColors.current

    val chronological = remember(records) { records.sortedBy { it.timestamp } }
    val points = remember(chronological, commodity.recentPricePoints) {
        if (chronological.isNotEmpty()) {
            chronological.map { it.unitPrice }
        } else if (commodity.recentPricePoints.isNotEmpty()) {
            commodity.recentPricePoints
        } else {
            listOf(commodity.currentPrice, commodity.currentPrice)
        }
    }

    var selectedIndex by remember(points) { mutableStateOf<Int?>(null) }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = appColors.cardBackground,
        border = BorderStroke(HairlineInkStroke, appColors.divider),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (isBangla) "মূল্যের গতিপ্রকৃতি" else "Price Trajectory",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = WeightSemiBold,
                        fontSize = 13.5.sp
                    ),
                    color = appColors.textPrimary
                )

                Text(
                    text = if (isBangla) "বিন্দু স্পর্শ করুন" else "Tap a point for details",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 12.sp
                    ),
                    color = appColors.textSecondary.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            val idx = selectedIndex ?: 0
            val record = chronological.getOrNull(idx)
            val rate = points.getOrNull(idx) ?: commodity.currentPrice

            PriceChartTooltip(
                visible = selectedIndex != null && selectedIndex in points.indices,
                record = record,
                rate = rate,
                defaultUnit = commodity.defaultUnit,
                currencySymbol = currencySymbol,
                isBangla = isBangla
            )

            val strokeColor = when (commodity.priceTrend) {
                PriceTrend.UP -> appColors.expense
                PriceTrend.DOWN -> appColors.income
                PriceTrend.STABLE -> appColors.accent
            }

            PriceChartCanvas(
                points = points,
                strokeColor = strokeColor,
                selectedIndex = selectedIndex,
                onSelectIndex = { selectedIndex = it }
            )
        }
    }
}
