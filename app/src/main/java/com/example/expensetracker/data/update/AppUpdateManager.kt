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
    data class Error(val message: String) : UpdateState
}

object AppUpdateManager {
    private const val TAG = "AppUpdateManager"
    private const val GITHUB_REPO_API = "https://api.github.com/repos/sabbirmohammad/IncomeExpense/releases/latest"

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

        _updateState.value = UpdateState.Checking

        withContext(Dispatchers.IO) {
            try {
                val url = URL(GITHUB_REPO_API)
                val connection = (url.openConnection() as HttpURLConnection).apply {
                    connectTimeout = 10000
                    readTimeout = 15000
                    setRequestProperty("Accept", "application/vnd.github.v3+json")
                    setRequestProperty("User-Agent", "ExpenseTracker-Android")
                }

                val responseCode = connection.responseCode
                if (responseCode == 404) {
                    // No release published yet
                    _updateState.value = UpdateState.Idle
                    if (isManualCheck) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "কোনো নতুন আপডেট পাওয়া যায়নি (v${getCurrentVersion(context)})", Toast.LENGTH_SHORT).show()
                        }
                    }
                    return@withContext
                }

                if (responseCode != 200) {
                    _updateState.value = UpdateState.Idle
                    if (isManualCheck) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "আপডেট চেক করতে সমস্যা হয়েছে (কোড: $responseCode)", Toast.LENGTH_SHORT).show()
                        }
                    }
                    return@withContext
                }

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

                val currentVersion = getCurrentVersion(context)
                val isNewer = isNewerVersion(current = currentVersion, remote = tagName)

                if (isNewer && apkDownloadUrl.isNotEmpty()) {
                    val updateInfo = AppUpdateInfo(
                        latestVersion = tagName,
                        releaseNotes = body,
                        downloadUrl = apkDownloadUrl,
                        apkSize = apkSize,
                        isUpdateAvailable = true
                    )
                    _updateState.value = UpdateState.Available(updateInfo)
                    _isDialogVisible.value = true
                } else {
                    _updateState.value = UpdateState.Idle
                    if (isManualCheck) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "আপনার অ্যাপ্লিকেশনটি আপ-টু-ডেট আছে (v$currentVersion)", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error checking for updates", e)
                _updateState.value = UpdateState.Idle
                if (isManualCheck) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "ইন্টারনেট কানেকশন চেক করুন", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
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

                val totalBytes = if (connection.contentLengthLong > 0) connection.contentLengthLong else expectedSize
                var downloadedBytes = 0L

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
                            _updateState.value = UpdateState.Downloading(progress, downloadedBytes, totalBytes)
                        }
                        output.flush()
                    }
                }

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

    private fun getCurrentVersion(context: Context): String {
        return try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            pInfo.versionName?.removePrefix("v")?.removePrefix("V") ?: AppConstants.APP_VERSION.removePrefix("v")
        } catch (_: Exception) {
            AppConstants.APP_VERSION.removePrefix("v")
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
