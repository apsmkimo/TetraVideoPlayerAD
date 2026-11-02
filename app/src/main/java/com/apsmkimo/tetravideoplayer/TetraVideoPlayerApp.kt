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
import com.apsmkimo.tetravideoplayer.ads.RemoveAdsBilling
// import com.google.android.gms.ads.MobileAds

class TetraVideoPlayerApp : Application() {
    lateinit var removeAdsBilling: RemoveAdsBilling
        private set

    override fun onCreate() {
        super.onCreate()
        // MobileAds.initialize(this)
        removeAdsBilling = RemoveAdsBilling(this).also { it.start() }
    }
}
