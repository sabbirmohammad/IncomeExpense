package com.example.expensetracker.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.AssignmentReturn
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.outlined.AssignmentReturn
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.expensetracker.ui.components.Strings

enum class NavigationItem(
    val title: String,
    val activeIcon: ImageVector,
    val inactiveIcon: ImageVector
) {
    HOME("Home", Icons.Filled.Home, Icons.Outlined.Home),
    TRANSACTIONS("Transactions", Icons.AutoMirrored.Filled.ReceiptLong, Icons.AutoMirrored.Outlined.ReceiptLong),
    MARKET("Market", Icons.Filled.ShoppingCart, Icons.Outlined.ShoppingCart),
    ANALYTICS("Analytics", Icons.Filled.PieChart, Icons.Outlined.PieChart),
    ACCOUNTS("Accounts & MFS", Icons.Filled.AccountBalance, Icons.Outlined.AccountBalance),
    BILLS("Bills & Subscriptions", Icons.Filled.Payments, Icons.Outlined.Payments),
    RECEIVABLES("Money Owed", Icons.AutoMirrored.Filled.AssignmentReturn, Icons.AutoMirrored.Outlined.AssignmentReturn),
    BUDGETS("Budgets", Icons.Filled.AccountBalanceWallet, Icons.Outlined.AccountBalanceWallet),
    REPORTS("Reports", Icons.Filled.Assessment, Icons.Outlined.Assessment),
    SETTINGS("Settings", Icons.Filled.Settings, Icons.Outlined.Settings);

    val icon: ImageVector get() = inactiveIcon
}

fun NavigationItem.getLocalizedTitle(strings: Strings): String = when (this) {
    NavigationItem.HOME -> strings.navHome
    NavigationItem.TRANSACTIONS -> strings.navTransactions
    NavigationItem.MARKET -> strings.navMarket
    NavigationItem.ANALYTICS -> strings.navAnalytics
    NavigationItem.ACCOUNTS -> strings.navAccounts
    NavigationItem.BILLS -> strings.navBills
    NavigationItem.RECEIVABLES -> strings.navReceivables
    NavigationItem.BUDGETS -> strings.navBudgets
    NavigationItem.REPORTS -> strings.navReports
    NavigationItem.SETTINGS -> strings.navSettings
}
