package com.example.expensetracker.ui.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.AppColors
import com.example.expensetracker.ui.components.AppHaptics

@Composable
fun MarketEmptyListsState(
    isBangla: Boolean,
    onCreateNewClick: () -> Unit,
    appColors: AppColors,
    haptics: AppHaptics,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 36.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (isBangla) "বর্তমানে কোনো বাজার ফর্দ সক্রিয় নেই" else "No active market list",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            ),
            color = appColors.textPrimary
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = if (isBangla)
                "নতুন ফর্দ তৈরি করতে উপরের 'নতুন ফর্দ তৈরি' ট্যাবে যান অথবা নিচের বাটনে চাপ দিন"
            else
                "Tap below to create a morning, evening, or weekly market list",
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
            color = appColors.textSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                haptics.mediumClick()
                onCreateNewClick()
            },
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = appColors.accent)
        ) {
            Text(text = if (isBangla) "➕ নতুন ফর্দ তৈরি করুন" else "➕ Create New List")
        }
    }
}
