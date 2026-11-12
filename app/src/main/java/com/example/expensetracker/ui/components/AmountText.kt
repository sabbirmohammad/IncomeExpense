package com.example.expensetracker.ui.components

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.TabularNumFeature
import kotlin.math.abs

enum class AmountType {
    Expense,
    Income,
    Neutral
}

/**
 * V7 Hero & List Amount Component:
 * - Guarantees tabular figures (tnum)
 * - Sign and semantic coloring by type
 * - Privacy mode masking (••••)
 * - Bengali numerals support
 * - Screen-reader / TalkBack accessible announcement
 * - Never truncates with ellipsis; scales down if needed
 */
@Composable
fun AmountText(
    amount: Double,
    modifier: Modifier = Modifier,
    type: AmountType = AmountType.Neutral,
    symbol: String = "৳",
    isPrivacyMasked: Boolean = false,
    useBengaliNumerals: Boolean = globalBengaliNumeralsEnabled,
    style: TextStyle = MaterialTheme.typography.titleMedium,
    color: Color = Color.Unspecified,
    showSign: Boolean = true
) {
    val colors = LocalAppColors.current

    val resolvedColor = when {
        color != Color.Unspecified -> color
        type == AmountType.Expense -> colors.expense
        type == AmountType.Income -> colors.income
        else -> colors.textPrimary
    }

    if (isPrivacyMasked) {
        Text(
            text = "••••",
            style = style.copy(
                fontFeatureSettings = TabularNumFeature,
                fontWeight = style.fontWeight ?: FontWeight.SemiBold
            ),
            color = resolvedColor,
            modifier = modifier.semantics {
                contentDescription = "Hidden amount in privacy mode"
            }
        )
        return
    }

    val absAmount = abs(amount)
    val formattedNum = formatCurrency(absAmount, symbol = symbol, useBengaliNumerals = useBengaliNumerals)

    val signStr = when {
        !showSign -> ""
        type == AmountType.Expense || amount < 0 -> "− "
        type == AmountType.Income -> "+ "
        else -> ""
    }

    val displayText = "$signStr$formattedNum"

    val a11yDescription = buildString {
        append(if (type == AmountType.Expense || amount < 0) "Minus " else if (type == AmountType.Income) "Plus " else "")
        append(absAmount.toString())
        append(" taka")
        when (type) {
            AmountType.Expense -> append(" expense")
            AmountType.Income -> append(" income")
            AmountType.Neutral -> {}
        }
    }

    Text(
        text = displayText,
        style = style.copy(
            fontFeatureSettings = TabularNumFeature,
            fontWeight = style.fontWeight ?: FontWeight.SemiBold
        ),
        color = resolvedColor,
        maxLines = 1,
        softWrap = false,
        modifier = modifier.semantics {
            contentDescription = a11yDescription
        }
    )
}
