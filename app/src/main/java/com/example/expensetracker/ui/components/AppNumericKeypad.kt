package com.example.expensetracker.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.expensetracker.theme.LocalAppColors

/**
 * Legacy wrapper around [PulseKeypad] for backwards compatibility with un-redesigned screens.
 * To be removed in a later phase once all callers are migrated.
 */
@Deprecated(
    message = "Use PulseKeypad directly for Pulse design language.",
    replaceWith = ReplaceWith("PulseKeypad(expression, onExpressionChange, modifier, isBangla, onDone, doneText, doneEnabled, doneButtonColor, haptics = haptics)")
)
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
    PulseKeypad(
        expression = expression,
        onExpressionChange = onExpressionChange,
        modifier = modifier,
        isBangla = isBangla,
        onDone = onDone,
        doneText = doneText,
        doneEnabled = doneEnabled,
        doneButtonColor = doneButtonColor,
        haptics = haptics
    )
}
