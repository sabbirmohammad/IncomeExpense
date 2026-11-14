package com.example.expensetracker.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.AppColors
import com.example.expensetracker.theme.HairlineInkStroke
import com.example.expensetracker.ui.components.convertToBengaliDigits

@Composable
fun MarketPromptExpressionPreview(
    evaluatedAmount: Double,
    currencySymbol: String,
    isBangla: Boolean,
    appColors: AppColors,
    onApply: () -> Unit,
    modifier: Modifier = Modifier
) {
    Spacer(modifier = Modifier.height(4.dp))
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = appColors.accent.copy(alpha = 0.12f),
        border = BorderStroke(HairlineInkStroke, appColors.accent.copy(alpha = 0.3f)),
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 40.dp)
            .minimumInteractiveComponentSize()
            .clickable(onClick = onApply)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val evalStr = if (evaluatedAmount % 1.0 == 0.0) evaluatedAmount.toInt().toString() else "%.1f".format(evaluatedAmount)
            Text(
                text = "= $currencySymbol ${if (isBangla) convertToBengaliDigits(evalStr) else evalStr}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.5.sp,
                    fontFeatureSettings = "tnum"
                ),
                color = appColors.accent
            )
            Text(
                text = if (isBangla) "ট্যাপ করে বসান" else "Tap to apply",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp),
                color = appColors.textSecondary
            )
        }
    }
}
