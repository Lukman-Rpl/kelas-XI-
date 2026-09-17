package com.example.sakubijak

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.sakubijak.databinding.ItemBudgetCardBinding
import java.text.NumberFormat
import java.util.Locale

class BudgetAdapter(
    private val onItemClick: (BudgetItem) -> Unit
) : ListAdapter<BudgetItem, BudgetAdapter.BudgetViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BudgetViewHolder {
        val binding = ItemBudgetCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BudgetViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BudgetViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }

    inner class BudgetViewHolder(
        private val binding: ItemBudgetCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: BudgetItem) {
            val context = itemView.context
            val usedAmount = item.usedAmount
            val totalAmount = item.totalAmount
            val remaining = totalAmount - usedAmount

            val percentage = if (totalAmount > 0) {
                ((usedAmount.toDouble() / totalAmount.toDouble()) * 100).toInt()
            } else {
                0
            }

            // Bind data ke komponen ViewBinding yang sesuai dengan XML
            binding.tvBudgetCategoryName.text = item.categoryName
            binding.tvBudgetPeriod.text = item.monthYear
            binding.tvBudgetPercentage.text = "$percentage%"
            binding.progressBudget.progress = percentage.coerceAtMost(100)

            binding.tvBudgetUsed.text = formatRupiah(usedAmount)
            binding.tvBudgetTotal.text = formatRupiah(totalAmount)
            binding.tvBudgetRemaining.text = formatRupiah(if (remaining < 0) 0 else remaining)

            // Logika Status dan Peringatan Anggaran
            when {
                percentage >= 100 -> {
                    binding.tvBudgetStatus.text = "Status: Overbudget"
                    val colorRed = ContextCompat.getColor(context, android.R.color.holo_red_dark)
                    binding.tvBudgetStatus.setTextColor(colorRed)
                    binding.ivBudgetStatusIcon.setColorFilter(colorRed)

                    binding.tvBudgetWarningText.visibility = View.VISIBLE
                    binding.tvBudgetWarningText.text = "🚨 Anggaran Melebihi Limit!"
                }
                percentage >= 90 -> {
                    binding.tvBudgetStatus.text = "Status: Kritis"
                    val colorRed = ContextCompat.getColor(context, android.R.color.holo_red_dark)
                    binding.tvBudgetStatus.setTextColor(colorRed)
                    binding.ivBudgetStatusIcon.setColorFilter(colorRed)

                    binding.tvBudgetWarningText.visibility = View.VISIBLE
                    binding.tvBudgetWarningText.text = "⚠️ Anggaran Hampir Habis!"
                }
                percentage >= 75 -> {
                    binding.tvBudgetStatus.text = "Status: Waspada"
                    val colorOrange = ContextCompat.getColor(context, android.R.color.holo_orange_dark)
                    binding.tvBudgetStatus.setTextColor(colorOrange)
                    binding.ivBudgetStatusIcon.setColorFilter(colorOrange)

                    binding.tvBudgetWarningText.visibility = View.VISIBLE
                    binding.tvBudgetWarningText.text = "⚠️ Mendekati Limit!"
                }
                else -> {
                    binding.tvBudgetStatus.text = "Status: Aman"
                    val colorGreen = ContextCompat.getColor(context, android.R.color.holo_green_dark)
                    binding.tvBudgetStatus.setTextColor(colorGreen)
                    binding.ivBudgetStatusIcon.setColorFilter(colorGreen)

                    binding.tvBudgetWarningText.visibility = View.GONE
                }
            }

            // Click listener untuk berpindah ke BudgetFragment
            itemView.setOnClickListener {
                onItemClick(item)
            }
        }

        private fun formatRupiah(number: Long): String {
            val localeID = Locale("in", "ID")
            val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
            formatRupiah.maximumFractionDigits = 0
            return formatRupiah.format(number).replace("Rp", "Rp ")
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<BudgetItem>() {
            override fun areItemsTheSame(oldItem: BudgetItem, newItem: BudgetItem): Boolean {
                return oldItem.categoryName == newItem.categoryName && oldItem.monthYear == newItem.monthYear
            }

            override fun areContentsTheSame(oldItem: BudgetItem, newItem: BudgetItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}