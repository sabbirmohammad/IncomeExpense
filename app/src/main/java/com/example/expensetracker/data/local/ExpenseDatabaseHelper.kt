package com.example.expensetracker.data.local

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.AccountType
import com.example.expensetracker.data.model.BackupPayload
import com.example.expensetracker.data.model.BillFrequency
import com.example.expensetracker.data.model.BillSplitData
import com.example.expensetracker.data.model.Budget
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.data.model.CategorySpend
import com.example.expensetracker.data.model.DebtBookSummary
import com.example.expensetracker.data.model.FinancialSummary
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.data.model.MarketList
import com.example.expensetracker.data.model.MarketListWithItems
import com.example.expensetracker.data.model.Payable
import com.example.expensetracker.data.model.PayableStatus
import com.example.expensetracker.data.model.PayablesSummary
import com.example.expensetracker.data.model.PaymentMethod
import com.example.expensetracker.data.model.PaymentMethodSpend
import com.example.expensetracker.data.model.Receivable
import com.example.expensetracker.data.model.ReceivableStatus
import com.example.expensetracker.data.model.ReceivablesSummary
import com.example.expensetracker.data.model.RecurringBill
import com.example.expensetracker.data.model.SavingsGoal
import com.example.expensetracker.data.model.TimeFilter
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.data.model.UserProfile
import com.example.expensetracker.ui.components.CategoryColorHelper
import java.util.Calendar

