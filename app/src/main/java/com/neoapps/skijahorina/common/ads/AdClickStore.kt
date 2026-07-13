package com.neoapps.skijahorina.common.ads

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.neoapps.skijahorina.common.PreferenceProvider

/** Per-screen interstitial click counters backed by a JSON map in prefs. */
object AdClickStore {

    private val gson = Gson()
    private val type = object : TypeToken<MutableMap<String, Int>>() {}.type

    fun increment(screenId: String): Int {
        val map = read()
        val next = (map[screenId] ?: 0) + 1
        map[screenId] = next
        write(map)
        return next
    }

    fun reset(screenId: String) {
        val map = read()
        map[screenId] = 0
        write(map)
    }

    fun count(screenId: String): Int = read()[screenId] ?: 0

    private fun read(): MutableMap<String, Int> {
        val json = PreferenceProvider.adsClickCountsJson
        if (json.isBlank()) return mutableMapOf()
        return try {
            gson.fromJson<MutableMap<String, Int>>(json, type) ?: mutableMapOf()
        } catch (_: Exception) {
            mutableMapOf()
        }
    }

    private fun write(map: Map<String, Int>) {
        PreferenceProvider.adsClickCountsJson = gson.toJson(map)
    }
}
