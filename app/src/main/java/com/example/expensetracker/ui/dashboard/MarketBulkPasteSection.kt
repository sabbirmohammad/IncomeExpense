package com.example.expensetracker.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.common.MarketListParser
import com.example.expensetracker.data.model.MarketItem
import com.example.expensetracker.theme.ColorTextSecondary
import com.example.expensetracker.theme.LocalAppColors
import com.example.expensetracker.ui.components.DateTimeHelper
import com.example.expensetracker.ui.components.rememberAppHaptics

@Composable
fun MarketBulkPasteSection(
    isBangla: Boolean,
    onListImported: (title: String, items: List<MarketItem>) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptics = rememberAppHaptics()
    var bulkInputText by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 16.dp)
    ) {
        Text(
            text = if (isBangla) "হোয়াটসঅ্যাপ থেকে কপি করা পুরো লিস্টটি এখানে পেস্ট করুন:"
            else "Paste your copied WhatsApp/Notes list below:",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            ),
            color = ColorTextSecondary
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = bulkInputText,
            onValueChange = { bulkInputText = it },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            placeholder = {
                Text(
                    text = if (isBangla)
                        "যেমন:\n১. আলু ২ কেজি\n২. পেঁয়াজ ১ কেজি\n৩. সয়াবিন তেল ২ লিটার\n৪. ডিম ১ ডজন\n৫. সাবান ২ পিস\n৬. মুরগি দেড় কেজি\n৭. কাঁচামরিচ ২৫০ গ্রাম"
                    else
                        "e.g.\nPotato 2kg\nOnion 1 kg\nSoybean oil 2L\nEggs 12 pcs\nSoap 2 pcs\nChicken 1.5 kg",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                    color = ColorTextSecondary.copy(alpha = 0.6f)
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF25D366),
                unfocusedBorderColor = LocalAppColors.current.border,
                focusedContainerColor = LocalAppColors.current.background,
                unfocusedContainerColor = LocalAppColors.current.background
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        Button(
            onClick = {
                if (bulkInputText.isNotBlank()) {
                    haptics.mediumClick()
                    val parsedItems = MarketListParser.parseBulkText(bulkInputText)
                    if (parsedItems.isNotEmpty()) {
                        val defaultTitle = DateTimeHelper.formatMarketTripTitle(System.currentTimeMillis(), isBangla)
                        onListImported(defaultTitle, parsedItems)
                        bulkInputText = ""
                    }
                }
            },
            enabled = bulkInputText.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF25D366),
                disabledContainerColor = Color(0xFF25D366).copy(alpha = 0.35f)
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = if (isBangla) "ফর্দ যুক্ত করুন" else "Import Market List",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.White
                )
            }
        }
    }
}
