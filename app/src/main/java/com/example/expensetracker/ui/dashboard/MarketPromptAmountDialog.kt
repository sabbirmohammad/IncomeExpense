package com.example.expensetracker.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.CalculatorEvaluator
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.rememberAppHaptics

/**
 * Clean & fast price entry bottom sheet for an item in the market checklist.
 * - Migrated to modern ModalBottomSheet with numeric keypad.
 * - Shows item name and quantity badge.
 * - Auto-focuses keyboard with FocusRequester.
 * - Primary 1-Tap button to confirm previous unit price.
 * - Provides quick price increment chips (+50, +100, +200, +500).
 * - Live math calculator evaluation and price anomaly insights.
 * - Supports free/zero price option or cancel.
 */
private data class MarketUnitPreset(
    val quantity: Double,
    val unit: String,
    val labelBn: String,
    val labelEn: String,
    val subtitleBn: String? = null,
    val subtitleEn: String? = null
)

/**
 * Backwards-compatible overload for single-parameter onConfirm callers.
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
    priceInsight: ProductPriceInsight? = null,
    onUpdateQuantity: ((quantity: Double, unit: String) -> Unit)? = null
) {
    MarketPromptAmountDialog(
        item = item,
        currencySymbol = currencySymbol,
        isBangla = isBangla,
        onDismiss = onDismiss,
        onConfirm = { amt, _, _ ->
            if (amt == 0.0) onConfirm(0.0) else onConfirm(amt)
        },
        lastRate = lastRate,
        priceInsight = priceInsight,
        onUpdateQuantity = onUpdateQuantity
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketPromptAmountDialog(
    item: MarketItem,
    currencySymbol: String,
    isBangla: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (amount: Double, quantity: Double, unit: String) -> Unit,
    lastRate: Pair<Double, String>? = null,
    priceInsight: ProductPriceInsight? = null,
    onUpdateQuantity: ((quantity: Double, unit: String) -> Unit)? = null
) {
    val appColors = LocalAppColors.current
    val haptics = rememberAppHaptics()
    val focusRequester = remember { FocusRequester() }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var currentQuantity by remember(item.id, item.quantity) { mutableDoubleStateOf(item.quantity) }
    var currentUnit by remember(item.id, item.unit) { mutableStateOf(item.unit) }

    var promptAmountText by remember(item.id, item.amount) {
        mutableStateOf(
            item.amount?.let { amt ->
                if (amt > 0) {
                    if (amt % 1.0 == 0.0) amt.toInt().toString() else amt.toString()
                } else ""
            } ?: ""
        )
    }

    val evaluatedAmount = remember(promptAmountText) {
        CalculatorEvaluator.evaluate(promptAmountText)
            ?: MarketListParser.convertBengaliDigitsToEnglish(promptAmountText).toDoubleOrNull()
    }
    val isExpression = promptAmountText.any { it in "+-*xX/×÷" }

    LaunchedEffect(item.id) {
        try {
            focusRequester.requestFocus()
        } catch (_: Exception) {}
    }

    val submitAmount = {
        val amt = evaluatedAmount ?: MarketListParser.convertBengaliDigitsToEnglish(promptAmountText).toDoubleOrNull() ?: 0.0
        if (currentQuantity != item.quantity || currentUnit != item.unit) {
            onUpdateQuantity?.invoke(currentQuantity, currentUnit)
        }
        haptics.mediumClick()
        onConfirm(amt, currentQuantity, currentUnit)
    }

    val addQuickAmount = { addAmt: Int ->
        haptics.lightTick()
        val current = evaluatedAmount ?: MarketListParser.convertBengaliDigitsToEnglish(promptAmountText).toDoubleOrNull() ?: 0.0
        val nextAmt = (current + addAmt).toInt()
        promptAmountText = nextAmt.toString()
    }

    // Suggested Estimated Cost Quick Chip
    val estimatedCostSuggestion = remember(currentQuantity, currentUnit, priceInsight, lastRate) {
        val unitPrice = priceInsight?.currentUnitPrice ?: lastRate?.first
        if (unitPrice != null && unitPrice > 0.0 && currentQuantity > 0.0) {
            UnitConverterEngine.estimateCost(unitPrice, currentQuantity, currentUnit)
        } else null
    }

    val normalizedUnit = currentUnit.lowercase().trim()
    val isDiscrete = normalizedUnit in listOf("টি", "পিস", "হালি", "ডজন", "জোড়া", "জোড়া", "প্যাকেট", "আঁটি", "আটি", "বোতল", "pcs", "piece", "packet")
    val isVolume = normalizedUnit in listOf("লিটার", "লি", "মিলি", "l", "liter", "ml")

    val unitPresets = remember(currentUnit) {
        when {
            isDiscrete -> listOf(
                MarketUnitPreset(4.0, "টি", "৪টি", "4 pcs", "১ হালি", "1 Hali"),
                MarketUnitPreset(6.0, "টি", "৬টি", "6 pcs", "হাফ ডজন", "Half Dozen"),
                MarketUnitPreset(12.0, "টি", "১২টি", "12 pcs", "১ ডজন", "1 Dozen"),
                MarketUnitPreset(2.0, "টি", "২টি", "2 pcs", "১ জোড়া", "1 Pair")
            )
            isVolume -> listOf(
                MarketUnitPreset(0.5, "লিটার", "০.৫ লি", "0.5 L"),
                MarketUnitPreset(1.0, "লিটার", "১ লি", "1 L"),
                MarketUnitPreset(2.0, "লিটার", "২ লি", "2 L"),
                MarketUnitPreset(5.0, "লিটার", "৫ লি", "5 L")
            )
            else -> listOf(
                MarketUnitPreset(0.5, "কেজি", "০.৫ কেজি", "0.5 kg", "৫০০ গ্রাম", "500g"),
                MarketUnitPreset(1.0, "কেজি", "১ কেজি", "1 kg"),
                MarketUnitPreset(1.5, "কেজি", "১.৫ কেজি", "1.5 kg"),
                MarketUnitPreset(2.0, "কেজি", "২ কেজি", "2 kg"),
                MarketUnitPreset(0.25, "কেজি", "১ পোয়া", "1 Powa", "২৫০ গ্রাম", "250g"),
                MarketUnitPreset(5.0, "কেজি", "১ পাল্লা", "1 Palla", "৫ কেজি", "5 kg")
            )
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = appColors.surface,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(38.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(appColors.border.copy(alpha = 0.8f))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp)
                .navigationBarsPadding()
        ) {
            // Header: Item name + quantity pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    ),
                    color = appColors.textPrimary,
                    modifier = Modifier.weight(1f)
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = appColors.accent.copy(alpha = 0.12f)
                ) {
                    val currentDisplayItem = item.copy(quantity = currentQuantity, unit = currentUnit)
                    Text(
                        text = currentDisplayItem.formatQuantity(),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        ),
                        color = appColors.accent,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            // Smart Preset Unit Multipliers
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(unitPresets) { preset ->
                    val isSelected = currentQuantity == preset.quantity && (currentUnit == preset.unit || (preset.unit == "কেজি" && currentUnit in listOf("কেজি", "kg", "গ্রাম", "পোয়া", "পোয়া", "পাল্লা")))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) appColors.accent.copy(alpha = 0.16f) else appColors.surfaceVariant.copy(alpha = 0.45f),
                        border = BorderStroke(1.dp, if (isSelected) appColors.accent else appColors.border),
                        modifier = Modifier
                            .heightIn(min = 34.dp)
                            .clickable {
                                haptics.lightTick()
                                currentQuantity = preset.quantity
                                currentUnit = preset.unit
                                onUpdateQuantity?.invoke(preset.quantity, preset.unit)
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = if (isBangla) preset.labelBn else preset.labelEn,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 11.5.sp
                                ),
                                color = if (isSelected) appColors.accent else appColors.textPrimary
                            )
                            val sub = if (isBangla) preset.subtitleBn else preset.subtitleEn
                            if (sub != null) {
                                Text(
                                    text = "($sub)",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = if (isSelected) appColors.accent.copy(alpha = 0.85f) else appColors.textSecondary
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isBangla) "পণ্যটির ক্রয়মূল্য লিখুন:" else "Enter actual purchase price:",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.5.sp),
                color = appColors.textSecondary
            )

            // Primary 1-Tap Auto-Suggest Rate Banner to Confirm / Apply Previous Unit Price
            if (estimatedCostSuggestion != null && estimatedCostSuggestion > 0.0) {
                val estAmtStr = if (estimatedCostSuggestion % 1.0 == 0.0) {
                    estimatedCostSuggestion.toLong().toString()
                } else {
                    "%.1f".format(estimatedCostSuggestion)
                }
                val estAmtBengali = if (isBangla) convertToBengaliDigits(estAmtStr) else estAmtStr
                val isAlreadyApplied = promptAmountText.trim() == estAmtStr ||
                    MarketListParser.convertBengaliDigitsToEnglish(promptAmountText.trim()) == estAmtStr

                val refRate = priceInsight?.currentUnitPrice ?: lastRate?.first
                val refUnit = priceInsight?.baseUnit ?: lastRate?.second ?: currentUnit
                val refRateStr = if (refRate != null) {
                    if (refRate % 1.0 == 0.0) refRate.toLong().toString() else "%.1f".format(refRate)
                } else ""
                val refRateBengali = if (isBangla) convertToBengaliDigits(refRateStr) else refRateStr

                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isAlreadyApplied) appColors.income.copy(alpha = 0.12f) else appColors.accent.copy(alpha = 0.10f),
                    border = BorderStroke(
                        1.dp,
                        if (isAlreadyApplied) appColors.income.copy(alpha = 0.35f) else appColors.accent.copy(alpha = 0.35f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            haptics.lightTick()
                            promptAmountText = estAmtStr
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val bannerText = if (isBangla) {
                            if (refRate != null && refRate > 0) {
                                "💡 গতবারের দর ৳$refRateBengali/$refUnit অনুযায়ী মোট ৳$estAmtBengali বসিয়ে দিন (সম্ভাব্য দাম)"
                            } else {
                                "💡 গতবারের দর অনুযায়ী মোট $currencySymbol$estAmtBengali বসিয়ে দিন (সম্ভাব্য দাম)"
                            }
                        } else {
                            if (refRate != null && refRate > 0) {
                                "💡 Use last rate $currencySymbol$refRateStr/$refUnit total $currencySymbol$estAmtStr (Estimated price)"
                            } else {
                                "💡 Use last rate total $currencySymbol$estAmtStr (Estimated price)"
                            }
                        }
                        Text(
                            text = bannerText,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp
                            ),
                            color = if (isAlreadyApplied) appColors.income else appColors.accent,
                            modifier = Modifier.weight(1f)
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isAlreadyApplied) appColors.income else appColors.accent
                        ) {
                            Text(
                                text = if (isAlreadyApplied) {
                                    if (isBangla) "✓ বসানো হয়েছে" else "✓ Applied"
                                } else {
                                    if (isBangla) "বসিয়ে দিন" else "Use"
                                },
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = Color.White
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Price Input Field with KeyboardType.Decimal
            OutlinedTextField(
                value = promptAmountText,
                onValueChange = { promptAmountText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { submitAmount() }),
                leadingIcon = {
                    Text(
                        text = currencySymbol,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = appColors.accent
                        )
                    )
                },
                placeholder = {
                    Text(
                        text = if (isBangla) "০ (বা ৪৫ * ২.৫)" else "0 (or 45 * 2.5)",
                        color = appColors.textSecondary.copy(alpha = 0.5f)
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = appColors.accent,
                    unfocusedBorderColor = appColors.border,
                    focusedContainerColor = appColors.background,
                    unfocusedContainerColor = appColors.background
                ),
                shape = RoundedCornerShape(12.dp)
            )

            // Live Evaluated Result Preview (for math expressions like 45 * 2.5 or 120 + 80)
            if (isExpression && evaluatedAmount != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = appColors.accent.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, appColors.accent.copy(alpha = 0.3f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            haptics.lightTick()
                            promptAmountText = if (evaluatedAmount % 1.0 == 0.0) evaluatedAmount.toInt().toString() else "%.1f".format(evaluatedAmount)
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val evalStr = if (evaluatedAmount % 1.0 == 0.0) evaluatedAmount.toInt().toString() else "%.1f".format(evaluatedAmount)
                        Text(
                            text = "= $currencySymbol ${if (isBangla) convertToBengaliDigits(evalStr) else evalStr}",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.5.sp
                            ),
                            color = appColors.accent
                        )
                        Text(
                            text = if (isBangla) "ট্যাপ করে বসান" else "Tap to apply",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                            color = appColors.textSecondary
                        )
                    }
                }
            }

            // Real-time Unit Price & Anomaly / Price Variance Intelligence
            if (evaluatedAmount != null && evaluatedAmount > 0 && currentQuantity > 0) {
                val (currentRate, baseUnit) = UnitConverterEngine.calculateBaseUnitPrice(evaluatedAmount, currentQuantity, currentUnit)
                val referenceRate = priceInsight?.currentUnitPrice ?: lastRate?.first
                val rateStr = if (currentRate % 1.0 == 0.0) currentRate.toInt().toString() else "%.1f".format(currentRate)
                val rateText = if (isBangla) convertToBengaliDigits(rateStr) else rateStr

                Spacer(modifier = Modifier.height(6.dp))

                // 1. Calculated Unit Price Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = appColors.accent.copy(alpha = 0.08f),
                    border = BorderStroke(1.dp, appColors.accent.copy(alpha = 0.2f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isBangla) "⚡ হিসাবকৃত প্রতি $baseUnit:" else "⚡ Rate per $baseUnit:",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                            color = appColors.textSecondary
                        )
                        Text(
                            text = "৳$rateText/$baseUnit",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 12.sp),
                            color = appColors.accent
                        )
                    }
                }

                // 2. Comparison against previous purchase
                if (referenceRate != null && referenceRate > 0) {
                    val percentDiff = ((currentRate - referenceRate) / referenceRate) * 100.0
                    val diffRate = currentRate - referenceRate
                    val diffAbs = kotlin.math.abs(diffRate)
                    val diffAbsStr = if (diffAbs % 1.0 == 0.0) diffAbs.toInt().toString() else "%.1f".format(diffAbs)
                    val diffText = if (isBangla) convertToBengaliDigits(diffAbsStr) else diffAbsStr
                    val pctAbsStr = "%.1f".format(kotlin.math.abs(percentDiff))
                    val pctText = if (isBangla) convertToBengaliDigits(pctAbsStr) else pctAbsStr

                    Spacer(modifier = Modifier.height(4.dp))
                    when {
                        percentDiff >= 1.0 -> {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = appColors.expense.copy(alpha = 0.12f),
                                border = BorderStroke(1.dp, appColors.expense.copy(alpha = 0.3f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)) {
                                    Text(
                                        text = if (isBangla) "🔺 ৳$diffText/$baseUnit বেশি (+$pctText%) — বেশি দর" else "🔺 ৳$diffText/$baseUnit higher (+$pctText%) — Expensive",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp),
                                        color = appColors.expense
                                    )
                                    priceInsight?.oneMonthPercentDiff?.let { mPct ->
                                        val mPctAbs = "%.1f".format(kotlin.math.abs(mPct))
                                        val mPctText = if (isBangla) convertToBengaliDigits(mPctAbs) else mPctAbs
                                        val mSign = if (mPct > 0) "+" else "-"
                                        Text(
                                            text = if (isBangla) "📅 গত ১ মাসে পরিবর্তন: $mSign$mPctText%" else "📅 30-day change: $mSign$mPctText%",
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                            color = appColors.textSecondary
                                        )
                                    }
                                }
                            }
                        }
                        percentDiff <= -1.0 -> {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = appColors.income.copy(alpha = 0.12f),
                                border = BorderStroke(1.dp, appColors.income.copy(alpha = 0.3f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = if (isBangla) "🔻 ৳$diffText/$baseUnit কম (-$pctText%) — সাশ্রয়ী" else "🔻 ৳$diffText/$baseUnit cheaper (-$pctText%) — Cheaper/Savings",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp),
                                    color = appColors.income,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                )
                            }
                        }
                        else -> {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = appColors.surface,
                                border = BorderStroke(1.dp, appColors.border),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = if (isBangla) "➖ একই দর" else "➖ Same rate",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                    color = appColors.textSecondary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Math Operators: [ × ] [ + ] [ ÷ ] [ − ]
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("×" to "*", "+" to "+", "÷" to "/", "−" to "-").forEach { (displayOp, actualOp) ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(appColors.accent.copy(alpha = 0.08f))
                            .border(1.dp, appColors.accent.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
                            .clickable {
                                haptics.lightTick()
                                promptAmountText = "$promptAmountText$actualOp"
                            }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = displayOp,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            ),
                            color = appColors.accent
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Quick increment chips: +10, +50, +100, +200, +500
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val chips = listOf(50, 100, 200, 500)
                val allChips = listOf(10) + chips
                allChips.forEach { chipValue ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(appColors.background)
                            .border(1.dp, appColors.border, RoundedCornerShape(8.dp))
                            .clickable { addQuickAmount(chipValue) }
                            .padding(vertical = 7.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        val chipText = if (isBangla) "+${convertToBengaliDigits(chipValue.toString())}" else "+$chipValue"
                        Text(
                            text = chipText,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.5.sp
                            ),
                            color = appColors.accent
                        )
                    }
                }
            }

            // Thumb-Zone On-Screen Numeric Keypad
            Spacer(modifier = Modifier.height(10.dp))
            val keypadRows = listOf(
                listOf("1", "2", "3"),
                listOf("4", "5", "6"),
                listOf("7", "8", "9"),
                listOf(".", "0", "⌫")
            )
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                keypadRows.forEach { rowKeys ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        rowKeys.forEach { key ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = appColors.surfaceVariant.copy(alpha = 0.55f),
                                border = BorderStroke(0.8.dp, appColors.border.copy(alpha = 0.5f)),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp)
                                    .minimumInteractiveComponentSize()
                                    .clickable {
                                        haptics.lightTick()
                                        when (key) {
                                            "⌫" -> {
                                                if (promptAmountText.isNotEmpty()) {
                                                    promptAmountText = promptAmountText.dropLast(1)
                                                }
                                            }
                                            "." -> {
                                                val currentToken = promptAmountText.takeLastWhile { it !in "+-*xX/×÷" }
                                                if (!currentToken.contains(".")) {
                                                    promptAmountText = if (currentToken.isEmpty()) "${promptAmountText}0." else "$promptAmountText."
                                                }
                                            }
                                            else -> {
                                                promptAmountText = if (promptAmountText == "0") key else "$promptAmountText$key"
                                            }
                                        }
                                    }
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    val keyText = if (isBangla && key != "⌫" && key != ".") convertToBengaliDigits(key) else key
                                    Text(
                                        text = keyText,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp
                                        ),
                                        color = appColors.textPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action buttons: Cancel, Confirm Purchase (Fast 1-Tap)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, appColors.border)
                ) {
                    Text(
                        text = if (isBangla) "বাতিল" else "Cancel",
                        color = appColors.textSecondary,
                        fontSize = 13.5.sp
                    )
                }

                Button(
                    onClick = submitAmount,
                    modifier = Modifier
                        .weight(1.3f)
                        .heightIn(min = 48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = appColors.income)
                ) {
                    Text(
                        text = if (isBangla) "কেনা নিশ্চিত করুন" else "Confirm Purchase",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp
                    )
                }
            }

            // Secondary option: Mark without price
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                TextButton(
                    onClick = {
                        haptics.lightTick()
                        if (currentQuantity != item.quantity || currentUnit != item.unit) {
                            onUpdateQuantity?.invoke(currentQuantity, currentUnit)
                        }
                        onConfirm(0.0, currentQuantity, currentUnit)
                    },
                    modifier = Modifier.minimumInteractiveComponentSize()
                ) {
                    Text(
                        text = if (isBangla) "মূল্য ছাড়া কেনা" else "Mark without price",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = appColors.textSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
