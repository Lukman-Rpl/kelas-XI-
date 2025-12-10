package com.example.blogging.activities

import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.blogapp.model.Blog
import com.example.blogging.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.io.ByteArrayOutputStream
import java.io.InputStream

class CreateBlogActivity : AppCompatActivity() {

    private var imageUri: Uri? = null
    private var imageSelected = false
    private lateinit var imageView: ImageView
    private lateinit var etTitle: EditText
    private lateinit var etContent: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_blog)

        imageView = findViewById(R.id.ivSelectImage)
        etTitle = findViewById(R.id.etTitle)
        etContent = findViewById(R.id.etContent)
        val btnUpload = findViewById<Button>(R.id.btnUpload)

        val userId = FirebaseAuth.getInstance().uid
        if (userId == null) {
            Toast.makeText(this, "Silakan login terlebih dahulu", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        imageView.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK).apply {
                type = "image/*"
            }
            startActivityForResult(intent, 100)
        }

        btnUpload.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val content = etContent.text.toString().trim()

            if (!imageSelected || title.isEmpty() || content.isEmpty()) {
                Toast.makeText(this, "Lengkapi semua data", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val progressDialog = ProgressDialog(this)
            progressDialog.setMessage("Mengunggah blog...")
            progressDialog.setCancelable(false)
            progressDialog.show()

            val base64Image = convertImageToBase64(imageUri!!)
            if (base64Image == null) {
                progressDialog.dismiss()
                Toast.makeText(this, "Gagal mengubah gambar", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val blogRef = FirebaseDatabase.getInstance().getReference("blogs")
            val key = blogRef.push().key!!

            val blog = Blog(
                id = key,
                title = title,
                content = content,
                imageUrl = base64Image,
                authorId = userId,
                timestamp = System.currentTimeMillis()
            )

            blogRef.child(key).setValue(blog)
                .addOnSuccessListener {
                    val userBlogRef = FirebaseDatabase.getInstance().getReference("userBlogs")
                    userBlogRef.child(userId).child(key).setValue(true)
                        .addOnSuccessListener {
                            progressDialog.dismiss()
                            Toast.makeText(this, "Blog berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        .addOnFailureListener {
                            progressDialog.dismiss()
                            Toast.makeText(this, "Gagal menyimpan ke userBlogs", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener {
                    progressDialog.dismiss()
                    Toast.makeText(this, "Gagal menyimpan data blog", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK && data?.data != null) {
            imageUri = data.data!!
            imageView.setImageURI(imageUri)
            imageSelected = true
        }
    }

    private fun convertImageToBase64(uri: Uri): String? {
        return try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
            val byteArray = outputStream.toByteArray()
            Base64.encodeToString(byteArray, Base64.DEFAULT)
        } catch (e: Exception) {
            null
        }
    }
}
