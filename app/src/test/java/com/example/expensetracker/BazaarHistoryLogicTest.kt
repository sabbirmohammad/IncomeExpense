package com.example.expensetracker

import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.data.model.MarketList
import com.example.expensetracker.data.model.MarketListWithItems
import com.example.expensetracker.ui.bazaar.BazaarDomainLogic
import com.example.expensetracker.ui.bazaar.BazaarTripDetailStats
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class BazaarHistoryLogicTest {

    private fun createCalendarMillis(year: Int, month: Int, day: Int, hour: Int = 10): Long {
        return Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, day)
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    @Test
    fun testMonthGroupingOnlyIncludesCompletedTrips() {
        val completed1 = MarketListWithItems(
            list = MarketList(id = 1L, title = "মে মাসের বাজার", createdAt = createCalendarMillis(2026, Calendar.MAY, 5), isCompleted = true),
            items = listOf(
                MarketItem(id = 11L, name = "আলু", quantity = 2.0, unit = "কেজি", amount = 80.0, isPurchased = true),
                MarketItem(id = 12L, name = "পেঁয়াজ", quantity = 1.0, unit = "কেজি", amount = null, isPurchased = false)
            )
        )
        val activeTrip = MarketListWithItems(
            list = MarketList(id = 2L, title = "চলতি বাজার", createdAt = createCalendarMillis(2026, Calendar.MAY, 8), isCompleted = false),
            items = listOf(
                MarketItem(id = 21L, name = "রুই মাছ", quantity = 1.0, unit = "কেজি", amount = 450.0, isPurchased = true)
            )
        )
        val completed2 = MarketListWithItems(
            list = MarketList(id = 3L, title = "এপ্রিলের বাজার", createdAt = createCalendarMillis(2026, Calendar.APRIL, 20), isCompleted = true),
            items = listOf(
                MarketItem(id = 31L, name = "গরুর মাংস", quantity = 1.0, unit = "কেজি", amount = 750.0, isPurchased = true)
            )
        )

        val groups = BazaarDomainLogic.groupCompletedTripsByMonth(listOf(completed1, activeTrip, completed2), isBangla = true)

        assertEquals("Must produce 2 month groups (active trip excluded)", 2, groups.size)
        assertEquals("First group must be May 2026", Calendar.MAY, groups[0].month)
        assertEquals("May group has 1 trip", 1, groups[0].tripCount)
        assertEquals("May group total spend is 80.0 (purchased item only)", 80.0, groups[0].totalSpend, 0.001)

        assertEquals("Second group must be April 2026", Calendar.APRIL, groups[1].month)
        assertEquals("April group has 1 trip", 1, groups[1].tripCount)
        assertEquals("April group total spend is 750.0", 750.0, groups[1].totalSpend, 0.001)
    }

    @Test
    fun testHistoryTotalsRulePurchasedItemsOnly() {
        // Invariant: trip item count = purchased items only; unpurchased items contribute 0 to count and total
        val trip = MarketListWithItems(
            list = MarketList(id = 10L, title = "সাপ্তাহিক বাজার", createdAt = createCalendarMillis(2026, Calendar.NOVEMBER, 12), isCompleted = true),
            items = listOf(
                MarketItem(id = 1L, name = "রুই মাছ", quantity = 1.5, unit = "কেজি", amount = 650.0, isPurchased = true),
                MarketItem(id = 2L, name = "টমেটো", quantity = 1.0, unit = "কেজি", amount = 60.0, isPurchased = true),
                MarketItem(id = 3L, name = "ধনেপাতা", quantity = 1.0, unit = "আঁটি", amount = null, isPurchased = false),
                MarketItem(id = 4L, name = "কাঁচা মরিচ", quantity = 250.0, unit = "গ্রাম", amount = null, isPurchased = false)
            )
        )

        val groups = BazaarDomainLogic.groupCompletedTripsByMonth(listOf(trip), isBangla = true)
        assertEquals(1, groups.size)

        val summary = groups[0].trips[0]
        assertEquals("Purchased item count must be exactly 2", 2, summary.purchasedItemCount)
        assertEquals("Unbought item count must be 2", 2, summary.unboughtItemCount)
        assertEquals("Total spend must sum only purchased items (650 + 60 = 710)", 710.0, summary.totalSpend, 0.001)
        assertEquals(listOf("রুই মাছ", "টমেটো"), summary.purchasedItemsPreview)
    }

    @Test
    fun testSearchFilteringByTitleOrContainedItem() {
        val tripA = MarketListWithItems(
            list = MarketList(id = 1L, title = "শুক্রবারের স্পেশাল বাজার", createdAt = createCalendarMillis(2026, Calendar.OCTOBER, 2), isCompleted = true),
            items = listOf(
                MarketItem(id = 11L, name = "ইলিশ মাছ", quantity = 1.0, unit = "পিস", amount = 1200.0, isPurchased = true),
                MarketItem(id = 12L, name = "সরিষা তেল", quantity = 500.0, unit = "মিলি", amount = 180.0, isPurchased = true)
            )
        )
        val tripB = MarketListWithItems(
            list = MarketList(id = 2L, title = "মাসিক মুদি সদাই", createdAt = createCalendarMillis(2026, Calendar.OCTOBER, 15), isCompleted = true),
            items = listOf(
                MarketItem(id = 21L, name = "বাসমতী চাল", quantity = 5.0, unit = "কেজি", amount = 450.0, isPurchased = true),
                MarketItem(id = 22L, name = "মসুর ডাল", quantity = 2.0, unit = "কেজি", amount = 260.0, isPurchased = true)
            )
        )

        val allGroups = BazaarDomainLogic.groupCompletedTripsByMonth(listOf(tripA, tripB), isBangla = true)
        assertEquals(1, allGroups.size)
        assertEquals(2, allGroups[0].trips.size)

        // 1. Search by title keyword
        val filteredByTitle = BazaarDomainLogic.filterHistoryGroups(allGroups, "স্পেশাল")
        assertEquals(1, filteredByTitle.size)
        assertEquals(1, filteredByTitle[0].trips.size)
        assertEquals(1L, filteredByTitle[0].trips[0].listId)

        // 2. Search by item name contained inside list
        val filteredByItem = BazaarDomainLogic.filterHistoryGroups(allGroups, "মসুর ডাল")
        assertEquals(1, filteredByItem.size)
        assertEquals(1, filteredByItem[0].trips.size)
        assertEquals(2L, filteredByItem[0].trips[0].listId)

        // 3. Search case-insensitive
        val filteredCase = BazaarDomainLogic.filterHistoryGroups(allGroups, "ইলিশ")
        assertEquals(1, filteredCase.size)
        assertEquals(1L, filteredCase[0].trips[0].listId)

        // 4. Non-matching query yields empty groups
        val filteredNone = BazaarDomainLogic.filterHistoryGroups(allGroups, "অজানা পণ্য")
        assertTrue("No groups match unknown query", filteredNone.isEmpty())

        // 5. Blank query returns all groups unchanged
        val filteredBlank = BazaarDomainLogic.filterHistoryGroups(allGroups, "   ")
        assertEquals(1, filteredBlank.size)
        assertEquals(2, filteredBlank[0].trips.size)
    }

    @Test
    fun testTripDetailStatsSeparatesPurchasedAndUnboughtItems() {
        val trip = MarketListWithItems(
            list = MarketList(id = 99L, title = "সন্ধ্যা বাজার", createdAt = 1000000L, isCompleted = true),
            items = listOf(
                MarketItem(id = 1L, name = "দুধ", quantity = 1.0, unit = "লিটার", amount = 90.0, isPurchased = true),
                MarketItem(id = 2L, name = "ডিম", quantity = 1.0, unit = "ডজন", amount = 150.0, isPurchased = true),
                MarketItem(id = 3L, name = "পাউরুটি", quantity = 1.0, unit = "প্যাকেট", amount = null, isPurchased = false)
            )
        )

        val stats: BazaarTripDetailStats = BazaarDomainLogic.calculateTripDetailStats(trip)

        assertEquals(99L, stats.listId)
        assertEquals("সন্ধ্যা বাজার", stats.title)
        assertEquals(2, stats.purchasedCount)
        assertEquals(1, stats.unboughtCount)
        assertEquals(240.0, stats.totalSpend, 0.001)

        assertEquals(2, stats.purchasedItems.size)
        assertTrue(stats.purchasedItems.all { it.isPurchased })
        assertEquals(1, stats.unboughtItems.size)
        assertFalse(stats.unboughtItems[0].isPurchased)
        assertEquals("পাউরুটি", stats.unboughtItems[0].name)
    }

    @Test
    fun testBuyAgainClonesPurchasedItemsOnly() {
        // Invariant: "Buy again" clones PURCHASED items only into new items with amount = null, isPurchased = false
        val sourceItems = listOf(
            MarketItem(id = 1L, name = "মাছ", quantity = 1.0, unit = "কেজি", amount = 400.0, isPurchased = true),
            MarketItem(id = 2L, name = "সবজি", quantity = 2.0, unit = "কেজি", amount = 80.0, isPurchased = true),
            MarketItem(id = 3L, name = "ফল", quantity = 1.0, unit = "কেজি", amount = null, isPurchased = false)
        )

        val clonedForBuyAgain = sourceItems.filter { it.isPurchased }.map {
            MarketItem(
                name = it.name,
                quantity = it.quantity,
                unit = it.unit,
                amount = null,
                isPurchased = false,
                transactionId = null
            )
        }

        assertEquals(2, clonedForBuyAgain.size)
        assertEquals("মাছ", clonedForBuyAgain[0].name)
        assertEquals(null, clonedForBuyAgain[0].amount)
        assertFalse(clonedForBuyAgain[0].isPurchased)
        assertEquals("সবজি", clonedForBuyAgain[1].name)
        assertEquals(null, clonedForBuyAgain[1].amount)
        assertFalse(clonedForBuyAgain[1].isPurchased)
    }

    @Test
    fun testCarryOverClonesUnboughtItemsOnly() {
        // Invariant: "Carry over" clones UNBOUGHT items only into new items with amount = null, isPurchased = false
        val sourceItems = listOf(
            MarketItem(id = 1L, name = "মাছ", quantity = 1.0, unit = "কেজি", amount = 400.0, isPurchased = true),
            MarketItem(id = 2L, name = "সবজি", quantity = 2.0, unit = "কেজি", amount = 80.0, isPurchased = true),
            MarketItem(id = 3L, name = "ফল", quantity = 1.0, unit = "কেজি", amount = null, isPurchased = false),
            MarketItem(id = 4L, name = "মিষ্টি", quantity = 500.0, unit = "গ্রাম", amount = null, isPurchased = false)
        )

        val clonedForCarryOver = sourceItems.filter { !it.isPurchased }.map {
            MarketItem(
                name = it.name,
                quantity = it.quantity,
                unit = it.unit,
                amount = null,
                isPurchased = false,
                transactionId = null
            )
        }

        assertEquals(2, clonedForCarryOver.size)
        assertEquals("ফল", clonedForCarryOver[0].name)
        assertEquals(null, clonedForCarryOver[0].amount)
        assertFalse(clonedForCarryOver[0].isPurchased)
        assertEquals("মিষ্টি", clonedForCarryOver[1].name)
        assertEquals(null, clonedForCarryOver[1].amount)
        assertFalse(clonedForCarryOver[1].isPurchased)
    }
}
