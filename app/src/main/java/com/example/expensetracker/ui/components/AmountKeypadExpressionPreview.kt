package com.example.expensetracker.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.HairlineInkStroke
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.RadiusS

@Composable
internal fun AmountKeypadExpressionPreview(
    visible: Boolean,
    evalStr: String,
    currencySymbol: String,
    isBangla: Boolean,
    onApply: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier
    ) {
        Surface(
            shape = RoundedCornerShape(RadiusS),
            color = appColors.accentContainer,
            border = BorderStroke(HairlineInkStroke, appColors.accent.copy(alpha = 0.35f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
                .defaultMinSize(minHeight = 44.dp)
                .minimumInteractiveComponentSize()
                .clickable { onApply() }
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "= $currencySymbol $evalStr",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        fontFeatureSettings = "tnum"
                    ),
                    color = appColors.accent
                )
                Text(
                    text = if (isBangla) "ট্যাপ করে বসান" else "Tap to apply",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp),
                    color = appColors.textSecondary
                )
            }
        }
    }
}
