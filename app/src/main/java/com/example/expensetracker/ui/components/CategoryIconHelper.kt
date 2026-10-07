package com.example.expensetracker.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.BakeryDining
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.BusinessCenter
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.CastForEducation
import androidx.compose.material.icons.outlined.Chair
import androidx.compose.material.icons.outlined.Checkroom
import androidx.compose.material.icons.outlined.ChildFriendly
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Commute
import androidx.compose.material.icons.outlined.ContentCut
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Diamond
import androidx.compose.material.icons.outlined.DirectionsBus
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.DirectionsSubway
import androidx.compose.material.icons.outlined.Fastfood
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Flight
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Icecream
import androidx.compose.material.icons.outlined.Kitchen
import androidx.compose.material.icons.outlined.Laptop
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.LocalBar
import androidx.compose.material.icons.outlined.LocalCafe
import androidx.compose.material.icons.outlined.LocalGasStation
import androidx.compose.material.icons.outlined.LocalGroceryStore
import androidx.compose.material.icons.outlined.LocalHospital
import androidx.compose.material.icons.outlined.LocalMall
import androidx.compose.material.icons.outlined.LocalPizza
import androidx.compose.material.icons.outlined.LocalTaxi
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material.icons.outlined.Medication
import androidx.compose.material.icons.outlined.MonetizationOn
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Payment
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material.icons.outlined.RamenDining
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.RequestQuote
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.SelfImprovement
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material.icons.outlined.SportsEsports
import androidx.compose.material.icons.outlined.SportsSoccer
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.Store
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material.icons.outlined.TheaterComedy
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material.icons.outlined.Tv
import androidx.compose.material.icons.outlined.TwoWheeler
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material.icons.outlined.Work
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.expensetracker.theme.ColorNeutralCategory

data class CategoryIconItem(
    val name: String,
    val group: String,
    val label: String
)

object CategoryIconHelper {

    val iconGroups = listOf(
        "All",
        "Food",
        "Shopping",
        "Transport",
        "Home",
        "Entertainment",
        "Health",
        "Work",
        "Finance"
    )

