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
import com.example.expensetracker.common.AppConstants
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorBorder
import com.example.expensetracker.theme.ColorTextMuted
import com.example.expensetracker.theme.ColorTextSecondary
import com.example.expensetracker.ui.components.Strings
import com.example.expensetracker.ui.components.convertToBengaliDigits

@Composable
fun DrawerFooter(
    strings: Strings,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        HorizontalDivider(thickness = 1.dp, color = ColorBorder)
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
                    color = ColorTextSecondary
                )
                Text(
                    text = if (strings.isBangla) "ভার্সন ${convertToBengaliDigits(AppConstants.APP_VERSION.removePrefix("v"))}" else AppConstants.APP_VERSION,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal,
                    color = ColorTextMuted
                )
            }
            Text(
                text = if (strings.isBangla) "নির্মাতা: AWFI ENTERPRISE" else "Developed by AWFI ENTERPRISE",
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = ColorAccent.copy(alpha = 0.85f)
            )
        }
    }
}
