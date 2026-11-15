package com.example.expensetracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.LocalAppColors

@Composable
fun SpendingBarChartLegend(
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val strings = LocalAppStrings.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(appColors.income))
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = strings.income,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
            color = appColors.textSecondary
        )

        Spacer(modifier = Modifier.width(20.dp))

        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(appColors.expense))
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = strings.expense,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
            color = appColors.textSecondary
        )
    }
}
