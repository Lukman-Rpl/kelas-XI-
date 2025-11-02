package com.example.socialapp.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.socialapp.R
import com.example.socialapp.activities.CommentActivity
import com.example.socialapp.activities.EditPostActivity
import com.example.socialapp.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class PostAdapter(
    private val context: Context,
    private val postList: List<Post>
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgPostMedia: ImageView = itemView.findViewById(R.id.imgPostMedia)
        val videoPostMedia: VideoView = itemView.findViewById(R.id.videoPostMedia)
        val tvCaption: TextView = itemView.findViewById(R.id.tvCaption)
        val btnMore: ImageButton = itemView.findViewById(R.id.btnMore)
        val btnLike: ImageButton = itemView.findViewById(R.id.btnLike)
        val btnComment: ImageButton = itemView.findViewById(R.id.btnComment)
        val tvLikeCount: TextView = itemView.findViewById(R.id.tvLikeCount) // ✅ Tambahan
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun getItemCount(): Int = postList.size

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = postList[position]
        val currentUserId = auth.uid ?: ""

        // === Media (image / video) ===
        holder.imgPostMedia.visibility = View.GONE
        holder.videoPostMedia.visibility = View.GONE

        post.mediaUrl?.let { url ->
            if (post.mediaType == "image") {
                holder.imgPostMedia.visibility = View.VISIBLE
                Glide.with(context)
                    .load(url)
                    .placeholder(R.drawable.placeholder_image)
                    .into(holder.imgPostMedia)
                holder.videoPostMedia.stopPlayback()
            } else if (post.mediaType == "video") {
                holder.videoPostMedia.visibility = View.VISIBLE
                holder.videoPostMedia.setVideoURI(Uri.parse(url))
                holder.videoPostMedia.seekTo(1)
                holder.videoPostMedia.setOnPreparedListener { mp ->
                    mp.isLooping = true
                    holder.videoPostMedia.start()
                }
            }
        }

        // Caption
        holder.tvCaption.text = post.caption ?: ""

        // === Like Feature ===
        val likeRef = database.child("likes").child("posts").child(post.postId ?: "")
        likeRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val isLiked = snapshot.hasChild(currentUserId)
                holder.btnLike.setImageResource(if (isLiked) R.drawable.ic_liked else R.drawable.ic_like)

                // update jumlah like
                val likeCount = snapshot.childrenCount
                holder.tvLikeCount.text = "$likeCount Likes"
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        holder.btnLike.setOnClickListener {
            likeRef.child(currentUserId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        likeRef.child(currentUserId).removeValue()
                    } else {
                        likeRef.child(currentUserId).setValue(true)
                        sendNotification(post.userId ?: "", "like", post.postId ?: "")
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        }

        // === Comment Feature ===
        holder.btnComment.setOnClickListener {
            val intent = Intent(context, CommentActivity::class.java).apply {
                putExtra("postId", post.postId)
                putExtra("postOwnerId", post.userId)
                putExtra("isStory", false)
            }
            context.startActivity(intent)
        }

        // === More Options (Edit / Delete) ===
        holder.btnMore.visibility = if (post.userId == currentUserId) View.VISIBLE else View.GONE
        holder.btnMore.setOnClickListener {
            val popup = PopupMenu(context, holder.btnMore)
            popup.menuInflater.inflate(R.menu.menu_post_options, popup.menu)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_edit -> {
                        val intent = Intent(context, EditPostActivity::class.java).apply {
                            putExtra("postId", post.postId)
                            putExtra("caption", post.caption)
                        }
                        context.startActivity(intent)
                        true
                    }
                    R.id.action_delete -> {
                        post.postId?.let { postId ->
                            database.child("posts").child(postId).removeValue()
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Post deleted", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(context, "Failed to delete post", Toast.LENGTH_SHORT).show()
                                }
                        }
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }
    }

    private fun sendNotification(receiverId: String, type: String, postId: String) {
        if (receiverId == auth.uid) return
        val notifRef = database.child("notifications").child(receiverId).push()
        val notif = mapOf(
            "senderId" to auth.uid,
            "receiverId" to receiverId,
            "type" to type,
            "postId" to postId,
            "timestamp" to System.currentTimeMillis()
        )
        notifRef.setValue(notif)
    }
}
