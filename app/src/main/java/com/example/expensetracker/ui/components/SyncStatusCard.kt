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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.CloudDone
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.CloudSync
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = tween(if (motion.reduceMotion) 0 else motion.durationStandard),
        label = "sync_chevron_rotate"
    )

    val borderStroke = if (!appColors.isDark) BorderStroke(HairlineInkStroke, appColors.border) else null

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(RadiusM),
        color = appColors.surfaceRaised,
        border = borderStroke
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Account Status & Expand Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        haptics.lightTick()
                        isExpanded = !isExpanded
                    }
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(
                                if (isConnected) appColors.accentContainer else appColors.surfaceVariant
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when {
                                isSyncing -> Icons.Outlined.CloudSync
                                isConnected -> Icons.Outlined.CloudDone
                                else -> Icons.Outlined.CloudOff
                            },
                            contentDescription = null,
                            tint = if (isConnected) appColors.accent else appColors.textMuted,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = strings.syncStatusTitle,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = appColors.textPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        if (isConnected && !userProfile.googleAccountEmail.isNullOrBlank()) {
                            val email = userProfile.googleAccountEmail
                            Text(
                                text = if (showFullEmail) email else truncateMiddle(email),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                color = appColors.accent,
                                modifier = Modifier.clickable { showFullEmail = !showFullEmail }
                            )
                        } else {
                            Text(
                                text = strings.syncDisconnected,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                color = appColors.textMuted
                            )
                        }
                    }
                }

                // Expand / Collapse Chevron (>= 48dp)
                Surface(
                    onClick = {
                        haptics.lightTick()
                        isExpanded = !isExpanded
                    },
                    shape = CircleShape,
                    color = appColors.surfaceVariant,
                    modifier = Modifier
                        .size(48.dp)
                        .minimumInteractiveComponentSize()
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Outlined.ExpandMore,
                            contentDescription = if (isExpanded) "Collapse" else "Expand",
                            tint = appColors.textSecondary,
                            modifier = Modifier
                                .size(22.dp)
                                .rotate(chevronRotation)
                        )
                    }
                }
            }

            // Sync State Status Badge Row
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(appColors.surfaceVariant)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Status Description
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (isSyncing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(14.dp),
                            color = appColors.accent,
                            strokeWidth = 2.dp
                        )
                        Text(
                            text = strings.syncSyncing,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = appColors.accent
                        )
                    } else if (isConnected && userProfile.lastSyncMillis > 0) {
                        Icon(
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = null,
                            tint = appColors.income,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = strings.syncProtected,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = appColors.income
                        )
                    } else {
                        Text(
                            text = if (isConnected) strings.connected else strings.syncDisconnected,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = appColors.textMuted
                        )
                    }
                }

                // Last Sync Timestamp
                Text(
                    text = if (userProfile.lastSyncMillis > 0)
                        DateTimeHelper.formatFullDateTime(userProfile.lastSyncMillis, isBangla, userProfile.useBengaliNumerals || isBangla)
                    else
                        strings.notSyncedYet,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium,
                        fontFeatureSettings = "tnum"
                    ),
                    color = appColors.textSecondary
                )
            }

            // Expandable Error Detail Box (if error exists)
            if (!errorMessage.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = appColors.expenseContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ErrorOutline,
                                contentDescription = null,
                                tint = appColors.expense,
                                modifier = Modifier.size(18.dp)
                            )
                            Column {
                                Text(
                                    text = strings.syncErrorDetected,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = appColors.expense
                                )
                                Text(
                                    text = errorMessage,
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                                    color = appColors.textPrimary,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        if (onRetryClick != null) {
                            TextButton(
                                onClick = onRetryClick,
                                modifier = Modifier.height(48.dp)
                            ) {
                                Text(
                                    text = strings.syncRetry,
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = appColors.expense
                                )
                            }
                        }
                    }
                }
            }

            // Primary Actions Row (Guaranteed >= 48dp)
            Spacer(modifier = Modifier.height(14.dp))
            if (!isConnected) {
                Button(
                    onClick = {
                        haptics.mediumClick()
                        onInitiateGoogleSignIn()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = appColors.accent,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CloudSync,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = strings.connectGoogleDrive,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Backup Now Button (48dp height)
                    Button(
                        onClick = {
                            haptics.mediumClick()
                            onBackupToGoogleDriveNow()
                        },
                        enabled = !isSyncing,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = appColors.accent,
                            contentColor = Color.White
                        )
                    ) {
                        if (isSyncing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = strings.syncSyncing,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Outlined.CloudUpload,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = strings.backupNow,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Restore Button (48dp height)
                    OutlinedButton(
                        onClick = {
                            haptics.lightTick()
                            onRestoreFromDriveClick()
                        },
                        enabled = !isSyncing,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(HairlineInkStroke, appColors.border),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = appColors.surfaceVariant,
                            contentColor = appColors.textPrimary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = appColors.textPrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = strings.restoreFromDrive,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Expandable Accordion: Auto-sync switch + Disconnect account
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(tween(motion.durationStandard)) + fadeIn(tween(motion.durationStandard)),
                exit = shrinkVertically(tween(motion.durationFast)) + fadeOut(tween(motion.durationFast))
            ) {
                Column(modifier = Modifier.padding(top = 14.dp)) {
                    HairlineDivider()
                    Spacer(modifier = Modifier.height(12.dp))

                    // Auto-sync Daily Switch
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = strings.syncAutoDailyTitle,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                color = appColors.textPrimary
                            )
                            Text(
                                text = strings.syncAutoDailyDesc,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                                color = appColors.textMuted
                            )
                        }

                        Switch(
                            checked = userProfile.isAutoSyncEnabled,
                            onCheckedChange = {
                                haptics.lightTick()
                                onAutoSyncChange(it)
                            },
                            modifier = Modifier.semantics {
                                stateDescription = if (userProfile.isAutoSyncEnabled) (if (isBangla) "চালু" else "On") else (if (isBangla) "বন্ধ" else "Off")
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = appColors.accent,
                                uncheckedThumbColor = appColors.textPrimary,
                                uncheckedTrackColor = appColors.surfaceVariant,
                                uncheckedBorderColor = appColors.border
                            )
                        )
                    }

                    if (isConnected) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = {
                                    haptics.lightTick()
                                    onDisconnectGoogleAccount()
                                },
                                modifier = Modifier
                                    .height(48.dp)
                                    .minimumInteractiveComponentSize()
                            ) {
                                Text(
                                    text = strings.disconnect,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = appColors.expense
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
