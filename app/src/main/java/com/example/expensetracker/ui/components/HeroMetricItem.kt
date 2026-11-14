package com.example.expensetracker.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
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
import com.example.expensetracker.theme.LocalAppColors

@Composable
fun HeroMetricItem(
    indicator: String,
    label: String,
    formattedAmount: String,
    indicatorColor: Color,
    amountColor: Color,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = indicator,
            fontSize = 10.sp,
            color = indicatorColor,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal
            ),
            color = appColors.textSecondary
        )
        Text(
            text = formattedAmount,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 12.5.sp,
                fontWeight = FontWeight.SemiBold,
                fontFeatureSettings = "tnum"
            ),
            color = amountColor
        )
    }
}
