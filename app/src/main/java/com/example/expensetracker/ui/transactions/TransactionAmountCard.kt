package com.example.expensetracker.ui.transactions

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.HairlineInkStroke
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.RadiusM
import com.example.expensetracker.theme.RadiusS
import com.example.expensetracker.ui.components.AmountKeypadLogic
import com.example.expensetracker.ui.components.CalculatorEvaluator
import com.example.expensetracker.ui.components.Strings
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.rememberAppHaptics

/**
 * V7 Transaction Amount Card:
 * - Height <= 140dp total footprint.
 * - Flat tonal surface (no outline in Dark/AMOLED per C1).
 * - Big amount with tabular numbers ("tnum"), display style.
 * - Placeholder "0" (shows decimals only after "." is typed).
 * - Live math expression and evaluated result line.
 */
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
    modifier: Modifier = Modifier,
    isTransfer: Boolean = false,
    onCardClick: (() -> Unit)? = null
) {
    val appColors = LocalAppColors.current
    val haptics = rememberAppHaptics()

    val hasOperators = remember(amountText) {
        AmountKeypadLogic.hasOperators(amountText)
    }
    val evaluatedValue = remember(amountText) {
        AmountKeypadLogic.evaluate(amountText)
    }

    val displayAmount = remember(amountText, strings.isBangla) {
        when {
            amountText.isEmpty() -> "0"
            strings.isBangla -> convertToBengaliDigits(amountText)
            else -> amountText
        }
    }

    val borderStroke = if (appColors.isDark) {
        null
    } else {
        BorderStroke(HairlineInkStroke, appColors.border)
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Surface(
            shape = RoundedCornerShape(RadiusM),
            color = appColors.surfaceVariant,
            border = borderStroke,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 140.dp)
                .then(
                    if (onCardClick != null) Modifier.clickable {
                        haptics.lightTick()
                        onCardClick()
                    } else Modifier
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Header indicator row
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
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(typeAccentColor)
                        )
                        Text(
                            text = when {
                                isTransfer -> strings.transferTab
                                isExpense -> strings.expenseAmount
                                else -> strings.incomeAmount
                            },
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = appColors.textSecondary
                        )
                    }

                    // Clear button if amount entered
                    if (amountText.isNotEmpty()) {
                        Text(
                            text = strings.clear,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = appColors.textSecondary,
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .clickable {
                                    haptics.lightTick()
                                    onAmountChange("")
                                }
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Big Hero Amount Display (<= 140dp area) with responsive dynamic font sizing
                val amountFontSize = when {
                    displayAmount.length > 14 -> 20.sp
                    displayAmount.length > 10 -> 25.sp
                    displayAmount.length > 7 -> 30.sp
                    else -> 38.sp
                }
                val symbolFontSize = when {
                    displayAmount.length > 14 -> 18.sp
                    displayAmount.length > 10 -> 22.sp
                    displayAmount.length > 7 -> 26.sp
                    else -> 32.sp
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .semantics {
                            contentDescription = "Amount $currencySymbol $displayAmount"
                        }
                ) {
                    Text(
                        text = "$currencySymbol ",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontSize = symbolFontSize,
                            fontWeight = FontWeight.Bold
                        ),
                        color = typeAccentColor,
                        maxLines = 1,
                        softWrap = false
                    )
                    Text(
                        text = displayAmount,
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontSize = amountFontSize,
                            fontWeight = FontWeight.Bold,
                            fontFeatureSettings = "tnum"
                        ),
                        color = if (amountText.isEmpty()) appColors.textSecondary.copy(alpha = 0.45f) else appColors.textPrimary,
                        textAlign = TextAlign.Start,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Live Evaluated Result Line (when math expression is entered)
                AnimatedVisibility(
                    visible = hasOperators && evaluatedValue != null,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    val evalStr = evaluatedValue?.let { AmountKeypadLogic.formatEvaluated(it, strings.isBangla) } ?: ""
                    Surface(
                        shape = RoundedCornerShape(RadiusS),
                        color = appColors.accentContainer,
                        border = BorderStroke(HairlineInkStroke, appColors.accent.copy(alpha = 0.35f)),
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .clickable {
                                haptics.lightTick()
                                evaluatedValue?.let {
                                    val clean = CalculatorEvaluator.formatResult(it)
                                    onAmountChange(clean)
                                }
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "= $currencySymbol $evalStr",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    fontFeatureSettings = "tnum"
                                ),
                                color = appColors.accent
                            )
                            Text(
                                text = if (strings.isBangla) "(ট্যাপ করুন)" else "(Tap to apply)",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                color = appColors.textSecondary
                            )
                        }
                    }
                }
            }
        }

        // Inline Error Message
        if (errorMessage != null) {
            Surface(
                shape = RoundedCornerShape(RadiusS),
                color = appColors.expense.copy(alpha = 0.12f),
                border = BorderStroke(HairlineInkStroke, appColors.expense.copy(alpha = 0.35f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp)
            ) {
                Text(
                    text = errorMessage,
                    color = appColors.expense,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp, fontWeight = FontWeight.Medium),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}
