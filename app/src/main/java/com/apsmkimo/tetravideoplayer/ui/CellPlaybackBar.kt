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

// SMCPKG_SUPPORT>>>Cursor021
// package com.example.quadvideoplayer.ui
// SMCPKG_SUPPORT>>>Cursor030
// package com.apsmkimo.tetraview.ui
package com.apsmkimo.tetravideoplayer.ui
// SMCPKG_SUPPORT<<<Cursor030
// SMCPKG_SUPPORT<<<Cursor021

import androidx.compose.foundation.background
// SMCPKG_SUPPORT>>>Cursor049
// import androidx.compose.foundation.clickable
// Bar buttons use tapWithoutConsumingDown so a vertical swipe that starts on
// an icon still reaches the screen-level volume gesture.
// SMCPKG_SUPPORT<<<Cursor049
// SMCPKG_SUPPORT>>>Cursor050
import androidx.compose.foundation.clickable
// SMCPKG_SUPPORT<<<Cursor050
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
// SMCPKG_SUPPORT>>>Cursor022
// import androidx.compose.material3.IconButton
// SMCPKG_SUPPORT<<<Cursor022
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
// SMCPKG_SUPPORT>>>Cursor052
import android.os.SystemClock
import androidx.media3.exoplayer.SeekParameters
// SMCPKG_SUPPORT<<<Cursor052
// SMCPKG_SUPPORT>>>Cursor021
// import com.example.quadvideoplayer.R
// import com.example.quadvideoplayer.data.LocalVideoStore
// SMCPKG_SUPPORT>>>Cursor030
// import com.apsmkimo.tetraview.R
import com.apsmkimo.tetravideoplayer.R
// SMCPKG_SUPPORT<<<Cursor030
// SMCPKG_SUPPORT>>>Cursor030
// import com.apsmkimo.tetraview.data.LocalVideoStore
import com.apsmkimo.tetravideoplayer.data.LocalVideoStore
// SMCPKG_SUPPORT<<<Cursor030
// SMCPKG_SUPPORT<<<Cursor021
import kotlin.math.abs
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withTimeoutOrNull

