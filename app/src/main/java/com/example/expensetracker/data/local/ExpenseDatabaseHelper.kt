package com.example.expensetracker.data.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.expensetracker.data.model.*

class ExpenseDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = com.example.expensetracker.data.local.DATABASE_NAME
        const val DATABASE_VERSION = com.example.expensetracker.data.local.DATABASE_VERSION
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
    internal val productPriceHelper = ProductPriceTableHelper(this)
    internal val masterProductHelper = MasterProductTableHelper(this)
    internal val backupHelper = DatabaseBackupHelper(this)

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.enableWriteAheadLogging()
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        DatabaseSchema.createAllTablesAndIndexes(db)
        DatabaseSeedData.seedInitialData(db, this)
        DatabaseSeedData.seedSampleRecurringBillsIfEmpty(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) =
        DatabaseMigrations.onUpgrade(db, oldVersion, newVersion, this)

    // --- Preferences Operations ---
    fun isPrivacyModeEnabled(): Boolean = prefsHelper.isPrivacyModeEnabled()
    fun setPrivacyModeEnabled(enabled: Boolean) = prefsHelper.setPrivacyModeEnabled(enabled)
    fun isOnboardingCompleted(): Boolean = prefsHelper.isOnboardingCompleted()
    fun setOnboardingCompleted(completed: Boolean) = prefsHelper.setOnboardingCompleted(completed)
    fun getLastBudgetPromptKey(): String? = prefsHelper.getLastBudgetPromptKey()
    fun setLastBudgetPromptKey(key: String) = prefsHelper.setLastBudgetPromptKey(key)
    fun getLastBudgetNotifiedKey(): String? = prefsHelper.getLastBudgetNotifiedKey()
    fun setLastBudgetNotifiedKey(key: String) = prefsHelper.setLastBudgetNotifiedKey(key)
    fun getMarketListBudget(listId: Long): Double? = prefsHelper.getMarketListBudget(listId)
    fun setMarketListBudget(listId: Long, budget: Double?) = prefsHelper.setMarketListBudget(listId, budget)
    fun deleteMarketListBudget(listId: Long) = prefsHelper.deleteMarketListBudget(listId)

    // --- Transactions Operations ---
    fun insertTransaction(tx: Transaction): Long = transactionHelper.insertTransaction(tx)
    fun updateTransaction(tx: Transaction): Int = transactionHelper.updateTransaction(tx)
    fun deleteTransaction(id: Long): Int = transactionHelper.deleteTransaction(id)
    fun getAllTransactions(): List<Transaction> = transactionHelper.getAllTransactions()
    fun getTransactionsForAccount(accountId: Long): List<Transaction> = transactionHelper.getTransactionsForAccount(accountId)
    fun getFilteredTransactions(timeFilter: TimeFilter = TimeFilter.ALL, type: TransactionType? = null, categoryId: Long? = null, query: String? = null, accountId: Long? = null): List<Transaction> =
        transactionQueryHelper.getFilteredTransactions(timeFilter, type, categoryId, query, accountId)
    fun getTransactionsBetween(startMillis: Long, endMillis: Long, type: TransactionType? = null, categoryId: Long? = null, query: String? = null, accountId: Long? = null): List<Transaction> =
        transactionQueryHelper.getTransactionsBetween(startMillis, endMillis, type, categoryId, query, accountId)
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

    // --- User Profile Operations ---
    fun getUserProfile(): UserProfile = userProfileHelper.getUserProfile()
    fun updateUserProfile(profile: UserProfile): Int = userProfileHelper.updateUserProfile(profile)
    fun updateProfileLanguage(languageCode: String): Int = userProfileHelper.updateProfileLanguage(languageCode)
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
    fun getActiveMarketLists(): List<MarketListWithItems> = marketHelper.getActiveMarketLists()
    fun getMarketListById(listId: Long): MarketListWithItems? = marketHelper.getMarketListById(listId)
    fun updateMarketListTitle(listId: Long, title: String): Boolean = marketHelper.updateMarketListTitle(listId, title)
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
    fun getLastPurchasedRates(): Map<String, Pair<Double, String>> = marketHelper.getLastPurchasedRates()

    // --- Product Price Intelligence Operations ---
    fun recordProductPrice(
        productName: String, rawQuantity: Double, rawUnit: String, totalAmount: Double,
        marketItemId: Long? = null, transactionId: Long? = null, timestamp: Long = System.currentTimeMillis()
    ): Long = productPriceHelper.recordPrice(productName, rawQuantity, rawUnit, totalAmount, marketItemId, transactionId, timestamp)

    fun recordPricesForMarketItems(items: List<MarketItem>, transactionId: Long?, timestamp: Long) =
        productPriceHelper.recordPricesForMarketItems(items, transactionId, timestamp)

    fun getProductPriceInsight(productName: String): ProductPriceInsight? =
        productPriceHelper.getPriceInsight(productName)

    fun getAllProductPriceInsights(): Map<String, ProductPriceInsight> =
        productPriceHelper.getAllPriceInsights()

    fun estimateProductCost(productName: String, quantity: Double, unit: String): Double? =
        productPriceHelper.estimateCost(productName, quantity, unit)

    fun getProductPriceHistory(productName: String, limit: Int = 50): List<ProductPriceRecord> =
        productPriceHelper.getPriceHistory(productName, limit)

    // --- Master Product Catalog Operations ---
    fun getAllMasterProducts(): List<MasterProduct> = masterProductHelper.getAllMasterProducts()
    fun getStapleProducts(limit: Int = 15): List<MasterProduct> = masterProductHelper.getStapleProducts(limit)
    fun updateMasterProductCategory(productName: String, newCategory: String): Boolean =
        masterProductHelper.updateProductCategory(productName, newCategory)
}
