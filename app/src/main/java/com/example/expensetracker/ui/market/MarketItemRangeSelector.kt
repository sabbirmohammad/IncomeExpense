package com.example.expensetracker.ui.market

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.HairlineInkStroke
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.WeightBold
import com.example.expensetracker.theme.WeightMedium
import com.example.expensetracker.ui.components.rememberAppHaptics

@Composable
fun MarketItemRangeSelector(
    selectedRange: PriceHistoryRange,
    onRangeSelected: (PriceHistoryRange) -> Unit,
    isBangla: Boolean,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val haptics = rememberAppHaptics()

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        PriceHistoryRange.entries.forEach { range ->
            val isSelected = selectedRange == range
            Surface(
                onClick = {
                    haptics.lightTick()
                    onRangeSelected(range)
                },
                shape = RoundedCornerShape(10.dp),
                color = if (isSelected) appColors.accent.copy(alpha = 0.12f) else appColors.cardBackground,
                border = BorderStroke(HairlineInkStroke, if (isSelected) appColors.accent else appColors.divider),
                modifier = Modifier
                    .weight(1f)
                    .defaultMinSize(minHeight = 44.dp)
                    .minimumInteractiveComponentSize()
            ) {
                Box(
                    modifier = Modifier.padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isBangla) range.titleBn else range.titleEn,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (isSelected) WeightBold else WeightMedium,
                            fontSize = 12.sp
                        ),
                        color = if (isSelected) appColors.accent else appColors.textSecondary
                    )
                }
            }
        }
    }
}
