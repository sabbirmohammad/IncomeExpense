package com.example.expensetracker.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.ColorAccent

@Composable
fun CalculatorKeypadBar(
    currentExpression: String,
    onExpressionChanged: (String) -> Unit,
    onApplyResult: (Double) -> Unit,
    modifier: Modifier = Modifier,
    haptics: AppHaptics = rememberAppHaptics()
) {
    val evaluated = remember(currentExpression) {
        if (currentExpression.any { it in listOf('+', '-', '*', '/', '×', '÷', '−', '—') }) {
            CalculatorEvaluator.evaluate(currentExpression)
        } else null
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF141923))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val ops = listOf("+", "−", "×", "÷")

        ops.forEach { op ->
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF1F2633))
                    .clickable {
                        haptics.lightTick()
                        val trimmed = currentExpression.trimEnd()
                        if (trimmed.isNotEmpty() && trimmed.last() !in listOf('+', '-', '*', '/', '×', '÷', '−', '—', '.')) {
                            onExpressionChanged("$trimmed $op ")
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = op,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorAccent
                )
            }
        }

        // Equals (=) Button
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(ColorAccent)
                .clickable(enabled = evaluated != null) {
                    haptics.successFeedback()
                    evaluated?.let { onApplyResult(it) }
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "=",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        // Clear (C) Button
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF2E1A22))
                .clickable {
                    haptics.lightTick()
                    onExpressionChanged("")
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "C",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF5252)
            )
        }
    }
}

@Composable
fun CalculatorPreviewBadge(
    expression: String,
    currencySymbol: String,
    onApply: (Double) -> Unit,
    modifier: Modifier = Modifier
) {
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
                color = ColorAccent.copy(alpha = 0.15f),
                border = BorderStroke(1.dp, ColorAccent.copy(alpha = 0.4f)),
                modifier = modifier
                    .clickable { onApply(evaluated) }
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Calculate,
                        contentDescription = null,
                        tint = ColorAccent,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = " = $currencySymbol ${CalculatorEvaluator.formatResult(evaluated)} (Tap to apply)",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = ColorAccent,
                            fontSize = 12.sp
                        ),
                        modifier = Modifier.padding(start = 6.dp)
                    )
                }
            }
        }
    }
}
