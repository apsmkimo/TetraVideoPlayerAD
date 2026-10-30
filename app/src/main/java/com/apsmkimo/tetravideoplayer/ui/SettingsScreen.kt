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
// SMCPKG_SUPPORT>>>Cursor044
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
// SMCPKG_SUPPORT<<<Cursor044

// SMCPKG_SUPPORT>>>Cursor038
package com.apsmkimo.tetravideoplayer.ui

import android.app.Activity
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
// SMCPKG_SUPPORT>>>Cursor053
// import androidx.compose.foundation.clickable
// SMCPKG_SUPPORT<<<Cursor053
// SMCPKG_SUPPORT>>>Cursor054
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableIntStateOf
import com.apsmkimo.tetravideoplayer.TetraVideoPlayerApp
import com.apsmkimo.tetravideoplayer.ads.AdPreferences
import com.apsmkimo.tetravideoplayer.ads.RemoveAdsBilling
// SMCPKG_SUPPORT<<<Cursor054
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
// SMCPKG_SUPPORT>>>Cursor054
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
// SMCPKG_SUPPORT<<<Cursor054
import androidx.compose.material.icons.outlined.Settings
// SMCPKG_SUPPORT>>>Cursor054
// import androidx.compose.material3.Button
// SMCPKG_SUPPORT<<<Cursor054
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
// SMCPKG_SUPPORT>>>Cursor054
// import androidx.compose.material3.HorizontalDivider
// SMCPKG_SUPPORT<<<Cursor054
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
// SMCPKG_SUPPORT>>>Cursor054
// import androidx.compose.material3.OutlinedButton
// SMCPKG_SUPPORT<<<Cursor054
import androidx.compose.material3.OutlinedTextField
// SMCPKG_SUPPORT>>>Cursor053
// import androidx.compose.material3.RadioButton
// SMCPKG_SUPPORT<<<Cursor053
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.apsmkimo.tetravideoplayer.R
import com.apsmkimo.tetravideoplayer.ads.AdsConsentManager
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
// SMCPKG_SUPPORT>>>Cursor054
// private val SettingsDivider = Color(0xFF2A3340)
// SMCPKG_SUPPORT<<<Cursor054
// SMCPKG_SUPPORT>>>Cursor051
/** Settings-only accent. The rest of the app keeps AccentGreen. */
private val SettingsAccent = Color(0xFF007979)
// SMCPKG_SUPPORT<<<Cursor051
// SMCPKG_SUPPORT>>>Cursor054
private val SettingsGlassTop = Color(0x2EFFFFFF)
private val SettingsGlassBottom = Color(0x12FFFFFF)
private val SettingsGlassStroke = Color(0x38FFFFFF)
// SMCPKG_SUPPORT<<<Cursor054

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
    // SMCPKG_SUPPORT>>>Cursor043
    val consent = remember(context) { AdsConsentManager.getInstance(context) }
    val privacyOptionsRequired by consent.privacyOptionsRequired.collectAsStateWithLifecycle()
    // SMCPKG_SUPPORT<<<Cursor043
    // SMCPKG_SUPPORT>>>Cursor054
    val billing = (context.applicationContext as? TetraVideoPlayerApp)?.removeAdsBilling
    var adsRemoved by remember { mutableStateOf(AdPreferences.isAdRemoved(context)) }
    DisposableEffect(billing) {
        val listener = billing?.let { client ->
            RemoveAdsBilling.EntitlementListener { owned ->
                adsRemoved = owned
            }.also { client.addListener(it) }
        }
        if (billing != null) {
            adsRemoved = AdPreferences.isAdRemoved(context)
        }
        onDispose {
            if (billing != null && listener != null) {
                billing.removeListener(listener)
            }
        }
    }
    var selectedPane by remember { mutableIntStateOf(0) }
    // SMCPKG_SUPPORT<<<Cursor054

    // SMCPKG_SUPPORT>>>Cursor051
    // Switches, selected chips/radios, sliders, and buttons on this screen only.
    val settingsScheme = MaterialTheme.colorScheme.copy(
        primary = SettingsAccent,
        onPrimary = Color.White,
        primaryContainer = SettingsAccent,
        onPrimaryContainer = Color.White,
        inversePrimary = SettingsAccent,
        secondary = SettingsAccent,
        onSecondary = Color.White,
        secondaryContainer = SettingsAccent,
        onSecondaryContainer = Color.White,
    )
    MaterialTheme(colorScheme = settingsScheme) {
    // SMCPKG_SUPPORT<<<Cursor051
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
            // SMCPKG_SUPPORT>>>Cursor054
            // verticalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            // SMCPKG_SUPPORT<<<Cursor054
        ) {
            // SMCPKG_SUPPORT>>>Cursor054
            // // SMCPKG_SUPPORT>>>Cursor048
            // LanguageSetting()
            // // SMCPKG_SUPPORT<<<Cursor048
            // // SMCPKG_SUPPORT>>>Cursor051
            // PlayerChromeSetting(
            //     enabled = settings.showPlayerChrome,
            //     onEnabledChange = { enabled ->
            //         onSettingsChange(settings.copy(showPlayerChrome = enabled))
            //     },
            // )
            // // SMCPKG_SUPPORT<<<Cursor051
            // RotationSetting(
            //     enabled = settings.allowRotation,
            //     onEnabledChange = { enabled ->
            //         onSettingsChange(settings.copy(allowRotation = enabled))
            //     },
            // )
            // // SMCPKG_SUPPORT>>>Cursor042
            // StrongBufferingSetting(
            //     enabled = settings.strongBuffering,
            //     onEnabledChange = { enabled ->
            //         onSettingsChange(settings.copy(strongBuffering = enabled))
            //     },
            // )
            // PercentSlider(
            //     title = stringResource(R.string.settings_screen_margin),
            //     summary = stringResource(R.string.settings_screen_margin_summary),
            //     value = settings.screenMargin,
            //     onValueChange = { margin ->
            //         onSettingsChange(settings.copy(screenMargin = margin))
            //     },
            // )
            // // SMCPKG_SUPPORT<<<Cursor042
            // // SMCPKG_SUPPORT>>>Cursor040
            // // ContrastSetting was app-wide. Cursor041 moves contrast onto each pane.
            // // SMCPKG_SUPPORT<<<Cursor040
            // HorizontalDivider(color = SettingsDivider)
            // Text(
            //     text = stringResource(R.string.settings_panes_title),
            //     style = MaterialTheme.typography.titleMedium,
            //     fontWeight = FontWeight.SemiBold,
            //     color = SettingsText,
            // )
            // // SMCPKG_SUPPORT>>>Cursor040
            // // Text(
            // //     text = stringResource(R.string.settings_decode_sw_note),
            // //     style = MaterialTheme.typography.bodySmall,
            // //     color = SettingsMuted,
            // // )
            // // SMCPKG_SUPPORT<<<Cursor040
            // repeat(SETTINGS_PANE_COUNT) { index ->
            //     PaneSettingsBlock(
            //         index = index,
            //         pane = settings.pane(index),
            //         onPaneChange = { pane ->
            //             onSettingsChange(settings.withPane(index, pane))
            //         },
            //     )
            // }
            // HorizontalDivider(color = SettingsDivider)
            // Text(
            //     text = stringResource(R.string.settings_about_section),
            //     style = MaterialTheme.typography.titleMedium,
            //     fontWeight = FontWeight.SemiBold,
            //     color = SettingsText,
            // )
            // Text(
            //     text = stringResource(R.string.app_name),
            //     style = MaterialTheme.typography.titleLarge,
            //     fontWeight = FontWeight.Bold,
            //     color = SettingsText,
            // )
            // Text(text = versionLabel, color = SettingsMuted)
            // Button(
            //     onClick = { openUrl(context, PRIVACY_POLICY_URL) },
            //     modifier = Modifier.fillMaxWidth(),
            // ) {
            //     Text(stringResource(R.string.about_privacy_policy))
            // }
            // // SMCPKG_SUPPORT>>>Cursor043
            // if (privacyOptionsRequired) {
            //     OutlinedButton(
            //         onClick = {
            //             val activity = context as? Activity ?: return@OutlinedButton
            //             consent.showPrivacyOptionsForm(activity) { formError ->
            //                 val message = formError?.message
            //                 if (!message.isNullOrBlank()) {
            //                     Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            //                 }
            //             }
            //         },
            //         modifier = Modifier.fillMaxWidth(),
            //     ) {
            //         Text(stringResource(R.string.privacy_options))
            //     }
            // }
            // // SMCPKG_SUPPORT<<<Cursor043
            // OutlinedButton(
            //     onClick = { openPlayStoreListing(context) },
            //     modifier = Modifier.fillMaxWidth(),
            // ) {
            //     Text(stringResource(R.string.about_rate_us))
            // }
            SettingsSectionCard(title = stringResource(R.string.settings_section_player)) {
                PlayerChromeSetting(
                    enabled = settings.showPlayerChrome,
                    onEnabledChange = { enabled ->
                        onSettingsChange(settings.copy(showPlayerChrome = enabled))
                    },
                )
                RotationSetting(
                    enabled = settings.allowRotation,
                    onEnabledChange = { enabled ->
                        onSettingsChange(settings.copy(allowRotation = enabled))
                    },
                )
                PercentSlider(
                    title = stringResource(R.string.settings_screen_margin),
                    summary = stringResource(R.string.settings_screen_margin_summary),
                    value = settings.screenMargin,
                    onValueChange = { margin ->
                        onSettingsChange(settings.copy(screenMargin = margin))
                    },
                )
            }
            SettingsSectionCard(title = stringResource(R.string.settings_section_videos)) {
                VideoPaneTabRow(
                    selected = selectedPane,
                    onSelected = { selectedPane = it },
                )
                val paneIndex = selectedPane.coerceIn(0, SETTINGS_PANE_COUNT - 1)
                PaneSettingsBlock(
                    index = paneIndex,
                    pane = settings.pane(paneIndex),
                    onPaneChange = { pane ->
                        onSettingsChange(settings.withPane(paneIndex, pane))
                    },
                    showHeading = false,
                )
            }
            SettingsSectionCard(title = stringResource(R.string.settings_section_advanced)) {
                StrongBufferingSetting(
                    enabled = settings.strongBuffering,
                    onEnabledChange = { enabled ->
                        onSettingsChange(settings.copy(strongBuffering = enabled))
                    },
                )
            }
            SettingsSectionCard(title = stringResource(R.string.settings_section_general)) {
                LanguageSetting()
                SettingsChevronRow(
                    title = stringResource(R.string.remove_ads),
                    onClick = { launchRemoveAds(context, billing, adsRemoved) },
                )
                SettingsChevronRow(
                    title = stringResource(R.string.settings_restore_purchase),
                    onClick = { restoreRemoveAdsPurchase(context, billing) },
                )
            }
            SettingsSectionCard(title = stringResource(R.string.settings_section_about)) {
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = SettingsText,
                )
                Text(text = versionLabel, color = SettingsMuted)
                SettingsChevronRow(
                    title = stringResource(R.string.about_privacy_policy),
                    onClick = { openUrl(context, PRIVACY_POLICY_URL) },
                )
                if (privacyOptionsRequired) {
                    SettingsChevronRow(
                        title = stringResource(R.string.privacy_options),
                        onClick = {
                            val activity = context as? Activity
                            if (activity != null) {
                                consent.showPrivacyOptionsForm(activity) { formError ->
                                    val message = formError?.message
                                    if (!message.isNullOrBlank()) {
                                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        },
                    )
                }
                SettingsChevronRow(
                    title = stringResource(R.string.about_rate_us),
                    onClick = { openPlayStoreListing(context) },
                )
            }
            // SMCPKG_SUPPORT<<<Cursor054
        }
    }
    // SMCPKG_SUPPORT>>>Cursor051
    }
    // SMCPKG_SUPPORT<<<Cursor051
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

