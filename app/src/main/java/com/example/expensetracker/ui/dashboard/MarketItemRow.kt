package com.example.expensetracker.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.data.model.ProductPriceInsight
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.formatCurrency
import com.example.expensetracker.ui.components.rememberAppHaptics

/**
 * Clean & modern Market Checklist Item Card:
 * - Shows: Circular Checkbox, Item Name, Quantity Badge.
 * - Clicking row or checkbox triggers price entry or completion.
 * - Purchased items display the logged price in a green pill.
 * - Swipe left to delete with a safety confirmation dialog.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketItemRow(
    item: MarketItem,
    currencySymbol: String,
    isBangla: Boolean,
    onUnitClick: () -> Unit,
    onTickClick: () -> Unit,
    onRowClick: () -> Unit = onTickClick,
    onAmountChanged: (Double) -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onRequestDelete: () -> Unit = onDeleteClick,
    onSubstituteClick: (() -> Unit)? = null,
    isEditMode: Boolean = false,
    modifier: Modifier = Modifier,
    lastRateText: String? = null,
    priceInsight: ProductPriceInsight? = null
) {
    val appColors = LocalAppColors.current
    val haptics = rememberAppHaptics()

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            if (dismissValue == SwipeToDismissBoxValue.EndToStart && isEditMode) {
                haptics.mediumClick()
                onRequestDelete()
                // Return false so item doesn't disappear until confirmed in dialog
                false
            } else {
                false
            }
        }
    )

    val checkScale by animateFloatAsState(
        targetValue = if (item.isPurchased) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "checkScale"
    )
    val checkAlpha by animateFloatAsState(
        targetValue = if (item.isPurchased) 1f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "checkAlpha"
    )

    // Helper for amount input and price clear
    val handleAmountChange: (String) -> Unit = {
        if (it.isBlank()) {
            onAmountChanged(0.0)
        } else {
            val cleanNum = com.example.expensetracker.common.MarketListParser.convertBengaliDigitsToEnglish(it)
            cleanNum.toDoubleOrNull()?.let { amt -> onAmountChanged(amt) }
        }
    }

    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier.fillMaxWidth(),
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = isEditMode,
        backgroundContent = {
            val direction = dismissState.dismissDirection
            if (direction == SwipeToDismissBoxValue.EndToStart && isEditMode) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp))
                        .background(appColors.expense)
                        .padding(horizontal = 20.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = if (isBangla) "মুছে ফেলুন" else "Delete",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Delete",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (item.isPurchased) appColors.income.copy(alpha = 0.08f) else appColors.surface,
            border = BorderStroke(
                1.dp,
                if (item.isPurchased) appColors.income.copy(alpha = 0.25f) else appColors.border
            ),
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    haptics.lightTick()
                    if (isEditMode) {
                        onUnitClick()
                    } else {
                        onRowClick()
                    }
                }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // 1. Circular Checkbox
                Box(
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .sizeIn(minWidth = 48.dp, minHeight = 48.dp)
                        .clickable(enabled = !isEditMode) {
                            haptics.lightTick()
                            onTickClick()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(
                                if (item.isPurchased) appColors.income.copy(alpha = if (isEditMode) 0.6f else 1f) else Color.Transparent
                            )
                            .border(
                                width = 1.5.dp,
                                color = if (item.isPurchased) appColors.income.copy(alpha = if (isEditMode) 0.6f else 1f) else appColors.border,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (item.isPurchased || checkScale > 0.05f) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = "Purchased",
                                tint = Color.White,
                                modifier = Modifier
                                    .size(15.dp)
                                    .graphicsLayer {
                                        scaleX = checkScale
                                        scaleY = checkScale
                                        alpha = checkAlpha
                                    }
                            )
                        }
                    }
                }

                // 2. Product Name, Quantity & Badges (2-Row Balanced Layout)
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Row 1: Product Name (Left) + Estimated Cost / Purchased Price (Right)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.name,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 15.sp,
                                fontWeight = if (item.isPurchased) FontWeight.Normal else FontWeight.SemiBold,
                                textDecoration = if (item.isPurchased) TextDecoration.LineThrough else TextDecoration.None
                            ),
                            color = if (item.isPurchased) appColors.textSecondary else appColors.textPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )

                        // First Row End: Price Badge (Purchased) or Estimated Cost (Unpurchased)
                        if (!isEditMode) {
                            if (item.isPurchased) {
                                val amt = item.amount ?: 0.0
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = appColors.income.copy(alpha = 0.12f),
                                    border = BorderStroke(1.dp, appColors.income.copy(alpha = 0.3f)),
                                    modifier = Modifier.padding(start = 6.dp)
                                ) {
                                    Text(
                                        text = if (amt > 0) formatCurrency(amt, currencySymbol) else (if (isBangla) "কেনা হয়েছে" else "Done"),
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontSize = 12.5.sp,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = appColors.income,
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                                    )
                                }
                            } else {
                                // Right: Estimated cost pill & last purchased rate when unpurchased
                                if (priceInsight != null && priceInsight.currentUnitPrice > 0.0) {
                                    if (item.quantity > 0.0) {
                                        val estCost = com.example.expensetracker.common.UnitConverterEngine.estimateCost(
                                            priceInsight.currentUnitPrice,
                                            item.quantity,
                                            item.unit
                                        )
                                        val estStr = if (estCost % 1.0 == 0.0) estCost.toLong().toString() else "%.0f".format(estCost)
                                        val estNum = if (isBangla) convertToBengaliDigits(estStr) else estStr
                                        val estBadgeText = if (isBangla) "সম্ভাব্য $currencySymbol$estNum" else "Est. $currencySymbol$estNum"

                                        // Stylish pill badge showing the expected estimated cost
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = appColors.accent.copy(alpha = 0.10f),
                                            border = BorderStroke(1.dp, appColors.accent.copy(alpha = 0.25f)),
                                            modifier = Modifier.padding(start = 6.dp)
                                        ) {
                                            Text(
                                                text = estBadgeText,
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontSize = 11.5.sp,
                                                    fontWeight = FontWeight.Bold
                                                ),
                                                color = appColors.accent,
                                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.5.dp),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    } else {
                                        val rateStr = if (priceInsight.currentUnitPrice % 1.0 == 0.0) {
                                            priceInsight.currentUnitPrice.toLong().toString()
                                        } else {
                                            "%.1f".format(priceInsight.currentUnitPrice)
                                        }
                                        val rateNum = if (isBangla) convertToBengaliDigits(rateStr) else rateStr
                                        val lastRateResolved = if (isBangla) "গত দর: $currencySymbol$rateNum/${priceInsight.baseUnit}" else "Last: $currencySymbol$rateNum/${priceInsight.baseUnit}"

                                        Text(
                                            text = lastRateResolved,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Medium
                                            ),
                                            color = appColors.textSecondary.copy(alpha = 0.85f),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.padding(start = 6.dp)
                                        )
                                    }
                                } else if (lastRateText != null) {
                                    Text(
                                        text = lastRateText,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Medium
                                        ),
                                        color = appColors.textSecondary.copy(alpha = 0.85f),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.padding(start = 6.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Row 2: Quantity + Department (Left) + Substitute / Trend Button (Card End)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left side of Row 2: Quantity & Department pills (+ subtle rate if available)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.weight(1f, fill = false)
                        ) {
                            // Quantity pill
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        if (item.isPurchased) appColors.background
                                        else appColors.accent.copy(alpha = 0.10f)
                                    )
                                    .clickable(enabled = !item.isPurchased) {
                                        haptics.lightTick()
                                        onUnitClick()
                                    }
                                    .padding(horizontal = 7.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = item.formatQuantity(),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = if (item.isPurchased) appColors.textSecondary else appColors.accent
                                )
                                if (!item.isPurchased) {
                                    Icon(
                                        imageVector = Icons.Outlined.Edit,
                                        contentDescription = "Edit quantity",
                                        tint = appColors.accent.copy(alpha = 0.8f),
                                        modifier = Modifier.size(10.dp)
                                    )
                                }
                            }

                            // Department indicator pill (e.g. 🥬 কাঁচাবাজার / 🧂 মুদি সদাই / 🥛 দুধ ও বেকারি)
                            val dept = getDepartmentMeta(getMarketItemCategory(item.name))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = appColors.surfaceVariant.copy(alpha = 0.5f)
                            ) {
                                Text(
                                    text = "${dept.icon} ${if (isBangla) dept.titleBn else dept.titleEn}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    color = if (item.isPurchased) appColors.textSecondary.copy(alpha = 0.7f) else appColors.textSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }

                            // Subtle text showing last purchased rate when quantity > 0
                            if (!item.isPurchased && priceInsight != null && priceInsight.currentUnitPrice > 0.0 && item.quantity > 0.0) {
                                val rateStr = if (priceInsight.currentUnitPrice % 1.0 == 0.0) {
                                    priceInsight.currentUnitPrice.toLong().toString()
                                } else {
                                    "%.1f".format(priceInsight.currentUnitPrice)
                                }
                                val rateNum = if (isBangla) convertToBengaliDigits(rateStr) else rateStr
                                val lastRateResolved = if (isBangla) "গত দর: $currencySymbol$rateNum/${priceInsight.baseUnit}" else "Last: $currencySymbol$rateNum/${priceInsight.baseUnit}"
                                Text(
                                    text = lastRateResolved,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    color = appColors.textSecondary.copy(alpha = 0.8f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        // Right side of Row 2: Substitute item quick button (কার্ডের একদম শেষে)
                        if (!isEditMode) {
                            if (!item.isPurchased && onSubstituteClick != null) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = appColors.accent.copy(alpha = 0.08f),
                                    border = BorderStroke(1.dp, appColors.accent.copy(alpha = 0.2f)),
                                    modifier = Modifier
                                        .padding(start = 6.dp)
                                        .clickable {
                                            haptics.lightTick()
                                            onSubstituteClick()
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.5.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.SwapHoriz,
                                            contentDescription = "Substitute item",
                                            tint = appColors.accent,
                                            modifier = Modifier.size(11.dp)
                                        )
                                        Text(
                                            text = if (isBangla) "বিকল্প" else "Alt",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 9.5.sp,
                                                fontWeight = FontWeight.SemiBold
                                            ),
                                            color = appColors.accent
                                        )
                                    }
                                }
                            } else if (item.isPurchased && priceInsight?.previousUnitPrice != null && (item.amount ?: 0.0) > 0) {
                                // Smart Price Trend pill if previous comparison is known
                                val diffText = priceInsight.formatDiff()
                                val isUp = priceInsight.isPriceUp
                                val isDown = priceInsight.isPriceDown
                                val trendColor = if (isUp) appColors.expense else if (isDown) appColors.income else appColors.textSecondary
                                val trendIcon = if (isUp) "🔺 " else if (isDown) "🔻 " else "➖ "
                                Text(
                                    text = "$trendIcon$diffText",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = trendColor,
                                    maxLines = 1,
                                    modifier = Modifier.padding(start = 6.dp)
                                )
                            }
                        }
                    }
                }

                // 3. Right: In Edit Mode, show Delete item button
                if (isEditMode) {
                    IconButton(
                        onClick = {
                            haptics.mediumClick()
                            onRequestDelete()
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(appColors.expense.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = if (isBangla) "পণ্য মুছুন" else "Delete",
                                tint = appColors.expense,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
