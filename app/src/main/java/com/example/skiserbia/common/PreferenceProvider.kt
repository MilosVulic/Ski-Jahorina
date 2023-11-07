package com.example.skiserbia.common

import com.chibatching.kotpref.KotprefModel

object PreferenceProvider : KotprefModel() {

    var language: String by stringPref("en")

    var kopaonikUrl: String by stringPref("https://www.skijalistasrbije.rs/sr/prohodnost-staza-i-rad-zicara-kopaonik/")
    var zlatiborUrl: String by stringPref("https://www.skijalistasrbije.rs/sr/prohodnost-staza-i-rad-zicara-kopaonik/")
    var staraPlaninaUrl: String by stringPref("https://www.skijalistasrbije.rs/sr/prohodnost-staza-i-rad-zicara-kopaonik/")
}