package com.example.expensetracker.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.expensetracker.theme.LocalAppColors

@Composable
fun GalleryCardsAndBannersSection(
    isBengali: Boolean,
    selectedSegment: Int,
    onSegmentSelected: (Int) -> Unit
) {
    val colors = LocalAppColors.current

    SectionHeader(title = if (isBengali) "৪. সেগমেন্টেড কন্ট্রোল" else "4. SegmentedControl (48dp)")
    SegmentedControl(
        items = if (isBengali) listOf("খরচ", "আয়", "স্থানান্তর") else listOf("Expense", "Income", "Transfer"),
        selectedIndex = selectedSegment,
        onItemSelected = onSegmentSelected
    )

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
}
