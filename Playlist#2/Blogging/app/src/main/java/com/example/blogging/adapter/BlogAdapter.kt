package com.example.blogging.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.blogapp.model.Blog
import com.example.blogging.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class BlogAdapter(
    private val blogs: List<Blog>,
    private val context: Context
) : RecyclerView.Adapter<BlogAdapter.BlogViewHolder>() {

    private val usernameCache = mutableMapOf<String, String>()
    private val currentUserId = FirebaseAuth.getInstance().uid

    inner class BlogViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvBlogTitle)
        val tvContent: TextView = view.findViewById(R.id.tvBlogContent)
        val tvAuthor: TextView = view.findViewById(R.id.tvBlogAuthor)
        val ivImage: ImageView = view.findViewById(R.id.imageViewBlog)
        val btnLike: ImageButton = view.findViewById(R.id.btnLike)
        val tvLikeCount: TextView = view.findViewById(R.id.tvLikeCount)
        val btnSave: ImageButton = view.findViewById(R.id.btnSave)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_blog, parent, false)
        return BlogViewHolder(view)
    }

    override fun getItemCount(): Int = blogs.size

    override fun onBindViewHolder(holder: BlogViewHolder, position: Int) {
        val blog = blogs[position]
        holder.tvTitle.text = blog.title
        holder.tvContent.text = blog.content

        // Tampilkan gambar dari Base64
        val imageBytes = Base64.decode(blog.imageUrl, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        holder.ivImage.setImageBitmap(bitmap)

        // Tampilkan nama penulis (cached)
        usernameCache[blog.authorId]?.let {
            holder.tvAuthor.text = "Ditulis oleh: $it"
        } ?: run {
            holder.tvAuthor.text = "Memuat..."
            FirebaseDatabase.getInstance()
                .getReference("users")
                .child(blog.authorId)
                .child("username")
                .get()
                .addOnSuccessListener { snapshot ->
                    val username = snapshot.getValue(String::class.java) ?: "Tidak diketahui"
                    holder.tvAuthor.text = "Ditulis oleh: $username"
                    usernameCache[blog.authorId] = username
                }
                .addOnFailureListener {
                    holder.tvAuthor.text = "Ditulis oleh: Tidak diketahui"
                }
        }

        // ---- LIKE feature ----
        val likesRef = FirebaseDatabase.getInstance().getReference("likes").child(blog.id)
        likesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val count = snapshot.childrenCount.toInt()
                holder.tvLikeCount.text = "$count Suka"
                val isLiked = currentUserId?.let { snapshot.hasChild(it) } ?: false
                holder.btnLike.setImageResource(
                    if (isLiked) R.drawable.ic_liked else R.drawable.ic_like
                )
                holder.btnLike.setOnClickListener {
                    currentUserId?.let {
                        if (isLiked) likesRef.child(it).removeValue()
                        else likesRef.child(it).setValue(true)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) { }
        })

        // ---- SAVE feature ----
        val saveRef = FirebaseDatabase.getInstance()
            .getReference("saves")
            .child(currentUserId ?: "")
            .child(blog.id)

        saveRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val isSaved = snapshot.value == true
                holder.btnSave.setImageResource(
                    if (isSaved) R.drawable.ic_saved else R.drawable.ic_save
                )
                holder.btnSave.setOnClickListener {
                    if (isSaved) saveRef.removeValue()
                    else saveRef.setValue(true)
                }
            }
            override fun onCancelled(error: DatabaseError) { }
        })
    }
}
