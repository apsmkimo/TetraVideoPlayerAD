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
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.graphics.Shader
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.compose.foundation.background
// import androidx.compose.foundation.border
// import androidx.compose.foundation.clickable
// Empty panes use tapWithoutConsumingDown so a vertical swipe still reaches
// the screen-level volume gesture. clickable consumed the pointer-down.
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
// import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
// import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
// import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlin.math.abs
import kotlin.math.roundToInt
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
// import com.example.quadvideoplayer.R
// import com.apsmkimo.tetraview.R
import com.apsmkimo.tetravideoplayer.R

@OptIn(UnstableApi::class)
@Composable
fun VideoCell(
    index: Int,
    player: ExoPlayer,
    videoUri: Uri?,
    onPickVideo: () -> Unit,
    // systemVolume: SystemMusicVolumeController,
    modifier: Modifier = Modifier,
    attachSurface: Boolean = true,
    // onBindPlayerView: (PlayerView?) -> Unit = {},
    onAttachPlayerView: (PlayerView) -> Unit = {},
    onDetachPlayerView: (PlayerView) -> Unit = {},
    onTogglePlay: () -> Unit = {
        if (player.isPlaying) player.pause() else {
            player.playWhenReady = true
            player.play()
        }
    },
    onScrubbingChange: (Boolean) -> Unit = {},
    toolbarLift: Int = 0,
    contrast: Int = 50,
    sharpness: Int = 50,
    onBrightnessAdjusting: (Int?) -> Unit = {},
    safeEdges: WindowInsetsSides? = null,
) {
    // val shape = RoundedCornerShape(12.dp)
    // val borderColor = if (isUnmuted) {
    //     UnmutedGold
    // } else {
    //     MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
    // }
    // var controlsVisible by rememberSaveable(videoUri?.toString()) { mutableStateOf(false) }
    // rememberSaveable(uri) allocated a new MutableState on in-cell swap, while
    // pointerInput(player) kept writing the previous instance — taps no-op'd the
    // toolbar. Volume overlay uses remember{} so vertical drag still worked.
    var controlsVisible by remember { mutableStateOf(false) }
    LaunchedEffect(videoUri?.toString()) {
        controlsVisible = false
    }
    var volumeOverlayVisible by remember { mutableStateOf(false) }
    var overlayVolume by remember { mutableFloatStateOf(player.volume.coerceIn(0f, 1f)) }
    var brightnessOverlayVisible by remember { mutableStateOf(false) }
    var overlayBrightness by remember { mutableIntStateOf(50) }
    val activity = LocalContext.current as? Activity
    val screenWidthDp = LocalConfiguration.current.screenWidthDp
    val screenHeightDp = LocalConfiguration.current.screenHeightDp.dp
    val volumeBarHeight = screenHeightDp * 0.8f
    val volumeTravelPx = VolumeGestureMath.fullRangePixels(
        LocalContext.current.resources.displayMetrics.ydpi,
    )
    // val density = LocalDensity.current

    // Box(
    BoxWithConstraints(
        modifier = modifier
            // .padding(4.dp)
            // .clip(shape)
            // .border(2.dp, borderColor, shape)
            // .background(MaterialTheme.colorScheme.background)
            .background(androidx.compose.ui.graphics.Color.Black)
            // .clickable(
            //     indication = null,
            //     interactionSource = remember { MutableInteractionSource() },
            // ) {
            //     if (videoUri == null) {
            //         onPickVideo()
            //     } else {
            //         controlsVisible = !controlsVisible
            //     }
            // },
            // Empty cells still pick a video. Playing cells use pointerInput only
            // so a tap is not consumed twice (clickable + gesture = net no-op).
            .then(
                if (videoUri == null) {
                    // Modifier.tapWithoutConsumingDown(onPickVideo)
                    // A swipe on an empty pane does nothing. A tap still picks a video.
                    Modifier.clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                    ) {
                        onPickVideo()
                    }
                } else {
                    Modifier
                },
            ),
    ) {
        if (videoUri == null) {
            EmptyVideoPlaceholder(index = index)
        // } else if (!attachSurface) {
        //     // Keep a black placeholder while the picker is open so SurfaceView
        //     // does not punch through the overlay or sit in a zero-size lazy slot.
        //     Box(Modifier.fillMaxSize().background(Color.Black))
        // TextureView: stay mounted so the live ExoPlayer keeps (or re-gains) a surface.
        } else if (!attachSurface) {
            EmptyVideoPlaceholder(index = index)
        } else {
            AndroidView(
                factory = { context ->
                    // PlayerView(context).apply { this.player = player }
                    (LayoutInflater.from(context).inflate(
                        R.layout.player_view_texture,
                        null,
                        false,
                    ) as PlayerView).apply {
                        useController = false
                        controllerAutoShow = false
                        // resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                        resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                        setBackgroundColor(android.graphics.Color.BLACK)
                        setShutterBackgroundColor(android.graphics.Color.BLACK)
                        isClickable = false
                        isFocusable = false
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT,
                        )
                        this.player = player
                    }
                },
                update = { view ->
                    view.useController = false
                    // view.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                    view.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                    view.setBackgroundColor(android.graphics.Color.BLACK)
                    // view.player = player
                    // onBindPlayerView(view)
                    // onAttachPlayerView(view)
                    // Always assign the live ExoPlayer here so first-pick prepare
                    // never races a PlayerView that has no player/surface.
                    view.player = player
                    onAttachPlayerView(view)
                    // val pictureContrast = contrast
                    // view.post { applyVideoContrast(view, pictureContrast) }
                    val pictureContrast = contrast
                    val pictureSharpness = sharpness
                    view.post { applyVideoPicture(view, pictureContrast, pictureSharpness) }
                },
                onRelease = { view ->
                    // onBindPlayerView(null)
                    onDetachPlayerView(view)
                    view.player = null
                },
                modifier = Modifier.fillMaxSize(),
            )

            // Transparent tap target above PlayerView so Compose receives show/hide taps.
            // Box(Modifier.fillMaxSize().clickable { controlsVisible = !controlsVisible })
            // Press + vertical drag on the video surface adjusts this cell's volume.
            // Box(
            //     modifier = Modifier
            //         .fillMaxSize()
            //         .zIndex(1f)
            //         .pointerInput(player) {
            //             val slopPx = 16f
            //             val fullTravelPx = with(density) { volumeBarHeight.toPx() }
            //                 .coerceAtLeast(1f)
            //             awaitEachGesture {
            //                 ...
            //                 if (!dragged) controlsVisible = !controlsVisible
            //                 volume = player.volume - dy / fullTravelPx
            //             }
            //         },
            // ) { ... SurfaceVolumeBar ... }
            // if (controlsVisible) {
            //     CellPlaybackBar(..., Modifier.align(Alignment.BottomCenter))
            // }
            // 1.0.13 hid the toolbar: parent clickable + this tap both toggled
            // controlsVisible, and the zIndex(1) layer sat on top of the bar.
            // Tap (movement <= slop) shows/hides the bar. Vertical-dominant
            // drag changes this pane's ExoPlayer volume, relative to its current
            // level, over about 4 cm. Horizontal drag in the upper half is brightness.
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(1f)
                    // .pointerInput(player) {
                    // .pointerInput(player, videoUri?.toString(), systemVolume) {
                    .pointerInput(player, videoUri?.toString()) {
                        val slopPx = viewConfiguration.touchSlop
                        awaitEachGesture {
                            val down = awaitFirstDown()
                            var dragged = false
                            // var dragged volume-only: vertical drag set player.volume from Y.
                            var adjustingVolume = false
                            var adjustingBrightness = false
                            // var dragStartFraction = systemVolume.fraction
                            // var dragEpoch = systemVolume.externalEpoch
                            // val travelPx = systemVolume.fullRangePx().coerceAtLeast(1f)
                            var dragStartFraction = player.volume.coerceIn(0f, 1f)
                            var dragStartY = down.position.y
                            val travelPx = volumeTravelPx.coerceAtLeast(1f)
                            val cellH = size.height.toFloat().coerceAtLeast(1f)
                            val cellW = size.width.toFloat().coerceAtLeast(1f)
                            val upperHalf = down.position.y < cellH / 2f
                            while (true) {
                                val event = awaitPointerEvent()
                                val change = event.changes.firstOrNull() ?: break
                                if (!change.pressed) {
                                    if (!dragged) {
                                        controlsVisible = !controlsVisible
                                    }
                                    volumeOverlayVisible = false
                                    brightnessOverlayVisible = false
                                    onBrightnessAdjusting(null)
                                    break
                                }
                                val dx = change.position.x - down.position.x
                                val dy = change.position.y - down.position.y
                                // if (!dragged && abs(dy) > slopPx && abs(dy) > abs(dx)) {
                                //     dragged = true
                                //     overlayVolume = player.volume.coerceIn(0f, 1f)
                                //     volumeOverlayVisible = true
                                // }
                                // if (dragged) {
                                //     val next = (1f - (change.position.y.coerceIn(0f, cellH) / cellH))
                                //         .coerceIn(0f, 1f)
                                //     player.volume = next
                                //     overlayVolume = next
                                //     change.consume()
                                // }
                                if (!dragged) {
                                    val vertical = abs(dy) > slopPx && abs(dy) > abs(dx)
                                    val horizontal = abs(dx) > slopPx && abs(dx) > abs(dy)
                                    if (vertical) {
                                        dragged = true
                                        adjustingVolume = true
                                        // dragStartFraction = systemVolume.fraction
                                        // dragEpoch = systemVolume.externalEpoch
                                        // Zero point is where the swipe was recognized,
                                        // from this pane's current player.volume.
                                        dragStartFraction = player.volume.coerceIn(0f, 1f)
                                        dragStartY = change.position.y
                                        overlayVolume = dragStartFraction
                                        volumeOverlayVisible = true
                                    } else if (horizontal && upperHalf) {
                                        dragged = true
                                        adjustingBrightness = true
                                        brightnessOverlayVisible = true
                                    } else if (abs(dx) > slopPx || abs(dy) > slopPx) {
                                        dragged = true
                                    }
                                }
                                if (adjustingVolume) {
                                    // if (systemVolume.externalEpoch != dragEpoch) {
                                    //     dragStartFraction = systemVolume.fraction
                                    //     dragStartY = change.position.y
                                    //     dragEpoch = systemVolume.externalEpoch
                                    // }
                                    // overlayVolume = systemVolume.setFraction(desired)
                                    val desired = VolumeGestureMath.fractionAfterDrag(
                                        startFraction = dragStartFraction,
                                        deltaYPx = change.position.y - dragStartY,
                                        fullRangePx = travelPx,
                                    )
                                    player.volume = desired
                                    overlayVolume = desired
                                    change.consume()
                                } else if (adjustingBrightness) {
                                    val percent = (
                                        (change.position.x.coerceIn(0f, cellW) / cellW) * 100f
                                        ).roundToInt().coerceIn(0, 100)
                                    overlayBrightness = percent
                                    activity?.let { host -> applyWindowBrightness(host, percent) }
                                    onBrightnessAdjusting(percent)
                                    change.consume()
                                }
                            }
                            volumeOverlayVisible = false
                            brightnessOverlayVisible = false
                            onBrightnessAdjusting(null)
                        }
                    },
            ) {
                if (volumeOverlayVisible) {
                    SurfaceVolumeBar(
                        volume = overlayVolume,
                        barHeight = volumeBarHeight,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 16.dp),
                    )
                }
                if (brightnessOverlayVisible) {
                    // val readout = minOf(screenWidthDp, screenHeightDp.value.toInt()).dp / 16f
                    // Box(Modifier.size(readout)) { Text(overlayBrightness, fontSize = readout * 0.38) }
                    val readout = minOf(screenWidthDp, screenHeightDp.value.toInt()).dp / 16f
                    val edgePad = with(LocalDensity.current) { 10.toDp() }
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(start = 8.dp, top = 8.dp)
                            // .size(readout)
                            .clip(RoundedCornerShape(6.dp))
                            .background(androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.72f))
                            .padding(edgePad),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            // text = overlayBrightness.toString(),
                            text = stringResource(R.string.brightness_percent, overlayBrightness),
                            color = androidx.compose.ui.graphics.Color.White,
                            // fontSize = (readout.value * 0.38f).sp,
                            fontSize = (readout.value * 0.38f * 3f).sp,
                            maxLines = 1,
                        )
                    }
                }
            }

            if (controlsVisible) {
                // CellPlaybackBar(..., Modifier.align(Alignment.BottomCenter).zIndex(2f))
                // 0 keeps the bar on the bottom edge. 100 lifts it by the free
                // space in this pane so the bar sits on the top edge.
                val liftFraction = toolbarLift.coerceIn(0, 100) / 100f
                val lift = (maxHeight - CellPlaybackBarHeight).coerceAtLeast(0.dp) * liftFraction
                // Video stays full-bleed. Only the bar moves off the nav bar / cutout.
                // Once lift already clears the bottom inset, do not push the bar further.
                val edgeInsets = if (safeEdges == null) {
                    WindowInsets(0, 0, 0, 0)
                } else {
                    WindowInsets.navigationBars
                        .union(WindowInsets.displayCutout)
                        .only(safeEdges)
                }
                val bottomInset = edgeInsets
                    .only(WindowInsetsSides.Bottom)
                    .asPaddingValues()
                    .calculateBottomPadding()
                val bottomOverlap = (bottomInset - lift).coerceAtLeast(0.dp)
                CellPlaybackBar(
                    player = player,
                    onPickVideo = onPickVideo,
                    onTogglePlay = onTogglePlay,
                    onScrubbingChange = onScrubbingChange,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        // .offset(y = -lift)
                        .offset(y = -(lift + bottomOverlap))
                        .windowInsetsPadding(edgeInsets.only(WindowInsetsSides.Horizontal))
                        .zIndex(2f),
                )
            }
        }

        // CellOverlay(
        //     index = index,
        //     hasVideo = videoUri != null,
        //     isUnmuted = isUnmuted,
        //     onPickVideo = onPickVideo,
        //     modifier = Modifier.align(Alignment.TopStart),
        // )
    }
}

