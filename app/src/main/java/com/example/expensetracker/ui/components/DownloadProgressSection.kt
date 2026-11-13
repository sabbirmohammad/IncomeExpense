package com.example.expensetracker.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.update.UpdateState
import com.example.expensetracker.theme.LocalAppColors
import java.util.Locale

@Composable
fun DownloadProgressSection(
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
