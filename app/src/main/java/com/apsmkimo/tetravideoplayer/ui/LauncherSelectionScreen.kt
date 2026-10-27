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

package com.apsmkimo.tetravideoplayer.ui

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
// SMCPKG_SUPPORT>>>Cursor046
// import androidx.annotation.DrawableRes
// import androidx.compose.foundation.Image
// import androidx.compose.foundation.layout.offset
// SMCPKG_SUPPORT<<<Cursor046
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// SMCPKG_SUPPORT>>>Cursor046
// import androidx.compose.ui.draw.alpha
// import androidx.compose.ui.graphics.FilterQuality
// import androidx.compose.ui.graphics.ImageBitmap
// import androidx.compose.ui.layout.ContentScale
// import androidx.compose.ui.platform.LocalDensity
// import androidx.compose.ui.res.imageResource
// import androidx.compose.ui.unit.min
// SMCPKG_SUPPORT<<<Cursor046
import com.apsmkimo.tetravideoplayer.ui.theme.QuadVideoPlayerTheme
import kotlin.math.max
import kotlin.math.min
import com.apsmkimo.tetravideoplayer.BuildConfig
import com.apsmkimo.tetravideoplayer.R
import com.apsmkimo.tetravideoplayer.TetraVideoPlayerApp
import com.apsmkimo.tetravideoplayer.ads.AdPreferences
import com.apsmkimo.tetravideoplayer.ads.AdsConsentManager
// SMCPKG_SUPPORT>>>Cursor033
// import com.apsmkimo.tetravideoplayer.ads.AdSession
// SMCPKG_SUPPORT<<<Cursor033
import com.apsmkimo.tetravideoplayer.ads.BannerAd
import com.apsmkimo.tetravideoplayer.ads.RemoveAdsBilling
import com.apsmkimo.tetravideoplayer.ads.maybeShowInterstitialOnce
import com.apsmkimo.tetravideoplayer.ads.preloadInterstitial
import com.apsmkimo.tetravideoplayer.data.PlayerLayout
import kotlinx.coroutines.launch

