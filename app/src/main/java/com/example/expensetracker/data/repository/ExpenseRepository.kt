package com.example.expensetracker.data.repository

import android.content.Context
import com.example.expensetracker.data.local.ExpenseDatabaseHelper
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.Budget
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.data.model.DebtBookSummary
import com.example.expensetracker.data.model.MarketListWithItems
import com.example.expensetracker.data.model.Payable
import com.example.expensetracker.data.model.PayablesSummary
import com.example.expensetracker.data.model.Receivable
import com.example.expensetracker.data.model.ReceivablesSummary
import com.example.expensetracker.data.model.RecurringBill
import com.example.expensetracker.data.model.SavingsGoal
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.data.model.UserProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.util.Calendar

class ExpenseRepository private constructor(context: Context) {

    internal val context: Context = context.applicationContext
    internal val dbHelper = ExpenseDatabaseHelper(this.context)
    internal val jsonFormatter = Json { prettyPrint = true; ignoreUnknownKeys = true; encodeDefaults = true }

    fun triggerAutoSync() {
        try {
            val profile = _userProfile.value
            if (profile.isAutoSyncEnabled && !profile.googleAccountEmail.isNullOrBlank()) {
                val syncManager = com.example.expensetracker.data.drive.GoogleDriveSyncManager(this.context)
                syncManager.triggerAutoSync()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    internal val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    internal val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    internal val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    internal val _isPrivacyMode = MutableStateFlow(false)
    val isPrivacyMode: StateFlow<Boolean> = _isPrivacyMode.asStateFlow()

    internal val _budgets = MutableStateFlow<List<Budget>>(emptyList())
    val budgets: StateFlow<List<Budget>> = _budgets.asStateFlow()

    internal val _accounts = MutableStateFlow<List<Account>>(emptyList())
    val accounts: StateFlow<List<Account>> = _accounts.asStateFlow()

    internal val _receivables = MutableStateFlow<List<Receivable>>(emptyList())
    val receivables: StateFlow<List<Receivable>> = _receivables.asStateFlow()

    internal val _receivablesSummary = MutableStateFlow(ReceivablesSummary())
    val receivablesSummary: StateFlow<ReceivablesSummary> = _receivablesSummary.asStateFlow()

    internal val _payables = MutableStateFlow<List<Payable>>(emptyList())
    val payables: StateFlow<List<Payable>> = _payables.asStateFlow()

    internal val _payablesSummary = MutableStateFlow(PayablesSummary())
    val payablesSummary: StateFlow<PayablesSummary> = _payablesSummary.asStateFlow()

    internal val _debtBookSummary = MutableStateFlow(DebtBookSummary())
    val debtBookSummary: StateFlow<DebtBookSummary> = _debtBookSummary.asStateFlow()

    internal val _savingsGoals = MutableStateFlow<List<SavingsGoal>>(emptyList())
    val savingsGoals: StateFlow<List<SavingsGoal>> = _savingsGoals.asStateFlow()

    internal val _recurringBills = MutableStateFlow<List<RecurringBill>>(emptyList())
    val recurringBills: StateFlow<List<RecurringBill>> = _recurringBills.asStateFlow()

    internal val _activeMarketList = MutableStateFlow<MarketListWithItems?>(null)
    val activeMarketList: StateFlow<MarketListWithItems?> = _activeMarketList.asStateFlow()

    internal val _activeMarketLists = MutableStateFlow<List<MarketListWithItems>>(emptyList())
    val activeMarketLists: StateFlow<List<MarketListWithItems>> = _activeMarketLists.asStateFlow()

    internal val _allMarketLists = MutableStateFlow<List<MarketListWithItems>>(emptyList())
    val allMarketLists: StateFlow<List<MarketListWithItems>> = _allMarketLists.asStateFlow()

    internal val _lastPurchasedRates = MutableStateFlow<Map<String, Pair<Double, String>>>(emptyMap())
    val lastPurchasedRates: StateFlow<Map<String, Pair<Double, String>>> = _lastPurchasedRates.asStateFlow()

    internal val _productPriceInsights = MutableStateFlow<Map<String, com.example.expensetracker.data.model.ProductPriceInsight>>(emptyMap())
    val productPriceInsights: StateFlow<Map<String, com.example.expensetracker.data.model.ProductPriceInsight>> = _productPriceInsights.asStateFlow()

    private val repositoryScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    init {
        try {
            val profile = dbHelper.getUserProfile()
            _userProfile.value = profile
            _isPrivacyMode.value = profile.isPrivacyModeEnabled
        } catch (e: Exception) {
            e.printStackTrace()
        }
        repositoryScope.launch {
            refreshAll()
        }
    }

    fun refreshTransactions() {
        _transactions.value = dbHelper.getAllTransactions()
    }

    fun refreshCategories() {
        _categories.value = dbHelper.getAllCategories()
    }

    fun refreshUserProfile() {
        val profile = dbHelper.getUserProfile()
        _userProfile.value = profile
        _isPrivacyMode.value = profile.isPrivacyModeEnabled
    }

    fun togglePrivacyMode(): Boolean {
        val newMode = !_isPrivacyMode.value
        _isPrivacyMode.value = newMode
        dbHelper.setPrivacyModeEnabled(newMode)
        _userProfile.value = _userProfile.value.copy(isPrivacyModeEnabled = newMode)
        return newMode
    }

    fun refreshAccounts() {
        _accounts.value = dbHelper.getAllAccounts()
    }

    fun refreshReceivables() {
        _receivables.value = dbHelper.getAllReceivables()
        _receivablesSummary.value = dbHelper.getReceivablesSummary()
        _debtBookSummary.value = dbHelper.getDebtBookSummary()
    }

    fun refreshPayables() {
        _payables.value = dbHelper.getAllPayables()
        _payablesSummary.value = dbHelper.getPayablesSummary()
        _debtBookSummary.value = dbHelper.getDebtBookSummary()
    }

    fun refreshSavingsGoals() {
        _savingsGoals.value = dbHelper.getAllSavingsGoals()
    }

    fun refreshRecurringBills() {
        _recurringBills.value = dbHelper.getAllRecurringBills()
    }

    fun refreshBudgets() {
        val cal = Calendar.getInstance()
        _budgets.value = dbHelper.getBudgets(cal.get(Calendar.MONTH), cal.get(Calendar.YEAR))
    }

    fun refreshMarketList() {
        _activeMarketList.value = dbHelper.getActiveMarketList()
        _activeMarketLists.value = dbHelper.getActiveMarketLists()
        _allMarketLists.value = dbHelper.getAllMarketLists()
        refreshProductPrices()
    }

    fun refreshProductPrices() {
        val insights = dbHelper.getAllProductPriceInsights()
        _productPriceInsights.value = insights
        _lastPurchasedRates.value = insights.mapValues { Pair(it.value.currentUnitPrice, it.value.baseUnit) }
    }

    fun refreshAll() {
        refreshTransactions()
        refreshCategories()
        refreshUserProfile()
        refreshAccounts()
        refreshReceivables()
        refreshPayables()
        refreshSavingsGoals()
        refreshRecurringBills()
        refreshBudgets()
        refreshMarketList()
        refreshProductPrices()
    }

    suspend fun addTransaction(tx: Transaction): Long = withContext(Dispatchers.IO) {
        val id = dbHelper.insertTransaction(tx)
        refreshTransactions()
        refreshAccounts()
        refreshBudgets()
        triggerAutoSync()
        id
    }

    suspend fun updateTransaction(tx: Transaction): Boolean = withContext(Dispatchers.IO) {
        val rows = dbHelper.updateTransaction(tx)
        refreshTransactions()
        refreshAccounts()
        refreshBudgets()
        triggerAutoSync()
        rows > 0
    }

    suspend fun deleteTransaction(id: Long): Boolean = withContext(Dispatchers.IO) {
        val rows = dbHelper.deleteTransaction(id)
        refreshTransactions()
        refreshAccounts()
        refreshBudgets()
        triggerAutoSync()
        rows > 0
    }

    suspend fun getOrCreateCategoryByName(
        name: String,
        type: TransactionType,
        defaultIcon: String = "MoreHoriz",
        defaultColor: String = "#8B90A0"
    ): Category = withContext(Dispatchers.IO) {
        val cat = dbHelper.getOrCreateCategoryByName(name, type, defaultIcon, defaultColor)
        refreshCategories()
        cat
    }

    suspend fun addCategory(category: Category): Long = withContext(Dispatchers.IO) {
        val id = dbHelper.insertCategory(category)
        if (id > 0) {
            refreshCategories()
            triggerAutoSync()
        }
        id
    }

    suspend fun updateCategory(category: Category): Boolean = withContext(Dispatchers.IO) {
        val rows = dbHelper.updateCategory(category)
        if (rows > 0) {
            refreshCategories()
            refreshTransactions()
            refreshBudgets()
            refreshRecurringBills()
            triggerAutoSync()
        }
        rows > 0
    }

    suspend fun deleteCategory(id: Long): Boolean = withContext(Dispatchers.IO) {
        val rows = dbHelper.deleteCategory(id)
        if (rows > 0) {
            refreshCategories()
            refreshTransactions()
            refreshBudgets()
            refreshRecurringBills()
            triggerAutoSync()
        }
        rows > 0
    }

    companion object {
        @Volatile
        private var INSTANCE: ExpenseRepository? = null

        fun getInstance(context: Context): ExpenseRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ExpenseRepository(context).also { INSTANCE = it }
            }
        }
    }
}
