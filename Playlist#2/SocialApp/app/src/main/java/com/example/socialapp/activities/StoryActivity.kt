package com.example.socialapp.activities

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.socialapp.R
import com.example.socialapp.model.Story
import com.google.firebase.database.*

class StoryActivity : AppCompatActivity() {

    private lateinit var imgStory: ImageView
    private lateinit var videoStory: VideoView
    private lateinit var tvUsername: TextView

    private val db = FirebaseDatabase.getInstance().reference
    private var userId: String? = null

    private val storyList = mutableListOf<Story>()
    private var currentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_story)

        imgStory = findViewById(R.id.imgStory)
        videoStory = findViewById(R.id.videoStory)
        tvUsername = findViewById(R.id.tvStoryUsername)

        userId = intent.getStringExtra("userId") // dikirim dari StoryAdapter

        if (userId != null) {
            loadStories(userId!!)
        }

        imgStory.setOnClickListener { showNextStory() }
        videoStory.setOnClickListener { showNextStory() }
    }

    private fun loadStories(uid: String) {
        db.child("stories").child(uid)
            .orderByChild("timestampStart")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    storyList.clear()
                    for (snap in snapshot.children) {
                        val story = snap.getValue(Story::class.java)
                        if (story != null && (story.timestampEnd ?: 0) > System.currentTimeMillis()) {
                            storyList.add(story)
                        }
                    }
                    storyList.sortBy { it.timestampStart }
                    if (storyList.isNotEmpty()) showStory(0)
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun showStory(index: Int) {
        if (index >= storyList.size) {
            finish()
            return
        }

        currentIndex = index
        val story = storyList[index]

        tvUsername.text = story.username

        if (story.mediaType == "image") {
            imgStory.visibility = ImageView.VISIBLE
            videoStory.visibility = VideoView.GONE
            Glide.with(this).load(story.mediaUrl).into(imgStory)
        } else {
            imgStory.visibility = ImageView.GONE
            videoStory.visibility = VideoView.VISIBLE
            videoStory.setVideoURI(Uri.parse(story.mediaUrl))
            videoStory.setOnPreparedListener { mp ->
                mp.start()
            }
            videoStory.setOnCompletionListener {
                showNextStory()
            }
        }
    }

    private fun showNextStory() {
        showStory(currentIndex + 1)
    }
}
