package com.example.expensetracker.ui.bazaar

import android.app.Application
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.common.UnitConverterEngine
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
    private val savedStateHandle: SavedStateHandle,
    repoOverride: ExpenseRepository? = null,
    prefsOverride: android.content.SharedPreferences? = null
) : AndroidViewModel(application) {

    private val repository = repoOverride ?: ExpenseRepository.getInstance(application)
    private val prefs = prefsOverride ?: try {
        application.getSharedPreferences("bazaar_shop_preferences", android.content.Context.MODE_PRIVATE)
    } catch (_: Exception) {
        null
    }

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

    // 2. Auto-advance setting (defaults to true per Amendment 12, persisted in SharedPreferences across restarts)
    private val AUTO_ADVANCE_KEY = "shop_auto_advance_enabled"
    private val PREF_AUTO_ADVANCE = "bazaar_auto_advance_enabled"
    private val PREF_PRICING_MODE_PREFIX = "pricing_mode_"
    private val PREF_LAST_USED_ACCOUNT = "bazaar_last_used_account_id"

    private val _isAutoAdvanceEnabled = MutableStateFlow(
        savedStateHandle.get<Boolean>(AUTO_ADVANCE_KEY)
            ?: (try { prefs?.getBoolean(PREF_AUTO_ADVANCE, true) } catch (_: Exception) { null } ?: true)
    )
    val isAutoAdvanceEnabled: StateFlow<Boolean> = _isAutoAdvanceEnabled.asStateFlow()

    // 3. Pricing mode memory per item (Per unit vs Total paid per Amendment 9)
    private val _itemPricingMode = MutableStateFlow<Map<Long, PricingMode>>(emptyMap())
    val itemPricingMode: StateFlow<Map<Long, PricingMode>> = _itemPricingMode.asStateFlow()

    // 4. Pending carry-over snapshot for failure recovery and retry
    private val _pendingCarryOverSnapshot = MutableStateFlow<Pair<String, List<MarketItem>>?>(null)
    val pendingCarryOverSnapshot: StateFlow<Pair<String, List<MarketItem>>?> = _pendingCarryOverSnapshot.asStateFlow()

    // 5. Highlighted item for auto-advance visual pulsing
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
        try {
            prefs?.edit { putBoolean(PREF_AUTO_ADVANCE, enabled) }
        } catch (_: Exception) {}
    }

    fun getPricingModeForItem(productName: String): PricingMode {
        val norm = UnitConverterEngine.normalizeProductName(productName)
        val saved = try {
            prefs?.getString("$PREF_PRICING_MODE_PREFIX$norm", null)
        } catch (_: Exception) {
            null
        }
        return if (saved == PricingMode.TOTAL_PAID.name) PricingMode.TOTAL_PAID else PricingMode.PER_UNIT
    }

    fun savePricingModeForItem(productName: String, mode: PricingMode) {
        val norm = UnitConverterEngine.normalizeProductName(productName)
        try {
            prefs?.edit { putString("$PREF_PRICING_MODE_PREFIX$norm", mode.name) }
        } catch (_: Exception) {}
    }

    fun retryPendingCarryOver(onSuccess: () -> Unit, onFailure: () -> Unit) {
        val pending = _pendingCarryOverSnapshot.value ?: return
        viewModelScope.launch(coroutineExceptionHandler) {
            val createdId = runCatching {
                repository.createMarketListWithItems(pending.first, pending.second)
            }.getOrDefault(0L)
            if (createdId > 0L) {
                _pendingCarryOverSnapshot.value = null
                onSuccess()
            } else {
                onFailure()
            }
        }
    }

    fun dismissPendingCarryOver() {
        _pendingCarryOverSnapshot.value = null
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

        // Remember pricing mode for this item (in-session and persisted across trips)
        val modeMap = _itemPricingMode.value.toMutableMap()
        modeMap[item.id] = mode
        _itemPricingMode.value = modeMap
        savePricingModeForItem(item.name, mode)

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

    fun getLastUsedAccountId(): Long? {
        val id = try {
            prefs?.getLong(PREF_LAST_USED_ACCOUNT, -1L) ?: -1L
        } catch (_: Exception) {
            -1L
        }
        return if (id > 0L) id else null
    }

    /**
     * Completes market trip with full checkout parameters and carry-over bridge.
     */
    fun completeTripWithFullCheckout(
        listId: Long,
        accountId: Long?,
        customTotalAmount: Double?,
        otherCosts: Double,
        customDateMillis: Long?,
        customNote: String?,
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

            // 2. Complete the active market list with full custom details
            val result = repository.completeActiveMarketList(
                listId = listId,
                accountId = accountId,
                customTotalAmount = customTotalAmount,
                otherCosts = otherCosts,
                customDateMillis = customDateMillis,
                customNote = customNote
            )

            // Save last-used account id
            if (accountId != null && accountId > 0L) {
                try {
                    prefs?.edit { putLong(PREF_LAST_USED_ACCOUNT, accountId) }
                } catch (_: Exception) {}
            }

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
                    _pendingCarryOverSnapshot.value = Pair(carryOverTitle, unpurchasedSnapshot)
                    onCarryOverFailed(unpurchasedSnapshot)
                } else {
                    _pendingCarryOverSnapshot.value = null
                }
            }

            onFinished(result)
        }
    }

    /**
     * Backward-compatible overload for simple callers.
     */
    fun completeTripWithCarryOver(
        listId: Long,
        accountId: Long?,
        carryOverUnpurchased: Boolean = true,
        onFinished: (MarketCompletionResult) -> Unit,
        onCarryOverFailed: (unpurchasedSnapshot: List<MarketItem>) -> Unit
    ) {
        completeTripWithFullCheckout(
            listId = listId,
            accountId = accountId,
            customTotalAmount = null,
            otherCosts = 0.0,
            customDateMillis = null,
            customNote = null,
            carryOverUnpurchased = carryOverUnpurchased,
            onFinished = onFinished,
            onCarryOverFailed = onCarryOverFailed
        )
    }
}
