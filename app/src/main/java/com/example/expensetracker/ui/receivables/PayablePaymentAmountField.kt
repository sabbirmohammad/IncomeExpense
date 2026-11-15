package com.example.expensetracker.ui.receivables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.CalculatorPreviewBadge
import com.example.expensetracker.ui.components.ExpandableCalculatorKeypadBar
import com.example.expensetracker.ui.components.LocalAppStrings
import java.util.Locale

@Composable
fun PayablePaymentAmountField(
    amountText: String,
    onAmountChange: (String) -> Unit,
    showCalculator: Boolean,
    onToggleCalculator: () -> Unit,
    currencySymbol: String,
    onCalculatorClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val appColors = LocalAppColors.current
    val hasOperators = amountText.any { it in listOf('+', '-', '*', '/', '×', '÷', '−', '—') }

    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = amountText,
            onValueChange = onAmountChange,
            label = { Text(strings.amount) },
            trailingIcon = {
                IconButton(onClick = onToggleCalculator) {
                    Icon(
                        imageVector = Icons.Filled.Calculate,
                        contentDescription = "Calculator",
                        tint = if (showCalculator || hasOperators) appColors.accent else appColors.textSecondary
                    )
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = appColors.accent,
                unfocusedBorderColor = appColors.border
            )
        )

        CalculatorPreviewBadge(
            expression = amountText,
            currencySymbol = currencySymbol,
            onApply = { evaluated ->
                val formatted = if (evaluated % 1.0 == 0.0) evaluated.toLong().toString() else String.format(Locale.US, "%.2f", evaluated)
                onAmountChange(formatted)
                onCalculatorClose()
            }
        )

        ExpandableCalculatorKeypadBar(
            expanded = showCalculator,
            currentExpression = amountText,
            onExpressionChanged = onAmountChange,
            onApplyResult = { evaluated ->
                val formatted = if (evaluated % 1.0 == 0.0) evaluated.toLong().toString() else String.format(Locale.US, "%.2f", evaluated)
                onAmountChange(formatted)
                onCalculatorClose()
            }
        )
    }
}
