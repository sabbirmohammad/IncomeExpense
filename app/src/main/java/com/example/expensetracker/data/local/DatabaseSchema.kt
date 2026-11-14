package com.example.expensetracker.data.local

import android.database.sqlite.SQLiteDatabase

internal object DatabaseSchema {

    fun createAllTablesAndIndexes(db: SQLiteDatabase) {
        DatabaseCoreSchema.createCoreTables(db)
        createMarketListsAndItems(db)
        createProductPriceHistory(db)
        createMasterProducts(db)
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
