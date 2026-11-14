package com.example.expensetracker.ui.market

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.expensetracker.common.UnitConverterEngine
import com.example.expensetracker.data.model.*
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.UnitAndQuantityDialog
import com.example.expensetracker.ui.dashboard.MarketSubstituteItemDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketScreenDialogs(
    itemForUnitDialog: MarketItem?,
    onDismissUnitDialog: () -> Unit,
    onConfirmUnitDialog: (MarketItem, Double, String) -> Unit,
    promptAmountForItem: MarketItem?,
    onDismissPromptAmount: () -> Unit,
    onSavePromptAmount: (MarketItem, Double, String, Double) -> Unit,
    itemToSubstitute: MarketItem?,
    onDismissSubstitute: () -> Unit,
    onConfirmSubstitute: (MarketItem, String) -> Unit,
    showForgotStaplesDialog: Boolean,
    onDismissForgotStaples: () -> Unit,
    onConfirmForgotStaples: () -> Unit,
    itemToDeleteConfirm: MarketItem?,
    onDismissDeleteItem: () -> Unit,
    onConfirmDeleteItem: (MarketItem) -> Unit,
    listToDeleteConfirmId: Long?,
    effectiveActiveLists: List<MarketListWithItems>,
    onDismissDeleteList: () -> Unit,
    onConfirmDeleteList: (Long) -> Unit,
    tripForCompletionSheet: MarketListWithItems?,
    onDismissCompletionSheet: () -> Unit,
    onConfirmCompleteTrip: (MarketListWithItems, Long?, LeftoverHandlingOption) -> Unit,
    showDuplicateMergeSheet: Boolean,
    onDismissDuplicateMerge: () -> Unit,
    onMergeCommodity: (String, String) -> Unit,
    masterProducts: List<MasterProduct>,
    accounts: List<Account>,
    currencySymbol: String,
    isBangla: Boolean,
    lastPurchasedRates: Map<String, Pair<Double, String>>,
    productPriceInsights: Map<String, ProductPriceInsight>
) {
    val appColors = LocalAppColors.current

    if (itemForUnitDialog != null) {
        UnitAndQuantityDialog(
            initialQuantity = itemForUnitDialog.quantity,
            initialUnit = itemForUnitDialog.unit,
            isBangla = isBangla,
            onDismiss = onDismissUnitDialog,
            onConfirm = { qty, unit -> onConfirmUnitDialog(itemForUnitDialog, qty, unit) }
        )
    }

    if (promptAmountForItem != null) {
        val lastRate = lastPurchasedRates[promptAmountForItem.name.lowercase().trim()]
        val targetInsight = UnitConverterEngine.findMatchingInsight(promptAmountForItem.name, productPriceInsights)
        MarketPriceEntrySheet(
            item = promptAmountForItem,
            currencySymbol = currencySymbol,
            isBangla = isBangla,
            hasNextPendingItem = false,
            onDismiss = onDismissPromptAmount,
            onSaveItem = { updatedQty, updatedUnit, totalAmount, _ ->
                onSavePromptAmount(promptAmountForItem, updatedQty, updatedUnit, totalAmount)
            },
            lastRate = lastRate,
            priceInsight = targetInsight
        )
    }

    if (itemToSubstitute != null) {
        MarketSubstituteItemDialog(
            item = itemToSubstitute,
            isBangla = isBangla,
            onDismiss = onDismissSubstitute,
            onConfirmSubstitute = { newTitle -> onConfirmSubstitute(itemToSubstitute, newTitle) }
        )
    }

    if (showForgotStaplesDialog) {
        AlertDialog(
            onDismissRequest = onDismissForgotStaples,
            title = {
                Text(
                    text = if (isBangla) "কাঁচামরিচ বা লেবু কি নিয়েছেন?" else "Forgot Chili or Lemon?",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = appColors.textPrimary
                )
            },
            text = {
                Text(
                    text = if (isBangla) "ফর্দে কোনো কাঁচামরিচ, ধনেপাতা বা লেবু কেনা চিহ্নিত নেই। আপনি কি এগুলো নিয়েছেন নাকি ফর্দ এখনই সম্পন্ন করবেন?"
                    else "No chili, lemon, or coriander was marked as purchased. Did you already buy them or want to finish now?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = appColors.textSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = onConfirmForgotStaples,
                    colors = ButtonDefaults.buttonColors(containerColor = appColors.income),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(text = if (isBangla) "হ্যাঁ, সম্পন্ন করুন" else "Yes, Finish Now", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissForgotStaples) {
                    Text(text = if (isBangla) "না, ফর্দে যোগ করব" else "Add to List", color = appColors.accent)
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = appColors.surface
        )
    }

    MarketDeleteConfirmationDialogs(
        itemToDeleteConfirm = itemToDeleteConfirm,
        onDismissDeleteItem = onDismissDeleteItem,
        onConfirmDeleteItem = onConfirmDeleteItem,
        listToDeleteConfirmId = listToDeleteConfirmId,
        effectiveActiveLists = effectiveActiveLists,
        onDismissDeleteList = onDismissDeleteList,
        onConfirmDeleteList = onConfirmDeleteList,
        isBangla = isBangla
    )

    if (tripForCompletionSheet != null) {
        MarketTripFinishSummarySheet(
            trip = tripForCompletionSheet,
            accounts = accounts,
            currencySymbol = currencySymbol,
            isBangla = isBangla,
            onDismiss = onDismissCompletionSheet,
            onConfirmComplete = { selectedAccountId, leftoverOption ->
                onConfirmCompleteTrip(tripForCompletionSheet, selectedAccountId, leftoverOption)
            },
            productPriceInsights = productPriceInsights
        )
    }

    if (showDuplicateMergeSheet) {
        val allCommodities = remember(masterProducts, productPriceInsights) {
            buildMarketCommodities(masterProducts, productPriceInsights)
        }
        MarketDuplicateMergeSheet(
            commodities = allCommodities,
            isBangla = isBangla,
            currencySymbol = currencySymbol,
            onMergeItems = onMergeCommodity,
            onDismiss = onDismissDuplicateMerge
        )
    }
}
