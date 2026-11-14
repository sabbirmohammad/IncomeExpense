package com.example.expensetracker.ui.accounts

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.formatCurrency

@Composable
fun ReconcileDifferenceCard(
    diff: Double,
    currencySymbol: String,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val strings = LocalAppStrings.current

    val isGain = diff > 0
    val isLoss = diff < 0
    val diffColor = when {
        isGain -> appColors.income
        isLoss -> appColors.expense
        else -> appColors.textMuted
    }
    val diffText = when {
        isGain -> "+ ${formatCurrency(diff, currencySymbol)}"
        isLoss -> "- ${formatCurrency(kotlin.math.abs(diff), currencySymbol)}"
        else -> if (strings.isBangla) "কোনো পার্থক্য নেই" else "0 difference (Balances match)"
    }
    val diffTypeLabel = when {
        isGain -> if (strings.isBangla) "সমন্বয় আয় হিসেবে যোগ হবে" else "Will log as adjustment income"
        isLoss -> if (strings.isBangla) "সমন্বয় ব্যয় হিসেবে বাদ হবে" else "Will log as adjustment expense"
        else -> ""
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = diffColor.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, diffColor.copy(alpha = 0.3f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = strings.balanceDifference,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = appColors.textSecondary
                )
                Text(
                    text = diffText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = diffColor
                )
            }
            if (diffTypeLabel.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = diffTypeLabel,
                    fontSize = 10.5.sp,
                    color = diffColor
                )
            }
        }
    }
}
