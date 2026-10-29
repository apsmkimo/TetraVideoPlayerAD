// SMCPKG_SUPPORT>>>Cursor049
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

import kotlin.math.roundToInt

/**
 * Relative system-volume drag.
 *
 * A physical travel of [FULL_RANGE_CM] maps onto the whole 0–100% range.
 * Pixels come from the display's ydpi so the same finger distance feels the
 * same on different screens. The result depends only on the level where the
 * finger landed plus how far it has moved — never on where it sits on screen.
 */
object VolumeGestureMath {
    const val FULL_RANGE_CM = 4f
    private const val CM_PER_INCH = 2.54f
    private const val FALLBACK_YDPI = 160f

    fun fullRangePixels(ydpi: Float): Float {
        val dpi = if (ydpi.isFinite() && ydpi >= 50f) ydpi else FALLBACK_YDPI
        return (FULL_RANGE_CM / CM_PER_INCH) * dpi
    }

    /**
     * @param deltaYPx finger movement since the gesture's zero point. Positive is
     * down (quieter). Negative is up (louder).
     */
    fun fractionAfterDrag(startFraction: Float, deltaYPx: Float, fullRangePx: Float): Float {
        val travel = if (fullRangePx.isFinite() && fullRangePx > 1f) fullRangePx else 1f
        if (!startFraction.isFinite() || !deltaYPx.isFinite()) {
            return startFraction.coerceIn(0f, 1f)
        }
        return (startFraction - (deltaYPx / travel)).coerceIn(0f, 1f)
    }

    fun streamIndexForFraction(fraction: Float, maxIndex: Int): Int {
        if (maxIndex <= 0) return 0
        return (fraction.coerceIn(0f, 1f) * maxIndex).roundToInt().coerceIn(0, maxIndex)
    }
}
// SMCPKG_SUPPORT<<<Cursor049
