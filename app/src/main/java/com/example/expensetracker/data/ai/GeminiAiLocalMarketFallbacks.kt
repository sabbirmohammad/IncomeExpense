package com.example.expensetracker.data.ai

import com.example.expensetracker.common.MarketListParser
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.data.model.MarketCostEstimationResult
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.data.model.ParsedTransactionResult
import com.example.expensetracker.data.model.PaymentMethod
import com.example.expensetracker.data.model.TransactionType

object GeminiAiLocalMarketFallbacks {

    fun generateLocalSmartMarketList(prompt: String, isBangla: Boolean): List<MarketItem> {
        val p = prompt.lowercase()
        return when {
            p.contains("মেস") || p.contains("ব্যাচেলর") || p.contains("bachelor") -> listOf(
                MarketItem(name = if (isBangla) "ডিম" else "Eggs", quantity = 1.0, unit = "ডজন", amount = 155.0),
                MarketItem(name = if (isBangla) "মসুর ডাল" else "Lentils", quantity = 500.0, unit = "গ্রাম", amount = 70.0),
                MarketItem(name = if (isBangla) "আলু" else "Potatoes", quantity = 2.0, unit = "কেজি", amount = 110.0),
                MarketItem(name = if (isBangla) "সয়াবিন তেল" else "Soybean Oil", quantity = 1.0, unit = "লিটার", amount = 175.0),
                MarketItem(name = if (isBangla) "পেঁয়াজ" else "Onions", quantity = 1.0, unit = "কেজি", amount = 90.0),
                MarketItem(name = if (isBangla) "ব্রয়লার মুরগি" else "Broiler Chicken", quantity = 1.5, unit = "কেজি", amount = 285.0),
                MarketItem(name = if (isBangla) "কাঁচামরিচ" else "Green Chilli", quantity = 100.0, unit = "গ্রাম", amount = 25.0)
            )
            p.contains("রমজান") || p.contains("ramadan") || p.contains("ইফতার") -> listOf(
                MarketItem(name = if (isBangla) "ছোলা" else "Chickpeas", quantity = 1.0, unit = "কেজি", amount = 110.0),
                MarketItem(name = if (isBangla) "খেজুর" else "Dates", quantity = 500.0, unit = "গ্রাম", amount = 200.0),
                MarketItem(name = if (isBangla) "বেসন" else "Gram Flour", quantity = 500.0, unit = "গ্রাম", amount = 65.0),
                MarketItem(name = if (isBangla) "মুড়ি" else "Puffed Rice", quantity = 1.0, unit = "কেজি", amount = 80.0),
                MarketItem(name = if (isBangla) "সয়াবিন তেল" else "Soybean Oil", quantity = 2.0, unit = "লিটার", amount = 350.0),
                MarketItem(name = if (isBangla) "শসা" else "Cucumber", quantity = 1.0, unit = "কেজি", amount = 60.0),
                MarketItem(name = if (isBangla) "লেবু" else "Lemon", quantity = 4.0, unit = "পিস", amount = 40.0),
                MarketItem(name = if (isBangla) "চিনি" else "Sugar", quantity = 1.0, unit = "কেজি", amount = 140.0)
            )
            p.contains("ডায়েট") || p.contains("diet") || p.contains("স্বাস্থ্য") -> listOf(
                MarketItem(name = if (isBangla) "ডিম" else "Eggs", quantity = 1.0, unit = "ডজন", amount = 155.0),
                MarketItem(name = if (isBangla) "ওটস" else "Oats", quantity = 500.0, unit = "গ্রাম", amount = 160.0),
                MarketItem(name = if (isBangla) "লাল চাল" else "Brown Rice", quantity = 2.0, unit = "কেজি", amount = 180.0),
                MarketItem(name = if (isBangla) "মুরগির বুকের মাংস" else "Chicken Breast", quantity = 1.0, unit = "কেজি", amount = 350.0),
                MarketItem(name = if (isBangla) "আপেল" else "Apples", quantity = 1.0, unit = "কেজি", amount = 260.0),
                MarketItem(name = if (isBangla) "কলা" else "Bananas", quantity = 1.0, unit = "ডজন", amount = 100.0),
                MarketItem(name = if (isBangla) "শসা" else "Cucumber", quantity = 1.0, unit = "কেজি", amount = 60.0),
                MarketItem(name = if (isBangla) "দুধ" else "Milk", quantity = 1.0, unit = "লিটার", amount = 90.0),
                MarketItem(name = if (isBangla) "পালং শাক" else "Spinach", quantity = 2.0, unit = "আঁটি", amount = 40.0)
            )
            p.contains("বিরিয়ানি") || p.contains("biryani") -> listOf(
                MarketItem(name = if (isBangla) "বাসমতি বা চিনিগুঁড়া চাল" else "Basmati Rice", quantity = 1.0, unit = "কেজি", amount = 160.0),
                MarketItem(name = if (isBangla) "মুরগি বা খাসির মাংস" else "Meat", quantity = 1.5, unit = "কেজি", amount = 450.0),
                MarketItem(name = if (isBangla) "ঘি" else "Ghee", quantity = 250.0, unit = "গ্রাম", amount = 300.0),
                MarketItem(name = if (isBangla) "টকদই" else "Curd", quantity = 500.0, unit = "গ্রাম", amount = 90.0),
                MarketItem(name = if (isBangla) "আলুবোখারা" else "Prunes", quantity = 100.0, unit = "গ্রাম", amount = 60.0),
                MarketItem(name = if (isBangla) "বিরিয়ানি মশলা" else "Biryani Spice", quantity = 1.0, unit = "প্যাকেট", amount = 85.0),
                MarketItem(name = if (isBangla) "পেঁয়াজ (বেরেস্তার জন্য)" else "Onions", quantity = 1.0, unit = "কেজি", amount = 90.0)
            )
            else -> listOf(
                MarketItem(name = if (isBangla) "মিনিকেট চাল" else "Miniket Rice", quantity = 5.0, unit = "কেজি", amount = 360.0),
                MarketItem(name = if (isBangla) "আলু" else "Potatoes", quantity = 3.0, unit = "কেজি", amount = 165.0),
                MarketItem(name = if (isBangla) "পেঁয়াজ" else "Onions", quantity = 2.0, unit = "কেজি", amount = 180.0),
                MarketItem(name = if (isBangla) "সয়াবিন তেল" else "Soybean Oil", quantity = 2.0, unit = "লিটার", amount = 350.0),
                MarketItem(name = if (isBangla) "ব্রয়লার মুরগি" else "Broiler Chicken", quantity = 2.0, unit = "কেজি", amount = 380.0),
                MarketItem(name = if (isBangla) "রুই মাছ" else "Rui Fish", quantity = 1.5, unit = "কেজি", amount = 520.0),
                MarketItem(name = if (isBangla) "মসুর ডাল" else "Lentils", quantity = 1.0, unit = "কেজি", amount = 135.0),
                MarketItem(name = if (isBangla) "ডিম" else "Eggs", quantity = 1.0, unit = "ডজন", amount = 155.0),
                MarketItem(name = if (isBangla) "কাঁচামরিচ" else "Green Chilli", quantity = 250.0, unit = "গ্রাম", amount = 50.0),
                MarketItem(name = if (isBangla) "লাল শাক" else "Red Spinach", quantity = 2.0, unit = "আঁটি", amount = 40.0),
                MarketItem(name = if (isBangla) "লবণ" else "Salt", quantity = 1.0, unit = "প্যাকেট", amount = 40.0)
            )
        }
    }

