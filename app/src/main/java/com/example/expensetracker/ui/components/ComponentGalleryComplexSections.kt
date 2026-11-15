package com.example.expensetracker.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.AccountType
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.data.model.TransactionType

@Composable
fun GalleryFeedbackAndStatesSection(isBengali: Boolean) {
    SectionHeader(title = if (isBengali) "৭. স্কেলিটন শিমার (SkeletonBox)" else "7. SkeletonBox & ShimmerList")
    ShimmerList(itemCount = 2)

    SectionHeader(title = if (isBengali) "৮. এম্পটি স্টেট (EmptyState)" else "8. EmptyState with Templates")
    EmptyState(
        title = if (isBengali) "কোনো বিল তালিকাভুক্ত নেই" else "No upcoming bills",
        body = if (isBengali) "দ্রুত যুক্ত করতে নিচের টেমপ্লেট থেকে বেছে নিন" else "Choose a starter template below to track utility bills.",
        buttonText = if (isBengali) "নতুন বিল যোগ করুন" else "Add custom bill",
        onButtonClick = {},
        templates = listOf(
            EmptyStateTemplate(title = if (isBengali) "বিদ্যুৎ" else "Electricity", icon = Icons.Outlined.Bolt, onClick = {}),
            EmptyStateTemplate(title = if (isBengali) "ওয়াইফাই" else "Wi-Fi", icon = Icons.Outlined.Wifi, onClick = {})
        )
    )
}

@Composable
fun GalleryPickersAndInputsSection(isBengali: Boolean) {
    SectionHeader(title = if (isBengali) "৯. টাকার কী-প্যাড (AmountKeypad)" else "9. AmountKeypad (4x4, >=56dp)")
    var keypadExpression by remember { mutableStateOf("120+85") }
    AmountKeypad(
        expression = keypadExpression,
        onExpressionChange = { keypadExpression = it },
        isBangla = isBengali
    )

    SectionHeader(title = if (isBengali) "১০. ক্যাটাগরি চয়নকারী (CategoryPicker)" else "10. CategoryPicker (Inline chips)")
    val galleryCategories = remember {
        listOf(
            Category(1, "Groceries", TransactionType.EXPENSE, "ShoppingCart", "#10B981"),
            Category(2, "Dining", TransactionType.EXPENSE, "Restaurant", "#F97316"),
            Category(3, "Transport", TransactionType.EXPENSE, "DirectionsCar", "#0EA5E9"),
            Category(4, "Rent", TransactionType.EXPENSE, "Home", "#8B5CF6"),
            Category(5, "Bills", TransactionType.EXPENSE, "Receipt", "#F59E0B"),
            Category(6, "Health", TransactionType.EXPENSE, "LocalHospital", "#EC4899")
        )
    }
    var selectedGalleryCategory by remember { mutableStateOf<Category?>(galleryCategories.first()) }
    CategoryPicker(
        categories = galleryCategories,
        selectedCategory = selectedGalleryCategory,
        onCategorySelected = { selectedGalleryCategory = it },
        isBangla = isBengali
    )

    SectionHeader(title = if (isBengali) "১১. অ্যাকাউন্ট রো (AccountChipRow)" else "11. AccountChipRow (With balances)")
    val galleryAccounts = remember {
        listOf(
            Account(1, "Cash in Hand", AccountType.CASH, currentBalance = 3450.0),
            Account(2, "Bank Account", AccountType.BANK, currentBalance = 45000.0),
            Account(3, "bKash Personal", AccountType.MFS, currentBalance = 12500.0)
        )
    }
    var selectedGalleryAccount by remember { mutableStateOf<Account?>(galleryAccounts.first()) }
    AccountChipRow(
        accounts = galleryAccounts,
        selectedAccountId = selectedGalleryAccount?.id,
        onAccountSelected = { selectedGalleryAccount = it },
        isBangla = isBengali
    )
}
