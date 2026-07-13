package com.neoapps.skijahorina.common

import com.chibatching.kotpref.KotprefModel

object PreferenceProvider : KotprefModel() {

    var language: String by stringPref("en")
    var darkMode: Boolean by booleanPref(false)

    var lastLiftInfoJahorinaFetchTime: String by stringPref("2007-12-03T10:15:30")

    var liftsJahorina: String by stringPref("")

    var lastWeatherFetchTime: String by stringPref("2007-12-03T10:15:30")
    var temperature: String by stringPref("")
    var wind: String by stringPref("")
    var snow: String by stringPref("")
    var weatherImage: String by stringPref("")
    var forecast: String by stringPref("")

    /** ISO timestamp from API `updatedAt`, when available. */
    var resortApiUpdatedAt: String by stringPref("")

    var weatherClicks: Int by intPref(0)
    var liftsClicks: Int by intPref(0)
    var cameraClicks: Int by intPref(0)

    /** JSON map of screenId -> click count for interstitial gating. */
    var adsClickCountsJson: String by stringPref("")

    /** Cached JSON from GET /v1/ads-config/{appId}. */
    var adsConfigJson: String by stringPref("")
    var adsConfigFetchedAtEpochMs: Long by longPref(0L)
}