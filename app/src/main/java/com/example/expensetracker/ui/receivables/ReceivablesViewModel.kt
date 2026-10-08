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

    fun addReceivable(
        personName: String,
        phoneNumber: String,
        amount: Double,
        dueDateMillis: Long?,
        notes: String,
        deductFromAccountId: Long?
    ) {
        viewModelScope.launch {
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
        viewModelScope.launch {
            repository.updateReceivable(receivable)
        }
    }

    fun deleteReceivable(id: Long) {
        viewModelScope.launch {
            repository.deleteReceivable(id)
        }
    }

    fun recordPayment(
        receivableId: Long,
        amountPaid: Double,
        depositToAccountId: Long?,
        note: String
    ) {
        viewModelScope.launch {
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
        viewModelScope.launch {
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
        viewModelScope.launch {
            repository.updatePayable(payable)
        }
    }

    fun deletePayable(id: Long) {
        viewModelScope.launch {
            repository.deletePayable(id)
        }
    }

    fun recordPayablePayment(
        payableId: Long,
        amountPaid: Double,
        deductFromAccountId: Long?,
        note: String
    ) {
        viewModelScope.launch {
            repository.recordPayablePayment(payableId, amountPaid, deductFromAccountId, note)
        }
    }

    fun executeBillSplit(splitData: com.example.expensetracker.data.model.BillSplitData) {
        viewModelScope.launch {
            repository.executeBillSplit(splitData)
        }
    }

    private val _generatedReminder = MutableStateFlow<String?>(null)
    val generatedReminder: StateFlow<String?> = _generatedReminder.asStateFlow()

    private val _isGeneratingReminder = MutableStateFlow(false)
    val isGeneratingReminder: StateFlow<Boolean> = _isGeneratingReminder.asStateFlow()

    fun generatePoliteReminder(receivable: Receivable) {
        viewModelScope.launch {
            _isGeneratingReminder.value = true
            val profile = repository.userProfile.value
            val isBn = profile.appLanguage == "bn"
            val due = receivable.amount - receivable.amountReceived
            val currency = profile.currencySymbol
            val amountFormatted = String.format(java.util.Locale.US, "%,.2f", due)
            val message = if (isBn) {
                "আসসালামু আলাইকুম ${receivable.personName} ভাই, আশা করি ভালো আছেন। আপনার কাছে পাওনা $currency $amountFormatted পরিশোধের বিষয়টি একটু মনে করিয়ে দিচ্ছি। আপনার সুবিধাজনক সময়ে পরিশোধ করলে উপকৃত হব। ধন্যবাদ!"
            } else {
                "Hello ${receivable.personName}, hope you are well. This is a gentle reminder regarding the pending amount of $currency $amountFormatted. Please let me know when you can settle it. Thank you!"
            }
            _generatedReminder.value = message
            _isGeneratingReminder.value = false
        }
    }

    fun clearGeneratedReminder() {
        _generatedReminder.value = null
    }
}
