package com.example.expensetracker.theme

import androidx.compose.ui.graphics.Color

// =========================================================================
// PULSE DESIGN SYSTEM COLOR PALETTE
// =========================================================================

// --- Dark & AMOLED Palette ---
val ColorPulseBackgroundDark     = Color(0xFF0B0F19)
val ColorPulseSurfaceDark        = Color(0xFF141A28)
val ColorPulseSurfaceVariantDark = Color(0xFF1D2538)
val ColorPulseBorderDark         = Color(0xFF27304A)
val ColorPulseBorderDashedDark   = Color(0xFF333E5E)

val ColorPulseTextPrimaryDark    = Color(0xFFF2F5FF)
val ColorPulseTextSecondaryDark  = Color(0xFFA6B0CC)
val ColorPulseTextMutedDark      = Color(0xFF7F8AAA)

val ColorPulseAccentDark         = Color(0xFFFFA033) // Mango
val ColorPulseOnAccentDark       = Color(0xFF1A1200)

val ColorPulseIncomeDark         = Color(0xFF34D399) // Mint emerald
val ColorPulseExpenseDark        = Color(0xFFFF7A7F) // Coral rose
val ColorPulseWarningDark        = Color(0xFFFBBF24) // Amber
val ColorPulseInfoDark           = Color(0xFF7C9BFF) // Cobalt

// --- Light Palette ---
val ColorPulseBackgroundLight     = Color(0xFFF4F6FB)
val ColorPulseSurfaceLight        = Color(0xFFFFFFFF)
val ColorPulseSurfaceVariantLight = Color(0xFFEAEEF7)
val ColorPulseBorderLight         = Color(0xFFDDE3F0)
val ColorPulseBorderDashedLight   = Color(0xFFCBD5E1)

val ColorPulseTextPrimaryLight    = Color(0xFF0F1729)
val ColorPulseTextSecondaryLight  = Color(0xFF4B5675)
val ColorPulseTextMutedLight      = Color(0xFF64748B)

val ColorPulseAccentLight         = Color(0xFFF57C00) // Mango
val ColorPulseOnAccentLight       = Color(0xFF1A1200) // AA on Mango (6.87:1)

val ColorPulseIncomeLight         = Color(0xFF047857) // Forest green (5.48:1 on #FFFFFF)
val ColorPulseExpenseLight        = Color(0xFFC53030) // Red (5.47:1 on #FFFFFF)
val ColorPulseWarningLight        = Color(0xFFB45309) // Amber 700 (5.02:1 on #FFFFFF)
val ColorPulseInfoLight           = Color(0xFF2563EB) // Cobalt Blue (5.17:1 on #FFFFFF)

// --- AMOLED Canvas ---
val ColorPulseBackgroundAmoled    = Color(0xFF000000)
val ColorPulseSurfaceAmoled       = Color(0xFF0A0D14)
val ColorPulseSurfaceVariantAmoled= Color(0xFF121722)
val ColorPulseBorderAmoled        = Color(0xFF1B2133)
val ColorPulseBorderDashedAmoled  = Color(0xFF252D42)

// =========================================================================
// LIVING LEDGER COMPATIBILITY TOKENS (Preserved for legacy test suites)
// =========================================================================

val ColorLivingSlateBg           = ColorPulseBackgroundDark
val ColorLivingSlateSurface      = ColorPulseSurfaceDark
val ColorLivingSlateVariant      = ColorPulseSurfaceVariantDark
val ColorLivingSlateBorder       = ColorPulseBorderDark
val ColorLivingSlateBorderDashed = ColorPulseBorderDashedDark

val ColorLivingTextPrimaryDark   = ColorPulseTextPrimaryDark
val ColorLivingTextSecondaryDark = ColorPulseTextSecondaryDark
val ColorLivingTextMutedDark     = ColorPulseTextMutedDark

val ColorLivingEmerald    = Color(0xFF10B981)
val ColorLivingTerracotta = Color(0xFFF43F5E)
val ColorLivingTurmeric   = Color(0xFFF59E0B)
val ColorLivingIndigo     = Color(0xFF6366F1)

val ColorLivingParchmentBg           = ColorPulseBackgroundLight
val ColorLivingParchmentSurface      = ColorPulseSurfaceLight
val ColorLivingParchmentVariant      = ColorPulseSurfaceVariantLight
val ColorLivingParchmentBorder       = ColorPulseBorderLight
val ColorLivingParchmentBorderDashed = ColorPulseBorderDashedLight

val ColorLivingTextPrimaryLight   = Color(0xFF1C1917)
val ColorLivingTextSecondaryLight = Color(0xFF57534E)
val ColorLivingTextMutedLight     = Color(0xFF78716C)

val ColorLivingEmeraldLight    = Color(0xFF047857)
val ColorLivingTerracottaLight = Color(0xFFBE123C)
val ColorLivingTurmericLight   = Color(0xFFB45309)
val ColorLivingIndigoLight     = Color(0xFF4338CA)

// =========================================================================
// BACKWARD-COMPATIBLE ALIASES
// =========================================================================

val ColorBackground = ColorPulseBackgroundDark
val ColorSurface = ColorPulseSurfaceDark
val ColorSurfaceVariant = ColorPulseSurfaceVariantDark
val ColorBorder = ColorPulseBorderDark
val ColorBorderDashed = ColorPulseBorderDashedDark
val ColorTextPrimary = Color(0xFFFFFFFF)
val ColorTextSecondary = ColorPulseTextSecondaryDark
val ColorTextMuted = ColorPulseTextMutedDark
val ColorAccent = ColorPulseAccentDark

