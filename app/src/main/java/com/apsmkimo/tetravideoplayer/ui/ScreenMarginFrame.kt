// SMCPKG_SUPPORT>>>Cursor042
/*
 * TetraVideoPlayer
 * Copyright (C) 2026 apsmkimo
 * All rights reserved.
 *
 * This software is proprietary. Unauthorized copying, distribution,
 * modification, or use is strictly prohibited except as expressly
 * permitted in writing by the copyright holder.
 */
// SMCPKG_SUPPORT<<<Cursor042
// SMCPKG_SUPPORT>>>Cursor043
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
// SMCPKG_SUPPORT<<<Cursor043

package com.apsmkimo.tetravideoplayer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.apsmkimo.tetravideoplayer.data.SCREEN_MARGIN_MAX_DP

private val MarginBlack = Color(0xFF000000)

/**
 * Equal inset on every side: `progress / 100 × [SCREEN_MARGIN_MAX_DP]`.
 * Progress 0 is zero and does not change layout.
 */
fun screenMarginInset(progress: Int): Dp {
    val fraction = progress.coerceIn(0, 100) / 100f
    return (SCREEN_MARGIN_MAX_DP * fraction).dp
}

/**
 * Shrinks [content] inside an equal black border.
 *
 * Progress 0 does not pad and does not touch window insets, so the full-bleed
 * video grid stays as it is today.
 *
 * Above 0, system bars and the display cutout are applied first (and consumed,
 * so screens that already pad for them do not pad twice). The equal margin is
 * stacked inside that safe area. The border strips are not ancestors of the
 * panes, and they consume pointers, so a brightness or volume drag cannot start
 * on the border.
 */
@Composable
fun ScreenMarginFrame(
    progress: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val margin = screenMarginInset(progress)
    val safeArea = if (margin > 0.dp) {
        Modifier.windowInsetsPadding(
            WindowInsets.systemBars.union(WindowInsets.displayCutout),
        )
    } else {
        Modifier
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MarginBlack),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(safeArea),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(margin)
                    .clipToBounds(),
            ) {
                content()
            }
            if (margin > 0.dp) {
                MarginEdge(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth()
                        .height(margin),
                )
                MarginEdge(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(margin),
                )
                MarginEdge(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .fillMaxHeight()
                        .width(margin),
                )
                MarginEdge(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .fillMaxHeight()
                        .width(margin),
                )
            }
        }
    }
}

@Composable
private fun MarginEdge(modifier: Modifier) {
    Box(
        modifier = modifier
            .background(MarginBlack)
            .consumeBorderPointers(),
    )
}

/**
 * Sibling of the playback tree, covering only the border. Not an ancestor, so
 * consuming the gesture does not steal drags that start on a pane.
 */
private fun Modifier.consumeBorderPointers(): Modifier = pointerInput(Unit) {
    awaitEachGesture {
        val down = awaitFirstDown(requireUnconsumed = false)
        down.consume()
        while (true) {
            val event = awaitPointerEvent()
            event.changes.forEach { change -> change.consume() }
            if (event.changes.none { it.pressed }) break
        }
    }
}
