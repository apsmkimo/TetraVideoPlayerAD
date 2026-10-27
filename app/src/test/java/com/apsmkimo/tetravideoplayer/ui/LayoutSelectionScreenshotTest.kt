// SMCPKG_SUPPORT>>>Cursor046
// package com.apsmkimo.tetravideoplayer.ui
//
// import app.cash.paparazzi.DeviceConfig
// import app.cash.paparazzi.Paparazzi
// import org.junit.Rule
// import org.junit.Test
//
// class LayoutSelectionScreenshotTest {
//     @get:Rule
//     val paparazzi = Paparazzi(
//         deviceConfig = DeviceConfig.PIXEL_5.copy(softButtons = false),
//     )
//
//     @Test
//     fun layoutMenu() {
//         paparazzi.snapshot {
//             LayoutSelectionMenuPreview()
//         }
//     }
// }
// SMCPKG_SUPPORT<<<Cursor046
// SMCPKG_SUPPORT>>>Cursor047
// The Paparazzi test is commented out with the plugin. CI's Debug APK job uses
// JDK 17, and paparazzi-gradle-plugin 2.0.0-alpha05 refuses to resolve on it.
// SMCPKG_SUPPORT<<<Cursor047
