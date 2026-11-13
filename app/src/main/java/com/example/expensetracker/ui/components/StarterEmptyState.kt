package com.example.expensetracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.ExpenseTrackerTheme
import com.example.expensetracker.theme.LocalAppColors

data class StarterSuggestion(
    val title: String,
    val leadingIcon: ImageVector? = null,
    val payload: String = title
)

/**
 * StarterEmptyState:
 * Living Ledger 2.0 Illustration-Free Empty State:
 * - 64dp tinted circle with semantic icon
 * - Clear title + 1-line human-readable description
 * - 2-4 one-tap starter suggestion chips (>= 48dp touch targets)
 * - Seamless pre-fill onboarding without modal clutter
 */
@Composable
fun StarterEmptyState(
    title: String,
    description: String,
    icon: ImageVector,
    suggestions: List<StarterSuggestion>,
    onSuggestionClick: (StarterSuggestion) -> Unit,
    modifier: Modifier = Modifier,
    iconContainerColor: Color = LocalAppColors.current.accentContainer,
    iconTint: Color = LocalAppColors.current.accent,
    actionButtonText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    val appColors = LocalAppColors.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 24.dp)
            .semantics(mergeDescendants = true) {},
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 1. 64dp Tinted Circle with Icon
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(iconContainerColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Title
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold
            ),
            color = appColors.textPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        // 3. 1-line Description
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 13.5.sp,
                lineHeight = 19.sp
            ),
            color = appColors.textSecondary,
            textAlign = TextAlign.Center
        )

        if (suggestions.isNotEmpty()) {
            Spacer(modifier = Modifier.height(20.dp))

            // 4. Starter Suggestions Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                suggestions.forEach { suggestion ->
                    AppChip(
                        label = suggestion.title,
                        selected = false,
                        leadingIcon = suggestion.leadingIcon,
                        onClick = { onSuggestionClick(suggestion) }
                    )
                }
            }
        }

        // Optional Primary Action Button
        if (actionButtonText != null && onActionClick != null) {
            Spacer(modifier = Modifier.height(16.dp))
            AppButton(
                text = actionButtonText,
                onClick = onActionClick,
                variant = AppButtonVariant.Primary
            )
        }
    }
}

// =========================================================================
// PREVIEWS IN ALL 4 THEMES (EN + BN)
// =========================================================================

@Preview(name = "StarterEmptyState - Dark En Ledger", showBackground = true)
@Composable
private fun StarterEmptyStateDarkPreview() {
    ExpenseTrackerTheme(themeMode = "dark") {
        Surface(color = LocalAppColors.current.surface, modifier = Modifier.padding(16.dp)) {
            StarterEmptyState(
                title = "No transactions yet",
                description = "Start your ledger by adding your first daily expense",
                icon = Icons.AutoMirrored.Outlined.ReceiptLong,
                suggestions = listOf(
                    StarterSuggestion("Bazaar"),
                    StarterSuggestion("Transport / Rickshaw"),
                    StarterSuggestion("Mobile recharge"),
                    StarterSuggestion("Tea & snacks")
                ),
                onSuggestionClick = {}
            )
        }
    }
}

@Preview(name = "StarterEmptyState - Light Bn Bills", showBackground = true)
@Composable
private fun StarterEmptyStateLightPreview() {
    ExpenseTrackerTheme(themeMode = "light") {
        Surface(color = LocalAppColors.current.surface, modifier = Modifier.padding(16.dp)) {
            StarterEmptyState(
                title = "কোনো বিল তালিকাভুক্ত নেই",
                description = "প্রতিমাসের নিয়মিত ইউটিলিটি বিল যোগ করে রিমাইন্ডার পান",
                icon = Icons.AutoMirrored.Outlined.ReceiptLong,
                suggestions = listOf(
                    StarterSuggestion("বিদ্যুৎ বিল"),
                    StarterSuggestion("গ্যাস বিল"),
                    StarterSuggestion("ইন্টারনেট"),
                    StarterSuggestion("বাসা ভাড়া")
                ),
                onSuggestionClick = {}
            )
        }
    }
}

@Preview(name = "StarterEmptyState - AMOLED Bn Goals", showBackground = true)
@Composable
private fun StarterEmptyStateAmoledPreview() {
    ExpenseTrackerTheme(themeMode = "amoled") {
        Surface(color = LocalAppColors.current.surface, modifier = Modifier.padding(16.dp)) {
            StarterEmptyState(
                title = "কোনো সঞ্চয় লক্ষ্য তৈরি করা হয়নি",
                description = "ভবিষ্যতের লক্ষ্য নির্ধারণ করে নিয়মিত সঞ্চয় করুন",
                icon = Icons.AutoMirrored.Outlined.ReceiptLong,
                suggestions = listOf(
                    StarterSuggestion("জরুরি তহবিল"),
                    StarterSuggestion("ঈদ কেনাকাটা"),
                    StarterSuggestion("হজ্জ / ওমরাহ")
                ),
                onSuggestionClick = {}
            )
        }
    }
}

@Preview(name = "StarterEmptyState - System En Action", showBackground = true)
@Composable
private fun StarterEmptyStateSystemPreview() {
    ExpenseTrackerTheme(themeMode = "system") {
        Surface(color = LocalAppColors.current.surface, modifier = Modifier.padding(16.dp)) {
            StarterEmptyState(
                title = "No budget limits set",
                description = "Set category caps to track monthly expenditures calmly",
                icon = Icons.AutoMirrored.Outlined.ReceiptLong,
                suggestions = emptyList(),
                onSuggestionClick = {},
                actionButtonText = "+ Create First Budget",
                onActionClick = {}
            )
        }
    }
}
