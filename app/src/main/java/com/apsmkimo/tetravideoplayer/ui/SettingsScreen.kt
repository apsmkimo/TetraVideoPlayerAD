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
package com.apsmkimo.tetravideoplayer.ui

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.apsmkimo.tetravideoplayer.R
// SMCPKG_SUPPORT>>>Cursor040
// import com.apsmkimo.tetravideoplayer.data.DecodeMode
// SMCPKG_SUPPORT<<<Cursor040
import com.apsmkimo.tetravideoplayer.data.PaneSettings
import com.apsmkimo.tetravideoplayer.data.PlayerSettings
import com.apsmkimo.tetravideoplayer.data.SETTINGS_PANE_COUNT
import kotlin.math.roundToInt

/** Gear icon. Kept here so call sites can avoid clashing with android.provider.Settings. */
val SettingsGearIcon: ImageVector = Icons.Outlined.Settings

private val SettingsBackground = Color(0xFF000000)
private val SettingsText = Color(0xFFE8EEF6)
private val SettingsMuted = Color(0xFFB4C0D0)
private val SettingsDivider = Color(0xFF2A3340)

@Composable
fun SettingsScreen(
    settings: PlayerSettings,
    onSettingsChange: (PlayerSettings) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BackHandler(onBack = onClose)
    val context = LocalContext.current
    val versionLabel = remember(context) {
        val info = runCatching {
            context.packageManager.getPackageInfo(context.packageName, 0)
        }.getOrNull()
        val name = info?.versionName.orEmpty().ifEmpty { "—" }
        val code = if (info == null) {
            "—"
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            info.longVersionCode.toString()
        } else {
            @Suppress("DEPRECATION")
            info.versionCode.toString()
        }
        context.getString(R.string.about_version_line, name, code)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SettingsBackground)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        SettingsTopBar(onClose = onClose)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            RotationSetting(
                enabled = settings.allowRotation,
                onEnabledChange = { enabled ->
                    onSettingsChange(settings.copy(allowRotation = enabled))
                },
            )
            // SMCPKG_SUPPORT>>>Cursor040
            ContrastSetting(
                contrast = settings.contrast,
                onContrastChange = { contrast ->
                    onSettingsChange(settings.copy(contrast = contrast))
                },
            )
            // SMCPKG_SUPPORT<<<Cursor040
            HorizontalDivider(color = SettingsDivider)
            Text(
                text = stringResource(R.string.settings_panes_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = SettingsText,
            )
            // SMCPKG_SUPPORT>>>Cursor040
            // Text(
            //     text = stringResource(R.string.settings_decode_sw_note),
            //     style = MaterialTheme.typography.bodySmall,
            //     color = SettingsMuted,
            // )
            // SMCPKG_SUPPORT<<<Cursor040
            repeat(SETTINGS_PANE_COUNT) { index ->
                PaneSettingsBlock(
                    index = index,
                    pane = settings.pane(index),
                    onPaneChange = { pane ->
                        onSettingsChange(settings.withPane(index, pane))
                    },
                )
            }
            HorizontalDivider(color = SettingsDivider)
            Text(
                text = stringResource(R.string.settings_about_section),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = SettingsText,
            )
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = SettingsText,
            )
            Text(text = versionLabel, color = SettingsMuted)
            Button(
                onClick = { openUrl(context, PRIVACY_POLICY_URL) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.about_privacy_policy))
            }
            OutlinedButton(
                onClick = { openPlayStoreListing(context) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.about_rate_us))
            }
        }
    }
}

@Composable
private fun SettingsTopBar(onClose: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onClose) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.about_close),
                tint = SettingsText,
            )
        }
        Text(
            text = stringResource(R.string.settings_title),
            style = MaterialTheme.typography.titleLarge,
            color = SettingsText,
        )
    }
}

@Composable
private fun RotationSetting(
    enabled: Boolean,
    onEnabledChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.settings_rotation),
                style = MaterialTheme.typography.titleMedium,
                color = SettingsText,
            )
            Text(
                text = stringResource(R.string.settings_rotation_summary),
                style = MaterialTheme.typography.bodySmall,
                color = SettingsMuted,
            )
        }
        Switch(
            checked = enabled,
            onCheckedChange = onEnabledChange,
        )
    }
}

// SMCPKG_SUPPORT>>>Cursor040
@Composable
private fun ContrastSetting(
    contrast: Int,
    onContrastChange: (Int) -> Unit,
) {
    val value = contrast.coerceIn(0, 100)
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.settings_contrast),
                    style = MaterialTheme.typography.titleMedium,
                    color = SettingsText,
                )
                Text(
                    text = stringResource(R.string.settings_contrast_summary),
                    style = MaterialTheme.typography.bodySmall,
                    color = SettingsMuted,
                )
            }
            Text(
                text = value.toString(),
                color = SettingsMuted,
                style = MaterialTheme.typography.titleMedium,
            )
        }
        Slider(
            value = value.toFloat(),
            onValueChange = { next ->
                onContrastChange(next.roundToInt().coerceIn(0, 100))
            },
            valueRange = 0f..100f,
            steps = 99,
        )
    }
}
// SMCPKG_SUPPORT<<<Cursor040

@Composable
private fun PaneSettingsBlock(
    index: Int,
    pane: PaneSettings,
    onPaneChange: (PaneSettings) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = stringResource(R.string.cell_index, index + 1),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = SettingsText,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.settings_toolbar_position),
                color = SettingsText,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = pane.toolbarLift.coerceIn(0, 100).toString(),
                color = SettingsMuted,
                style = MaterialTheme.typography.titleMedium,
            )
        }
        Slider(
            value = pane.toolbarLift.coerceIn(0, 100).toFloat(),
            onValueChange = { value ->
                onPaneChange(pane.copy(toolbarLift = value.roundToInt().coerceIn(0, 100)))
            },
            valueRange = 0f..100f,
            steps = 99,
        )
        // SMCPKG_SUPPORT>>>Cursor040
        // Text(
        //     text = stringResource(R.string.settings_decode),
        //     color = SettingsMuted,
        //     style = MaterialTheme.typography.bodySmall,
        // )
        // Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        //     FilterChip(
        //         selected = pane.decodeMode == DecodeMode.HARDWARE,
        //         onClick = { onPaneChange(pane.copy(decodeMode = DecodeMode.HARDWARE)) },
        //         label = { Text(stringResource(R.string.settings_decode_hw)) },
        //     )
        //     FilterChip(
        //         selected = pane.decodeMode == DecodeMode.SOFTWARE,
        //         onClick = { onPaneChange(pane.copy(decodeMode = DecodeMode.SOFTWARE)) },
        //         label = { Text(stringResource(R.string.settings_decode_sw)) },
        //     )
        // }
        // SMCPKG_SUPPORT<<<Cursor040
        Text(
            text = stringResource(R.string.settings_playback),
            color = SettingsMuted,
            style = MaterialTheme.typography.bodySmall,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = pane.loop,
                onClick = { onPaneChange(pane.copy(loop = true)) },
                label = { Text(stringResource(R.string.settings_loop)) },
            )
            FilterChip(
                selected = !pane.loop,
                onClick = { onPaneChange(pane.copy(loop = false)) },
                label = { Text(stringResource(R.string.settings_once)) },
            )
        }
    }
}
// SMCPKG_SUPPORT<<<Cursor038
