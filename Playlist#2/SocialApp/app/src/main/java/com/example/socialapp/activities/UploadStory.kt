package com.example.socialapp.activities

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.socialapp.databinding.ActivityUploadStoryBinding
import com.example.socialapp.model.Story
import com.example.socialapp.SupabaseClientProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import io.github.jan.supabase.storage.storage
import io.github.jan.supabase.storage.upload
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.util.*

class UploadStory : AppCompatActivity() {

    private lateinit var binding: ActivityUploadStoryBinding
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference
    private var mediaUri: Uri? = null
    private var mediaType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnPickMedia.setOnClickListener { pickMedia() }
        binding.btnUploadStory.setOnClickListener {
            if (mediaUri == null) {
                Toast.makeText(this, "Pilih media dulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            uploadStory()
        }
    }

    private fun pickMedia() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*", "video/*"))
        startActivityForResult(intent, 101)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == RESULT_OK) {
            mediaUri = data?.data
            val type = contentResolver.getType(mediaUri!!)
            mediaType = if (type?.startsWith("video") == true) "video" else "image"
        }
    }

    private fun uploadStory() {
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Uploading story...")
        progressDialog.show()

        val currentUserId = auth.uid ?: return
        val fileExt = if (mediaType == "video") ".mp4" else ".jpg"
        val fileName = "stories/${UUID.randomUUID()}$fileExt"

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // ✅ Upload ke Supabase Storage
                val bytes = contentResolver.openInputStream(mediaUri!!)!!.readBytes()
                SupabaseClientProvider.client.storage.from("stories").upload(fileName, bytes, upsert = true)

                // ✅ Ambil URL publik
                val mediaUrl = SupabaseClientProvider.client.storage.from("stories").publicUrl(fileName)

                // ✅ Ambil data user (username + profileImageUrl) dari Firebase
                val userSnap = database.child("users").child(currentUserId).get().await()
                val username = userSnap.child("username").getValue(String::class.java) ?: "Unknown"
                val profileUrl = userSnap.child("profileImageUrl").getValue(String::class.java)

                withContext(Dispatchers.Main) {
                    val storyId = database.child("stories").child(currentUserId).push().key!!
                    val story = Story(
                        storyId = storyId,
                        userId = currentUserId,
                        username = username,
                        mediaUrl = mediaUrl,
                        mediaType = mediaType,
                        timestampStart = System.currentTimeMillis(),
                        timestampEnd = System.currentTimeMillis() + 24 * 60 * 60 * 1000 // 24 jam
                    )

                    // ✅ Simpan ke Firebase Realtime Database
                    database.child("stories").child(currentUserId).child(storyId)
                        .setValue(story)
                        .addOnSuccessListener {
                            progressDialog.dismiss()
                            Toast.makeText(this@UploadStory, "Story uploaded!", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        .addOnFailureListener {
                            progressDialog.dismiss()
                            Toast.makeText(this@UploadStory, "Failed: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressDialog.dismiss()
                    Toast.makeText(this@UploadStory, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
