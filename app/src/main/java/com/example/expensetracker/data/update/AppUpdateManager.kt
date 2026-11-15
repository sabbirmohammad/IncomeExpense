package com.example.expensetracker.data.update

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

object AppUpdateManager {
    private const val TAG = "AppUpdateManager"
    private const val GITHUB_REPO_API = "https://api.github.com/repos/sabbirmohammad/IncomeExpense/releases/latest"
    private const val PREFS_NAME = "app_update_prefs"
    private const val KEY_LAST_CHECK_TIME = "last_auto_check_timestamp"
    private const val AUTO_CHECK_INTERVAL_MS = 2 * 60 * 60 * 1000L

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

        if (!isManualCheck) {
            try {
                val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                val lastCheck = prefs.getLong(KEY_LAST_CHECK_TIME, 0L)
                val now = System.currentTimeMillis()
                if (now - lastCheck < AUTO_CHECK_INTERVAL_MS) {
                    return
                }
            } catch (_: Exception) {}
        }

        _updateState.value = UpdateState.Checking

        withContext(Dispatchers.IO) {
            var updateInfo: AppUpdateInfo? = null
            var is404NoRelease = false
            var lastResponseCode = 0

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

            if (updateInfo == null && !is404NoRelease) {
                try {
                    updateInfo = AppUpdateWebFallback.fetchUpdateInfo()
                } catch (e: Exception) {
                    Log.e(TAG, "Web redirect fallback failed", e)
                }
            }

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

    suspend fun startDownload(context: Context, downloadUrl: String, expectedSize: Long) {
        _updateState.value = UpdateState.Downloading(0f, 0L, expectedSize)

        withContext(Dispatchers.IO) {
            try {
                val targetFile = AppUpdateDownloader.downloadApk(
                    context = context,
                    downloadUrl = downloadUrl,
                    expectedSize = expectedSize,
                    onProgress = { progress, downloaded, total ->
                        _updateState.value = UpdateState.Downloading(progress, downloaded, total)
                    }
                )

                _updateState.value = UpdateState.ReadyToInstall(targetFile)

                withContext(Dispatchers.Main) {
                    installApk(context, targetFile)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error downloading update", e)
                _updateState.value = UpdateState.Error("ডাউনলোড ব্যর্থ হয়েছে: ${e.localizedMessage ?: "অজানা ত্রুটি"}")
            }
        }
    }

    fun installApk(context: Context, apkFile: File) {
        AppUpdateInstaller.installApk(context, apkFile)
    }

    fun getCurrentVersion(context: Context): String {
        return AppUpdateInstaller.getCurrentVersion(context)
    }

    fun isNewerVersion(current: String, remote: String): Boolean {
        return AppUpdateInstaller.isNewerVersion(current, remote)
    }
}
