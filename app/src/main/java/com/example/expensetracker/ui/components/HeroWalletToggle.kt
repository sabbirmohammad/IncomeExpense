package com.example.expensetracker.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.LocalAppColors

@Composable
fun HeroWalletToggle(
    spendingWalletBalance: Double?,
    onToggleWallet: (() -> Unit)?,
    isPrivacyMode: Boolean,
    currencySymbol: String,
    useBengaliNumerals: Boolean,
    modifier: Modifier = Modifier
) {
    if (spendingWalletBalance == null || onToggleWallet == null) return

    val appColors = LocalAppColors.current
    val strings = LocalAppStrings.current

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .clickable { onToggleWallet() }
            .padding(horizontal = 4.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        val walletDisplay = if (isPrivacyMode) "••••" else formatCurrency(spendingWalletBalance, currencySymbol, useBengaliNumerals)
        Text(
            text = "$walletDisplay ${strings.spendingWalletSuffix}",
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 11.sp,
                fontFeatureSettings = "tnum"
            ),
            color = appColors.textMuted
        )
        Icon(
            imageVector = Icons.Outlined.SwapHoriz,
            contentDescription = "Switch wallet",
            tint = appColors.textMuted,
            modifier = Modifier.size(13.dp)
        )
    }
}