val ColorBackgroundDark = ColorPulseBackgroundDark
val ColorSurfaceDark = ColorPulseSurfaceDark
val ColorSurfaceVariantDark = ColorPulseSurfaceVariantDark
val ColorBorderDark = ColorPulseBorderDark
val ColorBorderDashedDark = ColorPulseBorderDashedDark
val ColorTextPrimaryDark = Color(0xFFFFFFFF)
val ColorTextSecondaryDark = ColorPulseTextSecondaryDark
val ColorTextMutedDark = ColorPulseTextMutedDark
val ColorAccentDark = ColorPulseAccentDark

val ColorBackgroundLight = ColorPulseBackgroundLight
val ColorSurfaceLight = ColorPulseSurfaceLight
val ColorSurfaceSubtleLight = ColorPulseSurfaceVariantLight
val ColorBorderLight = ColorPulseBorderLight
val ColorBorderDashedLight = ColorPulseBorderDashedLight
val ColorTextPrimaryLight = ColorPulseTextPrimaryLight
val ColorTextSecondaryLight = ColorPulseTextSecondaryLight
val ColorTextMutedLight = ColorPulseTextMutedLight
val ColorAccentLight = ColorPulseAccentLight

val ColorBackgroundAmoled = ColorPulseBackgroundAmoled
val ColorSurfaceAmoled = ColorPulseSurfaceAmoled
val ColorSurfaceVariantAmoled = ColorPulseSurfaceVariantAmoled
val ColorBorderAmoled = ColorPulseBorderAmoled
val ColorBorderDashedAmoled = ColorPulseBorderDashedAmoled

val ColorNavBorder = ColorPulseBorderDark
val ColorNavInactive = ColorPulseTextMutedDark

val ColorSuccessIncome = ColorPulseIncomeDark
val ColorDangerExpense = ColorPulseExpenseDark
val ColorWarningBills = ColorPulseWarningDark
val ColorInfoTransport = ColorPulseInfoDark
val ColorNeutralCategory = ColorPulseTextSecondaryDark

val ColorSuccessIncomeLight = ColorPulseIncomeLight
val ColorDangerExpenseLight = ColorPulseExpenseLight
val ColorWarningBillsLight = ColorPulseWarningLight
val ColorInfoTransportLight = ColorPulseInfoLight
val ColorNeutralCategoryLight = ColorPulseTextSecondaryLight

val IncomeGreen = ColorSuccessIncome
val ExpenseRed = ColorDangerExpense
val WarningAmber = ColorWarningBills
val InfoBlue = ColorInfoTransport
val NeutralGray = ColorNeutralCategory

// V7 Container / Scrim Tokens
val ColorSurfaceRaisedDark = ColorPulseSurfaceVariantDark
val ColorAccentContainerDark = ColorPulseAccentDark.copy(alpha = 0.18f)
val ColorExpenseContainerDark = ColorPulseExpenseDark.copy(alpha = 0.18f)
val ColorWarningContainerDark = ColorPulseWarningDark.copy(alpha = 0.18f)
val ColorInfoContainerDark = ColorPulseInfoDark.copy(alpha = 0.18f)
val ColorScrimDark = Color(0x85000000)
val ColorOnAccentDark = ColorPulseOnAccentDark

val ColorSurfaceRaisedAmoled = ColorPulseSurfaceVariantAmoled
val ColorAccentContainerAmoled = ColorPulseAccentDark.copy(alpha = 0.18f)
val ColorExpenseContainerAmoled = ColorPulseExpenseDark.copy(alpha = 0.18f)
val ColorWarningContainerAmoled = ColorPulseWarningDark.copy(alpha = 0.18f)
val ColorInfoContainerAmoled = ColorPulseInfoDark.copy(alpha = 0.18f)
val ColorScrimAmoled = Color(0x85000000)
val ColorOnAccentAmoled = ColorPulseOnAccentDark

val ColorSurfaceRaisedLight = Color(0xFFFFFFFF)
val ColorAccentContainerLight = ColorPulseAccentLight.copy(alpha = 0.12f)
val ColorExpenseContainerLight = ColorPulseExpenseLight.copy(alpha = 0.12f)
val ColorWarningContainerLight = ColorPulseWarningLight.copy(alpha = 0.12f)
val ColorInfoContainerLight = ColorPulseInfoLight.copy(alpha = 0.12f)
val ColorScrimLight = Color(0x85000000)
val ColorOnAccentLight = ColorPulseOnAccentLight

val CategoryPaletteV7 = listOf(
    Color(0xFF10B981), // Emerald
    Color(0xFF14B8A6), // Teal
    Color(0xFF0EA5E9), // Sky blue
    Color(0xFF6366F1), // Iris Blue
    Color(0xFF8B5CF6), // Purple
    Color(0xFFA855F7), // Violet
    Color(0xFFEC4899), // Pink
    Color(0xFFF43F5E), // Terracotta Rose
    Color(0xFFF57C00), // Mango
    Color(0xFFF59E0B), // Turmeric
    Color(0xFF84CC16), // Lime
    Color(0xFF64748B)  // Slate
)
