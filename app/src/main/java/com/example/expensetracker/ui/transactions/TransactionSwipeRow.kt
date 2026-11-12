package com.example.expensetracker.ui.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
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
import com.example.expensetracker.data.model.getLocalizedCategoryName
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.DateTimeHelper
import com.example.expensetracker.ui.components.ListRow
import com.example.expensetracker.ui.components.formatCurrency

/**
 * V7 TransactionSwipeRow per UX_FLOWS section 4 & Screenshot Patch C5:
 * - 40dp category circle · title · subtitle (category only if different from title + time)
 * - Amount right-aligned colored with sign and tnum
 * - Account pill shown only when multiple accounts or differs from default
 * - Tap row -> detail sheet / edit
 * - Swipe left -> delete, Swipe right -> edit
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionSwipeRow(
    transaction: Transaction,
    currencySymbol: String,
    isBangla: Boolean,
    useBengaliNumerals: Boolean,
    onEdit: (Transaction) -> Unit,
    onDelete: (Transaction) -> Unit,
    modifier: Modifier = Modifier,
    onRowClick: ((Transaction) -> Unit)? = null,
    showAccountPill: Boolean = false
) {
    val appColors = LocalAppColors.current
    val isIncome = transaction.type == TransactionType.INCOME
    val amountFormatted = "${if (isIncome) "+" else "-"} ${formatCurrency(transaction.amount, currencySymbol, useBengaliNumerals || isBangla)}"
    val amountColor = if (isIncome) appColors.income else appColors.expense

    // Subtitle = category only if different from title, + time per C5
    val categoryLocalized = getLocalizedCategoryName(transaction.categoryName, isBangla)
    val timeFormatted = DateTimeHelper.formatTimeOnly(transaction.dateMillis, isBangla, useBengaliNumerals || isBangla)
    val meta = if (transaction.title.equals(categoryLocalized, ignoreCase = true) || transaction.title.equals(transaction.categoryName, ignoreCase = true)) {
        timeFormatted
    } else {
        "$categoryLocalized • $timeFormatted"
    }

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
            val bgColor = if (isDelete) appColors.expense else appColors.accent
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
                .background(appColors.surface)
                .padding(horizontal = 16.dp)
        ) {
            ListRow(
                iconName = transaction.categoryIcon,
                categoryColorHex = transaction.categoryColor,
                title = transaction.title,
                meta = meta,
                amountText = amountFormatted,
                amountColor = amountColor,
                onClick = {
                    if (onRowClick != null) {
                        onRowClick(transaction)
                    } else {
                        onEdit(transaction)
                    }
                },
                paymentMethod = transaction.paymentMethod,
                paymentMethodName = if (showAccountPill) transaction.accountName?.takeIf { it.isNotBlank() } else null,
                hasReceipt = transaction.receiptImagePath != null
            )
        }
    }
}
