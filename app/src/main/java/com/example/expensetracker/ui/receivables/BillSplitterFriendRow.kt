package com.example.expensetracker.ui.receivables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.BillSplitFriend
import com.example.expensetracker.ui.components.AppHaptics
import java.util.Locale
import com.example.expensetracker.theme.LocalAppColors

@Composable
fun BillSplitterFriendRow(
    friend: BillSplitFriend,
    isBangla: Boolean,
    isSplitEqually: Boolean,
    equalShare: Double,
    currencySymbol: String,
    canRemove: Boolean,
    onNameChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onAmountChange: (Double) -> Unit,
    onRemove: () -> Unit,
    haptics: AppHaptics,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = friend.name,
            onValueChange = onNameChange,
            label = { Text(if (isBangla) "বন্ধুর নাম" else "Friend Name") },
            singleLine = true,
            modifier = Modifier.weight(1.2f),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = appColors.accent,
                unfocusedBorderColor = appColors.border
            )
        )

        OutlinedTextField(
            value = friend.phoneNumber,
            onValueChange = onPhoneChange,
            label = { Text(if (isBangla) "ফোন নম্বর (ঐচ্ছিক)" else "Phone (Opt)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.weight(1f),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = appColors.accent,
                unfocusedBorderColor = appColors.border
            )
        )

        if (!isSplitEqually) {
            OutlinedTextField(
                value = if (friend.amount > 0.0) String.format(Locale.US, "%.0f", friend.amount) else "",
                onValueChange = { newAmt ->
                    val parsed = newAmt.toDoubleOrNull() ?: 0.0
                    onAmountChange(parsed)
                },
                label = { Text(if (isBangla) "পরিমাণ" else "Amount") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.width(90.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = appColors.accent,
                    unfocusedBorderColor = appColors.border
                )
            )
        } else {
            Text(
                text = "$currencySymbol ${String.format(Locale.US, "%,.0f", equalShare)}",
                fontWeight = FontWeight.Bold,
                color = appColors.textPrimary,
                fontSize = 13.sp
            )
        }

        if (canRemove) {
            IconButton(
                onClick = {
                    haptics.lightTick()
                    onRemove()
                }
            ) {
                Icon(
                    Icons.Filled.Close,
                    contentDescription = if (isBangla) "মুছে ফেলুন" else "Remove",
                    tint = appColors.expense,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
