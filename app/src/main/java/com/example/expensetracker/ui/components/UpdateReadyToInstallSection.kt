package com.example.expensetracker.ui.components

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.update.AppUpdateManager
import com.example.expensetracker.data.update.UpdateState
import com.example.expensetracker.theme.LocalAppColors

@Composable
fun UpdateReadyToInstallSection(
    state: UpdateState.ReadyToInstall,
    isBangla: Boolean,
    context: Context,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = if (isBangla) {
                "অ্যাপটি প্রস্তুত! ইনস্টলেশন শুরু করতে নিচের বাটনে চাপুন।"
            } else {
                "The update is ready! Tap below to start installation."
            },
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
            color = appColors.textSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
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
}
