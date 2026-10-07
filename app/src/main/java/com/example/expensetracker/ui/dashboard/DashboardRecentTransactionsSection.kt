package com.example.expensetracker.ui.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.data.model.UserProfile
import com.example.expensetracker.data.model.getLocalizedAccountName
import com.example.expensetracker.data.model.getLocalizedCategoryName
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorDangerExpense
import com.example.expensetracker.theme.ColorSuccessIncome
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.theme.ScreenPaddingHorizontal
import com.example.expensetracker.ui.components.AppCard
import com.example.expensetracker.ui.components.DateTimeHelper
import com.example.expensetracker.ui.components.EmptyStateCard
import com.example.expensetracker.ui.components.HairlineDivider
import com.example.expensetracker.ui.components.ListRow
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.formatCurrency

@Composable
fun DashboardRecentTransactionsSection(
    transactions: List<Transaction>,
    userProfile: UserProfile,
    isPrivacyMode: Boolean,
    onNavigateToTransactions: () -> Unit,
    onOpenAddTransaction: () -> Unit,
    onEditTransaction: (Transaction) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val recentTxs = transactions.take(4)

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ScreenPaddingHorizontal, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = strings.recentTransactions,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = ColorTextPrimary
            )

            Text(
                text = strings.seeAll,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = ColorAccent,
                modifier = Modifier.clickable { onNavigateToTransactions() }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (recentTxs.isEmpty()) {
            EmptyStateCard(
                icon = Icons.Outlined.ReceiptLong,
                headline = strings.logFirstTransaction,
                explanation = strings.startTrackingSubtitle,
                buttonText = strings.addTransaction,
                onButtonClick = onOpenAddTransaction,
                modifier = Modifier.padding(horizontal = ScreenPaddingHorizontal)
            )
        } else {
            AppCard(
                modifier = Modifier.padding(horizontal = ScreenPaddingHorizontal),
                padding = 16.dp
            ) {
                Column {
                    recentTxs.forEachIndexed { index, tx ->
                        val isIncome = tx.type == TransactionType.INCOME
                        val amountFormatted = if (isPrivacyMode) {
                            "${if (isIncome) "+" else "-"} ••••"
                        } else {
                            "${if (isIncome) "+" else "-"} ${formatCurrency(tx.amount, userProfile.currencySymbol, userProfile.useBengaliNumerals || strings.isBangla)}"
                        }
                        val amountColor = if (isIncome) ColorSuccessIncome else ColorDangerExpense

                        ListRow(
                            iconName = tx.categoryIcon,
                            categoryColorHex = tx.categoryColor,
                            title = tx.title,
                            meta = "${getLocalizedCategoryName(tx.categoryName, strings.isBangla)} • ${DateTimeHelper.formatItemDateTime(tx.dateMillis, strings.isBangla, userProfile.useBengaliNumerals || strings.isBangla)}",
                            amountText = amountFormatted,
                            amountColor = amountColor,
                            onClick = { onEditTransaction(tx) },
                            paymentMethod = tx.paymentMethod,
                            paymentMethodName = getLocalizedAccountName(tx.accountName, strings.isBangla),
                            hasReceipt = tx.receiptImagePath != null
                        )

                        if (index < recentTxs.lastIndex) {
                            HairlineDivider()
                        }
                    }
                }
            }
        }
    }
}
