package com.example.expensetracker.data.local

import android.database.sqlite.SQLiteDatabase

internal object DatabaseSchema {

    fun createAllTablesAndIndexes(db: SQLiteDatabase) {
        createCategories(db)
        createTransactions(db)
        createBudgets(db)
        createProfile(db)
        createAccounts(db)
        createReceivables(db)
        createRecurringBills(db)
        createPayables(db)
        createSavingsGoals(db)
        createMarketListsAndItems(db)
        createProductPriceHistory(db)
        createMasterProducts(db)
    }

    private fun createCategories(db: SQLiteDatabase) {
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
    }

    private fun createTransactions(db: SQLiteDatabase) {
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
    }

    private fun createBudgets(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE $TABLE_BUDGETS (
                $COL_BUDGET_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_BUDGET_CAT_ID INTEGER,
                $COL_BUDGET_CAT_NAME TEXT NOT NULL,
                $COL_BUDGET_AMOUNT REAL NOT NULL,
                $COL_BUDGET_MONTH INTEGER NOT NULL,
                $COL_BUDGET_YEAR INTEGER NOT NULL,
                UNIQUE($COL_BUDGET_CAT_ID, $COL_BUDGET_MONTH, $COL_BUDGET_YEAR) ON CONFLICT REPLACE
            )
        """.trimIndent())
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_budgets_month_year ON $TABLE_BUDGETS ($COL_BUDGET_MONTH, $COL_BUDGET_YEAR)")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_budgets_cat_month_year ON $TABLE_BUDGETS ($COL_BUDGET_CAT_ID, $COL_BUDGET_MONTH, $COL_BUDGET_YEAR)")
    }

    private fun createProfile(db: SQLiteDatabase) {
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
                $COL_PROFILE_THEME TEXT NOT NULL DEFAULT 'dark',
                $COL_PROFILE_BENGALI_NUMERALS INTEGER NOT NULL DEFAULT 1
            )
        """.trimIndent())
    }

    private fun createAccounts(db: SQLiteDatabase) {
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
    }

    private fun createReceivables(db: SQLiteDatabase) {
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
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_rec_due_date ON $TABLE_RECEIVABLES ($COL_REC_DUE_DATE)")
    }

    private fun createRecurringBills(db: SQLiteDatabase) {
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
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_bills_due_day ON $TABLE_RECURRING_BILLS ($COL_BILL_DUE_DAY)")
    }

    private fun createPayables(db: SQLiteDatabase) {
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
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_pay_due_date ON $TABLE_PAYABLES ($COL_PAY_DUE_DATE)")
    }

    private fun createSavingsGoals(db: SQLiteDatabase) {
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
    }

    private fun createMarketListsAndItems(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE $TABLE_MARKET_LISTS (
                $COL_ML_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_ML_TITLE TEXT NOT NULL,
                $COL_ML_CREATED_AT INTEGER NOT NULL,
                $COL_ML_IS_COMPLETED INTEGER NOT NULL DEFAULT 0
            )
        """.trimIndent())
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
    }

    private fun createProductPriceHistory(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE $TABLE_PRODUCT_PRICE_HISTORY (
                $COL_PPH_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_PPH_PRODUCT_NAME TEXT NOT NULL,
                $COL_PPH_RAW_QUANTITY REAL NOT NULL,
                $COL_PPH_RAW_UNIT TEXT NOT NULL,
                $COL_PPH_BASE_QUANTITY REAL NOT NULL,
                $COL_PPH_BASE_UNIT TEXT NOT NULL,
                $COL_PPH_TOTAL_AMOUNT REAL NOT NULL,
                $COL_PPH_UNIT_PRICE REAL NOT NULL,
                $COL_PPH_MARKET_ITEM_ID INTEGER,
                $COL_PPH_TRANSACTION_ID INTEGER,
                $COL_PPH_TIMESTAMP INTEGER NOT NULL
            )
        """.trimIndent())
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_pph_name_time ON $TABLE_PRODUCT_PRICE_HISTORY ($COL_PPH_PRODUCT_NAME COLLATE NOCASE, $COL_PPH_TIMESTAMP DESC)")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_pph_time ON $TABLE_PRODUCT_PRICE_HISTORY ($COL_PPH_TIMESTAMP DESC)")
    }

    private fun createMasterProducts(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE $TABLE_MASTER_PRODUCTS (
                $COL_MP_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_MP_NAME TEXT NOT NULL,
                $COL_MP_NORMALIZED_NAME TEXT NOT NULL UNIQUE COLLATE NOCASE,
                $COL_MP_CATEGORY TEXT NOT NULL,
                $COL_MP_DEFAULT_UNIT TEXT NOT NULL,
                $COL_MP_LATEST_UNIT_PRICE REAL NOT NULL DEFAULT 0.0,
                $COL_MP_PREVIOUS_UNIT_PRICE REAL,
                $COL_MP_MIN_UNIT_PRICE REAL NOT NULL DEFAULT 0.0,
                $COL_MP_MAX_UNIT_PRICE REAL NOT NULL DEFAULT 0.0,
                $COL_MP_AVG_UNIT_PRICE REAL NOT NULL DEFAULT 0.0,
                $COL_MP_PURCHASE_COUNT INTEGER NOT NULL DEFAULT 1,
                $COL_MP_IS_STAPLE INTEGER NOT NULL DEFAULT 0,
                $COL_MP_FIRST_PURCHASED_AT INTEGER NOT NULL,
                $COL_MP_LAST_PURCHASED_AT INTEGER NOT NULL
            )
        """.trimIndent())
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_mp_norm_name ON $TABLE_MASTER_PRODUCTS ($COL_MP_NORMALIZED_NAME)")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_mp_staple_count ON $TABLE_MASTER_PRODUCTS ($COL_MP_IS_STAPLE, $COL_MP_PURCHASE_COUNT DESC)")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_mp_last_purchased ON $TABLE_MASTER_PRODUCTS ($COL_MP_LAST_PURCHASED_AT DESC)")
    }
}
