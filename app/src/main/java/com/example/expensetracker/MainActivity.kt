package com.example.expensetracker

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.example.expensetracker.data.repository.*
import com.example.expensetracker.data.worker.BudgetReminderWorker
import com.example.expensetracker.theme.ExpenseTrackerTheme
import com.example.expensetracker.ui.components.globalBengaliNumeralsEnabled
import com.example.expensetracker.ui.navigation.MainAppNavigation

class MainActivity : ComponentActivity() {

  private val showBudgetPromptFromNotification = mutableStateOf(false)

  private val requestPermissionLauncher = registerForActivityResult(
    ActivityResultContracts.RequestPermission()
  ) { _: Boolean ->
    // Notification permission response handled
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Request notification permission on Android 13+ (API 33+)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
      }
    }

    handleNotificationIntent(intent)

    val repository = ExpenseRepository.getInstance(applicationContext)

    enableEdgeToEdge()
    setContent {
      val userProfile by repository.userProfile.collectAsState()
      LaunchedEffect(userProfile.useBengaliNumerals, userProfile.appLanguage) {
        globalBengaliNumeralsEnabled = userProfile.useBengaliNumerals || userProfile.appLanguage.equals("bn", ignoreCase = true)
      }

      ExpenseTrackerTheme(themeMode = userProfile.appTheme) {
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = MaterialTheme.colorScheme.background
        ) {
          MainAppNavigation(
            triggerBudgetPromptFromNotification = showBudgetPromptFromNotification.value,
            onBudgetPromptHandled = { showBudgetPromptFromNotification.value = false }
          )
        }
      }
    }
  }

  override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    handleNotificationIntent(intent)
  }

  private fun handleNotificationIntent(intent: Intent?) {
    if (intent?.getBooleanExtra(BudgetReminderWorker.EXTRA_SHOW_BUDGET_PROMPT, false) == true) {
      showBudgetPromptFromNotification.value = true
    }
  }
}
