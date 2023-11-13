package com.example.skiserbia.common

import com.chibatching.kotpref.KotprefModel

object PreferenceProvider : KotprefModel() {

    var language: String by stringPref("en")
    var darkMode: Boolean by booleanPref(false)

    var kopaonikUrl: String by stringPref("https://www.skijalistasrbije.rs/sr/prohodnost-staza-i-rad-zicara-kopaonik/")
    var zlatiborUrl: String by stringPref("https://www.skijalistasrbije.rs/en/lifts-and-slopes-tornik-zlatibor/")
    var staraPlaninaUrl: String by stringPref("https://www.skijalistasrbije.rs/en/lifts-and-slopes-stara-planina/")

    var weatherUrl: String by stringPref("https://www.skijalistasrbije.rs/en/node/1313/")

}