package com.example.expensetracker.ui.dashboard

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.Handshake
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.ScreenPaddingHorizontal
import com.example.expensetracker.ui.components.AppHaptics

@Composable
fun DashboardQuickActionsDock(
    isBn: Boolean,
    haptics: AppHaptics,
    onOpenMarketList: (() -> Unit)?,
    onNavigateToAccounts: (() -> Unit)?,
    onNavigateToBills: (() -> Unit)?,
    onNavigateToReceivables: (() -> Unit)?,
    onNavigateToBudgets: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = LocalAppColors.current.surface,
        border = BorderStroke(1.dp, LocalAppColors.current.border),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = ScreenPaddingHorizontal)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onOpenMarketList != null) {
                QuickActionItem(
                    icon = Icons.Outlined.ShoppingCart,
                    label = if (isBn) "বাজার সদাই" else "Market",
                    tintColor = Color(0xFF00A8FF),
                    onClick = {
                        haptics.lightTick()
                        onOpenMarketList()
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            if (onNavigateToAccounts != null) {
                QuickActionItem(
                    icon = Icons.Outlined.AccountBalance,
                    label = if (isBn) "অ্যাকাউন্টস" else "Accounts",
                    tintColor = Color(0xFF00B894),
                    onClick = {
                        haptics.mediumClick()
                        onNavigateToAccounts()
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            if (onNavigateToBills != null) {
                QuickActionItem(
                    icon = Icons.Outlined.ReceiptLong,
                    label = if (isBn) "বিল পে" else "Bills",
                    tintColor = Color(0xFFF7941D),
                    onClick = {
                        haptics.mediumClick()
                        onNavigateToBills()
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            if (onNavigateToReceivables != null) {
                QuickActionItem(
                    icon = Icons.Outlined.Handshake,
                    label = if (isBn) "ধার-দেনা" else "Debts",
                    tintColor = Color(0xFF9C27B0),
                    onClick = {
                        haptics.mediumClick()
                        onNavigateToReceivables()
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            if (onNavigateToBudgets != null) {
                QuickActionItem(
                    icon = Icons.Outlined.Savings,
                    label = if (isBn) "বাজেট" else "Budget",
                    tintColor = Color(0xFFE84393),
                    onClick = {
                        haptics.mediumClick()
                        onNavigateToBudgets()
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun QuickActionItem(
    icon: ImageVector,
    label: String,
    tintColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp, horizontal = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(tintColor.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = tintColor,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Medium
            ),
            color = LocalAppColors.current.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
