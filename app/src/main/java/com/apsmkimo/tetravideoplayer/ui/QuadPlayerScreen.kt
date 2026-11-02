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

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
// import androidx.compose.foundation.clickable
// Top chrome uses tapWithoutConsumingDown so a swipe can still change volume.
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DashboardCustomize
// import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
// import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.zIndex
// import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.LifecycleStartEffect
// import com.example.quadvideoplayer.R
// import com.example.quadvideoplayer.data.PlayerLayout
// import com.example.quadvideoplayer.player.QuadPlayerController
// import com.example.quadvideoplayer.util.VideoPermissions
// import com.apsmkimo.tetraview.R
import com.apsmkimo.tetravideoplayer.R
// import com.apsmkimo.tetraview.data.PlayerLayout
import com.apsmkimo.tetravideoplayer.data.PlayerLayout
import com.apsmkimo.tetravideoplayer.data.PlayerSettings
import com.apsmkimo.tetravideoplayer.data.DecodeMode
import com.apsmkimo.tetravideoplayer.data.SETTINGS_PANE_COUNT
// import com.apsmkimo.tetraview.player.QuadPlayerController
import com.apsmkimo.tetravideoplayer.player.QuadPlayerController
// import com.apsmkimo.tetraview.util.VideoPermissions
import com.apsmkimo.tetravideoplayer.util.VideoPermissions
import kotlinx.coroutines.delay

private val Hairline = 1.dp
private val HairlineColor = Color(0xFF5A5A5A)
// private val TopChromeTouch = 48.dp
// private val TopChromeIcon = 44.dp
private val TopChromeTouch = 33.dp
private val TopChromeIcon = 22.dp
private const val TOP_CHROME_IDLE_MS = 10_000L
private const val TOP_CHROME_FADE_MS = 350

/** Top and side system bars / cutout. Bottom stays with the playback bar. */
@Composable
private fun Modifier.playerChromePadding(): Modifier = windowInsetsPadding(
    WindowInsets.statusBars
        .union(WindowInsets.navigationBars)
        .union(WindowInsets.displayCutout)
        .only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal),
)

/**
 * Edges of this pane that can sit under a system bar or cutout.
 * Parents that already consumed those insets (ScreenMarginFrame when
 * the margin is above 0) leave a zero inset, so this does not pad twice.
 */
private fun paneSafeEdges(index: Int, layout: PlayerLayout): WindowInsetsSides? {
    val column = index % layout.columns
    val atStart = column == 0
    val atEnd = column == layout.columns - 1
    val onBottom = index / layout.columns == layout.rows - 1
    val horizontal = when {
        atStart && atEnd -> WindowInsetsSides.Horizontal
        atStart -> WindowInsetsSides.Start
        atEnd -> WindowInsetsSides.End
        else -> null
    }
    return when {
        horizontal != null && onBottom -> horizontal + WindowInsetsSides.Bottom
        horizontal != null -> horizontal
        onBottom -> WindowInsetsSides.Bottom
        else -> null
    }
}

