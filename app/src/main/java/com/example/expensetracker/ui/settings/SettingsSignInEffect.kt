package com.example.expensetracker.ui.settings

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

@Composable
fun SettingsSignInEffect(
    signInIntent: Intent?,
    onHandleResult: (Intent?) -> Unit,
    onClearIntent: () -> Unit
) {
    val signInLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        onHandleResult(result.data)
    }

    LaunchedEffect(signInIntent) {
        signInIntent?.let { intent ->
            try {
                signInLauncher.launch(intent)
            } catch (_: Exception) {
            }
            onClearIntent()
        }
    }
}
