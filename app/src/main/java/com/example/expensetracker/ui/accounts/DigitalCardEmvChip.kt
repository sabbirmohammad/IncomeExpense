package com.example.expensetracker.ui.accounts

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun DigitalCardEmvChip(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(width = 32.dp, height = 24.dp)
            .clip(RoundedCornerShape(5.dp))
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFFFFDF7A), Color(0xFFD4AF37), Color(0xFFB8860B))
                )
            )
            .border(0.5.dp, Color(0xFF8B6508), RoundedCornerShape(5.dp)),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(width = 22.dp, height = 14.dp)
                .border(0.5.dp, Color(0xFF8B6508).copy(alpha = 0.5f), RoundedCornerShape(2.dp))
        )
    }
}
