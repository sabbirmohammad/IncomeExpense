package com.example.expensetracker.data.ai

import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.BillCashFlowAiForecast
import com.example.expensetracker.data.model.DebtPortfolioAiAdvice
import com.example.expensetracker.data.model.Receivable
import com.example.expensetracker.data.model.RecurringBill
import com.example.expensetracker.data.model.SavingsGoalAiAnalysis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object GeminiAiGoalsAndBills {

    suspend fun analyzeSavingsGoal(
        goalTitle: String,
        targetAmount: Double,
        savedAmount: Double,
        targetDateMillis: Long?,
        monthlyIncome: Double,
        monthlyExpense: Double,
        isBangla: Boolean,
        apiKey: String?
    ): SavingsGoalAiAnalysis = withContext(Dispatchers.IO) {
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

            val modelsToTry = GeminiAiClientHelper.getModelsToTry()
            for (modelName in modelsToTry) {
                try {
                    val model = GeminiAiClientHelper.createModel(modelName, trimmedKey, temp = 0.2f, maxTokens = 300)
                    val response = model.generateContent(prompt)
                    val lines = response.text?.lines().orEmpty()
                        .map { it.replace(Regex("""^[-*•\d.]+\s*"""), "").trim() }
                        .filter { it.isNotBlank() }

                    if (lines.isNotEmpty()) {
                        return@withContext SavingsGoalAiAnalysis(
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

        SavingsGoalAiAnalysis(
            feasibilityGrade = grade,
            requiredDailySavings = requiredDaily,
            requiredWeeklySavings = requiredWeekly,
            requiredMonthlySavings = requiredMonthly,
            estimatedCompletionMonths = monthsToAchieve,
            adviceTips = defaultTips
        )
    }

    suspend fun analyzeDebtPortfolio(
        receivables: List<Receivable>,
        isBangla: Boolean,
        apiKey: String?
    ): DebtPortfolioAiAdvice = withContext(Dispatchers.IO) {
        val unsettled = receivables.filter { !it.isSettled }
        if (unsettled.isEmpty()) {
            return@withContext DebtPortfolioAiAdvice(
                healthSummary = if (isBangla) "আপনার কোনো বকেয়া পাওনা টাকা অবশিষ্ট নেই।" else "You have no outstanding debts owed to you.",
                actionSteps = listOf(if (isBangla) "আর্থিক স্থিতি সম্পূর্ণ স্বচ্ছ।" else "All personal debts are clear.")
            )
        }

        val totalDue = unsettled.sumOf { it.remainingAmount }
        val now = System.currentTimeMillis()
        val overdue = unsettled.filter { it.dueDateMillis != null && it.dueDateMillis < now }

        val priorityList = unsettled.sortedWith(
            compareByDescending<Receivable> { it.dueDateMillis != null && it.dueDateMillis < now }
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

        DebtPortfolioAiAdvice(
            recoveryPriority = priorityList,
            healthSummary = summary,
            actionSteps = steps
        )
    }

    suspend fun forecastBillCashFlow(
        bills: List<RecurringBill>,
        accounts: List<Account>,
        isBangla: Boolean,
        apiKey: String?
    ): BillCashFlowAiForecast = withContext(Dispatchers.IO) {
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

        BillCashFlowAiForecast(
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
}
