package com.example.expensetracker.ui.receivables

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorBorder
import com.example.expensetracker.theme.ColorDangerExpense
import com.example.expensetracker.theme.ColorSuccessIncome
import com.example.expensetracker.theme.ColorTextMuted
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.theme.ColorTextSecondary
import com.example.expensetracker.ui.components.AppCard
import com.example.expensetracker.ui.components.AppHaptics
import com.example.expensetracker.ui.share.ShareExpenseHelper
import java.util.Locale

@Composable
fun BillSplitterModeToggle(
    isSplitEqually: Boolean,
    isBangla: Boolean,
    onSplitModeChange: (Boolean) -> Unit,
    haptics: AppHaptics,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(ColorBorder.copy(alpha = 0.35f))
            .padding(3.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (isSplitEqually) ColorAccent else Color.Transparent,
            modifier = Modifier
                .weight(1f)
                .clickable {
                    haptics.lightTick()
                    onSplitModeChange(true)
                }
        ) {
            Box(modifier = Modifier.padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                Text(
                    text = if (isBangla) "সমান ভাগ" else "Equal Split",
                    fontSize = 12.sp,
                    fontWeight = if (isSplitEqually) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSplitEqually) Color.White else ColorTextSecondary
                )
            }
        }

        Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (!isSplitEqually) ColorAccent else Color.Transparent,
            modifier = Modifier
                .weight(1f)
                .clickable {
                    haptics.lightTick()
                    onSplitModeChange(false)
                }
        ) {
            Box(modifier = Modifier.padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                Text(
                    text = if (isBangla) "কাস্টম ভাগ" else "Custom Split",
                    fontSize = 12.sp,
                    fontWeight = if (!isSplitEqually) FontWeight.Bold else FontWeight.Normal,
                    color = if (!isSplitEqually) Color.White else ColorTextSecondary
                )
            }
        }
    }
}

@Composable
fun BillSplitterSummaryCard(
    myShareLabel: String,
    myShare: Double,
    friendsCount: Int,
    friendsTotalShare: Double,
    currencySymbol: String,
    isBangla: Boolean,
    modifier: Modifier = Modifier
) {
    AppCard(
        modifier = modifier.fillMaxWidth(),
        padding = 14.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = myShareLabel, color = ColorTextMuted, fontSize = 13.sp)
                Text(
                    text = "$currencySymbol ${String.format(Locale.US, "%,.2f", myShare)}",
                    fontWeight = FontWeight.Bold,
                    color = ColorSuccessIncome,
                    fontSize = 15.sp
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (isBangla) "বন্ধুদের প্রাপ্য ($friendsCount জন)" else "Friends Owe ($friendsCount people)",
                    color = ColorTextMuted,
                    fontSize = 13.sp
                )
                Text(
                    text = "$currencySymbol ${String.format(Locale.US, "%,.2f", friendsTotalShare)}",
                    fontWeight = FontWeight.Bold,
                    color = ColorDangerExpense,
                    fontSize = 15.sp
                )
            }
        }
    }
}

@Composable
fun BillSplitterShareRow(
    context: Context,
    billShareMessage: String,
    copiedText: String,
    isBangla: Boolean,
    haptics: AppHaptics,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = ColorBorder.copy(alpha = 0.4f),
            modifier = Modifier
                .weight(1f)
                .clickable {
                    haptics.lightTick()
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("Bill Split", billShareMessage))
                    Toast.makeText(context, copiedText, Toast.LENGTH_SHORT).show()
                }
        ) {
            Row(
                modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Filled.ContentCopy, contentDescription = null, tint = ColorAccent, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isBangla) "মেসেজ কপি করুন" else "Copy Message",
                    color = ColorTextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Surface(
            shape = RoundedCornerShape(10.dp),
            color = ColorAccent.copy(alpha = 0.18f),
            modifier = Modifier
                .weight(1f)
                .clickable {
                    haptics.lightTick()
                    ShareExpenseHelper.shareText(
                        context,
                        billShareMessage,
                        if (isBangla) "বিল শেয়ার করুন" else "Share Bill Split"
                    )
                }
        ) {
            Row(
                modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Filled.Share, contentDescription = null, tint = ColorAccent, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isBangla) "নোট শেয়ার করুন" else "Share Note",
                    color = ColorTextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
