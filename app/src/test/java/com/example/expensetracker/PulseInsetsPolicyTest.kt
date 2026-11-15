package com.example.expensetracker

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class PulseInsetsPolicyTest {

    private fun readFile(relativePath: String): String {
        val root = File(System.getProperty("user.dir") ?: ".")
        var target = File(root, "app/src/main/java/$relativePath")
        if (!target.exists() && root.name == "app") {
            target = File(root, "src/main/java/$relativePath")
        }
        if (target.exists()) return target.readText()
        val alt = File(root, "src/main/java/$relativePath")
        if (alt.exists()) return alt.readText()
        throw IllegalArgumentException("File not found: $relativePath at ${target.absolutePath}")
    }

    private fun readDocFile(relativePath: String): String {
        val root = File(System.getProperty("user.dir") ?: ".")
        var target = File(root, relativePath)
        if (!target.exists() && root.name == "app") {
            target = File(root.parentFile, relativePath)
        }
        if (target.exists()) return target.readText()
        throw IllegalArgumentException("Doc not found: $relativePath at ${target.absolutePath}")
    }

    @Test
    fun testShellProvidesCentralInsets() {
        val adaptiveScaffold = readFile("com/example/expensetracker/ui/navigation/AdaptiveScaffold.kt")
        assertTrue(
            "AdaptiveScaffold must apply pulseScreenInsets to content container",
            adaptiveScaffold.contains("pulseScreenInsets()")
        )
    }

    @Test
    fun testScreensDoNotUsePerScreenStatusBarHacks() {
        val planScreen = readFile("com/example/expensetracker/ui/bazaar/BazaarPlanScreen.kt")
        assertFalse(
            "BazaarPlanScreen must not hardcode statusBarsPadding; gets it centrally from shell",
            planScreen.contains(".statusBarsPadding()")
        )

        val topBar = readFile("com/example/expensetracker/ui/components/PulseTopBar.kt")
        assertFalse(
            "PulseTopBar must not hardcode statusBarsPadding; gets it centrally from shell",
            topBar.contains(".statusBarsPadding()")
        )
    }

    @Test
    fun testRedesignPromptContainsShellInsetsRule() {
        val prompt = readDocFile("docs/REDESIGN_PROMPT.md")
        assertTrue(
            "REDESIGN_PROMPT.md must mandate central insets from the shell",
            prompt.contains("Every screen gets insets from the shell via `Modifier.pulseScreenInsets()`")
        )
    }
}
