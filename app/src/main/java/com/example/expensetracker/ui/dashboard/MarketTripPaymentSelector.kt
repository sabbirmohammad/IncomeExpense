package com.example.expensetracker.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.AccountType
import com.example.expensetracker.theme.AppColors
import com.example.expensetracker.theme.HairlineInkStroke
import com.example.expensetracker.ui.components.AppHaptics

@Composable
fun MarketTripPaymentSelector(
    accounts: List<Account>,
    selectedAccountId: Long?,
    onSelectAccount: (Long?) -> Unit,
    haptics: AppHaptics,
    isBangla: Boolean,
    appColors: AppColors,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = if (isBangla) "যে অ্যাকাউন্ট থেকে খরচ পরিশোধ হবে:" else "Select Payment Account:",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
            ),
            color = appColors.textSecondary
        )

        if (accounts.isEmpty()) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = appColors.accent.copy(alpha = 0.12f),
                border = BorderStroke(HairlineInkStroke, appColors.accent),
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 48.dp)
                    .minimumInteractiveComponentSize()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Payments,
                        contentDescription = null,
                        tint = appColors.accent,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = if (isBangla) "নগদ (Cash)" else "Cash",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.5.sp
                        ),
                        color = appColors.accent
                    )
                }
            }
        } else if (accounts.size <= 3) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                accounts.forEach { acc ->
                    MarketTripAccountPill(
                        account = acc,
                        isSelected = selectedAccountId == acc.id,
                        onClick = {
                            haptics.lightTick()
                            onSelectAccount(acc.id)
                        },
                        appColors = appColors,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        } else {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(accounts, key = { it.id }) { acc ->
                    MarketTripAccountPill(
                        account = acc,
                        isSelected = selectedAccountId == acc.id,
                        onClick = {
                            haptics.lightTick()
                            onSelectAccount(acc.id)
                        },
                        appColors = appColors,
                        modifier = Modifier.width(115.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun MarketTripAccountPill(
    account: Account,
    isSelected: Boolean,
    onClick: () -> Unit,
    appColors: AppColors,
    modifier: Modifier = Modifier
) {
    val accountIcon: ImageVector = when (account.type) {
        AccountType.BANK -> Icons.Filled.AccountBalance
        AccountType.MFS -> Icons.Filled.CreditCard
        else -> Icons.Filled.Payments
    }

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) appColors.accent.copy(alpha = 0.14f) else appColors.background,
        border = BorderStroke(
            if (isSelected) 1.5.dp else HairlineInkStroke,
            if (isSelected) appColors.accent else appColors.divider
        ),
        modifier = modifier
            .defaultMinSize(minHeight = 48.dp)
            .minimumInteractiveComponentSize()
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = accountIcon,
                contentDescription = null,
                tint = if (isSelected) appColors.accent else appColors.textSecondary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = account.name,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 12.sp
                ),
                color = if (isSelected) appColors.accent else appColors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
