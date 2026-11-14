package com.example.expensetracker.ui.market

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import com.example.expensetracker.data.model.*
import com.example.expensetracker.ui.dashboard.DashboardViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class MarketDialogController(
    val onOpenUnitDialog: (MarketItem) -> Unit,
    val onOpenPromptAmount: (MarketItem) -> Unit,
    val onOpenDeleteItem: (MarketItem) -> Unit,
    val onOpenSubstitute: (MarketItem) -> Unit,
    val onOpenDeleteList: (Long) -> Unit,
    val onStartShopping: () -> Unit,
    val onOpenDuplicateMerge: () -> Unit,
    val onFinishTripClick: (MarketListWithItems?) -> Unit
)

@Composable
fun rememberMarketDialogController(
    currentViewingList: MarketListWithItems?,
    effectiveActiveLists: List<MarketListWithItems>,
    dashboardViewModel: DashboardViewModel,
    masterProducts: List<MasterProduct>,
    accounts: List<Account>,
    currencySymbol: String,
    isBangla: Boolean,
    lastPurchasedRates: Map<String, Pair<Double, String>>,
    productPriceInsights: Map<String, ProductPriceInsight>,
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope,
    onResetSelectedList: () -> Unit
): MarketDialogController {
    var itemForUnitDialog by remember { mutableStateOf<MarketItem?>(null) }
    var promptAmountForItem by remember { mutableStateOf<MarketItem?>(null) }
    var itemToDeleteConfirm by remember { mutableStateOf<MarketItem?>(null) }
    var itemToSubstitute by remember { mutableStateOf<MarketItem?>(null) }
    var showForgotStaplesDialog by remember { mutableStateOf(false) }
    var listToDeleteConfirmId by remember { mutableStateOf<Long?>(null) }
    var tripForCompletionSheet by remember { mutableStateOf<MarketListWithItems?>(null) }
    var isShopModeActive by rememberSaveable(currentViewingList?.list?.id) { mutableStateOf(false) }
    var showDuplicateMergeSheet by remember { mutableStateOf(false) }

    MarketScreenShopModeDialog(
        isActive = isShopModeActive,
        currentViewingList = currentViewingList,
        accounts = accounts,
        currencySymbol = currencySymbol,
        isBangla = isBangla,
        dashboardViewModel = dashboardViewModel,
        lastPurchasedRates = lastPurchasedRates,
        productPriceInsights = productPriceInsights,
        onDismiss = { isShopModeActive = false }
    )

    MarketScreenDialogs(
        itemForUnitDialog = itemForUnitDialog,
        onDismissUnitDialog = { itemForUnitDialog = null },
        onConfirmUnitDialog = { curItem, qty, unit ->
            dashboardViewModel.updateMarketItem(curItem.copy(quantity = qty, unit = unit))
            itemForUnitDialog = null
        },
        promptAmountForItem = promptAmountForItem,
        onDismissPromptAmount = { promptAmountForItem = null },
        onSavePromptAmount = { targetItem, updatedQty, updatedUnit, totalAmount ->
            if (updatedQty != targetItem.quantity || updatedUnit != targetItem.unit) {
                dashboardViewModel.updateMarketItem(targetItem.copy(quantity = updatedQty, unit = updatedUnit))
            }
            dashboardViewModel.markMarketItemPurchased(targetItem, totalAmount)
            promptAmountForItem = null
        },
        itemToSubstitute = itemToSubstitute,
        onDismissSubstitute = { itemToSubstitute = null },
        onConfirmSubstitute = { curItem, newTitle ->
            dashboardViewModel.updateMarketItem(curItem.copy(name = newTitle))
            itemToSubstitute = null
        },
        showForgotStaplesDialog = showForgotStaplesDialog,
        onDismissForgotStaples = { showForgotStaplesDialog = false },
        onConfirmForgotStaples = {
            showForgotStaplesDialog = false
            tripForCompletionSheet = currentViewingList
        },
        itemToDeleteConfirm = itemToDeleteConfirm,
        onDismissDeleteItem = { itemToDeleteConfirm = null },
        onConfirmDeleteItem = { itemToDelete ->
            val deletedItem = itemToDelete
            val listId = currentViewingList?.list?.id ?: deletedItem.listId
            dashboardViewModel.deleteMarketItem(deletedItem.id)
            itemToDeleteConfirm = null
            coroutineScope.launch {
                val result = snackbarHostState.showSnackbar(
                    message = if (isBangla) "'${deletedItem.name}' মুছে ফেলা হয়েছে" else "'${deletedItem.name}' deleted",
                    actionLabel = if (isBangla) "পূর্বাবস্থায় আনুন" else "Undo",
                    duration = SnackbarDuration.Short
                )
                if (result == SnackbarResult.ActionPerformed) {
                    dashboardViewModel.addMarketItem(deletedItem, listId)
                }
            }
        },
        listToDeleteConfirmId = listToDeleteConfirmId,
        effectiveActiveLists = effectiveActiveLists,
        onDismissDeleteList = { listToDeleteConfirmId = null },
        onConfirmDeleteList = { listId ->
            dashboardViewModel.deleteActiveMarketList(listId)
            onResetSelectedList()
            listToDeleteConfirmId = null
        },
        tripForCompletionSheet = tripForCompletionSheet,
        onDismissCompletionSheet = { tripForCompletionSheet = null },
        onConfirmCompleteTrip = { tripToComplete, selectedAccountId, leftoverOption ->
            val rollOverItems = when (leftoverOption) {
                LeftoverHandlingOption.MOVE_TO_NEXT -> tripToComplete.items.filter { !it.isPurchased }
                else -> emptyList()
            }
            dashboardViewModel.completeActiveMarketList(
                listId = tripToComplete.list.id,
                rollOverItems = rollOverItems,
                accountId = selectedAccountId
            ) {
                onResetSelectedList()
                tripForCompletionSheet = null
            }
        },
        showDuplicateMergeSheet = showDuplicateMergeSheet,
        onDismissDuplicateMerge = { showDuplicateMergeSheet = false },
        onMergeCommodity = { src, tgt -> dashboardViewModel.mergeCommodities(src, tgt) },
        masterProducts = masterProducts,
        accounts = accounts,
        currencySymbol = currencySymbol,
        isBangla = isBangla,
        lastPurchasedRates = lastPurchasedRates,
        productPriceInsights = productPriceInsights
    )

    return remember(currentViewingList) {
        MarketDialogController(
            onOpenUnitDialog = { itemForUnitDialog = it },
            onOpenPromptAmount = { promptAmountForItem = it },
            onOpenDeleteItem = { itemToDeleteConfirm = it },
            onOpenSubstitute = { itemToSubstitute = it },
            onOpenDeleteList = { listToDeleteConfirmId = it },
            onStartShopping = { isShopModeActive = true },
            onOpenDuplicateMerge = { showDuplicateMergeSheet = true },
            onFinishTripClick = { list ->
                val items = list?.items ?: emptyList()
                val purchased = items.filter { it.isPurchased }
                val hasStaple = purchased.any {
                    val name = it.name.lowercase()
                    name.contains("মরিচ") || name.contains("লেবু") || name.contains("ধনে") ||
                    name.contains("আদা") || name.contains("রসুন") || name.contains("chili") || name.contains("lemon")
                }
                if (!hasStaple && items.size >= 3) showForgotStaplesDialog = true
                else if (list != null) tripForCompletionSheet = list
            }
        )
    }
}
