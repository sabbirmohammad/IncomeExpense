package com.example.expensetracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expensetracker.theme.ColorAccent
import com.example.expensetracker.theme.ColorTextPrimary

@Composable
fun AiFormattedMessageView(
    rawText: String,
    isBangla: Boolean,
    modifier: Modifier = Modifier
) {
    val blocks = remember(rawText) { parseAiContent(rawText) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        blocks.forEach { block ->
            when (block) {
                is AiContentBlock.Metrics -> {
                    AiMetricsBlockView(block = block)
                }
                is AiContentBlock.Chart -> {
                    AiChartBlockView(block = block, isBangla = isBangla)
                }
                is AiContentBlock.Table -> {
                    AiTableBlockView(block = block)
                }
                is AiContentBlock.Header -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(top = 8.dp, bottom = 2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(width = 3.dp, height = 14.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(ColorAccent)
                        )
                        Text(
                            text = block.text,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.5.sp,
                                color = ColorTextPrimary
                            )
                        )
                    }
                }
                is AiContentBlock.Bullet -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(top = 7.dp)
                                .size(5.dp)
                                .clip(CircleShape)
                                .background(ColorAccent)
                        )
                        Text(
                            text = formatAnnotatedText(block.text),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = ColorTextPrimary,
                                fontSize = 13.sp,
                                lineHeight = 20.sp
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                is AiContentBlock.Numbered -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(ColorAccent.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = block.index,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    color = ColorAccent
                                )
                            )
                        }
                        Text(
                            text = formatAnnotatedText(block.text),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = ColorTextPrimary,
                                fontSize = 13.sp,
                                lineHeight = 20.sp
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                is AiContentBlock.Paragraph -> {
                    Text(
                        text = formatAnnotatedText(block.text),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = ColorTextPrimary,
                            fontSize = 13.sp,
                            lineHeight = 20.sp
                        ),
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
        }
    }
}
