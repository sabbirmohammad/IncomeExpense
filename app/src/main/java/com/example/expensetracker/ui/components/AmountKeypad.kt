package com.example.expensetracker.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Backspace
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.ExpenseTrackerTheme
import com.example.expensetracker.theme.HairlineInkStroke
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.RadiusS

/**
 * Pure evaluation and input transformation engine for AmountKeypad.
 * 100% testable without Compose runtime.
 */
object AmountKeypadLogic {

    private val OPERATORS = listOf('+', '−', '-', '×', '*', '÷', '/')

    fun isOperator(c: Char): Boolean = c in OPERATORS

    /**
     * Appends a digit ('0'..'9') to the expression.
     * Replaces lone "0" with the digit (unless digit is '0').
     */
    fun appendDigit(current: String, digit: Char): String {
        if (!digit.isDigit()) return current
        if (current == "0") return digit.toString()
        // If current ends with an operator followed by "0", replace that "0"
        val lastOpIndex = current.indexOfLast { isOperator(it) }
        if (lastOpIndex >= 0 && lastOpIndex == current.length - 2 && current.endsWith("0")) {
            return current.dropLast(1) + digit
        }
        return current + digit
    }

    /**
     * Appends a decimal point ('.') safely.
     * Prevents multiple decimals in the current number segment.
     */
    fun appendDecimal(current: String): String {
        if (current.isEmpty()) return "0."
        val lastOpIndex = current.indexOfLast { isOperator(it) }
        val currentSegment = if (lastOpIndex >= 0) current.substring(lastOpIndex + 1) else current
        return when {
            currentSegment.contains('.') -> current
            currentSegment.isEmpty() -> current + "0."
            else -> current + "."
        }
    }

    /**
     * Appends an operator ('+', '−', '×', '÷').
     * If the expression is empty, only '−' is allowed (for negative numbers).
     * If the expression already ends with an operator, it replaces it.
     */
    fun appendOperator(current: String, op: Char): String {
        if (!isOperator(op)) return current
        val normalizedOp = when (op) {
            '-' -> '−'
            '*' -> '×'
            '/' -> '÷'
            else -> op
        }
        if (current.isEmpty()) {
            return if (normalizedOp == '−') "−" else ""
        }
        // If it ends with decimal, drop it before appending operator
        val trimmed = if (current.endsWith('.')) current.dropLast(1) else current
        if (trimmed.isEmpty()) return ""

        return if (isOperator(trimmed.last())) {
            // Replace previous operator
            trimmed.dropLast(1) + normalizedOp
        } else {
            trimmed + normalizedOp
        }
    }

    /**
     * Deletes the last character. If length is 1 or empty, returns "".
     */
    fun backspace(current: String): String {
        return if (current.isNotEmpty()) current.dropLast(1) else ""
    }

    /**
     * Clears the entire expression.
     */
    fun clear(): String = ""

    /**
     * Evaluates the expression using CalculatorEvaluator.
     */
    fun evaluate(expression: String): Double? {
        if (expression.isBlank()) return null
        return CalculatorEvaluator.evaluate(expression)
    }

    /**
     * Formats evaluated double value cleanly.
     */
    fun formatEvaluated(value: Double, isBangla: Boolean = false): String {
        val formatted = CalculatorEvaluator.formatResult(value)
        return if (isBangla) convertToBengaliDigits(formatted) else formatted
    }

