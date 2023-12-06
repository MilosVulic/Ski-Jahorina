package com.example.skiserbia.common

import com.chibatching.kotpref.KotprefModel

object PreferenceProvider : KotprefModel() {

    var language: String by stringPref("en")
    var darkMode: Boolean by booleanPref(false)

    var kopaonikUrl: String by stringPref("https://www.skijalistasrbije.rs/sr/prohodnost-staza-i-rad-zicara-kopaonik/")
    var zlatiborUrl: String by stringPref("https://www.skijalistasrbije.rs/en/lifts-and-slopes-tornik-zlatibor/")
    var staraPlaninaUrl: String by stringPref("https://www.skijalistasrbije.rs/en/lifts-and-slopes-stara-planina/")

    var kopaonikCameraUrl: String by stringPref("https://www.yr.no/en/other-conditions/2-8436574/Serbia/Central%20Serbia/Ra%C5%A1ka/Kopaonik/")
    var zlatiborCameraUrl: String by stringPref("https://www.yr.no/en/other-conditions/2-3186400/Serbia/Zlatibor/")
    var staraPlaninaCameraUrl: String by stringPref("https://www.yr.no/en/other-conditions/2-8555992/Serbia/Central%20Serbia/Pirot/Stara%20Planina%20Nature%20Park/")

    var slopesKopaonik: String by stringPref("")
    var lastSlopesInfoKopaonikFetchTime: String by stringPref("2007-12-03T10:15:30")

    var slopesTornik: String by stringPref("")
    var lastSlopesInfoTornikFetchTime: String by stringPref("2007-12-03T10:15:30")

    var slopesStaraPlanina: String by stringPref("")
    var lastSlopesInfoStaraPlaninaFetchTime: String by stringPref("2007-12-03T10:15:30")

    var liftsKopaonik: String by stringPref("")
    var liftsTornik: String by stringPref("")
    var liftsStaraPlanina: String by stringPref("")
}