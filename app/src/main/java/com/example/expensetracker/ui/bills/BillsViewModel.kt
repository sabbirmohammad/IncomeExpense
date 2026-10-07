package com.example.expensetracker.ui.bills

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.data.model.RecurringBill
import com.example.expensetracker.data.model.UserProfile
import com.example.expensetracker.data.repository.ExpenseRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BillsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ExpenseRepository.getInstance(application)

    val recurringBills: StateFlow<List<RecurringBill>> = repository.recurringBills
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val accounts: StateFlow<List<Account>> = repository.accounts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categories: StateFlow<List<Category>> = repository.categories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userProfile: StateFlow<UserProfile> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserProfile())

    fun addBill(bill: RecurringBill) {
        viewModelScope.launch {
            repository.addRecurringBill(bill)
        }
    }

    fun updateBill(bill: RecurringBill) {
        viewModelScope.launch {
            repository.updateRecurringBill(bill)
        }
    }

    fun deleteBill(id: Long) {
        viewModelScope.launch {
            repository.deleteRecurringBill(id)
        }
    }

    fun payBill(
        billId: Long,
        accountId: Long?,
        accountName: String?,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val txId = repository.payRecurringBill(billId, accountId, accountName)
            if (txId > 0) {
                onSuccess()
            }
        }
    }
}
