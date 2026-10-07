package com.example.expensetracker.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.AccountType

data class AccountBrandStyle(
    val brandName: String,
    val gradient: Brush,
    val primaryColor: Color,
    val secondaryColor: Color,
    val badgeBackground: Color,
    val badgeTextColor: Color,
    val icon: ImageVector,
    val isDarkCard: Boolean = true
)

object BrandIdentityHelper {

    fun getBrandStyle(account: Account): AccountBrandStyle {
        val searchString = "${account.name} ${account.institutionName}".lowercase()

        return when {
            // bKash (বিকাশ)
            searchString.contains("bkash") || searchString.contains("বিকাশ") -> {
                AccountBrandStyle(
                    brandName = "bKash",
                    gradient = Brush.linearGradient(
                        colors = listOf(Color(0xFFE2136E), Color(0xFFA8084D))
                    ),
                    primaryColor = Color(0xFFE2136E),
                    secondaryColor = Color(0xFFA8084D),
                    badgeBackground = Color.White.copy(alpha = 0.25f),
                    badgeTextColor = Color.White,
                    icon = Icons.Filled.PhoneAndroid
                )
            }

            // Nagad (নগদ)
            searchString.contains("nagad") || searchString.contains("নগদ") -> {
                AccountBrandStyle(
                    brandName = "Nagad",
                    gradient = Brush.linearGradient(
                        colors = listOf(Color(0xFFF7941D), Color(0xFFE02B20))
                    ),
                    primaryColor = Color(0xFFF7941D),
                    secondaryColor = Color(0xFFE02B20),
                    badgeBackground = Color.White.copy(alpha = 0.25f),
                    badgeTextColor = Color.White,
                    icon = Icons.Filled.PhoneAndroid
                )
            }

            // Rocket (রকেট)
            searchString.contains("rocket") || searchString.contains("রকেট") -> {
                AccountBrandStyle(
                    brandName = "Rocket",
                    gradient = Brush.linearGradient(
                        colors = listOf(Color(0xFF8C3494), Color(0xFF551A5A))
                    ),
                    primaryColor = Color(0xFF8C3494),
                    secondaryColor = Color(0xFF551A5A),
                    badgeBackground = Color.White.copy(alpha = 0.25f),
                    badgeTextColor = Color.White,
                    icon = Icons.Filled.PhoneAndroid
                )
            }

            // Upay (উপায়)
            searchString.contains("upay") || searchString.contains("উপায়") || searchString.contains("উপায়") -> {
                AccountBrandStyle(
                    brandName = "Upay",
                    gradient = Brush.linearGradient(
                        colors = listOf(Color(0xFFFFB800), Color(0xFFD48800))
                    ),
                    primaryColor = Color(0xFFFFB800),
                    secondaryColor = Color(0xFFD48800),
                    badgeBackground = Color.Black.copy(alpha = 0.20f),
                    badgeTextColor = Color.Black,
                    icon = Icons.Filled.PhoneAndroid,
                    isDarkCard = false
                )
            }

            // City Bank (সিটি ব্যাংক)
            searchString.contains("city") || searchString.contains("সিটি") -> {
                AccountBrandStyle(
                    brandName = "City Bank",
                    gradient = Brush.linearGradient(
                        colors = listOf(Color(0xFF0D254C), Color(0xFF1B3D73))
                    ),
                    primaryColor = Color(0xFF1B3D73),
                    secondaryColor = Color(0xFF0D254C),
                    badgeBackground = Color(0xFFFFB800).copy(alpha = 0.3f),
                    badgeTextColor = Color(0xFFFFD166),
                    icon = Icons.Filled.AccountBalance
                )
            }

            // BRAC Bank (ব্র্যাক ব্যাংক)
            searchString.contains("brac") || searchString.contains("ব্র্যাক") -> {
                AccountBrandStyle(
                    brandName = "BRAC Bank",
                    gradient = Brush.linearGradient(
                        colors = listOf(Color(0xFF0054A6), Color(0xFF002A54))
                    ),
                    primaryColor = Color(0xFF0054A6),
                    secondaryColor = Color(0xFF002A54),
                    badgeBackground = Color.White.copy(alpha = 0.25f),
                    badgeTextColor = Color.White,
                    icon = Icons.Filled.AccountBalance
                )
            }

            // Dutch-Bangla Bank (DBBL / ডাচ বাংলা)
            searchString.contains("dutch") || searchString.contains("dbbl") || searchString.contains("ডাচ") -> {
                AccountBrandStyle(
                    brandName = "DBBL",
                    gradient = Brush.linearGradient(
                        colors = listOf(Color(0xFF006633), Color(0xFF003D1E))
                    ),
                    primaryColor = Color(0xFF006633),
                    secondaryColor = Color(0xFF003D1E),
                    badgeBackground = Color.White.copy(alpha = 0.25f),
                    badgeTextColor = Color.White,
                    icon = Icons.Filled.AccountBalance
                )
            }

            // Islami Bank (IBBL / ইসলামী ব্যাংক)
            searchString.contains("islami") || searchString.contains("ibbl") || searchString.contains("ইসলামী") || searchString.contains("ইসলামিক") -> {
                AccountBrandStyle(
                    brandName = "IBBL",
                    gradient = Brush.linearGradient(
                        colors = listOf(Color(0xFF00843D), Color(0xFF004D23))
                    ),
                    primaryColor = Color(0xFF00843D),
                    secondaryColor = Color(0xFF004D23),
                    badgeBackground = Color.White.copy(alpha = 0.25f),
                    badgeTextColor = Color.White,
                    icon = Icons.Filled.AccountBalance
                )
            }

            // Cash in Hand / ক্যাশ
            account.type == AccountType.CASH || searchString.contains("cash") || searchString.contains("ক্যাশ") || searchString.contains("হাতে নগদ") -> {
                AccountBrandStyle(
                    brandName = "Cash",
                    gradient = Brush.linearGradient(
                        colors = listOf(Color(0xFF10B981), Color(0xFF047857))
                    ),
                    primaryColor = Color(0xFF10B981),
                    secondaryColor = Color(0xFF047857),
                    badgeBackground = Color.White.copy(alpha = 0.25f),
                    badgeTextColor = Color.White,
                    icon = Icons.Filled.Payments
                )
            }

            // Default Custom Card from account colorHex
            else -> {
                val parsed = CategoryIconHelper.parseColor(account.colorHex)
                val darker = Color(
                    red = (parsed.red * 0.65f).coerceIn(0f, 1f),
                    green = (parsed.green * 0.65f).coerceIn(0f, 1f),
                    blue = (parsed.blue * 0.65f).coerceIn(0f, 1f),
                    alpha = 1f
                )
                AccountBrandStyle(
                    brandName = if (account.institutionName.isNotBlank()) account.institutionName else account.type.displayName,
                    gradient = Brush.linearGradient(
                        colors = listOf(parsed, darker)
                    ),
                    primaryColor = parsed,
                    secondaryColor = darker,
                    badgeBackground = Color.White.copy(alpha = 0.2f),
                    badgeTextColor = Color.White,
                    icon = when (account.type) {
                        AccountType.BANK -> Icons.Filled.AccountBalance
                        AccountType.MFS -> Icons.Filled.PhoneAndroid
                        AccountType.CASH -> Icons.Filled.Payments
                    }
                )
            }
        }
    }
}
