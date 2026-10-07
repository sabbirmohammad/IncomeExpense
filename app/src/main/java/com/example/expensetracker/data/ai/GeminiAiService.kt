package com.example.expensetracker.data.ai

import com.example.expensetracker.data.model.AiBudgetRecommendation
import com.example.expensetracker.data.model.AiChatMessage
import com.example.expensetracker.data.model.AiFinancialAudit
import com.example.expensetracker.data.model.AiInsightItem
import com.example.expensetracker.data.model.ApiConnectionTestResult
import com.example.expensetracker.data.model.ApiTestStatusType
import com.example.expensetracker.data.model.CategorySpend
import com.example.expensetracker.data.model.DailySpend
import com.example.expensetracker.data.model.FinancialSummary
import com.example.expensetracker.data.model.PaymentMethod
import com.example.expensetracker.data.model.TransactionType
import com.example.expensetracker.ui.components.formatCurrency
import android.graphics.Bitmap
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.data.model.ScannedReceiptData
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import android.util.Log
import kotlinx.serialization.json.jsonPrimitive
import java.util.Locale

object GeminiAiService {

    private const val PRIMARY_MODEL = "gemini-3.5-flash-lite"
    private const val FALLBACK_MODEL = "gemini-3.1-flash-lite"
    private const val TERTIARY_MODEL = "gemini-3.6-flash"

    private fun getModelsToTry(): List<String> = listOf("gemini-2.0-flash", "gemini-1.5-flash", PRIMARY_MODEL, FALLBACK_MODEL, TERTIARY_MODEL, "gemini-3.5-flash")

    private val jsonParser = Json { ignoreUnknownKeys = true; isLenient = true }

    fun extractDouble(element: kotlinx.serialization.json.JsonElement?): Double? {
        if (element == null) return null
        return try {
            val primitive = element.jsonPrimitive
            primitive.doubleOrNull ?: run {
                val clean = primitive.content.replace(Regex("""[^0-9.]"""), "").trim()
                clean.toDoubleOrNull()
            }
        } catch (_: Exception) {
            null
        }
    }

    fun extractInt(element: kotlinx.serialization.json.JsonElement?): Int? {
        if (element == null) return null
        return try {
            val primitive = element.jsonPrimitive
            primitive.intOrNull ?: run {
                val clean = primitive.content.replace(Regex("""[^0-9]"""), "").trim()
                clean.toIntOrNull()
            }
        } catch (_: Exception) {
            null
        }
    }

    private fun createModel(modelName: String, apiKey: String, temp: Float = 0.15f, maxTokens: Int? = null): GenerativeModel {
        return GenerativeModel(
            modelName = modelName,
            apiKey = apiKey.trim(),
            generationConfig = generationConfig {
                temperature = temp
                if (maxTokens != null) {
                    maxOutputTokens = maxTokens
                }
            }
        )
    }

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
        val modelsToTry = getModelsToTry()
        var lastException: Exception? = null

        for (modelName in modelsToTry) {
            try {
                val model = createModel(modelName, trimmed, temp = 0.1f, maxTokens = 100)
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
            message = errMsg
        )
    }


