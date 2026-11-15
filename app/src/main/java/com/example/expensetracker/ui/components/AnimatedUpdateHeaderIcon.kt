package com.example.expensetracker.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.CloudDownload
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.SystemUpdate
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import com.example.expensetracker.data.update.UpdateState
import com.example.expensetracker.theme.LocalAppColors

@Composable
fun AnimatedUpdateHeaderIcon(
    updateState: UpdateState
) {
    val appColors = LocalAppColors.current

    // Key entrance animation strictly by state category to prevent continuous cancel/restart during downloads
    val stateKey = updateState::class
    val scaleAnim = remember { Animatable(0.75f) }
    LaunchedEffect(stateKey) {
        scaleAnim.snapTo(0.75f)
        scaleAnim.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulseGlow")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.12f,
        targetValue = 0.32f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    // Dedicated smooth download bounce animation
    val downloadTransition = rememberInfiniteTransition(label = "downloadTransition")
    val downloadBobY by downloadTransition.animateFloat(
        initialValue = -3f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(650, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "downloadBobY"
    )

    val iconColor = when (updateState) {
        is UpdateState.UpToDate, is UpdateState.ReadyToInstall -> appColors.income
        is UpdateState.Error -> appColors.expense
        else -> appColors.accent
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(76.dp)
    ) {
        // Pulsing background ring for UpToDate, ReadyToInstall AND Downloading
        if (updateState is UpdateState.UpToDate || updateState is UpdateState.ReadyToInstall || updateState is UpdateState.Downloading) {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .scale(pulseScale)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = pulseAlpha))
            )
        }

        // Inner circular badge
        Box(
            modifier = Modifier
                .size(64.dp)
                .scale(scaleAnim.value)
                .clip(CircleShape)
                .background(iconColor.copy(alpha = 0.14f)),
            contentAlignment = Alignment.Center
        ) {
            when (updateState) {
                is UpdateState.UpToDate -> Icon(
                    imageVector = Icons.Outlined.CheckCircle,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(34.dp)
                )
                is UpdateState.ReadyToInstall -> Icon(
                    imageVector = Icons.Outlined.CheckCircle,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(34.dp)
                )
                is UpdateState.Error -> Icon(
                    imageVector = Icons.Outlined.ErrorOutline,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(34.dp)
                )
                is UpdateState.Downloading -> Icon(
                    imageVector = Icons.Outlined.CloudDownload,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier
                        .size(34.dp)
                        .offset(y = downloadBobY.dp)
                )
                else -> Icon(
                    imageVector = Icons.Outlined.SystemUpdate,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(34.dp)
                )
            }
        }
    }
}
