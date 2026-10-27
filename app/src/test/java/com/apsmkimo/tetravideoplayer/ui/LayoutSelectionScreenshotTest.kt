// SMCPKG_SUPPORT>>>Cursor046
package com.apsmkimo.tetravideoplayer.ui

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import org.junit.Rule
import org.junit.Test

class LayoutSelectionScreenshotTest {
    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_5.copy(softButtons = false),
    )

    @Test
    fun layoutMenu() {
        paparazzi.snapshot {
            LayoutSelectionMenuPreview()
        }
    }
}
// SMCPKG_SUPPORT<<<Cursor046
