package com.example.expensetracker.ui.receivables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorBorder
import com.example.expensetracker.ui.components.AppHaptics
import com.example.expensetracker.ui.components.CalculatorKeypadBar
import com.example.expensetracker.ui.components.CalculatorPreviewBadge
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
    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = amountText,
            onValueChange = { onAmountChange(it.filter { c -> c.isDigit() || c == '.' || c in "+-×÷/* " }) },
            label = { Text("$label ($currencySymbol)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ColorAccent,
                unfocusedBorderColor = ColorBorder
            )
        )

        CalculatorPreviewBadge(
            expression = amountText,
            currencySymbol = currencySymbol,
            onApply = { evaluated ->
                val formatted = if (evaluated % 1.0 == 0.0) evaluated.toLong().toString() else String.format(Locale.US, "%.2f", evaluated)
                onAmountChange(formatted)
            }
        )

        Spacer(modifier = Modifier.height(6.dp))

        CalculatorKeypadBar(
            currentExpression = amountText,
            onExpressionChanged = onAmountChange,
            onApplyResult = { evaluated ->
                val formatted = if (evaluated % 1.0 == 0.0) evaluated.toLong().toString() else String.format(Locale.US, "%.2f", evaluated)
                onAmountChange(formatted)
            },
            haptics = haptics
        )
    }
}
