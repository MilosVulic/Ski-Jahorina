package com.neoapps.skijahorina.common.ads

object AdsAppIds {
    const val SKI_JAHORINA = "ski_jahorina"
}

/**
 * Screen IDs that the app can monetize. Add a matching row in
 * `ad_screen_policies` (app_id=ski_jahorina) and set enabled=true to turn ads on.
 */
object AdsScreens {
    const val WEATHER = "weather"
    const val LIFTS = "lifts"
    const val CAMERAS = "cameras"
    const val MAP = "map"
    const val USEFUL_INFO = "useful_info"
    const val APARTMENTS = "apartments"

    val ALL = listOf(WEATHER, LIFTS, CAMERAS, MAP, USEFUL_INFO, APARTMENTS)
}
