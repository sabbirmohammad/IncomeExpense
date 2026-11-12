package com.example.expensetracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.ExpenseTrackerTheme
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.Space16
import com.example.expensetracker.theme.Space20

/**
 * Debug-only Component Gallery displaying all Refined Ledger v7 base components
 * across themes (Dark, Light, AMOLED) and languages (English, Bengali).
 */
@Composable
fun ComponentGalleryScreen(
    isBengali: Boolean = false,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current
    val strings = if (isBengali) BanglaStrings else EnglishStrings

    var selectedSegment by remember { mutableIntStateOf(0) }
    var filterSelected by remember { mutableStateOf(true) }
    var choiceSelected by remember { mutableStateOf(false) }
    var isExpanded by remember { mutableStateOf(true) }
    var isPrivacyActive by remember { mutableStateOf(false) }

    CompositionLocalProvider(
        LocalAppStrings provides strings
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(colors.background)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Space20, vertical = Space16),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = if (isBengali) "ভি৭ উপাদান গ্যালারি" else "v7 Component Gallery",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = colors.textPrimary
            )

            // --- 1. AMOUNT TEXT ---
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

            // --- 2. BUTTONS ---
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

            // --- 3. CHIPS ---
            SectionHeader(title = if (isBengali) "৩. চিপস (AppChip)" else "3. AppChip (48dp Touch Target)")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AppChip(
                    label = if (isBengali) "বাজার" else "Bazaar",
                    selected = filterSelected,
                    onClick = { filterSelected = !filterSelected },
                    leadingIcon = Icons.Outlined.ShoppingBag,
                    variant = AppChipVariant.Filter
                )
                AppChip(
                    label = if (isBengali) "নগদ" else "Cash",
                    selected = choiceSelected,
                    onClick = { choiceSelected = !choiceSelected },
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

            // --- 4. SEGMENTED CONTROL ---
            SectionHeader(title = if (isBengali) "৪. সেগমেন্টেড কন্ট্রোল" else "4. SegmentedControl (48dp)")
            SegmentedControl(
                items = if (isBengali) listOf("খরচ", "আয়", "স্থানান্তর") else listOf("Expense", "Income", "Transfer"),
                selectedIndex = selectedSegment,
                onItemSelected = { selectedSegment = it }
            )

            // --- 5. CARDS (FLAT, RAISED, TINTED) ---
            SectionHeader(title = if (isBengali) "৫. কার্ড রূপভেদ (AppCard Variants)" else "5. AppCard Variants (C1 Flat/Raised/Tinted)")
            AppCard(variant = AppCardVariant.Flat) {
                Text(
                    text = if (isBengali) "ফ্ল্যাট কার্ড: ডার্ক মোডে বর্ডারহীন টোনাল পৃষ্ঠ" else "Flat Card: Tonal surface without outline in Dark/AMOLED",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textPrimary
                )
            }
            AppCard(variant = AppCardVariant.Raised) {
                Text(
                    text = if (isBengali) "রাইজড কার্ড: হালকা শ্যাডো সহ শিট সারফেস" else "Raised Card: Elevated surfaceRaised with soft shadow",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textPrimary
                )
            }
            AppCard(variant = AppCardVariant.Tinted) {
                Text(
                    text = if (isBengali) "টিন্টেড কার্ড: অ্যাকসেন্ট কন্টেইনার ব্যাকগ্রাউন্ড" else "Tinted Card: Soft accentContainer surface",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.accent
                )
            }

            // --- 6. STATUS BANNERS ---
            SectionHeader(title = if (isBengali) "৬. স্ট্যাটাস ব্যানার" else "6. StatusBanner")
            StatusBanner(
                message = if (isBengali) "বিদ্যুৎ বিল আগামী ৩ দিনের মধ্যে পরিশোধযোগ্য" else "Electricity bill due in 3 days (৳ 2,400)",
                variant = StatusBannerVariant.Warning,
                actionText = if (isBengali) "পরিশোধ" else "Pay now",
                onActionClick = {}
            )
            StatusBanner(
                message = if (isBengali) "ড্রাইভ ব্যাকআপ সফলভাবে সম্পন্ন হয়েছে" else "Drive sync completed 5m ago",
                variant = StatusBannerVariant.Success
            )

            // --- 7. SKELETON / SHIMMER ---
            SectionHeader(title = if (isBengali) "৭. স্কেলিটন শিমার (SkeletonBox)" else "7. SkeletonBox & ShimmerList")
            ShimmerList(itemCount = 2)

            // --- 8. EMPTY STATE ---
            SectionHeader(title = if (isBengali) "৮. এম্পটি স্টেট (EmptyState)" else "8. EmptyState with Templates")
            EmptyState(
                title = if (isBengali) "কোনো বিল তালিকাভুক্ত নেই" else "No upcoming bills",
                body = if (isBengali) "দ্রুত যুক্ত করতে নিচের টেমপ্লেট থেকে বেছে নিন" else "Choose a starter template below to track utility bills.",
                buttonText = if (isBengali) "নতুন বিল যোগ করুন" else "Add custom bill",
                onButtonClick = {},
                templates = listOf(
                    EmptyStateTemplate(title = if (isBengali) "বিদ্যুৎ" else "Electricity", icon = Icons.Outlined.Bolt, onClick = {}),
                    EmptyStateTemplate(title = if (isBengali) "ওয়াইফাই" else "Wi-Fi", icon = Icons.Outlined.Wifi, onClick = {})
                )
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// =========================================================================
// COMPOSE PREVIEWS (Light, Dark, AMOLED x English, Bengali)
// =========================================================================

@Preview(name = "Dark - English", showBackground = true)
@Composable
fun PreviewComponentGalleryDarkEn() {
    ExpenseTrackerTheme(themeMode = "dark") {
        ComponentGalleryScreen(isBengali = false)
    }
}

@Preview(name = "Light - English", showBackground = true)
@Composable
fun PreviewComponentGalleryLightEn() {
    ExpenseTrackerTheme(themeMode = "light") {
        ComponentGalleryScreen(isBengali = false)
    }
}

@Preview(name = "AMOLED - English", showBackground = true)
@Composable
fun PreviewComponentGalleryAmoledEn() {
    ExpenseTrackerTheme(themeMode = "amoled") {
        ComponentGalleryScreen(isBengali = false)
    }
}

@Preview(name = "Dark - Bengali", showBackground = true)
@Composable
fun PreviewComponentGalleryDarkBn() {
    ExpenseTrackerTheme(themeMode = "dark") {
        ComponentGalleryScreen(isBengali = true)
    }
}

@Preview(name = "Light - Bengali", showBackground = true)
@Composable
fun PreviewComponentGalleryLightBn() {
    ExpenseTrackerTheme(themeMode = "light") {
        ComponentGalleryScreen(isBengali = true)
    }
}
