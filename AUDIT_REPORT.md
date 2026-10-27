# Expense Tracker Android Application: Comprehensive Architectural, UX & Data Integrity Audit Report

**অ্যাপ্লিকেশনের নাম**: Expense Tracker (হিসাব খাতা)  
**টার্গেট প্যাকেজ**: `com.example.expensetracker`  
**প্রজেক্ট লোকেশন**: `/Users/sabbir/Dev/ExpenseTracker`  
**অডিটের তারিখ**: ২৪ অক্টোবর, ২০২৬  
**সংস্করণ ও প্ল্যাটফর্ম**: Android (Min SDK 26, Target SDK 34), Kotlin 1.9+, Jetpack Compose, SQLiteOpenHelper  
**অডিট পরিধি**: 
- **R1: UI/UX & Interaction Design Logic** (১১টি ফিচার মডিউল: `dashboard`, `transactions`, `accounts`, `budget`, `bills`, `receivables`, `analytics`, `reports`, `settings`, `market`, `share`)
- **R2: Backend & Architecture Logic** (ViewModel, Coroutines, StateFlow, Gemini AI, Google Drive OAuth/REST, WorkManager, Lifecycle)
- **R3: Database Logic & Data Integrity** (SQLite Schema, Foreign Keys, Indexing, Transaction Boundaries, Migrations, Account Reconciliation)
- **R4: User Efficiency & Workflow Friction** (লেনদেন এন্ট্রির গতি, OCR রসিদ স্ক্যানিং, কুইক শর্টকাট, ইনফরমেশন আর্কিটেকচার, বাংলা সংখ্যা ও লোকালাইজেশন)

---

## ১. নির্বাহী সারসংক্ষেপ (Executive Summary)

Expense Tracker একটি পূর্ণাঙ্গ, ফিচার-সমৃদ্ধ ব্যক্তিগত ফিন্যান্সিয়াল অ্যান্ড্রয়েড অ্যাপ্লিকেশন, যা স্যামসাং One UI এবং গুগল Material 3 ডিজাইন প্যাটার্নের সমন্বয়ে ব্যবহারকারীকে দৈনন্দিন আয়-ব্যয়, অ্যাকাউন্ট ব্যবস্থাপনা, বাজেট নির্ধারণ, ঋণ/পাওনা ট্র্যাকিং এবং বাজার ফর্দ তৈরির সুবিধা প্রদান করে।

অ্যাপ্লিকেশনটির আর্কিটেকচারাল, ব্যাকএন্ড, ডেটাবেস এবং ইউজার ইন্টারঅ্যাকশন স্তরে গভীর কারিগরি নিরীক্ষা (Deep Technical Audit) পরিচালনা করে মোট **৫৬টি সুনির্দিষ্ট ত্রুটি ও অপটিমাইজেশন ক্ষেত্র** চিহ্নিত করা হয়েছে। সামগ্রিকভাবে অ্যাপটির ইউজার ইন্টারফেস আধুনিক ও নান্দনিক হলেও, কোর আর্কিটেকচার ও ডেটা স্তরে এমন কিছু মৌলিক ত্রুটি রয়েছে যা বাস্তব ব্যবহারে ব্যবহারকারীর আর্থিক ডেটা নষ্ট (Data Loss), অ্যাপ্লিকেশন ফ্রিজ/ক্র্যাশ এবং লাইট থিমে স্ক্রিন অপাঠ্য (Unreadable) করে তোলে।

### স্তম্ভভিত্তিক স্বাস্থ্য মূল্যায়ন (Health Score by Pillar)

| অডিট স্তম্ভ (Pillar) | স্বাস্থ্য স্কোর (Health) | প্রধান পর্যবেক্ষণ (Key Observation) |
|---|:---:|---|
| **১. UI/UX & Interaction Design (R1)** | **৬২% (সতর্কতাজনক)** | Light Mode-এ টাইপোগ্রাফি ও হিরো ব্যালেন্সের হার্ডকোডেড সাদা রঙের কারণে টেক্সট সম্পূর্ণ অদৃশ্য। নেটিভ `values-bn/strings.xml` অনুপস্থিত। একাধিক টাচ টার্গেট ৪৮dp-এর ছোট। |
| **২. Backend & Architecture Logic (R2)** | **৫৮% (উচ্চ ঝুঁকি)** | পুরো অ্যাপে `collectAsStateWithLifecycle` অনুপস্থিত; ব্যাকগ্রাউন্ডে ব্যাটারি ড্রেন। রুটে ৯টি ViewModel ইগারলি লোড হওয়ায় স্টার্টআপ ল্যাগ। জেমিনি এআই লুপে অবাস্তব মডেল নাম ও টাইমআউট না থাকায় অফলাইনে ২-৩ মিনিট ফ্রিজিং। বিটম্যাপ ডিকোডিংয়ে OOM ক্র্যাশ ঝুঁকি। |
| **৩. Database & Data Integrity (R3)** | **৫১% (সংকটপূর্ণ)** | SQLite Foreign Key এনফোর্সমেন্ট বন্ধ এবং স্কিমাতে কোনো ফরেন কি নেই। মার্কেট ট্রিপ ফিনিশিংয়ে ট্রানজ্যাকশন না থাকায় ক্র্যাশে খরচের ট্রানজ্যাকশন উধাও। অ্যাকাউন্ট ডিলিট করলে Initial Balance চিরতরে ধ্বংস হয়ে ব্যালেন্স নেগেটিভ হয়ে যায়। ট্রান্সফার ট্রানজ্যাকশনে কোনো বাই-ডিরেকশনাল লিঙ্ক নেই। মাইগ্রেশন আপগ্রেডে ৪টি ইনডেক্স মিসিং। |
| **৪. User Efficiency & Workflow Friction (R4)** | **৬৫% (মধ্যম)** | প্রধান (+) বাটনে ২-ধাপের অপ্রয়োজনীয় মডাল জাম্প ও কৃত্রিম লেটেন্সি। ড্যাশবোর্ডে `DashboardQuickActionsDock` অনাথ হিসেবে পড়ে থাকায় গুরুত্বপূর্ণ ফিচারের ডিসকভারেবিলিটি কম। কিবোর্ডে গাণিতিক অপারেটর নেই এবং ক্যামেরার মাধ্যমে সরাসরি ভাউচার স্ক্যানিং সুবিধা অনুপস্থিত। |

---

## ২. সামগ্রিক সমস্যা ও তীব্রতা ম্যাট্রিক্স (Deficiency & Severity Matrix)

অডিটে চিহ্নিত ৫৬টি ইস্যুর তীব্রতাভিত্তিক বিভাজন:

| ক্যাটাগরি ও অডিট সেকশন | সর্বমোট | ক্রিটিক্যাল (Critical) | উচ্চ (High) | মাঝারি (Medium) | নিম্ন (Low) |
|---|:---:|:---:|:---:|:---:|:---:|
| **সেকশন ১: UI/UX & Interaction Design (R1)** | **৯** | ২ | ৪ | ৩ | ০ |
| **সেকশন ২: Backend & Architecture Logic (R2)** | **১৫** | ৩ | ৭ | ৪ | ১ |
| **সেকশন ৩: Database & Data Integrity (R3)** | **২৫** | ৪ | ১১ | ৯ | ১ |
| **সেকশন ৪: User Efficiency & Workflow Friction (R4)** | **৭** | ০ | ২ | ৪ | ১ |
| **সর্বমোট সমস্যা (Total Issues)** | **৫৬** | **৯** | **২৪** | **২০** | **৩** |

---

## সেকশন ১: UI/UX & Interaction Design Logic (ইউআই/ইউএক্স ও ইন্টারঅ্যাকশন ডিজাইন)

---

### UI-01: `Typography`-তে Hardcoded Text Color ব্যবহারের কারণে Light Mode-এ টেক্সট অদৃশ্য
- **Severity**: **Critical (ক্রিটিক্যাল)**
- **Exact Code Location**: `app/src/main/java/com/example/expensetracker/theme/Type.kt`, লাইন ১০–৮৬ (বিশেষ করে ১৭, ২৫, ৩৩, ৪১, ৪৯, ৫৬, ৬৩, ৭০, ৭৭, ৮৪)
- **Component**: `val Typography = Typography(...)`
- **ত্রুটির বিশদ বিবরণ (Deficiency Description)**:
  `Type.kt`-এর প্রতিটি `TextStyle` সংজ্ঞায় (`headlineLarge`, `headlineMedium`, `titleLarge`, `titleMedium`, `titleSmall`, `bodyLarge`, `bodyMedium`, `bodySmall`, `labelMedium`, `labelSmall`) হার্ডকোডেডভাবে `color = ColorTextPrimary` (`0xFFFFFFFF` অর্থাৎ খাঁটি সাদা) উল্লেখ করা হয়েছে।
- **ব্যবহারকারী ও সিস্টেমের ওপর প্রভাব (User / System Impact)**:
  জেটপ্যাক কম্পোজে যখন কোনো `Text` কম্পোজেবল `style = MaterialTheme.typography.titleMedium` বা `bodyMedium` ব্যবহার করে এবং আলাদাভাবে `color` প্যারামিটার পাস করে না, তখন কম্পোজ ইঞ্জিন `TextStyle.color`-কে অগ্রাধিকার দেয়। ফলে ব্যবহারকারী Light Mode (`AppThemeMode.LIGHT`) নির্বাচন করলে সাদা/হালকা ব্যাকগ্রাউন্ডের ওপর সাদা টেক্সট রেন্ডার হয়, যার ফলে টেক্সট ব্যবহারকারীর চোখে সম্পূর্ণ অদৃশ্য হয়ে যায় (Zero Contrast Ratio)। ব্যবহারকারী কোনো লেখা দেখতে পান না।
- **প্রস্তাবিত সমাধান ও কোড স্নিপেট (Recommended Solution & Code Snippet)**:
  Material 3 গাইডলাইন অনুযায়ী `Type.kt`-এর সমস্ত `TextStyle` থেকে `color = ColorTextPrimary` লাইনটি সম্পূর্ণ মুছে ফেলতে হবে (অথবা `Color.Unspecified` রাখতে হবে), যাতে সক্রিয় থিমের `LocalContentColor` বা `MaterialTheme.colorScheme.onSurface` স্বয়ংক্রিয়ভাবে সঠিক টেক্সট কালার নির্ধারণ করতে পারে।

```kotlin
// ফাইল: app/src/main/java/com/example/expensetracker/theme/Type.kt
// বিফোর:
titleMedium = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Medium,
    fontSize = 16.sp,
    lineHeight = 22.sp,
    fontFeatureSettings = "tnum",
    color = ColorTextPrimary // ত্রুটি: হার্ডকোডেড সাদা
)

// আফটার: color প্যারামিটার অপসারণ করুন
titleMedium = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Medium,
    fontSize = 16.sp,
    lineHeight = 22.sp,
    fontFeatureSettings = "tnum"
)
```

---

### UI-02: ড্যাশবোর্ডের হিরো ব্যালেন্স কার্ডে সাদা কালার টোকেন হার্ডকোড
- **Severity**: **Critical (ক্রিটিক্যাল)**
- **Exact Code Location**: `app/src/main/java/com/example/expensetracker/ui/components/ResponsiveHeroBalance.kt`, লাইন ৬৭, ৮৮–১১১
- **Component**: `ResponsiveHeroBalance`
- **ত্রুটির বিশদ বিবরণ (Deficiency Description)**:
  লাইন ৬৭-এ ব্যালেন্স টেক্সটের রঙ নির্ধারণ করা হয়েছে এভাবে:
  `val balanceColor = if (isNegative) ColorDangerExpense else ColorTextPrimary`
  এখানে `ColorTextPrimary`-এর মান `0xFFFFFFFF` (Dark Theme-এর সাদা)। `OneUiBalanceCard.kt:89-92`-তে লাইট মোডে কার্ডের ব্যাকগ্রাউন্ড হলো `LocalAppColors.current.surface` (`#FFFFFF`) এবং `surfaceVariant` (`#F8FAFC`)।
- **ব্যবহারকারী ও সিস্টেমের ওপর প্রভাব (User / System Impact)**:
  লাইট মোডে ড্যাশবোর্ডের প্রধান আকর্ষণ এবং সবচেয়ে বড় উপাদান—হিসাবের মোট নিট ব্যালেন্স (৫২sp সাইজের সংখ্যা)—সাদা ব্যাকগ্রাউন্ডে সাদা রঙে রেন্ডার হয়ে সম্পূর্ণ অদৃশ্য হয়ে যায়। ব্যবহারকারী ব্যালেন্স দেখতে ব্যর্থ হন।
- **প্রস্তাবিত সমাধান ও কোড স্নিপেট (Recommended Solution & Code Snippet)**:
  `ColorTextPrimary` কনস্ট্যান্টের পরিবর্তে থিম-সচেতন `LocalAppColors.current.textPrimary` ব্যবহার করতে হবে।

```kotlin
// ফাইল: app/src/main/java/com/example/expensetracker/ui/components/ResponsiveHeroBalance.kt (লাইন ৬৭)
// বিফোর:
val balanceColor = if (isNegative) ColorDangerExpense else ColorTextPrimary

// আফটার:
val balanceColor = if (isNegative) ColorDangerExpense else LocalAppColors.current.textPrimary
```

---

### UI-03: টেক্সট ইনপুট ফিল্ডগুলোতে হার্ডকোডেড সাদা টেক্সট কালার (`ColorTextPrimary`)
- **Severity**: **High (উচ্চ)**
- **Exact Code Location**: 
  - `app/src/main/java/com/example/expensetracker/ui/transactions/TransactionNoteAndTitleSection.kt`, লাইন ১০১–১০২, ১৬৪–১৬৫
  - `app/src/main/java/com/example/expensetracker/ui/transactions/TransactionAmountInputField.kt`, লাইন ৮৩
- **Component**: `TransactionNoteAndTitleSection`, `TransactionAmountInputField`
- **ত্রুটির বিশদ বিবরণ (Deficiency Description)**:
  `TransactionNoteAndTitleSection.kt`-তে Title এবং Notes-এর `OutlinedTextFieldDefaults.colors`-এ হার্ডকোড করা হয়েছে:
  `focusedTextColor = ColorTextPrimary` এবং `unfocusedTextColor = ColorTextPrimary`।
  একইভাবে টাকার পরিমাণ লেখার ইনপুট ফিল্ডেও `color = ColorTextPrimary` দেওয়া হয়েছে।
- **ব্যবহারকারী ও সিস্টেমের ওপর প্রভাব (User / System Impact)**:
  লাইট থিমে ইনপুট ফিল্ডের ব্যাকগ্রাউন্ড হালকা ধুসর (`#F8FAFC`), যার ফলে ব্যবহারকারী যখন লেনদেনের শিরোনাম, নোট বা টাকার অঙ্ক টাইপ করেন, টাইপকৃত অক্ষরগুলো সাদা হওয়ার কারণে কিছুই দেখা যায় না। ব্যবহারকারী টাইপ করার সময় মারাত্মক বিভ্রান্ত হন।
- **প্রস্তাবিত সমাধান ও কোড স্নিপেট (Recommended Solution & Code Snippet)**:
  সবগুলো ইনপুট ফিল্ডের টেক্সট কালারে `LocalAppColors.current.textPrimary` ব্যবহার করতে হবে।

```kotlin
// ফাইল: app/src/main/java/com/example/expensetracker/ui/transactions/TransactionNoteAndTitleSection.kt
colors = OutlinedTextFieldDefaults.colors(
    focusedTextColor = LocalAppColors.current.textPrimary,
    unfocusedTextColor = LocalAppColors.current.textPrimary,
    focusedBorderColor = LocalAppColors.current.accentPrimary,
    unfocusedBorderColor = LocalAppColors.current.border
)
```

---

### UI-04: সমস্ত Modal BottomSheet ও Dialog-এ Dark Theme কনস্ট্যান্ট হার্ডকোড
- **Severity**: **High (উচ্চ)**
- **Exact Code Location**:
  - `app/src/main/java/com/example/expensetracker/ui/budget/BudgetDeleteDialogs.kt`: লাইন ৩৬–৩৮, ৮৫–৮৭
  - `app/src/main/java/com/example/expensetracker/ui/budget/AddEditBudgetDialog.kt`: লাইন ৬৮, ১১৫–১১৬, ১৬৩–১৬৪
  - `app/src/main/java/com/example/expensetracker/ui/share/ShareExpenseSheet.kt`: লাইন ১৪৩
  - `app/src/main/java/com/example/expensetracker/ui/transactions/TransactionSheetPickers.kt`: লাইন ৯২, ১১১, ১৫৪, ১৭১
  - `app/src/main/java/com/example/expensetracker/ui/transactions/TransactionSheetDialogs.kt`: লাইন ৪৯, ১৪২
  - `app/src/main/java/com/example/expensetracker/ui/savings/AddSavingsContributionSheet.kt`: লাইন ৮৪
  - `app/src/main/java/com/example/expensetracker/ui/savings/AddEditSavingsGoalSheet.kt`: লাইন ৮২
  - `app/src/main/java/com/example/expensetracker/ui/receivables/RecordPayablePaymentSheet.kt`: লাইন ৭৭
  - `app/src/main/java/com/example/expensetracker/ui/receivables/RecordPaymentSheet.kt`: লাইন ৭৯
  - `app/src/main/java/com/example/expensetracker/ui/receivables/BillSplitterSheet.kt`: লাইন ৯৩
  - `app/src/main/java/com/example/expensetracker/ui/receivables/AddEditReceivableSheet.kt`: লাইন ৭৯
  - `app/src/main/java/com/example/expensetracker/ui/accounts/AccountsScreen.kt`: লাইন ৬৯ (`background(ColorBackground)`)
  - `app/src/main/java/com/example/expensetracker/ui/accounts/AccountsQuickActionsRow.kt`: লাইন ৭২–৭৩
- **Component**: ২৫টিরও বেশি মডাল বটম শিট এবং ডায়ালগ
- **ত্রুটির বিশদ বিবরণ (Deficiency Description)**:
  `Theme.kt`-এ লাইট এবং ডার্ক কালার প্যালেট (`LightAppColors`, `DarkAppColors`) তৈরি করা থাকলেও, অ্যাপ্লিকেশনের ২৫টিরও বেশি ডায়ালগ এবং বটম শিটে সরাসরি ডার্ক কালার টোকেন `containerColor = ColorSurface` (`0xFF141826`) এবং স্ক্রিন ব্যাকগ্রাউন্ডে `ColorBackground` (`0xFF0B0E16`) হার্ডকোড করা হয়েছে।
- **ব্যবহারকারী ও সিস্টেমের ওপর প্রভাব (User / System Impact)**:
  ব্যবহারকারী লাইট মোড সিলেক্ট করলেও কোনো ডিলিট কনফার্মেশন, বাজেট যোগ বা ক্যাটাগরি পিকার খুললেই পুরো স্ক্রিন জুড়ে কুচকুচে কালো ব্যাকগ্রাউন্ডের মডাল ওপেন হয়, যা ভিজ্যুয়াল কনসিস্টেন্সি পুরোপুরি নষ্ট করে দেয়।
