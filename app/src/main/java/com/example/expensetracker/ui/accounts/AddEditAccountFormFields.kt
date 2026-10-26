package com.example.expensetracker.ui.accounts

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.AccountType
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.SmallElementRadius
import com.example.expensetracker.ui.components.CalculatorPreviewBadge
import com.example.expensetracker.ui.components.ExpandableCalculatorKeypadBar
import com.example.expensetracker.ui.components.LocalAppStrings
import java.util.Locale

@Composable
fun AddEditAccountFormFields(
    name: String,
    onNameChange: (String) -> Unit,
    institution: String,
    onInstitutionChange: (String) -> Unit,
    accountNumber: String,
    onAccountNumberChange: (String) -> Unit,
    initialBalanceText: String,
    onInitialBalanceChange: (String) -> Unit,
    type: AccountType,
    currencySymbol: String
) {
    val strings = LocalAppStrings.current
    val appColors = LocalAppColors.current

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = appColors.surfaceVariant,
        unfocusedContainerColor = appColors.surfaceVariant,
        focusedBorderColor = appColors.accent,
        unfocusedBorderColor = appColors.border,
        focusedTextColor = appColors.textPrimary,
        unfocusedTextColor = appColors.textPrimary
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(strings.accountName, fontSize = 12.sp, color = appColors.textSecondary)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            placeholder = { Text(if (strings.isBangla) "যেমন: ব্র্যাক স্যালারি অ্যাকাউন্ট, বিকাশ পার্সোনাল" else "e.g. Brac Salary Account, bKash Personal", color = appColors.textMuted) },
            singleLine = true,
            shape = RoundedCornerShape(SmallElementRadius),
            colors = textFieldColors,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (type != AccountType.CASH) {
            Text(strings.institutionName, fontSize = 12.sp, color = appColors.textSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = institution,
                onValueChange = onInstitutionChange,
                placeholder = { Text(if (strings.isBangla) "যেমন: ডাচ-বাংলা ব্যাংক, বিকাশ" else "e.g. Dutch-Bangla Bank, bKash", color = appColors.textMuted) },
                singleLine = true,
                shape = RoundedCornerShape(SmallElementRadius),
                colors = textFieldColors,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            val accNumberLabel = if (strings.isBangla) {
                if (type == AccountType.BANK) "অ্যাকাউন্ট বা কার্ড নম্বর (ঐচ্ছিক)" else "মোবাইল বা ওয়ালেট নম্বর (ঐচ্ছিক)"
            } else {
                if (type == AccountType.BANK) "Account Number or Card (Optional)" else "Phone or Wallet Number (Optional)"
            }
            Text(accNumberLabel, fontSize = 12.sp, color = appColors.textSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = accountNumber,
                onValueChange = onAccountNumberChange,
                placeholder = { Text(if (strings.isBangla) (if (type == AccountType.BANK) "উদাঃ ১৫৮৯ ..." else "উদাঃ ০১৭XXXXXXXX") else (if (type == AccountType.BANK) "e.g. 1589 ..." else "e.g. 017XXXXXXXX"), color = appColors.textMuted) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(SmallElementRadius),
                colors = textFieldColors,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))
        }

        var showCalculator by remember { mutableStateOf(false) }
        val hasOperators = remember(initialBalanceText) {
            initialBalanceText.any { it in listOf('+', '-', '*', '/', '×', '÷', '−', '—') }
        }

        Text("${strings.initialBalance} ($currencySymbol)", fontSize = 12.sp, color = appColors.textSecondary)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = initialBalanceText,
            onValueChange = { onInitialBalanceChange(it.filter { c -> c.isDigit() || c == '.' || c in "+-×÷/* " }) },
            placeholder = { Text(if (strings.isBangla) "০.০০" else "0.00", color = appColors.textMuted) },
            trailingIcon = {
                IconButton(onClick = { showCalculator = !showCalculator }) {
                    Icon(
                        imageVector = Icons.Filled.Calculate,
                        contentDescription = "Calculator",
                        tint = if (showCalculator || hasOperators) appColors.accent else appColors.textSecondary
                    )
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(SmallElementRadius),
            colors = textFieldColors,
            modifier = Modifier.fillMaxWidth()
        )

        CalculatorPreviewBadge(
            expression = initialBalanceText,
            currencySymbol = currencySymbol,
            onApply = { evaluated ->
                onInitialBalanceChange(if (evaluated % 1.0 == 0.0) evaluated.toLong().toString() else String.format(Locale.US, "%.2f", evaluated))
                showCalculator = false
            }
        )

        ExpandableCalculatorKeypadBar(
            expanded = showCalculator,
            currentExpression = initialBalanceText,
            onExpressionChanged = onInitialBalanceChange,
            onApplyResult = { evaluated ->
                onInitialBalanceChange(if (evaluated % 1.0 == 0.0) evaluated.toLong().toString() else String.format(Locale.US, "%.2f", evaluated))
                showCalculator = false
            }
        )
    }
}
