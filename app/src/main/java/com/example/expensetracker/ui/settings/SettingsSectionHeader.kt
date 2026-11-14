package com.example.expensetracker.ui.settings

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.LocalAppColors

@Composable
fun SettingsSectionHeader(title: String, modifier: Modifier = Modifier) {
    val appColors = LocalAppColors.current
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge.copy(
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        ),
        color = appColors.accent,
        modifier = modifier.padding(top = 10.dp, bottom = 4.dp, start = 4.dp)
    )
}
