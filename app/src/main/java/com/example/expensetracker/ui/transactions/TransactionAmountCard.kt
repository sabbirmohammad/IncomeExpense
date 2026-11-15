package com.example.expensetracker.ui.transactions

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.common.InputSanitizer
import com.example.expensetracker.theme.HairlineInkStroke
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.RadiusM
import com.example.expensetracker.theme.RadiusS
import com.example.expensetracker.ui.components.AmountKeypadLogic
import com.example.expensetracker.ui.components.Strings
import com.example.expensetracker.ui.components.convertToBengaliDigits

/**
 * V7 Transaction Amount Card:
 * - Height <= 140dp total footprint.
 * - Flat tonal surface (no outline in Dark/AMOLED per C1).
 * - Big amount with tabular numbers ("tnum"), display style.
 * - Native numeric keyboard integration via BasicTextField.
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
    isTransfer: Boolean = false
) {
    val appColors = LocalAppColors.current
    val focusManager = LocalFocusManager.current

    val hasOperators = remember(amountText) { AmountKeypadLogic.hasOperators(amountText) }
    val evaluatedValue = remember(amountText) { AmountKeypadLogic.evaluate(amountText) }

    val displayAmount = remember(amountText, strings.isBangla) {
        when {
            amountText.isEmpty() -> "0"
            strings.isBangla -> convertToBengaliDigits(amountText)
            else -> amountText
        }
    }

    val borderStroke = if (appColors.isDark) null else BorderStroke(HairlineInkStroke, appColors.border)

    Column(modifier = modifier.fillMaxWidth()) {
        Surface(
            shape = RoundedCornerShape(RadiusM),
            color = appColors.surfaceVariant,
            border = borderStroke,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 140.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    try { amountFocusRequester.requestFocus() } catch (_: Exception) {}
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                TransactionAmountHeaderRow(
                    isTransfer = isTransfer,
                    isExpense = isExpense,
                    typeAccentColor = typeAccentColor,
                    amountText = amountText,
                    strings = strings,
                    onClear = { onAmountChange("") }
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text(
                        text = "$currencySymbol ",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = typeAccentColor
                    )
                    BasicTextField(
                        value = amountText,
                        onValueChange = { newVal ->
                            val sanitized = InputSanitizer.sanitizeMathInput(newVal)
                            onAmountChange(sanitized)
                        },
                        modifier = Modifier
                            .focusRequester(amountFocusRequester)
                            .semantics {
                                contentDescription = "Amount $currencySymbol $displayAmount"
                            },
                        textStyle = MaterialTheme.typography.displayMedium.copy(
                            fontSize = 38.sp,
                            fontWeight = FontWeight.Bold,
                            fontFeatureSettings = "tnum",
                            color = appColors.textPrimary,
                            textAlign = TextAlign.Start
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() }
                        ),
                        singleLine = true,
                        cursorBrush = SolidColor(typeAccentColor),
                        decorationBox = { innerTextField ->
                            Box(contentAlignment = Alignment.CenterStart) {
                                if (amountText.isEmpty()) {
                                    Text(
                                        text = if (strings.isBangla) "০" else "0",
                                        style = MaterialTheme.typography.displayMedium.copy(
                                            fontSize = 38.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFeatureSettings = "tnum"
                                        ),
                                        color = appColors.textSecondary.copy(alpha = 0.45f)
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )
                }

                TransactionAmountLiveEvaluation(
                    hasOperators = hasOperators,
                    evaluatedValue = evaluatedValue,
                    currencySymbol = currencySymbol,
                    strings = strings,
                    onApplyEvaluated = onAmountChange
                )
            }
        }

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
