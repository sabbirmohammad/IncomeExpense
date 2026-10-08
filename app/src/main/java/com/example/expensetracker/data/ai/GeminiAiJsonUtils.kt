package com.example.expensetracker.data.ai

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive

object GeminiAiJsonUtils {

    fun extractDouble(element: JsonElement?): Double? {
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

    fun extractInt(element: JsonElement?): Int? {
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

    fun normalizeBengaliDigits(input: String): String {
        val banglaDigits = "০১২৩৪৫৬৭৮৯"
        val engDigits = "0123456789"
        var res = input
        for (i in 0..9) {
            res = res.replace(banglaDigits[i], engDigits[i])
        }
        return res
    }
}
