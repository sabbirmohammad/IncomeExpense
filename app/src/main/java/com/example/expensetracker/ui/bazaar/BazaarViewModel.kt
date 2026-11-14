package com.example.expensetracker.ui.bazaar

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.data.model.MarketListWithItems
import com.example.expensetracker.data.model.MasterProduct
import com.example.expensetracker.data.model.ProductPriceInsight
import com.example.expensetracker.data.model.UserProfile
import com.example.expensetracker.data.repository.ExpenseRepository
import com.example.expensetracker.data.repository.addMarketItem
import com.example.expensetracker.data.repository.cloneMarketTrip
import com.example.expensetracker.data.repository.createMarketListWithItems
import com.example.expensetracker.data.repository.deleteMarketItem
import com.example.expensetracker.data.repository.deleteMarketList
import com.example.expensetracker.data.repository.duplicateMarketList
import com.example.expensetracker.data.repository.getMarketListBudget
import com.example.expensetracker.data.repository.setMarketListBudget
import com.example.expensetracker.data.repository.updateCommodityStall
import com.example.expensetracker.data.repository.updateMarketItem
import com.example.expensetracker.data.repository.updateMarketListTitle
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BazaarViewModel(
    application: Application,
    repoOverride: ExpenseRepository? = null
) : AndroidViewModel(application) {

    private val repository = repoOverride ?: ExpenseRepository.getInstance(application)

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        android.util.Log.e("BazaarViewModel", "Error in bazaar operation", throwable)
    }

    // Repository state flows
    val allMarketLists: StateFlow<List<MarketListWithItems>> = repository.allMarketLists
    val isMarketListsLoaded: StateFlow<Boolean> = repository.isMarketListsLoaded
    val activeMarketLists: StateFlow<List<MarketListWithItems>> = repository.activeMarketLists
    val activeMarketList: StateFlow<MarketListWithItems?> = repository.activeMarketList
    val lastPurchasedRates: StateFlow<Map<String, Pair<Double, String>>> = repository.lastPurchasedRates
    val productPriceInsights: StateFlow<Map<String, ProductPriceInsight>> = repository.productPriceInsights
    val masterProducts: StateFlow<List<MasterProduct>> = repository.masterProducts
    val stapleProducts: StateFlow<List<MasterProduct>> = repository.stapleProducts
    val isPrivacyMode: StateFlow<Boolean> = repository.isPrivacyMode
    val userProfile: StateFlow<UserProfile> = repository.userProfile

    // Derived: master products map keyed by normalized name
    val masterProductsMap: StateFlow<Map<String, MasterProduct>> = masterProducts.map { list: List<MasterProduct> ->
        list.associateBy { it.normalizedName }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap<String, MasterProduct>())

    // Derived: completed trips (latest 10)
    val completedTrips: StateFlow<List<MarketListWithItems>> = allMarketLists.map { lists: List<MarketListWithItems> ->
        lists.filter { it.list.isCompleted }.sortedByDescending { it.list.createdAt }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList<MarketListWithItems>())

    // In-memory budget caps cache
    private val _budgetCaps = MutableStateFlow<Map<Long, Double>>(emptyMap())
    val budgetCaps: StateFlow<Map<Long, Double>> = _budgetCaps.asStateFlow()

    // Hub-level snackbar messages that survive screen pops
    private val _hubSnackbarMessage = MutableStateFlow<String?>(null)
    val hubSnackbarMessage: StateFlow<String?> = _hubSnackbarMessage.asStateFlow()

    fun showHubSnackbar(message: String) {
        _hubSnackbarMessage.value = message
    }

    fun clearHubSnackbar() {
        _hubSnackbarMessage.value = null
    }

    // Locally pending deleted list/item IDs for deferred deletion
    private val _pendingDeletedListIds = MutableStateFlow<Set<Long>>(emptySet())
    val pendingDeletedListIds: StateFlow<Set<Long>> = _pendingDeletedListIds.asStateFlow()

    private val _pendingDeletedItemIds = MutableStateFlow<Set<Long>>(emptySet())
    val pendingDeletedItemIds: StateFlow<Set<Long>> = _pendingDeletedItemIds.asStateFlow()

    private var titleDebounceJob: Job? = null

    init {
        loadBudgetCaps()
    }

    private fun loadBudgetCaps() {
        viewModelScope.launch(coroutineExceptionHandler) {
            allMarketLists.collect { lists ->
                val map = mutableMapOf<Long, Double>()
                for (list in lists) {
                    val cap = repository.getMarketListBudget(list.list.id)
                    if (cap != null && cap > 0.0) {
                        map[list.list.id] = cap
                    }
                }
                _budgetCaps.value = map
            }
        }
    }

    fun getBudgetCap(listId: Long): Double? {
        return _budgetCaps.value[listId] ?: repository.getMarketListBudget(listId)
    }

    fun setBudgetCap(listId: Long, budget: Double?) {
        repository.setMarketListBudget(listId, budget)
        val current = _budgetCaps.value.toMutableMap()
        if (budget != null && budget > 0.0) {
            current[listId] = budget
        } else {
            current.remove(listId)
        }
        _budgetCaps.value = current
    }

    fun togglePrivacyMode() {
        repository.togglePrivacyMode()
    }

    /**
     * Creates a new market list with optional budget cap and items.
     */
    fun createMarketList(
        title: String,
        items: List<MarketItem> = emptyList(),
        budgetCap: Double? = null,
        onCreated: ((Long) -> Unit)? = null
    ) {
        viewModelScope.launch(coroutineExceptionHandler) {
            val listId = repository.createMarketListWithItems(title, items)
            if (listId > 0 && budgetCap != null && budgetCap > 0.0) {
                setBudgetCap(listId, budgetCap)
            }
            onCreated?.invoke(listId)
        }
    }

    /**
     * Debounced list title update (~400ms).
     */
    fun updateMarketListTitleDebounced(listId: Long, newTitle: String) {
        titleDebounceJob?.cancel()
        titleDebounceJob = viewModelScope.launch(coroutineExceptionHandler) {
            delay(400L)
            repository.updateMarketListTitle(listId, newTitle)
        }
    }

    fun addMarketItem(listId: Long, item: MarketItem) {
        viewModelScope.launch(coroutineExceptionHandler) {
            repository.addMarketItem(listId, item)
        }
    }

    fun updateMarketItem(item: MarketItem) {
        viewModelScope.launch(coroutineExceptionHandler) {
            repository.updateMarketItem(item)
        }
    }

    /**
     * Deletes a market item with a 5-second deferred deletion window.
     * If user triggers undo in snackbar, deletion is cancelled.
     */
    fun deleteMarketItemWithUndo(
        item: MarketItem,
        showSnackbar: suspend (message: String, actionLabel: String) -> Boolean
    ) {
        viewModelScope.launch(coroutineExceptionHandler) {
            _pendingDeletedItemIds.value = _pendingDeletedItemIds.value + item.id
            val isBangla = userProfile.value.appLanguage != "en"
            val msg = if (isBangla) "\"${item.name}\" মুছে ফেলা হয়েছে" else "\"${item.name}\" removed"
            val undoLabel = if (isBangla) "পূর্বাবস্থায় ফেরত" else "Undo"

            val undone = showSnackbar(msg, undoLabel)
            if (undone) {
                // Cancel deletion
                _pendingDeletedItemIds.value = _pendingDeletedItemIds.value - item.id
            } else {
                // Perform real DB deletion
                _pendingDeletedItemIds.value = _pendingDeletedItemIds.value - item.id
                repository.deleteMarketItem(item.id)
            }
        }
    }

    /**
     * Deletes a market list with a 5-second deferred deletion window.
     */
    fun deleteMarketListWithUndo(
        listId: Long,
        listTitle: String,
        showSnackbar: suspend (message: String, actionLabel: String) -> Boolean,
        onDeletedFinal: (() -> Unit)? = null
    ) {
        viewModelScope.launch(coroutineExceptionHandler) {
            _pendingDeletedListIds.value = _pendingDeletedListIds.value + listId
            val isBangla = userProfile.value.appLanguage != "en"
            val msg = if (isBangla) "\"$listTitle\" ফর্দ মুছে ফেলা হয়েছে" else "\"$listTitle\" list deleted"
            val undoLabel = if (isBangla) "পূর্বাবস্থায় ফেরত" else "Undo"

            val undone = showSnackbar(msg, undoLabel)
            if (undone) {
                _pendingDeletedListIds.value = _pendingDeletedListIds.value - listId
            } else {
                _pendingDeletedListIds.value = _pendingDeletedListIds.value - listId
                repository.deleteMarketList(listId)
                onDeletedFinal?.invoke()
            }
        }
    }

    /**
     * Duplicates a list, copying items with price/purchased state cleared.
     */
    fun duplicateMarketList(listId: Long, onDuplicated: ((Long) -> Unit)? = null) {
        viewModelScope.launch(coroutineExceptionHandler) {
            val newListId = repository.duplicateMarketList(listId)
            onDuplicated?.invoke(newListId)
        }
    }

    /**
     * Clones a past trip into a new active list with quantities/units kept and prices cleared.
     */
    fun cloneTrip(sourceListId: Long, onCloned: ((Long) -> Unit)? = null) {
        viewModelScope.launch(coroutineExceptionHandler) {
            val isBn = userProfile.value.appLanguage != "en"
            val newListId = repository.cloneMarketTrip(sourceListId, isBn)
            onCloned?.invoke(newListId)
        }
    }

    /**
     * Updates stall override in master_products.category (zero-schema).
     */
    fun updateCommodityStall(productName: String, newCategory: String) {
        viewModelScope.launch(coroutineExceptionHandler) {
            repository.updateCommodityStall(productName, newCategory)
        }
    }
}