// SMCPKG_SUPPORT>>>Cursor049
@OptIn(ExperimentalMaterial3Api::class)
// SMCPKG_SUPPORT<<<Cursor049
@Composable
fun CellPlaybackBar(
    player: ExoPlayer,
    onPickVideo: () -> Unit,
    modifier: Modifier = Modifier,
    // SMCPKG_SUPPORT>>>Cursor017
    onTogglePlay: (() -> Unit)? = null,
    // SMCPKG_SUPPORT<<<Cursor017
    // SMCPKG_SUPPORT>>>Cursor052
    onScrubbingChange: (Boolean) -> Unit = {},
    // SMCPKG_SUPPORT<<<Cursor052
) {
    var isPlaying by remember(player) { mutableStateOf(player.isPlaying) }
    var durationMs by remember(player) { mutableLongStateOf(resolvedDuration(player)) }
    var sliderValue by remember(player) { mutableFloatStateOf(player.currentPosition.toFloat()) }
    var isSeeking by remember { mutableStateOf(false) }
    // SMCPKG_SUPPORT>>>Cursor052
    val scrub = remember(player) { ScrubSession() }
    val currentScrubbingChange = rememberUpdatedState(onScrubbingChange)
    // SMCPKG_SUPPORT<<<Cursor052
    var volume by remember(player) { mutableFloatStateOf(player.volume.coerceIn(0f, 1f)) }
    var lastAudibleVolume by remember(player) {
        mutableFloatStateOf(player.volume.takeIf { it > 0f } ?: 1f)
    }

    DisposableEffect(player) {
        val listener = object : Player.Listener {
            override fun onEvents(player: Player, events: Player.Events) {
                // SMCPKG_SUPPORT>>>Cursor052
                // isPlaying = player.isPlaying
                // Keep the play/pause icon stable while this pane is scrubbing.
                if (!scrub.active) {
                    isPlaying = player.isPlaying
                }
                // SMCPKG_SUPPORT<<<Cursor052
                val duration = resolvedDuration(player)
                if (duration > 0L) {
                    durationMs = duration
                }
                // SMCPKG_SUPPORT>>>Cursor052
                // if (!isSeeking) {
                if (!scrub.active) {
                // SMCPKG_SUPPORT<<<Cursor052
                    sliderValue = player.currentPosition.toFloat()
                }
                volume = player.volume.coerceIn(0f, 1f)
                if (volume > 0f) {
                    lastAudibleVolume = volume
                }
            }
        }
        player.addListener(listener)
        onDispose {
            player.removeListener(listener)
            // SMCPKG_SUPPORT>>>Cursor052
            if (scrub.active) {
                scrub.active = false
                scrub.pendingMs = -1L
                runCatching { player.setSeekParameters(SeekParameters.DEFAULT) }
                currentScrubbingChange.value(false)
                if (scrub.resume) {
                    runCatching {
                        player.playWhenReady = true
                        player.play()
                    }
                }
            }
            // SMCPKG_SUPPORT<<<Cursor052
        }
    }

    // SMCPKG_SUPPORT>>>Cursor052
    LaunchedEffect(player, isSeeking) {
        if (!isSeeking) return@LaunchedEffect
        while (isActive && scrub.active) {
            delay(ScrubSeek.PREVIEW_INTERVAL_MS)
            if (!scrub.active) break
            val pending = scrub.pendingMs
            if (pending >= 0L) {
                scrub.pendingMs = -1L
                scrub.lastPreviewAtMs = SystemClock.uptimeMillis()
                runCatching {
                    player.setSeekParameters(SeekParameters.CLOSEST_SYNC)
                    player.seekTo(pending)
                }
            }
        }
    }

    fun beginScrub() {
        if (scrub.active) return
        scrub.active = true
        scrub.resume = player.playWhenReady
        scrub.pendingMs = -1L
        scrub.lastPreviewAtMs = 0L
        isSeeking = true
        runCatching { player.setSeekParameters(SeekParameters.CLOSEST_SYNC) }
        currentScrubbingChange.value(true)
        player.pause()
    }

    fun previewScrub(positionMs: Long) {
        val target = positionMs.coerceAtLeast(0L)
        val now = SystemClock.uptimeMillis()
        if (ScrubSeek.shouldPreview(now, scrub.lastPreviewAtMs)) {
            scrub.lastPreviewAtMs = now
            scrub.pendingMs = -1L
            runCatching {
                player.setSeekParameters(SeekParameters.CLOSEST_SYNC)
                player.seekTo(target)
            }
        } else {
            scrub.pendingMs = target
        }
    }

    fun finishScrub() {
        if (!scrub.active) return
        val target = sliderValue.toLong().coerceAtLeast(0L)
        val resume = scrub.resume
        scrub.active = false
        scrub.pendingMs = -1L
        scrub.lastPreviewAtMs = 0L
        runCatching {
            player.setSeekParameters(SeekParameters.EXACT)
            player.seekTo(target)
            player.setSeekParameters(SeekParameters.DEFAULT)
        }
        isSeeking = false
        currentScrubbingChange.value(false)
        if (resume) {
            player.playWhenReady = true
            runCatching { player.play() }
        }
    }
    // SMCPKG_SUPPORT<<<Cursor052

    LaunchedEffect(player, isPlaying, isSeeking) {
        while (isActive && isPlaying && !isSeeking) {
            val duration = resolvedDuration(player)
            if (duration > 0L) {
                durationMs = duration
            }
            sliderValue = player.currentPosition.toFloat()
            delay(200)
        }
    }

    val maxValue = durationMs.toFloat().coerceAtLeast(1f)
    val elapsedMs = sliderValue.toLong().coerceAtLeast(0L)
    val isMuted = volume <= 0f
    // SMCPKG_SUPPORT>>>Cursor049
    val sliderInteraction = remember { MutableInteractionSource() }
    val sliderColors = SliderDefaults.colors(
        thumbColor = Color.White,
        activeTrackColor = Color.White,
        inactiveTrackColor = Color.White.copy(alpha = 0.35f),
        disabledThumbColor = Color.White.copy(alpha = 0.5f),
        disabledActiveTrackColor = Color.White.copy(alpha = 0.35f),
    )
    val timeStyle = MaterialTheme.typography.labelSmall.copy(
        fontSize = BarTimeFontSize,
        lineHeight = BarTimeLineHeight,
    )
    // SMCPKG_SUPPORT<<<Cursor049

    // SMCPKG_SUPPORT>>>Cursor022
    // Row(fillMaxWidth, default IconButton 48.dp + Volume 40.dp) — icons sat
    // on different baselines and the bar was ~48.dp tall.
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(BarHeight)
            .background(Color.Black.copy(alpha = 0.72f))
            // SMCPKG_SUPPORT>>>Cursor049
            // .padding(horizontal = 4.dp),
            // SMCPKG_SUPPORT<<<Cursor049
            // SMCPKG_SUPPORT>>>Cursor050
            .padding(horizontal = 2.dp),
            // SMCPKG_SUPPORT<<<Cursor050
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // SMCPKG_SUPPORT>>>Cursor049
        // Play/pause used to be the leftmost control. Volume is now leftmost;
        // play/pause sits immediately to its right. Mute/unmute is unchanged.
        // CompactBarIconButton(onClick = { onTogglePlay / pause }) { PlayArrow }
        // SMCPKG_SUPPORT<<<Cursor049

        // SMCPKG_SUPPORT>>>Cursor006
        // SMCPKG_SUPPORT>>>Cursor023
        // VolumeGestureIcon(... onVolumeDelta ... hold + swipe on speaker)
        // Speaker mutes and unmutes only this pane. The level is this pane's
        // ExoPlayer volume, changed by a vertical swipe on the video.
        CompactBarIconButton(
            onClick = {
                if (player.volume > 0f) {
                    lastAudibleVolume = player.volume
                    player.volume = 0f
                    volume = 0f
                } else {
                    val restored = lastAudibleVolume.takeIf { it > 0f } ?: 1f
                    player.volume = restored
                    volume = restored
                }
            },
        ) {
            Icon(
                imageVector = if (isMuted) {
                    Icons.AutoMirrored.Filled.VolumeOff
                } else {
                    Icons.AutoMirrored.Filled.VolumeUp
                },
                contentDescription = stringResource(
                    if (isMuted) R.string.cell_unmute else R.string.cell_mute,
                ),
                tint = Color.White,
                modifier = Modifier.size(BarIconSize),
            )
        }
        // SMCPKG_SUPPORT<<<Cursor023

        // SMCPKG_SUPPORT>>>Cursor049
        CompactBarIconButton(
            onClick = {
                // SMCPKG_SUPPORT>>>Cursor017
                // if (player.isPlaying) {
                //     player.pause()
                // } else {
                //     player.play()
                // }
                if (onTogglePlay != null) {
                    onTogglePlay()
                } else if (player.isPlaying) {
                    player.pause()
                } else {
                    player.playWhenReady = true
                    player.play()
                }
                // SMCPKG_SUPPORT<<<Cursor017
            },
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                contentDescription = stringResource(
                    if (isPlaying) R.string.cell_pause else R.string.cell_play,
                ),
                tint = Color.White,
                modifier = Modifier.size(BarIconSize),
            )
        }
        // SMCPKG_SUPPORT<<<Cursor049
        // SMCPKG_SUPPORT<<<Cursor022

        Text(
            text = LocalVideoStore.formatDuration(durationMs),
            // SMCPKG_SUPPORT>>>Cursor049
            // style = MaterialTheme.typography.labelSmall,
            style = timeStyle,
            // SMCPKG_SUPPORT<<<Cursor049
            color = Color.White,
            textAlign = TextAlign.End,
            modifier = Modifier
                // SMCPKG_SUPPORT>>>Cursor049
                // .widthIn(min = BarTimeMinWidth)
                // .padding(end = 4.dp),
                // SMCPKG_SUPPORT<<<Cursor049
                // SMCPKG_SUPPORT>>>Cursor050
                .widthIn(min = BarTimeMinWidth)
                .padding(end = 2.dp),
                // SMCPKG_SUPPORT<<<Cursor050
        )
        // SMCPKG_SUPPORT<<<Cursor006

        Slider(
            value = sliderValue.coerceIn(0f, maxValue),
            // SMCPKG_SUPPORT>>>Cursor052
            // onValueChange = { value ->
            //     isSeeking = true
            //     sliderValue = value
            // },
            // onValueChangeFinished = {
            //     player.seekTo(sliderValue.toLong().coerceAtLeast(0L))
            //     isSeeking = false
            // },
            // Drag previews with CLOSEST_SYNC. Release seeks EXACT, then restores DEFAULT.
            onValueChange = { value ->
                beginScrub()
                sliderValue = value
                previewScrub(value.toLong())
            },
            onValueChangeFinished = { finishScrub() },
            // SMCPKG_SUPPORT<<<Cursor052
            valueRange = 0f..maxValue,
            enabled = durationMs > 0L,
            modifier = Modifier
                .weight(1f)
                .height(BarHeight),
            // SMCPKG_SUPPORT>>>Cursor049
            // colors = SliderDefaults.colors(
            //     thumbColor = Color.White,
            //     activeTrackColor = Color.White,
            //     inactiveTrackColor = Color.White.copy(alpha = 0.35f),
            //     disabledThumbColor = Color.White.copy(alpha = 0.5f),
            //     disabledActiveTrackColor = Color.White.copy(alpha = 0.35f),
            // ),
            colors = sliderColors,
            interactionSource = sliderInteraction,
            thumb = {
                SliderDefaults.Thumb(
                    interactionSource = sliderInteraction,
                    colors = sliderColors,
                    enabled = durationMs > 0L,
                    thumbSize = DpSize(BarThumbWidth, BarThumbHeight),
                )
            },
            track = { sliderState ->
                SliderDefaults.Track(
                    sliderState = sliderState,
                    colors = sliderColors,
                    enabled = durationMs > 0L,
                )
            },
            // SMCPKG_SUPPORT<<<Cursor049
        )

        // SMCPKG_SUPPORT>>>Cursor006
        Text(
            text = LocalVideoStore.formatDuration(elapsedMs),
            // SMCPKG_SUPPORT>>>Cursor049
            // style = MaterialTheme.typography.labelSmall,
            style = timeStyle,
            // SMCPKG_SUPPORT<<<Cursor049
            color = Color.White,
            textAlign = TextAlign.Start,
            modifier = Modifier
                // SMCPKG_SUPPORT>>>Cursor049
                // .widthIn(min = BarTimeMinWidth)
                // .padding(start = 4.dp),
                // SMCPKG_SUPPORT<<<Cursor049
                // SMCPKG_SUPPORT>>>Cursor050
                .widthIn(min = BarTimeMinWidth)
                .padding(start = 2.dp),
                // SMCPKG_SUPPORT<<<Cursor050
        )
        // SMCPKG_SUPPORT<<<Cursor006

        // SMCPKG_SUPPORT>>>Cursor022
        // IconButton(onClick = onPickVideo) { Icon(FolderOpen) }
        CompactBarIconButton(onClick = onPickVideo) {
            Icon(
                imageVector = Icons.Outlined.FolderOpen,
                contentDescription = stringResource(R.string.pick_video),
                tint = Color.White,
                modifier = Modifier.size(BarIconSize),
            )
        }
        // SMCPKG_SUPPORT<<<Cursor022
    }
}

