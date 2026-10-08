package com.example.expensetracker.data.ai

import android.graphics.Bitmap
import android.util.Log
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.data.model.ParsedTransactionResult
import com.example.expensetracker.data.model.PaymentMethod
import com.example.expensetracker.data.model.ScannedReceiptData
import com.example.expensetracker.data.model.TransactionType
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.text.SimpleDateFormat
import java.util.Locale

object GeminiAiReceiptParser {

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

        val modelsToTry = GeminiAiClientHelper.getModelsToTry()
        for (modelName in modelsToTry) {
            try {
                val model = GeminiAiClientHelper.createModel(modelName, trimmedKey, temp = 0.1f, maxTokens = 500)
                val inputContent = content {
                    image(bitmap)
                    text(prompt)
                }
                val response = model.generateContent(inputContent)
                val rawText = response.text?.trim()
                if (!rawText.isNullOrBlank()) {
                    Log.d("GeminiAiService", "scanReceipt response from $modelName: $rawText")
                    val cleanJson = rawText.removePrefix("```json").removePrefix("```").removeSuffix("```").trim()
                    val json = GeminiAiClientHelper.jsonParser.parseToJsonElement(cleanJson).jsonObject

                    val amount = GeminiAiJsonUtils.extractDouble(json["amount"])
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
                            SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(it)?.time
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

    suspend fun parseTransactionFromNaturalText(
        input: String,
        categories: List<Category>,
        accounts: List<Account>,
        isBangla: Boolean,
        apiKey: String?
    ): ParsedTransactionResult = withContext(Dispatchers.IO) {
        val trimmed = input.trim()
        if (trimmed.isBlank()) return@withContext ParsedTransactionResult()

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

            val modelsToTry = GeminiAiClientHelper.getModelsToTry()
            for (modelName in modelsToTry) {
                try {
                    val model = GeminiAiClientHelper.createModel(modelName, trimmedKey, temp = 0.1f, maxTokens = 400)
                    val response = model.generateContent(prompt)
                    val cleanJson = GeminiAiJsonUtils.extractJsonBlock(response.text.orEmpty())

                    if (cleanJson.isNotBlank()) {
                        val normalized = GeminiAiJsonUtils.normalizeBengaliDigits(cleanJson)
                        val obj = GeminiAiClientHelper.jsonParser.parseToJsonElement(normalized).jsonObject

                        val amount = GeminiAiJsonUtils.extractDouble(obj["amount"])
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
                            return@withContext ParsedTransactionResult(
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

        GeminiAiLocalMarketFallbacks.generateLocalTransactionParse(trimmed, categories, accounts)
    }
}
