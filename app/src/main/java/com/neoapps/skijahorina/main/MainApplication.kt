package com.neoapps.skijahorina.main

import android.app.Application
import android.content.Context
import android.content.res.Resources
import androidx.appcompat.app.AppCompatDelegate
import com.neoapps.skijahorina.common.PreferenceProvider
import dev.b3nedikt.app_locale.AppLocale
import dev.b3nedikt.reword.RewordInterceptor
import dev.b3nedikt.viewpump.ViewPump
import java.util.Locale

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        ViewPump.init(RewordInterceptor)
        AppLocale.supportedLocales = listOf(Locale("en"), Locale("sr"), Locale("ru") , Locale("de"))
        AppLocale.desiredLocale = Locale(PreferenceProvider.language)

        setTheme()
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(newBase)
    }

    override fun getResources(): Resources {
        return AppLocale.wrap(baseContext).resources
    }

    private fun setTheme(){
        if (PreferenceProvider.darkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}