// @Composable
// fun QuadPlayerScreen() {
@Composable
fun QuadPlayerScreen(
    layout: PlayerLayout,
    onChangeLayout: () -> Unit,
    // onAbout: () -> Unit = {},
    settings: PlayerSettings = PlayerSettings(),
    onOpenSettings: () -> Unit = {},
) {
    val context = LocalContext.current
    // val systemVolume = rememberSystemMusicVolume()
    // Volume is per pane. The activity touch relay only restarts the icon fade.
    val topChromeAlpha = remember { Animatable(1f) }
    val fingersDown = remember { mutableIntStateOf(0) }
    DisposableEffect(Unit) {
        PlayerChromeSignals.onTouch = { action, pointerCount ->
            val down = fingersDownAfterEvent(action, pointerCount)
            if (down != fingersDown.intValue) {
                fingersDown.intValue = down
            }
        }
        onDispose { PlayerChromeSignals.onTouch = null }
    }
    LaunchedEffect(fingersDown.intValue) {
        topChromeAlpha.snapTo(1f)
        if (fingersDown.intValue > 0) return@LaunchedEffect
        delay(TOP_CHROME_IDLE_MS)
        topChromeAlpha.animateTo(0.3f, tween(durationMillis = TOP_CHROME_FADE_MS))
    }
    val chromeOpacity = topChromeAlpha.value
    // val controller = remember { QuadPlayerController(context) }
    val controller = remember {
        QuadPlayerController(
            context,
            // initialDecodeModes = settings.panes.map { it.decodeMode },
            // Decode is no longer a setting. Stay on hardware so a saved
            // software mode cannot stick with no control to turn it off.
            initialDecodeModes = List(SETTINGS_PANE_COUNT) { DecodeMode.HARDWARE },
            initialLoop = settings.panes.map { it.loop },
            // Read once. LoadControl cannot change on a live ExoPlayer, so a
            // settings toggle applies the next time this screen is created.
            strongBuffering = settings.strongBuffering,
        )
    }
    var playerGeneration by remember { mutableIntStateOf(0) }
    DisposableEffect(controller) {
        controller.onPlayerReplaced = { playerGeneration++ }
        onDispose { controller.onPlayerReplaced = null }
    }
    // val decodeKey = settings.panes.joinToString(separator = ",") { it.decodeMode.name }
    val loopKey = settings.panes.joinToString(separator = ",") { it.loop.toString() }
    // LaunchedEffect(decodeKey) {
    //     settings.panes.forEachIndexed { index, pane ->
    //         controller.applyDecodeMode(index, pane.decodeMode)
    //     }
    // }
    LaunchedEffect(loopKey) {
        settings.panes.forEachIndexed { index, pane ->
            controller.applyLoop(index, pane.loop)
        }
    }

    // var videoUriStrings by rememberSaveable {
    //     mutableStateOf(List(QuadPlayerController.PLAYER_COUNT) { "" })
    // }
    // Session URIs must not survive SavedState or a return to selection.
    var videoUriStrings by remember {
        mutableStateOf(List(QuadPlayerController.PLAYER_COUNT) { "" })
    }
    // var unmutedIndex by rememberSaveable {
    //     mutableIntStateOf(QuadPlayerController.DEFAULT_UNMUTED_INDEX)
    // }
    // var isGlobalPlaying by rememberSaveable { mutableStateOf(false) }
    var brightnessHud by remember { mutableStateOf<Int?>(null) }
    var pickingIndex by rememberSaveable { mutableIntStateOf(0) }
    var hasPermission by remember { mutableStateOf(VideoPermissions.hasReadAccess(context)) }
    var showPicker by remember { mutableStateOf(false) }
    var pendingPicker by remember { mutableStateOf(false) }
    val playingSnapshot = remember { MutableList(QuadPlayerController.PLAYER_COUNT) { false } }
    // var pendingPick by remember { mutableStateOf<Pair<Int, String>?>(null) }
    // val pickerResumeFlags = remember { MutableList(QuadPlayerController.PLAYER_COUNT) { false } }
    // var pausedForPicker by remember { mutableStateOf(false) }
    // var surfacesReady by remember { mutableStateOf(true) }

    DisposableEffect(controller) {
        onDispose { controller.releaseAll() }
    }

    LaunchedEffect(layout) {
        delay(32)
        // Pause panes that are not composed in 2-window modes so they
        // cannot play audio without a surface. Visible cells are restored.
        for (index in layout.paneCount until QuadPlayerController.PLAYER_COUNT) {
            controller.players.getOrNull(index)?.pause()
        }
        controller.reattachAllBoundViews()
        controller.restorePlayback(
            List(QuadPlayerController.PLAYER_COUNT) { index ->
                // controller.players.getOrNull(index)?.mediaItemCount?.let { it > 0 } == true
                index < layout.paneCount &&
                    controller.players.getOrNull(index)?.mediaItemCount?.let { it > 0 } == true
            },
        )
    }

    LifecycleStartEffect(controller) {
        controller.reattachAllBoundViews()
        controller.resumePlaying(playingSnapshot.toList())
        onStopOrDispose {
            val snapshot = controller.snapshotPlaying()
            snapshot.forEachIndexed { index, playing ->
                if (index < playingSnapshot.size) {
                    playingSnapshot[index] = playing
                }
            }
            controller.pauseAll()
        }
    }

    LifecycleResumeEffect(Unit) {
        hasPermission = VideoPermissions.hasReadAccess(context)
        onPauseOrDispose { }
    }

    // LaunchedEffect(unmutedIndex) {
    //     controller.setUnmuted(unmutedIndex)
    // }
    //
    // LaunchedEffect(isGlobalPlaying) {
    //     controller.setPlaying(isGlobalPlaying)
    // }

    // videoUriStrings.forEachIndexed { index, uriString ->
    //     LaunchedEffect(index, uriString) {
    //         controller.setVideo(index, uriString.takeIf { it.isNotEmpty() }?.let(Uri::parse),
    //             playWhenReady = uriString.isNotEmpty())
    //     }
    // }
    // Per-index effect: only THIS slot's URI. Empty slots do not call setVideo(null).
    // repeat(QuadPlayerController.PLAYER_COUNT) { index ->
    repeat(layout.paneCount) { index ->
        val uriString = videoUriStrings.getOrElse(index) { "" }
        key(index) {
            LaunchedEffect(uriString) {
                if (uriString.isEmpty()) return@LaunchedEffect
                // First pick composes AndroidView in this same snapshot; wait one
                // frame so attachPlayerView can bind the TextureView before prepare.
                // delay(16)
                controller.setVideo(
                    index = index,
                    uri = Uri.parse(uriString),
                    playWhenReady = true,
                )
            }
        }
    }

    // LaunchedEffect(showPicker) { pauseAll(); surfacesReady = false; restorePlayback() }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        hasPermission = granted || VideoPermissions.hasReadAccess(context)
        if (hasPermission && pendingPicker) {
            showPicker = true
            pendingPicker = false
        }
    }

    // val documentLauncher = rememberLauncherForActivityResult(
    //     contract = ActivityResultContracts.OpenDocument(),
    // ) { uri ->
    //     if (uri == null) return@rememberLauncherForActivityResult
    //     try {
    //         context.contentResolver.takePersistableUriPermission(
    //             uri,
    //             Intent.FLAG_GRANT_READ_URI_PERMISSION,
    //         )
    //     } catch (_: SecurityException) {
    //     }
    //     videoUriStrings = videoUriStrings.toMutableList().also { list ->
    //         list[pickingIndex] = uri.toString()
    //     }
    // }
    //
    // fun pickVideo(index: Int) {
    //     pickingIndex = index
    //     documentLauncher.launch(arrayOf("video/*"))
    // }
    fun applyPickedUri(uri: Uri) {
        try {
            context.contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION,
            )
        } catch (_: SecurityException) {
            // MediaStore URIs rely on READ_MEDIA_VIDEO / READ_EXTERNAL_STORAGE.
        }
        // videoUriStrings = videoUriStrings.toMutableList().also { list ->
        //     list[pickingIndex] = uri.toString()
        // }
        // showPicker = false
        // pendingPicker = false
        val index = pickingIndex
        showPicker = false
        pendingPicker = false
        // if (index !in 0 until QuadPlayerController.PLAYER_COUNT) return
        if (index !in 0 until layout.paneCount) return
        // pendingPick = index to uri.toString()
        videoUriStrings = videoUriStrings.toMutableList().also { list ->
            list[index] = uri.toString()
        }
    }

    fun pickVideo(index: Int) {
        // if (index !in 0 until QuadPlayerController.PLAYER_COUNT) return
        if (index !in 0 until layout.paneCount) return
        pickingIndex = index
        if (VideoPermissions.hasReadAccess(context)) {
            hasPermission = true
            showPicker = true
        } else {
            pendingPicker = true
            permissionLauncher.launch(VideoPermissions.requiredPermission())
        }
    }

    fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
        }
        context.startActivity(intent)
    }

    fun leaveToSelection() {
        videoUriStrings = List(QuadPlayerController.PLAYER_COUNT) { "" }
        controller.clearAllMedia()
        onChangeLayout()
    }
    // Picker handles its own back while a folder is open. At the picker root,
    // back still dismisses nothing here so the existing close control stays.
    ConfirmBackHandler(
        enabled = !showPicker,
        message = stringResource(R.string.back_again_to_return),
        onConfirmed = { leaveToSelection() },
    )

    // Scaffold(topBar = { TopAppBar(...) }) { innerPadding ->
    //     Column(Modifier.padding(innerPadding).padding(horizontal = 8.dp, vertical = 4.dp)) { ... }
    // }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(HairlineColor)
            // .relativeSystemVolumeSwipe(
            //     volume = systemVolume,
            //     acceptConsumedDown = false,
            //     driveScreenHud = true,
            // ),
            // Screen-level STREAM_MUSIC swipe removed. Only a playing pane adjusts
            // its own ExoPlayer volume.
    ) {
        // BoxWithConstraints + LazyVerticalGrid used computed cellHeight. In 1x4
        // the 4th row could land on a clipped/zero lazy slot; PlayerView's
        // SurfaceView then died the process when the 4th video attached.
        // BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        //     val columns = layout.columns
        //     val rows = layout.rows
        //     val cellHeight = (maxHeight - Hairline * (rows - 1).coerceAtLeast(0)) / rows
        //     LazyVerticalGrid(...) { items(PLAYER_COUNT) { ... height(cellHeight) } }
        // }
        PlayerPaneGrid(
            layout = layout,
            modifier = Modifier.fillMaxSize(),
        ) { index ->
            val uri = videoUriStrings.getOrNull(index)
                ?.takeIf { it.isNotEmpty() }
                ?.let(Uri::parse)
            // val player = controller.players.getOrNull(index) ?: return@PlayerPaneGrid
            // playerGeneration changes when one pane's ExoPlayer is rebuilt.
            if (playerGeneration < 0) return@PlayerPaneGrid
            val player = controller.players.getOrNull(index) ?: return@PlayerPaneGrid
            VideoCell(
                index = index,
                player = player,
                videoUri = uri,
                onPickVideo = { pickVideo(index) },
                // systemVolume = systemVolume,
                // attachSurface = !(showPicker && index == pickingIndex),
                // TextureView does not punch through the picker. Keep the cell's
                // PlayerView mounted so setVideo can attach → prepare → play.
                attachSurface = true,
                onTogglePlay = { controller.togglePlay(index) },
                onScrubbingChange = { active -> controller.setScrubbing(index, active) },
                onAttachPlayerView = { view -> controller.attachPlayerView(index, view) },
                onDetachPlayerView = { view -> controller.detachPlayerView(index, view) },
                toolbarLift = settings.pane(index).toolbarLift,
                // contrast = settings.contrast,
                contrast = settings.pane(index).contrast,
                sharpness = settings.pane(index).sharpness,
                onBrightnessAdjusting = { brightnessHud = it },
                safeEdges = paneSafeEdges(index, layout),
                modifier = Modifier.fillMaxSize(),
            )
        }

        if (!showPicker) {
            // Row(...) { About IconButton 40.dp; layout IconButton 40.dp }
            // About stays on LauncherSelectionScreen only. Layout chip is 50% of 40.dp.
            // Box( ... DashboardCustomize only ... )
            // val iconNudge = with(LocalDensity.current) { 10.toDp() }
            // val chromeBg = Color(0x99000000)
            // val chromeBg = Color(0x66000000)
            // Nudge and scrim now live in PlayerTopChrome.
            // Settings was left of the layout icon. Layout is now left, gear right.
            // 20.dp chips / 12.dp glyphs grew to a 48.dp target and a 44.dp glyph.
            // The row fades to 0.3 after 10s idle. The playback bar does not.
            // if (settings.showPlayerChrome) {
            if (settings.showPlayerChrome) {
            PlayerTopChrome(
                chromeOpacity = chromeOpacity,
                onChangeLayout = { leaveToSelection() },
                onOpenSettings = onOpenSettings,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .playerChromePadding(),
            )
            }
            PermissionBanner(
                granted = hasPermission,
                onGrantClick = {
                    permissionLauncher.launch(VideoPermissions.requiredPermission())
                },
                onSettingsClick = { openAppSettings() },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    // .statusBarsPadding()
                    .playerChromePadding()
                    .fillMaxWidth(),
            )
        }

        val shownBrightness = brightnessHud
        if (shownBrightness != null && !showPicker) {
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    // .statusBarsPadding()
                    .playerChromePadding()
                    .zIndex(4f)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
            ) {
                Text(
                    text = stringResource(R.string.brightness_bar, shownBrightness),
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                )
                Box(
                    modifier = Modifier
                        .padding(top = 6.dp)
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.White.copy(alpha = 0.28f)),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(shownBrightness.coerceIn(0, 100) / 100f)
                            .background(Color.White),
                    )
                }
            }
        }

        // if (!showPicker && (systemVolume.hudVisible || systemVolume.flashVisible)) {
        //     SurfaceVolumeBar(volume = systemVolume.fraction, ...)
        // }
        // The pane under the finger draws its own volume bar from player.volume.

        if (showPicker) {
            VideoPickerScreen(
                onVideoSelected = { uri -> applyPickedUri(uri) },
                onDismiss = {
                    showPicker = false
                    pendingPicker = false
                },
            )
        }
    }
}

