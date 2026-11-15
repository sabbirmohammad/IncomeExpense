package com.example.expensetracker.ui.bills

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.SmallElementRadius
import com.example.expensetracker.ui.components.AppHaptics
import com.example.expensetracker.ui.components.BrandIdentityHelper
import com.example.expensetracker.ui.components.formatCurrency

@Composable
fun PayBillAccountSelector(
    accounts: List<Account>,
    selectedAccount: Account?,
    onSelectAccount: (Account) -> Unit,
    currencySymbol: String,
    isBangla: Boolean,
    haptics: AppHaptics,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current

    if (accounts.isNotEmpty()) {
        Column(modifier = modifier.fillMaxWidth()) {
            Text(
                text = if (isBangla) "পরিশোধের অ্যাকাউন্ট:" else "Pay From Account:",
                fontSize = 12.sp,
                color = appColors.textSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                accounts.forEach { acc ->
                    val isSelected = selectedAccount?.id == acc.id
                    val brandStyle = BrandIdentityHelper.getBrandStyle(acc)

                    Surface(
                        shape = RoundedCornerShape(SmallElementRadius),
                        color = if (isSelected) brandStyle.primaryColor.copy(alpha = 0.15f) else appColors.surfaceVariant,
                        border = BorderStroke(if (isSelected) 1.5.dp else 1.dp, if (isSelected) brandStyle.primaryColor else appColors.border),
                        modifier = Modifier.clickable {
                            haptics.lightTick()
                            onSelectAccount(acc)
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(brandStyle.gradient),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = brandStyle.icon,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(11.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = acc.name,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 11.5.sp,
                                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                    ),
                                    color = appColors.textPrimary
                                )
                                Text(
                                    text = formatCurrency(acc.currentBalance, currencySymbol),
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = if (isSelected) brandStyle.primaryColor else appColors.textMuted
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
