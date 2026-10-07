package com.example.expensetracker.data.model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class AiChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

@Serializable
data class AiInsightItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val type: String = "INFO",
    val timestamp: Long = System.currentTimeMillis()
)

@Serializable
data class AiFinancialAudit(
    val healthScore: Int = 80,
    val statusGrade: String = "Good",
    val summaryEvaluation: String = "",
    val detectedLeaksOrSpikes: List<String> = emptyList(),
    val actionableTips: List<String> = emptyList(),
    val timestamp: Long = System.currentTimeMillis()
)

@Serializable
enum class ApiTestStatusType {
    SUCCESS,
    INVALID_KEY,
    QUOTA_EXCEEDED,
    NETWORK_ERROR,
    ERROR
}

@Serializable
data class ApiConnectionTestResult(
    val isSuccess: Boolean,
    val statusType: ApiTestStatusType,
    val latencyMs: Long = 0L,
    val modelUsed: String = "",
    val message: String = ""
)
