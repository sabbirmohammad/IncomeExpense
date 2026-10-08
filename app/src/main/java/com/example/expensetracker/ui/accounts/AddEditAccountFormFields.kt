package com.example.expensetracker.ui.accounts

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
import com.example.expensetracker.data.model.AccountType
import com.example.expensetracker.ui.components.CalculatorKeypadBar
import com.example.expensetracker.ui.components.CalculatorPreviewBadge
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorBackground
import com.example.expensetracker.theme.ColorBorder
import com.example.expensetracker.theme.ColorTextMuted
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.theme.ColorTextSecondary
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.theme.SmallElementRadius
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
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(strings.accountName, fontSize = 12.sp, color = ColorTextSecondary)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            placeholder = { Text(if (strings.isBangla) "যেমন: ব্র্যাক স্যালারি অ্যাকাউন্ট, বিকাশ পার্সোনাল" else "e.g. Brac Salary Account, bKash Personal", color = ColorTextMuted) },
            singleLine = true,
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

        Spacer(modifier = Modifier.height(12.dp))

        if (type != AccountType.CASH) {
            Text(strings.institutionName, fontSize = 12.sp, color = ColorTextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = institution,
                onValueChange = onInstitutionChange,
                placeholder = { Text(if (strings.isBangla) "যেমন: ডাচ-বাংলা ব্যাংক, বিকাশ" else "e.g. Dutch-Bangla Bank, bKash", color = ColorTextMuted) },
                singleLine = true,
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

            Spacer(modifier = Modifier.height(12.dp))

            val accNumberLabel = if (strings.isBangla) {
                if (type == AccountType.BANK) "অ্যাকাউন্ট বা কার্ড নম্বর (ঐচ্ছিক)" else "মোবাইল বা ওয়ালেট নম্বর (ঐচ্ছিক)"
            } else {
                if (type == AccountType.BANK) "Account Number or Card (Optional)" else "Phone or Wallet Number (Optional)"
            }
            Text(accNumberLabel, fontSize = 12.sp, color = ColorTextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = accountNumber,
                onValueChange = onAccountNumberChange,
                placeholder = { Text(if (strings.isBangla) (if (type == AccountType.BANK) "উদাঃ ১৫৮৯ ..." else "উদাঃ ০১৭XXXXXXXX") else (if (type == AccountType.BANK) "e.g. 1589 ..." else "e.g. 017XXXXXXXX"), color = ColorTextMuted) },
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

            Spacer(modifier = Modifier.height(12.dp))
        }

        Text("${strings.initialBalance} ($currencySymbol)", fontSize = 12.sp, color = ColorTextSecondary)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = initialBalanceText,
            onValueChange = { onInitialBalanceChange(it.filter { c -> c.isDigit() || c == '.' || c in "+-×÷/* " }) },
            placeholder = { Text(if (strings.isBangla) "০.০০" else "0.00", color = ColorTextMuted) },
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
            expression = initialBalanceText,
            currencySymbol = currencySymbol,
            onApply = { evaluated ->
                onInitialBalanceChange(if (evaluated % 1.0 == 0.0) evaluated.toLong().toString() else String.format(Locale.US, "%.2f", evaluated))
            }
        )

        Spacer(modifier = Modifier.height(4.dp))

        CalculatorKeypadBar(
            currentExpression = initialBalanceText,
            onExpressionChanged = onInitialBalanceChange,
            onApplyResult = { evaluated ->
                onInitialBalanceChange(if (evaluated % 1.0 == 0.0) evaluated.toLong().toString() else String.format(Locale.US, "%.2f", evaluated))
            }
        )
    }
}
