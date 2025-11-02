package com.example.communicationapp.ui.settings

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.communicationapp.R
import com.example.communicationapp.data.AuthRepository
import com.example.communicationapp.ui.auth.LoginActivity
import com.example.communicationapp.ui.profile.EditProfileActivity
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val btnEditProfile: Button = findViewById(R.id.btnEditProfile)
        val btnLogout: Button = findViewById(R.id.btnLogout)

        btnEditProfile.setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
        }

        btnLogout.setOnClickListener {
            // Logout harus di coroutine
            lifecycleScope.launch {
                AuthRepository.logout()
                startActivity(Intent(this@SettingsActivity, LoginActivity::class.java))
                finish()
            }
        }
    }
}
