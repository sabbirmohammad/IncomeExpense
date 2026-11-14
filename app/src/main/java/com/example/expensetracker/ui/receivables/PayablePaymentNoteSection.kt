package com.example.expensetracker.ui.receivables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.LocalAppStrings

@Composable
fun PayablePaymentNoteSection(
    note: String,
    onNoteChange: (String) -> Unit,
    hasAccounts: Boolean,
    deductFromAccount: Boolean,
    onDeductChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val appColors = LocalAppColors.current

    Column(modifier = modifier.fillMaxWidth()) {
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

        if (hasAccounts) {
            Spacer(modifier = Modifier.height(10.dp))
            PayableDeductAccountToggle(
                checked = deductFromAccount,
                onCheckedChange = onDeductChange,
                isBangla = strings.isBangla
            )
        }
    }
}
