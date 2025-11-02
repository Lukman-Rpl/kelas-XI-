package com.example.communicationapp.ui.calls

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.communicationapp.databinding.FragmentCallsBinding

class CallsFragment : Fragment() {

    private var _binding: FragmentCallsBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: CallsAdapter
    private val calls = mutableListOf<CallsAdapter.CallItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCallsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = CallsAdapter(calls)

        binding.recyclerCalls.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerCalls.adapter = adapter

        // Dummy data sementara
        calls.add(CallsAdapter.CallItem("User A", "Incoming", "12:30"))
        calls.add(CallsAdapter.CallItem("User B", "Outgoing", "13:45"))
        adapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
