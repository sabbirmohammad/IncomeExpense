package com.example.expensetracker.ui.market

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.expensetracker.common.UnitConverterEngine
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.data.model.ProductPriceInsight
import com.example.expensetracker.ui.components.AmountKeypadLogic

@Stable
class MarketPriceEntryState(
    val standardUnits: List<String>,
    initialQuantity: Double,
    initialUnit: String,
    initialExpression: String,
    val baselineUnitPrice: Double?,
    val lastRate: Pair<Double, String>?,
    val priceInsight: ProductPriceInsight?
) {
    var quantity by mutableDoubleStateOf(initialQuantity)
    var unit by mutableStateOf(initialUnit)
    var priceMode by mutableIntStateOf(0) // 0: Per unit rate, 1: Total price
    var autoAdvanceEnabled by mutableStateOf(true)
    var expression by mutableStateOf(initialExpression)

    val evaluatedValue: Double?
        get() = AmountKeypadLogic.evaluate(expression)

    val enteredNumber: Double
        get() = evaluatedValue ?: expression.toDoubleOrNull() ?: 0.0

    val totalAmountComputed: Double
        get() = if (priceMode == 0) {
            if (enteredNumber > 0 && quantity > 0) {
                UnitConverterEngine.estimateCost(enteredNumber, quantity, unit)
            } else 0.0
        } else {
            enteredNumber
        }

    val unitRateComputed: Double
        get() = if (priceMode == 0) {
            enteredNumber
        } else {
            if (enteredNumber > 0 && quantity > 0) {
                UnitConverterEngine.calculateBaseUnitPrice(enteredNumber, quantity, unit).first
            } else 0.0
        }

    val varianceDiff: Double?
        get() = if (baselineUnitPrice != null && baselineUnitPrice > 0 && unitRateComputed > 0) {
            unitRateComputed - baselineUnitPrice
        } else null

    val variancePercent: Double?
        get() = if (baselineUnitPrice != null && baselineUnitPrice > 0 && varianceDiff != null) {
            (varianceDiff!! / baselineUnitPrice) * 100.0
        } else null
}

@Composable
fun rememberMarketPriceEntryState(
    item: MarketItem,
    lastRate: Pair<Double, String>? = null,
    priceInsight: ProductPriceInsight? = null
): MarketPriceEntryState {
    val standardUnits = remember {
        listOf("কেজি", "গ্রাম", "লিটার", "পিস", "হালি", "ডজন")
    }

    val initialQuantity = remember(item.id, item.quantity) {
        if (item.quantity > 0) item.quantity else 1.0
    }

    val initialUnit = remember(item.id, item.unit) {
        item.unit.trim().ifBlank { "কেজি" }
    }

    val initialExpression = remember(item.id, item.amount) {
        val initialAmt = item.amount
        if (initialAmt != null && initialAmt > 0) {
            if (initialAmt % 1.0 == 0.0) initialAmt.toInt().toString() else initialAmt.toString()
        } else {
            val prevRate = lastRate?.first ?: priceInsight?.currentUnitPrice
            if (prevRate != null && prevRate > 0) {
                if (prevRate % 1.0 == 0.0) prevRate.toInt().toString() else prevRate.toString()
            } else {
                ""
            }
        }
    }

    val baselineUnitPrice = remember(lastRate, priceInsight) {
        lastRate?.first ?: priceInsight?.currentUnitPrice
    }

    return remember(item.id) {
        MarketPriceEntryState(
            standardUnits = standardUnits,
            initialQuantity = initialQuantity,
            initialUnit = initialUnit,
            initialExpression = initialExpression,
            baselineUnitPrice = baselineUnitPrice,
            lastRate = lastRate,
            priceInsight = priceInsight
        )
    }
}
