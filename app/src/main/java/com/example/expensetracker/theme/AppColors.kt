package com.example.expensetracker.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Pulse Design System AppColors tokens across Light, Dark, and AMOLED themes.
 * All contrast pairs meet WCAG AA (>= 4.5:1 text, >= 3:1 graphical/large).
 */
@Immutable
data class AppColors(
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val border: Color,
    val borderDashed: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val accent: Color,
    val onAccent: Color,
    val income: Color,
    val expense: Color,
    val warning: Color,
    val info: Color,
    val neutral: Color = textSecondary,
    val navBorder: Color = border,
    val navInactive: Color = textMuted,
    val isDark: Boolean = true,
    val cardBackground: Color = surface,
    val divider: Color = border,
    val surfaceRaised: Color = surfaceVariant,
    val accentContainer: Color = accent.copy(alpha = if (isDark) 0.18f else 0.12f),
    val expenseContainer: Color = expense.copy(alpha = if (isDark) 0.18f else 0.12f),
    val warningContainer: Color = warning.copy(alpha = if (isDark) 0.18f else 0.12f),
    val infoContainer: Color = info.copy(alpha = if (isDark) 0.18f else 0.12f),
    val incomeContainer: Color = income.copy(alpha = if (isDark) 0.18f else 0.12f),
    val scrim: Color = Color(0x85000000), // 52% alpha
    val focusRing: Color = accent,
    // Module Hero 2-stop Gradients (top-left -> bottom-right)
    // Home = Blue, Money = Purple, Bazaar = Leaf Green, Plan = Mango Orange
    val heroHome: Pair<Color, Color> = if (isDark) Color(0xFF1E3A8A) to Color(0xFF172554) else Color(0xFF1E3A8A) to Color(0xFF2563EB),
    val heroMoney: Pair<Color, Color> = if (isDark) Color(0xFF3B0764) to Color(0xFF2E1065) else Color(0xFF581C87) to Color(0xFF7C3AED),
    val heroBazaar: Pair<Color, Color> = if (isDark) Color(0xFF064E3B) to Color(0xFF022C22) else Color(0xFF065F46) to Color(0xFF047857),
    val heroPlan: Pair<Color, Color> = if (isDark) Color(0xFF7C2D12) to Color(0xFF431407) else Color(0xFF9A3412) to Color(0xFFC2410C)
) {
    val successIncome: Color get() = income
    val dangerExpense: Color get() = expense
    val warningBills: Color get() = warning
    val infoTransport: Color get() = info
    val moduleHome: Color get() = heroHome.first
    val moduleMoney: Color get() = heroMoney.first
    val moduleBazaar: Color get() = heroBazaar.first
    val modulePlan: Color get() = heroPlan.first
}

// --- Pulse Light Theme Tokens ---
val LightAppColors = AppColors(
    background = Color(0xFFF4F6FB),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFEAEEF7),
    border = Color(0xFFDDE3F0),
    borderDashed = Color(0xFFCBD5E1),
    textPrimary = Color(0xFF0F1729),
    textSecondary = Color(0xFF4B5675),
    textMuted = Color(0xFF64748B),
    accent = Color(0xFFF57C00),     // Brand Mango
    onAccent = Color(0xFF1A1200),   // High contrast AA on Mango (6.87:1)
    income = Color(0xFF047857),     // Forest Green (5.48:1 on #FFFFFF)
    expense = Color(0xFFC53030),    // Rich Red (5.47:1 on #FFFFFF)
    warning = Color(0xFFB45309),    // Amber 700 (5.02:1 on #FFFFFF)
    info = Color(0xFF2563EB),       // Cobalt Blue (5.17:1 on #FFFFFF)
    neutral = Color(0xFF4B5675),
    navBorder = Color(0xFFDDE3F0),
    navInactive = Color(0xFF64748B),
    isDark = false,
    cardBackground = Color(0xFFFFFFFF),
    divider = Color(0xFFDDE3F0),
    surfaceRaised = ColorSurfaceRaisedLight,
    accentContainer = ColorAccentContainerLight,
    expenseContainer = ColorExpenseContainerLight,
    warningContainer = ColorWarningContainerLight,
    infoContainer = ColorInfoContainerLight,
    scrim = ColorScrimLight,
    heroHome = Color(0xFF1E3A8A) to Color(0xFF2563EB),
    heroMoney = Color(0xFF581C87) to Color(0xFF7C3AED),
    heroBazaar = Color(0xFF065F46) to Color(0xFF047857),
    heroPlan = Color(0xFF9A3412) to Color(0xFFC2410C)
)

