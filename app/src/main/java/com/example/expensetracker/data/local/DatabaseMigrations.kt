package com.example.expensetracker.data.local

import android.database.sqlite.SQLiteDatabase

internal object DatabaseMigrations {

    fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int, dbHelper: ExpenseDatabaseHelper) {
        DatabaseEarlyMigrations.applyEarlyMigrations(db, oldVersion, dbHelper)
        if (oldVersion < 10) upgradeToVersion10(db)
        if (oldVersion < 11) upgradeToVersion11(db)
        if (oldVersion < 12) upgradeToVersion12(db, dbHelper)
        if (oldVersion < 13) upgradeToVersion13(db)
        if (oldVersion < 14) upgradeToVersion14(db)
        if (oldVersion < 15) upgradeToVersion15(db, dbHelper)
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

    private fun upgradeToVersion13(db: SQLiteDatabase) {
        try {
            db.execSQL("CREATE INDEX IF NOT EXISTS idx_budgets_month_year ON $TABLE_BUDGETS ($COL_BUDGET_MONTH, $COL_BUDGET_YEAR)")
            db.execSQL("CREATE INDEX IF NOT EXISTS idx_budgets_cat_month_year ON $TABLE_BUDGETS ($COL_BUDGET_CAT_ID, $COL_BUDGET_MONTH, $COL_BUDGET_YEAR)")
            db.execSQL("CREATE INDEX IF NOT EXISTS idx_rec_due_date ON $TABLE_RECEIVABLES ($COL_REC_DUE_DATE)")
            db.execSQL("CREATE INDEX IF NOT EXISTS idx_pay_due_date ON $TABLE_PAYABLES ($COL_PAY_DUE_DATE)")
            db.execSQL("CREATE INDEX IF NOT EXISTS idx_bills_due_day ON $TABLE_RECURRING_BILLS ($COL_BILL_DUE_DAY)")
        } catch (_: Exception) {}
    }

    private fun upgradeToVersion14(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS $TABLE_PRODUCT_PRICE_HISTORY (
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
        try {
            db.execSQL("CREATE INDEX IF NOT EXISTS idx_pph_name_time ON $TABLE_PRODUCT_PRICE_HISTORY ($COL_PPH_PRODUCT_NAME COLLATE NOCASE, $COL_PPH_TIMESTAMP DESC)")
            db.execSQL("CREATE INDEX IF NOT EXISTS idx_pph_time ON $TABLE_PRODUCT_PRICE_HISTORY ($COL_PPH_TIMESTAMP DESC)")
        } catch (_: Exception) {}
    }

    private fun upgradeToVersion15(db: SQLiteDatabase, dbHelper: ExpenseDatabaseHelper) {
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS $TABLE_MASTER_PRODUCTS (
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
        try {
            db.execSQL("CREATE INDEX IF NOT EXISTS idx_mp_norm_name ON $TABLE_MASTER_PRODUCTS ($COL_MP_NORMALIZED_NAME)")
            db.execSQL("CREATE INDEX IF NOT EXISTS idx_mp_staple_count ON $TABLE_MASTER_PRODUCTS ($COL_MP_IS_STAPLE, $COL_MP_PURCHASE_COUNT DESC)")
            db.execSQL("CREATE INDEX IF NOT EXISTS idx_mp_last_purchased ON $TABLE_MASTER_PRODUCTS ($COL_MP_LAST_PURCHASED_AT DESC)")
        } catch (_: Exception) {}

        dbHelper.masterProductHelper.backfillFromPriceHistory(db)
        dbHelper.masterProductHelper.seedDefaultStaplesIfEmpty(db)
    }
}
