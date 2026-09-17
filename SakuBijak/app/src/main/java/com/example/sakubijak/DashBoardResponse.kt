package com.example.sakubijak

import com.google.gson.annotations.SerializedName

data class DashboardResponse(
    @SerializedName("status")
    val status: String? = null,

    @SerializedName("wallet_summary")
    val walletSummary: WalletSummaryDto? = null,

    @SerializedName("category_budgets")
    val categoryBudgets: List<CategoryBudgetDto>? = null,

    @SerializedName("daily_tip")
    val dailyTip: String? = null,

    @SerializedName("recent_transactions")
    val recentTransactions: List<RecentTransactionDto>? = null
)

// DTO khusus untuk Ringkasan Dompet Utama
data class WalletSummaryDto(
    @SerializedName("category_name")
    val categoryName: String? = "Total Anggaran Dompet",

    @SerializedName("month_year")
    val monthYear: String? = null,

    @SerializedName("used_amount")
    val usedAmount: Long = 0L,

    @SerializedName("total_amount")
    val totalAmount: Long = 0L,

    @SerializedName("percentage")
    val percentage: Int = 0,

    @SerializedName("status_label")
    val statusLabel: String? = "Aman"
)

// DTO khusus untuk Anggaran Kategori
data class CategoryBudgetDto(
    @SerializedName("id")
    val id: Long = 0L,

    @SerializedName("category_id")
    val categoryId: Long = 0L,

    @SerializedName("category_name")
    val categoryName: String? = null,

    @SerializedName("limit_amount")
    val limitAmount: Long = 0L,

    @SerializedName("used_amount")
    val usedAmount: Long = 0L,

    @SerializedName("period")
    val period: String? = null
)

data class RecentTransactionDto(
    @SerializedName("id")
    val id: Long = 0L,

    @SerializedName("category")
    val category: String? = null,

    @SerializedName("amount")
    val amount: Double = 0.0,

    @SerializedName("type")
    val type: String = "expense",

    @SerializedName("date")
    val date: String = ""
)

// Extension Function untuk mengubah DTO Backend menjadi BudgetItem yang dipakai Adapter ViewPager2
fun WalletSummaryDto.toBudgetItem(): BudgetItem {
    return BudgetItem(
        categoryName = this.categoryName ?: "Total Anggaran Dompet",
        monthYear = this.monthYear ?: "",
        usedAmount = this.usedAmount,
        totalAmount = this.totalAmount
    )
}

fun CategoryBudgetDto.toBudgetItem(defaultMonthYear: String): BudgetItem {
    return BudgetItem(
        categoryName = this.categoryName ?: "Tanpa Kategori",
        monthYear = this.period ?: defaultMonthYear,
        usedAmount = this.usedAmount,
        totalAmount = this.limitAmount
    )
}