package com.example.expensetracker.ui.accounts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.data.model.Account
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.theme.RadiusS
import com.example.expensetracker.ui.components.AccountBrandStyle
import com.example.expensetracker.ui.components.LocalAppStrings

@Composable
fun AccountCardIdentityRow(
    account: Account,
    brandStyle: AccountBrandStyle,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val appColors = LocalAppColors.current

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(RadiusS))
                .background(brandStyle.gradient),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = brandStyle.icon,
                contentDescription = null,
                tint = if (brandStyle.isDarkCard) Color.White else Color.Black,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = account.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = appColors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (account.isDefault) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = appColors.accentContainer
                    ) {
                        Text(
                            text = "DEFAULT",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = appColors.accent,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = when {
                    account.institutionName.isNotBlank() && account.accountNumber.isNotBlank() ->
                        "${account.institutionName} • •••• ${account.accountNumber.takeLast(4)}"
                    account.institutionName.isNotBlank() -> account.institutionName
                    account.accountNumber.isNotBlank() -> "•••• ${account.accountNumber.takeLast(4)}"
                    else -> account.type.getDisplayName(strings.isBangla)
                },
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = appColors.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
