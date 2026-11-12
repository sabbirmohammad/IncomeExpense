package com.example.expensetracker.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.RadiusM
import com.example.expensetracker.theme.ScreenPaddingHorizontal

/**
 * V7 HeroBalanceCard per Design System v7 & Screenshot Patch C3:
 * - Target height <= 200dp
 * - Tonal surface (no border in dark/AMOLED, hairline in light)
 * - Left-aligned displayLarge hero number (Total balance default)
 * - Secondary line: Spending wallet balance
 * - Inline metrics: ▲ Income ৳...   ▼ Expense ৳... (single row, no boxes)
 * - Budget progress: 6dp bar with warning color and percentage
 * - Privacy mode masking support (••••)
 */
@Composable
fun HeroBalanceCard(
    totalBalance: Double,
    totalIncome: Double,
    totalExpense: Double,
    budgetAmount: Double,
    budgetSpent: Double,
    currencySymbol: String,
    modifier: Modifier = Modifier,
    spendingWalletBalance: Double? = null,
    isPrivacyMode: Boolean = false,
    useBengaliNumerals: Boolean = false,
    onIncomeClick: (() -> Unit)? = null,
    onExpenseClick: (() -> Unit)? = null,
    onToggleWallet: (() -> Unit)? = null
) {
    val appColors = LocalAppColors.current
    val strings = LocalAppStrings.current

    val formattedHero = if (isPrivacyMode) {
        "$currencySymbol ••••••"
    } else {
        formatCurrency(totalBalance, currencySymbol, useBengaliNumerals)
    }

    val formattedIncome = if (isPrivacyMode) {
        "••••"
    } else {
        formatCurrency(totalIncome, currencySymbol, useBengaliNumerals)
    }

    val formattedExpense = if (isPrivacyMode) {
        "••••"
    } else {
        formatCurrency(totalExpense, currencySymbol, useBengaliNumerals)
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = ScreenPaddingHorizontal),
        shape = RoundedCornerShape(RadiusM),
        color = appColors.surfaceRaised,
        border = if (appColors.isDark) null else BorderStroke(0.5.dp, appColors.border.copy(alpha = 0.7f)),
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            // Label row: "Total balance"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = strings.totalBalance,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = appColors.textSecondary
                )

                if (spendingWalletBalance != null && onToggleWallet != null) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { onToggleWallet() }
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        val walletDisplay = if (isPrivacyMode) "••••" else formatCurrency(spendingWalletBalance, currencySymbol, useBengaliNumerals)
                        Text(
                            text = "$walletDisplay ${strings.spendingWalletSuffix}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 11.sp,
                                fontFeatureSettings = "tnum"
                            ),
                            color = appColors.textMuted
                        )
                        Icon(
                            imageVector = Icons.Outlined.SwapHoriz,
                            contentDescription = "Switch wallet",
                            tint = appColors.textMuted,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            // Hero number: DisplayLarge, left-aligned, tabular numbers
            Text(
                text = formattedHero,
                style = MaterialTheme.typography.displaySmall.copy(
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    fontFeatureSettings = "tnum"
                ),
                color = appColors.textPrimary
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Inline metrics: ▲ Income ৳8,000   ▼ Expense ৳8,077 (single row, no boxes)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Income metric
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .then(if (onIncomeClick != null) Modifier.clickable { onIncomeClick() } else Modifier)
                        .padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "▲",
                        fontSize = 10.sp,
                        color = appColors.accent,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = strings.income,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal
                        ),
                        color = appColors.textSecondary
                    )
                    Text(
                        text = formattedIncome,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFeatureSettings = "tnum"
                        ),
                        color = appColors.accent
                    )
                }

                // Expense metric
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .then(if (onExpenseClick != null) Modifier.clickable { onExpenseClick() } else Modifier)
                        .padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "▼",
                        fontSize = 10.sp,
                        color = appColors.expense,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = strings.expense,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal
                        ),
                        color = appColors.textSecondary
                    )
                    Text(
                        text = formattedExpense,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFeatureSettings = "tnum"
                        ),
                        color = appColors.expense
                    )
                }
            }

            // Budget progress row: 6dp bar, warning color + text
            if (budgetAmount > 0) {
                Spacer(modifier = Modifier.height(10.dp))
                val percent = ((budgetSpent / budgetAmount) * 100).toInt()
                val barColor = when {
                    percent >= 100 -> appColors.expense
                    percent >= 75 -> appColors.warning
                    else -> appColors.accent
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${strings.navBudgets} $percent%",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = barColor
                    )
                    val spentStr = if (isPrivacyMode) "••••" else formatCurrency(budgetSpent, currencySymbol, useBengaliNumerals)
                    val limitStr = if (isPrivacyMode) "••••" else formatCurrency(budgetAmount, currencySymbol, useBengaliNumerals)
                    Text(
                        text = "$spentStr / $limitStr",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            fontFeatureSettings = "tnum"
                        ),
                        color = appColors.textSecondary
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(appColors.border.copy(alpha = 0.35f))
                ) {
                    val progressFraction = (budgetSpent / budgetAmount).toFloat().coerceIn(0f, 1f)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction = progressFraction)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(3.dp))
                            .background(barColor)
                    )
                }
            }
        }
    }
}
