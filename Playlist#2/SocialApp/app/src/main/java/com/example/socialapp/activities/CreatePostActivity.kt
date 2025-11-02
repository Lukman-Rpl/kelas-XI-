package com.example.socialapp.activities

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.MediaController
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.socialapp.databinding.FragmentPostBinding
import com.example.socialapp.model.Post
import com.example.socialapp.SupabaseClientProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*

class CreatePostActivity : AppCompatActivity() {

    private lateinit var binding: FragmentPostBinding
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    private var mediaUri: Uri? = null
    private var mediaType: String? = null // "image" atau "video"

    private val selectMediaLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                mediaUri = result.data?.data
                val mimeType = contentResolver.getType(mediaUri!!)
                mediaType = if (mimeType?.startsWith("video") == true) "video" else "image"

                showPreview()
                binding.tvSelectedMedia.text = "Media dipilih: $mediaType"
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Pilih media
        binding.btnSelectMedia.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "*/*"
            intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*", "video/*"))
            selectMediaLauncher.launch(intent)
        }

        // Upload post
        binding.btnUpload.setOnClickListener {
            if (mediaUri == null) {
                Toast.makeText(this, "Pilih media dulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            uploadPost()
        }
    }

    private fun showPreview() {
        binding.imgPreview.visibility = View.GONE
        binding.videoPreview.visibility = View.GONE

        when (mediaType) {
            "image" -> {
                binding.imgPreview.visibility = View.VISIBLE
                Glide.with(this).load(mediaUri).into(binding.imgPreview)
            }
            "video" -> {
                binding.videoPreview.visibility = View.VISIBLE
                binding.videoPreview.setVideoURI(mediaUri)
                binding.videoPreview.setMediaController(MediaController(this))
                binding.videoPreview.requestFocus()
                binding.videoPreview.start()
            }
        }
    }

    private fun uploadPost() {
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Uploading...")
        progressDialog.show()

        val uid = auth.uid ?: return
        val caption = binding.etCaption.text.toString()
        val fileName = "posts/${UUID.randomUUID()}"

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // ✅ Cek apakah Supabase user masih login
                val session = SupabaseClientProvider.client.auth.currentSessionOrNull()
                if (session == null) {
                    withContext(Dispatchers.Main) {
                        progressDialog.dismiss()
                        Toast.makeText(this@CreatePostActivity, "User belum login Supabase", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }

                // ✅ Upload file ke Supabase Storage
                val inputStream = contentResolver.openInputStream(mediaUri!!)!!.readBytes()
                SupabaseClientProvider.client.storage
                    .from("posts")
                    .upload(fileName, inputStream, upsert = true)

                // ✅ Ambil URL publik dari Supabase
                val mediaUrl = SupabaseClientProvider.client.storage
                    .from("posts")
                    .publicUrl(fileName)

                // ✅ Ambil username dari Firebase Realtime DB
                val snapshot = database.child("users").child(uid).get().await()
                val username = snapshot.child("email").getValue(String::class.java) ?: "unknown"

                // ✅ Buat post object
                val postId = database.child("posts").push().key!!
                val post = Post(
                    postId = postId,
                    userId = uid,
                    username = username,
                    caption = caption,
                    mediaUrl = mediaUrl,
                    mediaType = mediaType,
                    timestamp = System.currentTimeMillis()
                )

                withContext(Dispatchers.Main) {
                    // ✅ Simpan metadata ke Firebase
                    database.child("posts").child(postId).setValue(post)
                        .addOnSuccessListener {
                            progressDialog.dismiss()
                            Toast.makeText(this@CreatePostActivity, "Post uploaded!", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        .addOnFailureListener {
                            progressDialog.dismiss()
                            Toast.makeText(this@CreatePostActivity, "Failed: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressDialog.dismiss()
                    Toast.makeText(this@CreatePostActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
