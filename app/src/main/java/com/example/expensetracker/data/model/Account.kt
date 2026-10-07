package com.example.expensetracker.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class AccountType(val displayName: String) {
    BANK("Bank Account"),
    MFS("MFS / Mobile Wallet"),
    CASH("Cash");

    fun getDisplayName(isBangla: Boolean): String {
        return if (isBangla) {
            when (this) {
                BANK -> "ব্যাংক অ্যাকাউন্ট"
                MFS -> "মোবাইল ওয়ালেট (MFS)"
                CASH -> "নগদ ক্যাশ"
            }
        } else {
            displayName
        }
    }
}

@Serializable
data class Account(
    val id: Long = 0,
    val name: String,
    val type: AccountType,
    val accountNumber: String = "",
    val institutionName: String = "",
    val initialBalance: Double = 0.0,
    val currentBalance: Double = 0.0,
    val colorHex: String = "#3B8FF0",
    val isDefault: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun getLocalizedName(isBangla: Boolean): String {
        return getLocalizedAccountName(name, isBangla)
    }
}

fun getLocalizedAccountName(accountName: String?, isBangla: Boolean): String {
    if (accountName.isNullOrBlank()) return if (isBangla) "অ্যাকাউন্ট" else "Account"
    val trimmed = accountName.trim()
    if (isBangla) {
        return when (trimmed) {
            "Cash in Hand", "Cash", "cash" -> "নগদ ক্যাশ"
            "Bank Account", "Bank", "bank" -> "ব্যাংক অ্যাকাউন্ট"
            "bKash Personal", "bKash", "bkash" -> "বিকাশ ওয়ালেট"
            "Nagad Personal", "Nagad", "nagad" -> "নগদ ওয়ালেট"
            "Rocket Personal", "Rocket", "rocket" -> "রকেট ওয়ালেট"
            "Upay Personal", "Upay", "upay" -> "উপায় ওয়ালেট"
            "City Bank" -> "সিটি ব্যাংক"
            "Dutch Bangla Bank", "DBBL" -> "ডাচ বাংলা ব্যাংক"
            "BRAC Bank", "Brac Bank" -> "ব্র্যাক ব্যাংক"
            "Islami Bank", "IBBL" -> "ইসলামী ব্যাংক"
            else -> accountName
        }
    } else {
        return when (trimmed) {
            "নগদ ক্যাশ", "ক্যাশ", "নগদ টাকা", "ক্যাশ ইন হ্যান্ড" -> "Cash in Hand"
            "ব্যাংক অ্যাকাউন্ট", "ব্যাংক" -> "Bank Account"
            "বিকাশ ওয়ালেট", "বিকাশ" -> "bKash Personal"
            "নগদ ওয়ালেট", "নগদ (MFS)" -> "Nagad Personal"
            "রকেট ওয়ালেট", "রকেট" -> "Rocket Personal"
            "উপায় ওয়ালেট", "উপায়" -> "Upay Personal"
            "সিটি ব্যাংক" -> "City Bank"
            "ডাচ বাংলা ব্যাংক" -> "Dutch Bangla Bank"
            "ব্র্যাক ব্যাংক" -> "BRAC Bank"
            "ইসলামী ব্যাংক" -> "Islami Bank"
            else -> accountName
        }
    }
}
