package com.example.expensetracker.ui.analytics

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Payment
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.PaymentMethodSpend
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.data.model.UserProfile
import com.example.expensetracker.theme.SmallElementRadius
import com.example.expensetracker.ui.components.AppCard
import com.example.expensetracker.ui.components.AppProgressBar
import com.example.expensetracker.ui.components.HairlineDivider
import com.example.expensetracker.ui.components.PaymentMethodDonutChart
import com.example.expensetracker.ui.components.Strings
import com.example.expensetracker.ui.components.formatCurrency
import com.example.expensetracker.ui.components.getPaymentMethodColor
import com.example.expensetracker.theme.LocalAppColors

@Composable
fun PaymentMethodDonutChartCard(
    spends: List<PaymentMethodSpend>,
    currencySymbol: String,
    totalAmount: Double,
    transactionType: TransactionType,
    strings: Strings,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    AppCard(modifier = modifier) {
        Column {
            Text(
                text = if (transactionType == TransactionType.EXPENSE) {
                    if (strings.isBangla) "পেমেন্ট মাধ্যম অনুযায়ী খরচ" else "Spending by payment method"
                } else {
                    if (strings.isBangla) "পেমেন্ট মাধ্যম অনুযায়ী আয়" else "Income by payment channel"
                },
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = appColors.textPrimary
            )
            Spacer(modifier = Modifier.height(16.dp))

            PaymentMethodDonutChart(
                spends = spends,
                currencySymbol = currencySymbol,
                totalAmount = totalAmount,
                centerLabel = if (transactionType == TransactionType.EXPENSE) {
                    if (strings.isBangla) "মোট খরচ" else "Total spent"
                } else {
                    if (strings.isBangla) "মোট আয়" else "Total income"
                }
            )
        }
    }
}

@Composable
fun PaymentMethodRankingCard(
    spends: List<PaymentMethodSpend>,
    userProfile: UserProfile,
    strings: Strings,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    AppCard(modifier = modifier) {
        Column {
            Text(
                text = if (strings.isBangla) "পেমেন্ট চ্যানেল বিশ্লেষণ" else "Payment Channels Breakdown",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = appColors.textPrimary
            )
            Spacer(modifier = Modifier.height(14.dp))

            spends.forEachIndexed { index, spend ->
                val color = getPaymentMethodColor(spend.paymentMethod, appColors.isDark)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(SmallElementRadius))
                                    .background(color.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when (spend.paymentMethod.name) {
                                        "CASH" -> Icons.Outlined.AccountBalanceWallet
                                        "CREDIT_CARD", "DEBIT_CARD" -> Icons.Outlined.CreditCard
                                        else -> Icons.Outlined.Payment
                                    },
                                    contentDescription = null,
                                    tint = color,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = spend.paymentMethod.getDisplayName(strings.isBangla),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    color = appColors.textPrimary,
                                    maxLines = 1,
                                    softWrap = false,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${spend.transactionCount} ${if (strings.isBangla) "টি লেনদেন" else "transactions"}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = appColors.textMuted
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = formatCurrency(spend.totalAmount, userProfile.currencySymbol),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = appColors.textPrimary
                            )
                            Text(
                                text = "${(spend.percentage * 100).toInt()}%",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                color = appColors.textSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    AppProgressBar(
                        progress = spend.percentage,
                        fillColor = color,
                        height = 4.dp
                    )
                }

                if (index < spends.lastIndex) {
                    HairlineDivider()
                }
            }
        }
    }
}
