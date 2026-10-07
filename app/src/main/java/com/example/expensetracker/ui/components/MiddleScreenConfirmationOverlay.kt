package com.example.expensetracker.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.CallSplit
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorDangerExpense
import com.example.expensetracker.theme.ColorSuccessIncome

@Composable
fun MiddleScreenConfirmationOverlay(
    controller: TransactionFeedbackController,
    modifier: Modifier = Modifier
) {
    val feedbackData = controller.currentFeedback
    val strings = LocalAppStrings.current
    val haptics = rememberAppHaptics()
    var showConfetti by remember { mutableStateOf(false) }

    val isVisible = feedbackData != null
    var lastData by remember { mutableStateOf<TransactionFeedbackData?>(null) }
    if (feedbackData != null) {
        lastData = feedbackData
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulseGlow")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.38f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseScale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.55f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseAlpha"
    )

    val timerProgress = remember { Animatable(1f) }

    LaunchedEffect(feedbackData) {
        if (feedbackData != null) {
            if (feedbackData.type == FeedbackVisualType.INCOME) {
                showConfetti = true
            }
            if (feedbackData.type == FeedbackVisualType.TRANSACTION_DELETED ||
                feedbackData.type == FeedbackVisualType.ITEM_DELETED
            ) {
                haptics.warningFeedback()
            } else {
                haptics.successFeedback()
            }

            timerProgress.snapTo(1f)
            timerProgress.animateTo(
                targetValue = 0f,
                animationSpec = tween(
                    durationMillis = feedbackData.durationMillis.toInt(),
                    easing = LinearEasing
                )
            )
            controller.dismiss()
        } else {
            showConfetti = false
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Full screen scrim
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(tween(180)),
            exit = fadeOut(tween(220))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.55f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        controller.dismiss()
                    }
            )
        }

        // Confetti Celebration for Income
        if (showConfetti) {
            ConfettiCelebration(
                trigger = showConfetti,
                onAnimationEnd = { showConfetti = false }
            )
        }

        // Center Animated Card
        AnimatedVisibility(
            visible = isVisible,
            enter = scaleIn(
                initialScale = 0.72f,
                animationSpec = spring(dampingRatio = 0.65f, stiffness = Spring.StiffnessMediumLow)
            ) + fadeIn(tween(180)),
            exit = scaleOut(targetScale = 0.85f, animationSpec = tween(200)) + fadeOut(tween(200))
        ) {
            val data = lastData ?: return@AnimatedVisibility

            val (tintColor, badgeBgColor, icon) = when (data.type) {
                FeedbackVisualType.INCOME -> Triple(ColorSuccessIncome, Color(0xFF0F3820), Icons.Outlined.ArrowUpward)
                FeedbackVisualType.EXPENSE -> Triple(ColorDangerExpense, Color(0xFF381219), Icons.Outlined.ArrowDownward)
                FeedbackVisualType.TRANSFER -> Triple(ColorAccent, Color(0xFF0D253A), Icons.Outlined.SwapHoriz)
                FeedbackVisualType.BILL_PAID -> Triple(Color(0xFFFFB300), Color(0xFF36280B), Icons.Outlined.ReceiptLong)
                FeedbackVisualType.BILL_SPLIT -> Triple(Color(0xFFB388FF), Color(0xFF26153B), Icons.Outlined.CallSplit)
                FeedbackVisualType.DEBT_PAYMENT -> Triple(Color(0xFF26A69A), Color(0xFF0E2E2A), Icons.Outlined.AccountBalanceWallet)
                FeedbackVisualType.TRANSACTION_DELETED, FeedbackVisualType.ITEM_DELETED -> Triple(Color(0xFFFF3366), Color(0xFF3B101C), Icons.Outlined.DeleteOutline)
                FeedbackVisualType.GENERIC_SUCCESS -> Triple(ColorSuccessIncome, Color(0xFF0F3820), Icons.Filled.Check)
            }

            ConfirmationFeedbackCard(
                data = data,
                tintColor = tintColor,
                badgeBgColor = badgeBgColor,
                icon = icon,
                pulseScale = pulseScale,
                pulseAlpha = pulseAlpha,
                timerProgress = timerProgress.value,
                isBangla = strings.isBangla,
                onDismiss = { controller.dismiss() }
            )
        }
    }
}
