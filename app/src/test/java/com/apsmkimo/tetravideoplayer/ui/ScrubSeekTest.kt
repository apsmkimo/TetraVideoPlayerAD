// SMCPKG_SUPPORT>>>Cursor052
package com.apsmkimo.tetravideoplayer.ui

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ScrubSeekTest {
    @Test
    fun firstPreviewIsImmediate() {
        assertTrue(ScrubSeek.shouldPreview(nowMs = 1_000L, lastPreviewAtMs = 0L))
    }

    @Test
    fun previewsAreAtLeastOneIntervalApart() {
        val last = 5_000L
        assertFalse(ScrubSeek.shouldPreview(last + 49L, last))
        assertFalse(ScrubSeek.shouldPreview(last + 79L, last))
        assertTrue(ScrubSeek.shouldPreview(last + 80L, last))
        assertTrue(ScrubSeek.shouldPreview(last + 100L, last))
    }
}
// SMCPKG_SUPPORT<<<Cursor052
