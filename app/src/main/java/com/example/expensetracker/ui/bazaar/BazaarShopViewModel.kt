package com.example.expensetracker.ui.bazaar

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.model.MarketCompletionResult
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.data.model.MarketListWithItems
import com.example.expensetracker.data.model.MasterProduct
import com.example.expensetracker.data.model.ProductPriceInsight
import com.example.expensetracker.data.model.UserProfile
import com.example.expensetracker.data.repository.ExpenseRepository
import com.example.expensetracker.data.repository.addMarketItem
import com.example.expensetracker.data.repository.completeActiveMarketList
import com.example.expensetracker.data.repository.createMarketListWithItems
import com.example.expensetracker.data.repository.deleteMarketItem
import com.example.expensetracker.data.repository.getMarketListBudget
import com.example.expensetracker.data.repository.updateMarketItem
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BazaarShopViewModel(
    application: Application,
    private val savedStateHandle: SavedStateHandle = SavedStateHandle(),
    repoOverride: ExpenseRepository? = null
) : AndroidViewModel(application) {

    private val repository = repoOverride ?: ExpenseRepository.getInstance(application)

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        android.util.Log.e("BazaarShopViewModel", "Error in shop mode operation", throwable)
    }

    // Repository state flows
    val allMarketLists: StateFlow<List<MarketListWithItems>> = repository.allMarketLists
    val isMarketListsLoaded: StateFlow<Boolean> = repository.isMarketListsLoaded
    val lastPurchasedRates: StateFlow<Map<String, Pair<Double, String>>> = repository.lastPurchasedRates
    val productPriceInsights: StateFlow<Map<String, ProductPriceInsight>> = repository.productPriceInsights
    val masterProducts: StateFlow<List<MasterProduct>> = repository.masterProducts
    val stapleProducts: StateFlow<List<MasterProduct>> = repository.stapleProducts
    val isPrivacyMode: StateFlow<Boolean> = repository.isPrivacyMode
    val userProfile: StateFlow<UserProfile> = repository.userProfile
    val accounts = repository.accounts

    val masterProductsMap: StateFlow<Map<String, MasterProduct>> = masterProducts.map { list ->
        list.associateBy { it.normalizedName }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    // 1. Skipped Item IDs backed by SavedStateHandle for process death persistence
    private val SKIPPED_ITEMS_KEY = "shop_skipped_item_ids"
    private val _skippedItemIds = MutableStateFlow<Set<Long>>(
        savedStateHandle.get<List<Long>>(SKIPPED_ITEMS_KEY)?.toSet() ?: emptySet()
    )
    val skippedItemIds: StateFlow<Set<Long>> = _skippedItemIds.asStateFlow()

    // 2. Auto-advance setting (defaults to true per Amendment 12)
    private val AUTO_ADVANCE_KEY = "shop_auto_advance_enabled"
    private val _isAutoAdvanceEnabled = MutableStateFlow(
        savedStateHandle.get<Boolean>(AUTO_ADVANCE_KEY) ?: true
    )
    val isAutoAdvanceEnabled: StateFlow<Boolean> = _isAutoAdvanceEnabled.asStateFlow()

    // 3. Pricing mode memory per item (Per unit vs Total paid per Amendment 9)
    private val _itemPricingMode = MutableStateFlow<Map<Long, PricingMode>>(emptyMap())
    val itemPricingMode: StateFlow<Map<Long, PricingMode>> = _itemPricingMode.asStateFlow()

    // 4. Highlighted item for auto-advance visual pulsing
    private val _pendingHighlightItemId = MutableStateFlow<Long?>(null)
    val pendingHighlightItemId: StateFlow<Long?> = _pendingHighlightItemId.asStateFlow()

    // 5. Active Price Pad Item
    private val _activePricePadItem = MutableStateFlow<MarketItem?>(null)
    val activePricePadItem: StateFlow<MarketItem?> = _activePricePadItem.asStateFlow()

    // 6. Compact receipt panel expansion state
    private val _isReceiptExpanded = MutableStateFlow(false)
    val isReceiptExpanded: StateFlow<Boolean> = _isReceiptExpanded.asStateFlow()

    fun toggleReceiptExpanded() {
        _isReceiptExpanded.value = !_isReceiptExpanded.value
    }

    fun setReceiptExpanded(expanded: Boolean) {
        _isReceiptExpanded.value = expanded
    }

    fun openPricePad(item: MarketItem) {
        _activePricePadItem.value = item
    }

    fun closePricePad() {
        _activePricePadItem.value = null
    }

    fun setAutoAdvanceEnabled(enabled: Boolean) {
        _isAutoAdvanceEnabled.value = enabled
        savedStateHandle[AUTO_ADVANCE_KEY] = enabled
    }

    fun clearHighlight() {
        _pendingHighlightItemId.value = null
    }

    fun getBudgetCap(listId: Long): Double? {
        return repository.getMarketListBudget(listId)
    }

    /**
     * Swipe right (or check circle tap) action:
     * - If item has last purchased rate -> marks bought at last price with unit conversion, triggers undo.
     * - If no last purchased rate -> opens Price Pad.
     */
    fun swipeRightDone(
        item: MarketItem,
        onShowUndoSnackbar: (item: MarketItem, message: String, undoAction: () -> Unit) -> Unit
    ) {
        val lastRate = lastPurchasedRates.value[item.name.lowercase().trim()]
        if (lastRate != null && lastRate.first > 0.0) {
            val amount = BazaarShopLogic.calculateLastPriceAmount(
                itemQuantity = item.quantity,
                itemUnit = item.unit,
                lastRate = lastRate.first,
                lastRateUnit = lastRate.second
            )
            val updated = item.copy(amount = amount, isPurchased = true)
            updateItemInDb(updated)

            // Auto-advance if enabled
            triggerAutoAdvance(item.id)

            val isBn = userProfile.value.appLanguage != "en"
            val msg = if (isBn) "${item.name} কেনা সম্পন্ন (৳${amount.toInt()})" else "${item.name} marked bought (৳$amount)"
            onShowUndoSnackbar(item, msg) {
                undoPurchase(item)
            }
        } else {
            // No last rate known -> open Price Pad
            openPricePad(item)
        }
    }

    /**
     * Swipe left (or overflow menu skip) action:
     * - Adds to skippedItemIds and triggers undo snackbar.
     */
    fun swipeLeftSkip(
        item: MarketItem,
        onShowUndoSnackbar: (item: MarketItem, message: String, undoAction: () -> Unit) -> Unit
    ) {
        val current = _skippedItemIds.value.toMutableSet()
        current.add(item.id)
        _skippedItemIds.value = current
        savedStateHandle[SKIPPED_ITEMS_KEY] = current.toList()

        // Auto-advance if enabled
        triggerAutoAdvance(item.id)

        val isBn = userProfile.value.appLanguage != "en"
        val msg = if (isBn) "${item.name} স্কিপ করা হয়েছে" else "${item.name} skipped"
        onShowUndoSnackbar(item, msg) {
            undoSkip(item.id)
        }
    }

    fun undoSkip(itemId: Long) {
        val current = _skippedItemIds.value.toMutableSet()
        current.remove(itemId)
        _skippedItemIds.value = current
        savedStateHandle[SKIPPED_ITEMS_KEY] = current.toList()
    }

    fun undoPurchase(originalItem: MarketItem) {
        val reset = originalItem.copy(amount = null, isPurchased = false)
        updateItemInDb(reset)
    }

    /**
     * Confirms entry from Price Pad (Amendment 9 & 12).
     */
    fun confirmPricePad(
        item: MarketItem,
        totalAmount: Double,
        mode: PricingMode
    ) {
        val roundedAmount = BazaarShopLogic.roundToTwoDecimals(totalAmount)
        val updated = item.copy(amount = roundedAmount, isPurchased = true)
        updateItemInDb(updated)

        // Remember pricing mode for this item
        val modeMap = _itemPricingMode.value.toMutableMap()
        modeMap[item.id] = mode
        _itemPricingMode.value = modeMap

        // If item was previously skipped, unskip it
        if (item.id in _skippedItemIds.value) {
            undoSkip(item.id)
        }

        closePricePad()
        triggerAutoAdvance(item.id)
    }

    /**
     * Triggers auto-advance to next unpurchased, non-skipped item.
     */
    private fun triggerAutoAdvance(currentItemId: Long) {
        if (!_isAutoAdvanceEnabled.value) return
        viewModelScope.launch(coroutineExceptionHandler) {
            val allLists = repository.allMarketLists.value
            val currentList = allLists.find { trip -> trip.items.any { it.id == currentItemId } }
            if (currentList != null) {
                val next = BazaarShopLogic.findNextUnpurchasedItem(
                    items = currentList.items,
                    currentItemId = currentItemId,
                    skippedIds = _skippedItemIds.value
                )
                if (next != null) {
                    _pendingHighlightItemId.value = next.id
                }
            }
        }
    }

    fun markNotAvailable(item: MarketItem) {
        swipeLeftSkip(item) { _, _, _ -> }
        closePricePad()
    }

    fun deleteItem(item: MarketItem) {
        viewModelScope.launch(coroutineExceptionHandler) {
            repository.deleteMarketItem(item.id)
            undoSkip(item.id)
            closePricePad()
        }
    }

    fun addImpromptuItem(listId: Long, name: String, qty: Double, unit: String) {
        if (name.isBlank() || qty <= 0.0) return
        viewModelScope.launch(coroutineExceptionHandler) {
            val newItem = MarketItem(
                listId = listId,
                name = name.trim(),
                quantity = qty,
                unit = unit
            )
            repository.addMarketItem(listId, newItem)
        }
    }

    private fun updateItemInDb(item: MarketItem) {
        viewModelScope.launch(coroutineExceptionHandler) {
            repository.updateMarketItem(item)
        }
    }

    /**
     * Completes market trip with carry-over bridge (Amendment 14).
     * Order of operations ensures no item loss:
     * 1. In-memory snapshot of unpurchased items.
     * 2. Complete active market trip in database.
     * 3. Create carry-over list for unpurchased items. If step 3 fails, trip completion
     *    is preserved and onCarryOverFailed delivers the snapshot for retry.
     */
    fun completeTripWithCarryOver(
        listId: Long,
        accountId: Long?,
        carryOverUnpurchased: Boolean = true,
        onFinished: (MarketCompletionResult) -> Unit,
        onCarryOverFailed: (unpurchasedSnapshot: List<MarketItem>) -> Unit
    ) {
        viewModelScope.launch(coroutineExceptionHandler) {
            val allLists = repository.allMarketLists.value
            val trip = allLists.find { it.list.id == listId }
            val items = trip?.items ?: emptyList()

            // 1. Snapshot unpurchased and skipped items
            val unpurchasedSnapshot = items.filter { !it.isPurchased || it.id in _skippedItemIds.value }.map {
                it.copy(
                    id = 0L,
                    listId = 0L,
                    amount = null,
                    isPurchased = false,
                    transactionId = null
                )
            }

            // 2. Complete the active market list
            val result = repository.completeActiveMarketList(listId, accountId)

            // 3. Clear session skipped items
            _skippedItemIds.value = emptySet()
            savedStateHandle[SKIPPED_ITEMS_KEY] = emptyList<Long>()

            // 4. Carry-over if requested and unpurchased items exist
            if (result.success && carryOverUnpurchased && unpurchasedSnapshot.isNotEmpty()) {
                val baseTitle = trip?.list?.title ?: "বাজারের ফর্দ"
                val carryOverTitle = "$baseTitle – বাকি"
                val createSuccess = runCatching {
                    repository.createMarketListWithItems(carryOverTitle, unpurchasedSnapshot)
                }.getOrDefault(0L)

                if (createSuccess <= 0L) {
                    onCarryOverFailed(unpurchasedSnapshot)
                }
            }

            onFinished(result)
        }
    }
}
