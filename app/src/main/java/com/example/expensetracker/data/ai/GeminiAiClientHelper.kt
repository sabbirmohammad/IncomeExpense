package com.example.expensetracker.data.ai

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.serialization.json.Json

object GeminiAiClientHelper {

    const val PRIMARY_MODEL = "gemini-3.5-flash-lite"
    const val FALLBACK_MODEL = "gemini-3.1-flash-lite"
    const val TERTIARY_MODEL = "gemini-3.6-flash"

    fun getModelsToTry(): List<String> = listOf(
        "gemini-2.0-flash",
        "gemini-1.5-flash",
        PRIMARY_MODEL,
        FALLBACK_MODEL,
        TERTIARY_MODEL,
        "gemini-3.5-flash"
    )

    val jsonParser = Json { ignoreUnknownKeys = true; isLenient = true }

    fun createModel(
        modelName: String,
        apiKey: String,
        temp: Float = 0.15f,
        maxTokens: Int? = null
    ): GenerativeModel {
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
}
