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

// SMCPKG_SUPPORT>>>Cursor043
import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.apsmkimo.tetravideoplayer.BuildConfig
import com.google.android.gms.ads.MobileAds
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.FormError
import com.google.android.ump.UserMessagingPlatform
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Google UMP consent gate for AdMob.
 *
 * Startup (every [Activity] launch, because the form needs an Activity):
 * 1. [gatherConsent] calls [ConsentInformation.requestConsentInfoUpdate].
 * 2. That call synchronously exposes the previous session's cached
 *    [ConsentInformation.canRequestAds]. When it is true, [MobileAds.initialize]
 *    starts immediately so ads are not blocked on this session's network round-trip.
 * 3. On success, [UserMessagingPlatform.loadAndShowConsentFormIfRequired] presents
 *    a form only when Google says one is required.
 * 4. On a consent-info or form error the app does not crash. [canRequestAds] is
 *    checked again; UMP keeps the previous session's status.
 * 5. [MobileAds.initialize] runs at most once per process. Banner and interstitial
 *    loads wait until that callback, and only while [mayRequestAds] is true.
 *
 * remove_ads: the consent update (and form, when required) still runs. Play Billing
 * may not have restored the entitlement yet, and Privacy & messaging can require a
 * privacy-options entry. Ad requests and [MobileAds.initialize] stay off while
 * [AdPreferences.isAdRemoved] is true.
 *
 * Debug builds force EEA geography for the hashed test device id below.
 * Release builds never attach [ConsentDebugSettings].
 */
class AdsConsentManager private constructor(private val appContext: Context) {
    private val consentInformation: ConsentInformation =
        UserMessagingPlatform.getConsentInformation(appContext)
    private val mainHandler = Handler(Looper.getMainLooper())
    private val adsInitScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val mobileAdsInitializeCalled = AtomicBoolean(false)

    @Volatile
    private var mobileAdsInitializeFinished: Boolean = false

    private val _canRequestAds = MutableStateFlow(false)
    val canRequestAdsFlow: StateFlow<Boolean> = _canRequestAds.asStateFlow()

    private val _adsSdkReady = MutableStateFlow(false)
    val adsSdkReady: StateFlow<Boolean> = _adsSdkReady.asStateFlow()

    private val _privacyOptionsRequired = MutableStateFlow(false)
    val privacyOptionsRequired: StateFlow<Boolean> = _privacyOptionsRequired.asStateFlow()

    val canRequestAds: Boolean
        get() = consentInformation.canRequestAds()

    /**
     * True only when UMP allows an ad request, the Mobile Ads SDK has finished
     * its single initialize, and the user has not purchased remove_ads.
     */
    fun mayRequestAds(context: Context): Boolean {
        return consentInformation.canRequestAds() &&
            _adsSdkReady.value &&
            !AdPreferences.isAdRemoved(context)
    }

    fun gatherConsent(activity: Activity) {
        val params = consentRequestParameters(activity)
        consentInformation.requestConsentInfoUpdate(
            activity,
            params,
            {
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) { formError ->
                    if (formError != null) {
                        Log.w(TAG, "Consent form: ${formError.errorCode}: ${formError.message}")
                    }
                    publish()
                    ensureMobileAdsInitialized(activity)
                }
            },
            { requestConsentError ->
                Log.w(
                    TAG,
                    "Consent info update: ${requestConsentError.errorCode}: ${requestConsentError.message}",
                )
                publish()
                ensureMobileAdsInitialized(activity)
            },
        )
        // Previous-session canRequestAds() is updated synchronously by the call above.
        publish()
        ensureMobileAdsInitialized(activity)
    }

    fun showPrivacyOptionsForm(
        activity: Activity,
        onDismissed: (FormError?) -> Unit,
    ) {
        UserMessagingPlatform.showPrivacyOptionsForm(activity) { formError ->
            if (formError != null) {
                Log.w(TAG, "Privacy options: ${formError.errorCode}: ${formError.message}")
            }
            publish()
            ensureMobileAdsInitialized(activity)
            onDismissed(formError)
        }
    }

    /**
     * Starts [MobileAds.initialize] once [canRequestAds] is true and ads are not removed.
     * Safe to call again after a purchase restore or a later consent grant: the SDK
     * initialize still runs at most once, and [adsSdkReady] is raised if it already finished.
     */
    fun ensureMobileAdsInitialized(context: Context) {
        if (AdPreferences.isAdRemoved(context) || !consentInformation.canRequestAds()) {
            return
        }
        if (mobileAdsInitializeFinished) {
            _adsSdkReady.value = true
            return
        }
        if (!mobileAdsInitializeCalled.compareAndSet(false, true)) {
            return
        }
        val applicationContext = context.applicationContext
        adsInitScope.launch {
            MobileAds.initialize(applicationContext) {
                mainHandler.post {
                    mobileAdsInitializeFinished = true
                    publish()
                }
            }
        }
    }

    private fun publish() {
        val allowed = consentInformation.canRequestAds()
        _canRequestAds.value = allowed
        _privacyOptionsRequired.value =
            consentInformation.privacyOptionsRequirementStatus ==
            ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED
        if (!allowed) {
            AdSession.loadedInterstitial = null
        }
        val showAds = allowed &&
            mobileAdsInitializeFinished &&
            !AdPreferences.isAdRemoved(appContext)
        _adsSdkReady.value = showAds
        if (BuildConfig.DEBUG) {
            Log.d(
                TAG,
                "canRequestAds=$allowed privacyOptions=${consentInformation.privacyOptionsRequirementStatus} adsSdkReady=$showAds",
            )
        }
    }

    private fun consentRequestParameters(activity: Activity): ConsentRequestParameters {
        val builder = ConsentRequestParameters.Builder()
        if (BuildConfig.DEBUG) {
            val debugSettings = ConsentDebugSettings.Builder(activity)
                .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
                .addTestDeviceHashedId(DEBUG_TEST_DEVICE_HASHED_ID)
                .build()
            builder.setConsentDebugSettings(debugSettings)
        }
        return builder.build()
    }

    companion object {
        private const val TAG = "AdsConsent"

        /**
         * Placeholder. Replace with the hashed id from logcat, for example
         * `Use new ConsentDebugSettings.Builder().addTestDeviceHashedId("ABCDEF012345")`.
         * EEA debug geography applies only to registered test devices, and only in debug builds.
         */
        const val DEBUG_TEST_DEVICE_HASHED_ID = "TEST-DEVICE-HASHED-ID"

        @Volatile
        private var instance: AdsConsentManager? = null

        fun getInstance(context: Context): AdsConsentManager {
            return instance ?: synchronized(this) {
                instance ?: AdsConsentManager(context.applicationContext).also { instance = it }
            }
        }
    }
}
// SMCPKG_SUPPORT<<<Cursor043
