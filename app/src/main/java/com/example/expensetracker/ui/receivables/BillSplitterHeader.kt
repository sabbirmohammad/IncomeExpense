package com.example.expensetracker.ui.receivables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.ui.components.AppHaptics
import com.example.expensetracker.ui.components.convertToBengaliDigits
import java.util.Locale
import com.example.expensetracker.theme.LocalAppColors

@Composable
fun BillSplitterHeader(
    title: String,
    onClose: () -> Unit,
    closeDesc: String,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(appColors.accent.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Group,
                    contentDescription = null,
                    tint = appColors.accent,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                ),
                color = appColors.textPrimary
            )
        }
        IconButton(onClick = onClose) {
            Icon(Icons.Filled.Close, contentDescription = closeDesc, tint = appColors.textMuted)
        }
    }
}

@Composable
fun BillSplitterFriendsSectionHeader(
    isBangla: Boolean,
    addFriendLabel: String,
    onAddFriend: () -> Unit,
    haptics: AppHaptics,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = if (isBangla) "বিল শেয়ারকারী বন্ধুরা" else "FRIENDS SHARING BILL",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = appColors.textMuted,
            letterSpacing = 1.sp
        )
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = appColors.accent.copy(alpha = 0.2f),
            modifier = Modifier.clickable {
                haptics.lightTick()
                onAddFriend()
            }
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = null,
                    tint = appColors.accent,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = addFriendLabel,
                    color = appColors.accent,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

fun createBillShareMessage(
    isBangla: Boolean,
    note: String,
    totalBill: Double,
    equalShare: Double,
    currencySymbol: String
): String {
    return if (isBangla) {
        val totalStr = convertToBengaliDigits(String.format(Locale.US, "%,.0f", totalBill))
        val shareStr = convertToBengaliDigits(String.format(Locale.US, "%,.0f", equalShare))
        "হেই! ${note.ifBlank { "আমাদের আড্ডা" }}-র জন্য মোট বিল হয়েছে $currencySymbol $totalStr। তোমার অংশ $currencySymbol $shareStr। সুবিধাজনক সময়ে পাঠিয়ে দিও!"
    } else {
        "Hey! For ${note.ifBlank { "our hangout" }}, total bill was $currencySymbol ${String.format(Locale.US, "%,.0f", totalBill)}. Your share is $currencySymbol ${String.format(Locale.US, "%,.0f", equalShare)}. Please send your share when convenient!"
    }
}
