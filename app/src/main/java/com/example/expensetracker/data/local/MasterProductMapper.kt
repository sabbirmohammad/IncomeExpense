package com.example.expensetracker.data.local

import android.database.Cursor
import com.example.expensetracker.data.model.MasterProduct

internal object MasterProductMapper {
    fun cursorToMasterProduct(c: Cursor): MasterProduct {
        val prevRateIdx = c.getColumnIndexOrThrow(COL_MP_PREVIOUS_UNIT_PRICE)
        return MasterProduct(
            id = c.getLong(c.getColumnIndexOrThrow(COL_MP_ID)),
            name = c.getString(c.getColumnIndexOrThrow(COL_MP_NAME)),
            normalizedName = c.getString(c.getColumnIndexOrThrow(COL_MP_NORMALIZED_NAME)),
            category = c.getString(c.getColumnIndexOrThrow(COL_MP_CATEGORY)),
            defaultUnit = c.getString(c.getColumnIndexOrThrow(COL_MP_DEFAULT_UNIT)),
            latestUnitPrice = c.getDouble(c.getColumnIndexOrThrow(COL_MP_LATEST_UNIT_PRICE)),
            previousUnitPrice = if (!c.isNull(prevRateIdx)) c.getDouble(prevRateIdx) else null,
            minUnitPrice = c.getDouble(c.getColumnIndexOrThrow(COL_MP_MIN_UNIT_PRICE)),
            maxUnitPrice = c.getDouble(c.getColumnIndexOrThrow(COL_MP_MAX_UNIT_PRICE)),
            avgUnitPrice = c.getDouble(c.getColumnIndexOrThrow(COL_MP_AVG_UNIT_PRICE)),
            purchaseCount = c.getInt(c.getColumnIndexOrThrow(COL_MP_PURCHASE_COUNT)),
            isStaple = c.getInt(c.getColumnIndexOrThrow(COL_MP_IS_STAPLE)) == 1,
            firstPurchasedAt = c.getLong(c.getColumnIndexOrThrow(COL_MP_FIRST_PURCHASED_AT)),
            lastPurchasedAt = c.getLong(c.getColumnIndexOrThrow(COL_MP_LAST_PURCHASED_AT))
        )
    }
}
