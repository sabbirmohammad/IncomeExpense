package com.example.expensetracker.ui.savings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.ColorBackground
import com.example.expensetracker.theme.ColorBorder
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.ui.components.LocalAppStrings
import java.util.Locale

@Composable
fun ContributionQuickChips(
    amountText: String,
    remainingAmount: Double,
    goalColor: Color,
    onAmountSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        listOf(500, 1000, 2000, 5000).forEach { shortcut ->
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = ColorBackground,
                border = BorderStroke(1.dp, ColorBorder),
                modifier = Modifier.clickable {
                    val curr = amountText.toDoubleOrNull() ?: 0.0
                    val next = curr + shortcut
                    onAmountSelected(next.toLong().toString())
                }
            ) {
                Text(
                    text = "+$shortcut",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = ColorTextPrimary,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }

        if (remainingAmount > 0) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = goalColor.copy(alpha = 0.15f),
                border = BorderStroke(1.dp, goalColor.copy(alpha = 0.35f)),
                modifier = Modifier.clickable {
                    val full = if (remainingAmount % 1.0 == 0.0) remainingAmount.toLong().toString()
                    else String.format(Locale.US, "%.2f", remainingAmount)
                    onAmountSelected(full)
                }
            ) {
                Text(
                    text = if (strings.isBangla) "বাকি পুরোটা" else "Full Remaining",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = goalColor,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }
    }
}
