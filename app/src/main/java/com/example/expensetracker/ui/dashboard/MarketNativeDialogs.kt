package com.example.expensetracker.ui.dashboard

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.data.model.ProductPriceInsight
import com.example.expensetracker.theme.AppColors
import com.example.expensetracker.ui.components.AppHaptics
import com.example.expensetracker.ui.components.convertToBengaliDigits

@Composable
fun MarketNativeRollOverDialog(
    unpurchasedCount: Int,
    isBangla: Boolean,
    onConfirmMove: () -> Unit,
    onConfirmFinishAnyway: () -> Unit,
    onDismiss: () -> Unit,
    appColors: AppColors
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isBangla) "বাজার সম্পন্নকরণ" else "Finish Shopping",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Text(
                text = if (isBangla)
                    "আপনার ফর্দে ${convertToBengaliDigits(unpurchasedCount.toString())}টি পণ্য এখনো কেনা হয়নি। আপনি কি এগুলো পরবর্তী নতুন বাজার ফর্দে স্থানান্তর করতে চান?"
                else
                    "$unpurchasedCount items were not purchased. Do you want to move them to a new market checklist?",
                style = MaterialTheme.typography.bodyMedium,
                color = appColors.textSecondary
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirmMove) {
                Text(if (isBangla) "নতুন ফর্দে নিন" else "Move to Next List", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            Row {
                TextButton(onClick = onConfirmFinishAnyway) {
                    Text(if (isBangla) "না, সম্পন্ন করুন" else "Finish Anyway")
                }
                TextButton(onClick = onDismiss) {
                    Text(if (isBangla) "বাতিল" else "Cancel")
                }
            }
        }
    )
}

@Composable
fun MarketNativeDeleteConfirmDialog(
    itemToDelete: MarketItem,
    isBangla: Boolean,
    onConfirmDelete: () -> Unit,
    onDismiss: () -> Unit,
    appColors: AppColors,
    haptics: AppHaptics
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isBangla) "পণ্য মুছে ফেলতে চান?" else "Delete Item?",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = appColors.textPrimary
            )
        },
        text = {
            Text(
                text = if (isBangla)
                    "'${itemToDelete.name}' (${itemToDelete.formatQuantity()}) পণ্যটি কি নিশ্চিতভাবে এই ফর্দ থেকে মুছে ফেলতে চান?"
                else
                    "Are you sure you want to delete '${itemToDelete.name}' (${itemToDelete.formatQuantity()}) from this list?",
                style = MaterialTheme.typography.bodyMedium,
                color = appColors.textSecondary
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    haptics.warningFeedback()
                    onConfirmDelete()
                },
                colors = ButtonDefaults.buttonColors(containerColor = appColors.expense),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = if (isBangla) "মুছে ফেলুন" else "Delete",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = if (isBangla) "বাতিল" else "Cancel", color = appColors.textSecondary)
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = appColors.surface
    )
}

@Composable
fun MarketNativeAllDialogs(
    itemForUnitDialog: MarketItem?,
    onDismissUnitDialog: () -> Unit,
    onConfirmUnitDialog: (MarketItem, Double, String) -> Unit,
    promptAmountForItem: MarketItem?,
    onDismissPromptDialog: () -> Unit,
    onConfirmPromptDialog: (MarketItem, Double) -> Unit,
    itemToSubstitute: MarketItem?,
    onDismissSubstituteDialog: () -> Unit,
    onConfirmSubstituteDialog: (MarketItem, String) -> Unit,
    showForgotStaplesDialog: Boolean,
    onDismissForgotStaples: () -> Unit,
    onConfirmForgotStaples: () -> Unit,
    showRollOverDialog: Boolean,
    unpurchasedCount: Int,
    onDismissRollOver: () -> Unit,
    onConfirmRollOverMove: () -> Unit,
    onConfirmRollOverFinishAnyway: () -> Unit,
    itemToDeleteConfirm: MarketItem?,
    onDismissDeleteConfirm: () -> Unit,
    onConfirmDelete: (Long) -> Unit,
    currencySymbol: String,
    isBangla: Boolean,
    lastPurchasedRates: Map<String, Pair<Double, String>>,
    productPriceInsights: Map<String, ProductPriceInsight>,
    appColors: AppColors,
    haptics: AppHaptics
) {
    MarketNativeItemDialogs(
        itemForUnitDialog = itemForUnitDialog, onDismissUnitDialog = onDismissUnitDialog,
        onConfirmUnitDialog = onConfirmUnitDialog, promptAmountForItem = promptAmountForItem,
        onDismissPromptDialog = onDismissPromptDialog, onConfirmPromptDialog = onConfirmPromptDialog,
        itemToSubstitute = itemToSubstitute, onDismissSubstituteDialog = onDismissSubstituteDialog,
        onConfirmSubstituteDialog = onConfirmSubstituteDialog, currencySymbol = currencySymbol,
        isBangla = isBangla, lastPurchasedRates = lastPurchasedRates,
        productPriceInsights = productPriceInsights, haptics = haptics
    )

    if (showForgotStaplesDialog) {
        MarketForgotStaplesDialog(
            isBangla = isBangla, onConfirmFinish = onConfirmForgotStaples,
            onDismiss = onDismissForgotStaples, appColors = appColors
        )
    }

    if (showRollOverDialog) {
        MarketNativeRollOverDialog(
            unpurchasedCount = unpurchasedCount, isBangla = isBangla,
            onConfirmMove = onConfirmRollOverMove, onConfirmFinishAnyway = onConfirmRollOverFinishAnyway,
            onDismiss = onDismissRollOver, appColors = appColors
        )
    }

    if (itemToDeleteConfirm != null) {
        MarketNativeDeleteConfirmDialog(
            itemToDelete = itemToDeleteConfirm, isBangla = isBangla,
            onConfirmDelete = { onConfirmDelete(itemToDeleteConfirm.id) },
            onDismiss = onDismissDeleteConfirm, appColors = appColors, haptics = haptics
        )
    }
}
