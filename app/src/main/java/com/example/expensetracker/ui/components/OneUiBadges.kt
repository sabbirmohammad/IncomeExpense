package com.example.expensetracker.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.expensetracker.theme.ColorBorder
import com.example.expensetracker.theme.ColorSuccessIncome
import com.example.expensetracker.theme.SmallElementRadius

@Composable
fun CategoryIconBadge(
    iconName: String,
    colorHex: String,
    modifier: Modifier = Modifier,
    size: Dp = 38.dp
) {
    val icon = CategoryIconHelper.getIcon(iconName)
    val color = CategoryIconHelper.parseColor(colorHex)

    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(SmallElementRadius))
            .background(color.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(20.dp)
        )
    }
}

// Compatibility alias for existing screens
@Composable
fun CategoryBadge(
    iconName: String,
    colorHex: String,
    modifier: Modifier = Modifier,
    size: Int = 38
) {
    CategoryIconBadge(
        iconName = iconName,
        colorHex = colorHex,
        modifier = modifier,
        size = size.dp
    )
}

@Composable
fun AppProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Dp = 7.dp,
    trackColor: Color = ColorBorder,
    fillColor: Color = ColorSuccessIncome
) {
    val clampedProgress = progress.coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = clampedProgress,
        animationSpec = tween(500),
        label = "progressBar"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(height / 2))
            .background(trackColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(animatedProgress)
                .height(height)
                .clip(RoundedCornerShape(height / 2))
                .background(fillColor)
        )
    }
}
