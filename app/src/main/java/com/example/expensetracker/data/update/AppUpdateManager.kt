package com.example.expensetracker.data.update

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.expensetracker.common.AppConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

data class AppUpdateInfo(
    val latestVersion: String,
    val releaseNotes: String,
    val downloadUrl: String,
    val apkSize: Long,
    val isUpdateAvailable: Boolean
)

sealed interface UpdateState {
    data object Idle : UpdateState
    data object Checking : UpdateState
    data class Available(val updateInfo: AppUpdateInfo) : UpdateState
    data class Downloading(val progress: Float, val downloadedBytes: Long, val totalBytes: Long) : UpdateState
    data class ReadyToInstall(val apkFile: File) : UpdateState
    data class Error(val message: String, val isRateLimited: Boolean = false) : UpdateState
    data class UpToDate(val currentVersion: String) : UpdateState
}

object AppUpdateManager {
    private const val TAG = "AppUpdateManager"
    private const val GITHUB_REPO_API = "https://api.github.com/repos/sabbirmohammad/IncomeExpense/releases/latest"
    private const val GITHUB_WEB_LATEST_RELEASE = "https://github.com/sabbirmohammad/IncomeExpense/releases/latest"
    private const val GITHUB_EXPANDED_ASSETS_BASE = "https://github.com/sabbirmohammad/IncomeExpense/releases/expanded_assets/"
    private const val PREFS_NAME = "app_update_prefs"
    private const val KEY_LAST_CHECK_TIME = "last_auto_check_timestamp"
    private const val AUTO_CHECK_INTERVAL_MS = 2 * 60 * 60 * 1000L // 2 hours (reduced for responsive updates)

    private val _updateState = MutableStateFlow<UpdateState>(UpdateState.Idle)
    val updateState: StateFlow<UpdateState> = _updateState.asStateFlow()

    private val _isDialogVisible = MutableStateFlow(false)
    val isDialogVisible: StateFlow<Boolean> = _isDialogVisible.asStateFlow()

    fun dismissDialog() {
        _isDialogVisible.value = false
        if (_updateState.value !is UpdateState.Downloading) {
            _updateState.value = UpdateState.Idle
        }
    }

    suspend fun checkForUpdates(context: Context, isManualCheck: Boolean = false) {
        if (_updateState.value is UpdateState.Downloading) return

        // For automatic background checks, enforce cooldown to avoid rate limits
        if (!isManualCheck) {
            try {
                val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                val lastCheck = prefs.getLong(KEY_LAST_CHECK_TIME, 0L)
                val now = System.currentTimeMillis()
                if (now - lastCheck < AUTO_CHECK_INTERVAL_MS) {
                    Log.d(TAG, "Skipping background update check (Cooldown active)")
                    return
                }
            } catch (_: Exception) {}
        }

        _updateState.value = UpdateState.Checking

        withContext(Dispatchers.IO) {
            var updateInfo: AppUpdateInfo? = null
            var is404NoRelease = false
            var lastResponseCode = 0

            // 1. First attempt: Official GitHub REST API
            try {
                val url = URL(GITHUB_REPO_API)
                val connection = (url.openConnection() as HttpURLConnection).apply {
                    connectTimeout = 8000
                    readTimeout = 10000
                    setRequestProperty("Accept", "application/vnd.github.v3+json")
                    setRequestProperty("User-Agent", "ExpenseTracker-Android")
                }

                lastResponseCode = connection.responseCode
                if (lastResponseCode == 404) {
                    is404NoRelease = true
                } else if (lastResponseCode == 200) {
                    val responseText = connection.inputStream.bufferedReader().use { it.readText() }
                    val json = JSONObject(responseText)

                    val tagName = json.optString("tag_name", "").trim()
                    val body = json.optString("body", "নতুন ফিচার এবং বাগ ফিক্স অন্তর্ভুক্ত।").trim()
                    val assets = json.optJSONArray("assets")

                    var apkDownloadUrl = ""
                    var apkSize = 0L

                    if (assets != null) {
                        for (i in 0 until assets.length()) {
                            val asset = assets.getJSONObject(i)
                            val name = asset.optString("name", "")
                            if (name.endsWith(".apk", ignoreCase = true)) {
                                apkDownloadUrl = asset.optString("browser_download_url", "")
                                apkSize = asset.optLong("size", 0L)
                                break
                            }
                        }
                    }

                    if (tagName.isNotEmpty() && apkDownloadUrl.isNotEmpty()) {
                        updateInfo = AppUpdateInfo(
                            latestVersion = tagName,
                            releaseNotes = body,
                            downloadUrl = apkDownloadUrl,
                            apkSize = apkSize,
                            isUpdateAvailable = true
                        )
                    }
                } else {
                    Log.w(TAG, "GitHub API returned code $lastResponseCode, falling back to Web Release check")
                }
                connection.disconnect()
            } catch (e: Exception) {
                Log.w(TAG, "GitHub API request failed: ${e.message}, falling back to Web Release check")
            }

            // 2. Fallback: If GitHub API returned 403 (Rate Limited) or failed, use Web 302 Redirect
            if (updateInfo == null && !is404NoRelease) {
                try {
                    updateInfo = fetchUpdateInfoViaWebFallback()
                } catch (e: Exception) {
                    Log.e(TAG, "Web redirect fallback failed", e)
                }
            }

            // 3. Handle resolution
            if (updateInfo != null) {
                try {
                    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    prefs.edit().putLong(KEY_LAST_CHECK_TIME, System.currentTimeMillis()).apply()
                } catch (_: Exception) {}

                val currentVersion = getCurrentVersion(context)
                val isNewer = isNewerVersion(current = currentVersion, remote = updateInfo.latestVersion)

                if (isNewer && updateInfo.downloadUrl.isNotEmpty()) {
                    _updateState.value = UpdateState.Available(updateInfo)
                    _isDialogVisible.value = true
                } else {
                    if (isManualCheck) {
                        _updateState.value = UpdateState.UpToDate(currentVersion)
                        _isDialogVisible.value = true
                    } else {
                        _updateState.value = UpdateState.Idle
                    }
                }
            } else if (is404NoRelease) {
                if (isManualCheck) {
                    val currentVersion = getCurrentVersion(context)
                    _updateState.value = UpdateState.UpToDate(currentVersion)
                    _isDialogVisible.value = true
                } else {
                    _updateState.value = UpdateState.Idle
                }
            } else {
                if (isManualCheck) {
                    val isRateLimited = (lastResponseCode == 403)
                    val errorMsg = if (isRateLimited) "RATE_LIMIT" else "NETWORK_ERROR"
                    _updateState.value = UpdateState.Error(message = errorMsg, isRateLimited = isRateLimited)
                    _isDialogVisible.value = true
                } else {
                    _updateState.value = UpdateState.Idle
                }
            }
        }
    }

