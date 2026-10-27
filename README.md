# Expense Tracker (হিসাব কিতাব) 💰

[![Android](https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white)](https://developer.android.com/)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0+-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose%20%26%20Material%203-4285F4?logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Min SDK](https://img.shields.io/badge/Min%20SDK-24-blue)](https://developer.android.com/)
[![Target SDK](https://img.shields.io/badge/Target%20SDK-35-green)](https://developer.android.com/)

A modern, privacy-focused, and 100% offline personal finance manager for Android. Built entirely with **Jetpack Compose** and **Material 3**, featuring offline receipt image attachments, comprehensive analytics, and personalized budgeting.

---

## 🌟 Key Features

### 📊 Comprehensive Financial Management
- **Dashboard & Summary**: Real-time overview of monthly income, expenses, and total net balance.
- **Multi-Account & Wallet Support**: Manage Cash, Bank accounts, and Mobile Financial Services (bKash, Nagad, Rocket, etc.) with seamless transfers.
- **Category-wise Tracking**: Predefined and custom categories with personalized icons and colors.
- **Transaction Filters**: Filter by date range, account, type (Income, Expense, Transfer), and category.
- **Offline Receipt Attachment**: Capture photos using your camera or pick receipts from device storage, stored locally and securely.

### 🎯 Planning & Obligations
- **Monthly Category Budgets**: Set spending limits per category with visual progress indicators and overrun alerts.
- **Savings Goals**: Set target amounts and target dates, track milestone progress, and calculate projected completion dates.
- **Recurring Bills & Subscriptions**: Track due dates, mark bills paid, and receive scheduled reminder notifications via WorkManager.
- **Receivables & Payables (ধার / দেনা)**: Track money lent to or borrowed from others and log partial payments with clear history.

### 🛒 Market & Grocery Tracker
- **Bazar Checklist**: Interactive market list with itemized quantities, estimated prices, and real-time expense calculations.
- **Bill Splitter**: Quickly calculate split shares among friends or roommates.

### 📈 Reports & Data Export
- **Visual Analytics**: Interactive Donut charts, daily spending bar charts, and payment method distribution.
- **Export Statements**: Generate branded PDF financial statements or shareable image slips.
- **Cloud Backup & Restore**: Backup database securely to the user's private Google Drive AppData folder with optional daily auto-sync.

### 🌐 Localization & Design
- **Bilingual Support**: Full English and Bengali (বাংলা) language options.
- **Bengali Numeral Support**: Optional display of all numbers and amounts in Bengali digits (১, ২, ৩...).
- **One UI Inspired Aesthetics**: Clean cards, rounded corners, fluid animations, and complete Light & Dark theme support.

---

## 🛠️ Architecture & Tech Stack

- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) with Material 3 Design
- **Navigation**: Modern Jetpack Compose Navigation 3 with 3D Scaffold Drawer & Clean Bottom Navigation
  - **Single Source of Navigation**: Bottom Navigation Bar (`ModernBottomNavBar`) handles primary navigation: `Home`, `Transactions`, `+ (Add)`, `Analytics`, and `More` (Menu).
  - **Drawer Access**: The 3D side drawer is exclusively opened via the bottom `More` menu or edge gestures.
  - **No Header Menu Duplication**: Screen headers omit duplicate hamburger buttons. Primary screens feature uncluttered One UI headers; secondary screens (`Accounts`, `Bills`, `Receivables`, `Budgets`, `Reports`, `Settings`) provide standard back-arrow (`←`) navigation returning to Home.
- **Language**: [Kotlin](https://kotlinlang.org/) with Coroutines & StateFlow
- **Database**: Optimized SQLite Database Helper architecture with modular table delegates
- **Background Tasks**: [AndroidX WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager) for automated Google Drive sync
- **Preferences**: [DataStore Preferences](https://developer.android.com/topic/libraries/architecture/datastore)
- **Serialization**: [Kotlinx Serialization JSON](https://github.com/Kotlin/kotlinx.serialization)
- **Cloud Services**: [Google Drive REST API](https://developers.google.com/drive) & Google Sign-In (AppData folder scope)
- **Build System**: Gradle Version Catalog (`libs.versions.toml`) & Gradle Kotlin DSL (`build.gradle.kts`)

---

## 📂 Project Structure

```
ExpenseTracker/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/expensetracker/
│   │   │   │   ├── data/           # Database, Models, Repositories, & Drive services
│   │   │   │   ├── ui/             # Jetpack Compose Screens, ViewModels & Components
│   │   │   │   │   ├── accounts/   # Multi-account management
│   │   │   │   │   ├── analytics/  # Visual charts & Reports
│   │   │   │   │   ├── bills/      # Recurring bills & Subscriptions
│   │   │   │   │   ├── budget/     # Category budgets & Savings goals
│   │   │   │   │   ├── dashboard/  # Main overview screen
│   │   │   │   │   ├── market/     # Market / Bazar checklist
│   │   │   │   │   ├── payables/   # Debt & loan tracking
│   │   │   │   │   ├── settings/   # Profile, Drive backup, & themes
│   │   │   │   │   └── transactions/# Add/Edit transactions & receipt attachments
│   │   │   │   └── utils/          # Formatting, PDF export, Image helpers
│   │   │   └── res/                # XML Resources, Drawables, Mipmaps, Strings
│   │   └── test/                   # Unit & logic test suites
│   ├── build.gradle.kts
│   └── proguard-rules.pro
├── gradle/
│   ├── libs.versions.toml          # Centralized dependency catalog
│   └── wrapper/                    # Gradle wrapper files
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
└── .gitignore
```

---

## 🚀 Getting Started

### Prerequisites
- **Android Studio**: Android Studio Ladybug (2024.2+) or newer
- **JDK**: Java 17 or higher
- **Android Device / Emulator**: Android 7.0 (API level 24) or higher

### Building & Running
1. Clone the repository:
   ```bash
   git clone https://github.com/sabbirmohammad/IncomeExpense.git
   cd IncomeExpense
   ```

2. Open the project in Android Studio.

3. Build the project using Gradle:
   ```bash
   ./gradlew assembleDebug
   ```

4. Run the unit tests:
   ```bash
   ./gradlew testDebugUnitTest
   ```

5. Install on a connected Android device or emulator:
   ```bash
   ./gradlew installDebug
   ```

---

## 🔒 Privacy & Security

- **100% Local Storage**: All financial records, accounts, receipts, and transactions are stored locally on your device in a private SQLite database.
- **Zero Third-Party Data Tracking**: No telemetry or third-party tracking scripts are bundled.
- **Secure Cloud Backup**: Uses Google Drive's restricted `DRIVE_APPDATA` scope, meaning the app can only read and write files within its own private application folder on your Google Drive.

---

## 📄 License

This project is open-source and available under the standard project terms.
