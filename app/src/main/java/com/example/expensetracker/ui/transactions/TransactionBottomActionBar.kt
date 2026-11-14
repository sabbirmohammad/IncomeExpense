package com.example.expensetracker.ui.transactions

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.HairlineInkStroke
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.ShapePill
import com.example.expensetracker.ui.components.Strings
import com.example.expensetracker.ui.components.formatCurrency
import com.example.expensetracker.ui.components.toParsedAmountOrNull

/**
 * V7 Transaction Bottom Action Bar:
 * C4 rule: Save button: full width, solid accent for income/transfer, solid expense token for expense,
 * text >= 4.5:1 contrast, shows the amount.
 */
@Composable
fun TransactionBottomActionBar(
    isExistingTransaction: Boolean,
    isEditMode: Boolean,
    isExpense: Boolean,
    typeAccentColor: Color,
    amountText: String,
    currencySymbol: String,
    strings: Strings,
    isSaving: Boolean = false,
    onEditClick: () -> Unit,
    onCancelEditClick: () -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier,
    isTransfer: Boolean = false
) {
    val appColors = LocalAppColors.current
    val isAmountValid = (amountText.toParsedAmountOrNull() ?: 0.0) > 0.0
    val currentAmountVal = amountText.toParsedAmountOrNull()
    val amountLabel = if (currentAmountVal != null && currentAmountVal > 0.0) {
        " " + formatCurrency(currentAmountVal, currencySymbol, useBengaliNumerals = strings.isBangla)
    } else ""

    val isIncome = !isExpense || typeAccentColor == appColors.income
    val actionContentColor = if (isIncome && appColors.isDark) Color(0xFF052B14) else Color.White

    // Button colors: solid expense for expense, solid accent for income / transfer
    val buttonContainerColor = when {
        isTransfer -> appColors.accent
        isExpense -> appColors.expense
        else -> appColors.accent
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = appColors.surface,
        border = if (appColors.isDark) null else BorderStroke(HairlineInkStroke, appColors.border)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            if (isExistingTransaction && !isEditMode) {
                TransactionViewModeButton(
                    isExpense = isExpense,
                    actionContentColor = actionContentColor,
                    strings = strings,
                    onEditClick = onEditClick
                )
            } else if (isExistingTransaction && isEditMode) {
                TransactionEditModeRow(
                    isExpense = isExpense,
                    isAmountValid = isAmountValid,
                    isSaving = isSaving,
                    amountLabel = amountLabel,
                    buttonContainerColor = buttonContainerColor,
                    actionContentColor = actionContentColor,
                    strings = strings,
                    onCancelEditClick = onCancelEditClick,
                    onSaveClick = onSaveClick
                )
            } else {
                Button(
                    onClick = { onSaveClick() },
                    enabled = isAmountValid && !isSaving,
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = ShapePill,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = buttonContainerColor,
                        contentColor = if (isAmountValid) actionContentColor else actionContentColor.copy(alpha = 0.6f),
                        disabledContainerColor = buttonContainerColor.copy(alpha = 0.45f),
                        disabledContentColor = actionContentColor.copy(alpha = 0.7f)
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = actionContentColor,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = strings.saving,
                            style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp, fontWeight = FontWeight.Bold),
                            color = actionContentColor
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.Check,
                            contentDescription = null,
                            tint = actionContentColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        val saveBtnText = when {
                            isTransfer -> if (amountLabel.isNotBlank()) "${strings.saveTransfer} •$amountLabel" else strings.saveTransfer
                            isExpense -> if (amountLabel.isNotBlank()) "${strings.saveExpense} •$amountLabel" else strings.saveExpense
                            else -> if (amountLabel.isNotBlank()) "${strings.saveIncome} •$amountLabel" else strings.saveIncome
                        }
                        Text(
                            text = saveBtnText,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                fontFeatureSettings = "tnum"
                            ),
                            color = actionContentColor
                        )
                    }
                }
            }
        }
    }
}
