package com.example.expensetracker.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Backspace
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.HairlineInkStroke
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.RadiusS

/**
 * V7 AmountKeypad:
 * - 4x4 keypad grid with keys >= 56dp height and touch targets >= 48dp.
 * - Digits 1-9, 0, decimal '.', backspace '⌫' (long-press clears all).
 * - Operators: +, −, ×, ÷.
 * - Expression preview line with live CalculatorEvaluator result and tap-to-apply.
 * - Bengali numeral display toggle support.
 * - Haptics on all taps.
 * - Optional Save button slot at bottom.
 */
@Composable
fun AmountKeypad(
    expression: String,
    onExpressionChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    currencySymbol: String = "৳",
    isBangla: Boolean = false,
    showPreviewLine: Boolean = true,
    onSave: (() -> Unit)? = null,
    saveButtonText: String? = null,
    saveButtonEnabled: Boolean = true,
    saveButtonColor: Color = LocalAppColors.current.accent,
    haptics: AppHaptics = rememberAppHaptics()
) {
    val evaluatedValue = remember(expression) { AmountKeypadLogic.evaluate(expression) }
    val hasOperators = remember(expression) { AmountKeypadLogic.hasOperators(expression) }
    val evalStr = evaluatedValue?.let { AmountKeypadLogic.formatEvaluated(it, isBangla) } ?: ""

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (showPreviewLine) {
            AmountKeypadExpressionPreview(
                visible = hasOperators && evaluatedValue != null,
                evalStr = evalStr,
                currencySymbol = currencySymbol,
                isBangla = isBangla,
                onApply = {
                    haptics.lightTick()
                    evaluatedValue?.let {
                        val clean = CalculatorEvaluator.formatResult(it)
                        onExpressionChange(clean)
                    }
                }
            )
        }

        // 4x4 Keypad Grid
        val keypadRows = remember(isBangla) {
            listOf(
                listOf(KeypadItem.Digit('7', if (isBangla) "৭" else "7"), KeypadItem.Digit('8', if (isBangla) "৮" else "8"), KeypadItem.Digit('9', if (isBangla) "৯" else "9"), KeypadItem.Operator('÷', "÷")),
                listOf(KeypadItem.Digit('4', if (isBangla) "৪" else "4"), KeypadItem.Digit('5', if (isBangla) "৫" else "5"), KeypadItem.Digit('6', if (isBangla) "৬" else "6"), KeypadItem.Operator('×', "×")),
                listOf(KeypadItem.Digit('1', if (isBangla) "১" else "1"), KeypadItem.Digit('2', if (isBangla) "২" else "2"), KeypadItem.Digit('3', if (isBangla) "৩" else "3"), KeypadItem.Operator('−', "−")),
                listOf(KeypadItem.Decimal, KeypadItem.Digit('0', if (isBangla) "০" else "0"), KeypadItem.Backspace, KeypadItem.Operator('+', "+"))
            )
        }

        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            keypadRows.forEach { rowItems ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    rowItems.forEach { item ->
                        KeypadButton(
                            item = item,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                haptics.lightTick()
                                when (item) {
                                    is KeypadItem.Digit -> onExpressionChange(AmountKeypadLogic.appendDigit(expression, item.char))
                                    is KeypadItem.Operator -> onExpressionChange(AmountKeypadLogic.appendOperator(expression, item.char))
                                    is KeypadItem.Decimal -> onExpressionChange(AmountKeypadLogic.appendDecimal(expression))
                                    is KeypadItem.Backspace -> onExpressionChange(AmountKeypadLogic.backspace(expression))
                                }
                            },
                            onLongClick = if (item is KeypadItem.Backspace) {
                                { haptics.mediumClick(); onExpressionChange(AmountKeypadLogic.clear()) }
                            } else null
                        )
                    }
                }
            }
        }

        if (onSave != null) {
            Spacer(modifier = Modifier.height(12.dp))
            AppButton(
                text = saveButtonText ?: (if (isBangla) "সংরক্ষণ" else "Save"),
                onClick = onSave,
                enabled = saveButtonEnabled,
                variant = AppButtonVariant.Primary,
                modifier = Modifier.fillMaxWidth().height(56.dp)
            )
        }
    }
}

@Composable
private fun KeypadButton(
    item: KeypadItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null
) {
    val appColors = LocalAppColors.current
    val isOperator = item is KeypadItem.Operator
    val isBackspace = item is KeypadItem.Backspace

    val backgroundColor = when {
        isOperator -> appColors.accentContainer
        else -> appColors.surfaceVariant
    }
    val contentColor = when {
        isOperator -> appColors.accent
        else -> appColors.textPrimary
    }

    val semanticsDesc = when (item) {
        is KeypadItem.Digit -> item.display
        is KeypadItem.Operator -> when (item.char) {
            '+' -> "Plus"; '−' -> "Minus"; '×' -> "Multiply"; '÷' -> "Divide"; else -> item.display
        }
        is KeypadItem.Decimal -> "Point"
        is KeypadItem.Backspace -> "Backspace"
    }

    Box(
        modifier = modifier
            .defaultMinSize(minHeight = 56.dp)
            .minimumInteractiveComponentSize()
            .clip(RoundedCornerShape(RadiusS))
            .background(backgroundColor)
            .pointerInput(Unit) {
                detectTapGestures(onTap = { onClick() }, onLongPress = { onLongClick?.invoke() })
            }
            .semantics { contentDescription = semanticsDesc },
        contentAlignment = Alignment.Center
    ) {
        when (item) {
            is KeypadItem.Digit -> Text(
                text = item.display,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp, fontFeatureSettings = "tnum"),
                color = contentColor
            )
            is KeypadItem.Operator -> Text(
                text = item.display,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 22.sp),
                color = contentColor
            )
            is KeypadItem.Decimal -> Text(
                text = ".",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 22.sp),
                color = contentColor
            )
            is KeypadItem.Backspace -> Icon(
                imageVector = Icons.AutoMirrored.Outlined.Backspace,
                contentDescription = semanticsDesc,
                tint = appColors.textSecondary,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}
