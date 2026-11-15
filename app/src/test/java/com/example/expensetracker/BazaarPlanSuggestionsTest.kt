package com.example.expensetracker

import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.data.model.MarketList
import com.example.expensetracker.data.model.MarketListWithItems
import com.example.expensetracker.data.model.MasterProduct
import com.example.expensetracker.ui.bazaar.BazaarDomainLogic
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar
import java.util.concurrent.atomic.AtomicBoolean

class BazaarPlanSuggestionsTest {

    @Test
    fun testSuggestionsDedupeAgainstExistingItems() {
        val existingItems = listOf(
            MarketItem(id = 1L, name = "আলু", quantity = 1.0, unit = "কেজি"),
            MarketItem(id = 2L, name = "পেঁয়াজ", quantity = 2.0, unit = "কেজি")
        )

        val completedTrips = listOf(
            MarketListWithItems(
                list = MarketList(id = 10L, title = "Trip 1", isCompleted = true),
                items = listOf(
                    MarketItem(id = 101L, name = "আলু", isPurchased = true, amount = 60.0),
                    MarketItem(id = 102L, name = "রসুন", isPurchased = true, amount = 40.0)
                )
            )
        )

        val stapleProducts = listOf(
            MasterProduct(id = 1L, name = "আলু", normalizedName = "alu", isStaple = true),
            MasterProduct(id = 2L, name = "রসুন", normalizedName = "rosun", isStaple = true),
            MasterProduct(id = 3L, name = "আদা", normalizedName = "ada", isStaple = true)
        )

        val groups = BazaarDomainLogic.buildEmptyStateSuggestions(
            existingItems = existingItems,
            completedTrips = completedTrips,
            stapleProducts = stapleProducts,
            currentDayOfWeek = Calendar.FRIDAY
        )

        // Verify "আলু" and "পেঁয়াজ" are NOT suggested because they are already in existingItems
        for (g in groups) {
            assertFalse(
                "Group ${g.id} must not suggest 'আলু' because it already exists on the list",
                g.chips.any { it.name == "আলু" }
            )
            assertFalse(
                "Group ${g.id} must not suggest 'পেঁয়াজ' because it already exists on the list",
                g.chips.any { it.name == "পেঁয়াজ" }
            )
        }

        // "রসুন" and "আদা" should be suggested
        val allChipNames = groups.flatMap { it.chips }.map { it.name }
        assertTrue("Suggestions should contain 'রসুন'", allChipNames.contains("রসুন"))
    }

    @Test
    fun testLastTripSuggestions() {
        val completedTrips = listOf(
            MarketListWithItems(
                list = MarketList(id = 20L, title = "Previous Trip", isCompleted = true),
                items = listOf(
                    MarketItem(id = 201L, name = "কাঁচামরিচ", quantity = 250.0, unit = "গ্রাম", isPurchased = true, amount = 30.0),
                    MarketItem(id = 202L, name = "টমেটো", quantity = 1.0, unit = "কেজি", isPurchased = true, amount = 80.0),
                    MarketItem(id = 203L, name = "বাদাম", isPurchased = false) // Not purchased, must not be suggested from last trip
                )
            )
        )

        val groups = BazaarDomainLogic.buildEmptyStateSuggestions(
            existingItems = emptyList(),
            completedTrips = completedTrips,
            stapleProducts = emptyList()
        )

        val lastTripGroup = groups.find { it.id == "last_trip" }
        assertTrue("Last trip group must be present", lastTripGroup != null)
        val chipNames = lastTripGroup!!.chips.map { it.name }
        assertTrue(chipNames.contains("কাঁচামরিচ"))
        assertTrue(chipNames.contains("টমেটো"))
        assertFalse("Unpurchased items must not appear in last trip suggestions", chipNames.contains("বাদাম"))
    }

    @Test
    fun testWeekdayFrequencySuggestions() {
        val friday = Calendar.getInstance().apply { set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY) }.timeInMillis
        val completedTrips = listOf(
            MarketListWithItems(
                list = MarketList(id = 30L, title = "Friday 1", isCompleted = true, createdAt = friday),
                items = listOf(MarketItem(id = 301L, name = "গরুর মাংস", isPurchased = true))
            ),
            MarketListWithItems(
                list = MarketList(id = 31L, title = "Friday 2", isCompleted = true, createdAt = friday),
                items = listOf(MarketItem(id = 302L, name = "গরুর মাংস", isPurchased = true))
            ),
            MarketListWithItems(
                list = MarketList(id = 32L, title = "Friday 3", isCompleted = true, createdAt = friday),
                items = listOf(MarketItem(id = 303L, name = "পোলাও চাল", isPurchased = true)) // Only 1 occurrence
            )
        )