    private fun fetchUpdateInfoViaWebFallback(): AppUpdateInfo? {
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
                    val headConn = openConnectionWithRedirects(apkUrl)
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

    suspend fun startDownload(context: Context, downloadUrl: String, expectedSize: Long) {
        _updateState.value = UpdateState.Downloading(0f, 0L, expectedSize)

        withContext(Dispatchers.IO) {
            try {
                val updatesDir = File(context.cacheDir, "updates").apply { mkdirs() }
                val targetFile = File(updatesDir, "ExpenseTracker-update.apk")
                if (targetFile.exists()) {
                    targetFile.delete()
                }

                var currentUrl = downloadUrl
                var connection = openConnectionWithRedirects(currentUrl)

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
                                _updateState.value = UpdateState.Downloading(progress, downloadedBytes, totalBytes)
                            }
                        }
                        output.flush()
                    }
                }

                // Final download state emission before install
                _updateState.value = UpdateState.Downloading(1f, downloadedBytes, if (totalBytes > 0) totalBytes else downloadedBytes)
                _updateState.value = UpdateState.ReadyToInstall(targetFile)

                // Trigger install directly once download is complete
                withContext(Dispatchers.Main) {
                    installApk(context, targetFile)
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error downloading update", e)
                _updateState.value = UpdateState.Error("ডাউনলোড ব্যর্থ হয়েছে: ${e.localizedMessage ?: "অজানা ত্রুটি"}")
            }
        }
    }

    private fun openConnectionWithRedirects(initialUrl: String): HttpURLConnection {
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

    fun installApk(context: Context, apkFile: File) {
        if (!apkFile.exists()) {
            Toast.makeText(context, "ইনস্টলার ফাইল খুঁজে পাওয়া যায়নি", Toast.LENGTH_SHORT).show()
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!context.packageManager.canRequestPackageInstalls()) {
                val manageSourcesIntent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                    data = Uri.parse("package:${context.packageName}")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(manageSourcesIntent)
                Toast.makeText(context, "আপডেট ইনস্টল করতে পারমিশন অন করুন", Toast.LENGTH_LONG).show()
                return
            }
        }

        try {
            val apkUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                apkFile
            )

            val installIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(apkUri, "application/vnd.android.package-archive")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(installIntent)
        } catch (e: Exception) {
            Log.e(TAG, "Error launching package installer", e)
            Toast.makeText(context, "ইনস্টলার চালু করতে ব্যর্থ হয়েছে: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    fun getCurrentVersion(context: Context): String {
        return try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            pInfo.versionName?.removePrefix("v")?.removePrefix("V") ?: AppConstants.getAppVersionClean(context)
        } catch (_: Exception) {
            AppConstants.getAppVersionClean(context)
        }
    }

    fun isNewerVersion(current: String, remote: String): Boolean {
        val curClean = current.trim().removePrefix("v").removePrefix("V")
        val remClean = remote.trim().removePrefix("v").removePrefix("V")

        val curParts = curClean.split(".").mapNotNull { it.toIntOrNull() }
        val remParts = remClean.split(".").mapNotNull { it.toIntOrNull() }

        val maxLen = maxOf(curParts.size, remParts.size)
        for (i in 0 until maxLen) {
            val c = curParts.getOrElse(i) { 0 }
            val r = remParts.getOrElse(i) { 0 }
            if (r > c) return true
            if (r < c) return false
        }
        return false
    }
}
