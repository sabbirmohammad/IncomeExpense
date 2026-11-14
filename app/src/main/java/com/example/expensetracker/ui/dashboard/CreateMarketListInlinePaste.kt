package com.example.expensetracker.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.expensetracker.theme.AppColors
import com.example.expensetracker.theme.HairlineInkStroke

@Composable
fun CreateMarketListInlinePasteBox(
    isBangla: Boolean,
    onImportText: (String) -> Unit,
    appColors: AppColors,
    modifier: Modifier = Modifier
) {
    var inlinePasteText by remember { mutableStateOf("") }
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = appColors.cardBackground,
        border = BorderStroke(HairlineInkStroke, appColors.divider),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            OutlinedTextField(
                value = inlinePasteText,
                onValueChange = { inlinePasteText = it },
                placeholder = { Text(if (isBangla) "হোয়াটসঅ্যাপ মেসেজ পেস্ট করুন..." else "Paste WhatsApp message here...") },
                modifier = Modifier.fillMaxWidth().height(100.dp),
                shape = RoundedCornerShape(10.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Button(
                    onClick = {
                        if (inlinePasteText.isNotBlank()) {
                            onImportText(inlinePasteText)
                            inlinePasteText = ""
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = appColors.accent)
                ) {
                    Text(if (isBangla) "যুক্ত করুন" else "Import")
                }
            }
        }
    }
}
