package com.example.expensetracker.ui.transactions

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.HairlineInkStroke
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.RadiusS
import com.example.expensetracker.ui.components.AmountKeypadLogic
import com.example.expensetracker.ui.components.CalculatorEvaluator
import com.example.expensetracker.ui.components.Strings
import com.example.expensetracker.ui.components.rememberAppHaptics

@Composable
fun TransactionAmountLiveEvaluation(
    hasOperators: Boolean,
    evaluatedValue: Double?,
    currencySymbol: String,
    strings: Strings,
    onApplyEvaluated: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val haptics = rememberAppHaptics()

    AnimatedVisibility(
        visible = hasOperators && evaluatedValue != null,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier
    ) {
        val evalStr = evaluatedValue?.let { AmountKeypadLogic.formatEvaluated(it, strings.isBangla) } ?: ""
        Surface(
            shape = RoundedCornerShape(RadiusS),
            color = appColors.accentContainer,
            border = BorderStroke(HairlineInkStroke, appColors.accent.copy(alpha = 0.35f)),
            modifier = Modifier
                .padding(top = 4.dp)
                .clickable {
                    haptics.lightTick()
                    evaluatedValue?.let {
                        val clean = CalculatorEvaluator.formatResult(it)
                        onApplyEvaluated(clean)
                    }
                }
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "= $currencySymbol $evalStr",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        fontFeatureSettings = "tnum"
                    ),
                    color = appColors.accent
                )
                Text(
                    text = if (strings.isBangla) "(ট্যাপ করুন)" else "(Tap to apply)",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    color = appColors.textSecondary
                )
            }
        }
    }
}
