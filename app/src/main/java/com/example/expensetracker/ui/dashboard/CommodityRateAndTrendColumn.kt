package com.example.expensetracker.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.MarketCommodityUiModel
import com.example.expensetracker.theme.*

@Composable
fun CommodityRateAndTrendColumn(
    commodity: MarketCommodityUiModel,
    currentRateText: String,
    currencySymbol: String,
    isBangla: Boolean,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current

    Column(horizontalAlignment = Alignment.End, modifier = modifier) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = "$currencySymbol$currentRateText",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = WeightBold,
                    fontSize = 14.5.sp,
                    fontFeatureSettings = TabularNumFeature
                ),
                color = appColors.textPrimary
            )
            Text(
                text = "/${commodity.defaultUnit}",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp, fontWeight = WeightMedium),
                color = appColors.textSecondary,
                modifier = Modifier.padding(bottom = 1.dp)
            )
        }

        if (!commodity.isBenchmarkOnly && commodity.previousPrice != null) {
            if (commodity.priceDiff != 0.0) {
                val isUp = commodity.isPriceUp
                val isDown = commodity.isPriceDown
                val pillColor = if (isUp) appColors.expense else if (isDown) appColors.income else appColors.textSecondary
                val pillBg = if (isUp) appColors.expense.copy(alpha = 0.12f)
                else if (isDown) appColors.income.copy(alpha = 0.12f)
                else appColors.surfaceVariant

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = pillBg,
                    modifier = Modifier.defaultMinSize(minWidth = 52.dp)
                ) {
                    Text(
                        text = commodity.formatDiff(isBangla),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = WeightBold,
                            fontSize = 12.sp,
                            fontFeatureSettings = TabularNumFeature
                        ),
                        color = pillColor,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }
        } else {
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = appColors.surfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier.defaultMinSize(minWidth = 50.dp)
            ) {
                Text(
                    text = if (commodity.previousPrice == null) (if (isBangla) "১ম ক্রয়" else "1st buy") else commodity.priceTier.titleBn,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = WeightMedium,
                        fontSize = 12.sp
                    ),
                    color = appColors.textSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
        }
    }
}
