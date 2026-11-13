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
            MarketPriceEntryHeader(
                itemName = item.name,
                baselineUnitPrice = baselineUnitPrice,
                baseUnit = lastRate?.second ?: "কেজি",
                currencySymbol = currencySymbol,
                isBangla = isBangla,
                onDismiss = onDismiss,
                haptics = haptics
            )

            // 2. Quantity Stepper + Unit Chips
            MarketPriceQuantityStepper(
                quantity = quantity,
                onQuantityChange = { quantity = it },
                unit = unit,
                onUnitChange = { unit = it },
                standardUnits = standardUnits,
                isBangla = isBangla,
                haptics = haptics
            )

            // 3. Segmented Control: "Per unit | Total price" (48dp height)
            MarketPriceModeSelector(
                priceMode = priceMode,
                onPriceModeChange = { priceMode = it },
                haptics = haptics
            )

            // 4. Live Display Card (Entered Amount + Computed Cross Value & Variance)
            MarketPriceVarianceDisplay(
                priceMode = priceMode,
                unit = unit,
                quantity = quantity,
                expression = expression,
                currencySymbol = currencySymbol,
                isBangla = isBangla,
                totalAmountComputed = totalAmountComputed,
                unitRateComputed = unitRateComputed,
                baselineUnitPrice = baselineUnitPrice,
                varianceDiff = varianceDiff,
                variancePercent = variancePercent
            )

            // 6. Quick Chips: "Same as last", "+৳5", "−৳5"
            MarketPriceQuickChips(
                baselineUnitPrice = baselineUnitPrice,
                enteredNumber = enteredNumber,
                onApplyPrice = { newExpr, newMode ->
                    expression = newExpr
                    if (newMode >= 0) {
                        priceMode = newMode
                    }
                },
                isBangla = isBangla,
                haptics = haptics
            )

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

            // 8. Action Buttons: Primary "Save & next" + secondary "Save" + Auto-advance toggle
            MarketPriceEntryActionButtons(
                hasNextPendingItem = hasNextPendingItem,
                autoAdvanceEnabled = autoAdvanceEnabled,
                onAutoAdvanceToggle = { autoAdvanceEnabled = !autoAdvanceEnabled },
                onSaveClick = { shouldAdvance ->
                    val finalTotal = totalAmountComputed.coerceAtLeast(0.0)
                    onSaveItem(quantity, unit, finalTotal, shouldAdvance)
                },
                haptics = haptics
            )

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