// SMCPKG_SUPPORT>>>Cursor046
// private val ScreenBlack = Color(0xFF000000)
private val MenuBackground = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF121A30),
        Color(0xFF070B16),
        Color(0xFF05070E),
    ),
)
private val MenuSubtitle = Color(0xFF8E97A8)
// SMCPKG_SUPPORT<<<Cursor046
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
    // SMCPKG_SUPPORT>>>Cursor043
    val consent = remember(context) { AdsConsentManager.getInstance(context) }
    val canRequestAds by consent.canRequestAdsFlow.collectAsStateWithLifecycle()
    val adsSdkReady by consent.adsSdkReady.collectAsStateWithLifecycle()
    // SMCPKG_SUPPORT<<<Cursor043
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

    LaunchedEffect(adsRemoved, canRequestAds, adsSdkReady) {
        // SMCPKG_SUPPORT>>>Cursor043
        // if (!adsRemoved) {
        //     preloadInterstitial(context)
        // }
        if (!adsRemoved && canRequestAds) {
            consent.ensureMobileAdsInitialized(context)
            if (adsSdkReady) {
                preloadInterstitial(context)
            }
        }
        // SMCPKG_SUPPORT<<<Cursor043
    }

    fun pick(layout: PlayerLayout) {
        if (gating) return
        scope.launch {
            gating = true
            try {
                // SMCPKG_SUPPORT>>>Cursor043
                // if (!AdPreferences.isAdRemoved(context)) {
                //     maybeShowInterstitialOnce(activity)
                // }
                if (!AdPreferences.isAdRemoved(context) && canRequestAds && adsSdkReady) {
                    maybeShowInterstitialOnce(activity)
                }
                // SMCPKG_SUPPORT<<<Cursor043
                onLayoutSelected(layout)
            } finally {
                gating = false
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            // SMCPKG_SUPPORT>>>Cursor046
            // .background(ScreenBlack),
            .background(MenuBackground),
            // SMCPKG_SUPPORT<<<Cursor046
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                // SMCPKG_SUPPORT>>>Cursor045
                // .statusBarsPadding()
                // .navigationBarsPadding(),
                .windowInsetsPadding(
                    WindowInsets.systemBars.union(WindowInsets.displayCutout),
                ),
                // SMCPKG_SUPPORT<<<Cursor045
        ) {
            // SMCPKG_SUPPORT>>>Cursor040
            // if (!adsRemoved) {
            //     BannerAd(Modifier.fillMaxWidth())
            // }
            // Selection keeps the bottom banner only.
            // SMCPKG_SUPPORT<<<Cursor040
            // SMCPKG_SUPPORT>>>Cursor046
            // Box(
            //     modifier = Modifier
            //         .weight(1f)
            //         .fillMaxWidth()
            //         .padding(20.dp),
            //     contentAlignment = Alignment.Center,
            // ) {
            //     BoxWithConstraints {
            //         val gap = 18.dp
            //         // SMCPKG_SUPPORT>>>Cursor040
            //         // val tile = min((maxWidth - gap) / 2, (maxHeight - gap) / 2)
            //         //     .coerceIn(128.dp, 220.dp)
            //         val rowCount = 3
            //         val tile = min(
            //             (maxWidth - gap) / 2,
            //             (maxHeight - gap * (rowCount - 1)) / rowCount,
            //         ).coerceIn(72.dp, 168.dp)
            //         val iconNudge = with(LocalDensity.current) { 10.toDp() }
            //         // SMCPKG_SUPPORT<<<Cursor040
            //         Column(
            //             modifier = Modifier
            //                 // SMCPKG_SUPPORT>>>Cursor040
            //                 .offset(x = -iconNudge)
            //                 .alpha(0.88f),
            //                 // SMCPKG_SUPPORT<<<Cursor040
            //             verticalArrangement = Arrangement.spacedBy(gap),
            //             horizontalAlignment = Alignment.CenterHorizontally,
            //         ) {
            //             Row(horizontalArrangement = Arrangement.spacedBy(gap)) {
            //                 LayoutTile(
            //                     tile = tile,
            //                     label = stringResource(R.string.layout_2x2_grid),
            //                     artwork = R.drawable.layout_2x2_grid,
            //                     onClick = { pick(PlayerLayout.LANDSCAPE_2X2) },
            //                 )
            //                 LayoutTile(
            //                     tile = tile,
            //                     label = stringResource(R.string.layout_1x4_stack),
            //                     artwork = R.drawable.layout_1x4_stack,
            //                     onClick = { pick(PlayerLayout.VERTICAL_1X4) },
            //                 )
            //             }
            //             Row(horizontalArrangement = Arrangement.spacedBy(gap)) {
            //                 LayoutTile(
            //                     tile = tile,
            //                     label = stringResource(R.string.layout_2x1_horizontal),
            //                     artwork = R.drawable.layout_2x1_horizontal,
            //                     onClick = { pick(PlayerLayout.LANDSCAPE_2X1) },
            //                 )
            //                 LayoutTile(
            //                     tile = tile,
            //                     label = stringResource(R.string.layout_1x2_vertical),
            //                     artwork = R.drawable.layout_1x2_vertical,
            //                     onClick = { pick(PlayerLayout.VERTICAL_1X2) },
            //                 )
            //             }
            //             // SMCPKG_SUPPORT>>>Cursor040
            //             Row(horizontalArrangement = Arrangement.spacedBy(gap)) {
            //                 LayoutTile(
            //                     tile = tile,
            //                     label = stringResource(R.string.layout_1x1),
            //                     artwork = R.drawable.layout_1x1,
            //                     onClick = { pick(PlayerLayout.VERTICAL_1X1) },
            //                 )
            //                 LayoutTile(
            //                     tile = tile,
            //                     label = stringResource(R.string.layout_1x3),
            //                     artwork = R.drawable.layout_1x3,
            //                     onClick = { pick(PlayerLayout.VERTICAL_1X3) },
            //                 )
            //             }
            //             // SMCPKG_SUPPORT<<<Cursor040
            //         }
            //     }
            // }
            LayoutSelectionGrid(
                onLayoutSelected = ::pick,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            )
            // SMCPKG_SUPPORT<<<Cursor046
            // SMCPKG_SUPPORT>>>Cursor043
            // if (!adsRemoved) {
            //     BannerAd(Modifier.fillMaxWidth())
            // }
            if (!adsRemoved && canRequestAds && adsSdkReady) {
                BannerAd(Modifier.fillMaxWidth())
            }
            // SMCPKG_SUPPORT<<<Cursor043
        }

        // SMCPKG_SUPPORT>>>Cursor040
        // val iconNudge = with(LocalDensity.current) { 10.toDp() }
        val chromeBg = Color(0x22202A3A)
        // was Color(0x33202A3A)
        // SMCPKG_SUPPORT<<<Cursor040
        // SMCPKG_SUPPORT>>>Cursor046
        // .padding(end = 4.dp + iconNudge, top = 4.dp) kept the bitmap tiles shifted.
        // Cards are centered, so the corner controls sit on the normal inset.
        // SMCPKG_SUPPORT<<<Cursor046
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                // SMCPKG_SUPPORT>>>Cursor045
                // .statusBarsPadding()
                .windowInsetsPadding(
                    WindowInsets.statusBars
                        .union(WindowInsets.navigationBars)
                        .union(WindowInsets.displayCutout)
                        .only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal),
                )
                // SMCPKG_SUPPORT<<<Cursor045
                // SMCPKG_SUPPORT>>>Cursor040
                // .padding(end = 4.dp, top = 4.dp),
                // SMCPKG_SUPPORT>>>Cursor046
                // .padding(end = 4.dp + iconNudge, top = 4.dp),
                .padding(end = 4.dp, top = 4.dp),
                // SMCPKG_SUPPORT<<<Cursor046
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

// SMCPKG_SUPPORT>>>Cursor046
// @Composable
// private fun LayoutTile(
//     tile: androidx.compose.ui.unit.Dp,
//     label: String,
//     @DrawableRes artwork: Int,
//     onClick: () -> Unit,
// ) {
//     // SMCPKG_SUPPORT>>>Cursor036
//     // Image(
//     //     painter = painterResource(artwork),
//     //     contentDescription = label,
//     //     contentScale = ContentScale.Fit,
//     //     modifier = Modifier
//     //         .size(tile)
//     //         .clip(RoundedCornerShape(tile * 0.22f))
//     //         .clickable(onClick = onClick),
//     // )
//     Image(
//         bitmap = ImageBitmap.imageResource(artwork),
//         contentDescription = label,
//         contentScale = ContentScale.Fit,
//         filterQuality = FilterQuality.High,
//         modifier = Modifier
//             .size(tile)
//             .clip(RoundedCornerShape(tile * 0.22f))
//             .clickable(onClick = onClick),
//     )
//     // SMCPKG_SUPPORT<<<Cursor036
// }

private data class LayoutChoice(
    val layout: PlayerLayout,
    @param:StringRes val titleRes: Int,
    @param:StringRes val subtitleRes: Int,
    val accent: Color,
    val iconTop: Color,
    val iconBottom: Color,
)

private val LayoutChoices = listOf(
    LayoutChoice(
        layout = PlayerLayout.LANDSCAPE_2X2,
        titleRes = R.string.layout_2x2_grid,
        subtitleRes = R.string.layout_2x2_subtitle,
        accent = Color(0xFF3D7CFF),
        iconTop = Color(0xFF7EC4FF),
        iconBottom = Color(0xFF5B46F0),
    ),
    LayoutChoice(
        layout = PlayerLayout.VERTICAL_1X4,
        titleRes = R.string.layout_1x4_stack,
        subtitleRes = R.string.layout_1x4_subtitle,
        accent = Color(0xFFFF9A1F),
        iconTop = Color(0xFFFFE08A),
        iconBottom = Color(0xFFFF6A00),
    ),
    LayoutChoice(
        layout = PlayerLayout.LANDSCAPE_2X1,
        titleRes = R.string.layout_2x1_horizontal,
        subtitleRes = R.string.layout_2x1_subtitle,
        accent = Color(0xFFFF4D9A),
        iconTop = Color(0xFFFF9CCE),
        iconBottom = Color(0xFFE03088),
    ),
    LayoutChoice(
        layout = PlayerLayout.VERTICAL_1X2,
        titleRes = R.string.layout_1x2_vertical,
        subtitleRes = R.string.layout_1x2_subtitle,
        accent = Color(0xFF3CB4FF),
        iconTop = Color(0xFF8AE4FF),
        iconBottom = Color(0xFF2B7CFF),
    ),
    LayoutChoice(
        layout = PlayerLayout.VERTICAL_1X1,
        titleRes = R.string.layout_1x1,
        subtitleRes = R.string.layout_1x1_subtitle,
        accent = Color(0xFF8B6CFF),
        iconTop = Color(0xFF8EBEFF),
        iconBottom = Color(0xFFC45CFF),
    ),
    LayoutChoice(
        layout = PlayerLayout.VERTICAL_1X3,
        titleRes = R.string.layout_1x3,
        subtitleRes = R.string.layout_1x3_subtitle,
        accent = Color(0xFF3DDC84),
        iconTop = Color(0xFFC6F56A),
        iconBottom = Color(0xFF1FB85A),
    ),
)

@Composable
private fun LayoutSelectionGrid(
    onLayoutSelected: (PlayerLayout) -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier) {
        val gap = 12.dp
        // Corner controls (remove ads / settings) overlay the top of this grid.
        val buttonClearance = 48.dp
        val columns = 2
        val rows = LayoutChoices.chunked(columns)
        val maxCardWidth = 220.dp
        val cardWidth = ((maxWidth - gap) / columns).coerceAtMost(maxCardWidth)
        val gridWidth = cardWidth * columns + gap
        val bottomPad = 12.dp
        val rowCount = rows.size.coerceAtLeast(1)
        val gaps = gap * (rowCount - 1)
        // Fill the phone height the way the menu mockup does, but cap card
        // height on large screens so a tablet does not grow huge slabs.
        val usable = (maxHeight - buttonClearance - bottomPad - gaps).coerceAtLeast(0.dp)
        val fillHeight = usable / rowCount
        val heightCap = maxOf(cardWidth * 1.45f, 120.dp)
        val cardHeight = fillHeight.coerceIn(120.dp, heightCap)
        val gridHeight = cardHeight * rows.size + gap * (rows.size - 1)
        val needsScroll = gridHeight + buttonClearance > maxHeight + 0.5.dp
        val leftover = (maxHeight - gridHeight).coerceAtLeast(0.dp)
        // Center the grid when the corner controls already sit in the leftover space.
        val topInset = if (needsScroll) buttonClearance else maxOf(buttonClearance, leftover / 2)
        val scroll = rememberScrollState()
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = topInset)
                .then(if (needsScroll) Modifier.verticalScroll(scroll) else Modifier),
            contentAlignment = Alignment.TopCenter,
        ) {
            Column(
                modifier = Modifier.width(gridWidth),
                verticalArrangement = Arrangement.spacedBy(gap),
            ) {
                rows.forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(gap)) {
                        row.forEach { choice ->
                            LayoutOptionCard(
                                choice = choice,
                                onClick = { onLayoutSelected(choice.layout) },
                                modifier = Modifier
                                    .width(cardWidth)
                                    .height(cardHeight),
                            )
                        }
                        if (row.size < columns) {
                            Spacer(Modifier.width(cardWidth))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LayoutOptionCard(
    choice: LayoutChoice,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(22.dp)
    val title = stringResource(choice.titleRes)
    val subtitle = stringResource(choice.subtitleRes)
    BoxWithConstraints(
        modifier = modifier
            .shadow(
                elevation = 14.dp,
                shape = shape,
                clip = false,
                ambientColor = choice.accent.copy(alpha = 0.28f),
                spotColor = choice.accent.copy(alpha = 0.62f),
            )
            .clip(shape)
            .drawBehind {
                drawRect(Color(0xFF0C1428))
                drawRect(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            choice.accent.copy(alpha = 0.42f),
                            Color(0xFF10182C).copy(alpha = 0.55f),
                            choice.accent.copy(alpha = 0.16f),
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(size.width, size.height),
                    ),
                )
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            choice.accent.copy(alpha = 0.50f),
                            Color.Transparent,
                        ),
                        center = Offset(size.width * 0.92f, size.height * 0.08f),
                        radius = size.maxDimension * 0.72f,
                    ),
                )
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.10f),
                            Color.Transparent,
                        ),
                        center = Offset(size.width * 0.12f, 0f),
                        radius = size.maxDimension * 0.7f,
                    ),
                )
            }
            .border(
                width = 1.2.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        choice.accent.copy(alpha = 0.95f),
                        choice.accent.copy(alpha = 0.38f),
                        choice.accent.copy(alpha = 0.90f),
                    ),
                ),
                shape = shape,
            )
            .clickable(role = Role.Button, onClick = onClick),
    ) {
        val titleSize = (maxWidth.value * 0.092f).coerceIn(13f, 17f).sp
        val subtitleSize = (maxWidth.value * 0.066f).coerceIn(10f, 12.5f).sp
        val iconSize = minOf(maxWidth * 0.42f, maxHeight * 0.34f)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 14.dp),
        ) {
            LayoutGlyph(
                columns = choice.layout.columns,
                rows = choice.layout.rows,
                colorTop = choice.iconTop,
                colorBottom = choice.iconBottom,
                modifier = Modifier.size(iconSize),
            )
            Spacer(Modifier.weight(1f))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = titleSize,
                        lineHeight = titleSize * 1.15f,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = subtitle,
                        color = MenuSubtitle,
                        fontWeight = FontWeight.Normal,
                        fontSize = subtitleSize,
                        lineHeight = subtitleSize * 1.2f,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                MenuChevron(
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .size(width = 12.dp, height = 16.dp),
                )
            }
        }
    }
}

