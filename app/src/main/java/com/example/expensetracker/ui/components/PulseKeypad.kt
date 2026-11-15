package com.example.expensetracker.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.ExpenseTrackerTheme
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.ShapeButton
import com.example.expensetracker.theme.ShapeInput
import com.example.expensetracker.theme.TextStyleNumberKeypad
import com.example.expensetracker.ui.adaptive.LocalWindowClass

sealed interface PulseKeypadKey {
    val display: String

    data class Digit(val char: Char, override val display: String) : PulseKeypadKey
    data class Operator(val char: Char, override val display: String) : PulseKeypadKey
    object Decimal : PulseKeypadKey { override val display: String = "." }
    object Backspace : PulseKeypadKey { override val display: String = "⌫" }
}

/**
 * PulseKeypad:
 * Unified Thumb-First Numeric Keypad for Pulse Design Language:
 * - 4x4 Grid (+ - x /, backspace, long-press clear, decimals)
 * - Key size: clamp(56dp, availableWidth/4 - gaps, 72dp)
 * - Centered & capped at 360dp on Expanded screens
 * - 56dp full-width action button (Done / Save)
 * - Tactile haptics & subtle scale feedback
 */
@Composable
fun PulseKeypad(
    expression: String,
    onExpressionChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isBangla: Boolean = false,
    onDone: (() -> Unit)? = null,
    doneText: String? = null,
    doneEnabled: Boolean = true,
    doneButtonColor: Color = LocalAppColors.current.accent,
    onDoneTextColor: Color = LocalAppColors.current.onAccent,
    haptics: AppHaptics = rememberAppHaptics()
) {
    val windowClass = LocalWindowClass.current

    val rows = remember(isBangla) {
        listOf(
            listOf(
                PulseKeypadKey.Digit('7', if (isBangla) "৭" else "7"),
                PulseKeypadKey.Digit('8', if (isBangla) "৮" else "8"),
                PulseKeypadKey.Digit('9', if (isBangla) "৯" else "9"),
                PulseKeypadKey.Operator('÷', "÷")
            ),
            listOf(
                PulseKeypadKey.Digit('4', if (isBangla) "৪" else "4"),
                PulseKeypadKey.Digit('5', if (isBangla) "৫" else "5"),
                PulseKeypadKey.Digit('6', if (isBangla) "৬" else "6"),
                PulseKeypadKey.Operator('×', "×")
            ),
            listOf(
                PulseKeypadKey.Digit('1', if (isBangla) "১" else "1"),
                PulseKeypadKey.Digit('2', if (isBangla) "২" else "2"),
                PulseKeypadKey.Digit('3', if (isBangla) "৩" else "3"),
                PulseKeypadKey.Operator('−', "−")
            ),
            listOf(
                PulseKeypadKey.Decimal,
                PulseKeypadKey.Digit('0', if (isBangla) "০" else "0"),
                PulseKeypadKey.Backspace,
                PulseKeypadKey.Operator('+', "+")
            )
        )
    }

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        BoxWithConstraints(
            modifier = Modifier.widthIn(max = if (windowClass.isExpanded) 360.dp else Dp.Infinity)
        ) {
            val keyGap = 8.dp
            val totalGaps = keyGap * 3
            val keyHeight = if (maxHeight < 280.dp && maxHeight > 0.dp) 48.dp else 56.dp

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(keyGap)
            ) {
                rows.forEach { rowKeys ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(keyGap)
                    ) {
                        rowKeys.forEach { key ->
                            PulseKeyItem(
                                key = key,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(keyHeight),
                                onClick = {
                                    haptics.lightTick()
                                    when (key) {
                                        is PulseKeypadKey.Digit -> onExpressionChange(AmountKeypadLogic.appendDigit(expression, key.char))
                                        is PulseKeypadKey.Operator -> onExpressionChange(AmountKeypadLogic.appendOperator(expression, key.char))
                                        is PulseKeypadKey.Decimal -> onExpressionChange(AmountKeypadLogic.appendDecimal(expression))
                                        is PulseKeypadKey.Backspace -> onExpressionChange(AmountKeypadLogic.backspace(expression))
                                    }
                                },
                                onLongClick = if (key is PulseKeypadKey.Backspace) {
                                    {
                                        haptics.mediumClick()
                                        onExpressionChange(AmountKeypadLogic.clear())
                                    }
                                } else null
                            )
                        }
                    }
                }

                if (onDone != null && doneText != null) {
                    Button(
                        onClick = {
                            haptics.successFeedback()
                            onDone()
                        },
                        enabled = doneEnabled,
                        shape = ShapeButton,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = doneButtonColor,
                            contentColor = onDoneTextColor
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .padding(top = 4.dp)
                    ) {
                        Text(
                            text = doneText,
                            style = TextStyleNumberKeypad.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                            color = onDoneTextColor
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PulseKeyItem(
    key: PulseKeypadKey,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLongClick: (() -> Unit)? = null
) {
    val appColors = LocalAppColors.current
    val isOperator = key is PulseKeypadKey.Operator
    val isBackspace = key is PulseKeypadKey.Backspace

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.94f else 1f, label = "KeyScale")

    val bgColor = when {
        isPressed -> if (isOperator) appColors.info.copy(alpha = 0.25f) else appColors.border
        isOperator -> appColors.infoContainer
        else -> appColors.surfaceVariant
    }

    val contentColor = when {
        isOperator -> appColors.info
        isBackspace -> appColors.expense
        else -> appColors.textPrimary
    }

    Box(
        modifier = modifier
            .scale(scale)
            .clip(ShapeInput)
            .background(bgColor)
            .combinedClickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                onClick = onClick,
                onLongClick = onLongClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = key.display,
            style = TextStyleNumberKeypad,
            color = contentColor,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(name = "PulseKeypad - Dark")
@Composable
private fun PulseKeypadDarkPreview() {
    ExpenseTrackerTheme(themeMode = "dark") {
        PulseKeypad(
            expression = "120+45",
            onExpressionChange = {},
            doneText = "Save Expense",
            onDone = {}
        )
    }
}

@Preview(name = "PulseKeypad - Light Bengali")
@Composable
private fun PulseKeypadLightPreview() {
    ExpenseTrackerTheme(themeMode = "light") {
        PulseKeypad(
            expression = "120",
            onExpressionChange = {},
            isBangla = true,
            doneText = "সংরক্ষণ করুন",
            onDone = {}
        )
    }
}
