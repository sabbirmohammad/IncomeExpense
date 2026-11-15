package com.example.expensetracker

import com.example.expensetracker.ui.components.PulseInsetPolicy
import com.example.expensetracker.ui.components.PulseWindowFormat
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class PulseInsetsPolicyTest {

    @Test
    fun testCompactPortraitWithDockDelegatesBottomToDock() {
        val config = PulseInsetPolicy.resolveContentInsets(
            format = PulseWindowFormat.COMPACT,
            hasBottomDock = true
        )
        assertTrue("Top inset must be applied for status bar", config.applyTop)
        assertFalse("Bottom inset must NOT be applied to content; handled by dock", config.applyBottom)
        assertFalse("Horizontal insets not needed in portrait", config.applyStart || config.applyEnd)
        assertTrue(config.isBottomHandledByDock)
    }

    @Test
    fun testCompactPortraitWithoutDockAppliesBottomInset() {
        val config = PulseInsetPolicy.resolveContentInsets(
            format = PulseWindowFormat.COMPACT,
            hasBottomDock = false
        )
        assertTrue("Top inset must be applied", config.applyTop)
        assertTrue("Bottom inset must be applied when no dock exists", config.applyBottom)
        assertFalse(config.isBottomHandledByDock)
    }

    @Test
    fun testCompactLandscapeAppliesHorizontalCutoutAndTopInsets() {
        val config = PulseInsetPolicy.resolveContentInsets(
            format = PulseWindowFormat.COMPACT_LANDSCAPE,
            hasBottomDock = false
        )
        assertTrue("Top inset must be applied in landscape", config.applyTop)
        assertTrue("Start inset must be applied for left camera cutout / rail", config.applyStart)
        assertTrue("End inset must be applied for right system bar / gesture area", config.applyEnd)
        assertFalse("Bottom inset not applied to content in landscape", config.applyBottom)
    }

    @Test
    fun testMediumAndExpandedConfigurations() {
        val medium = PulseInsetPolicy.resolveContentInsets(PulseWindowFormat.MEDIUM, hasBottomDock = true)
        assertTrue(medium.applyTop)
        assertTrue(medium.applyEnd)
        assertFalse(medium.applyStart) // Handled by rail

        val expanded = PulseInsetPolicy.resolveContentInsets(PulseWindowFormat.EXPANDED, hasBottomDock = false)
        assertTrue(expanded.applyTop)
        assertTrue(expanded.applyEnd)
        assertFalse(expanded.applyStart) // Handled by side panel
    }

    // Single minimal structural check: ensures shell container wires the central inset policy to prevent regressing to per-screen insets.
    @Test
    fun testShellContainerWiresCentralInsets() {
        val root = File(System.getProperty("user.dir") ?: ".")
        var target = File(root, "app/src/main/java/com/example/expensetracker/ui/navigation/AdaptiveScaffold.kt")
        if (!target.exists() && root.name == "app") {
            target = File(root, "src/main/java/com/example/expensetracker/ui/navigation/AdaptiveScaffold.kt")
        }
        if (!target.exists()) {
            target = File(root.parentFile, "app/src/main/java/com/example/expensetracker/ui/navigation/AdaptiveScaffold.kt")
        }
        assertTrue("AdaptiveScaffold.kt must exist", target.exists())
        assertTrue(
            "AdaptiveScaffold must centrally apply pulseScreenInsets() to content containers",
            target.readText().contains(".pulseScreenInsets()")
        )
    }
}