@Composable
private fun LayoutGlyph(
    columns: Int,
    rows: Int,
    colorTop: Color,
    colorBottom: Color,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier) {
        val gap = size.minDimension * 0.10f
        val cellW = (size.width - gap * (columns - 1)) / columns
        val cellH = (size.height - gap * (rows - 1)) / rows
        val tileBrush = Brush.linearGradient(
            colors = listOf(colorTop, colorBottom),
            start = Offset(0f, 0f),
            end = Offset(size.width * 0.15f, size.height),
        )
        for (row in 0 until rows) {
            for (column in 0 until columns) {
                val left = column * (cellW + gap)
                val top = row * (cellH + gap)
                val shortSide = min(cellW, cellH)
                val longSide = max(cellW, cellH)
                val radius = if (longSide > shortSide * 1.35f) shortSide * 0.48f else shortSide * 0.26f
                drawRoundRect(
                    brush = tileBrush,
                    topLeft = Offset(left, top),
                    size = Size(cellW, cellH),
                    cornerRadius = CornerRadius(radius, radius),
                )
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.38f),
                            Color.Transparent,
                        ),
                        startY = top,
                        endY = top + cellH * 0.55f,
                    ),
                    topLeft = Offset(left, top),
                    size = Size(cellW, cellH),
                    cornerRadius = CornerRadius(radius, radius),
                )
            }
        }
    }
}

