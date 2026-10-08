package com.example.expensetracker.data.ai

import android.util.Log
import com.example.expensetracker.data.model.MarketCostEstimationResult
import com.example.expensetracker.data.model.MarketItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

object GeminiAiMarketEstimator {

    suspend fun estimateMarketListPrices(
        items: List<MarketItem>,
        isBangla: Boolean,
        apiKey: String?
    ): MarketCostEstimationResult = withContext(Dispatchers.IO) {
        if (items.isEmpty()) {
            return@withContext MarketCostEstimationResult(
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

            val modelsToTry = GeminiAiClientHelper.getModelsToTry()
            for (modelName in modelsToTry) {
                try {
                    val model = GeminiAiClientHelper.createModel(modelName, trimmedKey, temp = 0.1f, maxTokens = 800)
                    val response = model.generateContent(aiPrompt)
                    val cleanJson = GeminiAiJsonUtils.extractJsonBlock(response.text.orEmpty())

                    if (cleanJson.isNotBlank()) {
                        val normalized = GeminiAiJsonUtils.normalizeBengaliDigits(cleanJson)
                        val obj = GeminiAiClientHelper.jsonParser.parseToJsonElement(normalized).jsonObject
                        val min = GeminiAiJsonUtils.extractDouble(obj["estimatedTotalMin"]) ?: 0.0
                        val max = GeminiAiJsonUtils.extractDouble(obj["estimatedTotalMax"]) ?: (min * 1.15)
                        val advice = obj["advice"]?.jsonPrimitive?.content ?: ""

                        val priceMap = mutableMapOf<String, Double>()
                        val pricesObj = obj["itemPrices"]?.jsonObject
                        pricesObj?.forEach { (k, v) ->
                            GeminiAiJsonUtils.extractDouble(v)?.let { priceMap[k] = it }
                        }

                        if (priceMap.isNotEmpty() || min > 0) {
                            Log.d("GeminiAiService", "estimateMarketListPrices succeeded with $modelName")
                            return@withContext MarketCostEstimationResult(
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

        GeminiAiLocalMarketFallbacks.generateLocalMarketPriceEstimation(items, isBangla)
    }

    suspend fun getMarketSmartTips(
        items: List<MarketItem>,
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

            val modelsToTry = GeminiAiClientHelper.getModelsToTry()
            for (modelName in modelsToTry) {
                try {
                    val model = GeminiAiClientHelper.createModel(modelName, trimmedKey, temp = 0.3f, maxTokens = 200)
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
}
