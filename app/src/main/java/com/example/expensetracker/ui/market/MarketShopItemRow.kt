package com.example.expensetracker.ui.market

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.ui.components.formatCurrency
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.theme.HairlineInkStroke
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.LocalAppStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketShopItemRow(
    item: MarketItem,
    currencySymbol: String,
    isBangla: Boolean,
    onRowClick: () -> Unit,
    onTickClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    lastRateText: String? = null
) {
    val appColors = LocalAppColors.current
    val strings = LocalAppStrings.current

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            when (dismissValue) {
                SwipeToDismissBoxValue.StartToEnd -> {
                    onTickClick()
                    false
                }
                SwipeToDismissBoxValue.EndToStart -> {
                    onDelete()
                    false
                }
                else -> false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = true,
        enableDismissFromEndToStart = true,
        backgroundContent = {
            val direction = dismissState.dismissDirection
            val color = when (direction) {
                SwipeToDismissBoxValue.StartToEnd -> appColors.income.copy(alpha = 0.2f)
                SwipeToDismissBoxValue.EndToStart -> appColors.warning.copy(alpha = 0.2f)
                else -> Color.Transparent
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp))
                    .background(color)
                    .padding(horizontal = 20.dp),
                contentAlignment = if (direction == SwipeToDismissBoxValue.StartToEnd) Alignment.CenterStart else Alignment.CenterEnd
            ) {
                if (direction == SwipeToDismissBoxValue.StartToEnd) {
                    Icon(imageVector = Icons.Filled.Check, contentDescription = null, tint = appColors.income)
                } else if (direction == SwipeToDismissBoxValue.EndToStart) {
                    Icon(imageVector = Icons.Filled.Close, contentDescription = null, tint = appColors.warning)
                }
            }
        },
        modifier = modifier
    ) {
        Surface(
            onClick = onRowClick,
            shape = RoundedCornerShape(12.dp),
            color = appColors.cardBackground,
            border = BorderStroke(HairlineInkStroke, appColors.divider),
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left: Checkbox (28dp visual, 48dp touch target)
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        onClick = onTickClick,
                        shape = CircleShape,
                        color = if (item.isPurchased) appColors.income else Color.Transparent,
                        border = BorderStroke(
                            2.dp,
                            if (item.isPurchased) appColors.income else appColors.divider
                        ),
                        modifier = Modifier
                            .size(28.dp)
                            .minimumInteractiveComponentSize()
                    ) {
                        if (item.isPurchased) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = "Bought",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    // Middle: Name (up to 2 lines, never ellipsized) + Qty chip + Last rate
                    Column(
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

                // Right: Amount Display (Paid or Estimate)
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    if (item.isPurchased && item.amount != null && item.amount > 0) {
                        Text(
                            text = formatCurrency(item.amount, currencySymbol),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                fontFeatureSettings = "tnum"
                            ),
                            color = appColors.income
                        )
                    } else {
                        Text(
                            text = if (isBangla) "দরদাম ✎" else "Price ✎",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            color = appColors.accent
                        )
                    }
                }
            }
        }
    }
}