// SMCPKG_SUPPORT>>>Cursor048
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LanguageSetting() {
    var selected by remember { mutableStateOf(AppLanguage.current()) }
    // SMCPKG_SUPPORT>>>Cursor053
    var expanded by remember { mutableStateOf(false) }
    // SMCPKG_SUPPORT<<<Cursor053
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = stringResource(R.string.settings_language),
            style = MaterialTheme.typography.titleMedium,
            color = SettingsText,
        )
        Text(
            text = stringResource(R.string.settings_language_summary),
            style = MaterialTheme.typography.bodySmall,
            color = SettingsMuted,
        )
        // SMCPKG_SUPPORT>>>Cursor053
        // AppLanguage.entries.forEach { language ->
        //     Row(
        //         modifier = Modifier
        //             .fillMaxWidth()
        //             .clickable {
        //                 selected = language
        //                 language.apply()
        //             }
        //             .padding(vertical = 2.dp),
        //         verticalAlignment = Alignment.CenterVertically,
        //     ) {
        //         RadioButton(
        //             selected = selected == language,
        //             onClick = {
        //                 selected = language
        //                 language.apply()
        //             },
        //         )
        //         Text(
        //             text = stringResource(language.labelRes),
        //             color = SettingsText,
        //             style = MaterialTheme.typography.bodyLarge,
        //         )
        //     }
        // }
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
            modifier = Modifier.fillMaxWidth(),
        ) {
            OutlinedTextField(
                value = stringResource(selected.labelRes),
                onValueChange = {},
                readOnly = true,
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = SettingsText),
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                    focusedTextColor = SettingsText,
                    unfocusedTextColor = SettingsText,
                    focusedBorderColor = SettingsAccent,
                    unfocusedBorderColor = SettingsMuted,
                    focusedTrailingIconColor = SettingsAccent,
                    unfocusedTrailingIconColor = SettingsMuted,
                    cursorColor = SettingsAccent,
                ),
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth(),
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                AppLanguage.entries.forEach { language ->
                    val isSelected = language == selected
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = stringResource(language.labelRes),
                                color = if (isSelected) SettingsAccent else SettingsText,
                            )
                        },
                        onClick = {
                            expanded = false
                            if (language != selected) {
                                selected = language
                                language.apply()
                            }
                        },
                    )
                }
            }
        }
        // SMCPKG_SUPPORT<<<Cursor053
    }
}

