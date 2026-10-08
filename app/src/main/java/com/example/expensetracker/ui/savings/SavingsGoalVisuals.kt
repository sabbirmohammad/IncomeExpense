package com.example.expensetracker.ui.savings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Flight
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Laptop
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Smartphone
import androidx.compose.material.icons.outlined.Star
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.expensetracker.theme.ColorAccent
import java.util.Locale

/**
 * Maps string icon keys to Compose ImageVector
 */
fun getSavingsGoalIcon(key: String): ImageVector {
    return when (key.lowercase(Locale.ROOT)) {
        "star" -> Icons.Outlined.Star
        "car" -> Icons.Outlined.DirectionsCar
        "flight" -> Icons.Outlined.Flight
        "home" -> Icons.Outlined.Home
        "phone" -> Icons.Outlined.Smartphone
        "laptop" -> Icons.Outlined.Laptop
        "school" -> Icons.Outlined.School
        "heart" -> Icons.Outlined.Favorite
        "gift" -> Icons.Outlined.CardGiftcard
        else -> Icons.Outlined.Savings
    }
}

/**
 * Parses color hex safely, with fallback to default theme accent
 */
fun parseGoalColor(hex: String): Color {
    return try {
        Color(android.graphics.Color.parseColor(hex))
    } catch (e: Exception) {
        ColorAccent
    }
}

internal val AVAILABLE_GOAL_ICONS = listOf(
    "savings" to Icons.Outlined.Savings,
    "star" to Icons.Outlined.Star,
    "car" to Icons.Outlined.DirectionsCar,
    "flight" to Icons.Outlined.Flight,
    "home" to Icons.Outlined.Home,
    "phone" to Icons.Outlined.Smartphone,
    "laptop" to Icons.Outlined.Laptop,
    "school" to Icons.Outlined.School,
    "heart" to Icons.Outlined.Favorite,
    "gift" to Icons.Outlined.CardGiftcard
)

internal val AVAILABLE_GOAL_COLORS = listOf(
    "#3B82F6", // Blue
    "#10B981", // Emerald
    "#8B5CF6", // Purple
    "#F59E0B", // Amber
    "#EC4899", // Pink
    "#06B6D4", // Cyan
    "#EF4444"  // Coral Red
)
