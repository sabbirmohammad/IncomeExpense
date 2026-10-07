package com.example.expensetracker.theme

import androidx.compose.ui.graphics.Color

// --- Dark Theme Tokens (Default) ---
val ColorBackground = Color(0xFF0B0E16)
val ColorSurface = Color(0xFF141826)
val ColorBorder = Color(0xFF232838)
val ColorBorderDashed = Color(0xFF2A2F40)
val ColorTextPrimary = Color(0xFFFFFFFF)
val ColorTextSecondary = Color(0xFF9DA3B4) // Enhanced WCAG AA contrast
val ColorTextMuted = Color(0xFF7E8597)     // Enhanced contrast
val ColorAccent = Color(0xFF3B6EF5)

// --- Light Theme Tokens ---
val ColorBackgroundLight = Color(0xFFF4F6FB)
val ColorSurfaceLight = Color(0xFFFFFFFF)
val ColorSurfaceSubtleLight = Color(0xFFF8FAFC)
val ColorBorderLight = Color(0xFFE2E8F0)
val ColorBorderDashedLight = Color(0xFFCBD5E1)
val ColorTextPrimaryLight = Color(0xFF0F172A)
val ColorTextSecondaryLight = Color(0xFF475569)
val ColorTextMutedLight = Color(0xFF64748B)
val ColorAccentLight = Color(0xFF2563EB)

// --- AMOLED Pure Black Theme Tokens ---
val ColorBackgroundAmoled = Color(0xFF000000)
val ColorSurfaceAmoled = Color(0xFF0A0D14)
val ColorBorderAmoled = Color(0xFF191F2C)
val ColorBorderDashedAmoled = Color(0xFF222938)

// Navigation Bar Specific Tokens
val ColorNavBorder = Color(0xFF1C2130)
val ColorNavInactive = Color(0xFF82889A)
val ColorAccentPressed = Color(0xFF2F5CD0)

// Semantic & Category Colors ("One color = one meaning everywhere")
val ColorSuccessIncome = Color(0xFF3DDC97)
val ColorDangerExpense = Color(0xFFF0716B)
val ColorWarningBills = Color(0xFFF0A63D)
val ColorInfoTransport = Color(0xFF3B8FF0)
val ColorNeutralCategory = Color(0xFF8B90A0)

// Canonical Aliases
val IncomeGreen = ColorSuccessIncome
val ExpenseRed = ColorDangerExpense
val WarningAmber = ColorWarningBills
val InfoBlue = ColorInfoTransport
val NeutralGray = ColorNeutralCategory

