package com.example.expensetracker.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudDownload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.common.AppConstants
import com.example.expensetracker.data.update.AppUpdateManager
import com.example.expensetracker.data.update.UpdateState
import com.example.expensetracker.theme.LocalAppColors
import kotlinx.coroutines.launch

@Composable
fun SettingsAboutAppCard(
    transactionsCount: Int,
    categoriesCount: Int,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val updateState by AppUpdateManager.updateState.collectAsState()
    val isChecking = updateState is UpdateState.Checking

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "$transactionsCount transactions · $categoriesCount categories active",
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
            color = appColors.textSecondary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "${AppConstants.APP_NAME} ${AppConstants.APP_VERSION}",
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
            color = appColors.textMuted
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = {
                if (!isChecking) {
                    coroutineScope.launch {
                        AppUpdateManager.checkForUpdates(context, isManualCheck = true)
                    }
                }
            },
            shape = RoundedCornerShape(20.dp),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
            modifier = Modifier.height(34.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = appColors.accent
            )
        ) {
            if (isChecking) {
                CircularProgressIndicator(
                    modifier = Modifier.size(14.dp),
                    strokeWidth = 2.dp,
                    color = appColors.accent
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "চেক করা হচ্ছে...",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp)
                )
            } else {
                Icon(
                    imageVector = Icons.Outlined.CloudDownload,
                    contentDescription = null,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "আপডেট চেক করুন",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Develop by AWFI ENTERPRISE",
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            ),
            color = appColors.accent.copy(alpha = 0.9f)
        )
    }
}
