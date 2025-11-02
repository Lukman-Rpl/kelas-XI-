package com.example.communicationapp.ui.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.communicationapp.data.AuthRepository
import com.example.communicationapp.data.StorageRepository
import com.example.communicationapp.data.UserRepository
import com.example.communicationapp.databinding.ActivityEditProfileBinding
import kotlinx.coroutines.launch

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private var imageUri: Uri? = null
    private val PICK_IMAGE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val user = AuthRepository.currentUser()
        binding.etUsername.setText(user?.username)
        binding.etStatus.setText(user?.status)
        Glide.with(this).load(user?.profileImageUrl).into(binding.ivEditProfile)

        binding.ivEditProfile.setOnClickListener { pickImage() }
        binding.btnSaveProfile.setOnClickListener { saveProfile() }
    }

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            imageUri = data?.data
            binding.ivEditProfile.setImageURI(imageUri)
        }
    }

    private fun saveProfile() {
        val username = binding.etUsername.text.toString()
        val status = binding.etStatus.text.toString()
        val user = AuthRepository.currentUser() ?: return

        lifecycleScope.launch {
            try {
                var profileImageUrl = user.profileImageUrl
                imageUri?.let { uri ->
                    profileImageUrl = StorageRepository.uploadImage("profile_pics/${user.id}.jpg", uri)
                }

                UserRepository.updateUser(
                    user.id,
                    username = username,
                    status = status,
                    profileImageUrl = profileImageUrl
                )

                Toast.makeText(this@EditProfileActivity, "Profil diperbarui", Toast.LENGTH_SHORT).show()
                finish()
            } catch (e: Exception) {
                Toast.makeText(this@EditProfileActivity, "Gagal update: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
