package com.example.expensetracker.ui.transactions

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.data.model.getLocalizedCategoryName
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.AppChip
import com.example.expensetracker.ui.components.LocalAppStrings

@Composable
fun TransactionFilterCategorySection(
    categories: List<Category>,
    selectedCategoryId: Long?,
    onCategorySelected: (Long?) -> Unit,
    modifier: Modifier = Modifier
) {
    if (categories.isEmpty()) return

    val appColors = LocalAppColors.current
    val strings = LocalAppStrings.current

    Column(modifier = modifier) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = strings.category,
            style = MaterialTheme.typography.labelMedium.copy(fontSize = 13.sp, fontWeight = FontWeight.SemiBold),
            color = appColors.textSecondary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AppChip(
                label = if (strings.isBangla) "সকল ক্যাটাগরি" else "All Categories",
                selected = selectedCategoryId == null,
                onClick = { onCategorySelected(null) }
            )
            categories.forEach { cat ->
                AppChip(
                    label = getLocalizedCategoryName(cat.name, strings.isBangla),
                    selected = selectedCategoryId == cat.id,
                    onClick = { onCategorySelected(cat.id) }
                )
            }
        }
    }
}
