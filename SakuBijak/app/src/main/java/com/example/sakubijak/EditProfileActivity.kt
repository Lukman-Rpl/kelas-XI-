package com.example.sakubijak

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.sakubijak.databinding.ActivityEditProfileBinding
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private var selectedImageUri: Uri? = null

    private lateinit var apiService: ApiService

    private val selectImageLauncher = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            binding.ivProfile.setImageURI(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        apiService = ApiClient.getApiService(this)

        loadExistingUserData()
        setupListeners()
    }

    private fun loadExistingUserData() {
        val sharedPref = getSharedPreferences("sakubijak_pref", Context.MODE_PRIVATE)
        val currentName = sharedPref.getString("user_name", "")
        val currentEmail = sharedPref.getString("user_email", "")

        binding.etName.setText(currentName)
        binding.etEmail.setText(currentEmail)

        // Catatan: Sebaiknya tampilkan foto menggunakan Library Gambar (Glide/Coil)
        // dengan URL dari server, bukan Uri lokal.
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnChangePhoto.setOnClickListener {
            openGallery()
        }

        binding.btnSaveProfile.setOnClickListener {
            updateProfile()
        }
    }

    private fun openGallery() {
        selectImageLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    private fun updateProfile() {
        val name = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()

        if (name.isEmpty()) {
            binding.etName.error = "Nama tidak boleh kosong"
            return
        }

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.error = "Masukkan format email yang valid!"
            return
        }

        binding.btnSaveProfile.isEnabled = false
        binding.btnSaveProfile.text = "Menyimpan..."

        lifecycleScope.launch {
            try {
                val namePart = name.toRequestBodyText()
                val emailPart = email.toRequestBodyText()

                // Sesuaikan "photo" dengan nama field di Backend (misal: "image", "avatar", "photo")
                val photoPart = selectedImageUri?.let { uri ->
                    uriToMultipartBodyPart(uri, "photo")
                }

                val response = apiService.updateProfile(namePart, emailPart, photoPart)

                if (response.isSuccessful && response.body() != null) {
                    val apiResponseBody = response.body()!!
                    val authData = apiResponseBody.data

                    val sharedPref = getSharedPreferences("sakubijak_pref", Context.MODE_PRIVATE)

                    val updatedName = authData?.user?.name ?: name
                    val updatedEmail = authData?.user?.email ?: email

                    sharedPref.edit().apply {
                        putString("user_name", updatedName)
                        putString("user_email", updatedEmail)
                        // Disarankan menyimpan URL foto dari server jika backend menyediakannya:
                        // putString("user_photo_url", authData?.user?.photoUrl)
                        apply()
                    }

                    Toast.makeText(
                        this@EditProfileActivity,
                        apiResponseBody.message ?: "Profil berhasil diperbarui",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("EditProfile", "Error Body: $errorBody")
                    resetSaveButton("Gagal memperbarui profil (Error ${response.code()})")
                }

            } catch (e: Exception) {
                Log.e("EditProfile", "Exception: ${e.message}", e)
                resetSaveButton("Gagal memperbarui profil: ${e.message}")
            }
        }
    }

    private fun resetSaveButton(message: String) {
        binding.btnSaveProfile.isEnabled = true
        binding.btnSaveProfile.text = "Simpan Perubahan"
        Toast.makeText(this@EditProfileActivity, message, Toast.LENGTH_SHORT).show()
    }

    private fun String.toRequestBodyText(): RequestBody {
        return this.toRequestBody("text/plain".toMediaTypeOrNull())
    }

    private fun uriToMultipartBodyPart(uri: Uri, paramName: String): MultipartBody.Part? {
        return try {
            val mimeType = contentResolver.getType(uri) ?: "image/jpeg"
            val inputStream = contentResolver.openInputStream(uri) ?: return null

            // Buat file temporary unik
            val file = File(cacheDir, "temp_profile_${System.currentTimeMillis()}.jpg")
            val outputStream = FileOutputStream(file)

            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }

            val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull())
            MultipartBody.Part.createFormData(paramName, file.name, requestFile)
        } catch (e: Exception) {
            Log.e("EditProfile", "Gagal konversi URI ke Multipart: ${e.message}")
            null
        }
    }
}