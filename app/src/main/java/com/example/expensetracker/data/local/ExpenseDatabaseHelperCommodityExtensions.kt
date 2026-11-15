package com.example.expensetracker.data.local

import android.database.sqlite.SQLiteDatabase
import com.example.expensetracker.data.model.MasterProduct

// --- Product Price Intelligence & Commodity Extensions ---
fun ExpenseDatabaseHelper.renameCommodity(oldName: String, newName: String): Int =
    productPriceHelper.renameCommodity(oldName, newName)

fun ExpenseDatabaseHelper.mergeCommodities(sourceName: String, targetName: String): Int =
    productPriceHelper.mergeCommodities(sourceName, targetName)

fun ExpenseDatabaseHelper.backfillProductPriceHistory(db: SQLiteDatabase) =
    productPriceHelper.backfillProductPriceHistory(db)

// --- Master Product Additional Extensions ---
fun ExpenseDatabaseHelper.getMasterProductByName(name: String): MasterProduct? =
    masterProductHelper.getMasterProductByName(name)

fun ExpenseDatabaseHelper.searchMasterProducts(query: String, limit: Int = 20): List<MasterProduct> =
    masterProductHelper.searchMasterProducts(query, limit)

fun ExpenseDatabaseHelper.recordOrUpdateMasterProduct(
    productName: String, rawQuantity: Double, rawUnit: String, totalAmount: Double, timestamp: Long = System.currentTimeMillis()
): Pair<MasterProduct, Boolean>? = masterProductHelper.recordOrUpdateProduct(productName, rawQuantity, rawUnit, totalAmount, timestamp)
