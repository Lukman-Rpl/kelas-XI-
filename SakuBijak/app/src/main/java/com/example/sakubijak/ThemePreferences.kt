package com.example.sakubijak

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.graphics.drawable.DrawableCompat.applyTheme

class ThemePreferences (context: Context) {
 private val pref: SharedPreferences =context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE)

    companion object{
        private const val PREF_NAME="user_settings"
        private const val KEY_DARK_MODE="is_dark_mode"
    }


    fun setDarkMode(isDarkMode:Boolean){
        pref.edit().putBoolean(KEY_DARK_MODE,isDarkMode).apply()
        applyTheme(isDarkMode)
    }

    fun isDarkMode():Boolean{
      return pref.getBoolean(KEY_DARK_MODE,false)
    }

    fun applyTheme(isDarkMode: Boolean){
        if(isDarkMode){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}