@Composable
private fun EmptyVideoPlaceholder(index: Int) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = Icons.Filled.Movie,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = stringResource(R.string.cell_index, index + 1),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 8.dp),
        )
        Text(
            text = stringResource(R.string.placeholder_title),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = stringResource(R.string.placeholder_hint),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary,
        )
    }
}

@Composable
internal fun SurfaceVolumeBar(
    volume: Float,
    barHeight: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier,
) {
    val fraction = volume.coerceIn(0f, 1f)
    Box(
        modifier = modifier
            .width(28.dp)
            .height(barHeight)
            .clip(RoundedCornerShape(14.dp))
            .background(androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.82f))
            .padding(horizontal = 8.dp, vertical = 10.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(8.dp))
                .background(androidx.compose.ui.graphics.Color.White.copy(alpha = 0.18f)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(fraction)
                    .align(Alignment.BottomCenter)
                    .clip(RoundedCornerShape(8.dp))
                    .background(androidx.compose.ui.graphics.Color.White),
            )
        }
    }
}

private fun applyWindowBrightness(activity: Activity, percent: Int) {
    val window = activity.window
    val attrs = window.attributes
    // 0f is darkest. -1f would mean "use the system brightness".
    attrs.screenBrightness = percent.coerceIn(0, 100) / 100f
    window.attributes = attrs
}

