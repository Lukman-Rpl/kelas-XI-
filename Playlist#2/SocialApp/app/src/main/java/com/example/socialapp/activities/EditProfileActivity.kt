package com.example.socialapp.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.socialapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.InputStream
import java.util.*

class EditProfileActivity : AppCompatActivity() {

    private lateinit var imgProfile: ImageView
    private lateinit var btnPickImage: Button
    private lateinit var etUsername: EditText
    private lateinit var etEmail: EditText
    private lateinit var btnSave: Button

    private var selectedImageUri: Uri? = null
    private val uid = FirebaseAuth.getInstance().uid ?: ""

    // Supabase Client
    private val supabase = createSupabaseClient(
        supabaseUrl = "https://YOUR_PROJECT_ID.supabase.co",
        supabaseKey = "YOUR_ANON_KEY"
    ) {
        install(Storage)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_change_profile)

        imgProfile = findViewById(R.id.imgEditProfile)
        btnPickImage = findViewById(R.id.btnPickImage)
        etUsername = findViewById(R.id.etEditUsername)
        etEmail = findViewById(R.id.etEditEmail)
        btnSave = findViewById(R.id.btnSaveProfile)

        loadCurrentProfile()

        btnPickImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 1001)
        }

        btnSave.setOnClickListener {
            saveProfile()
        }
    }

    private fun loadCurrentProfile() {
        val userRef = FirebaseDatabase.getInstance().getReference("users").child(uid)
        userRef.get().addOnSuccessListener { snapshot ->
            val username = snapshot.child("username").getValue(String::class.java) ?: ""
            val email = snapshot.child("email").getValue(String::class.java) ?: ""
            val profileImageUrl = snapshot.child("profileImageUrl").getValue(String::class.java)

            etUsername.setText(username)
            etEmail.setText(email)
            Glide.with(this)
                .load(profileImageUrl)
                .placeholder(R.drawable.ic_profile)
                .into(imgProfile)
        }
    }

    private fun saveProfile() {
        val username = etUsername.text.toString().trim()
        val email = etEmail.text.toString().trim()

        if (username.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Username dan Email tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }

        val userRef = FirebaseDatabase.getInstance().getReference("users").child(uid)

        if (selectedImageUri != null) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // convert Uri ke ByteArray
                    val inputStream: InputStream? = contentResolver.openInputStream(selectedImageUri!!)
                    val bytes = inputStream?.readBytes() ?: return@launch
                    val fileName = "profile_${uid}_${UUID.randomUUID()}.jpg"

                    // upload ke bucket "profile-images"
                    val bucket = supabase.storage.from("profile-images")
                    bucket.upload(fileName, bytes)

                    // ambil URL publik
                    val publicUrl = bucket.publicUrl(fileName)

                    // update ke Realtime Database
                    val updates = mapOf(
                        "username" to username,
                        "email" to email,
                        "profileImageUrl" to publicUrl
                    )
                    userRef.updateChildren(updates).addOnSuccessListener {
                        runOnUiThread {
                            Toast.makeText(this@EditProfileActivity, "Profil berhasil diperbarui", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }

                } catch (e: Exception) {
                    runOnUiThread {
                        Toast.makeText(this@EditProfileActivity, "Upload gagal: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            val updates = mapOf(
                "username" to username,
                "email" to email
            )
            userRef.updateChildren(updates).addOnSuccessListener {
                Toast.makeText(this, "Profil berhasil diperbarui", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            imgProfile.setImageURI(selectedImageUri)
        }
    }
}
