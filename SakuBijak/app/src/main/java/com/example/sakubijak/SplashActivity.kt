package com.example.sakubijak

import android.annotation.SuppressLint
import android.app.Application
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Gunakan PreferenceManager secara konsisten
        val prefManager = PreferenceManager(this)
        val token = prefManager.getToken()

        if (!token.isNullOrEmpty()) {
            // User sudah login -> Buka MainActivity
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            // User belum login -> Buka LoginActivity
            startActivity(Intent(this, LoginActivity::class.java))
        }

        // Matikan animasi transisi agar tidak ada kedipan
        overridePendingTransition(0, 0)
        finish()
    }
}
