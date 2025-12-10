package com.example.socialapp.activities

import android.app.ProgressDialog
import android.net.Uri
import android.os.Bundle
import android.widget.MediaController
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.socialapp.databinding.ActivityEditPostBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.example.socialapp.SupabaseClientProvider
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.*
import java.util.*

class EditPostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditPostBinding
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    private var postId: String? = null
    private var oldMediaUrl: String? = null
    private var mediaUri: Uri? = null
    private var mediaType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ✅ Ambil data dari intent
        postId = intent.getStringExtra("postId")
        val caption = intent.getStringExtra("caption")
        oldMediaUrl = intent.getStringExtra("mediaUrl")
        val oldType = intent.getStringExtra("mediaType")

        // ✅ Set caption lama
        binding.etEditCaption.setText(caption)

        // ✅ Tampilkan media lama
        if (oldType == "image") {
            binding.imgPreviewEdit.setImageURI(Uri.parse(oldMediaUrl))
            binding.imgPreviewEdit.visibility = android.view.View.VISIBLE
        } else if (oldType == "video") {
            binding.videoPreviewEdit.setVideoURI(Uri.parse(oldMediaUrl))
            binding.videoPreviewEdit.setMediaController(MediaController(this))
            binding.videoPreviewEdit.start()
            binding.videoPreviewEdit.visibility = android.view.View.VISIBLE
        }

        // ✅ Ganti media
        binding.btnPickMediaEdit.setOnClickListener {
            selectMediaLauncher.launch("*/*")
        }

        // ✅ Update post
        binding.btnSavePost.setOnClickListener {
            if (postId == null) {
                Toast.makeText(this, "Post tidak ditemukan", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            updatePost()
        }
    }

    private val selectMediaLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                mediaUri = uri
                val type = contentResolver.getType(uri) ?: ""
                if (type.startsWith("image")) {
                    mediaType = "image"
                    binding.imgPreviewEdit.setImageURI(uri)
                    binding.imgPreviewEdit.visibility = android.view.View.VISIBLE
                    binding.videoPreviewEdit.visibility = android.view.View.GONE
                } else if (type.startsWith("video")) {
                    mediaType = "video"
                    binding.videoPreviewEdit.setVideoURI(uri)
                    binding.videoPreviewEdit.setMediaController(MediaController(this))
                    binding.videoPreviewEdit.start()
                    binding.videoPreviewEdit.visibility = android.view.View.VISIBLE
                    binding.imgPreviewEdit.visibility = android.view.View.GONE
                }
            }
        }

    private fun updatePost() {
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Updating post...")
        progressDialog.show()

        val currentUserId = auth.uid ?: return

        CoroutineScope(Dispatchers.IO).launch {
            try {
                var finalUrl = oldMediaUrl
                var finalType = mediaType

                // ✅ Jika user ganti media → upload ulang ke Supabase
                if (mediaUri != null) {
                    val bytes = contentResolver.openInputStream(mediaUri!!)!!.readBytes()
                    val fileName = "posts/${UUID.randomUUID()}"
                    SupabaseClientProvider.client.storage
                        .from("posts")
                        .upload(fileName, bytes, upsert = true)

                    finalUrl = SupabaseClientProvider.client.storage
                        .from("posts")
                        .publicUrl(fileName)
                }

                withContext(Dispatchers.Main) {
                    // ✅ Update ke Firebase
                    val updatedPost = mapOf(
                        "caption" to binding.etEditCaption.text.toString(),
                        "mediaUrl" to finalUrl,
                        "mediaType" to (finalType ?: "image"),
                        "userId" to currentUserId,
                        "timestamp" to System.currentTimeMillis()
                    )

                    database.child("posts").child(postId!!).updateChildren(updatedPost)
                        .addOnSuccessListener {
                            progressDialog.dismiss()
                            Toast.makeText(this@EditPostActivity, "Post updated!", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        .addOnFailureListener {
                            progressDialog.dismiss()
                            Toast.makeText(this@EditPostActivity, "Failed: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressDialog.dismiss()
                    Toast.makeText(this@EditPostActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
