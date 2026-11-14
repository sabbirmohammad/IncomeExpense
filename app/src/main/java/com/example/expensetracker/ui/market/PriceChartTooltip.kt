package com.example.expensetracker.ui.market

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.ProductPriceRecord
import com.example.expensetracker.theme.HairlineInkStroke
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.TabularNumFeature
import com.example.expensetracker.theme.WeightBold
import com.example.expensetracker.theme.WeightMedium
import com.example.expensetracker.ui.components.DateTimeHelper
import com.example.expensetracker.ui.components.convertToBengaliDigits

@Composable
fun PriceChartTooltip(
    visible: Boolean,
    record: ProductPriceRecord?,
    rate: Double,
    defaultUnit: String,
    currencySymbol: String,
    isBangla: Boolean,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier
    ) {
        val rateFormatted = if (rate % 1.0 == 0.0) rate.toInt().toString() else "%.1f".format(rate)
        val rateText = if (isBangla) convertToBengaliDigits(rateFormatted) else rateFormatted
        val dateText = if (record != null) DateTimeHelper.formatDayGroup(record.timestamp, isBangla) else ""
        val qtyText = if (record != null) {
            val q = if (record.rawQuantity % 1.0 == 0.0) record.rawQuantity.toInt().toString() else "%.1f".format(record.rawQuantity)
            val qNum = if (isBangla) convertToBengaliDigits(q) else q
            "$qNum ${record.rawUnit}"
        } else ""

        Surface(
            shape = RoundedCornerShape(8.dp),
            color = appColors.accent.copy(alpha = 0.10f),
            border = BorderStroke(HairlineInkStroke, appColors.accent.copy(alpha = 0.35f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    if (dateText.isNotBlank()) {
                        Text(
                            text = dateText,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 12.sp,
                                fontWeight = WeightMedium
                            ),
                            color = appColors.textPrimary
                        )
                    }
                    if (qtyText.isNotBlank()) {
                        Text(
                            text = qtyText,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp),
                            color = appColors.textSecondary
                        )
                    }
                }

                Text(
                    text = "$currencySymbol$rateText/$defaultUnit",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = WeightBold,
                        fontSize = 13.5.sp,
                        fontFeatureSettings = TabularNumFeature
                    ),
                    color = appColors.accent
                )
            }
        }
    }
}