/**
 * Color-matrix contrast on the TextureView. 50 clears the effect so the
 * picture matches the decoder output. API 31+ uses RenderEffect; older
 * releases use a hardware-layer color filter (TextureView may ignore it).
 */
// @OptIn(UnstableApi::class)
// private fun applyVideoContrast(root: View, contrast: Int) { ... contrast only ... }

private const val SHARPEN_SHADER = """
    uniform shader content;
    uniform float amount;
    half4 main(float2 coord) {
        half4 center = content.eval(coord);
        half4 north = content.eval(coord + float2(0.0, -1.0));
        half4 south = content.eval(coord + float2(0.0, 1.0));
        half4 east = content.eval(coord + float2(1.0, 0.0));
        half4 west = content.eval(coord + float2(-1.0, 0.0));
        half4 blur = (north + south + east + west) * 0.25;
        half4 sharpened = center + (center - blur) * amount;
        return half4(clamp(sharpened.rgb, 0.0, 1.0), center.a);
    }
"""

@OptIn(UnstableApi::class)
private fun applyVideoPicture(root: View, contrast: Int, sharpness: Int) {
    val host = (root as? PlayerView)?.videoSurfaceView ?: findTextureView(root) ?: root
    val contrastPercent = contrast.coerceIn(0, 100)
    val sharpnessPercent = sharpness.coerceIn(0, 100)
    if (contrastPercent == 50 && sharpnessPercent == 50) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            host.setRenderEffect(null)
        }
        host.setLayerType(View.LAYER_TYPE_NONE, null)
        return
    }
    val filter = contrastColorFilter(contrastPercent)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        var effect: RenderEffect? = spatialSharpnessEffect(sharpnessPercent)
        if (contrastPercent != 50) {
            val colorEffect = RenderEffect.createColorFilterEffect(filter)
            effect = if (effect == null) {
                colorEffect
            } else {
                RenderEffect.createChainEffect(colorEffect, effect)
            }
        }
        host.setRenderEffect(effect)
        host.setLayerType(View.LAYER_TYPE_NONE, null)
    } else if (contrastPercent != 50) {
        val paint = Paint().apply { colorFilter = filter }
        host.setLayerType(View.LAYER_TYPE_HARDWARE, paint)
    } else {
        host.setLayerType(View.LAYER_TYPE_NONE, null)
    }
}

