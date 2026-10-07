package com.example.expensetracker.ui.settings

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.ai.GeminiAiService
import com.example.expensetracker.data.drive.GoogleAuthHelper
import com.example.expensetracker.data.drive.GoogleDriveSyncManager
import com.example.expensetracker.data.model.ApiConnectionTestResult
import com.example.expensetracker.data.model.ApiTestStatusType
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.data.model.UserProfile
import com.example.expensetracker.data.repository.ExpenseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    internal val repository = ExpenseRepository.getInstance(application)
    internal val syncManager = GoogleDriveSyncManager(application)
    internal val authHelper = GoogleAuthHelper(application)

    val userProfile: StateFlow<UserProfile> = repository.userProfile
    val categories: StateFlow<List<Category>> = repository.categories
    val transactions: StateFlow<List<Transaction>> = repository.transactions

    internal val _syncMessage = MutableStateFlow<String?>(null)
    val syncMessage: StateFlow<String?> = _syncMessage.asStateFlow()

    internal val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    /** Non-null when the sign-in intent needs to be launched by the UI. */
    internal val _signInIntent = MutableStateFlow<Intent?>(null)
    val signInIntent: StateFlow<Intent?> = _signInIntent.asStateFlow()

    init {
        // If email isn't yet saved in local profile, check if user is already signed in on background thread
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            val lastAccount = authHelper.getSignedInAccount()
            if (lastAccount != null && repository.userProfile.value.googleAccountEmail == null) {
                val email = lastAccount.email ?: lastAccount.account?.name
                if (!email.isNullOrBlank()) {
                    val updated = repository.userProfile.value.copy(googleAccountEmail = email)
                    repository.updateProfile(updated)
                }
            }
        }
    }

    // ── Profile ───────────────────────────────────────────────────────────

    fun updateProfile(firstName: String, lastName: String, avatarIndex: Int = userProfile.value.avatarIndex) {
        val trimmedFirst = firstName.trim()
        val trimmedLast = lastName.trim()
        val fullName = when {
            trimmedFirst.isNotEmpty() && trimmedLast.isNotEmpty() -> "$trimmedFirst $trimmedLast"
            trimmedFirst.isNotEmpty() -> trimmedFirst
            trimmedLast.isNotEmpty() -> trimmedLast
            else -> "User"
        }
        viewModelScope.launch {
            val updated = repository.userProfile.value.copy(
                firstName = trimmedFirst,
                lastName = trimmedLast,
                name = fullName,
                avatarIndex = avatarIndex
            )
            repository.updateProfile(updated)
        }
    }

    fun updateProfile(name: String, avatarIndex: Int) {
        val parts = name.trim().split(Regex("\\s+"), limit = 2)
        val first = parts.getOrNull(0) ?: ""
        val last = parts.getOrNull(1) ?: ""
        updateProfile(first, last, avatarIndex)
    }

    fun setCurrency(code: String, symbol: String) {
        viewModelScope.launch {
            val updated = repository.userProfile.value.copy(
                currencyCode = code,
                currencySymbol = symbol
            )
            repository.updateProfile(updated)
        }
    }

    fun setAppLanguage(langCode: String) {
        viewModelScope.launch {
            repository.setAppLanguage(langCode)
        }
    }

    fun setAppTheme(theme: String) {
        viewModelScope.launch {
            repository.setAppTheme(theme)
        }
    }

    fun setBengaliNumeralsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repository.setBengaliNumeralsEnabled(enabled)
        }
    }

    fun setGeminiApiKey(key: String?) {
        viewModelScope.launch {
            repository.setGeminiApiKey(key)
        }
    }

    private val _apiConnectionTest = MutableStateFlow<ApiConnectionTestResult?>(null)
    val apiConnectionTest: StateFlow<ApiConnectionTestResult?> = _apiConnectionTest.asStateFlow()

    private val _isTestingApi = MutableStateFlow(false)
    val isTestingApi: StateFlow<Boolean> = _isTestingApi.asStateFlow()

    fun testGeminiApiKey(key: String) {
        val trimmed = key.trim()
        if (trimmed.isBlank()) return
        viewModelScope.launch {
            try {
                _isTestingApi.value = true
                val res = GeminiAiService.testApiKeyConnection(trimmed)
                _apiConnectionTest.value = res
                if (res.isSuccess) {
                    // Auto-persist successfully verified API key so it survives app/mobile restarts
                    repository.setGeminiApiKey(trimmed)
                }
            } catch (e: Exception) {
                _apiConnectionTest.value = ApiConnectionTestResult(
                    isSuccess = false,
                    statusType = ApiTestStatusType.ERROR,
                    message = e.localizedMessage ?: "Unexpected error"
                )
            } finally {
                _isTestingApi.value = false
            }
        }
    }

    fun clearApiTestResult() {
        _apiConnectionTest.value = null
    }

    fun setAiInsightsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repository.setAiInsightsEnabled(enabled)
        }
    }

    // ── Categories ───────────────────────────────────────────────────────

    fun addCategory(name: String, type: TransactionType, icon: String, colorHex: String) {
        viewModelScope.launch {
            val cat = Category(name = name, type = type, icon = icon, colorHex = colorHex)
            repository.addCategory(cat)
        }
    }

    fun updateCategory(category: Category) {
        viewModelScope.launch {
            repository.updateCategory(category)
        }
    }

    fun deleteCategory(id: Long) {
        viewModelScope.launch {
            repository.deleteCategory(id)
        }
    }

    suspend fun getCsvData(): String {
        return repository.exportToCsv()
    }

    suspend fun getJsonBackup(): String {
        return repository.exportToJson()
    }
}
