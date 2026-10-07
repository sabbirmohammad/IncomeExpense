package com.example.expensetracker.ui.settings

import android.app.Application
import android.content.Intent
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import kotlinx.coroutines.launch

/**
 * Initiates the Google sign-in flow.
 */
fun SettingsViewModel.initiateGoogleSignIn() {
    val lastAccount = authHelper.getSignedInAccount()
    if (lastAccount != null && authHelper.hasDrivePermission(lastAccount)) {
        val email = lastAccount.email ?: lastAccount.account?.name
        if (!email.isNullOrBlank()) {
            viewModelScope.launch {
                val updated = repository.userProfile.value.copy(googleAccountEmail = email)
                repository.updateProfile(updated)
                _syncMessage.value = "Connected: $email"
            }
            return
        }
    }
    _signInIntent.value = authHelper.getSignInIntent()
}

fun SettingsViewModel.clearSignInIntent() {
    _signInIntent.value = null
}

/**
 * Handles the result of the Google Sign-in activity.
 */
fun SettingsViewModel.handleSignInResult(data: Intent?) {
    viewModelScope.launch {
        try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)
            val email = account?.email ?: account?.account?.name
            if (!email.isNullOrBlank()) {
                val updated = repository.userProfile.value.copy(
                    googleAccountEmail = email
                )
                repository.updateProfile(updated)
                if (updated.isAutoSyncEnabled) {
                    syncManager.scheduleDailySync(true)
                }
                _syncMessage.value = "Connected: $email"
            } else {
                _syncMessage.value = "Sign-in succeeded but could not retrieve email."
            }
        } catch (e: ApiException) {
            when (e.statusCode) {
                GoogleSignInStatusCodes.SIGN_IN_CANCELLED -> {
                    _syncMessage.value = "Google Sign-in cancelled"
                }
                CommonStatusCodes.NETWORK_ERROR -> {
                    _syncMessage.value = "Sign-in failed: Network error. Please check your internet connection."
                }
                else -> {
                    _syncMessage.value = "Sign-in failed (${e.statusCode}): ${e.localizedMessage ?: e.message}"
                }
            }
        } catch (e: Exception) {
            _syncMessage.value = "Sign-in failed: ${e.localizedMessage ?: e.message}"
        }
    }
}

fun SettingsViewModel.disconnectGoogleAccount() {
    viewModelScope.launch {
        authHelper.signOut()
        val updated = repository.userProfile.value.copy(
            googleAccountEmail = null
        )
        repository.updateProfile(updated)
        syncManager.scheduleDailySync(false)
        _syncMessage.value = "Google Account disconnected"
    }
}

fun SettingsViewModel.setAutoSync(enabled: Boolean) {
    viewModelScope.launch {
        val updated = repository.userProfile.value.copy(
            isAutoSyncEnabled = enabled
        )
        repository.updateProfile(updated)
        syncManager.scheduleDailySync(enabled)
    }
}

fun SettingsViewModel.backupToGoogleDriveNow() {
    viewModelScope.launch {
        _isSyncing.value = true
        val result = syncManager.performBackup()
        _isSyncing.value = false
        _syncMessage.value = if (result.isSuccess) {
            result.getOrNull() ?: "Backup successful!"
        } else {
            "Backup failed: ${result.exceptionOrNull()?.message}"
        }
    }
}

fun SettingsViewModel.restoreFromGoogleDrive() {
    viewModelScope.launch {
        _isSyncing.value = true
        val result = syncManager.performRestore()
        _isSyncing.value = false
        _syncMessage.value = if (result.isSuccess) {
            result.getOrNull() ?: "Restored successfully!"
        } else {
            "Restore failed: ${result.exceptionOrNull()?.message}"
        }
    }
}

fun SettingsViewModel.restoreFromUri(uri: android.net.Uri) {
    viewModelScope.launch {
        _isSyncing.value = true
        try {
            val context = getApplication<Application>()
            val json = context.contentResolver.openInputStream(uri)?.use { stream ->
                stream.bufferedReader(Charsets.UTF_8).readText()
            }
            if (json.isNullOrBlank()) {
                _isSyncing.value = false
                _syncMessage.value = "Selected backup file is empty."
                return@launch
            }
            val success = repository.restoreFromJson(json)
            _isSyncing.value = false
            _syncMessage.value = if (success) {
                "Backup restored successfully from file!"
            } else {
                "Restore failed: Invalid or incompatible backup file format."
            }
        } catch (e: Exception) {
            _isSyncing.value = false
            _syncMessage.value = "Restore failed: ${e.localizedMessage ?: e.message}"
        }
    }
}

fun SettingsViewModel.clearSyncMessage() {
    _syncMessage.value = null
}
