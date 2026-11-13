package com.example.expensetracker.ui.reports

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.FinancialSummary
import com.example.expensetracker.theme.HairlineInkStroke
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.RadiusM
import com.example.expensetracker.ui.components.HairlineDivider
import com.example.expensetracker.ui.components.Strings
import com.example.expensetracker.ui.components.formatCurrency

/**
 * Visual Statement Preview Card per V7 spec:
 * Displays statement period, ledger net balance in displaySmall with tabular numbers (`tnum`),
 * income vs expense breakdown, account scope, and verification status stamp.
 */
@Composable
fun StatementPreviewCard(
    periodTitle: String,
    accountName: String,
    summary: FinancialSummary,
    transactionCount: Int,
    currencySymbol: String,
    isPrivacyMode: Boolean,
    strings: Strings,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val borderStroke = if (!appColors.isDark) BorderStroke(HairlineInkStroke, appColors.border) else null

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(RadiusM),
        color = appColors.surfaceRaised,
        border = borderStroke
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header: Document Type Badge + Period
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(appColors.accentContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Description,
                            contentDescription = null,
                            tint = appColors.accent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Column {
                        Text(
                            text = if (strings.isBangla) "স্টেটমেন্ট প্রিভিউ" else "STATEMENT PREVIEW",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            ),
                            color = appColors.textMuted
                        )
                        Text(
                            text = periodTitle,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = appColors.textPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Status pill
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = appColors.accentContainer
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = null,
                            tint = appColors.accent,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = if (strings.isBangla) "প্রস্তুত" else "Ready",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = appColors.accent
                        )
                    }
                }
            }

            HairlineDivider()

            // Hero Balance Section
            Column {
                Text(
                    text = if (strings.isBangla) "মোট স্থিতি (নেট ব্যালেন্স)" else "Ledger Net Balance",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = appColors.textSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                val balanceColor = if (summary.netBalance >= 0) appColors.income else appColors.expense
                Text(
                    text = if (isPrivacyMode) "••••••" else formatCurrency(summary.netBalance, currencySymbol),
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        fontFeatureSettings = "tnum"
                    ),
                    color = balanceColor
                )
            }

            // Summary 3-Col Metric Strip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(appColors.surfaceVariant)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Income
                Column {
                    Text(
                        text = strings.income,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                        color = appColors.textMuted
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isPrivacyMode) "••••••" else "+${formatCurrency(summary.totalIncome, currencySymbol)}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFeatureSettings = "tnum"
                        ),
                        color = appColors.income
                    )
                }

                // Expense
                Column {
                    Text(
                        text = strings.expense,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                        color = appColors.textMuted
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isPrivacyMode) "••••••" else "-${formatCurrency(summary.totalExpense, currencySymbol)}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFeatureSettings = "tnum"
                        ),
                        color = appColors.expense
                    )
                }

                // Scope
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = if (strings.isBangla) "লেনদেন সংখ্যা" else "Transactions",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                        color = appColors.textMuted
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "$transactionCount ${if (strings.isBangla) "টি" else "items"}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFeatureSettings = "tnum"
                        ),
                        color = appColors.textPrimary
                    )
                }
            }
        }
    }
}
