package com.example.expensetracker.ui.dashboard

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.data.model.ProductPriceInsight
import com.example.expensetracker.theme.HairlineInkStroke
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.formatCurrency
import com.example.expensetracker.ui.components.rememberAppHaptics

/**
 * Handcrafted 64dp Bazaar Item Row with 1-Swipe Check-off & living ledger styling.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MarketItemRow(
    item: MarketItem, currencySymbol: String, isBangla: Boolean,
    onUnitClick: () -> Unit, onTickClick: () -> Unit,
    onRowClick: () -> Unit = onTickClick, onAmountChanged: (Double) -> Unit = {},
    onDeleteClick: () -> Unit = {}, onRequestDelete: () -> Unit = onDeleteClick,
    onSubstituteClick: (() -> Unit)? = null, isEditMode: Boolean = false,
    modifier: Modifier = Modifier, lastRateText: String? = null,
    priceInsight: ProductPriceInsight? = null
) {
    val appColors = LocalAppColors.current
    val haptics = rememberAppHaptics()

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            when (dismissValue) {
                SwipeToDismissBoxValue.StartToEnd -> { haptics.successFeedback(); onTickClick(); false }
                SwipeToDismissBoxValue.EndToStart -> { if (isEditMode) { haptics.mediumClick(); onRequestDelete() }; false }
                SwipeToDismissBoxValue.Settled -> false
            }
        }
    )

    val isThresholdPassed = dismissState.targetValue != SwipeToDismissBoxValue.Settled
    var wasThresholdPassed by remember { mutableStateOf(false) }
    LaunchedEffect(isThresholdPassed) {
        if (isThresholdPassed != wasThresholdPassed) { haptics.lightTick(); wasThresholdPassed = isThresholdPassed }
    }

    val checkScale by animateFloatAsState(targetValue = if (item.isPurchased) 1f else 0f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium), label = "checkScale")
    val checkAlpha by animateFloatAsState(targetValue = if (item.isPurchased) 1f else 0f, animationSpec = spring(stiffness = Spring.StiffnessMedium), label = "checkAlpha")
    val strikeProgress by animateFloatAsState(targetValue = if (item.isPurchased) 1f else 0f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium), label = "strikeProgress")

    val handleAmountChange: (String) -> Unit = {
        if (it.isBlank()) { onAmountChanged(0.0) } else {
            val cleanNum = com.example.expensetracker.common.MarketListParser.convertBengaliDigitsToEnglish(it)
            cleanNum.toDoubleOrNull()?.let { amt -> onAmountChanged(amt) }
        }
    }

    val lastRateResolved: String? = remember(priceInsight, lastRateText, isBangla, currencySymbol) {
        priceInsight?.takeIf { it.currentUnitPrice > 0.0 }?.let {
            val rate = if (it.currentUnitPrice % 1.0 == 0.0) it.currentUnitPrice.toLong().toString() else "%.1f".format(it.currentUnitPrice)
            val rateNum = if (isBangla) convertToBengaliDigits(rate) else rate
            if (isBangla) "গত দর: $currencySymbol$rateNum/${it.baseUnit}" else "Last: $currencySymbol$rateNum/${it.baseUnit}"
        } ?: lastRateText
    }

    val estBadgeText: String? = remember(item, priceInsight, isBangla, currencySymbol) {
        if (!item.isPurchased && priceInsight != null && priceInsight.currentUnitPrice > 0.0 && item.quantity > 0.0) {
            val estCost = com.example.expensetracker.common.UnitConverterEngine.estimateCost(priceInsight.currentUnitPrice, item.quantity, item.unit)
            val estStr = if (estCost % 1.0 == 0.0) estCost.toLong().toString() else "%.0f".format(estCost)
            val estNum = if (isBangla) convertToBengaliDigits(estStr) else estStr
            if (isBangla) "সম্ভাব্য $currencySymbol$estNum" else "Est. $currencySymbol$estNum"
        } else null
    }

    val accessibilityLabel = "${item.name}, ${item.formatQuantity()}, ${if (item.isPurchased) (if (isBangla) "কেনা হয়েছে" else "Bought") + (item.amount?.takeIf { it > 0 }?.let { ", ${formatCurrency(it, currencySymbol)}" } ?: "") else (if (isBangla) "কেনা হয়নি" else "Not bought")}${if (!lastRateResolved.isNullOrBlank()) ", $lastRateResolved" else ""}"
    val dept = getDepartmentMeta(getMarketItemCategory(item.name))

    Column(modifier = modifier.fillMaxWidth()) {
        SwipeToDismissBox(
            state = dismissState, modifier = Modifier.fillMaxWidth(),
            enableDismissFromStartToEnd = true, enableDismissFromEndToStart = isEditMode,
            backgroundContent = {
                when (dismissState.dismissDirection) {
                    SwipeToDismissBoxValue.StartToEnd -> {
                        Box(modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp)).background(appColors.income).padding(horizontal = 20.dp), contentAlignment = Alignment.CenterStart) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(imageVector = Icons.Filled.Check, contentDescription = if (isBangla) "কেনা হয়েছে" else "Purchased", tint = Color.White, modifier = Modifier.size(22.dp))
                                Text(text = if (item.isPurchased) (if (isBangla) "অসম্পূর্ণ রাখুন" else "Revert") else (if (isBangla) "কেনা হয়েছে" else "Done"), style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp, fontFeatureSettings = "tnum"))
                            }
                        }
                    }
                    SwipeToDismissBoxValue.EndToStart -> if (isEditMode) MarketItemDeleteSwipeReveal(isBangla = isBangla, appColors = appColors)
                    SwipeToDismissBoxValue.Settled -> Unit
                }
            }
        ) {
            Surface(
                color = if (item.isPurchased) appColors.cardBackground.copy(alpha = 0.65f) else appColors.cardBackground,
                modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 64.dp)
                    .semantics {
                        contentDescription = accessibilityLabel
                        customActions = listOf(
                            CustomAccessibilityAction(if (item.isPurchased) "Mark as unbought" else "Mark as bought") { onTickClick(); true },
                            CustomAccessibilityAction("Delete item") { onRequestDelete(); true }
                        )
                    }
                    .clickable { haptics.lightTick(); if (isEditMode) onUnitClick() else onRowClick() }
                    .combinedClickable(
                        onClick = { haptics.lightTick(); if (isEditMode) onUnitClick() else onRowClick() },
                        onLongClick = { haptics.longPressFeedback(); if (!item.isPurchased && onSubstituteClick != null) onSubstituteClick() else onUnitClick() }
                    )
            ) {
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(modifier = Modifier.minimumInteractiveComponentSize().sizeIn(minWidth = 48.dp, minHeight = 48.dp).clickable(enabled = !isEditMode) { haptics.mediumClick(); onTickClick() }, contentAlignment = Alignment.Center) {
                        Box(modifier = Modifier.size(28.dp).clip(CircleShape).background(if (item.isPurchased) appColors.income else Color.Transparent).border(1.5.dp, if (item.isPurchased) appColors.income else appColors.divider, CircleShape), contentAlignment = Alignment.Center) {
                            if (item.isPurchased || checkScale > 0.05f) {
                                Icon(Icons.Filled.Check, "Purchased", tint = Color.White, modifier = Modifier.size(17.dp).graphicsLayer { scaleX = checkScale; scaleY = checkScale; alpha = checkAlpha })
                            }
                        }
                    }
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.weight(1f, fill = false)) {
                            val strikethroughColor = appColors.textMuted
                            Text(
                                text = item.name,
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp, lineHeight = 20.sp, fontWeight = if (item.isPurchased) FontWeight.Normal else FontWeight.SemiBold, textDecoration = if (item.isPurchased) TextDecoration.LineThrough else TextDecoration.None),
                                color = if (item.isPurchased) appColors.textMuted else appColors.textPrimary, maxLines = 2, overflow = TextOverflow.Clip,
                                modifier = Modifier.weight(1f, fill = false).drawWithContent {
                                    drawContent()
                                    if (strikeProgress > 0.01f) {
                                        val y = size.height / 2f
                                        drawLine(color = strikethroughColor, start = Offset(0f, y), end = Offset(size.width * strikeProgress, y), strokeWidth = 2.dp.toPx(), cap = StrokeCap.Round)
                                    }
                                }
                            )
                            Surface(
                                shape = RoundedCornerShape(8.dp), color = if (item.isPurchased) appColors.surfaceVariant.copy(alpha = 0.5f) else appColors.accentContainer.copy(alpha = 0.4f),
                                border = BorderStroke(HairlineInkStroke, if (item.isPurchased) appColors.divider else appColors.accent.copy(alpha = 0.25f)),
                                modifier = Modifier.minimumInteractiveComponentSize().defaultMinSize(minHeight = 32.dp).clickable(enabled = !item.isPurchased) { haptics.lightTick(); onUnitClick() }
                            ) {
                                Row(modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                    Text(text = item.formatQuantity(), style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFeatureSettings = "tnum"), color = if (item.isPurchased) appColors.textMuted else appColors.textPrimary)
                                    if (!item.isPurchased) Icon(Icons.Outlined.Edit, "Edit quantity", tint = appColors.textSecondary, modifier = Modifier.size(10.dp))
                                }
                            }
                        }
                        if (!lastRateResolved.isNullOrBlank()) {
                            Text(text = lastRateResolved, style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp, lineHeight = 16.sp, fontWeight = FontWeight.Normal, fontFeatureSettings = "tnum"), color = appColors.textMuted, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                        if (isEditMode) {
                            Text(text = "${dept.icon} ${if (isBangla) dept.titleBn else dept.titleEn}", style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp), color = appColors.textMuted, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }
                    if (isEditMode) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            if (onSubstituteClick != null && !item.isPurchased) {
                                IconButton(onClick = { haptics.lightTick(); onSubstituteClick() }, modifier = Modifier.size(48.dp).minimumInteractiveComponentSize()) {
                                    Icon(Icons.Outlined.SwapHoriz, "Substitute item", tint = appColors.accent, modifier = Modifier.size(20.dp))
                                }
                            }
                            IconButton(onClick = { haptics.mediumClick(); onRequestDelete() }, modifier = Modifier.size(48.dp).minimumInteractiveComponentSize()) {
                                Icon(Icons.Outlined.Delete, "Delete item", tint = appColors.expense, modifier = Modifier.size(20.dp))
                            }
                        }
                    } else {
                        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            if (item.isPurchased) {
                                val amt = item.amount ?: 0.0
                                Surface(shape = RoundedCornerShape(8.dp), color = appColors.income.copy(alpha = 0.12f), border = BorderStroke(HairlineInkStroke, appColors.income.copy(alpha = 0.3f))) {
                                    Text(text = if (amt > 0) formatCurrency(amt, currencySymbol) else (if (isBangla) "কেনা হয়েছে" else "Bought"), style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFeatureSettings = "tnum"), color = appColors.income, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                                }
                            } else if (estBadgeText != null) {
                                Surface(shape = RoundedCornerShape(8.dp), color = appColors.surfaceVariant, border = BorderStroke(HairlineInkStroke, appColors.divider)) {
                                    Text(text = estBadgeText, style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp, fontWeight = FontWeight.SemiBold, fontFeatureSettings = "tnum"), color = appColors.textSecondary, modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
        HorizontalDivider(color = appColors.divider, thickness = HairlineInkStroke)
    }
}