@Preview(widthDp = 390, heightDp = 520, showBackground = true)
@Composable
internal fun SettingsLanguagePreview() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .background(SettingsBackground)
                .padding(horizontal = 20.dp, vertical = 12.dp),
        ) {
            LanguageSetting()
        }
    }
}
// SMCPKG_SUPPORT<<<Cursor048

// SMCPKG_SUPPORT>>>Cursor051
@Composable
private fun PlayerChromeSetting(
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
                text = stringResource(R.string.settings_show_player_chrome),
                style = MaterialTheme.typography.titleMedium,
                color = SettingsText,
            )
            Text(
                text = stringResource(R.string.settings_show_player_chrome_summary),
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
// SMCPKG_SUPPORT<<<Cursor051

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

// SMCPKG_SUPPORT>>>Cursor042
@Composable
private fun StrongBufferingSetting(
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
                text = stringResource(R.string.settings_strong_buffering),
                style = MaterialTheme.typography.titleMedium,
                color = SettingsText,
            )
            Text(
                text = stringResource(R.string.settings_strong_buffering_summary),
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
// SMCPKG_SUPPORT<<<Cursor042

// SMCPKG_SUPPORT>>>Cursor040
// @Composable
// private fun ContrastSetting(...) { app-wide slider, replaced by per-pane sliders }
// SMCPKG_SUPPORT<<<Cursor040

// SMCPKG_SUPPORT>>>Cursor041
@Composable
private fun PercentSlider(
    title: String,
    summary: String,
    value: Int,
    onValueChange: (Int) -> Unit,
) {
    val shown = value.coerceIn(0, 100)
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = SettingsText,
                )
                Text(
                    text = summary,
                    style = MaterialTheme.typography.bodySmall,
                    color = SettingsMuted,
                )
            }
            Text(
                text = shown.toString(),
                color = SettingsMuted,
                style = MaterialTheme.typography.titleMedium,
            )
        }
        Slider(
            value = shown.toFloat(),
            onValueChange = { next ->
                onValueChange(next.roundToInt().coerceIn(0, 100))
            },
            valueRange = 0f..100f,
            steps = 99,
        )
    }
}
// SMCPKG_SUPPORT<<<Cursor041

