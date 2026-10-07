package com.example.expensetracker.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorDangerExpense
import com.example.expensetracker.theme.ColorTextMuted
import com.example.expensetracker.theme.ColorTextSecondary
import com.example.expensetracker.theme.LocalAppColors

@Composable
fun BalanceCardHeaderRow(
    currentHeaderLabel: String,
    displayedHero: Double,
    isPrivacyMode: Boolean,
    hasWalletBalance: Boolean,
    onToggleWallet: () -> Unit,
    onTogglePrivacyMode: (() -> Unit)?,
    strings: Strings,
    modifier: Modifier = Modifier
) {
    val haptics = rememberAppHaptics()

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = LocalAppColors.current.surfaceVariant.copy(alpha = 0.6f),
            border = BorderStroke(1.dp, LocalAppColors.current.border.copy(alpha = 0.8f)),
            modifier = Modifier.then(
                if (hasWalletBalance) {
                    Modifier.clickable {
                        haptics.lightTick()
                        onToggleWallet()
                    }
                } else Modifier
            )
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(if (displayedHero < 0 && !isPrivacyMode) ColorDangerExpense else ColorAccent)
                )
                Text(
                    text = currentHeaderLabel,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.2.sp
                    ),
                    color = ColorTextSecondary,
                    textAlign = TextAlign.Center
                )
                if (hasWalletBalance) {
                    Icon(
                        imageVector = Icons.Outlined.SwapHoriz,
                        contentDescription = "Toggle view",
                        tint = ColorTextMuted,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
        }

        if (onTogglePrivacyMode != null) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (isPrivacyMode) ColorAccent.copy(alpha = 0.16f) else Color.White.copy(alpha = 0.05f))
                    .border(
                        1.dp,
                        if (isPrivacyMode) ColorAccent.copy(alpha = 0.45f) else LocalAppColors.current.border,
                        CircleShape
                    )
                    .clickable {
                        haptics.lightTick()
                        onTogglePrivacyMode()
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isPrivacyMode) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                    contentDescription = if (isPrivacyMode) strings.showBalance else strings.hideBalance,
                    tint = if (isPrivacyMode) ColorAccent else LocalAppColors.current.textSecondary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
