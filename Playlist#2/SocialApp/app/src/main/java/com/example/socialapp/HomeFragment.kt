package com.example.socialapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.socialapp.activities.UploadStory
import com.example.socialapp.adapter.PostAdapter
import com.example.socialapp.adapter.StoryAdapter
import com.example.socialapp.databinding.FragmentHomeBinding
import com.example.socialapp.model.Post
import com.example.socialapp.model.Story
import com.google.firebase.database.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var storyAdapter: StoryAdapter
    private lateinit var postAdapter: PostAdapter
    private val storyList = mutableListOf<Story>()
    private val postList = mutableListOf<Post>()

    private val database = FirebaseDatabase.getInstance().reference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        setupRecyclerViews()
        loadStories()
        loadPosts()

        binding.btnUploadStory.setOnClickListener {
            startActivity(Intent(requireContext(), UploadStory::class.java))
        }

        return binding.root
    }

    private fun setupRecyclerViews() {
        // Stories
        storyAdapter = StoryAdapter(requireContext(), storyList)
        binding.recyclerViewStories.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = storyAdapter
        }

        // Posts
        postAdapter = PostAdapter(requireContext(), postList)
        binding.recyclerViewPosts.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = postAdapter
        }
    }

    private fun loadStories() {
        val ref = database.child("stories")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                storyList.clear()
                for (userSnap in snapshot.children) {
                    for (storySnap in userSnap.children) {
                        val story = storySnap.getValue(Story::class.java)
                        story?.let {
                            // filter hanya story yang masih aktif (24 jam)
                            if (it.timestampEnd ?: 0 > System.currentTimeMillis()) {
                                storyList.add(it)
                            }
                        }
                    }
                }
                // Urutkan story berdasarkan waktu terbaru
                storyList.sortByDescending { it.timestampStart }
                storyAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }


    private fun loadPosts() {
        val ref = database.child("posts")
        ref.orderByChild("timestamp").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                postList.clear()
                for (snap in snapshot.children) {
                    val post = snap.getValue(Post::class.java)
                    post?.let { postList.add(it) }
                }
                // Urutkan berdasarkan timestamp terbaru
                postList.sortByDescending { it.timestamp }
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