@Composable
private fun PaneSettingsBlock(
    index: Int,
    pane: PaneSettings,
    onPaneChange: (PaneSettings) -> Unit,
    // SMCPKG_SUPPORT>>>Cursor054
    showHeading: Boolean = true,
    // SMCPKG_SUPPORT<<<Cursor054
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // SMCPKG_SUPPORT>>>Cursor054
        // Text(
        //     // SMCPKG_SUPPORT>>>Cursor041
        //     // text = stringResource(R.string.cell_index, index + 1),
        //     text = stringResource(R.string.settings_video_pane, index + 1),
        //     // SMCPKG_SUPPORT<<<Cursor041
        //     style = MaterialTheme.typography.titleSmall,
        //     fontWeight = FontWeight.SemiBold,
        //     color = SettingsText,
        // )
        if (showHeading) {
            Text(
                text = stringResource(R.string.settings_video_pane, index + 1),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = SettingsText,
            )
        }
        // SMCPKG_SUPPORT<<<Cursor054
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
        // SMCPKG_SUPPORT>>>Cursor041
        PercentSlider(
            title = stringResource(R.string.settings_contrast),
            summary = stringResource(R.string.settings_contrast_summary),
            value = pane.contrast,
            onValueChange = { contrast ->
                onPaneChange(pane.copy(contrast = contrast))
            },
        )
        PercentSlider(
            title = stringResource(R.string.settings_sharpness),
            summary = stringResource(R.string.settings_sharpness_summary),
            value = pane.sharpness,
            onValueChange = { sharpness ->
                onPaneChange(pane.copy(sharpness = sharpness))
            },
        )
        // SMCPKG_SUPPORT<<<Cursor041
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

// SMCPKG_SUPPORT>>>Cursor054
@Composable
private fun SettingsSectionCard(
    title: String,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(SettingsGlassTop, SettingsGlassBottom),
                ),
            )
            .border(1.dp, SettingsGlassStroke, RoundedCornerShape(20.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = SettingsAccent,
        )
        content()
    }
}

