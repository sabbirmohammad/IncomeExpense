package com.example.expensetracker.ui.transactions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.ui.components.DateTimeHelper
import com.example.expensetracker.ui.components.formatCurrency
import java.util.Calendar
import kotlin.math.abs
import com.example.expensetracker.theme.LocalAppColors

fun groupTransactionsByDate(
    transactions: List<Transaction>,
    todayLabel: String,
    yesterdayLabel: String,
    isBangla: Boolean,
    useBengaliNumerals: Boolean
): Map<String, List<Transaction>> {
    val calNow = Calendar.getInstance()
    val nowYear = calNow.get(Calendar.YEAR)
    val nowDayOfYear = calNow.get(Calendar.DAY_OF_YEAR)
    val calTx = Calendar.getInstance()

    return transactions.groupBy { tx ->
        calTx.timeInMillis = tx.dateMillis
        val txYear = calTx.get(Calendar.YEAR)
        val txDayOfYear = calTx.get(Calendar.DAY_OF_YEAR)

        if (nowYear == txYear && nowDayOfYear == txDayOfYear) {
            todayLabel
        } else if (nowYear == txYear && nowDayOfYear - txDayOfYear == 1) {
            yesterdayLabel
        } else {
            DateTimeHelper.formatDayGroup(tx.dateMillis, isBangla, useBengaliNumerals || isBangla)
        }
    }
}

@Composable
fun TransactionDateGroupHeader(
    dateGroup: String,
    itemsCount: Int,
    netAmount: Double,
    currencySymbol: String,
    isBangla: Boolean,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = dateGroup,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                color = appColors.textPrimary
            )
            Text(
                text = "• ${DateTimeHelper.formatCounter(itemsCount, isBangla)}${if (isBangla) "টি" else ""}",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                color = appColors.textMuted
            )
        }

        val netText = "${if (netAmount >= 0) "+" else "-"}${formatCurrency(abs(netAmount), currencySymbol)}"
        val netColor = if (netAmount >= 0) appColors.income else appColors.expense

        Text(
            text = netText,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Bold
            ),
            color = netColor
        )
    }
}
