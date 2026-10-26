package com.example.expensetracker.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import com.example.expensetracker.data.model.AppLanguage

class Strings(
    isBangla: Boolean = false
) : StringsFeatures(isBangla) {
    // Settings & Preferences
    val settingsTitle: String get() = if (isBangla) "সেটিংস" else "Settings"
    val profileSection: String get() = if (isBangla) "ইউজার প্রোফাইল" else "User Profile"
    val languageSection: String get() = if (isBangla) "ভাষা পরিবর্তন" else "Language"
    val selectLanguage: String get() = if (isBangla) "অ্যাপের ভাষা নির্বাচন করুন" else "Select App Language"
    val banglaLang: String get() = if (isBangla) "বাংলা" else "বাংলা"
    val englishLang: String get() = if (isBangla) "English" else "English"
    val currencySection: String get() = if (isBangla) "মুদ্রা ও চিহ্ন" else "Currency & Symbol"
    val backupRestoreSection: String get() = if (isBangla) "ব্যাকআপ ও রিস্টোর" else "Backup & Restore"
    val backupToDrive: String get() = if (isBangla) "গুগল ড্রাইভে অটো ব্যাকআপ" else "Auto Backup to Google Drive"
    val googleDriveBackup: String get() = if (isBangla) "গুগল ড্রাইভ ব্যাকআপ" else "Google Drive backup"
    val googleDriveBackupDesc: String get() = if (isBangla) "ক্লাউডে আপনার আর্থিক লেনদেনের ডেটা নিরাপদে সংরক্ষণ ও পুনরুদ্ধার করুন।" else "Securely backup and restore your financial data with Google Drive cloud storage."
    val connected: String get() = if (isBangla) "সংযুক্ত" else "Connected"
    val disconnect: String get() = if (isBangla) "বিচ্ছিন্ন করুন" else "Disconnect"
    val connectGoogleDrive: String get() = if (isBangla) "গুগল ড্রাইভ যুক্ত করুন" else "Connect Google Drive"
    val backupNow: String get() = if (isBangla) "এখনই ব্যাকআপ নিন" else "Back up now"
    val restoreFromDrive: String get() = if (isBangla) "ড্রাইভ থেকে রিস্টোর" else "Restore from Drive"
    val autoCloudSync: String get() = if (isBangla) "স্বয়ংক্রিয় ক্লাউড সিঙ্ক" else "Automatic cloud sync"
    val autoCloudSyncDesc: String get() = if (isBangla) "প্রতিদিন রাতে স্বয়ংক্রিয়ভাবে ড্রাইভে ডেটা ব্যাকআপ হবে।" else "Sync data automatically once a day in the background."
    val lastSyncStatus: String get() = if (isBangla) "সর্বশেষ সিঙ্কের বিবরণ" else "Last sync status"
    val notSyncedYet: String get() = if (isBangla) "এখনও সিঙ্ক করা হয়নি" else "Not synced yet"
    val exportData: String get() = if (isBangla) "ডেটা এক্সপোর্ট" else "Export Data"
    val importData: String get() = if (isBangla) "ডেটা ইমপোর্ট" else "Import Data"
    val darkTheme: String get() = if (isBangla) "ডার্ক মোড" else "Dark Theme"
    val settingsSubtitle: String get() = if (isBangla) "পছন্দসমূহ, ক্যাটাগরি ও ব্যাকআপ" else "Preferences, categories, and backups"
    val selectLanguageSubtitle: String get() = if (isBangla) "আপনার পছন্দের ভাষা নির্বাচন করুন" else "Select your preferred application language"
    val analyticsTitle: String get() = if (isBangla) "অ্যানালিটিক্স" else "Analytics"
    val analyticsSubtitle: String get() = if (isBangla) "আর্থিক বিশ্লেষণ ও পর্যালোচনা" else "Financial breakdown and insights"
    val budgetTitle: String get() = if (isBangla) "বাজেট" else "Budgets"
    val budgetSubtitle: String get() = if (isBangla) "চলতি মাসের খরচের সীমা নিয়ন্ত্রণ করুন" else "Manage your spending limits for this month"

    // Upcoming Month Budget Prompt & Notification
    val upcomingBudgetPromptTitle: String get() = if (isBangla) "নতুন মাসের বাজেট প্ল্যানিং" else "Upcoming Month Budget"
    val upcomingBudgetPromptDesc: String get() = if (isBangla) "আগামীকাল থেকে নতুন মাস শুরু হচ্ছে। আপনি কি চলতি মাসের বাজেটটিই নতুন মাসে বহাল রাখতে চান, নাকি নতুন বাজেট নির্ধারণ করবেন?" else "Tomorrow a new month begins. Would you like to keep this month's budget for the new month, or set a new budget?"
    val keepPreviousBudget: String get() = if (isBangla) "প্রিভিয়াস বাজেটেই বহাল রাখুন" else "Keep Previous Budget"
    val setNewBudget: String get() = if (isBangla) "নতুন বাজেট নির্ধারণ করুন" else "Set New Budget"
    val notNow: String get() = if (isBangla) "এখন নয়" else "Not Now"
    val budgetCopiedSuccess: String get() = if (isBangla) "নতুন মাসের বাজেট সফলভাবে সেট করা হয়েছে!" else "New month budget applied successfully!"
    val currentMonthBudgetLabel: String get() = if (isBangla) "চলতি মাসের নির্ধারিত বাজেট" else "Current Month Budget"
    val categoriesConfiguredCount: String get() = if (isBangla) "টি ক্যাটাগরির বাজেট সক্রিয়" else "categories budgeted"
    val accountsSubtitle: String get() = if (isBangla) "ব্যাংক, এমএফএস ও নগদ টাকার হিসাব" else "Banks, MFS & Cash Management"
    val transactionsSubtitle: String get() = if (isBangla) "রেকর্ড পাওয়া গেছে" else "records found"

    // Multi-Theme & Bengali Numerals
    val themeSettingTitle: String get() = if (isBangla) "থিম নির্বাচন" else "App Theme"
    val themeSettingSubtitle: String get() = if (isBangla) "ডার্ক, লাইট বা পিওর ব্ল্যাক AMOLED থিম পছন্দ করুন" else "Choose Dark, Light, or Pure Black AMOLED theme"
    val themeSystem: String get() = if (isBangla) "সিস্টেম" else "System"
    val themeDark: String get() = if (isBangla) "ডার্ক" else "Dark"
    val themeLight: String get() = if (isBangla) "লাইট" else "Light"
    val themeAmoled: String get() = if (isBangla) "পিওর ব্ল্যাক" else "AMOLED"
    val bengaliNumeralsTitle: String get() = if (isBangla) "বাংলা সংখ্যা (১২৩৪)" else "Bengali Numerals (১২৩৪)"
    val bengaliNumeralsSubtitle: String get() = if (isBangla) "টাকার পরিমাণ বাংলা সংখ্যায় দেখান" else "Display amounts with Bengali digits"

    // Discard Guard
    val discardDialogTitle: String get() = if (isBangla) "পরিবর্তন বাতিল করবেন?" else "Discard changes?"
    val discardDialogMessage: String get() = if (isBangla) "আপনার পূরণ করা তথ্য মুছে যাবে। আপনি কি নিশ্চিত?" else "You have unsaved changes. Are you sure you want to discard them?"
    val keepEditing: String get() = if (isBangla) "লিখতে থাকুন" else "Keep Editing"
    val discard: String get() = if (isBangla) "বাতিল করুন" else "Discard"

    // Undo Actions
    val undo: String get() = if (isBangla) "পূর্বাবস্থায় ফেরান" else "Undo"
    val transactionDeleted: String get() = if (isBangla) "লেনদেন মুছে ফেলা হয়েছে" else "Transaction deleted"
    val transactionRestored: String get() = if (isBangla) "লেনদেন পুনরুদ্ধার করা হয়েছে" else "Transaction restored"

    // Account Balance Reconciliation
    val reconcileBalance: String get() = if (isBangla) "ব্যালেন্স সমন্বয় করুন" else "Reconcile Balance"
    val actualCurrentBalance: String get() = if (isBangla) "প্রকৃত বর্তমান ব্যালেন্স" else "Actual Current Balance"
    val balanceDifference: String get() = if (isBangla) "পার্থক্য" else "Difference"
    val reconcileSuccess: String get() = if (isBangla) "ব্যালেন্স সফলভাবে সমন্বয় করা হয়েছে" else "Account balance reconciled successfully"
    val reconciliationNote: String get() = if (isBangla) "ব্যালেন্স সমন্বয় এন্ট্রি" else "Balance adjustment entry"

    // Custom Date Range & Filters
    val customDateRange: String get() = if (isBangla) "কাস্টম সময়কাল" else "Custom Range"
    val selectDateRange: String get() = if (isBangla) "তারিখ নির্বাচন করুন" else "Select Date Range"
    val startDate: String get() = if (isBangla) "শুরুর তারিখ" else "Start Date"
    val endDate: String get() = if (isBangla) "শেষের তারিখ" else "End Date"

    // Category Selector
    val searchCategories: String get() = if (isBangla) "ক্যাটাগরি খুঁজুন..." else "Search categories..."
    val frequentCategories: String get() = if (isBangla) "জনপ্রিয় ক্যাটাগরি" else "Frequent Categories"

    // Reports & Statements
    val navReports: String get() = if (isBangla) "রিপোর্ট ও স্টেটমেন্ট" else "Reports & Statements"
    val reportsTitle: String get() = if (isBangla) "আর্থিক রিপোর্ট ও বিবরণী" else "Financial Reports & Statements"
    val reportsSubtitle: String get() = if (isBangla) "যেকোনো নির্দিষ্ট সময়ের বিস্তারিত হিসাব বিবরণী PDF ও Excel এ এক্সপোর্ট করুন" else "Export detailed statements in PDF & Excel for any period"
    val viewPdf: String get() = if (isBangla) "PDF সরাসরি দেখুন" else "Open PDF"
    val shareReport: String get() = if (isBangla) "রিপোর্ট শেয়ার করুন" else "Share Report"
    val generatingReport: String get() = if (isBangla) "রিপোর্ট প্রস্তুত হচ্ছে..." else "Generating report..."
    val reportGeneratedSuccess: String get() = if (isBangla) "রিপোর্ট সফলভাবে প্রস্তুত হয়েছে!" else "Report generated successfully!"
    val selectPeriod: String get() = if (isBangla) "সময়কাল নির্বাচন" else "Select Period"
    val filterByAccount: String get() = if (isBangla) "অ্যাকাউন্ট অনুযায়ী" else "Filter by Account"
    val filterByType: String get() = if (isBangla) "লেনদেনের ধরণ" else "Transaction Type"
    val allAccounts: String get() = if (isBangla) "সব অ্যাকাউন্ট" else "All Accounts"
    val allTypes: String get() = if (isBangla) "সব ধরণ" else "All Types"
    val onlyExpenses: String get() = if (isBangla) "শুধু খরচ" else "Only Expenses"
    val onlyIncomes: String get() = if (isBangla) "শুধু আয়" else "Only Incomes"
    val totalIncomeLabel: String get() = if (isBangla) "মোট আয়" else "Total Income"
    val totalExpenseLabel: String get() = if (isBangla) "মোট ব্যয়" else "Total Expense"
    val netCashFlowLabel: String get() = if (isBangla) "নিট উদ্বৃত্ত" else "Net Cash Flow"
    val savingsRateLabel: String get() = if (isBangla) "সঞ্চয়ের হার" else "Savings Rate"
    val categoryBreakdown: String get() = if (isBangla) "ক্যাটাগরি অনুযায়ী ব্যয়" else "Category Breakdown"
    val transactionsInReport: String get() = if (isBangla) "স্টেটমেন্টের লেনদেনসমূহ" else "Transactions in Statement"
    val noTransactionsForPeriod: String get() = if (isBangla) "এই সময়ের কোনো লেনদেন পাওয়া যায়নি" else "No transactions found for this period"
    val totalRecords: String get() = if (isBangla) "মোট রেকর্ড" else "Total Records"

    // Privacy Mode
    val hideBalance: String get() = if (isBangla) "ব্যালেন্স লুকান" else "Hide Balance"
    val showBalance: String get() = if (isBangla) "ব্যালেন্স দেখুন" else "Show Balance"
    val privacyModeActive: String get() = if (isBangla) "গোপনীয়তা মোড সক্রিয়" else "Privacy Mode Active"

    // Restore dialog action
    val restoreNow: String get() = if (isBangla) "এখনই রিস্টোর করুন" else "Restore now"

    // Category Unique Color Strings
    val selectUniqueColor: String get() = if (isBangla) "ইউনিক রঙ নির্বাচন করুন" else "Select unique color"
    val uniqueColorAssigned: String get() = if (isBangla) "ইউনিক রঙ বরাদ্দ করা হয়েছে" else "Unique color allocated"
    val colorAlreadyUsed: String get() = if (isBangla) "এই রঙটি ইতিমধ্যে অন্য ক্যাটাগরিতে ব্যবহৃত হয়েছে" else "This color is already used by another category"
    fun colorUsedByFormat(categoryName: String): String = if (isBangla) "এই রঙটি '$categoryName' ক্যাটাগরিতে ব্যবহৃত হচ্ছে" else "This color is used by '$categoryName'"
    val pickNextColor: String get() = if (isBangla) "অন্য ইউনিক রঙ দিন" else "Pick next color"
    fun availableColorsCount(count: Int): String = if (isBangla) "$count টি উপলব্ধ রঙ" else "$count available colors"
    val colorMustBeUnique: String get() = if (isBangla) "প্রতিটি ক্যাটাগরির জন্য আলাদা ও ইউনিক রঙ থাকা আবশ্যক" else "Each category must have a unique color"

    // Profile & Settings Additional Strings
    val profileUpdatedSuccess: String get() = if (isBangla) "প্রোফাইল সফলভাবে আপডেট করা হয়েছে" else "Profile updated successfully"
    val langSwitchedBangla: String get() = if (isBangla) "ভাষা পরিবর্তন করা হয়েছে: বাংলা" else "Language switched to Bangla"
    val langSwitchedEnglish: String get() = if (isBangla) "ভাষা পরিবর্তন করা হয়েছে: ইংরেজি" else "Language switched to English"
    val googleDriveRestoreTitle: String get() = if (isBangla) "গুগল ড্রাইভ থেকে রিস্টোর করবেন?" else "Restore from Google Drive?"
    val googleDriveRestoreDesc: String get() = if (isBangla) "এটি আপনার স্থানীয় ডেটা গুগল ড্রাইভ ব্যাকআপ দিয়ে প্রতিস্থাপন করবে।" else "This will overwrite your local data with your latest Google Drive backup."
    val egFirstName: String get() = if (isBangla) "উদাঃ সাব্বির" else "e.g. Sabbir"
    val egLastName: String get() = if (isBangla) "উদাঃ আহমেদ" else "e.g. Ahmed"
    val editProfileTitle: String get() = if (isBangla) "প্রোফাইল সম্পাদনা" else "Edit profile"
    val firstNameLabel: String get() = if (isBangla) "নামের প্রথম অংশ *" else "First name *"
    val lastNameLabel: String get() = if (isBangla) "নামের শেষ অংশ (ঐচ্ছিক)" else "Last name"
    val chooseAvatarLabel: String get() = if (isBangla) "অ্যাভাটার আইকন নির্বাচন করুন" else "Choose avatar icon"
    val dashboardPreviewLabel: String get() = if (isBangla) "ড্যাশবোর্ড প্রিভিউ" else "Dashboard preview"
    fun previewGreeting(name: String): String = if (isBangla) "হ্যালো, $name" else "Hello, $name"
}

val BanglaStrings = Strings(isBangla = true)
val EnglishStrings = Strings(isBangla = false)

val LocalAppStrings = compositionLocalOf { BanglaStrings }

@Composable
fun rememberAppStrings(language: AppLanguage): Strings {
    return if (language == AppLanguage.ENGLISH) EnglishStrings else BanglaStrings
}
