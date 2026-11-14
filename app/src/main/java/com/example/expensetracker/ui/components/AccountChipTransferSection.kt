package com.example.expensetracker.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.theme.LocalAppColors

@Composable
internal fun AccountChipTransferSection(
    accounts: List<Account>,
    selectedAccount: Account?,
    targetToAccount: Account?,
    currencySymbol: String,
    isBangla: Boolean,
    isPrivacyMode: Boolean,
    onAccountSelected: (Account) -> Unit,
    onToAccountSelected: ((Account) -> Unit)?
) {
    val appColors = LocalAppColors.current
    val haptics = rememberAppHaptics()

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // From Account Selection
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = if (isBangla) "উৎস অ্যাকাউন্ট (From):" else "From Account:",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                color = appColors.textSecondary
            )
            val fromScrollState = rememberScrollState()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(fromScrollState),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                accounts.forEach { account ->
                    AccountChipItem(
                        account = account,
                        isSelected = account.id == selectedAccount?.id,
                        currencySymbol = currencySymbol,
                        isBangla = isBangla,
                        isPrivacyMode = isPrivacyMode,
                        onClick = {
                            haptics.lightTick()
                            onAccountSelected(account)
                            if (account.id == targetToAccount?.id && onToAccountSelected != null) {
                                val other = accounts.firstOrNull { it.id != account.id }
                                if (other != null) onToAccountSelected(other)
                            }
                        }
                    )
                }
            }
        }

        // Transfer arrow indicator
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                contentDescription = "Transfer to",
                tint = appColors.accent,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = if (isBangla) "গন্তব্য অ্যাকাউন্ট (To):" else "To Account:",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                color = appColors.textSecondary
            )
        }

        // To Account Selection
        val toScrollState = rememberScrollState()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(toScrollState),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            accounts.forEach { account ->
                AccountChipItem(
                    account = account,
                    isSelected = account.id == targetToAccount?.id,
                    currencySymbol = currencySymbol,
                    isBangla = isBangla,
                    isPrivacyMode = isPrivacyMode,
                    onClick = {
                        haptics.lightTick()
                        onToAccountSelected?.invoke(account)
                        if (account.id == selectedAccount?.id) {
                            val other = accounts.firstOrNull { it.id != account.id }
                            if (other != null) onAccountSelected(other)
                        }
                    }
                )
            }
        }
    }
}
