package com.example.expensetracker.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.LocalMotion
import com.example.expensetracker.theme.RadiusS
import com.example.expensetracker.theme.ShapeCard
import com.example.expensetracker.theme.ShapeChip

/**
 * Animated Shimmer Skeleton Box for loading states.
 * Respects reduce-motion by displaying a static tone without animation.
 */
@Composable
fun SkeletonBox(
    modifier: Modifier = Modifier,
    shape: Shape = ShapeChip
) {
    val colors = LocalAppColors.current
    val motion = LocalMotion.current

    val baseColor = if (colors.isDark) colors.surfaceVariant else colors.surfaceVariant.copy(alpha = 0.6f)
    val highlightColor = if (colors.isDark) colors.surfaceRaised else Color.White.copy(alpha = 0.8f)

    if (motion.reduceMotion) {
        Box(
            modifier = modifier
                .clip(shape)
                .background(baseColor)
        )
        return
    }

    val transition = rememberInfiniteTransition(label = "shimmerTransition")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )

    val brush = Brush.linearGradient(
        colors = listOf(baseColor, highlightColor, baseColor),
        start = Offset(translateAnim - 500f, translateAnim - 500f),
        end = Offset(translateAnim, translateAnim)
    )

    Box(
        modifier = modifier
            .clip(shape)
            .background(brush)
    )
}

/**
 * Placeholder list of shimmer rows.
 */
@Composable
fun ShimmerList(
    itemCount: Int = 3,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        repeat(itemCount) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SkeletonBox(
                    modifier = Modifier.size(40.dp),
                    shape = CircleShape
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    SkeletonBox(
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .height(16.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    SkeletonBox(
                        modifier = Modifier
                            .fillMaxWidth(0.35f)
                            .height(12.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                SkeletonBox(
                    modifier = Modifier
                        .width(64.dp)
                        .height(18.dp)
                )
            }
        }
    }
}
