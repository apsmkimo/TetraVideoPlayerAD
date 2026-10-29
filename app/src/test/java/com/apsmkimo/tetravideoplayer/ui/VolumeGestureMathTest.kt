// SMCPKG_SUPPORT>>>Cursor049
package com.apsmkimo.tetravideoplayer.ui

import org.junit.Assert.assertEquals
import org.junit.Test

class VolumeGestureMathTest {
    @Test
    fun fourCentimetersUsesYdpi() {
        // 254 dpi → 100 px per cm, so 4 cm is 400 px.
        assertEquals(400f, VolumeGestureMath.fullRangePixels(254f), 0.01f)
    }

    @Test
    fun invalidYdpiFallsBackToMdpi() {
        val expected = (4f / 2.54f) * 160f
        assertEquals(expected, VolumeGestureMath.fullRangePixels(0f), 0.01f)
        assertEquals(expected, VolumeGestureMath.fullRangePixels(Float.NaN), 0.01f)
    }

    @Test
    fun dragIsRelativeToTheStartingLevel() {
        val travel = 400f
        assertEquals(0.25f, VolumeGestureMath.fractionAfterDrag(0.25f, 0f, travel), 0.0001f)
        assertEquals(0.50f, VolumeGestureMath.fractionAfterDrag(0.25f, -100f, travel), 0.0001f)
        assertEquals(0.90f, VolumeGestureMath.fractionAfterDrag(0.80f, -40f, travel), 0.0001f)
        assertEquals(0.10f, VolumeGestureMath.fractionAfterDrag(0.40f, 120f, travel), 0.0001f)
    }

    @Test
    fun fullRangeSwipeClamps() {
        val travel = VolumeGestureMath.fullRangePixels(254f)
        assertEquals(1f, VolumeGestureMath.fractionAfterDrag(0f, -travel, travel), 0.0001f)
        assertEquals(0f, VolumeGestureMath.fractionAfterDrag(1f, travel, travel), 0.0001f)
        assertEquals(1f, VolumeGestureMath.fractionAfterDrag(0.4f, -travel * 3f, travel), 0.0001f)
        assertEquals(0f, VolumeGestureMath.fractionAfterDrag(0.2f, travel * 2f, travel), 0.0001f)
    }

    @Test
    fun streamIndexMatchesFractionEnds() {
        assertEquals(0, VolumeGestureMath.streamIndexForFraction(0f, 15))
        assertEquals(15, VolumeGestureMath.streamIndexForFraction(1f, 15))
        assertEquals(8, VolumeGestureMath.streamIndexForFraction(0.5f, 15))
        assertEquals(0, VolumeGestureMath.streamIndexForFraction(-1f, 10))
        assertEquals(0, VolumeGestureMath.streamIndexForFraction(1f, 0))
    }
}
// SMCPKG_SUPPORT<<<Cursor049
