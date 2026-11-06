package com.example.expensetracker.ui.market

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.theme.HairlineInkStroke
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.rememberAppHaptics
import com.example.expensetracker.ui.dashboard.getDepartmentMeta
import com.example.expensetracker.ui.dashboard.getMarketItemCategory

/**
 * Handcrafted Living Ledger & Tactile Bazaar Interactive Tag Chip:
 * - Represents a parsed WhatsApp / bulk item or docket tag.
 * - Displays department aisle emoji, item name, and formatQuantity pill.
 * - Tap to adjust quantity/unit, or tap delete with guaranteed >= 48dp touch target.
 * - 0.5dp ink stroke border and warm ledger styling.
 * - Strictly zero artificial AI sparkles or tropes.
 */
@Composable
fun MarketParsedTagChip(
    item: MarketItem,
    isBangla: Boolean,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
    onQuantityClick: (() -> Unit)? = null,
    showDepartmentIcon: Boolean = true
) {
    val appColors = LocalAppColors.current
    val haptics = rememberAppHaptics()
    val deptMeta = getDepartmentMeta(getMarketItemCategory(item.name))

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = appColors.cardBackground,
        border = BorderStroke(HairlineInkStroke, appColors.divider),
        modifier = modifier
            .defaultMinSize(minHeight = 44.dp)
            .padding(vertical = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(start = 10.dp, end = 2.dp, top = 4.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Department Stall Emoji Badge
            if (showDepartmentIcon) {
                Text(
                    text = deptMeta.icon,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(end = 2.dp)
                )
            }

            // Item Name
            Text(
                text = item.name,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.5.sp
                ),
                color = appColors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Quantity & Unit Pill (Clickable for quick adjustment)
            Surface(
                onClick = {
                    haptics.lightTick()
                    onQuantityClick?.invoke()
                },
                enabled = onQuantityClick != null,
                shape = RoundedCornerShape(6.dp),
                color = appColors.accent.copy(alpha = 0.12f),
                border = BorderStroke(HairlineInkStroke, appColors.accent.copy(alpha = 0.25f)),
                modifier = Modifier.defaultMinSize(minHeight = 28.dp)
            ) {
                Text(
                    text = item.formatQuantity(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFeatureSettings = "tnum"
                    ),
                    color = appColors.accent,
                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                )
            }

            // 1-Tap Remove Button with guaranteed >= 48dp touch target
            Box(
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .size(36.dp)
                    .clip(CircleShape)
                    .clickable {
                        haptics.lightTick()
                        onRemove()
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = if (isBangla) "বাদ দিন" else "Remove",
                    tint = appColors.textSecondary,
                    modifier = Modifier.size(15.dp)
                )
            }
        }
    }
}
