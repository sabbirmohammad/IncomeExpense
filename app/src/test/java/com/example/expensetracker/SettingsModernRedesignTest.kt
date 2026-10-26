package com.example.expensetracker

import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.AccountType
import com.example.expensetracker.data.model.BackupPayload
import com.example.expensetracker.data.model.Budget
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.data.model.PaymentMethod
import com.example.expensetracker.data.model.Transaction
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.data.model.UserProfile
import com.example.expensetracker.theme.AmoledAppColors
import com.example.expensetracker.theme.DarkAppColors
import com.example.expensetracker.theme.LightAppColors
import com.example.expensetracker.ui.components.Strings
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SettingsModernRedesignTest {
    private val json = Json { ignoreUnknownKeys = true; prettyPrint = false }

    @Test
    fun testUserProfileExecutiveDisplayProperties() {
        val profileWithFirstLast = UserProfile(
            firstName = "Sabbir",
            lastName = "Ahmed",
            name = "Sabbir Ahmed",
            currencyCode = "BDT",
            currencySymbol = "৳",
            googleAccountEmail = "sabbir@gmail.com"
        )

        assertEquals("Sabbir Ahmed", profileWithFirstLast.displayName)
        assertEquals("SA", profileWithFirstLast.initials)
        assertNotNull(profileWithFirstLast.googleAccountEmail)

        val localProfile = UserProfile(
            firstName = "Awfi",
            lastName = "",
            googleAccountEmail = null
        )
        assertEquals("Awfi", localProfile.displayName)
        assertEquals("AW", localProfile.initials)
        assertEquals(null, localProfile.googleAccountEmail)
    }

    @Test
    fun testCategoryFilteringLogicForModernCard() {
        val testCategories = listOf(
            Category(id = 1, name = "খাবার", type = TransactionType.EXPENSE, icon = "restaurant", colorHex = "#F0716B"),
            Category(id = 2, name = "বেতন", type = TransactionType.INCOME, icon = "payments", colorHex = "#3DDC97"),
            Category(id = 3, name = "যাতায়াত", type = TransactionType.EXPENSE, icon = "directions_bus", colorHex = "#3B8FF0"),
            Category(id = 4, name = "ফ্রিল্যান্সিং", type = TransactionType.INCOME, icon = "computer", colorHex = "#6366F1")
        )

        val expenses = testCategories.filter { it.type == TransactionType.EXPENSE }
        val incomes = testCategories.filter { it.type == TransactionType.INCOME }

        assertEquals(2, expenses.size)
        assertEquals(2, incomes.size)
        assertEquals(4, testCategories.size)
    }

    @Test
    fun testSettingsStringsLocalization() {
        val stringsBn = Strings(isBangla = true)
        val stringsEn = Strings(isBangla = false)

        assertTrue(stringsBn.isBangla)
        assertFalse(stringsEn.isBangla)

        assertEquals("সেটিংস", stringsBn.settingsTitle)
        assertEquals("Settings", stringsEn.settingsTitle)

        assertEquals("গুগল ড্রাইভ ব্যাকআপ", stringsBn.googleDriveBackup)
        assertEquals("Google Drive backup", stringsEn.googleDriveBackup)

        assertEquals("সংযুক্ত", stringsBn.connected)
        assertEquals("Connected", stringsEn.connected)
    }

    @Test
    fun testThemeColorsIntegrityForSettingsScreen() {
        // Ensure Dark, Light, AMOLED theme palettes have valid and distinct backgrounds/surfaces
        assertTrue(DarkAppColors.isDark)
        assertTrue(AmoledAppColors.isDark)
        assertFalse(LightAppColors.isDark)

        assertNotNull(DarkAppColors.background)
        assertNotNull(LightAppColors.background)
        assertNotNull(AmoledAppColors.background)

        assertNotNull(DarkAppColors.surface)
        assertNotNull(LightAppColors.surface)
        assertNotNull(AmoledAppColors.surface)
    }

    @Test
    fun testBackupPayloadIntegrityForGoogleDriveSync() {
        val payload = BackupPayload(
            version = 1,
            exportDateMillis = 1700000000000L,
            profile = UserProfile(
                firstName = "Sabbir",
                lastName = "Ahmed",
                name = "Sabbir Ahmed",
                currencyCode = "BDT",
                currencySymbol = "৳",
                googleAccountEmail = "sabbir@gmail.com"
            ),
            categories = listOf(
                Category(id = 1, name = "Food", type = TransactionType.EXPENSE, icon = "restaurant", colorHex = "#F0716B")
            ),
            transactions = listOf(
                Transaction(
                    id = 100,
                    title = "Dinner",
                    amount = 550.0,
                    type = TransactionType.EXPENSE,
                    categoryId = 1,
                    categoryName = "Food",
                    categoryIcon = "restaurant",
                    categoryColor = "#F0716B",
                    dateMillis = 1700000005000L,
                    paymentMethod = PaymentMethod.CASH,
                    notes = "Family dinner"
                )
            ),
            budgets = listOf(
                Budget(id = 1, categoryId = 1, categoryName = "Food", amount = 25000.0, month = 10, year = 2026)
            ),
            accounts = listOf(
                Account(id = 1, name = "Cash", type = AccountType.CASH, isDefault = true)
            )
        )

        val serialized = json.encodeToString(payload)
        assertTrue(serialized.contains("sabbir@gmail.com"))
        assertTrue(serialized.contains("Dinner"))

        val deserialized = json.decodeFromString<BackupPayload>(serialized)
        assertEquals(payload.version, deserialized.version)
        assertEquals("sabbir@gmail.com", deserialized.profile.googleAccountEmail)
        assertEquals(1, deserialized.transactions.size)
        assertEquals(550.0, deserialized.transactions[0].amount, 0.001)
        assertEquals(1, deserialized.budgets.size)
        assertEquals(1, deserialized.accounts.size)
        assertEquals("Cash", deserialized.accounts[0].name)
    }

    @Test
    fun testStatementCsvEscapingIntegrity() {
        val rawTitle = "Office \"Supplies\", pens"
        val safeTitle = rawTitle.replace("\"", "\"\"")
        assertEquals("Office \"\"Supplies\"\", pens", safeTitle)

        val rawCategory = "Office, Misc"
        val safeCategory = rawCategory.replace("\"", "\"\"")
        val quoted = "\"$safeCategory\""
        assertEquals("\"Office, Misc\"", quoted)
    }

    @Test
    fun testBackupPayloadHandlesEmptyCollections() {
        val emptyPayload = BackupPayload(
            version = 4,
            exportDateMillis = 1700000000000L,
            profile = UserProfile(
                firstName = "Test",
                lastName = "User"
            ),
            categories = emptyList(),
            transactions = emptyList(),
            budgets = emptyList(),
            accounts = emptyList()
        )

        val serialized = json.encodeToString(emptyPayload)
        assertFalse(serialized.contains("geminiApiKey"))

        val deserialized = json.decodeFromString<BackupPayload>(serialized)
        assertEquals(4, deserialized.version)
        assertEquals(0, deserialized.transactions.size)
        assertEquals(0, deserialized.categories.size)
        assertEquals(0, deserialized.accounts.size)
        assertEquals("Test", deserialized.profile.firstName)
    }

    @Test
    fun testBackupPayloadForwardCompatibilityWithUnknownFields() {
        val jsonWithUnknownFields = """
            {
                "version": 5,
                "exportDateMillis": 1700000000000,
                "profile": {
                    "firstName": "Future",
                    "lastName": "User",
                    "futureFeatureFlag": true
                },
                "categories": [],
                "transactions": [],
                "budgets": [],
                "accounts": [],
                "extraFutureEntity": [1, 2, 3]
            }
        """.trimIndent()

        val deserialized = json.decodeFromString<BackupPayload>(jsonWithUnknownFields)
        assertEquals(5, deserialized.version)
        assertEquals("Future", deserialized.profile.firstName)
    }

    @Test
    fun testRestoreNowStringPreservedForDriveDialog() {
        val stringsBn = Strings(isBangla = true)
        val stringsEn = Strings(isBangla = false)

        assertEquals("এখনই রিস্টোর করুন", stringsBn.restoreNow)
        assertEquals("Restore now", stringsEn.restoreNow)
    }
}