    val allIcons: List<CategoryIconItem> = listOf(
        // Food & Dining
        CategoryIconItem("Restaurant", "Food", "Dining"),
        CategoryIconItem("Fastfood", "Food", "Fast Food"),
        CategoryIconItem("LocalCafe", "Food", "Coffee & Tea"),
        CategoryIconItem("LocalBar", "Food", "Drinks"),
        CategoryIconItem("BakeryDining", "Food", "Bakery"),
        CategoryIconItem("LocalPizza", "Food", "Pizza"),
        CategoryIconItem("Icecream", "Food", "Dessert"),
        CategoryIconItem("RamenDining", "Food", "Noodles"),

        // Shopping & Retail
        CategoryIconItem("ShoppingCart", "Shopping", "Groceries"),
        CategoryIconItem("ShoppingBag", "Shopping", "Shopping"),
        CategoryIconItem("Storefront", "Shopping", "Store"),
        CategoryIconItem("LocalGroceryStore", "Shopping", "Supermarket"),
        CategoryIconItem("Checkroom", "Shopping", "Clothing"),
        CategoryIconItem("Diamond", "Shopping", "Jewelry"),
        CategoryIconItem("CardGiftcard", "Shopping", "Gifts"),
        CategoryIconItem("LocalMall", "Shopping", "Mall"),

        // Transport & Travel
        CategoryIconItem("DirectionsCar", "Transport", "Car"),
        CategoryIconItem("Flight", "Transport", "Flight"),
        CategoryIconItem("DirectionsBus", "Transport", "Bus"),
        CategoryIconItem("TwoWheeler", "Transport", "Bike"),
        CategoryIconItem("DirectionsSubway", "Transport", "Metro"),
        CategoryIconItem("LocalGasStation", "Transport", "Fuel"),
        CategoryIconItem("Commute", "Transport", "Commute"),
        CategoryIconItem("LocalTaxi", "Transport", "Taxi"),

        // Home & Utilities
        CategoryIconItem("Home", "Home", "Housing & Rent"),
        CategoryIconItem("Bolt", "Home", "Electricity"),
        CategoryIconItem("WaterDrop", "Home", "Water"),
        CategoryIconItem("Wifi", "Home", "Internet"),
        CategoryIconItem("Tv", "Home", "TV & Cable"),
        CategoryIconItem("Kitchen", "Home", "Appliances"),
        CategoryIconItem("Chair", "Home", "Furniture"),
        CategoryIconItem("Lightbulb", "Home", "Utilities"),
        CategoryIconItem("Build", "Home", "Repairs"),

        // Entertainment & Lifestyle
        CategoryIconItem("Movie", "Entertainment", "Movies & Cinema"),
        CategoryIconItem("SportsEsports", "Entertainment", "Gaming"),
        CategoryIconItem("MusicNote", "Entertainment", "Music"),
        CategoryIconItem("FitnessCenter", "Entertainment", "Gym & Fitness"),
        CategoryIconItem("SportsSoccer", "Entertainment", "Sports"),
        CategoryIconItem("TheaterComedy", "Entertainment", "Events"),
        CategoryIconItem("CameraAlt", "Entertainment", "Photography"),
        CategoryIconItem("Palette", "Entertainment", "Art & Hobby"),

        // Health & Personal Care
        CategoryIconItem("MedicalServices", "Health", "Healthcare"),
        CategoryIconItem("LocalHospital", "Health", "Hospital"),
        CategoryIconItem("Medication", "Health", "Medicine"),
        CategoryIconItem("Spa", "Health", "Spa & Wellness"),
        CategoryIconItem("ContentCut", "Health", "Salon & Grooming"),
        CategoryIconItem("Pets", "Health", "Pets"),
        CategoryIconItem("ChildFriendly", "Health", "Baby & Child"),
        CategoryIconItem("SelfImprovement", "Health", "Wellness"),

        // Work, Tech & Education
        CategoryIconItem("Work", "Work", "Salary & Job"),
        CategoryIconItem("Laptop", "Work", "Freelance & Tech"),
        CategoryIconItem("School", "Work", "Education"),
        CategoryIconItem("BusinessCenter", "Work", "Business"),
        CategoryIconItem("PhoneAndroid", "Work", "Mobile & Gadget"),
        CategoryIconItem("Code", "Work", "Software"),
        CategoryIconItem("Cloud", "Work", "Cloud & Subscriptions"),
        CategoryIconItem("CastForEducation", "Work", "Courses"),

        // Finance & Money
        CategoryIconItem("AttachMoney", "Finance", "Cash & Salary"),
        CategoryIconItem("AccountBalance", "Finance", "Bank & Savings"),
        CategoryIconItem("CreditCard", "Finance", "Credit Card"),
        CategoryIconItem("Savings", "Finance", "Savings"),
        CategoryIconItem("Payments", "Finance", "Payments"),
        CategoryIconItem("TrendingUp", "Finance", "Investments"),
        CategoryIconItem("MonetizationOn", "Finance", "Bonus & Profit"),
        CategoryIconItem("RequestQuote", "Finance", "Invoices & Fees"),
        CategoryIconItem("MoreHoriz", "Finance", "Other")
    )

    val curatedColors: List<String> get() = CategoryColorHelper.distinctColors