        val groups = BazaarDomainLogic.buildEmptyStateSuggestions(
            existingItems = emptyList(),
            completedTrips = completedTrips,
            stapleProducts = emptyList(),
            currentDayOfWeek = Calendar.FRIDAY
        )

        val freqGroup = groups.find { it.id == "weekday_freq" }
        assertTrue("Weekday freq group must be present for Friday", freqGroup != null)
        val names = freqGroup!!.chips.map { it.name }
        assertTrue("গরুর মাংস bought twice on Friday must appear", names.contains("গরুর মাংস"))
        assertFalse("পোলাও চাল bought only once must NOT appear (minFrequency=2)", names.contains("পোলাও চাল"))
    }

    @Test
    fun testStaplesSuggestions() {
        val stapleProducts = listOf(
            MasterProduct(id = 1L, name = "সয়াবিন তেল", normalizedName = "soyabin_tel", isStaple = true, defaultUnit = "লিটার", latestUnitPrice = 175.0),
            MasterProduct(id = 2L, name = "মসুর ডাল", normalizedName = "mosur_dal", isStaple = true, defaultUnit = "কেজি", latestUnitPrice = 130.0)
        )

        val groups = BazaarDomainLogic.buildEmptyStateSuggestions(
            existingItems = emptyList(),
            completedTrips = emptyList(),
            stapleProducts = stapleProducts
        )

        val staplesGroup = groups.find { it.id == "staples" }
        assertTrue("Staples group must be present", staplesGroup != null)
        assertEquals(2, staplesGroup!!.chips.size)
        assertEquals("সয়াবিন তেল", staplesGroup.chips[0].name)
        assertEquals("লিটার", staplesGroup.chips[0].defaultUnit)
        assertEquals(175.0, staplesGroup.chips[0].estimatedPrice ?: 0.0, 0.01)
    }

    @Test
    fun testTemplatesSuggestions() {
        val groups = BazaarDomainLogic.buildEmptyStateSuggestions(
            existingItems = emptyList(),
            completedTrips = emptyList(),
            stapleProducts = emptyList()
        )

        val templateGroup = groups.find { it.id == "templates" }
        assertTrue("Template group must be present", templateGroup != null)
        val templateNames = templateGroup!!.chips.map { it.name }
        assertTrue(templateNames.contains("সাপ্তাহিক বাজার"))
        assertTrue(templateNames.contains("মাছ ও মাংস"))
        assertTrue(templateNames.contains("ঈদের বাজার"))

        val weekly = templateGroup.chips.find { it.name == "সাপ্তাহিক বাজার" }!!
        assertTrue("Template chip must have isTemplate = true", weekly.isTemplate)
        assertTrue("Weekly template must contain items", weekly.templateItems.isNotEmpty())
    }

    @Test
    fun testHideGroupsWithNoData() {
        val groups = BazaarDomainLogic.buildEmptyStateSuggestions(
            existingItems = emptyList(),
            completedTrips = emptyList(),
            stapleProducts = emptyList(),
            currentDayOfWeek = Calendar.MONDAY
        )

        // When completedTrips and stapleProducts are empty:
        // "last_trip", "weekday_freq", and "staples" groups must be hidden!
        assertFalse("last_trip group must not exist when no completed trips", groups.any { it.id == "last_trip" })
        assertFalse("weekday_freq group must not exist when no trips on Monday", groups.any { it.id == "weekday_freq" })
        assertFalse("staples group must not exist when no staple products", groups.any { it.id == "staples" })

        // Only templates remain
        assertTrue("Templates group should be present", groups.any { it.id == "templates" })
    }

    @Test
    fun testDoubleTapGuard() {
        val atomicGuard = AtomicBoolean(false)
        var creationCount = 0

        val simulateTap = {
            if (atomicGuard.compareAndSet(false, true)) {
                creationCount++
                // simulate job running...
            }
        }

        // Tap 1
        simulateTap()
        assertEquals(1, creationCount)

        // Immediate rapid Tap 2 while Tap 1 is in progress
        simulateTap()
        assertEquals(1, creationCount) // Must NOT increment

        // After completion
        atomicGuard.set(false)
        simulateTap()
        assertEquals(2, creationCount)
    }
}