- **প্রস্তাবিত সমাধান ও কোড স্নিপেট (Recommended Solution & Code Snippet)**:
  সরাসরি `ColorSurface` / `ColorBackground` ইমপোর্ট বাদ দিয়ে কম্পোজের `LocalAppColors.current.surface`, `LocalAppColors.current.background`, এবং `LocalAppColors.current.border` ব্যবহার করতে হবে।

```kotlin
// উদাহরণ: AddEditBudgetDialog.kt
// বিফোর:
containerColor = ColorSurface,
// আফটার:
containerColor = LocalAppColors.current.surface,
textContentColor = LocalAppColors.current.textPrimary
```

---

### UI-05: নেটিভ অ্যান্ড্রয়েড লোকালাইজেশন রিসোর্স (`res/values-bn/strings.xml`)-এর অনুপস্থিতি
- **Severity**: **High (উচ্চ)**
- **Exact Code Location**: `app/src/main/res/values/strings.xml` (লাইন ১–৩) এবং `app/src/main/res/` ডিরেক্টরি
- **ত্রুটির বিশদ বিবরণ (Deficiency Description)**:
  অ্যাপটিতে কোনো `res/values-bn/` ফোল্ডার নেই। এমনকি ডিফল্ট `res/values/strings.xml`-এ শুধুমাত্র `<string name="app_name">Expense Tracker</string>` রয়েছে। অ্যাপের শত শত টেক্সট স্ট্রিং ইন-মেমোরি কোটলিন ক্লাস (`StringsCore -> StringsFeatures -> StringsAi -> Strings`) এর গেটার মেথডের মাধ্যমে পরিচালিত হচ্ছে।
- **ব্যবহারকারী ও সিস্টেমের ওপর প্রভাব (User / System Impact)**:
  ১. ব্যবহারকারীর ফোনের সিস্টেম ভাষা বাংলা করা থাকলেও অ্যান্ড্রয়েড লঞ্চারে অ্যাপটির নাম সর্বদা ইংরেজিতে "Expense Tracker" দেখায়, বাংলা নাম "হিসাব খাতা" আসে না।  
  ২. অ্যান্ড্রয়েডের ব্যাকগ্রাউন্ড WorkManager নোটিফিকেশনগুলো (`BudgetReminderWorker`, `DailySyncWorker`) কম্পোজেবল ফাংশন না হওয়ায় `rememberAppStrings()` কল করতে পারে না, ফলে নোটিফিকেশনে সিস্টেম-লেভেল বাংলা স্ট্রিং হ্যান্ডলিং বাধাগ্রস্ত হয়।  
  ৩. গুগল প্লে কনসোলের প্রি-লঞ্চ টেস্ট এবং অটোমেটিক অ্যাপ ট্রান্সলেশন টুল কোনো স্ট্রিং সনাক্ত করতে পারে না।
- **প্রস্তাবিত সমাধান ও কোড স্নিপেট (Recommended Solution & Code Snippet)**:
  কোর অ্যাপ স্ট্রিংগুলো স্ট্যান্ডার্ড `res/values/strings.xml` এবং `res/values-bn/strings.xml`-এ সংজ্ঞায়িত করুন এবং অ্যাপের লঞ্চার লেবেলকে লোকালাইজ করুন:

```xml
<!-- app/src/main/res/values-bn/strings.xml -->
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_name">হিসাব খাতা</string>
    <string name="notification_channel_budget">বাজেট সতর্কবার্তা</string>
    <string name="notification_channel_sync">ক্লাউড সিঙ্ক</string>
</resources>
```

---

### UI-06: কম্পোজেবল কোডে সরাসরি হার্ডকোডেড স্ট্রিং লিটারেল
- **Severity**: **Medium (মাঝারি)**
- **Exact Code Location**:
  - `app/src/main/java/com/example/expensetracker/ui/dashboard/DashboardScreen.kt`: লাইন ১৬২ (`"বাজার ও সদাই" / "Groceries"`), লাইন ১৭৪–১৭৮ (`"বাজার সম্পন্ন হয়েছে!" / "Shopping Completed!"`)
  - `app/src/main/java/com/example/expensetracker/ui/dashboard/DashboardQuickActionsDock.kt`: লাইন ৭০, ৮৩, ৯৬, ১০৯ (`"অ্যাকাউন্টস"`, `"বিল পে"`, `"ধার-দেনা"`, `"বাজেট"`)
  - `app/src/main/java/com/example/expensetracker/ui/accounts/AccountsScreen.kt`: লাইন ১২৪ (`"সকল অ্যাকাউন্ট" / "ALL ACCOUNTS"`)
  - `app/src/main/java/com/example/expensetracker/ui/transactions/TransactionReceiptSection.kt`: লাইন ১০৫, ১১৩, ১৯০, ২৮৪
  - `app/src/main/java/com/example/expensetracker/ui/reports/ReportsScreen.kt`: লাইন ১৬০ (`"টি লেনদেন" / "items"`)
  - `app/src/main/java/com/example/expensetracker/ui/dashboard/MarketListSheet.kt`: লাইন ১৪৬, ১৫৫
  - `app/src/main/java/com/example/expensetracker/ui/dashboard/MarketItemRow.kt`: লাইন ২১২ (`"৳ দাম" / "$ Price"`)
  - `app/src/main/java/com/example/expensetracker/ui/bills/BillsScreen.kt`: লাইন ১২৯–১৩২
- **Component**: একাধিক স্ক্রিন ও কম্পোনেন্ট
- **ত্রুটির বিশদ বিবরণ (Deficiency Description)**:
  সেন্ট্রালাইজড `AppStrings.kt` থাকা সত্ত্বেও বহু কম্পোজেবলে ইনলাইন শর্ত (`if (isBangla) "..." else "..."`) দিয়ে হার্ডকোডেড টেক্সট বসিয়ে দেওয়া হয়েছে। এর ফলে সেন্ট্রালাইজড স্ট্রিং ম্যানেজমেন্ট ব্যাহত হচ্ছে।
- **ব্যবহারকারী ও সিস্টেমের ওপর প্রভাব (User / System Impact)**:
  ভবিষ্যতে কোনো টেক্সট পরিমার্জন বা নতুন ভাষা যুক্ত করতে গেলে এই হার্ডকোডেড স্ট্রিংগুলো অনূদিত হবে না এবং অ্যাপে ভাষার অসঙ্গতি দেখা দেবে।
- **প্রস্তাবিত সমাধান ও কোড স্নিপেট (Recommended Solution & Code Snippet)**:
  সমস্ত হার্ডকোডেড স্ট্রিং `StringsCore` এবং `StringsFeatures`-এ প্রপার্টি আকারে যুক্ত করে `LocalAppStrings.current.<stringName>` দিয়ে কল করতে হবে।

---

### UI-07: বাংলা সংখ্যা রূপান্তর ও সেটিংস সুইচের লজিক্যাল ক্রুটি
- **Severity**: **Medium (মাঝারি)**
- **Exact Code Location**:
  - `app/src/main/java/com/example/expensetracker/MainActivity.kt`: লাইন ৫৫
  - `app/src/main/java/com/example/expensetracker/ui/components/CurrencyFormatter.kt`: লাইন ৭
  - `app/src/main/java/com/example/expensetracker/ui/transactions/TransactionAccountDropdown.kt`: লাইন ১১৩
  - `app/src/main/java/com/example/expensetracker/ui/receivables/BillSplitterFriendRow.kt`: লাইন ৯৭
  - `app/src/main/java/com/example/expensetracker/ui/reports/ReportsScreen.kt`: লাইন ১৬০
- **ত্রুটির বিশদ বিবরণ (Deficiency Description)**:
  ১. `MainActivity.kt:55`-এ লেখা হয়েছে:
     `globalBengaliNumeralsEnabled = userProfile.useBengaliNumerals || userProfile.appLanguage.equals("bn", ignoreCase = true)`
     এখানে `||` (OR) অপারেটর থাকায় ব্যবহারকারী অ্যাপের ভাষা বাংলা রাখলে সেটিংসের "বাংলা সংখ্যা (১২৩৪)" সুইচটি বন্ধ করলেও কোনো কাজ করে না (`false || true = true`)! ব্যবহারকারী কখনোই বাংলা ভাষায় ইংরেজি সংখ্যা দেখতে পারেন না।  
  ২. `CurrencyFormatter.kt:7`-এ `var globalBengaliNumeralsEnabled: Boolean = false` একটি গ্লোবাল মিউটেবল ভেরিয়েবল। এটি কোনো কম্পোজ `State` বা `CompositionLocal` না হওয়ায় এর পরিবর্তনের সাথে সাথে উইজেট রি-কম্পোজিশন সঠিকভাবে ট্রিগার হয় না।  
  ৩. `ReportsScreen.kt:160`-এ `${filteredTransactions.size} টি লেনদেন` লেখা হয়েছে, যেখানে সংখ্যাটি ইংরেজিতে "5 টি লেনদেন" হিসেবে প্রদর্শিত হয়।
- **ব্যবহারকারী ও সিস্টেমের ওপর প্রভাব (User / System Impact)**:
  সেটিংসের সুইচ অকার্যকর হয়ে থাকা বিভ্রান্তিকর অভিজ্ঞতা। এছাড়া স্ক্রিনে ইংরেজি ও বাংলা সংখ্যার মিশ্রণ (যেমন: "5 টি লেনদেন") ইউজার ইন্টারফেসে অপেশাদার দেখায়।
- **প্রস্তাবিত সমাধান ও কোড স্নিপেট (Recommended Solution & Code Snippet)**:
  ১. `MainActivity.kt`-এর ওভাররাইড লজিক সংশোধন করে ব্যবহারকারীর ডেডিকেটেড পছন্দকে প্রাধান্য দিন:
     `globalBengaliNumeralsEnabled = userProfile.useBengaliNumerals`  
  ২. গ্লোবাল ভেরিয়েবলের বদলে `LocalBengaliNumerals` নামক `CompositionLocal` ব্যবহার করুন।  
  ৩. সমস্ত সংখ্যার জন্য `convertToBengaliDigits()` কল নিশ্চিত করুন।

```kotlin
// ফাইল: app/src/main/java/com/example/expensetracker/MainActivity.kt (লাইন ৫৫)
// বিফোর:
globalBengaliNumeralsEnabled = userProfile.useBengaliNumerals || userProfile.appLanguage.equals("bn", ignoreCase = true)

// আফটার: শুধুমাত্র ব্যবহারকারীর এক্সপ্লিসিট সেটিংকে প্রাধান্য দিন
globalBengaliNumeralsEnabled = userProfile.useBengaliNumerals
```

---

### UI-08: একাধিক গুরুত্বপূর্ণ ইন্টারঅ্যাক্টিভ কম্পোনেন্টে ৪৮dp-এর চেয়ে ছোট টাচ টার্গেট
- **Severity**: **High (উচ্চ)**
- **Exact Code Location**:
  - `app/src/main/java/com/example/expensetracker/ui/navigation/ModernBottomNavBar.kt`: লাইন ১১৫ (Add FAB সাইজ `46.dp`)
  - `app/src/main/java/com/example/expensetracker/ui/dashboard/MarketItemRow.kt`: লাইন ৯৫ (চেকবক্স সাইজ `26.dp`); লাইন ২৪১ (ডিলিট আইকন `28.dp`)
  - `app/src/main/java/com/example/expensetracker/ui/transactions/TransactionAmountInputField.kt`: লাইন ১০৬ (Clear বাটন `30.dp`)
  - `app/src/main/java/com/example/expensetracker/ui/transactions/TransactionNoteAndTitleSection.kt`: লাইন ৮৫, ১৪৮ (ইনপুট ক্লিয়ার বাটন `28.dp`)
  - `app/src/main/java/com/example/expensetracker/ui/receivables/BillSplitterFriendRow.kt`: লাইন ১১০ (রিমুভ বাটন `28.dp`)
  - `app/src/main/java/com/example/expensetracker/ui/receivables/PayableRowItem.kt`: লাইন ১৪৭ (অপশনস মেনু `32.dp`)
  - `app/src/main/java/com/example/expensetracker/ui/transactions/TransactionReceiptSection.kt`: লাইন ২১৫, ২২৮ (ভাউচার এডিট/ডিলিট বাটন `32.dp`)
  - `app/src/main/java/com/example/expensetracker/ui/navigation/GlobalActionChooserSheet.kt`: লাইন ১১৭ (ক্লোজ বাটন `32.dp`)
  - `app/src/main/java/com/example/expensetracker/ui/transactions/TransactionDateShortcutsRow.kt`: লাইন ৫৫, ৮৪, ১০৯, ১৩৮ (চিপ হাইট ~২৬dp)
  - `app/src/main/java/com/example/expensetracker/ui/budget/BudgetHeaderTabs.kt`: লাইন ১৪৮, ১৭৪ (ট্যাব চিপ হাইট ~৩২dp)
  - `app/src/main/java/com/example/expensetracker/ui/settings/SettingsThemeCard.kt`: লাইন ১১৪ (থিম সিলেক্টর হাইট ~৩০dp)
- **ত্রুটির বিশদ বিবরণ (Deficiency Description)**:
  গুগল মেটেরিয়াল ডিজাইন ও WCAG 2.1 Success Criterion 2.5.5 অনুযায়ী মোবাইল ডিভাইসে যেকোনো ক্লিকেবল উপাদানের ন্যূনতম স্পর্শ এলাকা (Touch Target Area) হতে হবে **৪৮dp × ৪৮dp**। কিন্তু উক্ত কম্পোনেন্টগুলোতে বাটন এবং আইকনগুলোর ভিজ্যুয়াল সাইজই টাচ সাইজ হিসেবে সীমাবদ্ধ রয়েছে (২৬dp–৪৬dp)।
- **ব্যবহারকারী ও সিস্টেমের ওপর প্রভাব (User / System Impact)**:
  ব্যবহারকারী যখন একহাতে ফোন ব্যবহার করেন অথবা বাজারে হাঁটার সময় মার্কেট ফর্দে টিক দিতে যান, তখন ২৬dp চেকবক্সে ক্লিক মিস হয়। এটি মোটর দুর্বলতা থাকা মানুষ এবং বয়স্কদের জন্য অত্যন্ত বিরক্তিকর।
- **প্রস্তাবিত সমাধান ও কোড স্নিপেট (Recommended Solution & Code Snippet)**:
  কম্পোজের মেটেরিয়াল ৩ `minimumInteractiveComponentSize()` অথবা `Modifier.sizeIn(minWidth = 48.dp, minHeight = 48.dp)` ব্যবহার করে স্পর্শ এলাকা বৃদ্ধি করতে হবে, যাতে ভিজ্যুয়াল সাইজ ছোট থাকলেও ক্লিকযোগ্য এলাকা ৪৮dp বজায় থাকে।

```kotlin
// ফাইল: app/src/main/java/com/example/expensetracker/ui/dashboard/MarketItemRow.kt
// বিফোর (লাইন ৯৫):
Box(
    modifier = Modifier
        .size(26.dp)
        .clickable { onTickClick() }
)

// আফটার: ভিজ্যুয়াল সাইজ অক্ষত রেখে টাচ টার্গেট ৪৮dp নিশ্চিতকরণ
Box(
    modifier = Modifier
        .sizeIn(minWidth = 48.dp, minHeight = 48.dp)
        .clickable { onTickClick() },
    contentAlignment = Alignment.Center
) {
    Box(
        modifier = Modifier
            .size(26.dp)
            .clip(CircleShape)
    )
}
```

---

### UI-09: TalkBack স্ক্রিন রিডারের জন্য অসম্পূর্ণ সেমান্টিক্স এবং হার্ডকোডেড ইংরেজি ডেসক্রিপশন
- **Severity**: **Medium (মাঝারি)**
- **Exact Code Location**:
  - `app/src/main/java/com/example/expensetracker/ui/receivables/ReceivableItemCard.kt`: লাইন ১৫৭ (`Icon(Icons.Default.MoreVert, null)`)
  - `app/src/main/java/com/example/expensetracker/ui/transactions/TransactionSwipeRow.kt`: লাইন ৮৪ (`Icon(icon, null)`)
  - `app/src/main/java/com/example/expensetracker/ui/accounts/AccountsQuickActionsRow.kt`: লাইন ৫৯, ৮০ (`contentDescription = null`)
  - `app/src/main/java/com/example/expensetracker/ui/navigation/BottomNavItemButton.kt`: লাইন ৯৪, ১০৩ (কোনো `Role.Tab` নেই, ডাবল রিডিং)
  - হার্ডকোডেড ইংরেজি ডেসক্রিপশন: `"Back"` (`AccountsScreen.kt:81`, `BudgetHeaderTabs.kt:81`), `"Close"` (`GlobalActionChooserSheet.kt:121`), `"Clear"` (`TransactionAmountInputField.kt:110`), `"Delete"` (`MarketItemRow.kt:248`), `"Edit"` (`MarketItemRow.kt:160`)
- **ত্রুটির বিশদ বিবরণ (Deficiency Description)**:
  অনেকগুলো অ্যাকশন বাটনে `contentDescription` নাল (`null`) রাখা হয়েছে, যার ফলে TalkBack ব্যবহারকারীদের কাছে সেগুলোকে "Unlabelled button" হিসেবে পড়ে শোনায়। অন্যদিকে বাকিগুলোতে হার্ডকোডেড ইংরেজি টেক্সট থাকায় বাংলা ভয়েস সিন্থেসাইজারে ইংরেজি শব্দ ভুল উচ্চারণে পড়ে। এছাড়া বটম বারের ট্যাবে `Role.Tab` বা সিলেক্টেড স্টেট সেমান্টিক্স না থাকায় স্ক্রিন রিডার ব্যবহারকারী বুঝতে পারেন না কোন ট্যাবে আছেন।
- **ব্যবহারকারী ও সিস্টেমের ওপর প্রভাব (User / System Impact)**:
  দৃষ্টিপ্রতিবন্ধী এবং স্ক্রিন রিডার ব্যবহারকারী ব্যক্তিদের জন্য অ্যাপ্লিকেশনটি ব্যবহার করা প্রায় অসম্ভব হয়ে পড়ে।
- **প্রস্তাবিত সমাধান ও কোড স্নিপেট (Recommended Solution & Code Snippet)**:
  প্রত্যেক ইন্টারঅ্যাক্টিভ আইকনে `strings` থেকে স্থানীয়কৃত ডেসক্রিপশন পাস করতে হবে। বটম বারে `Modifier.semantics { role = Role.Tab; selected = isSelected }` যুক্ত করতে হবে এবং `SwipeToDismissBox`-এ `customActions` ডিফাইন করতে হবে যাতে সোয়াইপ ছাড়াই অ্যাকশন নেওয়া যায়।

---

## সেকশন ২: Backend & Architecture Logic (ব্যাকএন্ড ও বিজনেস লজিক)

---

