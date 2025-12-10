package com.example.socialapp.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.socialapp.R
import com.example.socialapp.adapter.CommentAdapter
import com.example.socialapp.model.Comment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class CommentActivity : AppCompatActivity() {

    private lateinit var etAddComment: EditText
    private lateinit var btnSend: ImageButton
    private lateinit var recyclerViewComments: RecyclerView

    private lateinit var commentAdapter: CommentAdapter
    private val commentList = mutableListOf<Comment>()

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance().reference

    private var targetId: String? = null         // postId atau storyId
    private var ownerId: String? = null          // pemilik post/story
    private var isStoryComment = false           // flag utk post/story

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)

        // ✅ Validasi login
        if (auth.currentUser == null) {
            Toast.makeText(this, "Silakan login dulu untuk berkomentar", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
            return
        }

        etAddComment = findViewById(R.id.etAddComment)
        btnSend = findViewById(R.id.btnSend)
        recyclerViewComments = findViewById(R.id.recyclerViewComments)

        // ✅ Ambil data dari intent
        targetId = intent.getStringExtra("postId") ?: intent.getStringExtra("storyId")
        ownerId = intent.getStringExtra("postOwnerId") ?: intent.getStringExtra("storyOwnerId")
        isStoryComment = intent.getBooleanExtra("isStoryComment", false)

        if (targetId == null || ownerId == null) {
            Toast.makeText(this, "❌ Data komentar tidak valid", Toast.LENGTH_LONG).show()
            Log.e("CommentActivity", "targetId=$targetId | ownerId=$ownerId | isStoryComment=$isStoryComment")
            finish()
            return
        }

        // ✅ Setup RecyclerView
        recyclerViewComments.layoutManager = LinearLayoutManager(this)
        commentAdapter = CommentAdapter(this, commentList)
        recyclerViewComments.adapter = commentAdapter

        // ✅ Load komentar
        loadComments()

        btnSend.setOnClickListener {
            val text = etAddComment.text.toString().trim()
            if (text.isEmpty()) {
                Toast.makeText(this, "Komentar tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            postComment(text)
        }
    }

    private fun loadComments() {
        val commentRef = if (isStoryComment) {
            db.child("comments").child("stories").child(targetId!!)
        } else {
            db.child("comments").child("posts").child(targetId!!)
        }

        // ✅ Pakai string path manual, jangan .path
        val path = if (isStoryComment) "comments/stories/$targetId" else "comments/posts/$targetId"
        Log.d("CommentActivity", "📥 loadComments from path: $path")

        commentRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                commentList.clear()
                if (!snapshot.exists()) {
                    Log.w("CommentActivity", "⚠️ Tidak ada komentar untuk targetId=$targetId")
                }
                for (snap in snapshot.children) {
                    val comment = snap.getValue(Comment::class.java)
                    if (comment == null) {
                        Log.e("CommentActivity", "❌ Gagal parse comment dari snapshot: ${snap.value}")
                    } else {
                        commentList.add(comment)
                    }
                }
                Log.d("CommentActivity", "✅ Jumlah komentar dimuat: ${commentList.size}")
                commentAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                val msg = "❌ Gagal memuat komentar: ${error.message} | Code: ${error.code}"
                Toast.makeText(this@CommentActivity, msg, Toast.LENGTH_LONG).show()
                Log.e("CommentActivity", msg, error.toException())
            }
        })
    }

    private fun postComment(text: String) {
        val uid = auth.uid ?: return
        val commentRef = if (isStoryComment) {
            db.child("comments").child("stories").child(targetId!!).push()
        } else {
            db.child("comments").child("posts").child(targetId!!).push()
        }

        val comment = Comment(
            commentId = commentRef.key,
            userId = uid,
            content = text,
            timestamp = System.currentTimeMillis()
        )

        Log.d("CommentActivity", "✍️ postComment: $comment")

        commentRef.setValue(comment).addOnSuccessListener {
            etAddComment.text.clear()
            sendNotification(ownerId!!, "comment", targetId!!)
            Toast.makeText(this, "✅ Komentar terkirim", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            val msg = "❌ Gagal mengirim komentar: ${it.message}"
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
            Log.e("CommentActivity", msg, it)
        }
    }

    private fun sendNotification(receiverId: String, type: String, targetId: String) {
        val uid = auth.uid ?: return
        if (receiverId == uid) return  // jangan kirim notif ke diri sendiri

        val notifRef = db.child("notifications").child(receiverId).push()
        val notif = mutableMapOf<String, Any?>(
            "senderId" to uid,
            "receiverId" to receiverId,
            "type" to type,
            "timestamp" to System.currentTimeMillis()
        )

        if (isStoryComment) {
            notif["storyId"] = targetId
        } else {
            notif["postId"] = targetId
        }

        notifRef.setValue(notif).addOnFailureListener {
            Log.e("CommentActivity", "❌ Gagal menyimpan notif: ${it.message}", it)
        }
    }
}
