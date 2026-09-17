package com.example.sakubijak

import android.app.Application


class MySubApplication : Application(){
    override fun onCreate(){
        super.onCreate()

        val themePref = ThemePreferences(this)
        themePref.applyTheme(themePref.isDarkMode())
    }
}