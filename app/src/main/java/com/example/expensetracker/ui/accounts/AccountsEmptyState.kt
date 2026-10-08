package com.example.expensetracker.ui.accounts

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.ui.components.AppCard
import com.example.expensetracker.theme.ColorTextMuted
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.theme.ColorTextSecondary
import com.example.expensetracker.ui.components.LocalAppStrings

@Composable
fun AccountsEmptyState() {
    val strings = LocalAppStrings.current
    AppCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        padding = 24.dp
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Outlined.AccountBalance, null, tint = ColorTextMuted, modifier = Modifier.size(36.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (strings.isBangla) "কোনো অ্যাকাউন্ট পাওয়া যায়নি" else "No accounts found",
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
                color = ColorTextPrimary
            )
            Text(
                text = if (strings.isBangla) "ব্যাংক বা এমএফএস ওয়ালেট যুক্ত করতে 'অ্যাকাউন্ট যোগ করুন' চাপুন।" else "Tap 'Add Account' to add your bank or MFS wallet.",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = ColorTextSecondary
            )
        }
    }
}
