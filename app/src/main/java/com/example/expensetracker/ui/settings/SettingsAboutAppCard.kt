package com.example.expensetracker.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.common.AppConstants
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorTextMuted
import com.example.expensetracker.theme.ColorTextSecondary

@Composable
fun SettingsAboutAppCard(
    transactionsCount: Int,
    categoriesCount: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "$transactionsCount transactions · $categoriesCount categories active",
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
            color = ColorTextSecondary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "${AppConstants.APP_NAME} ${AppConstants.APP_VERSION}",
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
            color = ColorTextMuted
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = "Develop by AWFI ENTERPRISE",
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            ),
            color = ColorAccent.copy(alpha = 0.9f)
        )
    }
}
