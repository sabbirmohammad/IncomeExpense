package com.example.expensetracker.ui.dashboard

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.data.model.ProductPriceInsight
import com.example.expensetracker.theme.ExpenseTrackerTheme
import com.example.expensetracker.theme.HairlineInkStroke
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.BanglaStrings
import com.example.expensetracker.ui.components.EnglishStrings
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.formatCurrency
import com.example.expensetracker.ui.components.rememberAppHaptics

/**
 * Handcrafted 64dp Bazaar Item Row (Target Design B6 / V7 Spec):
 * - Flat 64dp ROW (not a bulky card), hairline bottom divider.
 * - Visual 28dp circular checkbox, hit target >= 48dp.
 * - Name wraps to 1-2 lines (never ellipsized) with inline quantity chip.
 * - Below: "গতবার ৳25/কেজি" (muted last rate).
 * - Right: price if bought, else "~৳50" estimate.
 * - Swipe-right: mark bought quickly using last known price with Undo snackbar.
 * - Swipe-left: delete with Undo snackbar.
 * - Tap: opens Price sheet / edit.
 * - Accessibility: TalkBack semantics + custom accessibility actions.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
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
            when (dismissValue) {
                SwipeToDismissBoxValue.StartToEnd -> {
                    haptics.successFeedback()
                    onTickClick()
                    false
                }
                SwipeToDismissBoxValue.EndToStart -> {
                    if (isEditMode) {
                        haptics.mediumClick()
                        onRequestDelete()
                        false
                    } else {
                        false
                    }
                }
                SwipeToDismissBoxValue.Settled -> false
            }
        }
    )

    // Progressive drag threshold tactile feedback
    val isThresholdPassed = dismissState.targetValue != SwipeToDismissBoxValue.Settled
    var wasThresholdPassed by remember { mutableStateOf(false) }
    LaunchedEffect(isThresholdPassed) {
        if (isThresholdPassed != wasThresholdPassed) {
            haptics.lightTick()
            wasThresholdPassed = isThresholdPassed
        }
    }

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

    // Spring-animated strikethrough upon check-off
    val strikeProgress by animateFloatAsState(
        targetValue = if (item.isPurchased) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "strikeProgress"
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

    // Resolving last rate text
    val lastRateResolved: String? = remember(priceInsight, lastRateText, isBangla, currencySymbol) {
        if (priceInsight != null && priceInsight.currentUnitPrice > 0.0) {
            val rateStr = if (priceInsight.currentUnitPrice % 1.0 == 0.0) {
                priceInsight.currentUnitPrice.toLong().toString()
            } else {
                "%.1f".format(priceInsight.currentUnitPrice)
            }
            val rateNum = if (isBangla) convertToBengaliDigits(rateStr) else rateStr
            if (isBangla) "গত দর: $currencySymbol$rateNum/${priceInsight.baseUnit}" else "Last: $currencySymbol$rateNum/${priceInsight.baseUnit}"
        } else {
            lastRateText
        }
    }

    // Resolving estimated cost badge text for unpurchased item
    val estBadgeText: String? = remember(item, priceInsight, isBangla, currencySymbol) {
        if (!item.isPurchased && priceInsight != null && priceInsight.currentUnitPrice > 0.0 && item.quantity > 0.0) {
            val estCost = com.example.expensetracker.common.UnitConverterEngine.estimateCost(
                priceInsight.currentUnitPrice,
                item.quantity,
                item.unit
            )
            val estStr = if (estCost % 1.0 == 0.0) estCost.toLong().toString() else "%.0f".format(estCost)
            val estNum = if (isBangla) convertToBengaliDigits(estStr) else estStr
            if (isBangla) "সম্ভাব্য $currencySymbol$estNum" else "Est. $currencySymbol$estNum"
        } else null
    }

    // TalkBack semantic label
    val accessibilityLabel = buildString {
        append(item.name)
        append(", ")
        append(item.formatQuantity())
        append(", ")
        if (item.isPurchased) {
            append(if (isBangla) "কেনা হয়েছে" else "Bought")
            val amt = item.amount ?: 0.0
            if (amt > 0) {
                append(", ")
                append(formatCurrency(amt, currencySymbol))
            }
        } else {
            append(if (isBangla) "কেনা হয়নি" else "Not bought")
        }
        if (!lastRateResolved.isNullOrBlank()) {
            append(", ")
            append(lastRateResolved)
        }
    }

    // Determine department meta for structural compatibility
    val dept = getDepartmentMeta(getMarketItemCategory(item.name))

    Column(modifier = modifier.fillMaxWidth()) {
        SwipeToDismissBox(
            state = dismissState,
            modifier = Modifier.fillMaxWidth(),
            enableDismissFromStartToEnd = true,
            enableDismissFromEndToStart = isEditMode,
            backgroundContent = {
                val direction = dismissState.dismissDirection
                when (direction) {
                    SwipeToDismissBoxValue.StartToEnd -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(12.dp))
                                .background(appColors.income)
                                .padding(horizontal = 20.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = if (isBangla) "কেনা হয়েছে" else "Purchased",
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                                Text(
                                    text = if (item.isPurchased) {
                                        if (isBangla) "অসম্পূর্ণ রাখুন" else "Revert"
                                    } else {
                                        if (isBangla) "কেনা হয়েছে" else "Done"
                                    },
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontFeatureSettings = "tnum"
                                    )
                                )
                            }
                        }
                    }
                    SwipeToDismissBoxValue.EndToStart -> {
                        if (isEditMode) {
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
                                            color = Color.White,
                                            fontSize = 13.sp,
                                            fontFeatureSettings = "tnum"
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
                    SwipeToDismissBoxValue.Settled -> Unit
                }
            }
        ) {
            Surface(
                color = if (item.isPurchased) appColors.cardBackground.copy(alpha = 0.65f) else appColors.cardBackground,
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 64.dp)
                    .semantics {
                        contentDescription = accessibilityLabel
                        customActions = listOf(
                            CustomAccessibilityAction(
                                label = if (item.isPurchased) "Mark as unbought" else "Mark as bought",
                                action = { onTickClick(); true }
                            ),
                            CustomAccessibilityAction(
                                label = "Delete item",
                                action = { onRequestDelete(); true }
                            )
                        )
                    }
                    .clickable {
                        haptics.lightTick()
                        if (isEditMode) {
                            onUnitClick()
                        } else {
                            onRowClick()
                        }
                    }
                    .combinedClickable(
                        onClick = {
                            haptics.lightTick()
                            if (isEditMode) {
                                onUnitClick()
                            } else {
                                onRowClick()
                            }
                        },
                        onLongClick = {
                            haptics.longPressFeedback()
                            if (!item.isPurchased && onSubstituteClick != null) {
                                onSubstituteClick()
                            } else {
                                onUnitClick()
                            }
                        }
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // 1. Visual 28dp Checkbox with min 48dp Hit Target
                    Box(
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .sizeIn(minWidth = 48.dp, minHeight = 48.dp)
                            .clickable(enabled = !isEditMode) {
                                haptics.mediumClick()
                                onTickClick()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(
                                    if (item.isPurchased) appColors.income else Color.Transparent
                                )
                                .border(
                                    width = 1.5.dp,
                                    color = if (item.isPurchased) appColors.income else appColors.divider,
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
                                        .size(17.dp)
                                        .graphicsLayer {
                                            scaleX = checkScale
                                            scaleY = checkScale
                                            alpha = checkAlpha
                                        }
                                )
                            }
                        }
                    }

                    // 2. Middle Content: Line 1 = Name (wraps) + Inline Quantity Chip; Line 2 = Last Price
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        // Line 1: Name (wraps, never ellipsized) + inline qty chip
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.weight(1f, fill = false)
                        ) {
                            val strikethroughColor = appColors.textMuted
                            Text(
                                text = item.name,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 15.sp,
                                    lineHeight = 20.sp,
                                    fontWeight = if (item.isPurchased) FontWeight.Normal else FontWeight.SemiBold,
                                    textDecoration = if (item.isPurchased) TextDecoration.LineThrough else TextDecoration.None
                                ),
                                color = if (item.isPurchased) appColors.textMuted else appColors.textPrimary,
                                maxLines = 2,
                                overflow = TextOverflow.Clip,
                                modifier = Modifier
                                    .weight(1f, fill = false)
                                    .drawWithContent {
                                        drawContent()
                                        if (strikeProgress > 0.01f) {
                                            val y = size.height / 2f
                                            drawLine(
                                                color = strikethroughColor,
                                                start = Offset(0f, y),
                                                end = Offset(size.width * strikeProgress, y),
                                                strokeWidth = 2.dp.toPx(),
                                                cap = StrokeCap.Round
                                            )
                                        }
                                    }
                            )

                            // Inline Quantity Chip (48dp hit area)
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (item.isPurchased) appColors.surfaceVariant.copy(alpha = 0.5f) else appColors.accentContainer.copy(alpha = 0.4f),
                                border = BorderStroke(HairlineInkStroke, if (item.isPurchased) appColors.divider else appColors.accent.copy(alpha = 0.25f)),
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .defaultMinSize(minHeight = 32.dp)
                                    .clickable(enabled = !item.isPurchased) {
                                        haptics.lightTick()
                                        onUnitClick()
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    Text(
                                        text = item.formatQuantity(),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFeatureSettings = "tnum"
                                        ),
                                        color = if (item.isPurchased) appColors.textMuted else appColors.textPrimary
                                    )
                                    if (!item.isPurchased) {
                                        Icon(
                                            imageVector = Icons.Outlined.Edit,
                                            contentDescription = "Edit quantity",
                                            tint = appColors.textSecondary,
                                            modifier = Modifier.size(10.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Line 2: Last price (muted) e.g. "গতবার ৳25/কেজি"
                        if (!lastRateResolved.isNullOrBlank()) {
                            Text(
                                text = lastRateResolved,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp,
                                    fontWeight = FontWeight.Normal,
                                    fontFeatureSettings = "tnum"
                                ),
                                color = appColors.textMuted,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        // Structural anchor for department badge
                        if (isEditMode) {
                            Text(
                                text = "${dept.icon} ${if (isBangla) dept.titleBn else dept.titleEn}",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp),
                                color = appColors.textMuted,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // 3. Right Side: Price if bought, else "~৳50" estimate (or edit actions if edit mode)
                    if (isEditMode) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            if (onSubstituteClick != null && !item.isPurchased) {
                                IconButton(
                                    onClick = {
                                        haptics.lightTick()
                                        onSubstituteClick()
                                    },
                                    modifier = Modifier
                                        .size(48.dp)
                                        .minimumInteractiveComponentSize()
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.SwapHoriz,
                                        contentDescription = "Substitute item",
                                        tint = appColors.accent,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            IconButton(
                                onClick = {
                                    haptics.mediumClick()
                                    onRequestDelete()
                                },
                                modifier = Modifier
                                    .size(48.dp)
                                    .minimumInteractiveComponentSize()
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Delete,
                                    contentDescription = "Delete item",
                                    tint = appColors.expense,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    } else {
                        Column(
                            horizontalAlignment = Alignment.End,
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            if (item.isPurchased) {
                                val amt = item.amount ?: 0.0
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = appColors.income.copy(alpha = 0.12f),
                                    border = BorderStroke(HairlineInkStroke, appColors.income.copy(alpha = 0.3f))
                                ) {
                                    Text(
                                        text = if (amt > 0) formatCurrency(amt, currencySymbol) else (if (isBangla) "কেনা হয়েছে" else "Bought"),
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFeatureSettings = "tnum"
                                        ),
                                        color = appColors.income,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            } else if (estBadgeText != null) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = appColors.surfaceVariant,
                                    border = BorderStroke(HairlineInkStroke, appColors.divider)
                                ) {
                                    Text(
                                        text = estBadgeText,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            fontFeatureSettings = "tnum"
                                        ),
                                        color = appColors.textSecondary,
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        HorizontalDivider(
            color = appColors.divider,
            thickness = HairlineInkStroke
        )
    }
}

// =========================================================================
// PREVIEWS (Light, Dark, AMOLED x English, Bengali)
// =========================================================================

@Preview(name = "MarketItemRow - Dark En", showBackground = true)
@Composable
fun PreviewMarketItemRowDarkEn() {
    ExpenseTrackerTheme(themeMode = "dark") {
        CompositionLocalProvider(LocalAppStrings provides EnglishStrings) {
            Surface(color = LocalAppColors.current.background) {
                MarketItemRow(
                    item = MarketItem(
                        id = 1L,
                        listId = 1L,
                        name = "Potato",
                        quantity = 2.0,
                        unit = "kg",
                        isPurchased = false
                    ),
                    currencySymbol = "৳",
                    isBangla = false,
                    onUnitClick = {},
                    onTickClick = {},
                    lastRateText = "Last: ৳30/kg"
                )
            }
        }
    }
}

@Preview(name = "MarketItemRow - Light Bn (Purchased)", showBackground = true)
@Composable
fun PreviewMarketItemRowLightBn() {
    ExpenseTrackerTheme(themeMode = "light") {
        CompositionLocalProvider(LocalAppStrings provides BanglaStrings) {
            Surface(color = LocalAppColors.current.background) {
                MarketItemRow(
                    item = MarketItem(
                        id = 2L,
                        listId = 1L,
                        name = "দেশি পেঁয়াজ",
                        quantity = 1.0,
                        unit = "কেজি",
                        isPurchased = true,
                        amount = 110.0
                    ),
                    currencySymbol = "৳",
                    isBangla = true,
                    onUnitClick = {},
                    onTickClick = {},
                    lastRateText = "গতবার: ৳১১০/কেজি"
                )
            }
        }
    }
}

@Preview(name = "MarketItemRow - AMOLED Bn", showBackground = true)
@Composable
fun PreviewMarketItemRowAmoledBn() {
    ExpenseTrackerTheme(themeMode = "amoled") {
        CompositionLocalProvider(LocalAppStrings provides BanglaStrings) {
            Surface(color = LocalAppColors.current.background) {
                MarketItemRow(
                    item = MarketItem(
                        id = 3L,
                        listId = 1L,
                        name = "কাঁচা মরিচ",
                        quantity = 250.0,
                        unit = "গ্রাম",
                        isPurchased = false
                    ),
                    currencySymbol = "৳",
                    isBangla = true,
                    onUnitClick = {},
                    onTickClick = {},
                    lastRateText = "গতবার: ৳৫০/২৫০ গ্রাম"
                )
            }
        }
    }
}
