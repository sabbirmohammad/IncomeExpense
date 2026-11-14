package com.example.expensetracker.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.update.UpdateState
import com.example.expensetracker.theme.LocalAppColors

@Composable
fun UpdateDialogTitleSubtitle(
    updateState: UpdateState,
    currentVersion: String,
    isBangla: Boolean,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current

    Column(modifier = modifier.fillMaxWidth()) {
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
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(6.dp))

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
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
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
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            is UpdateState.Downloading -> {
                Text(
                    text = if (isBangla) "নতুন সংস্করণের ফাইল ডাউনলোড হচ্ছে..." else "Downloading update package...",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                    color = appColors.textSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            else -> Unit
        }
    }
}
