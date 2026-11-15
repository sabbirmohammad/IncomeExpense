package com.example.expensetracker.ui.market

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.HairlineInkStroke
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.TabularNumFeature
import com.example.expensetracker.theme.WeightBold
import com.example.expensetracker.theme.WeightMedium
import com.example.expensetracker.ui.components.convertToBengaliDigits

@Composable
fun StatMetricCard(
    label: String,
    rate: Double,
    unit: String,
    currencySymbol: String,
    color: Color,
    isBangla: Boolean,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current

    val formatted = if (rate % 1.0 == 0.0) rate.toInt().toString() else "%.1f".format(rate)
    val rateText = if (isBangla) convertToBengaliDigits(formatted) else formatted

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = appColors.cardBackground,
        border = BorderStroke(HairlineInkStroke, appColors.divider),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = WeightMedium,
                    fontSize = 12.sp
                ),
                color = appColors.textSecondary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "$currencySymbol$rateText",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = WeightBold,
                    fontSize = 15.sp,
                    fontFeatureSettings = TabularNumFeature
                ),
                color = color
            )
            Text(
                text = "/$unit",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp),
                color = appColors.textSecondary.copy(alpha = 0.7f)
            )
        }
    }
}
