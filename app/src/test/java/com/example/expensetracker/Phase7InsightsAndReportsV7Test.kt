package com.example.expensetracker

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class Phase7InsightsAndReportsV7Test {

    private val basePath: String = if (File("src/main/java").exists()) "src/main/java" else "app/src/main/java"

    private fun readFile(relativePath: String): String {
        val file = File("$basePath/$relativePath")
        assertTrue("Target file ${file.path} must exist", file.exists())
        return file.readText()
    }

    @Test
    fun testDonutChartSpecInvariants() {
        val content = readFile("com/example/expensetracker/ui/components/AnalyticsCharts.kt")

        assertTrue("DonutChart must have 160dp diameter", content.contains("160.dp"))
        assertTrue("DonutChart must support tap-to-highlight", content.contains("detectTapGestures") && content.contains("selectedIndex"))
        assertTrue("DonutChart must respect reduceMotion", content.contains("motion.reduceMotion"))
        assertTrue("DonutChart must format center amount with tabular figures", content.contains("fontFeatureSettings = \"tnum\""))
        assertTrue("DonutChart must include TalkBack accessibility summary", content.contains("accessibilitySummary") && content.contains("semantics"))
    }

    @Test
    fun testPaymentMethodDonutChartSpecInvariants() {
        val content = readFile("com/example/expensetracker/ui/components/PaymentMethodDonutChart.kt")

        assertTrue("PaymentMethodDonutChart must have 160dp diameter", content.contains("160.dp"))
        assertTrue("PaymentMethodDonutChart must support tap-to-highlight", content.contains("detectTapGestures") && content.contains("selectedIndex"))
        assertTrue("PaymentMethodDonutChart must respect reduceMotion", content.contains("motion.reduceMotion"))
        assertTrue("PaymentMethodDonutChart must format center amount with tabular figures", content.contains("fontFeatureSettings = \"tnum\""))
        assertTrue("PaymentMethodDonutChart must include TalkBack accessibility summary", content.contains("accessibilitySummary") && content.contains("semantics"))
    }

    @Test
    fun testSpendingBarChartSpecInvariants() {
        val content = readFile("com/example/expensetracker/ui/components/SpendingBarChart.kt")

        assertTrue("SpendingBarChart must include interactive tap tooltip", content.contains("selectedSpend") && content.contains("AnimatedVisibility"))
        assertTrue("SpendingBarChart tooltip must use tabular figures", content.contains("fontFeatureSettings = \"tnum\""))
        assertTrue("SpendingBarChart must highlight current day / Today", content.contains("isToday") || content.contains("isSameDay"))
        assertTrue("SpendingBarChart must respect reduceMotion", content.contains("motion.reduceMotion"))
        assertTrue("SpendingBarChart must include TalkBack content description semantics", content.contains("a11ySummary") && content.contains("semantics"))
    }

    @Test
    fun testSparklineComponent() {
        val content = readFile("com/example/expensetracker/ui/components/Sparkline.kt")

        assertTrue("Sparkline must use smooth cubic Bézier curves", content.contains("cubicTo"))
        assertTrue("Sparkline must support gradient fill", content.contains("Brush.verticalGradient") && content.contains("showGradientFill"))
        assertTrue("Sparkline must support end dot", content.contains("showEndDot") && content.contains("drawCircle"))
        assertTrue("Sparkline must respect reduceMotion", content.contains("motion.reduceMotion"))
    }

    @Test
    fun testAnalyticsFilterBarDirectPeriodChips() {
        val content = readFile("com/example/expensetracker/ui/analytics/AnalyticsFilterBar.kt")

        assertTrue("AnalyticsFilterBar must use AppChip for direct period selection", content.contains("AppChip("))
        assertTrue("AnalyticsFilterBar must support TODAY", content.contains("TimeFilter.TODAY"))
        assertTrue("AnalyticsFilterBar must support THIS_WEEK", content.contains("TimeFilter.THIS_WEEK"))
        assertTrue("AnalyticsFilterBar must support THIS_MONTH", content.contains("TimeFilter.THIS_MONTH"))
        assertTrue("AnalyticsFilterBar must support THIS_YEAR", content.contains("TimeFilter.THIS_YEAR"))
        assertTrue("AnalyticsFilterBar must support ALL", content.contains("TimeFilter.ALL"))
        assertTrue("AnalyticsFilterBar must support Type dropdown", content.contains("onTypeChange"))
    }

    @Test
    fun testAnalyticsKpiStripTabularFiguresAndHeight() {
        val content = readFile("com/example/expensetracker/ui/analytics/AnalyticsKpiStrip.kt")

        assertTrue("AnalyticsKpiStrip Row must use height(IntrinsicSize.Max)", content.contains("height(IntrinsicSize.Max)"))
        assertTrue("AnalyticsKpiStrip cards must use fillMaxHeight()", content.contains(".fillMaxHeight()"))
        assertTrue("Card 3 must use concise label 'আগের তুলনা'", content.contains("আগের তুলনা"))
        assertTrue("AnalyticsKpiStrip must use tabular figures for amounts", content.contains("fontFeatureSettings = \"tnum\""))
    }

    @Test
    fun testStatementPreviewCardContract() {
        val content = readFile("com/example/expensetracker/ui/reports/StatementPreviewCard.kt")

        assertTrue("StatementPreviewCard must display STATEMENT PREVIEW header", content.contains("STATEMENT PREVIEW") || content.contains("স্টেটমেন্ট প্রিভিউ"))
        assertTrue("StatementPreviewCard must display ledger net balance in displaySmall", content.contains("displaySmall"))
        assertTrue("StatementPreviewCard must format balance and breakdown in tabular figures", content.contains("fontFeatureSettings = \"tnum\""))
        assertTrue("StatementPreviewCard must display summary breakdown for income, expense and count", content.contains("summary.totalIncome") && content.contains("summary.totalExpense"))
    }

    @Test
    fun testReportExportActionsCardTouchTargets() {
        val content = readFile("com/example/expensetracker/ui/reports/ReportExportActionsCard.kt")

        assertTrue("Export PDF button must have 48dp height", content.contains(".height(48.dp)"))
        assertTrue("Export CSV button must have 48dp height", content.contains("exportCsv") && content.contains("height(48.dp)"))
        assertTrue("View PDF button must have 48dp height", content.contains("viewPdf") && content.contains("height(48.dp)"))
        assertFalse("No button should use substandard 44dp height", content.contains("44.dp"))
    }

    @Test
    fun testReportsScreenIntegratesStatementPreview() {
        val content = readFile("com/example/expensetracker/ui/reports/ReportsScreen.kt")

        assertTrue("ReportsScreen must integrate StatementPreviewCard", content.contains("StatementPreviewCard("))
        assertTrue("ReportsScreen must use EmptyState component", content.contains("EmptyState("))
    }
}
