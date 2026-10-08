package com.example.expensetracker.ui.reports

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.CategorySpend
import com.example.expensetracker.data.model.FinancialSummary
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.data.model.UserProfile
import com.example.expensetracker.data.repository.*
import com.example.expensetracker.ui.components.DateTimeHelper
import com.example.expensetracker.ui.components.StatementExportHelper
import com.example.expensetracker.ui.components.convertToBengaliDigits
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class ReportsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ExpenseRepository.getInstance(application)

    val userProfile: StateFlow<UserProfile> = repository.userProfile
    val accounts: StateFlow<List<Account>> = repository.accounts
    val isPrivacyMode: StateFlow<Boolean> = repository.isPrivacyMode

    private val _selectedPeriod = MutableStateFlow(ReportPeriod.THIS_MONTH)
    val selectedPeriod: StateFlow<ReportPeriod> = _selectedPeriod.asStateFlow()

    private val _selectedAccountId = MutableStateFlow<Long?>(null)
    val selectedAccountId: StateFlow<Long?> = _selectedAccountId.asStateFlow()

    private val _selectedType = MutableStateFlow<TransactionType?>(null)
    val selectedType: StateFlow<TransactionType?> = _selectedType.asStateFlow()

    private val _customStartMillis = MutableStateFlow<Long>(ReportsDataComputer.getStartOfMonth(0))
    val customStartMillis: StateFlow<Long> = _customStartMillis.asStateFlow()

    private val _customEndMillis = MutableStateFlow<Long>(System.currentTimeMillis())
    val customEndMillis: StateFlow<Long> = _customEndMillis.asStateFlow()

    private val _filteredTransactions = MutableStateFlow<List<Transaction>>(emptyList())
    val filteredTransactions: StateFlow<List<Transaction>> = _filteredTransactions.asStateFlow()

    private val _summary = MutableStateFlow(FinancialSummary())
    val summary: StateFlow<FinancialSummary> = _summary.asStateFlow()

    private val _categorySpends = MutableStateFlow<List<CategorySpend>>(emptyList())
    val categorySpends: StateFlow<List<CategorySpend>> = _categorySpends.asStateFlow()

    private val _isExporting = MutableStateFlow(false)
    val isExporting: StateFlow<Boolean> = _isExporting.asStateFlow()

    private val _lastGeneratedPdfFile = MutableStateFlow<File?>(null)
    val lastGeneratedPdfFile: StateFlow<File?> = _lastGeneratedPdfFile.asStateFlow()

    private val _exportMessage = MutableStateFlow<String?>(null)
    val exportMessage: StateFlow<String?> = _exportMessage.asStateFlow()

    private data class ReportFilterCriteria(
        val period: ReportPeriod,
        val accountId: Long?,
        val type: TransactionType?,
        val customStart: Long,
        val customEnd: Long
    )

    init {
        val filterCriteriaFlow = combine(
            _selectedPeriod,
            _selectedAccountId,
            _selectedType,
            _customStartMillis,
            _customEndMillis
        ) { period, accId, type, customStart, customEnd ->
            ReportFilterCriteria(period, accId, type, customStart, customEnd)
        }

        viewModelScope.launch(Dispatchers.Default) {
            combine(
                repository.transactions,
                filterCriteriaFlow
            ) { allTxs, criteria ->
                ReportsDataComputer.computeReportData(
                    allTxs,
                    criteria.period,
                    criteria.accountId,
                    criteria.type,
                    criteria.customStart,
                    criteria.customEnd
                )
            }.collect { (txs, summary, catSpends) ->
                _filteredTransactions.value = txs
                _summary.value = summary
                _categorySpends.value = catSpends
            }
        }
    }

    fun setPeriod(period: ReportPeriod) {
        _selectedPeriod.value = period
    }

    fun setCustomRange(startMillis: Long, endMillis: Long) {
        _customStartMillis.value = startMillis
        _customEndMillis.value = endMillis
        _selectedPeriod.value = ReportPeriod.CUSTOM
    }

    fun setAccountFilter(accountId: Long?) {
        _selectedAccountId.value = accountId
    }

    fun setTypeFilter(type: TransactionType?) {
        _selectedType.value = type
    }

    fun togglePrivacyMode() {
        viewModelScope.launch {
            repository.togglePrivacyMode()
        }
    }

    fun clearExportMessage() {
        _exportMessage.value = null
    }

    fun getPeriodTitle(isBangla: Boolean): String {
        val locale = DateTimeHelper.getLocale(isBangla)
        val df = SimpleDateFormat("dd MMM yyyy", locale)
        val raw = when (_selectedPeriod.value) {
            ReportPeriod.THIS_MONTH -> {
                val cal = Calendar.getInstance()
                SimpleDateFormat("MMMM yyyy", locale).format(cal.time)
            }
            ReportPeriod.LAST_MONTH -> {
                val cal = Calendar.getInstance().apply { add(Calendar.MONTH, -1) }
                SimpleDateFormat("MMMM yyyy", locale).format(cal.time)
            }
            ReportPeriod.LAST_3_MONTHS -> if (isBangla) "গত ৩ মাস" else "Last 3 Months"
            ReportPeriod.THIS_YEAR -> {
                val cal = Calendar.getInstance()
                SimpleDateFormat("yyyy", locale).format(cal.time)
            }
            ReportPeriod.ALL_TIME -> if (isBangla) "সর্বমোট হিসাব" else "All Time"
            ReportPeriod.CUSTOM -> "${df.format(Date(_customStartMillis.value))} - ${df.format(Date(_customEndMillis.value))}"
        }
        return if (isBangla) convertToBengaliDigits(raw) else raw
    }

    fun exportAndSharePdf(context: Context, isBangla: Boolean) {
        viewModelScope.launch {
            _isExporting.value = true
            val periodTitle = getPeriodTitle(isBangla)
            val file = withContext(Dispatchers.IO) {
                StatementExportHelper.generatePdfFile(
                    context = context,
                    profile = userProfile.value,
                    summary = _summary.value,
                    transactions = _filteredTransactions.value,
                    periodTitle = periodTitle,
                    categorySpends = _categorySpends.value
                )
            }
            _lastGeneratedPdfFile.value = file
            _isExporting.value = false
            if (file != null) {
                StatementExportHelper.sharePdf(context, file, periodTitle)
                _exportMessage.value = if (isBangla) "PDF রিপোর্ট সফলভাবে তৈরি হয়েছে!" else "PDF report generated successfully!"
            } else {
                _exportMessage.value = if (isBangla) "PDF তৈরি করা সম্ভব হয়নি" else "Failed to generate PDF"
            }
        }
    }

    fun openPdfDirectly(context: Context, isBangla: Boolean) {
        viewModelScope.launch {
            val existing = _lastGeneratedPdfFile.value
            if (existing != null && existing.exists()) {
                StatementExportHelper.openPdf(context, existing)
                return@launch
            }

            _isExporting.value = true
            val periodTitle = getPeriodTitle(isBangla)
            val file = withContext(Dispatchers.IO) {
                StatementExportHelper.generatePdfFile(
                    context = context,
                    profile = userProfile.value,
                    summary = _summary.value,
                    transactions = _filteredTransactions.value,
                    periodTitle = periodTitle,
                    categorySpends = _categorySpends.value
                )
            }
            _lastGeneratedPdfFile.value = file
            _isExporting.value = false
            if (file != null) {
                StatementExportHelper.openPdf(context, file)
            } else {
                _exportMessage.value = if (isBangla) "PDF তৈরি করা সম্ভব হয়নি" else "Failed to generate PDF"
            }
        }
    }

    fun exportAndShareCsv(context: Context, isBangla: Boolean) {
        viewModelScope.launch {
            _isExporting.value = true
            val periodTitle = getPeriodTitle(isBangla)
            val success = withContext(Dispatchers.IO) {
                StatementExportHelper.exportAndShareCsv(
                    context = context,
                    profile = userProfile.value,
                    transactions = _filteredTransactions.value,
                    periodTitle = periodTitle
                )
            }
            _isExporting.value = false
            if (success) {
                _exportMessage.value = if (isBangla) "CSV ফাইল প্রস্তুত হয়েছে!" else "CSV file generated successfully!"
            } else {
                _exportMessage.value = if (isBangla) "CSV তৈরি করা সম্ভব হয়নি" else "Failed to generate CSV"
            }
        }
    }
}
