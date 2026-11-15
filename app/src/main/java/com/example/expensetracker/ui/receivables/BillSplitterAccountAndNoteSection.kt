package com.example.expensetracker.ui.receivables

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.AppHaptics
import com.example.expensetracker.ui.components.LocalAppStrings

@Composable
fun BillSplitterAccountAndNoteSection(
    accounts: List<Account>,
    selectedAccount: Account?,
    currencySymbol: String,
    onAccountSelect: (Account) -> Unit,
    note: String,
    onNoteChange: (String) -> Unit,
    haptics: AppHaptics
) {
    val strings = LocalAppStrings.current
    val appColors = LocalAppColors.current

    Text(text = strings.payingAccount, fontSize = 12.sp, color = appColors.textMuted)
    Spacer(modifier = Modifier.height(6.dp))
    BillSplitterAccountPicker(
        accounts = accounts,
        selectedAccount = selectedAccount,
        currencySymbol = currencySymbol,
        onAccountSelect = onAccountSelect,
        haptics = haptics
    )

    Spacer(modifier = Modifier.height(14.dp))

    OutlinedTextField(
        value = note,
        onValueChange = onNoteChange,
        label = { Text(strings.note) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = appColors.accent,
            unfocusedBorderColor = appColors.border
        )
    )
}
