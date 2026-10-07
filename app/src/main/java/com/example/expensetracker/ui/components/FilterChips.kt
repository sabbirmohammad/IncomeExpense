package com.example.expensetracker.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorBorder
import com.example.expensetracker.theme.ColorSurface
import com.example.expensetracker.theme.ColorTextMuted
import com.example.expensetracker.theme.ColorTextSecondary

@Composable
fun PillFilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = CircleShape,
        color = if (selected) ColorAccent else ColorSurface,
        border = if (selected) null else BorderStroke(1.dp, ColorBorder),
        modifier = modifier.clickable { onClick() }
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 12.sp,
                fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal
            ),
            color = if (selected) Color.White else ColorTextSecondary,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
        )
    }
}

@Composable
fun DropdownFilterChip(
    text: String,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    activeColor: Color = ColorAccent
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isActive) activeColor.copy(alpha = 0.16f) else ColorSurface,
        border = BorderStroke(
            1.dp,
            if (isActive) activeColor.copy(alpha = 0.55f) else ColorBorder
        ),
        modifier = modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 11.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isActive) activeColor else ColorTextSecondary,
                    modifier = Modifier.size(15.dp)
                )
            }
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 12.sp,
                    fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal
                ),
                color = if (isActive) (if (activeColor == ColorAccent) ColorAccent else activeColor) else ColorTextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = null,
                tint = if (isActive) activeColor else ColorTextMuted,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
