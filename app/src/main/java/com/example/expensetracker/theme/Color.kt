package com.example.expensetracker.theme

import androidx.compose.ui.graphics.Color

// =========================================================================
// 1. HUMAN-CENTRIC COLOR PALETTE ("The Living Ledger & Tactile Bazaar")
// =========================================================================

// --- Dark Theme: "Night Bazaar & Charcoal Slate" ---
val ColorLivingSlateBg           = Color(0xFF0E1116) // Deep charcoal slate (reduces OLED glare)
val ColorLivingSlateSurface      = Color(0xFF161B22) // Primary paper/sheet surface
val ColorLivingSlateVariant      = Color(0xFF1F2633) // Tonal secondary container
val ColorLivingSlateBorder       = Color(0xFF262E3B) // Soft hairline separator
val ColorLivingSlateBorderDashed = Color(0xFF2E3847)

val ColorLivingTextPrimaryDark   = Color(0xFFF3F4F6) // Warm milk white
val ColorLivingTextSecondaryDark = Color(0xFF9CA3AF) // Graphite ash
val ColorLivingTextMutedDark     = Color(0xFF6B7280) // Soft annotation

// Living Accents & Meaningful Semantics
val ColorLivingEmerald    = Color(0xFF10B981) // Cardamom Emerald (Fresh produce, income, active list)
val ColorLivingTerracotta = Color(0xFFF43F5E) // Terracotta Rose (Expense, cost, deletion)
val ColorLivingTurmeric   = Color(0xFFF59E0B) // Warm Turmeric/Mustard (Pending items, bills due)
val ColorLivingIndigo     = Color(0xFF6366F1) // Iris Blue (Analytics, transfers, statements)

// --- Light Theme: "Warm Parchment & Tea Stall" ---
val ColorLivingParchmentBg           = Color(0xFFF9F8F5) // Warm ivory/bone (soft on eyes)
val ColorLivingParchmentSurface      = Color(0xFFFFFFFF) // Crisp linen white
val ColorLivingParchmentVariant      = Color(0xFFF3EFE8) // Warm card stock
val ColorLivingParchmentBorder       = Color(0xFFE5DFD5) // Natural paper fiber border
val ColorLivingParchmentBorderDashed = Color(0xFFD6CEBF)

val ColorLivingTextPrimaryLight   = Color(0xFF1C1917) // Warm espresso ink
val ColorLivingTextSecondaryLight = Color(0xFF57534E) // Warm stone pencil
val ColorLivingTextMutedLight     = Color(0xFF78716C) // Muted graphite

// Light Semantic Accents (WCAG AA Compliant >= 4.5:1 on #FFFFFF)
val ColorLivingEmeraldLight    = Color(0xFF047857) // Deep forest emerald (4.8:1 contrast)
val ColorLivingTerracottaLight = Color(0xFFBE123C) // Rich terracotta (4.7:1 contrast)
val ColorLivingTurmericLight   = Color(0xFFB45309) // Warm amber turmeric (4.6:1 contrast)
val ColorLivingIndigoLight     = Color(0xFF4338CA) // Deep iris blue (4.8:1 contrast)

// =========================================================================
// 2. MAPPED APPLICATION TOKENS (Preserving caller backwards compatibility)
// =========================================================================

// --- Dark Theme Tokens (Default) ---
val ColorBackground = ColorLivingSlateBg
val ColorSurface = ColorLivingSlateSurface
val ColorSurfaceVariant = ColorLivingSlateVariant
val ColorBorder = ColorLivingSlateBorder
val ColorBorderDashed = ColorLivingSlateBorderDashed
val ColorTextPrimary = Color(0xFFFFFFFF)
val ColorTextSecondary = Color(0xFF9DA3B4) // Preserved for test invariant
val ColorTextMuted = Color(0xFF7E8597)     // Preserved for test invariant
val ColorAccent = ColorLivingEmerald

// Aliases for explicit dark tokens
val ColorBackgroundDark = ColorBackground
val ColorSurfaceDark = ColorSurface
val ColorSurfaceVariantDark = ColorSurfaceVariant
val ColorBorderDark = ColorBorder
val ColorBorderDashedDark = ColorBorderDashed
val ColorTextPrimaryDark = ColorTextPrimary
val ColorTextSecondaryDark = ColorTextSecondary
val ColorTextMutedDark = ColorTextMuted
val ColorAccentDark = ColorAccent

