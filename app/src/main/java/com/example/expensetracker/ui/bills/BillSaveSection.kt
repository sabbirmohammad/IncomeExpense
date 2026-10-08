package com.example.expensetracker.ui.bills

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.BillFrequency
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.data.model.RecurringBill
import com.example.expensetracker.data.model.calculateInitialDueDate
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorDangerExpense
import com.example.expensetracker.ui.components.AppHaptics
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.toParsedAmountOrNull

@Composable
fun BillSaveSection(
    title: String,
    amountText: String,
    dueDayText: String,
    selectedCategory: Category?,
    selectedAccount: Account?,
    frequency: BillFrequency,
    notes: String,
    billToEdit: RecurringBill?,
    errorMessage: String?,
    onErrorMessageChange: (String?) -> Unit,
    haptics: AppHaptics,
    onSave: (RecurringBill) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current

    Column(modifier = modifier.fillMaxWidth()) {
        if (errorMessage != null) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = ColorDangerExpense.copy(alpha = 0.15f),
                border = BorderStroke(1.dp, ColorDangerExpense.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = errorMessage,
                    color = ColorDangerExpense,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        Button(
            onClick = {
                val amount = amountText.toParsedAmountOrNull()
                val day = dueDayText.toIntOrNull() ?: 1

                if (title.isBlank()) {
                    onErrorMessageChange(if (strings.isBangla) "অনুগ্রহ করে বিলের নাম লিখুন" else "Please enter a bill title")
                    return@Button
                }
                if (amount == null || amount <= 0.0) {
                    onErrorMessageChange(if (strings.isBangla) "সঠিক পরিমাণ লিখুন" else "Please enter a valid amount")
                    return@Button
                }
                if (selectedCategory == null) {
                    onErrorMessageChange(if (strings.isBangla) "অনুগ্রহ করে ক্যাটাগরি নির্বাচন করুন" else "Please select a category")
                    return@Button
                }

                haptics.successFeedback()
                val clampedDay = day.coerceIn(1, 31)
                val nextDue = if (billToEdit != null) {
                    if (billToEdit.dueDay != clampedDay || billToEdit.frequency != frequency) {
                        calculateInitialDueDate(frequency, clampedDay)
                    } else {
                        billToEdit.nextDueDateMillis
                    }
                } else {
                    calculateInitialDueDate(frequency, clampedDay)
                }

                val bill = RecurringBill(
                    id = billToEdit?.id ?: 0,
                    title = title.trim(),
                    amount = amount,
                    categoryId = selectedCategory.id,
                    categoryName = selectedCategory.name,
                    categoryIcon = selectedCategory.icon,
                    categoryColor = selectedCategory.colorHex,
                    accountId = selectedAccount?.id,
                    accountName = selectedAccount?.name,
                    frequency = frequency,
                    dueDay = clampedDay,
                    nextDueDateMillis = nextDue,
                    lastPaidDateMillis = billToEdit?.lastPaidDateMillis,
                    notes = notes.trim()
                )
                onSave(bill)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ColorAccent)
        ) {
            Text(text = strings.save, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color.White)
        }
    }
}