    /**
     * Generates personalized, intelligent financial insights based on the user's spending context.
     */
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
            return@withContext generateLocalInsights(
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

        val modelsToTry = getModelsToTry()
        for (modelName in modelsToTry) {
            try {
                val model = createModel(modelName, apiKey, temp = 0.2f, maxTokens = 500)
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

        generateLocalInsights(
            contextSummary = contextSummary,
            isBangla = isBangla,
            summary = summary,
            categorySpends = categorySpends,
            dailySpends = dailySpends,
            currencySymbol = currencySymbol
        )
    }

    /**
     * Interactive financial advisor chat with conversational memory, deep personalization, and professional formatting.
     */
    suspend fun askAdvisor(
        userQuestion: String,
        chatHistory: List<AiChatMessage>,
        contextSummary: String,
        isBangla: Boolean,
        apiKey: String?,
        userName: String = ""
    ): String = withContext(Dispatchers.IO) {
        if (apiKey.isNullOrBlank()) {
            return@withContext if (isBangla) {
                "আর্থিক পরামর্শকের সাথে আলোচনা করতে সেটিংস স্ক্রিন থেকে একটি ফ্রি Google Gemini API Key যোগ করুন (Google AI Studio থেকে ১ মিনিটে সম্পূর্ণ ফ্রিতে নেওয়া যায়)।"
            } else {
                "To chat with the Financial Advisor, please configure a free Gemini API Key in Settings (available for free from Google AI Studio in 1 minute)."
            }
        }

        val langInstruction = if (isBangla) "Always answer in fluent, respectful, and friendly Bengali (বাংলা)." else "Answer in fluent English."

        val historyContext = buildString {
            chatHistory.takeLast(6).forEach { msg ->
                if (msg.isFromUser) appendLine("User: ${msg.text}") else appendLine("Advisor: ${msg.text}")
            }
        }

        val userAddressingInstruction = if (userName.isNotBlank()) {
            if (isBangla) {
                "The user's exact name is '$userName'. Always preserve this EXACT casing (if lowercase keep lowercase, if uppercase keep uppercase) and address them warmly as '$userName ভাই' or '$userName'."
            } else {
                "The user's exact name is '$userName'. Always preserve this EXACT casing and address them warmly by this exact name."
            }
        } else ""

        val prompt = """
You are 'FinAI' (AI আর্থিক পরামর্শক / Smart Personal Financial Advisor) inside an Expense Tracker Android app.
$langInstruction
$userAddressingInstruction
You provide executive, highly concise, and practical personal finance advice tailored to life in Bangladesh (BDT / টাকা, bKash, Nagad, living expenses).

Current User Financial Context:
$contextSummary

Previous Conversation:
$historyContext

User's Latest Question:
"$userQuestion"

CRITICAL GUIDELINES FOR SHORT & VISUAL RESPONSES:
1. BREVITY (VERY IMPORTANT): Keep the total answer SHORT (under 100-120 words). Crisp, minimalist, executive tone.
2. VISUAL CHARTS: Whenever discussing category breakdown or percentages, ALWAYS use this visual chart tag on a separate line (this renders an interactive colored bar chart widget):
   [Chart: খাত ১: ৭০%, খাত ২: ২০%, খাত ৩: ১০%]
   IMPORTANT FOR CHARTS:
   - Use short category names (1-2 words).
   - NEVER output raw ASCII text charts (e.g. '████') or markdown tables (|...|), as they wrap and break on mobile screens. ALWAYS use the [Chart: ...] tag above!
3. VISUAL METRICS: Whenever summarizing income, expense, or balance, include this metric tag on a separate line (this renders clean KPI cards):
   [Metrics: আয়: ৳... | ব্যয়: ৳... | নিট: ৳...]
4. ACTION TIPS: Provide 2-3 short, bullet points (•) with clear emojis (💡, ⚠️, 🎯) and bold numbers (**৳...**).
5. EXACT NUMBERS: Use the exact figures from the financial context above.
""".trimIndent()

        val modelsToTry = getModelsToTry()
        for (modelName in modelsToTry) {
            try {
                val model = createModel(modelName, apiKey, temp = 0.3f, maxTokens = 800)
                val response = model.generateContent(prompt)
                val text = response.text?.trim()
                if (!text.isNullOrBlank()) {
                    Log.d("GeminiAiService", "askAdvisor succeeded with $modelName")
                    return@withContext text
                }
            } catch (e: Exception) {
                Log.w("GeminiAiService", "askAdvisor error on $modelName: ${e.message}")
            }
        }

        if (isBangla) {
            "ইন্টারনেট বা API সংযোগে সমস্যা হয়েছে। অনুগ্রহ করে আপনার ইন্টারনেট কানেকশন বা API Key যাচাই করুন।"
        } else {
            "Unable to connect to service. Please check your internet connection or API Key."
        }
    }

    /**
     * Generates smart, data-driven budget recommendations per category adhering to 50/30/20 financial rules.
     */
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
            return@withContext generateLocalBudgetRecommendations(effectiveIncome, categorySpends, isBangla)
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

        val modelsToTry = getModelsToTry()
        for (modelName in modelsToTry) {
            try {
                val model = createModel(modelName, apiKey, temp = 0.1f, maxTokens = 700)
                val response = model.generateContent(prompt)
                val cleanJson = extractJsonArray(response.text.orEmpty())

                if (cleanJson.isNotBlank()) {
                    val normalized = normalizeBengaliDigits(cleanJson)
                    val jsonArr = jsonParser.parseToJsonElement(normalized).jsonArray
                    val results = mutableListOf<AiBudgetRecommendation>()

                    for (elem in jsonArr) {
                        val obj = elem.jsonObject
                        val catName = obj["categoryName"]?.jsonPrimitive?.content ?: continue
                        val recAmt = extractDouble(obj["recommendedAmount"]) ?: continue
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

        generateLocalBudgetRecommendations(effectiveIncome, categorySpends, isBangla)
    }

    /**
     * Conducts a comprehensive AI Financial Health Audit with score, leak detection, and cost-cutting steps.
     */
    suspend fun generateDeepFinancialAudit(
        contextSummary: String,
        periodLabel: String,
        isBangla: Boolean,
        apiKey: String?
    ): AiFinancialAudit = withContext(Dispatchers.IO) {
        if (apiKey.isNullOrBlank()) {
            return@withContext generateLocalFinancialAudit(isBangla)
        }

        val prompt = """
You are a chief financial auditor for personal finances in Bangladesh.
Analyze the user's financial statement for the period '$periodLabel':
$contextSummary

Evaluate financial health and return ONLY a valid JSON object with NO markdown formatting:
{
  "healthScore": 82,
  "statusGrade": "${if (isBangla) "ভারসাম্যপূর্ণ" else "Healthy"}",
  "summaryEvaluation": "1 crisp sentence summarizing overall financial health.",
  "detectedLeaksOrSpikes": [
    "Short punchy leak (max 5-7 words, e.g. 'রেস্তোরাঁর অনিয়মিত খরচ বৃদ্ধি')",
    "Observation 2"
  ],
  "actionableTips": [
    "Short punchy tip (max 5-8 words, e.g. 'বাইরের খাবার নিয়ন্ত্রণ: ২০০০৳ সাশ্রয়')",
    "Actionable step 2",
    "Actionable step 3"
  ]
}
Language: ${if (isBangla) "Bengali (বাংলা)" else "English"}.
CRITICAL: Keep summaryEvaluation to 1 concise sentence. Keep detectedLeaksOrSpikes and actionableTips short, punchy (5-8 words max each), never verbose paragraphs.
""".trimIndent()

        val modelsToTry = getModelsToTry()
        for (modelName in modelsToTry) {
            try {
                val model = createModel(modelName, apiKey, temp = 0.2f, maxTokens = 700)
                val response = model.generateContent(prompt)
                val cleanJson = extractJsonBlock(response.text.orEmpty())

                if (cleanJson.isNotBlank()) {
                    val normalized = normalizeBengaliDigits(cleanJson)
                    val obj = jsonParser.parseToJsonElement(normalized).jsonObject
                    val score = (extractInt(obj["healthScore"]) ?: 75).coerceIn(10, 100)
                    val grade = obj["statusGrade"]?.jsonPrimitive?.content ?: if (isBangla) "ভারসাম্যপূর্ণ" else "Healthy"
                    val summary = obj["summaryEvaluation"]?.jsonPrimitive?.content ?: ""
                    val leaks = obj["detectedLeaksOrSpikes"]?.jsonArray?.mapNotNull { it.jsonPrimitive.content } ?: emptyList()
                    val tips = obj["actionableTips"]?.jsonArray?.mapNotNull { it.jsonPrimitive.content } ?: emptyList()

                    Log.d("GeminiAiService", "generateDeepFinancialAudit succeeded with $modelName")
                    return@withContext AiFinancialAudit(
                        healthScore = score,
                        statusGrade = grade,
                        summaryEvaluation = summary,
                        detectedLeaksOrSpikes = leaks,
                        actionableTips = tips
                    )
                }
            } catch (e: Exception) {
                Log.w("GeminiAiService", "generateDeepFinancialAudit failed on $modelName: ${e.message}")
            }
        }

        generateLocalFinancialAudit(isBangla)
    }

    /**
     * Generates a culturally respectful, polite debt/payment reminder for a friend or debtor.
     */
    suspend fun generatePaymentReminder(
        personName: String,
        amount: Double,
        currencySymbol: String,
        notes: String,
        isBangla: Boolean,
        apiKey: String?
    ): String = withContext(Dispatchers.IO) {
        val formattedAmt = formatCurrency(amount, currencySymbol)
        val defaultMessage = if (isBangla) {
            "আসসালামু আলাইকুম $personName, আশা করি ভালো আছো। আমাদের হিসাবের $formattedAmt টাকার বকেয়াটি স্মরণ করিয়ে দিলাম${if (notes.isNotBlank()) " ($notes)" else ""}। সুবিধা অনুযায়ী বিকাশ/নগদে পাঠিয়ে দিলে উপকৃত হব। ধন্যবাদ!"
        } else {
            "Hi $personName, hope you're doing well! Just a friendly reminder regarding the pending $formattedAmt${if (notes.isNotBlank()) " ($notes)" else ""}. Please send it over via bKash or bank when convenient. Thank you!"
        }

        if (apiKey.isNullOrBlank()) return@withContext defaultMessage

        val prompt = """
Write a friendly, polite, and culturally natural payment reminder message for a friend/colleague named '$personName'.
Amount owed: $formattedAmt
Reason/Notes: "$notes"
Language: ${if (isBangla) "Bengali (বাংলা) in friendly, casual Bangladeshi tone" else "English"}
Length: 2-3 sentences.
Do not include subject lines or greetings like 'Subject:'. Just the message text.
""".trimIndent()

        val modelsToTry = getModelsToTry()
        for (modelName in modelsToTry) {
            try {
                val model = createModel(modelName, apiKey, temp = 0.3f, maxTokens = 250)
                val response = model.generateContent(prompt)
                val text = response.text?.trim()
                if (!text.isNullOrBlank()) {
                    Log.d("GeminiAiService", "generatePaymentReminder succeeded with $modelName")
                    return@withContext text
                }
            } catch (e: Exception) {
                Log.w("GeminiAiService", "generatePaymentReminder failed on $modelName: ${e.message}")
            }
        }

        defaultMessage
    }

    private fun generateLocalBudgetRecommendations(
        income: Double,
        categorySpends: List<CategorySpend>,
        isBangla: Boolean
    ): List<AiBudgetRecommendation> {
        val allocations = listOf(
            Triple("Food & Dining", 0.12, if (isBangla) "মাসিক আয়ের ১০-১২% এর মধ্যে রাখা বাঞ্ছনীয়" else "Ideal within 10-12% of income"),
            Triple("Groceries", 0.20, if (isBangla) "জরুরি বাজার সদাইয়ে ২০% যুক্তিসঙ্গত সীমা" else "Reasonable grocery allocation at 20%"),
            Triple("Bills & Utilities", 0.10, if (isBangla) "বিদ্যুৎ, গ্যাস ও নিয়মিত ইউটিলিটি বিল" else "Electricity, gas & monthly utilities"),
            Triple("Transportation", 0.08, if (isBangla) "দৈনিক রিকশা, বাস ও যাতায়াত ব্যয়" else "Daily commute and travel expenses"),
            Triple("Shopping", 0.06, if (isBangla) "পোশাক ও ব্যক্তিগত কেনাকাটা নিয়ন্ত্রণ" else "Discretionary clothing & lifestyle shopping"),
            Triple("Health & Care", 0.05, if (isBangla) "ওষুধ ও জরুরি স্বাস্থ্য সেবা ফান্ড" else "Healthcare, medicine & emergency buffer")
        )

        return allocations.map { (cat, pct, reason) ->
            val rec = kotlin.math.round((income * pct) / 100.0) * 100.0
            val current = categorySpends.find { it.categoryName.equals(cat, ignoreCase = true) }?.totalAmount ?: 0.0
            AiBudgetRecommendation(
                categoryName = cat,
                recommendedAmount = rec.coerceAtLeast(1000.0),
                currentSpend = current,
                reason = reason
            )
        }
    }

    private fun generateLocalFinancialAudit(isBangla: Boolean): AiFinancialAudit {
        return if (isBangla) {
            AiFinancialAudit(
                healthScore = 80,
                statusGrade = "সন্তোষজনক",
                summaryEvaluation = "আপনার আয় ও ব্যয়ের অনুপাত স্থিতিশীল ও নিয়ন্ত্রণে রয়েছে।",
                detectedLeaksOrSpikes = listOf(
                    "রেস্তোরাঁ ও বাইরের খাবারে অতিরিক্ত খরচ",
                    "ছোট ছোট অনলাইন কেনাকাটার স্পাইক"
                ),
                actionableTips = listOf(
                    "বাইরের খাবারে লাগাম টেনে ২০০০৳ সাশ্রয়",
                    "মাস শুরুর প্রথমেই ২০% অর্থ সঞ্চয়",
                    "নগদ/বিকাশ ক্যাশআউট চার্জ নিয়ন্ত্রণ"
                )
            )
        } else {
            AiFinancialAudit(
                healthScore = 80,
                statusGrade = "Satisfactory",
                summaryEvaluation = "Your income-to-expense ratio is stable and well controlled.",
                detectedLeaksOrSpikes = listOf(
                    "Frequent food delivery expenditure spikes",
                    "Accumulating micro digital payment fees"
                ),
                actionableTips = listOf(
                    "Cap dining out to save ৳2,000 this month",
                    "Allocate 20% savings right on payday",
                    "Review recurring subscriptions"
                )
            )
        }
    }

    private fun generateLocalInsights(
        contextSummary: String,
        isBangla: Boolean,
        summary: FinancialSummary? = null,
        categorySpends: List<CategorySpend> = emptyList(),
        dailySpends: List<DailySpend> = emptyList(),
        currencySymbol: String = "৳"
    ): List<AiInsightItem> {
        val list = mutableListOf<AiInsightItem>()

        // 1. Top Expense Category Insight
        val topCategory = categorySpends.filter { !it.categoryName.equals("Loan & Debt Given", ignoreCase = true) }
            .maxByOrNull { it.totalAmount }
        if (topCategory != null && (summary?.totalExpense ?: 0.0) > 0) {
            val totalExpense = summary?.totalExpense ?: 1.0
            val pct = ((topCategory.totalAmount / totalExpense) * 100).toInt().coerceIn(1, 100)
            val formattedAmt = formatCurrency(topCategory.totalAmount, currencySymbol)
            val desc = if (isBangla) {
                "চলতি সময়ে আপনার সর্বোচ্চ ব্যয় হয়েছে '${topCategory.categoryName}' খাতে ($formattedAmt, মোট খরচের $pct%)। এই খাতে সচেতন বাজেট বজায় রাখলে বড় সঞ্চয় সম্ভব।"
            } else {
                "Your highest spending is in '${topCategory.categoryName}' ($formattedAmt, $pct% of total expenses). Keeping this sector optimized directly protects savings."
            }
            list.add(
                AiInsightItem(
                    title = if (isBangla) "প্রধান খরচের খাত" else "Top Expense Driver",
                    description = desc,
                    type = "INFO"
                )
            )
        }

        // 2. Budget Pace & Velocity Insight
        if (summary != null && summary.budgetAmount > 0) {
            val calendar = java.util.Calendar.getInstance()
            val dayOfMonth = calendar.get(java.util.Calendar.DAY_OF_MONTH)
            val daysInMonth = calendar.getActualMaximum(java.util.Calendar.DAY_OF_MONTH)
            val monthProgress = ((dayOfMonth.toFloat() / daysInMonth.toFloat()) * 100).toInt()
            val budgetSpentPct = (summary.budgetPercent * 100).toInt()

            if (budgetSpentPct > monthProgress + 10 || summary.budgetOverspent > 0) {
                val desc = if (isBangla) {
                    "মাসের $monthProgress% সময়ে বাজেটের $budgetSpentPct% ব্যয় হয়ে গেছে। মাসের শেষ দিনগুলোতে ঘাটতি এড়াতে দৈনিক ব্যয়ের গতি কিছুটা শ্লথ রাখা প্রয়োজন।"
                } else {
                    "At $monthProgress% of the month elapsed, $budgetSpentPct% of your budget is consumed. Moderating daily spend will prevent month-end deficits."
                }
                list.add(
                    AiInsightItem(
                        title = if (isBangla) "বাজেট পেস সতর্কতা" else "Budget Pace Alert",
                        description = desc,
                        type = "WARNING"
                    )
                )
            } else {
                val remaining = formatCurrency(summary.budgetRemaining, currencySymbol)
                val desc = if (isBangla) {
                    "বাজেট ব্যবহারের গতি স্বাভাবিক ও নিয়ন্ত্রণে রয়েছে। অবশিষ্ট $remaining দিয়ে মাসের বাকি দিনগুলো পরিকল্পিতভাবে ব্যয় করুন।"
                } else {
                    "Your spending velocity is healthy and well-balanced. You have $remaining remaining for the rest of the month."
                }
                list.add(
                    AiInsightItem(
                        title = if (isBangla) "বাজেট নিয়ন্ত্রণ স্বাভাবিক" else "Budget On Track",
                        description = desc,
                        type = "SAVING"
                    )
                )
            }
        }

        // 3. Savings Rate / Surplus Insight
        if (summary != null && summary.totalIncome > 0) {
            val savingsRate = (((summary.totalIncome - summary.totalExpense) / summary.totalIncome) * 100).toInt()
            if (savingsRate >= 15) {
                val net = formatCurrency(summary.netBalance.coerceAtLeast(0.0), currencySymbol)
                val desc = if (isBangla) {
                    "দারুণ অগ্রগতি! বর্তমান সময়ে আয়ের $savingsRate% নিট সঞ্চয় হিসেবে রয়েছে ($net)। এই উদ্বৃত্ত অর্থ জরুরি তহবিল বা লাভজনক ডিপিএসে রাখতে পারেন।"
                } else {
                    "Strong financial health! You are retaining $savingsRate% of your income ($net) as net surplus."
                }
                list.add(
                    AiInsightItem(
                        title = if (isBangla) "ইতিবাচক সঞ্চয় হার" else "Healthy Savings Rate",
                        description = desc,
                        type = "SAVING"
                    )
                )
            } else if (savingsRate < 0) {
                val deficit = formatCurrency(summary.totalExpense - summary.totalIncome, currencySymbol)
                val desc = if (isBangla) {
                    "চলতি সময়ে মোট ব্যয় আয়কে $deficit অতিক্রম করেছে। ঘাটতি কমাতে তাৎক্ষণিকভাবে অনাবশ্যক খরচ পর্যালোচনা করুন।"
                } else {
                    "Expenses exceed income by $deficit this period. Review discretionary spending to balance cash flow."
                }
                list.add(
                    AiInsightItem(
                        title = if (isBangla) "ঘাটতি ব্যয় সতর্কতা" else "Deficit Spending Alert",
                        description = desc,
                        type = "WARNING"
                    )
                )
            }
        }

        // 4. Daily Average Spend (if fewer than 2 items)
        if (list.size < 2 && dailySpends.isNotEmpty()) {
            val validDays = dailySpends.filter { it.expense > 0 }
            if (validDays.isNotEmpty()) {
                val avg = validDays.map { it.expense }.average()
                val formattedAvg = formatCurrency(avg, currencySymbol)
                val desc = if (isBangla) {
                    "গত ৭ দিনে আপনার গড় দৈনিক খরচ হয়েছে $formattedAvg। দৈনিক খরচ এই সীমার মধ্যে বজায় রাখলে মাসিক হিসাব সুশৃঙ্খল থাকবে।"
                } else {
                    "Your 7-day average daily spend is $formattedAvg. Maintaining daily spends within this band ensures financial discipline."
                }
                list.add(
                    AiInsightItem(
                        title = if (isBangla) "দৈনিক গড় ব্যয়" else "Daily Spending Pace",
                        description = desc,
                        type = "INFO"
                    )
                )
            }
        }

        if (list.isEmpty()) {
            if (isBangla) {
                list.add(
                    AiInsightItem(
                        title = "বাজেট ও খরচ ট্র্যাকিং",
                        description = "প্রতিদিনের সব আয়-ব্যয় ও বিল নিয়মমাফিক রেকর্ড করলে আর্থিক লক্ষ্য অর্জন এবং নিয়মিত সঞ্চয় সহজ হয়।",
                        type = "SAVING"
                    )
                )
                list.add(
                    AiInsightItem(
                        title = "ক্যাশলেস ও ওয়ালেট ব্যবস্থাপনা",
                        description = "বিকাশ, নগদ বা ব্যাংক অ্যাকাউন্টের লেনদেন নিয়মিত হালনাগাদ রাখলে রিয়েল-টাইম ব্যালেন্স নিখুঁত থাকে।",
                        type = "INFO"
                    )
                )
            } else {
                list.add(
                    AiInsightItem(
                        title = "Budget Discipline",
                        description = "Logging daily income, expenses, and recurring bills helps you stay organized and hit savings targets.",
                        type = "SAVING"
                    )
                )
                list.add(
                    AiInsightItem(
                        title = "Wallet Balances",
                        description = "Keeping your MFS and bank accounts reconciled provides accurate real-time cash flow visibility.",
                        type = "INFO"
                    )
                )
            }
        }

        return list
    }

    fun extractJsonBlock(text: String): String {
        val trimmed = text.trim()
            .removePrefix("```json")
            .removePrefix("```")
            .removeSuffix("```")
            .trim()
        if (trimmed.startsWith("{") && trimmed.endsWith("}")) {
            return trimmed
        }
        val start = trimmed.indexOf('{')
        val end = trimmed.lastIndexOf('}')
        return if (start != -1 && end != -1 && end > start) {
            trimmed.substring(start, end + 1)
        } else {
            ""
        }
    }

    fun extractJsonArray(text: String): String {
        val trimmed = text.trim()
            .removePrefix("```json")
            .removePrefix("```")
            .removeSuffix("```")
            .trim()
        if (trimmed.startsWith("[") && trimmed.endsWith("]")) {
            return trimmed
        }
        val start = trimmed.indexOf('[')
        val end = trimmed.lastIndexOf(']')
        return if (start != -1 && end != -1 && end > start) {
            trimmed.substring(start, end + 1)
        } else {
            ""
        }
    }

    private fun normalizeBengaliDigits(input: String): String {
        val banglaDigits = "০১২৩৪৫৬৭৮৯"
        val engDigits = "0123456789"
        var res = input
        for (i in 0..9) {
            res = res.replace(banglaDigits[i], engDigits[i])
        }
        return res
    }

    /**
     * Scans a receipt/voucher image and extracts structured transaction fields via Gemini AI.
     */
    suspend fun scanReceipt(
        apiKey: String,
        bitmap: Bitmap,
        categories: List<Category>
    ): ScannedReceiptData? = withContext(Dispatchers.IO) {
        val trimmedKey = apiKey.trim()
        if (trimmedKey.isBlank()) return@withContext null

        val categoryListStr = categories.joinToString(", ") { it.name }
        val prompt = """
You are an expert financial OCR assistant for an Expense Tracker app.
Analyze this image of a receipt, cash memo, invoice, or voucher.
Extract transaction details in strict JSON format:
{
  "amount": <numeric total amount, e.g. 250.0, or null if unreadable>,
  "title": "<merchant, vendor, store, or main item description, max 35 characters>",
  "categoryName": "<choose the best matching category from this list: [$categoryListStr], or null>",
  "date": "<date in YYYY-MM-DD format if visible, otherwise null>",
  "notes": "<short list of main items or invoice number, max 100 characters>",
  "paymentMethod": "<one of CASH, BANK_TRANSFER, CREDIT_CARD, DEBIT_CARD, BKASH, NAGAD, OTHER, or null>"
}
IMPORTANT: Return ONLY the raw JSON object. Do not include markdown code blocks or any other text.
""".trimIndent()

        val modelsToTry = getModelsToTry()
        for (modelName in modelsToTry) {
            try {
                val model = createModel(modelName, trimmedKey, temp = 0.1f, maxTokens = 500)
                val inputContent = content {
                    image(bitmap)
                    text(prompt)
                }
                val response = model.generateContent(inputContent)
                val rawText = response.text?.trim()
                if (!rawText.isNullOrBlank()) {
                    Log.d("GeminiAiService", "scanReceipt response from $modelName: $rawText")
                    val cleanJson = rawText.removePrefix("```json").removePrefix("```").removeSuffix("```").trim()
                    val json = jsonParser.parseToJsonElement(cleanJson).jsonObject

                    val amount = extractDouble(json["amount"])
                    val title = json["title"]?.jsonPrimitive?.content?.takeIf { it.isNotBlank() && it != "null" }
                    val catName = json["categoryName"]?.jsonPrimitive?.content?.takeIf { it.isNotBlank() && it != "null" }
                    val dateStr = json["date"]?.jsonPrimitive?.content?.takeIf { it.isNotBlank() && it != "null" }
                    val notes = json["notes"]?.jsonPrimitive?.content?.takeIf { it.isNotBlank() && it != "null" }
                    val methodStr = json["paymentMethod"]?.jsonPrimitive?.content?.takeIf { it.isNotBlank() && it != "null" }

                    val paymentMethod = methodStr?.let {
                        try {
                            PaymentMethod.valueOf(it.uppercase())
                        } catch (_: Exception) {
                            null
                        }
                    }

                    val dateMillis = dateStr?.let {
                        try {
                            java.text.SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(it)?.time
                        } catch (_: Exception) {
                            null
                        }
                    }

                    return@withContext ScannedReceiptData(
                        amount = amount,
                        title = title,
                        categoryName = catName,
                        dateMillis = dateMillis,
                        notes = notes,
                        paymentMethod = paymentMethod
                    )
                }
            } catch (e: Exception) {
                Log.w("GeminiAiService", "scanReceipt failed with $modelName: ${e.message}")
            }
        }

        null
    }

    /**
     * AI Smart Bazar List Generator: Generates an itemized market checklist based on user prompt or presets.
     */
    suspend fun generateSmartMarketList(
        prompt: String,
        isBangla: Boolean,
        apiKey: String?
    ): List<com.example.expensetracker.data.model.MarketItem> = withContext(Dispatchers.IO) {
        val trimmedPrompt = prompt.trim()
        val trimmedKey = apiKey?.trim()

        if (!trimmedKey.isNullOrBlank()) {
            val modelsToTry = getModelsToTry()
            val aiPrompt = """
You are a grocery and bazar planning specialist for households in Bangladesh.
The user wants a grocery list for: "$trimmedPrompt"

Generate a realistic, itemized list of items with proper quantities, units (one of কেজি, গ্রাম, লিটার, পিস, ডজন, হালি, আঁটি, প্যাকেট, বোতল), and estimated unit prices in BDT (৳).
Return ONLY a valid JSON array of objects with NO markdown formatting:
[
  {
    "name": "${if (isBangla) "আলু" else "Potato"}",
    "quantity": 2.0,
    "unit": "${if (isBangla) "কেজি" else "kg"}",
    "estimatedPrice": 110.0
  }
]
Language: ${if (isBangla) "Bengali (বাংলা)" else "English"}.
Keep items practical and standard for Bangladesh.
""".trimIndent()

            for (modelName in modelsToTry) {
                try {
                    val model = createModel(modelName, trimmedKey, temp = 0.2f, maxTokens = 800)
                    val response = model.generateContent(aiPrompt)
                    val cleanJson = extractJsonArray(response.text.orEmpty())

                    if (cleanJson.isNotBlank()) {
                        val normalized = normalizeBengaliDigits(cleanJson)
                        val jsonArr = jsonParser.parseToJsonElement(normalized).jsonArray
                        val items = mutableListOf<com.example.expensetracker.data.model.MarketItem>()

                        for (elem in jsonArr) {
                            val obj = elem.jsonObject
                            val name = obj["name"]?.jsonPrimitive?.content ?: continue
                            val qty = extractDouble(obj["quantity"]) ?: 1.0
                            val unit = obj["unit"]?.jsonPrimitive?.content ?: "কেজি"
                            val price = extractDouble(obj["estimatedPrice"])

                            items.add(
                                com.example.expensetracker.data.model.MarketItem(
                                    name = name,
                                    quantity = qty,
                                    unit = unit,
                                    amount = price
                                )
                            )
                        }

                        if (items.isNotEmpty()) {
                            Log.d("GeminiAiService", "generateSmartMarketList succeeded with $modelName (${items.size} items)")
                            return@withContext items
                        }
                    }
                } catch (e: Exception) {
                    Log.w("GeminiAiService", "generateSmartMarketList failed on $modelName: ${e.message}")
                }
            }
        }

        generateLocalSmartMarketList(trimmedPrompt, isBangla)
    }

    /**
     * AI Bazar Cost & Price Estimator: Estimates current Bangladesh market prices and total budget range.
     */
    suspend fun estimateMarketListPrices(
        items: List<com.example.expensetracker.data.model.MarketItem>,
        isBangla: Boolean,
        apiKey: String?
    ): com.example.expensetracker.data.model.MarketCostEstimationResult = withContext(Dispatchers.IO) {
        if (items.isEmpty()) {
            return@withContext com.example.expensetracker.data.model.MarketCostEstimationResult(
                advice = if (isBangla) "ফর্দে কোনো পণ্য নেই।" else "No items in the list."
            )
        }

        val trimmedKey = apiKey?.trim()
        if (!trimmedKey.isNullOrBlank()) {
            val itemsFormatted = items.joinToString("\n") { "- ${it.name}: ${it.formatQuantity()}" }
            val aiPrompt = """
You are a Bangladesh consumer commodity pricing analyst.
Estimate current typical market prices (in BDT / ৳) for these items in Bangladesh:
$itemsFormatted

Return ONLY a valid JSON object with NO markdown formatting:
{
  "estimatedTotalMin": 1200.0,
  "estimatedTotalMax": 1450.0,
  "itemPrices": {
    "${items.first().name}": 110.0
  },
  "advice": "${if (isBangla) "বর্তমানে বাজারে সবজির সরবরাহ ভালো থাকায় দাম কিছুটা স্থিতিশীল।" else "Vegetable prices are currently stable due to good supply."}"
}
Advice should be 1 crisp sentence in ${if (isBangla) "Bengali (বাংলা)" else "English"}.
""".trimIndent()

            val modelsToTry = getModelsToTry()
            for (modelName in modelsToTry) {
                try {
                    val model = createModel(modelName, trimmedKey, temp = 0.1f, maxTokens = 800)
                    val response = model.generateContent(aiPrompt)
                    val cleanJson = extractJsonBlock(response.text.orEmpty())

                    if (cleanJson.isNotBlank()) {
                        val normalized = normalizeBengaliDigits(cleanJson)
                        val obj = jsonParser.parseToJsonElement(normalized).jsonObject
                        val min = extractDouble(obj["estimatedTotalMin"]) ?: 0.0
                        val max = extractDouble(obj["estimatedTotalMax"]) ?: (min * 1.15)
                        val advice = obj["advice"]?.jsonPrimitive?.content ?: ""

                        val priceMap = mutableMapOf<String, Double>()
                        val pricesObj = obj["itemPrices"]?.jsonObject
                        pricesObj?.forEach { (k, v) ->
                            extractDouble(v)?.let { priceMap[k] = it }
                        }

                        if (priceMap.isNotEmpty() || min > 0) {
                            Log.d("GeminiAiService", "estimateMarketListPrices succeeded with $modelName")
                            return@withContext com.example.expensetracker.data.model.MarketCostEstimationResult(
                                estimatedTotalMin = min,
                                estimatedTotalMax = max,
                                itemPrices = priceMap,
                                advice = advice
                            )
                        }
                    }
                } catch (e: Exception) {
                    Log.w("GeminiAiService", "estimateMarketListPrices failed on $modelName: ${e.message}")
                }
            }
        }

        generateLocalMarketPriceEstimation(items, isBangla)
    }

    /**
     * AI Conversational / Voice Market List Parser: Parses messy or conversational Bengali text into structured MarketItems.
     */
    suspend fun parseMarketListWithAi(
        rawText: String,
        isBangla: Boolean,
        apiKey: String?
    ): List<com.example.expensetracker.data.model.MarketItem> = withContext(Dispatchers.IO) {
        val trimmed = rawText.trim()
        if (trimmed.isBlank()) return@withContext emptyList()

        val trimmedKey = apiKey?.trim()
        if (!trimmedKey.isNullOrBlank()) {
            val aiPrompt = """
You are a precise grocery checklist extractor for Bengali and English text.
Extract all grocery items from this text:
"$trimmed"

Rules:
1. Detect quantity (e.g. দেড় -> 1.5, আড়াইশ গ্রাম -> 250, এক ডজন -> 12, দুই কেজি -> 2).
2. Detect unit (কেজি, গ্রাম, লিটার, পিস, ডজন, হালি, আঁটি, প্যাকেট, বোতল).
3. Return ONLY a valid JSON array of objects:
[
  {"name": "আলু", "quantity": 2.0, "unit": "কেজি"}
]
Language: ${if (isBangla) "Bengali (বাংলা)" else "English"}.
""".trimIndent()

            val modelsToTry = getModelsToTry()
            for (modelName in modelsToTry) {
                try {
                    val model = createModel(modelName, trimmedKey, temp = 0.1f, maxTokens = 800)
                    val response = model.generateContent(aiPrompt)
                    val cleanJson = extractJsonArray(response.text.orEmpty())

                    if (cleanJson.isNotBlank()) {
                        val normalized = normalizeBengaliDigits(cleanJson)
                        val jsonArr = jsonParser.parseToJsonElement(normalized).jsonArray
                        val results = mutableListOf<com.example.expensetracker.data.model.MarketItem>()

                        for (elem in jsonArr) {
                            val obj = elem.jsonObject
                            val name = obj["name"]?.jsonPrimitive?.content ?: continue
                            val qty = extractDouble(obj["quantity"]) ?: 1.0
                            val unit = obj["unit"]?.jsonPrimitive?.content ?: "কেজি"
                            results.add(
                                com.example.expensetracker.data.model.MarketItem(
                                    name = name,
                                    quantity = qty,
                                    unit = unit
                                )
                            )
                        }

                        if (results.isNotEmpty()) {
                            Log.d("GeminiAiService", "parseMarketListWithAi succeeded with $modelName (${results.size} items)")
                            return@withContext results
                        }
                    }
                } catch (e: Exception) {
                    Log.w("GeminiAiService", "parseMarketListWithAi failed on $modelName: ${e.message}")
                }
            }
        }

        com.example.expensetracker.common.MarketListParser.parseBulkText(trimmed)
    }

    /**
     * AI Market Smart Tips: Suggests 2-3 actionable tips regarding seasonal bargains and shopping efficiency.
     */
    suspend fun getMarketSmartTips(
        items: List<com.example.expensetracker.data.model.MarketItem>,
        isBangla: Boolean,
        apiKey: String?
    ): List<String> = withContext(Dispatchers.IO) {
        val trimmedKey = apiKey?.trim()
        if (!trimmedKey.isNullOrBlank() && items.isNotEmpty()) {
            val itemNames = items.take(8).joinToString(", ") { it.name }
            val prompt = """
Give 2 short, crisp shopping tips (max 10-12 words each) for someone buying these items in Bangladesh:
$itemNames
Language: ${if (isBangla) "Bengali (বাংলা)" else "English"}.
Return ONLY 2 lines with a bullet point (•).
""".trimIndent()

            val modelsToTry = getModelsToTry()
            for (modelName in modelsToTry) {
                try {
                    val model = createModel(modelName, trimmedKey, temp = 0.3f, maxTokens = 200)
                    val response = model.generateContent(prompt)
                    val lines = response.text?.lines().orEmpty().map { it.replace(Regex("""^[-*•\d.]+\s*"""), "").trim() }.filter { it.isNotBlank() }
                    if (lines.isNotEmpty()) {
                        return@withContext lines.take(2)
                    }
                } catch (_: Exception) {}
            }
        }

        if (isBangla) {
            listOf(
                "সকাল সকাল বাজারে গেলে সবচেয়ে তাজা শাকসবজি ও মাছ পাওয়া যায়।",
                "তেল, পেঁয়াজ ও আলু পাইকারি বা ৫ কেজির প্যাকে কিনলে প্রতি কেজিতে ৫-১০৳ সাশ্রয় হয়।"
            )
        } else {
            listOf(
                "Early morning shopping offers the freshest fish and leafy greens.",
                "Buying staple groceries like onions and oil in bulk saves 5-10%."
            )
        }
    }

    /**
     * AI Natural Language & SMS Transaction Parser (AI Magic Fill):
     * Parses bank/MFS SMS or conversational text into structured transaction fields.
     */
    suspend fun parseTransactionFromNaturalText(
        input: String,
        categories: List<Category>,
        accounts: List<com.example.expensetracker.data.model.Account>,
        isBangla: Boolean,
        apiKey: String?
    ): com.example.expensetracker.data.model.ParsedTransactionResult = withContext(Dispatchers.IO) {
        val trimmed = input.trim()
        if (trimmed.isBlank()) return@withContext com.example.expensetracker.data.model.ParsedTransactionResult()

        val trimmedKey = apiKey?.trim()
        if (!trimmedKey.isNullOrBlank()) {
            val categoryNames = categories.joinToString(", ") { it.name }
            val accountNames = accounts.joinToString(", ") { it.name }

            val prompt = """
You are a financial transaction extractor in Bangladesh.
Extract transaction details from this text or SMS:
"$trimmed"

Available Categories: [$categoryNames]
Available Accounts: [$accountNames]

Return ONLY a valid JSON object with NO markdown formatting:
{
  "amount": 1500.0,
  "title": "Short merchant or purpose, max 30 chars",
  "type": "EXPENSE or INCOME",
  "categoryName": "Best matching category from list above or null",
  "paymentMethod": "One of CASH, BANK_TRANSFER, CREDIT_CARD, DEBIT_CARD, BKASH, NAGAD, OTHER or null",
  "accountName": "Best matching account name from list above or null",
  "notes": "Extracted transaction ID or reference or null"
}
""".trimIndent()

            val modelsToTry = getModelsToTry()
            for (modelName in modelsToTry) {
                try {
                    val model = createModel(modelName, trimmedKey, temp = 0.1f, maxTokens = 400)
                    val response = model.generateContent(prompt)
                    val cleanJson = extractJsonBlock(response.text.orEmpty())

                    if (cleanJson.isNotBlank()) {
                        val normalized = normalizeBengaliDigits(cleanJson)
                        val obj = jsonParser.parseToJsonElement(normalized).jsonObject

                        val amount = extractDouble(obj["amount"])
                        val title = obj["title"]?.jsonPrimitive?.content?.takeIf { it.isNotBlank() && it != "null" }
                        val typeStr = obj["type"]?.jsonPrimitive?.content?.uppercase() ?: "EXPENSE"
                        val type = if (typeStr.contains("INCOME")) TransactionType.INCOME else TransactionType.EXPENSE
                        val catName = obj["categoryName"]?.jsonPrimitive?.content?.takeIf { it.isNotBlank() && it != "null" }
                        val methodStr = obj["paymentMethod"]?.jsonPrimitive?.content?.takeIf { it.isNotBlank() && it != "null" }
                        val accName = obj["accountName"]?.jsonPrimitive?.content?.takeIf { it.isNotBlank() && it != "null" }
                        val notes = obj["notes"]?.jsonPrimitive?.content?.takeIf { it.isNotBlank() && it != "null" }

                        val method = methodStr?.let {
                            try { PaymentMethod.valueOf(it.uppercase()) } catch (_: Exception) { null }
                        }

                        if (amount != null && amount > 0) {
                            Log.d("GeminiAiService", "parseTransactionFromNaturalText succeeded with $modelName")
                            return@withContext com.example.expensetracker.data.model.ParsedTransactionResult(
                                amount = amount,
                                title = title,
                                type = type,
                                categoryName = catName,
                                paymentMethod = method,
                                accountName = accName,
                                notes = notes
                            )
                        }
                    }
                } catch (e: Exception) {
                    Log.w("GeminiAiService", "parseTransactionFromNaturalText failed on $modelName: ${e.message}")
                }
            }
        }

        generateLocalTransactionParse(trimmed, categories, accounts)
    }

    /**
     * AI Savings Goal Feasibility & Action Plan:
     */
    suspend fun analyzeSavingsGoal(
        goalTitle: String,
        targetAmount: Double,
        savedAmount: Double,
        targetDateMillis: Long?,
        monthlyIncome: Double,
        monthlyExpense: Double,
        isBangla: Boolean,
        apiKey: String?
    ): com.example.expensetracker.data.model.SavingsGoalAiAnalysis = withContext(Dispatchers.IO) {
        val remaining = (targetAmount - savedAmount).coerceAtLeast(0.0)
        val monthlySurplus = (monthlyIncome - monthlyExpense).coerceAtLeast(0.0)

        val now = System.currentTimeMillis()
        val daysRemaining = if (targetDateMillis != null && targetDateMillis > now) {
            ((targetDateMillis - now) / (1000 * 60 * 60 * 24)).coerceAtLeast(1L)
        } else {
            90L
        }

        val requiredDaily = remaining / daysRemaining.toDouble()
        val requiredWeekly = requiredDaily * 7.0
        val requiredMonthly = requiredDaily * 30.0

        val monthsToAchieve = if (monthlySurplus > 0) remaining / monthlySurplus else (daysRemaining / 30.0)
        val grade = when {
            monthlySurplus >= requiredMonthly -> if (isBangla) "অত্যন্ত বাস্তবসম্মত" else "Highly Achievable"
            monthlySurplus >= requiredMonthly * 0.7 -> if (isBangla) "অর্জনযোগ্য" else "Achievable with focus"
            else -> if (isBangla) "পরিকল্পনা পুনর্বিবেচনা প্রয়োজন" else "Needs budget adjustment"
        }

        val trimmedKey = apiKey?.trim()
        if (!trimmedKey.isNullOrBlank()) {
            val prompt = """
You are a personal financial planner in Bangladesh.
Goal: '$goalTitle', Target: ৳$targetAmount, Saved: ৳$savedAmount, Days Left: $daysRemaining.
Monthly Income: ৳$monthlyIncome, Monthly Expenses: ৳$monthlyExpense (Surplus: ৳$monthlySurplus).
Required Monthly Savings: ৳$requiredMonthly.

Provide 2-3 short, actionable, bulleted cost-cutting recommendations (max 10 words each) tailored to Bangladesh to hit this savings goal.
Language: ${if (isBangla) "Bengali (বাংলা)" else "English"}.
Return ONLY the bulleted lines.
""".trimIndent()

            val modelsToTry = getModelsToTry()
            for (modelName in modelsToTry) {
                try {
                    val model = createModel(modelName, trimmedKey, temp = 0.2f, maxTokens = 300)
                    val response = model.generateContent(prompt)
                    val lines = response.text?.lines().orEmpty()
                        .map { it.replace(Regex("""^[-*•\d.]+\s*"""), "").trim() }
                        .filter { it.isNotBlank() }

                    if (lines.isNotEmpty()) {
                        return@withContext com.example.expensetracker.data.model.SavingsGoalAiAnalysis(
                            feasibilityGrade = grade,
                            requiredDailySavings = requiredDaily,
                            requiredWeeklySavings = requiredWeekly,
                            requiredMonthlySavings = requiredMonthly,
                            estimatedCompletionMonths = monthsToAchieve,
                            adviceTips = lines.take(3)
                        )
                    }
                } catch (_: Exception) {}
            }
        }

        val defaultTips = if (isBangla) {
            listOf(
                "বাইরের রেস্তোরাঁ ও ফুড ডেলিভারির খরচ কিছুটা নিয়ন্ত্রণ করুন।",
                "মাসিক বেতনের দিনেই নির্দিষ্ট ৳${requiredMonthly.toInt()} টাকা আলাদা অ্যাকাউন্টে সরিয়ে রাখুন।",
                "অনাবশ্যক অনলাইন কেনাকাটা ৩০ দিনের জন্য স্থগিত রাখুন।"
            )
        } else {
            listOf(
                "Cap dining out and food delivery expenses this month.",
                "Automate saving ৳${requiredMonthly.toInt()} right on payday.",
                "Pause non-essential impulse shopping for 30 days."
            )
        }

        com.example.expensetracker.data.model.SavingsGoalAiAnalysis(
            feasibilityGrade = grade,
            requiredDailySavings = requiredDaily,
            requiredWeeklySavings = requiredWeekly,
            requiredMonthlySavings = requiredMonthly,
            estimatedCompletionMonths = monthsToAchieve,
            adviceTips = defaultTips
        )
    }

    /**
     * AI Debt Portfolio Recovery & Settlement Strategy:
     */
    suspend fun analyzeDebtPortfolio(
        receivables: List<com.example.expensetracker.data.model.Receivable>,
        isBangla: Boolean,
        apiKey: String?
    ): com.example.expensetracker.data.model.DebtPortfolioAiAdvice = withContext(Dispatchers.IO) {
        val unsettled = receivables.filter { !it.isSettled }
        if (unsettled.isEmpty()) {
            return@withContext com.example.expensetracker.data.model.DebtPortfolioAiAdvice(
                healthSummary = if (isBangla) "আপনার কোনো বকেয়া পাওনা টাকা অবশিষ্ট নেই।" else "You have no outstanding debts owed to you.",
                actionSteps = listOf(if (isBangla) "আর্থিক স্থিতি সম্পূর্ণ স্বচ্ছ।" else "All personal debts are clear.")
            )
        }

        val totalDue = unsettled.sumOf { it.remainingAmount }
        val now = System.currentTimeMillis()
        val overdue = unsettled.filter { it.dueDateMillis != null && it.dueDateMillis < now }

        val priorityList = unsettled.sortedWith(
            compareByDescending<com.example.expensetracker.data.model.Receivable> { it.dueDateMillis != null && it.dueDateMillis < now }
                .thenByDescending { it.remainingAmount }
        ).take(3).map { "${it.personName} (${it.remainingAmount.toInt()}৳)" }

        val summary = if (isBangla) {
            "মোট ${unsettled.size} জনের কাছে ${totalDue.toInt()}৳ পাওনা আছে${if (overdue.isNotEmpty()) ", যার মধ্যে ${overdue.size}টি বকেয়া মেয়াদোত্তীর্ণ" else ""}।"
        } else {
            "Total ৳${totalDue.toInt()} owed across ${unsettled.size} records${if (overdue.isNotEmpty()) ", with ${overdue.size} overdue" else ""}."
        }

        val steps = if (isBangla) {
            listOf(
                "মেয়াদোত্তীর্ণ ঋণগ্রহীতাদের বিনম্র AI তাগাদা বার্তা পাঠান।",
                "বড় অঙ্কের বকেয়ার ক্ষেত্রে কিস্তিতে আংশিক আদায়ের প্রস্তাব দিন।"
            )
        } else {
            listOf(
                "Send courteous AI payment reminders to overdue contacts.",
                "Offer installment repayment options for larger amounts."
            )
        }

        com.example.expensetracker.data.model.DebtPortfolioAiAdvice(
            recoveryPriority = priorityList,
            healthSummary = summary,
            actionSteps = steps
        )
    }

    /**
     * AI Bill Cash Flow & Buffer Forecast:
     */
    suspend fun forecastBillCashFlow(
        bills: List<com.example.expensetracker.data.model.RecurringBill>,
        accounts: List<com.example.expensetracker.data.model.Account>,
        isBangla: Boolean,
        apiKey: String?
    ): com.example.expensetracker.data.model.BillCashFlowAiForecast = withContext(Dispatchers.IO) {
        val totalUpcoming = bills.filter { it.isActive }.sumOf { it.amount }
        val totalLiquid = accounts.sumOf { it.currentBalance }
        val isSafe = totalLiquid >= totalUpcoming

        val deficit = (totalUpcoming - totalLiquid).coerceAtLeast(0.0)
        val hasShortfall = !isSafe && deficit > 0.0
        val msg = if (isSafe) {
            if (isBangla) {
                "আগামী বিল বাবদ মোট ৳${totalUpcoming.toInt()} এর বিপরীতে ওয়ালেটে ৳${totalLiquid.toInt()} ব্যালেন্স আছে। ক্যাশফ্লো সম্পূর্ণ নিরাপদ।"
            } else {
                "Upcoming bills total ৳${totalUpcoming.toInt()} against ৳${totalLiquid.toInt()} liquid funds. Cash flow is fully safe."
            }
        } else {
            if (isBangla) {
                "⚠️ সতর্কতা: আসন্ন বিলগুলোর জন্য ৳${deficit.toInt()} ঘাটতি হতে পারে! দ্রুত ওয়ালেট বা অ্যাকাউন্টে ফান্ড জমা করার পরামর্শ।"
            } else {
                "⚠️ Alert: Potential ৳${deficit.toInt()} deficit for upcoming bills! Please top up your active accounts."
            }
        }
        val transfers = if (hasShortfall) {
            listOf(if (isBangla) "অন্যান্য সেভিংস বা ব্যাংক অ্যাকাউন্ট থেকে প্রধান ওয়ালেটে ফান্ড ট্রান্সফার করুন।" else "Transfer funds from other savings/bank accounts into primary wallet.")
        } else emptyList()
        val tips = if (hasShortfall) {
            listOf(if (isBangla) "বিলম্বে পরিশোধযোগ্য বিলগুলোর শেষ তারিখ পুনরায় পরীক্ষা করুন।" else "Check due dates of flexible bills to avoid late fees.")
        } else {
            listOf(if (isBangla) "আপনার ক্যাশফ্লো বাফার স্বাস্থ্যকর পর্যায়ে রয়েছে।" else "Your cash flow buffer is at a healthy level.")
        }

        com.example.expensetracker.data.model.BillCashFlowAiForecast(
            isBufferSafe = isSafe,
            upcomingTotal = totalUpcoming,
            availableLiquid = totalLiquid,
            alertMessage = msg,
            hasShortfall = hasShortfall,
            shortfallAmount = deficit,
            warningMessage = msg,
            suggestedTransfers = transfers,
            safetyTips = tips
        )
    }

    // --- LOCAL HEURISTIC FALLBACK ENGINES ---

    private fun generateLocalSmartMarketList(prompt: String, isBangla: Boolean): List<com.example.expensetracker.data.model.MarketItem> {
        val p = prompt.lowercase()
        return when {
            p.contains("মেস") || p.contains("ব্যাচেলর") || p.contains("bachelor") -> listOf(
                com.example.expensetracker.data.model.MarketItem(name = if (isBangla) "ডিম" else "Eggs", quantity = 1.0, unit = "ডজন", amount = 155.0),
                com.example.expensetracker.data.model.MarketItem(name = if (isBangla) "মসুর ডাল" else "Lentils", quantity = 500.0, unit = "গ্রাম", amount = 70.0),
                com.example.expensetracker.data.model.MarketItem(name = if (isBangla) "আলু" else "Potatoes", quantity = 2.0, unit = "কেজি", amount = 110.0),
                com.example.expensetracker.data.model.MarketItem(name = if (isBangla) "সয়াবিন তেল" else "Soybean Oil", quantity = 1.0, unit = "লিটার", amount = 175.0),
                com.example.expensetracker.data.model.MarketItem(name = if (isBangla) "পেঁয়াজ" else "Onions", quantity = 1.0, unit = "কেজি", amount = 90.0),
                com.example.expensetracker.data.model.MarketItem(name = if (isBangla) "ব্রয়লার মুরগি" else "Broiler Chicken", quantity = 1.5, unit = "কেজি", amount = 285.0),
                com.example.expensetracker.data.model.MarketItem(name = if (isBangla) "কাঁচামরিচ" else "Green Chilli", quantity = 100.0, unit = "গ্রাম", amount = 25.0)
            )
            p.contains("রমজান") || p.contains("ramadan") || p.contains("ইফতার") -> listOf(
                com.example.expensetracker.data.model.MarketItem(name = if (isBangla) "ছোলা" else "Chickpeas", quantity = 1.0, unit = "কেজি", amount = 110.0),
                com.example.expensetracker.data.model.MarketItem(name = if (isBangla) "খেজুর" else "Dates", quantity = 500.0, unit = "গ্রাম", amount = 200.0),
                com.example.expensetracker.data.model.MarketItem(name = if (isBangla) "বেসন" else "Gram Flour", quantity = 500.0, unit = "গ্রাম", amount = 65.0),
                com.example.expensetracker.data.model.MarketItem(name = if (isBangla) "মুড়ি" else "Puffed Rice", quantity = 1.0, unit = "কেজি", amount = 80.0),
                com.example.expensetracker.data.model.MarketItem(name = if (isBangla) "সয়াবিন তেল" else "Soybean Oil", quantity = 2.0, unit = "লিটার", amount = 350.0),
                com.example.expensetracker.data.model.MarketItem(name = if (isBangla) "শসা" else "Cucumber", quantity = 1.0, unit = "কেজি", amount = 60.0),
                com.example.expensetracker.data.model.MarketItem(name = if (isBangla) "লেবু" else "Lemon", quantity = 4.0, unit = "পিস", amount = 40.0),
                com.example.expensetracker.data.model.MarketItem(name = if (isBangla) "চিনি" else "Sugar", quantity = 1.0, unit = "কেজি", amount = 140.0)
            )
            p.contains("ডায়েট") || p.contains("diet") || p.contains("স্বাস্থ্য") -> listOf(
                com.example.expensetracker.data.model.MarketItem(name = if (isBangla) "ডিম" else "Eggs", quantity = 1.0, unit = "ডজন", amount = 155.0),
                com.example.expensetracker.data.model.MarketItem(name = if (isBangla) "ওটস" else "Oats", quantity = 500.0, unit = "গ্রাম", amount = 160.0),
                com.example.expensetracker.data.model.MarketItem(name = if (isBangla) "লাল চাল" else "Brown Rice", quantity = 2.0, unit = "কেজি", amount = 180.0),
                com.example.expensetracker.data.model.MarketItem(name = if (isBangla) "মুরগির বুকের মাংস" else "Chicken Breast", quantity = 1.0, unit = "কেজি", amount = 350.0),
                com.example.expensetracker.data.model.MarketItem(name = if (isBangla) "আপেল" else "Apples", quantity = 1.0, unit = "কেজি", amount = 260.0),
                com.example.expensetracker.data.model.MarketItem(name = if (isBangla) "কলা" else "Bananas", quantity = 1.0, unit = "ডজন", amount = 100.0),
                com.example.expensetracker.data.model.MarketItem(name = if (isBangla) "শসা" else "Cucumber", quantity = 1.0, unit = "কেজি", amount = 60.0),
                com.example.expensetracker.data.model.MarketItem(name = if (isBangla) "দুধ" else "Milk", quantity = 1.0, unit = "লিটার", amount = 90.0),
                com.example.expensetracker.data.model.MarketItem(name = if (isBangla) "পালং শাক" else "Spinach", quantity = 2.0, unit = "আঁটি", amount = 40.0)
            )
            p.contains("বিরিয়ানি") || p.contains("biryani") -> listOf(
                com.example.expensetracker.data.model.MarketItem(name = if (isBangla) "বাসমতি বা চিনিগুঁড়া চাল" else "Basmati Rice", quantity = 1.0, unit = "কেজি", amount = 160.0),
                com.example.expensetracker.data.model.MarketItem(name = if (isBangla) "মুরগি বা খাসির মাংস" else "Meat", quantity = 1.5, unit = "কেজি", amount = 450.0),
                com.example.expensetracker.data.model.MarketItem(name = if (isBangla) "ঘি" else "Ghee", quantity = 250.0, unit = "গ্রাম", amount = 300.0),
                com.example.expensetracker.data.model.MarketItem(name = if (isBangla) "টকদই" else "Curd", quantity = 500.0, unit = "গ্রাম", amount = 90.0),
                com.example.expensetracker.data.model.MarketItem(name = if (isBangla) "আলুবোখারা" else "Prunes", quantity = 100.0, unit = "গ্রাম", amount = 60.0),
                com.example.expensetracker.data.model.MarketItem(name = if (isBangla) "বিরিয়ানি মশলা" else "Biryani Spice", quantity = 1.0, unit = "প্যাকেট", amount = 85.0),
                com.example.expensetracker.data.model.MarketItem(name = if (isBangla) "পেঁয়াজ (বেরেস্তার জন্য)" else "Onions", quantity = 1.0, unit = "কেজি", amount = 90.0)
            )
            else -> listOf( // Default 1-Week Family Grocery
                com.example.expensetracker.data.model.MarketItem(name = if (isBangla) "মিনিকেট চাল" else "Miniket Rice", quantity = 5.0, unit = "কেজি", amount = 360.0),
                com.example.expensetracker.data.model.MarketItem(name = if (isBangla) "আলু" else "Potatoes", quantity = 3.0, unit = "কেজি", amount = 165.0),
                com.example.expensetracker.data.model.MarketItem(name = if (isBangla) "পেঁয়াজ" else "Onions", quantity = 2.0, unit = "কেজি", amount = 180.0),
                com.example.expensetracker.data.model.MarketItem(name = if (isBangla) "সয়াবিন তেল" else "Soybean Oil", quantity = 2.0, unit = "লিটার", amount = 350.0),
                com.example.expensetracker.data.model.MarketItem(name = if (isBangla) "ব্রয়লার মুরগি" else "Broiler Chicken", quantity = 2.0, unit = "কেজি", amount = 380.0),
                com.example.expensetracker.data.model.MarketItem(name = if (isBangla) "রুই মাছ" else "Rui Fish", quantity = 1.5, unit = "কেজি", amount = 520.0),
                com.example.expensetracker.data.model.MarketItem(name = if (isBangla) "মসুর ডাল" else "Lentils", quantity = 1.0, unit = "কেজি", amount = 135.0),
                com.example.expensetracker.data.model.MarketItem(name = if (isBangla) "ডিম" else "Eggs", quantity = 1.0, unit = "ডজন", amount = 155.0),
                com.example.expensetracker.data.model.MarketItem(name = if (isBangla) "কাঁচামরিচ" else "Green Chilli", quantity = 250.0, unit = "গ্রাম", amount = 50.0),
                com.example.expensetracker.data.model.MarketItem(name = if (isBangla) "লাল শাক" else "Red Spinach", quantity = 2.0, unit = "আঁটি", amount = 40.0),
                com.example.expensetracker.data.model.MarketItem(name = if (isBangla) "লবণ" else "Salt", quantity = 1.0, unit = "প্যাকেট", amount = 40.0)
            )
        }
    }

    private fun generateLocalMarketPriceEstimation(
        items: List<com.example.expensetracker.data.model.MarketItem>,
        isBangla: Boolean
    ): com.example.expensetracker.data.model.MarketCostEstimationResult {
        val priceLookup = mapOf(
            "আলু" to 55.0, "potato" to 55.0,
            "পেঁয়াজ" to 90.0, "onion" to 90.0,
            "সয়াবিন" to 175.0, "oil" to 175.0, "তেল" to 175.0,
            "সরিষা" to 260.0,
            "ডিম" to 13.0, "egg" to 13.0,
            "মুরগি" to 190.0, "chicken" to 190.0, "চিকেন" to 190.0,
            "গরু" to 780.0, "beef" to 780.0,
            "খাসি" to 1100.0, "mutton" to 1100.0,
            "রুই" to 350.0, "মাছ" to 350.0, "fish" to 350.0,
            "ইলিশ" to 1400.0,
            "চাল" to 72.0, "rice" to 72.0,
            "ডাল" to 135.0, "lentil" to 135.0,
            "চিনি" to 140.0, "sugar" to 140.0,
            "লবণ" to 40.0, "salt" to 40.0,
            "কাঁচামরিচ" to 200.0, "মরিচ" to 200.0, "chilli" to 200.0,
            "আদা" to 220.0, "ginger" to 220.0,
            "রসুন" to 220.0, "garlic" to 220.0,
            "শাক" to 25.0, "spinach" to 25.0,
            "দুধ" to 90.0, "milk" to 90.0,
            "শসা" to 60.0, "cucumber" to 60.0,
            "টমেটো" to 80.0, "tomato" to 80.0,
            "কলা" to 9.0, "banana" to 9.0,
            "সাবান" to 65.0, "soap" to 65.0,
            "রুটি" to 60.0, "bread" to 60.0
        )

        val itemPrices = mutableMapOf<String, Double>()
        var total = 0.0

        for (item in items) {
            val lowerName = item.name.lowercase()
            val unitPrice = priceLookup.entries.find { lowerName.contains(it.key) }?.value ?: 75.0

            val multiplier = when (item.unit) {
                "গ্রাম" -> item.quantity / 1000.0
                "ডজন" -> item.quantity * 12.0
                "হালি" -> item.quantity * 4.0
                else -> item.quantity
            }

            val est = (unitPrice * multiplier).coerceAtLeast(10.0)
            itemPrices[item.name] = kotlin.math.round(est)
            total += est
        }

        val min = kotlin.math.round(total * 0.95)
        val max = kotlin.math.round(total * 1.15)
        val advice = if (isBangla) {
            "বর্তমান স্থানীয় বাজার দর অনুযায়ী আনুমানিক বাজেট গণনা করা হয়েছে।"
        } else {
            "Estimated budget calculated based on standard local market rates."
        }

        return com.example.expensetracker.data.model.MarketCostEstimationResult(
            estimatedTotalMin = min,
            estimatedTotalMax = max,
            itemPrices = itemPrices,
            advice = advice
        )
    }

    private fun generateLocalTransactionParse(
        input: String,
        categories: List<Category>,
        accounts: List<com.example.expensetracker.data.model.Account>
    ): com.example.expensetracker.data.model.ParsedTransactionResult {
        // Detect bKash, Nagad or Bank SMS patterns
        var amount: Double? = null
        var isIncome = false
        var title = "Expense"
        var paymentMethod = PaymentMethod.CASH
        var notes = ""

        val eng = com.example.expensetracker.common.MarketListParser.convertBengaliDigitsToEnglish(input)
        val lower = eng.lowercase()

        if (lower.contains("bkash") || lower.contains("বিকাশ")) {
            paymentMethod = PaymentMethod.BKASH
        } else if (lower.contains("nagad") || lower.contains("নগদ")) {
            paymentMethod = PaymentMethod.NAGAD
        } else if (lower.contains("bank") || lower.contains("card") || lower.contains("ব্যাংক")) {
            paymentMethod = PaymentMethod.BANK_TRANSFER
        }

        if (lower.contains("received") || lower.contains("credited") || lower.contains("আদায়") || lower.contains("বেতন") || lower.contains("salary")) {
            isIncome = true
            title = if (lower.contains("salary") || lower.contains("বেতন")) "Salary" else "Received Money"
        } else if (lower.contains("cash out") || lower.contains("ক্যাশ আউট")) {
            title = "Cash Out"
        } else if (lower.contains("rickshaw") || lower.contains("রিকশা")) {
            title = "রিকশা ভাড়া"
        } else if (lower.contains("bill") || lower.contains("বিল")) {
            title = "Bill Payment"
        }

        // Extract amount: e.g. "Tk 1,500.00", "BDT 500", "৬০ টাকা"
        val amtMatch = Regex("""(?:(?:tk|bdt|টাকা|৳)\s*([0-9,]+(?:\.[0-9]+)?)|([0-9,]+(?:\.[0-9]+)?)\s*(?:tk|bdt|টাকা|৳))""", RegexOption.IGNORE_CASE).find(eng)
        if (amtMatch != null) {
            val numStr = (amtMatch.groupValues[1].ifBlank { amtMatch.groupValues[2] }).replace(",", "")
            amount = numStr.toDoubleOrNull()
        } else {
            // General first number
            val numOnly = Regex("""([0-9]+(?:\.[0-9]+)?)""").find(eng)
            amount = numOnly?.groupValues?.get(1)?.toDoubleOrNull()
        }

        // Match category
        val matchedCategory = categories.find { cat ->
            lower.contains(cat.name.lowercase()) ||
            (cat.name.contains("Transport", ignoreCase = true) && (lower.contains("রিকশা") || lower.contains("বাস") || lower.contains("ride"))) ||
            (cat.name.contains("Food", ignoreCase = true) && (lower.contains("নাস্তা") || lower.contains("খাবার") || lower.contains("restaurant"))) ||
            (cat.name.contains("Bills", ignoreCase = true) && lower.contains("বিল"))
        }

        // Match account
        val matchedAccount = accounts.find { acc ->
            lower.contains(acc.name.lowercase()) ||
            (paymentMethod == PaymentMethod.BKASH && acc.name.contains("bKash", ignoreCase = true)) ||
            (paymentMethod == PaymentMethod.NAGAD && acc.name.contains("Nagad", ignoreCase = true))
        }

        val type = if (isIncome) TransactionType.INCOME else TransactionType.EXPENSE

        return com.example.expensetracker.data.model.ParsedTransactionResult(
            amount = amount,
            title = title,
            type = type,
            categoryName = matchedCategory?.name,
            paymentMethod = paymentMethod,
            accountName = matchedAccount?.name,
            notes = if (input.length > 20) input.take(60) + ".." else input
        )
    }
}

