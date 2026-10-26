package com.example.expensetracker.ui.reports

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.data.model.getLocalizedAccountName
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.CategoryIconBadge
import com.example.expensetracker.ui.components.DateTimeHelper
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.formatCurrency

@Composable
fun ReportTransactionRowItem(
    transaction: Transaction,
    currencySymbol: String,
    isPrivacyMode: Boolean,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val appColors = LocalAppColors.current
    val isIncome = transaction.type == TransactionType.INCOME

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = appColors.surface,
        border = BorderStroke(1.dp, appColors.border)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                CategoryIconBadge(
                    iconName = transaction.categoryIcon,
                    colorHex = transaction.categoryColor,
                    size = 36.dp
                )

                Column {
                    Text(
                        text = transaction.title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = appColors.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = DateTimeHelper.formatItemDateTime(transaction.dateMillis, strings.isBangla),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Normal,
                            color = appColors.textMuted
                        )
                        Text(
                            text = "•",
                            fontSize = 10.sp,
                            color = appColors.textMuted
                        )
                        Text(
                            text = transaction.accountName?.takeIf { it.isNotBlank() }?.let { getLocalizedAccountName(it, strings.isBangla) } ?: transaction.paymentMethod.getDisplayName(strings.isBangla),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = appColors.textSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = if (isPrivacyMode) {
                        if (isIncome) "+ ••••" else "- ••••"
                    } else {
                        val sign = if (isIncome) "+" else "-"
                        "$sign ${formatCurrency(transaction.amount, currencySymbol)}"
                    },
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isIncome) appColors.income else appColors.expense
                )

                if (transaction.receiptImagePath != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ReceiptLong,
                            contentDescription = if (strings.isBangla) "রশিদ সংযুক্ত" else "Receipt Attached",
                            tint = appColors.accent,
                            modifier = Modifier.size(11.dp)
                        )
                        Text(
                            text = if (strings.isBangla) "রশিদ" else "Receipt",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Medium,
                            color = appColors.accent
                        )
                    }
                }
            }
        }
    }
}
