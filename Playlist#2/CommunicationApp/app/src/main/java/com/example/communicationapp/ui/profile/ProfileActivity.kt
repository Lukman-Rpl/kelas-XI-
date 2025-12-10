package com.example.communicationapp.ui.profile

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.communicationapp.data.AuthRepository
import com.example.communicationapp.data.models.User
import com.example.communicationapp.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private var currentUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ambil user saat ini
        currentUser = AuthRepository.currentUser()

        // Set data user ke UI
        binding.tvUsername.text = currentUser?.username
        binding.tvStatus.text = currentUser?.status
        Glide.with(this)
            .load(currentUser?.profileImageUrl)
            .into(binding.imgAvatar)

        // Tombol edit profil
        binding.btnEditProfile.setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
        }
    }
}