    fun generateLocalMarketPriceEstimation(
        items: List<MarketItem>,
        isBangla: Boolean
    ): MarketCostEstimationResult {
        val priceLookup = mapOf(
            "আলু" to 55.0, "potato" to 55.0, "পেঁয়াজ" to 90.0, "onion" to 90.0,
            "সয়াবিন" to 175.0, "oil" to 175.0, "তেল" to 175.0, "সরিষা" to 260.0,
            "ডিম" to 13.0, "egg" to 13.0, "মুরগি" to 190.0, "chicken" to 190.0, "চিকেন" to 190.0,
            "গরু" to 780.0, "beef" to 780.0, "খাসি" to 1100.0, "mutton" to 1100.0,
            "রুই" to 350.0, "মাছ" to 350.0, "fish" to 350.0, "ইলিশ" to 1400.0,
            "চাল" to 72.0, "rice" to 72.0, "ডাল" to 135.0, "lentil" to 135.0,
            "চিনি" to 140.0, "sugar" to 140.0, "লবণ" to 40.0, "salt" to 40.0,
            "কাঁচামরিচ" to 200.0, "মরিচ" to 200.0, "chilli" to 200.0,
            "আদা" to 220.0, "ginger" to 220.0, "রসুন" to 220.0, "garlic" to 220.0,
            "শাক" to 25.0, "spinach" to 25.0, "দুধ" to 90.0, "milk" to 90.0,
            "শসা" to 60.0, "cucumber" to 60.0, "টমেটো" to 80.0, "tomato" to 80.0,
            "কলা" to 9.0, "banana" to 9.0, "সাবান" to 65.0, "soap" to 65.0,
            "রুটি" to 60.0, "bread" to 60.0
        )

        val itemPrices = mutableMapOf<String, Double>()
        var total = 0.0

        for (item in items) {
            val lowerName = item.name.lowercase()
            val unitPrice = priceLookup.entries.find { lowerName.contains(it.key) }?.value ?: 75.0

            val multiplier = when (item.unit) {
                "গ্রাম" -> item.quantity / 1000.0
                "ডজন" -> item.quantity * 12.0
                "হালি" -> item.quantity * 4.0
                else -> item.quantity
            }

            val est = (unitPrice * multiplier).coerceAtLeast(10.0)
            itemPrices[item.name] = kotlin.math.round(est)
            total += est
        }

        val min = kotlin.math.round(total * 0.95)
        val max = kotlin.math.round(total * 1.15)
        val advice = if (isBangla) {
            "বর্তমান স্থানীয় বাজার দর অনুযায়ী আনুমানিক বাজেট গণনা করা হয়েছে।"
        } else {
            "Estimated budget calculated based on standard local market rates."
        }

        return MarketCostEstimationResult(
            estimatedTotalMin = min,
            estimatedTotalMax = max,
            itemPrices = itemPrices,
            advice = advice
        )
    }