class ExpenseDatabaseHelper(private val context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "expense_tracker.db"
        const val DATABASE_VERSION = 12
        const val PREFS_NAME = "expense_tracker_secure_prefs"
        const val PREF_KEY_GEMINI_API_KEY = "gemini_api_key"
        const val PREF_KEY_PRIVACY_MODE = "is_privacy_mode_enabled"
        const val PREF_KEY_LAST_BUDGET_PROMPT = "last_budget_prompt_month_key"
        const val PREF_KEY_LAST_BUDGET_NOTIFIED = "last_budget_notified_month_key"

        // Table Categories
        const val TABLE_CATEGORIES = "categories"
        const val COL_CAT_ID = "id"
        const val COL_CAT_NAME = "name"
        const val COL_CAT_TYPE = "type"
        const val COL_CAT_ICON = "icon"
        const val COL_CAT_COLOR = "color_hex"
        const val COL_CAT_IS_DEFAULT = "is_default"

        // Table Transactions
        const val TABLE_TRANSACTIONS = "transactions"
        const val COL_TX_ID = "id"
        const val COL_TX_TITLE = "title"
        const val COL_TX_AMOUNT = "amount"
        const val COL_TX_TYPE = "type"
        const val COL_TX_CAT_ID = "category_id"
        const val COL_TX_CAT_NAME = "category_name"
        const val COL_TX_CAT_ICON = "category_icon"
        const val COL_TX_CAT_COLOR = "category_color"
        const val COL_TX_DATE = "date_millis"
        const val COL_TX_PAYMENT_METHOD = "payment_method"
        const val COL_TX_NOTES = "notes"
        const val COL_TX_ACCOUNT_ID = "account_id"
        const val COL_TX_ACCOUNT_NAME = "account_name"
        const val COL_TX_RECEIPT_PATH = "receipt_image_path"

        // Table Budgets
        const val TABLE_BUDGETS = "budgets"
        const val COL_BUDGET_ID = "id"
        const val COL_BUDGET_CAT_ID = "category_id"
        const val COL_BUDGET_CAT_NAME = "category_name"
        const val COL_BUDGET_AMOUNT = "amount"
        const val COL_BUDGET_MONTH = "month"
        const val COL_BUDGET_YEAR = "year"

        // Table User Profile
        const val TABLE_PROFILE = "user_profile"
        const val COL_PROFILE_ID = "id"
        const val COL_PROFILE_NAME = "name"
        const val COL_PROFILE_FIRST_NAME = "first_name"
        const val COL_PROFILE_LAST_NAME = "last_name"
        const val COL_PROFILE_CURRENCY_CODE = "currency_code"
        const val COL_PROFILE_CURRENCY_SYMBOL = "currency_symbol"
        const val COL_PROFILE_AVATAR = "avatar_index"
        const val COL_PROFILE_GOOGLE_EMAIL = "google_email"
        const val COL_PROFILE_AUTO_SYNC = "auto_sync"
        const val COL_PROFILE_LAST_SYNC = "last_sync"
        const val COL_PROFILE_LANGUAGE = "app_language"
        const val COL_PROFILE_GEMINI_KEY = "gemini_api_key"
        const val COL_PROFILE_AI_INSIGHTS_ENABLED = "ai_insights_enabled"
        const val COL_PROFILE_THEME = "app_theme"
        const val COL_PROFILE_BENGALI_NUMERALS = "use_bengali_numerals"

        // Table Accounts
        const val TABLE_ACCOUNTS = "accounts"
        const val COL_ACC_ID = "id"
        const val COL_ACC_NAME = "name"
        const val COL_ACC_TYPE = "type"
        const val COL_ACC_NUMBER = "account_number"
        const val COL_ACC_INSTITUTION = "institution_name"
        const val COL_ACC_INITIAL_BALANCE = "initial_balance"
        const val COL_ACC_CURRENT_BALANCE = "current_balance"
        const val COL_ACC_COLOR = "color_hex"
        const val COL_ACC_IS_DEFAULT = "is_default"
        const val COL_ACC_CREATED_AT = "created_at"

        // Table Receivables
        const val TABLE_RECEIVABLES = "receivables"
        const val COL_REC_ID = "id"
        const val COL_REC_PERSON_NAME = "person_name"
        const val COL_REC_PHONE = "phone_number"
        const val COL_REC_AMOUNT = "amount"
        const val COL_REC_AMOUNT_RECEIVED = "amount_received"
        const val COL_REC_DUE_DATE = "due_date_millis"
        const val COL_REC_CREATED_DATE = "created_date_millis"
        const val COL_REC_NOTES = "notes"
        const val COL_REC_STATUS = "status"

        // Table Recurring Bills
        const val TABLE_RECURRING_BILLS = "recurring_bills"
        const val COL_BILL_ID = "id"
        const val COL_BILL_TITLE = "title"
        const val COL_BILL_AMOUNT = "amount"
        const val COL_BILL_CAT_ID = "category_id"
        const val COL_BILL_CAT_NAME = "category_name"
        const val COL_BILL_CAT_ICON = "category_icon"
        const val COL_BILL_CAT_COLOR = "category_color"
        const val COL_BILL_ACCOUNT_ID = "account_id"
        const val COL_BILL_ACCOUNT_NAME = "account_name"
        const val COL_BILL_FREQUENCY = "frequency"
        const val COL_BILL_DUE_DAY = "due_day"
        const val COL_BILL_NEXT_DUE = "next_due_date_millis"
        const val COL_BILL_LAST_PAID = "last_paid_date_millis"
        const val COL_BILL_IS_ACTIVE = "is_active"
        const val COL_BILL_NOTES = "notes"

        // Table Payables
        const val TABLE_PAYABLES = "payables"
        const val COL_PAY_ID = "id"
        const val COL_PAY_PERSON_NAME = "person_name"
        const val COL_PAY_PHONE = "phone_number"
        const val COL_PAY_AMOUNT = "amount"
        const val COL_PAY_AMOUNT_PAID = "amount_paid"
        const val COL_PAY_DUE_DATE = "due_date_millis"
        const val COL_PAY_CREATED_DATE = "created_date_millis"
        const val COL_PAY_NOTES = "notes"
        const val COL_PAY_STATUS = "status"

        // Table Savings Goals
        const val TABLE_SAVINGS_GOALS = "savings_goals"
        const val COL_GOAL_ID = "id"
        const val COL_GOAL_TITLE = "title"
        const val COL_GOAL_TARGET_AMOUNT = "target_amount"
        const val COL_GOAL_SAVED_AMOUNT = "saved_amount"
        const val COL_GOAL_TARGET_DATE = "target_date_millis"
        const val COL_GOAL_CREATED_DATE = "created_date_millis"
        const val COL_GOAL_ICON = "icon"
        const val COL_GOAL_COLOR = "color_hex"
        const val COL_GOAL_NOTES = "notes"
        const val COL_GOAL_IS_COMPLETED = "is_completed"

        // Table Market Lists
        const val TABLE_MARKET_LISTS = "market_lists"
        const val COL_ML_ID = "id"
        const val COL_ML_TITLE = "title"
        const val COL_ML_CREATED_AT = "created_at"
        const val COL_ML_IS_COMPLETED = "is_completed"

        // Table Market Items
        const val TABLE_MARKET_ITEMS = "market_items"
        const val COL_MI_ID = "id"
        const val COL_MI_LIST_ID = "list_id"
        const val COL_MI_NAME = "name"
        const val COL_MI_QUANTITY = "quantity"
        const val COL_MI_UNIT = "unit"
        const val COL_MI_AMOUNT = "amount"
        const val COL_MI_IS_PURCHASED = "is_purchased"
        const val COL_MI_TRANSACTION_ID = "transaction_id"
        const val COL_MI_CREATED_AT = "created_at"
    }

    private val secureStorageHelper by lazy { SecureStorageHelper(context) }

    private fun getPersistedApiKey(): String? {
        return secureStorageHelper.getGeminiApiKey()
    }

    private fun persistApiKey(key: String?) {
        secureStorageHelper.saveGeminiApiKey(key)
    }

    fun isPrivacyModeEnabled(): Boolean {
        return try {
            val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.getBoolean(PREF_KEY_PRIVACY_MODE, false)
        } catch (_: Exception) {
            false
        }
    }

    fun setPrivacyModeEnabled(enabled: Boolean) {
        try {
            val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putBoolean(PREF_KEY_PRIVACY_MODE, enabled).apply()
        } catch (_: Exception) {}
    }

    fun getLastBudgetPromptKey(): String? {
        return try {
            val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.getString(PREF_KEY_LAST_BUDGET_PROMPT, null)
        } catch (_: Exception) {
            null
        }
    }

    fun setLastBudgetPromptKey(key: String) {
        try {
            val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putString(PREF_KEY_LAST_BUDGET_PROMPT, key).apply()
        } catch (_: Exception) {}
    }

    fun getLastBudgetNotifiedKey(): String? {
        return try {
            val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.getString(PREF_KEY_LAST_BUDGET_NOTIFIED, null)
        } catch (_: Exception) {
            null
        }
    }

    fun setLastBudgetNotifiedKey(key: String) {
        try {
            val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putString(PREF_KEY_LAST_BUDGET_NOTIFIED, key).apply()
        } catch (_: Exception) {}
    }

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.enableWriteAheadLogging()
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Create Categories
        db.execSQL("""
            CREATE TABLE $TABLE_CATEGORIES (
                $COL_CAT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_CAT_NAME TEXT NOT NULL,
                $COL_CAT_TYPE TEXT NOT NULL,
                $COL_CAT_ICON TEXT NOT NULL,
                $COL_CAT_COLOR TEXT NOT NULL,
                $COL_CAT_IS_DEFAULT INTEGER NOT NULL
            )
        """.trimIndent())

        // Create Transactions
        db.execSQL("""
            CREATE TABLE $TABLE_TRANSACTIONS (
                $COL_TX_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_TX_TITLE TEXT NOT NULL,
                $COL_TX_AMOUNT REAL NOT NULL,
                $COL_TX_TYPE TEXT NOT NULL,
                $COL_TX_CAT_ID INTEGER NOT NULL,
                $COL_TX_CAT_NAME TEXT NOT NULL,
                $COL_TX_CAT_ICON TEXT NOT NULL,
                $COL_TX_CAT_COLOR TEXT NOT NULL,
                $COL_TX_DATE INTEGER NOT NULL,
                $COL_TX_PAYMENT_METHOD TEXT NOT NULL,
                $COL_TX_NOTES TEXT NOT NULL,
                $COL_TX_ACCOUNT_ID INTEGER,
                $COL_TX_ACCOUNT_NAME TEXT,
                $COL_TX_RECEIPT_PATH TEXT
            )
        """.trimIndent())

        db.execSQL("CREATE INDEX IF NOT EXISTS idx_tx_date ON $TABLE_TRANSACTIONS ($COL_TX_DATE)")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_tx_type ON $TABLE_TRANSACTIONS ($COL_TX_TYPE)")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_tx_category ON $TABLE_TRANSACTIONS ($COL_TX_CAT_ID)")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_tx_account ON $TABLE_TRANSACTIONS ($COL_TX_ACCOUNT_ID)")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_tx_acc_type ON $TABLE_TRANSACTIONS ($COL_TX_ACCOUNT_ID, $COL_TX_TYPE)")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_tx_date_type ON $TABLE_TRANSACTIONS ($COL_TX_DATE, $COL_TX_TYPE)")

        // Create Budgets
        db.execSQL("""
            CREATE TABLE $TABLE_BUDGETS (
                $COL_BUDGET_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_BUDGET_CAT_ID INTEGER,
                $COL_BUDGET_CAT_NAME TEXT NOT NULL,
                $COL_BUDGET_AMOUNT REAL NOT NULL,
                $COL_BUDGET_MONTH INTEGER NOT NULL,
                $COL_BUDGET_YEAR INTEGER NOT NULL
            )
        """.trimIndent())

        // Create Profile
        db.execSQL("""
            CREATE TABLE $TABLE_PROFILE (
                $COL_PROFILE_ID INTEGER PRIMARY KEY CHECK ($COL_PROFILE_ID = 1),
                $COL_PROFILE_NAME TEXT NOT NULL,
                $COL_PROFILE_FIRST_NAME TEXT NOT NULL DEFAULT '',
                $COL_PROFILE_LAST_NAME TEXT NOT NULL DEFAULT '',
                $COL_PROFILE_CURRENCY_CODE TEXT NOT NULL,
                $COL_PROFILE_CURRENCY_SYMBOL TEXT NOT NULL,
                $COL_PROFILE_AVATAR INTEGER NOT NULL,
                $COL_PROFILE_GOOGLE_EMAIL TEXT,
                $COL_PROFILE_AUTO_SYNC INTEGER NOT NULL,
                $COL_PROFILE_LAST_SYNC INTEGER NOT NULL,
                $COL_PROFILE_LANGUAGE TEXT NOT NULL DEFAULT 'bn',
                $COL_PROFILE_GEMINI_KEY TEXT,
                $COL_PROFILE_AI_INSIGHTS_ENABLED INTEGER NOT NULL DEFAULT 1,
                $COL_PROFILE_THEME TEXT NOT NULL DEFAULT 'dark',
                $COL_PROFILE_BENGALI_NUMERALS INTEGER NOT NULL DEFAULT 1
            )
        """.trimIndent())

        // Create Accounts
        db.execSQL("""
            CREATE TABLE $TABLE_ACCOUNTS (
                $COL_ACC_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_ACC_NAME TEXT NOT NULL,
                $COL_ACC_TYPE TEXT NOT NULL,
                $COL_ACC_NUMBER TEXT NOT NULL DEFAULT '',
                $COL_ACC_INSTITUTION TEXT NOT NULL DEFAULT '',
                $COL_ACC_INITIAL_BALANCE REAL NOT NULL DEFAULT 0.0,
                $COL_ACC_CURRENT_BALANCE REAL NOT NULL DEFAULT 0.0,
                $COL_ACC_COLOR TEXT NOT NULL DEFAULT '#3B8FF0',
                $COL_ACC_IS_DEFAULT INTEGER NOT NULL DEFAULT 0,
                $COL_ACC_CREATED_AT INTEGER NOT NULL DEFAULT 0
            )
        """.trimIndent())

        // Create Receivables
        db.execSQL("""
            CREATE TABLE $TABLE_RECEIVABLES (
                $COL_REC_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_REC_PERSON_NAME TEXT NOT NULL,
                $COL_REC_PHONE TEXT NOT NULL DEFAULT '',
                $COL_REC_AMOUNT REAL NOT NULL,
                $COL_REC_AMOUNT_RECEIVED REAL NOT NULL DEFAULT 0.0,
                $COL_REC_DUE_DATE INTEGER,
                $COL_REC_CREATED_DATE INTEGER NOT NULL,
                $COL_REC_NOTES TEXT NOT NULL DEFAULT '',
                $COL_REC_STATUS TEXT NOT NULL DEFAULT 'PENDING'
            )
        """.trimIndent())

        db.execSQL("CREATE INDEX IF NOT EXISTS idx_rec_status_date ON $TABLE_RECEIVABLES ($COL_REC_STATUS, $COL_REC_CREATED_DATE)")

        // Create Recurring Bills
        db.execSQL("""
            CREATE TABLE $TABLE_RECURRING_BILLS (
                $COL_BILL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_BILL_TITLE TEXT NOT NULL,
                $COL_BILL_AMOUNT REAL NOT NULL,
                $COL_BILL_CAT_ID INTEGER NOT NULL,
                $COL_BILL_CAT_NAME TEXT NOT NULL,
                $COL_BILL_CAT_ICON TEXT NOT NULL,
                $COL_BILL_CAT_COLOR TEXT NOT NULL,
                $COL_BILL_ACCOUNT_ID INTEGER,
                $COL_BILL_ACCOUNT_NAME TEXT,
                $COL_BILL_FREQUENCY TEXT NOT NULL DEFAULT 'MONTHLY',
                $COL_BILL_DUE_DAY INTEGER NOT NULL DEFAULT 1,
                $COL_BILL_NEXT_DUE INTEGER NOT NULL,
                $COL_BILL_LAST_PAID INTEGER,
                $COL_BILL_IS_ACTIVE INTEGER NOT NULL DEFAULT 1,
                $COL_BILL_NOTES TEXT NOT NULL DEFAULT ''
            )
        """.trimIndent())

        db.execSQL("CREATE INDEX IF NOT EXISTS idx_bills_due_active ON $TABLE_RECURRING_BILLS ($COL_BILL_IS_ACTIVE, $COL_BILL_NEXT_DUE)")

        // Create Payables
        db.execSQL("""
            CREATE TABLE $TABLE_PAYABLES (
                $COL_PAY_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_PAY_PERSON_NAME TEXT NOT NULL,
                $COL_PAY_PHONE TEXT NOT NULL DEFAULT '',
                $COL_PAY_AMOUNT REAL NOT NULL,
                $COL_PAY_AMOUNT_PAID REAL NOT NULL DEFAULT 0.0,
                $COL_PAY_DUE_DATE INTEGER,
                $COL_PAY_CREATED_DATE INTEGER NOT NULL,
                $COL_PAY_NOTES TEXT NOT NULL DEFAULT '',
                $COL_PAY_STATUS TEXT NOT NULL DEFAULT 'PENDING'
            )
        """.trimIndent())

        db.execSQL("CREATE INDEX IF NOT EXISTS idx_pay_status_date ON $TABLE_PAYABLES ($COL_PAY_STATUS, $COL_PAY_CREATED_DATE)")

        // Create Savings Goals
        db.execSQL("""
            CREATE TABLE $TABLE_SAVINGS_GOALS (
                $COL_GOAL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_GOAL_TITLE TEXT NOT NULL,
                $COL_GOAL_TARGET_AMOUNT REAL NOT NULL,
                $COL_GOAL_SAVED_AMOUNT REAL NOT NULL DEFAULT 0.0,
                $COL_GOAL_TARGET_DATE INTEGER,
                $COL_GOAL_CREATED_DATE INTEGER NOT NULL,
                $COL_GOAL_ICON TEXT NOT NULL DEFAULT 'savings',
                $COL_GOAL_COLOR TEXT NOT NULL DEFAULT '#10B981',
                $COL_GOAL_NOTES TEXT NOT NULL DEFAULT '',
                $COL_GOAL_IS_COMPLETED INTEGER NOT NULL DEFAULT 0
            )
        """.trimIndent())

        // Create Market Lists
        db.execSQL("""
            CREATE TABLE $TABLE_MARKET_LISTS (
                $COL_ML_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_ML_TITLE TEXT NOT NULL,
                $COL_ML_CREATED_AT INTEGER NOT NULL,
                $COL_ML_IS_COMPLETED INTEGER NOT NULL DEFAULT 0
            )
        """.trimIndent())

        // Create Market Items
        db.execSQL("""
            CREATE TABLE $TABLE_MARKET_ITEMS (
                $COL_MI_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_MI_LIST_ID INTEGER NOT NULL,
                $COL_MI_NAME TEXT NOT NULL,
                $COL_MI_QUANTITY REAL NOT NULL DEFAULT 1.0,
                $COL_MI_UNIT TEXT NOT NULL DEFAULT 'কেজি',
                $COL_MI_AMOUNT REAL,
                $COL_MI_IS_PURCHASED INTEGER NOT NULL DEFAULT 0,
                $COL_MI_TRANSACTION_ID INTEGER,
                $COL_MI_CREATED_AT INTEGER NOT NULL
            )
        """.trimIndent())

        db.execSQL("CREATE INDEX IF NOT EXISTS idx_market_items_list ON $TABLE_MARKET_ITEMS ($COL_MI_LIST_ID)")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_market_lists_completed ON $TABLE_MARKET_LISTS ($COL_ML_IS_COMPLETED)")

        seedInitialData(db)
        seedSampleRecurringBillsIfEmpty(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE $TABLE_PROFILE ADD COLUMN $COL_PROFILE_FIRST_NAME TEXT NOT NULL DEFAULT ''")
            db.execSQL("ALTER TABLE $TABLE_PROFILE ADD COLUMN $COL_PROFILE_LAST_NAME TEXT NOT NULL DEFAULT ''")
            db.execSQL("UPDATE $TABLE_PROFILE SET $COL_PROFILE_FIRST_NAME = $COL_PROFILE_NAME WHERE $COL_PROFILE_FIRST_NAME = ''")
        }
        if (oldVersion < 3) {
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS $TABLE_ACCOUNTS (
                    $COL_ACC_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                    $COL_ACC_NAME TEXT NOT NULL,
                    $COL_ACC_TYPE TEXT NOT NULL,
                    $COL_ACC_NUMBER TEXT NOT NULL DEFAULT '',
                    $COL_ACC_INSTITUTION TEXT NOT NULL DEFAULT '',
                    $COL_ACC_INITIAL_BALANCE REAL NOT NULL DEFAULT 0.0,
                    $COL_ACC_CURRENT_BALANCE REAL NOT NULL DEFAULT 0.0,
                    $COL_ACC_COLOR TEXT NOT NULL DEFAULT '#3B8FF0',
                    $COL_ACC_IS_DEFAULT INTEGER NOT NULL DEFAULT 0,
                    $COL_ACC_CREATED_AT INTEGER NOT NULL DEFAULT 0
                )
            """.trimIndent())

            db.execSQL("""
                CREATE TABLE IF NOT EXISTS $TABLE_RECEIVABLES (
                    $COL_REC_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                    $COL_REC_PERSON_NAME TEXT NOT NULL,
                    $COL_REC_PHONE TEXT NOT NULL DEFAULT '',
                    $COL_REC_AMOUNT REAL NOT NULL,
                    $COL_REC_AMOUNT_RECEIVED REAL NOT NULL DEFAULT 0.0,
                    $COL_REC_DUE_DATE INTEGER,
                    $COL_REC_CREATED_DATE INTEGER NOT NULL,
                    $COL_REC_NOTES TEXT NOT NULL DEFAULT '',
                    $COL_REC_STATUS TEXT NOT NULL DEFAULT 'PENDING'
                )
            """.trimIndent())

            try {
                db.execSQL("ALTER TABLE $TABLE_TRANSACTIONS ADD COLUMN $COL_TX_ACCOUNT_ID INTEGER")
            } catch (_: Exception) {}
            try {
                db.execSQL("ALTER TABLE $TABLE_TRANSACTIONS ADD COLUMN $COL_TX_ACCOUNT_NAME TEXT")
            } catch (_: Exception) {}

            seedDefaultAccountsIfEmpty(db)
            backfillTransactionAccounts(db)
            recalculateAllAccountBalances(db)
        }
        if (oldVersion < 4) {
            try {
                db.execSQL("ALTER TABLE $TABLE_PROFILE ADD COLUMN $COL_PROFILE_LANGUAGE TEXT NOT NULL DEFAULT 'bn'")
            } catch (_: Exception) {}

            db.execSQL("""
                CREATE TABLE IF NOT EXISTS $TABLE_RECURRING_BILLS (
                    $COL_BILL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                    $COL_BILL_TITLE TEXT NOT NULL,
                    $COL_BILL_AMOUNT REAL NOT NULL,
                    $COL_BILL_CAT_ID INTEGER NOT NULL,
                    $COL_BILL_CAT_NAME TEXT NOT NULL,
                    $COL_BILL_CAT_ICON TEXT NOT NULL,
                    $COL_BILL_CAT_COLOR TEXT NOT NULL,
                    $COL_BILL_ACCOUNT_ID INTEGER,
                    $COL_BILL_ACCOUNT_NAME TEXT,
                    $COL_BILL_FREQUENCY TEXT NOT NULL DEFAULT 'MONTHLY',
                    $COL_BILL_DUE_DAY INTEGER NOT NULL DEFAULT 1,
                    $COL_BILL_NEXT_DUE INTEGER NOT NULL,
                    $COL_BILL_LAST_PAID INTEGER,
                    $COL_BILL_IS_ACTIVE INTEGER NOT NULL DEFAULT 1,
                    $COL_BILL_NOTES TEXT NOT NULL DEFAULT ''
                )
            """.trimIndent())

            seedSampleRecurringBillsIfEmpty(db)
        }
        if (oldVersion < 5) {
            try {
                db.execSQL("ALTER TABLE $TABLE_PROFILE ADD COLUMN $COL_PROFILE_GEMINI_KEY TEXT")
            } catch (_: Exception) {}
            try {
                db.execSQL("ALTER TABLE $TABLE_PROFILE ADD COLUMN $COL_PROFILE_AI_INSIGHTS_ENABLED INTEGER NOT NULL DEFAULT 1")
            } catch (_: Exception) {}
        }
        if (oldVersion < 6) {
            try {
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_tx_acc_type ON $TABLE_TRANSACTIONS ($COL_TX_ACCOUNT_ID, $COL_TX_TYPE)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_tx_date_type ON $TABLE_TRANSACTIONS ($COL_TX_DATE, $COL_TX_TYPE)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_rec_status_date ON $TABLE_RECEIVABLES ($COL_REC_STATUS, $COL_REC_CREATED_DATE)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_bills_due_active ON $TABLE_RECURRING_BILLS ($COL_BILL_IS_ACTIVE, $COL_BILL_NEXT_DUE)")
            } catch (_: Exception) {}
        }
        if (oldVersion < 7) {
            try {
                db.execSQL("ALTER TABLE $TABLE_PROFILE ADD COLUMN $COL_PROFILE_THEME TEXT NOT NULL DEFAULT 'dark'")
            } catch (_: Exception) {}
            try {
                db.execSQL("ALTER TABLE $TABLE_PROFILE ADD COLUMN $COL_PROFILE_BENGALI_NUMERALS INTEGER NOT NULL DEFAULT 0")
            } catch (_: Exception) {}
        }
        if (oldVersion < 8) {
            try {
                db.execSQL("UPDATE $TABLE_PROFILE SET $COL_PROFILE_BENGALI_NUMERALS = 1 WHERE $COL_PROFILE_LANGUAGE = 'bn'")
            } catch (_: Exception) {}
        }
        if (oldVersion < 9) {
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS $TABLE_PAYABLES (
                    $COL_PAY_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                    $COL_PAY_PERSON_NAME TEXT NOT NULL,
                    $COL_PAY_PHONE TEXT NOT NULL DEFAULT '',
                    $COL_PAY_AMOUNT REAL NOT NULL,
                    $COL_PAY_AMOUNT_PAID REAL NOT NULL DEFAULT 0.0,
                    $COL_PAY_DUE_DATE INTEGER,
                    $COL_PAY_CREATED_DATE INTEGER NOT NULL,
                    $COL_PAY_NOTES TEXT NOT NULL DEFAULT '',
                    $COL_PAY_STATUS TEXT NOT NULL DEFAULT 'PENDING'
                )
            """.trimIndent())
            try {
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_pay_status_date ON $TABLE_PAYABLES ($COL_PAY_STATUS, $COL_PAY_CREATED_DATE)")
            } catch (_: Exception) {}

            db.execSQL("""
                CREATE TABLE IF NOT EXISTS $TABLE_SAVINGS_GOALS (
                    $COL_GOAL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                    $COL_GOAL_TITLE TEXT NOT NULL,
                    $COL_GOAL_TARGET_AMOUNT REAL NOT NULL,
                    $COL_GOAL_SAVED_AMOUNT REAL NOT NULL DEFAULT 0.0,
                    $COL_GOAL_TARGET_DATE INTEGER,
                    $COL_GOAL_CREATED_DATE INTEGER NOT NULL,
                    $COL_GOAL_ICON TEXT NOT NULL DEFAULT 'savings',
                    $COL_GOAL_COLOR TEXT NOT NULL DEFAULT '#10B981',
                    $COL_GOAL_NOTES TEXT NOT NULL DEFAULT '',
                    $COL_GOAL_IS_COMPLETED INTEGER NOT NULL DEFAULT 0
                )
            """.trimIndent())
        }

        if (oldVersion < 10) {
            try {
                db.execSQL("ALTER TABLE $TABLE_TRANSACTIONS ADD COLUMN $COL_TX_RECEIPT_PATH TEXT")
            } catch (_: Exception) {}
        }

        if (oldVersion < 11) {
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS $TABLE_MARKET_LISTS (
                    $COL_ML_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                    $COL_ML_TITLE TEXT NOT NULL,
                    $COL_ML_CREATED_AT INTEGER NOT NULL,
                    $COL_ML_IS_COMPLETED INTEGER NOT NULL DEFAULT 0
                )
            """.trimIndent())

            db.execSQL("""
                CREATE TABLE IF NOT EXISTS $TABLE_MARKET_ITEMS (
                    $COL_MI_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                    $COL_MI_LIST_ID INTEGER NOT NULL,
                    $COL_MI_NAME TEXT NOT NULL,
                    $COL_MI_QUANTITY REAL NOT NULL DEFAULT 1.0,
                    $COL_MI_UNIT TEXT NOT NULL DEFAULT 'কেজি',
                    $COL_MI_AMOUNT REAL,
                    $COL_MI_IS_PURCHASED INTEGER NOT NULL DEFAULT 0,
                    $COL_MI_TRANSACTION_ID INTEGER,
                    $COL_MI_CREATED_AT INTEGER NOT NULL
                )
            """.trimIndent())

            try {
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_market_items_list ON $TABLE_MARKET_ITEMS ($COL_MI_LIST_ID)")
                db.execSQL("CREATE INDEX IF NOT EXISTS idx_market_lists_completed ON $TABLE_MARKET_LISTS ($COL_ML_IS_COMPLETED)")
            } catch (_: Exception) {}
        }

        if (oldVersion < 12) {
            reconcileAndDeduplicateCategoryColors(db)
        }
    }

    private fun seedSampleRecurringBillsIfEmpty(db: SQLiteDatabase) {
        val cursor = db.rawQuery("SELECT COUNT(*) FROM $TABLE_RECURRING_BILLS", null)
        var count = 0
        cursor.use {
            if (it.moveToFirst()) count = it.getInt(0)
        }
        if (count == 0) {
            val now = System.currentTimeMillis()
            val cal = Calendar.getInstance()
            val currentDay = cal.get(Calendar.DAY_OF_MONTH)

            // WiFi Bill (Category 4: Bills & Utilities)
            val cvWifi = ContentValues().apply {
                put(COL_BILL_TITLE, "WiFi Internet Bill")
                put(COL_BILL_AMOUNT, 1000.0)
                put(COL_BILL_CAT_ID, 4L)
                put(COL_BILL_CAT_NAME, "Bills & Utilities")
                put(COL_BILL_CAT_ICON, "Receipt")
                put(COL_BILL_CAT_COLOR, "#F0A63D")
                put(COL_BILL_ACCOUNT_ID, 3L) // bKash
                put(COL_BILL_ACCOUNT_NAME, "bKash Personal")
                put(COL_BILL_FREQUENCY, "MONTHLY")
                put(COL_BILL_DUE_DAY, 5)
                put(COL_BILL_NEXT_DUE, now + (3L * 24 * 3600 * 1000))
                put(COL_BILL_IS_ACTIVE, 1)
                put(COL_BILL_NOTES, "Dot Internet Fiber Monthly")
            }
            db.insert(TABLE_RECURRING_BILLS, null, cvWifi)

            // Electricity / House Rent (Category 4: Bills & Utilities)
            val cvRent = ContentValues().apply {
                put(COL_BILL_TITLE, "House Rent / বাড়ি ভাড়া")
                put(COL_BILL_AMOUNT, 15000.0)
                put(COL_BILL_CAT_ID, 4L)
                put(COL_BILL_CAT_NAME, "Bills & Utilities")
                put(COL_BILL_CAT_ICON, "Receipt")
                put(COL_BILL_CAT_COLOR, "#3B8FF0")
                put(COL_BILL_ACCOUNT_ID, 2L) // Bank
                put(COL_BILL_ACCOUNT_NAME, "Bank Account")
                put(COL_BILL_FREQUENCY, "MONTHLY")
                put(COL_BILL_DUE_DAY, 7)
                put(COL_BILL_NEXT_DUE, now + (5L * 24 * 3600 * 1000))
                put(COL_BILL_IS_ACTIVE, 1)
                put(COL_BILL_NOTES, "Apartment Monthly Rent")
            }
            db.insert(TABLE_RECURRING_BILLS, null, cvRent)
        }
    }

    private fun seedDefaultAccountsIfEmpty(db: SQLiteDatabase) {
        val cursor = db.rawQuery("SELECT COUNT(*) FROM $TABLE_ACCOUNTS", null)
        var count = 0
        cursor.use {
            if (it.moveToFirst()) count = it.getInt(0)
        }
        if (count == 0) {
            val now = System.currentTimeMillis()
            val accounts = listOf(
                Account(1, "Cash in Hand", AccountType.CASH, "", "Cash", 5000.0, 5000.0, "#2EC4B6", true, now),
                Account(2, "Bank Account", AccountType.BANK, "**** 4589", "City Bank", 50000.0, 50000.0, "#3B8FF0", false, now),
                Account(3, "bKash Personal", AccountType.MFS, "017XXXXXXXX", "bKash", 10000.0, 10000.0, "#E056FD", false, now),
                Account(4, "Nagad Personal", AccountType.MFS, "018XXXXXXXX", "Nagad", 4000.0, 4000.0, "#F0A63D", false, now)
            )
            for (acc in accounts) {
                val cv = ContentValues().apply {
                    put(COL_ACC_ID, acc.id)
                    put(COL_ACC_NAME, acc.name)
                    put(COL_ACC_TYPE, acc.type.name)
                    put(COL_ACC_NUMBER, acc.accountNumber)
                    put(COL_ACC_INSTITUTION, acc.institutionName)
                    put(COL_ACC_INITIAL_BALANCE, acc.initialBalance)
                    put(COL_ACC_CURRENT_BALANCE, acc.currentBalance)
                    put(COL_ACC_COLOR, acc.colorHex)
                    put(COL_ACC_IS_DEFAULT, if (acc.isDefault) 1 else 0)
                    put(COL_ACC_CREATED_AT, acc.createdAt)
                }
                db.insert(TABLE_ACCOUNTS, null, cv)
            }
        }
    }

    private fun backfillTransactionAccounts(db: SQLiteDatabase) {
        db.execSQL("UPDATE $TABLE_TRANSACTIONS SET $COL_TX_ACCOUNT_ID = 3, $COL_TX_ACCOUNT_NAME = 'bKash Personal' WHERE $COL_TX_PAYMENT_METHOD = 'BKASH' AND $COL_TX_ACCOUNT_ID IS NULL")
        db.execSQL("UPDATE $TABLE_TRANSACTIONS SET $COL_TX_ACCOUNT_ID = 4, $COL_TX_ACCOUNT_NAME = 'Nagad Personal' WHERE $COL_TX_PAYMENT_METHOD = 'NAGAD' AND $COL_TX_ACCOUNT_ID IS NULL")
        db.execSQL("UPDATE $TABLE_TRANSACTIONS SET $COL_TX_ACCOUNT_ID = 2, $COL_TX_ACCOUNT_NAME = 'Bank Account' WHERE $COL_TX_PAYMENT_METHOD IN ('BANK_TRANSFER', 'CREDIT_CARD', 'DEBIT_CARD') AND $COL_TX_ACCOUNT_ID IS NULL")
        db.execSQL("UPDATE $TABLE_TRANSACTIONS SET $COL_TX_ACCOUNT_ID = 1, $COL_TX_ACCOUNT_NAME = 'Cash in Hand' WHERE $COL_TX_ACCOUNT_ID IS NULL")
    }

    fun seedDefaultCategories(db: SQLiteDatabase) {
        val defaultCategories = listOf(
            Category(0, "Food & Dining", TransactionType.EXPENSE, "Restaurant", "#EF4444", true),
            Category(0, "Groceries", TransactionType.EXPENSE, "ShoppingCart", "#10B981", true),
            Category(0, "Transportation", TransactionType.EXPENSE, "DirectionsCar", "#0284C7", true),
            Category(0, "Shopping", TransactionType.EXPENSE, "ShoppingBag", "#DB2777", true),
            Category(0, "Housing & Rent", TransactionType.EXPENSE, "Home", "#7C3AED", true),
            Category(0, "Bills & Utilities", TransactionType.EXPENSE, "Bolt", "#F59E0B", true),
            Category(0, "Entertainment", TransactionType.EXPENSE, "Movie", "#EC4899", true),
            Category(0, "Health & Care", TransactionType.EXPENSE, "MedicalServices", "#06B6D4", true),
            Category(0, "Education", TransactionType.EXPENSE, "School", "#4F46E5", true),
            Category(0, "Travel", TransactionType.EXPENSE, "Flight", "#14B8A6", true),
            Category(0, "Loan & Debt Given", TransactionType.EXPENSE, "Handshake", "#E11D48", true),
            Category(0, "Other Expense", TransactionType.EXPENSE, "MoreHoriz", "#64748B", true),
            Category(0, "Salary", TransactionType.INCOME, "Work", "#22C55E", true),
            Category(0, "Freelance", TransactionType.INCOME, "Laptop", "#2563EB", true),
            Category(0, "Business", TransactionType.INCOME, "Store", "#D97706", true),
            Category(0, "Investment", TransactionType.INCOME, "TrendingUp", "#84CC16", true),
            Category(0, "Debt Recovery", TransactionType.INCOME, "Handshake", "#0D9488", true),
            Category(0, "Bonus", TransactionType.INCOME, "CardGiftcard", "#9333EA", true),
            Category(0, "Other Income", TransactionType.INCOME, "AttachMoney", "#475569", true)
        )

        for (cat in defaultCategories) {
            val cv = ContentValues().apply {
                put(COL_CAT_NAME, cat.name)
                put(COL_CAT_TYPE, cat.type.name)
                put(COL_CAT_ICON, cat.icon)
                put(COL_CAT_COLOR, cat.colorHex)
                put(COL_CAT_IS_DEFAULT, if (cat.isDefault) 1 else 0)
            }
            db.insert(TABLE_CATEGORIES, null, cv)
        }
    }

    private fun seedInitialData(db: SQLiteDatabase) {
        seedDefaultCategories(db)

        // Seed default profile
        val profileCv = ContentValues().apply {
            put(COL_PROFILE_ID, 1)
            put(COL_PROFILE_NAME, "Sabbir")
            put(COL_PROFILE_FIRST_NAME, "Sabbir")
            put(COL_PROFILE_LAST_NAME, "")
            put(COL_PROFILE_CURRENCY_CODE, "BDT")
            put(COL_PROFILE_CURRENCY_SYMBOL, "৳")
            put(COL_PROFILE_AVATAR, 0)
            putNull(COL_PROFILE_GOOGLE_EMAIL)
            put(COL_PROFILE_AUTO_SYNC, 1)
            put(COL_PROFILE_LAST_SYNC, 0L)
            put(COL_PROFILE_THEME, "dark")
            put(COL_PROFILE_BENGALI_NUMERALS, 1)
        }
        db.insert(TABLE_PROFILE, null, profileCv)

        // Seed default monthly budget for current month
        val cal = Calendar.getInstance()
        val budgetCv = ContentValues().apply {
            putNull(COL_BUDGET_CAT_ID)
            put(COL_BUDGET_CAT_NAME, "Overall Monthly")
            put(COL_BUDGET_AMOUNT, 35000.0)
            put(COL_BUDGET_MONTH, cal.get(Calendar.MONTH))
            put(COL_BUDGET_YEAR, cal.get(Calendar.YEAR))
        }
        db.insert(TABLE_BUDGETS, null, budgetCv)

        // Seed default starter accounts
        seedDefaultAccountsIfEmpty(db)

        // Seed starter receivable (পাওনা টাকা)
        val recCv = ContentValues().apply {
            put(COL_REC_PERSON_NAME, "Karim Ahmed")
            put(COL_REC_PHONE, "01712345678")
            put(COL_REC_AMOUNT, 5000.0)
            put(COL_REC_AMOUNT_RECEIVED, 0.0)
            put(COL_REC_DUE_DATE, System.currentTimeMillis() + 7 * 86400000L)
            put(COL_REC_CREATED_DATE, System.currentTimeMillis() - 86400000L)
            put(COL_REC_NOTES, "Equipment purchase advance")
            put(COL_REC_STATUS, ReceivableStatus.PENDING.name)
        }
        db.insert(TABLE_RECEIVABLES, null, recCv)

        // Seed demo transactions
        val now = System.currentTimeMillis()
        val oneDay = 86400000L
        val demoTransactions = listOf(
            Transaction(0, "Monthly Salary", 65000.0, TransactionType.INCOME, 13, "Salary", "Work", "#3DDC97", now - 2 * oneDay, PaymentMethod.BANK_TRANSFER, "Direct company deposit", accountId = 2, accountName = "Bank Account"),
            Transaction(0, "Freelance Web Project", 18000.0, TransactionType.INCOME, 14, "Freelance", "Laptop", "#3B8FF0", now - 1 * oneDay, PaymentMethod.BKASH, "Milestone payout", accountId = 3, accountName = "bKash Personal"),
            Transaction(0, "Supermarket Groceries", 4200.0, TransactionType.EXPENSE, 2, "Groceries", "ShoppingCart", "#2EC4B6", now - 1 * oneDay, PaymentMethod.DEBIT_CARD, "Weekly essentials", accountId = 2, accountName = "Bank Account"),
            Transaction(0, "Dinner with Family", 2100.0, TransactionType.EXPENSE, 1, "Food & Dining", "Restaurant", "#F0716B", now - oneDay / 2, PaymentMethod.BKASH, "Weekend dinner", accountId = 3, accountName = "bKash Personal"),
            Transaction(0, "Electricity & Internet Bill", 3450.0, TransactionType.EXPENSE, 6, "Bills & Utilities", "Bolt", "#F0A63D", now - oneDay / 4, PaymentMethod.NAGAD, "Monthly utility bills", accountId = 4, accountName = "Nagad Personal")
        )

        for (tx in demoTransactions) {
            val cv = ContentValues().apply {
                put(COL_TX_TITLE, tx.title)
                put(COL_TX_AMOUNT, tx.amount)
                put(COL_TX_TYPE, tx.type.name)
                put(COL_TX_CAT_ID, tx.categoryId)
                put(COL_TX_CAT_NAME, tx.categoryName)
                put(COL_TX_CAT_ICON, tx.categoryIcon)
                put(COL_TX_CAT_COLOR, tx.categoryColor)
                put(COL_TX_DATE, tx.dateMillis)
                put(COL_TX_PAYMENT_METHOD, tx.paymentMethod.name)
                put(COL_TX_NOTES, tx.notes)
                if (tx.accountId != null) put(COL_TX_ACCOUNT_ID, tx.accountId) else putNull(COL_TX_ACCOUNT_ID)
                if (tx.accountName != null) put(COL_TX_ACCOUNT_NAME, tx.accountName) else putNull(COL_TX_ACCOUNT_NAME)
            }
            db.insert(TABLE_TRANSACTIONS, null, cv)
        }

        recalculateAllAccountBalances(db)
    }

    // --- Transactions CRUD ---
    /**
     * Resolves a valid account ID and account name for any transaction.
     * Guarantees that no transaction is ever orphaned or unlinked from a financial wallet.
     */
    fun resolveAccountForTransaction(db: SQLiteDatabase, accountId: Long?, paymentMethod: PaymentMethod?): Pair<Long, String> {
        // 1. If accountId is provided and exists in TABLE_ACCOUNTS, use it
        if (accountId != null && accountId > 0) {
            val cur = db.query(TABLE_ACCOUNTS, arrayOf(COL_ACC_ID, COL_ACC_NAME), "$COL_ACC_ID = ?", arrayOf(accountId.toString()), null, null, null)
            cur.use {
                if (it.moveToFirst()) {
                    return Pair(it.getLong(0), it.getString(1))
                }
            }
        }

        // 2. Try to match by payment method
        if (paymentMethod != null) {
            val (typeMatch, nameKeyword) = when (paymentMethod) {
                PaymentMethod.CASH -> Pair("CASH", null)
                PaymentMethod.BKASH -> Pair("MFS", "bkash")
                PaymentMethod.NAGAD -> Pair("MFS", "nagad")
                PaymentMethod.BANK_TRANSFER, PaymentMethod.CREDIT_CARD, PaymentMethod.DEBIT_CARD -> Pair("BANK", null)
                PaymentMethod.OTHER -> Pair(null, null)
            }

            if (nameKeyword != null) {
                val cur = db.rawQuery(
                    "SELECT $COL_ACC_ID, $COL_ACC_NAME FROM $TABLE_ACCOUNTS WHERE $COL_ACC_TYPE = ? AND (LOWER($COL_ACC_NAME) LIKE ? OR LOWER($COL_ACC_INSTITUTION) LIKE ?) LIMIT 1",
                    arrayOf(typeMatch, "%$nameKeyword%", "%$nameKeyword%")
                )
                cur.use {
                    if (it.moveToFirst()) {
                        return Pair(it.getLong(0), it.getString(1))
                    }
                }
            }

            if (typeMatch != null) {
                val cur = db.rawQuery(
                    "SELECT $COL_ACC_ID, $COL_ACC_NAME FROM $TABLE_ACCOUNTS WHERE $COL_ACC_TYPE = ? ORDER BY $COL_ACC_IS_DEFAULT DESC, $COL_ACC_ID ASC LIMIT 1",
                    arrayOf(typeMatch)
                )
                cur.use {
                    if (it.moveToFirst()) {
                        return Pair(it.getLong(0), it.getString(1))
                    }
                }
            }
        }

        // 3. Fallback to default account
        val defCur = db.rawQuery(
            "SELECT $COL_ACC_ID, $COL_ACC_NAME FROM $TABLE_ACCOUNTS WHERE $COL_ACC_IS_DEFAULT = 1 LIMIT 1",
            null
        )
        defCur.use {
            if (it.moveToFirst()) {
                return Pair(it.getLong(0), it.getString(1))
            }
        }

        // 4. Fallback to first available account
        val firstCur = db.rawQuery(
            "SELECT $COL_ACC_ID, $COL_ACC_NAME FROM $TABLE_ACCOUNTS ORDER BY $COL_ACC_ID ASC LIMIT 1",
            null
        )
        firstCur.use {
            if (it.moveToFirst()) {
                return Pair(it.getLong(0), it.getString(1))
            }
        }

        // 5. If no accounts exist at all, seed default starter accounts and return Cash in Hand (id=1)
        seedDefaultAccountsIfEmpty(db)
        return Pair(1L, "Cash in Hand")
    }

    fun insertTransaction(tx: Transaction): Long {
        val db = writableDatabase
        db.beginTransaction()
        try {
            val (resolvedAccountId, resolvedAccountName) = resolveAccountForTransaction(db, tx.accountId, tx.paymentMethod)
            val cv = ContentValues().apply {
                put(COL_TX_TITLE, tx.title)
                put(COL_TX_AMOUNT, tx.amount)
                put(COL_TX_TYPE, tx.type.name)
                put(COL_TX_CAT_ID, tx.categoryId)
                put(COL_TX_CAT_NAME, tx.categoryName)
                put(COL_TX_CAT_ICON, tx.categoryIcon)
                put(COL_TX_CAT_COLOR, tx.categoryColor)
                put(COL_TX_DATE, tx.dateMillis)
                put(COL_TX_PAYMENT_METHOD, tx.paymentMethod.name)
                put(COL_TX_NOTES, tx.notes)
                put(COL_TX_ACCOUNT_ID, resolvedAccountId)
                put(COL_TX_ACCOUNT_NAME, resolvedAccountName)
                if (tx.receiptImagePath != null) put(COL_TX_RECEIPT_PATH, tx.receiptImagePath) else putNull(COL_TX_RECEIPT_PATH)
            }
            val id = db.insert(TABLE_TRANSACTIONS, null, cv)
            if (id > 0) {
                recalculateAccountBalance(db, resolvedAccountId)
            }
            db.setTransactionSuccessful()
            return id
        } finally {
            db.endTransaction()
        }
    }

    fun updateTransaction(tx: Transaction): Int {
        val db = writableDatabase
        db.beginTransaction()
        try {
            var priorAccountId: Long? = null
            val cur = db.query(TABLE_TRANSACTIONS, arrayOf(COL_TX_ACCOUNT_ID), "$COL_TX_ID = ?", arrayOf(tx.id.toString()), null, null, null)
            cur.use {
                if (it.moveToFirst() && !it.isNull(0)) {
                    priorAccountId = it.getLong(0)
                }
            }

            val (resolvedAccountId, resolvedAccountName) = resolveAccountForTransaction(db, tx.accountId, tx.paymentMethod)
            val cv = ContentValues().apply {
                put(COL_TX_TITLE, tx.title)
                put(COL_TX_AMOUNT, tx.amount)
                put(COL_TX_TYPE, tx.type.name)
                put(COL_TX_CAT_ID, tx.categoryId)
                put(COL_TX_CAT_NAME, tx.categoryName)
                put(COL_TX_CAT_ICON, tx.categoryIcon)
                put(COL_TX_CAT_COLOR, tx.categoryColor)
                put(COL_TX_DATE, tx.dateMillis)
                put(COL_TX_PAYMENT_METHOD, tx.paymentMethod.name)
                put(COL_TX_NOTES, tx.notes)
                put(COL_TX_ACCOUNT_ID, resolvedAccountId)
                put(COL_TX_ACCOUNT_NAME, resolvedAccountName)
                if (tx.receiptImagePath != null) put(COL_TX_RECEIPT_PATH, tx.receiptImagePath) else putNull(COL_TX_RECEIPT_PATH)
            }
            val rows = db.update(TABLE_TRANSACTIONS, cv, "$COL_TX_ID = ?", arrayOf(tx.id.toString()))
            if (rows > 0) {
                if (priorAccountId != null) recalculateAccountBalance(db, priorAccountId)
                if (resolvedAccountId != priorAccountId) recalculateAccountBalance(db, resolvedAccountId)
            }
            db.setTransactionSuccessful()
            return rows
        } finally {
            db.endTransaction()
        }
    }

    fun deleteTransaction(id: Long): Int {
        val db = writableDatabase
        db.beginTransaction()
        try {
            var accountId: Long? = null
            val cur = db.query(TABLE_TRANSACTIONS, arrayOf(COL_TX_ACCOUNT_ID), "$COL_TX_ID = ?", arrayOf(id.toString()), null, null, null)
            cur.use {
                if (it.moveToFirst() && !it.isNull(0)) {
                    accountId = it.getLong(0)
                }
            }
            val rows = db.delete(TABLE_TRANSACTIONS, "$COL_TX_ID = ?", arrayOf(id.toString()))
            if (rows > 0) {
                val cvMarket = ContentValues().apply { putNull(COL_MI_TRANSACTION_ID) }
                db.update(TABLE_MARKET_ITEMS, cvMarket, "$COL_MI_TRANSACTION_ID = ?", arrayOf(id.toString()))
                if (accountId != null) {
                    recalculateAccountBalance(db, accountId)
                }
            }
            db.setTransactionSuccessful()
            return rows
        } finally {
            db.endTransaction()
        }
    }

    fun getAllTransactions(): List<Transaction> {
        val db = readableDatabase
        val list = mutableListOf<Transaction>()
        val cursor = db.query(
            TABLE_TRANSACTIONS,
            null,
            null,
            null,
            null,
            null,
            "$COL_TX_DATE DESC, $COL_TX_ID DESC"
        )
        cursor.use { c ->
            while (c.moveToNext()) {
                list.add(cursorToTransaction(c))
            }
        }
        return list
    }

    fun getFilteredTransactions(
        timeFilter: TimeFilter = TimeFilter.ALL,
        type: TransactionType? = null,
        categoryId: Long? = null,
        query: String? = null
    ): List<Transaction> {
        val db = readableDatabase
        val whereClauses = mutableListOf<String>()
        val args = mutableListOf<String>()

        if (type != null) {
            whereClauses.add("$COL_TX_TYPE = ?")
            args.add(type.name)
        }
        if (categoryId != null && categoryId > 0) {
            whereClauses.add("$COL_TX_CAT_ID = ?")
            args.add(categoryId.toString())
        }
        if (!query.isNullOrBlank()) {
            whereClauses.add("($COL_TX_TITLE LIKE ? OR $COL_TX_NOTES LIKE ? OR $COL_TX_CAT_NAME LIKE ?)")
            val wildcard = "%$query%"
            args.add(wildcard)
            args.add(wildcard)
            args.add(wildcard)
        }

        // Time Filtering
        val (startMillis, endMillis) = getTimeRange(timeFilter)
        if (startMillis > 0) {
            whereClauses.add("$COL_TX_DATE >= ? AND $COL_TX_DATE <= ?")
            args.add(startMillis.toString())
            args.add(endMillis.toString())
        }

        val where = if (whereClauses.isNotEmpty()) whereClauses.joinToString(" AND ") else null
        val cursor = db.query(
            TABLE_TRANSACTIONS,
            null,
            where,
            if (args.isNotEmpty()) args.toTypedArray() else null,
            null,
            null,
            "$COL_TX_DATE DESC, $COL_TX_ID DESC"
        )
        val list = mutableListOf<Transaction>()
        cursor.use { c ->
            while (c.moveToNext()) {
                list.add(cursorToTransaction(c))
            }
        }
        return list
    }

    fun getTransactionsBetween(
        startMillis: Long,
        endMillis: Long,
        type: TransactionType? = null,
        categoryId: Long? = null,
        query: String? = null
    ): List<Transaction> {
        val db = readableDatabase
        val whereClauses = mutableListOf<String>()
        val args = mutableListOf<String>()

        if (type != null) {
            whereClauses.add("$COL_TX_TYPE = ?")
            args.add(type.name)
        }
        if (categoryId != null && categoryId > 0) {
            whereClauses.add("$COL_TX_CAT_ID = ?")
            args.add(categoryId.toString())
        }
        if (!query.isNullOrBlank()) {
            whereClauses.add("($COL_TX_TITLE LIKE ? OR $COL_TX_NOTES LIKE ? OR $COL_TX_CAT_NAME LIKE ?)")
            val wildcard = "%$query%"
            args.add(wildcard)
            args.add(wildcard)
            args.add(wildcard)
        }

        whereClauses.add("$COL_TX_DATE >= ? AND $COL_TX_DATE <= ?")
        args.add(startMillis.toString())
        args.add(endMillis.toString())

        val where = whereClauses.joinToString(" AND ")
        val cursor = db.query(
            TABLE_TRANSACTIONS,
            null,
            where,
            args.toTypedArray(),
            null,
            null,
            "$COL_TX_DATE DESC, $COL_TX_ID DESC"
        )
        val list = mutableListOf<Transaction>()
        cursor.use { c ->
            while (c.moveToNext()) {
                list.add(cursorToTransaction(c))
            }
        }
        return list
    }

    fun getTimeRange(filter: TimeFilter): Pair<Long, Long> {
        return when (filter) {
            TimeFilter.TODAY -> {
                val cal = Calendar.getInstance()
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                val start = cal.timeInMillis

                cal.set(Calendar.HOUR_OF_DAY, 23)
                cal.set(Calendar.MINUTE, 59)
                cal.set(Calendar.SECOND, 59)
                cal.set(Calendar.MILLISECOND, 999)
                val end = cal.timeInMillis
                Pair(start, end)
            }
            TimeFilter.THIS_WEEK -> {
                val cal = Calendar.getInstance()
                cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                val start = cal.timeInMillis

                cal.add(Calendar.DAY_OF_WEEK, 6)
                cal.set(Calendar.HOUR_OF_DAY, 23)
                cal.set(Calendar.MINUTE, 59)
                cal.set(Calendar.SECOND, 59)
                cal.set(Calendar.MILLISECOND, 999)
                val end = cal.timeInMillis
                Pair(start, end)
            }
            TimeFilter.THIS_MONTH -> {
                val cal = Calendar.getInstance()
                cal.set(Calendar.DAY_OF_MONTH, 1)
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                val start = cal.timeInMillis

                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
                cal.set(Calendar.HOUR_OF_DAY, 23)
                cal.set(Calendar.MINUTE, 59)
                cal.set(Calendar.SECOND, 59)
                cal.set(Calendar.MILLISECOND, 999)
                val end = cal.timeInMillis
                Pair(start, end)
            }
            TimeFilter.THIS_YEAR -> {
                val cal = Calendar.getInstance()
                cal.set(Calendar.DAY_OF_YEAR, 1)
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                val start = cal.timeInMillis

                cal.set(Calendar.DAY_OF_YEAR, cal.getActualMaximum(Calendar.DAY_OF_YEAR))
                cal.set(Calendar.HOUR_OF_DAY, 23)
                cal.set(Calendar.MINUTE, 59)
                cal.set(Calendar.SECOND, 59)
                cal.set(Calendar.MILLISECOND, 999)
                val end = cal.timeInMillis
                Pair(start, end)
            }
            TimeFilter.ALL, TimeFilter.CUSTOM -> Pair(0L, Long.MAX_VALUE)
        }
    }

    fun getAggregatedCategorySpends(
        startMillis: Long,
        endMillis: Long,
        type: TransactionType = TransactionType.EXPENSE
    ): List<CategorySpend> {
        val db = readableDatabase
        val sql = """
            SELECT 
                $COL_TX_CAT_ID,
                $COL_TX_CAT_NAME,
                $COL_TX_CAT_ICON,
                $COL_TX_CAT_COLOR,
                SUM($COL_TX_AMOUNT) AS total_amount,
                COUNT(*) AS tx_count
            FROM $TABLE_TRANSACTIONS
            WHERE $COL_TX_DATE >= ? AND $COL_TX_DATE <= ?
              AND $COL_TX_TYPE = ?
              AND $COL_TX_CAT_NAME NOT IN ('Transfer', 'Account Transfer', 'অ্যাকাউন্ট স্থানান্তর', 'Loan & Debt Given', 'ঋণ বা ধার প্রদান', 'Debt Recovery', 'পাওনা টাকা আদায়')
              AND $COL_TX_NOTES NOT LIKE '[Transfer]%'
              AND $COL_TX_TITLE NOT LIKE 'Transfer to %'
              AND $COL_TX_TITLE NOT LIKE 'Transfer from %'
            GROUP BY $COL_TX_CAT_ID, $COL_TX_CAT_NAME, $COL_TX_CAT_ICON, $COL_TX_CAT_COLOR
            ORDER BY total_amount DESC
        """.trimIndent()

        val cursor = db.rawQuery(sql, arrayOf(startMillis.toString(), endMillis.toString(), type.name))
        val list = mutableListOf<CategorySpend>()
        var grandTotal = 0.0

        cursor.use { c ->
            while (c.moveToNext()) {
                val catId = c.getLong(0)
                val name = c.getString(1)
                val icon = c.getString(2)
                val color = c.getString(3)
                val amount = c.getDouble(4)
                val count = c.getInt(5)
                grandTotal += amount
                list.add(
                    CategorySpend(
                        categoryId = catId,
                        categoryName = name,
                        categoryIcon = icon,
                        categoryColor = color,
                        totalAmount = amount,
                        percentage = 0f,
                        transactionCount = count
                    )
                )
            }
        }

        return if (grandTotal > 0) {
            list.map { it.copy(percentage = (it.totalAmount / grandTotal).toFloat()) }
        } else {
            list
        }
    }

    fun getAggregatedCategorySpends(
        timeFilter: TimeFilter,
        type: TransactionType = TransactionType.EXPENSE
    ): List<CategorySpend> {
        val (start, end) = getTimeRange(timeFilter)
        return getAggregatedCategorySpends(start, end, type)
    }

    fun getAggregatedFinancialSummary(
        startMillis: Long,
        endMillis: Long,
        overallBudget: Double = 0.0
    ): FinancialSummary {
        val db = readableDatabase
        val sql = """
            SELECT
                SUM(CASE WHEN $COL_TX_TYPE = 'INCOME' THEN $COL_TX_AMOUNT ELSE 0 END) AS total_income,
                SUM(CASE WHEN $COL_TX_TYPE = 'EXPENSE' THEN $COL_TX_AMOUNT ELSE 0 END) AS total_expense
            FROM $TABLE_TRANSACTIONS
            WHERE $COL_TX_DATE >= ? AND $COL_TX_DATE <= ?
              AND $COL_TX_CAT_NAME NOT IN ('Transfer', 'Account Transfer', 'অ্যাকাউন্ট স্থানান্তর', 'Loan & Debt Given', 'ঋণ বা ধার প্রদান', 'Debt Recovery', 'পাওনা টাকা আদায়')
              AND $COL_TX_NOTES NOT LIKE '[Transfer]%'
              AND $COL_TX_TITLE NOT LIKE 'Transfer to %'
              AND $COL_TX_TITLE NOT LIKE 'Transfer from %'
        """.trimIndent()

        var totalIncome = 0.0
        var totalExpense = 0.0
        val cursor = db.rawQuery(sql, arrayOf(startMillis.toString(), endMillis.toString()))
        cursor.use { c ->
            if (c.moveToFirst()) {
                if (!c.isNull(0)) totalIncome = c.getDouble(0)
                if (!c.isNull(1)) totalExpense = c.getDouble(1)
            }
        }

        val netBalance = totalIncome - totalExpense
        val budgetRemaining = if (overallBudget > 0) (overallBudget - totalExpense).coerceAtLeast(0.0) else 0.0
        val budgetOverspent = if (overallBudget > 0 && totalExpense > overallBudget) totalExpense - overallBudget else 0.0
        val budgetPercent = if (overallBudget > 0) ((totalExpense / overallBudget).toFloat()).coerceIn(0f, 2f) else 0f

        return FinancialSummary(
            totalIncome = totalIncome,
            totalExpense = totalExpense,
            netBalance = netBalance,
            budgetAmount = overallBudget,
            budgetSpent = totalExpense,
            budgetRemaining = budgetRemaining,
            budgetPercent = budgetPercent,
            budgetOverspent = budgetOverspent
        )
    }

    fun getAggregatedFinancialSummary(
        timeFilter: TimeFilter,
        overallBudget: Double = 0.0
    ): FinancialSummary {
        val (start, end) = getTimeRange(timeFilter)
        return getAggregatedFinancialSummary(start, end, overallBudget)
    }

    fun getAggregatedPaymentMethodSpends(
        startMillis: Long,
        endMillis: Long,
        type: TransactionType = TransactionType.EXPENSE
    ): List<PaymentMethodSpend> {
        val db = readableDatabase
        val sql = """
            SELECT 
                $COL_TX_PAYMENT_METHOD,
                SUM($COL_TX_AMOUNT) AS total_amount,
                COUNT(*) AS tx_count
            FROM $TABLE_TRANSACTIONS
            WHERE $COL_TX_DATE >= ? AND $COL_TX_DATE <= ?
              AND $COL_TX_TYPE = ?
              AND $COL_TX_CAT_NAME NOT IN ('Transfer', 'Account Transfer', 'অ্যাকাউন্ট স্থানান্তর', 'Loan & Debt Given', 'ঋণ বা ধার প্রদান', 'Debt Recovery', 'পাওনা টাকা আদায়')
              AND $COL_TX_NOTES NOT LIKE '[Transfer]%'
              AND $COL_TX_TITLE NOT LIKE 'Transfer to %'
              AND $COL_TX_TITLE NOT LIKE 'Transfer from %'
            GROUP BY $COL_TX_PAYMENT_METHOD
            ORDER BY total_amount DESC
        """.trimIndent()

        val cursor = db.rawQuery(sql, arrayOf(startMillis.toString(), endMillis.toString(), type.name))
        val list = mutableListOf<PaymentMethodSpend>()
        var grandTotal = 0.0

        cursor.use { c ->
            while (c.moveToNext()) {
                val pmStr = c.getString(0)
                val method = try { PaymentMethod.valueOf(pmStr) } catch (_: Exception) { PaymentMethod.CASH }
                val amount = c.getDouble(1)
                val count = c.getInt(2)
                grandTotal += amount
                list.add(
                    PaymentMethodSpend(
                        paymentMethod = method,
                        totalAmount = amount,
                        percentage = 0f,
                        transactionCount = count
                    )
                )
            }
        }

        return if (grandTotal > 0) {
            list.map { it.copy(percentage = (it.totalAmount / grandTotal).toFloat()) }
        } else {
            list
        }
    }

    fun getAggregatedPaymentMethodSpends(
        timeFilter: TimeFilter,
        type: TransactionType = TransactionType.EXPENSE
    ): List<PaymentMethodSpend> {
        val (start, end) = getTimeRange(timeFilter)
        return getAggregatedPaymentMethodSpends(start, end, type)
    }

    fun getTransactionsForAccount(accountId: Long): List<Transaction> {
        val db = readableDatabase
        val list = mutableListOf<Transaction>()
        val cursor = db.query(
            TABLE_TRANSACTIONS,
            null,
            "$COL_TX_ACCOUNT_ID = ?",
            arrayOf(accountId.toString()),
            null,
            null,
            "$COL_TX_DATE DESC, $COL_TX_ID DESC"
        )
        cursor.use { c ->
            while (c.moveToNext()) {
                list.add(cursorToTransaction(c))
            }
        }
        return list
    }

    private fun cursorToTransaction(c: Cursor): Transaction {
        val paymentMethodStr = c.getString(c.getColumnIndexOrThrow(COL_TX_PAYMENT_METHOD))
        val paymentMethod = try {
            PaymentMethod.valueOf(paymentMethodStr)
        } catch (_: Exception) {
            PaymentMethod.CASH
        }

        val accIdIndex = c.getColumnIndex(COL_TX_ACCOUNT_ID)
        val accountId = if (accIdIndex != -1 && !c.isNull(accIdIndex)) c.getLong(accIdIndex) else null
        val accNameIndex = c.getColumnIndex(COL_TX_ACCOUNT_NAME)
        val accountName = if (accNameIndex != -1 && !c.isNull(accNameIndex)) c.getString(accNameIndex) else null

        val receiptIndex = c.getColumnIndex(COL_TX_RECEIPT_PATH)
        val receiptImagePath = if (receiptIndex != -1 && !c.isNull(receiptIndex)) c.getString(receiptIndex) else null

        return Transaction(
            id = c.getLong(c.getColumnIndexOrThrow(COL_TX_ID)),
            title = c.getString(c.getColumnIndexOrThrow(COL_TX_TITLE)),
            amount = c.getDouble(c.getColumnIndexOrThrow(COL_TX_AMOUNT)),
            type = TransactionType.valueOf(c.getString(c.getColumnIndexOrThrow(COL_TX_TYPE))),
            categoryId = c.getLong(c.getColumnIndexOrThrow(COL_TX_CAT_ID)),
            categoryName = c.getString(c.getColumnIndexOrThrow(COL_TX_CAT_NAME)),
            categoryIcon = c.getString(c.getColumnIndexOrThrow(COL_TX_CAT_ICON)),
            categoryColor = c.getString(c.getColumnIndexOrThrow(COL_TX_CAT_COLOR)),
            dateMillis = c.getLong(c.getColumnIndexOrThrow(COL_TX_DATE)),
            paymentMethod = paymentMethod,
            notes = c.getString(c.getColumnIndexOrThrow(COL_TX_NOTES)),
            accountId = accountId,
            accountName = accountName,
            receiptImagePath = receiptImagePath
        )
    }

    // --- Categories CRUD ---
    fun getAllCategories(): List<Category> {
        val db = readableDatabase
        val list = mutableListOf<Category>()
        val cursor = db.query(TABLE_CATEGORIES, null, null, null, null, null, "$COL_CAT_NAME ASC")
        var hasDuplicates = false
        val seenColors = mutableSetOf<String>()
        cursor.use { c ->
            while (c.moveToNext()) {
                val colorHex = c.getString(c.getColumnIndexOrThrow(COL_CAT_COLOR))
                val norm = CategoryColorHelper.normalizeHex(colorHex)
                if (norm in seenColors) {
                    hasDuplicates = true
                } else {
                    seenColors.add(norm)
                }
                list.add(
                    Category(
                        id = c.getLong(c.getColumnIndexOrThrow(COL_CAT_ID)),
                        name = c.getString(c.getColumnIndexOrThrow(COL_CAT_NAME)),
                        type = TransactionType.valueOf(c.getString(c.getColumnIndexOrThrow(COL_CAT_TYPE))),
                        icon = c.getString(c.getColumnIndexOrThrow(COL_CAT_ICON)),
                        colorHex = colorHex,
                        isDefault = c.getInt(c.getColumnIndexOrThrow(COL_CAT_IS_DEFAULT)) == 1
                    )
                )
            }
        }
        if (hasDuplicates) {
            reconcileAndDeduplicateCategoryColors(writableDatabase)
            return fetchCategoriesDirect(readableDatabase)
        }
        return list
    }

    private fun fetchCategoriesDirect(db: SQLiteDatabase): List<Category> {
        val list = mutableListOf<Category>()
        val cursor = db.query(TABLE_CATEGORIES, null, null, null, null, null, "$COL_CAT_NAME ASC")
        cursor.use { c ->
            while (c.moveToNext()) {
                list.add(
                    Category(
                        id = c.getLong(c.getColumnIndexOrThrow(COL_CAT_ID)),
                        name = c.getString(c.getColumnIndexOrThrow(COL_CAT_NAME)),
                        type = TransactionType.valueOf(c.getString(c.getColumnIndexOrThrow(COL_CAT_TYPE))),
                        icon = c.getString(c.getColumnIndexOrThrow(COL_CAT_ICON)),
                        colorHex = c.getString(c.getColumnIndexOrThrow(COL_CAT_COLOR)),
                        isDefault = c.getInt(c.getColumnIndexOrThrow(COL_CAT_IS_DEFAULT)) == 1
                    )
                )
            }
        }
        return list
    }

    fun reconcileAndDeduplicateCategoryColors(db: SQLiteDatabase) {
        val cursor = db.query(TABLE_CATEGORIES, null, null, null, null, null, "$COL_CAT_ID ASC")
        val categories = mutableListOf<Category>()
        cursor.use { c ->
            while (c.moveToNext()) {
                categories.add(
                    Category(
                        id = c.getLong(c.getColumnIndexOrThrow(COL_CAT_ID)),
                        name = c.getString(c.getColumnIndexOrThrow(COL_CAT_NAME)),
                        type = TransactionType.valueOf(c.getString(c.getColumnIndexOrThrow(COL_CAT_TYPE))),
                        icon = c.getString(c.getColumnIndexOrThrow(COL_CAT_ICON)),
                        colorHex = c.getString(c.getColumnIndexOrThrow(COL_CAT_COLOR)),
                        isDefault = c.getInt(c.getColumnIndexOrThrow(COL_CAT_IS_DEFAULT)) == 1
                    )
                )
            }
        }

        val claimedColors = mutableSetOf<String>()
        val updatedCategories = mutableListOf<Category>()

        for (cat in categories) {
            val normColor = CategoryColorHelper.normalizeHex(cat.colorHex)
            if (normColor in claimedColors) {
                val availableColor = CategoryColorHelper.getAvailableColor(updatedCategories)
                val newCat = cat.copy(colorHex = availableColor)
                claimedColors.add(CategoryColorHelper.normalizeHex(availableColor))
                updatedCategories.add(newCat)

                val cv = ContentValues().apply {
                    put(COL_CAT_COLOR, availableColor)
                }
                db.update(TABLE_CATEGORIES, cv, "$COL_CAT_ID = ?", arrayOf(cat.id.toString()))

                val txCv = ContentValues().apply {
                    put(COL_TX_CAT_COLOR, availableColor)
                }
                db.update(TABLE_TRANSACTIONS, txCv, "$COL_TX_CAT_ID = ?", arrayOf(cat.id.toString()))

                val billCv = ContentValues().apply {
                    put(COL_BILL_CAT_COLOR, availableColor)
                }
                db.update(TABLE_RECURRING_BILLS, billCv, "$COL_BILL_CAT_ID = ?", arrayOf(cat.id.toString()))
            } else {
                claimedColors.add(normColor)
                updatedCategories.add(cat)
            }
        }
    }

    fun insertCategory(cat: Category): Long {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(COL_CAT_NAME, cat.name)
            put(COL_CAT_TYPE, cat.type.name)
            put(COL_CAT_ICON, cat.icon)
            put(COL_CAT_COLOR, cat.colorHex)
            put(COL_CAT_IS_DEFAULT, if (cat.isDefault) 1 else 0)
        }
        return db.insert(TABLE_CATEGORIES, null, cv)
    }

    fun updateCategory(cat: Category): Int {
        val db = writableDatabase
        db.beginTransaction()
        try {
            val cv = ContentValues().apply {
                put(COL_CAT_NAME, cat.name)
                put(COL_CAT_TYPE, cat.type.name)
                put(COL_CAT_ICON, cat.icon)
                put(COL_CAT_COLOR, cat.colorHex)
            }
            val rows = db.update(TABLE_CATEGORIES, cv, "$COL_CAT_ID = ?", arrayOf(cat.id.toString()))
            if (rows > 0) {
                // Keep denormalized columns in transactions synchronized
                val txCv = ContentValues().apply {
                    put(COL_TX_CAT_NAME, cat.name)
                    put(COL_TX_CAT_ICON, cat.icon)
                    put(COL_TX_CAT_COLOR, cat.colorHex)
                }
                db.update(TABLE_TRANSACTIONS, txCv, "$COL_TX_CAT_ID = ?", arrayOf(cat.id.toString()))

                // Keep category name in budgets synchronized
                val bCv = ContentValues().apply {
                    put(COL_BUDGET_CAT_NAME, cat.name)
                }
                db.update(TABLE_BUDGETS, bCv, "$COL_BUDGET_CAT_ID = ?", arrayOf(cat.id.toString()))

                // Keep category details in recurring bills synchronized
                val billCv = ContentValues().apply {
                    put(COL_BILL_CAT_NAME, cat.name)
                    put(COL_BILL_CAT_ICON, cat.icon)
                    put(COL_BILL_CAT_COLOR, cat.colorHex)
                }
                db.update(TABLE_RECURRING_BILLS, billCv, "$COL_BILL_CAT_ID = ?", arrayOf(cat.id.toString()))
            }
            db.setTransactionSuccessful()
            return rows
        } finally {
            db.endTransaction()
        }
    }

    fun deleteCategory(id: Long): Int {
        val db = writableDatabase
        db.beginTransaction()
        try {
            // 1. Get the category to be deleted to know its type
            var catType = TransactionType.EXPENSE
            val cur = db.query(
                TABLE_CATEGORIES,
                arrayOf(COL_CAT_TYPE),
                "$COL_CAT_ID = ?",
                arrayOf(id.toString()),
                null, null, null, "1"
            )
            cur.use {
                if (it.moveToFirst()) {
                    val typeStr = it.getString(0)
                    catType = runCatching { TransactionType.valueOf(typeStr) }.getOrDefault(TransactionType.EXPENSE)
                }
            }

            // 2. Find fallback category of the same type
            val fallbackName = if (catType == TransactionType.EXPENSE) "Other Expense" else "Other Income"
            val fallback = getOrCreateCategoryByName(
                name = fallbackName,
                type = catType,
                defaultIcon = if (catType == TransactionType.EXPENSE) "MoreHoriz" else "AttachMoney",
                defaultColor = if (catType == TransactionType.EXPENSE) "#64748B" else "#475569"
            )

            // If the fallback happens to have the same ID as the one being deleted, find another category of same type
            val finalFallback = if (fallback.id == id) {
                val altCur = db.query(
                    TABLE_CATEGORIES,
                    null,
                    "$COL_CAT_ID != ? AND $COL_CAT_TYPE = ?",
                    arrayOf(id.toString(), catType.name),
                    null, null, null, "1"
                )
                altCur.use {
                    if (it.moveToFirst()) {
                        Category(
                            id = it.getLong(it.getColumnIndexOrThrow(COL_CAT_ID)),
                            name = it.getString(it.getColumnIndexOrThrow(COL_CAT_NAME)),
                            type = TransactionType.valueOf(it.getString(it.getColumnIndexOrThrow(COL_CAT_TYPE))),
                            icon = it.getString(it.getColumnIndexOrThrow(COL_CAT_ICON)),
                            colorHex = it.getString(it.getColumnIndexOrThrow(COL_CAT_COLOR)),
                            isDefault = it.getInt(it.getColumnIndexOrThrow(COL_CAT_IS_DEFAULT)) == 1
                        )
                    } else fallback
                }
            } else fallback

            if (finalFallback.id != id) {
                // Migrate transactions to fallback category
                val txCv = ContentValues().apply {
                    put(COL_TX_CAT_ID, finalFallback.id)
                    put(COL_TX_CAT_NAME, finalFallback.name)
                    put(COL_TX_CAT_ICON, finalFallback.icon)
                    put(COL_TX_CAT_COLOR, finalFallback.colorHex)
                }
                db.update(TABLE_TRANSACTIONS, txCv, "$COL_TX_CAT_ID = ?", arrayOf(id.toString()))

                // Migrate recurring bills to fallback category
                val billCv = ContentValues().apply {
                    put(COL_BILL_CAT_ID, finalFallback.id)
                    put(COL_BILL_CAT_NAME, finalFallback.name)
                    put(COL_BILL_CAT_ICON, finalFallback.icon)
                    put(COL_BILL_CAT_COLOR, finalFallback.colorHex)
                }
                db.update(TABLE_RECURRING_BILLS, billCv, "$COL_BILL_CAT_ID = ?", arrayOf(id.toString()))
            }

            // Remove any budgets specific to this deleted category
            db.delete(TABLE_BUDGETS, "$COL_BUDGET_CAT_ID = ?", arrayOf(id.toString()))

            // Delete the category itself
            val rows = db.delete(TABLE_CATEGORIES, "$COL_CAT_ID = ?", arrayOf(id.toString()))
            db.setTransactionSuccessful()
            return rows
        } finally {
            db.endTransaction()
        }
    }

    fun getOrCreateCategoryByName(
        name: String,
        type: TransactionType,
        defaultIcon: String = "MoreHoriz",
        defaultColor: String = "#8B90A0"
    ): Category {
        val db = writableDatabase
        // 1. Try exact name and type match
        val cursor = db.query(
            TABLE_CATEGORIES,
            null,
            "$COL_CAT_NAME = ? AND $COL_CAT_TYPE = ?",
            arrayOf(name, type.name),
            null, null, null, "1"
        )
        cursor.use { c ->
            if (c.moveToFirst()) {
                return Category(
                    id = c.getLong(c.getColumnIndexOrThrow(COL_CAT_ID)),
                    name = c.getString(c.getColumnIndexOrThrow(COL_CAT_NAME)),
                    type = TransactionType.valueOf(c.getString(c.getColumnIndexOrThrow(COL_CAT_TYPE))),
                    icon = c.getString(c.getColumnIndexOrThrow(COL_CAT_ICON)),
                    colorHex = c.getString(c.getColumnIndexOrThrow(COL_CAT_COLOR)),
                    isDefault = c.getInt(c.getColumnIndexOrThrow(COL_CAT_IS_DEFAULT)) == 1
                )
            }
        }

        // 2. Fallback: match by name ignoring type
        val curFallback = db.query(
            TABLE_CATEGORIES,
            null,
            "$COL_CAT_NAME = ?",
            arrayOf(name),
            null, null, null, "1"
        )
        curFallback.use { c ->
            if (c.moveToFirst()) {
                return Category(
                    id = c.getLong(c.getColumnIndexOrThrow(COL_CAT_ID)),
                    name = c.getString(c.getColumnIndexOrThrow(COL_CAT_NAME)),
                    type = TransactionType.valueOf(c.getString(c.getColumnIndexOrThrow(COL_CAT_TYPE))),
                    icon = c.getString(c.getColumnIndexOrThrow(COL_CAT_ICON)),
                    colorHex = c.getString(c.getColumnIndexOrThrow(COL_CAT_COLOR)),
                    isDefault = c.getInt(c.getColumnIndexOrThrow(COL_CAT_IS_DEFAULT)) == 1
                )
            }
        }

        // 3. Fallback: Insert category dynamically
        val cv = ContentValues().apply {
            put(COL_CAT_NAME, name)
            put(COL_CAT_TYPE, type.name)
            put(COL_CAT_ICON, defaultIcon)
            put(COL_CAT_COLOR, defaultColor)
            put(COL_CAT_IS_DEFAULT, 0)
        }
        val newId = db.insert(TABLE_CATEGORIES, null, cv)
        return Category(
            id = newId,
            name = name,
            type = type,
            icon = defaultIcon,
            colorHex = defaultColor,
            isDefault = false
        )
    }

    // --- Budgets CRUD ---
    fun getBudgets(month: Int, year: Int): List<Budget> {
        val db = readableDatabase
        val list = mutableListOf<Budget>()
        val cursor = db.query(
            TABLE_BUDGETS,
            null,
            "$COL_BUDGET_MONTH = ? AND $COL_BUDGET_YEAR = ?",
            arrayOf(month.toString(), year.toString()),
            null,
            null,
            "$COL_BUDGET_CAT_NAME ASC"
        )
        cursor.use { c ->
            while (c.moveToNext()) {
                val catIdCol = c.getColumnIndexOrThrow(COL_BUDGET_CAT_ID)
                val catId = if (c.isNull(catIdCol)) null else c.getLong(catIdCol)
                list.add(
                    Budget(
                        id = c.getLong(c.getColumnIndexOrThrow(COL_BUDGET_ID)),
                        categoryId = catId,
                        categoryName = c.getString(c.getColumnIndexOrThrow(COL_BUDGET_CAT_NAME)),
                        amount = c.getDouble(c.getColumnIndexOrThrow(COL_BUDGET_AMOUNT)),
                        month = c.getInt(c.getColumnIndexOrThrow(COL_BUDGET_MONTH)),
                        year = c.getInt(c.getColumnIndexOrThrow(COL_BUDGET_YEAR))
                    )
                )
            }
        }
        return list
    }

    fun getAllBudgets(): List<Budget> {
        val db = readableDatabase
        val list = mutableListOf<Budget>()
        val cursor = db.query(
            TABLE_BUDGETS,
            null,
            null,
            null,
            null,
            null,
            "$COL_BUDGET_YEAR DESC, $COL_BUDGET_MONTH DESC, $COL_BUDGET_CAT_NAME ASC"
        )
        cursor.use { c ->
            while (c.moveToNext()) {
                val catIdCol = c.getColumnIndexOrThrow(COL_BUDGET_CAT_ID)
                val catId = if (c.isNull(catIdCol)) null else c.getLong(catIdCol)
                list.add(
                    Budget(
                        id = c.getLong(c.getColumnIndexOrThrow(COL_BUDGET_ID)),
                        categoryId = catId,
                        categoryName = c.getString(c.getColumnIndexOrThrow(COL_BUDGET_CAT_NAME)),
                        amount = c.getDouble(c.getColumnIndexOrThrow(COL_BUDGET_AMOUNT)),
                        month = c.getInt(c.getColumnIndexOrThrow(COL_BUDGET_MONTH)),
                        year = c.getInt(c.getColumnIndexOrThrow(COL_BUDGET_YEAR))
                    )
                )
            }
        }
        return list
    }

    fun saveBudget(budget: Budget): Long {
        val db = writableDatabase
        val cv = ContentValues().apply {
            if (budget.categoryId != null) {
                put(COL_BUDGET_CAT_ID, budget.categoryId)
            } else {
                putNull(COL_BUDGET_CAT_ID)
            }
            put(COL_BUDGET_CAT_NAME, budget.categoryName)
            put(COL_BUDGET_AMOUNT, budget.amount)
            put(COL_BUDGET_MONTH, budget.month)
            put(COL_BUDGET_YEAR, budget.year)
        }
        if (budget.id > 0) {
            val rows = db.update(TABLE_BUDGETS, cv, "$COL_BUDGET_ID = ?", arrayOf(budget.id.toString()))
            return if (rows > 0) budget.id else db.insert(TABLE_BUDGETS, null, cv)
        } else {
            val cursor = if (budget.categoryId != null) {
                db.query(
                    TABLE_BUDGETS,
                    arrayOf(COL_BUDGET_ID),
                    "$COL_BUDGET_CAT_ID = ? AND $COL_BUDGET_MONTH = ? AND $COL_BUDGET_YEAR = ?",
                    arrayOf(budget.categoryId.toString(), budget.month.toString(), budget.year.toString()),
                    null, null, null
                )
            } else {
                db.query(
                    TABLE_BUDGETS,
                    arrayOf(COL_BUDGET_ID),
                    "$COL_BUDGET_CAT_ID IS NULL AND $COL_BUDGET_MONTH = ? AND $COL_BUDGET_YEAR = ?",
                    arrayOf(budget.month.toString(), budget.year.toString()),
                    null, null, null
                )
            }
            val existingId = cursor.use { c ->
                if (c.moveToFirst()) c.getLong(0) else null
            }
            return if (existingId != null && existingId > 0) {
                db.update(TABLE_BUDGETS, cv, "$COL_BUDGET_ID = ?", arrayOf(existingId.toString()))
                existingId
            } else {
                db.insert(TABLE_BUDGETS, null, cv)
            }
        }
    }

    fun deleteBudget(id: Long): Int {
        val db = writableDatabase
        return db.delete(TABLE_BUDGETS, "$COL_BUDGET_ID = ?", arrayOf(id.toString()))
    }

    fun hasBudgetsForMonth(month: Int, year: Int): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT 1 FROM $TABLE_BUDGETS WHERE $COL_BUDGET_MONTH = ? AND $COL_BUDGET_YEAR = ? LIMIT 1",
            arrayOf(month.toString(), year.toString())
        )
        return cursor.use { it.moveToFirst() }
    }

    fun copyBudgets(fromMonth: Int, fromYear: Int, toMonth: Int, toYear: Int): Boolean {
        val previousBudgets = getBudgets(fromMonth, fromYear)
        if (previousBudgets.isEmpty()) return false
        val db = writableDatabase
        db.beginTransaction()
        try {
            for (b in previousBudgets) {
                val newBudget = Budget(
                    id = 0L,
                    categoryId = b.categoryId,
                    categoryName = b.categoryName,
                    amount = b.amount,
                    month = toMonth,
                    year = toYear
                )
                saveBudget(newBudget)
            }
            db.setTransactionSuccessful()
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        } finally {
            db.endTransaction()
        }
    }

    // --- User Profile ---
    fun getUserProfile(): UserProfile {
        val db = readableDatabase
        val cursor = db.query(TABLE_PROFILE, null, "$COL_PROFILE_ID = 1", null, null, null, null)
        cursor.use { c ->
            if (c.moveToFirst()) {
                val emailCol = c.getColumnIndexOrThrow(COL_PROFILE_GOOGLE_EMAIL)
                val email = if (c.isNull(emailCol)) null else c.getString(emailCol)
                val rawName = c.getString(c.getColumnIndexOrThrow(COL_PROFILE_NAME))
                val firstNameCol = c.getColumnIndex(COL_PROFILE_FIRST_NAME)
                val lastNameCol = c.getColumnIndex(COL_PROFILE_LAST_NAME)
                val firstName = if (firstNameCol != -1 && !c.isNull(firstNameCol)) {
                    val fn = c.getString(firstNameCol)
                    if (fn.isNotBlank()) fn else (rawName.split(Regex("\\s+")).firstOrNull() ?: rawName)
                } else {
                    rawName.split(Regex("\\s+")).firstOrNull() ?: rawName
                }
                val lastName = if (lastNameCol != -1 && !c.isNull(lastNameCol)) {
                    c.getString(lastNameCol)
                } else {
                    val parts = rawName.split(Regex("\\s+"), limit = 2)
                    if (parts.size > 1) parts[1] else ""
                }
                val langCol = c.getColumnIndex(COL_PROFILE_LANGUAGE)
                val appLang = if (langCol != -1 && !c.isNull(langCol)) c.getString(langCol) else "bn"
                val keyCol = c.getColumnIndex(COL_PROFILE_GEMINI_KEY)
                var geminiKey = if (keyCol != -1 && !c.isNull(keyCol)) c.getString(keyCol)?.trim()?.ifBlank { null } else null
                val aiInsightsCol = c.getColumnIndex(COL_PROFILE_AI_INSIGHTS_ENABLED)
                val aiInsights = if (aiInsightsCol != -1 && !c.isNull(aiInsightsCol)) c.getInt(aiInsightsCol) == 1 else true
                val themeCol = c.getColumnIndex(COL_PROFILE_THEME)
                val appTheme = if (themeCol != -1 && !c.isNull(themeCol)) c.getString(themeCol) else "dark"
                val bengaliNumCol = c.getColumnIndex(COL_PROFILE_BENGALI_NUMERALS)
                val useBengaliNumerals = if (bengaliNumCol != -1 && !c.isNull(bengaliNumCol)) c.getInt(bengaliNumCol) == 1 else (appLang == "bn")

                // Dual-persistence recovery: if SQLite returned null, check SharedPreferences
                if (geminiKey.isNullOrBlank()) {
                    val fallbackKey = getPersistedApiKey()
                    if (!fallbackKey.isNullOrBlank()) {
                        geminiKey = fallbackKey
                        try {
                            val healCv = ContentValues().apply {
                                put(COL_PROFILE_GEMINI_KEY, fallbackKey)
                            }
                            writableDatabase.update(TABLE_PROFILE, healCv, "$COL_PROFILE_ID = 1", null)
                        } catch (_: Exception) {}
                    }
                } else {
                    persistApiKey(geminiKey)
                }

                return UserProfile(
                    firstName = firstName.ifBlank { "Sabbir" },
                    lastName = lastName,
                    name = rawName,
                    currencyCode = c.getString(c.getColumnIndexOrThrow(COL_PROFILE_CURRENCY_CODE)),
                    currencySymbol = c.getString(c.getColumnIndexOrThrow(COL_PROFILE_CURRENCY_SYMBOL)),
                    avatarIndex = c.getInt(c.getColumnIndexOrThrow(COL_PROFILE_AVATAR)),
                    googleAccountEmail = email,
                    isAutoSyncEnabled = c.getInt(c.getColumnIndexOrThrow(COL_PROFILE_AUTO_SYNC)) == 1,
                    lastSyncMillis = c.getLong(c.getColumnIndexOrThrow(COL_PROFILE_LAST_SYNC)),
                    appLanguage = appLang,
                    geminiApiKey = geminiKey,
                    aiInsightsEnabled = aiInsights,
                    appTheme = appTheme,
                    useBengaliNumerals = useBengaliNumerals,
                    isPrivacyModeEnabled = isPrivacyModeEnabled()
                )
            }
        }
        val fallbackKey = getPersistedApiKey()
        return UserProfile(geminiApiKey = fallbackKey, isPrivacyModeEnabled = isPrivacyModeEnabled())
    }

    fun updateUserProfile(profile: UserProfile): Int {
        val db = writableDatabase
        setPrivacyModeEnabled(profile.isPrivacyModeEnabled)
        val fallbackKey = getPersistedApiKey()
        val effectiveApiKey = profile.geminiApiKey?.trim()?.ifBlank { null } ?: fallbackKey

        if (!effectiveApiKey.isNullOrBlank()) {
            persistApiKey(effectiveApiKey)
        }

        val cv = ContentValues().apply {
            put(COL_PROFILE_NAME, profile.name)
            put(COL_PROFILE_FIRST_NAME, profile.firstName)
            put(COL_PROFILE_LAST_NAME, profile.lastName)
            put(COL_PROFILE_CURRENCY_CODE, profile.currencyCode)
            put(COL_PROFILE_CURRENCY_SYMBOL, profile.currencySymbol)
            put(COL_PROFILE_AVATAR, profile.avatarIndex)
            if (profile.googleAccountEmail != null) {
                put(COL_PROFILE_GOOGLE_EMAIL, profile.googleAccountEmail)
            } else {
                putNull(COL_PROFILE_GOOGLE_EMAIL)
            }
            put(COL_PROFILE_AUTO_SYNC, if (profile.isAutoSyncEnabled) 1 else 0)
            put(COL_PROFILE_LAST_SYNC, profile.lastSyncMillis)
            put(COL_PROFILE_LANGUAGE, profile.appLanguage)
            if (effectiveApiKey != null) {
                put(COL_PROFILE_GEMINI_KEY, effectiveApiKey)
            } else {
                putNull(COL_PROFILE_GEMINI_KEY)
            }
            put(COL_PROFILE_AI_INSIGHTS_ENABLED, if (profile.aiInsightsEnabled) 1 else 0)
            put(COL_PROFILE_THEME, profile.appTheme)
            put(COL_PROFILE_BENGALI_NUMERALS, if (profile.useBengaliNumerals) 1 else 0)
        }
        var rows = db.update(TABLE_PROFILE, cv, "$COL_PROFILE_ID = 1", null)
        if (rows == 0) {
            cv.put(COL_PROFILE_ID, 1)
            val insertId = db.insertWithOnConflict(TABLE_PROFILE, null, cv, SQLiteDatabase.CONFLICT_REPLACE)
            if (insertId != -1L) rows = 1
        }
        try {
            val cur = db.rawQuery("PRAGMA wal_checkpoint(FULL)", null)
            cur.close()
        } catch (_: Exception) {}
        return rows
    }

    // --- Accounts CRUD & Balance Calculation ---
    fun recalculateAccountBalance(db: SQLiteDatabase, accountId: Long) {
        var initial = 0.0
        val accCursor = db.query(TABLE_ACCOUNTS, arrayOf(COL_ACC_INITIAL_BALANCE), "$COL_ACC_ID = ?", arrayOf(accountId.toString()), null, null, null)
        accCursor.use {
            if (it.moveToFirst()) {
                initial = it.getDouble(0)
            }
        }

        var income = 0.0
        val incCursor = db.rawQuery("SELECT SUM($COL_TX_AMOUNT) FROM $TABLE_TRANSACTIONS WHERE $COL_TX_ACCOUNT_ID = ? AND $COL_TX_TYPE = 'INCOME'", arrayOf(accountId.toString()))
        incCursor.use {
            if (it.moveToFirst() && !it.isNull(0)) {
                income = it.getDouble(0)
            }
        }

        var expense = 0.0
        val expCursor = db.rawQuery("SELECT SUM($COL_TX_AMOUNT) FROM $TABLE_TRANSACTIONS WHERE $COL_TX_ACCOUNT_ID = ? AND $COL_TX_TYPE = 'EXPENSE'", arrayOf(accountId.toString()))
        expCursor.use {
            if (it.moveToFirst() && !it.isNull(0)) {
                expense = it.getDouble(0)
            }
        }

        val rawCurrent = initial + income - expense
        val current = kotlin.math.round(rawCurrent * 100.0) / 100.0
        val cv = ContentValues().apply {
            put(COL_ACC_CURRENT_BALANCE, current)
        }
        db.update(TABLE_ACCOUNTS, cv, "$COL_ACC_ID = ?", arrayOf(accountId.toString()))
    }

    fun recalculateAllAccountBalances(db: SQLiteDatabase) {
        // Auto-repair: Link any legacy orphaned transactions that have no valid account_id
        val orphanedCursor = db.query(
            TABLE_TRANSACTIONS,
            arrayOf(COL_TX_ID, COL_TX_PAYMENT_METHOD),
            "$COL_TX_ACCOUNT_ID IS NULL OR $COL_TX_ACCOUNT_ID NOT IN (SELECT $COL_ACC_ID FROM $TABLE_ACCOUNTS)",
            null,
            null,
            null,
            null
        )
        val orphanedTxs = mutableListOf<Pair<Long, PaymentMethod>>()
        orphanedCursor.use { c ->
            val idCol = c.getColumnIndexOrThrow(COL_TX_ID)
            val pmCol = c.getColumnIndexOrThrow(COL_TX_PAYMENT_METHOD)
            while (c.moveToNext()) {
                val txId = c.getLong(idCol)
                val pmStr = c.getString(pmCol)
                val pm = try { PaymentMethod.valueOf(pmStr) } catch (_: Exception) { PaymentMethod.CASH }
                orphanedTxs.add(Pair(txId, pm))
            }
        }
        for ((txId, pm) in orphanedTxs) {
            val (resolvedId, resolvedName) = resolveAccountForTransaction(db, null, pm)
            val repairCv = ContentValues().apply {
                put(COL_TX_ACCOUNT_ID, resolvedId)
                put(COL_TX_ACCOUNT_NAME, resolvedName)
            }
            db.update(TABLE_TRANSACTIONS, repairCv, "$COL_TX_ID = ?", arrayOf(txId.toString()))
        }

        val sql = """
            SELECT 
                a.$COL_ACC_ID,
                a.$COL_ACC_INITIAL_BALANCE,
                COALESCE(SUM(CASE WHEN t.$COL_TX_TYPE = 'INCOME' THEN t.$COL_TX_AMOUNT ELSE 0 END), 0) AS inc,
                COALESCE(SUM(CASE WHEN t.$COL_TX_TYPE = 'EXPENSE' THEN t.$COL_TX_AMOUNT ELSE 0 END), 0) AS exp
            FROM $TABLE_ACCOUNTS a
            LEFT JOIN $TABLE_TRANSACTIONS t ON a.$COL_ACC_ID = t.$COL_TX_ACCOUNT_ID
            GROUP BY a.$COL_ACC_ID, a.$COL_ACC_INITIAL_BALANCE
        """.trimIndent()

        val updates = mutableListOf<Pair<Long, Double>>()
        val cursor = db.rawQuery(sql, null)
        cursor.use { c ->
            while (c.moveToNext()) {
                val accId = c.getLong(0)
                val initBal = c.getDouble(1)
                val inc = c.getDouble(2)
                val exp = c.getDouble(3)
                val current = kotlin.math.round((initBal + inc - exp) * 100.0) / 100.0
                updates.add(Pair(accId, current))
            }
        }

        for ((accId, balance) in updates) {
            val cv = ContentValues().apply {
                put(COL_ACC_CURRENT_BALANCE, balance)
            }
            db.update(TABLE_ACCOUNTS, cv, "$COL_ACC_ID = ?", arrayOf(accId.toString()))
        }
    }

    fun getAllAccounts(): List<Account> {
        val db = readableDatabase
        val list = mutableListOf<Account>()
        val cursor = db.query(TABLE_ACCOUNTS, null, null, null, null, null, "$COL_ACC_IS_DEFAULT DESC, $COL_ACC_NAME ASC")
        cursor.use { c ->
            while (c.moveToNext()) {
                list.add(cursorToAccount(c))
            }
        }
        return list
    }

    fun getAccountById(id: Long): Account? {
        val db = readableDatabase
        val cursor = db.query(TABLE_ACCOUNTS, null, "$COL_ACC_ID = ?", arrayOf(id.toString()), null, null, null)
        cursor.use { c ->
            if (c.moveToFirst()) {
                return cursorToAccount(c)
            }
        }
        return null
    }

    fun insertAccount(acc: Account): Long {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(COL_ACC_NAME, acc.name)
            put(COL_ACC_TYPE, acc.type.name)
            put(COL_ACC_NUMBER, acc.accountNumber)
            put(COL_ACC_INSTITUTION, acc.institutionName)
            put(COL_ACC_INITIAL_BALANCE, acc.initialBalance)
            put(COL_ACC_CURRENT_BALANCE, acc.initialBalance)
            put(COL_ACC_COLOR, acc.colorHex)
            put(COL_ACC_IS_DEFAULT, if (acc.isDefault) 1 else 0)
            put(COL_ACC_CREATED_AT, if (acc.createdAt > 0) acc.createdAt else System.currentTimeMillis())
        }
        val id = db.insert(TABLE_ACCOUNTS, null, cv)
        if (id > 0) {
            recalculateAccountBalance(db, id)
        }
        return id
    }

    fun updateAccount(acc: Account): Int {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(COL_ACC_NAME, acc.name)
            put(COL_ACC_TYPE, acc.type.name)
            put(COL_ACC_NUMBER, acc.accountNumber)
            put(COL_ACC_INSTITUTION, acc.institutionName)
            put(COL_ACC_INITIAL_BALANCE, acc.initialBalance)
            put(COL_ACC_COLOR, acc.colorHex)
            put(COL_ACC_IS_DEFAULT, if (acc.isDefault) 1 else 0)
        }
        val rows = db.update(TABLE_ACCOUNTS, cv, "$COL_ACC_ID = ?", arrayOf(acc.id.toString()))
        if (rows > 0) {
            recalculateAccountBalance(db, acc.id)
            val txCv = ContentValues().apply {
                put(COL_TX_ACCOUNT_NAME, acc.name)
            }
            db.update(TABLE_TRANSACTIONS, txCv, "$COL_TX_ACCOUNT_ID = ?", arrayOf(acc.id.toString()))
            val billCv = ContentValues().apply {
                put(COL_BILL_ACCOUNT_NAME, acc.name)
            }
            db.update(TABLE_RECURRING_BILLS, billCv, "$COL_BILL_ACCOUNT_ID = ?", arrayOf(acc.id.toString()))
        }
        return rows
    }

    fun deleteAccount(id: Long): Int {
        val db = writableDatabase
        db.beginTransaction()
        try {
            // Find a replacement account (default account or any other remaining account)
            var targetAccountId = 0L
            var targetAccountName = ""
            val cur = db.rawQuery(
                "SELECT $COL_ACC_ID, $COL_ACC_NAME FROM $TABLE_ACCOUNTS WHERE $COL_ACC_ID != ? ORDER BY $COL_ACC_IS_DEFAULT DESC, $COL_ACC_ID ASC LIMIT 1",
                arrayOf(id.toString())
            )
            cur.use {
                if (it.moveToFirst()) {
                    targetAccountId = it.getLong(0)
                    targetAccountName = it.getString(1)
                }
            }

            if (targetAccountId > 0) {
                // Safely migrate all transactions belonging to deleted account to replacement account
                val txCv = ContentValues().apply {
                    put(COL_TX_ACCOUNT_ID, targetAccountId)
                    put(COL_TX_ACCOUNT_NAME, targetAccountName)
                }
                db.update(TABLE_TRANSACTIONS, txCv, "$COL_TX_ACCOUNT_ID = ?", arrayOf(id.toString()))

                val billCv = ContentValues().apply {
                    put(COL_BILL_ACCOUNT_ID, targetAccountId)
                    put(COL_BILL_ACCOUNT_NAME, targetAccountName)
                }
                db.update(TABLE_RECURRING_BILLS, billCv, "$COL_BILL_ACCOUNT_ID = ?", arrayOf(id.toString()))
            } else {
                val billCv = ContentValues().apply {
                    putNull(COL_BILL_ACCOUNT_ID)
                    putNull(COL_BILL_ACCOUNT_NAME)
                }
                db.update(TABLE_RECURRING_BILLS, billCv, "$COL_BILL_ACCOUNT_ID = ?", arrayOf(id.toString()))
            }

            val rows = db.delete(TABLE_ACCOUNTS, "$COL_ACC_ID = ?", arrayOf(id.toString()))
            if (targetAccountId > 0) {
                recalculateAccountBalance(db, targetAccountId)
            }
            db.setTransactionSuccessful()
            return rows
        } finally {
            db.endTransaction()
        }
    }

    fun transferBetweenAccounts(
        fromAccountId: Long,
        toAccountId: Long,
        amount: Double,
        note: String = ""
    ): Boolean {
        if (amount <= 0.0 || fromAccountId == toAccountId) return false
        val fromAcc = getAccountById(fromAccountId) ?: return false
        val toAcc = getAccountById(toAccountId) ?: return false

        val db = writableDatabase
        db.beginTransaction()
        try {
            val now = System.currentTimeMillis()
            val transferNote = if (note.isNotBlank()) "[Transfer] $note" else "[Transfer] Fund Transfer"
            val outCategory = getOrCreateCategoryByName("Transfer", TransactionType.EXPENSE, "SwapHoriz", "#6C5CE7")
            val inCategory = getOrCreateCategoryByName("Transfer", TransactionType.INCOME, "SwapHoriz", "#6C5CE7")

            // 1. Outgoing from source account
            val expenseTx = Transaction(
                id = 0,
                title = "Transfer to ${toAcc.name}",
                amount = amount,
                type = TransactionType.EXPENSE,
                categoryId = outCategory.id,
                categoryName = outCategory.name,
                categoryIcon = outCategory.icon,
                categoryColor = outCategory.colorHex,
                dateMillis = now,
                paymentMethod = when (fromAcc.type) {
                    AccountType.BANK -> PaymentMethod.BANK_TRANSFER
                    AccountType.MFS -> if (fromAcc.institutionName.contains("bkash", true)) PaymentMethod.BKASH else PaymentMethod.NAGAD
                    AccountType.CASH -> PaymentMethod.CASH
                },
                notes = transferNote,
                accountId = fromAccountId,
                accountName = fromAcc.name
            )
            insertTransaction(expenseTx)

            // 2. Incoming to destination account
            val incomeTx = Transaction(
                id = 0,
                title = "Transfer from ${fromAcc.name}",
                amount = amount,
                type = TransactionType.INCOME,
                categoryId = inCategory.id,
                categoryName = inCategory.name,
                categoryIcon = inCategory.icon,
                categoryColor = inCategory.colorHex,
                dateMillis = now,
                paymentMethod = when (toAcc.type) {
                    AccountType.BANK -> PaymentMethod.BANK_TRANSFER
                    AccountType.MFS -> if (toAcc.institutionName.contains("bkash", true)) PaymentMethod.BKASH else PaymentMethod.NAGAD
                    AccountType.CASH -> PaymentMethod.CASH
                },
                notes = transferNote,
                accountId = toAccountId,
                accountName = toAcc.name
            )
            insertTransaction(incomeTx)

            recalculateAccountBalance(db, fromAccountId)
            recalculateAccountBalance(db, toAccountId)

            db.setTransactionSuccessful()
            return true
        } catch (_: Exception) {
            return false
        } finally {
            db.endTransaction()
        }
    }

    private fun cursorToAccount(c: Cursor): Account {
        return Account(
            id = c.getLong(c.getColumnIndexOrThrow(COL_ACC_ID)),
            name = c.getString(c.getColumnIndexOrThrow(COL_ACC_NAME)),
            type = AccountType.valueOf(c.getString(c.getColumnIndexOrThrow(COL_ACC_TYPE))),
            accountNumber = c.getString(c.getColumnIndexOrThrow(COL_ACC_NUMBER)),
            institutionName = c.getString(c.getColumnIndexOrThrow(COL_ACC_INSTITUTION)),
            initialBalance = c.getDouble(c.getColumnIndexOrThrow(COL_ACC_INITIAL_BALANCE)),
            currentBalance = c.getDouble(c.getColumnIndexOrThrow(COL_ACC_CURRENT_BALANCE)),
            colorHex = c.getString(c.getColumnIndexOrThrow(COL_ACC_COLOR)),
            isDefault = c.getInt(c.getColumnIndexOrThrow(COL_ACC_IS_DEFAULT)) == 1,
            createdAt = c.getLong(c.getColumnIndexOrThrow(COL_ACC_CREATED_AT))
        )
    }

    // --- Receivables (পাওনা টাকা) CRUD ---
    fun getAllReceivables(): List<Receivable> {
        val db = readableDatabase
        val list = mutableListOf<Receivable>()
        val cursor = db.query(TABLE_RECEIVABLES, null, null, null, null, null, "$COL_REC_STATUS ASC, $COL_REC_CREATED_DATE DESC")
        cursor.use { c ->
            while (c.moveToNext()) {
                list.add(cursorToReceivable(c))
            }
        }
        return list
    }

    fun getReceivableById(id: Long): Receivable? {
        val db = readableDatabase
        val cursor = db.query(TABLE_RECEIVABLES, null, "$COL_REC_ID = ?", arrayOf(id.toString()), null, null, null)
        cursor.use { c ->
            if (c.moveToFirst()) {
                return cursorToReceivable(c)
            }
        }
        return null
    }

    fun insertReceivable(receivable: Receivable, deductFromAccountId: Long? = null): Long {
        val db = writableDatabase
        db.beginTransaction()
        try {
            val cv = ContentValues().apply {
                put(COL_REC_PERSON_NAME, receivable.personName)
                put(COL_REC_PHONE, receivable.phoneNumber)
                put(COL_REC_AMOUNT, receivable.amount)
                put(COL_REC_AMOUNT_RECEIVED, receivable.amountReceived)
                if (receivable.dueDateMillis != null) put(COL_REC_DUE_DATE, receivable.dueDateMillis) else putNull(COL_REC_DUE_DATE)
                put(COL_REC_CREATED_DATE, receivable.createdDateMillis)
                put(COL_REC_NOTES, receivable.notes)
                put(COL_REC_STATUS, receivable.status.name)
            }
            val id = db.insert(TABLE_RECEIVABLES, null, cv)

            if (id > 0 && deductFromAccountId != null) {
                val acc = getAccountById(deductFromAccountId)
                val loanCat = getOrCreateCategoryByName("Loan & Debt Given", TransactionType.EXPENSE, "Handshake", "#FF758F")
                val expenseTx = Transaction(
                    id = 0,
                    title = "Lent to ${receivable.personName}",
                    amount = receivable.amount,
                    type = TransactionType.EXPENSE,
                    categoryId = loanCat.id,
                    categoryName = loanCat.name,
                    categoryIcon = loanCat.icon,
                    categoryColor = loanCat.colorHex,
                    dateMillis = receivable.createdDateMillis,
                    paymentMethod = when (acc?.type) {
                        AccountType.BANK -> PaymentMethod.BANK_TRANSFER
                        AccountType.MFS -> if (acc.institutionName.contains("bkash", true)) PaymentMethod.BKASH else PaymentMethod.NAGAD
                        else -> PaymentMethod.CASH
                    },
                    notes = "Money owed by ${receivable.personName}: ${receivable.notes}",
                    accountId = deductFromAccountId,
                    accountName = acc?.name
                )
                insertTransaction(expenseTx)
            }

            db.setTransactionSuccessful()
            return id
        } finally {
            db.endTransaction()
        }
    }

    fun updateReceivable(receivable: Receivable): Int {
        val db = writableDatabase
        val computedStatus = when {
            receivable.amountReceived >= receivable.amount - 0.001 -> ReceivableStatus.SETTLED
            receivable.amountReceived > 0.0 -> ReceivableStatus.PARTIAL
            else -> ReceivableStatus.PENDING
        }
        val cv = ContentValues().apply {
            put(COL_REC_PERSON_NAME, receivable.personName)
            put(COL_REC_PHONE, receivable.phoneNumber)
            put(COL_REC_AMOUNT, receivable.amount)
            put(COL_REC_AMOUNT_RECEIVED, receivable.amountReceived)
            if (receivable.dueDateMillis != null) put(COL_REC_DUE_DATE, receivable.dueDateMillis) else putNull(COL_REC_DUE_DATE)
            put(COL_REC_NOTES, receivable.notes)
            put(COL_REC_STATUS, computedStatus.name)
        }
        return db.update(TABLE_RECEIVABLES, cv, "$COL_REC_ID = ?", arrayOf(receivable.id.toString()))
    }

    fun deleteReceivable(id: Long): Int {
        val db = writableDatabase
        return db.delete(TABLE_RECEIVABLES, "$COL_REC_ID = ?", arrayOf(id.toString()))
    }

    fun recordReceivablePayment(
        receivableId: Long,
        amountPaid: Double,
        depositToAccountId: Long? = null,
        note: String = ""
    ): Boolean {
        if (amountPaid <= 0.0) return false
        val rec = getReceivableById(receivableId) ?: return false

        val db = writableDatabase
        db.beginTransaction()
        try {
            val newReceived = (rec.amountReceived + amountPaid).coerceAtMost(rec.amount)
            val isFullyPaid = newReceived >= rec.amount - 0.001
            val newStatus = if (isFullyPaid) ReceivableStatus.SETTLED else ReceivableStatus.PARTIAL

            val cv = ContentValues().apply {
                put(COL_REC_AMOUNT_RECEIVED, newReceived)
                put(COL_REC_STATUS, newStatus.name)
            }
            db.update(TABLE_RECEIVABLES, cv, "$COL_REC_ID = ?", arrayOf(receivableId.toString()))

            val (targetAccountId, targetAccountName) = resolveAccountForTransaction(db, depositToAccountId, PaymentMethod.CASH)
            val acc = getAccountById(targetAccountId)
            val recoveryCat = getOrCreateCategoryByName("Debt Recovery", TransactionType.INCOME, "Handshake", "#2EC4B6")
            val incomeTx = Transaction(
                id = 0,
                title = "Repayment from ${rec.personName}",
                amount = amountPaid,
                type = TransactionType.INCOME,
                categoryId = recoveryCat.id,
                categoryName = recoveryCat.name,
                categoryIcon = recoveryCat.icon,
                categoryColor = recoveryCat.colorHex,
                dateMillis = System.currentTimeMillis(),
                paymentMethod = when (acc?.type) {
                    AccountType.BANK -> PaymentMethod.BANK_TRANSFER
                    AccountType.MFS -> if (acc.institutionName.contains("bkash", true)) PaymentMethod.BKASH else PaymentMethod.NAGAD
                    else -> PaymentMethod.CASH
                },
                notes = if (note.isNotBlank()) note else "Pawnar taka ferot: ${rec.personName}",
                accountId = targetAccountId,
                accountName = targetAccountName
            )
            insertTransaction(incomeTx)

            db.setTransactionSuccessful()
            return true
        } catch (_: Exception) {
            return false
        } finally {
            db.endTransaction()
        }
    }

    fun getReceivablesSummary(): ReceivablesSummary {
        val list = getAllReceivables()
        var totalLent = 0.0
        var totalCollected = 0.0
        var pendingCount = 0

        for (r in list) {
            totalLent += r.amount
            totalCollected += r.amountReceived
            if (!r.isSettled) {
                pendingCount++
            }
        }
        val remainingDue = (totalLent - totalCollected).coerceAtLeast(0.0)
        return ReceivablesSummary(
            totalLent = totalLent,
            totalCollected = totalCollected,
            remainingDue = remainingDue,
            pendingCount = pendingCount
        )
    }

    private fun cursorToReceivable(c: Cursor): Receivable {
        val dueIndex = c.getColumnIndexOrThrow(COL_REC_DUE_DATE)
        val dueDate = if (c.isNull(dueIndex)) null else c.getLong(dueIndex)
        val statusStr = c.getString(c.getColumnIndexOrThrow(COL_REC_STATUS))
        val status = try {
            ReceivableStatus.valueOf(statusStr)
        } catch (_: Exception) {
            ReceivableStatus.PENDING
        }

        return Receivable(
            id = c.getLong(c.getColumnIndexOrThrow(COL_REC_ID)),
            personName = c.getString(c.getColumnIndexOrThrow(COL_REC_PERSON_NAME)),
            phoneNumber = c.getString(c.getColumnIndexOrThrow(COL_REC_PHONE)),
            amount = c.getDouble(c.getColumnIndexOrThrow(COL_REC_AMOUNT)),
            amountReceived = c.getDouble(c.getColumnIndexOrThrow(COL_REC_AMOUNT_RECEIVED)),
            dueDateMillis = dueDate,
            createdDateMillis = c.getLong(c.getColumnIndexOrThrow(COL_REC_CREATED_DATE)),
            notes = c.getString(c.getColumnIndexOrThrow(COL_REC_NOTES)),
            status = status
        )
    }

    // --- Payables (দেনা টাকা) CRUD ---
    fun getAllPayables(): List<Payable> {
        val db = readableDatabase
        val list = mutableListOf<Payable>()
        val cursor = db.query(TABLE_PAYABLES, null, null, null, null, null, "$COL_PAY_STATUS ASC, $COL_PAY_CREATED_DATE DESC")
        cursor.use { c ->
            while (c.moveToNext()) {
                list.add(cursorToPayable(c))
            }
        }
        return list
    }

    fun getPayableById(id: Long): Payable? {
        val db = readableDatabase
        val cursor = db.query(TABLE_PAYABLES, null, "$COL_PAY_ID = ?", arrayOf(id.toString()), null, null, null)
        cursor.use { c ->
            if (c.moveToFirst()) {
                return cursorToPayable(c)
            }
        }
        return null
    }

    fun insertPayable(payable: Payable, depositToAccountId: Long? = null): Long {
        val db = writableDatabase
        db.beginTransaction()
        try {
            val cv = ContentValues().apply {
                put(COL_PAY_PERSON_NAME, payable.personName)
                put(COL_PAY_PHONE, payable.phoneNumber)
                put(COL_PAY_AMOUNT, payable.amount)
                put(COL_PAY_AMOUNT_PAID, payable.amountPaid)
                if (payable.dueDateMillis != null) put(COL_PAY_DUE_DATE, payable.dueDateMillis) else putNull(COL_PAY_DUE_DATE)
                put(COL_PAY_CREATED_DATE, payable.createdDateMillis)
                put(COL_PAY_NOTES, payable.notes)
                put(COL_PAY_STATUS, payable.status.name)
            }
            val id = db.insert(TABLE_PAYABLES, null, cv)

            if (id > 0 && depositToAccountId != null) {
                val acc = getAccountById(depositToAccountId)
                val loanCat = getOrCreateCategoryByName("Loan & Debt Borrowed", TransactionType.INCOME, "Handshake", "#3B8FF0")
                val incomeTx = Transaction(
                    id = 0,
                    title = "Borrowed from ${payable.personName}",
                    amount = payable.amount,
                    type = TransactionType.INCOME,
                    categoryId = loanCat.id,
                    categoryName = loanCat.name,
                    categoryIcon = loanCat.icon,
                    categoryColor = loanCat.colorHex,
                    dateMillis = payable.createdDateMillis,
                    paymentMethod = when (acc?.type) {
                        AccountType.BANK -> PaymentMethod.BANK_TRANSFER
                        AccountType.MFS -> if (acc.institutionName.contains("bkash", true)) PaymentMethod.BKASH else PaymentMethod.NAGAD
                        else -> PaymentMethod.CASH
                    },
                    notes = "Loan borrowed from ${payable.personName}: ${payable.notes}",
                    accountId = depositToAccountId,
                    accountName = acc?.name
                )
                insertTransaction(incomeTx)
            }

            db.setTransactionSuccessful()
            return id
        } finally {
            db.endTransaction()
        }
    }

    fun updatePayable(payable: Payable): Int {
        val db = writableDatabase
        val computedStatus = when {
            payable.amountPaid >= payable.amount - 0.001 -> PayableStatus.SETTLED
            payable.amountPaid > 0.0 -> PayableStatus.PARTIAL
            else -> PayableStatus.PENDING
        }
        val cv = ContentValues().apply {
            put(COL_PAY_PERSON_NAME, payable.personName)
            put(COL_PAY_PHONE, payable.phoneNumber)
            put(COL_PAY_AMOUNT, payable.amount)
            put(COL_PAY_AMOUNT_PAID, payable.amountPaid)
            if (payable.dueDateMillis != null) put(COL_PAY_DUE_DATE, payable.dueDateMillis) else putNull(COL_PAY_DUE_DATE)
            put(COL_PAY_NOTES, payable.notes)
            put(COL_PAY_STATUS, computedStatus.name)
        }
        return db.update(TABLE_PAYABLES, cv, "$COL_PAY_ID = ?", arrayOf(payable.id.toString()))
    }

    fun deletePayable(id: Long): Int {
        val db = writableDatabase
        return db.delete(TABLE_PAYABLES, "$COL_PAY_ID = ?", arrayOf(id.toString()))
    }

    fun recordPayablePayment(
        payableId: Long,
        amountPaid: Double,
        deductFromAccountId: Long? = null,
        note: String = ""
    ): Boolean {
        if (amountPaid <= 0.0) return false
        val pay = getPayableById(payableId) ?: return false

        val db = writableDatabase
        db.beginTransaction()
        try {
            val newPaid = (pay.amountPaid + amountPaid).coerceAtMost(pay.amount)
            val isFullyPaid = newPaid >= pay.amount - 0.001
            val newStatus = if (isFullyPaid) PayableStatus.SETTLED else PayableStatus.PARTIAL

            val cv = ContentValues().apply {
                put(COL_PAY_AMOUNT_PAID, newPaid)
                put(COL_PAY_STATUS, newStatus.name)
            }
            db.update(TABLE_PAYABLES, cv, "$COL_PAY_ID = ?", arrayOf(payableId.toString()))

            val (sourceAccountId, sourceAccountName) = resolveAccountForTransaction(db, deductFromAccountId, PaymentMethod.CASH)
            val acc = getAccountById(sourceAccountId)
            val repaymentCat = getOrCreateCategoryByName("Debt Repayment", TransactionType.EXPENSE, "Handshake", "#E63946")
            val expenseTx = Transaction(
                id = 0,
                title = "Repaid to ${pay.personName}",
                amount = amountPaid,
                type = TransactionType.EXPENSE,
                categoryId = repaymentCat.id,
                categoryName = repaymentCat.name,
                categoryIcon = repaymentCat.icon,
                categoryColor = repaymentCat.colorHex,
                dateMillis = System.currentTimeMillis(),
                paymentMethod = when (acc?.type) {
                    AccountType.BANK -> PaymentMethod.BANK_TRANSFER
                    AccountType.MFS -> if (acc.institutionName.contains("bkash", true)) PaymentMethod.BKASH else PaymentMethod.NAGAD
                    else -> PaymentMethod.CASH
                },
                notes = if (note.isNotBlank()) note else "Denar taka porishodh: ${pay.personName}",
                accountId = sourceAccountId,
                accountName = sourceAccountName
            )
            insertTransaction(expenseTx)

            db.setTransactionSuccessful()
            return true
        } catch (_: Exception) {
            return false
        } finally {
            db.endTransaction()
        }
    }

    fun getPayablesSummary(): PayablesSummary {
        val list = getAllPayables()
        var totalBorrowed = 0.0
        var totalRepaid = 0.0
        var pendingCount = 0

        for (p in list) {
            totalBorrowed += p.amount
            totalRepaid += p.amountPaid
            if (!p.isSettled) {
                pendingCount++
            }
        }
        val remainingDue = (totalBorrowed - totalRepaid).coerceAtLeast(0.0)
        return PayablesSummary(
            totalBorrowed = totalBorrowed,
            totalRepaid = totalRepaid,
            remainingDue = remainingDue,
            pendingCount = pendingCount
        )
    }

    fun getDebtBookSummary(): DebtBookSummary {
        val recSummary = getReceivablesSummary()
        val paySummary = getPayablesSummary()
        return DebtBookSummary(
            totalReceivable = recSummary.remainingDue,
            totalPayable = paySummary.remainingDue
        )
    }

    private fun cursorToPayable(c: Cursor): Payable {
        val dueIndex = c.getColumnIndexOrThrow(COL_PAY_DUE_DATE)
        val dueDate = if (c.isNull(dueIndex)) null else c.getLong(dueIndex)
        val statusStr = c.getString(c.getColumnIndexOrThrow(COL_PAY_STATUS))
        val status = try {
            PayableStatus.valueOf(statusStr)
        } catch (_: Exception) {
            PayableStatus.PENDING
        }
        return Payable(
            id = c.getLong(c.getColumnIndexOrThrow(COL_PAY_ID)),
            personName = c.getString(c.getColumnIndexOrThrow(COL_PAY_PERSON_NAME)),
            phoneNumber = c.getString(c.getColumnIndexOrThrow(COL_PAY_PHONE)),
            amount = c.getDouble(c.getColumnIndexOrThrow(COL_PAY_AMOUNT)),
            amountPaid = c.getDouble(c.getColumnIndexOrThrow(COL_PAY_AMOUNT_PAID)),
            dueDateMillis = dueDate,
            createdDateMillis = c.getLong(c.getColumnIndexOrThrow(COL_PAY_CREATED_DATE)),
            notes = c.getString(c.getColumnIndexOrThrow(COL_PAY_NOTES)),
            status = status
        )
    }

    // --- Savings Goals (সঞ্চয় লক্ষ্যমাত্রা) CRUD ---
    fun getAllSavingsGoals(): List<SavingsGoal> {
        val db = readableDatabase
        val list = mutableListOf<SavingsGoal>()
        val cursor = db.query(TABLE_SAVINGS_GOALS, null, null, null, null, null, "$COL_GOAL_IS_COMPLETED ASC, $COL_GOAL_CREATED_DATE DESC")
        cursor.use { c ->
            while (c.moveToNext()) {
                list.add(cursorToSavingsGoal(c))
            }
        }
        return list
    }

    fun getSavingsGoalById(id: Long): SavingsGoal? {
        val db = readableDatabase
        val cursor = db.query(TABLE_SAVINGS_GOALS, null, "$COL_GOAL_ID = ?", arrayOf(id.toString()), null, null, null)
        cursor.use { c ->
            if (c.moveToFirst()) {
                return cursorToSavingsGoal(c)
            }
        }
        return null
    }

    fun insertSavingsGoal(goal: SavingsGoal): Long {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(COL_GOAL_TITLE, goal.title)
            put(COL_GOAL_TARGET_AMOUNT, goal.targetAmount)
            put(COL_GOAL_SAVED_AMOUNT, goal.savedAmount)
            if (goal.targetDateMillis != null) put(COL_GOAL_TARGET_DATE, goal.targetDateMillis) else putNull(COL_GOAL_TARGET_DATE)
            put(COL_GOAL_CREATED_DATE, goal.createdDateMillis)
            put(COL_GOAL_ICON, goal.icon)
            put(COL_GOAL_COLOR, goal.colorHex)
            put(COL_GOAL_NOTES, goal.notes)
            put(COL_GOAL_IS_COMPLETED, if (goal.isCompleted || goal.savedAmount >= goal.targetAmount) 1 else 0)
        }
        return db.insert(TABLE_SAVINGS_GOALS, null, cv)
    }

    fun updateSavingsGoal(goal: SavingsGoal): Int {
        val db = writableDatabase
        val isComplete = goal.isCompleted || (goal.targetAmount > 0 && goal.savedAmount >= goal.targetAmount - 0.001)
        val cv = ContentValues().apply {
            put(COL_GOAL_TITLE, goal.title)
            put(COL_GOAL_TARGET_AMOUNT, goal.targetAmount)
            put(COL_GOAL_SAVED_AMOUNT, goal.savedAmount)
            if (goal.targetDateMillis != null) put(COL_GOAL_TARGET_DATE, goal.targetDateMillis) else putNull(COL_GOAL_TARGET_DATE)
            put(COL_GOAL_ICON, goal.icon)
            put(COL_GOAL_COLOR, goal.colorHex)
            put(COL_GOAL_NOTES, goal.notes)
            put(COL_GOAL_IS_COMPLETED, if (isComplete) 1 else 0)
        }
        return db.update(TABLE_SAVINGS_GOALS, cv, "$COL_GOAL_ID = ?", arrayOf(goal.id.toString()))
    }

    fun deleteSavingsGoal(id: Long): Int {
        val db = writableDatabase
        return db.delete(TABLE_SAVINGS_GOALS, "$COL_GOAL_ID = ?", arrayOf(id.toString()))
    }

    fun addSavingsContribution(
        goalId: Long,
        amount: Double,
        deductFromAccountId: Long? = null,
        note: String = ""
    ): Boolean {
        if (amount <= 0.0) return false
        val goal = getSavingsGoalById(goalId) ?: return false

        val db = writableDatabase
        db.beginTransaction()
        try {
            val newSaved = goal.savedAmount + amount
            val isNowCompleted = newSaved >= goal.targetAmount - 0.001

            val cv = ContentValues().apply {
                put(COL_GOAL_SAVED_AMOUNT, newSaved)
                put(COL_GOAL_IS_COMPLETED, if (isNowCompleted) 1 else 0)
            }
            db.update(TABLE_SAVINGS_GOALS, cv, "$COL_GOAL_ID = ?", arrayOf(goalId.toString()))

            if (deductFromAccountId != null) {
                val (sourceAccountId, sourceAccountName) = resolveAccountForTransaction(db, deductFromAccountId, PaymentMethod.CASH)
                val acc = getAccountById(sourceAccountId)
                val savingsCat = getOrCreateCategoryByName("Savings & Investment", TransactionType.EXPENSE, "Savings", "#10B981")
                val expenseTx = Transaction(
                    id = 0,
                    title = "Saved for: ${goal.title}",
                    amount = amount,
                    type = TransactionType.EXPENSE,
                    categoryId = savingsCat.id,
                    categoryName = savingsCat.name,
                    categoryIcon = savingsCat.icon,
                    categoryColor = savingsCat.colorHex,
                    dateMillis = System.currentTimeMillis(),
                    paymentMethod = when (acc?.type) {
                        AccountType.BANK -> PaymentMethod.BANK_TRANSFER
                        AccountType.MFS -> if (acc.institutionName.contains("bkash", true)) PaymentMethod.BKASH else PaymentMethod.NAGAD
                        else -> PaymentMethod.CASH
                    },
                    notes = if (note.isNotBlank()) note else "Savings deposit: ${goal.title}",
                    accountId = sourceAccountId,
                    accountName = sourceAccountName
                )
                insertTransaction(expenseTx)
            }

            db.setTransactionSuccessful()
            return true
        } catch (_: Exception) {
            return false
        } finally {
            db.endTransaction()
        }
    }

    private fun cursorToSavingsGoal(c: Cursor): SavingsGoal {
        val targetDateIndex = c.getColumnIndexOrThrow(COL_GOAL_TARGET_DATE)
        val targetDate = if (c.isNull(targetDateIndex)) null else c.getLong(targetDateIndex)
        return SavingsGoal(
            id = c.getLong(c.getColumnIndexOrThrow(COL_GOAL_ID)),
            title = c.getString(c.getColumnIndexOrThrow(COL_GOAL_TITLE)),
            targetAmount = c.getDouble(c.getColumnIndexOrThrow(COL_GOAL_TARGET_AMOUNT)),
            savedAmount = c.getDouble(c.getColumnIndexOrThrow(COL_GOAL_SAVED_AMOUNT)),
            targetDateMillis = targetDate,
            createdDateMillis = c.getLong(c.getColumnIndexOrThrow(COL_GOAL_CREATED_DATE)),
            icon = c.getString(c.getColumnIndexOrThrow(COL_GOAL_ICON)),
            colorHex = c.getString(c.getColumnIndexOrThrow(COL_GOAL_COLOR)),
            notes = c.getString(c.getColumnIndexOrThrow(COL_GOAL_NOTES)),
            isCompleted = c.getInt(c.getColumnIndexOrThrow(COL_GOAL_IS_COMPLETED)) == 1
        )
    }

    // --- Backup & Restore ---
    fun createBackupPayload(): BackupPayload {
        return BackupPayload(
            version = 4,
            exportDateMillis = System.currentTimeMillis(),
            profile = getUserProfile(),
            categories = getAllCategories(),
            transactions = getAllTransactions(),
            budgets = getAllBudgets(),
            accounts = getAllAccounts(),
            receivables = getAllReceivables(),
            recurringBills = getAllRecurringBills(),
            payables = getAllPayables(),
            savingsGoals = getAllSavingsGoals()
        )
    }

    fun restoreFromBackup(payload: BackupPayload): Boolean {
        val db = writableDatabase
        db.beginTransaction()
        try {
            // Restore Profile - preserve currently connected Google Account email and Gemini API Key on device
            val currentProfile = getUserProfile()
            val effectiveGoogleEmail = currentProfile.googleAccountEmail ?: payload.profile.googleAccountEmail
            val effectiveApiKey = currentProfile.geminiApiKey ?: payload.profile.geminiApiKey

            val baseProfile = if (payload.profile.firstName.isBlank() && payload.profile.name.isNotBlank()) {
                val parts = payload.profile.name.trim().split(Regex("\\s+"), limit = 2)
                payload.profile.copy(
                    firstName = parts.getOrNull(0) ?: "Sabbir",
                    lastName = parts.getOrNull(1) ?: ""
                )
            } else {
                payload.profile
            }
            val profileToRestore = baseProfile.copy(
                googleAccountEmail = effectiveGoogleEmail,
                geminiApiKey = effectiveApiKey,
                lastSyncMillis = System.currentTimeMillis()
            )
            updateUserProfile(profileToRestore)

            // Clear & Restore Categories
            db.delete(TABLE_CATEGORIES, null, null)
            if (payload.categories.isNotEmpty()) {
                for (cat in payload.categories) {
                    val cv = ContentValues().apply {
                        put(COL_CAT_ID, cat.id)
                        put(COL_CAT_NAME, cat.name)
                        put(COL_CAT_TYPE, cat.type.name)
                        put(COL_CAT_ICON, cat.icon)
                        put(COL_CAT_COLOR, cat.colorHex)
                        put(COL_CAT_IS_DEFAULT, if (cat.isDefault) 1 else 0)
                    }
                    db.insert(TABLE_CATEGORIES, null, cv)
                }
            } else {
                seedDefaultCategories(db)
            }
            reconcileAndDeduplicateCategoryColors(db)

            // Clear & Restore Accounts
            db.delete(TABLE_ACCOUNTS, null, null)
            if (payload.accounts.isNotEmpty()) {
                for (acc in payload.accounts) {
                    val cv = ContentValues().apply {
                        put(COL_ACC_ID, acc.id)
                        put(COL_ACC_NAME, acc.name)
                        put(COL_ACC_TYPE, acc.type.name)
                        put(COL_ACC_NUMBER, acc.accountNumber)
                        put(COL_ACC_INSTITUTION, acc.institutionName)
                        put(COL_ACC_INITIAL_BALANCE, acc.initialBalance)
                        put(COL_ACC_CURRENT_BALANCE, acc.currentBalance)
                        put(COL_ACC_COLOR, acc.colorHex)
                        put(COL_ACC_IS_DEFAULT, if (acc.isDefault) 1 else 0)
                        put(COL_ACC_CREATED_AT, acc.createdAt)
                    }
                    db.insert(TABLE_ACCOUNTS, null, cv)
                }
            } else {
                seedDefaultAccountsIfEmpty(db)
            }

            // Clear & Restore Transactions
            db.delete(TABLE_TRANSACTIONS, null, null)
            for (tx in payload.transactions) {
                val cv = ContentValues().apply {
                    put(COL_TX_ID, tx.id)
                    put(COL_TX_TITLE, tx.title)
                    put(COL_TX_AMOUNT, tx.amount)
                    put(COL_TX_TYPE, tx.type.name)
                    put(COL_TX_CAT_ID, tx.categoryId)
                    put(COL_TX_CAT_NAME, tx.categoryName)
                    put(COL_TX_CAT_ICON, tx.categoryIcon)
                    put(COL_TX_CAT_COLOR, tx.categoryColor)
                    put(COL_TX_DATE, tx.dateMillis)
                    put(COL_TX_PAYMENT_METHOD, tx.paymentMethod.name)
                    put(COL_TX_NOTES, tx.notes)
                    if (tx.accountId != null) put(COL_TX_ACCOUNT_ID, tx.accountId) else putNull(COL_TX_ACCOUNT_ID)
                    if (tx.accountName != null) put(COL_TX_ACCOUNT_NAME, tx.accountName) else putNull(COL_TX_ACCOUNT_NAME)
                }
                db.insert(TABLE_TRANSACTIONS, null, cv)
            }

            // Clear & Restore Budgets
            db.delete(TABLE_BUDGETS, null, null)
            if (payload.budgets.isNotEmpty()) {
                for (b in payload.budgets) {
                    val cv = ContentValues().apply {
                        put(COL_BUDGET_ID, b.id)
                        if (b.categoryId != null) put(COL_BUDGET_CAT_ID, b.categoryId) else putNull(COL_BUDGET_CAT_ID)
                        put(COL_BUDGET_CAT_NAME, b.categoryName)
                        put(COL_BUDGET_AMOUNT, b.amount)
                        put(COL_BUDGET_MONTH, b.month)
                        put(COL_BUDGET_YEAR, b.year)
                    }
                    db.insert(TABLE_BUDGETS, null, cv)
                }
            } else {
                val cal = Calendar.getInstance()
                val budgetCv = ContentValues().apply {
                    putNull(COL_BUDGET_CAT_ID)
                    put(COL_BUDGET_CAT_NAME, "Overall Monthly")
                    put(COL_BUDGET_AMOUNT, 35000.0)
                    put(COL_BUDGET_MONTH, cal.get(Calendar.MONTH))
                    put(COL_BUDGET_YEAR, cal.get(Calendar.YEAR))
                }
                db.insert(TABLE_BUDGETS, null, budgetCv)
            }

            // Clear & Restore Receivables unconditionally
            db.delete(TABLE_RECEIVABLES, null, null)
            for (r in payload.receivables) {
                val cv = ContentValues().apply {
                    put(COL_REC_ID, r.id)
                    put(COL_REC_PERSON_NAME, r.personName)
                    put(COL_REC_PHONE, r.phoneNumber)
                    put(COL_REC_AMOUNT, r.amount)
                    put(COL_REC_AMOUNT_RECEIVED, r.amountReceived)
                    if (r.dueDateMillis != null) put(COL_REC_DUE_DATE, r.dueDateMillis) else putNull(COL_REC_DUE_DATE)
                    put(COL_REC_CREATED_DATE, r.createdDateMillis)
                    put(COL_REC_NOTES, r.notes)
                    put(COL_REC_STATUS, r.status.name)
                }
                db.insert(TABLE_RECEIVABLES, null, cv)
            }

            // Clear & Restore Recurring Bills
            db.delete(TABLE_RECURRING_BILLS, null, null)
            if (payload.recurringBills.isNotEmpty()) {
                for (bill in payload.recurringBills) {
                    val cv = ContentValues().apply {
                        put(COL_BILL_ID, bill.id)
                        put(COL_BILL_TITLE, bill.title)
                        put(COL_BILL_AMOUNT, bill.amount)
                        put(COL_BILL_CAT_ID, bill.categoryId)
                        put(COL_BILL_CAT_NAME, bill.categoryName)
                        put(COL_BILL_CAT_ICON, bill.categoryIcon)
                        put(COL_BILL_CAT_COLOR, bill.categoryColor)
                        if (bill.accountId != null) put(COL_BILL_ACCOUNT_ID, bill.accountId) else putNull(COL_BILL_ACCOUNT_ID)
                        if (bill.accountName != null) put(COL_BILL_ACCOUNT_NAME, bill.accountName) else putNull(COL_BILL_ACCOUNT_NAME)
                        put(COL_BILL_FREQUENCY, bill.frequency.name)
                        put(COL_BILL_DUE_DAY, bill.dueDay)
                        put(COL_BILL_NEXT_DUE, bill.nextDueDateMillis)
                        if (bill.lastPaidDateMillis != null) put(COL_BILL_LAST_PAID, bill.lastPaidDateMillis) else putNull(COL_BILL_LAST_PAID)
                        put(COL_BILL_IS_ACTIVE, if (bill.isActive) 1 else 0)
                        put(COL_BILL_NOTES, bill.notes)
                    }
                    db.insert(TABLE_RECURRING_BILLS, null, cv)
                }
            } else if (payload.version < 4) {
                seedSampleRecurringBillsIfEmpty(db)
            }

            // Clear & Restore Payables
            db.delete(TABLE_PAYABLES, null, null)
            for (p in payload.payables) {
                val cv = ContentValues().apply {
                    put(COL_PAY_ID, p.id)
                    put(COL_PAY_PERSON_NAME, p.personName)
                    put(COL_PAY_PHONE, p.phoneNumber)
                    put(COL_PAY_AMOUNT, p.amount)
                    put(COL_PAY_AMOUNT_PAID, p.amountPaid)
                    if (p.dueDateMillis != null) put(COL_PAY_DUE_DATE, p.dueDateMillis) else putNull(COL_PAY_DUE_DATE)
                    put(COL_PAY_CREATED_DATE, p.createdDateMillis)
                    put(COL_PAY_NOTES, p.notes)
                    put(COL_PAY_STATUS, p.status.name)
                }
                db.insert(TABLE_PAYABLES, null, cv)
            }

            // Clear & Restore Savings Goals
            db.delete(TABLE_SAVINGS_GOALS, null, null)
            for (g in payload.savingsGoals) {
                val cv = ContentValues().apply {
                    put(COL_GOAL_ID, g.id)
                    put(COL_GOAL_TITLE, g.title)
                    put(COL_GOAL_TARGET_AMOUNT, g.targetAmount)
                    put(COL_GOAL_SAVED_AMOUNT, g.savedAmount)
                    if (g.targetDateMillis != null) put(COL_GOAL_TARGET_DATE, g.targetDateMillis) else putNull(COL_GOAL_TARGET_DATE)
                    put(COL_GOAL_CREATED_DATE, g.createdDateMillis)
                    put(COL_GOAL_ICON, g.icon)
                    put(COL_GOAL_COLOR, g.colorHex)
                    put(COL_GOAL_NOTES, g.notes)
                    put(COL_GOAL_IS_COMPLETED, if (g.isCompleted) 1 else 0)
                }
                db.insert(TABLE_SAVINGS_GOALS, null, cv)
            }

            recalculateAllAccountBalances(db)

            db.setTransactionSuccessful()
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        } finally {
            db.endTransaction()
        }
    }

    // --- Recurring Bills CRUD ---
    fun insertRecurringBill(bill: RecurringBill): Long {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(COL_BILL_TITLE, bill.title)
            put(COL_BILL_AMOUNT, bill.amount)
            put(COL_BILL_CAT_ID, bill.categoryId)
            put(COL_BILL_CAT_NAME, bill.categoryName)
            put(COL_BILL_CAT_ICON, bill.categoryIcon)
            put(COL_BILL_CAT_COLOR, bill.categoryColor)
            if (bill.accountId != null) put(COL_BILL_ACCOUNT_ID, bill.accountId) else putNull(COL_BILL_ACCOUNT_ID)
            if (bill.accountName != null) put(COL_BILL_ACCOUNT_NAME, bill.accountName) else putNull(COL_BILL_ACCOUNT_NAME)
            put(COL_BILL_FREQUENCY, bill.frequency.name)
            put(COL_BILL_DUE_DAY, bill.dueDay)
            put(COL_BILL_NEXT_DUE, bill.nextDueDateMillis)
            if (bill.lastPaidDateMillis != null) put(COL_BILL_LAST_PAID, bill.lastPaidDateMillis) else putNull(COL_BILL_LAST_PAID)
            put(COL_BILL_IS_ACTIVE, if (bill.isActive) 1 else 0)
            put(COL_BILL_NOTES, bill.notes)
        }
        return db.insert(TABLE_RECURRING_BILLS, null, cv)
    }

    fun updateRecurringBill(bill: RecurringBill): Int {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(COL_BILL_TITLE, bill.title)
            put(COL_BILL_AMOUNT, bill.amount)
            put(COL_BILL_CAT_ID, bill.categoryId)
            put(COL_BILL_CAT_NAME, bill.categoryName)
            put(COL_BILL_CAT_ICON, bill.categoryIcon)
            put(COL_BILL_CAT_COLOR, bill.categoryColor)
            if (bill.accountId != null) put(COL_BILL_ACCOUNT_ID, bill.accountId) else putNull(COL_BILL_ACCOUNT_ID)
            if (bill.accountName != null) put(COL_BILL_ACCOUNT_NAME, bill.accountName) else putNull(COL_BILL_ACCOUNT_NAME)
            put(COL_BILL_FREQUENCY, bill.frequency.name)
            put(COL_BILL_DUE_DAY, bill.dueDay)
            put(COL_BILL_NEXT_DUE, bill.nextDueDateMillis)
            if (bill.lastPaidDateMillis != null) put(COL_BILL_LAST_PAID, bill.lastPaidDateMillis) else putNull(COL_BILL_LAST_PAID)
            put(COL_BILL_IS_ACTIVE, if (bill.isActive) 1 else 0)
            put(COL_BILL_NOTES, bill.notes)
        }
        return db.update(TABLE_RECURRING_BILLS, cv, "$COL_BILL_ID = ?", arrayOf(bill.id.toString()))
    }

    fun deleteRecurringBill(id: Long): Int {
        val db = writableDatabase
        return db.delete(TABLE_RECURRING_BILLS, "$COL_BILL_ID = ?", arrayOf(id.toString()))
    }

    fun getAllRecurringBills(): List<RecurringBill> {
        val db = readableDatabase
        val list = mutableListOf<RecurringBill>()
        val cursor = db.query(TABLE_RECURRING_BILLS, null, null, null, null, null, "$COL_BILL_NEXT_DUE ASC")
        cursor.use { c ->
            while (c.moveToNext()) {
                val accIdCol = c.getColumnIndex(COL_BILL_ACCOUNT_ID)
                val accNameCol = c.getColumnIndex(COL_BILL_ACCOUNT_NAME)
                val lastPaidCol = c.getColumnIndex(COL_BILL_LAST_PAID)

                val freqStr = c.getString(c.getColumnIndexOrThrow(COL_BILL_FREQUENCY))
                val frequency = try {
                    BillFrequency.valueOf(freqStr)
                } catch (_: Exception) {
                    BillFrequency.MONTHLY
                }

                list.add(
                    RecurringBill(
                        id = c.getLong(c.getColumnIndexOrThrow(COL_BILL_ID)),
                        title = c.getString(c.getColumnIndexOrThrow(COL_BILL_TITLE)),
                        amount = c.getDouble(c.getColumnIndexOrThrow(COL_BILL_AMOUNT)),
                        categoryId = c.getLong(c.getColumnIndexOrThrow(COL_BILL_CAT_ID)),
                        categoryName = c.getString(c.getColumnIndexOrThrow(COL_BILL_CAT_NAME)),
                        categoryIcon = c.getString(c.getColumnIndexOrThrow(COL_BILL_CAT_ICON)),
                        categoryColor = c.getString(c.getColumnIndexOrThrow(COL_BILL_CAT_COLOR)),
                        accountId = if (accIdCol != -1 && !c.isNull(accIdCol)) c.getLong(accIdCol) else null,
                        accountName = if (accNameCol != -1 && !c.isNull(accNameCol)) c.getString(accNameCol) else null,
                        frequency = frequency,
                        dueDay = c.getInt(c.getColumnIndexOrThrow(COL_BILL_DUE_DAY)),
                        nextDueDateMillis = c.getLong(c.getColumnIndexOrThrow(COL_BILL_NEXT_DUE)),
                        lastPaidDateMillis = if (lastPaidCol != -1 && !c.isNull(lastPaidCol)) c.getLong(lastPaidCol) else null,
                        isActive = c.getInt(c.getColumnIndexOrThrow(COL_BILL_IS_ACTIVE)) == 1,
                        notes = c.getString(c.getColumnIndexOrThrow(COL_BILL_NOTES))
                    )
                )
            }
        }
        return list
    }

    fun markBillAsPaid(billId: Long, paymentAccountId: Long?, paymentAccountName: String?): Long {
        val db = writableDatabase
        db.beginTransaction()
        try {
            var bill: RecurringBill? = null
            val cursor = db.query(TABLE_RECURRING_BILLS, null, "$COL_BILL_ID = ?", arrayOf(billId.toString()), null, null, null)
            cursor.use { c ->
                if (c.moveToFirst()) {
                    val accIdCol = c.getColumnIndex(COL_BILL_ACCOUNT_ID)
                    val accNameCol = c.getColumnIndex(COL_BILL_ACCOUNT_NAME)
                    val lastPaidCol = c.getColumnIndex(COL_BILL_LAST_PAID)
                    val freqStr = c.getString(c.getColumnIndexOrThrow(COL_BILL_FREQUENCY))
                    val frequency = try { BillFrequency.valueOf(freqStr) } catch (_: Exception) { BillFrequency.MONTHLY }
                    bill = RecurringBill(
                        id = c.getLong(c.getColumnIndexOrThrow(COL_BILL_ID)),
                        title = c.getString(c.getColumnIndexOrThrow(COL_BILL_TITLE)),
                        amount = c.getDouble(c.getColumnIndexOrThrow(COL_BILL_AMOUNT)),
                        categoryId = c.getLong(c.getColumnIndexOrThrow(COL_BILL_CAT_ID)),
                        categoryName = c.getString(c.getColumnIndexOrThrow(COL_BILL_CAT_NAME)),
                        categoryIcon = c.getString(c.getColumnIndexOrThrow(COL_BILL_CAT_ICON)),
                        categoryColor = c.getString(c.getColumnIndexOrThrow(COL_BILL_CAT_COLOR)),
                        accountId = if (accIdCol != -1 && !c.isNull(accIdCol)) c.getLong(accIdCol) else null,
                        accountName = if (accNameCol != -1 && !c.isNull(accNameCol)) c.getString(accNameCol) else null,
                        frequency = frequency,
                        dueDay = c.getInt(c.getColumnIndexOrThrow(COL_BILL_DUE_DAY)),
                        nextDueDateMillis = c.getLong(c.getColumnIndexOrThrow(COL_BILL_NEXT_DUE)),
                        lastPaidDateMillis = if (lastPaidCol != -1 && !c.isNull(lastPaidCol)) c.getLong(lastPaidCol) else null,
                        isActive = c.getInt(c.getColumnIndexOrThrow(COL_BILL_IS_ACTIVE)) == 1,
                        notes = c.getString(c.getColumnIndexOrThrow(COL_BILL_NOTES))
                    )
                }
            }

            val b = bill ?: return -1L

            val (effectiveAccountId, effectiveAccountName) = resolveAccountForTransaction(db, paymentAccountId ?: b.accountId, null)
            val acc = getAccountById(effectiveAccountId)
            val payMethod = when (acc?.type) {
                AccountType.BANK -> PaymentMethod.BANK_TRANSFER
                AccountType.MFS -> if (acc.institutionName.contains("bkash", true)) PaymentMethod.BKASH else PaymentMethod.NAGAD
                else -> PaymentMethod.CASH
            }

            // 1. Log an Expense Transaction
            val txCv = ContentValues().apply {
                put(COL_TX_TITLE, "Bill: ${b.title}")
                put(COL_TX_AMOUNT, b.amount)
                put(COL_TX_TYPE, TransactionType.EXPENSE.name)
                put(COL_TX_CAT_ID, b.categoryId)
                put(COL_TX_CAT_NAME, b.categoryName)
                put(COL_TX_CAT_ICON, b.categoryIcon)
                put(COL_TX_CAT_COLOR, b.categoryColor)
                put(COL_TX_DATE, System.currentTimeMillis())
                put(COL_TX_PAYMENT_METHOD, payMethod.name)
                put(COL_TX_NOTES, "Paid via Recurring Bills tracker")
                put(COL_TX_ACCOUNT_ID, effectiveAccountId)
                put(COL_TX_ACCOUNT_NAME, effectiveAccountName)
            }
            val txId = db.insert(TABLE_TRANSACTIONS, null, txCv)

            // 2. Advance next due date by 1 month/frequency while strictly preserving dueDay
            val cal = Calendar.getInstance()
            val baseTime = if (b.nextDueDateMillis > 0) b.nextDueDateMillis else System.currentTimeMillis()
            cal.timeInMillis = baseTime

            when (b.frequency) {
                BillFrequency.WEEKLY -> cal.add(Calendar.WEEK_OF_YEAR, 1)
                BillFrequency.YEARLY -> {
                    cal.add(Calendar.YEAR, 1)
                    val maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
                    cal.set(Calendar.DAY_OF_MONTH, b.dueDay.coerceIn(1, maxDay))
                }
                BillFrequency.MONTHLY -> {
                    cal.add(Calendar.MONTH, 1)
                    val maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
                    cal.set(Calendar.DAY_OF_MONTH, b.dueDay.coerceIn(1, maxDay))
                }
            }
            cal.set(Calendar.HOUR_OF_DAY, 9)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)

            val now = System.currentTimeMillis()
            while (cal.timeInMillis <= now && b.frequency == BillFrequency.MONTHLY) {
                cal.add(Calendar.MONTH, 1)
                val maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
                cal.set(Calendar.DAY_OF_MONTH, b.dueDay.coerceIn(1, maxDay))
            }

            val billCv = ContentValues().apply {
                put(COL_BILL_LAST_PAID, System.currentTimeMillis())
                put(COL_BILL_NEXT_DUE, cal.timeInMillis)
            }
            db.update(TABLE_RECURRING_BILLS, billCv, "$COL_BILL_ID = ?", arrayOf(billId.toString()))

            // 3. Recalculate account balance
            recalculateAccountBalance(db, effectiveAccountId)

            db.setTransactionSuccessful()
            return txId
        } finally {
            db.endTransaction()
        }
    }

    fun executeBillSplit(splitData: BillSplitData): Boolean {
        val db = writableDatabase
        db.beginTransaction()
        try {
            val now = System.currentTimeMillis()
            val (payingAccId, payingAccName) = resolveAccountForTransaction(db, splitData.payingAccountId, PaymentMethod.CASH)
            val payingAcc = getAccountById(payingAccId)
            val payMethod = when (payingAcc?.type) {
                AccountType.BANK -> PaymentMethod.BANK_TRANSFER
                AccountType.MFS -> if (payingAcc.institutionName.contains("bkash", true)) PaymentMethod.BKASH else PaymentMethod.NAGAD
                else -> PaymentMethod.CASH
            }

            // 1. Insert Expense Transaction for user's personal share
            val txCv = ContentValues().apply {
                put(COL_TX_TITLE, if (splitData.note.isNotBlank()) splitData.note else "Split Bill (My Share)")
                put(COL_TX_AMOUNT, splitData.myShare)
                put(COL_TX_TYPE, TransactionType.EXPENSE.name)
                put(COL_TX_CAT_ID, splitData.categoryId)
                put(COL_TX_CAT_NAME, splitData.categoryName)
                put(COL_TX_CAT_ICON, "Restaurant")
                put(COL_TX_CAT_COLOR, "#FF6B6B")
                put(COL_TX_DATE, now)
                put(COL_TX_PAYMENT_METHOD, payMethod.name)
                put(COL_TX_NOTES, "Bill Split: Total ৳${splitData.totalAmount}, My share ৳${splitData.myShare}")
                put(COL_TX_ACCOUNT_ID, payingAccId)
                put(COL_TX_ACCOUNT_NAME, payingAccName)
            }
            db.insert(TABLE_TRANSACTIONS, null, txCv)

            // 2. Insert Receivables for each friend and an expense for the lent amount if paying from account
            val friendLoanCat = getOrCreateCategoryByName("Loan & Debt Given", TransactionType.EXPENSE, "Handshake", "#FF758F")
            for (friend in splitData.friends) {
                if (friend.name.isNotBlank() && friend.amount > 0.0) {
                    val recCv = ContentValues().apply {
                        put(COL_REC_PERSON_NAME, friend.name.trim())
                        put(COL_REC_PHONE, friend.phoneNumber.trim())
                        put(COL_REC_AMOUNT, friend.amount)
                        put(COL_REC_AMOUNT_RECEIVED, 0.0)
                        put(COL_REC_CREATED_DATE, now)
                        put(COL_REC_NOTES, "Bill Split share: ${splitData.note.ifBlank { "Group Bill" }}")
                        put(COL_REC_STATUS, ReceivableStatus.PENDING.name)
                    }
                    db.insert(TABLE_RECEIVABLES, null, recCv)

                    // Also log the lent amount deducted from account under Loan & Debt Given
                    val lentCv = ContentValues().apply {
                        put(COL_TX_TITLE, "Bill Split Lent to ${friend.name.trim()}")
                        put(COL_TX_AMOUNT, friend.amount)
                        put(COL_TX_TYPE, TransactionType.EXPENSE.name)
                        put(COL_TX_CAT_ID, friendLoanCat.id)
                        put(COL_TX_CAT_NAME, friendLoanCat.name)
                        put(COL_TX_CAT_ICON, friendLoanCat.icon)
                        put(COL_TX_CAT_COLOR, friendLoanCat.colorHex)
                        put(COL_TX_DATE, now)
                        put(COL_TX_PAYMENT_METHOD, payMethod.name)
                        put(COL_TX_NOTES, "Lent via Bill Split")
                        put(COL_TX_ACCOUNT_ID, payingAccId)
                        put(COL_TX_ACCOUNT_NAME, payingAccName)
                    }
                    db.insert(TABLE_TRANSACTIONS, null, lentCv)
                }
            }

            // 3. Recalculate paying account balance
            recalculateAccountBalance(db, payingAccId)

            db.setTransactionSuccessful()
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        } finally {
            db.endTransaction()
        }
    }

    fun updateProfileLanguage(languageCode: String): Int {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(COL_PROFILE_LANGUAGE, languageCode)
        }
        return db.update(TABLE_PROFILE, cv, "$COL_PROFILE_ID = 1", null)
    }

    fun updateGeminiApiKey(key: String?): Int {
        val cleanKey = key?.trim()?.ifBlank { null }
        // 1. Immediately persist synchronously to SharedPreferences with commit()
        persistApiKey(cleanKey)

        // 2. Persist to SQLite
        val db = writableDatabase
        val cv = ContentValues().apply {
            if (cleanKey != null) put(COL_PROFILE_GEMINI_KEY, cleanKey) else putNull(COL_PROFILE_GEMINI_KEY)
        }
        var rows = db.update(TABLE_PROFILE, cv, "$COL_PROFILE_ID = 1", null)
        if (rows == 0) {
            val insertCv = ContentValues().apply {
                put(COL_PROFILE_ID, 1)
                put(COL_PROFILE_NAME, "Sabbir")
                put(COL_PROFILE_FIRST_NAME, "Sabbir")
                put(COL_PROFILE_LAST_NAME, "")
                put(COL_PROFILE_CURRENCY_CODE, "BDT")
                put(COL_PROFILE_CURRENCY_SYMBOL, "৳")
                put(COL_PROFILE_AVATAR, 0)
                put(COL_PROFILE_AUTO_SYNC, 1)
                put(COL_PROFILE_LAST_SYNC, 0L)
                put(COL_PROFILE_LANGUAGE, "bn")
                if (cleanKey != null) put(COL_PROFILE_GEMINI_KEY, cleanKey) else putNull(COL_PROFILE_GEMINI_KEY)
                put(COL_PROFILE_AI_INSIGHTS_ENABLED, 1)
            }
            val insertId = db.insertWithOnConflict(TABLE_PROFILE, null, insertCv, SQLiteDatabase.CONFLICT_REPLACE)
            if (insertId != -1L) rows = 1
        }
        try {
            val cur = db.rawQuery("PRAGMA wal_checkpoint(FULL)", null)
            cur.close()
        } catch (_: Exception) {}
        return rows
    }

    fun updateAiInsightsEnabled(enabled: Boolean): Int {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(COL_PROFILE_AI_INSIGHTS_ENABLED, if (enabled) 1 else 0)
        }
        return db.update(TABLE_PROFILE, cv, "$COL_PROFILE_ID = 1", null)
    }

    fun updateProfileTheme(theme: String): Int {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(COL_PROFILE_THEME, theme)
        }
        return db.update(TABLE_PROFILE, cv, "$COL_PROFILE_ID = 1", null)
    }

    fun updateProfileBengaliNumerals(enabled: Boolean): Int {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(COL_PROFILE_BENGALI_NUMERALS, if (enabled) 1 else 0)
        }
        return db.update(TABLE_PROFILE, cv, "$COL_PROFILE_ID = 1", null)
    }

    // ==========================================
    // MARKET LISTS & ITEMS OPERATIONS
    // ==========================================

    fun createMarketList(title: String, createdAt: Long = System.currentTimeMillis()): Long {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(COL_ML_TITLE, title.ifBlank { "বাজারের ফর্দ" })
            put(COL_ML_CREATED_AT, createdAt)
            put(COL_ML_IS_COMPLETED, 0)
        }
        return db.insert(TABLE_MARKET_LISTS, null, cv)
    }

    fun getActiveMarketList(): MarketListWithItems? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_MARKET_LISTS,
            null,
            "$COL_ML_IS_COMPLETED = 0",
            null,
            null,
            null,
            "$COL_ML_CREATED_AT DESC",
            "1"
        )
        var marketList: MarketList? = null
        cursor.use {
            if (it.moveToFirst()) {
                marketList = MarketList(
                    id = it.getLong(it.getColumnIndexOrThrow(COL_ML_ID)),
                    title = it.getString(it.getColumnIndexOrThrow(COL_ML_TITLE)),
                    createdAt = it.getLong(it.getColumnIndexOrThrow(COL_ML_CREATED_AT)),
                    isCompleted = it.getInt(it.getColumnIndexOrThrow(COL_ML_IS_COMPLETED)) == 1
                )
            }
        }
        if (marketList == null) return null

        val items = getMarketItemsForList(marketList!!.id)
        return MarketListWithItems(list = marketList!!, items = items)
    }

    fun getAllMarketLists(): List<MarketListWithItems> {
        val db = readableDatabase
        val result = mutableListOf<MarketListWithItems>()
        val cursor = db.query(
            TABLE_MARKET_LISTS,
            null,
            null,
            null,
            null,
            null,
            "$COL_ML_CREATED_AT DESC"
        )
        val lists = mutableListOf<MarketList>()
        cursor.use {
            while (it.moveToNext()) {
                lists.add(
                    MarketList(
                        id = it.getLong(it.getColumnIndexOrThrow(COL_ML_ID)),
                        title = it.getString(it.getColumnIndexOrThrow(COL_ML_TITLE)),
                        createdAt = it.getLong(it.getColumnIndexOrThrow(COL_ML_CREATED_AT)),
                        isCompleted = it.getInt(it.getColumnIndexOrThrow(COL_ML_IS_COMPLETED)) == 1
                    )
                )
            }
        }
        for (list in lists) {
            val items = getMarketItemsForList(list.id)
            result.add(MarketListWithItems(list, items))
        }
        return result
    }

    fun getMarketItemsForList(listId: Long): List<MarketItem> {
        val db = readableDatabase
        val items = mutableListOf<MarketItem>()
        val cursor = db.query(
            TABLE_MARKET_ITEMS,
            null,
            "$COL_MI_LIST_ID = ?",
            arrayOf(listId.toString()),
            null,
            null,
            "$COL_MI_IS_PURCHASED ASC, $COL_MI_ID ASC"
        )
        cursor.use {
            while (it.moveToNext()) {
                val amountIndex = it.getColumnIndexOrThrow(COL_MI_AMOUNT)
                val txIdIndex = it.getColumnIndexOrThrow(COL_MI_TRANSACTION_ID)
                items.add(
                    MarketItem(
                        id = it.getLong(it.getColumnIndexOrThrow(COL_MI_ID)),
                        listId = it.getLong(it.getColumnIndexOrThrow(COL_MI_LIST_ID)),
                        name = it.getString(it.getColumnIndexOrThrow(COL_MI_NAME)),
                        quantity = it.getDouble(it.getColumnIndexOrThrow(COL_MI_QUANTITY)),
                        unit = it.getString(it.getColumnIndexOrThrow(COL_MI_UNIT)),
                        amount = if (!it.isNull(amountIndex)) it.getDouble(amountIndex) else null,
                        isPurchased = it.getInt(it.getColumnIndexOrThrow(COL_MI_IS_PURCHASED)) == 1,
                        transactionId = if (!it.isNull(txIdIndex)) it.getLong(txIdIndex) else null,
                        createdAt = it.getLong(it.getColumnIndexOrThrow(COL_MI_CREATED_AT))
                    )
                )
            }
        }
        return items
    }

    fun addMarketItem(item: MarketItem): Long {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(COL_MI_LIST_ID, item.listId)
            put(COL_MI_NAME, item.name.trim())
            put(COL_MI_QUANTITY, item.quantity)
            put(COL_MI_UNIT, item.unit)
            if (item.amount != null) put(COL_MI_AMOUNT, item.amount) else putNull(COL_MI_AMOUNT)
            put(COL_MI_IS_PURCHASED, if (item.isPurchased) 1 else 0)
            if (item.transactionId != null) put(COL_MI_TRANSACTION_ID, item.transactionId) else putNull(COL_MI_TRANSACTION_ID)
            put(COL_MI_CREATED_AT, item.createdAt)
        }
        return db.insert(TABLE_MARKET_ITEMS, null, cv)
    }

    fun addMarketItems(listId: Long, items: List<MarketItem>) {
        val db = writableDatabase
        db.beginTransaction()
        try {
            for (item in items) {
                val cv = ContentValues().apply {
                    put(COL_MI_LIST_ID, listId)
                    put(COL_MI_NAME, item.name.trim())
                    put(COL_MI_QUANTITY, item.quantity)
                    put(COL_MI_UNIT, item.unit)
                    if (item.amount != null) put(COL_MI_AMOUNT, item.amount) else putNull(COL_MI_AMOUNT)
                    put(COL_MI_IS_PURCHASED, if (item.isPurchased) 1 else 0)
                    if (item.transactionId != null) put(COL_MI_TRANSACTION_ID, item.transactionId) else putNull(COL_MI_TRANSACTION_ID)
                    put(COL_MI_CREATED_AT, item.createdAt)
                }
                db.insert(TABLE_MARKET_ITEMS, null, cv)
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun updateMarketItem(item: MarketItem): Boolean {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(COL_MI_NAME, item.name.trim())
            put(COL_MI_QUANTITY, item.quantity)
            put(COL_MI_UNIT, item.unit)
            if (item.amount != null) put(COL_MI_AMOUNT, item.amount) else putNull(COL_MI_AMOUNT)
            put(COL_MI_IS_PURCHASED, if (item.isPurchased) 1 else 0)
            if (item.transactionId != null) put(COL_MI_TRANSACTION_ID, item.transactionId) else putNull(COL_MI_TRANSACTION_ID)
        }
        return db.update(TABLE_MARKET_ITEMS, cv, "$COL_MI_ID = ?", arrayOf(item.id.toString())) > 0
    }

    fun deleteMarketItem(itemId: Long): Boolean {
        val db = writableDatabase
        return db.delete(TABLE_MARKET_ITEMS, "$COL_MI_ID = ?", arrayOf(itemId.toString())) > 0
    }

    fun updateMarketItemPurchased(itemId: Long, isPurchased: Boolean, amount: Double?, transactionId: Long?): Boolean {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(COL_MI_IS_PURCHASED, if (isPurchased) 1 else 0)
            if (amount != null) put(COL_MI_AMOUNT, amount) else putNull(COL_MI_AMOUNT)
            if (transactionId != null) put(COL_MI_TRANSACTION_ID, transactionId) else putNull(COL_MI_TRANSACTION_ID)
        }
        return db.update(TABLE_MARKET_ITEMS, cv, "$COL_MI_ID = ?", arrayOf(itemId.toString())) > 0
    }

    fun completeMarketList(listId: Long): Boolean {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(COL_ML_IS_COMPLETED, 1)
        }
        return db.update(TABLE_MARKET_LISTS, cv, "$COL_ML_ID = ?", arrayOf(listId.toString())) > 0
    }

    fun deleteMarketList(listId: Long): Boolean {
        val db = writableDatabase
        db.delete(TABLE_MARKET_ITEMS, "$COL_MI_LIST_ID = ?", arrayOf(listId.toString()))
        return db.delete(TABLE_MARKET_LISTS, "$COL_ML_ID = ?", arrayOf(listId.toString())) > 0
    }
}
