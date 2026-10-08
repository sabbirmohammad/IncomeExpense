package com.example.expensetracker.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.expensetracker.data.drive.GoogleDriveSyncManager
import com.example.expensetracker.data.repository.*

class DailySyncWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val repository = ExpenseRepository.getInstance(applicationContext)
        val profile = repository.userProfile.value

        if (!profile.isAutoSyncEnabled || profile.googleAccountEmail.isNullOrBlank()) {
            return Result.success()
        }

        val syncManager = GoogleDriveSyncManager(applicationContext)
        val result = syncManager.performBackup()

        return if (result.isSuccess) {
            Result.success()
        } else {
            val exception = result.exceptionOrNull()
            val msg = exception?.message ?: ""
            // Fail without infinite retry if user authorization is required or if we exceeded max attempts
            val isAuth = msg.contains("authorization", ignoreCase = true) ||
                    msg.contains("UserRecoverableAuthException", ignoreCase = true) ||
                    exception is com.google.android.gms.auth.UserRecoverableAuthException ||
                    exception?.cause is com.google.android.gms.auth.UserRecoverableAuthException
            if (isAuth || runAttemptCount >= 3) {
                Result.failure()
            } else {
                Result.retry()
            }
        }
    }
}
