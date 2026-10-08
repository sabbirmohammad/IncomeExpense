package com.example.expensetracker.data.repository

import com.example.expensetracker.data.model.BackupPayload
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

suspend fun ExpenseRepository.exportToJson(): String = withContext(Dispatchers.IO) {
    val payload = dbHelper.createBackupPayload()
    jsonFormatter.encodeToString(payload)
}

suspend fun ExpenseRepository.exportToCsv(): String = withContext(Dispatchers.IO) {
    val txs = dbHelper.getAllTransactions()
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    val sb = StringBuilder()
    sb.append("ID,Date,Title,Type,Category,Amount,Payment Method,Notes\n")
    for (tx in txs) {
        val dateStr = sdf.format(Date(tx.dateMillis))
        val safeTitle = tx.title.replace(",", ";").replace("\"", "'")
        val safeNotes = tx.notes.replace(",", ";").replace("\"", "'")
        val safeCategory = tx.categoryName.replace(",", ";")
        sb.append("${tx.id},\"$dateStr\",\"$safeTitle\",${tx.type.name},\"$safeCategory\",${tx.amount},${tx.paymentMethod.name},\"$safeNotes\"\n")
    }
    sb.toString()
}

suspend fun ExpenseRepository.restoreFromJson(jsonString: String): Boolean = withContext(Dispatchers.IO) {
    try {
        val payload = jsonFormatter.decodeFromString<BackupPayload>(jsonString)
        val success = dbHelper.restoreFromBackup(payload)
        if (success) {
            refreshAll()
        }
        success
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}
