package com.example.expensetracker.ui.budget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.AppProgressBar
import com.example.expensetracker.ui.components.Strings
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.formatCurrency

@Composable
fun OverallBudgetProgressSection(
    overallStatus: CategoryBudgetStatus,
    currencySymbol: String,
    strings: Strings,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current

    val statusFillColor = when {
        overallStatus.isOverBudget || overallStatus.progressPercent >= 1.0f -> appColors.expense
        overallStatus.progressPercent >= 0.75f -> appColors.warning
        else -> appColors.accent
    }

    Column(modifier = modifier.fillMaxWidth()) {
        AppProgressBar(
            progress = overallStatus.progressPercent,
            height = 6.dp,
            fillColor = statusFillColor
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val overallPctInt = (overallStatus.progressPercent * 100).toInt()
            val overallPctDisplay = if (strings.isBangla) "${convertToBengaliDigits(overallPctInt.toString())}% ব্যবহৃত" else "$overallPctInt% used"
            Text(
                text = if (overallStatus.isOverBudget)
                    "${strings.overBudgetBy} ${formatCurrency(overallStatus.spentAmount - overallStatus.budget.amount, currencySymbol)}"
                else
                    overallPctDisplay,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    fontFeatureSettings = "tnum"
                ),
                color = if (overallStatus.isOverBudget) appColors.expense else appColors.textSecondary
            )
            Text(
                text = if (strings.isBangla) "${formatCurrency(overallStatus.remainingAmount, currencySymbol)} বাকি" else "${formatCurrency(overallStatus.remainingAmount, currencySymbol)} left",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 12.sp,
                    fontFeatureSettings = "tnum"
                ),
                color = appColors.textSecondary
            )
        }
    }
}
