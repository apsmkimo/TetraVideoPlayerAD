// SMCPKG_SUPPORT>>>Cursor049
/*
 * TetraVideoPlayer
 * Copyright (C) 2026 apsmkimo
 * All rights reserved.
 *
 * This software is proprietary. Unauthorized copying, distribution,
 * modification, or use is strictly prohibited except as expressly
 * permitted in writing by the copyright holder.
 */
package com.apsmkimo.tetravideoplayer.ui

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.changedToDown
import androidx.compose.ui.input.pointer.changedToDownIgnoreConsumed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import kotlin.math.abs

// SMCPKG_SUPPORT>>>Cursor050
// Screen-level volume swipe and non-consuming taps were only for STREAM_MUSIC.
// Pane volume is handled inside the playing cell. These helpers are unused.
// /**
//  * Tap that does not consume the pointer-down. An ancestor can still start a
//  * vertical volume drag from the same contact. The click runs only if the
//  * finger lifts without leaving the touch slop and nobody else consumed the up.
//  */
// @Composable
// fun Modifier.tapWithoutConsumingDown(onTap: () -> Unit): Modifier {
//     val current = rememberUpdatedState(onTap)
//     return this
//         .semantics(mergeDescendants = true) {
//             role = Role.Button
//             onClick {
//                 current.value()
//                 true
//             }
//         }
//         .pointerInput(Unit) {
//             val slop = viewConfiguration.touchSlop
//             awaitEachGesture {
//                 val down = awaitDown(acceptConsumed = true)
//                 var cancelled = false
//                 while (true) {
//                     val event = awaitPointerEvent()
//                     val change = event.changes.firstOrNull { it.id == down.id } ?: break
//                     if (!change.pressed) {
//                         if (!cancelled && !change.isConsumed) {
//                             current.value()
//                             change.consume()
//                         }
//                         break
//                     }
//                     val dx = change.position.x - down.position.x
//                     val dy = change.position.y - down.position.y
//                     if (change.isConsumed || abs(dx) > slop || abs(dy) > slop) {
//                         cancelled = true
//                     }
//                 }
//             }
//         }
// }
//
// /**
//  * Vertical volume drag for contacts that children did not claim.
//  *
//  * Playing panes consume the pointer-down themselves (brightness, tap-to-toggle,
//  * and the in-pane volume drag). This modifier only sees a down that is still
//  * unconsumed, so it cannot steal those gestures. Pass [acceptConsumedDown] only
//  * for a control that already swallowed the down and still wants the drag.
//  */
// @Composable
// fun Modifier.relativeSystemVolumeSwipe(
//     volume: SystemMusicVolumeController,
//     acceptConsumedDown: Boolean,
//     driveScreenHud: Boolean,
// ): Modifier {
//     return pointerInput(volume, acceptConsumedDown, driveScreenHud) {
//         val slop = viewConfiguration.touchSlop
//         awaitEachGesture {
//             val down = awaitDown(acceptConsumed = acceptConsumedDown)
//             val travelPx = volume.fullRangePx().coerceAtLeast(1f)
//             var decided = false
//             var adjusting = false
//             var hudStarted = false
//             var startFraction = volume.fraction
//             var startY = down.position.y
//             var epoch = volume.externalEpoch
//             try {
//                 while (true) {
//                     val event = awaitPointerEvent()
//                     val change = event.changes.firstOrNull { it.id == down.id } ?: break
//                     if (!change.pressed) break
//                     val dx = change.position.x - down.position.x
//                     val dy = change.position.y - down.position.y
//                     if (!decided) {
//                         val vertical = abs(dy) > slop && abs(dy) > abs(dx)
//                         if (vertical) {
//                             decided = true
//                             adjusting = true
//                             // Zero at the finger's current point so the slop
//                             // distance does not jump the level. Still relative:
//                             // the level is whatever the stream is right now.
//                             startFraction = volume.fraction
//                             startY = change.position.y
//                             epoch = volume.externalEpoch
//                             if (driveScreenHud) {
//                                 volume.beginHud()
//                                 hudStarted = true
//                             }
//                         } else if (abs(dx) > slop || abs(dy) > slop) {
//                             decided = true
//                         }
//                     }
//                     if (!adjusting) continue
//                     if (change.isConsumed) break
//                     if (volume.externalEpoch != epoch) {
//                         startFraction = volume.fraction
//                         startY = change.position.y
//                         epoch = volume.externalEpoch
//                     }
//                     val desired = VolumeGestureMath.fractionAfterDrag(
//                         startFraction = startFraction,
//                         deltaYPx = change.position.y - startY,
//                         fullRangePx = travelPx,
//                     )
//                     volume.setFraction(desired)
//                     change.consume()
//                 }
//             } finally {
//                 if (hudStarted) volume.endHud()
//             }
//         }
//     }
// }
//
// private suspend fun AwaitPointerEventScope.awaitDown(acceptConsumed: Boolean): PointerInputChange {
//     while (true) {
//         val event = awaitPointerEvent()
//         val change = event.changes.firstOrNull { change ->
//             if (acceptConsumed) {
//                 change.changedToDownIgnoreConsumed()
//             } else {
//                 change.changedToDown()
//             }
//         }
//         if (change != null) return change
//     }
// }
// // SMCPKG_SUPPORT<<<Cursor049
// SMCPKG_SUPPORT<<<Cursor050
