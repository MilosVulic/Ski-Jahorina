package com.neoapps.skijahorina.common

import com.chibatching.kotpref.KotprefModel

object PreferenceProvider : KotprefModel() {

    var language: String by stringPref("en")
    var darkMode: Boolean by booleanPref(false)

    var lastLiftInfoJahorinaFetchTime: String by stringPref("2007-12-03T10:15:30")

    var liftsJahorina: String by stringPref("")

    var weatherClicks: Int by intPref(0)
    var liftsClicks: Int by intPref(0)
    var cameraClicks: Int by intPref(0)
}