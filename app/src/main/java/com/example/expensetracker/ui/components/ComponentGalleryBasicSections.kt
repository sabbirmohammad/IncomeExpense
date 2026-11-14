package com.example.expensetracker.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.expensetracker.theme.LocalAppColors

@Composable
fun GalleryAmountTextSection(isBengali: Boolean) {
    val colors = LocalAppColors.current
    SectionHeader(title = if (isBengali) "১. টাকার সংখ্যা (AmountText)" else "1. AmountText (tnum & a11y)")
    AppCard(variant = AppCardVariant.Flat) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Expense", style = MaterialTheme.typography.bodySmall, color = colors.textSecondary)
                    AmountText(
                        amount = 1250.0,
                        type = AmountType.Expense,
                        useBengaliNumerals = isBengali,
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
                Column {
                    Text("Income", style = MaterialTheme.typography.bodySmall, color = colors.textSecondary)
                    AmountText(
                        amount = 48500.0,
                        type = AmountType.Income,
                        useBengaliNumerals = isBengali,
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Masked (Privacy)", style = MaterialTheme.typography.bodySmall, color = colors.textSecondary)
                    AmountText(
                        amount = 33015.0,
                        isPrivacyMasked = true,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                Column {
                    Text("Neutral Balance", style = MaterialTheme.typography.bodySmall, color = colors.textSecondary)
                    AmountText(
                        amount = 33015.0,
                        type = AmountType.Neutral,
                        useBengaliNumerals = isBengali,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
        }
    }
}

@Composable
fun GalleryButtonsSection(isBengali: Boolean) {
    SectionHeader(title = if (isBengali) "২. বাটন (AppButton)" else "2. AppButton (WCAG >= 48dp)")
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AppButton(
            text = if (isBengali) "সংরক্ষণ" else "Save ৳ 450",
            onClick = {},
            variant = AppButtonVariant.Primary,
            modifier = Modifier.weight(1f)
        )
        AppButton(
            text = if (isBengali) "বাতিল" else "Cancel",
            onClick = {},
            variant = AppButtonVariant.Secondary,
            modifier = Modifier.weight(1f)
        )
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AppButton(
            text = if (isBengali) "মুছে ফেলুন" else "Delete",
            onClick = {},
            variant = AppButtonVariant.Destructive,
            modifier = Modifier.weight(1f)
        )
        AppButton(
            text = if (isBengali) "লোডিং..." else "Loading",
            onClick = {},
            loading = true,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun GalleryChipsSection(
    isBengali: Boolean,
    filterSelected: Boolean,
    onFilterToggle: () -> Unit,
    choiceSelected: Boolean,
    onChoiceToggle: () -> Unit
) {
    SectionHeader(title = if (isBengali) "৩. চিপস (AppChip)" else "3. AppChip (48dp Touch Target)")
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AppChip(
            label = if (isBengali) "বাজার" else "Bazaar",
            selected = filterSelected,
            onClick = onFilterToggle,
            leadingIcon = Icons.Outlined.ShoppingBag,
            variant = AppChipVariant.Filter
        )
        AppChip(
            label = if (isBengali) "নগদ" else "Cash",
            selected = choiceSelected,
            onClick = onChoiceToggle,
            leadingIcon = Icons.Outlined.AccountBalanceWallet,
            variant = AppChipVariant.Choice
        )
        AppChip(
            label = if (isBengali) "মুছে ফেলার যোগ্য" else "Removable",
            selected = false,
            onClick = {},
            onRemoveClick = {},
            variant = AppChipVariant.Input
        )
    }
}
