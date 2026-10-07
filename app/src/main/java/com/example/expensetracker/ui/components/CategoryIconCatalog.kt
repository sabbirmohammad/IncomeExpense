package com.example.expensetracker.ui.components

data class CategoryIconItem(
    val name: String,
    val group: String,
    val label: String
)

object CategoryIconCatalog {

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
}
