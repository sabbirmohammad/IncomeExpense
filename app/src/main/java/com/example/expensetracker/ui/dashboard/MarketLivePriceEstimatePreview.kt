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
import com.example.expensetracker.data.model.ProductPriceInsight
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.convertToBengaliDigits

@Composable
fun MarketLivePriceEstimatePreview(
    matchedInsight: ProductPriceInsight,
    estimatedCost: Double,
    currencySymbol: String,
    isBangla: Boolean,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current

    val estFormatted = if (estimatedCost % 1.0 == 0.0) estimatedCost.toLong().toString() else "%.0f".format(estimatedCost)
    val estText = if (isBangla) convertToBengaliDigits(estFormatted) else estFormatted

    val rateStr = if (matchedInsight.currentUnitPrice % 1.0 == 0.0) {
        matchedInsight.currentUnitPrice.toLong().toString()
    } else {
        "%.1f".format(matchedInsight.currentUnitPrice)
    }
    val rateNum = if (isBangla) convertToBengaliDigits(rateStr) else rateStr

    Spacer(modifier = Modifier.height(4.dp))
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (matchedInsight.isPriceUp && matchedInsight.percentDiff >= 20.0) appColors.expense.copy(alpha = 0.08f) else appColors.accent.copy(alpha = 0.08f),
        border = BorderStroke(1.dp, if (matchedInsight.isPriceUp && matchedInsight.percentDiff >= 20.0) appColors.expense.copy(alpha = 0.35f) else appColors.accent.copy(alpha = 0.25f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (isBangla) "পূর্বের দর: $currencySymbol$rateNum/${matchedInsight.baseUnit}" else "Last rate: $currencySymbol$rateNum/${matchedInsight.baseUnit}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 11.sp,
                        fontFeatureSettings = "tnum"
                    ),
                    color = appColors.textSecondary
                )
                Text(
                    text = if (isBangla) "সম্ভাব্য: $currencySymbol$estText" else "Est: $currencySymbol$estText",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.5.sp,
                        fontFeatureSettings = "tnum"
                    ),
                    color = appColors.accent
                )
            }

            if (matchedInsight.isPriceUp && matchedInsight.percentDiff >= 20.0) {
                val pctFormatted = if (matchedInsight.percentDiff % 1.0 == 0.0) matchedInsight.percentDiff.toInt().toString() else "%.0f".format(matchedInsight.percentDiff)
                val pctNum = if (isBangla) convertToBengaliDigits(pctFormatted) else pctFormatted
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (isBangla) "সতর্কতা: গতবারের চেয়ে দর $pctNum% বেশি ছিল" else "Notice: Prior rate jumped by $pctNum%",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFeatureSettings = "tnum"
                    ),
                    color = appColors.expense
                )
            }
        }
    }
}
