package com.example.expensetracker.data.local

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.BackupPayload
import com.example.expensetracker.data.model.BillSplitData
import com.example.expensetracker.data.model.Budget
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.data.model.CategorySpend
import com.example.expensetracker.data.model.DebtBookSummary
import com.example.expensetracker.data.model.FinancialSummary
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.data.model.MarketListWithItems
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

class ExpenseDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = com.example.expensetracker.data.local.DATABASE_NAME
        const val DATABASE_VERSION = com.example.expensetracker.data.local.DATABASE_VERSION
        const val PREFS_NAME = com.example.expensetracker.data.local.PREFS_NAME
        const val PREF_KEY_GEMINI_API_KEY = com.example.expensetracker.data.local.PREF_KEY_GEMINI_API_KEY
        const val PREF_KEY_PRIVACY_MODE = com.example.expensetracker.data.local.PREF_KEY_PRIVACY_MODE
        const val PREF_KEY_LAST_BUDGET_PROMPT = com.example.expensetracker.data.local.PREF_KEY_LAST_BUDGET_PROMPT
        const val PREF_KEY_LAST_BUDGET_NOTIFIED = com.example.expensetracker.data.local.PREF_KEY_LAST_BUDGET_NOTIFIED

        const val TABLE_CATEGORIES = com.example.expensetracker.data.local.TABLE_CATEGORIES
        const val TABLE_TRANSACTIONS = com.example.expensetracker.data.local.TABLE_TRANSACTIONS
        const val TABLE_BUDGETS = com.example.expensetracker.data.local.TABLE_BUDGETS
        const val TABLE_PROFILE = com.example.expensetracker.data.local.TABLE_PROFILE
        const val TABLE_ACCOUNTS = com.example.expensetracker.data.local.TABLE_ACCOUNTS
        const val TABLE_RECEIVABLES = com.example.expensetracker.data.local.TABLE_RECEIVABLES
        const val TABLE_RECURRING_BILLS = com.example.expensetracker.data.local.TABLE_RECURRING_BILLS
        const val TABLE_PAYABLES = com.example.expensetracker.data.local.TABLE_PAYABLES
        const val TABLE_SAVINGS_GOALS = com.example.expensetracker.data.local.TABLE_SAVINGS_GOALS
        const val TABLE_MARKET_LISTS = com.example.expensetracker.data.local.TABLE_MARKET_LISTS
        const val TABLE_MARKET_ITEMS = com.example.expensetracker.data.local.TABLE_MARKET_ITEMS
    }

    internal val prefsHelper = UserPreferencesHelper(context)
    internal val transactionHelper = TransactionTableHelper(this)
    internal val transactionQueryHelper = TransactionQueryHelper(this)
    internal val transactionStatsHelper = TransactionStatsHelper(this)
    internal val accountHelper = AccountTableHelper(this)
    internal val categoryHelper = CategoryTableHelper(this)
    internal val budgetHelper = BudgetTableHelper(this)
    internal val userProfileHelper = UserProfileTableHelper(this)
    internal val receivableHelper = ReceivableTableHelper(this)
    internal val payableHelper = PayableTableHelper(this)
    internal val savingsGoalHelper = SavingsGoalTableHelper(this)
    internal val recurringBillHelper = RecurringBillTableHelper(this)
    internal val billSplitHelper = BillSplitHelper(this)
    internal val marketHelper = MarketTableHelper(this)
    internal val backupHelper = DatabaseBackupHelper(this)

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.enableWriteAheadLogging()
    }

    override fun onCreate(db: SQLiteDatabase) {
        DatabaseSchema.createAllTablesAndIndexes(db)
        DatabaseSeedData.seedInitialData(db, this)
        DatabaseSeedData.seedSampleRecurringBillsIfEmpty(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        DatabaseMigrations.onUpgrade(db, oldVersion, newVersion, this)
    }

    // --- Internal Seed & Balance Operations ---
    internal fun seedDefaultAccountsIfEmpty(db: SQLiteDatabase) = DatabaseSeedData.seedDefaultAccountsIfEmpty(db)
    internal fun backfillTransactionAccounts(db: SQLiteDatabase) = DatabaseSeedData.backfillTransactionAccounts(db)
    internal fun seedSampleRecurringBillsIfEmpty(db: SQLiteDatabase) = DatabaseSeedData.seedSampleRecurringBillsIfEmpty(db)
    fun seedDefaultCategories(db: SQLiteDatabase) = DatabaseSeedData.seedDefaultCategories(db)
    fun reconcileAndDeduplicateCategoryColors(db: SQLiteDatabase) = CategoryColorReconciler.reconcileAndDeduplicateCategoryColors(db)
    fun recalculateAccountBalance(db: SQLiteDatabase, accountId: Long) = AccountBalanceHelper.recalculateAccountBalance(db, accountId)
    fun recalculateAllAccountBalances(db: SQLiteDatabase) = AccountBalanceHelper.recalculateAllAccountBalances(db, this)
    fun resolveAccountForTransaction(db: SQLiteDatabase, accountId: Long?, paymentMethod: PaymentMethod?): Pair<Long, String> =
        AccountBalanceHelper.resolveAccountForTransaction(db, accountId, paymentMethod, this)
    internal fun cursorToTransaction(c: Cursor): Transaction = transactionHelper.cursorToTransaction(c)

    // --- Preferences Operations ---
    internal fun getPersistedApiKey(): String? = prefsHelper.getPersistedApiKey()
    internal fun persistApiKey(key: String?) = prefsHelper.persistApiKey(key)
    fun isPrivacyModeEnabled(): Boolean = prefsHelper.isPrivacyModeEnabled()
    fun setPrivacyModeEnabled(enabled: Boolean) = prefsHelper.setPrivacyModeEnabled(enabled)
    fun getLastBudgetPromptKey(): String? = prefsHelper.getLastBudgetPromptKey()
    fun setLastBudgetPromptKey(key: String) = prefsHelper.setLastBudgetPromptKey(key)
    fun getLastBudgetNotifiedKey(): String? = prefsHelper.getLastBudgetNotifiedKey()
    fun setLastBudgetNotifiedKey(key: String) = prefsHelper.setLastBudgetNotifiedKey(key)

    // --- Transactions Operations ---
    fun insertTransaction(tx: Transaction): Long = transactionHelper.insertTransaction(tx)
    fun updateTransaction(tx: Transaction): Int = transactionHelper.updateTransaction(tx)
    fun deleteTransaction(id: Long): Int = transactionHelper.deleteTransaction(id)
    fun getAllTransactions(): List<Transaction> = transactionHelper.getAllTransactions()
    fun getTransactionsForAccount(accountId: Long): List<Transaction> = transactionHelper.getTransactionsForAccount(accountId)
    fun getFilteredTransactions(timeFilter: TimeFilter = TimeFilter.ALL, type: TransactionType? = null, categoryId: Long? = null, query: String? = null): List<Transaction> =
        transactionQueryHelper.getFilteredTransactions(timeFilter, type, categoryId, query)
    fun getTransactionsBetween(startMillis: Long, endMillis: Long, type: TransactionType? = null, categoryId: Long? = null, query: String? = null): List<Transaction> =
        transactionQueryHelper.getTransactionsBetween(startMillis, endMillis, type, categoryId, query)
    fun getTimeRange(filter: TimeFilter): Pair<Long, Long> = transactionQueryHelper.getTimeRange(filter)

    // --- Transaction Stats Operations ---
    fun getAggregatedCategorySpends(startMillis: Long, endMillis: Long, type: TransactionType = TransactionType.EXPENSE): List<CategorySpend> =
        transactionStatsHelper.getAggregatedCategorySpends(startMillis, endMillis, type)
    fun getAggregatedCategorySpends(timeFilter: TimeFilter, type: TransactionType = TransactionType.EXPENSE): List<CategorySpend> =
        transactionStatsHelper.getAggregatedCategorySpends(timeFilter, type)
    fun getAggregatedFinancialSummary(startMillis: Long, endMillis: Long, overallBudget: Double = 0.0): FinancialSummary =
        transactionStatsHelper.getAggregatedFinancialSummary(startMillis, endMillis, overallBudget)
    fun getAggregatedFinancialSummary(timeFilter: TimeFilter, overallBudget: Double = 0.0): FinancialSummary =
        transactionStatsHelper.getAggregatedFinancialSummary(timeFilter, overallBudget)
    fun getAggregatedPaymentMethodSpends(startMillis: Long, endMillis: Long, type: TransactionType = TransactionType.EXPENSE): List<PaymentMethodSpend> =
        transactionStatsHelper.getAggregatedPaymentMethodSpends(startMillis, endMillis, type)
    fun getAggregatedPaymentMethodSpends(timeFilter: TimeFilter, type: TransactionType = TransactionType.EXPENSE): List<PaymentMethodSpend> =
        transactionStatsHelper.getAggregatedPaymentMethodSpends(timeFilter, type)

    // --- Categories Operations ---
    fun getAllCategories(): List<Category> = categoryHelper.getAllCategories()
    fun insertCategory(cat: Category): Long = categoryHelper.insertCategory(cat)
    fun updateCategory(cat: Category): Int = categoryHelper.updateCategory(cat)
    fun deleteCategory(id: Long): Int = categoryHelper.deleteCategory(id)
    fun getOrCreateCategoryByName(name: String, type: TransactionType, defaultIcon: String = "MoreHoriz", defaultColor: String = "#8B90A0"): Category =
        categoryHelper.getOrCreateCategoryByName(name, type, defaultIcon, defaultColor)

    // --- Budgets Operations ---
    fun getBudgets(month: Int, year: Int): List<Budget> = budgetHelper.getBudgets(month, year)
    fun getAllBudgets(): List<Budget> = budgetHelper.getAllBudgets()
    fun saveBudget(budget: Budget): Long = budgetHelper.saveBudget(budget)
    fun deleteBudget(id: Long): Int = budgetHelper.deleteBudget(id)
    fun hasBudgetsForMonth(month: Int, year: Int): Boolean = budgetHelper.hasBudgetsForMonth(month, year)
    fun copyBudgets(fromMonth: Int, fromYear: Int, toMonth: Int, toYear: Int): Boolean = budgetHelper.copyBudgets(fromMonth, fromYear, toMonth, toYear)

    // --- User Profile Operations ---
    fun getUserProfile(): UserProfile = userProfileHelper.getUserProfile()
    fun updateUserProfile(profile: UserProfile): Int = userProfileHelper.updateUserProfile(profile)
    fun updateProfileLanguage(languageCode: String): Int = userProfileHelper.updateProfileLanguage(languageCode)
    fun updateGeminiApiKey(key: String?): Int = userProfileHelper.updateGeminiApiKey(key)
    fun updateAiInsightsEnabled(enabled: Boolean): Int = userProfileHelper.updateAiInsightsEnabled(enabled)
    fun updateProfileTheme(theme: String): Int = userProfileHelper.updateProfileTheme(theme)
    fun updateProfileBengaliNumerals(enabled: Boolean): Int = userProfileHelper.updateProfileBengaliNumerals(enabled)

    // --- Accounts Operations ---
    fun getAllAccounts(): List<Account> = accountHelper.getAllAccounts()
    fun getAccountById(id: Long): Account? = accountHelper.getAccountById(id)
    fun insertAccount(acc: Account): Long = accountHelper.insertAccount(acc)
    fun updateAccount(acc: Account): Int = accountHelper.updateAccount(acc)
    fun deleteAccount(id: Long): Int = accountHelper.deleteAccount(id)
    fun transferBetweenAccounts(fromAccountId: Long, toAccountId: Long, amount: Double, note: String = ""): Boolean =
        accountHelper.transferBetweenAccounts(fromAccountId, toAccountId, amount, note)

    // --- Receivables Operations ---
    fun getAllReceivables(): List<Receivable> = receivableHelper.getAllReceivables()
    fun getReceivableById(id: Long): Receivable? = receivableHelper.getReceivableById(id)
    fun insertReceivable(receivable: Receivable, deductFromAccountId: Long? = null): Long =
        receivableHelper.insertReceivable(receivable, deductFromAccountId)
    fun updateReceivable(receivable: Receivable): Int = receivableHelper.updateReceivable(receivable)
    fun deleteReceivable(id: Long): Int = receivableHelper.deleteReceivable(id)
    fun recordReceivablePayment(receivableId: Long, amountPaid: Double, depositToAccountId: Long? = null, note: String = ""): Boolean =
        receivableHelper.recordReceivablePayment(receivableId, amountPaid, depositToAccountId, note)
    fun getReceivablesSummary(): ReceivablesSummary = receivableHelper.getReceivablesSummary()

    // --- Payables & Debt Book Operations ---
    fun getAllPayables(): List<Payable> = payableHelper.getAllPayables()
    fun getPayableById(id: Long): Payable? = payableHelper.getPayableById(id)
    fun insertPayable(payable: Payable, depositToAccountId: Long? = null): Long =
        payableHelper.insertPayable(payable, depositToAccountId)
    fun updatePayable(payable: Payable): Int = payableHelper.updatePayable(payable)
    fun deletePayable(id: Long): Int = payableHelper.deletePayable(id)
    fun recordPayablePayment(payableId: Long, amountPaid: Double, deductFromAccountId: Long? = null, note: String = ""): Boolean =
        payableHelper.recordPayablePayment(payableId, amountPaid, deductFromAccountId, note)
    fun getPayablesSummary(): PayablesSummary = payableHelper.getPayablesSummary()
    fun getDebtBookSummary(): DebtBookSummary = payableHelper.getDebtBookSummary()

    // --- Savings Goals Operations ---
    fun getAllSavingsGoals(): List<SavingsGoal> = savingsGoalHelper.getAllSavingsGoals()
    fun getSavingsGoalById(id: Long): SavingsGoal? = savingsGoalHelper.getSavingsGoalById(id)
    fun insertSavingsGoal(goal: SavingsGoal): Long = savingsGoalHelper.insertSavingsGoal(goal)
    fun updateSavingsGoal(goal: SavingsGoal): Int = savingsGoalHelper.updateSavingsGoal(goal)
    fun deleteSavingsGoal(id: Long): Int = savingsGoalHelper.deleteSavingsGoal(id)
    fun addSavingsContribution(goalId: Long, amount: Double, deductFromAccountId: Long? = null, note: String = ""): Boolean =
        savingsGoalHelper.addSavingsContribution(goalId, amount, deductFromAccountId, note)

    // --- Backup & Restore Operations ---
    fun createBackupPayload(): BackupPayload = backupHelper.createBackupPayload()
    fun restoreFromBackup(payload: BackupPayload): Boolean = backupHelper.restoreFromBackup(payload)

    // --- Recurring Bills & Bill Split Operations ---
    fun insertRecurringBill(bill: RecurringBill): Long = recurringBillHelper.insertRecurringBill(bill)
    fun updateRecurringBill(bill: RecurringBill): Int = recurringBillHelper.updateRecurringBill(bill)
    fun deleteRecurringBill(id: Long): Int = recurringBillHelper.deleteRecurringBill(id)
    fun getAllRecurringBills(): List<RecurringBill> = recurringBillHelper.getAllRecurringBills()
    fun markBillAsPaid(billId: Long, paymentAccountId: Long?, paymentAccountName: String?): Long =
        recurringBillHelper.markBillAsPaid(billId, paymentAccountId, paymentAccountName)
    fun executeBillSplit(splitData: BillSplitData): Boolean = billSplitHelper.executeBillSplit(splitData)

    // --- Market Lists & Items Operations ---
    fun createMarketList(title: String, createdAt: Long = System.currentTimeMillis()): Long =
        marketHelper.createMarketList(title, createdAt)
    fun getActiveMarketList(): MarketListWithItems? = marketHelper.getActiveMarketList()
    fun getAllMarketLists(): List<MarketListWithItems> = marketHelper.getAllMarketLists()
    fun getMarketItemsForList(listId: Long): List<MarketItem> = marketHelper.getMarketItemsForList(listId)
    fun addMarketItem(item: MarketItem): Long = marketHelper.addMarketItem(item)
    fun addMarketItems(listId: Long, items: List<MarketItem>) = marketHelper.addMarketItems(listId, items)
    fun updateMarketItem(item: MarketItem): Boolean = marketHelper.updateMarketItem(item)
    fun deleteMarketItem(itemId: Long): Boolean = marketHelper.deleteMarketItem(itemId)
    fun updateMarketItemPurchased(itemId: Long, isPurchased: Boolean, amount: Double?, transactionId: Long?): Boolean =
        marketHelper.updateMarketItemPurchased(itemId, isPurchased, amount, transactionId)
    fun completeMarketList(listId: Long): Boolean = marketHelper.completeMarketList(listId)
    fun deleteMarketList(listId: Long): Boolean = marketHelper.deleteMarketList(listId)
}