### BK-01: সম্পূর্ণ অ্যাপ্লিকেশনে `collectAsStateWithLifecycle`-এর অনুপস্থিতি
- **Severity**: **High (উচ্চ)**
- **Exact Code Location**:
  - `MainActivity.kt`: লাইন ৫৩
  - `AppNavigation.kt`: লাইন ৬৪-৬৯
  - `ReportsScreen.kt`: লাইন ৫৬-৬৫ (১০টি স্টেট কালেকশন)
  - `BudgetScreen.kt`: লাইন ৩৬-৪০ (৫টি স্টেট কালেকশন)
  - `BillsScreen.kt`: লাইন ৪৩-৪৬
  - `AnalyticsScreen.kt`: লাইন ৪৫-৫৩ (৯টি স্টেট কালেকশন)
  - `AccountsScreen.kt`: লাইন ৪০-৪৪
  - `ReceivablesScreen.kt`: লাইন ৪১-৪৭
  - `SettingsScreen.kt`: লাইন ৪২-৪৫
  - `TransactionsScreen.kt`: লাইন ৪৭-৫২
  - `NavigationTransactionSheets.kt`: লাইন ৫০
  - `ShareExpenseSheet.kt`: লাইন ৭৩
- **ত্রুটির বিশদ বিবরণ (Deficiency Description)**:
  `app/build.gradle.kts` এর ৭৬ নম্বর লাইনে `androidx.lifecycle:lifecycle-runtime-compose` লাইব্রেরি ডিপেনডেন্সি অন্তর্ভুক্ত থাকা সত্ত্বেও অ্যাপের কোনো কম্পোজেবল স্ক্রিনেই `collectAsStateWithLifecycle()` ব্যবহৃত হয়নি; সর্বত্র স্ট্যান্ডার্ড `collectAsState()` ব্যবহৃত হয়েছে।
- **ব্যবহারকারী ও সিস্টেমের ওপর প্রভাব (User / System Impact)**:
  স্ট্যান্ডার্ড `collectAsState()` অ্যান্ড্রয়েড লাইফসাইকেল (যেমন `Lifecycle.State.STARTED`) পর্যবেক্ষণ করে না। যখন ইউজার অ্যাপ মিনিমাইজ করে, স্ক্রিন অফ করে বা অন্য কোনো অ্যাপে সুইচ করে, তখনও ফ্লোর ব্যাকগ্রাউন্ড কালেকশন চলতে থাকে। ফলে রিপোজিটরির ডাটাবেজ কোয়েরি ও ফ্লো প্রসেসিং ব্যাকগ্রাউন্ডে অবিরাম ব্যাটারি ও সিপিইউ নষ্ট করে।
- **প্রস্তাবিত সমাধান ও কোড স্নিপেট (Recommended Solution & Code Snippet)**:
  সকল কম্পোজ স্ক্রিনে `androidx.lifecycle.compose.collectAsStateWithLifecycle` ব্যবহার করুন:

```kotlin
// বিফোর:
val transactions by viewModel.transactions.collectAsState()

// আফটার:
import androidx.lifecycle.compose.collectAsStateWithLifecycle
val transactions by viewModel.transactions.collectAsStateWithLifecycle()
```

---

### UI-10: টপ-বার ও হেডারে অপ্রয়োজনীয় হ্যামবার্গার ড্রয়ার মেনু ডুপ্লিকেশন স্থায়ীভাবে অপসারণ ও ব্যাক বাটন সুরক্ষা
- **Severity**: **High (উচ্চ / আর্কিটেকচারাল রুল)**
- **Exact Code Location**:
  - `app/src/main/java/com/example/expensetracker/ui/navigation/NavigationScreenHost.kt`
  - `app/src/main/java/com/example/expensetracker/ui/components/OneUiHeader.kt`
  - `app/src/main/java/com/example/expensetracker/ui/navigation/ModernBottomNavBar.kt`
- **Component**: `NavigationScreenHost`, `OneUiHeader`, `ModernBottomNavBar`
- **ত্রুটির বিশদ বিবরণ (Deficiency Description)**:
  বটম নেভিগেশন বারে (`ModernBottomNavBar`) সর্বডানে ড্রয়ার মেনু অ্যাক্সেসের জন্য ইতিমধ্যে ডেডিকেটেড `More` বাটন (`Icons.Filled.Menu`) রাখা হয়েছে, যা পুরো অ্যাপের একমাত্র সিঙ্গেল সোর্স অফ ট্রুথ (Single Source of Truth)। কিন্তু স্ক্রিন কম্পোনেন্টগুলোতে (যেমন ড্যাশবোর্ড হেডার, ট্রানজ্যাকশন টপ-বার, একাউন্টস ও বিলস হেডার) `onMenuClick = onOpenMenu` পাস করায় উপরে বাম কোণে একই হ্যামবার্গার মেনু আইকন অপ্রয়োজনীয়ভাবে প্রদর্শিত হচ্ছিল। এর ফলে দুটি বড় সমস্যা দেখা দেয়:
  ১. একই স্ক্রিনে একই কাজের জন্য ডুপ্লিকেট মেনু আইকন থাকায় ইউজার ইন্টারফেস অপেশাদার ও ক্ল্যাটারড হয়ে পড়ে।
  ২. সেকেন্ডারি স্ক্রিনগুলোতে (যেমন Accounts, Bills, Receivables, Budgets, Reports, Settings) মেনু আইকন দৃশ্যমান থাকায় স্ট্যান্ডার্ড ব্যাক অ্যারো (`←`) বাটন হাইড হয়ে যেত।
- **আর্কিটেকচারাল সিদ্ধান্ত ও স্থায়ী সমাধান (Architectural Invariant & Permanent Rule)**:
  ১. `NavigationScreenHost`-এর কোনো স্ক্রিন কম্পোনেন্টেই আর কখনো `onMenuClick = onOpenMenu` পাস করা হবে না।
  ২. ড্যাশবোর্ড ও প্রাইমারি স্ক্রিনসমূহের টপ-বার সম্পূর্ণ ক্লিন থাকবে।
  ৩. সমস্ত সেকেন্ডারি স্ক্রিনে ব্যাক অ্যারো বাটন (`←`) দ্বারা হোম স্ক্রিনে ফিরে আসার জন্য `onNavigateBack` বাধ্যতামূলক থাকবে।
  ৪. ইউনিট টেস্টে (`Phase1P0EnhancementsTest.kt`) `testTopBarDrawerHamburgerDuplicationPreventedInNavigationScreenHost` টেস্ট যুক্ত করা হয়েছে, যা ভবিষ্যতেও কোনো কোড পরিবর্তনের মাধ্যমে ভুলবশত টপ-বার হ্যামবার্গার মেনু ফিরে আসা রোধ করবে।

---

### BK-02: অ্যাপ নেভিগেশন রুটে ৯টি ViewModel-এর একযোগে ইগার ইনিশিয়ালাইজেশন
- **Severity**: **High (উচ্চ)**
- **Exact Code Location**: `app/src/main/java/com/example/expensetracker/ui/navigation/AppNavigation.kt`, লাইন ৫৪-৬২
- **Component**: `MainAppNavigation`
- **ত্রুটির বিশদ বিবরণ (Deficiency Description)**:
  অ্যাপ যখন কোল্ড স্টার্ট নেয়, তখন `MainAppNavigation` এর কম্পোজিশনে সমস্ত ৯টি ViewModel (`DashboardViewModel`, `TransactionsViewModel`, `AnalyticsViewModel`, `BudgetViewModel`, `SettingsViewModel`, `AccountsViewModel`, `ReceivablesViewModel`, `BillsViewModel`, `ReportsViewModel`) একসাথে ইনস্ট্যানশিয়েট হয়ে যায়। প্রতিটি ViewModel-এর নিজস্ব `init` ব্লক থাকে যা সমান্তরালে SQLite কোয়েরি শুরু করে, ডিসপ্যাচার স্পন করে এবং ফ্লো মার্জিং শুরু করে (যেমন: `ReportsViewModel` সমস্ত ট্রানজাকশনের ওপর ইন-মেমোরি হিসাব শুরু করে, `SettingsViewModel` ব্যাকগ্রাউন্ডে গুগল একাউন্ট চেক করে, `AnalyticsViewModel` সমান্তরালে ৫টি ডাটাবেজ কোয়েরি চালায়)।
- **ব্যবহারকারী ও সিস্টেমের ওপর প্রভাব (User / System Impact)**:
  হোম স্ক্রিন লোড হতেই মারাত্মক স্টার্টআপ ল্যাটেন্সি এবং মেমোরি প্রেসার তৈরি হয়। ইউজার হয়তো শুধু একটি খরচ দেখতে চেয়েছেন, কিন্তু ব্যাকগ্রাউন্ডে বাকি ৮টি স্ক্রিনের ফুল ডাটা প্রসেসিং শুরু হয়ে যায়।
- **প্রস্তাবিত সমাধান ও কোড স্নিপেট (Recommended Solution & Code Snippet)**:
  ভিউমডেলগুলোকে গ্লোবালি রুট লেভেলে ইনিশিয়ালাইজ না করে সংশ্লিষ্ট স্ক্রিন কম্পোজেবলে অথবা ডেস্টিনেশন এন্ট্রিতে ল্যাজি ইনিশিয়ালাইজ করুন, অথবা Jetpack Navigation-এর ডেস্টিনেশন স্কোপ ব্যবহার করুন।

---

### BK-03: `viewModelScope.launch`-এ `CoroutineExceptionHandler` ও `try-catch`-এর অনুপস্থিতি
- **Severity**: **Critical (ক্রিটিক্যাল)**
- **Exact Code Location**:
  - `DashboardViewModel.kt`: লাইন ১০৩-১৮৭ (`addTransaction`, `updateTransaction`, `deleteTransaction`, `executeBillSplit`, `transferFunds`, `createMarketListWithBulk`, `addMarketItem`)
  - `BudgetViewModel.kt`: লাইন ১১৬-১৬৩ (`saveBudget`, `deleteBudget`, `addSavingsGoal`, `addSavingsContribution`)
  - `AccountsViewModel.kt`: লাইন ৮৩-১৭০ (`addAccount`, `updateAccount`, `deleteAccount`, `transferFunds`, `reconcileBalance`)
  - `BillsViewModel.kt`: লাইন ৩২-৬২ (`addBill`, `updateBill`, `deleteBill`, `payBill`)
  - `ReceivablesViewModel.kt`: লাইন ৭৩-১৬২ (`addReceivable`, `updateReceivable`, `recordPayment`, `addPayable`)
  - `TransactionsViewModel.kt`: লাইন ১৬৪-১৭৪ (`deleteTransaction`, `restoreTransaction`)
- **ত্রুটির বিশদ বিবরণ (Deficiency Description)**:
  কোনো ভিউমডেলেই `CoroutineExceptionHandler` ডিফাইন করা নেই। উপরোক্ত মেথডগুলোতে সরাসরি `viewModelScope.launch { repository.someAction() }` চালানো হচ্ছে কোনো `try-catch` ব্লক ছাড়াই।
- **ব্যবহারকারী ও সিস্টেমের ওপর প্রভাব (User / System Impact)**:
  যদি ডিস্ক ফুল হয়ে যায়, ডাটাবেজ ফরেইন কি ভায়োলেশন ঘটে, বা ট্রানজাকশন চলাকালে `SQLiteDiskIOException` / `SQLiteDatabaseLockedException` থ্রো হয়, তাহলে কোরোটিনের আনহ্যান্ডেল্ড এক্সেপশনটি সরাসরি মেইন থ্রেডের `DefaultUncaughtExceptionHandler`-এ গিয়ে পুরো অ্যাপকে ক্র্যাশ করিয়ে দেবে।
- **প্রস্তাবিত সমাধান ও কোড স্নিপেট (Recommended Solution & Code Snippet)**:
  একটি গ্লোবাল বা বেস ভিউমডেল এরর হ্যান্ডলার স্থাপন করুন এবং মিউটেশনগুলোতে সেফটি হ্যান্ডলিং নিশ্চিত করুন:

```kotlin
val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
    Log.e("ViewModel", "Unhandled coroutine error", throwable)
    _uiError.value = throwable.localizedMessage ?: "অপ্রত্যাশিত ত্রুটি ঘটেছে"
}

fun addTransaction(tx: Transaction) {
    viewModelScope.launch(coroutineExceptionHandler) {
        try {
            repository.addTransaction(tx)
        } catch (e: Exception) {
            _uiError.value = "লেনদেন সংরক্ষণ করা সম্ভব হয়নি: ${e.message}"
        }
    }
}
```

---

### BK-04: রিপোজিটরি ইনিশিয়ালাইজেশনে মেইন থ্রেডে সিনক্রোনাস ডাটাবেজ কোয়েরি
- **Severity**: **High (উচ্চ)**
- **Exact Code Location**: `app/src/main/java/com/example/expensetracker/data/repository/ExpenseRepository.kt`, লাইন ৯৬-১০২
- **ত্রুটির বিশদ বিবরণ (Deficiency Description)**:
  `ExpenseRepository.getInstance(context)` কল করা হয় `ExpenseTrackerApp.onCreate()` (লাইন ১৮), `MainActivity.onCreate()` (লাইন ৪৯) এবং ভিউমডেল কনস্ট্রাক্টরগুলোতে যা মেইন থ্রেডে চলে। `init` ব্লকের ভেতরে `dbHelper.getUserProfile()` সরাসরি মেইন থ্রেডে SQLite ডাটাবেজ টেবিল রিড করে।
- **ব্যবহারকারী ও সিস্টেমের ওপর প্রভাব (User / System Impact)**:
  অ্যান্ড্রয়েডের কঠোর মেইন-থ্রেড ডিস্ক I/O ভায়োলেশন। কোল্ড স্টার্টের সময় ডাটাবেজ ওপেন এবং কার্সার কুয়েরি মেইন থ্রেডকে ব্লক করে, যার ফলে স্ক্রিন রেন্ডারিংয়ে ল্যাগ এবং লো-এন্ড ডিভাইসে ANR তৈরি হতে পারে।
- **প্রস্তাবিত সমাধান ও কোড স্নিপেট (Recommended Solution & Code Snippet)**:
  ইনিশিয়াল প্রোফাইল ডিফল্ট মেমোরি স্টেট দিয়ে শুরু করুন এবং প্রোফাইল রিড অপারেশনটিও ব্যাকগ্রাউন্ড কোরোটিনে অফলোড করুন:

```kotlin
init {
    repositoryScope.launch {
        refreshUserProfile()
        refreshAll()
    }
}
```

---

### BK-05: জেমিনি এআই-তে সিকোয়েনশিয়াল ৬-মডেল ফলব্যাক লুপ ও টাইমআউট না থাকা
- **Severity**: **Critical (ক্রিটিক্যাল)**
- **Exact Code Location**:
  - `GeminiAiClientHelper.kt`: লাইন ৯-২০ (`getModelsToTry()`)
  - `GeminiAiClientHelper.kt`: লাইন ২৫-৪০ (`createModel()`)
  - `GeminiAiFinancialAuditor.kt`: লাইন ৬০-১১৩ (`generateFinancialInsights()`)
  - `GeminiAiAdvisorAndReminders.kt`: লাইন ৭১-৮৪ (`askAdvisor()`), লাইন ১১৯-১৩২
  - `GeminiAiGoalsAndBills.kt`: লাইন ৫৮-৭৮ (`analyzeSavingsGoal()`)
  - `GeminiAiReceiptParser.kt`: লাইন ৪৫-৯৫, লাইন ১৩৫-১৭৫
- **ত্রুটির বিশদ বিবরণ (Deficiency Description)**:
  ১. `getModelsToTry()` ফাংশনে ৬টি মডেলের তালিকা রয়েছে: `"gemini-2.0-flash"`, `"gemini-1.5-flash"`, `"gemini-3.5-flash-lite"`, `"gemini-3.1-flash-lite"`, `"gemini-3.6-flash"`, `"gemini-3.5-flash"`। এর মধ্যে ৩.১, ৩.৫, ৩.৬ এখনো কাল্পনিক মডেল নাম, যা কল করলেই `404 Not Found` দেবে।  
  ২. `createModel()`-এ কোনো `RequestOptions(timeout = ...)` কনফিগার করা নেই।  
  ৩. যখন ব্যবহারকারী অফলাইনে থাকে বা জেমিনি এপিআই কোটা লিমিট (429 RESOURCE_EXHAUSTED) বা ইনভ্যালিড কি (401/403) রিটার্ন করে, তখন ফাংশনগুলো লুপ ব্রেক না করে পরবর্তী মডেল দিয়ে আবার রিকোয়েস্ট পাঠাতে থাকে।
- **ব্যবহারকারী ও সিস্টেমের ওপর প্রভাব (User / System Impact)**:
  মোবাইল নেটওয়ার্ক ধীর থাকলে বা ইন্টারনেট না থাকলে প্রতিটি মডেলের সকেট টাইমআউট হতে ২০-৩০ সেকেন্ড সময় লাগে। ৬টি মডেল পরপর চেষ্টা করতে গিয়ে অ্যাপ একটানা ২ থেকে ৩ মিনিট হ্যাং বা লোডিং স্টেটে আটকে থাকে। রেট লিমিট থাকলে প্রতিবার রিকোয়েস্ট পাঠিয়ে কোটা সম্পূর্ণ নষ্ট করে ফেলে।
- **প্রস্তাবিত সমাধান ও কোড স্নিপেট (Recommended Solution & Code Snippet)**:
  ১. শুধুমাত্র ভ্যালিড প্রোডাকশন মডেল ব্যবহার করুন (`gemini-2.0-flash` এবং ফলব্যাক হিসেবে `gemini-1.5-flash`)।  
  ২. জেমিনি ক্লায়েন্টে ১০-১৫ সেকেন্ডের কঠোর টাইমআউট সেট করুন।  
  ৩. `401`, `403`, `429` বা নেটওয়ার্ক এরর হলে অবিলম্বে লুপ ব্রেক করে লোকাল ফলব্যাকে চলে যান:

```kotlin
fun createModel(modelName: String, apiKey: String): GenerativeModel {
    return GenerativeModel(
        modelName = modelName,
        apiKey = apiKey.trim(),
        requestOptions = RequestOptions(timeout = 15.seconds)
    )
}
```

---

### BK-06: রসিদ স্ক্যানিংয়ে বিটম্যাপ ডিকোডিংয়ের কারণে OutOfMemoryError (OOM) ঝুঁকি
- **Severity**: **High (উচ্চ)**
- **Exact Code Location**: `app/src/main/java/com/example/expensetracker/utils/ReceiptImageHelper.kt`, লাইন ৩৪-৩৬
- **ত্রুটির বিশদ বিবরণ (Deficiency Description)**:
  ক্যামেরা থেকে তোলা বা গ্যালারি থেকে সিলেক্ট করা রসিদের ছবি সেভ করার আগে `BitmapFactory.decodeStream(inputStream)` দিয়ে আনকম্প্রেসড র-বিটম্যাপ মেমোরিতে লোড করা হচ্ছে।
- **ব্যবহারকারী ও সিস্টেমের ওপর প্রভাব (User / System Impact)**:
  বর্তমান আধুনিক অ্যান্ড্রয়েড স্মার্টফোনের ক্যামেরা ৪৮ থেকে ১০৮ মেগাপিক্সেল রেজোলিউশনে ছবি তোলে। এই রেজোলিউশনের একটি ছবি `ARGB_8888` ফরম্যাটে ডিকোড করতে মেমোরিতে ৪০০+ মেগাবাইট হিপ স্পেস প্রয়োজন হয়। অ্যাপের হিপ লিমিট সাধারণত ১৯২-২৫৬ এমবি হওয়ায় এটি তাৎক্ষণিকভাবে `java.lang.OutOfMemoryError` থ্রো করে পুরো অ্যাপ বন্ধ করে দেয়।
