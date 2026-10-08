package com.example.expensetracker.ui.components

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import com.example.expensetracker.theme.ColorTextPrimary

fun normalizeBengaliNumber(input: String): String {
    val bnDigits = "০১২৩৪৫৬৭৮৯"
    val enDigits = "0123456789"
    var res = input
    for (i in 0..9) {
        res = res.replace(bnDigits[i], enDigits[i])
    }
    return res.replace(Regex("[^0-9.]"), "")
}

sealed class AiContentBlock {
    data class Header(val text: String) : AiContentBlock()
    data class Bullet(val text: String) : AiContentBlock()
    data class Numbered(val index: String, val text: String) : AiContentBlock()
    data class Chart(val items: List<Pair<String, Float>>) : AiContentBlock()
    data class Metrics(val items: List<Pair<String, String>>) : AiContentBlock()
    data class Table(val rows: List<List<String>>) : AiContentBlock()
    data class Paragraph(val text: String) : AiContentBlock()
}

fun parseAiContent(raw: String): List<AiContentBlock> {
    val lines = raw.lines()
    val blocks = mutableListOf<AiContentBlock>()
    var currentParagraph = StringBuilder()

    fun flushParagraph() {
        if (currentParagraph.isNotBlank()) {
            blocks.add(AiContentBlock.Paragraph(currentParagraph.toString().trim()))
            currentParagraph = StringBuilder()
        }
    }

    var i = 0
    while (i < lines.size) {
        val line = lines[i]
        val trimmed = line.trim()
        if (trimmed.isBlank()) {
            flushParagraph()
            i++
            continue
        }

        // Visual Chart Tag: [Chart: মুদি বাজার: 98%, অন্যান্য: 2%]
        val chartMatch = Regex("""\[(?:Chart|Bar|Graph|চার্ট|গ্রাফ):\s*(.*?)\]""", RegexOption.IGNORE_CASE).find(trimmed)
        if (chartMatch != null) {
            flushParagraph()
            val content = chartMatch.groupValues[1]
            val items = content.split(Regex("[,|;]")).mapNotNull { part ->
                val pieces = part.split(":")
                if (pieces.size >= 2) {
                    val label = pieces[0].trim()
                    val cleanVal = normalizeBengaliNumber(pieces[1])
                    val pct = cleanVal.toFloatOrNull() ?: 0f
                    if (pct > 0f) label to pct else null
                } else null
            }
            if (items.isNotEmpty()) {
                blocks.add(AiContentBlock.Chart(items))
                i++
                continue
            }
        }

        // Visual Metrics Tag: [Metrics: আয়: ৳৮৮,০০০ | ব্যয়: ৳৬,১৬,৩৫৫ | নিট: -৳৫,২৮,৩৫৫]
        val metricsMatch = Regex("""\[(?:Metrics|KPI):\s*(.*?)\]""", RegexOption.IGNORE_CASE).find(trimmed)
        if (metricsMatch != null) {
            flushParagraph()
            val content = metricsMatch.groupValues[1]
            val items = content.split("|").mapNotNull { part ->
                val pieces = part.split(":")
                if (pieces.size >= 2) {
                    val label = pieces[0].trim()
                    val value = pieces.drop(1).joinToString(":").trim()
                    label to value
                } else null
            }
            if (items.isNotEmpty()) {
                blocks.add(AiContentBlock.Metrics(items))
                i++
                continue
            }
        }

        // Markdown Table: | Col1 | Col2 |
        if (trimmed.startsWith("|") && trimmed.endsWith("|") && trimmed.count { it == '|' } >= 2) {
            flushParagraph()
            val tableRows = mutableListOf<List<String>>()
            while (i < lines.size) {
                val rowLine = lines[i].trim()
                if (rowLine.startsWith("|") && rowLine.endsWith("|")) {
                    val isSeparator = rowLine.replace("|", "").replace("-", "").replace(":", "").isBlank()
                    if (!isSeparator) {
                        val cells = rowLine.removeSurrounding("|")
                            .split("|")
                            .map { it.trim() }
                        if (cells.isNotEmpty()) tableRows.add(cells)
                    }
                    i++
                } else {
                    break
                }
            }
            if (tableRows.isNotEmpty()) {
                blocks.add(AiContentBlock.Table(tableRows))
                continue
            }
        }

        // Markdown Header: ### Title or ## Title
        if (trimmed.startsWith("###") || trimmed.startsWith("##")) {
            flushParagraph()
            val headerText = trimmed.replace(Regex("^#+\\s*"), "").replace("**", "").trim()
            blocks.add(AiContentBlock.Header(headerText))
        } else if (trimmed.startsWith("**") && trimmed.endsWith("**") && trimmed.length < 60 && !trimmed.contains("\n")) {
            flushParagraph()
            val headerText = trimmed.removeSurrounding("**").trim()
            blocks.add(AiContentBlock.Header(headerText))
        } else if (trimmed.startsWith("•") || trimmed.startsWith("* ") || trimmed.startsWith("- ") || trimmed.startsWith("✦")) {
            flushParagraph()
            val bulletText = trimmed.replace(Regex("^[•*\\-✦]\\s*"), "").trim()
            blocks.add(AiContentBlock.Bullet(bulletText))
        } else if (trimmed.matches(Regex("""^\d+[\.\)]\s+.*"""))) {
            flushParagraph()
            val num = trimmed.substringBefore(".").substringBefore(")").trim()
            val itemText = trimmed.replace(Regex("""^\d+[\.\)]\s*"""), "").trim()
            blocks.add(AiContentBlock.Numbered(num, itemText))
        } else {
            if (currentParagraph.isNotEmpty()) {
                currentParagraph.append("\n")
            }
            currentParagraph.append(trimmed)
        }
        i++
    }
    flushParagraph()
    return blocks
}

fun formatAnnotatedText(text: String): AnnotatedString {
    return buildAnnotatedString {
        val boldRegex = Regex("""\*\*(.*?)\*\*""")
        var currentIndex = 0
        val matches = boldRegex.findAll(text)

        for (match in matches) {
            val start = match.range.first
            val end = match.range.last + 1
            if (start > currentIndex) {
                append(text.substring(currentIndex, start))
            }
            val boldContent = match.groupValues[1]
            pushStyle(
                SpanStyle(
                    fontWeight = FontWeight.Bold,
                    color = ColorTextPrimary
                )
            )
            append(boldContent)
            pop()
            currentIndex = end
        }
        if (currentIndex < text.length) {
            append(text.substring(currentIndex))
        }
    }
}
