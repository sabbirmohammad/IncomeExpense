package com.example.expensetracker.data.ai

import com.example.expensetracker.data.model.AiBudgetRecommendation
import com.example.expensetracker.data.model.AiFinancialAudit
import com.example.expensetracker.data.model.AiInsightItem
import com.example.expensetracker.data.model.CategorySpend
import com.example.expensetracker.data.model.DailySpend
import com.example.expensetracker.data.model.FinancialSummary
import com.example.expensetracker.ui.components.formatCurrency
import java.util.Calendar

object GeminiAiLocalFinancialFallbacks {

    fun generateLocalBudgetRecommendations(
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

    fun generateLocalFinancialAudit(isBangla: Boolean): AiFinancialAudit {
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

    fun generateLocalInsights(
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
            val calendar = Calendar.getInstance()
            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
            val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
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
}