@Composable
private fun PlayerPaneGrid(
    layout: PlayerLayout,
    modifier: Modifier = Modifier,
    cell: @Composable (index: Int) -> Unit,
) {
    when (layout) {
        PlayerLayout.VERTICAL_1X4 -> {
            Column(
                modifier = modifier,
                verticalArrangement = Arrangement.spacedBy(Hairline),
            ) {
                repeat(QuadPlayerController.PLAYER_COUNT) { index ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                    ) {
                        key(index) { cell(index) }
                    }
                }
            }
        }

        PlayerLayout.LANDSCAPE_2X2 -> {
            Column(
                modifier = modifier,
                verticalArrangement = Arrangement.spacedBy(Hairline),
            ) {
                repeat(2) { row ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(Hairline),
                    ) {
                        repeat(2) { col ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight(),
                            ) {
                                key(row * 2 + col) { cell(row * 2 + col) }
                            }
                        }
                    }
                }
            }
        }

        PlayerLayout.VERTICAL_1X2 -> {
            Column(
                modifier = modifier,
                verticalArrangement = Arrangement.spacedBy(Hairline),
            ) {
                repeat(2) { index ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                    ) {
                        key(index) { cell(index) }
                    }
                }
            }
        }

        PlayerLayout.LANDSCAPE_2X1 -> {
            Row(
                modifier = modifier,
                horizontalArrangement = Arrangement.spacedBy(Hairline),
            ) {
                repeat(2) { index ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                    ) {
                        key(index) { cell(index) }
                    }
                }
            }
        }

        PlayerLayout.VERTICAL_1X1 -> {
            Box(modifier = modifier) {
                key(0) { cell(0) }
            }
        }

        PlayerLayout.VERTICAL_1X3 -> {
            Column(
                modifier = modifier,
                verticalArrangement = Arrangement.spacedBy(Hairline),
            ) {
                repeat(3) { index ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                    ) {
                        key(index) { cell(index) }
                    }
                }
            }
        }
    }
}

@Composable
internal fun PlayerTopChrome(
    chromeOpacity: Float,
    onChangeLayout: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val iconNudge = with(LocalDensity.current) { 10.toDp() }
    val chromeBg = Color(0x66000000)
    Row(
        modifier = modifier
            .padding(end = 8.dp + iconNudge, top = 4.dp)
            .alpha(chromeOpacity),
        // horizontalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(TopChromeTouch)
                .clip(CircleShape)
                .background(chromeBg)
                // .tapWithoutConsumingDown(onChangeLayout),
                .clickable(onClick = onChangeLayout),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.DashboardCustomize,
                contentDescription = stringResource(R.string.change_layout),
                tint = Color.White,
                modifier = Modifier.size(TopChromeIcon),
            )
        }
        Box(
            modifier = Modifier
                .size(TopChromeTouch)
                .clip(CircleShape)
                .background(chromeBg)
                // .tapWithoutConsumingDown(onOpenSettings),
                .clickable(onClick = onOpenSettings),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = SettingsGearIcon,
                contentDescription = stringResource(R.string.settings_open),
                tint = Color.White,
                modifier = Modifier.size(TopChromeIcon),
            )
        }
    }
}
