package com.example.expensetracker.ui.market

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.data.model.MarketListWithItems
import com.example.expensetracker.theme.LocalAppColors

@Composable
fun MarketDeleteConfirmationDialogs(
    itemToDeleteConfirm: MarketItem?,
    onDismissDeleteItem: () -> Unit,
    onConfirmDeleteItem: (MarketItem) -> Unit,
    listToDeleteConfirmId: Long?,
    effectiveActiveLists: List<MarketListWithItems>,
    onDismissDeleteList: () -> Unit,
    onConfirmDeleteList: (Long) -> Unit,
    isBangla: Boolean
) {
    val appColors = LocalAppColors.current

    if (itemToDeleteConfirm != null) {
        AlertDialog(
            onDismissRequest = onDismissDeleteItem,
            title = {
                Text(
                    text = if (isBangla) "পণ্য মুছে ফেলতে চান?" else "Delete Item?",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = appColors.textPrimary
                )
            },
            text = {
                Text(
                    text = if (isBangla) "'${itemToDeleteConfirm.name}' (${itemToDeleteConfirm.formatQuantity()}) পণ্যটি কি নিশ্চিতভাবে এই ফর্দ থেকে মুছে ফেলতে চান?"
                    else "Are you sure you want to delete '${itemToDeleteConfirm.name}' (${itemToDeleteConfirm.formatQuantity()}) from this list?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = appColors.textSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = { onConfirmDeleteItem(itemToDeleteConfirm) },
                    colors = ButtonDefaults.buttonColors(containerColor = appColors.expense),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(text = if (isBangla) "মুছে ফেলুন" else "Delete", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissDeleteItem) {
                    Text(text = if (isBangla) "বাতিল" else "Cancel", color = appColors.textSecondary)
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = appColors.surface
        )
    }

    if (listToDeleteConfirmId != null) {
        val listToDelete = effectiveActiveLists.find { it.list.id == listToDeleteConfirmId }
        val title = listToDelete?.list?.title ?: if (isBangla) "এই ফর্দটি" else "This list"
        AlertDialog(
            onDismissRequest = onDismissDeleteList,
            title = {
                Text(
                    text = if (isBangla) "ফর্দ মুছে ফেলতে চান?" else "Delete Market List?",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = appColors.expense
                )
            },
            text = {
                Text(
                    text = if (isBangla) "'$title' ফর্দটি মুছে ফেলা হলে এর ভেতরের সকল আইটেম ডিলিট হয়ে যাবে। আপনি কি নিশ্চিত?"
                    else "Are you sure you want to delete '$title' and all items inside it?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = appColors.textSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = { onConfirmDeleteList(listToDeleteConfirmId) },
                    colors = ButtonDefaults.buttonColors(containerColor = appColors.expense),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(text = if (isBangla) "মুছে ফেলুন" else "Delete List", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissDeleteList) {
                    Text(text = if (isBangla) "বাতিল" else "Cancel", color = appColors.textSecondary)
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = appColors.surface
        )
    }
}
