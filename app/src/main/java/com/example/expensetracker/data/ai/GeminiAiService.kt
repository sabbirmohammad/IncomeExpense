package com.example.expensetracker.data.ai

import android.graphics.Bitmap
import android.util.Log
import com.example.expensetracker.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonElement

object GeminiAiService {

    fun extractDouble(element: JsonElement?): Double? = GeminiAiJsonUtils.extractDouble(element)

    fun extractInt(element: JsonElement?): Int? = GeminiAiJsonUtils.extractInt(element)

    fun extractJsonBlock(text: String): String = GeminiAiJsonUtils.extractJsonBlock(text)

    fun extractJsonArray(text: String): String = GeminiAiJsonUtils.extractJsonArray(text)

    /**
     * Verifies API Key validity and checks server responsiveness with latency measurement.
     */
    suspend fun testApiKeyConnection(apiKey: String): ApiConnectionTestResult = withContext(Dispatchers.IO) {
        val trimmed = apiKey.trim()
        if (trimmed.isBlank()) {
            return@withContext ApiConnectionTestResult(
                isSuccess = false,
                statusType = ApiTestStatusType.INVALID_KEY,
                message = "API Key is empty"
            )
        }

        val startTime = System.currentTimeMillis()
        val modelsToTry = GeminiAiClientHelper.getModelsToTry()
        var lastException: Exception? = null

        for (modelName in modelsToTry) {
            try {
                val model = GeminiAiClientHelper.createModel(modelName, trimmed, temp = 0.1f, maxTokens = 100)
                val response = model.generateContent("Test connection ping. Respond with: OK")
                val text = response.text
                val latency = System.currentTimeMillis() - startTime
                if (!text.isNullOrBlank()) {
                    Log.d("GeminiAiService", "testApiKeyConnection succeeded with $modelName in ${latency}ms")
                    return@withContext ApiConnectionTestResult(
                        isSuccess = true,
                        statusType = ApiTestStatusType.SUCCESS,
                        latencyMs = latency,
                        modelUsed = modelName,
                        message = "Connected to Gemini AI ($modelName)"
                    )
                }
            } catch (e: Exception) {
                lastException = e
                Log.w("GeminiAiService", "testApiKeyConnection failed on $modelName: ${e.message}")
                val msg = e.localizedMessage.orEmpty()
                if (msg.contains("API_KEY_INVALID", ignoreCase = true) ||
                    msg.contains("API key not valid", ignoreCase = true) ||
                    msg.contains("invalid api key", ignoreCase = true) ||
                    msg.contains("403", ignoreCase = true) ||
                    msg.contains("401", ignoreCase = true)) {
                    return@withContext ApiConnectionTestResult(
                        isSuccess = false,
                        statusType = ApiTestStatusType.INVALID_KEY,
                        latencyMs = System.currentTimeMillis() - startTime,
                        message = "Invalid or unauthorized API key"
                    )
                }
                if (msg.contains("RESOURCE_EXHAUSTED", ignoreCase = true) || msg.contains("429", ignoreCase = true)) {
                    return@withContext ApiConnectionTestResult(
                        isSuccess = false,
                        statusType = ApiTestStatusType.QUOTA_EXCEEDED,
                        latencyMs = System.currentTimeMillis() - startTime,
                        message = "Quota limit reached (Rate limit exceeded)"
                    )
                }
            }
        }

        val totalLatency = System.currentTimeMillis() - startTime
        val errMsg = lastException?.localizedMessage ?: "Connection error"
        val statusType = if (errMsg.contains("Unable to resolve host", ignoreCase = true) || errMsg.contains("timeout", ignoreCase = true)) {
            ApiTestStatusType.NETWORK_ERROR
        } else {
            ApiTestStatusType.ERROR
        }

        ApiConnectionTestResult(
            isSuccess = false,
            statusType = statusType,
            latencyMs = totalLatency,
            message = lastException?.message ?: "Failed to connect to Gemini AI"
        )
    }

    suspend fun generateFinancialInsights(
        contextSummary: String,
        isBangla: Boolean,
        apiKey: String?,
        summary: FinancialSummary? = null,
        categorySpends: List<CategorySpend> = emptyList(),
        dailySpends: List<DailySpend> = emptyList(),
        currencySymbol: String = "৳"
    ): List<AiInsightItem> = GeminiAiFinancialAuditor.generateFinancialInsights(
        contextSummary, isBangla, apiKey, summary, categorySpends, dailySpends, currencySymbol
    )

