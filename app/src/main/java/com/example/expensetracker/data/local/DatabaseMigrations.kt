package com.example.expensetracker.data.local

import android.database.sqlite.SQLiteDatabase

internal object DatabaseMigrations {

    fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int, dbHelper: ExpenseDatabaseHelper) {
        if (oldVersion < 2) upgradeToVersion2(db)
        if (oldVersion < 3) upgradeToVersion3(db, dbHelper)
        if (oldVersion < 4) upgradeToVersion4(db, dbHelper)
        if (oldVersion < 5) upgradeToVersion5(db)
        if (oldVersion < 6) upgradeToVersion6(db)
        if (oldVersion < 7) upgradeToVersion7(db)
        if (oldVersion < 8) upgradeToVersion8(db)
        if (oldVersion < 9) upgradeToVersion9(db)
        if (oldVersion < 10) upgradeToVersion10(db)
        if (oldVersion < 11) upgradeToVersion11(db)
        if (oldVersion < 12) upgradeToVersion12(db, dbHelper)
    }

    private fun upgradeToVersion2(db: SQLiteDatabase) {
        db.execSQL("ALTER TABLE $TABLE_PROFILE ADD COLUMN $COL_PROFILE_FIRST_NAME TEXT NOT NULL DEFAULT ''")
        db.execSQL("ALTER TABLE $TABLE_PROFILE ADD COLUMN $COL_PROFILE_LAST_NAME TEXT NOT NULL DEFAULT ''")
        db.execSQL("UPDATE $TABLE_PROFILE SET $COL_PROFILE_FIRST_NAME = $COL_PROFILE_NAME WHERE $COL_PROFILE_FIRST_NAME = ''")
    }

    private fun upgradeToVersion3(db: SQLiteDatabase, dbHelper: ExpenseDatabaseHelper) {
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

        dbHelper.seedDefaultAccountsIfEmpty(db)
        dbHelper.backfillTransactionAccounts(db)
        dbHelper.recalculateAllAccountBalances(db)
    }

    private fun upgradeToVersion4(db: SQLiteDatabase, dbHelper: ExpenseDatabaseHelper) {
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

        dbHelper.seedSampleRecurringBillsIfEmpty(db)
    }

    private fun upgradeToVersion5(db: SQLiteDatabase) {
        try {
            db.execSQL("ALTER TABLE $TABLE_PROFILE ADD COLUMN $COL_PROFILE_GEMINI_KEY TEXT")
        } catch (_: Exception) {}
        try {
            db.execSQL("ALTER TABLE $TABLE_PROFILE ADD COLUMN $COL_PROFILE_AI_INSIGHTS_ENABLED INTEGER NOT NULL DEFAULT 1")
        } catch (_: Exception) {}
    }

    private fun upgradeToVersion6(db: SQLiteDatabase) {
        try {
            db.execSQL("CREATE INDEX IF NOT EXISTS idx_tx_acc_type ON $TABLE_TRANSACTIONS ($COL_TX_ACCOUNT_ID, $COL_TX_TYPE)")
            db.execSQL("CREATE INDEX IF NOT EXISTS idx_tx_date_type ON $TABLE_TRANSACTIONS ($COL_TX_DATE, $COL_TX_TYPE)")
            db.execSQL("CREATE INDEX IF NOT EXISTS idx_rec_status_date ON $TABLE_RECEIVABLES ($COL_REC_STATUS, $COL_REC_CREATED_DATE)")
            db.execSQL("CREATE INDEX IF NOT EXISTS idx_bills_due_active ON $TABLE_RECURRING_BILLS ($COL_BILL_IS_ACTIVE, $COL_BILL_NEXT_DUE)")
        } catch (_: Exception) {}
    }

    private fun upgradeToVersion7(db: SQLiteDatabase) {
        try {
            db.execSQL("ALTER TABLE $TABLE_PROFILE ADD COLUMN $COL_PROFILE_THEME TEXT NOT NULL DEFAULT 'dark'")
        } catch (_: Exception) {}
        try {
            db.execSQL("ALTER TABLE $TABLE_PROFILE ADD COLUMN $COL_PROFILE_BENGALI_NUMERALS INTEGER NOT NULL DEFAULT 0")
        } catch (_: Exception) {}
    }

    private fun upgradeToVersion8(db: SQLiteDatabase) {
        try {
            db.execSQL("UPDATE $TABLE_PROFILE SET $COL_PROFILE_BENGALI_NUMERALS = 1 WHERE $COL_PROFILE_LANGUAGE = 'bn'")
        } catch (_: Exception) {}
    }

    private fun upgradeToVersion9(db: SQLiteDatabase) {
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

    private fun upgradeToVersion10(db: SQLiteDatabase) {
        try {
            db.execSQL("ALTER TABLE $TABLE_TRANSACTIONS ADD COLUMN $COL_TX_RECEIPT_PATH TEXT")
        } catch (_: Exception) {}
    }

    private fun upgradeToVersion11(db: SQLiteDatabase) {
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

    private fun upgradeToVersion12(db: SQLiteDatabase, dbHelper: ExpenseDatabaseHelper) {
        dbHelper.reconcileAndDeduplicateCategoryColors(db)
    }
}
