package com.example.expensetracker.data.ai

import android.util.Log
import com.example.expensetracker.data.model.AiFinancialAudit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

object GeminiAiAuditService {

    suspend fun generateDeepFinancialAudit(
        contextSummary: String,
        periodLabel: String,
        isBangla: Boolean,
        apiKey: String?
    ): AiFinancialAudit = withContext(Dispatchers.IO) {
        if (apiKey.isNullOrBlank()) {
            return@withContext GeminiAiLocalFinancialFallbacks.generateLocalFinancialAudit(isBangla)
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

        val modelsToTry = GeminiAiClientHelper.getModelsToTry()
        for (modelName in modelsToTry) {
            try {
                val model = GeminiAiClientHelper.createModel(modelName, apiKey, temp = 0.2f, maxTokens = 700)
                val response = model.generateContent(prompt)
                val cleanJson = GeminiAiJsonUtils.extractJsonBlock(response.text.orEmpty())

                if (cleanJson.isNotBlank()) {
                    val normalized = GeminiAiJsonUtils.normalizeBengaliDigits(cleanJson)
                    val obj = GeminiAiClientHelper.jsonParser.parseToJsonElement(normalized).jsonObject
                    val score = (GeminiAiJsonUtils.extractInt(obj["healthScore"]) ?: 75).coerceIn(10, 100)
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

        GeminiAiLocalFinancialFallbacks.generateLocalFinancialAudit(isBangla)
    }
}
