package com.example.expensetracker.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.CloudSync
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.UserProfile
import com.example.expensetracker.theme.HairlineInkStroke
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.LocalMotion
import com.example.expensetracker.theme.RadiusM

private fun truncateMiddle(text: String, maxLength: Int = 24): String {
    if (text.length <= maxLength) return text
    val half = (maxLength - 3) / 2
    return text.take(half) + "..." + text.takeLast(half)
}

/**
 * V7 SyncStatusCard per Component Spec & 3_UX_FLOWS section 7:
 * - Last sync time, state (OK / Syncing / Failed + reason)
 * - Backup now / Restore buttons with guaranteed >= 48dp touch targets
 * - Expandable error / diagnostic details
 * - Accessible auto-sync toggle with >= 3:1 contrast and TalkBack state description
 * - Truncates email in middle by default; tap toggles full display
 */
@Composable
fun SyncStatusCard(
    userProfile: UserProfile,
    isSyncing: Boolean,
    onDisconnectGoogleAccount: () -> Unit,
    onInitiateGoogleSignIn: () -> Unit,
    onBackupToGoogleDriveNow: () -> Unit,
    onRestoreFromDriveClick: () -> Unit,
    onAutoSyncChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    errorMessage: String? = null,
    onRetryClick: (() -> Unit)? = null
) {
    val strings = LocalAppStrings.current
    val appColors = LocalAppColors.current
    val motion = LocalMotion.current
    val haptics = rememberAppHaptics()
    val isBangla = strings.isBangla
    val isConnected = userProfile.googleAccountEmail != null

    var isExpanded by rememberSaveable { mutableStateOf(false) }
    var showFullEmail by remember { mutableStateOf(false) }

    val chevronRotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f, animationSpec = tween(if (motion.reduceMotion) 0 else motion.durationStandard), label = "sync_chevron_rotate"
    )

    Surface(
        modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(com.example.expensetracker.theme.RadiusCard), color = appColors.surfaceRaised
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            val email = userProfile.googleAccountEmail
            val emailText = if (isConnected && !email.isNullOrBlank()) {
                if (showFullEmail) email else truncateMiddle(email)
            } else strings.syncDisconnected

            SyncStatusCardHeader(
                isSyncing = isSyncing, isConnected = isConnected, titleText = strings.syncStatusTitle,
                statusText = emailText, onEmailClick = if (isConnected && !email.isNullOrBlank()) { { showFullEmail = !showFullEmail } } else null,
                isExpanded = isExpanded, chevronRotation = chevronRotation, onToggleExpand = { haptics.lightTick(); isExpanded = !isExpanded }
            )

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(appColors.surfaceVariant).padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (isSyncing) {
                        CircularProgressIndicator(modifier = Modifier.size(14.dp), color = appColors.accent, strokeWidth = 2.dp)
                        Text(text = strings.syncSyncing, style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp), color = appColors.accent)
                    } else if (isConnected && userProfile.lastSyncMillis > 0) {
                        Icon(imageVector = Icons.Outlined.CheckCircle, contentDescription = null, tint = appColors.income, modifier = Modifier.size(15.dp))
                        Text(text = strings.syncProtected, style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, fontWeight = FontWeight.SemiBold), color = appColors.income)
                    } else {
                        Text(text = if (isConnected) strings.connected else strings.syncDisconnected, style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp), color = appColors.textMuted)
                    }
                }

                Text(
                    text = if (userProfile.lastSyncMillis > 0) DateTimeHelper.formatFullDateTime(userProfile.lastSyncMillis, isBangla, userProfile.useBengaliNumerals || isBangla) else strings.notSyncedYet,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp, fontWeight = FontWeight.Medium, fontFeatureSettings = "tnum"),
                    color = appColors.textSecondary
                )
            }

            if (!errorMessage.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                SyncStatusErrorBanner(errorMessage = errorMessage, titleText = strings.syncErrorDetected, retryText = strings.syncRetry, onRetryClick = onRetryClick)
            }

            Spacer(modifier = Modifier.height(14.dp))
            if (!isConnected) {
                Button(
                    onClick = { haptics.mediumClick(); onInitiateGoogleSignIn() }, modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = appColors.accent, contentColor = Color.White)
                ) {
                    Icon(imageVector = Icons.Outlined.CloudSync, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = strings.connectGoogleDrive, style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp, fontWeight = FontWeight.SemiBold))
                }
            } else {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = { haptics.mediumClick(); onBackupToGoogleDriveNow() },
                        enabled = !isSyncing, modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = appColors.accent, contentColor = Color.White)
                    ) {
                        if (isSyncing) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = strings.syncSyncing, fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold)
                        } else {
                            Icon(imageVector = Icons.Outlined.CloudUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = strings.backupNow, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    OutlinedButton(
                        onClick = { haptics.lightTick(); onRestoreFromDriveClick() },
                        enabled = !isSyncing, modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp), border = BorderStroke(HairlineInkStroke, appColors.border),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = appColors.surfaceVariant, contentColor = appColors.textPrimary)
                    ) {
                        Icon(imageVector = Icons.Outlined.Refresh, contentDescription = null, modifier = Modifier.size(18.dp), tint = appColors.textPrimary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = strings.restoreFromDrive, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(tween(motion.durationStandard)) + fadeIn(tween(motion.durationStandard)),
                exit = shrinkVertically(tween(motion.durationFast)) + fadeOut(tween(motion.durationFast))
            ) {
                SyncStatusAutoSyncAccordion(
                    userProfile = userProfile, onAutoSyncChange = onAutoSyncChange, onDisconnectGoogleAccount = onDisconnectGoogleAccount,
                    stateDescription = if (userProfile.isAutoSyncEnabled) (if (isBangla) "চালু" else "On") else (if (isBangla) "বন্ধ" else "Off"),
                    autoDailyTitle = strings.syncAutoDailyTitle, autoDailyDesc = strings.syncAutoDailyDesc, disconnectText = strings.disconnect,
                    isConnected = isConnected, haptics = haptics
                )
            }
        }
    }
}
