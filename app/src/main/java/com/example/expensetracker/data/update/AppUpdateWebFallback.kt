package com.example.expensetracker.data.update

import android.util.Log
import java.net.HttpURLConnection
import java.net.URL

object AppUpdateWebFallback {
    private const val TAG = "AppUpdateWebFallback"
    private const val GITHUB_WEB_LATEST_RELEASE = "https://github.com/sabbirmohammad/IncomeExpense/releases/latest"
    private const val GITHUB_EXPANDED_ASSETS_BASE = "https://github.com/sabbirmohammad/IncomeExpense/releases/expanded_assets/"

    fun fetchUpdateInfo(): AppUpdateInfo? {
        val conn = (URL(GITHUB_WEB_LATEST_RELEASE).openConnection() as HttpURLConnection).apply {
            connectTimeout = 10000
            readTimeout = 10000
            instanceFollowRedirects = false
            setRequestProperty("User-Agent", "ExpenseTracker-Android")
        }
        val code = conn.responseCode
        val location = conn.getHeaderField("Location")
        conn.disconnect()

        if (code in 300..399 && !location.isNullOrEmpty()) {
            val tagName = location.substringAfterLast("/").trim()
            if (tagName.isNotEmpty()) {
                var apkUrl = "https://github.com/sabbirmohammad/IncomeExpense/releases/download/$tagName/ExpenseTracker-$tagName.apk"
                var apkSize = 0L

                // Attempt to fetch exact asset download URL from GitHub expanded assets
                try {
                    val assetsConn = (URL("$GITHUB_EXPANDED_ASSETS_BASE$tagName").openConnection() as HttpURLConnection).apply {
                        connectTimeout = 8000
                        readTimeout = 8000
                        setRequestProperty("User-Agent", "ExpenseTracker-Android")
                    }
                    if (assetsConn.responseCode == 200) {
                        val html = assetsConn.inputStream.bufferedReader().use { it.readText() }
                        val apkMatch = Regex("""href="([^"]+\.apk)"""").find(html)
                        if (apkMatch != null) {
                            val path = apkMatch.groupValues[1]
                            apkUrl = if (path.startsWith("http")) path else "https://github.com$path"
                        }
                    }
                    assetsConn.disconnect()
                } catch (e: Exception) {
                    Log.w(TAG, "Expanded assets check fallback to default apk url pattern: ${e.message}")
                }

                // Attempt lightweight HEAD probe to determine APK size in advance
                try {
                    val headConn = AppUpdateDownloader.openConnectionWithRedirects(apkUrl)
                    val headerLen = headConn.getHeaderField("Content-Length")?.toLongOrNull() ?: -1L
                    val resolvedLen = if (headConn.contentLengthLong > 0) headConn.contentLengthLong else headerLen
                    if (resolvedLen > 0) {
                        apkSize = resolvedLen
                    }
                    headConn.disconnect()
                } catch (e: Exception) {
                    Log.w(TAG, "HEAD probe for apk size failed: ${e.message}")
                }

                return AppUpdateInfo(
                    latestVersion = tagName,
                    releaseNotes = "নতুন ফিচার এবং বাগ ফিক্স অন্তর্ভুক্ত।",
                    downloadUrl = apkUrl,
                    apkSize = apkSize,
                    isUpdateAvailable = true
                )
            }
        }
        return null
    }
}