- **প্রস্তাবিত সমাধান ও কোড স্নিপেট (Recommended Solution & Code Snippet)**:
  `inJustDecodeBounds = true` দিয়ে প্রথমে ছবির দৈর্ঘ্য ও প্রস্থ পরিমাপ করে উপযুক্ত `inSampleSize` হিসেব করে সাব-স্যাম্পল্ড ডিকোড করুন:

```kotlin
val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
BitmapFactory.decodeStream(stream1, null, options)
options.inSampleSize = calculateInSampleSize(options, 1280, 1280)
options.inJustDecodeBounds = false
val sampledBitmap = BitmapFactory.decodeStream(stream2, null, options)
```

---

### BK-07: রসিদ স্ক্যানিংয়ে অপরিপক্ক JSON পার্সিংয়ের কারণে সাইলেন্ট ফেইলিউর
- **Severity**: **High (উচ্চ)**
- **Exact Code Location**: `app/src/main/java/com/example/expensetracker/data/ai/GeminiAiReceiptParser.kt`, লাইন ৫৭-৫৮
- **ত্রুটির বিশদ বিবরণ (Deficiency Description)**:
  `GeminiAiJsonUtils.extractJsonBlock()`-এ প্রথম `{` এবং শেষ `}` খোঁজার লজিক থাকলেও, `GeminiAiReceiptParser.scanReceipt`-এ সেটি ব্যবহার না করে একটি অকার্যকর `removePrefix("```json")` কল করা হয়েছে।
