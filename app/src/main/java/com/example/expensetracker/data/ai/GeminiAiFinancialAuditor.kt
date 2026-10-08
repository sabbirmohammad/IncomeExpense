package com.example.expensetracker.data.ai

import android.util.Log
import com.example.expensetracker.data.model.AiBudgetRecommendation
import com.example.expensetracker.data.model.AiFinancialAudit
import com.example.expensetracker.data.model.AiInsightItem
import com.example.expensetracker.data.model.CategorySpend
import com.example.expensetracker.data.model.DailySpend
import com.example.expensetracker.data.model.FinancialSummary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

object GeminiAiFinancialAuditor {

    suspend fun generateFinancialInsights(
        contextSummary: String,
        isBangla: Boolean,
        apiKey: String?,
        summary: FinancialSummary? = null,
        categorySpends: List<CategorySpend> = emptyList(),
        dailySpends: List<DailySpend> = emptyList(),
        currencySymbol: String = "৳"
    ): List<AiInsightItem> = withContext(Dispatchers.IO) {
        if (apiKey.isNullOrBlank()) {
            return@withContext GeminiAiLocalFinancialFallbacks.generateLocalInsights(
                contextSummary = contextSummary,
                isBangla = isBangla,
                summary = summary,
                categorySpends = categorySpends,
                dailySpends = dailySpends,
                currencySymbol = currencySymbol
            )
        }

        val langInstruction = if (isBangla) "Respond in natural, fluent Bengali (বাংলা)." else "Respond in fluent English."
        val prompt = """
You are a senior, highly analytical personal finance specialist for an individual in Bangladesh.
$langInstruction
Analyze the provided financial summary and extract 2 or 3 sharp, data-driven observations.

Financial Summary:
$contextSummary

Rules:
1. Always cite specific numbers, percentages, or categories from the summary (e.g. "খাবার খাতে মোট ব্যয়ের ৪২%").
2. DO NOT mention "AI", "রোবট", "কৃত্রিম বুদ্ধিমত্তা", or that you are an AI model. Speak as an objective, native financial analyst and advisor.
3. Keep observations practical, actionable, and encouraging.

Format each observation on a separate line starting with one of these tags:
[SAVING] for savings opportunities or positive saving habits
[WARNING] for budget velocity or overspending alerts
[INFO] for category spending distributions and key facts

Keep each observation under 30 words. Do not include asterisks, emojis, bullet points, or numbering.
""".trimIndent()

        val modelsToTry = GeminiAiClientHelper.getModelsToTry()
        for (modelName in modelsToTry) {
            try {
                val model = GeminiAiClientHelper.createModel(modelName, apiKey, temp = 0.2f, maxTokens = 500)
                val response = model.generateContent(prompt)
                val lines = response.text?.lines().orEmpty().filter { it.isNotBlank() }

                val items = mutableListOf<AiInsightItem>()
                for (line in lines) {
                    val cleanLine = line.trim()
                    val stripped = cleanLine.replace(Regex("""^[-*•\d.]+\s*"""), "").trim()
                    val parsed = when {
                        stripped.startsWith("[SAVING]", ignoreCase = true) -> "SAVING" to stripped.substring(8).trim()
                        stripped.startsWith("[WARNING]", ignoreCase = true) -> "WARNING" to stripped.substring(9).trim()
                        stripped.startsWith("[INFO]", ignoreCase = true) -> "INFO" to stripped.substring(6).trim()
                        cleanLine.startsWith("[SAVING]", ignoreCase = true) -> "SAVING" to cleanLine.substring(8).trim()
                        cleanLine.startsWith("[WARNING]", ignoreCase = true) -> "WARNING" to cleanLine.substring(9).trim()
                        cleanLine.startsWith("[INFO]", ignoreCase = true) -> "INFO" to cleanLine.substring(6).trim()
                        else -> null
                    }
                    if (parsed != null && parsed.second.isNotBlank()) {
                        val (type, content) = parsed
                        val title = when (type) {
                            "SAVING" -> if (isBangla) "সঞ্চয় ও উদ্বৃত্ত" else "Savings & Surplus"
                            "WARNING" -> if (isBangla) "বাজেট সতর্কতা" else "Budget Alert"
                            else -> if (isBangla) "ব্যয় পর্যবেক্ষণ" else "Spending Observation"
                        }
                        items.add(AiInsightItem(title = title, description = content, type = type))
                    }
                }

                if (items.isEmpty()) {
                    for (line in lines) {
                        val stripped = line.trim().replace(Regex("""^[-*•\d.]+\s*"""), "").trim()
                        if (stripped.length >= 15 && !stripped.contains("observation", ignoreCase = true) && !stripped.contains("summary", ignoreCase = true)) {
                            items.add(
                                AiInsightItem(
                                    title = if (isBangla) "ব্যয় পর্যবেক্ষণ" else "Spending Observation",
                                    description = stripped,
                                    type = "INFO"
                                )
                            )
                        }
                    }
                }

                if (items.isNotEmpty()) {
                    Log.d("GeminiAiService", "generateFinancialInsights succeeded with $modelName")
                    return@withContext items
                }
            } catch (e: Exception) {
                Log.w("GeminiAiService", "generateFinancialInsights failed on $modelName: ${e.message}")
            }
        }

        GeminiAiLocalFinancialFallbacks.generateLocalInsights(
            contextSummary = contextSummary,
            isBangla = isBangla,
            summary = summary,
            categorySpends = categorySpends,
            dailySpends = dailySpends,
            currencySymbol = currencySymbol
        )
    }

