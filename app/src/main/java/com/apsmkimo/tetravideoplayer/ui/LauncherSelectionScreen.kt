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

package com.apsmkimo.tetravideoplayer.ui

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
// SMCPKG_SUPPORT>>>Cursor038
// import androidx.compose.material.icons.Icons
// SMCPKG_SUPPORT<<<Cursor038
// SMCPKG_SUPPORT>>>Cursor033
// import androidx.compose.material.icons.filled.Star
// import androidx.compose.material.icons.outlined.Star
// SMCPKG_SUPPORT<<<Cursor033
// SMCPKG_SUPPORT>>>Cursor038
// import androidx.compose.material.icons.outlined.Info
// SMCPKG_SUPPORT<<<Cursor038
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import com.apsmkimo.tetravideoplayer.BuildConfig
import com.apsmkimo.tetravideoplayer.R
import com.apsmkimo.tetravideoplayer.TetraVideoPlayerApp
import com.apsmkimo.tetravideoplayer.ads.AdPreferences
// SMCPKG_SUPPORT>>>Cursor033
// import com.apsmkimo.tetravideoplayer.ads.AdSession
// SMCPKG_SUPPORT<<<Cursor033
import com.apsmkimo.tetravideoplayer.ads.BannerAd
import com.apsmkimo.tetravideoplayer.ads.RemoveAdsBilling
import com.apsmkimo.tetravideoplayer.ads.maybeShowInterstitialOnce
import com.apsmkimo.tetravideoplayer.ads.preloadInterstitial
import com.apsmkimo.tetravideoplayer.data.PlayerLayout
import kotlinx.coroutines.launch

