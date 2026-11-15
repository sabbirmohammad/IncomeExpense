package com.example.expensetracker.ui.settings

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.expensetracker.theme.AppColors

internal object SettingsProfileGradients {
    fun background(appColors: AppColors): Brush = if (appColors.isDark) {
        Brush.linearGradient(
            colors = listOf(Color(0xFF141A2D), Color(0xFF161C30), Color(0xFF0F1424))
        )
    } else {
        Brush.linearGradient(
            colors = listOf(Color(0xFFFFFFFF), Color(0xFFF7F9FC))
        )
    }

    fun border(appColors: AppColors): Brush = Brush.linearGradient(
        colors = listOf(
            appColors.accent.copy(alpha = 0.45f),
            appColors.border.copy(alpha = 0.7f),
            appColors.accent.copy(alpha = 0.2f)
        )
    )
}
