package com.example.expensetracker.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.CloudDownload
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.NewReleases
import androidx.compose.material.icons.outlined.SystemUpdate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
    val strings = LocalAppStrings.current
    val isBangla = strings.isBangla

    if (!isVisible) return

    val currentVersion = remember(context) {
        AppUpdateManager.getCurrentVersion(context)
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
                .padding(horizontal = 12.dp),
            shape = RoundedCornerShape(26.dp),
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
                                contentDescription = if (isBangla) "বন্ধ করুন" else "Close",
                                tint = appColors.textMuted
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.height(28.dp))
                    }
                }

                // Animated Header Icon
                AnimatedUpdateHeaderIcon(updateState = updateState)

                Spacer(modifier = Modifier.height(16.dp))

                // Title
                Text(
                    text = when (updateState) {
                        is UpdateState.UpToDate -> if (isBangla) "অ্যাপ্লিকেশনটি আপ-টু-ডেট আছে" else "You're All Up to Date"
                        is UpdateState.Downloading -> if (isBangla) "আপডেট ডাউনলোড হচ্ছে..." else "Downloading Update..."
                        is UpdateState.ReadyToInstall -> if (isBangla) "ডাউনলোড সম্পন্ন হয়েছে!" else "Download Complete!"
                        is UpdateState.Error -> if (isBangla) "আপডেট ব্যর্থ হয়েছে" else "Update Check Failed"
                        else -> if (isBangla) "নতুন আপডেট পাওয়া গেছে!" else "New Update Available!"
                    },
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.sp
                    ),
                    color = appColors.textPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Version / Subtitle
                when (val state = updateState) {
                    is UpdateState.UpToDate -> {
                        Text(
                            text = if (isBangla) {
                                "আপনি সর্বাধুনিক সংস্করণ (v${convertToBengaliDigits(state.currentVersion)}) ব্যবহার করছেন।"
                            } else {
                                "You are running the latest version (v${state.currentVersion})."
                            },
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                            color = appColors.textSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                    is UpdateState.Available -> {
                        Text(
                            text = if (isBangla) {
                                "সংস্করণ ${state.updateInfo.latestVersion} (বর্তমান: v${convertToBengaliDigits(currentVersion)})"
                            } else {
                                "Version ${state.updateInfo.latestVersion} (Current: v$currentVersion)"
                            },
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                            color = appColors.textSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                    is UpdateState.Downloading -> {
                        Text(
                            text = if (isBangla) "নতুন সংস্করণের ফাইল ডাউনলোড হচ্ছে..." else "Downloading update package...",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                            color = appColors.textSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                    else -> Unit
                }

                Spacer(modifier = Modifier.height(16.dp))

                // State-specific Content & Actions
                when (val state = updateState) {
                    is UpdateState.UpToDate -> {
                        // Modern Up-to-date Status Box
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = appColors.surfaceVariant.copy(alpha = 0.5f)
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.NewReleases,
                                            contentDescription = null,
                                            tint = appColors.accent,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = if (isBangla) "ইনস্টল করা ভার্সন" else "Installed Version",
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                            color = appColors.textSecondary
                                        )
                                    }
                                    Text(
                                        text = if (isBangla) "v${convertToBengaliDigits(state.currentVersion)}" else "v${state.currentVersion}",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = appColors.accent
                                    )
                                }

                                HorizontalDivider(
                                    modifier = Modifier.fillMaxWidth(),
                                    thickness = 0.5.dp,
                                    color = appColors.border.copy(alpha = 0.4f)
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.CheckCircle,
                                            contentDescription = null,
                                            tint = appColors.income,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = if (isBangla) "স্ট্যাটাস" else "Status",
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                            color = appColors.textSecondary
                                        )
                                    }
                                    Text(
                                        text = if (isBangla) "সর্বশেষ রিলিজ" else "Latest Release",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold
                                        ),
                                        color = appColors.income
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = { AppUpdateManager.dismissDialog() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = appColors.accent)
                        ) {
                            Text(
                                text = if (isBangla) "ঠিক আছে" else "Got it",
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

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
                                        text = if (isBangla) "নতুন কী আছে:" else "What's New:",
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
                                text = if (isBangla) {
                                    String.format(Locale.US, "ফাইলের আকার: ~%.1f MB", mbSize)
                                } else {
                                    String.format(Locale.US, "File size: ~%.1f MB", mbSize)
                                },
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
                                Text(
                                    text = if (isBangla) "পরে" else "Later",
                                    color = appColors.textSecondary
                                )
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
                                Text(
                                    text = if (isBangla) "আপডেট করুন" else "Update Now",
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    is UpdateState.Downloading -> {
                        DownloadProgressSection(
                            state = state,
                            isBangla = isBangla
                        )
                    }

                    is UpdateState.ReadyToInstall -> {
                        Text(
                            text = if (isBangla) {
                                "অ্যাপটি প্রস্তুত! ইনস্টলেশন শুরু করতে নিচের বাটনে চাপুন।"
                            } else {
                                "The update is ready! Tap below to start installation."
                            },
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = appColors.textSecondary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { AppUpdateManager.installApk(context, state.apkFile) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = appColors.income)
                        ) {
                            Text(
                                text = if (isBangla) "এখনই ইনস্টল করুন" else "Install Now",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    is UpdateState.Error -> {
                        val errorText = when {
                            state.isRateLimited || state.message == "RATE_LIMIT" ->
                                if (isBangla) "নেটওয়ার্ক রিকোয়েস্ট লিমিট শেষ হয়েছে, অনুগ্রহ করে কিছুক্ষণ পর চেষ্টা করুন।"
                                else "Network request limit reached, please try again later."
                            state.message == "NETWORK_ERROR" ->
                                if (isBangla) "আপডেট চেক করতে সমস্যা হয়েছে। ইন্টারনেট কানেকশন চেক করুন।"
                                else "Could not check for updates. Please check your internet connection."
                            else -> state.message
                        }

                        Text(
                            text = errorText,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = appColors.expense,
                            textAlign = TextAlign.Center
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
                                Text(
                                    text = if (isBangla) "বাতিল" else "Cancel",
                                    color = appColors.textSecondary
                                )
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
                                Text(
                                    text = if (isBangla) "পুনরায় চেষ্টা করুন" else "Try Again",
                                    color = Color.White
                                )
                            }
                        }
                    }

                    else -> Unit
                }
            }
        }
    }
}

@Composable
private fun DownloadProgressSection(
    state: UpdateState.Downloading,
    isBangla: Boolean
) {
    val appColors = LocalAppColors.current

    val animatedProgress by animateFloatAsState(
        targetValue = state.progress.coerceIn(0f, 1f),
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "downloadProgressAnim"
    )

    val percent = (state.progress * 100).toInt().coerceIn(0, 100)
    val percentDisplay = if (isBangla) "${convertToBengaliDigits(percent.toString())}%" else "$percent%"

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = appColors.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isBangla) "ডাউনলোড অগ্রগতি" else "Download Progress",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        color = appColors.textMuted
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = percentDisplay,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 24.sp
                        ),
                        color = appColors.accent
                    )
                }

                val mbDownloaded = state.downloadedBytes / (1024f * 1024f)
                val mbTotal = state.totalBytes / (1024f * 1024f)
                val sizeText = if (state.totalBytes > 0) {
                    val raw = String.format(Locale.US, "%.1f / %.1f MB", mbDownloaded, mbTotal)
                    if (isBangla) convertToBengaliDigits(raw) else raw
                } else {
                    val raw = String.format(Locale.US, "%.1f MB", mbDownloaded)
                    if (isBangla) "${convertToBengaliDigits(raw)} ডাউনলোড হয়েছে" else "$raw downloaded"
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = appColors.accent.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = sizeText,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = appColors.accent
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (state.totalBytes > 0) {
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = appColors.accent,
                    trackColor = appColors.accent.copy(alpha = 0.2f)
                )
            } else {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = appColors.accent,
                    trackColor = appColors.accent.copy(alpha = 0.2f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = if (isBangla) {
                    "নতুন সংস্করণ ডাউনলোড শেষে স্বয়ংক্রিয়ভাবে ইনস্টলেশন শুরু হবে..."
                } else {
                    "Installation will prompt automatically when download finishes..."
                },
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = appColors.textMuted,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun AnimatedUpdateHeaderIcon(
    updateState: UpdateState
) {
    val appColors = LocalAppColors.current

    // Key entrance animation strictly by state category to prevent continuous cancel/restart during downloads
    val stateKey = updateState::class
    val scaleAnim = remember { Animatable(0.75f) }
    LaunchedEffect(stateKey) {
        scaleAnim.snapTo(0.75f)
        scaleAnim.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulseGlow")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.12f,
        targetValue = 0.32f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    // Dedicated smooth download bounce animation
    val downloadTransition = rememberInfiniteTransition(label = "downloadTransition")
    val downloadBobY by downloadTransition.animateFloat(
        initialValue = -3f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(650, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "downloadBobY"
    )

    val iconColor = when (updateState) {
        is UpdateState.UpToDate, is UpdateState.ReadyToInstall -> appColors.income
        is UpdateState.Error -> appColors.expense
        else -> appColors.accent
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(76.dp)
    ) {
        // Pulsing background ring for UpToDate, ReadyToInstall AND Downloading
        if (updateState is UpdateState.UpToDate || updateState is UpdateState.ReadyToInstall || updateState is UpdateState.Downloading) {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .scale(pulseScale)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = pulseAlpha))
            )
        }

        // Inner circular badge
        Box(
            modifier = Modifier
                .size(64.dp)
                .scale(scaleAnim.value)
                .clip(CircleShape)
                .background(iconColor.copy(alpha = 0.14f)),
            contentAlignment = Alignment.Center
        ) {
            when (updateState) {
                is UpdateState.UpToDate -> Icon(
                    imageVector = Icons.Outlined.CheckCircle,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(34.dp)
                )
                is UpdateState.ReadyToInstall -> Icon(
                    imageVector = Icons.Outlined.CheckCircle,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(34.dp)
                )
                is UpdateState.Error -> Icon(
                    imageVector = Icons.Outlined.ErrorOutline,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(34.dp)
                )
                is UpdateState.Downloading -> Icon(
                    imageVector = Icons.Outlined.CloudDownload,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier
                        .size(34.dp)
                        .offset(y = downloadBobY.dp)
                )
                else -> Icon(
                    imageVector = Icons.Outlined.SystemUpdate,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(34.dp)
                )
            }
        }
    }
}
