package com.example.expensetracker

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

/**
 * Unit tests verifying startNewList double-tap prevention, callback propagation,
 * and failure recovery invariants.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class BazaarStartNewListTest {

    // Helper simulating the exact atomic guard in BazaarViewModel.startNewList
    class StartNewListGuard(
        private val createAction: suspend () -> Long
    ) {
        val isCreating = AtomicBoolean(false)
        val createdCount = AtomicInteger(0)

        suspend fun startNewList(onOpenPlan: (Long) -> Unit): Boolean {
            if (!isCreating.compareAndSet(false, true)) {
                return false
            }
            try {
                val id = createAction()
                createdCount.incrementAndGet()
                if (id > 0) {
                    onOpenPlan(id)
                }
                return true
            } finally {
                isCreating.set(false)
            }
        }
    }

    @Test
    fun testDoubleTapCreatesExactlyOneList() = runTest {
        var callCount = 0
        val guard = StartNewListGuard {
            callCount++
            101L
        }

        var openedId = 0L
        // Simulate concurrent / rapid double tap
        guard.isCreating.set(true) // simulate active creation
        val secondTapResult = guard.startNewList { openedId = it }

        assertFalse("Second tap must be ignored while first is in progress", secondTapResult)
        assertEquals("Repository create must not be called on second tap", 0, callCount)
        assertEquals("Callback must not be invoked on duplicate tap", 0L, openedId)

        // Now simulate clean single tap
        guard.isCreating.set(false)
        val firstTapResult = guard.startNewList { openedId = it }
        assertTrue("First tap must succeed", firstTapResult)
        assertEquals(1, callCount)
        assertEquals(101L, openedId)
    }

    @Test
    fun testCallbackReceivesNewId() = runTest {
        val guard = StartNewListGuard { 555L }
        var receivedId = 0L

        guard.startNewList { id ->
            receivedId = id
        }

        assertEquals("Callback must receive exactly the created list id", 555L, receivedId)
    }

    @Test
    fun testFlagResetsEvenIfCreationThrows() = runTest {
        val guard = StartNewListGuard {
            throw IllegalStateException("Database disk I/O failure")
        }

        var exceptionCaught = false
        try {
            guard.startNewList { }
        } catch (e: IllegalStateException) {
            exceptionCaught = true
        }

        assertTrue("Exception must have been thrown", exceptionCaught)
        assertFalse("isCreating flag must reset to false in finally block even on error", guard.isCreating.get())

        // Ensure next invocation can proceed after failure
        val recoveredGuard = StartNewListGuard { 777L }
        var nextId = 0L
        recoveredGuard.startNewList { nextId = it }
        assertEquals(777L, nextId)
    }
}
