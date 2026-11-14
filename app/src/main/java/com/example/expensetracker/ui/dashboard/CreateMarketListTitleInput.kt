package com.example.expensetracker.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.AppColors
import com.example.expensetracker.theme.HairlineInkStroke
import com.example.expensetracker.ui.components.AppHaptics

@Composable
fun CreateMarketListTitleInput(
    listTitle: String,
    onTitleChange: (String) -> Unit,
    defaultTitle: String,
    quickTitles: List<Pair<String, ImageVector>>,
    isBangla: Boolean,
    appColors: AppColors,
    haptics: AppHaptics,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = if (isBangla) "ফর্দের নাম" else "List Title",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, fontSize = 13.sp),
            color = appColors.textPrimary
        )
        OutlinedTextField(
            value = listTitle,
            onValueChange = onTitleChange,
            placeholder = { Text(defaultTitle) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
            items(quickTitles) { (titleText, icon) ->
                val isSelected = listTitle == titleText
                Surface(
                    onClick = { haptics.lightTick(); onTitleChange(titleText) },
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) appColors.accentContainer else appColors.cardBackground,
                    border = BorderStroke(HairlineInkStroke, if (isSelected) appColors.accent else appColors.divider),
                    modifier = Modifier.defaultMinSize(minHeight = 48.dp).minimumInteractiveComponentSize()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (isSelected) appColors.accent else appColors.textSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = titleText,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = if (isSelected) appColors.accent else appColors.textPrimary
                        )
                    }
                }
            }
        }
    }
}