@Composable
private fun MenuChevron(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val path = Path().apply {
            moveTo(size.width * 0.22f, size.height * 0.12f)
            lineTo(size.width * 0.78f, size.height * 0.50f)
            lineTo(size.width * 0.22f, size.height * 0.88f)
        }
        drawPath(
            path = path,
            color = Color.White.copy(alpha = 0.88f),
            style = Stroke(
                width = 1.6.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round,
            ),
        )
    }
}

@Preview(widthDp = 390, heightDp = 844, showBackground = true)
@Composable
internal fun LayoutSelectionMenuPreview() {
    QuadVideoPlayerTheme {
        val chromeBg = Color(0x22202A3A)
        Box(Modifier.fillMaxSize().background(MenuBackground)) {
            LayoutSelectionGrid(
                onLayoutSelected = {},
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
            )
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 4.dp, top = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(
                    onClick = {},
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(chromeBg),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_remove_ads),
                        contentDescription = stringResource(R.string.remove_ads),
                        tint = Color.Unspecified,
                    )
                }
                IconButton(
                    onClick = {},
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(chromeBg),
                ) {
                    Icon(
                        imageVector = SettingsGearIcon,
                        contentDescription = stringResource(R.string.settings_open),
                        tint = LabelWhite.copy(alpha = 0.72f),
                    )
                }
            }
        }
    }
}
// SMCPKG_SUPPORT<<<Cursor046
