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
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.SmallElementRadius
import com.example.expensetracker.ui.components.CalculatorEvaluator
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.rememberAppHaptics

@Composable
fun AddEditPayableSaveButton(
    personName: String,
    phoneNumber: String,
    amountText: String,
    dueDateMillis: Long?,
    notes: String,
    depositToAccount: Boolean,
    selectedAccountId: Long?,
    onErrorMessage: (String) -> Unit,
    onSave: (personName: String, phoneNumber: String, amount: Double, dueDateMillis: Long?, notes: String, depositToAccountId: Long?) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val haptics = rememberAppHaptics()

    Button(
        onClick = {
            haptics.mediumClick()
            val validAmount = CalculatorEvaluator.evaluate(amountText)
            if (personName.isBlank()) {
                onErrorMessage(if (strings.isBangla) "পাওনাদারের নাম লিখুন" else "Enter lender name")
                return@Button
            }
            if (validAmount == null || validAmount <= 0.0) {
                onErrorMessage(strings.invalidAmountError)
                return@Button
            }

            onSave(
                personName.trim(),
                phoneNumber.trim(),
                validAmount,
                dueDateMillis,
                notes.trim(),
                if (depositToAccount) selectedAccountId else null
            )
        },
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(SmallElementRadius),
        colors = ButtonDefaults.buttonColors(containerColor = ColorAccent)
    ) {
        Text(
            text = strings.save,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = Color.White
        )
    }
}
