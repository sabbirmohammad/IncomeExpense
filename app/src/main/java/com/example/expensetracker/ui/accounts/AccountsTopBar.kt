package com.example.expensetracker.ui.accounts

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.ScreenPaddingHorizontal
import com.example.expensetracker.ui.components.DrawerMenuButton

@Composable
fun AccountsTopBar(
    title: String,
    onMenuClick: (() -> Unit)?,
    onNavigateBack: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = ScreenPaddingHorizontal, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onMenuClick != null) {
            DrawerMenuButton(onClick = onMenuClick)
            Spacer(modifier = Modifier.width(12.dp))
        } else if (onNavigateBack != null) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Back",
                    tint = appColors.textPrimary
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
        }

        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp, fontWeight = FontWeight.SemiBold),
            color = appColors.textPrimary
        )
    }
}
