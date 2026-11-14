package com.example.expensetracker.ui.transactions

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

@Composable
fun ZoomableReceiptImage(
    receiptBitmap: Bitmap,
    contentDescription: String,
    scale: Float,
    offsetX: Float,
    offsetY: Float,
    onTransform: (zoomChange: Float, panX: Float, panY: Float, containerSize: IntSize) -> Unit,
    onDoubleTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    var containerSize by remember { mutableStateOf(IntSize.Zero) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged { containerSize = it }
            .pointerInput(Unit) {
                detectTapGestures(onDoubleTap = { onDoubleTap() })
            }
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    onTransform(zoom, pan.x, pan.y, containerSize)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Image(
            bitmap = receiptBitmap.asImageBitmap(),
            contentDescription = contentDescription,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 72.dp)
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offsetX,
                    translationY = offsetY
                ),
            contentScale = ContentScale.Fit
        )
    }
}
