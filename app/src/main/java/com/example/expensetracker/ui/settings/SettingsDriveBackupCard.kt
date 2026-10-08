package com.example.expensetracker.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.UserProfile
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorBorder
import com.example.expensetracker.theme.ColorSuccessIncome
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.theme.ColorTextSecondary
import com.example.expensetracker.theme.SmallElementRadius
import com.example.expensetracker.ui.components.AppCard
import com.example.expensetracker.ui.components.DateTimeHelper
import com.example.expensetracker.ui.components.LocalAppStrings

@Composable
fun SettingsDriveBackupCard(
    userProfile: UserProfile,
    isSyncing: Boolean,
    onDisconnectGoogleAccount: () -> Unit,
    onInitiateGoogleSignIn: () -> Unit,
    onBackupToGoogleDriveNow: () -> Unit,
    onRestoreFromDriveClick: () -> Unit,
    onAutoSyncChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current

    AppCard(modifier = modifier) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = strings.googleDriveBackup,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = ColorTextPrimary
                )

                if (userProfile.googleAccountEmail != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(SmallElementRadius))
                            .background(ColorSuccessIncome.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = strings.connected,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            color = ColorSuccessIncome
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = strings.googleDriveBackupDesc,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = ColorTextSecondary
            )

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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = strings.autoCloudSync,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = ColorTextPrimary
                    )
                    Text(
                        text = strings.autoCloudSyncDesc,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = ColorTextSecondary
                    )
                }
                Switch(
                    checked = userProfile.isAutoSyncEnabled,
                    onCheckedChange = onAutoSyncChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = ColorAccent,
                        uncheckedThumbColor = ColorTextSecondary,
                        uncheckedTrackColor = ColorBorder
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
                    color = ColorTextSecondary
                )
                Text(
                    text = if (userProfile.lastSyncMillis > 0)
                        DateTimeHelper.formatFullDateTime(userProfile.lastSyncMillis, strings.isBangla, userProfile.useBengaliNumerals || strings.isBangla)
                    else
                        strings.notSyncedYet,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = ColorTextPrimary
                )
            }
        }
    }
}
