package com.example.expensetracker.ui.transactions

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.CalculatorEvaluator
import com.example.expensetracker.ui.components.CalculatorKeypadBar
import com.example.expensetracker.ui.components.CalculatorPreviewBadge
import com.example.expensetracker.ui.components.CalculatorToggleButton
import com.example.expensetracker.ui.components.ExpandableCalculatorKeypadBar
import com.example.expensetracker.ui.components.Strings
import com.example.expensetracker.ui.components.rememberAppHaptics

@Composable
fun TransactionAmountCard(
    amountText: String,
    onAmountChange: (String) -> Unit,
    isExpense: Boolean,
    typeAccentColor: Color,
    currencySymbol: String,
    errorMessage: String?,
    amountFocusRequester: FocusRequester,
    strings: Strings,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val haptics = rememberAppHaptics()
    var showCalculator by remember { mutableStateOf(false) }
    val hasOperators = remember(amountText) {
        amountText.any { it in listOf('+', '-', '*', '/', '×', '÷', '−', '—') }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            border = BorderStroke(
                1.dp,
                Brush.verticalGradient(
                    listOf(typeAccentColor.copy(alpha = 0.45f), LocalAppColors.current.border)
                )
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(LocalAppColors.current.surface, LocalAppColors.current.surfaceVariant)
                        )
                    )
                    .padding(16.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(typeAccentColor)
                            )
                            Text(
                                text = if (isExpense) strings.expenseAmount else strings.incomeAmount,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    letterSpacing = 0.2.sp
                                ),
                                color = appColors.textSecondary
                            )
                        }

                        CalculatorToggleButton(
                            isOpen = showCalculator || hasOperators,
                            onToggle = {
                                haptics.lightTick()
                                showCalculator = !showCalculator
                            },
                            accentColor = typeAccentColor,
                            label = strings.calc
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    TransactionAmountInputField(
                        amountText = amountText,
                        onAmountChange = onAmountChange,
                        currencySymbol = currencySymbol,
                        typeAccentColor = typeAccentColor,
                        amountFocusRequester = amountFocusRequester
                    )

                    // Live calculation preview badge
                    CalculatorPreviewBadge(
                        expression = amountText,
                        currencySymbol = currencySymbol,
                        onApply = { evaluated ->
                            haptics.successFeedback()
                            onAmountChange(CalculatorEvaluator.formatResult(evaluated))
                            showCalculator = false
                        },
                        modifier = Modifier.padding(top = 4.dp),
                        accentColor = typeAccentColor
                    )

                    // Expandable On-Demand Calculator Operator Bar
                    ExpandableCalculatorKeypadBar(
                        expanded = showCalculator,
                        currentExpression = amountText,
                        onExpressionChanged = onAmountChange,
                        onApplyResult = { evaluated ->
                            haptics.successFeedback()
                            onAmountChange(CalculatorEvaluator.formatResult(evaluated))
                            showCalculator = false
                        },
                        accentColor = typeAccentColor,
                        haptics = haptics
                    )
                }
            }
        }

        if (errorMessage != null) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = appColors.expense.copy(alpha = 0.15f),
                border = BorderStroke(1.dp, appColors.expense.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                Text(
                    text = errorMessage,
                    color = appColors.expense,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}
