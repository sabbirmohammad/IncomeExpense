package com.example.expensetracker.ui.share

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.data.model.getLocalizedAccountName
import com.example.expensetracker.data.model.getLocalizedCategoryName
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorBorder
import com.example.expensetracker.theme.ColorDangerExpense
import com.example.expensetracker.theme.ColorSuccessIncome
import com.example.expensetracker.theme.ColorTextMuted
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.theme.ColorTextSecondary
import com.example.expensetracker.ui.components.CategoryIconBadge
import com.example.expensetracker.ui.components.DateTimeHelper
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.formatCurrency
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ExpenseShareSlipCard(
    periodTitle: String,
    periodSubtitle: String,
    userName: String,
    currencySymbol: String,
    totalExpense: Double,
    totalIncome: Double,
    transactions: List<Transaction>,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val generatedTime = remember(strings.isBangla) {
        DateTimeHelper.formatFullDateTime(System.currentTimeMillis(), strings.isBangla)
    }

    val backgroundBrush = remember {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFF161C2C),
                Color(0xFF101422),
                Color(0xFF0C0F1A)
            )
        )
    }

    Box(
        modifier = modifier
            .width(360.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundBrush)
            .border(1.dp, Color(0xFF283045), RoundedCornerShape(20.dp))
            .padding(20.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // 1. Top Header: App Branding & User Profile
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(ColorAccent.copy(alpha = 0.15f))
                            .border(1.dp, ColorAccent.copy(alpha = 0.4f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ReceiptLong,
                            contentDescription = null,
                            tint = ColorAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = if (strings.isBangla) strings.appName else "Expense Tracker",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = ColorTextPrimary
                        )
                        Text(
                            text = if (userName.isNotBlank()) (if (strings.isBangla) "$userName-এর খাতা" else "$userName's Ledger") else (if (strings.isBangla) "আর্থিক স্লিপ" else "Financial Slip"),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Normal
                            ),
                            color = ColorTextSecondary
                        )
                    }
                }

                // Statement tag badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF1F283E))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (strings.isBangla) "বিবরণী" else "STATEMENT",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = ColorAccent
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 2. Period Banner & Date
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF181F33))
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Text(
                    text = periodTitle.uppercase(Locale.getDefault()),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp
                    ),
                    color = ColorTextSecondary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = periodSubtitle,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = ColorTextPrimary
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 3. Hero Financial Summary
            Text(
                text = if (strings.isBangla) "মোট খরচ" else "TOTAL SPENT",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 1.sp
                ),
                color = ColorTextSecondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = formatCurrency(totalExpense, currencySymbol),
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = ColorDangerExpense
            )

            if (totalIncome > 0) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Income mini chip
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF13221E))
                            .border(1.dp, ColorSuccessIncome.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Column {
                            Text(
                                text = if (strings.isBangla) "মোট আয়" else "Total Income",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = ColorTextSecondary
                            )
                            Text(
                                text = "+${formatCurrency(totalIncome, currencySymbol)}",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = ColorSuccessIncome
                            )
                        }
                    }

                    // Net balance mini chip
                    val net = totalIncome - totalExpense
                    val netColor = if (net >= 0) ColorSuccessIncome else ColorDangerExpense
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF181F33))
                            .border(1.dp, ColorBorder, RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Column {
                            Text(
                                text = if (strings.isBangla) "নেট ব্যালেন্স" else "Net Balance",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = ColorTextSecondary
                            )
                            Text(
                                text = "${if (net >= 0) "+" else ""}${formatCurrency(net, currencySymbol)}",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = netColor
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 4. Ticket Perforation / Dashed Divider
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .drawBehind {
                        drawLine(
                            color = Color(0xFF2E3852),
                            start = Offset(0f, 0f),
                            end = Offset(size.width, 0f),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f),
                            strokeWidth = 2f
                        )
                    }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 5. Itemized Transactions List
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (strings.isBangla) "লেনদেনের তালিকা" else "ITEMIZED RECORDS",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp
                    ),
                    color = ColorTextSecondary
                )
                Text(
                    text = if (strings.isBangla) "${transactions.size} টি এন্ট্রি" else "${transactions.size} entries",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    color = ColorTextMuted
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (transactions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (strings.isBangla) "এই সময়ে কোনো লেনদেন নেই" else "No expenses recorded in this period",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = ColorTextMuted
                    )
                }
            } else {
                // Show up to 10 transactions to keep card compact and high-res
                val displayList = transactions.take(10)
                displayList.forEachIndexed { index, tx ->
                    val isIncome = tx.type == TransactionType.INCOME
                    val amountColor = if (isIncome) ColorSuccessIncome else ColorDangerExpense
                    val formattedAmount = "${if (isIncome) "+" else "-"}${formatCurrency(tx.amount, currencySymbol)}"

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            CategoryIconBadge(
                                iconName = tx.categoryIcon,
                                colorHex = tx.categoryColor,
                                size = 32.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = tx.title,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    color = ColorTextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(1.dp))
                                val metaString = buildString {
                                    append(getLocalizedCategoryName(tx.categoryName, strings.isBangla))
                                    append(" • ")
                                    val accDisplay = tx.accountName?.let { getLocalizedAccountName(it, strings.isBangla) } ?: tx.paymentMethod.getDisplayName(strings.isBangla)
                                    append(accDisplay)
                                    append(" • ")
                                    append(DateTimeHelper.formatTimeOnly(tx.dateMillis, strings.isBangla))
                                }
                                Text(
                                    text = metaString,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Normal
                                    ),
                                    color = ColorTextSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = formattedAmount,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = amountColor
                        )
                    }

                    if (index < displayList.lastIndex) {
                        Spacer(modifier = Modifier.height(2.dp))
                    }
                }

                if (transactions.size > 10) {
                    val remaining = transactions.size - 10
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF161D2E))
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (strings.isBangla) "+ আরও $remaining টি লেনদেন" else "+ $remaining more transaction${if (remaining > 1) "s" else ""}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            color = ColorTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 6. Footer Information
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(0.5.dp)
                    .background(Color(0xFF232B3E))
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = generatedTime,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    color = ColorTextMuted
                )
                Text(
                    text = if (strings.isBangla) "${strings.appName} দ্বারা তৈরি" else "Generated by Expense Tracker",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = ColorAccent.copy(alpha = 0.8f)
                )
            }
        }
    }
}