    fun generateLocalTransactionParse(
        input: String,
        categories: List<Category>,
        accounts: List<Account>
    ): ParsedTransactionResult {
        var amount: Double? = null
        var isIncome = false
        var title = "Expense"
        var paymentMethod = PaymentMethod.CASH

        val eng = MarketListParser.convertBengaliDigitsToEnglish(input)
        val lower = eng.lowercase()

        if (lower.contains("bkash") || lower.contains("বিকাশ")) {
            paymentMethod = PaymentMethod.BKASH
        } else if (lower.contains("nagad") || lower.contains("নগদ")) {
            paymentMethod = PaymentMethod.NAGAD
        } else if (lower.contains("bank") || lower.contains("card") || lower.contains("ব্যাংক")) {
            paymentMethod = PaymentMethod.BANK_TRANSFER
        }

        if (lower.contains("received") || lower.contains("credited") || lower.contains("আদায়") || lower.contains("বেতন") || lower.contains("salary")) {
            isIncome = true
            title = if (lower.contains("salary") || lower.contains("বেতন")) "Salary" else "Received Money"
        } else if (lower.contains("cash out") || lower.contains("ক্যাশ আউট")) {
            title = "Cash Out"
        } else if (lower.contains("rickshaw") || lower.contains("রিকশা")) {
            title = "রিকশা ভাড়া"
        } else if (lower.contains("bill") || lower.contains("বিল")) {
            title = "Bill Payment"
        }

        val amtMatch = Regex("""(?:(?:tk|bdt|টাকা|৳)\s*([0-9,]+(?:\.[0-9]+)?)|([0-9,]+(?:\.[0-9]+)?)\s*(?:tk|bdt|টাকা|৳))""", RegexOption.IGNORE_CASE).find(eng)
        if (amtMatch != null) {
            val numStr = (amtMatch.groupValues[1].ifBlank { amtMatch.groupValues[2] }).replace(",", "")
            amount = numStr.toDoubleOrNull()
        } else {
            val numOnly = Regex("""([0-9]+(?:\.[0-9]+)?)""").find(eng)
            amount = numOnly?.groupValues?.get(1)?.toDoubleOrNull()
        }

        val matchedCategory = categories.find { cat ->
            lower.contains(cat.name.lowercase()) ||
            (cat.name.contains("Transport", ignoreCase = true) && (lower.contains("রিকশা") || lower.contains("বাস") || lower.contains("ride"))) ||
            (cat.name.contains("Food", ignoreCase = true) && (lower.contains("নাস্তা") || lower.contains("খাবার") || lower.contains("restaurant"))) ||
            (cat.name.contains("Bills", ignoreCase = true) && lower.contains("বিল"))
        }

        val matchedAccount = accounts.find { acc ->
            lower.contains(acc.name.lowercase()) ||
            (paymentMethod == PaymentMethod.BKASH && acc.name.contains("bKash", ignoreCase = true)) ||
            (paymentMethod == PaymentMethod.NAGAD && acc.name.contains("Nagad", ignoreCase = true))
        }

        val type = if (isIncome) TransactionType.INCOME else TransactionType.EXPENSE

        return ParsedTransactionResult(
            amount = amount,
            title = title,
            type = type,
            categoryName = matchedCategory?.name,
            paymentMethod = paymentMethod,
            accountName = matchedAccount?.name,
            notes = if (input.length > 20) input.take(60) + ".." else input
        )
    }
}
