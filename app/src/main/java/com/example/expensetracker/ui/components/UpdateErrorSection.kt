package com.example.expensetracker.ui.components

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.update.AppUpdateManager
import com.example.expensetracker.data.update.UpdateState
import com.example.expensetracker.theme.LocalAppColors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun UpdateErrorSection(
    state: UpdateState.Error,
    isBangla: Boolean,
    context: Context,
    coroutineScope: CoroutineScope,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current

    val errorText = when {
        state.isRateLimited || state.message == "RATE_LIMIT" ->
            if (isBangla) "নেটওয়ার্ক রিকোয়েস্ট লিমিট শেষ হয়েছে, অনুগ্রহ করে কিছুক্ষণ পর চেষ্টা করুন।"
            else "Network request limit reached, please try again later."
        state.message == "NETWORK_ERROR" ->
            if (isBangla) "আপডেট চেক করতে সমস্যা হয়েছে। ইন্টারনেট কানেকশন চেক করুন।"
            else "Could not check for updates. Please check your internet connection."
        else -> state.message
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = errorText,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
            color = appColors.expense,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

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
                    text = if (isBangla) "বাতিল" else "Cancel",
                    color = appColors.textSecondary
                )
            }

            Button(
                onClick = {
                    coroutineScope.launch {
                        AppUpdateManager.checkForUpdates(context, isManualCheck = true)
                    }
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = appColors.accent)
            ) {
                Text(
                    text = if (isBangla) "পুনরায় চেষ্টা করুন" else "Try Again",
                    color = Color.White
                )
            }
        }
    }
}
