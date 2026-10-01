// SMCPKG_SUPPORT>>>Cursor038
/*
 * TetraVideoPlayer
 * Copyright (C) 2026 apsmkimo
 * All rights reserved.
 *
 * This software is proprietary. Unauthorized copying, distribution,
 * modification, or use is strictly prohibited except as expressly
 * permitted in writing by the copyright holder.
 */
// SMCPKG_SUPPORT<<<Cursor038
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

// SMCPKG_SUPPORT>>>Cursor038
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
)

data class PlayerSettings(
    /** Off locks the layout orientation. On follows the rotation sensor. */
    val allowRotation: Boolean = false,
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

    private fun keyLift(index: Int) = "pane_${index}_toolbar_lift"
    private fun keyDecode(index: Int) = "pane_${index}_decode"
    private fun keyLoop(index: Int) = "pane_${index}_loop"

    fun load(context: Context): PlayerSettings {
        val prefs = context.applicationContext
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val panes = List(SETTINGS_PANE_COUNT) { index ->
            PaneSettings(
                toolbarLift = prefs.getInt(keyLift(index), 0).coerceIn(0, 100),
                decodeMode = DecodeMode.fromPref(prefs.getString(keyDecode(index), null)),
                loop = prefs.getBoolean(keyLoop(index), true),
            )
        }
        return PlayerSettings(
            allowRotation = prefs.getBoolean(KEY_ROTATION, false),
            panes = panes,
        )
    }

    fun save(context: Context, settings: PlayerSettings) {
        val editor = context.applicationContext
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_ROTATION, settings.allowRotation)
        settings.panes.take(SETTINGS_PANE_COUNT).forEachIndexed { index, pane ->
            editor
                .putInt(keyLift(index), pane.toolbarLift.coerceIn(0, 100))
                .putString(keyDecode(index), pane.decodeMode.prefValue())
                .putBoolean(keyLoop(index), pane.loop)
        }
        editor.apply()
    }
}
// SMCPKG_SUPPORT<<<Cursor038
