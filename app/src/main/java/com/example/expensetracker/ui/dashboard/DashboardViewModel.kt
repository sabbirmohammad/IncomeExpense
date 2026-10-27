package com.example.expensetracker.ui.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.data.model.FinancialSummary
import com.example.expensetracker.data.model.Receivable
import com.example.expensetracker.data.model.ReceivablesSummary
import com.example.expensetracker.data.model.TimeFilter
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.data.model.DebtBookSummary
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.data.model.MarketListWithItems
import com.example.expensetracker.data.model.SavingsGoal
import com.example.expensetracker.data.model.UserProfile
import com.example.expensetracker.data.repository.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(FlowPreview::class)
class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ExpenseRepository.getInstance(application)

    val transactions: StateFlow<List<Transaction>> = repository.transactions
    val categories: StateFlow<List<Category>> = repository.categories
    val userProfile: StateFlow<UserProfile> = repository.userProfile
    val isPrivacyMode: StateFlow<Boolean> = repository.isPrivacyMode
    val accounts: StateFlow<List<Account>> = repository.accounts
    val receivables: StateFlow<List<Receivable>> = repository.receivables
    val receivablesSummary: StateFlow<ReceivablesSummary> = repository.receivablesSummary
    val recurringBills: StateFlow<List<com.example.expensetracker.data.model.RecurringBill>> = repository.recurringBills
    val savingsGoals: StateFlow<List<SavingsGoal>> = repository.savingsGoals
    val debtBookSummary: StateFlow<DebtBookSummary> = repository.debtBookSummary
    val activeMarketList: StateFlow<MarketListWithItems?> = repository.activeMarketList
    val activeMarketLists: StateFlow<List<MarketListWithItems>> = repository.activeMarketLists
    val lastPurchasedRates: StateFlow<Map<String, Pair<Double, String>>> = repository.lastPurchasedRates

    private val _summary = MutableStateFlow(FinancialSummary())
    val summary: StateFlow<FinancialSummary> = _summary.asStateFlow()

    private val _selectedTimeFilter = MutableStateFlow(TimeFilter.THIS_MONTH)
    val selectedTimeFilter: StateFlow<TimeFilter> = _selectedTimeFilter.asStateFlow()

    private val _recentDailySpends = MutableStateFlow<List<com.example.expensetracker.data.model.DailySpend>>(emptyList())
    val recentDailySpends: StateFlow<List<com.example.expensetracker.data.model.DailySpend>> = _recentDailySpends.asStateFlow()

    private val _topCategorySpend = MutableStateFlow<com.example.expensetracker.data.model.CategorySpend?>(null)
    val topCategorySpend: StateFlow<com.example.expensetracker.data.model.CategorySpend?> = _topCategorySpend.asStateFlow()

    private val _categorySpends = MutableStateFlow<List<com.example.expensetracker.data.model.CategorySpend>>(emptyList())
    val categorySpends: StateFlow<List<com.example.expensetracker.data.model.CategorySpend>> = _categorySpends.asStateFlow()

    init {
        viewModelScope.launch {
            merge(
                repository.transactions,
                repository.categories,
                repository.budgets,
                repository.accounts,
                repository.userProfile
            ).debounce(200L).collect {
                loadSummary(_selectedTimeFilter.value)
            }
        }
    }

    fun setTimeFilter(filter: TimeFilter) {
        _selectedTimeFilter.value = filter
        loadSummary(filter)
    }

    fun togglePrivacyMode() {
        repository.togglePrivacyMode()
    }

    private fun loadSummary(filter: TimeFilter) {
        viewModelScope.launch {
            val summary = withContext(Dispatchers.IO) { repository.getFinancialSummary(filter) }
            val daily = withContext(Dispatchers.IO) { repository.getRecentDailySpends(daysCount = 7) }
            val spends = withContext(Dispatchers.IO) {
                repository.getCategorySpends(filter = filter, type = TransactionType.EXPENSE)
            }
            val filteredSpends = spends.filter { spend -> !spend.categoryName.equals("Loan & Debt Given", ignoreCase = true) }

            _summary.value = summary
            _recentDailySpends.value = daily
            _categorySpends.value = filteredSpends
            _topCategorySpend.value = filteredSpends.maxByOrNull { spend -> spend.totalAmount }
        }
    }

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        android.util.Log.e("DashboardViewModel", "Unhandled coroutine error", throwable)
    }

    fun addTransaction(tx: Transaction) {
        viewModelScope.launch(coroutineExceptionHandler) {
            repository.addTransaction(tx)
        }
    }

    fun updateTransaction(tx: Transaction) {
        viewModelScope.launch(coroutineExceptionHandler) {
            repository.updateTransaction(tx)
        }
    }

    fun deleteTransaction(id: Long) {
        viewModelScope.launch(coroutineExceptionHandler) {
            repository.deleteTransaction(id)
        }
    }

    fun executeBillSplit(splitData: com.example.expensetracker.data.model.BillSplitData) {
        viewModelScope.launch(coroutineExceptionHandler) {
            repository.executeBillSplit(splitData)
        }
    }

    fun transferFunds(fromAccountId: Long, toAccountId: Long, amount: Double, note: String) {
        viewModelScope.launch(coroutineExceptionHandler) {
            repository.transferBetweenAccounts(fromAccountId, toAccountId, amount, note)
        }
    }

    fun createMarketListWithBulk(title: String, items: List<MarketItem>) {
        viewModelScope.launch(coroutineExceptionHandler) {
            repository.createMarketListWithItems(title, items)
        }
    }

    fun addMarketItem(item: MarketItem, targetListId: Long? = null) {
        viewModelScope.launch(coroutineExceptionHandler) {
            val listId = targetListId ?: (if (item.listId > 0) item.listId else activeMarketList.value?.list?.id) ?: repository.createMarketListWithItems(
                title = com.example.expensetracker.ui.components.DateTimeHelper.formatMarketTripTitle(System.currentTimeMillis(), isBangla = true),
                items = emptyList()
            )
            repository.addMarketItem(listId, item)
        }
    }

    fun updateMarketListTitle(listId: Long, title: String) {
        viewModelScope.launch(coroutineExceptionHandler) {
            repository.updateMarketListTitle(listId, title)
        }
    }

    fun updateMarketItem(item: MarketItem) {
        viewModelScope.launch(coroutineExceptionHandler) {
            repository.updateMarketItem(item)
        }
    }

    fun deleteMarketItem(itemId: Long) {
        viewModelScope.launch(coroutineExceptionHandler) {
            repository.deleteMarketItem(itemId)
        }
    }

    fun markMarketItemPurchased(item: MarketItem, amount: Double) {
        viewModelScope.launch(coroutineExceptionHandler) {
            repository.markMarketItemPurchased(item, amount)
        }
    }

    fun unmarkMarketItemPurchased(item: MarketItem) {
        viewModelScope.launch(coroutineExceptionHandler) {
            repository.unmarkMarketItemPurchased(item)
        }
    }

    fun completeActiveMarketList(
        listId: Long,
        rollOverItems: List<MarketItem> = emptyList(),
        onCompleted: ((com.example.expensetracker.data.model.MarketCompletionResult) -> Unit)? = null
    ) {
        viewModelScope.launch(coroutineExceptionHandler) {
            val result = repository.completeActiveMarketList(listId)
            if (rollOverItems.isNotEmpty()) {
                val newItems = rollOverItems.map {
                    it.copy(id = 0, listId = 0, isPurchased = false, amount = null)
                }
                repository.createMarketListWithItems(
                    title = com.example.expensetracker.ui.components.DateTimeHelper.formatMarketTripTitle(System.currentTimeMillis(), isBangla = true),
                    items = newItems
                )
            }
            onCompleted?.invoke(result)
        }
    }

    fun deleteActiveMarketList(listId: Long) {
        viewModelScope.launch(coroutineExceptionHandler) {
            repository.deleteActiveMarketList(listId)
        }
    }
}
