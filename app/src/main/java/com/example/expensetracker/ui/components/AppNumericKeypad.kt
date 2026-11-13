package com.example.expensetracker.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.example.expensetracker.theme.RadiusActionChip
import com.example.expensetracker.theme.StyleNumberKeypad

/**
 * AppNumericKeypad:
 * Living Ledger 2.0 Unified In-App Numeric Keypad:
 * - 4x4 standard grid:
 *     Row 1: 7  8  9  ÷
 *     Row 2: 4  5  6  ×
 *     Row 3: 1  2  3  −
 *     Row 4: .  0  ⌫  +
 * - Each key >= 56dp height, full-width/4
 * - Shape: RadiusActionChip (10dp)
 * - Background: surfaceVariant
 * - Operators tinted with info/infoContainer
 * - Pressed animation: scale 0.96 + haptic feedback
 * - Bengali numeral display support (ASCII maintained internally)
 * - Long-press backspace clears all
 * - Optional onDone big key (56dp tall button)
 */
@Composable
fun AppNumericKeypad(
    expression: String,
    onExpressionChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isBangla: Boolean = false,
    onDone: (() -> Unit)? = null,
    doneText: String? = null,
    doneEnabled: Boolean = true,
    doneButtonColor: Color = LocalAppColors.current.accent,
    haptics: AppHaptics = rememberAppHaptics()
) {
    val appColors = LocalAppColors.current

    // Standard 4x4 Grid definition
    val rows = remember(isBangla) {
        listOf(
            listOf(
                KeypadKey.Digit('7', if (isBangla) "৭" else "7"),
                KeypadKey.Digit('8', if (isBangla) "৮" else "8"),
                KeypadKey.Digit('9', if (isBangla) "৯" else "9"),
                KeypadKey.Operator('÷', "÷")
            ),
            listOf(
                KeypadKey.Digit('4', if (isBangla) "৪" else "4"),
                KeypadKey.Digit('5', if (isBangla) "৫" else "5"),
                KeypadKey.Digit('6', if (isBangla) "৬" else "6"),
                KeypadKey.Operator('×', "×")
            ),
            listOf(
                KeypadKey.Digit('1', if (isBangla) "১" else "1"),
                KeypadKey.Digit('2', if (isBangla) "২" else "2"),
                KeypadKey.Digit('3', if (isBangla) "৩" else "3"),
                KeypadKey.Operator('−', "−")
            ),
            listOf(
                KeypadKey.Decimal,
                KeypadKey.Digit('0', if (isBangla) "০" else "0"),
                KeypadKey.Backspace,
                KeypadKey.Operator('+', "+")
            )
        )
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        rows.forEach { rowKeys ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowKeys.forEach { key ->
                    NumericKeyButton(
                        key = key,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            haptics.lightTick()
                            when (key) {
                                is KeypadKey.Digit -> {
                                    onExpressionChange(AmountKeypadLogic.appendDigit(expression, key.char))
                                }
                                is KeypadKey.Operator -> {
                                    onExpressionChange(AmountKeypadLogic.appendOperator(expression, key.char))
                                }
                                is KeypadKey.Decimal -> {
                                    onExpressionChange(AmountKeypadLogic.appendDecimal(expression))
                                }
                                is KeypadKey.Backspace -> {
                                    onExpressionChange(AmountKeypadLogic.backspace(expression))
                                }
                            }
                        },
                        onLongClick = if (key is KeypadKey.Backspace) {
                            {
                                haptics.mediumClick()
                                onExpressionChange(AmountKeypadLogic.clear())
                            }
                        } else null
                    )
                }
            }
        }

        // Optional big Done key below keypad
        if (onDone != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            ) {
                AppButton(
                    text = doneText ?: (if (isBangla) "সম্পন্ন করুন" else "Done"),
                    onClick = onDone,
                    enabled = doneEnabled,
                    variant = AppButtonVariant.Primary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                )
            }
        }
    }
}

sealed interface KeypadKey {
    data class Digit(val char: Char, val display: String) : KeypadKey
    data class Operator(val char: Char, val display: String) : KeypadKey
    data object Decimal : KeypadKey
    data object Backspace : KeypadKey
}

