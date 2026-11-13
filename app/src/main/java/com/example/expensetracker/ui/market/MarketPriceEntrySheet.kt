package com.example.expensetracker.ui.market

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.data.model.ProductPriceInsight
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.AmountKeypad
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.rememberAppHaptics

/**
 * V7 Target Design D: Interactive Price Bottom Sheet.
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

    val state = rememberMarketPriceEntryState(item, lastRate, priceInsight)

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
                baselineUnitPrice = state.baselineUnitPrice,
                baseUnit = lastRate?.second ?: "কেজি",
                currencySymbol = currencySymbol,
                isBangla = isBangla,
                onDismiss = onDismiss,
                haptics = haptics
            )

            // 2. Quantity Stepper + Unit Chips
            MarketPriceQuantityStepper(
                quantity = state.quantity,
                onQuantityChange = { state.quantity = it },
                unit = state.unit,
                onUnitChange = { state.unit = it },
                standardUnits = state.standardUnits,
                isBangla = isBangla,
                haptics = haptics
            )

            // 3. Segmented Control: "Per unit | Total price" (48dp height)
            MarketPriceModeSelector(
                priceMode = state.priceMode,
                onPriceModeChange = { state.priceMode = it },
                haptics = haptics
            )

            // 4. Live Display Card (Entered Amount + Computed Cross Value & Variance)
            MarketPriceVarianceDisplay(
                priceMode = state.priceMode,
                unit = state.unit,
                quantity = state.quantity,
                expression = state.expression,
                currencySymbol = currencySymbol,
                isBangla = isBangla,
                totalAmountComputed = state.totalAmountComputed,
                unitRateComputed = state.unitRateComputed,
                baselineUnitPrice = state.baselineUnitPrice,
                varianceDiff = state.varianceDiff,
                variancePercent = state.variancePercent
            )

            // 6. Quick Chips: "Same as last", "+৳5", "−৳5"
            MarketPriceQuickChips(
                baselineUnitPrice = state.baselineUnitPrice,
                enteredNumber = state.enteredNumber,
                onApplyPrice = { newExpr, newMode ->
                    state.expression = newExpr
                    if (newMode >= 0) {
                        state.priceMode = newMode
                    }
                },
                isBangla = isBangla,
                haptics = haptics
            )

            // 7. Amount Keypad (Tactile 4x4 with operators)
            AmountKeypad(
                expression = state.expression,
                onExpressionChange = { newExpr ->
                    state.expression = newExpr
                },
                currencySymbol = currencySymbol,
                isBangla = isBangla,
                showPreviewLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // 8. Action Buttons: Primary "Save & next" + secondary "Save" + Auto-advance toggle
            MarketPriceEntryActionButtons(
                hasNextPendingItem = hasNextPendingItem,
                autoAdvanceEnabled = state.autoAdvanceEnabled,
                onAutoAdvanceToggle = { state.autoAdvanceEnabled = !state.autoAdvanceEnabled },
                onSaveClick = { shouldAdvance ->
                    val finalTotal = state.totalAmountComputed.coerceAtLeast(0.0)
                    onSaveItem(state.quantity, state.unit, finalTotal, shouldAdvance)
                },
                haptics = haptics
            )

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

