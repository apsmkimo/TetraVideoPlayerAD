/*
 * TetraVideoPlayer
 * Copyright (C) 2026 apsmkimo
 * All rights reserved.
 *
 * This software is proprietary. Unauthorized copying, distribution,
 * modification, or use is strictly prohibited except as expressly
 * permitted in writing by the copyright holder.
 */

package com.apsmkimo.tetravideoplayer

import android.app.Application
// SMCPKG_SUPPORT>>>Cursor033
import com.apsmkimo.tetravideoplayer.ads.RemoveAdsBilling
// SMCPKG_SUPPORT<<<Cursor033
import com.google.android.gms.ads.MobileAds

class TetraVideoPlayerApp : Application() {
    // SMCPKG_SUPPORT>>>Cursor033
    lateinit var removeAdsBilling: RemoveAdsBilling
        private set
    // SMCPKG_SUPPORT<<<Cursor033

    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this)
        // SMCPKG_SUPPORT>>>Cursor033
        removeAdsBilling = RemoveAdsBilling(this).also { it.start() }
        // SMCPKG_SUPPORT<<<Cursor033
    }
}