    suspend fun generateBudgetRecommendations(
        contextSummary: String,
        monthlyIncome: Double,
        categorySpends: List<CategorySpend>,
        isBangla: Boolean,
        apiKey: String?
    ): List<AiBudgetRecommendation> = withContext(Dispatchers.IO) {
        val effectiveIncome = if (monthlyIncome > 0) monthlyIncome else {
            val totalExpense = categorySpends.sumOf { it.totalAmount }
            if (totalExpense > 0) totalExpense * 1.25 else 30000.0
        }

        if (apiKey.isNullOrBlank()) {
            return@withContext GeminiAiLocalFinancialFallbacks.generateLocalBudgetRecommendations(effectiveIncome, categorySpends, isBangla)
        }

        val prompt = """
You are a senior personal budgeting specialist in Bangladesh.
User Monthly Income / Inflow: ৳$effectiveIncome
Current Spending Context:
$contextSummary

Generate realistic, prudent monthly budget recommendations for major categories following the 50/30/20 budget framework adapted for Bangladesh.
Return ONLY a valid JSON array of objects with NO markdown quotes or explanation:
[
  {
    "categoryName": "Food & Dining",
    "recommendedAmount": 5000.0,
    "reason": "${if (isBangla) "মাসিক আয়ের ১০-১৫% এর মধ্যে সীমাবদ্ধ রাখার পরামর্শ" else "Recommended within 10-15% of income"}"
  }
]
Categories should cover: Food & Dining, Groceries, Transportation, Bills & Utilities, Shopping, Health & Care, Entertainment.
""".trimIndent()

        val modelsToTry = GeminiAiClientHelper.getModelsToTry()
        for (modelName in modelsToTry) {
            try {
                val model = GeminiAiClientHelper.createModel(modelName, apiKey, temp = 0.1f, maxTokens = 700)
                val response = model.generateContent(prompt)
                val cleanJson = GeminiAiJsonUtils.extractJsonArray(response.text.orEmpty())

                if (cleanJson.isNotBlank()) {
                    val normalized = GeminiAiJsonUtils.normalizeBengaliDigits(cleanJson)
                    val jsonArr = GeminiAiClientHelper.jsonParser.parseToJsonElement(normalized).jsonArray
                    val results = mutableListOf<AiBudgetRecommendation>()

                    for (elem in jsonArr) {
                        val obj = elem.jsonObject
                        val catName = obj["categoryName"]?.jsonPrimitive?.content ?: continue
                        val recAmt = GeminiAiJsonUtils.extractDouble(obj["recommendedAmount"]) ?: continue
                        val reason = obj["reason"]?.jsonPrimitive?.content ?: ""
                        val current = categorySpends.find { it.categoryName.equals(catName, ignoreCase = true) }?.totalAmount ?: 0.0

                        if (recAmt > 0) {
                            results.add(
                                AiBudgetRecommendation(
                                    categoryName = catName,
                                    recommendedAmount = recAmt,
                                    currentSpend = current,
                                    reason = reason
                                )
                            )
                        }
                    }

                    if (results.isNotEmpty()) {
                        Log.d("GeminiAiService", "generateBudgetRecommendations succeeded with $modelName")
                        return@withContext results
                    }
                }
            } catch (e: Exception) {
                Log.w("GeminiAiService", "generateBudgetRecommendations failed on $modelName: ${e.message}")
            }
        }

        GeminiAiLocalFinancialFallbacks.generateLocalBudgetRecommendations(effectiveIncome, categorySpends, isBangla)
    }

    suspend fun generateDeepFinancialAudit(
        contextSummary: String,
        periodLabel: String,
        isBangla: Boolean,
        apiKey: String?
    ): AiFinancialAudit = GeminiAiAuditService.generateDeepFinancialAudit(
        contextSummary, periodLabel, isBangla, apiKey
    )
}
