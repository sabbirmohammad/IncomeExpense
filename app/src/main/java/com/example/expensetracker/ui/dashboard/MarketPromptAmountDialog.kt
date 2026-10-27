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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.expensetracker.common.MarketListParser
import com.example.expensetracker.common.UnitConverterEngine
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.data.model.ProductPriceInsight
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.CalculatorEvaluator
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.rememberAppHaptics

/**
 * Clean & fast price entry popup for an item in the market checklist.
 * - Shows item name and quantity badge.
 * - Auto-focuses keyboard.
 * - Provides quick price increment chips (+50, +100, +200, +500).
 * - Supports free/zero price option or cancel.
 */
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

    var promptAmountText by remember {
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

    LaunchedEffect(Unit) {
        try {
            focusRequester.requestFocus()
        } catch (_: Exception) {}
    }

    val submitAmount = {
        val amt = evaluatedAmount ?: MarketListParser.convertBengaliDigitsToEnglish(promptAmountText).toDoubleOrNull() ?: 0.0
        haptics.mediumClick()
        onConfirm(amt)
    }

    val addQuickAmount = { addAmt: Int ->
        haptics.lightTick()
        val current = evaluatedAmount ?: MarketListParser.convertBengaliDigitsToEnglish(promptAmountText).toDoubleOrNull() ?: 0.0
        val nextAmt = (current + addAmt).toInt()
        promptAmountText = nextAmt.toString()
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = LocalAppColors.current.surface,
            border = BorderStroke(1.dp, LocalAppColors.current.border),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
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
                            fontSize = 17.sp
                        ),
                        color = appColors.textPrimary,
                        modifier = Modifier.weight(1f)
                    )

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = appColors.accent.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = item.formatQuantity(),
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.5.sp
                            ),
                            color = appColors.accent,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isBangla) "পণ্যটির ক্রয়মূল্য লিখুন:" else "Enter actual purchase price:",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = appColors.textSecondary
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Price Input Field
                OutlinedTextField(
                    value = promptAmountText,
                    onValueChange = { promptAmountText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Done),
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
                        unfocusedBorderColor = LocalAppColors.current.border,
                        focusedContainerColor = LocalAppColors.current.background,
                        unfocusedContainerColor = LocalAppColors.current.background
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
                if (evaluatedAmount != null && evaluatedAmount > 0 && item.quantity > 0) {
                    val (currentRate, baseUnit) = UnitConverterEngine.calculateBaseUnitPrice(evaluatedAmount, item.quantity, item.unit)
                    val referenceRate = priceInsight?.currentUnitPrice ?: lastRate?.first
                    val rateStr = if (currentRate % 1.0 == 0.0) currentRate.toInt().toString() else "%.1f".format(currentRate)
                    val rateText = if (isBangla) convertToBengaliDigits(rateStr) else rateStr

                    Spacer(modifier = Modifier.height(4.dp))
                    
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
                                            text = if (isBangla) "🔺 গতবারের চেয়ে ৳$diffText/$baseUnit বেশি (+$pctText%)" else "🔺 ৳$diffText/$baseUnit higher than last time (+$pctText%)",
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
                                        text = if (isBangla) "🔻 গতবারের চেয়ে ৳$diffText/$baseUnit সাশ্রয়ী (-$pctText%)" else "🔻 ৳$diffText/$baseUnit cheaper than last time (-$pctText%)",
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
                                        text = if (isBangla) "➖ পূর্বের দরেই কেনা হচ্ছে" else "➖ Same rate as last purchase",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                        color = appColors.textSecondary,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

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
                                .padding(vertical = 4.dp),
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

                // Quick increment chips: +50, +100, +200, +500
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val chips = listOf(50, 100, 200, 500)
                    chips.forEach { chipValue ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(LocalAppColors.current.background)
                                .clickable { addQuickAmount(chipValue) }
                                .padding(vertical = 6.dp),
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

                Spacer(modifier = Modifier.height(18.dp))

                // Action buttons: Cancel, Free, Confirm
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, LocalAppColors.current.border)
                    ) {
                        Text(
                            text = if (isBangla) "বাতিল" else "Cancel",
                            color = appColors.textSecondary,
                            fontSize = 13.sp
                        )
                    }

                    Button(
                        onClick = submitAmount,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = appColors.accent)
                    ) {
                        Text(
                            text = if (isBangla) "যুক্ত করুন" else "Save Price",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                // Subtle option: Mark without price
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    TextButton(
                        onClick = {
                            haptics.lightTick()
                            onConfirm(0.0)
                        }
                    ) {
                        Text(
                            text = if (isBangla) "মূল্য ছাড়াই কেনা চিহ্নিত করুন" else "Mark purchased without price",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                            color = appColors.textSecondary
                        )
                    }
                }
            }
        }
    }
}
