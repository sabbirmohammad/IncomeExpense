package com.example.expensetracker.ui.receivables

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.AppHaptics
import com.example.expensetracker.ui.share.ShareExpenseHelper

@Composable
fun BillSplitterShareRow(
    context: Context,
    billShareMessage: String,
    copiedText: String,
    isBangla: Boolean,
    haptics: AppHaptics,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = appColors.border.copy(alpha = 0.4f),
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
                Icon(Icons.Filled.ContentCopy, contentDescription = null, tint = appColors.accent, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isBangla) "মেসেজ কপি করুন" else "Copy Message",
                    color = appColors.textPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Surface(
            shape = RoundedCornerShape(10.dp),
            color = appColors.accent.copy(alpha = 0.18f),
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
                Icon(Icons.Filled.Share, contentDescription = null, tint = appColors.accent, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isBangla) "নোট শেয়ার করুন" else "Share Note",
                    color = appColors.textPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
