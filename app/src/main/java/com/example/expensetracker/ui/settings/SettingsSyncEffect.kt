package com.example.expensetracker.ui.settings

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.example.expensetracker.ui.components.LocalAppStrings

@Composable
fun SettingsSyncEffect(
    syncMessage: String?,
    snackbarHostState: SnackbarHostState,
    onRetryBackup: () -> Unit,
    onClearSyncMessage: () -> Unit
) {
    val strings = LocalAppStrings.current

    LaunchedEffect(syncMessage) {
        syncMessage?.let { msg ->
            val isError = msg.contains("failed", ignoreCase = true) ||
                    msg.contains("error", ignoreCase = true) ||
                    msg.contains("বিফল", ignoreCase = true) ||
                    msg.contains("ত্রুটি", ignoreCase = true)
            val actionLabel = if (isError) (if (strings.isBangla) "পুনরায় চেষ্টা" else "Retry") else null
            val result = snackbarHostState.showSnackbar(
                message = msg,
                actionLabel = actionLabel,
                duration = if (isError) SnackbarDuration.Long else SnackbarDuration.Short
            )
            if (result == SnackbarResult.ActionPerformed) {
                onRetryBackup()
            }
            onClearSyncMessage()
        }
    }
}
