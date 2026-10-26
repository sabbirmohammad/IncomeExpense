package com.example.expensetracker.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.DecimalFormat
import com.example.expensetracker.theme.LocalAppColors

@Composable
fun ConfirmationFeedbackCard(
    data: TransactionFeedbackData,
    tintColor: Color,
    badgeBgColor: Color,
    icon: ImageVector,
    pulseScale: Float,
    pulseAlpha: Float,
    timerProgress: Float,
    isBangla: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    Surface(
        modifier = modifier
            .fillMaxWidth(0.85f)
            .widthIn(max = 350.dp)
            .padding(16.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onDismiss()
            },
        shape = RoundedCornerShape(26.dp),
        color = appColors.surface,
        border = BorderStroke(1.5.dp, tintColor.copy(alpha = 0.5f)),
        shadowElevation = 24.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Pulsing animated icon badge
            Box(
                modifier = Modifier.size(88.dp),
                contentAlignment = Alignment.Center
            ) {
                // Expanding glow ring
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .background(tintColor.copy(alpha = pulseAlpha))
                )

                // Outer ring border
                Box(
                    modifier = Modifier
                        .size(74.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(tintColor.copy(alpha = 0.25f), Color.Transparent)
                            )
                        )
                )

                // Central Badge
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(badgeBgColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = tintColor,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Title
            Text(
                text = data.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    letterSpacing = 0.15.sp
                ),
                color = appColors.textPrimary,
                textAlign = TextAlign.Center
            )

            // Hero Amount (if present)
            if (data.amount != null) {
                Spacer(modifier = Modifier.height(8.dp))
                val formattedNumber = remember(data.amount) {
                    val formatter = DecimalFormat("#,##0.##")
                    formatter.format(data.amount)
                }

                val prefix = when (data.type) {
                    FeedbackVisualType.INCOME -> "+ "
                    FeedbackVisualType.EXPENSE -> "- "
                    else -> ""
                }

                val amountColor = when (data.type) {
                    FeedbackVisualType.INCOME -> appColors.income
                    FeedbackVisualType.EXPENSE -> appColors.expense
                    else -> appColors.textPrimary
                }

                Text(
                    text = "$prefix${data.currencySymbol} $formattedNumber",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 30.sp,
                        letterSpacing = (-0.5).sp
                    ),
                    color = amountColor,
                    textAlign = TextAlign.Center
                )
            }

            // Subtitle / Categories / Accounts Pill
            if (!data.subtitle.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = appColors.surfaceVariant,
                    border = BorderStroke(1.dp, appColors.border)
                ) {
                    Text(
                        text = data.subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp
                        ),
                        color = appColors.textSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            // Extra Details (if any)
            if (!data.extraDetails.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = data.extraDetails,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    color = appColors.textMuted,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Slim Auto-dismiss Countdown Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.08f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(timerProgress)
                        .fillMaxHeight()
                        .clip(CircleShape)
                        .background(tintColor.copy(alpha = 0.75f))
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Tap to dismiss hint
            Text(
                text = FeedbackStrings.tapToDismiss(isBangla),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Normal
                ),
                color = appColors.textMuted
            )
        }
    }
}
