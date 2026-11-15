package com.example.expensetracker

import android.content.res.Configuration
import androidx.compose.ui.unit.dp
import com.example.expensetracker.ui.adaptive.WindowClass
import com.example.expensetracker.ui.adaptive.WindowHeightClass
import com.example.expensetracker.ui.adaptive.WindowWidthClass
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WindowClassTest {

    @Test
    fun testWidthBreakpoints() {
        // Compact: < 600dp
        assertEquals(WindowWidthClass.COMPACT, WindowWidthClass.fromWidth(320.dp))
        assertEquals(WindowWidthClass.COMPACT, WindowWidthClass.fromWidth(360.dp))
        assertEquals(WindowWidthClass.COMPACT, WindowWidthClass.fromWidth(412.dp))
        assertEquals(WindowWidthClass.COMPACT, WindowWidthClass.fromWidth(599.dp))

        // Medium: 600dp <= width < 840dp
        assertEquals(WindowWidthClass.MEDIUM, WindowWidthClass.fromWidth(600.dp))
        assertEquals(WindowWidthClass.MEDIUM, WindowWidthClass.fromWidth(720.dp))
        assertEquals(WindowWidthClass.MEDIUM, WindowWidthClass.fromWidth(839.dp))

        // Expanded: >= 840dp
        assertEquals(WindowWidthClass.EXPANDED, WindowWidthClass.fromWidth(840.dp))
        assertEquals(WindowWidthClass.EXPANDED, WindowWidthClass.fromWidth(1024.dp))
        assertEquals(WindowWidthClass.EXPANDED, WindowWidthClass.fromWidth(1280.dp))
    }

    @Test
    fun testHeightBreakpoints() {
        // Compact: < 480dp
        assertEquals(WindowHeightClass.COMPACT, WindowHeightClass.fromHeight(400.dp))
        assertEquals(WindowHeightClass.COMPACT, WindowHeightClass.fromHeight(479.dp))

        // Medium: 480dp <= height <= 900dp
        assertEquals(WindowHeightClass.MEDIUM, WindowHeightClass.fromHeight(480.dp))
        assertEquals(WindowHeightClass.MEDIUM, WindowHeightClass.fromHeight(800.dp))
        assertEquals(WindowHeightClass.MEDIUM, WindowHeightClass.fromHeight(900.dp))

        // Expanded: > 900dp
        assertEquals(WindowHeightClass.EXPANDED, WindowHeightClass.fromHeight(901.dp))
        assertEquals(WindowHeightClass.EXPANDED, WindowHeightClass.fromHeight(1200.dp))
    }

    @Test
    fun testLandscapeDetection() {
        // Portrait phone (360x640)
        val portraitPhone = WindowClass.calculate(360.dp, 640.dp, Configuration.ORIENTATION_PORTRAIT)
        assertTrue(portraitPhone.isCompact)
        assertFalse(portraitPhone.isLandscape)

        // Landscape phone (640x360)
        val landscapePhone = WindowClass.calculate(640.dp, 360.dp, Configuration.ORIENTATION_LANDSCAPE)
        assertTrue(landscapePhone.isMedium)
        assertTrue(landscapePhone.isLandscape)
        assertEquals(WindowHeightClass.COMPACT, landscapePhone.heightClass)

        // Tablet landscape (1280x800)
        val tabletLandscape = WindowClass.calculate(1280.dp, 800.dp, Configuration.ORIENTATION_LANDSCAPE)
        assertTrue(tabletLandscape.isExpanded)
        assertTrue(tabletLandscape.isLandscape)

        // Foldable unfolded (840x960)
        val foldable = WindowClass.calculate(840.dp, 960.dp, Configuration.ORIENTATION_PORTRAIT)
        assertTrue(foldable.isExpanded)
        assertFalse(foldable.isLandscape)
    }
}
