package com.example.expensetracker.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Category
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorBorder
import com.example.expensetracker.theme.ColorDangerExpense
import com.example.expensetracker.theme.ColorTextSecondary
import com.example.expensetracker.ui.components.AppHaptics
import com.example.expensetracker.ui.components.CategoryColorHelper
import com.example.expensetracker.ui.components.CategoryIconHelper
import com.example.expensetracker.ui.components.LocalAppStrings

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategoryColorPickerSection(
    selectedColor: String,
    onColorSelected: (String) -> Unit,
    categories: List<Category>,
    editingCategoryId: Long?,
    colorWarningMessage: String?,
    onWarningChange: (String?) -> Unit,
    haptics: AppHaptics,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = strings.selectUniqueColor,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, fontWeight = FontWeight.Medium),
                color = ColorTextSecondary
            )
            Text(
                text = "🎲 " + strings.pickNextColor,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    color = ColorAccent,
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .clickable {
                        val nextColor = CategoryColorHelper.getAvailableColor(
                            categories.filter { !it.colorHex.equals(selectedColor, ignoreCase = true) },
                            editingCategoryId
                        )
                        onColorSelected(nextColor)
                        onWarningChange(null)
                        haptics.lightTick()
                    }
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CategoryColorHelper.distinctColors.forEach { hex ->
                val parsed = CategoryIconHelper.parseColor(hex)
                val isSel = selectedColor.equals(hex, ignoreCase = true)
                val usedBy = CategoryColorHelper.getCategoryUsingColor(hex, categories, editingCategoryId)
                val isTaken = usedBy != null

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(if (isTaken) parsed.copy(alpha = 0.28f) else parsed)
                        .then(
                            when {
                                isSel -> Modifier.border(2.5.dp, Color.White, CircleShape)
                                isTaken -> Modifier.border(1.dp, ColorBorder.copy(alpha = 0.4f), CircleShape)
                                else -> Modifier.border(1.dp, ColorBorder, CircleShape)
                            }
                        )
                        .clickable {
                            if (isTaken) {
                                haptics.lightTick()
                                onWarningChange(strings.colorUsedByFormat(usedBy.getLocalizedName(strings.isBangla)))
                            } else {
                                haptics.lightTick()
                                onColorSelected(hex)
                                onWarningChange(null)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (isSel) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "Selected",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    } else if (isTaken) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.6f))
                        )
                    }
                }
            }
        }

        if (colorWarningMessage != null) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = colorWarningMessage,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.5.sp),
                color = ColorDangerExpense
            )
        }
    }
}
