package com.example.expensetracker.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.UserProfile
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.DateTimeHelper
import com.example.expensetracker.ui.components.HairlineDivider
import com.example.expensetracker.ui.components.LocalAppStrings
import com.example.expensetracker.ui.components.rememberAppHaptics

@Composable
fun SettingsDriveExpandedContent(
    userProfile: UserProfile,
    isSyncing: Boolean,
    onDisconnectGoogleAccount: () -> Unit,
    onInitiateGoogleSignIn: () -> Unit,
    onBackupToGoogleDriveNow: () -> Unit,
    onRestoreFromDriveClick: () -> Unit,
    onAutoSyncChange: (Boolean) -> Unit,
    onCollapse: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val appColors = LocalAppColors.current
    val haptics = rememberAppHaptics()
    val isBangla = strings.isBangla

    Column(modifier = modifier.padding(top = 14.dp)) {
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
                    onCollapse()
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
