package com.example.expensetracker.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.AccountType
import com.example.expensetracker.data.model.getLocalizedAccountName
import com.example.expensetracker.theme.HairlineInkStroke
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.RadiusS

@Composable
fun AccountChipRow(
    accounts: List<Account>,
    selectedAccountId: Long?,
    onAccountSelected: (Account) -> Unit,
    modifier: Modifier = Modifier,
    isTransfer: Boolean = false,
    toAccountId: Long? = null,
    onToAccountSelected: ((Account) -> Unit)? = null,
    amount: Double? = null,
    isExpense: Boolean = true,
    currencySymbol: String = "৳", isBangla: Boolean = false, isPrivacyMode: Boolean = false,
    haptics: AppHaptics = rememberAppHaptics()
) {
    val appColors = LocalAppColors.current

    val selectedAccount = remember(accounts, selectedAccountId) {
        accounts.find { it.id == selectedAccountId } ?: accounts.firstOrNull()
    }
    val targetToAccount = remember(accounts, toAccountId) {
        accounts.find { it.id == toAccountId } ?: accounts.getOrNull(1) ?: accounts.firstOrNull()
    }

    val isInsufficientBalance = remember(selectedAccount, amount, isExpense, isTransfer) {
        val checkAccount = selectedAccount
        if (checkAccount != null && amount != null && amount > 0.0 && (isExpense || isTransfer)) {
            checkAccount.currentBalance < amount
        } else false
    }

    Column(modifier = modifier.fillMaxWidth()) {
        if (!isTransfer) {
            val scrollState = rememberScrollState()
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(scrollState),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                accounts.forEach { account ->
                    AccountChipItem(
                        account = account,
                        isSelected = account.id == selectedAccount?.id,
                        currencySymbol = currencySymbol,
                        isBangla = isBangla,
                        isPrivacyMode = isPrivacyMode,
                        onClick = { haptics.lightTick(); onAccountSelected(account) }
                    )
                }
            }
        } else {
            AccountChipTransferSection(
                accounts = accounts,
                selectedAccount = selectedAccount,
                targetToAccount = targetToAccount,
                currencySymbol = currencySymbol,
                isBangla = isBangla,
                isPrivacyMode = isPrivacyMode,
                onAccountSelected = onAccountSelected,
                onToAccountSelected = onToAccountSelected
            )
        }

        AnimatedVisibility(visible = isInsufficientBalance, enter = fadeIn(), exit = fadeOut()) {
            val balText = selectedAccount?.currentBalance?.let {
                if (isPrivacyMode) "••••" else formatCurrency(it, currencySymbol, useBengaliNumerals = isBangla)
            } ?: ""
            Surface(
                shape = RoundedCornerShape(RadiusS),
                color = appColors.warningContainer,
                border = BorderStroke(HairlineInkStroke, appColors.warning.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(imageVector = Icons.Outlined.Warning, contentDescription = null, tint = appColors.warning, modifier = Modifier.size(16.dp))
                    Text(
                        text = if (isBangla) "সতর্কতা: নির্বাচিত অ্যাকাউন্টের ব্যালেন্স ($balText) খরচের পরিমাণের চেয়ে কম।"
                               else "Warning: Account balance ($balText) is less than amount.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, fontWeight = FontWeight.Medium, fontFeatureSettings = "tnum"),
                        color = appColors.textPrimary
                    )
                }
            }
        }
    }
}

@Composable
internal fun AccountChipItem(
    account: Account,
    isSelected: Boolean,
    currencySymbol: String,
    isBangla: Boolean,
    isPrivacyMode: Boolean,
    onClick: () -> Unit
) {
    val appColors = LocalAppColors.current
    val localizedName = remember(account.name, isBangla) { getLocalizedAccountName(account.name, isBangla) }
    val balanceText = remember(account.currentBalance, isPrivacyMode, isBangla, currencySymbol) {
        if (isPrivacyMode) "••••" else formatCurrency(account.currentBalance, currencySymbol, useBengaliNumerals = isBangla)
    }

    val icon: ImageVector = when (account.type) {
        AccountType.BANK -> Icons.Outlined.AccountBalance
        AccountType.MFS -> Icons.Outlined.PhoneAndroid
        AccountType.CASH -> Icons.Outlined.Payments
    }

    val backgroundColor = if (isSelected) appColors.accentContainer else appColors.surfaceVariant
    val borderStroke = if (isSelected) BorderStroke(1.5.dp, appColors.accent) else if (appColors.isDark) null else BorderStroke(HairlineInkStroke, appColors.border)
    val contentColor = if (isSelected) appColors.textPrimary else appColors.textSecondary

    Surface(
        shape = RoundedCornerShape(RadiusS),
        color = backgroundColor,
        border = borderStroke,
        modifier = Modifier
            .defaultMinSize(minHeight = 44.dp)
            .minimumInteractiveComponentSize()
            .clickable { onClick() }
            .semantics {
                role = Role.RadioButton
                selected = isSelected
                contentDescription = "$localizedName, balance $balanceText, ${if (isSelected) "selected" else "not selected"}"
            }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = if (isSelected) appColors.accent else appColors.textSecondary, modifier = Modifier.size(18.dp))
            Column {
                Text(
                    text = localizedName,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium, fontSize = 13.sp),
                    color = contentColor
                )
                Text(
                    text = balanceText,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal, fontSize = 11.5.sp, fontFeatureSettings = "tnum"),
                    color = if (isSelected) appColors.accent else appColors.textSecondary
                )
            }
        }
    }
}
