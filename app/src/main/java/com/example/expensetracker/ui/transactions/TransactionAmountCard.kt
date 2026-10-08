package com.example.expensetracker.ui.transactions

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.ColorDangerExpense
import com.example.expensetracker.theme.ColorTextSecondary
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.CalculatorKeypadBar
import com.example.expensetracker.ui.components.CalculatorPreviewBadge
import com.example.expensetracker.ui.components.Strings
import com.example.expensetracker.ui.components.rememberAppHaptics
import java.util.Locale

@Composable
fun TransactionAmountCard(
    amountText: String,
    onAmountChange: (String) -> Unit,
    isExpense: Boolean,
    typeAccentColor: Color,
    currencySymbol: String,
    showCalculatorBar: Boolean,
    onToggleCalculator: () -> Unit,
    errorMessage: String?,
    amountFocusRequester: FocusRequester,
    strings: Strings,
    modifier: Modifier = Modifier
) {
    val haptics = rememberAppHaptics()

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
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
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
                                color = ColorTextSecondary
                            )
                        }

                        Surface(
                            shape = CircleShape,
                            color = if (showCalculatorBar) typeAccentColor.copy(alpha = 0.2f) else LocalAppColors.current.surfaceVariant,
                            border = BorderStroke(1.dp, if (showCalculatorBar) typeAccentColor else LocalAppColors.current.border),
                            modifier = Modifier.clickable {
                                haptics.lightTick()
                                onToggleCalculator()
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Calculate,
                                    contentDescription = "Calculator",
                                    tint = if (showCalculatorBar) typeAccentColor else ColorTextSecondary,
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = strings.calc,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    color = if (showCalculatorBar) typeAccentColor else ColorTextSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    TransactionAmountInputField(
                        amountText = amountText,
                        onAmountChange = onAmountChange,
                        currencySymbol = currencySymbol,
                        typeAccentColor = typeAccentColor,
                        amountFocusRequester = amountFocusRequester
                    )

                    CalculatorPreviewBadge(
                        expression = amountText,
                        currencySymbol = currencySymbol,
                        onApply = { evaluated ->
                            haptics.successFeedback()
                            onAmountChange(if (evaluated % 1.0 == 0.0) evaluated.toLong().toString() else String.format(Locale.US, "%.2f", evaluated))
                        },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    AnimatedVisibility(
                        visible = showCalculatorBar,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        Column {
                            Spacer(modifier = Modifier.height(8.dp))
                            CalculatorKeypadBar(
                                currentExpression = amountText,
                                onExpressionChanged = onAmountChange,
                                onApplyResult = { evaluated ->
                                    haptics.successFeedback()
                                    onAmountChange(if (evaluated % 1.0 == 0.0) evaluated.toLong().toString() else String.format(Locale.US, "%.2f", evaluated))
                                }
                            )
                        }
                    }
                }
            }
        }

        if (errorMessage != null) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = ColorDangerExpense.copy(alpha = 0.15f),
                border = BorderStroke(1.dp, ColorDangerExpense.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                Text(
                    text = errorMessage,
                    color = ColorDangerExpense,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}
