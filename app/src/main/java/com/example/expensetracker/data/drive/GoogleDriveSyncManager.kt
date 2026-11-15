package com.example.expensetracker.data.drive

import android.content.Context
import com.example.expensetracker.data.repository.*
import com.google.api.client.http.ByteArrayContent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Manages Google Drive backup & restore using the Drive REST API (AppDataFolder).
 * Also schedules daily automatic sync via WorkManager.
 */
class GoogleDriveSyncManager(private val context: Context) {

    private val repository = ExpenseRepository.getInstance(context)
    private val authHelper = GoogleAuthHelper(context)

    companion object {
        private const val BACKUP_FILE_NAME = "ExpenseTracker_Backup.json"
        const val PRE_RESTORE_BAK_FILE_NAME = "expense_tracker.db.bak"
        const val PRE_RESTORE_JSON_BACKUP_NAME = "pre_restore_backup.json"
    }

    /**
     * Creates a local timestamped backup copy and fallback .bak snapshot of the active SQLite
     * database file before downloading and overwriting it during Google Drive restore.
     */
    suspend fun createPreRestoreSnapshot(): File? =
        DrivePreRestoreSnapshotHelper.createSnapshot(
            context,
            repository,
            PRE_RESTORE_BAK_FILE_NAME,
            PRE_RESTORE_JSON_BACKUP_NAME
        )

    /**
     * Exports all app data to JSON and uploads it to Google Drive's AppDataFolder.
     * Updates the existing backup file if one exists; creates a new one otherwise.
     */
    suspend fun performBackup(): Result<String> = withContext(Dispatchers.IO) {
        try {
            val profile = repository.userProfile.value
            val email = profile.googleAccountEmail
                ?: return@withContext Result.failure(
                    IllegalStateException("No Google account connected. Please sign in first.")
                )

            val driveService = authHelper.buildDriveService(email)
            val jsonPayload = repository.exportToJson()

            // Check if a backup file already exists in AppDataFolder (excluding trashed files)
            val existingFiles = driveService.files().list()
                .setSpaces("appDataFolder")
                .setQ("name = '$BACKUP_FILE_NAME' and trashed = false")
                .setOrderBy("modifiedTime desc")
                .setFields("files(id, name, modifiedTime)")
                .execute()

            val files = existingFiles.files.orEmpty()
            val mediaContent = ByteArrayContent.fromString("application/json", jsonPayload)

            if (files.isNotEmpty()) {
                // Update the latest existing backup file
                driveService.files()
                    .update(files[0].id, com.google.api.services.drive.model.File(), mediaContent)
                    .execute()

                // Clean up any extra duplicate backup files in appDataFolder if present
                for (i in 1 until files.size) {
                    try {
                        driveService.files().delete(files[i].id).execute()
                    } catch (_: Exception) {
                        // Best effort cleanup
                    }
                }
            } else {
                // Create a new backup file in AppDataFolder
                val fileMetadata = com.google.api.services.drive.model.File().apply {
                    name = BACKUP_FILE_NAME
                    parents = listOf("appDataFolder")
                }
                driveService.files()
                    .create(fileMetadata, mediaContent)
                    .setFields("id")
                    .execute()
            }

            // Record sync timestamp
            val now = System.currentTimeMillis()
            repository.updateProfile(profile.copy(lastSyncMillis = now))

            val sdf = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
            Result.success("Backed up to Google Drive at ${sdf.format(Date(now))}")
        } catch (e: Exception) {
            val isAuth = e is com.google.android.gms.auth.UserRecoverableAuthException ||
                    e.cause is com.google.android.gms.auth.UserRecoverableAuthException ||
                    (e.message?.contains("UserRecoverableAuthException", ignoreCase = true) == true)
            if (isAuth) {
                Result.failure(Exception("Google authorization required. Please reconnect your Google account.", e))
            } else {
                Result.failure(e)
            }
        }
    }

    /**
     * Downloads the backup file from Google Drive's AppDataFolder and restores app data.
     */
    suspend fun performRestore(): Result<String> = withContext(Dispatchers.IO) {
        try {
            val profile = repository.userProfile.value
            val email = profile.googleAccountEmail
                ?: return@withContext Result.failure(
                    IllegalStateException("No Google account connected. Please sign in first.")
                )

            val driveService = authHelper.buildDriveService(email)

            val existingFiles = driveService.files().list()
                .setSpaces("appDataFolder")
                .setQ("name = '$BACKUP_FILE_NAME' and trashed = false")
                .setOrderBy("modifiedTime desc")
                .setFields("files(id, name, modifiedTime)")
                .execute()

            val files = existingFiles.files.orEmpty()
            if (files.isEmpty()) {
                return@withContext Result.failure(
                    IllegalStateException("No backup found on Google Drive. Create a backup first.")
                )
            }

            // Create local safety snapshot prior to active file replacement
            createPreRestoreSnapshot()

            val outputStream = ByteArrayOutputStream()
            driveService.files()
                .get(files[0].id)
                .executeMediaAndDownloadTo(outputStream)
            val jsonPayload = outputStream.toString("UTF-8")

            val restored = repository.restoreFromJson(jsonPayload)
            if (restored) {
                Result.success("Restored from Google Drive successfully!")
            } else {
                Result.failure(IllegalStateException("Failed to parse Google Drive backup data."))
            }
        } catch (e: Exception) {
            val isAuth = e is com.google.android.gms.auth.UserRecoverableAuthException ||
                    e.cause is com.google.android.gms.auth.UserRecoverableAuthException ||
                    (e.message?.contains("UserRecoverableAuthException", ignoreCase = true) == true)
            if (isAuth) {
                Result.failure(Exception("Google authorization required. Please reconnect your Google account.", e))
            } else {
                Result.failure(e)
            }
        }
    }

    fun scheduleDailySync(enabled: Boolean) {
        DriveSyncScheduler.scheduleDailySync(context, enabled)
    }

    fun triggerAutoSync() {
        DriveSyncScheduler.triggerAutoSync(context, repository)
    }
}
