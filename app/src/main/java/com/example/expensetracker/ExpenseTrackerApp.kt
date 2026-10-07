package com.example.expensetracker

import android.app.Application
import com.example.expensetracker.data.drive.GoogleDriveSyncManager
import com.example.expensetracker.data.repository.ExpenseRepository
import com.example.expensetracker.data.worker.BudgetReminderWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class ExpenseTrackerApp : Application() {

    private val appScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onCreate() {
        super.onCreate()
        val repo = ExpenseRepository.getInstance(this)
        val profile = repo.userProfile.value

        // Setup end-of-month budget reminder notification channel
        BudgetReminderWorker.createNotificationChannel(this, profile.appLanguage == "bn")

        // Schedule background workers asynchronously to prevent main thread stalls during startup
        appScope.launch {
            try {
                BudgetReminderWorker.schedule(applicationContext)

                if (profile.isAutoSyncEnabled && !profile.googleAccountEmail.isNullOrBlank()) {
                    val syncManager = GoogleDriveSyncManager(applicationContext)
                    syncManager.scheduleDailySync(true)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
