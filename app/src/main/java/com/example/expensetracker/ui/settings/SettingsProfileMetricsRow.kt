package com.example.expensetracker.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.CloudDone
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.expensetracker.data.model.UserProfile
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.DateTimeHelper

@Composable
internal fun SettingsProfileMetricsRow(
    userProfile: UserProfile,
    categoriesCount: Int,
    isCloud: Boolean,
    isBangla: Boolean,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Metric 1: Active Categories
        ProfileMiniMetricPill(
            icon = Icons.Outlined.Category,
            iconTint = appColors.accent,
            title = if (isBangla) "ক্যাটাগরি" else "Categories",
            value = "${DateTimeHelper.formatCounter(categoriesCount, isBangla, userProfile.useBengaliNumerals || isBangla)} ${if (isBangla) "টি" else ""}".trim(),
            modifier = Modifier.weight(1f)
        )

        // Metric 2: Currency
        ProfileMiniMetricPill(
            icon = Icons.Outlined.AccountBalanceWallet,
            iconTint = appColors.income,
            title = if (isBangla) "মুদ্রা" else "Currency",
            value = "${userProfile.currencyCode} (${userProfile.currencySymbol})",
            modifier = Modifier.weight(1.1f)
        )

        // Metric 3: Backup Sync Mode
        ProfileMiniMetricPill(
            icon = if (isCloud) Icons.Outlined.CloudDone else Icons.Outlined.CloudOff,
            iconTint = if (isCloud) appColors.income else appColors.textMuted,
            title = if (isBangla) "ক্লাউড সিঙ্ক" else "Cloud Sync",
            value = if (isCloud) (if (isBangla) "সক্রিয়" else "Active") else (if (isBangla) "লোকাল" else "Offline"),
            modifier = Modifier.weight(1f)
        )
    }
}
