package com.example.expensetracker.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.expensetracker.theme.LocalAppColors

@Composable
fun ExpandableCalculatorKeypadBar(
    expanded: Boolean,
    currentExpression: String,
    onExpressionChanged: (String) -> Unit,
    onApplyResult: (Double) -> Unit,
    modifier: Modifier = Modifier,
    accentColor: Color = LocalAppColors.current.accent,
    haptics: AppHaptics = rememberAppHaptics()
) {
    val hasOperators = remember(currentExpression) {
        currentExpression.any { it in listOf('+', '-', '*', '/', '×', '÷', '−', '—') }
    }

    AnimatedVisibility(
        visible = expanded || hasOperators,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut(),
        modifier = modifier
    ) {
        Column {
            Spacer(modifier = Modifier.height(8.dp))
            CalculatorKeypadBar(
                currentExpression = currentExpression,
                onExpressionChanged = onExpressionChanged,
                onApplyResult = onApplyResult,
                accentColor = accentColor,
                haptics = haptics
            )
        }
    }
}
