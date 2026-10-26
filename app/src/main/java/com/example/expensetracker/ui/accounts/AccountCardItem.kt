package com.example.expensetracker.ui.accounts

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.BrandIdentityHelper
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.formatCurrency

@Composable
fun AccountCardItem(
    account: Account,
    currencySymbol: String,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onReconcile: () -> Unit
) {
    val strings = LocalAppStrings.current
    val appColors = LocalAppColors.current
    val brandStyle = BrandIdentityHelper.getBrandStyle(account)
    var showMenu by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        color = appColors.surface,
        border = BorderStroke(1.dp, brandStyle.primaryColor.copy(alpha = if (appColors.isDark) 0.28f else 0.20f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            brandStyle.primaryColor.copy(alpha = if (appColors.isDark) 0.12f else 0.08f),
                            appColors.surface.copy(alpha = 0.90f),
                            if (appColors.isDark) Color(0xFF0F1220) else appColors.surfaceVariant
                        )
                    )
                )
                .padding(14.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(RoundedCornerShape(13.dp))
                                .background(brandStyle.gradient)
                                .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(13.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = brandStyle.icon,
                                contentDescription = null,
                                tint = if (brandStyle.isDarkCard) Color.White else Color.Black,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = account.name,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontSize = 15.5.sp,
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = appColors.textPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                if (account.isDefault) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = appColors.accent.copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = "DEFAULT",
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = appColors.accent,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(3.dp))

                            Text(
                                text = when {
                                    account.institutionName.isNotBlank() && account.accountNumber.isNotBlank() ->
                                        "${account.institutionName} • •••• ${account.accountNumber.takeLast(4)}"
                                    account.institutionName.isNotBlank() -> account.institutionName
                                    account.accountNumber.isNotBlank() -> "•••• ${account.accountNumber.takeLast(4)}"
                                    else -> account.type.getDisplayName(strings.isBangla)
                                },
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                                color = appColors.textSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = formatCurrency(account.currentBalance, currencySymbol),
                                style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                                color = if (account.currentBalance >= 0) appColors.textPrimary else appColors.dangerExpense
                            )
                            Text(
                                text = if (strings.isBangla) "ব্যালেন্স" else "Balance",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = appColors.textMuted
                            )
                        }

                        Box {
                            IconButton(
                                onClick = { showMenu = true }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "Options",
                                    tint = appColors.textMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            AccountCardMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false },
                                onClick = onClick,
                                onReconcile = onReconcile,
                                onEdit = onEdit,
                                onDelete = onDelete
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                AccountCardFooterRow(
                    account = account,
                    brandStyle = brandStyle
                )
            }
        }
    }
}
