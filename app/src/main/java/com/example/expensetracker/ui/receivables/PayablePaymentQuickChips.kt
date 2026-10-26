package com.example.expensetracker.ui.receivables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.SmallElementRadius
import com.example.expensetracker.ui.components.rememberAppHaptics
import com.example.expensetracker.theme.LocalAppColors

@Composable
fun PayablePaymentQuickChips(
    remainingAmount: Double,
    isBangla: Boolean,
    onAmountSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val haptics = rememberAppHaptics()
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        listOf(
            Pair(if (isBangla) "পূর্ণ পরিশোধ" else "Full", remainingAmount),
            Pair("50%", remainingAmount * 0.5),
            Pair("25%", remainingAmount * 0.25)
        ).forEach { (label, amt) ->
            Surface(
                shape = RoundedCornerShape(SmallElementRadius),
                color = appColors.accent.copy(alpha = 0.12f),
                modifier = Modifier
                    .weight(1f)
                    .minimumInteractiveComponentSize()
                    .clickable {
                        haptics.lightTick()
                        onAmountSelected(amt.toInt().toString())
                    }
            ) {
                Text(
                    text = label,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = appColors.accent,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
