package com.neoapps.skijahorina.common

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics

/**
 * Firebase Analytics + Crashlytics helpers.
 * Events show up under Analytics > Events (DebugView for realtime).
 */
object AppAnalytics {

    object Feature {
        const val HUB = "hub"
        const val WEATHER = "weather"
        const val MAP = "map"
        const val WEBCAMS = "webcams"
        const val LIFTS = "lifts"
        const val APARTMENTS = "apartments"
        const val USEFUL_INFO = "useful_info"
        const val SETTINGS = "settings"
        const val ABOUT = "about"
        const val HELP_CENTER = "help_center"
    }

    private var firebaseAnalytics: FirebaseAnalytics? = null

    fun init(context: Context) {
        firebaseAnalytics = FirebaseAnalytics.getInstance(context.applicationContext)
    }

    fun logFeatureOpened(feature: String, extra: Map<String, String> = emptyMap()) {
        logEvent("feature_opened", mapOf("feature" to feature) + extra)
    }

    fun logAction(action: String, target: String? = null) {
        val params = mutableMapOf("action" to action)
        target?.let { params["target"] = it }
        logEvent("user_action", params)
    }

    fun logAdShown(screenId: String) {
        logEvent("ad_shown", mapOf("screen_id" to screenId))
    }

    fun logLanguageChanged(language: String) {
        logEvent("language_changed", mapOf("language" to language))
    }

    fun logThemeChanged(darkMode: Boolean) {
        logEvent("theme_changed", mapOf("dark_mode" to darkMode.toString()))
    }

    fun logDataFetchFailed(source: String, httpCode: Int? = null) {
        val params = mutableMapOf("source" to source)
        httpCode?.let { params["http_code"] = it.toString() }
        logEvent("data_fetch_failed", params)
    }

    fun recordException(
        throwable: Throwable,
        message: String? = null,
        keys: Map<String, String> = emptyMap()
    ) {
        val crashlytics = FirebaseCrashlytics.getInstance()
        message?.let { crashlytics.log(it) }
        keys.forEach { (key, value) -> crashlytics.setCustomKey(key, value) }
        crashlytics.recordException(throwable)
    }

    fun recordFailure(message: String, keys: Map<String, String> = emptyMap()) {
        val crashlytics = FirebaseCrashlytics.getInstance()
        crashlytics.log(message)
        keys.forEach { (key, value) -> crashlytics.setCustomKey(key, value) }
        crashlytics.recordException(RuntimeException(message))
    }

    private fun logEvent(name: String, params: Map<String, String> = emptyMap()) {
        val bundle = Bundle().apply {
            params.forEach { (key, value) -> putString(key, value.take(100)) }
        }
        firebaseAnalytics?.logEvent(name.take(40), bundle)
    }
}
