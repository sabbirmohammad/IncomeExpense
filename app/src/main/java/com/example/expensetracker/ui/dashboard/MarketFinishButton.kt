package com.example.expensetracker.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.formatCurrency
import com.example.expensetracker.ui.components.rememberAppHaptics

/**
 * V7 In-Store Running Total Dock Button per UX_FLOWS section 5 & Screenshot Patch C6:
 * - Shows total spent and finish action
 * - At 100% purchased items, the button becomes full primary-filled
 */
@Composable
fun MarketFinishButton(
    isBangla: Boolean,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier,
    totalSpent: Double = 0.0,
    currencySymbol: String = "৳",
    isAllPurchased: Boolean = false
) {
    val appColors = LocalAppColors.current
    val haptics = rememberAppHaptics()
    val contentColor = if (appColors.isDark) Color(0xFF052B14) else Color.White

    val buttonText = if (isBangla) {
        if (totalSpent > 0) {
            "বাজার সম্পন্ন করুন ($currencySymbol${formatCurrency(totalSpent, "", true).trim()})"
        } else {
            "বাজার সম্পন্ন করুন"
        }
    } else {
        if (totalSpent > 0) {
            "Finish Shopping ($currencySymbol${formatCurrency(totalSpent, "", false).trim()})"
        } else {
            "Finish Shopping"
        }
    }

    Button(
        onClick = {
            haptics.mediumClick()
            onComplete()
        },
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(14.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = if (isAllPurchased) 3.dp else 1.dp,
            pressedElevation = 0.dp
        ),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isAllPurchased) appColors.income else appColors.income.copy(alpha = 0.9f),
            contentColor = contentColor
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = buttonText,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.Bold,
                    fontFeatureSettings = "tnum"
                ),
                color = contentColor
            )
        }
    }
}
