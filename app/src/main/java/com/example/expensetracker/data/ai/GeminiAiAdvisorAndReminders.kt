package com.example.expensetracker.data.ai

import android.util.Log
import com.example.expensetracker.data.model.AiChatMessage
import com.example.expensetracker.ui.components.formatCurrency
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object GeminiAiAdvisorAndReminders {

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

        val modelsToTry = GeminiAiClientHelper.getModelsToTry()
        for (modelName in modelsToTry) {
            try {
                val model = GeminiAiClientHelper.createModel(modelName, apiKey, temp = 0.3f, maxTokens = 800)
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

        val modelsToTry = GeminiAiClientHelper.getModelsToTry()
        for (modelName in modelsToTry) {
            try {
                val model = GeminiAiClientHelper.createModel(modelName, apiKey, temp = 0.3f, maxTokens = 250)
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
}
