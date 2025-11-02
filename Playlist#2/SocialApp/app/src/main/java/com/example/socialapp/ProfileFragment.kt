package com.example.socialapp

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.socialapp.activities.AuthActivity
import com.example.socialapp.activities.EditProfileActivity
import com.example.socialapp.adapter.PostAdapter
import com.example.socialapp.databinding.FragmentProfileBinding
import com.example.socialapp.R
import com.example.socialapp.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    private val postList = mutableListOf<Post>()
    private lateinit var postAdapter: PostAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        setupRecyclerView()
        loadUserInfo()
        loadMyPosts()

        binding.btnLogout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(requireContext(), AuthActivity::class.java))
            requireActivity().finish()
        }

        binding.btnChangeProfile.setOnClickListener {
            startActivity(Intent(requireContext(), EditProfileActivity::class.java))
        }

        return binding.root
    }

    private fun setupRecyclerView() {
        postAdapter = PostAdapter(requireContext(), postList)
        binding.recyclerViewMyPosts.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = postAdapter
        }
    }

    private fun loadUserInfo() {
        val uid = auth.uid ?: return
        val userRef = database.child("users").child(uid)

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val username = snapshot.child("username").getValue(String::class.java)
                val email = snapshot.child("email").getValue(String::class.java)
                val profileImageUrl = snapshot.child("profileImageUrl").getValue(String::class.java)

                binding.tvUsername.text = username
                binding.tvEmail.text = email
                Glide.with(requireContext())
                    .load(profileImageUrl)
                    .placeholder(R.drawable.ic_profile)
                    .into(binding.imgProfile)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Gagal memuat data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun loadMyPosts() {
        val uid = auth.uid ?: return
        database.child("posts").orderByChild("userId").equalTo(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    postList.clear()
                    for (postSnap in snapshot.children) {
                        val post = postSnap.getValue(Post::class.java)
                        post?.let { postList.add(it) }
                    }
                    postAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
