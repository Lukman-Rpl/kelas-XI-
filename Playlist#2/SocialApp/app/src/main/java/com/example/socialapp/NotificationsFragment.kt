package com.example.socialapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.socialapp.adapter.NotificationAdapter
import com.example.socialapp.databinding.FragmentNotificationsBinding
import com.example.socialapp.model.Notification
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    private val notifList = mutableListOf<Notification>()
    private lateinit var notifAdapter: NotificationAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)

        setupRecyclerView()
        loadNotifications()

        return binding.root
    }

    private fun setupRecyclerView() {
        notifAdapter = NotificationAdapter(requireContext(), notifList)
        binding.recyclerViewNotifications.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = notifAdapter
        }
    }

    private fun loadNotifications() {
        val uid = auth.uid ?: return
        val ref = database.child("notifications").child(uid)

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                notifList.clear()
                for (notifSnap in snapshot.children) {
                    val notif = notifSnap.getValue(Notification::class.java)
                    notif?.let { notifList.add(it) }
                }
                // Urutkan berdasarkan timestamp terbaru
                notifList.sortByDescending { it.timestamp }
                notifAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
