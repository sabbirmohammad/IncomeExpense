package com.example.expensetracker.ui.receivables

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Receivable
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.formatCurrency
import com.example.expensetracker.ui.components.rememberAppHaptics

@Composable
fun ReceivablePaymentQuickChips(
    receivable: Receivable,
    currencySymbol: String,
    onAmountSelected: (String) -> Unit
) {
    val appColors = LocalAppColors.current
    val haptics = rememberAppHaptics()
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Full Remaining (100%)
        Surface(
            shape = RoundedCornerShape(6.dp),
            color = appColors.accent.copy(alpha = 0.15f),
            border = BorderStroke(1.dp, appColors.accent),
            modifier = Modifier
                .minimumInteractiveComponentSize()
                .clickable {
                    haptics.lightTick()
                    val formatted = if (receivable.remainingAmount % 1.0 == 0.0) {
                        receivable.remainingAmount.toLong().toString()
                    } else {
                        receivable.remainingAmount.toString()
                    }
                    onAmountSelected(formatted)
                }
        ) {
            Text(
                text = "100% (${formatCurrency(receivable.remainingAmount, currencySymbol)})",
                fontSize = 11.sp,
                color = appColors.accent,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }

        // Half Remaining (50%)
        val half = receivable.remainingAmount / 2.0
        if (half > 0) {
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = appColors.surfaceVariant,
                border = BorderStroke(1.dp, appColors.border),
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .clickable {
                        haptics.lightTick()
                        val formatted = if (half % 1.0 == 0.0) {
                            half.toLong().toString()
                        } else {
                            half.toString()
                        }
                        onAmountSelected(formatted)
                    }
            ) {
                Text(
                    text = "50% (${formatCurrency(half, currencySymbol)})",
                    fontSize = 11.sp,
                    color = appColors.textSecondary,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}