@Composable
private fun VolumeGestureIcon(
    volume: Float,
    isMuted: Boolean,
    onToggleMute: () -> Unit,
    onVolumeDelta: (Float) -> Unit,
) {
    // SMCPKG_SUPPORT>>>Cursor007
    // Immediate drag-to-adjust without an overlay. Replaced by press-and-hold bar.
    var overlayVisible by remember { mutableStateOf(false) }
    val touchSlopPx = 16f
    val dragSensitivity = 220f
    val holdTimeoutMs = 260L

    // SMCPKG_SUPPORT>>>Cursor022
    // Box(Modifier.size(40.dp)) — sat higher than play IconButton; now matches bar.
    Box(
        modifier = Modifier
            .size(BarButtonSize)
            .zIndex(2f)
            .pointerInput(Unit) {
    // SMCPKG_SUPPORT<<<Cursor022
                awaitEachGesture {
                    val down = awaitFirstDown()
                    var overlayShown = false
                    var lastY = down.position.y
                    while (true) {
                        val event = if (overlayShown) {
                            awaitPointerEvent()
                        } else {
                            withTimeoutOrNull(holdTimeoutMs) { awaitPointerEvent() }
                        }
                        if (event == null) {
                            overlayShown = true
                            overlayVisible = true
                            continue
                        }
                        val change = event.changes.firstOrNull() ?: break
                        if (!change.pressed) {
                            if (!overlayShown) {
                                onToggleMute()
                            }
                            overlayVisible = false
                            break
                        }
                        val dy = change.position.y - lastY
                        if (!overlayShown && abs(change.position.y - down.position.y) > touchSlopPx) {
                            overlayShown = true
                            overlayVisible = true
                        }
                        if (overlayShown) {
                            onVolumeDelta(-dy / dragSensitivity)
                            change.consume()
                        }
                        lastY = change.position.y
                    }
                    overlayVisible = false
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = if (isMuted) {
                Icons.AutoMirrored.Filled.VolumeOff
            } else {
                Icons.AutoMirrored.Filled.VolumeUp
            },
            contentDescription = stringResource(
                if (isMuted) R.string.cell_unmute else R.string.cell_mute,
            ),
            tint = Color.White,
            modifier = Modifier.size(BarIconSize),
        )
        if (overlayVisible) {
            VerticalVolumeOverlay(
                volume = volume,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    // SMCPKG_SUPPORT>>>Cursor022
                    // .offset(y = (-44).dp),
                    .offset(y = (-VolumeOverlayLift)),
                // SMCPKG_SUPPORT<<<Cursor022
            )
        }
    }
    // SMCPKG_SUPPORT<<<Cursor007
}

@Composable
private fun VerticalVolumeOverlay(
    volume: Float,
    modifier: Modifier = Modifier,
) {
    val fraction = volume.coerceIn(0f, 1f)
    Box(
        modifier = modifier
            .width(28.dp)
            .height(112.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color.Black.copy(alpha = 0.82f))
            .padding(horizontal = 9.dp, vertical = 10.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White.copy(alpha = 0.18f)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(fraction)
                    .align(Alignment.BottomCenter)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White),
            )
        }
    }
}

