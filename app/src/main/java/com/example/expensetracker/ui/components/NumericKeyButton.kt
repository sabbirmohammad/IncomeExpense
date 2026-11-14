package com.example.expensetracker.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Backspace
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.HairlineInkStroke
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.RadiusActionChip
import com.example.expensetracker.theme.StyleNumberKeypad

sealed interface KeypadKey {
    data class Digit(val char: Char, val display: String) : KeypadKey
    data class Operator(val char: Char, val display: String) : KeypadKey
    data object Decimal : KeypadKey
    data object Backspace : KeypadKey
}

@Composable
internal fun NumericKeyButton(
    key: KeypadKey,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null
) {
    val appColors = LocalAppColors.current
    val isOperator = key is KeypadKey.Operator
    val isBackspace = key is KeypadKey.Backspace

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scaleAnim by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1.0f,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessMediumLow),
        label = "keyScale"
    )

    val backgroundColor = when {
        isOperator -> appColors.infoContainer
        else -> appColors.surfaceVariant
    }

    val contentColor = when {
        isOperator -> appColors.info
        isBackspace -> appColors.textSecondary
        else -> appColors.textPrimary
    }

    val border = if (appColors.isDark) {
        null
    } else {
        BorderStroke(HairlineInkStroke, appColors.border)
    }

    val semanticsLabel = when (key) {
        is KeypadKey.Digit -> key.display
        is KeypadKey.Operator -> when (key.char) {
            '+' -> "Plus"
            '−' -> "Minus"
            '×' -> "Multiply"
            '÷' -> "Divide"
            else -> key.display
        }
        is KeypadKey.Decimal -> "Point"
        is KeypadKey.Backspace -> "Backspace"
    }

    Box(
        modifier = modifier
            .defaultMinSize(minHeight = 56.dp)
            .minimumInteractiveComponentSize()
            .scale(scaleAnim)
            .clip(RoundedCornerShape(RadiusActionChip))
            .background(backgroundColor)
            .then(
                if (border != null) Modifier.background(backgroundColor, RoundedCornerShape(RadiusActionChip))
                else Modifier
            )
            .pointerInput(key) {
                detectTapGestures(
                    onTap = { onClick() },
                    onLongPress = { onLongClick?.invoke() }
                )
            }
            .semantics { contentDescription = semanticsLabel },
        contentAlignment = Alignment.Center
    ) {
        when (key) {
            is KeypadKey.Digit -> {
                Text(
                    text = key.display,
                    style = StyleNumberKeypad.copy(fontWeight = FontWeight.SemiBold),
                    color = contentColor
                )
            }
            is KeypadKey.Operator -> {
                Text(
                    text = key.display,
                    style = StyleNumberKeypad.copy(fontWeight = FontWeight.Bold, fontSize = 24.sp),
                    color = contentColor
                )
            }
            is KeypadKey.Decimal -> {
                Text(
                    text = ".",
                    style = StyleNumberKeypad.copy(fontWeight = FontWeight.Bold, fontSize = 24.sp),
                    color = contentColor
                )
            }
            is KeypadKey.Backspace -> {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.Backspace,
                    contentDescription = semanticsLabel,
                    tint = contentColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
