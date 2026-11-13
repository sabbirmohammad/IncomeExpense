package com.example.expensetracker.ui.market

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.common.UnitConverterEngine
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.data.model.ProductPriceInsight
import com.example.expensetracker.theme.ExpenseTrackerTheme
import com.example.expensetracker.theme.HairlineInkStroke
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.AmountKeypad
import com.example.expensetracker.ui.components.AmountKeypadLogic
import com.example.expensetracker.ui.components.BanglaStrings
import com.example.expensetracker.ui.components.EnglishStrings
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.formatCurrency
import com.example.expensetracker.ui.components.rememberAppHaptics
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * V7 Target Design D: Interactive Price Bottom Sheet.
 * Layout (top to bottom):
 * 1. Item name & status
 * 2. Quantity stepper + unit chips (কেজি, গ্রাম, লিটার, পিস, হালি, ডজন)
 * 3. Segmented control "Per unit | Total price" (প্রতি ইউনিট | মোট দাম)
 * 4. Big price field using AmountKeypad (tactile 4x4 with math operators)
 * 5. Live variance line vs last price ("▼ ৳5 less than last, −9%")
 * 6. Quick chips: "Same as last ৳55", "+৳5", "−৳5"
 * 7. Action buttons: Primary "Save & next" and secondary "Save"
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketPriceEntrySheet(
    item: MarketItem,
    currencySymbol: String,
    isBangla: Boolean,
    hasNextPendingItem: Boolean,
    onDismiss: () -> Unit,
    onSaveItem: (updatedQuantity: Double, updatedUnit: String, totalAmount: Double, shouldAdvanceToNext: Boolean) -> Unit,
    lastRate: Pair<Double, String>? = null,
    priceInsight: ProductPriceInsight? = null,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val strings = LocalAppStrings.current
    val haptics = rememberAppHaptics()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Standard Bangladeshi Bazar units
    val standardUnits = remember {
        listOf("কেজি", "গ্রাম", "লিটার", "পিস", "হালি", "ডজন")
    }

    var quantity by remember(item.id, item.quantity) {
        mutableDoubleStateOf(if (item.quantity > 0) item.quantity else 1.0)
    }
    var unit by remember(item.id, item.unit) {
        mutableStateOf(item.unit.trim().ifBlank { "কেজি" })
    }

    // 0: Per unit rate, 1: Total price
    var priceMode by remember(item.id) { mutableIntStateOf(0) }

    // Auto-advance toggle preference
    var autoAdvanceEnabled by remember { mutableStateOf(true) }

    // Initial price expression derivation
    var expression by remember(item.id, item.amount) {
        val initialAmt = item.amount
        if (initialAmt != null && initialAmt > 0) {
            val formatted = if (initialAmt % 1.0 == 0.0) initialAmt.toInt().toString() else initialAmt.toString()
            mutableStateOf(formatted)
        } else {
            // Suggest previous rate if available
            val prevRate = lastRate?.first ?: priceInsight?.currentUnitPrice
            if (prevRate != null && prevRate > 0) {
                val formatted = if (prevRate % 1.0 == 0.0) prevRate.toInt().toString() else prevRate.toString()
                mutableStateOf(formatted)
            } else {
                mutableStateOf("")
            }
        }
    }

    // Amount evaluation
    val evaluatedValue = remember(expression) { AmountKeypadLogic.evaluate(expression) }
    val enteredNumber = evaluatedValue ?: expression.toDoubleOrNull() ?: 0.0

    // Computed cross-values:
    // If in "Per unit" mode -> enteredNumber is rate per base unit -> compute total
    // If in "Total price" mode -> enteredNumber is total -> compute rate per base unit
    val totalAmountComputed: Double = remember(priceMode, enteredNumber, quantity, unit) {
        if (priceMode == 0) {
            if (enteredNumber > 0 && quantity > 0) {
                UnitConverterEngine.estimateCost(enteredNumber, quantity, unit)
            } else 0.0
        } else {
            enteredNumber
        }
    }

    val unitRateComputed: Double = remember(priceMode, enteredNumber, quantity, unit) {
        if (priceMode == 0) {
            enteredNumber
        } else {
            if (enteredNumber > 0 && quantity > 0) {
                UnitConverterEngine.calculateBaseUnitPrice(enteredNumber, quantity, unit).first
            } else 0.0
        }
    }

    // Last rate resolution for variance comparison
    val baselineUnitPrice: Double? = remember(lastRate, priceInsight) {
        lastRate?.first ?: priceInsight?.currentUnitPrice
    }

    // Live variance comparison vs last price
    val varianceDiff: Double? = remember(baselineUnitPrice, unitRateComputed) {
        if (baselineUnitPrice != null && baselineUnitPrice > 0 && unitRateComputed > 0) {
            unitRateComputed - baselineUnitPrice
        } else null
    }

    val variancePercent: Double? = remember(baselineUnitPrice, varianceDiff) {
        if (baselineUnitPrice != null && baselineUnitPrice > 0 && varianceDiff != null) {
            (varianceDiff / baselineUnitPrice) * 100.0
        } else null
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        containerColor = appColors.cardBackground,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(38.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(appColors.divider)
            )
        },
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // 1. Header: Item Name & Close
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            lineHeight = 24.sp
                        ),
                        color = appColors.textPrimary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (baselineUnitPrice != null && baselineUnitPrice > 0) {
                        val baseUnit = lastRate?.second ?: "কেজি"
                        val rateStr = if (baselineUnitPrice % 1.0 == 0.0) baselineUnitPrice.toInt().toString() else "%.1f".format(baselineUnitPrice)
                        val rateDigits = if (isBangla) convertToBengaliDigits(rateStr) else rateStr
                        Text(
                            text = "${strings.marketLastTime}: $currencySymbol$rateDigits/$baseUnit",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 12.sp,
                                fontFeatureSettings = "tnum"
                            ),
                            color = appColors.textSecondary
                        )
                    }
                }

                IconButton(
                    onClick = {
                        haptics.lightTick()
                        onDismiss()
                    },
                    modifier = Modifier.minimumInteractiveComponentSize()
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Close",
                        tint = appColors.textSecondary
                    )
                }
            }

            // 2. Quantity Stepper + Unit Chips
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = appColors.surface,
                border = BorderStroke(HairlineInkStroke, appColors.divider),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isBangla) "পরিমাণ ও একক:" else "Quantity & Unit:",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            ),
                            color = appColors.textSecondary
                        )

                        // Stepper: [-] quantity [+]
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                onClick = {
                                    haptics.lightTick()
                                    val step = if (quantity <= 1.0) 0.25 else if (quantity <= 5.0) 0.5 else 1.0
                                    quantity = (quantity - step).coerceAtLeast(0.1)
                                },
                                shape = CircleShape,
                                color = appColors.cardBackground,
                                border = BorderStroke(HairlineInkStroke, appColors.divider),
                                modifier = Modifier
                                    .size(36.dp)
                                    .minimumInteractiveComponentSize()
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Filled.Remove,
                                        contentDescription = "Decrease",
                                        tint = appColors.textPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            val qtyFormatted = if (quantity % 1.0 == 0.0) quantity.toInt().toString() else "%.2f".format(quantity).trimEnd('0').trimEnd('.')
                            val qtyBengali = if (isBangla) convertToBengaliDigits(qtyFormatted) else qtyFormatted
                            Text(
                                text = "$qtyBengali $unit",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    fontFeatureSettings = "tnum"
                                ),
                                color = appColors.textPrimary
                            )

                            Surface(
                                onClick = {
                                    haptics.lightTick()
                                    val step = if (quantity < 1.0) 0.25 else if (quantity < 5.0) 0.5 else 1.0
                                    quantity = (quantity + step)
                                },
                                shape = CircleShape,
                                color = appColors.cardBackground,
                                border = BorderStroke(HairlineInkStroke, appColors.divider),
                                modifier = Modifier
                                    .size(36.dp)
                                    .minimumInteractiveComponentSize()
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Filled.Add,
                                        contentDescription = "Increase",
                                        tint = appColors.textPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Unit Chips (Scrollable row)
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(standardUnits) { u ->
                            val isSelected = unit == u
                            Surface(
                                onClick = {
                                    haptics.lightTick()
                                    unit = u
                                },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) appColors.accentContainer else appColors.cardBackground,
                                border = BorderStroke(
                                    HairlineInkStroke,
                                    if (isSelected) appColors.accent else appColors.divider
                                ),
                                modifier = Modifier
                                    .defaultMinSize(minHeight = 36.dp)
                                    .minimumInteractiveComponentSize()
                            ) {
                                Box(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = u,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        ),
                                        color = if (isSelected) appColors.accent else appColors.textPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 3. Segmented Control: "Per unit | Total price" (48dp height)
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = appColors.surface,
                border = BorderStroke(HairlineInkStroke, appColors.divider),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    // Option 0: Per unit
                    Surface(
                        onClick = {
                            haptics.lightTick()
                            priceMode = 0
                        },
                        shape = RoundedCornerShape(11.dp),
                        color = if (priceMode == 0) appColors.cardBackground else Color.Transparent,
                        border = if (priceMode == 0) BorderStroke(HairlineInkStroke, appColors.divider) else null,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .minimumInteractiveComponentSize()
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = strings.marketPerUnit,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = if (priceMode == 0) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 13.sp
                                ),
                                color = if (priceMode == 0) appColors.accent else appColors.textSecondary
                            )
                        }
                    }

                    // Option 1: Total price
                    Surface(
                        onClick = {
                            haptics.lightTick()
                            priceMode = 1
                        },
                        shape = RoundedCornerShape(11.dp),
                        color = if (priceMode == 1) appColors.cardBackground else Color.Transparent,
                        border = if (priceMode == 1) BorderStroke(HairlineInkStroke, appColors.divider) else null,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .minimumInteractiveComponentSize()
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = strings.marketTotalPrice,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = if (priceMode == 1) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 13.sp
                                ),
                                color = if (priceMode == 1) appColors.accent else appColors.textSecondary
                            )
                        }
                    }
                }
            }

            // 4. Live Display Card (Entered Amount + Computed Cross Value)
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = appColors.surface,
                border = BorderStroke(HairlineInkStroke, appColors.divider),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val label = if (priceMode == 0) "${strings.marketPerUnit} ($unit)" else strings.marketTotalPrice
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = appColors.textSecondary
                    )

                    // Big Entered Price Display
                    val displayExpr = if (expression.isBlank()) "0" else expression
                    val displayDigits = if (isBangla) convertToBengaliDigits(displayExpr) else displayExpr
                    Text(
                        text = "$currencySymbol$displayDigits",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 32.sp,
                            fontFeatureSettings = "tnum"
                        ),
                        color = appColors.textPrimary,
                        textAlign = TextAlign.Center
                    )

                    // Computed Counterpart
                    val crossInfo = if (priceMode == 0) {
                        val computedStr = formatCurrency(totalAmountComputed, currencySymbol)
                        "${strings.marketTotalPrice}: $computedStr"
                    } else {
                        val rateStr = formatCurrency(unitRateComputed, currencySymbol)
                        val norm = UnitConverterEngine.normalize(quantity, unit)
                        "${strings.marketPerUnit}: $rateStr/${norm.baseUnit}"
                    }

                    Text(
                        text = crossInfo,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            fontFeatureSettings = "tnum"
                        ),
                        color = appColors.accent
                    )

                    // 5. Live Variance Line vs Last Price
                    if (varianceDiff != null && baselineUnitPrice != null && baselineUnitPrice > 0) {
                        val diffAbs = abs(varianceDiff)
                        val diffStr = if (diffAbs % 1.0 == 0.0) diffAbs.toInt().toString() else "%.1f".format(diffAbs)
                        val diffDigits = if (isBangla) convertToBengaliDigits(diffStr) else diffStr
                        val pctAbs = abs(variancePercent ?: 0.0).roundToInt().toString()
                        val pctDigits = if (isBangla) convertToBengaliDigits(pctAbs) else pctAbs

                        val varianceText = when {
                            varianceDiff < -0.5 -> {
                                if (isBangla) "▼ ৳$diffDigits গতবারের চেয়ে কম, −$pctDigits%"
                                else "▼ ৳$diffDigits less than last, −$pctDigits%"
                            }
                            varianceDiff > 0.5 -> {
                                if (isBangla) "▲ ৳$diffDigits গতবারের চেয়ে বেশি, +$pctDigits%"
                                else "▲ ৳$diffDigits more than last, +$pctDigits%"
                            }
                            else -> {
                                strings.marketSameAsLast
                            }
                        }

                        val varianceColor = when {
                            varianceDiff < -0.5 -> appColors.income
                            varianceDiff > 0.5 -> appColors.warning
                            else -> appColors.textSecondary
                        }

                        Text(
                            text = varianceText,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                fontFeatureSettings = "tnum"
                            ),
                            color = varianceColor,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }

            // 6. Quick Chips: "Same as last", "+৳5", "−৳5"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (baselineUnitPrice != null && baselineUnitPrice > 0) {
                    val prevPriceFormatted = if (baselineUnitPrice % 1.0 == 0.0) baselineUnitPrice.toInt().toString() else baselineUnitPrice.toString()
                    val prevPriceDigits = if (isBangla) convertToBengaliDigits(prevPriceFormatted) else prevPriceFormatted
                    Surface(
                        onClick = {
                            haptics.lightTick()
                            priceMode = 0
                            expression = prevPriceFormatted
                        },
                        shape = RoundedCornerShape(10.dp),
                        color = appColors.accentContainer.copy(alpha = 0.35f),
                        border = BorderStroke(HairlineInkStroke, appColors.accent.copy(alpha = 0.35f)),
                        modifier = Modifier
                            .weight(1.2f)
                            .defaultMinSize(minHeight = 44.dp)
                            .minimumInteractiveComponentSize()
                    ) {
                        Box(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${strings.marketSameAsLast} ৳$prevPriceDigits",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    fontFeatureSettings = "tnum"
                                ),
                                color = appColors.textPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                // "+৳5" Chip
                Surface(
                    onClick = {
                        haptics.lightTick()
                        val current = enteredNumber
                        val next = (current + 5.0).coerceAtLeast(0.0)
                        expression = if (next % 1.0 == 0.0) next.toInt().toString() else next.toString()
                    },
                    shape = RoundedCornerShape(10.dp),
                    color = appColors.surface,
                    border = BorderStroke(HairlineInkStroke, appColors.divider),
                    modifier = Modifier
                        .weight(0.9f)
                        .defaultMinSize(minHeight = 44.dp)
                        .minimumInteractiveComponentSize()
                ) {
                    Box(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isBangla) "+৳৫" else "+৳5",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFeatureSettings = "tnum"
                            ),
                            color = appColors.textPrimary
                        )
                    }
                }

                // "−৳5" Chip
                Surface(
                    onClick = {
                        haptics.lightTick()
                        val current = enteredNumber
                        val next = (current - 5.0).coerceAtLeast(0.0)
                        expression = if (next % 1.0 == 0.0) next.toInt().toString() else next.toString()
                    },
                    shape = RoundedCornerShape(10.dp),
                    color = appColors.surface,
                    border = BorderStroke(HairlineInkStroke, appColors.divider),
                    modifier = Modifier
                        .weight(0.9f)
                        .defaultMinSize(minHeight = 44.dp)
                        .minimumInteractiveComponentSize()
                ) {
                    Box(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isBangla) "−৳৫" else "−৳5",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFeatureSettings = "tnum"
                            ),
                            color = appColors.textPrimary
                        )
                    }
                }
            }

            // 7. Amount Keypad (Tactile 4x4 with operators)
            AmountKeypad(
                expression = expression,
                onExpressionChange = { newExpr ->
                    expression = newExpr
                },
                currencySymbol = currencySymbol,
                isBangla = isBangla,
                showPreviewLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // 8. Action Buttons: Primary "Save & next" + secondary "Save"
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Primary Action Button
                val isNextAvailable = hasNextPendingItem && autoAdvanceEnabled
                Button(
                    onClick = {
                        haptics.mediumClick()
                        val finalTotal = totalAmountComputed.coerceAtLeast(0.0)
                        onSaveItem(quantity, unit, finalTotal, isNextAvailable)
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = appColors.accent),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = if (isNextAvailable) Icons.AutoMirrored.Filled.ArrowForward else Icons.Filled.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = if (isNextAvailable) strings.marketSaveAndNext else strings.save,
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            ),
                            color = Color.White
                        )
                    }
                }

                // Secondary row: "Save" (without advancing) + Auto-advance toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Secondary Save button (always finishes this item)
                    if (hasNextPendingItem) {
                        OutlinedButton(
                            onClick = {
                                haptics.lightTick()
                                val finalTotal = totalAmountComputed.coerceAtLeast(0.0)
                                onSaveItem(quantity, unit, finalTotal, false)
                            },
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(HairlineInkStroke, appColors.divider),
                            modifier = Modifier.defaultMinSize(minHeight = 44.dp)
                        ) {
                            Text(
                                text = strings.save,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = appColors.textPrimary
                            )
                        }
                    }

                    // Auto-advance toggle chip
                    Surface(
                        onClick = {
                            haptics.lightTick()
                            autoAdvanceEnabled = !autoAdvanceEnabled
                        },
                        shape = RoundedCornerShape(8.dp),
                        color = if (autoAdvanceEnabled) appColors.accentContainer.copy(alpha = 0.4f) else Color.Transparent,
                        border = BorderStroke(
                            HairlineInkStroke,
                            if (autoAdvanceEnabled) appColors.accent else appColors.divider
                        ),
                        modifier = Modifier
                            .defaultMinSize(minHeight = 36.dp)
                            .minimumInteractiveComponentSize()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = if (autoAdvanceEnabled) Icons.Filled.Check else Icons.Filled.Close,
                                contentDescription = null,
                                tint = if (autoAdvanceEnabled) appColors.accent else appColors.textMuted,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = strings.marketAutoAdvance,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                color = if (autoAdvanceEnabled) appColors.accent else appColors.textSecondary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

// =========================================================================
// PREVIEWS (Light, Dark, AMOLED x English, Bengali)
// =========================================================================

@Preview(name = "MarketPriceEntrySheet - Dark En", showBackground = true)
@Composable
fun PreviewMarketPriceEntrySheetDarkEn() {
    ExpenseTrackerTheme(themeMode = "dark") {
        CompositionLocalProvider(LocalAppStrings provides EnglishStrings) {
            Surface(color = LocalAppColors.current.background) {
                MarketPriceEntrySheet(
                    item = MarketItem(
                        id = 1L,
                        listId = 1L,
                        name = "Potatoes",
                        quantity = 2.0,
                        unit = "kg",
                        amount = null
                    ),
                    currencySymbol = "৳",
                    isBangla = false,
                    hasNextPendingItem = true,
                    onDismiss = {},
                    onSaveItem = { _, _, _, _ -> },
                    lastRate = Pair(30.0, "kg")
                )
            }
        }
    }
}

@Preview(name = "MarketPriceEntrySheet - Light Bn", showBackground = true)
@Composable
fun PreviewMarketPriceEntrySheetLightBn() {
    ExpenseTrackerTheme(themeMode = "light") {
        CompositionLocalProvider(LocalAppStrings provides BanglaStrings) {
            Surface(color = LocalAppColors.current.background) {
                MarketPriceEntrySheet(
                    item = MarketItem(
                        id = 2L,
                        listId = 1L,
                        name = "দেশি পেঁয়াজ",
                        quantity = 1.0,
                        unit = "কেজি",
                        amount = 120.0
                    ),
                    currencySymbol = "৳",
                    isBangla = true,
                    hasNextPendingItem = true,
                    onDismiss = {},
                    onSaveItem = { _, _, _, _ -> },
                    lastRate = Pair(110.0, "কেজি")
                )
            }
        }
    }
}

@Preview(name = "MarketPriceEntrySheet - AMOLED Bn", showBackground = true)
@Composable
fun PreviewMarketPriceEntrySheetAmoledBn() {
    ExpenseTrackerTheme(themeMode = "amoled") {
        CompositionLocalProvider(LocalAppStrings provides BanglaStrings) {
            Surface(color = LocalAppColors.current.background) {
                MarketPriceEntrySheet(
                    item = MarketItem(
                        id = 3L,
                        listId = 1L,
                        name = "রুই মাছ",
                        quantity = 1.5,
                        unit = "কেজি",
                        amount = null
                    ),
                    currencySymbol = "৳",
                    isBangla = true,
                    hasNextPendingItem = false,
                    onDismiss = {},
                    onSaveItem = { _, _, _, _ -> },
                    lastRate = Pair(350.0, "কেজি")
                )
            }
        }
    }
}
