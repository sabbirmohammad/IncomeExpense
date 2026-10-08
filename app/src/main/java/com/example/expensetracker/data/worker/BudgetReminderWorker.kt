package com.example.expensetracker.data.worker

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.expensetracker.MainActivity
import com.example.expensetracker.R
import com.example.expensetracker.data.repository.*
import java.util.Calendar
import java.util.concurrent.TimeUnit

class BudgetReminderWorker(
    private val appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    companion object {
        const val WORK_NAME = "BudgetReminderWorker"
        const val CHANNEL_ID_BUDGET_REMINDERS = "budget_reminders"
        const val NOTIFICATION_ID_BUDGET_REMINDER = 1002
        const val EXTRA_SHOW_BUDGET_PROMPT = "extra_show_budget_prompt"
        const val EXTRA_TARGET_MONTH = "extra_target_month"
        const val EXTRA_TARGET_YEAR = "extra_target_year"

        fun schedule(context: Context) {
            val request = PeriodicWorkRequestBuilder<BudgetReminderWorker>(
                12, TimeUnit.HOURS
            ).build()

            WorkManager.getInstance(context.applicationContext).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }

        fun createNotificationChannel(context: Context, isBangla: Boolean = true) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID_BUDGET_REMINDERS,
                    if (isBangla) "বাজেট সতর্কতা" else "Budget Alerts",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = if (isBangla) "নতুন মাসের বাজেট অনুস্মারক" else "Upcoming month budget reminders"
                    enableVibration(true)
                }
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
        }
    }

    override suspend fun doWork(): Result {
        val cal = Calendar.getInstance()
        val currentDay = cal.get(Calendar.DAY_OF_MONTH)
        val maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

        // Only fire on the last day of the current month
        if (currentDay != maxDay) {
            return Result.success()
        }

        val repository = ExpenseRepository.getInstance(appContext)
        val profile = repository.userProfile.value
        val isBangla = profile.appLanguage == "bn"

        val calNext = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, 1) }
        val nextMonth = calNext.get(Calendar.MONTH)
        val nextYear = calNext.get(Calendar.YEAR)
        val targetKey = "$nextYear-$nextMonth"

        // Check if user was already notified for this target month
        if (repository.getLastBudgetNotifiedKey() == targetKey) {
            return Result.success()
        }

        // Check if user already set budgets for next month
        if (repository.hasBudgetsForMonth(nextMonth, nextYear)) {
            return Result.success()
        }

        createNotificationChannel(appContext, isBangla)

        val intent = Intent(appContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(EXTRA_SHOW_BUDGET_PROMPT, true)
            putExtra(EXTRA_TARGET_MONTH, nextMonth)
            putExtra(EXTRA_TARGET_YEAR, nextYear)
        }

        val pendingIntent = PendingIntent.getActivity(
            appContext,
            NOTIFICATION_ID_BUDGET_REMINDER,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        val title = if (isBangla) "নতুন মাসের বাজেট প্ল্যানিং" else "Upcoming Month Budget"
        val message = if (isBangla) {
            "আগামীকাল থেকে নতুন মাস শুরু হচ্ছে! আপনার নতুন মাসের বাজেট প্রস্তুত করুন।"
        } else {
            "Tomorrow starts a new month! Plan your upcoming budget now."
        }

        val notification = NotificationCompat.Builder(appContext, CHANNEL_ID_BUDGET_REMINDERS)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ActivityCompat.checkSelfPermission(appContext, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        ) {
            val notificationManager = NotificationManagerCompat.from(appContext)
            notificationManager.notify(NOTIFICATION_ID_BUDGET_REMINDER, notification)
            repository.markBudgetNotified(targetKey)
        }

        return Result.success()
    }
}
