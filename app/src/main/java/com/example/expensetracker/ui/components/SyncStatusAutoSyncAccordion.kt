package com.example.expensetracker.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.UserProfile
import com.example.expensetracker.theme.LocalAppColors

@Composable
internal fun SyncStatusAutoSyncAccordion(
    userProfile: UserProfile,
    onAutoSyncChange: (Boolean) -> Unit,
    onDisconnectGoogleAccount: () -> Unit,
    stateDescription: String,
    autoDailyTitle: String,
    autoDailyDesc: String,
    disconnectText: String,
    isConnected: Boolean,
    haptics: AppHaptics,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current

    Column(modifier = modifier.padding(top = 14.dp)) {
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
                    text = autoDailyTitle,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = appColors.textPrimary
                )
                Text(
                    text = autoDailyDesc,
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
                    this.stateDescription = stateDescription
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
                        text = disconnectText,
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
