package com.example.expensetracker.ui.receivables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorBackground
import com.example.expensetracker.theme.ColorBorder
import com.example.expensetracker.theme.ColorTextMuted
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.theme.ColorTextSecondary
import com.example.expensetracker.theme.SmallElementRadius
import com.example.expensetracker.ui.components.CalculatorKeypadBar
import com.example.expensetracker.ui.components.CalculatorPreviewBadge
import java.util.Locale

@Composable
fun ReceivableAmountInputSection(
    amountText: String,
    onAmountChange: (String) -> Unit,
    currencySymbol: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(label, fontSize = 12.sp, color = ColorTextSecondary)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = amountText,
            onValueChange = { input ->
                onAmountChange(input.filter { c -> c.isDigit() || c == '.' || c in "+-×÷/* " })
            },
            placeholder = { Text("0.00", color = ColorTextMuted) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(SmallElementRadius),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = ColorBackground,
                unfocusedContainerColor = ColorBackground,
                focusedBorderColor = ColorAccent,
                unfocusedBorderColor = ColorBorder,
                focusedTextColor = ColorTextPrimary,
                unfocusedTextColor = ColorTextPrimary
            ),
            modifier = Modifier.fillMaxWidth()
        )

        CalculatorPreviewBadge(
            expression = amountText,
            currencySymbol = currencySymbol,
            onApply = { evaluated ->
                val formatted = if (evaluated % 1.0 == 0.0) {
                    evaluated.toLong().toString()
                } else {
                    String.format(Locale.US, "%.2f", evaluated)
                }
                onAmountChange(formatted)
            }
        )

        Spacer(modifier = Modifier.height(4.dp))

        CalculatorKeypadBar(
            currentExpression = amountText,
            onExpressionChanged = onAmountChange,
            onApplyResult = { evaluated ->
                val formatted = if (evaluated % 1.0 == 0.0) {
                    evaluated.toLong().toString()
                } else {
                    String.format(Locale.US, "%.2f", evaluated)
                }
                onAmountChange(formatted)
            }
        )
    }
}
