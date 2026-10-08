package com.example.expensetracker.ui.reports

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
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
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorTextMuted
import com.example.expensetracker.theme.ColorTextPrimary
import com.example.expensetracker.ui.components.DrawerMenuButton
import com.example.expensetracker.ui.components.Strings
import com.example.expensetracker.ui.components.rememberAppHaptics

@Composable
fun ReportsTopBar(
    periodTitle: String,
    isPrivacyMode: Boolean,
    strings: Strings,
    onTogglePrivacy: () -> Unit,
    onNavigateBack: (() -> Unit)?,
    onMenuClick: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    val haptics = rememberAppHaptics()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (onMenuClick != null) {
                DrawerMenuButton(onClick = onMenuClick)
            } else if (onNavigateBack != null) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Back",
                        tint = ColorTextPrimary
                    )
                }
            }

            Column {
                Text(
                    text = strings.reportsTitle,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = ColorTextPrimary
                )
                Text(
                    text = periodTitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = ColorAccent
                )
            }
        }

        IconButton(
            onClick = {
                haptics.lightTick()
                onTogglePrivacy()
            }
        ) {
            Icon(
                imageVector = if (isPrivacyMode) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                contentDescription = if (isPrivacyMode) strings.showBalance else strings.hideBalance,
                tint = if (isPrivacyMode) ColorAccent else ColorTextMuted,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}
