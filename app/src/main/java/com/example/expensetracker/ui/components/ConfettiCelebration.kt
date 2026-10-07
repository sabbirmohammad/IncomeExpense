package com.example.expensetracker.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.math.sin
import kotlin.random.Random

private data class ConfettiParticle(
    val initialX: Float,
    val initialY: Float,
    val vx: Float,
    val vy: Float,
    val size: Float,
    val color: Color,
    val rotationSpeed: Float,
    val shapeType: Int // 0: rectangle, 1: circle, 2: ribbon
)

@Composable
fun ConfettiCelebration(
    trigger: Boolean,
    onAnimationEnd: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    if (!trigger) return

    val animProgress = remember { Animatable(0f) }
    val haptics = rememberAppHaptics()

    val colors = listOf(
        Color(0xFFFFD700), // Gold
        Color(0xFFFF3366), // Crimson / Pink
        Color(0xFF00E5FF), // Cyan
        Color(0xFF76FF03), // Lime
        Color(0xFFFF9100), // Orange
        Color(0xFFE040FB), // Magenta
        Color(0xFF00E676)  // Emerald
    )

    val particles = remember {
        List(55) {
            ConfettiParticle(
                initialX = Random.nextFloat(),
                initialY = -0.05f - Random.nextFloat() * 0.15f,
                vx = (Random.nextFloat() - 0.5f) * 0.25f,
                vy = 0.55f + Random.nextFloat() * 0.45f,
                size = 12f + Random.nextFloat() * 14f,
                color = colors[Random.nextInt(colors.size)],
                rotationSpeed = (Random.nextFloat() - 0.5f) * 720f,
                shapeType = Random.nextInt(3)
            )
        }
    }

    LaunchedEffect(trigger) {
        haptics.successFeedback()
        animProgress.snapTo(0f)
        animProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 2400, easing = LinearEasing)
        )
        onAnimationEnd()
    }

    val progress = animProgress.value
    if (progress < 1f) {
        Box(modifier = modifier.fillMaxSize()) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                val alpha = if (progress > 0.75f) {
                    ((1f - progress) / 0.25f).coerceIn(0f, 1f)
                } else 1f

                for (p in particles) {
                    // Physics: position with slight sway
                    val currentY = (p.initialY + (p.vy * progress) + (0.5f * 0.2f * progress * progress)) * canvasHeight
                    val sway = sin(progress * 10f + p.initialX * 20f) * (canvasWidth * 0.04f)
                    val currentX = (p.initialX * canvasWidth) + (p.vx * progress * canvasWidth) + sway

                    val currentRotation = progress * p.rotationSpeed

                    rotate(degrees = currentRotation, pivot = Offset(currentX, currentY)) {
                        when (p.shapeType) {
                            0 -> { // Rectangle
                                drawRect(
                                    color = p.color.copy(alpha = alpha),
                                    topLeft = Offset(currentX - p.size / 2, currentY - p.size / 4),
                                    size = Size(p.size, p.size * 0.55f)
                                )
                            }
                            1 -> { // Circle
                                drawCircle(
                                    color = p.color.copy(alpha = alpha),
                                    radius = p.size * 0.35f,
                                    center = Offset(currentX, currentY)
                                )
                            }
                            else -> { // Ribbon
                                drawRoundRect(
                                    color = p.color.copy(alpha = alpha),
                                    topLeft = Offset(currentX - p.size / 3, currentY - p.size),
                                    size = Size(p.size * 0.4f, p.size * 1.2f),
                                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
