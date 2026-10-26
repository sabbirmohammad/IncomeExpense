package com.example.expensetracker.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.LocalAppColors

@Composable
fun CalculatorToggleButton(
    isOpen: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    accentColor: Color = LocalAppColors.current.accent,
    label: String = "হিসাব"
) {
    val haptics = rememberAppHaptics()
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (isOpen) accentColor.copy(alpha = 0.15f) else LocalAppColors.current.surfaceVariant,
        border = BorderStroke(
            1.dp,
            if (isOpen) accentColor.copy(alpha = 0.4f) else LocalAppColors.current.border
        ),
        modifier = modifier
            .minimumInteractiveComponentSize()
            .clip(RoundedCornerShape(8.dp))
            .clickable {
                haptics.lightTick()
                onToggle()
            }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Calculate,
                contentDescription = label,
                tint = if (isOpen) accentColor else LocalAppColors.current.textSecondary,
                modifier = Modifier.size(15.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    fontWeight = if (isOpen) FontWeight.Bold else FontWeight.Medium
                ),
                color = if (isOpen) accentColor else LocalAppColors.current.textSecondary
            )
        }
    }
}

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

@Composable
fun CalculatorKeypadBar(
    currentExpression: String,
    onExpressionChanged: (String) -> Unit,
    onApplyResult: (Double) -> Unit,
    modifier: Modifier = Modifier,
    accentColor: Color = LocalAppColors.current.accent,
    haptics: AppHaptics = rememberAppHaptics()
) {
    val appColors = LocalAppColors.current
    val evaluated = remember(currentExpression) {
        if (currentExpression.any { it in listOf('+', '-', '*', '/', '×', '÷', '−', '—') }) {
            CalculatorEvaluator.evaluate(currentExpression)
        } else null
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(LocalAppColors.current.surfaceVariant)
            .border(BorderStroke(1.dp, LocalAppColors.current.border), RoundedCornerShape(14.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val ops = listOf("+", "−", "×", "÷")

        ops.forEach { op ->
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = LocalAppColors.current.surface,
                border = BorderStroke(1.dp, LocalAppColors.current.border),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .padding(horizontal = 2.dp)
                    .clickable {
                        haptics.lightTick()
                        val trimmed = currentExpression.trimEnd()
                        if (trimmed.isEmpty()) {
                            if (op == "−") onExpressionChanged("- ")
                            return@clickable
                        }
                        val opChars = listOf('+', '-', '*', '/', '×', '÷', '−', '—')
                        if (trimmed.last() in opChars) {
                            // Replace previous operator
                            val base = trimmed.dropLast(1).trimEnd()
                            onExpressionChanged(if (base.isEmpty() && op == "−") "- " else "$base $op ")
                        } else if (trimmed.last() == '.') {
                            val base = trimmed.dropLast(1)
                            onExpressionChanged("$base $op ")
                        } else {
                            onExpressionChanged("$trimmed $op ")
                        }
                    }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = op,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentColor
                    )
                }
            }
        }

        // Equals (=) Button
        val equalsContentColor = if (accentColor == appColors.income && appColors.isDark) Color(0xFF052B14) else Color.White
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = if (evaluated != null) accentColor else accentColor.copy(alpha = 0.35f),
            modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .padding(horizontal = 2.dp)
                .clickable(enabled = evaluated != null) {
                    haptics.successFeedback()
                    evaluated?.let { onApplyResult(it) }
                }
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "=",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = equalsContentColor
                )
            }
        }

        // Clear (C) Button
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = appColors.expense.copy(alpha = 0.12f),
            border = BorderStroke(1.dp, appColors.expense.copy(alpha = 0.25f)),
            modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .padding(horizontal = 2.dp)
                .clickable {
                    haptics.lightTick()
                    onExpressionChanged("")
                }
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "C",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = appColors.expense
                )
            }
        }
    }
}

@Composable
fun CalculatorPreviewBadge(
    expression: String,
    currencySymbol: String,
    onApply: (Double) -> Unit,
    modifier: Modifier = Modifier,
    accentColor: Color = LocalAppColors.current.accent
) {
    val haptics = rememberAppHaptics()
    val evaluated = remember(expression) {
        if (expression.any { it in listOf('+', '-', '*', '/', '×', '÷', '−', '—') }) {
            CalculatorEvaluator.evaluate(expression)
        } else null
    }

    AnimatedVisibility(
        visible = evaluated != null && evaluated > 0.0,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        if (evaluated != null) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = accentColor.copy(alpha = 0.15f),
                border = BorderStroke(1.dp, accentColor.copy(alpha = 0.4f)),
                modifier = modifier
                    .minimumInteractiveComponentSize()
                    .clickable {
                        haptics.mediumClick()
                        onApply(evaluated)
                    }
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Calculate,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = " = $currencySymbol ${CalculatorEvaluator.formatResult(evaluated)} (Tap to apply)",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = accentColor,
                            fontSize = 12.sp
                        ),
                        modifier = Modifier.padding(start = 6.dp)
                    )
                }
            }
        }
    }
}
