package com.example.expensetracker.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.NewReleases
import androidx.compose.material.icons.outlined.SystemUpdate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.expensetracker.common.AppConstants
import com.example.expensetracker.data.update.AppUpdateManager
import com.example.expensetracker.data.update.UpdateState
import com.example.expensetracker.theme.LocalAppColors
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun AppUpdateDialog(
    modifier: Modifier = Modifier
) {
    val isVisible by AppUpdateManager.isDialogVisible.collectAsState()
    val updateState by AppUpdateManager.updateState.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val appColors = LocalAppColors.current

    if (!isVisible) return

    val currentVersion = remember {
        try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            pInfo.versionName ?: AppConstants.APP_VERSION
        } catch (_: Exception) {
            AppConstants.APP_VERSION
        }
    }

    Dialog(
        onDismissRequest = {
            if (updateState !is UpdateState.Downloading) {
                AppUpdateManager.dismissDialog()
            }
        },
        properties = DialogProperties(
            dismissOnBackPress = updateState !is UpdateState.Downloading,
            dismissOnClickOutside = updateState !is UpdateState.Downloading
        )
    ) {
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            shape = RoundedCornerShape(24.dp),
            color = appColors.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top close button (if not actively downloading)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    if (updateState !is UpdateState.Downloading) {
                        IconButton(
                            onClick = { AppUpdateManager.dismissDialog() },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = appColors.textMuted
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.height(28.dp))
                    }
                }

                // Header Icon
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(appColors.accent.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    when (updateState) {
                        is UpdateState.ReadyToInstall -> Icon(
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = null,
                            tint = appColors.income,
                            modifier = Modifier.size(32.dp)
                        )
                        is UpdateState.Error -> Icon(
                            imageVector = Icons.Outlined.ErrorOutline,
                            contentDescription = null,
                            tint = appColors.expense,
                            modifier = Modifier.size(32.dp)
                        )
                        else -> Icon(
                            imageVector = Icons.Outlined.SystemUpdate,
                            contentDescription = null,
                            tint = appColors.accent,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Title
                Text(
                    text = when (updateState) {
                        is UpdateState.Downloading -> "আপডেট ডাউনলোড হচ্ছে..."
                        is UpdateState.ReadyToInstall -> "ডাউনলোড সম্পন্ন হয়েছে!"
                        is UpdateState.Error -> "আপডেট ব্যর্থ হয়েছে"
                        else -> "নতুন আপডেট পাওয়া গেছে!"
                    },
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    ),
                    color = appColors.textPrimary
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Version comparison subtitle
                when (val state = updateState) {
                    is UpdateState.Available -> {
                        Text(
                            text = "সংস্করণ ${state.updateInfo.latestVersion} (বর্তমান: $currentVersion)",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                            color = appColors.textSecondary
                        )
                    }
                    is UpdateState.Downloading -> {
                        val mbDownloaded = state.downloadedBytes / (1024f * 1024f)
                        val mbTotal = state.totalBytes / (1024f * 1024f)
                        Text(
                            text = String.format(Locale.US, "%.1f MB / %.1f MB (%.0f%%)", mbDownloaded, mbTotal, state.progress * 100),
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                            color = appColors.textSecondary
                        )
                    }
                    else -> Unit
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Content area
                when (val state = updateState) {
                    is UpdateState.Available -> {
                        if (state.updateInfo.releaseNotes.isNotBlank()) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 140.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = appColors.background.copy(alpha = 0.5f))
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(12.dp)
                                        .verticalScroll(rememberScrollState())
                                ) {
                                    Text(
                                        text = "নতুন কী আছে:",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                        color = appColors.textPrimary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = state.updateInfo.releaseNotes,
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, lineHeight = 16.sp),
                                        color = appColors.textSecondary
                                    )
                                }
                            }
                        }

                        if (state.updateInfo.apkSize > 0) {
                            Spacer(modifier = Modifier.height(8.dp))
                            val mbSize = state.updateInfo.apkSize / (1024f * 1024f)
                            Text(
                                text = String.format(Locale.US, "ফাইলের আকার: ~%.1f MB", mbSize),
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                color = appColors.textMuted
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Action buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = { AppUpdateManager.dismissDialog() },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("পরে", color = appColors.textSecondary)
                            }

                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        AppUpdateManager.startDownload(
                                            context = context,
                                            downloadUrl = state.updateInfo.downloadUrl,
                                            expectedSize = state.updateInfo.apkSize
                                        )
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = appColors.accent)
                            ) {
                                Text("আপডেট করুন", color = androidx.compose.ui.graphics.Color.White, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    is UpdateState.Downloading -> {
                        LinearProgressIndicator(
                            progress = { state.progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = appColors.accent,
                            trackColor = appColors.accent.copy(alpha = 0.2f)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "অনুগ্রহ করে অপেক্ষা করুন...",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = appColors.textMuted
                        )
                    }

                    is UpdateState.ReadyToInstall -> {
                        Text(
                            text = "অ্যাপটি প্রস্তুত! ইনস্টলেশন শুরু করতে নিচের বাটনে চাপুন।",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = appColors.textSecondary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { AppUpdateManager.installApk(context, state.apkFile) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = appColors.income)
                        ) {
                            Text("এখনই ইনস্টল করুন", color = androidx.compose.ui.graphics.Color.White, fontWeight = FontWeight.Bold)
                        }
                    }

                    is UpdateState.Error -> {
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = appColors.expense
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = { AppUpdateManager.dismissDialog() },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("বাতিল", color = appColors.textSecondary)
                            }

                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        AppUpdateManager.checkForUpdates(context, isManualCheck = true)
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = appColors.accent)
                            ) {
                                Text("পুনরায় চেষ্টা করুন", color = androidx.compose.ui.graphics.Color.White)
                            }
                        }
                    }

                    else -> Unit
                }
            }
        }
    }
}
