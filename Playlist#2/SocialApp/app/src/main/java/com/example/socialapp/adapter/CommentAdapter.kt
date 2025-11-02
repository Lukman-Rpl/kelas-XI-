package com.example.socialapp.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.socialapp.R
import com.example.socialapp.model.Comment
import com.google.firebase.database.*

class CommentAdapter(
    private val context: Context,
    private val comments: List<Comment>
) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    private val database = FirebaseDatabase.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun getItemCount(): Int = comments.size

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]

        holder.tvCommentContent.text = comment.content
        holder.tvCommentTime.text = getTimeAgo(comment.timestamp ?: 0)

        // Ambil nama user dari Firebase berdasarkan userId
        val userRef = database.getReference("users").child(comment.userId ?: "")
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val username = snapshot.child("username").getValue(String::class.java) ?: "Unknown"
                holder.tvCommentUsername.text = username
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCommentUsername: TextView = itemView.findViewById(R.id.tvCommentUsername)
        val tvCommentContent: TextView = itemView.findViewById(R.id.tvCommentContent)
        val tvCommentTime: TextView = itemView.findViewById(R.id.tvCommentTime)
    }

    private fun getTimeAgo(time: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - time
        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

        return when {
            seconds < 60 -> "Baru saja"
            minutes < 60 -> "$minutes menit yang lalu"
            hours < 24 -> "$hours jam yang lalu"
            else -> "$days hari yang lalu"
        }
    }
}
