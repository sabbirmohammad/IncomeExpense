package com.example.expensetracker.ui.bills

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorBorder
import com.example.expensetracker.theme.ColorTextMuted
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.theme.ColorTextSecondary
import com.example.expensetracker.ui.components.CalculatorKeypadBar
import com.example.expensetracker.ui.components.CalculatorPreviewBadge
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.rememberAppHaptics
import java.util.Locale

@Composable
fun AddEditBillAmountSection(
    amountText: String,
    onAmountChange: (String) -> Unit,
    currencySymbol: String,
    showCalculatorBar: Boolean,
    onToggleCalculator: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val haptics = rememberAppHaptics()

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFF0F131D),
        border = BorderStroke(1.dp, ColorBorder),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = strings.expenseAmount,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    color = ColorTextSecondary
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (showCalculatorBar) ColorAccent.copy(alpha = 0.2f) else Color(0xFF0F131D),
                    border = BorderStroke(1.dp, if (showCalculatorBar) ColorAccent else ColorBorder),
                    modifier = Modifier.clickable {
                        haptics.lightTick()
                        onToggleCalculator()
                    }
                ) {
                    Text(
                        text = strings.calc,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = if (showCalculatorBar) ColorAccent else ColorTextSecondary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = currencySymbol,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = ColorAccent
                )
                Spacer(modifier = Modifier.width(6.dp))
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { input ->
                        val filtered = input.filter { it.isDigit() || it == '.' || it in "+-×÷/* " }
                        onAmountChange(filtered)
                    },
                    placeholder = {
                        Text(
                            text = if (strings.isBangla) "০.০০" else "0.00",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorTextMuted
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = ColorTextPrimary,
                        unfocusedTextColor = ColorTextPrimary
                    ),
                    textStyle = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorTextPrimary
                    )
                )
            }

            CalculatorPreviewBadge(
                expression = amountText,
                currencySymbol = currencySymbol,
                onApply = { evaluated ->
                    haptics.successFeedback()
                    val formatted = if (evaluated % 1.0 == 0.0) evaluated.toLong().toString() else String.format(Locale.US, "%.2f", evaluated)
                    onAmountChange(formatted)
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            AnimatedVisibility(visible = showCalculatorBar) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    CalculatorKeypadBar(
                        currentExpression = amountText,
                        onExpressionChanged = onAmountChange,
                        onApplyResult = { evaluated ->
                            haptics.successFeedback()
                            val formatted = if (evaluated % 1.0 == 0.0) evaluated.toLong().toString() else String.format(Locale.US, "%.2f", evaluated)
                            onAmountChange(formatted)
                        }
                    )
                }
            }
        }
    }
}
