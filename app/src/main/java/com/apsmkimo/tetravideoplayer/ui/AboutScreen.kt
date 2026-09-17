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

// SMCPKG_SUPPORT>>>Cursor021
// SMCPKG_SUPPORT>>>Cursor030
// package com.apsmkimo.tetraview.ui
package com.apsmkimo.tetravideoplayer.ui
// SMCPKG_SUPPORT<<<Cursor030
// SMCPKG_SUPPORT<<<Cursor021

// SMCPKG_SUPPORT>>>Cursor031
import android.content.ActivityNotFoundException
// SMCPKG_SUPPORT<<<Cursor031
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
// SMCPKG_SUPPORT>>>Cursor031
import android.content.ActivityNotFoundException
// SMCPKG_SUPPORT<<<Cursor031
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
// SMCPKG_SUPPORT>>>Cursor031
// import androidx.compose.material3.TextButton
// SMCPKG_SUPPORT<<<Cursor031
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
// SMCPKG_SUPPORT>>>Cursor031
// import androidx.compose.ui.text.font.FontFamily
// SMCPKG_SUPPORT<<<Cursor031
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
// SMCPKG_SUPPORT>>>Cursor031
// import androidx.compose.ui.unit.sp
// SMCPKG_SUPPORT<<<Cursor031
// SMCPKG_SUPPORT>>>Cursor030
// import com.apsmkimo.tetraview.R
import com.apsmkimo.tetravideoplayer.R
// SMCPKG_SUPPORT<<<Cursor030
private val AboutBackground = Color(0xFF000000)
private val AboutText = Color(0xFFE8EEF6)
private val AboutMuted = Color(0xFFB4C0D0)
// SMCPKG_SUPPORT>>>Cursor030
// private const val SOURCE_URL = "https://github.com/apsmkimo/FreeQuadPlayer"
// private const val LICENSE_URL = "https://github.com/apsmkimo/FreeQuadPlayer/blob/main/LICENSE"
// SMCPKG_SUPPORT>>>Cursor031
// private const val SOURCE_URL = "https://github.com/apsmkimo/TetraVideoPlayerAD"
// private const val LICENSE_URL = "https://github.com/apsmkimo/TetraVideoPlayerAD/blob/main/LICENSE"
private const val PRIVACY_POLICY_URL =
    "https://sites.google.com/view/tetravideoplayerprivacypolicy"
private const val PLAY_STORE_PACKAGE = "com.apsmkimo.tetravideoplayer"
private const val PLAY_STORE_MARKET_URI = "market://details?id=$PLAY_STORE_PACKAGE"
private const val PLAY_STORE_WEB_URI =
    "https://play.google.com/store/apps/details?id=$PLAY_STORE_PACKAGE"
// SMCPKG_SUPPORT<<<Cursor031
// SMCPKG_SUPPORT<<<Cursor030
// SMCPKG_SUPPORT>>>Cursor031
// private const val LICENSE_ASSET = "LICENSE"
// SMCPKG_SUPPORT<<<Cursor031

@Composable
fun AboutScreen(
    onClose: () -> Unit,
    // SMCPKG_SUPPORT>>>Cursor031
    // onViewLicense: () -> Unit,
    // SMCPKG_SUPPORT<<<Cursor031
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AboutBackground)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        AboutTopBar(
            title = stringResource(R.string.about_title),
            onClose = onClose,
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = AboutText,
            )
            Text(text = versionLabel, color = AboutMuted)
            // SMCPKG_SUPPORT>>>Cursor031
            // Text(
            //     text = stringResource(R.string.about_gpl_statement),
            //     color = AboutText,
            // )
            // Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            //     Button(onClick = onViewLicense) {
            //         Text(stringResource(R.string.about_view_license))
            //     }
            //     OutlinedButton(onClick = { openUrl(context, LICENSE_URL) }) {
            //         Text(stringResource(R.string.about_license_online))
            //     }
            // }
            // Text(
            //     text = stringResource(R.string.about_source_label),
            //     fontWeight = FontWeight.SemiBold,
            //     color = AboutText,
            // )
            // TextButton(onClick = { openUrl(context, SOURCE_URL) }) {
            //     Text(SOURCE_URL)
            // }
            // Text(
            //     text = stringResource(R.string.about_components_title),
            //     fontWeight = FontWeight.SemiBold,
            //     color = AboutText,
            // )
            // Text(
            //     text = stringResource(R.string.about_components_body),
            //     color = AboutMuted,
            // )
            // Text(
            //     text = stringResource(R.string.about_permissions),
            //     color = AboutMuted,
            // )
            Button(
                onClick = { openUrl(context, PRIVACY_POLICY_URL) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.about_privacy_policy))
            }
            OutlinedButton(
                onClick = { openPlayStoreListing(context) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.about_rate_us))
            }
            // SMCPKG_SUPPORT<<<Cursor031
        }
    }
}

// SMCPKG_SUPPORT>>>Cursor031
// @Composable
// fun LicenseTextScreen(
//     onClose: () -> Unit,
//     modifier: Modifier = Modifier,
// ) {
//     BackHandler(onBack = onClose)
//     val context = LocalContext.current
//     val licenseText = remember {
//         runCatching {
//             context.assets.open(LICENSE_ASSET).bufferedReader().use { it.readText() }
//         }.getOrElse { context.getString(R.string.about_license_missing) }
//     }
//     Column(
//         modifier = modifier
//             .fillMaxSize()
//             .background(AboutBackground)
//             .statusBarsPadding()
//             .navigationBarsPadding(),
//     ) {
//         AboutTopBar(
//             title = stringResource(R.string.about_license_title),
//             onClose = onClose,
//         )
//         Text(
//             text = licenseText,
//             color = AboutText,
//             fontFamily = FontFamily.Monospace,
//             fontSize = 12.sp,
//             modifier = Modifier
//                 .fillMaxSize()
//                 .verticalScroll(rememberScrollState())
//                 .padding(horizontal = 16.dp, vertical = 8.dp),
//         )
//     }
// }
// SMCPKG_SUPPORT<<<Cursor031

@Composable
private fun AboutTopBar(
    title: String,
    onClose: () -> Unit,
) {
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
                tint = AboutText,
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = AboutText,
        )
    }
}

private fun openUrl(context: android.content.Context, url: String) {
    runCatching {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }
}

// SMCPKG_SUPPORT>>>Cursor031
private fun openPlayStoreListing(context: android.content.Context) {
    val marketIntent = Intent(Intent.ACTION_VIEW, Uri.parse(PLAY_STORE_MARKET_URI))
    try {
        context.startActivity(marketIntent)
    } catch (_: ActivityNotFoundException) {
        openUrl(context, PLAY_STORE_WEB_URI)
    }
}
// SMCPKG_SUPPORT<<<Cursor031
