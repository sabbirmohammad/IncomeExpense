package com.example.expensetracker.ui.market

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.HairlineInkStroke
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.convertToBengaliDigits
import kotlin.math.roundToInt

@Composable
fun PriceChangesSection(
    priceChanges: List<PriceChangeInsight>,
    currencySymbol: String,
    isBangla: Boolean,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = appColors.surface,
        border = BorderStroke(HairlineInkStroke, appColors.divider),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = if (isBangla) "দরের উল্লেখযোগ্য পরিবর্তন:" else "Notable Price Shifts:",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                ),
                color = appColors.textPrimary
            )

            for (change in priceChanges) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = change.itemName,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = appColors.textPrimary
                    )

                    val pctStr = change.diffPercentage.roundToInt().toString()
                    val pctDigits = if (isBangla) convertToBengaliDigits(pctStr) else pctStr
                    val diffStr = if (change.diffAmount % 1.0 == 0.0) change.diffAmount.toInt().toString() else "%.1f".format(change.diffAmount)
                    val diffDigits = if (isBangla) convertToBengaliDigits(diffStr) else diffStr

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = if (change.isIncrease) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown,
                            contentDescription = null,
                            tint = if (change.isIncrease) appColors.warning else appColors.income,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "${if (change.isIncrease) "+" else "−"}$currencySymbol$diffDigits ($pctDigits%)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFeatureSettings = "tnum"
                            ),
                            color = if (change.isIncrease) appColors.warning else appColors.income
                        )
                    }
                }
            }
        }
    }
}
