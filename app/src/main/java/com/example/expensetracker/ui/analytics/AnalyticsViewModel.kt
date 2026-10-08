package com.example.expensetracker.ui.analytics

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.ai.GeminiAiService
import com.example.expensetracker.data.model.AiFinancialAudit
import com.example.expensetracker.data.model.AnalyticsOverview
import com.example.expensetracker.data.model.CategorySpend
import com.example.expensetracker.data.model.DailySpend
import com.example.expensetracker.data.model.FinancialSummary
import com.example.expensetracker.data.model.PaymentMethodSpend
import com.example.expensetracker.data.model.TimeFilter
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.data.model.UserProfile
import com.example.expensetracker.data.repository.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class AnalyticsBreakdownMode(val displayName: String) {
    CATEGORIES("Categories"),
    PAYMENT_METHODS("Payment Methods")
}

@OptIn(FlowPreview::class)
class AnalyticsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ExpenseRepository.getInstance(application)

    val userProfile: StateFlow<UserProfile> = repository.userProfile

    private val _timeFilter = MutableStateFlow(TimeFilter.THIS_MONTH)
    val timeFilter: StateFlow<TimeFilter> = _timeFilter.asStateFlow()

    private val _transactionType = MutableStateFlow(TransactionType.EXPENSE)
    val transactionType: StateFlow<TransactionType> = _transactionType.asStateFlow()

    private val _breakdownMode = MutableStateFlow(AnalyticsBreakdownMode.CATEGORIES)
    val breakdownMode: StateFlow<AnalyticsBreakdownMode> = _breakdownMode.asStateFlow()

    private val _categorySpends = MutableStateFlow<List<CategorySpend>>(emptyList())
    val categorySpends: StateFlow<List<CategorySpend>> = _categorySpends.asStateFlow()

    private val _paymentMethodSpends = MutableStateFlow<List<PaymentMethodSpend>>(emptyList())
    val paymentMethodSpends: StateFlow<List<PaymentMethodSpend>> = _paymentMethodSpends.asStateFlow()

    private val _dailySpends = MutableStateFlow<List<DailySpend>>(emptyList())
    val dailySpends: StateFlow<List<DailySpend>> = _dailySpends.asStateFlow()

    private val _summary = MutableStateFlow(FinancialSummary())
    val summary: StateFlow<FinancialSummary> = _summary.asStateFlow()

    private val _overview = MutableStateFlow(AnalyticsOverview())
    val overview: StateFlow<AnalyticsOverview> = _overview.asStateFlow()

    init {
        viewModelScope.launch {
            merge(
                repository.transactions,
                repository.categories,
                repository.userProfile
            ).debounce(200L).collect {
                loadAnalytics()
            }
        }
    }

    fun setTimeFilter(filter: TimeFilter) {
        _timeFilter.value = filter
        loadAnalytics()
    }

    fun setTransactionType(type: TransactionType) {
        _transactionType.value = type
        loadAnalytics()
    }

    fun setBreakdownMode(mode: AnalyticsBreakdownMode) {
        _breakdownMode.value = mode
    }

    fun loadAnalytics() {
        viewModelScope.launch {
            val filter = _timeFilter.value
            val type = _transactionType.value

            val catDeferred = async(Dispatchers.IO) {
                repository.getCategorySpends(filter, type)
            }
            val payDeferred = async(Dispatchers.IO) {
                repository.getPaymentMethodSpends(filter, type)
            }
            val dailyDeferred = async(Dispatchers.IO) {
                repository.getDailySpends(filter)
            }
            val sumDeferred = async(Dispatchers.IO) {
                repository.getFinancialSummary(filter)
            }
            val overDeferred = async(Dispatchers.IO) {
                repository.getAnalyticsOverview(filter, type)
            }

            _categorySpends.value = catDeferred.await()
            _paymentMethodSpends.value = payDeferred.await()
            _dailySpends.value = dailyDeferred.await()
            _summary.value = sumDeferred.await()
            _overview.value = overDeferred.await()
        }
    }
}
