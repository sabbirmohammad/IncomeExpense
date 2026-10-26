package com.example.expensetracker.ui.receivables

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.DebtBookSummary
import com.example.expensetracker.data.model.Payable
import com.example.expensetracker.data.model.PayableStatus
import com.example.expensetracker.data.model.PayablesSummary
import com.example.expensetracker.data.model.Receivable
import com.example.expensetracker.data.model.ReceivableStatus
import com.example.expensetracker.data.model.ReceivablesSummary
import com.example.expensetracker.data.model.UserProfile
import com.example.expensetracker.data.repository.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class DebtLedgerTab {
    RECEIVABLES,
    PAYABLES
}

class ReceivablesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ExpenseRepository.getInstance(application)

    val receivables: StateFlow<List<Receivable>> = repository.receivables
    val receivablesSummary: StateFlow<ReceivablesSummary> = repository.receivablesSummary
    val payables: StateFlow<List<Payable>> = repository.payables
    val payablesSummary: StateFlow<PayablesSummary> = repository.payablesSummary
    val debtBookSummary: StateFlow<DebtBookSummary> = repository.debtBookSummary
    val isPrivacyMode: StateFlow<Boolean> = repository.isPrivacyMode
    val accounts: StateFlow<List<Account>> = repository.accounts
    val categories: StateFlow<List<com.example.expensetracker.data.model.Category>> = repository.categories
    val userProfile: StateFlow<UserProfile> = repository.userProfile

    private val _selectedTab = MutableStateFlow(DebtLedgerTab.RECEIVABLES)
    val selectedTab: StateFlow<DebtLedgerTab> = _selectedTab.asStateFlow()

    private val _selectedStatusFilter = MutableStateFlow<ReceivableStatus?>(null) // null = ALL
    val selectedStatusFilter: StateFlow<ReceivableStatus?> = _selectedStatusFilter.asStateFlow()

    fun setSelectedTab(tab: DebtLedgerTab) {
        _selectedTab.value = tab
    }

    fun togglePrivacyMode() {
        repository.togglePrivacyMode()
    }

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    fun setStatusFilter(status: ReceivableStatus?) {
        _selectedStatusFilter.value = status
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        android.util.Log.e("ReceivablesViewModel", "Unhandled coroutine error", throwable)
    }

    fun addReceivable(
        personName: String,
        phoneNumber: String,
        amount: Double,
        dueDateMillis: Long?,
        notes: String,
        deductFromAccountId: Long?
    ) {
        viewModelScope.launch(coroutineExceptionHandler) {
            val rec = Receivable(
                personName = personName.trim(),
                phoneNumber = phoneNumber.trim(),
                amount = amount,
                amountReceived = 0.0,
                dueDateMillis = dueDateMillis,
                createdDateMillis = System.currentTimeMillis(),
                notes = notes.trim(),
                status = ReceivableStatus.PENDING
            )
            repository.addReceivable(rec, deductFromAccountId)
        }
    }

    fun updateReceivable(receivable: Receivable) {
        viewModelScope.launch(coroutineExceptionHandler) {
            repository.updateReceivable(receivable)
        }
    }

    fun deleteReceivable(id: Long) {
        viewModelScope.launch(coroutineExceptionHandler) {
            repository.deleteReceivable(id)
        }
    }

    fun recordPayment(
        receivableId: Long,
        amountPaid: Double,
        depositToAccountId: Long?,
        note: String
    ) {
        viewModelScope.launch(coroutineExceptionHandler) {
            repository.recordReceivablePayment(receivableId, amountPaid, depositToAccountId, note)
        }
    }

    // --- Payables ---
    fun addPayable(
        personName: String,
        phoneNumber: String,
        amount: Double,
        dueDateMillis: Long?,
        notes: String,
        depositToAccountId: Long?
    ) {
        viewModelScope.launch(coroutineExceptionHandler) {
            val pay = Payable(
                personName = personName.trim(),
                phoneNumber = phoneNumber.trim(),
                amount = amount,
                amountPaid = 0.0,
                dueDateMillis = dueDateMillis,
                createdDateMillis = System.currentTimeMillis(),
                notes = notes.trim(),
                status = PayableStatus.PENDING
            )
            repository.addPayable(pay, depositToAccountId)
        }
    }

    fun updatePayable(payable: Payable) {
        viewModelScope.launch(coroutineExceptionHandler) {
            repository.updatePayable(payable)
        }
    }

    fun deletePayable(id: Long) {
        viewModelScope.launch(coroutineExceptionHandler) {
            repository.deletePayable(id)
        }
    }

    fun recordPayablePayment(
        payableId: Long,
        amountPaid: Double,
        deductFromAccountId: Long?,
        note: String
    ) {
        viewModelScope.launch(coroutineExceptionHandler) {
            repository.recordPayablePayment(payableId, amountPaid, deductFromAccountId, note)
        }
    }

    fun executeBillSplit(splitData: com.example.expensetracker.data.model.BillSplitData) {
        viewModelScope.launch(coroutineExceptionHandler) {
            repository.executeBillSplit(splitData)
        }
    }
}
