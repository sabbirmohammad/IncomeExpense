package com.example.expensetracker.ui.components

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.update.AppUpdateManager
import com.example.expensetracker.data.update.UpdateState
import com.example.expensetracker.theme.LocalAppColors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun UpdateAvailableSection(
    state: UpdateState.Available,
    isBangla: Boolean,
    context: Context,
    coroutineScope: CoroutineScope,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current

    Column(modifier = modifier.fillMaxWidth()) {
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
}
