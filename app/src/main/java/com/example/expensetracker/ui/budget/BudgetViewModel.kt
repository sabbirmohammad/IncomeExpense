package com.example.expensetracker.ui.budget

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.model.Budget
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.data.model.FinancialSummary
import com.example.expensetracker.data.model.TimeFilter
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.data.model.UserProfile
import com.example.expensetracker.data.model.getLocalizedCategoryName
import com.example.expensetracker.data.repository.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.SavingsGoal
import java.util.Calendar

@OptIn(FlowPreview::class)
class BudgetViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ExpenseRepository.getInstance(application)

    val userProfile: StateFlow<UserProfile> = repository.userProfile
    val categories: StateFlow<List<Category>> = repository.categories
    val budgets: StateFlow<List<Budget>> = repository.budgets
    val accounts: StateFlow<List<Account>> = repository.accounts
    val savingsGoals: StateFlow<List<SavingsGoal>> = repository.savingsGoals
    val isPrivacyMode: StateFlow<Boolean> = repository.isPrivacyMode

    private val _selectedTab = MutableStateFlow(BudgetScreenTab.BUDGETS)
    val selectedTab: StateFlow<BudgetScreenTab> = _selectedTab.asStateFlow()

    fun setSelectedTab(tab: BudgetScreenTab) {
        _selectedTab.value = tab
    }

    fun togglePrivacyMode() {
        repository.togglePrivacyMode()
    }

    private val _summary = MutableStateFlow(FinancialSummary())
    val summary: StateFlow<FinancialSummary> = _summary.asStateFlow()

    private val _budgetStatuses = MutableStateFlow<List<CategoryBudgetStatus>>(emptyList())
    val budgetStatuses: StateFlow<List<CategoryBudgetStatus>> = _budgetStatuses.asStateFlow()

    init {
        viewModelScope.launch {
            merge(
                repository.transactions,
                repository.budgets,
                repository.categories,
                repository.userProfile
            ).debounce(200L).collect {
                loadBudgets()
            }
        }
    }

    fun loadBudgets() {
        viewModelScope.launch {
            val summary = withContext(Dispatchers.IO) {
                repository.getFinancialSummary(TimeFilter.THIS_MONTH)
            }
            val currentBudgets = repository.budgets.value
            val categorySpends = withContext(Dispatchers.IO) {
                repository.getCategorySpends(TimeFilter.THIS_MONTH)
            }

            val statuses = withContext(Dispatchers.Default) {
                currentBudgets.map { b ->
                    val spent = if (b.categoryId == null) {
                        summary.totalExpense
                    } else {
                        categorySpends.find { it.categoryId == b.categoryId }?.totalAmount ?: 0.0
                    }
                    val remaining = (b.amount - spent).coerceAtLeast(0.0)
                    val progress = if (b.amount > 0) (spent / b.amount).toFloat() else 0f
                    CategoryBudgetStatus(
                        budget = b,
                        spentAmount = spent,
                        remainingAmount = remaining,
                        progressPercent = progress.coerceIn(0f, 2f),
                        isOverBudget = spent > b.amount
                    )
                }
            }

            _summary.value = summary
            _budgetStatuses.value = statuses
        }
    }

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        android.util.Log.e("BudgetViewModel", "Unhandled coroutine error", throwable)
    }

    fun saveBudget(budget: Budget) {
        viewModelScope.launch(coroutineExceptionHandler) {
            repository.saveBudget(budget)
            loadBudgets()
        }
    }

    fun deleteBudget(id: Long) {
        viewModelScope.launch(coroutineExceptionHandler) {
            repository.deleteBudget(id)
            loadBudgets()
        }
    }

    fun addSavingsGoal(title: String, targetAmount: Double, initialSaved: Double, targetDateMillis: Long?, icon: String, colorHex: String, notes: String) {
        viewModelScope.launch(coroutineExceptionHandler) {
            repository.addSavingsGoal(
                SavingsGoal(
                    title = title,
                    targetAmount = targetAmount,
                    savedAmount = initialSaved,
                    targetDateMillis = targetDateMillis,
                    icon = icon,
                    colorHex = colorHex,
                    notes = notes
                )
            )
        }
    }

    fun updateSavingsGoal(goal: SavingsGoal) {
        viewModelScope.launch(coroutineExceptionHandler) {
            repository.updateSavingsGoal(goal)
        }
    }

    fun deleteSavingsGoal(id: Long) {
        viewModelScope.launch(coroutineExceptionHandler) {
            repository.deleteSavingsGoal(id)
        }
    }

    fun addSavingsContribution(goalId: Long, amount: Double, fromAccountId: Long? = null) {
        viewModelScope.launch(coroutineExceptionHandler) {
            repository.addSavingsContribution(goalId, amount, fromAccountId)
        }
    }



    companion object {
        fun findMatchingExpenseCategory(categoryName: String, categories: List<Category>): Category? =
            ExpenseCategoryMatcher.findMatchingExpenseCategory(categoryName, categories)
    }
}
