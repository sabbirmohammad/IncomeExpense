package com.example.expensetracker.ui.accounts

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.expensetracker.ui.components.EmptyState
import com.example.expensetracker.ui.components.LocalAppStrings

@Composable
fun AccountsEmptyState(
    onAddAccountClick: (() -> Unit)? = null
) {
    val strings = LocalAppStrings.current
    EmptyState(
        icon = Icons.Outlined.AccountBalance,
        title = if (strings.isBangla) "কোনো অ্যাকাউন্ট নেই" else "No accounts found",
        body = if (strings.isBangla) "ব্যাংক বা এমএফএস ওয়ালেট যুক্ত করতে 'অ্যাকাউন্ট যোগ করুন' চাপুন।" else "Tap 'Add Account' to add your bank or MFS wallet.",
        buttonText = if (onAddAccountClick != null) strings.addAccount else null,
        onButtonClick = onAddAccountClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp)
    )
}
