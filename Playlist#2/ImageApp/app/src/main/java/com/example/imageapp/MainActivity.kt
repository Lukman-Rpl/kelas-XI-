package com.example.imageapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Hilangkan action bar agar tampilan lebih bersih
        supportActionBar?.hide()

        // Inisialisasi ImageView
        val onlineImage: ImageView = findViewById(R.id.onlineImage)

        // URL gambar (pastikan ini file gambar langsung)
        val imageUrl = "https://images.unsplash.com/photo-1506744038136-46273834b3fb"

        // Gunakan Picasso untuk memuat gambar online
        Picasso.get()
            .load(imageUrl)
            .placeholder(R.drawable.placeholder) // tampil sementara
            .error(R.drawable.placeholder) // tampil jika gagal
            .into(onlineImage)
    }
}
