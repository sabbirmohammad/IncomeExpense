package com.example.expensetracker.ui.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.data.model.getLocalizedAccountName
import com.example.expensetracker.data.model.getLocalizedCategoryName
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorDangerExpense
import com.example.expensetracker.theme.ColorSuccessIncome
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.DateTimeHelper
import com.example.expensetracker.ui.components.ListRow
import com.example.expensetracker.ui.components.formatCurrency

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionSwipeRow(
    transaction: Transaction,
    currencySymbol: String,
    isBangla: Boolean,
    useBengaliNumerals: Boolean,
    onEdit: (Transaction) -> Unit,
    onDelete: (Transaction) -> Unit,
    modifier: Modifier = Modifier
) {
    val isIncome = transaction.type == TransactionType.INCOME
    val amountFormatted = "${if (isIncome) "+" else "-"} ${formatCurrency(transaction.amount, currencySymbol, useBengaliNumerals || isBangla)}"
    val amountColor = if (isIncome) ColorSuccessIncome else ColorDangerExpense
    val meta = "${getLocalizedCategoryName(transaction.categoryName, isBangla)} • ${DateTimeHelper.formatTimeOnly(transaction.dateMillis, isBangla, useBengaliNumerals || isBangla)}"

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            when (dismissValue) {
                SwipeToDismissBoxValue.EndToStart -> {
                    onDelete(transaction)
                    true
                }
                SwipeToDismissBoxValue.StartToEnd -> {
                    onEdit(transaction)
                    false
                }
                SwipeToDismissBoxValue.Settled -> false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier,
        backgroundContent = {
            val direction = dismissState.dismissDirection
            val isDelete = direction == SwipeToDismissBoxValue.EndToStart
            val bgColor = if (isDelete) ColorDangerExpense else ColorAccent
            val icon = if (isDelete) Icons.Outlined.Delete else Icons.Outlined.Edit
            val alignment = if (isDelete) Alignment.CenterEnd else Alignment.CenterStart
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(bgColor)
                    .padding(horizontal = 20.dp),
                contentAlignment = alignment
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        },
        enableDismissFromStartToEnd = true,
        enableDismissFromEndToStart = true
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(LocalAppColors.current.surface)
                .padding(horizontal = 16.dp)
        ) {
            ListRow(
                iconName = transaction.categoryIcon,
                categoryColorHex = transaction.categoryColor,
                title = transaction.title,
                meta = meta,
                amountText = amountFormatted,
                amountColor = amountColor,
                onClick = { onEdit(transaction) },
                paymentMethod = transaction.paymentMethod,
                paymentMethodName = getLocalizedAccountName(transaction.accountName, isBangla),
                hasReceipt = transaction.receiptImagePath != null
            )
        }
    }
}
