package com.example.expensetracker.data.drive

import android.content.Context
import com.example.expensetracker.data.local.ExpenseDatabaseHelper
import com.example.expensetracker.data.repository.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal object DrivePreRestoreSnapshotHelper {

    suspend fun createSnapshot(
        context: Context,
        repository: ExpenseRepository,
        fallbackBakFileName: String,
        jsonBackupName: String
    ): File? = withContext(Dispatchers.IO) {
        try {
            val dbFile = context.getDatabasePath(ExpenseDatabaseHelper.DATABASE_NAME)
            if (!dbFile.exists()) return@withContext null

            val parentDir = dbFile.parentFile ?: context.filesDir
            if (!parentDir.exists()) parentDir.mkdirs()

            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            val timestampedBak = File(parentDir, "expense_tracker_pre_restore_$timestamp.db.bak")
            val fallbackBak = File(parentDir, fallbackBakFileName)

            // Copy active SQLite database
            dbFile.copyTo(timestampedBak, overwrite = true)
            dbFile.copyTo(fallbackBak, overwrite = true)

            // Preserve WAL / SHM files if present
            val walFile = File(parentDir, "${dbFile.name}-wal")
            if (walFile.exists()) {
                walFile.copyTo(File(parentDir, "${timestampedBak.name}-wal"), overwrite = true)
                walFile.copyTo(File(parentDir, "${fallbackBak.name}-wal"), overwrite = true)
            }
            val shmFile = File(parentDir, "${dbFile.name}-shm")
            if (shmFile.exists()) {
                shmFile.copyTo(File(parentDir, "${timestampedBak.name}-shm"), overwrite = true)
                shmFile.copyTo(File(parentDir, "${fallbackBak.name}-shm"), overwrite = true)
            }

            // Also create JSON snapshot for auxiliary human-readable recovery
            try {
                val jsonPayload = repository.exportToJson()
                val jsonFile = File(context.filesDir, jsonBackupName)
                jsonFile.writeText(jsonPayload)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            timestampedBak
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
