package com.example.expensetracker.ui.accounts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.AccountType
import com.example.expensetracker.ui.components.Strings

@Composable
fun DigitalCardFooterRow(
    account: Account,
    isLightContent: Boolean,
    strings: Strings,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        Column {
            Text(
                text = if (strings.isBangla) "অ্যাকাউন্টের নাম" else "ACCOUNT NAME",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp, letterSpacing = 1.sp),
                color = if (isLightContent) Color.White.copy(alpha = 0.65f) else Color.Black.copy(alpha = 0.55f)
            )
            Text(
                text = account.name.uppercase(),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                ),
                color = if (isLightContent) Color.White else Color.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = if (strings.isBangla) "অ্যাকাউন্ট নম্বর" else "ACCOUNT NO",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp, letterSpacing = 1.sp),
                color = if (isLightContent) Color.White.copy(alpha = 0.65f) else Color.Black.copy(alpha = 0.55f)
            )
            val formattedNum = when {
                account.accountNumber.length >= 8 -> "•••• •••• ${account.accountNumber.takeLast(4)}"
                account.accountNumber.isNotBlank() -> "•••• ${account.accountNumber}"
                account.type == AccountType.CASH -> "CASH WALLET"
                else -> "•••• ${account.id.toString().padStart(4, '0')}"
            }
            Text(
                text = formattedNum,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.2.sp
                ),
                color = if (isLightContent) Color.White else Color.Black
            )
        }
    }
}