private val ScreenBlack = Color(0xFF000000)
private val LabelWhite = Color(0xFFFFFFFF)
// SMCPKG_SUPPORT>>>Cursor033
// private val StarGold = Color(0xFFFFC107)
// SMCPKG_SUPPORT<<<Cursor033

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LauncherSelectionScreen(
    onLayoutSelected: (PlayerLayout) -> Unit,
    onCancel: (() -> Unit)? = null,
    // SMCPKG_SUPPORT>>>Cursor038
    // onAbout: () -> Unit = {},
    onSettings: () -> Unit = {},
    // SMCPKG_SUPPORT<<<Cursor038
    modifier: Modifier = Modifier,
) {
    if (onCancel != null) {
        BackHandler(onBack = onCancel)
    }

    val context = LocalContext.current
    val activity = context as Activity
    val scope = rememberCoroutineScope()
    var adsRemoved by remember { mutableStateOf(AdPreferences.isAdRemoved(context)) }
    var gating by remember { mutableStateOf(false) }
    // SMCPKG_SUPPORT>>>Cursor033
    val billing = (context.applicationContext as TetraVideoPlayerApp).removeAdsBilling
    DisposableEffect(billing) {
        val listener = RemoveAdsBilling.EntitlementListener { owned ->
            adsRemoved = owned
        }
        billing.addListener(listener)
        adsRemoved = AdPreferences.isAdRemoved(context)
        onDispose { billing.removeListener(listener) }
    }
    // SMCPKG_SUPPORT<<<Cursor033

    LaunchedEffect(adsRemoved) {
        if (!adsRemoved) {
            preloadInterstitial(context)
        }
    }

    fun pick(layout: PlayerLayout) {
        if (gating) return
        scope.launch {
            gating = true
            try {
                if (!AdPreferences.isAdRemoved(context)) {
                    maybeShowInterstitialOnce(activity)
                }
                onLayoutSelected(layout)
            } finally {
                gating = false
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ScreenBlack),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
        ) {
            // SMCPKG_SUPPORT>>>Cursor040
            // if (!adsRemoved) {
            //     BannerAd(Modifier.fillMaxWidth())
            // }
            // Selection keeps the bottom banner only.
            // SMCPKG_SUPPORT<<<Cursor040
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(20.dp),
                contentAlignment = Alignment.Center,
            ) {
                BoxWithConstraints {
                    val gap = 18.dp
                    // SMCPKG_SUPPORT>>>Cursor040
                    // val tile = min((maxWidth - gap) / 2, (maxHeight - gap) / 2)
                    //     .coerceIn(128.dp, 220.dp)
                    val rowCount = 3
                    val tile = min(
                        (maxWidth - gap) / 2,
                        (maxHeight - gap * (rowCount - 1)) / rowCount,
                    ).coerceIn(72.dp, 168.dp)
                    val iconNudge = with(LocalDensity.current) { 10.toDp() }
                    // SMCPKG_SUPPORT<<<Cursor040
                    Column(
                        modifier = Modifier
                            // SMCPKG_SUPPORT>>>Cursor040
                            .offset(x = -iconNudge)
                            .alpha(0.88f),
                            // SMCPKG_SUPPORT<<<Cursor040
                        verticalArrangement = Arrangement.spacedBy(gap),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(gap)) {
                            LayoutTile(
                                tile = tile,
                                label = stringResource(R.string.layout_2x2_grid),
                                artwork = R.drawable.layout_2x2_grid,
                                onClick = { pick(PlayerLayout.LANDSCAPE_2X2) },
                            )
                            LayoutTile(
                                tile = tile,
                                label = stringResource(R.string.layout_1x4_stack),
                                artwork = R.drawable.layout_1x4_stack,
                                onClick = { pick(PlayerLayout.VERTICAL_1X4) },
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(gap)) {
                            LayoutTile(
                                tile = tile,
                                label = stringResource(R.string.layout_2x1_horizontal),
                                artwork = R.drawable.layout_2x1_horizontal,
                                onClick = { pick(PlayerLayout.LANDSCAPE_2X1) },
                            )
                            LayoutTile(
                                tile = tile,
                                label = stringResource(R.string.layout_1x2_vertical),
                                artwork = R.drawable.layout_1x2_vertical,
                                onClick = { pick(PlayerLayout.VERTICAL_1X2) },
                            )
                        }
                        // SMCPKG_SUPPORT>>>Cursor040
                        Row(horizontalArrangement = Arrangement.spacedBy(gap)) {
                            LayoutTile(
                                tile = tile,
                                label = stringResource(R.string.layout_1x1),
                                artwork = R.drawable.layout_1x1,
                                onClick = { pick(PlayerLayout.VERTICAL_1X1) },
                            )
                            LayoutTile(
                                tile = tile,
                                label = stringResource(R.string.layout_1x3),
                                artwork = R.drawable.layout_1x3,
                                onClick = { pick(PlayerLayout.VERTICAL_1X3) },
                            )
                        }
                        // SMCPKG_SUPPORT<<<Cursor040
                    }
                }
            }
            if (!adsRemoved) {
                BannerAd(Modifier.fillMaxWidth())
            }
        }

        // SMCPKG_SUPPORT>>>Cursor040
        val iconNudge = with(LocalDensity.current) { 10.toDp() }
        val chromeBg = Color(0x22202A3A)
        // was Color(0x33202A3A)
        // SMCPKG_SUPPORT<<<Cursor040
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                // SMCPKG_SUPPORT>>>Cursor040
                // .padding(end = 4.dp, top = 4.dp),
                .padding(end = 4.dp + iconNudge, top = 4.dp),
                // SMCPKG_SUPPORT<<<Cursor040
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // SMCPKG_SUPPORT>>>Cursor033
            // Box(
            //     modifier = Modifier
            //         .size(40.dp)
            //         .clip(CircleShape)
            //         .background(Color(0x33202A3A))
            //         .combinedClickable(
            //             onClick = {
            //                 AdPreferences.setAdRemoved(context, true)
            //                 adsRemoved = true
            //             },
            //             onLongClick = {
            //                 AdPreferences.setAdRemoved(context, false)
            //                 AdSession.interstitialConsumed = false
            //                 AdSession.loadedInterstitial = null
            //                 adsRemoved = false
            //             },
            //         ),
            //     contentAlignment = Alignment.Center,
            // ) {
            //     Icon(
            //         imageVector = if (adsRemoved) Icons.Filled.Star else Icons.Outlined.Star,
            //         contentDescription = stringResource(
            //             if (adsRemoved) R.string.ads_removed else R.string.remove_ads_test,
            //         ),
            //         tint = if (adsRemoved) StarGold else LabelWhite,
            //     )
            // }
            if (!adsRemoved) {
                IconButton(
                    onClick = {
                        when (billing.launchPurchase(activity)) {
                            RemoveAdsBilling.LaunchResult.STARTED -> Unit
                            RemoveAdsBilling.LaunchResult.UNAVAILABLE -> {
                                Toast.makeText(
                                    context,
                                    R.string.remove_ads_billing_unavailable,
                                    Toast.LENGTH_SHORT,
                                ).show()
                            }
                            RemoveAdsBilling.LaunchResult.PRODUCT_UNAVAILABLE -> {
                                Toast.makeText(
                                    context,
                                    R.string.remove_ads_product_unavailable,
                                    Toast.LENGTH_SHORT,
                                ).show()
                            }
                            RemoveAdsBilling.LaunchResult.FAILED -> {
                                Toast.makeText(
                                    context,
                                    R.string.remove_ads_purchase_failed,
                                    Toast.LENGTH_SHORT,
                                ).show()
                            }
                        }
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        // SMCPKG_SUPPORT>>>Cursor040
                        // .background(Color(0x33202A3A)),
                        .background(chromeBg),
                        // SMCPKG_SUPPORT<<<Cursor040
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_remove_ads),
                        contentDescription = stringResource(R.string.remove_ads),
                        tint = Color.Unspecified,
                    )
                }
            }
            if (BuildConfig.DEBUG) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        // SMCPKG_SUPPORT>>>Cursor040
                        // .background(Color(0x33202A3A))
                        .background(chromeBg)
                        // SMCPKG_SUPPORT<<<Cursor040
                        .combinedClickable(
                            // SMCPKG_SUPPORT>>>Cursor038
                            // onClick = onAbout,
                            onClick = onSettings,
                            // SMCPKG_SUPPORT<<<Cursor038
                            onLongClick = { billing.restoreAdsForDebug() },
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        // SMCPKG_SUPPORT>>>Cursor038
                        // imageVector = Icons.Outlined.Info,
                        imageVector = SettingsGearIcon,
                        // contentDescription = stringResource(R.string.about_title),
                        contentDescription = stringResource(R.string.settings_open),
                        // SMCPKG_SUPPORT<<<Cursor038
                        // SMCPKG_SUPPORT>>>Cursor040
                        // tint = LabelWhite,
                        tint = LabelWhite.copy(alpha = 0.72f),
                        // SMCPKG_SUPPORT<<<Cursor040
                    )
                }
            } else {
                IconButton(
                    // SMCPKG_SUPPORT>>>Cursor038
                    // onClick = onAbout,
                    onClick = onSettings,
                    // SMCPKG_SUPPORT<<<Cursor038
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        // SMCPKG_SUPPORT>>>Cursor040
                        // .background(Color(0x33202A3A)),
                        .background(chromeBg),
                        // SMCPKG_SUPPORT<<<Cursor040
                ) {
                    Icon(
                        // SMCPKG_SUPPORT>>>Cursor038
                        // imageVector = Icons.Outlined.Info,
                        imageVector = SettingsGearIcon,
                        // contentDescription = stringResource(R.string.about_title),
                        contentDescription = stringResource(R.string.settings_open),
                        // SMCPKG_SUPPORT<<<Cursor038
                        // SMCPKG_SUPPORT>>>Cursor040
                        // tint = LabelWhite,
                        tint = LabelWhite.copy(alpha = 0.72f),
                        // SMCPKG_SUPPORT<<<Cursor040
                    )
                }
            }
            // SMCPKG_SUPPORT<<<Cursor033
        }
    }
}

@Composable
private fun LayoutTile(
    tile: androidx.compose.ui.unit.Dp,
    label: String,
    @DrawableRes artwork: Int,
    onClick: () -> Unit,
) {
    // SMCPKG_SUPPORT>>>Cursor036
    // Image(
    //     painter = painterResource(artwork),
    //     contentDescription = label,
    //     contentScale = ContentScale.Fit,
    //     modifier = Modifier
    //         .size(tile)
    //         .clip(RoundedCornerShape(tile * 0.22f))
    //         .clickable(onClick = onClick),
    // )
    Image(
        bitmap = ImageBitmap.imageResource(artwork),
        contentDescription = label,
        contentScale = ContentScale.Fit,
        filterQuality = FilterQuality.High,
        modifier = Modifier
            .size(tile)
            .clip(RoundedCornerShape(tile * 0.22f))
            .clickable(onClick = onClick),
    )
    // SMCPKG_SUPPORT<<<Cursor036
}
