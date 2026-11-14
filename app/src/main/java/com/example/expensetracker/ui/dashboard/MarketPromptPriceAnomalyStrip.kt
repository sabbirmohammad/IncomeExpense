package com.example.expensetracker.ui.dashboard

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.common.UnitConverterEngine
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.data.model.ProductPriceInsight
import com.example.expensetracker.theme.AppColors
import com.example.expensetracker.theme.HairlineInkStroke
import com.example.expensetracker.ui.components.convertToBengaliDigits

@Composable
fun MarketPromptPriceAnomalyStrip(
    item: MarketItem,
    evaluatedAmount: Double?,
    percentDiff: Double?,
    diffRate: Double?,
    priceInsight: ProductPriceInsight?,
    isBangla: Boolean,
    appColors: AppColors,
    modifier: Modifier = Modifier
) {
    if (evaluatedAmount == null || evaluatedAmount <= 0 || item.quantity <= 0) return

    val (currentRate, baseUnit) = UnitConverterEngine.calculateBaseUnitPrice(evaluatedAmount, item.quantity, item.unit)
    val rateStr = if (currentRate % 1.0 == 0.0) currentRate.toInt().toString() else "%.1f".format(currentRate)
    val rateText = if (isBangla) convertToBengaliDigits(rateStr) else rateStr

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = when {
            percentDiff != null && percentDiff >= 1.0 -> appColors.expense.copy(alpha = 0.08f)
            percentDiff != null && percentDiff <= -1.0 -> appColors.income.copy(alpha = 0.08f)
            else -> appColors.accent.copy(alpha = 0.08f)
        },
        border = BorderStroke(
            HairlineInkStroke,
            when {
                percentDiff != null && percentDiff >= 1.0 -> appColors.expense.copy(alpha = 0.25f)
                percentDiff != null && percentDiff <= -1.0 -> appColors.income.copy(alpha = 0.25f)
                else -> appColors.divider
            }
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "⚡ ৳$rateText/$baseUnit",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        fontFeatureSettings = "tnum"
                    ),
                    color = appColors.textPrimary
                )

                if (percentDiff != null && diffRate != null) {
                    val diffAbs = kotlin.math.abs(diffRate)
                    val diffAbsStr = if (diffAbs % 1.0 == 0.0) diffAbs.toInt().toString() else "%.1f".format(diffAbs)
                    val diffText = if (isBangla) convertToBengaliDigits(diffAbsStr) else diffAbsStr
                    val pctAbsStr = "%.1f".format(kotlin.math.abs(percentDiff))
                    val pctText = if (isBangla) convertToBengaliDigits(pctAbsStr) else pctAbsStr

                    val (varianceText, varianceColor) = when {
                        percentDiff >= 1.0 -> (if (isBangla) "🔺 +৳$diffText/$baseUnit (+$pctText%)" else "🔺 +৳$diffText (+$pctText%)") to appColors.expense
                        percentDiff <= -1.0 -> (if (isBangla) "🔻 ৳$diffText সাশ্রয়ী (-$pctText%)" else "🔻 -৳$diffText (-$pctText%)") to appColors.income
                        else -> (if (isBangla) "➖ পূর্বের দরেই কেনা হচ্ছে" else "➖ Same rate") to appColors.textSecondary
                    }
                    Text(
                        text = varianceText,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            fontFeatureSettings = "tnum"
                        ),
                        color = varianceColor
                    )
                } else {
                    Text(
                        text = if (isBangla) "হিসাবকৃত প্রতি $baseUnit" else "Calculated rate",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 12.sp,
                            fontFeatureSettings = "tnum"
                        ),
                        color = appColors.textSecondary
                    )
                }
            }

            priceInsight?.oneMonthPercentDiff?.let { mPct ->
                val mPctAbs = "%.1f".format(kotlin.math.abs(mPct))
                val mPctText = if (isBangla) convertToBengaliDigits(mPctAbs) else mPctAbs
                val mSign = if (mPct > 0) "+" else "-"
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (isBangla) "📅 গত ১ মাসে পরিবর্তন: $mSign$mPctText%" else "📅 30-day change: $mSign$mPctText%",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 12.sp,
                        fontFeatureSettings = "tnum"
                    ),
                    color = appColors.textSecondary
                )
            }
        }
    }
}
