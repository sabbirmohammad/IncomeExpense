package com.example.expensetracker.ui.savings

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.common.InputSanitizer
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.SmallElementRadius
import com.example.expensetracker.ui.components.CalculatorPreviewBadge
import com.example.expensetracker.ui.components.ExpandableCalculatorKeypadBar
import com.example.expensetracker.ui.components.LocalAppStrings
import java.util.Locale

@Composable
fun ContributionAmountInputSection(
    amountText: String,
    onAmountChange: (String) -> Unit,
    showCalculator: Boolean,
    onToggleCalculator: () -> Unit,
    goalColor: Color,
    currencySymbol: String
) {
    val appColors = LocalAppColors.current
    val strings = LocalAppStrings.current
    val hasOperators = amountText.any { it in listOf('+', '-', '*', '/', '×', '÷', '−', '—') }

    Text("${strings.amount} ($currencySymbol)", fontSize = 12.sp, color = appColors.textSecondary)
    Spacer(modifier = Modifier.height(4.dp))
    OutlinedTextField(
        value = amountText,
        onValueChange = {
            onAmountChange(InputSanitizer.sanitizeMathInput(it))
        },
        placeholder = { Text(if (strings.isBangla) "০.০০" else "0.00", color = appColors.textMuted) },
        trailingIcon = {
            IconButton(onClick = onToggleCalculator) {
                Icon(
                    imageVector = Icons.Filled.Calculate,
                    contentDescription = "Calculator",
                    tint = if (showCalculator || hasOperators) goalColor else appColors.textSecondary
                )
            }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        shape = RoundedCornerShape(SmallElementRadius),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = appColors.surfaceVariant,
            unfocusedContainerColor = appColors.surfaceVariant,
            focusedBorderColor = goalColor,
            unfocusedBorderColor = appColors.border,
            focusedTextColor = appColors.textPrimary,
            unfocusedTextColor = appColors.textPrimary
        ),
        modifier = Modifier.fillMaxWidth()
    )

    CalculatorPreviewBadge(
        expression = amountText,
        currencySymbol = currencySymbol,
        onApply = { evaluated ->
            onAmountChange(formatEvaluated(evaluated))
            if (showCalculator) onToggleCalculator()
        }
    )

    ExpandableCalculatorKeypadBar(
        expanded = showCalculator,
        currentExpression = amountText,
        onExpressionChanged = onAmountChange,
        onApplyResult = { evaluated ->
            onAmountChange(formatEvaluated(evaluated))
            if (showCalculator) onToggleCalculator()
        },
        accentColor = goalColor
    )
}

private fun formatEvaluated(evaluated: Double): String {
    return if (evaluated % 1.0 == 0.0) evaluated.toLong().toString()
    else String.format(Locale.US, "%.2f", evaluated)
}
