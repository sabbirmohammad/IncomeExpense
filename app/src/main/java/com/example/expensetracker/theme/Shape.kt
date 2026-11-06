package com.example.expensetracker.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// =========================================================================
// CRAFTED SHAPE & ELEVATION SYSTEM ("The Living Ledger")
// =========================================================================

val RadiusOuterSheet   = 22.dp // Primary screen cards & bottom sheets
val RadiusCardGroup    = 16.dp // Grouped ledger lists & trip summaries
val RadiusActionChip   = 10.dp // Interactive filter pills & action buttons
val RadiusBadgeTag     = 6.dp  // Compact status tags & item counts

val HairlineInkStroke  = 0.5.dp // Elegant bookkeeper hairline divider

// Backwards-compatible aliases for existing callers
val CardCornerRadius = RadiusCardGroup
val SmallElementRadius = RadiusActionChip
val CardPadding = 16.dp
val ScreenPaddingHorizontal = 16.dp
val CardGap = 12.dp
val ListRowPaddingVertical = 10.dp
val HairlineDividerStroke = HairlineInkStroke

val LivingShapes = Shapes(
    extraSmall = RoundedCornerShape(RadiusBadgeTag),
    small = RoundedCornerShape(RadiusActionChip),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(RadiusCardGroup),
    extraLarge = RoundedCornerShape(RadiusOuterSheet)
)

val AppShapes = LivingShapes
val OneUiShapes = AppShapes

