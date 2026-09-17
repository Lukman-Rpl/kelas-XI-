package com.example.sakubijak

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sakubijak.databinding.ItemReportCategoryBinding

class ReportCategoryAdapter(
    private var items: List<CategoryReportItem> = emptyList()
) : RecyclerView.Adapter<ReportCategoryAdapter.CategoryViewHolder>() {

    fun submitList(newList: List<CategoryReportItem>) {
        items = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemReportCategoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class CategoryViewHolder(private val binding: ItemReportCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CategoryReportItem) {
            binding.tvCategoryName.text = item.name
            binding.tvCategoryAmount.text = item.amountFormatted
            binding.tvCategoryPercentage.text = item.percentageLabel
            binding.progressCategory.progress = item.percentage//Unresolved reference 'progressCategory'.

            // Jika backend menyediakan warna kustom (Hex)
            item.colorHex?.let { hex ->
                try {
                    val color = Color.parseColor(hex)
                    binding.progressCategory.setIndicatorColor(color)//Unresolved reference 'progressCategory'.
                } catch (e: Exception) {
                    // Fallback jika hex tidak valid
                }
            }
        }
    }
}