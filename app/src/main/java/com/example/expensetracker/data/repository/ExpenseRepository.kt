package com.example.expensetracker.data.repository

import android.content.Context
import com.example.expensetracker.data.local.ExpenseDatabaseHelper
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.AnalyticsOverview
import com.example.expensetracker.data.model.BackupPayload
import com.example.expensetracker.data.model.BillSplitData
import com.example.expensetracker.data.model.Budget
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.data.model.CategorySpend
import com.example.expensetracker.data.model.DailySpend
import com.example.expensetracker.data.model.DebtBookSummary
import com.example.expensetracker.data.model.FinancialSummary
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.data.model.MarketList
import com.example.expensetracker.data.model.MarketListWithItems
import com.example.expensetracker.data.model.MarketCompletionResult
import com.example.expensetracker.data.model.Payable
import com.example.expensetracker.data.model.PayablesSummary
import com.example.expensetracker.data.model.PaymentMethod
import com.example.expensetracker.data.model.PaymentMethodSpend
import com.example.expensetracker.data.model.Receivable
import com.example.expensetracker.data.model.ReceivablesSummary
import com.example.expensetracker.data.model.RecurringBill
import com.example.expensetracker.data.model.SavingsGoal
import com.example.expensetracker.data.model.TimeFilter
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.data.model.UserProfile
import com.example.expensetracker.data.model.toFinancialSummary
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import com.example.expensetracker.ui.components.DateTimeHelper
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ExpenseRepository private constructor(context: Context) {

    private val context: Context = context.applicationContext
    private val dbHelper = ExpenseDatabaseHelper(this.context)
    private val jsonFormatter = Json { prettyPrint = true; ignoreUnknownKeys = true; encodeDefaults = true }

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

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _isPrivacyMode = MutableStateFlow(false)
    val isPrivacyMode: StateFlow<Boolean> = _isPrivacyMode.asStateFlow()

    private val _budgets = MutableStateFlow<List<Budget>>(emptyList())
    val budgets: StateFlow<List<Budget>> = _budgets.asStateFlow()

    private val _accounts = MutableStateFlow<List<Account>>(emptyList())
    val accounts: StateFlow<List<Account>> = _accounts.asStateFlow()

    private val _receivables = MutableStateFlow<List<Receivable>>(emptyList())
    val receivables: StateFlow<List<Receivable>> = _receivables.asStateFlow()

    private val _receivablesSummary = MutableStateFlow(ReceivablesSummary())
    val receivablesSummary: StateFlow<ReceivablesSummary> = _receivablesSummary.asStateFlow()

    private val _payables = MutableStateFlow<List<Payable>>(emptyList())
    val payables: StateFlow<List<Payable>> = _payables.asStateFlow()

    private val _payablesSummary = MutableStateFlow(PayablesSummary())
    val payablesSummary: StateFlow<PayablesSummary> = _payablesSummary.asStateFlow()

    private val _debtBookSummary = MutableStateFlow(DebtBookSummary())
    val debtBookSummary: StateFlow<DebtBookSummary> = _debtBookSummary.asStateFlow()

    private val _savingsGoals = MutableStateFlow<List<SavingsGoal>>(emptyList())
    val savingsGoals: StateFlow<List<SavingsGoal>> = _savingsGoals.asStateFlow()

    private val _recurringBills = MutableStateFlow<List<RecurringBill>>(emptyList())
    val recurringBills: StateFlow<List<RecurringBill>> = _recurringBills.asStateFlow()

    private val _activeMarketList = MutableStateFlow<MarketListWithItems?>(null)
    val activeMarketList: StateFlow<MarketListWithItems?> = _activeMarketList.asStateFlow()

    private val _allMarketLists = MutableStateFlow<List<MarketListWithItems>>(emptyList())
    val allMarketLists: StateFlow<List<MarketListWithItems>> = _allMarketLists.asStateFlow()

    private val repositoryScope = kotlinx.coroutines.CoroutineScope(Dispatchers.IO + kotlinx.coroutines.SupervisorJob())

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
        _allMarketLists.value = dbHelper.getAllMarketLists()
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

    suspend fun saveBudget(budget: Budget): Long = withContext(Dispatchers.IO) {
        val id = dbHelper.saveBudget(budget)
        refreshBudgets()
        triggerAutoSync()
        id
    }

    suspend fun deleteBudget(id: Long): Boolean = withContext(Dispatchers.IO) {
        val rows = dbHelper.deleteBudget(id)
        refreshBudgets()
        triggerAutoSync()
        rows > 0
    }

    suspend fun hasBudgetsForMonth(month: Int, year: Int): Boolean = withContext(Dispatchers.IO) {
        dbHelper.hasBudgetsForMonth(month, year)
    }

    suspend fun copyBudgets(fromMonth: Int, fromYear: Int, toMonth: Int, toYear: Int): Boolean = withContext(Dispatchers.IO) {
        val success = dbHelper.copyBudgets(fromMonth, fromYear, toMonth, toYear)
        if (success) {
            refreshBudgets()
            triggerAutoSync()
        }
        success
    }

    fun getLastBudgetPromptKey(): String? {
        return dbHelper.getLastBudgetPromptKey()
    }

    fun markBudgetPromptHandled(key: String) {
        dbHelper.setLastBudgetPromptKey(key)
    }

    fun getLastBudgetNotifiedKey(): String? {
        return dbHelper.getLastBudgetNotifiedKey()
    }

    fun markBudgetNotified(key: String) {
        dbHelper.setLastBudgetNotifiedKey(key)
    }

    suspend fun checkShouldShowUpcomingBudgetPrompt(): Boolean = withContext(Dispatchers.IO) {
        val cal = Calendar.getInstance()
        val currentDay = cal.get(Calendar.DAY_OF_MONTH)
        val maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

        val (targetMonth, targetYear) = if (currentDay == maxDay) {
            val calNext = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, 1) }
            Pair(calNext.get(Calendar.MONTH), calNext.get(Calendar.YEAR))
        } else if (currentDay == 1) {
            Pair(cal.get(Calendar.MONTH), cal.get(Calendar.YEAR))
        } else {
            return@withContext false
        }

        val promptKey = "$targetYear-$targetMonth"
        val lastHandledKey = dbHelper.getLastBudgetPromptKey()
        if (lastHandledKey == promptKey) {
            return@withContext false
        }

        val hasBudgets = dbHelper.hasBudgetsForMonth(targetMonth, targetYear)
        !hasBudgets
    }

    suspend fun updateProfile(profile: UserProfile): Boolean = withContext(Dispatchers.IO) {
        val rows = dbHelper.updateUserProfile(profile)
        if (rows > 0) {
            refreshUserProfile()
            triggerAutoSync()
        }
        rows > 0
    }

    // --- Accounts ---
    suspend fun addAccount(account: Account): Long = withContext(Dispatchers.IO) {
        val id = dbHelper.insertAccount(account)
        refreshAccounts()
        triggerAutoSync()
        id
    }

    suspend fun updateAccount(account: Account): Boolean = withContext(Dispatchers.IO) {
        val rows = dbHelper.updateAccount(account)
        if (rows > 0) {
            refreshAccounts()
            refreshTransactions()
            refreshRecurringBills()
            triggerAutoSync()
        }
        rows > 0
    }

    suspend fun getAccountById(id: Long): Account? = withContext(Dispatchers.IO) {
        dbHelper.getAccountById(id)
    }

    suspend fun deleteAccount(id: Long): Boolean = withContext(Dispatchers.IO) {
        val rows = dbHelper.deleteAccount(id)
        if (rows > 0) {
            refreshAccounts()
            refreshTransactions()
            refreshRecurringBills()
            triggerAutoSync()
        }
        rows > 0
    }

    suspend fun transferBetweenAccounts(
        fromAccountId: Long,
        toAccountId: Long,
        amount: Double,
        note: String
    ): Boolean = withContext(Dispatchers.IO) {
        val success = dbHelper.transferBetweenAccounts(fromAccountId, toAccountId, amount, note)
        if (success) {
            refreshAccounts()
            refreshTransactions()
        }
        success
    }

    suspend fun getTransactionsForAccount(accountId: Long): List<Transaction> = withContext(Dispatchers.IO) {
        dbHelper.getTransactionsForAccount(accountId)
    }

    // --- Receivables (পাওনা টাকা) ---
    suspend fun addReceivable(receivable: Receivable, deductFromAccountId: Long? = null): Long = withContext(Dispatchers.IO) {
        val id = dbHelper.insertReceivable(receivable, deductFromAccountId)
        refreshReceivables()
        if (deductFromAccountId != null) {
            refreshAccounts()
            refreshTransactions()
        }
        id
    }

    suspend fun updateReceivable(receivable: Receivable): Boolean = withContext(Dispatchers.IO) {
        val rows = dbHelper.updateReceivable(receivable)
        refreshReceivables()
        rows > 0
    }

    suspend fun deleteReceivable(id: Long): Boolean = withContext(Dispatchers.IO) {
        val rows = dbHelper.deleteReceivable(id)
        refreshReceivables()
        rows > 0
    }

    suspend fun recordReceivablePayment(
        receivableId: Long,
        amountPaid: Double,
        depositToAccountId: Long?,
        note: String
    ): Boolean = withContext(Dispatchers.IO) {
        val success = dbHelper.recordReceivablePayment(receivableId, amountPaid, depositToAccountId, note)
        if (success) {
            refreshReceivables()
            refreshAccounts()
            refreshTransactions()
        }
        success
    }

    // --- Payables (দেনা টাকা) ---
    suspend fun addPayable(payable: Payable, depositToAccountId: Long? = null): Long = withContext(Dispatchers.IO) {
        val id = dbHelper.insertPayable(payable, depositToAccountId)
        refreshPayables()
        if (depositToAccountId != null) {
            refreshAccounts()
            refreshTransactions()
        }
        id
    }

    suspend fun updatePayable(payable: Payable): Boolean = withContext(Dispatchers.IO) {
        val rows = dbHelper.updatePayable(payable)
        refreshPayables()
        rows > 0
    }

    suspend fun deletePayable(id: Long): Boolean = withContext(Dispatchers.IO) {
        val rows = dbHelper.deletePayable(id)
        refreshPayables()
        rows > 0
    }

    suspend fun recordPayablePayment(
        payableId: Long,
        amountPaid: Double,
        deductFromAccountId: Long?,
        note: String
    ): Boolean = withContext(Dispatchers.IO) {
        val success = dbHelper.recordPayablePayment(payableId, amountPaid, deductFromAccountId, note)
        if (success) {
            refreshPayables()
            refreshAccounts()
            refreshTransactions()
        }
        success
    }

    // --- Savings Goals (সঞ্চয় লক্ষ্যমাত্রা) ---
    suspend fun addSavingsGoal(goal: SavingsGoal): Long = withContext(Dispatchers.IO) {
        val id = dbHelper.insertSavingsGoal(goal)
        refreshSavingsGoals()
        id
    }

    suspend fun updateSavingsGoal(goal: SavingsGoal): Boolean = withContext(Dispatchers.IO) {
        val rows = dbHelper.updateSavingsGoal(goal)
        refreshSavingsGoals()
        rows > 0
    }

    suspend fun deleteSavingsGoal(id: Long): Boolean = withContext(Dispatchers.IO) {
        val rows = dbHelper.deleteSavingsGoal(id)
        refreshSavingsGoals()
        rows > 0
    }

    suspend fun addSavingsContribution(
        goalId: Long,
        amount: Double,
        deductFromAccountId: Long? = null,
        note: String = ""
    ): Boolean = withContext(Dispatchers.IO) {
        val success = dbHelper.addSavingsContribution(goalId, amount, deductFromAccountId, note)
        if (success) {
            refreshSavingsGoals()
            if (deductFromAccountId != null) {
                refreshAccounts()
                refreshTransactions()
            }
        }
        success
    }

    suspend fun addRecurringBill(bill: RecurringBill): Long = withContext(Dispatchers.IO) {
        val id = dbHelper.insertRecurringBill(bill)
        refreshRecurringBills()
        id
    }

    suspend fun updateRecurringBill(bill: RecurringBill): Boolean = withContext(Dispatchers.IO) {
        val rows = dbHelper.updateRecurringBill(bill)
        refreshRecurringBills()
        rows > 0
    }

    suspend fun deleteRecurringBill(id: Long): Boolean = withContext(Dispatchers.IO) {
        val rows = dbHelper.deleteRecurringBill(id)
        refreshRecurringBills()
        rows > 0
    }

    suspend fun payRecurringBill(billId: Long, accountId: Long?, accountName: String?): Long = withContext(Dispatchers.IO) {
        val txId = dbHelper.markBillAsPaid(billId, accountId, accountName)
        if (txId > 0) {
            refreshRecurringBills()
            refreshTransactions()
            refreshAccounts()
        }
        txId
    }

    suspend fun executeBillSplit(splitData: BillSplitData): Boolean = withContext(Dispatchers.IO) {
        val success = dbHelper.executeBillSplit(splitData)
        if (success) {
            refreshTransactions()
            refreshAccounts()
            refreshReceivables()
        }
        success
    }

    suspend fun setAppLanguage(languageCode: String): Boolean = withContext(Dispatchers.IO) {
        val rows = dbHelper.updateProfileLanguage(languageCode)
        if (rows > 0) refreshUserProfile()
        rows > 0
    }

    suspend fun setGeminiApiKey(key: String?): Boolean = withContext(Dispatchers.IO) {
        val rows = dbHelper.updateGeminiApiKey(key?.trim()?.ifBlank { null })
        if (rows > 0) refreshUserProfile()
        rows > 0
    }

    suspend fun setAiInsightsEnabled(enabled: Boolean): Boolean = withContext(Dispatchers.IO) {
        val rows = dbHelper.updateAiInsightsEnabled(enabled)
        if (rows > 0) refreshUserProfile()
        rows > 0
    }

    suspend fun setAppTheme(theme: String): Boolean = withContext(Dispatchers.IO) {
        val rows = dbHelper.updateProfileTheme(theme)
        if (rows > 0) refreshUserProfile()
        rows > 0
    }

    suspend fun setBengaliNumeralsEnabled(enabled: Boolean): Boolean = withContext(Dispatchers.IO) {
        val rows = dbHelper.updateProfileBengaliNumerals(enabled)
        if (rows > 0) refreshUserProfile()
        rows > 0
    }

    suspend fun getAnonymizedFinancialContext(): String = withContext(Dispatchers.IO) {
        val profile = _userProfile.value
        val summary = getFinancialSummary(TimeFilter.THIS_MONTH)
        val topSpends = getCategorySpends(TimeFilter.THIS_MONTH, TransactionType.EXPENSE).take(5)
        val accounts = _accounts.value
        val bills = _recurringBills.value.filter { it.isActive }
        val recSummary = _receivablesSummary.value

        val currency = profile.currencySymbol
        val sb = StringBuilder()
        sb.appendLine("Current Financial Summary (This Month):")
        sb.appendLine("- Total Income: $currency ${summary.totalIncome}")
        sb.appendLine("- Total Expense: $currency ${summary.totalExpense}")
        sb.appendLine("- Net Balance: $currency ${summary.netBalance}")
        if (summary.budgetAmount > 0) {
            sb.appendLine("- Monthly Budget: $currency ${summary.budgetAmount} (Spent: ${String.format(Locale.US, "%.1f", summary.budgetPercent * 100)}%)")
            sb.appendLine("- Budget Remaining: $currency ${summary.budgetRemaining}")
            if (summary.budgetOverspent > 0) {
                sb.appendLine("- Budget Overspent: $currency ${summary.budgetOverspent}")
            }
        }
        if (topSpends.isNotEmpty()) {
            sb.appendLine("- Top Expense Categories:")
            topSpends.forEach { cs ->
                sb.appendLine("  * ${cs.categoryName}: $currency ${cs.totalAmount} (${String.format(Locale.US, "%.1f", cs.percentage * 100)}%)")
            }
        }
        if (accounts.isNotEmpty()) {
            val totalWallet = accounts.sumOf { it.currentBalance }
            sb.appendLine("- Total Available Across Accounts: $currency $totalWallet")
            accounts.forEach { acc ->
                sb.appendLine("  * ${acc.type.displayName}: $currency ${acc.currentBalance}")
            }
        }
        if (bills.isNotEmpty()) {
            val upcomingBills = bills.filter { it.getPaymentStatus() != com.example.expensetracker.data.model.BillPaymentStatus.PAID }
            if (upcomingBills.isNotEmpty()) {
                sb.appendLine("- Upcoming/Due Bills:")
                upcomingBills.forEach { b ->
                    sb.appendLine("  * ${b.title}: $currency ${b.amount} (Status: ${b.getPaymentStatus()})")
                }
            }
        }
        if (recSummary.remainingDue > 0) {
            sb.appendLine("- Money Owed by Others (Receivables): $currency ${recSummary.remainingDue}")
        }
        sb.toString()
    }

    suspend fun getFilteredTransactions(
        timeFilter: TimeFilter,
        type: TransactionType?,
        categoryId: Long?,
        query: String?
    ): List<Transaction> = withContext(Dispatchers.IO) {
        dbHelper.getFilteredTransactions(timeFilter, type, categoryId, query)
    }

    suspend fun getTransactionsBetween(
        startMillis: Long,
        endMillis: Long,
        type: TransactionType? = null,
        categoryId: Long? = null,
        query: String? = null
    ): List<Transaction> = withContext(Dispatchers.IO) {
        dbHelper.getTransactionsBetween(startMillis, endMillis, type, categoryId, query)
    }

    suspend fun getSummaryForTransactions(txs: List<Transaction>): FinancialSummary = withContext(Dispatchers.Default) {
        txs.toFinancialSummary()
    }

    suspend fun getFinancialSummary(filter: TimeFilter = TimeFilter.THIS_MONTH): FinancialSummary = withContext(Dispatchers.IO) {
        val cal = Calendar.getInstance()
        val currentBudgets = dbHelper.getBudgets(cal.get(Calendar.MONTH), cal.get(Calendar.YEAR))
        val overallBudget = currentBudgets.find { it.categoryId == null }?.amount
            ?: currentBudgets.sumOf { it.amount }

        dbHelper.getAggregatedFinancialSummary(filter, overallBudget)
    }

    suspend fun getCategorySpends(
        filter: TimeFilter = TimeFilter.THIS_MONTH,
        type: TransactionType = TransactionType.EXPENSE
    ): List<CategorySpend> = withContext(Dispatchers.IO) {
        dbHelper.getAggregatedCategorySpends(filter, type)
    }

    suspend fun getDailySpends(filter: TimeFilter = TimeFilter.THIS_WEEK): List<DailySpend> = withContext(Dispatchers.IO) {
        val txs = dbHelper.getFilteredTransactions(timeFilter = filter)
            .filter { !it.isAccountTransfer() && !it.isDebtFlow() }
        val sdf = SimpleDateFormat("EEE, d MMM", Locale.getDefault())
        val shortSdf = SimpleDateFormat("d MMM", Locale.getDefault())

        val dayMap = mutableMapOf<String, Pair<Double, Double>>() // DateLabel to (Income, Expense)
        val dateMillisMap = mutableMapOf<String, Long>()

        for (tx in txs) {
            val label = shortSdf.format(Date(tx.dateMillis))
            val current = dayMap.getOrDefault(label, Pair(0.0, 0.0))
            if (tx.type == TransactionType.INCOME) {
                dayMap[label] = Pair(current.first + tx.amount, current.second)
            } else {
                dayMap[label] = Pair(current.first, current.second + tx.amount)
            }
            if (!dateMillisMap.containsKey(label)) {
                dateMillisMap[label] = tx.dateMillis
            }
        }

        dayMap.map { (label, pair) ->
            DailySpend(
                dateMillis = dateMillisMap[label] ?: 0L,
                dayLabel = label,
                income = pair.first,
                expense = pair.second
            )
        }.sortedBy { it.dateMillis }
    }

    suspend fun getRecentDailySpends(daysCount: Int = 7): List<DailySpend> = withContext(Dispatchers.IO) {
        val sdf = SimpleDateFormat("EEE", Locale.getDefault())
        val cal = Calendar.getInstance()
        val result = ArrayList<DailySpend>(daysCount)

        // End of today
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        val endMillis = cal.timeInMillis

        // Start of (daysCount - 1) days ago
        cal.add(Calendar.DAY_OF_YEAR, -(daysCount - 1))
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val startMillis = cal.timeInMillis

        val txs = dbHelper.getTransactionsBetween(startMillis, endMillis)
            .filter { !it.isAccountTransfer() && !it.isDebtFlow() }

        val iterCal = Calendar.getInstance().apply { timeInMillis = startMillis }
        val dateObj = Date()
        for (i in 0 until daysCount) {
            val dayStart = iterCal.timeInMillis
            dateObj.time = dayStart
            val label = sdf.format(dateObj)

            iterCal.set(Calendar.HOUR_OF_DAY, 23)
            iterCal.set(Calendar.MINUTE, 59)
            iterCal.set(Calendar.SECOND, 59)
            iterCal.set(Calendar.MILLISECOND, 999)
            val dayEnd = iterCal.timeInMillis

            var inc = 0.0
            var exp = 0.0
            for (tx in txs) {
                if (tx.dateMillis in dayStart..dayEnd) {
                    if (tx.type == TransactionType.INCOME) inc += tx.amount
                    else if (tx.type == TransactionType.EXPENSE) exp += tx.amount
                }
            }

            result.add(
                DailySpend(
                    dateMillis = dayStart,
                    dayLabel = label,
                    income = inc,
                    expense = exp
                )
            )
            iterCal.add(Calendar.MILLISECOND, 1)
        }

        result
    }

    suspend fun getPaymentMethodSpends(
        filter: TimeFilter = TimeFilter.THIS_MONTH,
        type: TransactionType = TransactionType.EXPENSE
    ): List<PaymentMethodSpend> = withContext(Dispatchers.IO) {
        dbHelper.getAggregatedPaymentMethodSpends(filter, type)
    }

    suspend fun getAnalyticsOverview(
        filter: TimeFilter = TimeFilter.THIS_MONTH,
        type: TransactionType = TransactionType.EXPENSE
    ): AnalyticsOverview = withContext(Dispatchers.IO) {
        val currentTxs = dbHelper.getFilteredTransactions(timeFilter = filter)
            .filter { !it.isAccountTransfer() && !it.isDebtFlow() }
        val filteredTxs = currentTxs.filter { it.type == type }
        val totalIncome = currentTxs.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
        val totalExpense = currentTxs.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }

        val savingsRate = if (totalIncome > 0) {
            (((totalIncome - totalExpense) / totalIncome) * 100).toInt()
        } else 0

        val cal = Calendar.getInstance()
        val daysElapsed = when (filter) {
            TimeFilter.TODAY -> 1
            TimeFilter.THIS_WEEK -> {
                val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
                val firstDay = cal.firstDayOfWeek
                val diff = (dayOfWeek - firstDay + 7) % 7 + 1
                diff.coerceAtLeast(1)
            }
            TimeFilter.THIS_MONTH -> cal.get(Calendar.DAY_OF_MONTH).coerceAtLeast(1)
            TimeFilter.THIS_YEAR -> cal.get(Calendar.DAY_OF_YEAR).coerceAtLeast(1)
            TimeFilter.ALL, TimeFilter.CUSTOM -> 30
        }

        val dailyAvg = if (type == TransactionType.EXPENSE) {
            totalExpense / daysElapsed
        } else {
            totalIncome / daysElapsed
        }

        val highestTx = filteredTxs.maxByOrNull { it.amount }

        // Period-over-period comparison
        var prevPeriodSpend = 0.0
        when (filter) {
            TimeFilter.THIS_MONTH -> {
                val prevCal = (cal.clone() as Calendar).apply {
                    add(Calendar.MONTH, -1)
                    set(Calendar.DAY_OF_MONTH, 1)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                val prevStart = prevCal.timeInMillis
                val prevEndCal = (prevCal.clone() as Calendar).apply {
                    val maxDay = getActualMaximum(Calendar.DAY_OF_MONTH)
                    val targetDay = cal.get(Calendar.DAY_OF_MONTH).coerceAtMost(maxDay)
                    set(Calendar.DAY_OF_MONTH, targetDay)
                    set(Calendar.HOUR_OF_DAY, 23)
                    set(Calendar.MINUTE, 59)
                    set(Calendar.SECOND, 59)
                    set(Calendar.MILLISECOND, 999)
                }
                val prevEnd = prevEndCal.timeInMillis
                val prevTxs = dbHelper.getTransactionsBetween(prevStart, prevEnd, type = type)
                prevPeriodSpend = prevTxs.filter { !it.isAccountTransfer() && !it.isDebtFlow() }.sumOf { it.amount }
            }
            TimeFilter.THIS_WEEK -> {
                val weekStartCal = (cal.clone() as Calendar).apply {
                    set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                val dayOfWeekOffset = (cal.get(Calendar.DAY_OF_WEEK) - cal.firstDayOfWeek + 7) % 7
                val prevStartCal = (weekStartCal.clone() as Calendar).apply {
                    add(Calendar.WEEK_OF_YEAR, -1)
                }
                val prevEndCal = (prevStartCal.clone() as Calendar).apply {
                    add(Calendar.DAY_OF_YEAR, dayOfWeekOffset)
                    set(Calendar.HOUR_OF_DAY, 23)
                    set(Calendar.MINUTE, 59)
                    set(Calendar.SECOND, 59)
                    set(Calendar.MILLISECOND, 999)
                }
                val prevStart = prevStartCal.timeInMillis
                val prevEnd = prevEndCal.timeInMillis
                val prevTxs = dbHelper.getTransactionsBetween(prevStart, prevEnd, type = type)
                prevPeriodSpend = prevTxs.filter { !it.isAccountTransfer() && !it.isDebtFlow() }.sumOf { it.amount }
            }
            TimeFilter.TODAY -> {
                val yesterdayStartCal = (cal.clone() as Calendar).apply {
                    add(Calendar.DAY_OF_YEAR, -1)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                val prevStart = yesterdayStartCal.timeInMillis
                val prevEnd = prevStart + (24L * 60 * 60 * 1000) - 1
                val prevTxs = dbHelper.getTransactionsBetween(prevStart, prevEnd, type = type)
                prevPeriodSpend = prevTxs.filter { !it.isAccountTransfer() && !it.isDebtFlow() }.sumOf { it.amount }
            }
            TimeFilter.THIS_YEAR -> {
                val prevYearStartCal = (cal.clone() as Calendar).apply {
                    add(Calendar.YEAR, -1)
                    set(Calendar.DAY_OF_YEAR, 1)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                val prevStart = prevYearStartCal.timeInMillis
                val dayOfYear = cal.get(Calendar.DAY_OF_YEAR)
                val prevYearEndCal = (prevYearStartCal.clone() as Calendar).apply {
                    val maxDays = getActualMaximum(Calendar.DAY_OF_YEAR)
                    set(Calendar.DAY_OF_YEAR, dayOfYear.coerceAtMost(maxDays))
                    set(Calendar.HOUR_OF_DAY, 23)
                    set(Calendar.MINUTE, 59)
                    set(Calendar.SECOND, 59)
                    set(Calendar.MILLISECOND, 999)
                }
                val prevEnd = prevYearEndCal.timeInMillis
                val prevTxs = dbHelper.getTransactionsBetween(prevStart, prevEnd, type = type)
                prevPeriodSpend = prevTxs.filter { !it.isAccountTransfer() && !it.isDebtFlow() }.sumOf { it.amount }
            }
            else -> {
                prevPeriodSpend = 0.0
            }
        }

        val currentAmount = if (type == TransactionType.EXPENSE) totalExpense else totalIncome
        val periodChangePercent: Double? = if (prevPeriodSpend > 0) {
            ((currentAmount - prevPeriodSpend) / prevPeriodSpend) * 100.0
        } else null

        val isFavorable = if (type == TransactionType.EXPENSE) {
            (periodChangePercent ?: 0.0) <= 0.0
        } else {
            (periodChangePercent ?: 0.0) >= 0.0
        }

        AnalyticsOverview(
            dailyAverageSpend = dailyAvg,
            savingsRate = savingsRate,
            highestExpenseAmount = highestTx?.amount ?: 0.0,
            highestExpenseTitle = highestTx?.title ?: "None",
            totalTransactionsCount = filteredTxs.size,
            periodChangePercent = periodChangePercent,
            isPeriodChangeFavorable = isFavorable
        )
    }

    // --- JSON & CSV Export / Import ---
    suspend fun exportToJson(): String = withContext(Dispatchers.IO) {
        val payload = dbHelper.createBackupPayload()
        jsonFormatter.encodeToString(payload)
    }

    suspend fun exportToCsv(): String = withContext(Dispatchers.IO) {
        val txs = dbHelper.getAllTransactions()
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val sb = java.lang.StringBuilder()
        sb.append("ID,Date,Title,Type,Category,Amount,Payment Method,Notes\n")
        for (tx in txs) {
            val dateStr = sdf.format(Date(tx.dateMillis))
            val safeTitle = tx.title.replace(",", ";").replace("\"", "'")
            val safeNotes = tx.notes.replace(",", ";").replace("\"", "'")
            val safeCategory = tx.categoryName.replace(",", ";")
            sb.append("${tx.id},\"$dateStr\",\"$safeTitle\",${tx.type.name},\"$safeCategory\",${tx.amount},${tx.paymentMethod.name},\"$safeNotes\"\n")
        }
        sb.toString()
    }

    suspend fun restoreFromJson(jsonString: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val payload = jsonFormatter.decodeFromString<BackupPayload>(jsonString)
            val success = dbHelper.restoreFromBackup(payload)
            if (success) {
                refreshAll()
            }
            success
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // ==========================================
    // MARKET LIST & ITEM OPERATIONS
    // ==========================================

    suspend fun createMarketListWithItems(
        title: String,
        items: List<MarketItem>,
        createdAt: Long = System.currentTimeMillis()
    ): Long = withContext(Dispatchers.IO) {
        val finalTitle = if (title.isBlank() || title == "আজকের বাজার" || title == "বাজারের ফর্দ") {
            com.example.expensetracker.ui.components.DateTimeHelper.formatMarketTripTitle(createdAt, isBangla = true)
        } else {
            title
        }
        val listId = dbHelper.createMarketList(finalTitle, createdAt)
        if (listId > 0 && items.isNotEmpty()) {
            dbHelper.addMarketItems(listId, items)
        }
        refreshMarketList()
        listId
    }

    suspend fun addMarketItem(listId: Long, item: MarketItem): Long = withContext(Dispatchers.IO) {
        val itemId = dbHelper.addMarketItem(item.copy(listId = listId))
        refreshMarketList()
        itemId
    }

    suspend fun updateMarketItem(item: MarketItem): Boolean = withContext(Dispatchers.IO) {
        val success = dbHelper.updateMarketItem(item)
        refreshMarketList()
        success
    }

    suspend fun deleteMarketItem(itemId: Long): Boolean = withContext(Dispatchers.IO) {
        val success = dbHelper.deleteMarketItem(itemId)
        refreshMarketList()
        success
    }

    suspend fun markMarketItemPurchased(
        item: MarketItem,
        amount: Double,
        accountId: Long? = null
    ): Long = withContext(Dispatchers.IO) {
        // Find or fallback to Groceries category
        val categories = dbHelper.getAllCategories()
        val groceryCat = categories.find { 
            it.name.equals("Groceries", ignoreCase = true) || 
            it.name.equals("বাজার ও সদাই", ignoreCase = true) 
        }
        val catId = groceryCat?.id ?: 2L
        val catName = groceryCat?.name ?: "Groceries"
        val catIcon = groceryCat?.icon ?: "ShoppingCart"
        val catColor = groceryCat?.colorHex ?: "#2EC4B6"

        // Pick default account if not specified
        val accounts = dbHelper.getAllAccounts()
        val targetAccount = if (accountId != null) {
            accounts.find { it.id == accountId }
        } else {
            accounts.find { it.isDefault } ?: accounts.firstOrNull()
        }

        val qtyText = item.formatQuantity()
        val txTitle = "${item.name} ($qtyText)"
        val noteText = "বাজারের ফর্দ থেকে কেনা ($qtyText)"

        val newTx = Transaction(
            title = txTitle,
            amount = amount,
            type = TransactionType.EXPENSE,
            categoryId = catId,
            categoryName = catName,
            categoryIcon = catIcon,
            categoryColor = catColor,
            dateMillis = System.currentTimeMillis(),
            paymentMethod = when (targetAccount?.type) {
                com.example.expensetracker.data.model.AccountType.BANK -> PaymentMethod.BANK_TRANSFER
                com.example.expensetracker.data.model.AccountType.MFS -> PaymentMethod.BKASH
                else -> PaymentMethod.CASH
            },
            notes = noteText,
            accountId = targetAccount?.id,
            accountName = targetAccount?.name
        )

        val txId = dbHelper.insertTransaction(newTx)
        dbHelper.updateMarketItemPurchased(item.id, isPurchased = true, amount = amount, transactionId = txId)

        refreshTransactions()
        refreshAccounts()
        refreshBudgets()
        refreshMarketList()
        txId
    }

    suspend fun unmarkMarketItemPurchased(item: MarketItem): Boolean = withContext(Dispatchers.IO) {
        if (item.transactionId != null && item.transactionId > 0) {
            dbHelper.deleteTransaction(item.transactionId)
        }
        val success = dbHelper.updateMarketItemPurchased(item.id, isPurchased = false, amount = null, transactionId = null)
        refreshTransactions()
        refreshAccounts()
        refreshBudgets()
        refreshMarketList()
        success
    }

    suspend fun completeActiveMarketList(
        listId: Long,
        accountId: Long? = null
    ): MarketCompletionResult = withContext(Dispatchers.IO) {
        val items = dbHelper.getMarketItemsForList(listId)
        val allLists = dbHelper.getAllMarketLists()
        val currentTrip = allLists.find { it.list.id == listId }
        val marketList = currentTrip?.list

        // 1. Mark any unpurchased items as purchased when finishing the market trip
        for (item in items) {
            if (!item.isPurchased) {
                dbHelper.updateMarketItemPurchased(
                    itemId = item.id,
                    isPurchased = true,
                    amount = item.amount,
                    transactionId = item.transactionId
                )
            }
        }

        // 2. Re-fetch the updated items
        val updatedItems = dbHelper.getMarketItemsForList(listId)
        val purchasedItems = updatedItems.filter { it.isPurchased && (it.amount ?: 0.0) > 0 }
        val totalSpent = purchasedItems.sumOf { it.amount ?: 0.0 }
        val purchasedCount = updatedItems.count { it.isPurchased }

        // 3. Remove any old individual transactions created during tick to consolidate into one clean market trip transaction
        val existingTxIds = updatedItems.mapNotNull { it.transactionId }.filter { it > 0 }.distinct()
        for (oldTxId in existingTxIds) {
            dbHelper.deleteTransaction(oldTxId)
        }

        var newTxId: Long? = null
        if (totalSpent > 0) {
            // Find Groceries category
            val categories = dbHelper.getAllCategories()
            val groceryCat = categories.find {
                it.name.equals("Groceries", ignoreCase = true) ||
                it.name.equals("বাজার ও সদাই", ignoreCase = true)
            }
            val catId = groceryCat?.id ?: 2L
            val catName = groceryCat?.name ?: "Groceries"
            val catIcon = groceryCat?.icon ?: "ShoppingCart"
            val catColor = groceryCat?.colorHex ?: "#2EC4B6"

            // Target account
            val accounts = dbHelper.getAllAccounts()
            val targetAccount = if (accountId != null) {
                accounts.find { it.id == accountId }
            } else {
                accounts.find { it.isDefault } ?: accounts.firstOrNull()
            }

            val tripDate = marketList?.createdAt ?: System.currentTimeMillis()
            val isBn = userProfile.value.appLanguage != "en"
            val generatedTitle = DateTimeHelper.formatMarketTripTitle(tripDate, isBn, userProfile.value.useBengaliNumerals || isBn)
            val txTitle = if (marketList != null && marketList.title.isNotBlank() &&
                marketList.title != "আজকের বাজার" &&
                marketList.title != "বাজারের ফর্দ" &&
                marketList.title != "Market Checklist" &&
                !marketList.title.endsWith("বাজার") &&
                !marketList.title.endsWith("Market")
            ) {
                "$generatedTitle (${marketList.title})"
            } else {
                generatedTitle
            }

            val breakdownLines = updatedItems.filter { it.isPurchased }.joinToString("\n") { itm ->
                val amtStr = if (itm.amount != null && itm.amount > 0) {
                    val formattedNum = if (itm.amount % 1.0 == 0.0) "${itm.amount.toInt()}" else "${itm.amount}"
                    "৳$formattedNum"
                } else {
                    if (isBn) "মূল্য ছাড়া" else "No price"
                }
                "• ${itm.name} (${itm.formatQuantity()}) - $amtStr"
            }
            val noteHeader = if (isBn) "বাজারের বিবরণ (${purchasedCount}টি পণ্য):" else "Market Trip Details ($purchasedCount items):"
            val txNotes = "$noteHeader\n$breakdownLines"

            val newTx = Transaction(
                title = txTitle,
                amount = totalSpent,
                type = TransactionType.EXPENSE,
                categoryId = catId,
                categoryName = catName,
                categoryIcon = catIcon,
                categoryColor = catColor,
                dateMillis = tripDate,
                paymentMethod = when (targetAccount?.type) {
                    com.example.expensetracker.data.model.AccountType.BANK -> PaymentMethod.BANK_TRANSFER
                    com.example.expensetracker.data.model.AccountType.MFS -> PaymentMethod.BKASH
                    else -> PaymentMethod.CASH
                },
                notes = txNotes,
                accountId = targetAccount?.id,
                accountName = targetAccount?.name
            )

            newTxId = dbHelper.insertTransaction(newTx)

            // Update all purchased items in this list to point to newTxId
            for (item in updatedItems) {
                if (item.isPurchased) {
                    dbHelper.updateMarketItemPurchased(
                        itemId = item.id,
                        isPurchased = true,
                        amount = item.amount,
                        transactionId = newTxId
                    )
                }
            }
        }

        val success = dbHelper.completeMarketList(listId)

        refreshTransactions()
        refreshAccounts()
        refreshBudgets()
        refreshMarketList()

        MarketCompletionResult(
            success = success,
            totalSpent = totalSpent,
            purchasedCount = purchasedCount,
            transactionId = newTxId
        )
    }

    suspend fun deleteActiveMarketList(listId: Long): Boolean = withContext(Dispatchers.IO) {
        deleteMarketList(listId)
    }

    suspend fun deleteMarketList(listId: Long): Boolean = withContext(Dispatchers.IO) {
        val items = dbHelper.getMarketItemsForList(listId)
        val txIds = items.mapNotNull { it.transactionId }.filter { it > 0 }.distinct()
        for (txId in txIds) {
            dbHelper.deleteTransaction(txId)
        }
        val success = dbHelper.deleteMarketList(listId)
        refreshTransactions()
        refreshAccounts()
        refreshBudgets()
        refreshMarketList()
        success
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
