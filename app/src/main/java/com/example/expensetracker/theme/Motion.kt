package com.example.expensetracker.theme

import android.content.Context
import android.provider.Settings
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext

// =========================================================================
// MOTION SYSTEM ("Refined Ledger v7")
// =========================================================================

val FastEasing: Easing = FastOutSlowInEasing
val StandardEasing: Easing = CubicBezierEasing(0.65f, 0.0f, 0.35f, 1.0f) // EaseInOutCubic
val EmphasizedEasing: Easing = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1.0f) // Emphasized Decelerate

data class MotionConfig(
    val reduceMotion: Boolean = false
) {
    val durationFast: Int get() = if (reduceMotion) 0 else 120
    val durationStandard: Int get() = if (reduceMotion) 0 else 220
    val durationEmphasized: Int get() = if (reduceMotion) 0 else 340

    fun <T> fastTween(delayMillis: Int = 0) = tween<T>(
        durationMillis = durationFast,
        delayMillis = if (reduceMotion) 0 else delayMillis,
        easing = FastEasing
    )

    fun <T> standardTween(delayMillis: Int = 0) = tween<T>(
        durationMillis = durationStandard,
        delayMillis = if (reduceMotion) 0 else delayMillis,
        easing = StandardEasing
    )

    fun <T> emphasizedTween(delayMillis: Int = 0) = tween<T>(
        durationMillis = durationEmphasized,
        delayMillis = if (reduceMotion) 0 else delayMillis,
        easing = EmphasizedEasing
    )
}

val LocalMotion = staticCompositionLocalOf { MotionConfig() }

/**
 * Checks system "remove animations" setting (ANIMATOR_DURATION_SCALE == 0) safely.
 */
@Composable
fun rememberSystemMotionConfig(): MotionConfig {
    val context = LocalContext.current
    return remember(context) {
        val reduceMotion = try {
            val scale = Settings.Global.getFloat(
                context.contentResolver,
                Settings.Global.ANIMATOR_DURATION_SCALE,
                1.0f
            )
            scale == 0f
        } catch (_: Throwable) {
            false
        }
        MotionConfig(reduceMotion = reduceMotion)
    }
}
