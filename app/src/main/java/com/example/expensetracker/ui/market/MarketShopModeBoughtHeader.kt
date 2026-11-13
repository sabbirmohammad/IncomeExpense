package com.example.expensetracker.ui.market

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.HairlineInkStroke
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.rememberAppHaptics

@Composable
fun MarketShopModeBoughtHeader(
    boughtCount: Int,
    isBangla: Boolean,
    isBoughtCollapsed: Boolean,
    onToggleCollapse: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val strings = LocalAppStrings.current
    val haptics = rememberAppHaptics()

    Surface(
        onClick = {
            haptics.lightTick()
            onToggleCollapse()
        },
        shape = RoundedCornerShape(10.dp),
        color = appColors.cardBackground,
        border = BorderStroke(HairlineInkStroke, appColors.divider),
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .defaultMinSize(minHeight = 44.dp)
            .minimumInteractiveComponentSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val countDigits = if (isBangla) convertToBengaliDigits(boughtCount.toString()) else boughtCount.toString()
            Text(
                text = "${strings.marketBought} ($countDigits)",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                ),
                color = appColors.income
            )

            val arrowRotation by animateFloatAsState(
                targetValue = if (isBoughtCollapsed) -90f else 0f,
                animationSpec = tween(220),
                label = "arrowRotation"
            )
            Icon(
                imageVector = Icons.Filled.KeyboardArrowDown,
                contentDescription = if (isBoughtCollapsed) "Expand" else "Collapse",
                tint = appColors.textSecondary,
                modifier = Modifier
                    .size(18.dp)
                    .rotate(arrowRotation)
            )
        }
    }
}
