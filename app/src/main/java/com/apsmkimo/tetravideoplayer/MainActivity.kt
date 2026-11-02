/*
 * TetraVideoPlayer
 * Copyright (C) 2026 apsmkimo
 * All rights reserved.
 *
 * This software is proprietary. Unauthorized copying, distribution,
 * modification, or use is strictly prohibited except as expressly
 * permitted in writing by the copyright holder.
 */
// /*
//  * TetraView / FreeQuadPlayer
//  * Copyright (C) 2026 apsmkimo
//  *
//  * This program is free software: you can redistribute it and/or modify
//  * it under the terms of the GNU General Public License as published by
//  * the Free Software Foundation, either version 3 of the License, or
//  * (at your option) any later version.
//  *
//  * This program is distributed in the hope that it will be useful,
//  * but WITHOUT ANY WARRANTY; without even the implied warranty of
//  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  * GNU General Public License for more details.
//  *
//  * You should have received a copy of the GNU General Public License
//  * along with this program.  If not, see <https://www.gnu.org/licenses/>.
//  */

// package com.example.quadvideoplayer
// package com.apsmkimo.tetraview
package com.apsmkimo.tetravideoplayer

import android.os.Build
import android.os.Bundle
// import android.view.KeyEvent
import android.view.MotionEvent
import android.view.WindowManager
// import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
// import com.example.quadvideoplayer.ui.TetraViewApp
// import com.example.quadvideoplayer.ui.theme.QuadVideoPlayerTheme
// import com.apsmkimo.tetraview.ui.TetraViewApp
import com.apsmkimo.tetravideoplayer.ui.TetraViewApp
// import com.apsmkimo.tetraview.ui.theme.QuadVideoPlayerTheme
import com.apsmkimo.tetravideoplayer.ui.theme.QuadVideoPlayerTheme
import com.apsmkimo.tetravideoplayer.ads.AdsConsentManager
import com.apsmkimo.tetravideoplayer.ui.PlayerChromeSignals

// class MainActivity : ComponentActivity() {
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Edge-to-edge before setContent. androidx.activity.enableEdgeToEdge
        // also sets cutout mode ALWAYS on API 30+ and keeps icon contrast.
        enableEdgeToEdge()
        // Theme no longer sets deprecated statusBarColor / navigationBarColor.
        // The player UI is always dark, so system-bar icons stay light.
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = false
        }
        // requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        // Orientation is applied per layout: portrait for 1x4/1x2, landscape for 2x2/2x1.
        // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        //     window.attributes = window.attributes.apply {
        //         layoutInDisplayCutoutMode =
        //             WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        //     }
        // }
        // SHORT_EDGES is deprecated on Android 15. ALWAYS is the supported mode
        // (API 30+). API 28–29 stay on the mode enableEdgeToEdge() already set.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.attributes = window.attributes.apply {
                layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
            }
        }
        setContent {
            QuadVideoPlayerTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    // QuadPlayerScreen()
                    TetraViewApp()
                }
            }
        }
        // The consent form needs an Activity. MobileAds.initialize runs from
        // AdsConsentManager only after ConsentInformation.canRequestAds() is true.
        AdsConsentManager.getInstance(this).gatherConsent(this)
    }

    // Observe every touch so the top icons can fade. The event is not consumed.
    // Volume keys are not intercepted; they change system volume only.
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        PlayerChromeSignals.onTouch?.invoke(ev.actionMasked, ev.pointerCount)
        return super.dispatchTouchEvent(ev)
    }

    // override fun dispatchKeyEvent(event: KeyEvent): Boolean {
    //     val isVolumeKey = event.keyCode == KeyEvent.KEYCODE_VOLUME_UP ||
    //         event.keyCode == KeyEvent.KEYCODE_VOLUME_DOWN ||
    //         event.keyCode == KeyEvent.KEYCODE_VOLUME_MUTE
    //     val handled = super.dispatchKeyEvent(event)
    //     if (isVolumeKey && event.action == KeyEvent.ACTION_DOWN) {
    //         PlayerChromeSignals.onVolumeKey?.invoke()
    //     }
    //     return handled
    // }
    // Hardware volume keys stay with the system. Pane volumes are independent.
}
