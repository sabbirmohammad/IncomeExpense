package com.example.expensetracker.ui.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.data.model.UserProfile
import com.example.expensetracker.data.model.getLocalizedCategoryName
import com.example.expensetracker.theme.ScreenPaddingHorizontal
import com.example.expensetracker.ui.components.AppCard
import com.example.expensetracker.ui.components.DateTimeHelper
import com.example.expensetracker.ui.components.EmptyStateCard
import com.example.expensetracker.ui.components.HairlineDivider
import com.example.expensetracker.ui.components.ListRow
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.formatCurrency
import com.example.expensetracker.theme.LocalAppColors

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
    val appColors = LocalAppColors.current
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
                color = appColors.textPrimary
            )

            Box(
                modifier = Modifier
                    .sizeIn(minWidth = 48.dp, minHeight = 48.dp)
                    .minimumInteractiveComponentSize()
                    .clickable { onNavigateToTransactions() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = strings.seeAll,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = appColors.accent
                )
            }
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
                        val amountColor = if (isIncome) appColors.income else appColors.expense

                        ListRow(
                            iconName = tx.categoryIcon,
                            categoryColorHex = tx.categoryColor,
                            title = tx.title,
                            meta = "${getLocalizedCategoryName(tx.categoryName, strings.isBangla)} • ${DateTimeHelper.formatItemDateTime(tx.dateMillis, strings.isBangla, userProfile.useBengaliNumerals || strings.isBangla)}",
                            amountText = amountFormatted,
                            amountColor = amountColor,
                            onClick = { onEditTransaction(tx) },
                            paymentMethod = tx.paymentMethod,
                            paymentMethodName = tx.accountName?.takeIf { it.isNotBlank() },
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
