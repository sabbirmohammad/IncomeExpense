package com.example.expensetracker

import com.example.expensetracker.data.ai.GeminiAiService
import com.example.expensetracker.data.model.AiBudgetRecommendation
import com.example.expensetracker.data.model.AiFinancialAudit
import com.example.expensetracker.data.model.AiInsightItem
import com.example.expensetracker.data.model.ApiConnectionTestResult
import com.example.expensetracker.data.model.ApiTestStatusType
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.data.model.PaymentMethod
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.ui.budget.BudgetViewModel
import java.util.Calendar
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AiComponentsTest {

    @Test
    fun testExtractJsonBlockFromMarkdown() {
        val fenced = "```json\n{\"healthScore\": 82, \"summary\": \"Good standing\"}\n```"
        val extracted = GeminiAiService.extractJsonBlock(fenced)
        assertEquals("{\"healthScore\": 82, \"summary\": \"Good standing\"}", extracted)

        val embedded = "Here is the result:\n```json\n{\"status\": \"OK\"}\n```\nHope it helps!"
        val extractedEmbedded = GeminiAiService.extractJsonBlock(embedded)
        assertEquals("{\"status\": \"OK\"}", extractedEmbedded)
    }

    @Test
    fun testExtractJsonArrayFromMarkdown() {
        val fenced = "```json\n[{\"category\": \"Food\", \"recommendedAmount\": 5000.0}]\n```"
        val extracted = GeminiAiService.extractJsonArray(fenced)
        assertEquals("[{\"category\": \"Food\", \"recommendedAmount\": 5000.0}]", extracted)
    }

    @Test
    fun testAiModelsInstantiation() {
        val rec = AiBudgetRecommendation(
            categoryName = "Food & Dining",
            currentSpend = 6000.0,
            recommendedAmount = 8000.0,
            reason = "Healthy allocation based on income"
        )
        assertEquals("Food & Dining", rec.categoryName)
        assertEquals(8000.0, rec.recommendedAmount, 0.001)

        val audit = AiFinancialAudit(
            healthScore = 85,
            statusGrade = "Excellent",
            summaryEvaluation = "Excellent financial control",
            detectedLeaksOrSpikes = listOf("Multiple food deliveries"),
            actionableTips = listOf("Cook at home twice a week")
        )
        assertEquals(85, audit.healthScore)
        assertEquals(1, audit.detectedLeaksOrSpikes.size)
        assertEquals("Excellent", audit.statusGrade)

        val testResult = ApiConnectionTestResult(
            isSuccess = true,
            statusType = ApiTestStatusType.SUCCESS,
            latencyMs = 450,
            modelUsed = "gemini-1.5-flash",
            message = "Connected successfully"
        )
        assertTrue(testResult.isSuccess)
        assertEquals(ApiTestStatusType.SUCCESS, testResult.statusType)
        assertEquals("gemini-1.5-flash", testResult.modelUsed)
    }

    @Test
    fun testEmptyApiKeyConnectionValidation() = kotlinx.coroutines.runBlocking {
        val result = GeminiAiService.testApiKeyConnection("   ")
        org.junit.Assert.assertFalse(result.isSuccess)
        assertEquals(ApiTestStatusType.INVALID_KEY, result.statusType)
    }

    @Test
    fun testExtractDoubleWithVariousNumberFormats() {
        val numPrimitive = kotlinx.serialization.json.JsonPrimitive(1200.50)
        assertEquals(1200.50, GeminiAiService.extractDouble(numPrimitive)!!, 0.001)

        val intPrimitive = kotlinx.serialization.json.JsonPrimitive(500)
        assertEquals(500.0, GeminiAiService.extractDouble(intPrimitive)!!, 0.001)

        val commaString = kotlinx.serialization.json.JsonPrimitive("1,500.00")
        assertEquals(1500.0, GeminiAiService.extractDouble(commaString)!!, 0.001)

        val currencyString = kotlinx.serialization.json.JsonPrimitive("৳ 3,500")
        assertEquals(3500.0, GeminiAiService.extractDouble(currencyString)!!, 0.001)

        val dollarString = kotlinx.serialization.json.JsonPrimitive("$49.99")
        assertEquals(49.99, GeminiAiService.extractDouble(dollarString)!!, 0.001)

        val nullResult = GeminiAiService.extractDouble(null)
        org.junit.Assert.assertNull(nullResult)
    }

    @Test
    fun testExtractIntWithVariousFormats() {
        val intPrimitive = kotlinx.serialization.json.JsonPrimitive(85)
        assertEquals(85, GeminiAiService.extractInt(intPrimitive))

        val strPrimitive = kotlinx.serialization.json.JsonPrimitive("92")
        assertEquals(92, GeminiAiService.extractInt(strPrimitive))

        val spaceStr = kotlinx.serialization.json.JsonPrimitive(" 78 ")
        assertEquals(78, GeminiAiService.extractInt(spaceStr))

        val nullResult = GeminiAiService.extractInt(null)
        org.junit.Assert.assertNull(nullResult)
    }

    @Test
    fun testOfflineAiFallbackPipelines() = kotlinx.coroutines.runBlocking {
        // 1. Financial Insights fallback
        val insights = GeminiAiService.generateFinancialInsights(
            contextSummary = "Income: 50000, Expense: 20000",
            isBangla = true,
            apiKey = null
        )
        assertTrue("Insights should not be empty", insights.isNotEmpty())

        // 3. Budget Recommendations fallback
        val budgets = GeminiAiService.generateBudgetRecommendations(
            contextSummary = "Income: 50000",
            monthlyIncome = 50000.0,
            categorySpends = emptyList(),
            isBangla = true,
            apiKey = null
        )
        assertTrue("Budget recommendations should not be empty", budgets.isNotEmpty())

        // 4. Financial Audit fallback
        val audit = GeminiAiService.generateDeepFinancialAudit(
            contextSummary = "Income: 50000, Expense: 20000",
            periodLabel = "This Month",
            isBangla = true,
            apiKey = null
        )
        assertNotNull(audit)
        assertTrue(audit.healthScore in 10..100)
        assertTrue(audit.actionableTips.isNotEmpty())

        // 5. Payment Reminder fallback
        val reminder = GeminiAiService.generatePaymentReminder(
            personName = "Kamal",
            amount = 1500.0,
            currencySymbol = "৳",
            notes = "Lunch bill",
            isBangla = true,
            apiKey = null
        )
        assertTrue(reminder.contains("Kamal"))
        assertTrue(reminder.contains("1,500") || reminder.contains("1500"))
    }

    @Test
    fun testBudgetCategoryMatchingBothLanguages() {
        val categories = listOf(
            Category(1, "Food & Dining", TransactionType.EXPENSE, "Restaurant", "#F0716B", true),
            Category(2, "Groceries", TransactionType.EXPENSE, "ShoppingCart", "#2EC4B6", true),
            Category(3, "Transportation", TransactionType.EXPENSE, "DirectionsCar", "#3B8FF0", true),
            Category(4, "Bills & Utilities", TransactionType.EXPENSE, "Bolt", "#F0A63D", true),
            Category(5, "Shopping", TransactionType.EXPENSE, "ShoppingBag", "#E056FD", true),
            Category(6, "Health & Care", TransactionType.EXPENSE, "MedicalServices", "#00B4D8", true)
        )

        // Test English direct match
        val foodDirect = BudgetViewModel.findMatchingExpenseCategory("Food & Dining", categories)
        assertNotNull(foodDirect)
        assertEquals(1L, foodDirect!!.id)

        // Test English variations
        val foodAndDining = BudgetViewModel.findMatchingExpenseCategory("Food and Dining", categories)
        assertNotNull(foodAndDining)
        assertEquals(1L, foodAndDining!!.id)

        // Test Bengali full name
        val foodBengali = BudgetViewModel.findMatchingExpenseCategory("খাবার ও রেস্তোরাঁ", categories)
        assertNotNull(foodBengali)
        assertEquals(1L, foodBengali!!.id)

        // Test Bengali shorthand
        val foodShort = BudgetViewModel.findMatchingExpenseCategory("খাবার", categories)
        assertNotNull(foodShort)
        assertEquals(1L, foodShort!!.id)

        // Test Groceries in Bengali
        val groceriesBengali = BudgetViewModel.findMatchingExpenseCategory("বাজার ও সদাই", categories)
        assertNotNull(groceriesBengali)
        assertEquals(2L, groceriesBengali!!.id)

        // Test Transportation in Bengali
        val transportBengali = BudgetViewModel.findMatchingExpenseCategory("যাতায়াত ও ভ্রমণ", categories)
        assertNotNull(transportBengali)
        assertEquals(3L, transportBengali!!.id)

        // Test Health & Care in Bengali
        val healthBengali = BudgetViewModel.findMatchingExpenseCategory("স্বাস্থ্য ও চিকিৎসা", categories)
        assertNotNull(healthBengali)
        assertEquals(6L, healthBengali!!.id)
    }

    @Test
    fun testBudgetMonthConsistency() {
        val cal = Calendar.getInstance()
        val currentMonthZeroIndexed = cal.get(Calendar.MONTH)
        // 0-indexed month should be in range 0..11
        assertTrue(currentMonthZeroIndexed in 0..11)
    }
}
