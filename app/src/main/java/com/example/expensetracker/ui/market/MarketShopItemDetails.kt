package com.example.expensetracker.ui.market

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.theme.HairlineInkStroke
import com.example.expensetracker.theme.LocalAppColors

@Composable
fun MarketShopItemDetails(
    item: MarketItem,
    lastRateText: String?,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = item.name,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    lineHeight = 22.sp,
                    textDecoration = if (item.isPurchased) TextDecoration.LineThrough else TextDecoration.None
                ),
                color = if (item.isPurchased) appColors.textMuted else appColors.textPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Inline Quantity Chip
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = appColors.surface,
                border = BorderStroke(HairlineInkStroke, appColors.divider)
            ) {
                Text(
                    text = item.formatQuantity(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        fontFeatureSettings = "tnum"
                    ),
                    color = if (item.isPurchased) appColors.textMuted else appColors.textSecondary,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }

        if (lastRateText != null) {
            Text(
                text = lastRateText,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 12.sp,
                    fontFeatureSettings = "tnum"
                ),
                color = appColors.textSecondary
            )
        }
    }
}
