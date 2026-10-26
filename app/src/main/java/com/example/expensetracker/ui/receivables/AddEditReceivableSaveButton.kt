package com.example.expensetracker.ui.receivables

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
import com.example.expensetracker.theme.SmallElementRadius
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.toParsedAmountOrNull
import com.example.expensetracker.theme.LocalAppColors

@Composable
fun AddEditReceivableSaveButton(
    personName: String,
    amountText: String,
    phoneNumber: String,
    dueDateMillis: Long?,
    notes: String,
    deductFromAccount: Boolean,
    selectedAccountId: Long?,
    onValidationError: (String) -> Unit,
    onSave: (String, String, Double, Long?, String, Long?) -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val strings = LocalAppStrings.current
    Button(
        onClick = {
            if (personName.isBlank()) {
                onValidationError(
                    if (strings.isBangla) "অনুগ্রহ করে ব্যক্তির নাম লিখুন" else "Please enter the person's name"
                )
                return@Button
            }
            val amt = amountText.toParsedAmountOrNull()
            if (amt == null || amt <= 0.0) {
                onValidationError(strings.invalidAmountError)
                return@Button
            }
            val deductId = if (deductFromAccount) selectedAccountId else null
            onSave(personName.trim(), phoneNumber.trim(), amt, dueDateMillis, notes.trim(), deductId)
        },
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(SmallElementRadius),
        colors = ButtonDefaults.buttonColors(containerColor = appColors.accent)
    ) {
        Text(
            text = strings.save,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )
    }
}
