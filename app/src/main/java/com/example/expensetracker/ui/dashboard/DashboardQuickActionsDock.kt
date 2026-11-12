package com.example.expensetracker.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.ScreenPaddingHorizontal
import com.example.expensetracker.ui.components.AppHaptics

/**
 * V7 QuickActionsDock per UX_FLOWS section 2 & Screenshot Patch C3:
 * - 4 quick actions: [Expense] [Income] [Transfer] [Bazaar]
 * - Uniform accentContainer tint on icons (no rainbow colors)
 * - Single accent color, no outer enclosing container card
 * - Touch targets >= 48dp
 */
@Composable
fun DashboardQuickActionsDock(
    isBn: Boolean,
    haptics: AppHaptics,
    onNavigateToAccounts: (() -> Unit)? = null,
    onNavigateToBills: (() -> Unit)? = null,
    onNavigateToReceivables: (() -> Unit)? = null,
    onNavigateToBudgets: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    onOpenMarketList: (() -> Unit)? = null,
    onAddExpense: (() -> Unit)? = null,
    onAddIncome: (() -> Unit)? = null,
    onTransfer: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = ScreenPaddingHorizontal, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. Expense
        QuickActionItemV7(
            icon = Icons.Outlined.ArrowDownward,
            label = if (isBn) "খরচ" else "Expense",
            onClick = {
                haptics.mediumClick()
                (onAddExpense ?: onNavigateToAccounts)?.invoke()
            },
            modifier = Modifier.weight(1f)
        )

        // 2. Income
        QuickActionItemV7(
            icon = Icons.Outlined.ArrowUpward,
            label = if (isBn) "আয়" else "Income",
            onClick = {
                haptics.mediumClick()
                (onAddIncome ?: onNavigateToAccounts)?.invoke()
            },
            modifier = Modifier.weight(1f)
        )

        // 3. Transfer
        QuickActionItemV7(
            icon = Icons.Outlined.SwapHoriz,
            label = if (isBn) "স্থানান্তর" else "Transfer",
            onClick = {
                haptics.mediumClick()
                (onTransfer ?: onNavigateToAccounts)?.invoke()
            },
            modifier = Modifier.weight(1f)
        )

        // 4. Bazaar (Market)
        QuickActionItemV7(
            icon = Icons.Outlined.ShoppingCart,
            label = if (isBn) "বাজার" else "Bazaar",
            onClick = {
                haptics.mediumClick()
                onOpenMarketList?.invoke()
            },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun QuickActionItemV7(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .minimumInteractiveComponentSize()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp, horizontal = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(appColors.accentContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = appColors.accent,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 12.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.Medium
            ),
            color = appColors.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