// SMCPKG_SUPPORT>>>Cursor022
// SMCPKG_SUPPORT>>>Cursor038
// private val BarHeight = 24.dp
// SMCPKG_SUPPORT>>>Cursor049
// internal val CellPlaybackBarHeight = 24.dp
// private val BarButtonSize = 24.dp
// private val BarIconSize = 19.dp
// private val VolumeOverlayLift = 56.dp
// Toolbar grows to ~64dp. Icons, type, and the seek thumb scale with it.
// Button width stays 48dp so a phone-width pane still keeps a seek bar.
// internal val CellPlaybackBarHeight = 64.dp
// private val BarButtonSize = 48.dp
// private val BarIconSize = 40.dp
// private val BarTimeFontSize = 20.sp
// private val BarTimeLineHeight = 24.sp
// private val BarTimeMinWidth = 64.dp
// private val BarThumbWidth = 8.dp
// private val BarThumbHeight = 48.dp
// private val VolumeOverlayLift = 96.dp
// SMCPKG_SUPPORT>>>Cursor050
// Half of the 64dp bar: 32dp tall, icons and thumb scaled with it.
internal val CellPlaybackBarHeight = 32.dp
private val BarHeight = CellPlaybackBarHeight
private val BarButtonSize = 24.dp
private val BarIconSize = 20.dp
private val BarTimeFontSize = 10.sp
private val BarTimeLineHeight = 12.sp
private val BarTimeMinWidth = 32.dp
private val BarThumbWidth = 4.dp
private val BarThumbHeight = 24.dp
private val VolumeOverlayLift = 48.dp
// SMCPKG_SUPPORT<<<Cursor050
// SMCPKG_SUPPORT<<<Cursor049
// SMCPKG_SUPPORT<<<Cursor038
// private val BarButtonSize = 24.dp
// private val BarIconSize = 19.dp
// private val VolumeOverlayLift = 56.dp

@Composable
private fun CompactBarIconButton(
    onClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier
            // SMCPKG_SUPPORT>>>Cursor049
            // .width(BarButtonSize)
            // .height(BarHeight)
            // .tapWithoutConsumingDown(onClick),
            // SMCPKG_SUPPORT<<<Cursor049
            // SMCPKG_SUPPORT>>>Cursor050
            .width(BarButtonSize)
            .height(BarHeight)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
            // SMCPKG_SUPPORT<<<Cursor050
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}
// SMCPKG_SUPPORT<<<Cursor022

// SMCPKG_SUPPORT>>>Cursor052
private class ScrubSession {
    var active: Boolean = false
    var resume: Boolean = false
    var pendingMs: Long = -1L
    var lastPreviewAtMs: Long = 0L
}
// SMCPKG_SUPPORT<<<Cursor052

private fun resolvedDuration(player: Player): Long {
    val duration = player.duration
    return if (duration == C.TIME_UNSET || duration < 0L) 0L else duration
}
