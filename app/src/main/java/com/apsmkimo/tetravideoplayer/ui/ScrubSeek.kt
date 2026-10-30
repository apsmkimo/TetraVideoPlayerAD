// SMCPKG_SUPPORT>>>Cursor052
/*
 * TetraVideoPlayer
 * Copyright (C) 2026 apsmkimo
 * All rights reserved.
 *
 * This software is proprietary. Unauthorized copying, distribution,
 * modification, or use is strictly prohibited except as expressly
 * permitted in writing by the copyright holder.
 */
package com.apsmkimo.tetravideoplayer.ui

/**
 * How often a seek-bar drag may ask the decoder for a new preview frame.
 * The latest finger position is kept and issued on the next interval.
 */
internal object ScrubSeek {
    const val PREVIEW_INTERVAL_MS = 80L

    fun shouldPreview(nowMs: Long, lastPreviewAtMs: Long): Boolean {
        if (lastPreviewAtMs <= 0L) return true
        return nowMs - lastPreviewAtMs >= PREVIEW_INTERVAL_MS
    }
}
// SMCPKG_SUPPORT<<<Cursor052
