package com.example.newsapp

import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide() // Hilangkan action bar biar tampilan penuh

        webView = findViewById(R.id.webView)

        // Aktifkan JavaScript untuk situs modern
        val webSettings: WebSettings = webView.settings
        webSettings.javaScriptEnabled = true

        // Terapkan WebViewController agar tautan dibuka di dalam aplikasi
        webView.webViewClient = WebViewController()

        // Muat situs berita — bisa diganti dengan situs lain
        webView.loadUrl("https://www.bbc.com/news")

        // Cara baru untuk menangani tombol "Back"
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (webView.canGoBack()) {
                    webView.goBack()
                } else {
                    // Jika tidak ada halaman sebelumnya di WebView, panggil perilaku default
                    // (misalnya keluar dari aplikasi)
                    if (isEnabled) {
                        isEnabled = false
                        onBackPressedDispatcher.onBackPressed()
                    }
                }
            }
        })
    }
}
