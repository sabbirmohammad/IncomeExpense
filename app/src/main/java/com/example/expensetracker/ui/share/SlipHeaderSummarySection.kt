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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorBorder
import com.example.expensetracker.theme.ColorDangerExpense
import com.example.expensetracker.theme.ColorSuccessIncome
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.theme.ColorTextSecondary
import com.example.expensetracker.ui.components.Strings
import com.example.expensetracker.ui.components.formatCurrency
import java.util.Locale

@Composable
fun SlipHeaderSummarySection(
    periodTitle: String,
    periodSubtitle: String,
    userName: String,
    currencySymbol: String,
    totalExpense: Double,
    totalIncome: Double,
    strings: Strings,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
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
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = ColorTextSecondary
                    )
                }
            }

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
    }
}
