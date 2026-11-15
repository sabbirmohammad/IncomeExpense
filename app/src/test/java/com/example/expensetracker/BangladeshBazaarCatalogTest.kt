package com.example.expensetracker

import com.example.expensetracker.common.MarketListParser
import com.example.expensetracker.data.local.BangladeshBazaarCatalog
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BangladeshBazaarCatalogTest {

    @Test
    fun testCatalogContainsOver700UniqueItems() {
        val all = BangladeshBazaarCatalog.ALL_COMMODITIES
        assertTrue("Catalog must contain at least 700 items, found ${all.size}", all.size >= 700)
        
        val uniqueNames = all.map { it.name }.toSet()
        assertEquals("All items must have unique names", all.size, uniqueNames.size)
    }

    @Test
    fun testCatalogCoversRequestedSpecificCategories() {
        val all = BangladeshBazaarCatalog.ALL_COMMODITIES
        val names = all.map { it.name }

        // 1. কাঁচাবাজার ও শাকসবজি
        assertTrue(names.contains("পটল"))
        assertTrue(names.contains("ঢ্যাঁড়শ"))
        assertTrue(names.contains("করলা"))
        assertTrue(names.contains("পালং শাক"))
        assertTrue(names.contains("সজনে ডাঁটা"))

        // 2. মাছ ও শুঁটকি
        assertTrue(names.contains("ইলিশ মাছ (পদ্মা)"))
        assertTrue(names.contains("রুই মাছ"))
        assertTrue(names.contains("পাবদা মাছ"))
        assertTrue(names.contains("গলদা চিংড়ি (বড়)"))
        assertTrue(names.contains("লইট্টা শুঁটকি"))

        // 3. মাংস ও ডিম
        assertTrue(names.contains("গরুর মাংস (হাড়সহ)"))
        assertTrue(names.contains("খাসির মাংস"))
        assertTrue(names.contains("ব্রয়লার মুরগি (জীবিত)"))
        assertTrue(names.contains("ফার্মের লাল ডিম"))

        // 4. গরুর খাবার, মাছের খাবার, কৃষি
        assertTrue("Must contain gomer bhusi", names.any { it.contains("ভুসি") })
        assertTrue("Must contain shorisha khoil", names.any { it.contains("খৈল") })
        assertTrue("Must contain cattle feed", names.any { it.contains("ক্যাটল") })
        assertTrue("Must contain floating fish feed", names.any { it.contains("ভাসমান") || it.contains("মাছের") })

        // 5. ইলেকট্রিসিটি পণ্য
        assertTrue("Must contain LED bulb", names.any { it.contains("এলইডি বাল্ব") })
        assertTrue("Must contain multiplug", names.any { it.contains("মাল্টিপ্লাগ") })
        assertTrue("Must contain electric tape", names.any { it.contains("ইলেকট্রিক ব্ল্যাক টেপ") })
        assertTrue("Must contain battery", names.any { it.contains("পেন্সিল ব্যাটারি") })

        // 6. টয়লেট্রিজ ও ক্লিনিং
        assertTrue("Must contain soap", names.any { it.contains("সাবান") })
        assertTrue("Must contain toilet cleaner/harpic", names.any { it.contains("হারপিক") })
        assertTrue("Must contain toothpaste", names.any { it.contains("টুথপেস্ট") })
    }

    @Test
    fun testPhoneticBilingualSearch() {
        // Bengali search
        val potolBn = BangladeshBazaarCatalog.search("পটল")
        assertTrue(potolBn.isNotEmpty())
        assertEquals("পটল", potolBn.first().name)

        // Banglish search
        val potolEn = BangladeshBazaarCatalog.search("potol")
        assertTrue(potolEn.isNotEmpty())
        assertEquals("পটল", potolEn.first().name)

        // Dim search
        val dimEn = BangladeshBazaarCatalog.search("dim")
        assertTrue(dimEn.any { it.name.contains("ডিম") })

        // Goru / Beef search
        val goruEn = BangladeshBazaarCatalog.search("beef")
        assertTrue(goruEn.any { it.name.contains("গরু") })

        // Fish search
        val ruiEn = BangladeshBazaarCatalog.search("rui")
        assertTrue(ruiEn.first().name.contains("রুই"))

        // Feed search
        val feedEn = BangladeshBazaarCatalog.search("feed")
        assertTrue(feedEn.any { it.name.contains("ফিড") || it.name.contains("খাবার") || it.name.contains("ভুসি") })

        // LED / bulb search
        val bulbEn = BangladeshBazaarCatalog.search("bulb")
        assertTrue(bulbEn.first().name.contains("বাল্ব"))

        // Bhusi search
        val bhusiEn = BangladeshBazaarCatalog.search("bhusi")
        assertTrue(bhusiEn.any { it.name.contains("ভুসি") })
    }

    @Test
    fun testNaturalTypingWithQuantityAndUnitSuggestions() {
        // Case 1: "2kg alu"
        val parsed1 = MarketListParser.parseSingleLine("2kg alu")
        assertNotNull(parsed1)
        assertEquals(2.0, parsed1!!.quantity, 0.001)
        assertEquals("কেজি", parsed1.unit)
        val matches1 = BangladeshBazaarCatalog.search(parsed1.name)
        assertTrue(matches1.isNotEmpty())
        assertEquals("আলু", matches1.first().name)

        // Case 2: "পটল ৫০০ গ্রাম"
        val parsed2 = MarketListParser.parseSingleLine("পটল ৫০০ গ্রাম")
        assertNotNull(parsed2)
        assertEquals(500.0, parsed2!!.quantity, 0.001)
        assertEquals("গ্রাম", parsed2.unit)
        val matches2 = BangladeshBazaarCatalog.search(parsed2.name)
        assertTrue(matches2.isNotEmpty())
        assertEquals("পটল", matches2.first().name)

        // Case 3: "1 hali dim"
        val parsed3 = MarketListParser.parseSingleLine("1 hali dim")
        assertNotNull(parsed3)
        assertEquals(1.0, parsed3!!.quantity, 0.001)
        assertEquals("হালি", parsed3.unit)
        val matches3 = BangladeshBazaarCatalog.search(parsed3.name)
        assertTrue(matches3.any { it.name.contains("ডিম") })

        // Case 4: "৩টা এলইডি বাল্ব"
        val parsed4 = MarketListParser.parseSingleLine("৩টা এলইডি বাল্ব")
        assertNotNull(parsed4)
        assertEquals(3.0, parsed4!!.quantity, 0.001)
        assertEquals("পিস", parsed4.unit)
        val matches4 = BangladeshBazaarCatalog.search(parsed4.name)
        assertTrue(matches4.any { it.name.contains("এলইডি বাল্ব") })

        // Case 5: "১০ কেজি গমের ভুসি"
        val parsed5 = MarketListParser.parseSingleLine("১০ কেজি গমের ভুসি")
        assertNotNull(parsed5)
        assertEquals(10.0, parsed5!!.quantity, 0.001)
        assertEquals("কেজি", parsed5.unit)
        val matches5 = BangladeshBazaarCatalog.search(parsed5.name)
        assertTrue(matches5.any { it.name.contains("ভুসি") })
    }

    @Test
    fun testQuickStaplesArePopulated() {
        val staples = BangladeshBazaarCatalog.QUICK_STAPLES
        assertTrue("Staples should have at least 25 items", staples.size >= 25)
        val stapleNames = staples.map { it.name }
        assertTrue(stapleNames.contains("আলু"))
        assertTrue(stapleNames.contains("দেশি পেঁয়াজ"))
        assertTrue(stapleNames.contains("কাঁচামরিচ"))
        assertTrue(stapleNames.contains("ফার্মের লাল ডিম"))
    }
}
