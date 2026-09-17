/*
 * TetraVideoPlayer
 * Copyright (C) 2026 apsmkimo
 * All rights reserved.
 *
 * This software is proprietary. Unauthorized copying, distribution,
 * modification, or use is strictly prohibited except as expressly
 * permitted in writing by the copyright holder.
 */

package com.apsmkimo.tetravideoplayer.ads

import android.app.Activity
import android.content.Context
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume

object AdConfig {
    const val APP_ID = "ca-app-pub-3940256099942544~3347511713"
    const val BANNER_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"
    const val INTERSTITIAL_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"
}

object AdPreferences {
    private const val PREFS = "tetra_ads"
    const val KEY_AD_REMOVED = "is_ad_removed"

    fun isAdRemoved(context: Context): Boolean {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getBoolean(KEY_AD_REMOVED, false)
    }

    fun setAdRemoved(context: Context, removed: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_AD_REMOVED, removed)
            .apply()
    }
}

object AdSession {
    @Volatile
    var interstitialConsumed: Boolean = false

    @Volatile
    var loadedInterstitial: InterstitialAd? = null
}

fun preloadInterstitial(context: Context) {
    if (AdPreferences.isAdRemoved(context) || AdSession.interstitialConsumed) {
        return
    }
    if (AdSession.loadedInterstitial != null) {
        return
    }
    InterstitialAd.load(
        context,
        AdConfig.INTERSTITIAL_UNIT_ID,
        AdRequest.Builder().build(),
        object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(ad: InterstitialAd) {
                AdSession.loadedInterstitial = ad
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
                AdSession.loadedInterstitial = null
            }
        },
    )
}

suspend fun maybeShowInterstitialOnce(activity: Activity) {
    if (AdPreferences.isAdRemoved(activity) || AdSession.interstitialConsumed) {
        return
    }
    val ad = withTimeoutOrNull(2_500) {
        while (AdSession.loadedInterstitial == null) {
            delay(50)
        }
        AdSession.loadedInterstitial
    }
    if (ad == null) {
        AdSession.interstitialConsumed = true
        return
    }
    suspendCancellableCoroutine { cont ->
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                AdSession.interstitialConsumed = true
                AdSession.loadedInterstitial = null
                if (cont.isActive) cont.resume(Unit)
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                AdSession.interstitialConsumed = true
                AdSession.loadedInterstitial = null
                if (cont.isActive) cont.resume(Unit)
            }
        }
        ad.show(activity)
    }
}

@Composable
fun BannerAd(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val activity = context as? Activity
    val lifecycleOwner = LocalLifecycleOwner.current
    val adView = remember {
        AdView(context).apply {
            val widthDp = if (activity != null) {
                val density = resources.displayMetrics.density
                (resources.displayMetrics.widthPixels / density).toInt().coerceAtLeast(320)
            } else {
                320
            }
            setAdSize(
                if (activity != null) {
                    AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, widthDp)
                } else {
                    AdSize.BANNER
                },
            )
            adUnitId = AdConfig.BANNER_UNIT_ID
            loadAd(AdRequest.Builder().build())
        }
    }

    DisposableEffect(lifecycleOwner, adView) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> adView.resume()
                Lifecycle.Event.ON_PAUSE -> adView.pause()
                Lifecycle.Event.ON_DESTROY -> adView.destroy()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            adView.destroy()
        }
    }

    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        factory = {
            (adView.parent as? ViewGroup)?.removeView(adView)
            FrameLayout(it).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                )
                addView(adView)
            }
        },
        onRelease = { /* destroy handled in DisposableEffect */ },
    )
}