    /**
     * Checks if the expression contains any math operators.
     */
    fun hasOperators(expression: String): Boolean {
        return expression.any { isOperator(it) }
    }
}

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
    val appColors = LocalAppColors.current
    val evaluatedValue = remember(expression) { AmountKeypadLogic.evaluate(expression) }
    val hasOperators = remember(expression) { AmountKeypadLogic.hasOperators(expression) }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Expression preview line (appears when operators are used)
        if (showPreviewLine) {
            AnimatedVisibility(
                visible = hasOperators && evaluatedValue != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                val evalStr = evaluatedValue?.let { AmountKeypadLogic.formatEvaluated(it, isBangla) } ?: ""
                Surface(
                    shape = RoundedCornerShape(RadiusS),
                    color = appColors.accentContainer,
                    border = BorderStroke(HairlineInkStroke, appColors.accent.copy(alpha = 0.35f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .defaultMinSize(minHeight = 44.dp)
                        .minimumInteractiveComponentSize()
                        .clickable {
                            haptics.lightTick()
                            evaluatedValue?.let {
                                val clean = CalculatorEvaluator.formatResult(it)
                                onExpressionChange(clean)
                            }
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "= $currencySymbol $evalStr",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                fontFeatureSettings = "tnum"
                            ),
                            color = appColors.accent
                        )
                        Text(
                            text = if (isBangla) "ট্যাপ করে বসান" else "Tap to apply",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp),
                            color = appColors.textSecondary
                        )
                    }
                }
            }
        }

        // 4x4 Keypad Grid
        // Row 1: 1, 2, 3, ÷
        // Row 2: 4, 5, 6, ×
        // Row 3: 7, 8, 9, −
        // Row 4: ., 0, ⌫, +
        val keypadRows = remember(isBangla) {
            listOf(
                listOf(
                    KeypadItem.Digit('1', if (isBangla) "১" else "1"),
                    KeypadItem.Digit('2', if (isBangla) "২" else "2"),
                    KeypadItem.Digit('3', if (isBangla) "৩" else "3"),
                    KeypadItem.Operator('÷', "÷")
                ),
                listOf(
                    KeypadItem.Digit('4', if (isBangla) "৪" else "4"),
                    KeypadItem.Digit('5', if (isBangla) "৫" else "5"),
                    KeypadItem.Digit('6', if (isBangla) "৬" else "6"),
                    KeypadItem.Operator('×', "×")
                ),
                listOf(
                    KeypadItem.Digit('7', if (isBangla) "৭" else "7"),
                    KeypadItem.Digit('8', if (isBangla) "৮" else "8"),
                    KeypadItem.Digit('9', if (isBangla) "৯" else "9"),
                    KeypadItem.Operator('−', "−")
                ),
                listOf(
                    KeypadItem.Decimal,
                    KeypadItem.Digit('0', if (isBangla) "০" else "0"),
                    KeypadItem.Backspace,
                    KeypadItem.Operator('+', "+")
                )
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            keypadRows.forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowItems.forEach { item ->
                        KeypadButton(
                            item = item,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                haptics.lightTick()
                                when (item) {
                                    is KeypadItem.Digit -> {
                                        onExpressionChange(AmountKeypadLogic.appendDigit(expression, item.char))
                                    }
                                    is KeypadItem.Operator -> {
                                        onExpressionChange(AmountKeypadLogic.appendOperator(expression, item.char))
                                    }
                                    is KeypadItem.Decimal -> {
                                        onExpressionChange(AmountKeypadLogic.appendDecimal(expression))
                                    }
                                    is KeypadItem.Backspace -> {
                                        onExpressionChange(AmountKeypadLogic.backspace(expression))
                                    }
                                }
                            },
                            onLongClick = if (item is KeypadItem.Backspace) {
                                {
                                    haptics.mediumClick()
                                    onExpressionChange(AmountKeypadLogic.clear())
                                }
                            } else null
                        )
                    }
                }
            }
        }

        // Optional Save / Action Button slot
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

private sealed interface KeypadItem {
    data class Digit(val char: Char, val display: String) : KeypadItem
    data class Operator(val char: Char, val display: String) : KeypadItem
    data object Decimal : KeypadItem
    data object Backspace : KeypadItem
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
        isBackspace -> appColors.surfaceVariant
        else -> appColors.surfaceVariant
    }

    val contentColor = when {
        isOperator -> appColors.accent
        else -> appColors.textPrimary
    }

    val borderStroke = if (appColors.isDark) {
        null
    } else {
        BorderStroke(HairlineInkStroke, appColors.border)
    }

    val semanticsDesc = when (item) {
        is KeypadItem.Digit -> item.display
        is KeypadItem.Operator -> when (item.char) {
            '+' -> "Plus"
            '−' -> "Minus"
            '×' -> "Multiply"
            '÷' -> "Divide"
            else -> item.display
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
            .then(
                if (borderStroke != null) Modifier.background(backgroundColor, RoundedCornerShape(RadiusS))
                else Modifier
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onClick() },
                    onLongPress = { onLongClick?.invoke() }
                )
            }
            .semantics { contentDescription = semanticsDesc },
        contentAlignment = Alignment.Center
    ) {
        when (item) {
            is KeypadItem.Digit -> {
                Text(
                    text = item.display,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        fontFeatureSettings = "tnum"
                    ),
                    color = contentColor
                )
            }
            is KeypadItem.Operator -> {
                Text(
                    text = item.display,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    ),
                    color = contentColor
                )
            }
            is KeypadItem.Decimal -> {
                Text(
                    text = ".",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    ),
                    color = contentColor
                )
            }
            is KeypadItem.Backspace -> {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.Backspace,
                    contentDescription = semanticsDesc,
                    tint = appColors.textSecondary,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Preview(name = "AmountKeypad - Light En", showBackground = true)
@Composable
private fun AmountKeypadLightPreview() {
    ExpenseTrackerTheme(themeMode = "light") {
        Surface(color = LocalAppColors.current.surface, modifier = Modifier.padding(16.dp)) {
            AmountKeypad(
                expression = "120+85",
                onExpressionChange = {},
                isBangla = false
            )
        }
    }
}

@Preview(name = "AmountKeypad - Dark Bn", showBackground = true)
@Composable
private fun AmountKeypadDarkPreview() {
    ExpenseTrackerTheme(themeMode = "dark") {
        Surface(color = LocalAppColors.current.surface, modifier = Modifier.padding(16.dp)) {
            AmountKeypad(
                expression = "450",
                onExpressionChange = {},
                isBangla = true
            )
        }
    }
}