    suspend fun askAdvisor(
        userQuestion: String,
        chatHistory: List<AiChatMessage>,
        contextSummary: String,
        isBangla: Boolean,
        apiKey: String?,
        userName: String = ""
    ): String = GeminiAiAdvisorAndReminders.askAdvisor(
        userQuestion, chatHistory, contextSummary, isBangla, apiKey, userName
    )

    suspend fun generateBudgetRecommendations(
        contextSummary: String,
        monthlyIncome: Double,
        categorySpends: List<CategorySpend>,
        isBangla: Boolean,
        apiKey: String?
    ): List<AiBudgetRecommendation> = GeminiAiFinancialAuditor.generateBudgetRecommendations(
        contextSummary, monthlyIncome, categorySpends, isBangla, apiKey
    )

    suspend fun generateDeepFinancialAudit(
        contextSummary: String,
        periodLabel: String,
        isBangla: Boolean,
        apiKey: String?
    ): AiFinancialAudit = GeminiAiFinancialAuditor.generateDeepFinancialAudit(
        contextSummary, periodLabel, isBangla, apiKey
    )

    suspend fun generatePaymentReminder(
        personName: String,
        amount: Double,
        currencySymbol: String,
        notes: String,
        isBangla: Boolean,
        apiKey: String?
    ): String = GeminiAiAdvisorAndReminders.generatePaymentReminder(
        personName, amount, currencySymbol, notes, isBangla, apiKey
    )

    suspend fun scanReceipt(
        apiKey: String,
        bitmap: Bitmap,
        categories: List<Category>
    ): ScannedReceiptData? = GeminiAiReceiptParser.scanReceipt(
        apiKey, bitmap, categories
    )

    suspend fun generateSmartMarketList(
        prompt: String,
        isBangla: Boolean,
        apiKey: String?
    ): List<MarketItem> = GeminiAiMarketService.generateSmartMarketList(
        prompt, isBangla, apiKey
    )

    suspend fun estimateMarketListPrices(
        items: List<MarketItem>,
        isBangla: Boolean,
        apiKey: String?
    ): MarketCostEstimationResult = GeminiAiMarketService.estimateMarketListPrices(
        items, isBangla, apiKey
    )

    suspend fun parseMarketListWithAi(
        rawText: String,
        isBangla: Boolean,
        apiKey: String?
    ): List<MarketItem> = GeminiAiMarketService.parseMarketListWithAi(
        rawText, isBangla, apiKey
    )

    suspend fun getMarketSmartTips(
        items: List<MarketItem>,
        isBangla: Boolean,
        apiKey: String?
    ): List<String> = GeminiAiMarketService.getMarketSmartTips(
        items, isBangla, apiKey
    )

    suspend fun parseTransactionFromNaturalText(
        input: String,
        categories: List<Category>,
        accounts: List<Account>,
        isBangla: Boolean,
        apiKey: String?
    ): ParsedTransactionResult = GeminiAiReceiptParser.parseTransactionFromNaturalText(
        input, categories, accounts, isBangla, apiKey
    )

    suspend fun analyzeSavingsGoal(
        goalTitle: String,
        targetAmount: Double,
        savedAmount: Double,
        targetDateMillis: Long?,
        monthlyIncome: Double,
        monthlyExpense: Double,
        isBangla: Boolean,
        apiKey: String?
    ): SavingsGoalAiAnalysis = GeminiAiGoalsAndBills.analyzeSavingsGoal(
        goalTitle, targetAmount, savedAmount, targetDateMillis, monthlyIncome, monthlyExpense, isBangla, apiKey
    )

    suspend fun analyzeDebtPortfolio(
        receivables: List<Receivable>,
        isBangla: Boolean,
        apiKey: String?
    ): DebtPortfolioAiAdvice = GeminiAiGoalsAndBills.analyzeDebtPortfolio(
        receivables, isBangla, apiKey
    )

    suspend fun forecastBillCashFlow(
        bills: List<RecurringBill>,
        accounts: List<Account>,
        isBangla: Boolean,
        apiKey: String?
    ): BillCashFlowAiForecast = GeminiAiGoalsAndBills.forecastBillCashFlow(
        bills, accounts, isBangla, apiKey
    )
}
