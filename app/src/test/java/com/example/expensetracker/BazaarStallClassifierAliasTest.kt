package com.example.expensetracker

import com.example.expensetracker.data.model.MasterProduct
import com.example.expensetracker.ui.bazaar.BazaarDomainLogic
import com.example.expensetracker.ui.dashboard.classifyStallWithOverride
import com.example.expensetracker.ui.dashboard.resolveCommodityAlias
import org.junit.Assert.assertEquals
import org.junit.Test

class BazaarStallClassifierAliasTest {

    @Test
    fun testPiyajClassifiesToVeggiesInAllVariants() {
        // As specifically requested in BUG D.4:
        // Test: "piyaj", "Piyaj ", "পেঁয়াজ" classify to কাঁচাবাজার (veggies)
        assertEquals("veggies", classifyStallWithOverride("piyaj", null))
        assertEquals("veggies", classifyStallWithOverride("Piyaj ", null))
        assertEquals("veggies", classifyStallWithOverride("পেঁয়াজ", null))

        // Through BazaarDomainLogic as well
        assertEquals("veggies", BazaarDomainLogic.classifyStallWithOverride("piyaj", null))
        assertEquals("veggies", BazaarDomainLogic.classifyStallWithOverride("Piyaj ", null))
        assertEquals("veggies", BazaarDomainLogic.classifyStallWithOverride("পেঁয়াজ", null))
    }

    @Test
    fun testCommonTransliteratedAliasesResolveCorrectly() {
        assertEquals("পেঁয়াজ", resolveCommodityAlias("piyaj"))
        assertEquals("পেঁয়াজ", resolveCommodityAlias("Piyaj "))
        assertEquals("আলু", resolveCommodityAlias("alu"))
        assertEquals("আলু", resolveCommodityAlias("Aloo "))
        assertEquals("ডিম", resolveCommodityAlias("dim"))
        assertEquals("মুরগি", resolveCommodityAlias("murgi"))
        assertEquals("রসুন", resolveCommodityAlias("rosun"))
        assertEquals("মরিচ", resolveCommodityAlias("morich"))
        assertEquals("তেল", resolveCommodityAlias("tel"))
        assertEquals("চাল", resolveCommodityAlias("chal"))
        assertEquals("ডাল", resolveCommodityAlias("dal"))
    }

    @Test
    fun testDepartmentClassificationsForAliases() {
        // Veggies (কাঁচাবাজার)
        assertEquals("veggies", classifyStallWithOverride("alu", null))
        assertEquals("veggies", classifyStallWithOverride("Aloo", null))
        assertEquals("veggies", classifyStallWithOverride("রসুন", null))
        assertEquals("veggies", classifyStallWithOverride("morich", null))

        // Meat & Fish (মাছ ও মাংস)
        assertEquals("meat_fish", classifyStallWithOverride("murgi", null))
        assertEquals("meat_fish", classifyStallWithOverride("chicken", null))
        assertEquals("meat_fish", classifyStallWithOverride("mach", null))

        // Dairy & Bakery (দুধ ও বেকারি)
        assertEquals("dairy_bakery", classifyStallWithOverride("dim", null))
        assertEquals("dairy_bakery", classifyStallWithOverride("egg", null))
        assertEquals("dairy_bakery", classifyStallWithOverride("dudh", null))

        // Groceries (মুদি সদাই)
        assertEquals("groceries", classifyStallWithOverride("chal", null))
        assertEquals("groceries", classifyStallWithOverride("rice", null))
        assertEquals("groceries", classifyStallWithOverride("dal", null))
        assertEquals("groceries", classifyStallWithOverride("tel", null))
    }

    @Test
    fun testPiyajWithStaleMasterProductRowClassifiesToVeggies() {
        val staleMap = mapOf(
            "piyaj" to MasterProduct(
                id = 1,
                name = "piyaj",
                normalizedName = "piyaj",
                category = "groceries",
                defaultUnit = "কেজি",
                latestUnitPrice = 45.0,
                purchaseCount = 1,
                lastPurchasedAt = System.currentTimeMillis()
            )
        )
        // Even if old master_products table had "piyaj" under "groceries",
        // the canonical alias "পেঁয়াজ" ensures it classifies to "veggies" (কাঁচাবাজার)
        assertEquals("veggies", classifyStallWithOverride("piyaj", staleMap))
        assertEquals("veggies", BazaarDomainLogic.classifyStallWithOverride("piyaj", staleMap))
    }
}
