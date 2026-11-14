package com.example.expensetracker.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// =========================================================================
// PULSE DESIGN SYSTEM SHAPES & RADII
// =========================================================================

val RadiusCard     = 24.dp // Large soft cards (no hairline borders)
val RadiusSheet    = 28.dp // Bottom sheets top corners
val RadiusButton   = 16.dp // Primary / prominent buttons
val RadiusInput    = 14.dp // Text inputs & search fields
val RadiusIconTile = 14.dp // Category & quick action icon tiles
val RadiusPill     = 999.dp // Filter chips & dock pill
val RadiusBadge    = 6.dp  // Compact status tags

// Legacy Living Ledger Shape Tokens (Preserved for Test Invariants)
val RadiusOuterSheet   = 22.dp
val RadiusCardGroup    = 16.dp
val RadiusActionChip   = 10.dp
val RadiusBadgeTag     = 6.dp
val HairlineInkStroke  = 0.5.dp

val CardCornerRadius   = RadiusCardGroup
val SmallElementRadius = RadiusActionChip
val CardPadding        = 16.dp
val ScreenPaddingHorizontal = 16.dp
val CardGap            = 12.dp
val ListRowPaddingVertical = 12.dp
val HairlineDividerStroke = HairlineInkStroke

// V7 aliases
val RadiusXS    = RadiusBadge
val RadiusS     = 12.dp
val RadiusM     = 16.dp
val RadiusL     = 24.dp

val ShapeBadge       = RoundedCornerShape(RadiusBadge)
val ShapeChip        = RoundedCornerShape(RadiusPill)
val ShapeCard        = RoundedCornerShape(RadiusCard)
val ShapeHero        = RoundedCornerShape(RadiusCard)
val ShapeSheet       = RoundedCornerShape(topStart = RadiusSheet, topEnd = RadiusSheet)
val ShapePill        = CircleShape
val ShapeIconTile    = RoundedCornerShape(RadiusIconTile)
val ShapeInput       = RoundedCornerShape(RadiusInput)
val ShapeButton      = RoundedCornerShape(RadiusButton)

val LivingShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(22.dp)
)

val PulseShapes = Shapes(
    extraSmall = RoundedCornerShape(RadiusXS),
    small = RoundedCornerShape(RadiusInput),
    medium = RoundedCornerShape(RadiusButton),
    large = RoundedCornerShape(RadiusCard),
    extraLarge = RoundedCornerShape(RadiusSheet)
)

val AppShapes = LivingShapes
val OneUiShapes = AppShapes
val V7Shapes = PulseShapes
