package com.example.expensetracker.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.common.MarketListParser
import com.example.expensetracker.common.UnitConverterEngine
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.data.model.ProductPriceInsight
import com.example.expensetracker.theme.HairlineInkStroke
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.CalculatorEvaluator
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.rememberAppHaptics

/**
 * Handcrafted Living Ledger price entry bottom sheet for market checklist items.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketPromptAmountDialog(
    item: MarketItem,
    currencySymbol: String,
    isBangla: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (amount: Double) -> Unit,
    lastRate: Pair<Double, String>? = null,
    priceInsight: ProductPriceInsight? = null
) {
    val appColors = LocalAppColors.current
    val haptics = rememberAppHaptics()
    val focusRequester = remember { FocusRequester() }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var promptAmountText by remember(item.id, item.amount) {
        mutableStateOf(item.amount?.let { if (it > 0) (if (it % 1.0 == 0.0) it.toInt().toString() else it.toString()) else "" } ?: "")
    }

    val evaluatedAmount = remember(promptAmountText) {
        CalculatorEvaluator.evaluate(promptAmountText)
            ?: MarketListParser.convertBengaliDigitsToEnglish(promptAmountText).toDoubleOrNull()
    }
    val isExpression = promptAmountText.any { it in "+-*xX/×÷" }

    LaunchedEffect(item.id) {
        try { focusRequester.requestFocus() } catch (_: Exception) {}
    }

    val submitAmount = {
        val amt = evaluatedAmount ?: MarketListParser.convertBengaliDigitsToEnglish(promptAmountText).toDoubleOrNull() ?: 0.0
        haptics.mediumClick()
        onConfirm(amt)
    }

    val addQuickAmount = { addAmt: Int ->
        haptics.lightTick()
        val current = evaluatedAmount ?: MarketListParser.convertBengaliDigitsToEnglish(promptAmountText).toDoubleOrNull() ?: 0.0
        promptAmountText = (current + addAmt).toInt().toString()
    }

    val estimatedCostSuggestion = remember(item, priceInsight, lastRate) {
        val unitPrice = priceInsight?.currentUnitPrice ?: lastRate?.first
        if (unitPrice != null && unitPrice > 0.0 && item.quantity > 0.0) {
            UnitConverterEngine.estimateCost(unitPrice, item.quantity, item.unit)
        } else null
    }

    val (percentDiff, diffRate) = remember(evaluatedAmount, item, priceInsight, lastRate) {
        val refRate = priceInsight?.currentUnitPrice ?: lastRate?.first
        if (evaluatedAmount != null && evaluatedAmount > 0 && item.quantity > 0 && refRate != null && refRate > 0) {
            val (cRate, _) = UnitConverterEngine.calculateBaseUnitPrice(evaluatedAmount, item.quantity, item.unit)
            (((cRate - refRate) / refRate) * 100.0) to (cRate - refRate)
        } else null to null
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss, sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp), containerColor = appColors.cardBackground,
        dragHandle = {
            Box(modifier = Modifier.padding(vertical = 10.dp).width(38.dp).height(4.dp).clip(RoundedCornerShape(2.dp)).background(appColors.divider))
        }
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 6.dp).navigationBarsPadding()) {
            MarketPromptHeader(item = item, isBangla = isBangla, appColors = appColors)

            if (estimatedCostSuggestion != null && estimatedCostSuggestion > 0.0) {
                val estAmtStr = if (estimatedCostSuggestion % 1.0 == 0.0) estimatedCostSuggestion.toLong().toString() else "%.1f".format(estimatedCostSuggestion)
                val estAmtBengali = if (isBangla) convertToBengaliDigits(estAmtStr) else estAmtStr
                val isAlreadyApplied = promptAmountText.trim() == estAmtStr || MarketListParser.convertBengaliDigitsToEnglish(promptAmountText.trim()) == estAmtStr
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isAlreadyApplied) appColors.income.copy(alpha = 0.12f) else appColors.accent.copy(alpha = 0.10f),
                    border = BorderStroke(HairlineInkStroke, if (isAlreadyApplied) appColors.income.copy(alpha = 0.35f) else appColors.accent.copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 48.dp).minimumInteractiveComponentSize()
                        .clickable { haptics.lightTick(); promptAmountText = estAmtStr }
                ) {
                    Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(
                            text = if (isBangla) "💡 গতবারের দর অনুযায়ী মোট $currencySymbol$estAmtBengali বসিয়ে দিন (সম্ভাব্য দাম)" else "💡 Use last rate total $currencySymbol$estAmtBengali (Estimated price)",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold, fontSize = 12.sp, fontFeatureSettings = "tnum"),
                            color = if (isAlreadyApplied) appColors.income else appColors.accent, modifier = Modifier.weight(1f)
                        )
                        Surface(shape = RoundedCornerShape(6.dp), color = if (isAlreadyApplied) appColors.income else appColors.accent) {
                            Text(text = if (isAlreadyApplied) (if (isBangla) "✓ বসানো হয়েছে" else "✓ Applied") else (if (isBangla) "বসিয়ে দিন" else "Use"), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White), modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = promptAmountText, onValueChange = { promptAmountText = it },
                modifier = Modifier.fillMaxWidth().focusRequester(focusRequester), singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { submitAmount() }),
                leadingIcon = { Text(text = currencySymbol, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = appColors.accent)) },
                placeholder = { Text(text = if (isBangla) "০ (বা ৪৫ * ২.৫)" else "0 (or 45 * 2.5)", color = appColors.textSecondary.copy(alpha = 0.5f), fontSize = 13.sp) },
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = appColors.accent, unfocusedBorderColor = appColors.divider, focusedContainerColor = appColors.background, unfocusedContainerColor = appColors.background),
                shape = RoundedCornerShape(12.dp)
            )

            if (isExpression && evaluatedAmount != null) {
                MarketPromptExpressionPreview(evaluatedAmount = evaluatedAmount, currencySymbol = currencySymbol, isBangla = isBangla, appColors = appColors, onApply = {
                    haptics.lightTick()
                    promptAmountText = if (evaluatedAmount % 1.0 == 0.0) evaluatedAmount.toInt().toString() else "%.1f".format(evaluatedAmount)
                })
            }

            Spacer(modifier = Modifier.height(6.dp))
            MarketPromptPriceAnomalyStrip(item = item, evaluatedAmount = evaluatedAmount, percentDiff = percentDiff, diffRate = diffRate, priceInsight = priceInsight, isBangla = isBangla, appColors = appColors)

            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("×" to "*", "+" to "+", "÷" to "/", "−" to "-").forEach { (displayOp, actualOp) ->
                    Box(modifier = Modifier.weight(1f).defaultMinSize(minHeight = 48.dp).minimumInteractiveComponentSize().clip(RoundedCornerShape(10.dp)).background(appColors.accent.copy(alpha = 0.08f)).border(HairlineInkStroke, appColors.accent.copy(alpha = 0.25f), RoundedCornerShape(10.dp)).clickable { haptics.lightTick(); promptAmountText = "$promptAmountText$actualOp" }.padding(vertical = 7.dp), contentAlignment = Alignment.Center) {
                        Text(text = displayOp, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 16.sp), color = appColors.accent)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(50, 100, 200, 500).forEach { chipValue ->
                    Box(modifier = Modifier.weight(1f).defaultMinSize(minHeight = 48.dp).minimumInteractiveComponentSize().clip(RoundedCornerShape(10.dp)).background(appColors.background).border(HairlineInkStroke, appColors.divider, RoundedCornerShape(10.dp)).clickable { addQuickAmount(chipValue) }, contentAlignment = Alignment.Center) {
                        val chipText = if (isBangla) "+${convertToBengaliDigits(chipValue.toString())}" else "+$chipValue"
                        Text(text = chipText, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 12.sp, fontFeatureSettings = "tnum"), color = appColors.accent)
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f).height(48.dp), shape = RoundedCornerShape(12.dp), border = BorderStroke(HairlineInkStroke, appColors.divider)) {
                    Text(text = if (isBangla) "বাতিল" else "Cancel", color = appColors.textSecondary, fontSize = 14.sp)
                }
                Button(onClick = submitAmount, modifier = Modifier.weight(1f).height(48.dp), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = appColors.accent)) {
                    Text(text = if (isBangla) "যুক্ত করুন" else "Save Price", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                TextButton(onClick = { haptics.lightTick(); onConfirm(0.0) }, modifier = Modifier.minimumInteractiveComponentSize()) {
                    Text(text = if (isBangla) "মূল্য ছাড়াই কেনা চিহ্নিত করুন" else "Mark purchased without price", style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp), color = appColors.textSecondary)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
