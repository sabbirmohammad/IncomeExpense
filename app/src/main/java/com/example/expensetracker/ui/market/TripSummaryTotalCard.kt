package com.example.expensetracker.ui.market

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.HairlineInkStroke
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.formatCurrency

@Composable
fun TripSummaryTotalCard(
    totalSpent: Double,
    currencySymbol: String,
    boughtCount: Int,
    skippedCount: Int,
    isBangla: Boolean,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val strings = LocalAppStrings.current

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = appColors.surface,
        border = BorderStroke(HairlineInkStroke, appColors.divider),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "${strings.marketSpent} (${strings.marketTotalPrice})",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = appColors.textSecondary
            )

            Text(
                text = formatCurrency(totalSpent, currencySymbol),
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 34.sp,
                    fontFeatureSettings = "tnum"
                ),
                color = appColors.textPrimary
            )

            Row(
                modifier = Modifier.padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val boughtCountDigits = if (isBangla) convertToBengaliDigits(boughtCount.toString()) else boughtCount.toString()
                val skippedCountDigits = if (isBangla) convertToBengaliDigits(skippedCount.toString()) else skippedCount.toString()

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = appColors.income.copy(alpha = 0.12f),
                    border = BorderStroke(HairlineInkStroke, appColors.income.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = "✓ $boughtCountDigits ${strings.marketBought}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFeatureSettings = "tnum"
                        ),
                        color = appColors.income,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                if (skippedCount > 0) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = appColors.textSecondary.copy(alpha = 0.12f),
                        border = BorderStroke(HairlineInkStroke, appColors.divider)
                    ) {
                        Text(
                            text = "$skippedCountDigits ${strings.marketLeft}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Normal,
                                fontFeatureSettings = "tnum"
                            ),
                            color = appColors.textSecondary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
