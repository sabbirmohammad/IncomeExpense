package com.example.expensetracker.ui.savings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.Calculate
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.SavingsGoal
import com.example.expensetracker.theme.CardCornerRadius
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.SmallElementRadius
import com.example.expensetracker.ui.components.CalculatorPreviewBadge
import com.example.expensetracker.ui.components.ExpandableCalculatorKeypadBar
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.formatCurrency
import com.example.expensetracker.ui.components.toParsedAmountOrNull
import java.util.Locale

/**
 * Add Contribution to Savings Goal Bottom Sheet
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSavingsContributionSheet(
    goal: SavingsGoal,
    accounts: List<Account>,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onConfirm: (Double, Long?) -> Unit
) {
    val strings = LocalAppStrings.current
    val appColors = LocalAppColors.current
    var amountText by remember { mutableStateOf("") }
    var deductFromAccount by remember { mutableStateOf(false) }
    var selectedAccountId by remember { mutableStateOf(accounts.firstOrNull()?.id) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showCalculator by remember { mutableStateOf(false) }
    val hasOperators = remember(amountText) {
        amountText.any { it in listOf('+', '-', '*', '/', '×', '÷', '−', '—') }
    }
    val goalColor = remember(goal.colorHex) { parseGoalColor(goal.colorHex) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = appColors.surface,
        shape = RoundedCornerShape(topStart = CardCornerRadius, topEnd = CardCornerRadius)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 640.dp)
                .navigationBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = strings.addSavingsContribution,
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp, fontWeight = FontWeight.SemiBold),
                    color = appColors.textPrimary
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Outlined.Close, null, tint = appColors.textSecondary)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Subtitle with goal target
            Text(
                text = "${goal.title} • ${if (strings.isBangla) "বাকি" else "Remaining"}: ${formatCurrency(goal.remainingAmount, currencySymbol)}",
                fontSize = 12.sp,
                color = appColors.textSecondary
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Amount Field
            Text("${strings.amount} ($currencySymbol)", fontSize = 12.sp, color = appColors.textSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = amountText,
                onValueChange = {
                    amountText = it.filter { c -> c.isDigit() || c == '.' || c in "+-×÷/* " }
                    if (errorMessage != null) errorMessage = null
                },
                placeholder = { Text(if (strings.isBangla) "০.০০" else "0.00", color = appColors.textMuted) },
                trailingIcon = {
                    IconButton(onClick = { showCalculator = !showCalculator }) {
                        Icon(
                            imageVector = Icons.Filled.Calculate,
                            contentDescription = "Calculator",
                            tint = if (showCalculator || hasOperators) goalColor else appColors.textSecondary
                        )
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(SmallElementRadius),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = appColors.surfaceVariant,
                    unfocusedContainerColor = appColors.surfaceVariant,
                    focusedBorderColor = goalColor,
                    unfocusedBorderColor = appColors.border,
                    focusedTextColor = appColors.textPrimary,
                    unfocusedTextColor = appColors.textPrimary
                ),
                modifier = Modifier.fillMaxWidth()
            )

            CalculatorPreviewBadge(
                expression = amountText,
                currencySymbol = currencySymbol,
                onApply = { evaluated ->
                    targetEvaluated(evaluated, onUpdate = { amountText = it })
                    showCalculator = false
                }
            )

            ExpandableCalculatorKeypadBar(
                expanded = showCalculator,
                currentExpression = amountText,
                onExpressionChanged = {
                    amountText = it
                    if (errorMessage != null) errorMessage = null
                },
                onApplyResult = { evaluated ->
                    targetEvaluated(evaluated, onUpdate = { amountText = it })
                    showCalculator = false
                },
                accentColor = goalColor
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Quick Shortcut Chips
            ContributionQuickChips(
                amountText = amountText,
                remainingAmount = goal.remainingAmount,
                goalColor = goalColor,
                onAmountSelected = { amountText = it }
            )

            if (accounts.isNotEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))
                ContributionAccountSelector(
                    accounts = accounts,
                    deductFromAccount = deductFromAccount,
                    selectedAccountId = selectedAccountId,
                    goalColor = goalColor,
                    currencySymbol = currencySymbol,
                    onDeductChanged = { deductFromAccount = it },
                    onAccountSelected = { selectedAccountId = it }
                )
            }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(errorMessage!!, color = appColors.expense, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    val amt = amountText.toParsedAmountOrNull()
                    if (amt == null || amt <= 0.0) {
                        errorMessage = strings.invalidAmountError
                        return@Button
                    }
                    val fromAccId = if (deductFromAccount) selectedAccountId else null
                    onConfirm(amt, fromAccId)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(SmallElementRadius),
                colors = ButtonDefaults.buttonColors(containerColor = goalColor)
            ) {
                Text(
                    text = strings.confirm,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

private fun targetEvaluated(evaluated: Double, onUpdate: (String) -> Unit) {
    val text = if (evaluated % 1.0 == 0.0) evaluated.toLong().toString()
    else String.format(Locale.US, "%.2f", evaluated)
    onUpdate(text)
}
