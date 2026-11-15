package com.example.expensetracker.ui.market

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.common.UnitConverterEngine
import com.example.expensetracker.theme.HairlineInkStroke
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.formatCurrency
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * Display card for live entered price, computed counterpart (unit rate or total), and price delta variance vs last buy.
 */
@Composable
fun MarketPriceVarianceDisplay(
    priceMode: Int,
    unit: String,
    quantity: Double,
    expression: String,
    currencySymbol: String,
    isBangla: Boolean,
    totalAmountComputed: Double,
    unitRateComputed: Double,
    baselineUnitPrice: Double?,
    varianceDiff: Double?,
    variancePercent: Double?,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val strings = LocalAppStrings.current

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = appColors.surface,
        border = BorderStroke(HairlineInkStroke, appColors.divider),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val label = if (priceMode == 0) "${strings.marketPerUnit} ($unit)" else strings.marketTotalPrice
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = appColors.textSecondary
            )

            // Big Entered Price Display
            val displayExpr = if (expression.isBlank()) "0" else expression
            val displayDigits = if (isBangla) convertToBengaliDigits(displayExpr) else displayExpr
            Text(
                text = "$currencySymbol$displayDigits",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                    fontFeatureSettings = "tnum"
                ),
                color = appColors.textPrimary,
                textAlign = TextAlign.Center
            )

            // Computed Counterpart
            val crossInfo = if (priceMode == 0) {
                val computedStr = formatCurrency(totalAmountComputed, currencySymbol)
                "${strings.marketTotalPrice}: $computedStr"
            } else {
                val rateStr = formatCurrency(unitRateComputed, currencySymbol)
                val norm = UnitConverterEngine.normalize(quantity, unit)
                "${strings.marketPerUnit}: $rateStr/${norm.baseUnit}"
            }

            Text(
                text = crossInfo,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    fontFeatureSettings = "tnum"
                ),
                color = appColors.accent
            )

            // Live Variance Line vs Last Price
            if (varianceDiff != null && baselineUnitPrice != null && baselineUnitPrice > 0) {
                val diffAbs = abs(varianceDiff)
                val diffStr = if (diffAbs % 1.0 == 0.0) diffAbs.toInt().toString() else "%.1f".format(diffAbs)
                val diffDigits = if (isBangla) convertToBengaliDigits(diffStr) else diffStr
                val pctAbs = abs(variancePercent ?: 0.0).roundToInt().toString()
                val pctDigits = if (isBangla) convertToBengaliDigits(pctAbs) else pctAbs

                val varianceText = when {
                    varianceDiff < -0.5 -> {
                        if (isBangla) "▼ ৳$diffDigits গতবারের চেয়ে কম, −$pctDigits%"
                        else "▼ ৳$diffDigits less than last, −$pctDigits%"
                    }
                    varianceDiff > 0.5 -> {
                        if (isBangla) "▲ ৳$diffDigits গতবারের চেয়ে বেশি, +$pctDigits%"
                        else "▲ ৳$diffDigits more than last, +$pctDigits%"
                    }
                    else -> {
                        strings.marketSameAsLast
                    }
                }

                val varianceColor = when {
                    varianceDiff < -0.5 -> appColors.income
                    varianceDiff > 0.5 -> appColors.warning
                    else -> appColors.textSecondary
                }

                Text(
                    text = varianceText,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        fontFeatureSettings = "tnum"
                    ),
                    color = varianceColor,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}
