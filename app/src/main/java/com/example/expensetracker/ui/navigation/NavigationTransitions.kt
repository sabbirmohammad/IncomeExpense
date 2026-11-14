package com.example.expensetracker.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith

fun AnimatedContentTransitionScope<NavigationItem>.navigationScreenTransitionSpec(): ContentTransform {
    val forward = targetState.ordinal > initialState.ordinal
    return if (targetState.isTopLevel && initialState.isTopLevel) {
        (slideInHorizontally(
            initialOffsetX = { fullWidth -> if (forward) fullWidth / 4 else -fullWidth / 4 },
            animationSpec = tween(220)
        ) + fadeIn(animationSpec = tween(220)))
            .togetherWith(
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> if (forward) -fullWidth / 4 else fullWidth / 4 },
                    animationSpec = tween(180)
                ) + fadeOut(animationSpec = tween(180))
            )
    } else {
        fadeIn(animationSpec = tween(220, delayMillis = 30))
            .togetherWith(fadeOut(animationSpec = tween(150)))
    }
}
