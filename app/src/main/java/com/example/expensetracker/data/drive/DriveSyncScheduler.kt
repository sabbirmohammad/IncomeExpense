package com.example.expensetracker.data.drive

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.expensetracker.data.repository.ExpenseRepository
import com.example.expensetracker.data.worker.DailySyncWorker
import java.util.concurrent.TimeUnit

internal object DriveSyncScheduler {

    fun scheduleDailySync(context: Context, enabled: Boolean) {
        val workManager = WorkManager.getInstance(context)
        val workName = "DailyGoogleDriveSync"

        if (enabled) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val syncRequest = PeriodicWorkRequestBuilder<DailySyncWorker>(
                24, TimeUnit.HOURS,
                1, TimeUnit.HOURS
            )
                .setConstraints(constraints)
                .build()

            workManager.enqueueUniquePeriodicWork(
                workName,
                ExistingPeriodicWorkPolicy.KEEP,
                syncRequest
            )
        } else {
            workManager.cancelUniqueWork(workName)
        }
    }

    fun triggerAutoSync(context: Context, repository: ExpenseRepository) {
        try {
            val profile = repository.userProfile.value
            if (!profile.isAutoSyncEnabled || profile.googleAccountEmail.isNullOrBlank()) {
                return
            }

            val workManager = WorkManager.getInstance(context)
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val syncRequest = OneTimeWorkRequestBuilder<DailySyncWorker>()
                .setConstraints(constraints)
                .build()

            workManager.enqueueUniqueWork(
                "InstantGoogleDriveSync",
                ExistingWorkPolicy.REPLACE,
                syncRequest
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
