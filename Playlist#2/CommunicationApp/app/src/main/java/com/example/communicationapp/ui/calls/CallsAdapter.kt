package com.example.communicationapp.ui.calls

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.communicationapp.R

class CallsAdapter(private val calls: List<CallItem>) :
    RecyclerView.Adapter<CallsAdapter.CallViewHolder>() {
    data class CallItem(val username: String, val type: String, val time: String)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CallViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_call, parent, false)
        return CallViewHolder(view)
    }

    override fun onBindViewHolder(holder: CallViewHolder, position: Int) {
        val call = calls[position]
        holder.bind(call)
    }

    override fun getItemCount(): Int = calls.size

    class CallViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvUsername: TextView = itemView.findViewById(R.id.tvCallName)
        private val tvType: TextView = itemView.findViewById(R.id.tvTypeCall)
        private val tvTime: TextView = itemView.findViewById(R.id.tvCallTime)

        fun bind(call: CallItem) {
            tvUsername.text = call.username
            tvType.text = call.type
            tvTime.text = call.time
        }
    }
}
