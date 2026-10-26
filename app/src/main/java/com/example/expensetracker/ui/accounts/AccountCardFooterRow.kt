package com.example.expensetracker.ui.accounts

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.AccountType
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.AccountBrandStyle
import com.example.expensetracker.ui.components.LocalAppStrings

@Composable
fun AccountCardFooterRow(
    account: Account,
    brandStyle: AccountBrandStyle
) {
    val strings = LocalAppStrings.current
    val appColors = LocalAppColors.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(6.dp),
            color = brandStyle.primaryColor.copy(alpha = 0.15f),
            border = BorderStroke(0.5.dp, brandStyle.primaryColor.copy(alpha = 0.3f))
        ) {
            Text(
                text = when (account.type) {
                    AccountType.BANK -> if (strings.isBangla) "🏦 ব্যাংক অ্যাকাউন্ট" else "🏦 Bank Account"
                    AccountType.MFS -> if (strings.isBangla) "📱 মোবাইল ওয়ালেট" else "📱 Mobile Wallet"
                    AccountType.CASH -> if (strings.isBangla) "💵 ক্যাশ ইন হ্যান্ড" else "💵 Cash Wallet"
                },
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Medium),
                color = if (brandStyle.isDarkCard) brandStyle.primaryColor else appColors.accent,
                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text = if (strings.isBangla) "কার্ড দেখুন" else "View Card",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp, fontWeight = FontWeight.Medium),
                color = appColors.accent
            )
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = null,
                tint = appColors.accent,
                modifier = Modifier.size(11.dp).scale(scaleX = -1f, scaleY = 1f)
            )
        }
    }
}
