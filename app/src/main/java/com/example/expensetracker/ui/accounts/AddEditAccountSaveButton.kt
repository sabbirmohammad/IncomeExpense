package com.example.expensetracker.ui.accounts

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.AccountType
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.SmallElementRadius
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.toParsedAmountOrNull

@Composable
fun AddEditAccountSaveButton(
    account: Account?,
    name: String,
    type: AccountType,
    institution: String,
    accountNumber: String,
    initialBalanceText: String,
    selectedColor: String,
    onError: (String) -> Unit,
    onSave: (Account) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val appColors = LocalAppColors.current

    Button(
        onClick = {
            if (name.isBlank()) {
                onError(if (strings.isBangla) "অনুগ্রহ করে অ্যাকাউন্টের নাম লিখুন" else "Please enter an account name")
                return@Button
            }
            val initialBal = initialBalanceText.toParsedAmountOrNull() ?: 0.0
            val updated = (account ?: Account(name = name, type = type)).copy(
                name = name.trim(),
                type = type,
                institutionName = institution.trim(),
                accountNumber = accountNumber.trim(),
                initialBalance = initialBal,
                colorHex = selectedColor
            )
            onSave(updated)
        },
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(SmallElementRadius),
        colors = ButtonDefaults.buttonColors(containerColor = appColors.accent)
    ) {
        Text(
            text = if (account != null) strings.updateAccount else strings.saveAccount,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )
    }
}
