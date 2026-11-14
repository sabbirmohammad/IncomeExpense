package com.example.expensetracker.ui.accounts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.data.model.getLocalizedCategoryName
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.DateTimeHelper
import com.example.expensetracker.ui.components.HairlineDivider
import com.example.expensetracker.ui.components.ListRow
import com.example.expensetracker.ui.components.Strings
import com.example.expensetracker.ui.components.formatCurrency
import com.example.expensetracker.ui.components.globalBengaliNumeralsEnabled

@Composable
fun AccountTransactionHistoryList(
    transactions: List<Transaction>,
    currencySymbol: String,
    strings: Strings
) {
    val appColors = LocalAppColors.current

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = if (strings.isBangla) "লেনদেনের ইতিহাস (${transactions.size})" else "TRANSACTIONS HISTORY (${transactions.size})",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = appColors.textMuted,
            letterSpacing = 0.8.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (transactions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (strings.isBangla) "এই অ্যাকাউন্টের সাথে এখনও কোনো লেনদেন নেই।" else "No transactions linked to this account yet.",
                    color = appColors.textMuted,
                    fontSize = 12.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 280.dp)
            ) {
                itemsIndexed(transactions) { index, tx ->
                    val isIncome = tx.type == TransactionType.INCOME
                    val amountFormatted = "${if (isIncome) "+" else "-"} ${formatCurrency(tx.amount, currencySymbol, globalBengaliNumeralsEnabled || strings.isBangla)}"
                    val amountColor = if (isIncome) appColors.income else appColors.expense

                    ListRow(
                        iconName = tx.categoryIcon,
                        categoryColorHex = tx.categoryColor,
                        title = tx.title,
                        meta = "${getLocalizedCategoryName(tx.categoryName, strings.isBangla)} • ${DateTimeHelper.formatItemDateTime(tx.dateMillis, strings.isBangla, globalBengaliNumeralsEnabled || strings.isBangla)}",
                        amountText = amountFormatted,
                        amountColor = amountColor,
                        paymentMethod = tx.paymentMethod,
                        hasReceipt = tx.receiptImagePath != null
                    )
                    if (index < transactions.lastIndex) {
                        HairlineDivider()
                    }
                }
            }
        }
    }
}
