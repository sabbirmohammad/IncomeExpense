package com.example.expensetracker.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// =========================================================================
// CRAFTED SHAPE & ELEVATION SYSTEM ("The Living Ledger" + "Refined Ledger v7")
// =========================================================================

// --- Legacy Living Ledger Shape Tokens (Preserved for Test Invariants) ---
val RadiusOuterSheet   = 22.dp // Primary screen cards & bottom sheets
val RadiusCardGroup    = 16.dp // Grouped ledger lists & trip summaries
val RadiusActionChip   = 10.dp // Interactive filter pills & action buttons
val RadiusBadgeTag     = 6.dp  // Compact status tags & item counts

val HairlineInkStroke  = 0.5.dp // Elegant bookkeeper hairline divider

// Backwards-compatible aliases for existing callers
val CardCornerRadius   = RadiusCardGroup
val SmallElementRadius = RadiusActionChip
val CardPadding        = 16.dp
val ScreenPaddingHorizontal = 16.dp
val CardGap            = 12.dp
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

// --- V7 Design System Tokens ("Refined Ledger" + C1 Patch) ---
val RadiusXS    = 6.dp  // Badges
val RadiusS     = 12.dp // Chips, inputs, keypad keys (48dp touch target)
val RadiusM     = 16.dp // Cards (per C1: 16dp radius, no 28dp card corners)
val RadiusL     = 24.dp // Hero cards, large buttons
val RadiusSheet = 24.dp // Bottom sheets top corners (per C1 patch)
val RadiusPill  = 999.dp // Full pill buttons

val ShapeBadge       = RoundedCornerShape(RadiusXS)
val ShapeChip        = RoundedCornerShape(RadiusS)
val ShapeCard        = RoundedCornerShape(RadiusM)
val ShapeHero        = RoundedCornerShape(RadiusL)
val ShapeSheet       = RoundedCornerShape(topStart = RadiusSheet, topEnd = RadiusSheet)
val ShapePill        = CircleShape

val V7Shapes = Shapes(
    extraSmall = RoundedCornerShape(RadiusXS),
    small = RoundedCornerShape(RadiusS),
    medium = RoundedCornerShape(RadiusM),
    large = RoundedCornerShape(RadiusL),
    extraLarge = RoundedCornerShape(RadiusSheet)
)
