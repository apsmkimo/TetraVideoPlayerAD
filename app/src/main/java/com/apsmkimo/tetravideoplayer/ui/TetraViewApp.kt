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

// package com.example.quadvideoplayer.ui
// package com.apsmkimo.tetraview.ui
package com.apsmkimo.tetravideoplayer.ui

import android.app.Activity
import android.content.pm.ActivityInfo
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.rememberUpdatedState
// import androidx.compose.foundation.layout.Box
// import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
// import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
// import com.example.quadvideoplayer.data.LayoutPreferences
// import com.example.quadvideoplayer.data.PlayerLayout
// import com.apsmkimo.tetraview.data.LayoutPreferences
import com.apsmkimo.tetravideoplayer.data.LayoutPreferences
// import com.apsmkimo.tetraview.data.PlayerLayout
import com.apsmkimo.tetravideoplayer.data.PlayerLayout
import com.apsmkimo.tetravideoplayer.data.PlayerSettings
import com.apsmkimo.tetravideoplayer.data.PlayerSettingsStore

/**
 * Every cold start shows [LauncherSelectionScreen]. The chosen layout is
 * in-session only (not written to prefs). Back-to-selection disposes the
 * player so media is released and the next layout choice starts empty.
 */
@Composable
fun TetraViewApp() {
    val context = LocalContext.current
    val activity = context as Activity
    // var persisted by remember { mutableStateOf(LayoutPreferences.load(context)) }
    // var layout by remember { mutableStateOf(persisted ?: PlayerLayout.LANDSCAPE_2X2) }
    // var showSelection by remember { mutableStateOf(persisted == null) }
    // var layout by remember { mutableStateOf<PlayerLayout?>(null) }
    // var showSelection by remember { mutableStateOf(true) }
    // A language change recreates the activity. Save the session so playback
    // and the open Settings screen survive that recreation.
    var layoutName by rememberSaveable { mutableStateOf<String?>(null) }
    var showSelection by rememberSaveable { mutableStateOf(true) }
    val layout = layoutName?.let { name ->
        PlayerLayout.entries.firstOrNull { it.name == name }
    }
    // var showAbout by remember { mutableStateOf(false) }
    // var showSettings by remember { mutableStateOf(false) }
    var showSettings by rememberSaveable { mutableStateOf(false) }
    var settings by remember { mutableStateOf(PlayerSettingsStore.load(context)) }
    // var showLicense by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        LayoutPreferences.clear(context)
    }

    fun updateSettings(next: PlayerSettings) {
        settings = next
        PlayerSettingsStore.save(context, next)
    }

    fun orientationFor(selected: PlayerLayout?, atSelection: Boolean): Int {
        // if (settings.allowRotation) {
        //     return ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
        // }
        // if (atSelection && selected == null) {
        //     return ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        // }
        // return selected?.orientation ?: ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        // The layout menu stays portrait even when playback rotation is on.
        if (atSelection || selected == null) {
            return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        if (settings.allowRotation) {
            return ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
        }
        return selected.orientation
    }

    fun applyLayout(newLayout: PlayerLayout) {
        // LayoutPreferences.save(context, newLayout)
        // persisted = newLayout
        // layout = newLayout
        layoutName = newLayout.name
        showSelection = false
        // activity.requestedOrientation = newLayout.orientation
        activity.requestedOrientation = orientationFor(newLayout, atSelection = false)
    }

    // LaunchedEffect(showSelection, layout) {
    //     activity.requestedOrientation = if (showSelection && layout == null) {
    //         ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    //     } else {
    //         layout?.orientation ?: ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    //     }
    // }
    LaunchedEffect(showSelection, layout, settings.allowRotation) {
        activity.requestedOrientation = orientationFor(layout, atSelection = showSelection)
    }

    // if (persisted == null && showSelection) {
    //     LauncherSelectionScreen(onLayoutSelected = ::applyLayout)
    // } else { ... overlay picker ... }
    // Selection, playback, and settings were emitted directly on the window.
    ScreenMarginFrame(progress = settings.screenMargin) {
    val selected = layout
    if (selected == null) {
        LauncherSelectionScreen(
            onLayoutSelected = ::applyLayout,
            // onAbout = { showAbout = true },
            onSettings = { showSettings = true },
        )
    } else {
        // Box(modifier = Modifier.fillMaxSize()) {
        //     QuadPlayerScreen(
        //         layout = selected,
        //         onChangeLayout = { showSelection = true },
        //     )
        //     if (showSelection) {
        //         LauncherSelectionScreen(
        //             onLayoutSelected = ::applyLayout,
        //             onCancel = { showSelection = false },
        //             onAbout = { showAbout = true },
        //         )
        //     }
        // }
        // Overlaying the picker left players running and remembered URIs.
        // Dispose QuadPlayerScreen (layout = null) so releaseAll runs.
        QuadPlayerScreen(
            layout = selected,
            onChangeLayout = {
                // layout = null
                layoutName = null
                showSelection = true
            },
            // onAbout = { showAbout = true },
            settings = settings,
            onOpenSettings = { showSettings = true },
        )
    }
    // if (showLicense) {
    //     LicenseTextScreen(onClose = { showLicense = false })
    // } else if (showAbout) {
    //     AboutScreen(
    //         onClose = { showAbout = false },
    //         onViewLicense = { showLicense = true },
    //     )
    // }
    // if (showAbout) {
    //     AboutScreen(
    //         onClose = { showAbout = false },
    //     )
    // }
    if (showSettings) {
        SettingsScreen(
            settings = settings,
            onSettingsChange = ::updateSettings,
            onClose = { showSettings = false },
        )
    }
    }
}

/**
 * First back shows [message]. A second back within [windowMs] runs [onConfirmed].
 * Registered on [androidx.activity.OnBackPressedDispatcher] via [BackHandler],
 * so the system back button and the back gesture are the same event, including
 * predictive back.
 */
@Composable
internal fun ConfirmBackHandler(
    message: String,
    onConfirmed: () -> Unit,
    enabled: Boolean = true,
    windowMs: Long = 2_000L,
) {
    val context = LocalContext.current
    val currentMessage = rememberUpdatedState(message)
    val currentConfirmed = rememberUpdatedState(onConfirmed)
    var armedAt by remember { mutableLongStateOf(0L) }
    BackHandler(enabled = enabled) {
        val now = android.os.SystemClock.uptimeMillis()
        if (armedAt != 0L && now - armedAt <= windowMs) {
            armedAt = 0L
            currentConfirmed.value()
        } else {
            armedAt = now
            Toast.makeText(context, currentMessage.value, Toast.LENGTH_SHORT).show()
        }
    }
}
