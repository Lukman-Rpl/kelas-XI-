package com.example.sakubijak // Sesuaikan dengan nama package aplikasi kamu

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.sakubijak.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {

    // Deklarasi variabel binding
    private lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inisialisasi View Binding
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Mengatur aksi ketika tombol back diklik
        binding.btnBack.setOnClickListener {
            finish() // Menutup Activity dan kembali ke halaman sebelumnya
        }
    }
}