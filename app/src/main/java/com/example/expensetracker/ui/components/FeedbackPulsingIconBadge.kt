package com.example.expensetracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun FeedbackPulsingIconBadge(
    tintColor: Color,
    badgeBgColor: Color,
    icon: ImageVector,
    pulseScale: Float,
    pulseAlpha: Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.size(88.dp),
        contentAlignment = Alignment.Center
    ) {
        // Expanding glow ring
        Box(
            modifier = Modifier
                .size(72.dp)
                .scale(pulseScale)
                .clip(CircleShape)
                .background(tintColor.copy(alpha = pulseAlpha))
        )

        // Outer ring border
        Box(
            modifier = Modifier
                .size(74.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(tintColor.copy(alpha = 0.25f), Color.Transparent)
                    )
                )
        )

        // Central Badge
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(badgeBgColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tintColor,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}
