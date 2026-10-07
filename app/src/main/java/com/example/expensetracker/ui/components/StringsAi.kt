package com.example.expensetracker.ui.components

open class StringsAi(
    isBangla: Boolean
) : StringsFeatures(isBangla) {
    // AI Features
    val actionAiAdvisor: String get() = if (isBangla) "পরামর্শ" else "Advisor"
    val aiVoiceInputTitle: String get() = if (isBangla) "স্মার্ট টেক্সট বা এসএমএস ইনপুট" else "Smart Text & SMS Input"
    val aiVoiceInputPlaceholder: String get() = if (isBangla) "যেমন: ৬০ টাকা রিকশা ভাড়া বা bKash এ ১৫০০ বিদ্যুৎ বিল" else "e.g. 60 tk rickshaw fare or bKash 1500 electricity bill"
    val aiMagicFill: String get() = if (isBangla) "এআই দিয়ে পূরণ" else "AI Auto-Fill"
    val aiAdvisorTitle: String get() = if (isBangla) "AI পরামর্শক" else "FinAI Advisor"
    val aiAdvisorSubtitle: String get() = if (isBangla) "ব্যক্তিগত আর্থিক পরামর্শ ও খরচ বিশ্লেষণ" else "Personal financial insights & savings advice"
    val aiInsightsTitle: String get() = if (isBangla) "ইনসাইটস" else "Insights"
    val askAdvisorPlaceholder: String get() = if (isBangla) "টাকা-পয়সা বা বাজেট নিয়ে যেকোনো প্রশ্ন করুন..." else "Ask anything about your money or budgets..."
    val geminiApiKeyLabel: String get() = if (isBangla) "Gemini AI API Key (গুগল থেকে ফ্রি)" else "Gemini AI API Key (Free from Google)"
    val geminiApiKeyHint: String get() = if (isBangla) "aistudio.google.com থেকে ১ মিনিটে ফ্রি কি নেওয়া যায়" else "Get free key from aistudio.google.com in 1 minute"
    val aiInsightsEnabledLabel: String get() = if (isBangla) "স্মার্ট এআই ইনসাইটস ও পরামর্শ" else "Smart AI Insights & Tips"

    // API Diagnostics & Test
    val testApiConnection: String get() = if (isBangla) "API যাচাই করুন" else "Test Connection"
    val testingApiConnection: String get() = if (isBangla) "সংযোগ পরীক্ষা করা হচ্ছে..." else "Testing connection..."
    val apiConnectedSuccess: String get() = if (isBangla) "Gemini AI সফলভাবে সংযুক্ত!" else "Gemini AI Connected!"
    val apiConnectionFailed: String get() = if (isBangla) "API সংযোগ ব্যর্থ হয়েছে" else "API Connection Failed"
    val apiLatency: String get() = if (isBangla) "প্রতিক্রিয়া সময়" else "Latency"

    // SMS & Quick Entry
    val pasteSmsOrReceipt: String get() = if (isBangla) "SMS পেস্ট করুন" else "Paste SMS"
    val smsPastePlaceholder: String get() = if (isBangla) "বিকাশ, নগদ বা ব্যাংকের ট্রানজেকশন SMS পেস্ট করুন..." else "Paste Bank or MFS transaction SMS..."
    val smsParsedSuccess: String get() = if (isBangla) "SMS থেকে তথ্য সংগৃহীত হয়েছে" else "SMS parsed successfully"

    // AI Smart Budget Planning
    val aiBudgetPlanner: String get() = if (isBangla) "AI বাজেট সহকারী" else "AI Smart Budget Planner"
    val budgetRecommendations: String get() = if (isBangla) "বাজেট সুপারিশ" else "Budget Recommendations"
    val aiBudgetRecommendations: String get() = budgetRecommendations
    val budgetRecommendationsSubtitle: String get() = if (isBangla) "আপনার ব্যয়ের ধরনের উপর ভিত্তি করে স্মার্ট সুপারিশ পান" else "Get smart recommendations based on your spending patterns"
    val generateAiBudget: String get() = if (isBangla) "স্মার্ট বাজেট তৈরি করুন" else "Generate Smart Budget"
    val generatingAiBudget: String get() = if (isBangla) "বাজেট সুপারিশ তৈরি হচ্ছে..." else "Generating recommendations..."
    val applyAiBudget: String get() = if (isBangla) "এই বাজেট প্রয়োগ করুন" else "Apply Recommendations"
    val aiBudgetAppliedSuccess: String get() = if (isBangla) "স্মার্ট বাজেট সফলভাবে প্রয়োগ করা হয়েছে!" else "Smart budget applied successfully!"

    // AI Deep Financial Audit
    val aiFinancialAudit: String get() = if (isBangla) "স্মার্ট অডিট" else "Smart Audit"
    val financialHealthScore: String get() = if (isBangla) "আর্থিক স্বাস্থ্য স্কোর" else "Financial Health Score"
    val detectedLeaks: String get() = if (isBangla) "অতিরিক্ত খরচের খাত ও অসঙ্গতি" else "Detected Spikes & Cost Leaks"
    val actionableSavingsSteps: String get() = if (isBangla) "সঞ্চয় বৃদ্ধির ৩টি পদক্ষেপ" else "3 Actionable Savings Steps"
    val runFinancialAudit: String get() = if (isBangla) "অডিট শুরু করুন" else "Run Health Audit"
    val auditingFinancials: String get() = if (isBangla) "আর্থিক নিরীক্ষা চলছে..." else "Analyzing finances..."

    // Polite Debt Reminder
    val aiReminderTitle: String get() = if (isBangla) "AI তাগাদা বার্তা" else "AI Payment Reminder"
    val generatePoliteReminder: String get() = if (isBangla) "AI তাগাদা বার্তা তৈরি করুন" else "Generate Polite Reminder"
    val generatingReminder: String get() = if (isBangla) "বার্তা তৈরি হচ্ছে..." else "Generating reminder..."
    val copyReminder: String get() = if (isBangla) "বার্তা কপি করুন" else "Copy Message"
    val shareReminder: String get() = if (isBangla) "বার্তা পাঠান" else "Share Message"
    val reminderCopied: String get() = if (isBangla) "বার্তাটি ক্লিপবোর্ডে কপি করা হয়েছে" else "Reminder copied to clipboard"

    // Smart Receipts & OCR
    val attachReceipt: String get() = if (isBangla) "রসিদ / ভাউচার যুক্ত করুন" else "Attach Receipt / Bill"
    val takePhoto: String get() = if (isBangla) "ছবি তুলুন" else "Take Photo"
    val chooseFromGallery: String get() = if (isBangla) "গ্যালারি থেকে বেছে নিন" else "Choose from Gallery"
    val removeReceipt: String get() = if (isBangla) "রসিদ মুছুন" else "Remove Receipt"
    val scanReceiptWithAi: String get() = if (isBangla) "AI দিয়ে রসিদ স্ক্যান করুন" else "AI Smart Scan"
    val scanningReceipt: String get() = if (isBangla) "রসিদ স্ক্যান করা হচ্ছে..." else "Scanning receipt..."
    val receiptScanSuccess: String get() = if (isBangla) "রসিদের তথ্য সফলভাবে সংগৃহীত হয়েছে!" else "Receipt data extracted successfully!"
    val receiptScanFailed: String get() = if (isBangla) "রসিদ পড়তে পারেনি, তথ্য ম্যানুয়ালি লিখুন" else "Could not read receipt, please enter manually"
    val viewReceipt: String get() = if (isBangla) "রসিদ দেখুন" else "View Receipt"

    // Market List AI Superpowers
    val generateMarketListPrompt: String get() = if (isBangla) "কীসের জন্য বাজার ফর্দ চান লিখুন (যেমন: ৫ জনের ৩ দিনের বাজার, খিচুড়ির উপাদান)..." else "Enter what you need a grocery list for (e.g. 5-person 3-day meal, Biryani recipe)..."
    val generateMarketListBtn: String get() = if (isBangla) "ফর্দ তৈরি করুন" else "Generate List"
    val generatingMarketList: String get() = if (isBangla) "এআই ফর্দ তৈরি করছে..." else "Generating grocery list..."
    val addItemsToChecklist: String get() = if (isBangla) "✓ ফর্দে যোগ করুন" else "✓ Add to Checklist"
    val bazarPresetsFamily: String get() = if (isBangla) "👨‍👩‍👧‍👦 পরিবারের বাজার (১ সপ্তাহ)" else "👨‍👩‍👧‍👦 Family (1 Week)"
    val bazarPresetsBachelor: String get() = if (isBangla) "🍳 মেস / ব্যাচেলর (৩ দিন)" else "🍳 Bachelor (3 Days)"
    val bazarPresetsRamadan: String get() = if (isBangla) "🌙 রমজান স্পেশাল" else "🌙 Ramadan Special"
    val bazarPresetsHealthy: String get() = if (isBangla) "🥗 স্বাস্থ্যকর ডায়েট" else "🥗 Healthy Diet"
    val bazarPresetsBiryani: String get() = if (isBangla) "🍛 বিরিয়ানি রেসিপি" else "🍛 Biryani Recipe"
    val bazarPresetsKhichuri: String get() = if (isBangla) "🍲 ভুনা খিচুড়ি রেসিপি" else "🍲 Khichuri Recipe"
    val estimateBazarCost: String get() = if (isBangla) "💰 বর্তমান বাজার দর ও বাজেট অনুমান করুন" else "💰 Estimate Current Market Rates & Budget"
    val estimatingBazarCost: String get() = if (isBangla) "বাজার দর বিশ্লেষণ হচ্ছে..." else "Estimating market rates..."
    val estimatedTotalBudget: String get() = if (isBangla) "আনুমানিক মোট বাজেট:" else "Estimated Total Budget:"
    val applyEstimatedPrices: String get() = if (isBangla) "✓ ফর্দে আনুমানিক মূল্য বসিয়ে দিন" else "✓ Fill Estimated Prices into List"
    val pricesAppliedSuccess: String get() = if (isBangla) "আনুমানিক মূল্য সফলভাবে ফর্দে যুক্ত হয়েছে!" else "Estimated prices filled successfully!"
    val parseWithAi: String get() = if (isBangla) "AI দিয়ে নিখুঁত পার্স করুন" else "Parse with AI"
    val parsingMarketWithAi: String get() = if (isBangla) "AI দিয়ে ফর্দ বিশ্লেষণ হচ্ছে..." else "Parsing grocery list with AI..."
    val marketSmartTipsTitle: String get() = if (isBangla) "💡 এআই বাজার টিপস ও সাশ্রয়ী পরামর্শ" else "💡 AI Smart Market Tips"
    val allMarketCategories: String get() = if (isBangla) "সব পণ্য" else "All"
    val catVegetables: String get() = if (isBangla) "🥬 সবজি ও কাঁচা" else "🥬 Vegetables"
    val catMeatFish: String get() = if (isBangla) "🐟 মাছ ও মাংস" else "🐟 Meat & Fish"
    val catGrocery: String get() = if (isBangla) "🌾 মুদি ও মশলা" else "🌾 Grocery"
    val catDairy: String get() = if (isBangla) "🥛 ডেইরি ও বেকারি" else "🥛 Dairy"

    val aiParseMarketListBtn: String get() = parseWithAi
    val categoryVeggies: String get() = catVegetables
    val categoryMeatFish: String get() = catMeatFish
    val categoryGroceries: String get() = catGrocery
    val categoryDairyBakery: String get() = catDairy

    val marketColItem: String get() = if (isBangla) "পণ্যের নাম" else "Item Name"
    val marketColQty: String get() = if (isBangla) "পরিমাণ" else "Quantity"
    val marketColPrice: String get() = if (isBangla) "মূল্য" else "Price"
    val marketColDone: String get() = if (isBangla) "সম্পন্ন" else "Done"

    val aiMarketGeneratorTitle: String get() = if (isBangla) "এআই স্মার্ট বাজার ফর্দ জেনারেটর" else "AI Smart Grocery List Generator"
    val aiPresetFamily: String get() = bazarPresetsFamily
    val aiPresetBachelor: String get() = bazarPresetsBachelor
    val aiPresetRamadan: String get() = bazarPresetsRamadan
    val aiPresetHealthy: String get() = bazarPresetsHealthy
    val aiPresetBiryani: String get() = bazarPresetsBiryani
    val aiPresetKhichuri: String get() = bazarPresetsKhichuri
    val aiMarketGeneratorHint: String get() = if (isBangla) "উদা: ৪ জনের মেস, ৩ দিনের সাধারণ বাজার" else "e.g., 4-person mess, 3 days simple groceries"
    val aiGenerateMarketList: String get() = if (isBangla) "ফর্দ তৈরি করুন" else "Generate List"
    val aiApplyGeneratedItems: String get() = addItemsToChecklist
    val aiCostEstimatorTitle: String get() = estimateBazarCost
    val aiEstimateCostBtn: String get() = if (isBangla) "বাজার দর যাচাই করুন" else "Check Market Rates"
    val aiApplyEstimatedPrices: String get() = applyEstimatedPrices
    val aiSmartTipsTitle: String get() = marketSmartTipsTitle

    // Transaction AI Magic Fill & SMS
    val aiMagicFillTitle: String get() = if (isBangla) "এআই অটো-ফিল ও এসএমএস ইনপুট" else "AI Magic Fill & SMS Input"
    val aiMagicFillDesc: String get() = if (isBangla) "বিকাশ/নগদ/ব্যাংকের SMS পেস্ট করুন বা সাধারণ ভাষায় লিখুন" else "Paste Bank/MFS SMS or type in natural language"
    val aiMagicFillBtn: String get() = if (isBangla) "এআই দিয়ে পূরণ" else "AI Auto-Fill"
    val aiParsingTransaction: String get() = if (isBangla) "এআই দিয়ে লেনদেন বিশ্লেষণ হচ্ছে..." else "Analyzing transaction with AI..."
    val aiFillSuccess: String get() = if (isBangla) "লেনদেনের তথ্য স্বয়ংক্রিয়ভাবে পূরণ করা হয়েছে!" else "Transaction fields filled automatically!"

    // General AI
    val aiAnalyzing: String get() = if (isBangla) "বিশ্লেষণ করা হচ্ছে..." else "Analyzing..."

    // Savings Goals AI
    val aiSavingsAdvisorTitle: String get() = if (isBangla) "এআই সঞ্চয় সম্ভাব্যতা ও কৌশল" else "AI Savings Feasibility & Strategy"
    val aiAnalyzeGoal: String get() = if (isBangla) "এআই সঞ্চয় বিশ্লেষণ" else "AI Feasibility Analysis"
    val aiSavingsAnalysisBtn: String get() = if (isBangla) "এআই কৌশল" else "AI Strategy"
    val aiAnalyzingGoal: String get() = if (isBangla) "সঞ্চয়ের লক্ষ্য বিশ্লেষণ করা হচ্ছে..." else "Analyzing savings goal..."
    val savingsFeasibility: String get() = if (isBangla) "সম্ভাব্যতা:" else "Feasibility:"
    val dailyTargetNeeded: String get() = if (isBangla) "দৈনিক জমানোর টার্গেট:" else "Daily Target Needed:"

    // Debts & Receivables AI
    val aiDebtStrategyTitle: String get() = if (isBangla) "এআই আদায় ও পরিশোধ কৌশল" else "AI Debt Portfolio Strategy"
    val aiDebtStrategyBtn: String get() = if (isBangla) "এআই পাওনা পর্যালোচনা" else "AI Debt Strategy"
    val aiDebtAdvisorBtn: String get() = if (isBangla) "এআই পরামর্শ" else "AI Advice"
    val analyzingDebts: String get() = if (isBangla) "পাওনা ও দেনা বিশ্লেষণ করা হচ্ছে..." else "Analyzing debts..."

    // Bills & Subscriptions AI
    val aiBillCashFlowTitle: String get() = if (isBangla) "এআই বিল ও ক্যাশফ্লো চেক" else "AI Cash Flow & Bill Shield"
    val aiCashFlowAlert: String get() = if (isBangla) "ক্যাশফ্লো স্থিতি" else "Cash Flow Status"
    val aiBillCashFlowAlert: String get() = aiCashFlowAlert
}