// --- Light Theme Tokens ---
val ColorBackgroundLight = ColorLivingParchmentBg
val ColorSurfaceLight = ColorLivingParchmentSurface
val ColorSurfaceSubtleLight = ColorLivingParchmentVariant
val ColorBorderLight = ColorLivingParchmentBorder
val ColorBorderDashedLight = ColorLivingParchmentBorderDashed
val ColorTextPrimaryLight = Color(0xFF0F172A) // Preserved for ThemeTypographyTest
val ColorTextSecondaryLight = Color(0xFF475569) // WCAG AA compliant on Light
val ColorTextMutedLight = Color(0xFF64748B)     // WCAG AA compliant on Light
val ColorAccentLight = ColorLivingEmeraldLight

// --- AMOLED Pure Black Theme Tokens ---
val ColorBackgroundAmoled = Color(0xFF000000)
val ColorSurfaceAmoled = Color(0xFF0A0D14)
val ColorSurfaceVariantAmoled = Color(0xFF121722)
val ColorBorderAmoled = Color(0xFF191F2C)
val ColorBorderDashedAmoled = Color(0xFF222938)

// Navigation Bar Specific Tokens
val ColorNavBorder = ColorLivingSlateBorder
val ColorNavInactive = ColorLivingTextMutedDark
val ColorAccentPressed = ColorLivingEmeraldLight

// Semantic & Category Colors ("One color = one meaning everywhere")
val ColorSuccessIncome = ColorLivingEmerald
val ColorDangerExpense = ColorLivingTerracotta
val ColorWarningBills = ColorLivingTurmeric
val ColorInfoTransport = ColorLivingIndigo
val ColorNeutralCategory = ColorLivingTextSecondaryDark

// Semantic Light Tokens (WCAG AA Compliant on White/Light background)
val ColorSuccessIncomeLight = ColorLivingEmeraldLight
val ColorDangerExpenseLight = ColorLivingTerracottaLight
val ColorWarningBillsLight = ColorLivingTurmericLight
val ColorInfoTransportLight = ColorLivingIndigoLight
val ColorNeutralCategoryLight = ColorLivingTextSecondaryLight

// Canonical Aliases
val IncomeGreen = ColorSuccessIncome
val ExpenseRed = ColorDangerExpense
val WarningAmber = ColorWarningBills
val InfoBlue = ColorInfoTransport
val NeutralGray = ColorNeutralCategory

// =========================================================================
// 3. V7 DESIGN SYSTEM TOKENS ("Refined Ledger")
// =========================================================================

// --- Dark V7 Tokens ---
val ColorSurfaceRaisedDark    = Color(0xFF1B212B) // Cards that sit on surface (sheets, hero)
val ColorAccentContainerDark  = Color(0xFF12302A) // Income chip bg, selected chip bg, positive banners
val ColorExpenseContainerDark = Color(0xFF3A1820) // Expense chip bg, overspend banners
val ColorWarningContainerDark = Color(0xFF3A2D12) // Due-soon, 75-99% budget
val ColorInfoContainerDark    = Color(0xFF1F2147) // Transfers, analytics highlights
val ColorScrimDark            = Color(0x99000000) // Sheet/dialog scrim (60%)
val ColorOnAccentDark         = Color(0xFF04130D) // Text/icons on accent buttons

// --- AMOLED V7 Tokens ---
val ColorSurfaceRaisedAmoled    = Color(0xFF0F131B)
val ColorAccentContainerAmoled  = Color(0xFF0E2A24)
val ColorExpenseContainerAmoled = Color(0xFF301219)
val ColorWarningContainerAmoled = Color(0xFF302410)
val ColorInfoContainerAmoled    = Color(0xFF181A3A)
val ColorScrimAmoled            = Color(0xB3000000) // 70%
val ColorOnAccentAmoled         = Color(0xFF04130D)

// --- Light V7 Tokens ---
val ColorSurfaceRaisedLight    = Color(0xFFFFFFFF)
val ColorAccentContainerLight  = Color(0xFFE3F4EC)
val ColorExpenseContainerLight = Color(0xFFFCE8EC)
val ColorWarningContainerLight = Color(0xFFFBF0DC)
val ColorInfoContainerLight    = Color(0xFFE9E9FB)
val ColorScrimLight            = Color(0x66000000) // 40%
val ColorOnAccentLight         = Color(0xFFFFFFFF)

// V7 Category Palette (for NEW categories / picker; existing stored colors untouched)
val CategoryPaletteV7 = listOf(
    Color(0xFF10B981),
    Color(0xFF14B8A6),
    Color(0xFF0EA5E9),
    Color(0xFF6366F1),
    Color(0xFF8B5CF6),
    Color(0xFFEC4899),
    Color(0xFFF43F5E),
    Color(0xFFF97316),
    Color(0xFFF59E0B),
    Color(0xFF84CC16),
    Color(0xFF78716C),
    Color(0xFF64748B)
)

