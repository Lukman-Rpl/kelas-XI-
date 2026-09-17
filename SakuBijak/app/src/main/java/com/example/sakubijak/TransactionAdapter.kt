package com.example.sakubijak

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.sakubijak.databinding.ItemTransactionBinding
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

class TransactionAdapter(
    private val onItemClick: ((TransactionResponse) -> Unit)? = null
) : ListAdapter<TransactionResponse, TransactionAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTransactionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClick)
    }

    class ViewHolder(private val binding: ItemTransactionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            transaction: TransactionResponse,
            onItemClick: ((TransactionResponse) -> Unit)?
        ) {
            // 1. Tampilkan Kategori (Tambahkan fallback jika category null, misal untuk Top Up)
            binding.tvTransactionCategory.text = transaction.category?.categories
                ?: if (transaction.type?.lowercase() == "income") "Pemasukan / Top Up" else "Transaksi"

            // 2. Tampilkan Deskripsi/Catatan
            if (!transaction.description.isNullOrEmpty()) {
                binding.tvTransactionDescription.text = transaction.description
                binding.tvTransactionDescription.visibility = android.view.View.VISIBLE
            } else {
                binding.tvTransactionDescription.visibility = android.view.View.GONE
            }

            // 3. Tanggal Transaksi
            binding.tvTransactionDate.text = formatDate(transaction.date)

            // 4. Format Nominal Ke Rupiah (PERBAIKAN CRASH DI SINI)
            val localeID = Locale("in", "ID")
            val formatter = NumberFormat.getCurrencyInstance(localeID).apply {
                maximumFractionDigits = 0
            }

            // Ubah transaction.amount (String) menjadi Double aman terlebih dahulu
            val formattedAmount = formatter.format(transaction.amount ?: 0.0)

            // 5. Tentukan Warna dan Tanda (+ / -) Berdasarkan Tipe
            val isIncome = transaction.type?.lowercase() == "income"
            val greenColor = Color.parseColor("#4CAF50")
            val redColor = Color.parseColor("#E53935")

            if (isIncome) {
                binding.tvTransactionAmount.text = "+$formattedAmount"
                binding.tvTransactionAmount.setTextColor(greenColor)
                binding.ivTransactionIcon.setImageResource(android.R.drawable.ic_input_add)
                binding.ivTransactionIcon.setColorFilter(greenColor)
            } else {
                binding.tvTransactionAmount.text = "-$formattedAmount"
                binding.tvTransactionAmount.setTextColor(redColor)
                binding.ivTransactionIcon.setImageResource(android.R.drawable.ic_menu_send)
                binding.ivTransactionIcon.setColorFilter(redColor)
            }

            // Click Listener
            binding.root.setOnClickListener {
                onItemClick?.invoke(transaction)
            }
        }

        // Helper untuk memformat tanggal
        private fun formatDate(rawDate: String?): String {
            if (rawDate.isNullOrEmpty()) return "-"
            return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
                val cleanDateStr = rawDate.take(10)
                val parsedDate = inputFormat.parse(cleanDateStr)
                if (parsedDate != null) outputFormat.format(parsedDate) else rawDate
            } catch (e: Exception) {
                rawDate
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<TransactionResponse>() {
        override fun areItemsTheSame(
            oldItem: TransactionResponse,
            newItem: TransactionResponse
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: TransactionResponse,
            newItem: TransactionResponse
        ): Boolean {
            return oldItem == newItem
        }
    }
}