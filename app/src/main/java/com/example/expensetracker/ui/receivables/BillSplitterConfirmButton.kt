package com.example.expensetracker.ui.receivables

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.BillSplitData
import com.example.expensetracker.data.model.BillSplitFriend
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.ui.components.AppHaptics

@Composable
fun BillSplitterConfirmButton(
    totalBill: Double,
    selectedAccount: Account?,
    defaultCategory: Category?,
    myShare: Double,
    friends: List<BillSplitFriend>,
    isSplitEqually: Boolean,
    equalShare: Double,
    note: String,
    isBangla: Boolean,
    buttonText: String,
    onError: (String) -> Unit,
    onSuccess: (BillSplitData) -> Unit,
    haptics: AppHaptics,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = {
            if (totalBill <= 0.0) {
                onError(if (isBangla) "অনুগ্রহ করে মোট বিলের পরিমাণ লিখুন" else "Please enter total bill amount")
                return@Button
            }
            if (defaultCategory == null) {
                onError(if (isBangla) "কোনো ক্যাটাগরি পাওয়া যায়নি" else "No category available")
                return@Button
            }
            val splitFriends = friends.map { f ->
                f.copy(amount = if (isSplitEqually) equalShare else f.amount)
            }
            if (!isSplitEqually && splitFriends.sumOf { it.amount } > totalBill) {
                onError(if (isBangla) "বন্ধুদের প্রাপ্য মোট বিলের পরিমাণের চেয়ে বেশি হতে পারে না" else "Friends' shares cannot exceed total bill")
                return@Button
            }
            haptics.successFeedback()
            onSuccess(
                BillSplitData(
                    totalAmount = totalBill,
                    payingAccountId = selectedAccount?.id,
                    payingAccountName = selectedAccount?.name,
                    categoryId = defaultCategory.id,
                    categoryName = defaultCategory.name,
                    myShare = myShare,
                    friends = splitFriends,
                    note = note.trim()
                )
            )
        },
        modifier = modifier.fillMaxWidth().height(48.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = ColorAccent)
    ) {
        Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Color.White)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = buttonText,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = Color.White
        )
    }
}
