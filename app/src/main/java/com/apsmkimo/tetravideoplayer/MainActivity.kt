// SMCPKG_SUPPORT>>>Cursor030
/*
 * TetraVideoPlayer
 * Copyright (C) 2026 apsmkimo
 * All rights reserved.
 *
 * This software is proprietary. Unauthorized copying, distribution,
 * modification, or use is strictly prohibited except as expressly
 * permitted in writing by the copyright holder.
 */
// SMCPKG_SUPPORT<<<Cursor030
/*
 * TetraView / FreeQuadPlayer
 * Copyright (C) 2026 apsmkimo
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

// SMCPKG_SUPPORT>>>Cursor021
// package com.example.quadvideoplayer
// SMCPKG_SUPPORT>>>Cursor030
// package com.apsmkimo.tetraview
package com.apsmkimo.tetravideoplayer
// SMCPKG_SUPPORT<<<Cursor030
// SMCPKG_SUPPORT<<<Cursor021

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
// SMCPKG_SUPPORT>>>Cursor045
import androidx.core.view.WindowCompat
// SMCPKG_SUPPORT<<<Cursor045
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
// SMCPKG_SUPPORT>>>Cursor021
// import com.example.quadvideoplayer.ui.TetraViewApp
// import com.example.quadvideoplayer.ui.theme.QuadVideoPlayerTheme
// SMCPKG_SUPPORT>>>Cursor030
// import com.apsmkimo.tetraview.ui.TetraViewApp
import com.apsmkimo.tetravideoplayer.ui.TetraViewApp
// SMCPKG_SUPPORT<<<Cursor030
// SMCPKG_SUPPORT>>>Cursor030
// import com.apsmkimo.tetraview.ui.theme.QuadVideoPlayerTheme
import com.apsmkimo.tetravideoplayer.ui.theme.QuadVideoPlayerTheme
// SMCPKG_SUPPORT<<<Cursor030
// SMCPKG_SUPPORT<<<Cursor021
// SMCPKG_SUPPORT>>>Cursor043
import com.apsmkimo.tetravideoplayer.ads.AdsConsentManager
// SMCPKG_SUPPORT<<<Cursor043

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Edge-to-edge before setContent. androidx.activity.enableEdgeToEdge
        // also sets cutout mode ALWAYS on API 30+ and keeps icon contrast.
        enableEdgeToEdge()
        // SMCPKG_SUPPORT>>>Cursor045
        // Theme no longer sets deprecated statusBarColor / navigationBarColor.
        // The player UI is always dark, so system-bar icons stay light.
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = false
        }
        // SMCPKG_SUPPORT<<<Cursor045
        // SMCPKG_SUPPORT>>>Cursor005
        // requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        // SMCPKG_SUPPORT<<<Cursor005
        // SMCPKG_SUPPORT>>>Cursor012
        // Orientation is applied per layout: portrait for 1x4/1x2, landscape for 2x2/2x1.
        // SMCPKG_SUPPORT<<<Cursor012
        // SMCPKG_SUPPORT>>>Cursor004
        // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        //     window.attributes = window.attributes.apply {
        //         layoutInDisplayCutoutMode =
        //             WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        //     }
        // }
        // SMCPKG_SUPPORT>>>Cursor045
        // SHORT_EDGES is deprecated on Android 15. ALWAYS is the supported mode
        // (API 30+). API 28–29 stay on the mode enableEdgeToEdge() already set.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.attributes = window.attributes.apply {
                layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
            }
        }
        // SMCPKG_SUPPORT<<<Cursor045
        // SMCPKG_SUPPORT<<<Cursor004
        setContent {
            QuadVideoPlayerTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    // SMCPKG_SUPPORT>>>Cursor012
                    // QuadPlayerScreen()
                    TetraViewApp()
                    // SMCPKG_SUPPORT<<<Cursor012
                }
            }
        }
        // SMCPKG_SUPPORT>>>Cursor043
        // The consent form needs an Activity. MobileAds.initialize runs from
        // AdsConsentManager only after ConsentInformation.canRequestAds() is true.
        AdsConsentManager.getInstance(this).gatherConsent(this)
        // SMCPKG_SUPPORT<<<Cursor043
    }
}
