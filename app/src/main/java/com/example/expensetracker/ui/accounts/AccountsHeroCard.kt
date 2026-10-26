package com.example.expensetracker.ui.accounts

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.AccountType
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.HairlineDivider
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.formatCurrency
import com.example.expensetracker.ui.components.globalBengaliNumeralsEnabled

@Composable
fun AccountsHeroCard(
    accounts: List<Account>,
    totalNetWorth: Double,
    totalBank: Double,
    totalMfs: Double,
    totalCash: Double,
    currencySymbol: String
) {
    val strings = LocalAppStrings.current
    val appColors = LocalAppColors.current
    val isBn = strings.isBangla || globalBengaliNumeralsEnabled

    val bankColor = if (appColors.isDark) Color(0xFF3B8FF0) else Color(0xFF1D4ED8)
    val mfsColor = if (appColors.isDark) Color(0xFFE056FD) else Color(0xFF7E22CE)
    val cashColor = if (appColors.isDark) Color(0xFF2EC4B6) else Color(0xFF0F766E)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = BorderStroke(
            1.dp,
            Brush.verticalGradient(
                if (appColors.isDark) {
                    listOf(Color(0xFF2E3A56), Color(0xFF1B2338))
                } else {
                    listOf(appColors.border, appColors.border.copy(alpha = 0.5f))
                }
            )
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        if (appColors.isDark) {
                            listOf(Color(0xFF151C2F), Color(0xFF0E1321))
                        } else {
                            listOf(appColors.surface, appColors.surfaceVariant)
                        }
                    )
                )
                .padding(vertical = 22.dp, horizontal = 18.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(if (totalNetWorth < 0) appColors.dangerExpense else appColors.accent)
                    )
                    Text(
                        text = if (strings.isBangla) "মোট নেট ব্যালেন্স" else "Total Net Balance",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.2.sp
                        ),
                        color = appColors.textSecondary,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = formatCurrency(totalNetWorth, currencySymbol, isBn),
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.5).sp
                    ),
                    color = appColors.textPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))
                HairlineDivider(color = appColors.border)
                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SourceStatCard(
                        modifier = Modifier.weight(1f),
                        label = strings.banksTab,
                        amount = totalBank,
                        count = accounts.count { it.type == AccountType.BANK },
                        symbol = currencySymbol,
                        icon = Icons.Outlined.AccountBalance,
                        color = bankColor,
                        isBangla = strings.isBangla
                    )
                    SourceStatCard(
                        modifier = Modifier.weight(1f),
                        label = strings.mfsTab,
                        amount = totalMfs,
                        count = accounts.count { it.type == AccountType.MFS },
                        symbol = currencySymbol,
                        icon = Icons.Outlined.PhoneAndroid,
                        color = mfsColor,
                        isBangla = strings.isBangla
                    )
                    SourceStatCard(
                        modifier = Modifier.weight(1f),
                        label = strings.cashTab,
                        amount = totalCash,
                        count = accounts.count { it.type == AccountType.CASH },
                        symbol = currencySymbol,
                        icon = Icons.Outlined.Payments,
                        color = cashColor,
                        isBangla = strings.isBangla
                    )
                }
            }
        }
    }
}

@Composable
fun SourceStatCard(
    modifier: Modifier = Modifier,
    label: String,
    amount: Double,
    count: Int,
    symbol: String,
    icon: ImageVector,
    color: Color,
    isBangla: Boolean
) {
    val appColors = LocalAppColors.current
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = if (appColors.isDark) Color(0xFF101524) else appColors.surfaceVariant,
        border = BorderStroke(1.dp, color.copy(alpha = if (appColors.isDark) 0.22f else 0.35f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(15.dp)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = appColors.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = formatCurrency(amount, symbol, isBangla || globalBengaliNumeralsEnabled),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = appColors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(1.dp))
            val countDisp = if (isBangla || globalBengaliNumeralsEnabled) convertToBengaliDigits(count.toString()) else count.toString()
            Text(
                text = "$countDisp ${if (isBangla) "টি" else "acc"}",
                fontSize = 9.5.sp,
                color = appColors.textMuted
            )
        }
    }
}