@Composable
private fun SettingsChevronRow(
    title: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            color = SettingsText,
            style = MaterialTheme.typography.bodyLarge,
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = SettingsMuted,
        )
    }
}

@Composable
private fun VideoPaneTabRow(
    selected: Int,
    onSelected: (Int) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(SETTINGS_PANE_COUNT) { index ->
            val isSelected = index == selected
            val label = if (index == 0) {
                stringResource(R.string.settings_tab_video, index + 1)
            } else {
                stringResource(R.string.settings_tab_video_index, index + 1)
            }
            val spoken = stringResource(R.string.settings_tab_video, index + 1)
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (index > 0) {
                    Text(
                        text = "|",
                        color = SettingsMuted,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .padding(end = 6.dp)
                            .clearAndSetSemantics { },
                    )
                }
                Box(
                    modifier = Modifier
                        .defaultMinSize(minWidth = 40.dp, minHeight = 40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) SettingsAccent else Color.Transparent)
                        .selectable(
                            selected = isSelected,
                            onClick = { onSelected(index) },
                            role = Role.Tab,
                        )
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = label,
                        modifier = Modifier.clearAndSetSemantics {
                            contentDescription = spoken
                        },
                        color = if (isSelected) Color.White else SettingsText,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

private fun launchRemoveAds(
    context: android.content.Context,
    billing: RemoveAdsBilling?,
    adsRemoved: Boolean,
) {
    if (adsRemoved) {
        Toast.makeText(context, R.string.settings_ads_already_removed, Toast.LENGTH_SHORT).show()
        return
    }
    val activity = context as? Activity
    if (billing == null || activity == null) {
        Toast.makeText(context, R.string.remove_ads_billing_unavailable, Toast.LENGTH_SHORT).show()
        return
    }
    when (billing.launchPurchase(activity)) {
        RemoveAdsBilling.LaunchResult.STARTED -> Unit
        RemoveAdsBilling.LaunchResult.UNAVAILABLE -> {
            Toast.makeText(context, R.string.remove_ads_billing_unavailable, Toast.LENGTH_SHORT).show()
        }
        RemoveAdsBilling.LaunchResult.PRODUCT_UNAVAILABLE -> {
            Toast.makeText(context, R.string.remove_ads_product_unavailable, Toast.LENGTH_SHORT).show()
        }
        RemoveAdsBilling.LaunchResult.FAILED -> {
            Toast.makeText(context, R.string.remove_ads_purchase_failed, Toast.LENGTH_SHORT).show()
        }
    }
}

private fun restoreRemoveAdsPurchase(
    context: android.content.Context,
    billing: RemoveAdsBilling?,
) {
    if (billing == null) {
        Toast.makeText(context, R.string.remove_ads_billing_unavailable, Toast.LENGTH_SHORT).show()
        return
    }
    val appContext = context.applicationContext
    billing.restorePurchases { result ->
        val message = when (result) {
            RemoveAdsBilling.RestoreResult.RESTORED -> R.string.settings_restore_purchase_done
            RemoveAdsBilling.RestoreResult.NONE -> R.string.settings_restore_purchase_none
            RemoveAdsBilling.RestoreResult.UNAVAILABLE -> R.string.remove_ads_billing_unavailable
            RemoveAdsBilling.RestoreResult.FAILED -> R.string.remove_ads_purchase_failed
        }
        Toast.makeText(appContext, message, Toast.LENGTH_SHORT).show()
    }
}
// SMCPKG_SUPPORT<<<Cursor054
// SMCPKG_SUPPORT<<<Cursor038
