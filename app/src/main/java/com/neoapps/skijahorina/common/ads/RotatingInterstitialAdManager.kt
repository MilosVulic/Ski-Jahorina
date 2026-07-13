package com.neoapps.skijahorina.common.ads

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

/**
 * Loads one interstitial at a time, rotating through [unitIds] so all AdMob units stay in use.
 * Preloads the next ad after each show or failed load.
 */
class RotatingInterstitialAdManager(
    private val appContext: Context,
    private val unitIds: List<String>,
) {
    private var loadedAd: InterstitialAd? = null
    private var rotationIndex = 0
    private var isLoading = false

    val isReady: Boolean
        get() = loadedAd != null

    fun preload() {
        if (loadedAd != null || isLoading || unitIds.isEmpty()) return

        isLoading = true
        val unitId = unitIds[rotationIndex % unitIds.size]

        InterstitialAd.load(
            appContext,
            unitId,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    isLoading = false
                    loadedAd = interstitialAd
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    isLoading = false
                    loadedAd = null
                    advanceRotation()
                    preload()
                }
            }
        )
    }

    /**
     * Shows a loaded ad if available. [onAfterAd] runs after dismiss or if show fails.
     * @return true if an ad was shown, false if nothing was ready (caller should navigate immediately).
     */
    fun show(activity: Activity, onAfterAd: () -> Unit): Boolean {
        val ad = loadedAd ?: return false
        loadedAd = null

        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                ad.fullScreenContentCallback = null
                advanceRotation()
                preload()
                onAfterAd()
            }

            override fun onAdFailedToShowFullScreenContent(
                adError: com.google.android.gms.ads.AdError
            ) {
                ad.fullScreenContentCallback = null
                advanceRotation()
                preload()
                onAfterAd()
            }
        }

        ad.show(activity)
        return true
    }

    fun release() {
        loadedAd?.fullScreenContentCallback = null
        loadedAd = null
        isLoading = false
    }

    private fun advanceRotation() {
        if (unitIds.isEmpty()) return
        rotationIndex = (rotationIndex + 1) % unitIds.size
    }

    companion object {
        val HUB_UNIT_IDS = listOf(
            "ca-app-pub-7130760675198405/2157972704",
            "ca-app-pub-7130760675198405/4592564355",
            "ca-app-pub-7130760675198405/8365994691",
        )
    }
}
