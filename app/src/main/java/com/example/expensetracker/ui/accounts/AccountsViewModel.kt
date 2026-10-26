package com.example.expensetracker.ui.accounts

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.AccountType
import com.example.expensetracker.data.model.PaymentMethod
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.data.model.UserProfile
import com.example.expensetracker.data.repository.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AccountsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ExpenseRepository.getInstance(application)

    val accounts: StateFlow<List<Account>> = repository.accounts
    val userProfile: StateFlow<UserProfile> = repository.userProfile
    val transactions: StateFlow<List<Transaction>> = repository.transactions

    private val _selectedAccountFilter = MutableStateFlow<AccountType?>(null) // null = ALL
    val selectedAccountFilter: StateFlow<AccountType?> = _selectedAccountFilter.asStateFlow()

    private val _selectedAccount = MutableStateFlow<Account?>(null)
    val selectedAccount: StateFlow<Account?> = _selectedAccount.asStateFlow()

    private val _accountTransactions = MutableStateFlow<List<Transaction>>(emptyList())
    val accountTransactions: StateFlow<List<Transaction>> = _accountTransactions.asStateFlow()

    init {
        viewModelScope.launch {
            repository.transactions.collect {
                val cur = _selectedAccount.value
                if (cur != null) {
                    _accountTransactions.value = repository.getTransactionsForAccount(cur.id)
                }
            }
        }
        viewModelScope.launch {
            repository.accounts.collect { accList ->
                val cur = _selectedAccount.value
                if (cur != null) {
                    val updated = accList.find { it.id == cur.id }
                    _selectedAccount.value = updated
                    if (updated != null) {
                        _accountTransactions.value = repository.getTransactionsForAccount(updated.id)
                    } else {
                        _accountTransactions.value = emptyList()
                    }
                }
            }
        }
    }

    fun setFilter(type: AccountType?) {
        _selectedAccountFilter.value = type
    }

    fun selectAccount(account: Account?) {
        _selectedAccount.value = account
        if (account != null) {
            viewModelScope.launch {
                _accountTransactions.value = repository.getTransactionsForAccount(account.id)
            }
        } else {
            _accountTransactions.value = emptyList()
        }
    }

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        android.util.Log.e("AccountsViewModel", "Unhandled coroutine error", throwable)
    }

    fun addAccount(
        name: String,
        type: AccountType,
        institution: String,
        accountNumber: String,
        initialBalance: Double,
        colorHex: String
    ) {
        viewModelScope.launch(coroutineExceptionHandler) {
            val acc = Account(
                name = name.trim(),
                type = type,
                institutionName = institution.trim(),
                accountNumber = accountNumber.trim(),
                initialBalance = initialBalance,
                currentBalance = initialBalance,
                colorHex = colorHex
            )
            repository.addAccount(acc)
        }
    }

    fun updateAccount(account: Account) {
        viewModelScope.launch(coroutineExceptionHandler) {
            repository.updateAccount(account)
            if (_selectedAccount.value?.id == account.id) {
                _selectedAccount.value = account
                _accountTransactions.value = repository.getTransactionsForAccount(account.id)
            }
        }
    }

    fun deleteAccount(id: Long) {
        viewModelScope.launch(coroutineExceptionHandler) {
            repository.deleteAccount(id)
            if (_selectedAccount.value?.id == id) {
                _selectedAccount.value = null
                _accountTransactions.value = emptyList()
            }
        }
    }

    fun transferFunds(
        fromAccountId: Long,
        toAccountId: Long,
        amount: Double,
        note: String
    ) {
        viewModelScope.launch(coroutineExceptionHandler) {
            repository.transferBetweenAccounts(fromAccountId, toAccountId, amount, note)
            val curSelected = _selectedAccount.value
            if (curSelected != null && (curSelected.id == fromAccountId || curSelected.id == toAccountId)) {
                _accountTransactions.value = repository.getTransactionsForAccount(curSelected.id)
            }
        }
    }

    fun reconcileBalance(account: Account, newBalance: Double, note: String? = null) {
        viewModelScope.launch(coroutineExceptionHandler) {
            val diff = newBalance - account.currentBalance
            if (kotlin.math.abs(diff) > 0.001) {
                val isGain = diff > 0
                val adjAmount = kotlin.math.abs(diff)
                val adjType = if (isGain) TransactionType.INCOME else TransactionType.EXPENSE
                val adjCat = repository.getOrCreateCategoryByName("Adjustment", adjType, "Tune", "#64748B")
                val pm = when (account.type) {
                    AccountType.CASH -> PaymentMethod.CASH
                    AccountType.BANK -> PaymentMethod.BANK_TRANSFER
                    AccountType.MFS -> if (account.institutionName.contains("nagad", ignoreCase = true) || account.name.contains("nagad", ignoreCase = true)) {
                        PaymentMethod.NAGAD
                    } else {
                        PaymentMethod.BKASH
                    }
                }
                val tx = Transaction(
                    title = note?.ifBlank { null } ?: (if (isGain) "Balance Adjustment (+)" else "Balance Adjustment (-)"),
                    amount = adjAmount,
                    type = adjType,
                    categoryId = adjCat.id,
                    categoryName = adjCat.name,
                    categoryIcon = adjCat.icon,
                    categoryColor = adjCat.colorHex,
                    dateMillis = System.currentTimeMillis(),
                    paymentMethod = pm,
                    notes = "[Reconciliation] Adjusted balance from ${account.currentBalance} to $newBalance",
                    accountId = account.id,
                    accountName = account.name
                )
                repository.addTransaction(tx)
                val refreshed = repository.getAccountById(account.id)
                if (refreshed != null) {
                    _selectedAccount.value = refreshed
                    _accountTransactions.value = repository.getTransactionsForAccount(refreshed.id)
                }
            }
        }
    }
}
