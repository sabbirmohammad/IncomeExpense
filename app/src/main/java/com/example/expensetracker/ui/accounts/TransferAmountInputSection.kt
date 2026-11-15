package com.example.expensetracker.ui.accounts

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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.common.InputSanitizer
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.SmallElementRadius
import com.example.expensetracker.ui.components.AppHaptics
import com.example.expensetracker.ui.components.CalculatorPreviewBadge
import com.example.expensetracker.ui.components.ExpandableCalculatorKeypadBar
import com.example.expensetracker.ui.components.LocalAppStrings
import java.util.Locale

@Composable
fun TransferAmountInputSection(
    amountText: String,
    onAmountChange: (String) -> Unit,
    showCalculator: Boolean,
    onToggleCalculator: () -> Unit,
    currencySymbol: String,
    haptics: AppHaptics
) {
    val colors = LocalAppColors.current
    val strings = LocalAppStrings.current
    val hasOperators = amountText.any { it in listOf('+', '-', '*', '/', '×', '÷', '−', '—') }

    Text("${strings.amount} ($currencySymbol)", fontSize = 12.sp, color = colors.textSecondary)
    Spacer(modifier = Modifier.height(4.dp))
    OutlinedTextField(
        value = amountText,
        onValueChange = {
            onAmountChange(InputSanitizer.sanitizeMathInput(it))
        },
        placeholder = { Text(if (strings.isBangla) "০.০০" else "0.00", color = colors.textMuted) },
        trailingIcon = {
            IconButton(onClick = onToggleCalculator) {
                Icon(
                    imageVector = Icons.Filled.Calculate,
                    contentDescription = "Calculator",
                    tint = if (showCalculator || hasOperators) colors.accent else colors.textSecondary
                )
            }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        shape = RoundedCornerShape(SmallElementRadius),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = colors.surfaceVariant,
            unfocusedContainerColor = colors.surfaceVariant,
            focusedBorderColor = colors.accent,
            unfocusedBorderColor = colors.border,
            focusedTextColor = colors.textPrimary,
            unfocusedTextColor = colors.textPrimary
        ),
        modifier = Modifier.fillMaxWidth()
    )

    CalculatorPreviewBadge(
        expression = amountText,
        currencySymbol = currencySymbol,
        onApply = { evaluated ->
            haptics.successFeedback()
            val formatted = if (evaluated % 1.0 == 0.0) evaluated.toLong().toString() else String.format(Locale.US, "%.2f", evaluated)
            onAmountChange(formatted)
            if (showCalculator) onToggleCalculator()
        }
    )

    ExpandableCalculatorKeypadBar(
        expanded = showCalculator,
        currentExpression = amountText,
        onExpressionChanged = onAmountChange,
        onApplyResult = { evaluated ->
            haptics.successFeedback()
            val formatted = if (evaluated % 1.0 == 0.0) evaluated.toLong().toString() else String.format(Locale.US, "%.2f", evaluated)
            onAmountChange(formatted)
            if (showCalculator) onToggleCalculator()
        }
    )
}
