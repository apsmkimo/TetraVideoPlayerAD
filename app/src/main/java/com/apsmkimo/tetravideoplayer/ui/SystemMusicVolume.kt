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

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.ContentObserver
import android.media.AudioManager
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.MotionEvent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LifecycleResumeEffect
import kotlinx.coroutines.delay

/**
 * Activity-wide touch relay. [MainActivity][com.apsmkimo.tetravideoplayer.MainActivity]
 * forwards raw touches without consuming them, so the top icons can restart
 * their fade timer while buttons and gestures still receive the event.
 */
object PlayerChromeSignals {
    var onTouch: ((action: Int, pointerCount: Int) -> Unit)? = null
    // SMCPKG_SUPPORT>>>Cursor050
    // var onVolumeKey: (() -> Unit)? = null
    // SMCPKG_SUPPORT<<<Cursor050
}

/** Fingers still down after this MotionEvent. */
internal fun fingersDownAfterEvent(action: Int, pointerCount: Int): Int {
    val count = pointerCount.coerceAtLeast(0)
    return when (action) {
        MotionEvent.ACTION_UP,
        MotionEvent.ACTION_CANCEL,
        MotionEvent.ACTION_POINTER_UP,
        -> (count - 1).coerceAtLeast(0)
        else -> count
    }
}

// SMCPKG_SUPPORT>>>Cursor050
// Per-pane ExoPlayer volume replaced STREAM_MUSIC. Hardware keys are left to the system.
// /**
//  * STREAM_MUSIC level for the whole phone. ExoPlayer's own [androidx.media3.exoplayer.ExoPlayer.volume]
//  * stays the per-pane mute control.
//  */
// class SystemMusicVolumeController(context: Context) {
//     private val appContext = context.applicationContext
//     private val audioManager =
//         appContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
//     private val fractionState = mutableFloatStateOf(readFraction())
//     private val externalEpochState = mutableIntStateOf(0)
//     private val hudVisibleState = mutableStateOf(false)
//     private val flashVisibleState = mutableStateOf(false)
//     private var hudHolds = 0
//
//     /**
//      * Index we just wrote. The matching system broadcast is our echo and must
//      * not be treated as a hardware-button change.
//      */
//     private var echoIndex: Int? = null
//
//     val fraction: Float get() = fractionState.floatValue
//     val externalEpoch: Int get() = externalEpochState.intValue
//     val hudVisible: Boolean get() = hudVisibleState.value
//     var flashVisible: Boolean
//         get() = flashVisibleState.value
//         set(value) { flashVisibleState.value = value }
//
//     fun fullRangePx(): Float =
//         VolumeGestureMath.fullRangePixels(appContext.resources.displayMetrics.ydpi)
//
//     fun setFraction(target: Float): Float {
//         val max = maxIndex()
//         val index = VolumeGestureMath.streamIndexForFraction(target, max)
//         if (index != currentIndex()) {
//             echoIndex = index
//             try {
//                 audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0)
//             } catch (_: SecurityException) {
//                 echoIndex = null
//             }
//         }
//         val applied = readFraction()
//         fractionState.floatValue = applied
//         return applied
//     }
//
//     fun beginHud() {
//         hudHolds++
//         hudVisibleState.value = true
//     }
//
//     fun endHud() {
//         hudHolds = (hudHolds - 1).coerceAtLeast(0)
//         hudVisibleState.value = hudHolds > 0
//     }
//
//     fun onExternalChange(forcePulse: Boolean = false) {
//         val index = currentIndex()
//         val applied = index.toFloat() / maxIndex().toFloat()
//         if (echoIndex != null && echoIndex == index) {
//             echoIndex = null
//             fractionState.floatValue = applied
//             if (forcePulse) {
//                 externalEpochState.intValue++
//             }
//             return
//         }
//         echoIndex = null
//         val changed = applied != fractionState.floatValue
//         fractionState.floatValue = applied
//         if (changed || forcePulse) {
//             externalEpochState.intValue++
//         }
//     }
//
//     fun refreshQuietly() {
//         echoIndex = null
//         fractionState.floatValue = readFraction()
//     }
//
//     private fun maxIndex(): Int =
//         audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
//
//     private fun currentIndex(): Int =
//         audioManager.getStreamVolume(AudioManager.STREAM_MUSIC).coerceIn(0, maxIndex())
//
//     private fun readFraction(): Float = currentIndex().toFloat() / maxIndex().toFloat()
// }
//
// private const val VOLUME_CHANGED_ACTION = "android.media.VOLUME_CHANGED_ACTION"
// private const val EXTRA_VOLUME_STREAM_TYPE = "android.media.EXTRA_VOLUME_STREAM_TYPE"
//
// @Composable
// fun rememberSystemMusicVolume(): SystemMusicVolumeController {
//     val context = androidx.compose.ui.platform.LocalContext.current
//     val controller = remember { SystemMusicVolumeController(context) }
//     DisposableEffect(controller) {
//         val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
//             override fun onChange(selfChange: Boolean) {
//                 controller.onExternalChange()
//             }
//         }
//         context.contentResolver.registerContentObserver(
//             Settings.System.CONTENT_URI,
//             true,
//             observer,
//         )
//         val receiver = object : BroadcastReceiver() {
//             override fun onReceive(receiverContext: Context?, intent: Intent?) {
//                 if (intent?.action != VOLUME_CHANGED_ACTION) return
//                 val stream = intent.getIntExtra(
//                     EXTRA_VOLUME_STREAM_TYPE,
//                     AudioManager.STREAM_MUSIC,
//                 )
//                 if (stream != AudioManager.STREAM_MUSIC) return
//                 controller.onExternalChange()
//             }
//         }
//         // System broadcast. RECEIVER_NOT_EXPORTED would drop it on API 33+.
//         ContextCompat.registerReceiver(
//             context,
//             receiver,
//             IntentFilter(VOLUME_CHANGED_ACTION),
//             ContextCompat.RECEIVER_EXPORTED,
//         )
//         PlayerChromeSignals.onVolumeKey = { controller.onExternalChange(forcePulse = true) }
//         onDispose {
//             context.contentResolver.unregisterContentObserver(observer)
//             context.unregisterReceiver(receiver)
//             PlayerChromeSignals.onVolumeKey = null
//         }
//     }
//     LifecycleResumeEffect(controller) {
//         controller.refreshQuietly()
//         onPauseOrDispose { }
//     }
//     LaunchedEffect(controller.externalEpoch) {
//         if (controller.externalEpoch == 0) return@LaunchedEffect
//         controller.flashVisible = true
//         delay(900)
//         controller.flashVisible = false
//     }
//     return controller
// }
// // SMCPKG_SUPPORT<<<Cursor049
// SMCPKG_SUPPORT<<<Cursor050
