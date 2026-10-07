package com.example.expensetracker.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Redesign Specification Geometry Tokens
val CardCornerRadius = 16.dp
val SmallElementRadius = 10.dp
val CardPadding = 16.dp
val ScreenPaddingHorizontal = 16.dp
val CardGap = 12.dp
val ListRowPaddingVertical = 10.dp
val HairlineDividerStroke = 0.5.dp

val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(SmallElementRadius), // 10dp for icon badges, chips
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(14.dp),
    large = RoundedCornerShape(CardCornerRadius),       // 16dp for cards
    extraLarge = RoundedCornerShape(24.dp)
)

val OneUiShapes = AppShapes

