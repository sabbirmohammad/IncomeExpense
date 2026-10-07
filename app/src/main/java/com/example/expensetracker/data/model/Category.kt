package com.example.expensetracker.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val id: Long = 0,
    val name: String,
    val type: TransactionType,
    val icon: String,
    val colorHex: String,
    val isDefault: Boolean = false
) {
    fun getLocalizedName(isBangla: Boolean): String {
        return getLocalizedCategoryName(name, isBangla)
    }
}

fun getLocalizedCategoryName(categoryName: String, isBangla: Boolean): String {
    val trimmed = categoryName.trim()
    if (isBangla) {
        return when (trimmed) {
            "Food & Dining" -> "খাবার ও রেস্তোরাঁ"
            "Groceries" -> "বাজার ও সদাই"
            "Transportation" -> "যাতায়াত ও ভ্রমণ"
            "Shopping" -> "কেনাকাটা"
            "Housing & Rent" -> "বাসা ভাড়া ও বাসস্থান"
            "Bills & Utilities" -> "বিল ও ইউটিলিটি"
            "Entertainment" -> "বিনোদন"
            "Health & Care" -> "স্বাস্থ্য ও চিকিৎসা"
            "Education" -> "শিক্ষা ও বইপত্র"
            "Travel" -> "ভ্রমণ ও ট্যুর"
            "Loan & Debt Given" -> "ঋণ বা ধার প্রদান"
            "Other Expense" -> "অন্যান্য ব্যয়"
            "Salary" -> "বেতন"
            "Freelance" -> "ফ্রিল্যান্সিং"
            "Business" -> "ব্যবসা ও বাণিজ্য"
            "Investment" -> "বিনিয়োগ ও মুনাফা"
            "Debt Recovery" -> "পাওনা টাকা আদায়"
            "Bonus" -> "বোনাস ও উপহার"
            "Other Income" -> "অন্যান্য আয়"
            "Transfer", "Account Transfer" -> "অ্যাকাউন্ট স্থানান্তর"
            else -> categoryName
        }
    } else {
        return when (trimmed) {
            "খাবার ও রেস্তোরাঁ", "খাবার" -> "Food & Dining"
            "বাজার ও সদাই", "বাজার" -> "Groceries"
            "যাতায়াত ও ভ্রমণ", "যাতায়াত" -> "Transportation"
            "কেনাকাটা" -> "Shopping"
            "বাসা ভাড়া ও বাসস্থান", "বাসা ভাড়া" -> "Housing & Rent"
            "বিল ও ইউটিলিটি" -> "Bills & Utilities"
            "বিনোদন" -> "Entertainment"
            "স্বাস্থ্য ও চিকিৎসা", "চিকিৎসা" -> "Health & Care"
            "শিক্ষা ও বইপত্র", "শিক্ষা" -> "Education"
            "ভ্রমণ ও ট্যুর", "ভ্রমণ" -> "Travel"
            "ঋণ বা ধার প্রদান" -> "Loan & Debt Given"
            "অন্যান্য ব্যয়" -> "Other Expense"
            "বেতন" -> "Salary"
            "ফ্রিল্যান্সিং" -> "Freelance"
            "ব্যবসা ও বাণিজ্য", "ব্যবসা" -> "Business"
            "বিনিয়োগ ও মুনাফা", "বিনিয়োগ" -> "Investment"
            "পাওনা টাকা আদায়" -> "Debt Recovery"
            "বোনাস ও উপহার", "বোনাস" -> "Bonus"
            "অন্যান্য আয়" -> "Other Income"
            "অ্যাকাউন্ট স্থানান্তর" -> "Account Transfer"
            else -> categoryName
        }
    }
}
