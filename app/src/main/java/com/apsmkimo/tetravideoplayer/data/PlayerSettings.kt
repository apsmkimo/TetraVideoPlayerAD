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

package com.apsmkimo.tetravideoplayer.data

import android.content.Context

/** Four playback panes, matching [com.apsmkimo.tetravideoplayer.player.QuadPlayerController.PLAYER_COUNT]. */
const val SETTINGS_PANE_COUNT = 4

enum class DecodeMode {
    HARDWARE,
    SOFTWARE,
    ;

    fun prefValue(): String = if (this == SOFTWARE) PREF_SW else PREF_HW

    companion object {
        private const val PREF_HW = "hw"
        private const val PREF_SW = "sw"

        fun fromPref(value: String?): DecodeMode {
            return if (value == PREF_SW) SOFTWARE else HARDWARE
        }
    }
}

data class PaneSettings(
    /** 0 = toolbar at the bottom of the pane; 100 = toolbar at the top. */
    val toolbarLift: Int = 0,
    val decodeMode: DecodeMode = DecodeMode.HARDWARE,
    /** True = repeat the current item. False = play once. */
    val loop: Boolean = true,
    /** 50 leaves this pane's picture unchanged. 0 is flat, 100 is stronger. */
    val contrast: Int = 50,
    /** 50 is the original picture. Below 50 softens; above 50 sharpens. */
    val sharpness: Int = 50,
)

/**
 * Equal black border, in dp, at progress 100.
 * Inset per side is `progress / 100f * SCREEN_MARGIN_MAX_DP`.
 */
const val SCREEN_MARGIN_MAX_DP = 96

data class PlayerSettings(
    /** Off locks the layout orientation. On follows the rotation sensor. */
    val allowRotation: Boolean = false,
    // App-wide contrast moved onto each pane (Cursor041).
    // val contrast: Int = 50,
    /**
     * Larger read-ahead on every pane. ExoPlayer bakes [androidx.media3.exoplayer.LoadControl]
     * in at construction, so this applies the next time the playback screen creates
     * its players (leave playback and open a layout again). It does not rebuild
     * players that are already running.
     */
    val strongBuffering: Boolean = false,
    /** 0–100. 0 keeps today's full-bleed layout. */
    val screenMargin: Int = 0,
    /** Off hides the layout and settings buttons on the player together. */
    val showPlayerChrome: Boolean = true,
    val panes: List<PaneSettings> = List(SETTINGS_PANE_COUNT) { PaneSettings() },
) {
    fun pane(index: Int): PaneSettings = panes.getOrElse(index) { PaneSettings() }

    fun withPane(index: Int, pane: PaneSettings): PlayerSettings {
        if (index !in 0 until SETTINGS_PANE_COUNT) return this
        val next = panes.toMutableList()
        while (next.size < SETTINGS_PANE_COUNT) {
            next += PaneSettings()
        }
        next[index] = pane
        return copy(panes = next)
    }
}

/**
 * Persists rotation and per-pane playback options. Defaults match the
 * pre-settings player: rotation locked, hardware-first decode, loop on,
 * toolbar at the bottom of each pane.
 */
object PlayerSettingsStore {
    private const val PREFS_NAME = "tetravideoplayer_settings"
    private const val KEY_ROTATION = "allow_rotation"
    private const val KEY_CONTRAST = "contrast"
    private const val KEY_STRONG_BUFFERING = "strong_buffering"
    private const val KEY_SCREEN_MARGIN = "screen_margin"
    private const val KEY_SHOW_PLAYER_CHROME = "show_player_chrome"

    private fun keyLift(index: Int) = "pane_${index}_toolbar_lift"
    private fun keyDecode(index: Int) = "pane_${index}_decode"
    private fun keyLoop(index: Int) = "pane_${index}_loop"
    private fun keyContrast(index: Int) = "pane_${index}_contrast"
    private fun keySharpness(index: Int) = "pane_${index}_sharpness"

    fun load(context: Context): PlayerSettings {
        val prefs = context.applicationContext
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        // val legacyContrast = prefs.getInt(KEY_CONTRAST, 50).coerceIn(0, 100)
        val legacyContrast = if (prefs.contains(KEY_CONTRAST)) {
            prefs.getInt(KEY_CONTRAST, 50).coerceIn(0, 100)
        } else {
            50
        }
        val panes = List(SETTINGS_PANE_COUNT) { index ->
            PaneSettings(
                toolbarLift = prefs.getInt(keyLift(index), 0).coerceIn(0, 100),
                decodeMode = DecodeMode.fromPref(prefs.getString(keyDecode(index), null)),
                loop = prefs.getBoolean(keyLoop(index), true),
                contrast = if (prefs.contains(keyContrast(index))) {
                    prefs.getInt(keyContrast(index), 50).coerceIn(0, 100)
                } else {
                    legacyContrast
                },
                sharpness = prefs.getInt(keySharpness(index), 50).coerceIn(0, 100),
            )
        }
        return PlayerSettings(
            allowRotation = prefs.getBoolean(KEY_ROTATION, false),
            // contrast = prefs.getInt(KEY_CONTRAST, 50).coerceIn(0, 100),
            strongBuffering = prefs.getBoolean(KEY_STRONG_BUFFERING, false),
            screenMargin = prefs.getInt(KEY_SCREEN_MARGIN, 0).coerceIn(0, 100),
            showPlayerChrome = prefs.getBoolean(KEY_SHOW_PLAYER_CHROME, true),
            panes = panes,
        )
    }

    fun save(context: Context, settings: PlayerSettings) {
        val editor = context.applicationContext
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_ROTATION, settings.allowRotation)
            // .putInt(KEY_CONTRAST, settings.contrast.coerceIn(0, 100))
            .putBoolean(KEY_STRONG_BUFFERING, settings.strongBuffering)
            .putInt(KEY_SCREEN_MARGIN, settings.screenMargin.coerceIn(0, 100))
            .putBoolean(KEY_SHOW_PLAYER_CHROME, settings.showPlayerChrome)
        settings.panes.take(SETTINGS_PANE_COUNT).forEachIndexed { index, pane ->
            editor
                .putInt(keyLift(index), pane.toolbarLift.coerceIn(0, 100))
                .putString(keyDecode(index), pane.decodeMode.prefValue())
                .putBoolean(keyLoop(index), pane.loop)
                .putInt(keyContrast(index), pane.contrast.coerceIn(0, 100))
                .putInt(keySharpness(index), pane.sharpness.coerceIn(0, 100))
        }
        editor.apply()
    }
}
