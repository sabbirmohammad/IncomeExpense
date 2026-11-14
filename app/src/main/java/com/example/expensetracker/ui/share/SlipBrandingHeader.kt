package com.example.expensetracker.ui.share

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.Strings

@Composable
fun SlipBrandingHeader(
    userName: String,
    strings: Strings,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(appColors.accent.copy(alpha = 0.15f))
                    .border(1.dp, appColors.accent.copy(alpha = 0.4f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.ReceiptLong,
                    contentDescription = null,
                    tint = appColors.accent,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column {
                Text(
                    text = if (strings.isBangla) strings.appName else "Expense Tracker",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = appColors.textPrimary
                )
                Text(
                    text = if (userName.isNotBlank()) (if (strings.isBangla) "$userName-এর খাতা" else "$userName's Ledger") else (if (strings.isBangla) "আর্থিক স্লিপ" else "Financial Slip"),
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = appColors.textSecondary
                )
            }
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(if (appColors.isDark) Color(0xFF1F283E) else appColors.accent.copy(alpha = 0.12f))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                text = if (strings.isBangla) "বিবরণী" else "STATEMENT",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = appColors.accent
            )
        }
    }
}
