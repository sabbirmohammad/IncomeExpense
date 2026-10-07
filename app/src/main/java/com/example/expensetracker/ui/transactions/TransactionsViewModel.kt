package com.example.expensetracker.ui.transactions

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.data.model.TimeFilter
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.data.model.UserProfile
import com.example.expensetracker.data.repository.ExpenseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class TransactionsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ExpenseRepository.getInstance(application)

    val categories: StateFlow<List<Category>> = repository.categories
    val userProfile: StateFlow<UserProfile> = repository.userProfile
    val marketLists: StateFlow<List<com.example.expensetracker.data.model.MarketListWithItems>> = repository.allMarketLists

    private val _filteredTransactions = MutableStateFlow<List<Transaction>>(emptyList())
    val filteredTransactions: StateFlow<List<Transaction>> = _filteredTransactions.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedType = MutableStateFlow<TransactionType?>(null) // null = ALL
    val selectedType: StateFlow<TransactionType?> = _selectedType.asStateFlow()

    private val _selectedCategoryId = MutableStateFlow<Long?>(null)
    val selectedCategoryId: StateFlow<Long?> = _selectedCategoryId.asStateFlow()

    private val _selectedTimeFilter = MutableStateFlow(TimeFilter.ALL)
    val selectedTimeFilter: StateFlow<TimeFilter> = _selectedTimeFilter.asStateFlow()

    private val _customDateRange = MutableStateFlow<Pair<Long, Long>?>(null)
    val customDateRange: StateFlow<Pair<Long, Long>?> = _customDateRange.asStateFlow()

    private val _isMarketTripsView = MutableStateFlow(false)
    val isMarketTripsView: StateFlow<Boolean> = _isMarketTripsView.asStateFlow()

    fun setMarketTripsView(enabled: Boolean) {
        _isMarketTripsView.value = enabled
    }

    fun deleteMarketTrip(listId: Long) {
        viewModelScope.launch {
            repository.deleteMarketList(listId)
        }
    }

    init {
        val debouncedQuery = _searchQuery
            .debounce(300L)
            .distinctUntilChanged()

        combine(
            debouncedQuery,
            _selectedType,
            _selectedCategoryId,
            _selectedTimeFilter,
            repository.transactions
        ) { query, type, catId, timeFilter, _ ->
            val customRange = _customDateRange.value
            if (timeFilter == TimeFilter.CUSTOM && customRange != null) {
                repository.getTransactionsBetween(
                    startMillis = customRange.first,
                    endMillis = customRange.second,
                    type = type,
                    categoryId = catId,
                    query = query
                )
            } else {
                repository.getFilteredTransactions(
                    timeFilter = timeFilter,
                    type = type,
                    categoryId = catId,
                    query = query
                )
            }
        }.flowOn(Dispatchers.IO).onEach { result ->
            _filteredTransactions.value = result
        }.launchIn(viewModelScope)

        viewModelScope.launch {
            repository.categories.collect { cats ->
                val selectedId = _selectedCategoryId.value
                if (selectedId != null && cats.none { it.id == selectedId }) {
                    _selectedCategoryId.value = null
                }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun setTypeFilter(type: TransactionType?) {
        _selectedType.value = type
    }

    fun setCategoryFilter(categoryId: Long?) {
        _selectedCategoryId.value = categoryId
    }

    fun setTimeFilter(filter: TimeFilter) {
        _selectedTimeFilter.value = filter
        if (filter != TimeFilter.CUSTOM) {
            _customDateRange.value = null
        }
    }

    fun setCustomDateRange(startMillis: Long, endMillis: Long) {
        _customDateRange.value = Pair(startMillis, endMillis)
        _selectedTimeFilter.value = TimeFilter.CUSTOM
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction.id)
        }
    }

    fun restoreTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.addTransaction(transaction)
        }
    }

    fun applyFilters() {
        viewModelScope.launch {
            val customRange = _customDateRange.value
            val filter = _selectedTimeFilter.value
            _filteredTransactions.value = if (filter == TimeFilter.CUSTOM && customRange != null) {
                repository.getTransactionsBetween(
                    startMillis = customRange.first,
                    endMillis = customRange.second,
                    type = _selectedType.value,
                    categoryId = _selectedCategoryId.value,
                    query = _searchQuery.value
                )
            } else {
                repository.getFilteredTransactions(
                    timeFilter = filter,
                    type = _selectedType.value,
                    categoryId = _selectedCategoryId.value,
                    query = _searchQuery.value
                )
            }
        }
    }
}
