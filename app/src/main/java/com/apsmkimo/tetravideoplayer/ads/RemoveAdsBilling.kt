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
import android.app.Application
import android.os.Handler
import android.os.Looper
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.apsmkimo.tetravideoplayer.BuildConfig
import java.util.concurrent.CopyOnWriteArrayList

// SMCPKG_SUPPORT>>>Cursor033
/**
 * One-time managed product that removes ads.
 *
 * Play Console setup (required before the purchase sheet can open):
 * 1. Open Play Console → the TetraVideoPlayer app → Monetize → In-app products.
 * 2. Create a **managed / one-time** product with id [PRODUCT_ID_REMOVE_ADS]
 *    (`remove_ads`). Do not mark it consumable.
 * 3. Activate the product and publish an app build that includes this Billing
 *    Library integration (internal testing track is enough).
 * 4. Add license testers under Setup → License testing to buy without charge.
 */
const val PRODUCT_ID_REMOVE_ADS = "remove_ads"

/**
 * Google Play Billing client for the [PRODUCT_ID_REMOVE_ADS] entitlement.
 * Restores ownership when the client is ready (reinstall / new device).
 */
class RemoveAdsBilling(private val app: Application) {
    fun interface EntitlementListener {
        fun onEntitlementChanged(adsRemoved: Boolean)
    }

    enum class LaunchResult {
        STARTED,
        UNAVAILABLE,
        PRODUCT_UNAVAILABLE,
        FAILED,
    }

    private val mainHandler = Handler(Looper.getMainLooper())
    private val listeners = CopyOnWriteArrayList<EntitlementListener>()

    @Volatile
    private var productDetails: ProductDetails? = null

    @Volatile
    var billingReady: Boolean = false
        private set

    @Volatile
    var billingUnavailable: Boolean = false
        private set

    private val purchasesUpdatedListener = PurchasesUpdatedListener { result, purchases ->
        when (result.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                if (purchases != null) {
                    processPurchases(purchases)
                }
            }
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
                queryOwnedPurchases()
            }
            BillingClient.BillingResponseCode.BILLING_UNAVAILABLE -> {
                markUnavailable()
            }
            else -> Unit
        }
    }

    private val billingClient: BillingClient = BillingClient.newBuilder(app)
        .setListener(purchasesUpdatedListener)
        .enablePendingPurchases(
            PendingPurchasesParams.newBuilder()
                .enableOneTimeProducts()
                .build(),
        )
        .enableAutoServiceReconnection()
        .build()

    fun addListener(listener: EntitlementListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: EntitlementListener) {
        listeners.remove(listener)
    }

    fun start() {
        if (billingClient.isReady) {
            billingReady = true
            queryOwnedPurchases()
            queryProductDetails()
            return
        }
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    billingReady = true
                    billingUnavailable = false
                    queryOwnedPurchases()
                    queryProductDetails()
                } else if (
                    billingResult.responseCode == BillingClient.BillingResponseCode.BILLING_UNAVAILABLE ||
                    billingResult.responseCode == BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED
                ) {
                    markUnavailable()
                }
            }

            override fun onBillingServiceDisconnected() {
                billingReady = false
                // enableAutoServiceReconnection() retries on the next API call.
            }
        })
    }

    fun launchPurchase(activity: Activity): LaunchResult {
        if (billingUnavailable || !billingClient.isReady) {
            return LaunchResult.UNAVAILABLE
        }
        val details = productDetails
        if (details == null) {
            queryProductDetails()
            return LaunchResult.PRODUCT_UNAVAILABLE
        }
        val offerToken = offerTokenOf(details)
        if (offerToken.isNullOrEmpty()) {
            return LaunchResult.PRODUCT_UNAVAILABLE
        }
        return try {
            val flowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(
                    listOf(
                        BillingFlowParams.ProductDetailsParams.newBuilder()
                            .setProductDetails(details)
                            .setOfferToken(offerToken)
                            .build(),
                    ),
                )
                .build()
            val result = billingClient.launchBillingFlow(activity, flowParams)
            when (result.responseCode) {
                BillingClient.BillingResponseCode.OK -> LaunchResult.STARTED
                BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
                    queryOwnedPurchases()
                    LaunchResult.STARTED
                }
                BillingClient.BillingResponseCode.BILLING_UNAVAILABLE,
                BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED,
                BillingClient.BillingResponseCode.SERVICE_DISCONNECTED,
                -> {
                    markUnavailable()
                    LaunchResult.UNAVAILABLE
                }
                else -> LaunchResult.FAILED
            }
        } catch (_: Exception) {
            LaunchResult.UNAVAILABLE
        }
    }

    fun restoreAdsForDebug() {
        if (!BuildConfig.DEBUG) return
        AdPreferences.setAdRemoved(app, false)
        AdSession.interstitialConsumed = false
        AdSession.loadedInterstitial = null
        notifyEntitlement(false)
    }

    private fun queryOwnedPurchases() {
        if (!billingClient.isReady) return
        billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP)
                .build(),
        ) { result, purchases ->
            if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                processPurchases(purchases)
            }
        }
    }

    private fun queryProductDetails() {
        if (!billingClient.isReady) return
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(
                listOf(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(PRODUCT_ID_REMOVE_ADS)
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build(),
                ),
            )
            .build()
        billingClient.queryProductDetailsAsync(params) { result, productDetailsResult ->
            if (result.responseCode != BillingClient.BillingResponseCode.OK) {
                return@queryProductDetailsAsync
            }
            productDetails = productDetailsResult.productDetailsList.firstOrNull { details ->
                details.productId == PRODUCT_ID_REMOVE_ADS
            }
        }
    }

    private fun processPurchases(purchases: List<Purchase>) {
        var owned = false
        for (purchase in purchases) {
            if (!purchase.products.contains(PRODUCT_ID_REMOVE_ADS)) continue
            if (purchase.purchaseState != Purchase.PurchaseState.PURCHASED) continue
            owned = true
            if (!purchase.isAcknowledged) {
                billingClient.acknowledgePurchase(
                    AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.purchaseToken)
                        .build(),
                ) { /* entitlement is granted locally regardless of ack result */ }
            }
        }
        if (owned) {
            grantAdsRemoved()
        }
    }

    private fun grantAdsRemoved() {
        AdPreferences.setAdRemoved(app, true)
        notifyEntitlement(true)
    }

    private fun notifyEntitlement(adsRemoved: Boolean) {
        mainHandler.post {
            listeners.forEach { it.onEntitlementChanged(adsRemoved) }
        }
    }

    private fun markUnavailable() {
        billingReady = false
        billingUnavailable = true
    }

    private fun offerTokenOf(details: ProductDetails): String? {
        return details.oneTimePurchaseOfferDetailsList
            ?.firstOrNull()
            ?.offerToken
    }
}
// SMCPKG_SUPPORT<<<Cursor033
