package com.example.expensetracker.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorBackground
import com.example.expensetracker.theme.ColorBorder
import com.example.expensetracker.theme.ColorTextSecondary
import com.example.expensetracker.ui.components.getAvatarIcon

@Composable
fun EditProfileAvatarPicker(
    selectedAvatar: Int,
    firstNameInput: String,
    lastNameInput: String,
    onAvatarSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val avatarOptions = listOf(
        0 to "Initials",
        1 to "Person",
        2 to "Smile",
        3 to "Star",
        4 to "Rocket",
        5 to "Work"
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        avatarOptions.forEach { (idx, _) ->
            val isSel = selectedAvatar == idx
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (isSel) ColorAccent.copy(alpha = 0.25f) else ColorBackground)
                    .border(
                        width = if (isSel) 2.dp else 1.dp,
                        color = if (isSel) ColorAccent else ColorBorder,
                        shape = CircleShape
                    )
                    .clickable { onAvatarSelected(idx) },
                contentAlignment = Alignment.Center
            ) {
                if (idx == 0) {
                    val init = when {
                        firstNameInput.isNotBlank() && lastNameInput.isNotBlank() ->
                            "${firstNameInput.trim().first()}${lastNameInput.trim().first()}".uppercase()
                        firstNameInput.isNotBlank() -> firstNameInput.trim().take(2).uppercase()
                        else -> "U"
                    }
                    Text(
                        text = init,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = if (isSel) ColorAccent else ColorTextSecondary
                    )
                } else {
                    Icon(
                        imageVector = getAvatarIcon(idx),
                        contentDescription = null,
                        tint = if (isSel) ColorAccent else ColorTextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
