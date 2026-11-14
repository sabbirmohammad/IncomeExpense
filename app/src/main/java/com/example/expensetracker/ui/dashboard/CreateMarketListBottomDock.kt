package com.example.expensetracker.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.AppColors
import com.example.expensetracker.theme.HairlineInkStroke
import com.example.expensetracker.ui.components.AppHaptics
import com.example.expensetracker.ui.components.convertToBengaliDigits

@Composable
fun CreateMarketListBottomDock(
    itemCount: Int,
    isBangla: Boolean,
    onCreateClick: () -> Unit,
    appColors: AppColors,
    haptics: AppHaptics,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        color = appColors.surface,
        border = BorderStroke(HairlineInkStroke, appColors.border),
        shadowElevation = 8.dp,
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .imePadding()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            val countDigits = if (isBangla) convertToBengaliDigits(itemCount.toString()) else itemCount.toString()
            val buttonLabel = if (itemCount > 0) {
                if (isBangla) "ফর্দ তৈরি করুন ($countDigits টি পণ্য)" else "Create list ($countDigits items)"
            } else {
                if (isBangla) "ফর্দ তৈরি করুন" else "Create list"
            }

            Button(
                onClick = {
                    haptics.mediumClick()
                    onCreateClick()
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = appColors.accent),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .minimumInteractiveComponentSize()
            ) {
                Text(
                    text = buttonLabel,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    ),
                    color = Color.White
                )
            }
        }
    }
}