    fun getIcon(iconName: String): ImageVector {
        return when (iconName) {
            // Food & Dining
            "Restaurant" -> Icons.Outlined.Restaurant
            "Fastfood" -> Icons.Outlined.Fastfood
            "LocalCafe" -> Icons.Outlined.LocalCafe
            "LocalBar" -> Icons.Outlined.LocalBar
            "BakeryDining" -> Icons.Outlined.BakeryDining
            "LocalPizza" -> Icons.Outlined.LocalPizza
            "Icecream" -> Icons.Outlined.Icecream
            "RamenDining" -> Icons.Outlined.RamenDining

            // Shopping & Retail
            "ShoppingCart" -> Icons.Outlined.ShoppingCart
            "ShoppingBag" -> Icons.Outlined.ShoppingBag
            "Storefront", "Store" -> Icons.Outlined.Storefront
            "LocalGroceryStore" -> Icons.Outlined.LocalGroceryStore
            "Checkroom" -> Icons.Outlined.Checkroom
            "Diamond" -> Icons.Outlined.Diamond
            "CardGiftcard" -> Icons.Outlined.CardGiftcard
            "LocalMall" -> Icons.Outlined.LocalMall

            // Transport & Travel
            "DirectionsCar" -> Icons.Outlined.DirectionsCar
            "Flight" -> Icons.Outlined.Flight
            "DirectionsBus" -> Icons.Outlined.DirectionsBus
            "TwoWheeler" -> Icons.Outlined.TwoWheeler
            "DirectionsSubway" -> Icons.Outlined.DirectionsSubway
            "LocalGasStation" -> Icons.Outlined.LocalGasStation
            "Commute" -> Icons.Outlined.Commute
            "LocalTaxi" -> Icons.Outlined.LocalTaxi

            // Home & Utilities
            "Home" -> Icons.Outlined.Home
            "Bolt" -> Icons.Outlined.Bolt
            "WaterDrop" -> Icons.Outlined.WaterDrop
            "Wifi" -> Icons.Outlined.Wifi
            "Tv" -> Icons.Outlined.Tv
            "Kitchen" -> Icons.Outlined.Kitchen
            "Chair" -> Icons.Outlined.Chair
            "Lightbulb" -> Icons.Outlined.Lightbulb
            "Build" -> Icons.Outlined.Build

            // Entertainment & Lifestyle
            "Movie" -> Icons.Outlined.Movie
            "SportsEsports" -> Icons.Outlined.SportsEsports
            "MusicNote" -> Icons.Outlined.MusicNote
            "FitnessCenter" -> Icons.Outlined.FitnessCenter
            "SportsSoccer" -> Icons.Outlined.SportsSoccer
            "TheaterComedy" -> Icons.Outlined.TheaterComedy
            "CameraAlt" -> Icons.Outlined.CameraAlt
            "Palette" -> Icons.Outlined.Palette

            // Health & Personal Care
            "MedicalServices" -> Icons.Outlined.MedicalServices
            "LocalHospital" -> Icons.Outlined.LocalHospital
            "Medication" -> Icons.Outlined.Medication
            "Spa" -> Icons.Outlined.Spa
            "ContentCut" -> Icons.Outlined.ContentCut
            "Pets" -> Icons.Outlined.Pets
            "ChildFriendly" -> Icons.Outlined.ChildFriendly
            "SelfImprovement" -> Icons.Outlined.SelfImprovement

            // Work, Tech & Education
            "Work" -> Icons.Outlined.Work
            "Laptop" -> Icons.Outlined.Laptop
            "School" -> Icons.Outlined.School
            "BusinessCenter" -> Icons.Outlined.BusinessCenter
            "PhoneAndroid" -> Icons.Outlined.PhoneAndroid
            "Code" -> Icons.Outlined.Code
            "Cloud" -> Icons.Outlined.Cloud
            "CastForEducation" -> Icons.Outlined.CastForEducation

            // Finance & Money
            "AttachMoney", "Cash" -> Icons.Outlined.AttachMoney
            "AccountBalance" -> Icons.Outlined.AccountBalance
            "CreditCard" -> Icons.Outlined.CreditCard
            "Savings" -> Icons.Outlined.Savings
            "Payments", "Payment" -> Icons.Outlined.Payments
            "TrendingUp" -> Icons.Outlined.TrendingUp
            "MonetizationOn" -> Icons.Outlined.MonetizationOn
            "RequestQuote" -> Icons.Outlined.RequestQuote

            // Misc
            "Star" -> Icons.Outlined.Star
            "Favorite" -> Icons.Outlined.Favorite
            "AutoAwesome" -> Icons.Outlined.AutoAwesome
            "ReceiptLong" -> Icons.Outlined.ReceiptLong
            else -> Icons.Outlined.MoreHoriz
        }
    }

    fun parseColor(hex: String): Color {
        return try {
            val cleanHex = if (hex.startsWith("#")) hex.substring(1) else hex
            val colorInt = when (cleanHex.length) {
                6 -> ("FF$cleanHex").toLong(16)
                8 -> cleanHex.toLong(16)
                else -> 0xFF8B90A0
            }
            Color(colorInt)
        } catch (e: Exception) {
            ColorNeutralCategory
        }
    }
}

