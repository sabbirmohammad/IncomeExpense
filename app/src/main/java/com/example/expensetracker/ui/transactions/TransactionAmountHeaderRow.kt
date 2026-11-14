package com.example.expensetracker.ui.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.Strings
import com.example.expensetracker.ui.components.rememberAppHaptics

@Composable
fun TransactionAmountHeaderRow(
    isTransfer: Boolean,
    isExpense: Boolean,
    typeAccentColor: Color,
    amountText: String,
    strings: Strings,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val haptics = rememberAppHaptics()

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(typeAccentColor)
            )
            Text(
                text = when {
                    isTransfer -> strings.transferTab
                    isExpense -> strings.expenseAmount
                    else -> strings.incomeAmount
                },
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                color = appColors.textSecondary
            )
        }

        if (amountText.isNotEmpty()) {
            Text(
                text = strings.clear,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = appColors.textSecondary,
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .clickable {
                        haptics.lightTick()
                        onClear()
                    }
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            )
        }
    }
}