@Composable
private fun NumericKeyButton(
    key: KeypadKey,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null
) {
    val appColors = LocalAppColors.current
    val isOperator = key is KeypadKey.Operator
    val isBackspace = key is KeypadKey.Backspace

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scaleAnim by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1.0f,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessMediumLow),
        label = "keyScale"
    )

    val backgroundColor = when {
        isOperator -> appColors.infoContainer
        else -> appColors.surfaceVariant
    }

    val contentColor = when {
        isOperator -> appColors.info
        isBackspace -> appColors.textSecondary
        else -> appColors.textPrimary
    }

    val border = if (appColors.isDark) {
        null
    } else {
        BorderStroke(HairlineInkStroke, appColors.border)
    }

    val semanticsLabel = when (key) {
        is KeypadKey.Digit -> key.display
        is KeypadKey.Operator -> when (key.char) {
            '+' -> "Plus"
            '−' -> "Minus"
            '×' -> "Multiply"
            '÷' -> "Divide"
            else -> key.display
        }
        is KeypadKey.Decimal -> "Point"
        is KeypadKey.Backspace -> "Backspace"
    }

    Box(
        modifier = modifier
            .defaultMinSize(minHeight = 56.dp)
            .minimumInteractiveComponentSize()
            .scale(scaleAnim)
            .clip(RoundedCornerShape(RadiusActionChip))
            .background(backgroundColor)
            .then(
                if (border != null) Modifier.background(backgroundColor, RoundedCornerShape(RadiusActionChip))
                else Modifier
            )
            .pointerInput(key) {
                detectTapGestures(
                    onTap = { onClick() },
                    onLongPress = { onLongClick?.invoke() }
                )
            }
            .semantics { contentDescription = semanticsLabel },
        contentAlignment = Alignment.Center
    ) {
        when (key) {
            is KeypadKey.Digit -> {
                Text(
                    text = key.display,
                    style = StyleNumberKeypad.copy(fontWeight = FontWeight.SemiBold),
                    color = contentColor
                )
            }
            is KeypadKey.Operator -> {
                Text(
                    text = key.display,
                    style = StyleNumberKeypad.copy(fontWeight = FontWeight.Bold, fontSize = 24.sp),
                    color = contentColor
                )
            }
            is KeypadKey.Decimal -> {
                Text(
                    text = ".",
                    style = StyleNumberKeypad.copy(fontWeight = FontWeight.Bold, fontSize = 24.sp),
                    color = contentColor
                )
            }
            is KeypadKey.Backspace -> {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.Backspace,
                    contentDescription = semanticsLabel,
                    tint = contentColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

// =========================================================================
// PREVIEWS IN ALL 4 THEMES (EN + BN)
// =========================================================================

@Preview(name = "AppNumericKeypad - Dark En", showBackground = true)
@Composable
private fun AppNumericKeypadDarkPreview() {
    ExpenseTrackerTheme(themeMode = "dark") {
        Surface(color = LocalAppColors.current.surface, modifier = Modifier.padding(16.dp)) {
            AppNumericKeypad(
                expression = "1200+350",
                onExpressionChange = {},
                isBangla = false
            )
        }
    }
}

@Preview(name = "AppNumericKeypad - Light Bn", showBackground = true)
@Composable
private fun AppNumericKeypadLightPreview() {
    ExpenseTrackerTheme(themeMode = "light") {
        Surface(color = LocalAppColors.current.surface, modifier = Modifier.padding(16.dp)) {
            AppNumericKeypad(
                expression = "5000",
                onExpressionChange = {},
                isBangla = true
            )
        }
    }
}

@Preview(name = "AppNumericKeypad - AMOLED Bn", showBackground = true)
@Composable
private fun AppNumericKeypadAmoledPreview() {
    ExpenseTrackerTheme(themeMode = "amoled") {
        Surface(color = LocalAppColors.current.surface, modifier = Modifier.padding(16.dp)) {
            AppNumericKeypad(
                expression = "750",
                onExpressionChange = {},
                isBangla = true
            )
        }
    }
}

@Preview(name = "AppNumericKeypad - System En", showBackground = true)
@Composable
private fun AppNumericKeypadSystemPreview() {
    ExpenseTrackerTheme(themeMode = "system") {
        Surface(color = LocalAppColors.current.surface, modifier = Modifier.padding(16.dp)) {
            AppNumericKeypad(
                expression = "950÷2",
                onExpressionChange = {},
                isBangla = false
            )
        }
    }
}