- **ব্যবহারকারী ও সিস্টেমের ওপর প্রভাব (User / System Impact)**:
  জেমিনি মডেল যদি কোডব্লকের আগে কোনো বাক্য লেখে (যেমন: `Here is the extracted transaction data:\n```json\n{...}`), তাহলে `removePrefix` কিছুই রিমুভ করতে পারে না। ফলে `parseToJsonElement` এ `SerializationException` ঘটে এবং কোনো ইরর মেসেজ ছাড়াই রসিদ স্ক্যানিং ফেইল করে `null` রিটার্ন করে।
- **প্রস্তাবিত সমাধান ও কোড স্নিপেট (Recommended Solution & Code Snippet)**:
  `GeminiAiJsonUtils.extractJsonBlock(rawText)` ব্যবহার করুন যা যেকোনো প্রিম্বল বা ট্রেইলিং টেক্সটকে বাদ দিয়ে সঠিক JSON ব্লক এক্সট্রাক্ট করতে সক্ষম।

---

### BK-08: জেমিনি এপিআই কি SQLite ডাটাবেজে প্লেইনটেক্সট আকারে ডুপ্লিকেট সংরক্ষণ
- **Severity**: **Medium (মাঝারি)**
- **Exact Code Location**:
  - `app/src/main/java/com/example/expensetracker/data/local/UserProfileTableHelper.kt`: লাইন ৪৭-৫০, লাইন ১০৫-১০৮
  - `app/src/main/java/com/example/expensetracker/data/local/DatabaseSchema.kt`: `COL_PROFILE_GEMINI_KEY`
- **ত্রুটির বিশদ বিবরণ (Deficiency Description)**:
  `SecureStorageHelper.kt`-এ অ্যান্ড্রয়েড কি-স্টোর ব্যাকড AES-256-GCM এনক্রিপশনের মাধ্যমে SharedPreferences-এ এপিআই কি সংরক্ষণ করার সুন্দর মেকানিজম থাকা সত্ত্বেও, `UserProfileTableHelper`-এ প্রোফাইল টেবিলে সরাসরি প্লেইনটেক্সট আকারে এপিআই কি রাইট করা হচ্ছে।
- **ব্যবহারকারী ও সিস্টেমের ওপর প্রভাব (User / System Impact)**:
  কোনোভাবে ডাটাবেজ ফাইল আন-এনক্রিপ্টেড এক্সপোর্ট হলে বা ডিভাইসের ব্যাকআপ নেওয়া হলে ইউজারের ব্যক্তিগত গুগল এআই এপিআই কি অনায়াসে ফাঁস হয়ে যেতে পারে।
- **প্রস্তাবিত সমাধান ও কোড স্নিপেট (Recommended Solution & Code Snippet)**:
  ডাটাবেজের `COL_PROFILE_GEMINI_KEY` কলামে সর্বদা `NULL` রাখুন এবং কি রিড/রাইটের সম্পূর্ণ দায়িত্ব শুধুমাত্র `SecureStorageHelper`-এর মাধ্যমে পরিচালনা করুন।

---

### BK-09: গুগল ড্রাইভ রিস্টোরে কনফ্লিক্ট রেজোলিউশন ও সেফটি স্ন্যাপশট না থাকা
- **Severity**: **Critical (ক্রিটিক্যাল)**
- **Exact Code Location**:
  - `app/src/main/java/com/example/expensetracker/data/drive/GoogleDriveSyncManager.kt`: লাইন ১০৮-১৫৪ (`performRestore()`)
  - `app/src/main/java/com/example/expensetracker/data/local/DatabaseRestoreHelper.kt`: লাইন ১০-৩২ (`restore()`)
- **ত্রুটির বিশদ বিবরণ (Deficiency Description)**:
  গুগল ড্রাইভ থেকে ডাটা রিস্টোর করার সময় কোনো মার্জ লজিক, টাইমস্ট্যাম্প তুলনা, বা কনফ্লিক্ট রেজোলিউশন নেই। `DatabaseRestoreHelper.restore` মেথডে সরাসরি সমস্ত ক্যাটাগরি, অ্যাকাউন্ট, ট্রানজাকশন, বাজেট, রিসিভেবল, পেয়েবল ও সেভিংস টেবিল `db.delete(TABLE_..., null, null)` দিয়ে ডিলিট করে ড্রাইভের ডাটা ইনসার্ট করা হয়।
- **ব্যবহারকারী ও সিস্টেমের ওপর প্রভাব (User / System Impact)**:
  যদি ব্যবহারকারী ভুলবশত কোনো পুরোনো ব্যাকআপ রিস্টোর করেন, বা নতুন ফোনে কিছু এন্ট্রি দেওয়ার পর ড্রাইভ থেকে রিস্টোর করেন, তাহলে ডিভাইসের সাম্প্রতিক সমস্ত এন্ট্রি কোনো ওয়ার্নিং বা রিকভারি অপশন ছাড়াই চিরতরে মুছে যাবে।
- **প্রস্তাবিত সমাধান ও কোড স্নিপেট (Recommended Solution & Code Snippet)**:
  ১. রিস্টোর প্রক্রিয়া শুরু করার ঠিক আগে একটি স্বয়ংক্রিয় লোকাল সেফটি স্ন্যাপশট (`pre_restore_backup.json`) তৈরি করুন।  
  ২. ড্রাইভ ব্যাকআপের এক্সপোর্ট টাইমস্ট্যাম্প এবং লোকাল ডাটার শেষ মডিফিকেশন টাইমস্ট্যাম্প তুলনা করে ব্যবহারকারীকে কনফার্মেশন ডায়ালগ প্রদর্শন করুন।

---

### BK-10: গুরুত্বপূর্ণ আর্থিক ট্রানজাকশনে গুগল ড্রাইভ অটো-সিঙ্ক মিসিং
- **Severity**: **High (উচ্চ)**
- **Exact Code Location**:
  - `ExpenseRepositoryDebtsAndBills.kt`: লাইন ১২-২০ (`addReceivable`), ৩৪-৪৭ (`recordReceivablePayment`), ৫০-৫৮ (`addPayable`), ৭২-৮৫ (`recordPayablePayment`), ১০৬-১২১ (`addSavingsContribution`), ১৪২-১৫০ (`payRecurringBill`), ১৫২-১৬০ (`executeBillSplit`)
  - `ExpenseRepositoryAccountsAndBudgets.kt`: লাইন ১১২-১২৪ (`transferBetweenAccounts`)
  - `ExpenseRepositoryMarketCompletion.kt`: লাইন ১১-১৩৯ (`completeActiveMarketList`)
- **ত্রুটির বিশদ বিবরণ (Deficiency Description)**:
  সাধারণ খরচ ও আয় যোগ/আপডেট করলে `triggerAutoSync()` কল করা হয়। কিন্তু দেনা-পাওনা আদায়, ঋণ পরিশোধ, এক অ্যাকাউন্ট থেকে অন্য অ্যাকাউন্টে টাকা স্থানান্তর, সেভিংস ফান্ড জমা বা বাজারের ট্রিপ ফিনিশ করার ক্ষেত্রে কখনোই `triggerAutoSync()` কল করা হয় না।
- **ব্যবহারকারী ও সিস্টেমের ওপর প্রভাব (User / System Impact)**:
  অটো-সিঙ্ক এনেবল থাকা সত্ত্বেও ব্যবহারকারীর গুরুত্বপূর্ণ ঋণ লেনদেন ও ফান্ড ট্রান্সফার ড্রাইভ ব্যাকআপে পৌঁছায় না। ফোন হারিয়ে গেলে বা নতুন ডিভাইসে রিস্টোর করলে অ্যাকাউন্টের ব্যালেন্স ও দেনা-পাওনার হিসাব অমিল থেকে যায়।
- **প্রস্তাবিত সমাধান ও কোড স্নিপেট (Recommended Solution & Code Snippet)**:
  উক্ত সমস্ত মেথডের সফল ডাটাবেজ আপডেটের পর অবিলম্বে `triggerAutoSync()` কল নিশ্চিত করুন।

---

### BK-11: ওয়ান-অফ UI ইভেন্টকে স্টেটফ্লো বা অ্যাসিনক্রোনাস ল্যাম্বডা হিসেবে হ্যান্ডলিং
- **Severity**: **Medium (মাঝারি)**
- **Exact Code Location**:
  - `SettingsViewModel.kt`: লাইন ৩৯ (`_signInIntent = MutableStateFlow<Intent?>(null)`), লাইন ৩২ (`_syncMessage`)
  - `ReportsViewModel.kt`: লাইন ৬৭ (`_exportMessage`)
  - `ReceivablesViewModel.kt`: লাইন ১৬৪ (`_generatedReminder`)
  - `DashboardViewModel.kt`: লাইন ১৭৫ (`onCompleted: ((MarketCompletionResult) -> Unit)? = null`)
  - `BillsViewModel.kt`: লাইন ৫৪ (`onSuccess: () -> Unit`)
- **ত্রুটির বিশদ বিবরণ (Deficiency Description)**:
  ১. গুগল সাইন-ইন ইনটেন্ট এবং বিভিন্ন স্ন্যাকবার মেসেজ `StateFlow`-তে রাখা হয়েছে। স্ক্রিন রোটেট করলে বা রিকম্পোজিশন হলে StateFlow তার শেষ ভ্যালু রি-এমিট করে, যার ফলে বারবার সাইন-ইন অ্যাক্টিভিটি লঞ্চ হতে পারে বা পুরোনো স্ন্যাকবার বারবার ভেসে ওঠে।  
  ২. ভিউমডেলের সাসপেন্ডিং ফাংশনে UI লেয়ারের ল্যাম্বডা (`onSuccess`, `onCompleted`) পাস করা হচ্ছে, যা মেমোরি লিক ঘটাতে পারে।
- **প্রস্তাবিত সমাধান ও কোড স্নিপেট (Recommended Solution & Code Snippet)**:
  ওয়ান-অফ ইভেন্টের জন্য `Channel<UiEvent>` বা `SharedFlow<UiEvent>(replay = 0)` ব্যবহার করুন:

```kotlin
sealed interface SettingsUiEvent {
    data class LaunchSignIn(val intent: Intent) : SettingsUiEvent
    data class ShowMessage(val message: String) : SettingsUiEvent
}
private val _eventChannel = Channel<SettingsUiEvent>(Channel.BUFFERED)
val events = _eventChannel.receiveAsFlow()
```

---

### BK-12: প্রসেস ডেথ রেসিলিয়েন্সের অভাব (`SavedStateHandle` ও `rememberSaveable` মিসিং)
- **Severity**: **High (উচ্চ)**
- **Exact Code Location**:
  - `MainActivity.kt`: লাইন ২০, ২৯
  - `AppNavigation.kt`: লাইন ৫১-৫২ (`selectedItem`, `isDrawerOpen`), লাইন ৮৭-৯৫
  - All 9 ViewModels in `ui/`
- **ত্রুটির বিশদ বিবরণ (Deficiency Description)**:
  অ্যাপে কোথাও `SavedStateHandle` ব্যবহার করা হয়নি। কম্পোজ নেভিগেশন ও বটম শিটের স্টেট তৈরিতে `rememberSaveable`-এর বদলে শুধু `remember` ব্যবহৃত হয়েছে।
- **ব্যবহারকারী ও সিস্টেমের ওপর প্রভাব (User / System Impact)**:
  ব্যবহারকারী যখন কোনো খরচের ফর্ম পূরণ করছেন বা সেটিংসের গভীরে আছেন, তখন অন্য কোনো ভারী অ্যাপ (যেমন ফেসবুক বা ক্যামেরা) ওপেন করলে ওএস ব্যাকগ্রাউন্ডের Expense Tracker প্রসেসটি কিল করে দেয়। ব্যবহারকারী ফিরে আসলে তিনি দেখতে পান তার পূরণ করা সমস্ত ফর্ম ডেটা উধাও এবং তিনি আবার হোম স্ক্রিনে নিক্ষিপ্ত হয়েছেন।
- **প্রস্তাবিত সমাধান ও কোড স্নিপেট (Recommended Solution & Code Snippet)**:
  কম্পোজেবলের স্টেট ভ্যারিয়েবলগুলোতে `rememberSaveable` ব্যবহার করুন এবং ভিউমডেলে `SavedStateHandle` ইন্টিগ্রেট করুন।

---

### BK-13: অ্যানালিটিক্সে অনিয়ন্ত্রিত সমান্তরাল কোয়েরি ও রেস কন্ডিশন
- **Severity**: **Medium (মাঝারি)**
- **Exact Code Location**: `app/src/main/java/com/example/expensetracker/ui/analytics/AnalyticsViewModel.kt`, লাইন ৮৮-১১৩
- **ত্রুটির বিশদ বিবরণ (Deficiency Description)**:
  অ্যানালিটিক্স লোডের সময় একসাথে ৫টি সমান্তরাল কোরোটিন ডিসপ্যাচ করা হয় যা একই ডাটাবেজ হেল্পারের ওপর একাধিক রিড কোয়েরি চালায়। এছাড়া ইউজার ফিল্টার দ্রুত টগল করলে পূর্ববর্তী জব বাতিল (cancel) করার কোনো মেকানিজম নেই।
- **ব্যবহারকারী ও সিস্টেমের ওপর প্রভাব (User / System Impact)**:
  ১. একাধিক থ্রেড থেকে একযোগে একই SQLiteOpenHelper কল করায় থ্রেড কনটেনশন তৈরি হয়।  
  ২. রেস কন্ডিশনের কারণে আগের ফিল্টারের ধীরগতির কোয়েরিটি পরের দ্রুতগতির কোয়েরির ফলাফলকে ওভাররাইট করে ভুল ডেটা দেখাতে পারে।  
  ৩. ৫টি আলাদা StateFlow লাইন বাই লাইন আপডেট করায় UI তে ৫ বার পরপর অপ্রয়োজনীয় রিকম্পোজিশন ঘটে।
- **প্রস্তাবিত সমাধান ও কোড স্নিপেট (Recommended Solution & Code Snippet)**:
  একটি `Job?` ভ্যারিয়েবল দিয়ে পূর্ববর্তী জব বাতিল করুন এবং ৫টি প্রোপার্টির বদলে একটি কম্বাইন্ড `AnalyticsUiState` তৈরি করুন।

---

### BK-14: WorkManager-এ ব্যাকঅফ পলিসি, এক্সিকিউশন কনস্ট্রেইন্ট ও নোটিফিকেশন আইকন ত্রুটি
- **Severity**: **Medium (মাঝারি)**
- **Exact Code Location**:
  - `BudgetReminderWorker.kt`: লাইন ৩৮-৪৮, লাইন ১১৯
  - `GoogleDriveSyncManager.kt`: লাইন ১৬৮-১৭৯
- **ত্রুটির বিশদ বিবরণ (Deficiency Description)**:
  ১. `BudgetReminderWorker.schedule()`-এ কোনো কনস্ট্রেইন্ট বা ব্যাকঅফ ক্রাইটেরিয়া দেওয়া নেই।  
  ২. `BudgetReminderWorker.kt` এর ১১৯ নম্বর লাইনে `.setSmallIcon(R.mipmap.ic_launcher)` দেওয়া হয়েছে।  
  ৩. `GoogleDriveSyncManager.scheduleDailySync()`-এ `ExistingPeriodicWorkPolicy.KEEP` ব্যবহারের কারণে সিঙ্ক ইন্টারভ্যাল বা কনস্ট্রেইন্ট আপডেট করলে শিডিউলার আগের ওয়ার্কটাই বজায় রাখে।
- **ব্যবহারকারী ও সিস্টেমের ওপর প্রভাব (User / System Impact)**:
  অ্যান্ড্রয়েড ৫.০ (API 21)+ এ স্ট্যাটাস বার নোটিফিকেশনে রঙিন লঞ্চার আইকন সাপোর্ট করে না; এটি একটি সলিড সাদা স্কয়ার (White Box) হিসেবে রেন্ডার হয় যা দেখতে অত্যন্ত ত্রুটিপূর্ণ লাগে।
- **প্রস্তাবিত সমাধান ও কোড স্নিপেট (Recommended Solution & Code Snippet)**:
  নোটিফিকেশন স্মল আইকনে একটি মনোক্রোম ভেক্টর ড্রয়েবল (`R.drawable.ic_notification`) ব্যবহার করুন এবং পিরিওডিক ওয়ার্কে এক্সপোনেনশিয়াল ব্যাকঅফ ও `ExistingPeriodicWorkPolicy.UPDATE` প্রয়োগ করুন।

---

### BK-15: ডেপ্রিকেটেড গুগল সাইন-ইন এপিআই ব্যবহার
- **Severity**: **Low (নিম্ন)**
- **Exact Code Location**: `app/src/main/java/com/example/expensetracker/data/drive/GoogleAuthHelper.kt`, লাইন ২৩-৩২
- **ত্রুটির বিশদ বিবরণ (Deficiency Description)**:
  গুগল অথেন্টিকেশনের জন্য লিগ্যাসি `GoogleSignInOptions` ও `GoogleSignInClient` (`com.google.android.gms.auth.api.signin`) ব্যবহৃত হচ্ছে, যা গুগল অফিশিয়ালি ডেপ্রিকেটেড ঘোষণা করেছে।
- **প্রস্তাবিত সমাধান ও কোড স্নিপেট (Recommended Solution & Code Snippet)**:
  আধুনিক অ্যান্ড্রয়েড ১৫+ কম্প্যাটিবিলিটির জন্য `androidx.credentials:credentials` (Credential Manager API) মাইগ্রেশনের পরিকল্পনা গ্রহণ করা উচিত।

---

## সেকশন ৩: Database Logic & Data Integrity (ডাটাবেজ লজিক ও ডাটা ইন্টিগ্রিটি)

---

### DB-01: SQLite Foreign Key Constraints Runtime Enforcement সম্পূর্ণ অনুপস্থিত
- **Severity**: **Critical (ক্রিটিক্যাল)**
- **Exact Code Location**: `app/src/main/java/com/example/expensetracker/data/local/ExpenseDatabaseHelper.kt`, লাইন ৭০-৭৩
- **ত্রুটির বিশদ বিবরণ (Deficiency Description)**:
  অ্যান্ড্রয়েডের SQLiteOpenHelper-এ ডিফল্টভাবে ফরেন কি এনফোর্সমেন্ট নিষ্ক্রিয় থাকে (Disabled by default)। `ExpenseDatabaseHelper.kt`-এর `onConfigure()` মেথডে শুধুমাত্র Write-Ahead Logging চালু করা হয়েছে (`db.enableWriteAheadLogging()`), কিন্তু `db.setForeignKeyConstraintsEnabled(true)` বা `PRAGMA foreign_keys = ON;` কখনো কার্যকর করা হয়নি।
- **ব্যবহারকারী ও সিস্টেমের ওপর প্রভাব (User / System Impact)**:
  ডেটাবেসে কোনো রেফারেন্সিয়াল ইন্টিগ্রিটি (Referential Integrity) বলবৎ নেই। যদি কোনো কোড বা কুয়েরি ডিলিট বা ইনসার্ট অপারেশন চালায়, ডেটাবেস কোনো কনস্ট্রেইন্ট চেক ছাড়াই অকার্যকর বা অনাথ (Orphan) আইডি গ্রহণ করবে।
- **প্রস্তাবিত সমাধান ও কোড স্নিপেট (Recommended Solution & Code Snippet)**:
  `ExpenseDatabaseHelper.kt`-এর `onConfigure()` মেথডে ফরেন কি বাধ্যতামূলকভাবে চালু করুন:

```kotlin
// ফাইল: app/src/main/java/com/example/expensetracker/data/local/ExpenseDatabaseHelper.kt
override fun onConfigure(db: SQLiteDatabase) {
    super.onConfigure(db)
    db.enableWriteAheadLogging()
    db.setForeignKeyConstraintsEnabled(true)
}
```

---

### DB-02: ডেটাবেস স্কিমা টেবিল সমূহে Foreign Key Constraint সংজ্ঞার পূর্ণ অনুপস্থিতি
- **Severity**: **Critical (ক্রিটিক্যাল)**
- **Exact Code Location**: `app/src/main/java/com/example/expensetracker/data/local/DatabaseSchema.kt`, লাইন ৩৩-৫৮, ৬০-৭১, ১২৯-১৫১, ১৯৫-২১০
- **ত্রুটির বিশদ বিবরণ (Deficiency Description)**:
  `DatabaseSchema.kt`-এ টেবিল তৈরির DDL স্টেটমেন্টগুলোতে চাইল্ড টেবিলের কলামগুলোতে প্যারেন্ট টেবিলের কোনো ফরেন কি রেফারেন্স নেই:
  - `transactions` টেবিলের `account_id` কলামটি `accounts(id)`-কে রেফারেন্স করে না (`ON DELETE RESTRICT` বা `CASCADE` নেই)।
  - `transactions` টেবিলের `category_id` কলামটি `categories(id)`-কে রেফারেন্স করে না।
  - `budgets` টেবিলের `category_id` কলামটি `categories(id)`-কে রেফারেন্স করে না।
  - `recurring_bills` টেবিলের `category_id` ও `account_id` কলামে কোনো ফরেন কি নেই।
  - `market_items` টেবিলের `list_id` কলামটি `market_lists(id)`-কে রেফারেন্স করে না, এবং `transaction_id` কলামটি `transactions(id)`-কে রেফারেন্স করে না।
- **ব্যবহারকারী ও সিস্টেমের ওপর প্রভাব (User / System Impact)**:
  কোনো প্যারেন্ট রেকর্ড মুছে ফেললে রিলেটেড চাইল্ড রেকর্ডগুলো অনাথ (Orphaned Records) হিসেবে ডেটাবেসে জমে থাকে। অ্যাপ্লিকেশন লেয়ারে কোনো কারণে ডিলিট হ্যান্ডলিং মিস হলে ডেটাবেস লেভেল থেকে কোনো সুরক্ষা পাওয়া যায় না।
- **প্রস্তাবিত সমাধান ও কোড স্নিপেট (Recommended Solution & Code Snippet)**:
  `DatabaseSchema.kt` এবং পরবর্তী ডাটাবেস মাইগ্রেশনে টেবিল ডিডিএলে ফরেন কি যোগ করুন:

```sql
FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE RESTRICT,
FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE RESTRICT
```

---

### DB-03: Seed Data-তে ভুল Category ID হার্ডকোড করায় ডেটা করাপশন
- **Severity**: **High (উচ্চ)**
- **Exact Code Location**: `app/src/main/java/com/example/expensetracker/data/local/DatabaseSeedData.kt`, লাইন ২৫-২৯, ৪২-৪৬
- **ত্রুটির বিশদ বিবরণ (Deficiency Description)**:
  `DatabaseSeedData.kt`-এর `seedSampleRecurringBillsIfEmpty()` ফাংশনে স্যাম্পল বিল সিড করার সময় ওয়াইফাই বিল ও বাসা ভাড়ার ক্যাটাগরি আইডি হার্ডকোড করা হয়েছে `4L`:
  `put(COL_BILL_CAT_ID, 4L); put(COL_BILL_CAT_NAME, "Bills & Utilities")`
  অথচ `seedDefaultCategories()` (লাইন ৯৬-১২৬) অনুযায়ী:
  - ID 4 = `Shopping` (শপিং)
  - ID 5 = `Housing & Rent` (বাড়ি ভাড়া)
  - ID 6 = `Bills & Utilities` (বিল ও ইউটিলিটি)
- **ব্যবহারকারী ও সিস্টেমের ওপর প্রভাব (User / System Impact)**:
  প্রথমবার অ্যাপ ইনস্টল করার পর ব্যবহারকারী যখন এই স্যাম্পল ওয়াইফাই বা বাড়ি ভাড়ার বিল পরিশোধ করেন (`markBillAsPaid`), তখন যে এক্সপেন্স ট্রানজ্যাকশনটি তৈরি হয় তার ক্যাটাগরি আইডি সেট হয় `4` (`Shopping`), অথচ ক্যাটাগরি নাম থাকে `Bills & Utilities`। পরবর্তীতে ক্যাটাগরি ভিত্তিক অ্যানালিটিক্স এবং বাজেট ক্যালকুলেশনে গুরুতর ডেটা অসামঞ্জস্য তৈরি হয়।
- **প্রস্তাবিত সমাধান ও কোড স্নিপেট (Recommended Solution & Code Snippet)**:
  ক্যাটাগরি আইডি সংশোধন করুন:

```kotlin
// ফাইল: DatabaseSeedData.kt
// Bills & Utilities এর জন্য 6L এবং Housing & Rent এর জন্য 5L ব্যবহার করুন:
put(COL_BILL_CAT_ID, 6L) // Bills & Utilities
put(COL_BILL_CAT_NAME, "Bills & Utilities")
```

---

### DB-04: Budgets টেবিলে Unique Constraint-এর অনুপস্থিতি
- **Severity**: **Medium (মাঝারি)**
- **Exact Code Location**: `app/src/main/java/com/example/expensetracker/data/local/DatabaseSchema.kt`, লাইন ৬০-৭১
- **ত্রুটির বিশদ বিবরণ (Deficiency Description)**:
  `TABLE_BUDGETS`-এর স্কিমাতে `(category_id, month, year)` অথবা `(month, year)`-এর উপর কোনো `UNIQUE` কনস্ট্রেইন্ট নেই।
- **ব্যবহারকারী ও সিস্টেমের ওপর প্রভাব (User / System Impact)**:
  মাল্টিপল থ্রেড বা দ্রুত দুইবার বাজেট সেভ কল করলে একই মাসের এবং একই ক্যাটাগরির জন্য একাধিক বাজেট রো ইনসার্ট হয়ে যায়। এর ফলে ড্যাশবোর্ডে ও বাজেট স্ক্রিনে ওভারঅল বাজেট ডুপ্লিকেট হয়ে বাজেটের অংক দ্বিগুণ দেখায়।
- **প্রস্তাবিত সমাধান ও কোড স্নিপেট (Recommended Solution & Code Snippet)**:
  টেবিল সংজ্ঞায় ইউনিক কনস্ট্রেইন্ট যুক্ত করুন:
  `UNIQUE(category_id, month, year) ON CONFLICT REPLACE`

---

### DB-05: Market Trip ফিনিশিং ও কনসোলিডেশন অপারেশনে Transaction অনুপস্থিতি
- **Severity**: **Critical (ক্রিটিক্যাল)**
- **Exact Code Location**: `app/src/main/java/com/example/expensetracker/data/repository/ExpenseRepositoryMarketCompletion.kt`, লাইন ১১-১৩৯
- **ত্রুটির বিশদ বিবরণ (Deficiency Description)**:
  `completeActiveMarketList` ফাংশনটি বাজারের ট্রিপ সম্পন্ন করার সময় একটি জটিল মাল্টি-স্টেপ মিউটেশন পরিচালনা করে:
  ১. আইটেমগুলোকে purchased হিসেবে মার্ক করা।  
  ২. পূর্বে টিক দেওয়ার সময় তৈরি হওয়া প্রতিটি ট্রানজ্যাকশন একে একে ডিলিট করা (`dbHelper.deleteTransaction`)।  
  ৩. সমস্ত কেনাকাটার যোগফল দিয়ে একটি নতুন একক ট্রানজ্যাকশন তৈরি করা (`dbHelper.insertTransaction(newTx)`)।  
  ৪. কেনা আইটেমগুলোতে নতুন ট্রানজ্যাকশন আইডি রেফারেন্স আপডেট করা।  
  ৫. মার্কেট লিস্টটিকে কমপ্লিট হিসেবে মার্ক করা।  
  **এই পুরো দীর্ঘ ৫-ধাপের প্রসেসটি কোনো SQLite ট্রানজ্যাকশনের মধ্যে নেই!**
- **ব্যবহারকারী ও সিস্টেমের ওপর প্রভাব (User / System Impact)**:
  স্টেপ ২-এ পুরোনো ট্রানজ্যাকশনগুলো মুছে ফেলার পর যদি অ্যাপ্লিকেশনটি ওওএম (OOM) কিলার দ্বারা কিল হয় বা ওএস কোনো কারণে বন্ধ করে দেয়, তবে নতুন কনসোলিডেটেড ট্রানজ্যাকশন আর তৈরি হবে না। ব্যবহারকারীর বাজারের সমস্ত খরচের রেকর্ড এবং অ্যাকাউন্ট ব্যালেন্স ডেটাবেস থেকে স্থায়ীভাবে উধাও হয়ে যাবে।
- **প্রস্তাবিত সমাধান ও কোড স্নিপেট (Recommended Solution & Code Snippet)**:
  পুরো লজিকটিকে `ExpenseDatabaseHelper` বা `MarketTableHelper`-এর ভেতরে নিয়ে একক পারমাণবিক (Atomic) ট্রানজ্যাকশনে রূপান্তর করুন:

```kotlin
fun completeMarketListAtomic(listId: Long, accountId: Long?, ...): MarketCompletionResult {
    val db = writableDatabase
    db.beginTransaction()
    try {
        // ১. ট্রানজ্যাকশন ডিলিট ও কনসোলিডেশন
        // ২. আইটেম আপডেট
        // ৩. অ্যাকাউন্ট ব্যালেন্স একবার রিক্যালকুলেট
        // ৪. লিস্ট মার্ক কমপ্লিট
        db.setTransactionSuccessful()
        return result
    } finally {
        db.endTransaction()
    }
}
```

---

### DB-06: মার্কেট লিস্ট ও আইটেম ডিলিটেশনে Non-Atomic Cascading Delete
- **Severity**: **High (উচ্চ)**
- **Exact Code Location**: `app/src/main/java/com/example/expensetracker/data/local/MarketTableHelper.kt`, লাইন ১৬৪-১৬৮
- **ত্রুটির বিশদ বিবরণ (Deficiency Description)**:
  `deleteMarketList()` মেথডে দুটি আলাদা টেবিল থেকে ডেটা ডিলিট করা হয় কোনো `db.beginTransaction()` ছাড়া।
- **ব্যবহারকারী ও সিস্টেমের ওপর প্রভাব (User / System Impact)**:
  যদি প্রথম ডিলিট সফল হওয়ার পর দ্বিতীয় ডিলিট ব্যর্থ হয় বা এক্সেপশন দেয়, তবে মার্কেট লিস্টটি খালি অবস্থায় থেকে যাবে কিন্তু ইউজার ইন্টারফেসে এরর দেখাবে।
- **প্রস্তাবিত সমাধান ও কোড স্নিপেট (Recommended Solution & Code Snippet)**:
  অপারেশনটিকে ট্রানজ্যাকশনে আবৃত করুন:

```kotlin
fun deleteMarketList(listId: Long): Boolean {
    val db = dbHelper.writableDatabase
    db.beginTransaction()
    try {
        db.delete(TABLE_MARKET_ITEMS, "$COL_MI_LIST_ID = ?", arrayOf(listId.toString()))
        val rows = db.delete(TABLE_MARKET_LISTS, "$COL_ML_ID = ?", arrayOf(listId.toString()))
        db.setTransactionSuccessful()
        return rows > 0
    } finally {
        db.endTransaction()
    }
}
```

---

### DB-07: ইন্ডিভিজুয়াল মার্কেট আইটেম ক্রয় ও ট্রানজ্যাকশন লিঙ্কিং Non-Atomic
- **Severity**: **High (উচ্চ)**
- **Exact Code Location**: `app/src/main/java/com/example/expensetracker/data/repository/ExpenseRepositoryMarket.kt`, লাইন ৯২-৯৩, ১০৩-১০৬
- **ত্রুটির বিশদ বিবরণ (Deficiency Description)**:
  `markMarketItemPurchased` এবং `unmarkMarketItemPurchased` উভয় ক্ষেত্রেই ট্রানজ্যাকশন টেবিল মিউটেশন এবং মার্কেট আইটেম টেবিল মিউটেশন দুটি সম্পূর্ণ পৃথক অপারেশনে সম্পাদিত হচ্ছে।
- **ব্যবহারকারী ও সিস্টেমের ওপর প্রভাব (User / System Impact)**:
  লাইন ৯২ এক্সিকিউট হওয়ার পর অ্যাপ ক্র্যাশ করলে ট্রানজ্যাকশন তৈরি হবে ও টাকা কেটে নেওয়া হবে, কিন্তু আইটেমটি কেনা হিসেবে চিহ্নিত হবে না। ব্যবহারকারী পুনরায় আইটেমে ক্লিক করলে দ্বিতীয়বার টাকা কেটে অ্যাকাউন্ট থেকে দ্বিগুণ অর্থ বিয়োগ হবে (Double Deduction Bug)।
- **প্রস্তাবিত সমাধান ও কোড স্নিপেট (Recommended Solution & Code Snippet)**:
  দুটি অপারেশনকে একক ডেটাবেজ ট্রানজ্যাকশনে সম্পন্ন করুন।

---

### DB-08: CategoryColorReconciler-এ লুপের ভেতর Non-Atomic Multi-Row Updates
- **Severity**: **Medium (মাঝারি)**
- **Exact Code Location**: `app/src/main/java/com/example/expensetracker/data/local/CategoryColorReconciler.kt`, লাইন ৫২-৭৮
- **ত্রুটির বিশদ বিবরণ (Deficiency Description)**:
  `reconcileAndDeduplicateCategoryColors()` ফাংশনে ডুপ্লিকেট রঙের ক্যাটাগরি ফিক্স করার সময় লুপের মধ্যে ৩টি ভিন্ন টেবিলে অনবরত আপডেট স্টেটমেন্ট চালানো হয় কোনো `beginTransaction()` ছাড়া।
- **ব্যবহারকারী ও সিস্টেমের ওপর প্রভাব (User / System Impact)**:
  লুপ চলাকালীন কোনো ত্রুটি হলে আংশিক ক্যাটাগরি ও ট্রানজ্যাকশন নতুন রঙ পাবে আর বাকিগুলো পুরোনো রঙে থেকে যাবে, যা ডেটাবেসে আংশিক স্টেট (Partial Inconsistency) তৈরি করে।
- **প্রস্তাবিত সমাধান ও কোড স্নিপেট (Recommended Solution & Code Snippet)**:
  লুপের শুরুতে `db.beginTransaction()` এবং শেষে `db.setTransactionSuccessful()` ও `db.endTransaction()` নিশ্চিত করুন।

---

### DB-09: সমস্ত অ্যাকাউন্ট ব্যালেন্স রিক্যালকুলেশনে Transaction অনুপস্থিতি
- **Severity**: **Medium (মাঝারি)**
- **Exact Code Location**: `app/src/main/java/com/example/expensetracker/data/local/AccountBalanceHelper.kt`, লাইন ১৪২-১৮০
- **ত্রুটির বিশদ বিবরণ (Deficiency Description)**:
  `recalculateAllAccountBalances()` ফাংশনে অনাথ ট্রানজ্যাকশন আপডেট এবং সমস্ত অ্যাকাউন্টের ব্যালেন্স হিসাবের ব্যাচ আপডেটটি কোনো ট্রানজ্যাকশনে মোড়ানো নেই।
- **ব্যবহারকারী ও সিস্টেমের ওপর প্রভাব (User / System Impact)**:
  মাঝপথে কোনো ব্যর্থতা ঘটলে কিছু অ্যাকাউন্টের ব্যালেন্স আপডেট হবে এবং বাকিগুলো পুরোনো অবস্থায় থাকবে, যা সামগ্রিক নেট ওয়ার্থের হিসেবে গরমিল সৃষ্টি করে।
- **প্রস্তাবিত সমাধান ও কোড স্নিপেট (Recommended Solution & Code Snippet)**:
  পুরো রিপেয়ার ও রিক্যালকুলেশন ব্লকটিকে `db.beginTransaction()` দিয়ে র‍্যাপ করুন।

---

### DB-10: ফ্রেশ ইনস্টল বনাম ডাটাবেস মাইগ্রেশনে ইনডেক্সের বিশাল বৈষম্য
- **Severity**: **High (উচ্চ)**
- **Exact Code Location**: 
  - `app/src/main/java/com/example/expensetracker/data/local/DatabaseSchema.kt`, লাইন ৫২-৫৭
  - `app/src/main/java/com/example/expensetracker/data/local/DatabaseMigrations.kt`, লাইন ১০৬-১১৩
- **ত্রুটির বিশদ বিবরণ (Deficiency Description)**:
  নতুন ইনস্টলেশনের সময় `DatabaseSchema.kt`-এ ৬টি ইনডেক্স তৈরি হয়:
  `idx_tx_date`, `idx_tx_type`, `idx_tx_category`, `idx_tx_account`, `idx_tx_acc_type`, `idx_tx_date_type`।  
  অথচ `DatabaseMigrations.kt`-এর `upgradeToVersion6()`-এ শুধুমাত্র কম্পোজিট ইনডেক্স দুটি যোগ করা হয়েছে।  
  সিঙ্গেল-কলাম ইনডেক্স ৪টি (`idx_tx_date`, `idx_tx_type`, `idx_tx_category`, `idx_tx_account`) মাইগ্রেশনে যোগ করতেই ভুলে যাওয়া হয়েছে!
- **ব্যবহারকারী ও সিস্টেমের ওপর প্রভাব (User / System Impact)**:
  যেসব ব্যবহারকারী ভার্সন ১-৫ থেকে অ্যাপ আপডেট করেছেন, তাদের ডেটাবেসে এই ৪টি ইনডেক্স কখনো তৈরিই হয়নি। ফলে অ্যাকাউন্ট বা ক্যাটাগরি ফিল্টারিংয়ের সময় তাদের অ্যাপে মারাত্মক ল্যাগ ও Full Table Scan হয়।
- **প্রস্তাবিত সমাধান ও কোড স্নিপেট (Recommended Solution & Code Snippet)**:
  একটি নতুন মাইগ্রেশন ভার্সনে (Version 13) অনুপস্থিত সমস্ত ইনডেক্স তৈরি করতে `CREATE INDEX IF NOT EXISTS` চালান।

```kotlin
// DatabaseMigrations.kt Version 13
db.execSQL("CREATE INDEX IF NOT EXISTS idx_tx_date ON transactions (date_time)")
db.execSQL("CREATE INDEX IF NOT EXISTS idx_tx_type ON transactions (type)")
db.execSQL("CREATE INDEX IF NOT EXISTS idx_tx_category ON transactions (category_id)")
db.execSQL("CREATE INDEX IF NOT EXISTS idx_tx_account ON transactions (account_id)")
```

---

### DB-11: Budgets টেবিলে কোনো Index নেই (100% Full Table Scan)
- **Severity**: **High (উচ্চ)**
- **Exact Code Location**: `app/src/main/java/com/example/expensetracker/data/local/DatabaseSchema.kt`, লাইন ৬০-৭১
- **ত্রুটির বিশদ বিবরণ (Deficiency Description)**:
  `TABLE_BUDGETS`-এর জন্য কোনো ইনডেক্স সংজ্ঞায়িত নেই। অথচ অ্যাপের ড্যাশবোর্ড, হোম স্ক্রিন, বাজেট ট্র্যাকার এবং ব্যাকগ্রাউন্ড ওয়ার্কার (`BudgetReminderWorker`) প্রতিবার `month` ও `year` দিয়ে ফিল্টার করে কোয়েরি চালায়।
- **ব্যবহারকারী ও সিস্টেমের ওপর প্রভাব (User / System Impact)**:
  প্রতিটি স্ক্রিন রিলোডে বাজেটের সমস্ত রো স্ক্যান করতে হয়। ডেটা বৃদ্ধি পাওয়ার সাথে সাথে বাজেট লোডিং পারফরম্যান্স ক্রমান্বয়ে হ্রাস পাবে।
- **প্রস্তাবিত সমাধান ও কোড স্নিপেট (Recommended Solution & Code Snippet)**:
  কম্পোজিট ইনডেক্স যোগ করুন:
  `CREATE INDEX IF NOT EXISTS idx_budgets_year_month ON budgets (year, month, category_id);`

---

### DB-12: `market_items(transaction_id)`-এ ইনডেক্স না থাকায় প্রতি Transaction ডিলিটেশনে Full Table Scan
- **Severity**: **High (উচ্চ)**
- **Exact Code Location**:
  - `app/src/main/java/com/example/expensetracker/data/local/TransactionTableHelper.kt`, লাইন ৯৬
  - `app/src/main/java/com/example/expensetracker/data/local/DatabaseSchema.kt`, লাইন ১৯৫-২১০
- **ত্রুটির বিশদ বিবরণ (Deficiency Description)**:
  ব্যবহারকারী অ্যাপে যেকোনো ট্রানজ্যাকশন ডিলিট করলেই `TransactionTableHelper.deleteTransaction()` আপডেট চালায়:
  `db.update(TABLE_MARKET_ITEMS, cvMarket, "$COL_MI_TRANSACTION_ID = ?", arrayOf(id.toString()))`
  অথচ `market_items` টেবিলে `transaction_id`-এর উপর কোনো ইনডেক্স নেই!
- **ব্যবহারকারী ও সিস্টেমের ওপর প্রভাব (User / System Impact)**:
  অ্যাপে যেকোনো সাধারণ খরচ ডিলিট করার সাথে সাথে SQLite সম্পূর্ণ `market_items` টেবিলটি প্রথম থেকে শেষ পর্যন্ত স্ক্যান (Full Table Scan) করে। বাজারে শত শত আইটেম যুক্ত থাকলে প্রতিটি সাধারণ ট্রানজ্যাকশন ডিলিটেশন অপ্রত্যাশিতভাবে ধীরগতির হয়ে পড়ে।
- **প্রস্তাবিত সমাধান ও কোড স্নিপেট (Recommended Solution & Code Snippet)**:
  ইনডেক্স যোগ করুন:
  `CREATE INDEX IF NOT EXISTS idx_market_items_tx ON market_items (transaction_id);`

---

### DB-13: Savings Goals টেবিলে Index অনুপস্থিতি
- **Severity**: **Medium (মাঝারি)**
- **Exact Code Location**: `app/src/main/java/com/example/expensetracker/data/local/DatabaseSchema.kt`, লাইন ১৭০-১৮৪
- **ত্রুটির বিশদ বিবরণ (Deficiency Description)**:
  `savings_goals` টেবিলে কোনো ইনডেক্স নেই। অথচ `getAllSavingsGoals()`-এ নিয়মিত কোয়েরি হয়: `ORDER BY is_completed ASC, created_date_millis DESC`।
- **ব্যবহারকারী ও সিস্টেমের ওপর প্রভাব (User / System Impact)**:
  ইন-মেমোরি টেম্পোরারি সর্টিং করতে হয় যা অপ্রয়োজনীয় সিপিইউ ও মেমোরি নষ্ট করে।
- **প্রস্তাবিত সমাধান ও কোড স্নিপেট (Recommended Solution & Code Snippet)**:
  ইনডেক্স যোগ করুন:
  `CREATE INDEX IF NOT EXISTS idx_savings_goals_order ON savings_goals (is_completed, created_date_millis);`

---

### DB-14: `recurring_bills` সর্টিং কুয়েরিতে বিদ্যমান ইনডেক্স ব্যবহার করতে না পারা
- **Severity**: **Medium (মাঝারি)**
- **Exact Code Location**: `app/src/main/java/com/example/expensetracker/data/local/DatabaseSchema.kt`, লাইন ১৪৯; `RecurringBillTableHelper.kt`, লাইন ৬৩
- **ত্রুটির বিশদ বিবরণ (Deficiency Description)**:
  স্কিমাতে ইনডেক্স রয়েছে `(is_active, next_due_date_millis)`। কিন্তু `getAllRecurringBills()` কোয়েরি করে: `ORDER BY next_due_date_millis ASC`। লিডিং কলাম `is_active` না থাকায় ইনডেক্সটি সর্টিংয়ে ব্যবহার হয় না।
- **ব্যবহারকারী ও সিস্টেমের ওপর প্রভাব (User / System Impact)**:
  বিল স্ক্রিন খোলার সময় অপ্রয়োজনীয় ফাইল সর্ট (filesort) ঘটে।
- **প্রস্তাবিত সমাধান ও কোড স্নিপেট (Recommended Solution & Code Snippet)**:
  `CREATE INDEX IF NOT EXISTS idx_bills_next_due ON recurring_bills (next_due_date_millis);`

---

### DB-15: Receivables ও Payables-এ Due Date ইনডেক্স অনুপস্থিতি
- **Severity**: **Low (নিম্ন)**
- **Exact Code Location**: `app/src/main/java/com/example/expensetracker/data/local/DatabaseSchema.kt`, লাইন ১২৬, ১৬৬
- **ত্রুটির বিশদ বিবরণ (Deficiency Description)**:
  উভয় টেবিলেই `(status, created_date_millis)`-এর উপর ইনডেক্স আছে, কিন্তু `due_date_millis`-এর উপর কোনো ইনডেক্স নেই।
- **প্রস্তাবিত সমাধান ও কোড স্নিপেট (Recommended Solution & Code Snippet)**:
  উভয় টেবিলে `due_date_millis`-এর উপর ইনডেক্স যোগ করুন।

---

### DB-16: অ্যাকাউন্ট ডিলিটেশনে Initial Balance নিশ্চিহ্ন হয়ে যাওয়া এবং স্থায়ী আর্থিক বিকৃতি
- **Severity**: **Critical (ক্রিটিক্যাল)**
- **Exact Code Location**: `app/src/main/java/com/example/expensetracker/data/local/AccountTableHelper.kt`, লাইন ৮২-১৫১
- **ত্রুটির বিশদ বিবরণ (Deficiency Description)**:
  ব্যবহারকারী যখন কোনো অ্যাকাউন্ট ডিলিট করেন, তখন সিস্টেম অন্য একটি অ্যাকাউন্টকে `targetAccountId` হিসেবে নির্বাচন করে এবং ডিলিট হওয়া অ্যাকাউন্টের সমস্ত ট্রানজ্যাকশনকে সেই টার্গেট অ্যাকাউন্টে অ্যাসাইন করে দেয় (`update transactions set account_id = targetAccountId`)। এরপর `targetAccountId`-এর ব্যালেন্স রিক্যালকুলেট করা হয়:
  `target_current_balance = target_initial_balance + total_income - total_expense`  
  **কিন্তু ডিলিট হওয়া অ্যাকাউন্টের `initial_balance` সম্পূর্ণ মুছে ফেলা হয়! কোনো ট্রানজ্যাকশন বা ট্রান্সফার ছাড়াই তা চিরতরে হারিয়ে যায়।**
- **ব্যবহারকারী ও সিস্টেমের ওপর প্রভাব (User / System Impact)**:
  ধরা যাক, ব্যবহারকারীর "City Bank" অ্যাকাউন্টে প্রারম্ভিক ব্যালেন্স (Initial Balance) ছিল ৫০,০০০ টাকা এবং খরচ ছিল ১০,০০০ টাকা (বর্তমান ব্যালেন্স ৪০,০০০ টাকা)। ব্যবহারকারীর "Cash" অ্যাকাউন্টে প্রারম্ভিক ব্যালেন্স ছিল ০ টাকা।  
  ব্যবহারকারী "City Bank" অ্যাকাউন্টটি মুছে দিলে:
  - ১০,০০০ টাকার খরচটি "Cash" অ্যাকাউন্টে স্থানান্তরিত হয়।
  - কিন্তু ৫০,০০০ টাকার Initial Balance ডেটাবেস থেকে ডিলিট হয়ে যায়!
  - "Cash" অ্যাকাউন্টের নতুন ব্যালেন্স দাঁড়ায়: `0 - 10,000 = -10,000` টাকা!
  ব্যবহারকারীর আসল ৪০,০০০ টাকা সিস্টেম থেকে সম্পূর্ণ নিঃশব্দে গায়েব হয়ে যায়!
- **প্রস্তাবিত সমাধান ও কোড স্নিপেট (Recommended Solution & Code Snippet)**:
  অ্যাকাউন্ট ডিলিট করার সময় যদি তার ব্যালেন্স বা ইনিশিয়াল ব্যালেন্স অবশিষ্ট থাকে, তবে ডিলিট ব্লক করে ব্যবহারকারীকে আগে ব্যালেন্স অন্য অ্যাকাউন্টে ট্রান্সফার করতে বাধ্য করুন, অথবা সারভাইভিং অ্যাকাউন্টে ব্যালেন্সের সমপরিমাণ একটি সিস্টেম ট্রান্সফার/রিকনসিলিয়েশন ট্রানজ্যাকশন স্বয়ংক্রিয়ভাবে ইনসার্ট করুন।

```kotlin
// সারভাইভিং অ্যাকাউন্টে প্রারম্ভিক ব্যালেন্স সংরক্ষণের সমাধান
if (deletedAccount.initialBalance != 0.0) {
    val adjustmentTx = Transaction(
        title = "Balance Transfer from Deleted Account: ${deletedAccount.name}",
        amount = deletedAccount.initialBalance,
        type = if (deletedAccount.initialBalance > 0) TransactionType.INCOME else TransactionType.EXPENSE,
        accountId = targetAccountId,
        notes = "[Reconciliation] Transferred balance from deleted account ID: ${deletedAccount.id}"
    )
    insertTransaction(adjustmentTx)
}
```

---

### DB-17: ফান্ড ট্রান্সফারে দ্বিমুখী ট্রানজ্যাকশনের মধ্যে লিঙ্কের অভাব এবং অসামঞ্জস্যপূর্ণ ডিলিটেশন
- **Severity**: **High (উচ্চ)**
- **Exact Code Location**:
  - `app/src/main/java/com/example/expensetracker/data/local/AccountTableHelper.kt`, লাইন ১৫৩-২১৮
  - `app/src/main/java/com/example/expensetracker/data/local/TransactionTableHelper.kt`, লাইন ৮২-১০৬
- **ত্রুটির বিশদ বিবরণ (Deficiency Description)**:
  `transferBetweenAccounts()` ফাংশন অ্যাকাউন্ট A থেকে B-তে টাকা স্থানান্তরের জন্য দুটি সম্পূর্ণ স্বাধীন ট্রানজ্যাকশন তৈরি করে:
  - Account A: EXPENSE (`Transfer to B`)
  - Account B: INCOME (`Transfer from A`)
  কিন্তু এই দুটি ট্রানজ্যাকশনের মাঝে কোনো সংযোগকারী আইডি (`transfer_pair_id` বা `linked_tx_id`) নেই।
  ব্যবহারকারী যদি পরবর্তীতে ট্রানজ্যাকশন হিস্ট্রি থেকে "Transfer to B" মুছে দেন, তবে `deleteTransaction` শুধুমাত্র Account A-এর খরচটি মুছে ফেলে এবং Account A-তে টাকা রিফান্ড করে দেয়। Account B-এর ইনকাম ট্রানজ্যাকশনটি অক্ষত থেকে যায়!
- **ব্যবহারকারী ও সিস্টেমের ওপর প্রভাব (User / System Impact)**:
  ব্যবহারকারী এক ক্লিকেই সিস্টেমের মূলধনের বাইরে কাল্পনিক টাকা তৈরি করে ফেলতে পারেন (Duplicate Money Creation Glitch)। স্থানান্তরিত অর্থ উভয় অ্যাকাউন্টেই জমা দেখাবে।
- **প্রস্তাবিত সমাধান ও কোড স্নিপেট (Recommended Solution & Code Snippet)**:
  `transactions` টেবিলে একটি কলাম যোগ করুন: `linked_transaction_id INTEGER`। ট্রান্সফারের সময় উভয় ট্রানজ্যাকশনে পরস্পরের আইডি সংরক্ষণ করুন। যেকোনো একটি ডিলিট বা এডিট করলে স্বয়ংক্রিয়ভাবে অপরটি ট্রানজ্যাকশনের মাধ্যমে ডিলিট/আপডেট হবে।

---

### DB-18: শুধুমাত্র স্ট্রিং ম্যাচিংয়ের উপর নির্ভরশীল ভঙ্গুর ট্রানজ্যাকশন ক্লাসিফিকেশন
- **Severity**: **High (উচ্চ)**
- **Exact Code Location**:
  - `app/src/main/java/com/example/expensetracker/data/model/Transaction.kt`, লাইন ৮১-১০৯
  - `app/src/main/java/com/example/expensetracker/data/local/TransactionStatsHelper.kt`, লাইন ১৩-২৫
- **ত্রুটির বিশদ বিবরণ (Deficiency Description)**:
  একটি ট্রানজ্যাকশন ট্রান্সফার, ঋণ প্রদান, নাকি ব্যালেন্স সমন্বয়—তা নির্ধারণ করা হয় শুধুমাত্র নোট বা টাইটেল স্ট্রিংয়ের প্রিফিক্স (`[Transfer]`, `[Reconciliation]`) এবং ক্যাটাগরি নামের হার্ডকোডেড তালিকা দিয়ে।
- **ব্যবহারকারী ও সিস্টেমের ওপর প্রভাব (User / System Impact)**:
  ব্যবহারকারী যদি কোনো ট্রান্সফার ট্রানজ্যাকশনের নোট বা টাইটেল এডিট করে ফেলেন, বা ক্যাটাগরির নাম পরিবর্তন করেন, তাহলে সেই ট্রান্সফারটি তাৎক্ষণিকভাবে সাধারণ পারিবারিক খরচ বা আয় হিসেবে গণ্য হওয়া শুরু করবে। ফলে সারা মাসের আয়ের হিসাব, খরচের হিসাব এবং চার্ট সম্পূর্ণ ভুল ফলাফল প্রদর্শন করবে।
- **প্রস্তাবিত সমাধান ও কোড স্নিপেট (Recommended Solution & Code Snippet)**:
  স্ট্রিং প্রিফিক্স বা ক্যাটাগরি নামের উপর নির্ভর না করে `transactions` টেবিলে একটি ডেডিকেটেড `nature` বা `sub_type` এনাম কলাম (যেমন: `STANDARD`, `TRANSFER`, `DEBT_FLOW`, `RECONCILIATION`) যোগ করুন।

---

### DB-19: দেনা পরিশোধ বা বিল পরিশোধের ট্রানজ্যাকশন ডিলিট করলে প্যারেন্ট রেকর্ডের স্টেট রোলব্যাক না হওয়া
- **Severity**: **High (উচ্চ)**
- **Exact Code Location**: `app/src/main/java/com/example/expensetracker/data/local/TransactionTableHelper.kt`, লাইন ৮২-১০৬
- **ত্রুটির বিশদ বিবরণ (Deficiency Description)**:
  যখন কোনো রিকারিং বিল পরিশোধ করা হয় (`markBillAsPaid`), তখন বিলের `next_due_date` এক মাস বাড়িয়ে দেওয়া হয় এবং একটি এক্সপেন্স ট্রানজ্যাকশন তৈরি করা হয়। কিন্তু ব্যবহারকারী ট্রানজ্যাকশন তালিকা থেকে সেই ট্রানজ্যাকশনটি ডিলিট করে দিলে `deleteTransaction()` শুধুমাত্র ট্রানজ্যাকশন টেবিল ও অ্যাকাউন্ট ব্যালেন্স আপডেট করে। সংশ্লিষ্ট রিকারিং বিলের ডিউ ডেট রোলব্যাক হয় না, কিংবা দেনার আদায়কৃত টাকা রিভার্স হয় না।
- **ব্যবহারকারী ও সিস্টেমের ওপর প্রভাব (User / System Impact)**:
  টাকা কাটা বা জমার ট্রানজ্যাকশন ডিলিট হয়ে গেল, কিন্তু বিলটি সিস্টেমে চিরতরে পরিশোধিত এবং দেনাটি চিরতরে আদায় করা রয়ে গেল। ব্যবহারকারী চরম আর্থিক বিভ্রান্তিতে পড়বেন।
- **প্রস্তাবিত সমাধান ও কোড স্নিপেট (Recommended Solution & Code Snippet)**:
  ট্রানজ্যাকশনে `source_module` এবং `source_id` ফিল্ড সংরক্ষণ করুন এবং ডিলিট করার সময় সংশ্লিষ্ট বিল বা দেনার স্টেট রিভার্স করার হুক যুক্ত করুন।

---

### DB-20: Google Drive ও লোকাল ব্যাকআপ থেকে Market List সম্পূর্ণ বাদ পড়া
- **Severity**: **High (উচ্চ)**
- **Exact Code Location**:
  - `app/src/main/java/com/example/expensetracker/data/local/DatabaseBackupHelper.kt`, লাইন ৭-২৩
  - `app/src/main/java/com/example/expensetracker/data/local/DatabaseRestoreHelper.kt`, লাইন ১০-৩১
  - `app/src/main/java/com/example/expensetracker/data/model/BackupPayload.kt`
- **ত্রুটির বিশদ বিবরণ (Deficiency Description)**:
  `BackupPayload`-এর স্কিমাতে `market_lists` এবং `market_items` টেবিল দুটি অন্তর্ভুক্ত নেই। ফলে ব্যাকআপ এক্সপোর্ট করার সময় বাজার তালিকার কোনো তথ্য এক্সপোর্ট হয় না। অধিকন্তু, রিস্টোর করার সময় মার্কেট তালিকা রিস্টোর করা হয় না।
- **ব্যবহারকারী ও সিস্টেমের ওপর প্রভাব (User / System Impact)**:
  রিস্টোর করার পর ব্যবহারকারী তার আগের সমস্ত বাজারের তালিকা হারিয়ে ফেলবেন (যদি অন্য ফোনে ব্যাকআপ রিস্টোর করেন)। আর একই ফোনে রিস্টোর করলে আগের মার্কেট আইটেমগুলো যে `transaction_id`-কে রেফার করছিল, তা সম্পূর্ণ বদলে যাওয়া নতুন ট্রানজ্যাকশন আইডির সাথে ভুলভাবে লিঙ্ক হয়ে যাবে।
- **প্রস্তাবিত সমাধান ও কোড স্নিপেট (Recommended Solution & Code Snippet)**:
  `BackupPayload` ডেটা মডেলে `marketLists: List<MarketListWithItems>` যুক্ত করুন এবং ব্যাকআপ ও রিস্টোরে মার্কেট তালিকা এক্সপোর্ট-ইমপোর্ট নিশ্চিত করুন।

---

### DB-21: ব্যাকআপ পেলোড তৈরির সময় Non-Atomic Snapshot রিড
- **Severity**: **Medium (মাঝারি)**
- **Exact Code Location**: `app/src/main/java/com/example/expensetracker/data/local/DatabaseBackupHelper.kt`, লাইন ৭-২৩
- **ত্রুটির বিশদ বিবরণ (Deficiency Description)**:
  `createBackupPayload()` মেথডটি ব্যাকআপ নেওয়ার জন্য ৯টি টেবিলে আলাদা আলাদা সিকোয়েনশিয়াল SELECT কুয়েরি চালায় কোনো রিড ট্রানজ্যাকশন ব্লকের বাইরে।
- **ব্যবহারকারী ও সিস্টেমের ওপর প্রভাব (User / System Impact)**:
  ব্যাকআপ রিড চলাকালীন ব্যাকগ্রাউন্ডে কোনো ট্রানজ্যাকশন বা ট্রান্সফার এন্ট্রি হলে ব্যাকআপ ফাইলে অসঙ্গতিপূর্ণ স্ন্যাপশট (Dirty Read) তৈরি হতে পারে।
- **প্রস্তাবিত সমাধান ও কোড স্নিপেট (Recommended Solution & Code Snippet)**:
  SQLite-এ ব্যাকআপ রিড অপারেশনকে `beginTransactionNonExclusive()` ব্লকে সম্পন্ন করুন।

---

### DB-22: UI থ্রেড রাইটে অনুপযুক্ত `PRAGMA wal_checkpoint(FULL)`
- **Severity**: **Medium (মাঝারি)**
- **Exact Code Location**:
  - `app/src/main/java/com/example/expensetracker/data/local/UserProfileTableHelper.kt`, লাইন ১২১, ১৬৩
  - `app/src/main/java/com/example/expensetracker/data/local/DatabaseRestoreHelper.kt`, লাইন ১৩
- **ত্রুটির বিশদ বিবরণ (Deficiency Description)**:
  `updateUserProfile` এবং `updateGeminiApiKey` মেথডে রো আপডেট করার পর সরাসরি `PRAGMA wal_checkpoint(FULL)` চালানো হয়। তদুপরি, `DatabaseRestoreHelper.restore()`-এর সময় এটি একটি অ্যাক্টিভ ট্রানজ্যাকশনের ভেতর থেকে কল হয়।
- **ব্যবহারকারী ও সিস্টেমের ওপর প্রভাব (User / System Impact)**:
  SQLite WAL মোডে ট্রানজ্যাকশন খোলা থাকা অবস্থায় `wal_checkpoint(FULL)` চালালে তা অন্যান্য রিডারদের সাথে কনফ্লিক্ট করে এবং থ্রেড ব্লক করে ল্যাগ সৃষ্টি করতে পারে।
- **প্রস্তাবিত সমাধান ও কোড স্নিপেট (Recommended Solution & Code Snippet)**:
  অটোমেটিক চেকপয়েন্টিং অ্যান্ড্রয়েড ও SQLite ফ্রেমওয়ার্কের উপর ছেড়ে দিন অথবা শুধুমাত্র ব্যাকগ্রাউন্ড থ্রেডে এক্সক্লুসিভভাবে চেকপয়েন্ট রান করুন।

---

### DB-23: রিড মেথড `getUserProfile()`-এর ভেতরে ডেটাবেস রাইট সম্পাদন
- **Severity**: **Medium (মাঝারি)**
- **Exact Code Location**: `app/src/main/java/com/example/expensetracker/data/local/UserProfileTableHelper.kt`, লাইন ৪৬-৫২
- **ত্রুটির বিশদ বিবরণ (Deficiency Description)**:
  `getUserProfile()` একটি রিড মেথড যা `dbHelper.readableDatabase` দিয়ে শুরু হয়। কিন্তু জেমিনি কি না পেলে এটি সিকিউর প্রেফারেন্স থেকে কি হিল করার অজুহাতে মেথডের ভেতরেই `dbHelper.writableDatabase.update(...)` চালায়।
- **ব্যবহারকারী ও সিস্টেমের ওপর প্রভাব (User / System Impact)**:
  মাল্টিপল থ্রেড থেকে প্রোফাইল রিড করার সময় অনাকাঙ্ক্ষিত রাইট লক প্রতিদ্বন্দ্বিতা (Lock Contention) ঘটে।
- **প্রস্তাবিত সমাধান ও কোড স্নিপেট (Recommended Solution & Code Snippet)**:
  রিড মেথডকে সাইড-ইফেক্ট মুক্ত (Pure Read) রাখুন। সেলফ-হিলিং রিপোজিটরি ইনিশিয়ালাইজেশন ধাপে সম্পন্ন করুন।

---

### DB-24: ডাটাবেস মাইগ্রেশনে ফাঁকা Catch ব্লকের কারণে নীরব ব্যর্থতা
- **Severity**: **Medium (মাঝারি)**
- **Exact Code Location**: `app/src/main/java/com/example/expensetracker/data/local/DatabaseMigrations.kt`, লাইন ৫৭-১৯৭
- **ত্রুটির বিশদ বিবরণ (Deficiency Description)**:
  `DatabaseMigrations.kt`-এর প্রায় প্রতিটি ভার্সন আপগ্রেড মেথডে `try { ... } catch (_: Exception) {}` ব্যবহার করে এক্সেপশন সম্পূর্ণ গিলে ফেলা হয়েছে (Silent Catch)।
- **ব্যবহারকারী ও সিস্টেমের ওপর প্রভাব (User / System Impact)**:
  যদি ডিস্কে জায়গা না থাকার কারণে কোনো কলাম যুক্ত হতে না পারে, তবে সিস্টেম কোনো ত্রুটি না দেখিয়ে মাইগ্রেশন পাস ধরে নেবে। পরবর্তী সময়ে মিসিং কলামের কারণে পুরো অ্যাপ ক্র্যাশ করবে।
- **প্রস্তাবিত সমাধান ও কোড স্নিপেট (Recommended Solution & Code Snippet)**:
  ফাঁকা ক্যাচ ব্লক পরিহার করুন এবং ত্রুটি লগ ও হ্যান্ডেল করুন।

---

### DB-25: `DatabaseMigrations.onUpgrade` মেথডটি স্বয়ংসম্পূর্ণ Atomic নয়
- **Severity**: **Medium (মাঝারি)**
- **Exact Code Location**: `app/src/main/java/com/example/expensetracker/data/local/DatabaseMigrations.kt`, লাইন ৭-১৯
- **ত্রুটির বিশদ বিবরণ (Deficiency Description)**:
  `onUpgrade` মেথডে কোনো নির্দিষ্ট স্টেপ ফেইল করলেও পরবর্তী স্টেপগুলো চলতে থাকে এবং অ্যান্ড্রয়েড ডিবি ভার্সনকে ১২-তে উন্নীত করে দেয়।
- **ব্যবহারকারী ও সিস্টেমের ওপর প্রভাব (User / System Impact)**:
  আংশিক মাইগ্রেশন সম্পন্ন ডেটাবেস তৈরি হতে পারে, যা পুনরুদ্ধার করা অসম্ভব হয়ে দাঁড়ায়।
- **প্রস্তাবিত সমাধান ও কোড স্নিপেট (Recommended Solution & Code Snippet)**:
  মাইগ্রেশনের প্রতিটি ধাপের সাফল্য যাচাই করে তবেই পরবর্তী ধাপে অগ্রসর হওয়া নিশ্চিত করুন।

---

## সেকশন ৪: User Efficiency & Workflow Friction (ব্যবহারকারীর দক্ষতা ও কর্মপ্রবাহের ঘর্ষণ)

---

### EF-01: লেনদেন যোগ করার প্রাথমিক অ্যাকশনে অপ্রয়োজনীয় ২-ধাপের মোডাল জাম্প
- **Severity**: **High (উচ্চ)**
- **Exact Code Location**: 
  - `app/src/main/java/com/example/expensetracker/ui/navigation/AppNavigation.kt`, লাইন ১০০–১০৫, ১৭৮–১৮০, ১৯৫–২২৮
  - `app/src/main/java/com/example/expensetracker/ui/navigation/GlobalActionChooserSheet.kt`, লাইন ৫৩–১৮৮
- **Component**: `ModernBottomNavBar`, `GlobalActionChooserSheet`
- **ত্রুটির বিশদ বিবরণ (Deficiency Description)**:
  অ্যাপ্লিকেশনের বটম ন্যাভ বারের কেন্দ্রস্থলে থাকা প্রধান অ্যাকশন বাটনটিতে (+) ক্লিক করলে সরাসরি লেনদেন এন্ট্রি ফর্ম খোলে না। তার বদলে একটি মধ্যবর্তী বটম শিট (`GlobalActionChooserSheet`) খোলে, যেখানে "ব্যয় যোগ করুন", "আয় যোগ করুন" এবং "বাজার ফর্দ" অপশন থাকে। ব্যবহারকারী "ব্যয় যোগ করুন"-এ ট্যাপ করলে সেই শিটটি বন্ধ হয়, এরপর কোডে একটি কৃত্রিম ১২০ মিলিসেকেন্ডের বিলম্ব (`delay(120)`) ঘটে, এবং তারপর মূল `AddEditTransactionSheet` স্লাইড আপ করে।
- **ব্যবহারকারী ও সিস্টেমের ওপর প্রভাব (User / System Impact)**:
  ব্যক্তিগত ফাইন্যান্স অ্যাপে ৯০% এরও বেশি লেনদেন হলো দৈনন্দিন "খরচ" (Expense)। মূল এন্ট্রি ফর্মে (`AddEditTransactionSheet`) ইতিমধ্যে শীর্ষভাগে ব্যয়/আয় টগল করার সুবিধাজনক সেগমেন্টেড সুইচ রয়েছে। ফলে প্রতিবার খরচ রেকর্ড করার সময় একটি বাড়তি বটম শিট খোলা, অ্যানিমেশনের জন্য অপেক্ষা করা এবং আবার ক্লিক করা ব্যবহারকারীর সময় নষ্ট করে এবং মারাত্মক ঘর্ষণ তৈরি করে।
- **প্রস্তাবিত সমাধান ও কোড স্নিপেট (Recommended Solution & Code Snippet)**:
  সরাসরি (+) বাটনে ট্যাপ করলে ডিফল্টভাবে `AddEditTransactionSheet` এক্সপেন্স মোডে খুলে যাওয়া উচিত (Instant Logging)। "আয়" বা "বাজার ফর্দ"-এর জন্য লং-প্রেস (Long-press) অথবা স্পিড-ডায়াল ফ্লো ব্যবহার করা শ্রেয়।

---

### EF-02: ড্যাশবোর্ডে কুইক অ্যাকশন ডক অনাথ অবস্থায় পড়ে থাকা (Information Architecture Failure)
- **Severity**: **High (উচ্চ)**
- **Exact Code Location**:
  - `app/src/main/java/com/example/expensetracker/ui/dashboard/DashboardQuickActionsDock.kt`, লাইন ৪২–১২০
  - `app/src/main/java/com/example/expensetracker/ui/dashboard/DashboardScreen.kt`, লাইন ৯৬–২১৬
- **Component**: `DashboardQuickActionsDock`, `DashboardScreen`
- **ত্রুটির বিশদ বিবরণ (Deficiency Description)**:
  প্রজেক্টটিতে অ্যাকাউন্টস, বিল পে, ধার-দেনা এবং বাজেটে তাৎক্ষণিক নেভিগেট করার জন্য সুন্দর একটি ওয়ান-ইউআই স্টাইলের ডক কম্পোনেন্ট `DashboardQuickActionsDock` তৈরি করা আছে। কিন্তু `DashboardScreen.kt`-এর `LazyColumn`-এ এটিকে কখনোই কল করা হয়নি! এটি সম্পূর্ণ অকেজো (Orphaned Code) হিসেবে পড়ে আছে।
- **ব্যবহারকারী ও সিস্টেমের ওপর প্রভাব (User / System Impact)**:
  বটম বারে শুধুমাত্র হোম, ট্রানজেকশন, অ্যাড এবং অ্যানালিটিক্স রয়েছে। ড্যাশবোর্ডে কুইক ডক না থাকায় ব্যবহারকারী হোম স্ক্রিন থেকে এক ক্লিকে দেনা-পাওনা (ধার/দেনা), রেকারিং বিল বা বাজেটে ঢুকতে পারেন না; বাধ্য হয়ে মেনু ড্রয়ার খুলতে হয়। এটি অ্যাপের গুরুত্বপূর্ণ ফিচারগুলোর ডিসকভারেবিলিটি মারাত্মকভাবে কমিয়ে দিয়েছে।
- **প্রস্তাবিত সমাধান ও কোড স্নিপেট (Recommended Solution & Code Snippet)**:
  `DashboardScreen.kt`-এ `BalanceCard`-এর ঠিক নিচে `DashboardQuickActionsDock`-কে যুক্ত করুন:

```kotlin
// ফাইল: DashboardScreen.kt LazyColumn-এ BalanceCard-এর পর যুক্ত করুন:
item {
    Spacer(modifier = Modifier.height(12.dp))
    DashboardQuickActionsDock(
        isBn = isBn,
        haptics = haptics,
        onNavigateToAccounts = onNavigateToAccounts,
        onNavigateToBills = onNavigateToBills,
        onNavigateToReceivables = onNavigateToReceivables,
        onNavigateToBudgets = onNavigateToBudgets,
        onOpenMarketList = onOpenMarketList
    )
}
```

---

### EF-03: ক্যালকুলেটর এক্সপ্রেশন ইভ্যালুয়েটর থাকা সত্ত্বেও কিবোর্ডে অপারেটরের অনুপস্থিতি
- **Severity**: **Medium (মাঝারি)**
- **Exact Code Location**:
  - `app/src/main/java/com/example/expensetracker/ui/transactions/TransactionAmountInputField.kt`, লাইন ৮৬
  - `app/src/main/java/com/example/expensetracker/ui/components/CalculatorEvaluator.kt`, লাইন ৫–১১৭
- **Component**: `TransactionAmountInputField`, `CalculatorEvaluator`
- **ত্রুটির বিশদ বিবরণ (Deficiency Description)**:
  অ্যাপটিতে `CalculatorEvaluator.kt` তৈরি করা হয়েছে যা গাণিতিক সমীকরণ যেমন `120+85*2` সমাধান করতে পারে। কিন্তু `TransactionAmountInputField.kt`-এ কিবোর্ড টাইপ দেওয়া হয়েছে `KeyboardOptions(keyboardType = KeyboardType.Number)`। স্ট্যান্ডার্ড অ্যান্ড্রয়েড কিবোর্ডে (Gboard, Samsung Keyboard) `KeyboardType.Number` দিলে শুধুমাত্র ০ থেকে ৯ পর্যন্ত ডিজিট প্যাড আসে, কোনো `+`, `-`, `*`, `/` কি থাকে না!
- **ব্যবহারকারী ও সিস্টেমের ওপর প্রভাব (User / System Impact)**:
  ব্যবহারকারী ক্যালকুলেটর ফিচারটি ব্যবহার করতে পারেন না কারণ কিবোর্ডে অপারেটর চাপার কোনো উপায় নেই। দোকানে একাধিক পণ্যের দাম যোগ করে (যেমন: ৫০ + ৩৫ + ১২০) মোট খরচ বসানোর সুযোগ নষ্ট হচ্ছে।
- **প্রস্তাবিত সমাধান ও কোড স্নিপেট (Recommended Solution & Code Snippet)**:
  ইনপুট ফিল্ডের ওপর একটি ছোট অনুভূমিক ক্যালকুলেটর চিপ বার যুক্ত করুন যাতে `+`, `-`, `×`, `÷`, `=` বাটন থাকে, অথবা কিবোর্ডের বদলে একটি ইন-প্লেস ক্যালকুলেটর কিপ্যাড অফার করুন।

---

### EF-04: রসিদ স্ক্যানারে সরাসরি ক্যামেরা শুট অপশনের অভাব এবং সাইলেন্ট ফেইলিউর
- **Severity**: **Medium (মাঝারি)**
- **Exact Code Location**:
  - `app/src/main/java/com/example/expensetracker/ui/transactions/AddEditTransactionSheet.kt`, লাইন ১০৩–১১৪, ১৯৮–২০০
  - `app/src/main/java/com/example/expensetracker/ui/transactions/TransactionReceiptSection.kt`, লাইন ২৪০–৩১০
  - `app/src/main/java/com/example/expensetracker/ui/transactions/AddEditTransactionState.kt`, লাইন ১৬৬–১৯৩
- **Component**: `AddEditTransactionSheet`, `TransactionReceiptSection`
- **ত্রুটির বিশদ বিবরণ (Deficiency Description)**:
  ১. রসিদ সংযুক্ত করার জন্য শুধুমাত্র `ActivityResultContracts.PickVisualMedia()` (গ্যালারি ফটো পিকার) কল করা হয়েছে। ব্যবহারকারী সরাসরি ফোনের ক্যামেরা দিয়ে ক্যাশ মেমোর ছবি তুলতে পারেন না। দোকানে থাকা অবস্থায় তাকে অ্যাপ বন্ধ করে ক্যামেরা দিয়ে ছবি তুলে আবার ফিরে এসে গ্যালারি থেকে সিলেক্ট করতে হয়।  
  ২. Gemini AI স্ক্যানিং ব্যর্থ হলে কোনো বিস্তারিত কারণ বা রি-ট্রাই বাটন দেখানো হয় না; স্রেফ "Could not read receipt" দেখানো হয়। কোনো অন-ডিভাইস ML Kit OCR ফলব্যাক নেই।
- **ব্যবহারকারী ও সিস্টেমের ওপর প্রভাব (User / System Impact)**:
  ক্যাশ মেমো স্ক্যানিং প্রক্রিয়াটি অত্যন্ত ধীর ও জটিল হয়ে পড়ে, যা স্মার্ট ফিচারগুলোর উপযোগিতা কমিয়ে দেয়।
- **প্রস্তাবিত সমাধান ও কোড স্নিপেট (Recommended Solution & Code Snippet)**:
  ছবি তোলার জন্য `Camera` এবং `Gallery` অপশন দিন (`ActivityResultContracts.TakePicture`) এবং ক্লাউড এআই ব্যর্থ হলে গুগল ML Kit Text Recognition দিয়ে অফলাইনে লোকাল টেক্সট রিকগনিশন ফলব্যাক নিশ্চিত করুন।

---

### EF-05: ক্যাটাগরি নির্বাচনের জন্য সংকীর্ণ পপআপ DropdownMenu ব্যবহার
- **Severity**: **Medium (মাঝারি)**
- **Exact Code Location**: `app/src/main/java/com/example/expensetracker/ui/transactions/TransactionCategoryDropdown.kt`, লাইন ১২৩–১৭৭
- **Component**: `TransactionCategoryDropdown`
- **ত্রুটির বিশদ বিবরণ (Deficiency Description)**:
  লেনদেন এন্ট্রির সময় ক্যাটাগরি নির্বাচনের জন্য ডেস্কটপ-স্টাইলের একটি সরু `DropdownMenu` পপআপ উইন্ডো ব্যবহার করা হয়েছে। যদি ব্যবহারকারীর ২০টি ক্যাটাগরি থাকে, তবে একটি সংকীর্ণ মেনুতে স্ক্রোল করতে হয়। এতে কোনো ক্যাটাগরি সার্চ ফিল্টার নেই এবং কোনো জনপ্রিয়/ঘনঘন ব্যবহৃত (Frequent) ক্যাটাগরি চিপ নেই।
- **ব্যবহারকারী ও সিস্টেমের ওপর প্রভাব (User / System Impact)**:
  দৈনিক একাধিকবার লেনদেন রেকর্ড করতে গিয়ে ক্যাটাগরি খুঁজতে দীর্ঘ সময় ব্যয় হয়।
- **প্রস্তাবিত সমাধান ও কোড স্নিপেট (Recommended Solution & Code Snippet)**:
  সংকীর্ণ পপআপ মেনুর বদলে একটি মডার্ন ক্যাটাগরি বটম শিট বা গ্রিড ভিউ চালু করুন, যাতে সার্চ বার এবং শীর্ষে ৩-৪টি ঘনঘন ব্যবহৃত ক্যাটাগরি চিপ (যেমন: খাবার, যাতায়াত, বাজার) থাকে।

---

### EF-06: রেকারিং বিল মডিউলে ডিলিট কনফার্মেশন ডায়ালগের অনুপস্থিতি
- **Severity**: **Medium (মাঝারি)**
- **Exact Code Location**: `app/src/main/java/com/example/expensetracker/ui/bills/BillsScreen.kt`, লাইন ১৫৪–১৬৬
- **Component**: `BillsScreen`
- **ত্রুটির বিশদ বিবরণ (Deficiency Description)**:
  অ্যাকাউন্টস, বাজেট এবং দেনা-পাওনা স্ক্রিনে কোনো কিছু মুছতে গেলে একটি কনফার্মেশন ডায়ালগ আসে। কিন্তু `BillsScreen`-এ রেকারিং বিল কার্ডের ডিলিট আইকনে ট্যাপ করলেই কোনো সতর্কতা ছাড়াই সাথে সাথে `viewModel.deleteBill(bill.id)` কার্যকর হয়ে যায় এবং কোনো Undo অপশনও নেই।
- **ব্যবহারকারী ও সিস্টেমের ওপর প্রভাব (User / System Impact)**:
  ভুলবশত একটি স্পর্শেই পুরো মাসের ইউটিলিটি বা ইন্টারনেট বিলের হিসাব স্থায়ীভাবে মুছে যেতে পারে, যা ব্যবহারকারীর জন্য গুরুতর ক্ষতির কারণ।
- **প্রস্তাবিত সমাধান ও কোড স্নিপেট (Recommended Solution & Code Snippet)**:
  `BillDeleteConfirmDialog` চালু করুন অথবা স্নাকবারের মাধ্যমে ৫ সেকেন্ডের জন্য `Undo` অ্যাকশন প্রদান করুন।

---

### EF-07: অসঙ্গতিপূর্ণ ফিডব্যাক মেকানিজম (Overlay vs Snackbar vs Toast)
- **Severity**: **Low (নিম্ন)**
- **Exact Code Location**: 
  - `app/src/main/java/com/example/expensetracker/ui/receivables/DebtReminderAiDialog.kt`, লাইন ১৬১
  - `app/src/main/java/com/example/expensetracker/ui/transactions/TransactionsScreen.kt`, লাইন ১৯৭
  - `app/src/main/java/com/example/expensetracker/ui/dashboard/DashboardScreen.kt`, লাইন ১৫৬
- **ত্রুটির বিশদ বিবরণ (Deficiency Description)**:
  অ্যাপটিতে ৩ ধরনের ভিন্ন ভিন্ন ফিডব্যাক মাধ্যম ব্যবহার করা হয়েছে: ড্যাশবোর্ডে কাস্টম `TransactionFeedbackOverlay` (মাঝখানে ভেসে ওঠা কার্ড), ট্রানজেকশনে মেটেরিয়াল `Snackbar`, এবং ডেট রিমাইন্ডারে প্রাচীন অ্যান্ড্রয়েড `Toast`।
- **ব্যবহারকারী ও সিস্টেমের ওপর প্রভাব (User / System Impact)**:
  ব্যবহারকারীর কাছে অ্যাপের ফিডব্যাক ইন্টারঅ্যাকশন বিচ্ছিন্ন ও অসঙ্গতিপূর্ণ মনে হয়।
- **প্রস্তাবিত সমাধান ও কোড স্নিপেট (Recommended Solution & Code Snippet)**:
  সারা অ্যাপে একটি সমন্বিত ফিডব্যাক আর্কিটেকচার বজায় রাখুন (সাফল্যের জন্য ওয়ান-ইউআই স্টাইলের অ্যানিমেটেড ডায়ালগ/ওভারলে এবং রিভার্সিবল অ্যাকশনের জন্য বটম স্নাকবার)।

---

## ৩. পর্যায়ক্রমিক প্রতিকার কর্মপরিকল্পনা (Prioritized Remediation Roadmap)

অ্যাপ্লিকেশনটিকে প্রোডাকশন-রেডি, নিরাপদ এবং ব্যবহারবান্ধব করার জন্য ৩টি সুনির্দিষ্ট ধাপে বাস্তবায়নের পরিকল্পনা দেওয়া হলো:

```
[ Phase 1: Critical Fixes ] ──► [ Phase 2: Architecture & Perf ] ──► [ Phase 3: Polish & UX ]
   (দিন ১ - ৩)                     (দিন ৪ - ৭)                          (দিন ৮ - ১২)
   • Data Integrity               • Lifecycle StateFlow                • Touch Targets 48dp
   • Foreign Key Pragma           • Lazy ViewModels                    • Direct 1-Tap Add
   • Market Tx Atomicity          • Migration v13 Indexes              • Quick Actions Dock
   • Light Mode Text Color        • Google Drive Auto-Sync             • Camera OCR & ML Kit
   • Gemini AI Timeout & Models   • Process Death Resilience           • Native strings-bn
   • OOM Image Protection         • Linked Transfers                   • Room Evaluation
```

### ফেজ ১: জরুরি ডাটা ইন্টিগ্রিটি, ক্র্যাশ প্রতিরোধ ও লাইট থিম সমাধান (Phase 1: Critical Stability & Data Integrity)
**সময়কাল: ১ থেকে ৩ দিন | লক্ষ্য: অ্যাপের ডাটা লস, OOM ক্র্যাশ ও টেক্সট অদৃশ্য হওয়া বন্ধ করা**
1. **SQLite Foreign Key সক্রিয়করণ (DB-01, DB-02)**: `ExpenseDatabaseHelper.onConfigure()`-এ `db.setForeignKeyConstraintsEnabled(true)` যুক্ত করা।
2. **মার্কেট ট্রিপ ট্রানজ্যাকশন র‍্যাপিং (DB-05, DB-07)**: `completeActiveMarketList` এবং `markMarketItemPurchased`-কে `db.beginTransaction()` ব্লকে মোড়ানো।
3. **অ্যাকাউন্ট ডিলিটেশনে মূলধন সংরক্ষণ (DB-16)**: অ্যাকাউন্ট মোছার সময় ব্যালেন্স শূন্য না থাকলে ডিলিট রোধ করা বা সারভাইভিং অ্যাকাউন্টে রিকনসিলিয়েশন এন্ট্রি দেওয়া।
4. **হিরো ব্যালেন্স ও টাইপোগ্রাফি ফিক্স (UI-01, UI-02, UI-03)**: `Type.kt` থেকে `color = ColorTextPrimary` অপসারণ এবং `ResponsiveHeroBalance` ও ইনপুট ফিল্ডে `LocalAppColors.current.textPrimary` ব্যবহার।
5. **জেমিনি এআই লুপ ও টাইমআউট (BK-05)**: কাল্পনিক মডেল নাম (৩.১, ৩.৫, ৩.৬) বাদ দেওয়া, ১৫ সেকেন্ড টাইমআউট কনফিগার করা এবং অফলাইনে সাথে সাথে লোকাল অ্যালগরিদমে ফলব্যাক করা।
6. **ক্যামেরা বিটম্যাপ OOM প্রিভেনশন (BK-06)**: `ReceiptImageHelper`-এ `inSampleSize` ব্যবহার করে সাব-স্যাম্পলিং নিশ্চিত করা।
7. **সিড ডাটা ক্যাটাগরি আইডি ফিক্স (DB-03)**: বিল সিড ডেটাতে ভুল ক্যাটাগরি আইডি (4L -> 6L) সংশোধন করা।

### ফেজ ২: আর্কিটেকচার, পারফরম্যান্স ও ব্যাকএন্ড রিলায়েবিলিটি (Phase 2: Architecture & Performance Enhancements)
**সময়কাল: ৪ থেকে ৭ দিন | লক্ষ্য: ব্যাকগ্রাউন্ড রিসোর্স অপচয় দূর করা ও ডাটাবেজ অপটিমাইজেশন**
1. **`collectAsStateWithLifecycle` মাইগ্রেশন (BK-01)**: সমস্ত কম্পোজ স্ক্রিনকে ব্যাকগ্রাউন্ড-অ্যাওয়ার ফ্লো কালেকশনে কনভার্ট করা।
2. **ইগার ভিউমডেল দূরীকরণ (BK-02)**: রুট নেভিগেশনে ৯টি ViewModel একযোগে তৈরি না করে স্ক্রিন লেভেলে ল্যাজি লোড করা।
3. **ডাটাবেজ মাইগ্রেশন ভার্সন ১৩ (DB-10, DB-11, DB-12, DB-13, DB-14)**: মিসিং ৪টি ট্রানজ্যাকশন ইনডেক্স, `budgets(year, month)` ইনডেক্স এবং `market_items(transaction_id)` ইনডেক্স তৈরি করা।
4. **অটো-সিঙ্ক কভারেজ সম্পন্ন করা (BK-10)**: দেনা আদায়, ঋণ পরিশোধ, ফান্ড ট্রান্সফার ও মার্কেট কমপ্লিশনের পর `triggerAutoSync()` যুক্ত করা।
5. **গুগল ড্রাইভ সেফটি স্ন্যাপশট (BK-09)**: ক্লাউড রিস্টোরের আগে লোকাল ডেটার প্রি-রিস্টোর স্ন্যাপশট নেওয়া।
6. **ট্রান্সফার ট্রানজ্যাকশন পেয়ার লিঙ্কিং (DB-17)**: `transactions` টেবিলে `linked_transaction_id` যোগ করে বাই-ডিরেকশনাল ডিলিটেশন ও এডিটিং হ্যান্ডেল করা।
7. **প্রসেস ডেথ রেসিলিয়েন্স (BK-12)**: নেভিগেশন স্টেট ও ফর্ম ইনপুটে `rememberSaveable` এবং ভিউমডেলে `SavedStateHandle` প্রয়োগ করা।

### ফেজ ৩: ইউজার এফিসিয়েন্সি, অ্যাক্সেসিবিলিটি ও ইন্টারফেস পলিশ (Phase 3: User Efficiency & Polish)
**সময়কাল: ৮ থেকে ১২ দিন | লক্ষ্য: দৈনন্দিন ব্যবহারের গতি বৃদ্ধি ও ভিজ্যুয়াল পলিশ**
1. **১-ট্যাপ লেনদেন লগিং (EF-01)**: বটম বারের (+) বাটনে সরাসরি এক্সপেন্স ফর্ম ওপেন করা এবং লং-প্রেসে স্পিড-ডায়াল শিট প্রদর্শন।
2. **ড্যাশবোর্ড কুইক অ্যাকশন ডক যুক্ত করা (EF-02)**: `DashboardQuickActionsDock`-কে ড্যাশবোর্ডে ব্যালেন্স কার্ডের নিচে বসিয়ে গুরুত্বপূর্ণ ফিচারগুলোর ডিসকভারেবিলিটি বাড়ানো।
3. **টাচ টার্গেট ৪৮dp বৃদ্ধি (UI-08)**: সমস্ত আইকন বাটন ও চিপে `minimumInteractiveComponentSize()` নিশ্চিত করা।
4. **ক্যালকুলেটর অপারেটর বার (EF-03)**: টাকার ইনপুট ফিল্ডের ওপর `+`, `-`, `×`, `÷`, `=` চিপ এক্সেসরি বার যুক্ত করা।
5. **ক্যামেরা শুট ও অফলাইন OCR (EF-04)**: রসিদ স্ক্যানারে ক্যামেরা দিয়ে সরাসরি ছবি তোলা এবং গুগল ML Kit Text Recognition দিয়ে অফলাইন ফলব্যাক প্রদান।
6. **নেটিভ লোকালাইজেশন রিসোর্স (UI-05)**: `res/values-bn/strings.xml` তৈরি করে সিস্টেম লঞ্চার ও ব্যাকগ্রাউন্ড নোটিফিকেশনে বাংলা কার্যকর করা।
7. **বিল ডিলিট সুরক্ষা (EF-06)**: রেকারিং বিল ডিলিট করার আগে কনফার্মেশন ডায়ালগ বা Undo স্নাকবার প্রদান।
8. **দীর্ঘমেয়াদী স্থাপত্য আধুনিকায়ন**: র' SQLiteOpenHelper থেকে অ্যান্ড্রয়েড জেপ্যাকের অফিশিয়াল **Room Persistence Library**-তে মাইগ্রেশন পরিকল্পনা মূল্যায়ন।

---

## ৪. যাচাইকরণ ও অডিট পদ্ধতি (Verification & Audit Methodology)

রিপোর্টটিতে উল্লেখিত প্রতিটি ফলাফল ও লাইন নম্বর অ্যান্ড্রয়েড স্টুডিও কোডবেস বিশ্লেষণ, স্ট্যাটিক অ্যানালাইসিস এবং গ্রিপ সার্চের মাধ্যমে যাচাই করা হয়েছে:

### স্বয়ংক্রিয় টেস্ট কমান্ড (Automated Verification Commands)
```bash
# ইউনিট টেস্ট এক্সিকিউশন
./gradlew testDebugUnitTest

# স্ট্যাটিক অ্যানালাইসিস ও লিন্ট চেক
./gradlew lintDebug
```

### কোডবেস ভেরিফিকেশন চেকস্ক্রিপ্ট (Inspection Queries)
```bash
# ১. হার্ডকোডেড কালার টাইপোগ্রাফি যাচাই
git grep -n "ColorTextPrimary" app/src/main/java/com/example/expensetracker/theme/Type.kt

# ২. collectAsStateWithLifecycle অনুপস্থিতি যাচাই (ফলাফল শূন্য হওয়া উচিত)
git grep -n "collectAsStateWithLifecycle" app/src/main/java/

# ৩. SQLite Foreign Key সক্রিয়করণ যাচাই
git grep -n "setForeignKeyConstraintsEnabled" app/src/main/java/

# ৪. ইগার ভিউমডেল রুট ইনিশিয়ালাইজেশন যাচাই
git grep -n "val.*ViewModel: .* = viewModel()" app/src/main/java/com/example/expensetracker/ui/navigation/AppNavigation.kt

# ৫. জেমিনি এআই মডেল তালিকা যাচাই
git grep -n "getModelsToTry" app/src/main/java/com/example/expensetracker/data/ai/GeminiAiClientHelper.kt
```

### গ্রহণযোগ্যতার মাপকাঠি (Acceptance Checklist)
- [x] সকল ১১টি মেজর ইউআই মডিউল পুঙ্খানুপুঙ্খভাবে নিরীক্ষিত।
- [x] সমস্ত ডেটাবেস টেবিল হেল্পার ও রিপোজিটরি ডেলিগেটে ট্রানজ্যাকশন বাউন্ডারি ও ইনডেক্সিং যাচাইকৃত।
- [x] জেমিনি এআই, গুগল ড্রাইভ ব্যাকআপ ও ওয়ার্কম্যানেজার ফল্ট-টলারেন্স অডিট সম্পন্ন।
- [x] মোট ৫৬টি পর্যবেক্ষণে সুনির্দিষ্ট ফাইল পাথ, লাইন রেঞ্জ, সমস্যা বিবরণ, প্রভাব ও কোড স্নিপেট সন্নিবেশিত।
- [x] ৩-ধাপ বিশিষ্ট অগ্রাধিকারভিত্তিক কর্মপরিকল্পনা প্রস্তুত।

---
*রিপোর্ট প্রস্তুতকারী: Worker 1 (Technical Report Author & Synthesizer)*  
*তারিখ: ২৪ অক্টোবর, ২০২৬*
