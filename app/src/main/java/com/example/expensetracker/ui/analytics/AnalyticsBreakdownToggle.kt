package com.example.expensetracker.ui.analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorBorder
import com.example.expensetracker.theme.ColorSurface
import com.example.expensetracker.theme.ColorTextSecondary
import com.example.expensetracker.theme.SmallElementRadius
import com.example.expensetracker.ui.components.Strings

@Composable
fun AnalyticsBreakdownToggle(
    breakdownMode: AnalyticsBreakdownMode,
    strings: Strings,
    onModeChange: (AnalyticsBreakdownMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(SmallElementRadius))
            .background(ColorSurface)
            .border(1.dp, ColorBorder, RoundedCornerShape(SmallElementRadius))
            .padding(3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        AnalyticsBreakdownMode.entries.forEach { mode ->
            val isSelected = breakdownMode == mode
            val modeLabel = when (mode) {
                AnalyticsBreakdownMode.CATEGORIES -> strings.category
                AnalyticsBreakdownMode.PAYMENT_METHODS -> strings.paymentMethod
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(SmallElementRadius - 2.dp))
                    .background(if (isSelected) ColorAccent else Color.Transparent)
                    .clickable { onModeChange(mode) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = modeLabel,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
                    ),
                    color = if (isSelected) Color.White else ColorTextSecondary
                )
            }
        }
    }
}
