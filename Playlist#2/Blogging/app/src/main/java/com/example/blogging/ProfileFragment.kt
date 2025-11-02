package com.example.blogging

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.blogapp.model.Blog
import com.example.blogging.activities.LoginActivity
import com.example.blogging.adapter.SavedBlogAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ProfileFragment : Fragment() {

    private lateinit var imgProfile: ImageView
    private lateinit var tvUsername: TextView
    private lateinit var tvEmail: TextView
    private lateinit var btnChangeProfile: Button
    private lateinit var btnLogout: Button

    private lateinit var recyclerView: RecyclerView
    private lateinit var savedBlogAdapter: SavedBlogAdapter
    private val savedBlogList = mutableListOf<Blog>()

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val database by lazy { FirebaseDatabase.getInstance() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        imgProfile = view.findViewById(R.id.imgProfile)
        tvUsername = view.findViewById(R.id.tvUsername)
        tvEmail = view.findViewById(R.id.tvEmail)
        btnChangeProfile = view.findViewById(R.id.btnChangeProfile)
        btnLogout = view.findViewById(R.id.btnLogout)

        recyclerView = view.findViewById(R.id.recyclerSavedBlogs)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        savedBlogAdapter = SavedBlogAdapter(savedBlogList)
        recyclerView.adapter = savedBlogAdapter

        loadUserProfile()
        loadSavedBlogs()

        btnChangeProfile.setOnClickListener {
            Toast.makeText(requireContext(), "Fitur ubah profil belum tersedia.", Toast.LENGTH_SHORT).show()
        }

        btnLogout.setOnClickListener {
            auth.signOut()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }

        return view
    }

    private fun loadUserProfile() {
        val user = auth.currentUser ?: return
        val uid = user.uid

        val userRef = database.getReference("users").child(uid)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val username = snapshot.child("username").getValue(String::class.java)
                val profileImageUrl = snapshot.child("profileImageUrl").getValue(String::class.java)
                val email = user.email

                tvUsername.text = username ?: "Username"
                tvEmail.text = email ?: "Email tidak tersedia"

                if (!profileImageUrl.isNullOrEmpty()) {
                    Glide.with(requireContext())
                        .load(profileImageUrl)
                        .circleCrop()
                        .into(imgProfile)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Gagal memuat profil", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun loadSavedBlogs() {
        val uid = auth.currentUser?.uid ?: return
        val savesRef = database.getReference("saves").child(uid)
        val blogsRef = database.getReference("blogs")

        savesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                savedBlogList.clear()

                val savedIds = snapshot.children.mapNotNull { it.key }

                for (blogId in savedIds) {
                    blogsRef.child(blogId).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(blogSnapshot: DataSnapshot) {
                            val blog = blogSnapshot.getValue(Blog::class.java)
                            blog?.let {
                                it.id = blogSnapshot.key ?: ""
                                savedBlogList.add(it)
                                savedBlogAdapter.notifyDataSetChanged()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Optional: handle error
                        }
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Gagal memuat postingan tersimpan", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
