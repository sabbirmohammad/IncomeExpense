package com.example.expensetracker.ui.market

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.AccountType
import com.example.expensetracker.theme.HairlineInkStroke
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.DateTimeHelper
import com.example.expensetracker.ui.components.rememberAppHaptics

@Composable
fun TripPaymentAccountSelector(
    accounts: List<Account>,
    selectedAccountId: Long?,
    onSelectAccount: (Long?) -> Unit,
    isBangla: Boolean,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val haptics = rememberAppHaptics()

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = if (isBangla) "পরিশোধের অ্যাকাউন্ট:" else "Payment Account:",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
            ),
            color = appColors.textSecondary
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(accounts) { acc ->
                val isSelected = selectedAccountId == acc.id
                Surface(
                    onClick = {
                        haptics.lightTick()
                        onSelectAccount(acc.id)
                    },
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) appColors.accentContainer else appColors.surface,
                    border = BorderStroke(
                        HairlineInkStroke,
                        if (isSelected) appColors.accent else appColors.divider
                    ),
                    modifier = Modifier
                        .defaultMinSize(minHeight = 44.dp)
                        .minimumInteractiveComponentSize()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val icon = when (acc.type) {
                            AccountType.BANK -> Icons.Filled.AccountBalance
                            AccountType.MFS -> Icons.Filled.CreditCard
                            AccountType.CASH -> Icons.Filled.Payments
                        }
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (isSelected) appColors.accent else appColors.textSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = acc.name,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = if (isSelected) appColors.accent else appColors.textPrimary
                        )
                    }
                }
            }

            // Date Chip
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = appColors.surface,
                    border = BorderStroke(HairlineInkStroke, appColors.divider),
                    modifier = Modifier
                        .defaultMinSize(minHeight = 44.dp)
                        .minimumInteractiveComponentSize()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CalendarToday,
                            contentDescription = null,
                            tint = appColors.textSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = DateTimeHelper.formatMarketTripTitle(System.currentTimeMillis(), isBangla),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 12.sp,
                                fontFeatureSettings = "tnum"
                            ),
                            color = appColors.textSecondary
                        )
                    }
                }
            }
        }
    }
}
