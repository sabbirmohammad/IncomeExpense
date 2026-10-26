package com.example.expensetracker.ui.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.example.expensetracker.data.model.AccountType
import com.example.expensetracker.data.model.PaymentMethod
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.BrandIdentityHelper
import com.example.expensetracker.ui.components.Strings
import com.example.expensetracker.ui.components.formatCurrency
import com.example.expensetracker.ui.components.rememberAppHaptics

@Composable
fun TransactionAccountDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    accounts: List<Account>,
    selectedAccountId: Long?,
    selectedPaymentMethod: PaymentMethod,
    typeAccentColor: Color,
    currencySymbol: String,
    strings: Strings,
    onSelectAccount: (Account, PaymentMethod) -> Unit,
    onSelectPaymentMethod: (PaymentMethod) -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val haptics = rememberAppHaptics()

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = modifier.widthIn(min = 230.dp, max = 290.dp).background(LocalAppColors.current.surface)
    ) {
        if (accounts.isNotEmpty()) {
            accounts.forEach { acc ->
                val isSelected = selectedAccountId == acc.id
                val accBrand = BrandIdentityHelper.getBrandStyle(acc)
                DropdownMenuItem(
                    leadingIcon = {
                        Box(
                            modifier = Modifier.size(28.dp).clip(RoundedCornerShape(6.dp)).background(accBrand.gradient),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = accBrand.icon,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    },
                    text = {
                        Column {
                            Text(
                                text = acc.name,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = appColors.textPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = formatCurrency(acc.currentBalance, currencySymbol, strings.isBangla),
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Medium),
                                color = if (isSelected) accBrand.primaryColor else appColors.textMuted
                            )
                        }
                    },
                    trailingIcon = {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Outlined.Check,
                                contentDescription = null,
                                tint = accBrand.primaryColor,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    },
                    onClick = {
                        haptics.lightTick()
                        val method = when (acc.type) {
                            AccountType.BANK -> PaymentMethod.BANK_TRANSFER
                            AccountType.MFS -> {
                                val lower = (acc.name + " " + acc.institutionName).lowercase()
                                if (lower.contains("nagad") || lower.contains("নগদ")) PaymentMethod.NAGAD
                                else if (lower.contains("rocket") || lower.contains("রকেট")) PaymentMethod.OTHER
                                else PaymentMethod.BKASH
                            }
                            AccountType.CASH -> PaymentMethod.CASH
                        }
                        onSelectAccount(acc, method)
                        onDismissRequest()
                    }
                )
            }
        } else {
            PaymentMethod.entries.forEach { method ->
                val isSelected = selectedPaymentMethod == method
                DropdownMenuItem(
                    text = {
                        Text(
                            text = method.getDisplayName(strings.isBangla),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = appColors.textPrimary
                        )
                    },
                    trailingIcon = {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Outlined.Check,
                                contentDescription = null,
                                tint = typeAccentColor,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    },
                    onClick = {
                        haptics.lightTick()
                        onSelectPaymentMethod(method)
                        onDismissRequest()
                    }
                )
            }
        }
    }
}
