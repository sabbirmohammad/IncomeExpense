package com.example.expensetracker.ui.accounts

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.RadiusM
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
            .clip(RoundedCornerShape(RadiusM))
            .clickable { onClick() },
        shape = RoundedCornerShape(RadiusM),
        color = appColors.surfaceRaised,
        border = if (appColors.isDark) null else BorderStroke(0.5.dp, appColors.border)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    AccountCardIdentityRow(
                        account = account,
                        brandStyle = brandStyle,
                        modifier = Modifier.weight(1f)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = formatCurrency(account.currentBalance, currencySymbol),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFeatureSettings = "tnum"
                                ),
                                color = if (account.currentBalance >= 0) appColors.textPrimary else appColors.expense
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
