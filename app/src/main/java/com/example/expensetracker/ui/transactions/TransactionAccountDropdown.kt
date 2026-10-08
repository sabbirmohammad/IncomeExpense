package com.example.expensetracker.ui.transactions

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.Payment
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.PaymentMethod
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.theme.ColorTextSecondary
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.BrandIdentityHelper
import com.example.expensetracker.ui.components.Strings
import com.example.expensetracker.ui.components.formatCurrency
import com.example.expensetracker.ui.components.rememberAppHaptics
import com.example.expensetracker.ui.components.toParsedAmountOrNull

@Composable
fun TransactionAccountDropdown(
    accounts: List<Account>,
    selectedAccountId: Long?,
    onSelectAccount: (Account, PaymentMethod) -> Unit,
    selectedPaymentMethod: PaymentMethod,
    onSelectPaymentMethod: (PaymentMethod) -> Unit,
    showDropdown: Boolean,
    onShowDropdownChange: (Boolean) -> Unit,
    amountText: String,
    isExpense: Boolean,
    typeAccentColor: Color,
    currencySymbol: String,
    strings: Strings,
    modifier: Modifier = Modifier
) {
    val haptics = rememberAppHaptics()
    val currentAccount = accounts.find { it.id == selectedAccountId }
    val brandStyle = currentAccount?.let { BrandIdentityHelper.getBrandStyle(it) }

    Column(modifier = modifier) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column {
                Text(
                    text = if (accounts.isNotEmpty()) strings.account else strings.paymentMethod,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, fontWeight = FontWeight.SemiBold),
                    color = ColorTextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = LocalAppColors.current.surfaceVariant,
                    border = BorderStroke(1.dp, if (showDropdown) typeAccentColor else LocalAppColors.current.border),
                    modifier = Modifier.fillMaxWidth().height(56.dp).clickable {
                        haptics.lightTick()
                        onShowDropdownChange(true)
                    }
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f, fill = false),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (currentAccount != null && brandStyle != null) {
                                Box(
                                    modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).background(brandStyle.gradient),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = brandStyle.icon,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Column(modifier = Modifier.weight(1f, fill = false)) {
                                    Text(
                                        text = currentAccount.name,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold),
                                        color = ColorTextPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = formatCurrency(currentAccount.currentBalance, currencySymbol, strings.isBangla),
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Medium),
                                        color = brandStyle.primaryColor,
                                        maxLines = 1
                                    )
                                }
                            } else {
                                Box(
                                    modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).background(typeAccentColor.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Payment,
                                        contentDescription = null,
                                        tint = typeAccentColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Text(
                                    text = selectedPaymentMethod.getDisplayName(strings.isBangla),
                                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold),
                                    color = ColorTextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = "Select Account",
                            tint = ColorTextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            TransactionAccountDropdownMenu(
                expanded = showDropdown,
                onDismissRequest = { onShowDropdownChange(false) },
                accounts = accounts,
                selectedAccountId = selectedAccountId,
                selectedPaymentMethod = selectedPaymentMethod,
                typeAccentColor = typeAccentColor,
                currencySymbol = currencySymbol,
                strings = strings,
                onSelectAccount = onSelectAccount,
                onSelectPaymentMethod = onSelectPaymentMethod
            )
        }

        AccountBalanceWarning(
            account = currentAccount,
            amount = amountText.toParsedAmountOrNull() ?: 0.0,
            isExpense = isExpense,
            currencySymbol = currencySymbol,
            strings = strings
        )
    }
}
