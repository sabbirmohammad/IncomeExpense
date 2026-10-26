package com.example.expensetracker.ui.navigation

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.example.expensetracker.theme.LocalAppColors

@Composable
fun Navigation3DScaffold(
    isDrawerOpen: Boolean,
    onCloseDrawer: () -> Unit,
    onOpenDrawer: () -> Unit,
    drawerContent: @Composable () -> Unit,
    mainContent: @Composable androidx.compose.foundation.layout.BoxScope.(drawerProgress: Float) -> Unit
) {
    val configuration = LocalConfiguration.current
    val drawerProgress by animateFloatAsState(
        targetValue = if (isDrawerOpen) 1f else 0f,
        animationSpec = spring(
            dampingRatio = 0.82f,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "drawer3DProgress"
    )

    val screenWidth = configuration.screenWidthDp.dp
    val slideOffset = (screenWidth * 0.70f).coerceAtMost(320.dp)
    val translationX = (-slideOffset * drawerProgress).coerceAtMost(0.dp)
    val contentScale = (1f - (0.16f * drawerProgress)).coerceIn(0.5f, 1f)
    val cornerRadius = (26.dp * drawerProgress).coerceAtLeast(0.dp)
    val rotationY = (8f * drawerProgress).coerceAtLeast(0f)
    val elevation = (16.dp * drawerProgress).coerceAtLeast(0.dp)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalAppColors.current.background)
    ) {
        drawerContent()

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .offset(x = translationX)
                .graphicsLayer {
                    this.scaleX = contentScale
                    this.scaleY = contentScale
                    this.rotationY = rotationY
                    this.cameraDistance = 16 * this.density
                    this.shadowElevation = elevation.toPx()
                    this.clip = drawerProgress > 0.02f
                    this.shape = RoundedCornerShape(cornerRadius)
                },
            shape = RoundedCornerShape(cornerRadius),
            color = LocalAppColors.current.background,
            border = if (drawerProgress > 0.05f) BorderStroke(1.dp, LocalAppColors.current.border.copy(alpha = drawerProgress.coerceIn(0f, 1f))) else null
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
            ) {
                mainContent(drawerProgress)

                if (!isDrawerOpen) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .fillMaxHeight()
                            .width(28.dp)
                            .pointerInput(Unit) {
                                detectHorizontalDragGestures { _, dragAmount ->
                                    if (dragAmount < -15) {
                                        onOpenDrawer()
                                    }
                                }
                            }
                    )
                }

                if (drawerProgress > 0.01f) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(cornerRadius))
                            .background(Color.Black.copy(alpha = (0.35f * drawerProgress).coerceIn(0f, 1f)))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                onCloseDrawer()
                            }
                            .pointerInput(Unit) {
                                detectHorizontalDragGestures { _, dragAmount ->
                                    if (dragAmount > 15) {
                                        onCloseDrawer()
                                    }
                                }
                            }
                    )
                }
            }
        }
    }
}
