package com.example.expensetracker.data.ai

import android.util.Log
import com.example.expensetracker.common.MarketListParser
import com.example.expensetracker.data.model.MarketCostEstimationResult
import com.example.expensetracker.data.model.MarketItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

object GeminiAiMarketService {

    suspend fun generateSmartMarketList(
        prompt: String,
        isBangla: Boolean,
        apiKey: String?
    ): List<MarketItem> = withContext(Dispatchers.IO) {
        val trimmedPrompt = prompt.trim()
        val trimmedKey = apiKey?.trim()

        if (!trimmedKey.isNullOrBlank()) {
            val modelsToTry = GeminiAiClientHelper.getModelsToTry()
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
                    val model = GeminiAiClientHelper.createModel(modelName, trimmedKey, temp = 0.2f, maxTokens = 800)
                    val response = model.generateContent(aiPrompt)
                    val cleanJson = GeminiAiJsonUtils.extractJsonArray(response.text.orEmpty())

                    if (cleanJson.isNotBlank()) {
                        val normalized = GeminiAiJsonUtils.normalizeBengaliDigits(cleanJson)
                        val jsonArr = GeminiAiClientHelper.jsonParser.parseToJsonElement(normalized).jsonArray
                        val items = mutableListOf<MarketItem>()

                        for (elem in jsonArr) {
                            val obj = elem.jsonObject
                            val name = obj["name"]?.jsonPrimitive?.content ?: continue
                            val qty = GeminiAiJsonUtils.extractDouble(obj["quantity"]) ?: 1.0
                            val unit = obj["unit"]?.jsonPrimitive?.content ?: "কেজি"
                            val price = GeminiAiJsonUtils.extractDouble(obj["estimatedPrice"])

                            items.add(
                                MarketItem(
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

        GeminiAiLocalMarketFallbacks.generateLocalSmartMarketList(trimmedPrompt, isBangla)
    }

    suspend fun estimateMarketListPrices(
        items: List<MarketItem>,
        isBangla: Boolean,
        apiKey: String?
    ): MarketCostEstimationResult = GeminiAiMarketEstimator.estimateMarketListPrices(
        items, isBangla, apiKey
    )

    suspend fun parseMarketListWithAi(
        rawText: String,
        isBangla: Boolean,
        apiKey: String?
    ): List<MarketItem> = withContext(Dispatchers.IO) {
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

            val modelsToTry = GeminiAiClientHelper.getModelsToTry()
            for (modelName in modelsToTry) {
                try {
                    val model = GeminiAiClientHelper.createModel(modelName, trimmedKey, temp = 0.1f, maxTokens = 800)
                    val response = model.generateContent(aiPrompt)
                    val cleanJson = GeminiAiJsonUtils.extractJsonArray(response.text.orEmpty())

                    if (cleanJson.isNotBlank()) {
                        val normalized = GeminiAiJsonUtils.normalizeBengaliDigits(cleanJson)
                        val jsonArr = GeminiAiClientHelper.jsonParser.parseToJsonElement(normalized).jsonArray
                        val results = mutableListOf<MarketItem>()

                        for (elem in jsonArr) {
                            val obj = elem.jsonObject
                            val name = obj["name"]?.jsonPrimitive?.content ?: continue
                            val qty = GeminiAiJsonUtils.extractDouble(obj["quantity"]) ?: 1.0
                            val unit = obj["unit"]?.jsonPrimitive?.content ?: "কেজি"
                            results.add(
                                MarketItem(
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

        MarketListParser.parseBulkText(trimmed)
    }

    suspend fun getMarketSmartTips(
        items: List<MarketItem>,
        isBangla: Boolean,
        apiKey: String?
    ): List<String> = GeminiAiMarketEstimator.getMarketSmartTips(
        items, isBangla, apiKey
    )
}