// --- Pulse Dark Theme Tokens ---
val DarkAppColors = AppColors(
    background = Color(0xFF0B0F19),
    surface = Color(0xFF141A28),
    surfaceVariant = Color(0xFF1D2538),
    border = Color(0xFF27304A),
    borderDashed = Color(0xFF333E5E),
    textPrimary = Color(0xFFF2F5FF),
    textSecondary = Color(0xFFA6B0CC),
    textMuted = Color(0xFF7F8AAA),
    accent = Color(0xFFFFA033),     // Bright Mango
    onAccent = Color(0xFF1A1200),   // High contrast AA (9.14:1)
    income = Color(0xFF34D399),     // Mint Emerald (9.04:1 on surface)
    expense = Color(0xFFFF7A7F),    // Coral Rose (6.91:1 on surface)
    warning = Color(0xFFFBBF24),    // Golden Amber (10.41:1 on surface)
    info = Color(0xFF7C9BFF),       // Soft Cobalt (7.79:1 on surface)
    neutral = Color(0xFFA6B0CC),
    navBorder = Color(0xFF27304A),
    navInactive = Color(0xFF7F8AAA),
    isDark = true,
    cardBackground = Color(0xFF141A28),
    divider = Color(0xFF27304A),
    surfaceRaised = ColorSurfaceRaisedDark,
    accentContainer = ColorAccentContainerDark,
    expenseContainer = ColorExpenseContainerDark,
    warningContainer = ColorWarningContainerDark,
    infoContainer = ColorInfoContainerDark,
    scrim = ColorScrimDark,
    heroHome = Color(0xFF1E3A8A) to Color(0xFF172554),
    heroMoney = Color(0xFF3B0764) to Color(0xFF2E1065),
    heroBazaar = Color(0xFF064E3B) to Color(0xFF022C22),
    heroPlan = Color(0xFF7C2D12) to Color(0xFF431407)
)

// --- Pulse AMOLED Theme Tokens ---
val AmoledAppColors = AppColors(
    background = Color(0xFF000000),
    surface = Color(0xFF0A0D14),
    surfaceVariant = Color(0xFF121722),
    border = Color(0xFF1B2133),
    borderDashed = Color(0xFF252D42),
    textPrimary = Color(0xFFFFFFFF),
    textSecondary = Color(0xFFA6B0CC),
    textMuted = Color(0xFF7F8AAA),
    accent = Color(0xFFFFA033),
    onAccent = Color(0xFF1A1200),
    income = Color(0xFF34D399),
    expense = Color(0xFFFF7A7F),
    warning = Color(0xFFFBBF24),
    info = Color(0xFF7C9BFF),
    neutral = Color(0xFFA6B0CC),
    navBorder = Color(0xFF1B2133),
    navInactive = Color(0xFF7F8AAA),
    isDark = true,
    cardBackground = Color(0xFF0A0D14),
    divider = Color(0xFF1B2133),
    surfaceRaised = Color(0xFF0F131B),
    accentContainer = Color(0xFF0B1F19),
    expenseContainer = Color(0xFF240E13),
    warningContainer = Color(0xFF241A0C),
    infoContainer = Color(0xFF14162E),
    scrim = ColorScrimAmoled,
    heroHome = Color(0xFF1E3A8A) to Color(0xFF172554),
    heroMoney = Color(0xFF3B0764) to Color(0xFF2E1065),
    heroBazaar = Color(0xFF064E3B) to Color(0xFF022C22),
    heroPlan = Color(0xFF7C2D12) to Color(0xFF431407)
)

val LocalAppColors = staticCompositionLocalOf { DarkAppColors }
