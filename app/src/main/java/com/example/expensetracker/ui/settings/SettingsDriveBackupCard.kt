package com.example.expensetracker.ui.settings

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
import androidx.compose.material.icons.outlined.CloudDone
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.UserProfile
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.AppCard
import com.example.expensetracker.ui.components.DateTimeHelper
import com.example.expensetracker.ui.components.HairlineDivider
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.rememberAppHaptics

@Composable
fun SettingsDriveBackupCard(
    userProfile: UserProfile,
    isSyncing: Boolean,
    onDisconnectGoogleAccount: () -> Unit,
    onInitiateGoogleSignIn: () -> Unit,
    onBackupToGoogleDriveNow: () -> Unit,
    onRestoreFromDriveClick: () -> Unit,
    onAutoSyncChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    initiallyExpanded: Boolean = false
) {
    val strings = LocalAppStrings.current
    val appColors = LocalAppColors.current
    val haptics = rememberAppHaptics()
    val isBangla = strings.isBangla
    val isConnected = userProfile.googleAccountEmail != null

    var isExpanded by rememberSaveable { mutableStateOf(initiallyExpanded) }

    val chevronRotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = tween(durationMillis = 250),
        label = "drive_chevron_rotate"
    )

    AppCard(
        modifier = modifier,
        borderColor = if (isExpanded) appColors.accent.copy(alpha = 0.35f) else appColors.border
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Interactive Header
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
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isConnected) appColors.income.copy(alpha = 0.12f)
                                else appColors.accent.copy(alpha = 0.12f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isConnected) Icons.Outlined.CloudDone else Icons.Outlined.CloudUpload,
                            contentDescription = null,
                            tint = if (isConnected) appColors.income else appColors.accent,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = strings.googleDriveBackup,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = appColors.textPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f, fill = false)
                            )

                            if (isConnected) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = appColors.income.copy(alpha = 0.12f)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .clip(CircleShape)
                                                .background(appColors.income)
                                        )
                                        Text(
                                            text = strings.connected,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = appColors.income,
                                            maxLines = 1,
                                            softWrap = false
                                        )
                                    }
                                }
                            }
                        }

                        if (isConnected && !userProfile.googleAccountEmail.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = userProfile.googleAccountEmail,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                                color = appColors.textSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        } else if (!isConnected) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isBangla) "যুক্ত নেই" else "Disconnected",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                                color = appColors.textMuted,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                // Rotating Chevron Button
                Surface(
                    onClick = {
                        haptics.lightTick()
                        isExpanded = !isExpanded
                    },
                    shape = CircleShape,
                    color = appColors.surface.copy(alpha = 0.7f),
                    border = BorderStroke(0.5.dp, appColors.border)
                ) {
                    Box(
                        modifier = Modifier.size(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ExpandMore,
                            contentDescription = if (isExpanded) "Collapse" else "Expand",
                            tint = if (isExpanded) appColors.accent else appColors.textSecondary,
                            modifier = Modifier
                                .size(20.dp)
                                .rotate(chevronRotation)
                        )
                    }
                }
            }

            // Expanded Accordion Content
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(animationSpec = tween(280)) + fadeIn(animationSpec = tween(280)),
                exit = shrinkVertically(animationSpec = tween(240)) + fadeOut(animationSpec = tween(200))
            ) {
                Column(modifier = Modifier.padding(top = 14.dp)) {
                    HairlineDivider(color = appColors.border.copy(alpha = 0.6f))
                    Spacer(modifier = Modifier.height(14.dp))

                    SettingsDriveAccountRow(
                        email = userProfile.googleAccountEmail,
                        onConnect = onInitiateGoogleSignIn,
                        onDisconnect = onDisconnectGoogleAccount
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    SettingsDriveActionsRow(
                        isSyncing = isSyncing,
                        onBackupNow = onBackupToGoogleDriveNow,
                        onRestoreFromDrive = onRestoreFromDriveClick
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Auto Daily Sync toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = strings.autoCloudSync,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            color = appColors.textPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        Switch(
                            checked = userProfile.isAutoSyncEnabled,
                            onCheckedChange = {
                                haptics.lightTick()
                                onAutoSyncChange(it)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = appColors.accent,
                                uncheckedThumbColor = appColors.textSecondary,
                                uncheckedTrackColor = appColors.border
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Last Sync Status row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = strings.lastSyncStatus,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = appColors.textSecondary
                        )
                        Text(
                            text = if (userProfile.lastSyncMillis > 0)
                                DateTimeHelper.formatFullDateTime(userProfile.lastSyncMillis, isBangla, userProfile.useBengaliNumerals || isBangla)
                            else
                                strings.notSyncedYet,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            color = appColors.textPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Collapse quick button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = {
                                haptics.lightTick()
                                isExpanded = false
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ExpandLess,
                                contentDescription = null,
                                tint = appColors.textSecondary,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = if (isBangla) "সংকুচিত করুন" else "Collapse",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                color = appColors.textSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}
