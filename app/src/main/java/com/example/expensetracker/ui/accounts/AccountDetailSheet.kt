package com.example.expensetracker.ui.accounts

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.formatCurrency

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountDetailSheet(
    account: Account,
    transactions: List<Transaction>,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onReconcile: () -> Unit
) {
    val strings = LocalAppStrings.current
    val appColors = LocalAppColors.current

    val totalInflow = remember(transactions) {
        transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
    }
    val totalOutflow = remember(transactions) {
        transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = appColors.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (strings.isBangla) "অ্যাকাউন্ট ও কার্ড বিস্তারিত" else "Account & Card Details",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = appColors.textPrimary
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onReconcile) {
                        Icon(
                            imageVector = Icons.Outlined.AccountBalance,
                            contentDescription = strings.reconcileBalance,
                            tint = appColors.accent,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(onClick = onEdit) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = "Edit",
                            tint = appColors.textSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = "Close",
                            tint = appColors.textSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            DigitalAccountCard(
                account = account,
                currencySymbol = currencySymbol
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AccountStatMiniCard(
                    modifier = Modifier.weight(1f),
                    label = if (strings.isBangla) "প্রারম্ভিক" else "Initial",
                    amount = formatCurrency(account.initialBalance, currencySymbol),
                    color = appColors.textSecondary
                )
                AccountStatMiniCard(
                    modifier = Modifier.weight(1f),
                    label = if (strings.isBangla) "মোট জমা" else "Inflow",
                    amount = "+${formatCurrency(totalInflow, currencySymbol)}",
                    color = appColors.income
                )
                AccountStatMiniCard(
                    modifier = Modifier.weight(1f),
                    label = if (strings.isBangla) "মোট খরচ" else "Outflow",
                    amount = "-${formatCurrency(totalOutflow, currencySymbol)}",
                    color = appColors.expense
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onReconcile,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = appColors.accent),
                border = BorderStroke(1.dp, appColors.accent.copy(alpha = 0.45f))
            ) {
                Icon(
                    imageVector = Icons.Outlined.AccountBalance,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = strings.reconcileBalance,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            AccountTransactionHistoryList(
                transactions = transactions,
                currencySymbol = currencySymbol,
                strings = strings
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
