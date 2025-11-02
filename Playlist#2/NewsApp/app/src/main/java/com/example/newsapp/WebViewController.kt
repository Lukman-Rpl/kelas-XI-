package com.example.newsapp

import android.webkit.WebView
import android.webkit.WebViewClient

// WebViewController agar semua link tetap terbuka di dalam aplikasi
class WebViewController : WebViewClient() {
    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
        url?.let {
            view?.loadUrl(it)
        }
        return true
    }
}
