package com.example.expensetracker.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.data.model.getLocalizedCategoryName
import com.example.expensetracker.theme.HairlineInkStroke
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.RadiusS

@Composable
internal fun CategoryChipItem(
    category: Category,
    isSelected: Boolean,
    isBangla: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val catColor = remember(category.colorHex) {
        CategoryIconHelper.parseColor(category.colorHex)
    }
    val icon = remember(category.icon) {
        CategoryIconHelper.getIcon(category.icon)
    }
    val label = remember(category.name, isBangla) {
        getLocalizedCategoryName(category.name, isBangla)
    }

    val backgroundColor = when {
        isSelected -> appColors.accentContainer
        else -> appColors.surfaceVariant
    }

    val borderStroke = when {
        isSelected -> BorderStroke(1.5.dp, catColor)
        appColors.isDark -> null
        else -> BorderStroke(HairlineInkStroke, appColors.border)
    }

    val contentColor = when {
        isSelected -> appColors.textPrimary
        else -> appColors.textSecondary
    }

    Surface(
        shape = RoundedCornerShape(RadiusS),
        color = backgroundColor,
        border = borderStroke,
        modifier = modifier
            .defaultMinSize(minHeight = 44.dp)
            .minimumInteractiveComponentSize()
            .clickable { onClick() }
            .semantics {
                role = Role.RadioButton
                selected = isSelected
                contentDescription = "$label, ${if (isSelected) "selected" else "not selected"}"
            }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) catColor else appColors.textSecondary,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 13.5.sp
                ),
                color = contentColor
            )
        }
    }
}
