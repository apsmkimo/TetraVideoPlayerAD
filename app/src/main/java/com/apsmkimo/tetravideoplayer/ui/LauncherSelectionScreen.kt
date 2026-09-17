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
import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
// SMCPKG_SUPPORT>>>Cursor032
// import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Canvas
// SMCPKG_SUPPORT<<<Cursor032
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
// SMCPKG_SUPPORT>>>Cursor032
// import androidx.compose.foundation.combinedClickable
// SMCPKG_SUPPORT<<<Cursor032
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
// SMCPKG_SUPPORT>>>Cursor032
// import androidx.compose.material.icons.filled.Star
// SMCPKG_SUPPORT<<<Cursor032
import androidx.compose.material.icons.outlined.Info
// SMCPKG_SUPPORT>>>Cursor032
// import androidx.compose.material.icons.outlined.Star
// SMCPKG_SUPPORT<<<Cursor032
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
// SMCPKG_SUPPORT>>>Cursor032
import androidx.compose.material3.Text
// SMCPKG_SUPPORT<<<Cursor032
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
// SMCPKG_SUPPORT>>>Cursor032
import androidx.compose.ui.geometry.Offset
// SMCPKG_SUPPORT<<<Cursor032
import androidx.compose.ui.graphics.Color
// SMCPKG_SUPPORT>>>Cursor032
import androidx.compose.ui.graphics.StrokeCap
// SMCPKG_SUPPORT<<<Cursor032
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
// SMCPKG_SUPPORT>>>Cursor032
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
// SMCPKG_SUPPORT<<<Cursor032
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
// SMCPKG_SUPPORT>>>Cursor032
import androidx.compose.ui.unit.sp
// SMCPKG_SUPPORT<<<Cursor032
import com.apsmkimo.tetravideoplayer.R
import com.apsmkimo.tetravideoplayer.ads.AdPreferences
// SMCPKG_SUPPORT>>>Cursor032
// import com.apsmkimo.tetravideoplayer.ads.AdSession
// SMCPKG_SUPPORT<<<Cursor032
import com.apsmkimo.tetravideoplayer.ads.BannerAd
import com.apsmkimo.tetravideoplayer.ads.maybeShowInterstitialOnce
import com.apsmkimo.tetravideoplayer.ads.preloadInterstitial
import com.apsmkimo.tetravideoplayer.data.PlayerLayout
import kotlinx.coroutines.launch

private val ScreenBlack = Color(0xFF000000)
private val LabelWhite = Color(0xFFFFFFFF)
// SMCPKG_SUPPORT>>>Cursor032
// private val StarGold = Color(0xFFFFC107)
private val AdSlash = Color(0xFFFF5252)
// SMCPKG_SUPPORT<<<Cursor032

// SMCPKG_SUPPORT>>>Cursor032
// @OptIn(ExperimentalFoundationApi::class)
// SMCPKG_SUPPORT<<<Cursor032
@Composable
fun LauncherSelectionScreen(
    onLayoutSelected: (PlayerLayout) -> Unit,
    onCancel: (() -> Unit)? = null,
    onAbout: () -> Unit = {},
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
            if (!adsRemoved) {
                BannerAd(Modifier.fillMaxWidth())
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(20.dp),
                contentAlignment = Alignment.Center,
            ) {
                BoxWithConstraints {
                    val gap = 18.dp
                    val tile = min((maxWidth - gap) / 2, (maxHeight - gap) / 2)
                        .coerceIn(128.dp, 220.dp)
                    Column(
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
                    }
                }
            }
            if (!adsRemoved) {
                BannerAd(Modifier.fillMaxWidth())
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(end = 4.dp, top = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // SMCPKG_SUPPORT>>>Cursor032
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
                val removeAdsLabel = stringResource(R.string.remove_ads_test)
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0x33202A3A))
                        .semantics { contentDescription = removeAdsLabel }
                        .clickable {
                            AdPreferences.setAdRemoved(context, true)
                            adsRemoved = true
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    AdSlashMark()
                }
            }
            // SMCPKG_SUPPORT<<<Cursor032
            IconButton(
                onClick = onAbout,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0x33202A3A)),
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = stringResource(R.string.about_title),
                    tint = LabelWhite,
                )
            }
        }
    }
}

// SMCPKG_SUPPORT>>>Cursor032
@Composable
private fun AdSlashMark() {
    Box(
        modifier = Modifier.size(28.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "AD",
            color = LabelWhite,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            letterSpacing = 0.4.sp,
        )
        Canvas(modifier = Modifier.size(26.dp)) {
            val inset = 2.dp.toPx()
            drawLine(
                color = AdSlash,
                start = Offset(inset, size.height - inset),
                end = Offset(size.width - inset, inset),
                strokeWidth = 2.5.dp.toPx(),
                cap = StrokeCap.Round,
            )
        }
    }
}
// SMCPKG_SUPPORT<<<Cursor032

@Composable
private fun LayoutTile(
    tile: androidx.compose.ui.unit.Dp,
    label: String,
    @DrawableRes artwork: Int,
    onClick: () -> Unit,
) {
    Image(
        painter = painterResource(artwork),
        contentDescription = label,
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .size(tile)
            .clip(RoundedCornerShape(tile * 0.22f))
            .clickable(onClick = onClick),
    )
}
