package com.example.expensetracker.ui.transactions

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Payment
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.data.model.PaymentMethod
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.getLocalizedAccountName
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.CategoryIconBadge
import com.example.expensetracker.ui.components.DateTimeHelper
import com.example.expensetracker.ui.components.Strings
import com.example.expensetracker.ui.components.convertToBengaliDigits
import com.example.expensetracker.ui.components.globalBengaliNumeralsEnabled
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun TransactionDetailsCard(
    transaction: Transaction,
    selectedCategory: Category?,
    matchedAccount: Account?,
    selectedPaymentMethod: PaymentMethod,
    dateMillis: Long,
    strings: Strings,
    dateOnlyFormat: SimpleDateFormat,
    dayOfWeekFormat: SimpleDateFormat,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = appColors.surfaceVariant),
        border = BorderStroke(1.dp, appColors.border),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CategoryIconBadge(
                    iconName = selectedCategory?.icon ?: transaction.categoryIcon,
                    colorHex = selectedCategory?.colorHex ?: transaction.categoryColor
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = transaction.title.ifBlank { selectedCategory?.getLocalizedName(strings.isBangla) ?: transaction.categoryName },
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = appColors.textPrimary
                    )
                    Text(
                        text = selectedCategory?.getLocalizedName(strings.isBangla) ?: transaction.categoryName,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp
                        ),
                        color = appColors.textSecondary
                    )
                }
            }

            HorizontalDivider(color = appColors.border.copy(alpha = 0.7f), thickness = 0.8.dp)

            val rawDate = dateOnlyFormat.format(Date(dateMillis))
            val dateDisp = if (strings.isBangla) convertToBengaliDigits(rawDate) else rawDate
            val dayDisp = dayOfWeekFormat.format(Date(dateMillis))
            val timeDisp = DateTimeHelper.formatTimeOnly(dateMillis, strings.isBangla, globalBengaliNumeralsEnabled || strings.isBangla)
            TransactionDetailInfoRow(
                icon = Icons.Outlined.CalendarToday,
                label = strings.dateTimeLabel,
                value = "$dateDisp ($dayDisp) • $timeDisp"
            )

            TransactionDetailInfoRow(
                icon = Icons.Outlined.AccountBalanceWallet,
                label = strings.account,
                value = getLocalizedAccountName(matchedAccount?.name ?: transaction.accountName ?: "Cash in Hand", strings.isBangla)
            )

            TransactionDetailInfoRow(
                icon = Icons.Outlined.Payment,
                label = strings.paymentMethod,
                value = selectedPaymentMethod.getDisplayName(strings.isBangla)
            )
        }
    }
}
