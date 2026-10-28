package com.example.expensetracker.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.remember
import com.example.expensetracker.common.AppConstants
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.Strings
import com.example.expensetracker.ui.components.convertToBengaliDigits

@Composable
fun DrawerFooter(
    strings: Strings,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val context = LocalContext.current
    val appVersionClean = remember(context) { AppConstants.getAppVersionClean(context) }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        HorizontalDivider(thickness = 1.dp, color = appColors.border)
        Spacer(modifier = Modifier.height(12.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = strings.appName,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = appColors.textSecondary
                )
                Text(
                    text = if (strings.isBangla) "ভার্সন ${convertToBengaliDigits(appVersionClean)}" else "v$appVersionClean",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal,
                    color = appColors.textMuted
                )
            }
            Text(
                text = if (strings.isBangla) "নির্মাতা: AWFI ENTERPRISE" else "Developed by AWFI ENTERPRISE",
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = appColors.accent.copy(alpha = 0.85f)
            )
        }
    }
}
