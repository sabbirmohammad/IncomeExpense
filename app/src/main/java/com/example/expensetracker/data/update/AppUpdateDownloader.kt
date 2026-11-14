package com.example.expensetracker.data.update

import android.content.Context
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

object AppUpdateDownloader {

    fun openConnectionWithRedirects(initialUrl: String): HttpURLConnection {
        var currentUrl = initialUrl
        var redirectCount = 0
        while (redirectCount < 6) {
            val conn = (URL(currentUrl).openConnection() as HttpURLConnection).apply {
                connectTimeout = 15000
                readTimeout = 30000
                instanceFollowRedirects = false
                setRequestProperty("User-Agent", "ExpenseTracker-Android")
                setRequestProperty("Accept-Encoding", "identity") // Prevents OkHttp from stripping Content-Length
            }
            val status = conn.responseCode
            if (status in 300..399) {
                val newUrl = conn.getHeaderField("Location") ?: break
                conn.disconnect()
                currentUrl = newUrl
                redirectCount++
            } else {
                return conn
            }
        }
        return (URL(currentUrl).openConnection() as HttpURLConnection).apply {
            connectTimeout = 15000
            readTimeout = 30000
            setRequestProperty("User-Agent", "ExpenseTracker-Android")
            setRequestProperty("Accept-Encoding", "identity")
        }
    }

    fun downloadApk(
        context: Context,
        downloadUrl: String,
        expectedSize: Long,
        onProgress: (progress: Float, downloaded: Long, total: Long) -> Unit
    ): File {
        val updatesDir = File(context.cacheDir, "updates").apply { mkdirs() }
        val targetFile = File(updatesDir, "ExpenseTracker-update.apk")
        if (targetFile.exists()) {
            targetFile.delete()
        }

        val connection = openConnectionWithRedirects(downloadUrl)
        val headerLen = connection.getHeaderField("Content-Length")?.toLongOrNull() ?: -1L
        val connLen = if (connection.contentLengthLong > 0) connection.contentLengthLong else headerLen
        val totalBytes = if (connLen > 0) connLen else expectedSize
        var downloadedBytes = 0L

        var lastEmitTime = 0L
        var lastEmittedProgress = -1f

        connection.inputStream.use { input ->
            targetFile.outputStream().use { output ->
                val buffer = ByteArray(16384)
                var bytesRead: Int
                while (input.read(buffer).also { bytesRead = it } != -1) {
                    output.write(buffer, 0, bytesRead)
                    downloadedBytes += bytesRead
                    val progress = if (totalBytes > 0) {
                        (downloadedBytes.toFloat() / totalBytes.toFloat()).coerceIn(0f, 1f)
                    } else 0f

                    val now = System.currentTimeMillis()
                    // Throttle progress emissions: at most once every 60ms or if progress shifted by >= 0.5%
                    if (now - lastEmitTime >= 60L || Math.abs(progress - lastEmittedProgress) >= 0.005f || (totalBytes > 0 && downloadedBytes >= totalBytes)) {
                        lastEmitTime = now
                        lastEmittedProgress = progress
                        onProgress(progress, downloadedBytes, totalBytes)
                    }
                }
                output.flush()
            }
        }

        // Final progress callback
        onProgress(1f, downloadedBytes, if (totalBytes > 0) totalBytes else downloadedBytes)
        return targetFile
    }
}
