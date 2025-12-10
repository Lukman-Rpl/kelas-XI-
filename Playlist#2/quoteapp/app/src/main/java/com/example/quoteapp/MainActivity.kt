package com.example.quoteapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide() // Hilangkan Action Bar agar lebih clean

        val tvQuote = findViewById<TextView>(R.id.tvQuote)
        val btnShare = findViewById<Button>(R.id.btnShare)

        btnShare.setOnClickListener {
            val shareText = tvQuote.text.toString()
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, shareText)
            startActivity(Intent.createChooser(intent, "Bagikan kutipan melalui:"))
        }
    }
}
