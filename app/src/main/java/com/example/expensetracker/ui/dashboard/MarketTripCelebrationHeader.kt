package com.example.expensetracker.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Celebration
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.AppColors

@Composable
fun MarketTripCelebrationHeader(
    tripTitle: String,
    celebrationScale: Float,
    isBangla: Boolean,
    appColors: AppColors,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(54.dp)
            .clip(CircleShape)
            .background(appColors.income.copy(alpha = 0.14f))
            .graphicsLayer {
                scaleX = celebrationScale
                scaleY = celebrationScale
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.Celebration,
            contentDescription = null,
            tint = appColors.income,
            modifier = Modifier.size(30.dp)
        )
    }

    Spacer(modifier = Modifier.height(10.dp))

    Text(
        text = if (isBangla) "বাজার সম্পন্ন ও ডিজিটাল রসিদ" else "Market Trip Digital Receipt",
        style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 19.sp
        ),
        color = appColors.textPrimary,
        textAlign = TextAlign.Center
    )

    Text(
        text = tripTitle.ifBlank {
            if (isBangla) "আজকের বাজার" else "Today's Market Trip"
        },
        style = MaterialTheme.typography.bodyMedium.copy(
            fontWeight = FontWeight.Medium,
            fontSize = 13.5.sp
        ),
        color = appColors.textSecondary,
        textAlign = TextAlign.Center
    )
}