private fun contrastColorFilter(percent: Int): ColorMatrixColorFilter {
    val scale = percent.coerceIn(0, 100) / 50f
    val translate = (1f - scale) * 127.5f
    return ColorMatrixColorFilter(
        ColorMatrix(
            floatArrayOf(
                scale, 0f, 0f, 0f, translate,
                0f, scale, 0f, 0f, translate,
                0f, 0f, scale, 0f, translate,
                0f, 0f, 0f, 1f, 0f,
            ),
        ),
    )
}

private fun spatialSharpnessEffect(percent: Int): RenderEffect? {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S || percent == 50) return null
    if (percent < 50) {
        val radius = ((50 - percent) / 50f) * 2f
        if (radius < 0.2f) return null
        return RenderEffect.createBlurEffect(radius, radius, Shader.TileMode.CLAMP)
    }
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return null
    val amount = ((percent - 50) / 50f) * 1.5f
    val shader = RuntimeShader(SHARPEN_SHADER)
    shader.setFloatUniform("amount", amount)
    return RenderEffect.createRuntimeShaderEffect(shader, "content")
}

private fun findTextureView(view: View): TextureView? {
    if (view is TextureView) return view
    if (view is ViewGroup) {
        for (index in 0 until view.childCount) {
            findTextureView(view.getChildAt(index))?.let { return it }
        }
    }
    return null
}

// @Composable
// private fun CellOverlay(
//     index: Int,
//     hasVideo: Boolean,
//     isUnmuted: Boolean,
//     onPickVideo: () -> Unit,
//     modifier: Modifier = Modifier,
// ) { ... }
