package com.example.expensetracker.ui.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorBackground
import com.example.expensetracker.theme.ColorBorder
import com.example.expensetracker.theme.ColorTextSecondary
import com.example.expensetracker.theme.SmallElementRadius
import com.example.expensetracker.ui.components.CategoryIconHelper
import com.example.expensetracker.ui.components.LocalAppStrings

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategoryIconPickerSection(
    selectedIcon: String,
    onIconSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    var selectedIconGroup by remember { mutableStateOf("All") }

    val displayedIcons = remember(selectedIconGroup) {
        if (selectedIconGroup == "All") CategoryIconHelper.allIcons
        else CategoryIconHelper.allIcons.filter { it.group == selectedIconGroup }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (strings.isBangla) "আইকন নির্বাচন করুন (${displayedIcons.size})" else "Select icon (${displayedIcons.size})",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = ColorTextSecondary
            )
        }
        Spacer(modifier = Modifier.height(6.dp))

        // Category Group Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            CategoryIconHelper.iconGroups.forEach { group ->
                val isSelected = selectedIconGroup == group
                Surface(
                    shape = CircleShape,
                    color = if (isSelected) ColorAccent else ColorBackground,
                    border = if (isSelected) null else BorderStroke(1.dp, ColorBorder),
                    modifier = Modifier.clickable { selectedIconGroup = group }
                ) {
                    Text(
                        text = group,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        color = if (isSelected) Color.White else ColorTextSecondary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 160.dp)
                .verticalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            displayedIcons.forEach { iconItem ->
                val isSel = selectedIcon == iconItem.name
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(SmallElementRadius))
                        .background(if (isSel) ColorAccent.copy(alpha = 0.22f) else ColorBackground)
                        .border(
                            1.5.dp,
                            if (isSel) ColorAccent else ColorBorder,
                            RoundedCornerShape(SmallElementRadius)
                        )
                        .clickable { onIconSelected(iconItem.name) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = CategoryIconHelper.getIcon(iconItem.name),
                        contentDescription = iconItem.label,
                        tint = if (isSel) ColorAccent else ColorTextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
