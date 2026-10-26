package com.example.expensetracker.ui.receivables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.AppHaptics
import com.example.expensetracker.ui.components.CalculatorPreviewBadge
import com.example.expensetracker.ui.components.ExpandableCalculatorKeypadBar
import java.util.Locale

@Composable
fun BillSplitterAmountInput(
    amountText: String,
    onAmountChange: (String) -> Unit,
    currencySymbol: String,
    label: String,
    haptics: AppHaptics,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    var showCalculator by remember { mutableStateOf(false) }
    val hasOperators = remember(amountText) {
        amountText.any { it in listOf('+', '-', '*', '/', '×', '÷', '−', '—') }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = amountText,
            onValueChange = { onAmountChange(it.filter { c -> c.isDigit() || c == '.' || c in "+-×÷/* " }) },
            label = { Text("$label ($currencySymbol)") },
            trailingIcon = {
                IconButton(
                    onClick = {
                        haptics.lightTick()
                        showCalculator = !showCalculator
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Calculate,
                        contentDescription = "Calculator",
                        tint = if (showCalculator || hasOperators) appColors.accent else LocalAppColors.current.textSecondary
                    )
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
                showCalculator = false
            }
        )

        ExpandableCalculatorKeypadBar(
            expanded = showCalculator,
            currentExpression = amountText,
            onExpressionChanged = onAmountChange,
            onApplyResult = { evaluated ->
                val formatted = if (evaluated % 1.0 == 0.0) evaluated.toLong().toString() else String.format(Locale.US, "%.2f", evaluated)
                onAmountChange(formatted)
                showCalculator = false
            },
            haptics = haptics
        )
    }
}
