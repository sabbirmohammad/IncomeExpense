package com.example.expensetracker.ui.bazaar

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.common.DigitConverter
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.data.model.ProductPriceInsight
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.AdaptiveSheet
import com.example.expensetracker.ui.components.PulseKeypad
import com.example.expensetracker.ui.components.PulseSegmented
import com.example.expensetracker.ui.components.PulseTabularText
import com.example.expensetracker.ui.components.PulseText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BazaarPricePadSheet(
    item: MarketItem,
    initialMode: PricingMode = PricingMode.PER_UNIT,
    lastRate: Pair<Double, String>? = null,
    priceInsight: ProductPriceInsight? = null,
    onConfirm: (item: MarketItem, totalAmount: Double, mode: PricingMode) -> Unit,
    onDismiss: () -> Unit,
    onMarkNotAvailable: () -> Unit,
    onRemoveItem: () -> Unit,
    onUndoPurchase: (() -> Unit)? = null,
    isBangla: Boolean = false,
    useBengaliNumerals: Boolean = false,
    isPrivacyMode: Boolean = false
) {
    val appColors = LocalAppColors.current
    val typography = MaterialTheme.typography

    // 1. Pricing Mode
    var currentMode by remember { mutableStateOf(initialMode) }

    // 2. Mutable Qty and Unit
    var currentQty by remember { mutableDoubleStateOf(if (item.quantity > 0.0) item.quantity else 1.0) }
    var currentUnit by remember { mutableStateOf(item.unit.trim().ifBlank { "কেজি" }) }

    val standardUnits = remember {
        listOf("কেজি", "গ্রাম", "লিটার", "মিলি", "পিস", "হালি", "ডজন", "প্যাকেট")
    }
    var showUnitMenu by remember { mutableStateOf(false) }

    // 3. Keypad Expression Initializer
    var expression by remember {
        val initialExpr = BazaarShopLogic.resolvePricePadInitialExpression(
            item = item,
            initialMode = initialMode,
            lastRate = lastRate
        )
        mutableStateOf(initialExpr)
    }

    // 4. Derived Calculations
    val exprNumeric = remember(expression) {
        expression.toDoubleOrNull() ?: 0.0
    }

    val totalAmount = remember(currentMode, exprNumeric, currentQty, currentUnit) {
        if (currentMode == PricingMode.TOTAL_PAID) {
            exprNumeric
        } else {
            BazaarShopLogic.calculateTotalFromUnitPrice(exprNumeric, currentQty, currentUnit)
        }
    }

    val unitPrice = remember(currentMode, exprNumeric, currentQty, currentUnit) {
        if (currentMode == PricingMode.PER_UNIT) {
            exprNumeric
        } else {
            BazaarShopLogic.calculateUnitPriceFromTotal(exprNumeric, currentQty, currentUnit)
        }
    }

    // 5. Price Recommendation Badge (8% threshold and purchaseCount >= 3 per Amendment 11)
    val recommendation = remember(unitPrice, priceInsight, lastRate, isBangla) {
        BazaarShopLogic.evaluatePriceRecommendation(
            currentUnitPrice = unitPrice,
            avgUnitPrice = priceInsight?.avgUnitPrice ?: 0.0,
            purchaseCount = priceInsight?.purchaseCount ?: 0,
            lastUnitPrice = lastRate?.first,
            isBangla = isBangla
        )
    }

    // Quick adjustment helper
    fun adjustExpression(delta: Double) {
        val cur = expression.toDoubleOrNull() ?: 0.0
        val newAmt = BazaarShopLogic.applyQuickAdjustment(cur, delta)
        expression = if (newAmt % 1.0 == 0.0) newAmt.toInt().toString() else "%.1f".format(newAmt)
    }

    AdaptiveSheet(
        onDismissRequest = onDismiss,
        modifier = Modifier.padding(bottom = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header: Item name + Close Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.name,
                        style = typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = appColors.textPrimary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = if (isBangla) "মূল্য নির্ধারণ ও নিশ্চিতকরণ" else "Set and confirm price",
                        style = typography.bodySmall,
                        color = appColors.textMuted
                    )
                }
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = if (isBangla) "বন্ধ করুন" else "Close",
                        tint = appColors.textMuted
                    )
                }
            }

            // Mode Selector: [ প্রতি ইউনিট | মোট পরিশোধ ] (Amendment 4 & 9)
            val modes = remember(isBangla, currentUnit) {
                listOf(
                    if (isBangla) "প্রতি $currentUnit (৳)" else "Per $currentUnit (৳)",
                    if (isBangla) "মোট পরিশোধ (৳)" else "Total paid (৳)"
                )
            }
            val selectedModeIndex = if (currentMode == PricingMode.PER_UNIT) 0 else 1

            PulseSegmented(
                items = modes,
                selectedIndex = selectedModeIndex,
                onItemSelected = { index ->
                    val newMode = if (index == 0) PricingMode.PER_UNIT else PricingMode.TOTAL_PAID
                    if (newMode != currentMode) {
                        expression = BazaarShopLogic.computePricePadModeSwitchExpression(
                            newMode = newMode,
                            currentUnitPrice = unitPrice,
                            currentTotalAmount = totalAmount
                        )
                        currentMode = newMode
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Prominent Amount & Live Computation Display
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = appColors.surfaceVariant.copy(alpha = 0.5f),
                border = androidx.compose.foundation.BorderStroke(1.dp, appColors.border),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val displayExpr = if (expression.isBlank()) "০" else expression
                    val formattedExpr = if (useBengaliNumerals || isBangla) DigitConverter.toBengaliDigits(displayExpr) else displayExpr

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "৳",
                            style = typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            color = appColors.accent
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        PulseTabularText(
                            text = formattedExpr,
                            style = typography.displaySmall.copy(fontWeight = FontWeight.Bold),
                            color = appColors.accent
                        )
                        if (currentMode == PricingMode.PER_UNIT) {
                            Text(
                                text = " / $currentUnit",
                                style = typography.titleMedium.copy(fontWeight = FontWeight.Medium),
                                color = appColors.textSecondary,
                                modifier = Modifier.padding(start = 6.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Secondary Subtext
                    val subtext = if (currentMode == PricingMode.PER_UNIT) {
                        val totNum = if (totalAmount % 1.0 == 0.0) "${totalAmount.toInt()}" else "%.1f".format(totalAmount)
                        val totDisplay = if (useBengaliNumerals || isBangla) DigitConverter.toBengaliDigits(totNum) else totNum
                        val qtyNum = if (currentQty % 1.0 == 0.0) "${currentQty.toInt()}" else "$currentQty"
                        val qtyDisplay = if (useBengaliNumerals || isBangla) DigitConverter.toBengaliDigits(qtyNum) else qtyNum
                        if (isBangla) "মোট পরিশোধ: ৳$totDisplay ($qtyDisplay $currentUnit এর জন্য)" else "Total paid: ৳$totDisplay (for $qtyDisplay $currentUnit)"
                    } else {
                        val rateNum = if (unitPrice % 1.0 == 0.0) "${unitPrice.toInt()}" else "%.1f".format(unitPrice)
                        val rateDisplay = if (useBengaliNumerals || isBangla) DigitConverter.toBengaliDigits(rateNum) else rateNum
                        if (isBangla) "প্রতি ইউনিটের দর: ৳$rateDisplay/$currentUnit" else "Unit rate: ৳$rateDisplay/$currentUnit"
                    }

                    PulseText(
                        text = subtext,
                        style = typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = appColors.textSecondary
                    )

                    // Price Recommendation Pill (Amendment 11)
                    if (recommendation.badgeText.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        val pillBg = when {
                            recommendation.isGoodTimeToBuy -> appColors.income.copy(alpha = 0.15f)
                            recommendation.isPricey -> appColors.warning.copy(alpha = 0.15f)
                            else -> appColors.surface
                        }
                        val pillText = when {
                            recommendation.isGoodTimeToBuy -> appColors.income
                            recommendation.isPricey -> appColors.warning
                            else -> appColors.textSecondary
                        }
                        val trendIcon = when {
                            recommendation.isGoodTimeToBuy -> Icons.Default.ArrowDownward
                            recommendation.isPricey -> Icons.Default.ArrowUpward
                            else -> null
                        }
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = pillBg,
                            border = androidx.compose.foundation.BorderStroke(1.dp, pillText.copy(alpha = 0.3f)),
                            modifier = Modifier.semantics {
                                contentDescription = "${recommendation.badgeText}, ${recommendation.varianceText}"
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (trendIcon != null) {
                                    Icon(
                                        imageVector = trendIcon,
                                        contentDescription = null,
                                        tint = pillText,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                }
                                Text(
                                    text = recommendation.badgeText,
                                    style = typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = pillText
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = recommendation.varianceText,
                                    style = typography.bodySmall,
                                    color = pillText
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Editable Quantity & Unit Strip (Amendment 4)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Quantity controls
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isBangla) "পরিমাণ:" else "Qty:",
                        style = typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = appColors.textMuted,
                        modifier = Modifier.padding(end = 6.dp)
                    )

                    IconButton(
                        onClick = {
                            val newQty = (currentQty - 0.5).coerceAtLeast(0.1)
                            currentQty = BazaarShopLogic.roundToTwoDecimals(newQty)
                        },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Remove, contentDescription = "Decrease qty", tint = appColors.textPrimary)
                    }

                    val qtyText = if (currentQty % 1.0 == 0.0) "${currentQty.toInt()}" else "$currentQty"
                    val qtyDisplay = if (useBengaliNumerals || isBangla) DigitConverter.toBengaliDigits(qtyText) else qtyText

                    PulseTabularText(
                        text = qtyDisplay,
                        style = typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = appColors.textPrimary,
                        modifier = Modifier.padding(horizontal = 6.dp)
                    )

                    IconButton(
                        onClick = {
                            val newQty = currentQty + 0.5
                            currentQty = BazaarShopLogic.roundToTwoDecimals(newQty)
                        },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Increase qty", tint = appColors.textPrimary)
                    }
                }

                // Unit Selector Dropdown
                Box {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = appColors.surfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, appColors.border),
                        modifier = Modifier
                            .heightIn(min = 48.dp)
                            .clickable(role = Role.Button, onClick = { showUnitMenu = true })
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = currentUnit,
                                style = typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = appColors.textPrimary
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Select unit",
                                tint = appColors.textMuted
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = showUnitMenu,
                        onDismissRequest = { showUnitMenu = false }
                    ) {
                        standardUnits.forEach { u ->
                            DropdownMenuItem(
                                text = { Text(text = u) },
                                onClick = {
                                    currentUnit = u
                                    showUnitMenu = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Quick Chips Strip (Amendment 4): [-5], [+5], [+10], [Same as last], [Avg], [Min]
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // [-5]
                QuickAdjustChip(
                    label = if (useBengaliNumerals || isBangla) "-৫" else "-5",
                    onClick = { adjustExpression(-5.0) }
                )

                // [+5]
                QuickAdjustChip(
                    label = if (useBengaliNumerals || isBangla) "+৫" else "+5",
                    onClick = { adjustExpression(5.0) }
                )

                // [+10]
                QuickAdjustChip(
                    label = if (useBengaliNumerals || isBangla) "+১০" else "+10",
                    onClick = { adjustExpression(10.0) }
                )

                // [Same as last]
                if (lastRate != null && lastRate.first > 0.0) {
                    val targetVal = if (currentMode == PricingMode.PER_UNIT) {
                        lastRate.first
                    } else {
                        BazaarShopLogic.calculateLastPriceAmount(currentQty, currentUnit, lastRate.first, lastRate.second)
                    }
                    val targetStr = if (targetVal % 1.0 == 0.0) "${targetVal.toInt()}" else "%.1f".format(targetVal)
                    val display = if (useBengaliNumerals || isBangla) DigitConverter.toBengaliDigits(targetStr) else targetStr

                    QuickAdjustChip(
                        label = if (isBangla) "গতবার ৳$display" else "Last ৳$display",
                        onClick = {
                            expression = targetStr
                        }
                    )
                }

                // [Avg]
                if (priceInsight != null && priceInsight.avgUnitPrice > 0.0) {
                    val avgVal = if (currentMode == PricingMode.PER_UNIT) {
                        priceInsight.avgUnitPrice
                    } else {
                        BazaarShopLogic.calculateTotalFromUnitPrice(priceInsight.avgUnitPrice, currentQty, currentUnit)
                    }
                    val avgStr = if (avgVal % 1.0 == 0.0) "${avgVal.toInt()}" else "%.1f".format(avgVal)
                    val display = if (useBengaliNumerals || isBangla) DigitConverter.toBengaliDigits(avgStr) else avgStr

                    QuickAdjustChip(
                        label = if (isBangla) "গড় ৳$display" else "Avg ৳$display",
                        onClick = {
                            expression = avgStr
                        }
                    )
                }

                // [Min]
                if (priceInsight != null && priceInsight.minUnitPrice > 0.0) {
                    val minVal = if (currentMode == PricingMode.PER_UNIT) {
                        priceInsight.minUnitPrice
                    } else {
                        BazaarShopLogic.calculateTotalFromUnitPrice(priceInsight.minUnitPrice, currentQty, currentUnit)
                    }
                    val minStr = if (minVal % 1.0 == 0.0) "${minVal.toInt()}" else "%.1f".format(minVal)
                    val display = if (useBengaliNumerals || isBangla) DigitConverter.toBengaliDigits(minStr) else minStr

                    QuickAdjustChip(
                        label = if (isBangla) "নূন্যতম ৳$display" else "Min ৳$display",
                        onClick = {
                            expression = minStr
                        }
                    )
                }
            }

            // Canvas Sparkline with TalkBack Accessibility (Amendment 4 & 15)
            val pricePoints = priceInsight?.recentPricePoints ?: emptyList()
            if (pricePoints.size >= 2) {
                Spacer(modifier = Modifier.height(10.dp))
                PriceHistorySparkline(
                    points = pricePoints,
                    accentColor = appColors.accent,
                    isBangla = isBangla,
                    useBengaliNumerals = useBengaliNumerals
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Embedded PulseKeypad (Amendment 4)
            PulseKeypad(
                expression = expression,
                onExpressionChange = { expression = it },
                isBangla = isBangla,
                doneText = if (isBangla) "নিশ্চিত" else "Confirm",
                onDone = {
                    val updatedItem = item.copy(quantity = currentQty, unit = currentUnit)
                    onConfirm(updatedItem, totalAmount, currentMode)
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Secondary Actions Row (Amendment 4): [Not available], [Remove / Undo]
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Not available
                OutlinedButton(
                    onClick = onMarkNotAvailable,
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = if (isBangla) "পাওয়া যায়নি" else "Not available",
                        style = typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = appColors.textSecondary
                    )
                }

                // Undo Purchase or Delete
                if (item.isPurchased && onUndoPurchase != null) {
                    OutlinedButton(
                        onClick = onUndoPurchase,
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = appColors.warning)
                    ) {
                        Text(
                            text = if (isBangla) "ক্রয় বাতিল" else "Undo purchase",
                            style = typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                            color = appColors.warning
                        )
                    }
                } else {
                    OutlinedButton(
                        onClick = onRemoveItem,
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = appColors.expense)
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isBangla) "তালিকা থেকে মুছুন" else "Remove",
                            style = typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                            color = appColors.expense
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickAdjustChip(
    label: String,
    onClick: () -> Unit
) {
    val appColors = LocalAppColors.current
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = appColors.surfaceVariant.copy(alpha = 0.7f),
        border = androidx.compose.foundation.BorderStroke(1.dp, appColors.border),
        modifier = Modifier
            .heightIn(min = 48.dp)
            .clickable(role = Role.Button, onClick = onClick)
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            PulseTabularText(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = appColors.textPrimary
            )
        }
    }
}

@Composable
private fun PriceHistorySparkline(
    points: List<Double>,
    accentColor: Color,
    isBangla: Boolean,
    useBengaliNumerals: Boolean
) {
    val appColors = LocalAppColors.current
    val typography = MaterialTheme.typography

    val talkBackDesc = remember(points, isBangla, useBengaliNumerals) {
        val pointsStr = points.joinToString(", ") { pt ->
            val numStr = if (pt % 1.0 == 0.0) "${pt.toInt()}" else "%.1f".format(pt)
            val disp = if (useBengaliNumerals || isBangla) DigitConverter.toBengaliDigits(numStr) else numStr
            "৳$disp"
        }
        if (isBangla) "দামের ইতিহাস: $pointsStr" else "Price history: $pointsStr"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .semantics { contentDescription = talkBackDesc }
    ) {
        Text(
            text = if (isBangla) "সাম্প্রতিক দামের রেখাচিত্র" else "Recent Price Trend",
            style = typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = appColors.textMuted,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp)
        ) {
            val minVal = points.minOrNull() ?: 0.0
            val maxVal = points.maxOrNull() ?: 1.0
            val range = (maxVal - minVal).coerceAtLeast(1.0)

            val width = size.width
            val height = size.height
            val stepX = if (points.size > 1) width / (points.size - 1) else width

            val path = Path()
            val fillPath = Path()

            val coordinates = points.mapIndexed { index, pt ->
                val x = index * stepX
                val normalizedY = ((pt - minVal) / range).toFloat()
                val y = height - (normalizedY * (height - 12f)) - 6f
                Offset(x, y)
            }

            if (coordinates.isNotEmpty()) {
                path.moveTo(coordinates[0].x, coordinates[0].y)
                fillPath.moveTo(coordinates[0].x, height)
                fillPath.lineTo(coordinates[0].x, coordinates[0].y)

                for (i in 1 until coordinates.size) {
                    path.lineTo(coordinates[i].x, coordinates[i].y)
                    fillPath.lineTo(coordinates[i].x, coordinates[i].y)
                }

                fillPath.lineTo(coordinates.last().x, height)
                fillPath.close()

                // Draw gradient fill
                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(accentColor.copy(alpha = 0.25f), Color.Transparent),
                        startY = 0f,
                        endY = height
                    )
                )

                // Draw stroke line
                drawPath(
                    path = path,
                    color = accentColor,
                    style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                )

                // Draw dots on data points
                coordinates.forEachIndexed { idx, offset ->
                    val isLast = idx == coordinates.lastIndex
                    drawCircle(
                        color = if (isLast) accentColor else accentColor.copy(alpha = 0.6f),
                        radius = if (isLast) 4.dp.toPx() else 2.5.dp.toPx(),
                        center = offset
                    )
                }
            }
        }
    }
}

@Preview(name = "PricePad - 200% Font Scale Bengali", fontScale = 2f, showBackground = true)
@Composable
private fun PreviewBazaarPricePadSheetBengaliLongName() {
    Surface {
        BazaarPricePadSheet(
            item = MarketItem(
                id = 1,
                listId = 1,
                name = "নরম তুলতুলে বড় আকারের তাজা দেশি মুরগি",
                quantity = 1.0,
                unit = "কেজি",
                amount = 240.0,
                isPurchased = false
            ),
            lastRate = Pair(220.0, "কেজি"),
            priceInsight = ProductPriceInsight(
                productName = "মুরগি",
                currentUnitPrice = 240.0,
                baseUnit = "কেজি",
                avgUnitPrice = 220.0,
                purchaseCount = 5,
                recentPricePoints = listOf(200.0, 210.0, 215.0, 220.0, 240.0)
            ),
            onConfirm = { _, _, _ -> },
            onDismiss = {},
            onMarkNotAvailable = {},
            onRemoveItem = {},
            isBangla = true,
            useBengaliNumerals = true
        )
    }
}
