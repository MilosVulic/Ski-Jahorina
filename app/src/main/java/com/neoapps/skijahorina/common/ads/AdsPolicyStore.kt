package com.neoapps.skijahorina.common.ads

import com.google.gson.Gson
import com.neoapps.skijahorina.common.PreferenceProvider

/**
 * Local cache + defaults for remote ads policies.
 * Defaults match the previous hardcoded every-3-clicks behavior.
 */
object AdsPolicyStore {

    private val gson = Gson()

    /** Offline fallback only. New screens stay off until enabled in remote config. */
    private val defaults: Map<String, AdsScreenPolicyDto> = mapOf(
        AdsScreens.WEATHER to AdsScreenPolicyDto(AdsScreens.WEATHER, true, "interstitial", 3),
        AdsScreens.LIFTS to AdsScreenPolicyDto(AdsScreens.LIFTS, true, "interstitial", 3),
        AdsScreens.CAMERAS to AdsScreenPolicyDto(AdsScreens.CAMERAS, true, "interstitial", 3),
        AdsScreens.MAP to AdsScreenPolicyDto(AdsScreens.MAP, false, "interstitial", 3),
        AdsScreens.USEFUL_INFO to AdsScreenPolicyDto(AdsScreens.USEFUL_INFO, false, "interstitial", 3),
        AdsScreens.APARTMENTS to AdsScreenPolicyDto(AdsScreens.APARTMENTS, false, "interstitial", 3),
    )

    fun save(config: AdsConfigResponse) {
        PreferenceProvider.adsConfigJson = gson.toJson(config)
        PreferenceProvider.adsConfigFetchedAtEpochMs = System.currentTimeMillis()
    }

    fun policyFor(screenId: String): AdsScreenPolicyDto {
        val cached = cachedScreens()[screenId]
        return cached ?: defaults[screenId] ?: AdsScreenPolicyDto(
            screenId = screenId,
            enabled = false,
            adType = "interstitial",
            everyNClicks = Int.MAX_VALUE
        )
    }

    fun shouldShowAd(screenId: String, clickCount: Int): Boolean {
        val policy = policyFor(screenId)
        if (!policy.enabled) return false
        val everyN = policy.everyNClicks.coerceAtLeast(1)
        return clickCount > 0 && clickCount % everyN == 0
    }

    fun isCacheStale(ttlMs: Long = AdsConfigFetcher.CACHE_TTL_MS): Boolean {
        val fetchedAt = PreferenceProvider.adsConfigFetchedAtEpochMs
        if (fetchedAt <= 0L) return true
        return System.currentTimeMillis() - fetchedAt >= ttlMs
    }

    private fun cachedScreens(): Map<String, AdsScreenPolicyDto> {
        val json = PreferenceProvider.adsConfigJson
        if (json.isBlank()) return emptyMap()
        return try {
            gson.fromJson(json, AdsConfigResponse::class.java)
                ?.screens
                ?.associateBy { it.screenId }
                .orEmpty()
        } catch (_: Exception) {
            emptyMap()
        }
    }
}
